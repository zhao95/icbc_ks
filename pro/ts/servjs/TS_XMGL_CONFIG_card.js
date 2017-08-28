var _viewer = this;
$("#TS_XMGL_CONFIG-CONF_KWZDGZ").find("label").append("<br>");

if(_viewer.getItem("XM_ID").getValue() == ""){
	var xmId = _viewer.opts.xmId;
	_viewer.getItem("XM_ID").setValue(xmId);
}
