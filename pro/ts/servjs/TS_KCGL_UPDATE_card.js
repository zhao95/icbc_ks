var _viewer = this;
if (_viewer._actVar == "cardAdd") {
	var msg = "新建一条申请";
	if (confirm(msg) == true) {
		_viewer._saveForm();
	} else {
		_viewer.backA.mousedown();
	}
}

_viewer.getBtn("save").css("right", "500px");

_viewer.getBtn("commit").unbind("click").bind("click", function(event) {
	if(_viewer.getItem("KC_COMMIT").getValue() == 1){
		alert("申请单已提交过，无需重复提交");
		return false;
	}
	_viewer.getItem("KC_COMMIT").setValue(1);
	_viewer._saveForm();
});
