var _viewer = this;
if(_viewer.opts.act == "cardAdd"){
	var XM_SZ_ID = _viewer.opts.XM_SZ_ID;
	if(typeof(XM_SZ_ID)!="undefined"){ 
		_viewer.getItem("XM_SZ_ID").setValue(XM_SZ_ID);
	}
	
	var XM_ID = _viewer.opts.XM_ID;
	if(typeof(XM_ID)!="undefined"){ 
		_viewer.getItem("XM_ID").setValue(XM_ID);
	}
}

//针对开始和结束时间的校验
_viewer.getItem("YDJK_STADATE").obj.unbind("click").bind("click", function() {
	    WdatePicker({
		 dateFmt: 'yyyy-MM-dd HH:mm:ss';
	        maxDate : "#F{$dp.$D('" + _viewer.servId + "-YDJK_ENDDATE')}"
	    });
	});
_viewer.getItem("YDJK_ENDDATE").obj.unbind("click").bind("click", function() {

	    WdatePicker({
		 dateFmt: 'yyyy-MM-dd HH:mm:ss';
	        minDate : "#F{$dp.$D('" + _viewer.servId + "-YDJK_STADATE')}"
	    });
	});
 



