var _viewer = this;
//针对开始和结束时间的校验
_viewer.getItem("RYSZQZ_STARTTIME").obj.unbind("click").bind("click", function() {
	    WdatePicker({
	        maxDate : "#F{$dp.$D('" + _viewer.servId + "-RYSZQZ_ENDTTIME')}"
	    });
	});
_viewer.getItem("RYSZQZ_ENDTTIME").obj.unbind("click").bind("click", function() {

	    WdatePicker({
	        minDate : "#F{$dp.$D('" + _viewer.servId + "-RYSZQZ_STARTTIME')}"
	    });
	});
 




