/** 加载条组件 */
GLOBAL.namespace("rh.ui");
rh.ui.loadbar = function(options) {
	var defaults = {
		"id":"rh-loadbar",
//		"msg":"执行中，请稍后.."
		"msg":Language.transStatic("rh_ui_ccexSearch_string1")
	};
	this.opts = jQuery.extend(defaults,options);
	this._msg = this.opts.msg;
	this._id = this.opts.id;
};
rh.ui.loadbar.prototype.show = function() {
	this._layout();
};
/*
 * 构建弹出框页面布局
 */
rh.ui.loadbar.prototype._layout = function() {
	var _self = this;
	jQuery("#" + this._id).dialog("destroy");

	//1.构造dialog
	this.winDialog = jQuery("<div></div>").addClass("rh-loadBarDialog").attr("id",this._id).attr("title","");
	this.winDialog.appendTo(jQuery("body"));
	//@TODO:显示位置处理
	var hei = 50;
    var wid = 150;
	var bodyWid = parseInt((jQuery("body").width() - wid ) / 2);
	var bodyHei = parseInt(GLOBAL.getDefaultFrameHei() / 2);
    var posArray = [bodyWid,bodyHei];
    var dlg = jQuery("#" + this._id);
	dlg.dialog({
		autoOpen: false,
		height: hei,
		width: wid,
		modal: true,
		position:posArray,
		resizable:false
	});
	dlg.prev().hide()
	var img = jQuery("<div></div>").addClass("rh-loadbar").append(this._msg);
	img.appendTo(this.winDialog);
	dlg.parent().css({"border-radius":"6px"});
    dlg.dialog("open");
};
rh.ui.loadbar.prototype.hide = function() {
	jQuery("#" + this._id).dialog("destroy");
	if(this.winDialog) {
		this.winDialog.empty();
		this.winDialog.remove();
	}
};

rh.ui.loadbar.prototype.hideDelayed = function() {
	var _self = this;
	setTimeout(function() {
	    _self.hide();
    }, 300);
}
