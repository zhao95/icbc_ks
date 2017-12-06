var _viewer = this;

var action = _viewer.getItem("GLY_ACTION").getValue();
if(action == ""){
	_viewer.getItem("GLY_ACTION").setValue("add");
}

if(action == "" || action == "add"){
	$("#TS_KCGL_UPDATE_GLY-GLY_ACTION input[value='update']").attr("disabled", true);
	$("#TS_KCGL_UPDATE_GLY-GLY_ACTION input[value='delete']").attr("disabled", true);
}else{
	$("#TS_KCGL_UPDATE_GLY-GLY_ACTION input[value='add']").attr("disabled", true);
}

_viewer.afterSave = function(resultData) {
	setTimeout(function(){ 
		_viewer._parHandler.refreshGrid(); 
		jQuery("#" + _viewer.dialogId).dialog("close");
	}, 100);
};
