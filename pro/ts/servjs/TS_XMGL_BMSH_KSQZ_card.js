var _viewer = this;

var saveBtn = _viewer.getBtn("save");

saveBtn.unbind("click");
//隐藏保存按钮,且页面不只读
saveBtn.css('visibility','hidden');