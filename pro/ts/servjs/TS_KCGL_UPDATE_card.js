var _viewer = this;
var kcId = _viewer.getParHandler().getParHandler().getPKCode();
var pkCode = _viewer.getPKCode();
if (_viewer._actVar == "cardAdd") {
	if(_viewer.getItem("KC_ID").getValue() == ""){
		_viewer.getItem("KC_ID").setValue(kcId);
	}
	var msg = "新建一条申请";
	if (confirm(msg) == true) {
		_viewer.btns[UIConst.ACT_SAVE].click();
	} else {
		_viewer.backA.mousedown();
	}
}

//_viewer.getBtn("save").css("right", "500px");
_viewer.getBtn("commit").unbind("click").bind("click", function(event) {
	if (_viewer.getItem("KC_COMMIT").getValue() == 1) {
		alert("申请单已提交过，无需重复提交");
		return false;
	}
	_viewer.getItem("KC_COMMIT").setValue(1);
	_viewer._saveForm();
});

var servId = _viewer.getParHandler().getParHandler().servId;
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
		_viewer._saveForm();
	} else {
		alert("已审核过");
	}

});
_viewer.getBtn("shBtgBtn").unbind("click").bind("click", function(event) {
	if (_viewer.getItem("UPDATE_AGREE").getValue() == 0) {
		_viewer.getItem("UPDATE_AGREE").setValue(2);
		_viewer._saveForm();
	} else {
		alert("已审核过");
	}
});

