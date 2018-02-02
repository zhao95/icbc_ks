var _viewer = this;
$("#TS_KCGL_GLJG .rhGrid").find("tr").unbind("dblclick"); 
$("#TS_KCGL_GLJG .rhGrid").find("th[icode='del']").html("操作");

//删除单行数据
//_viewer.grid.getBtn("del").unbind("click").bind("click",function() {
//	var pk = jQuery(this).attr("rowpk");//获取主键信息
//	rowDelete(pk,_viewer);
//});

/*
* 删除前方法执行
*/
_viewer.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

$("#TS_KCGL_GLJG .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		$(item).find("td[icode='BUTTONS']").prepend(
				'<a class="rhGrid-td-rowBtnObj rh-icon"  id="TS_KCGL_GLJG_look" operCode="optLookBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">查看</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_KCGL_GLJG_edit"  operCode="optEditBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'
		);
		bindCard(dataId);
	}
});	
function bindCard(dataId){
	jQuery("td [id='TS_KCGL_GLJG_edit']").unbind("click").bind("click", function(){
		var pkCode = $(this).attr("rowpk");
		openMyCard(pkCode);
	});
	
	 //查看
	jQuery("td [id='TS_KCGL_GLJG_look']").unbind("click").bind("click", function(){
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
if(_viewer.getParHandler().opts.readOnly || _viewer.getParHandler()._readOnly || _viewer.getParHandler().servId == "TS_KCGL_SH" || _viewer.getParHandler().servId == "TS_KCGL_JY"){
	$("a#TS_KCGL_GLJG_edit").hide();
	_viewer.getBtn("far").hide();
	_viewer.getBtn("near").hide();
	$("#TS_KCGL_GLJG-tmplBtn").hide();
}

//1远2近
_viewer.getBtn("far").unbind("click").bind("click",function() {
	var pkCodes = _viewer.grid.getSelectPKCodes();//获取主键值
	for(var i=0;i<pkCodes.length;i++){
		var dataId = pkCodes[i];
		var param = {};
		param["JG_FAR"] = 1;
		param["_PK_"] = dataId;
		FireFly.doAct("TS_KCGL_GLJG","save",param);
	}
	if(pkCodes.length > 0){
		_viewer.refreshGrid();
	}
});

_viewer.getBtn("near").unbind("click").bind("click",function() {
	var pkCodes = _viewer.grid.getSelectPKCodes();//获取主键值
	for(var i=0;i<pkCodes.length;i++){
		var dataId = pkCodes[i];
		var param = {};
		param["JG_FAR"] = 2;
		param["_PK_"] = dataId;
		FireFly.doAct("TS_KCGL_GLJG","save",param);
	}
	if(pkCodes.length > 0){
		_viewer.refreshGrid();
	}
});

/**
 * 导出
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
	window.open(FireFly.getContextPath() + '/ts/imp_template/考场管理-关联机构导入模版.xls');
});

