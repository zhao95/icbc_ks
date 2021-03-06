var _viewer = this;

$("#TS_KCGL_ZWDYB .rhGrid").find("tr").unbind("dblclick"); 
$("#TS_KCGL_ZWDYB .rhGrid").find("th[icode='del']").html("操作");

var height = jQuery(window).height()-200;
var width = jQuery(window).width()-200;

//添加按钮

_viewer.getBtn("add").unbind("click").bind("click",function() {
	var kcId = _viewer.getParHandler().getItem("KC_ID").getValue();
	var kcMax = _viewer.getParHandler().getItem("KC_MAX").getValue();
	var result = FireFly.doAct("TS_KCGL_ZWDYB","count",{"_WHERE_":"and kc_id = '"+kcId+"'"})._DATA_;

	if(result == kcMax){
		Tip.showError("录入座位数不能大于考场最大设备数！");
		return false;
	}
	//打开添加页面act：方法（必填），sId：服务（必填），parHandler：当前句柄，widHeiArray:小卡片的宽度高度，xyArray：左上角坐标
    var temp = {"act":UIConst.ACT_CARD_ADD,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,100],"links":{"KC_ID":kcId}};
    var cardView = new rh.vi.cardView(temp);
    cardView.show();
});

/*
* 删除前方法执行
*/
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};
$("#TS_KCGL_ZWDYB .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		$(item).find("td[icode='BUTTONS']").prepend(
				'<a class="rhGrid-td-rowBtnObj rh-icon"  id="TS_KCGL_ZWDYB_look" operCode="optLookBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">查看</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_KCGL_ZWDYB_edit"  operCode="optEditBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'
		);
		bindCard();
	}
});	

function bindCard(){
	jQuery("td [id='TS_KCGL_ZWDYB_edit']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		openMyCard(pkCode);
	});
	

	 //查看
	jQuery("td [id='TS_KCGL_ZWDYB_look']").unbind("click").bind("click", function(){
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
//如果父页面是只读的，则隐藏编辑行按钮
if(_viewer.getParHandler().opts.readOnly || _viewer.getParHandler()._readOnly || _viewer.getParHandler().servId == "TS_KCGL_SH"){
	$("a#TS_KCGL_ZWDYB_edit").hide();
	$("#TS_KCGL_ZWDYB-tmplBtn").hide();
}

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
	window.open(FireFly.getContextPath() + '/ts/imp_template/考场管理-座位对应表导入模版.xls');
});