var _viewer = this;
var XM_SZ_ID = _viewer.opts.XM_SZ_ID;
if(_viewer.opts.act == "cardAdd"){
	XM_SZ_ID = _viewer.opts.XM_SZ_ID;
	if(typeof(XM_SZ_ID)!="undefined"){ 
		_viewer.getItem("XM_SZ_ID").setValue(XM_SZ_ID);
	}
	var XM_ID = _viewer.opts.XM_ID;
	if(typeof(XM_ID)!="undefined"){ 
		_viewer.getItem("XM_ID").setValue(XM_ID);
	}
}

//针对开始和结束时间的校验
_viewer.getItem("QJ_STADATE").obj.unbind("click").bind("click", function() {
	    WdatePicker({
		 dateFmt: 'yyyy-MM-dd HH:mm:ss';
	        maxDate : "#F{$dp.$D('" + _viewer.servId + "-QJ_ENDDATE')}"
	    });
	});
_viewer.getItem("QJ_ENDDATE").obj.unbind("click").bind("click", function() {

	    WdatePicker({
		 dateFmt: 'yyyy-MM-dd HH:mm:ss';
	        minDate : "#F{$dp.$D('" + _viewer.servId + "-QJ_STADATE')}"
	    });
	});
 



