_viewer=this;

//针对时间的校验
_viewer.getItem("JH_CREATEDATE").obj.unbind("click").bind("click", function() {
	WdatePicker({
		maxDate : "#F{$dp.$D('" + _viewer.servId + "-JH_ENDDATE')}"
	});
});
_viewer.getItem("JH_ENDDATE").obj.unbind("click").bind("click", function() {

	WdatePicker({
		minDate : "#F{$dp.$D('" + _viewer.servId + "-JH_CREATEDATE')}"
	});
});