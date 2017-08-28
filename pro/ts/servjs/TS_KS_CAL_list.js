var _viewer = this;

_viewer.getBtn("excel_imp").unbind("click").click(function(event) {
	var importWin = new rh.ui.popPrompt({
		title:"请选择文件",
		tip:"请选择要导入的Excel(.xls)文件",
		okFunc:function() {
			var fileData = file.getFileData();
			
			if (jQuery.isEmptyObject(fileData)) {
				alert("请选择文件上传");
				return;
			}
			
			var fileId = null;
			for (var key in fileData) {
				fileId = key;
			}
			
			if (fileId == null){
				alert("请选择文件上传");
				return;
			}
			//提交  导入 只将fileId传入即可
			FireFly.doAct(_viewer.servId,"excelImp",{"fileId":fileId},true);
			importWin.closePrompt();
			_viewer.refresh();
			
			file.destroy();
		},
		closeFunc:function() {
			file.destroy();
		}
	});
	importWin._layout(event,undefined,[450,230]);
	
	var config = {"SERV_ID":_viewer.servId, "FILE_CAT":"SERV_UPLOAD", "FILENUMBER":1, "VALUE":15, "TYPES":"*.xls;", "DESC":"批量导入"};
	var file = new rh.ui.File({
		"config" : config
	});
	
	var container = jQuery("#" + importWin.dialogId);
	container.empty();
	importWin.tipBar = jQuery("<div></div>").text(importWin.tip).css({"height":"40px","font-weight":"normal","margin":"10px 15px 0px 10px","color":"red"});
    container.append(importWin.tipBar);
	container.append(file.obj);
	file.obj.css('margin-left', '10px');
	file.initUpload();
});