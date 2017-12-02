var _viewer = this;

_viewer.tabHide("TS_XMGL_KCAP_DAPCC_CCSJ");

//_viewer._readBtns();
$("#TS_XMGL_KCAP_DAPCC-save").css("visibility","hidden");
//设置form的只读
_viewer.form.disabledAll();

$("#TS_XMGL_KCAP_DAPCC-winTabs").children().eq(0).hide();