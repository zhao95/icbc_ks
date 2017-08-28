var _viewer = this;

_viewer.getBtn("English").unbind("click").bind("click", function() {
	_viewer.openEnglishDialog(_viewer.servId, _viewer.getPKCode());
});