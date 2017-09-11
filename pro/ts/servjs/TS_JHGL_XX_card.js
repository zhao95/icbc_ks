var _viewer = this;
_viewer.getItem("JH_CREATEDATE").obj.unbind("click").bind("click", function() {
	    WdatePicker({
	        maxDate : "#F{$dp.$D('" + _viewer.servId + "-JH_ENDDATE')}"
	    });
	});
_viewer.getItem("JH_ENDDATE").obj.unbind("click").bind("click", function() {
	    WdatePicker({
	        minDate : "#F{$dp.$D('" + _viewer.servId + "-JH_CREATEDATE')}"
	    });
});

if(_viewer._actVar == "cardAdd"){
	var JH_ID = _viewer.opts.JH_ID;
	var JH_TITLE = _viewer.opts.JH_TITLE;
	_viewer.getItem("JH_PTITLE").setValue(JH_ID);
	_viewer.getItem("JH_TITLE").setValue(JH_TITLE);
}