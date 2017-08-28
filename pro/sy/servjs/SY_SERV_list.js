/** 服务卡片使用的js方法定义：开始 */
var _viewer = this;
_viewer.getBtn("fromTable").unbind("click").bind("click", function(event) {
	var temp = new rh.ui.popPrompt({
		title:Language.transStatic("syServ_List4"),
		tip:Language.transStatic("syServ_List5"),
		okFunc:function() {
			var table = temp.obj.val();
			if (table == "" || table == null) {
				alert(Language.transStatic("syServ_List6"));
			} else {
				var param = {};
				param["TABLE_VIEW"] = table;
				var resultData = FireFly.doAct(_viewer.servId, "fromTable", param, true);
				var tip = resultData[UIConst.RTN_MSG];
		        if (tip.indexOf(UIConst.RTN_OK) == 0) {//成功则刷新
		            temp.closePrompt();
					_viewer.refresh();	
			    }
			}
		}
	});
	temp.render(event);
});

_viewer.grid.getBtn("listPreview").unbind("click").bind("click", function(event) {
	var pk = jQuery(this).attr("rowpk");
	Tab.open({'url':pk + '.list.do','tTitle':_viewer.grid.getRowItemValue(pk, 'SERV_NAME'),'menuFlag':3,'icon':'fash'});
	event.stopPropagation();
	return false;
});

/**
 * 上传导入JSON定义文件
 */
_viewer.getBtn('uploadJson').unbind("click").bind("click",function(event) {
	var importWin = new rh.ui.popPrompt({
		title:Language.transStatic("syServ_List1"),
		tip:Language.transStatic("syServ_List2"),
		okFunc:function() {
			var fileData = file.getFileData();
			
			if (jQuery.isEmptyObject(fileData)) {
				alert(Language.transStatic("syServ_List3"));
				return;
			}
			
			var fileId = null;
			for (var key in fileData) {
				fileId = key;
			}
			
			if (fileId == null){
				alert(Language.transStatic("syServ_List3"));
				return;
			}
			//提交  导入 只将fileId传入即可
			rh_processData(_viewer.servId + ".uploadJson.do",{"fileId" : fileId}, true);
			importWin.closePrompt();
			_viewer.refresh();
			
			file.destroy();
		},
		closeFunc:function() {
			file.destroy();
		}
	});
	importWin._layout(event,undefined,[450,230]);
	
	var config = {"SERV_ID":_viewer.servId, "FILE_CAT":"SERV_UPLOAD", "FILENUMBER":1, "VALUE":15, "TYPES":"*.zip;*.json;", "DESC":"服务配置"};
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

/**
 * [导出详细设计]按钮绑定事件
 * @author hdy
 */
_viewer.getBtn("outputDesign").unbind("click").bind("click",function(){
	//获取主键数组
	var idsAttr = _viewer.grid.getSelectPKCodes();
	//id格式字符串
	var ids = idsAttr.join(",");
	var data = {"filePath":"sy/xdoc/detailed_design.xdoc","fileName":"导出详细设计","format":"doc","ids":ids};
	FireFly.doFormAct(_viewer.servId, "getOutputXdocFile", data);
});