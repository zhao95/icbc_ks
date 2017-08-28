var _viewer = this;
//针对开始和结束时间的校验
_viewer.getItem("FZGKS_STADATE").obj.unbind("click").bind("click", function() {
	    WdatePicker({
	        maxDate : "#F{$dp.$D('" + _viewer.servId + "-FZGKS_ENDDATE')}"
	    });
	});
_viewer.getItem("FZGKS_ENDDATE").obj.unbind("click").bind("click", function() {

	    WdatePicker({
	        minDate : "#F{$dp.$D('" + _viewer.servId + "-FZGKS_STADATE')}"
	    });
	});
 



