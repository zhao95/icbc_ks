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

var params = _viewer.getParHandler().getParams();
var pkCode = params.JH_ID;
var jhTitle = params.JH_TITLE;
if(pkCode!=""){
	document.getElementById("TS_JHGL_XX-JH_PTITLE").value=pkCode;
	document.getElementById("TS_JHGL_XX-JH_TITLE").value=jhTitle;
}

