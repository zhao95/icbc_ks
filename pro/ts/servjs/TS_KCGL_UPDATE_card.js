var _viewer = this;
var kcId = "";
if(_viewer.getParHandler().servId == "TS_KCGL"){
	kcId = _viewer.opts.kcId;
}else{
	kcId = _viewer.getParHandler().getParHandler().getPKCode();
}
var pkCode = _viewer.getPKCode();
if (_viewer._actVar == "cardAdd") {
	if(_viewer.getItem("KC_ID").getValue() == ""){
		_viewer.getItem("KC_ID").setValue(kcId);
	}
	var msg = "申请变更";
	if (confirm(msg) == true) {
		_viewer.btns[UIConst.ACT_SAVE].click();
	} else {
		_viewer.backA.mousedown();
	}
}

//_viewer.getBtn("save").css("right", "500px");
_viewer.getBtn("commit").unbind("click").bind("click", function(event) {
	if (_viewer.getItem("KC_COMMIT").getValue() == 1) {
		Tip.showError("申请单已提交过，无需重复提交", true);
		return false;
	}
	_viewer.getItem("KC_COMMIT").setValue(1);
	_viewer._saveForm();
	setTimeout(function(){
		jQuery("#TS_KCGL_UPDATE-winDialog").dialog("close");
	}, 200);
});

var servId = "";
if(_viewer.getParHandler().servId == "TS_KCGL"){
	servId = "TS_KCGL";
}else{
	servId = _viewer.getParHandler().getParHandler().servId;
}	

if (servId == "TS_KCGL_SH") {
	_viewer.getBtn("commit").hide();
} else {
	_viewer.getBtn("shTgBtn").hide();
	_viewer.getBtn("shBtgBtn").hide();
}

_viewer.getBtn("shTgBtn").unbind("click").bind("click", function(event) {
	if (_viewer.getItem("UPDATE_AGREE").getValue() == 0) {
		_viewer.getItem("UPDATE_AGREE").setValue(1);
		FireFly.doAct("TS_KCGL_SH","updateShInfo",{"kcId":kcId,"updateId":pkCode},true,false,function(data){
			
		});
		_viewer.getItem("SHR_NAME").setValue(System.getVar("@USER_NAME@"));
		_viewer.getItem("SHR_ODEPT_NAME").setValue(System.getVar("@ODEPT_NAME@"));
		_viewer._saveForm();
		setTimeout(function(){
			_viewer.getParHandler().refreshGrid();
			jQuery("#TS_KCGL_UPDATE-winDialog").dialog("close");
		}, 200);
	} else {
		Tip.showError("已审核过", true);
	}
});
_viewer.getBtn("shBtgBtn").unbind("click").bind("click", function(event) {
	if (_viewer.getItem("UPDATE_AGREE").getValue() == 0) {
		_viewer.getItem("UPDATE_AGREE").setValue(2);
		_viewer.getItem("SHR_NAME").setValue(System.getVar("@USER_NAME@"));
		_viewer.getItem("SHR_ODEPT_NAME").setValue(System.getVar("@ODEPT_NAME@"));
		_viewer._saveForm();
		setTimeout(function(){
			_viewer.getParHandler().refreshGrid();
			jQuery("#TS_KCGL_UPDATE-winDialog").dialog("close");
		}, 200);
	} else {
		Tip.showError("已审核过", true);
	}
});

$("#TS_KCGL_UPDATE-save").css("visibility","hidden");

