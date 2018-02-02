var _viewer = this;

$("#TS_KCGL_IPSCOPE  .rhGrid").find("th[icode='del']").html("操作");
$("#TS_KCGL_IPSCOPE  .rhGrid").find("tr").unbind("dblclick");

//如果父页面是只读的，则隐藏编辑行按钮
if(_viewer.getParHandler().opts.readOnly || _viewer.getParHandler()._readOnly || _viewer.getParHandler().servId == "TS_KCGL_SH"){
	$("a#TS_KCGL_IPSCOPE_edit").hide();
	$("#TS_KCGL_IPSCOPE-tmplBtn").hide();
}

/*
* 删除前方法执行
*/
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	var kcId = _viewer.getParHandler().getPKCode();
	var pkCodes = _viewer.grid.getSelectPKCodes();//获取主键值
	var pkCodes_str = pkCodes.join("','");
	var scope = FireFly.doAct("TS_KCGL_IPSCOPE","finds",{"_SELECT_":"IPS_SCOPE","_WHERE_":"and kc_id = '"+kcId+"' and IPS_ID not in ('"+pkCodes_str+"')"})._DATA_;
	var zw = FireFly.doAct("TS_KCGL_ZWDYB","finds",{"_SELECT_":"ZW_IP","_WHERE_":"and kc_id = '"+kcId+"'"})._DATA_;
	var flag = true;
	for(var j=0;j<zw.length;j++){
		var tmpIp = zw[j].ZW_IP;
		var tmpFlag = true;
		for(var i=0;i<scope.length;i++){
			var tmpScope = scope[i].IPS_SCOPE;
			if(checkScope(tmpScope,tmpIp)){
				tmpFlag = false;
				break;
			}
		}
		if(flag){
			flag = false;
			break;
		}
	}
	
	if(!flag){
		var msg = "考场IP段范围删除后存在超出存在IP范围的座位IP！";
		Tip.showError(msg, true);
		return false;
	}
	
	showVerify(pkArray,_viewer);
};

$("#TS_KCGL_IPSCOPE .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		$(item).find("td[icode='BUTTONS']").prepend(
				'<a class="rhGrid-td-rowBtnObj rh-icon"  id="TS_KCGL_IPSCOPE_look" operCode="optLookBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">查看</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_KCGL_IPSCOPE_edit" operCode="optEditBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'
		);
		bindCard();
	}
});	

function bindCard(){
	//编辑
	jQuery("td [id='TS_KCGL_IPSCOPE_edit']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		openMyCard(pkCode);
	});
	 //查看
	jQuery("td [id='TS_KCGL_IPSCOPE_look']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		//$(".hoverDiv").css('display','none');
		openMyCard(pkCode,true);
	 });
}

//列表操作按钮 弹dialog
function openMyCard(dataId,readOnly,showTab){
	var height = jQuery(window).height()-200;
	var width = jQuery(window).width()-200;
	
	var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,100]};
    temp[UIConst.PK_KEY] = dataId;
    if(readOnly != ""){
    	temp["readOnly"] = readOnly;
    }
    if(showTab != ""){
    	temp["showTab"] = showTab;
    }
    var cardView = new rh.vi.cardView(temp);
    cardView.show();
}

/**
 * 导入
 */
_viewer.getBtn("imp").unbind("click").bind("click",function() {
	var kcId = _viewer.getParHandler().getItem("KC_ID").getValue();
	
	var config = {"SERV_ID":_viewer.opts.sId, "FILE_CAT":"EXCEL_UPLOAD", "FILENUMBER":1, 
		"VALUE":5, "TYPES":"*.xls;*.xlsx", "DESC":"导入Excel文件"};
	var file = new rh.ui.File({
		"config" : config,"width":"99%"
	});
	
	var importWin = new rh.ui.popPrompt({
		title:"请选择文件",
		tip:"请选择要导入的Excel文件：",
		okFunc:function() {
			var fileData = file.getFileData();
			if (jQuery.isEmptyObject(fileData)) {
				Tip.showError("请选择文件上传", true);
				return;
			}
			var fileId = null;
			for (var key in fileData) {
				fileId = key;
			}
			if (fileId == null){
				Tip.showError("请选择文件上传", true);
				return;
			}
			
			var param = {};
			param["KC_ID"] = kcId;
			
			_viewer._imp(fileId,param);
			importWin.closePrompt();
	        // _viewer.refreshGrid();
			file.destroy();
		},
		closeFunc:function() {
			file.destroy();
		}
	});

    var container = _viewer._getImpContainer(event, importWin);
	container.append(file.obj);
	file.obj.css({'margin-left':'5px'});
	file.initUpload();
});

_viewer.getBtn("tmplBtn").unbind("click").bind("click",function(){
	window.open(FireFly.getContextPath() + '/ts/imp_template/考场管理-考场IP段导入模版.xls');
});

/**
 * 校验ip是不是在scope范围内
 * @param scope
 * @param ip
 * @returns {Boolean}
 */
function checkScope(scope,ip){
	var sz = scope.split("-");
	var a = sz[0];
	var b = sz[1];
	var r1 = a.split(".")[0];
	var r2 = a.split(".")[1];
	var r3 = a.split(".")[2];
	var sa4 = a.split(".")[3];
	var sb4 = b.split(".")[3];
	
	var ip_1 = ip.split(".")[0];
	var ip_2 = ip.split(".")[1];
	var ip_3 = ip.split(".")[2];
	var ip_4 = ip.split(".")[3];
	
	if(parseInt(r1) != parseInt(ip_1) || parseInt(r2) != parseInt(ip_2) || parseInt(r3) != parseInt(ip_3)){
		return false;
	}
	
	if(parseInt(ip_4) >= parseInt(sa4) && parseInt(ip_4) < parseInt(sb4)){
		return true;
	}
	return false;
}
