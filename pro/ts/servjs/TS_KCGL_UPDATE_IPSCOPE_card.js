var _viewer = this;

if(_viewer.opts.readOnly){
	_viewer.readCard();
}

var action = _viewer.getItem("IPS_ACTION").getValue();
if(action == ""){
	_viewer.getItem("IPS_ACTION").setValue("add");
}

if(action == "" || action == "add"){
	$("#TS_KCGL_UPDATE_IPSCOPE-IPS_ACTION input[value='update']").attr("disabled", true);
	$("#TS_KCGL_UPDATE_IPSCOPE-IPS_ACTION input[value='delete']").attr("disabled", true);
}else{
	$("#TS_KCGL_UPDATE_IPSCOPE-IPS_ACTION input[value='add']").attr("disabled", true);
}

_viewer.afterSave = function(resultData) {
	setTimeout(function(){ 
		_viewer._parHandler.refreshGrid(); 
		jQuery("#" + _viewer.dialogId).dialog("close");
	}, 100);
};

_viewer.beforeSave= function() {
	if(checkScopeWrite()){
		var msg = "操作IP区段格式不正确！";
		Tip.showError(msg, true);
		return false;
	}
}


function checkScopeWrite(){
	var scope = $("#TS_KCGL_UPDATE_IPSCOPE-IPS_SCOPE").val();
	var sz = scope.split("-");
	if(sz.length != 2){
		return true;
	}
	var a = sz[0];
	var b = sz[1];
	if(a.split(".").length != 4 || b.split(".").length != 4){
		return true;
	}
	var r1 = a.split(".")[0] != b.split(".")[0];
	var r2 = a.split(".")[1] != b.split(".")[1];
	var r3 = a.split(".")[2] != b.split(".")[2];
	if(r1 || r2 || r3){
		return true;
	}
	var sa4 = a.split(".")[3];
	var sb4 = b.split(".")[3];
	
	if(parseInt(sa4) > parseInt(sb4)){
		return true;
	}
	return false;
}
