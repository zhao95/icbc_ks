var _viewer = this;
_viewer.tabHide("TS_KCGL_UPDATE");

//设置卡片只读
if(_viewer.opts.readOnly){
	_viewer.readCard();
}

_viewer.getBtn("stop").click(function() {
	if(_viewer.getItem("KC_STATE").getValue() != 6){
		_viewer.getItem("KC_STATE").setValue(6);
		_viewer._saveForm();
	}
});
