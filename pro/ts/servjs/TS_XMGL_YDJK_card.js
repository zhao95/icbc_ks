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
_viewer.beforeSave = function() {
	var beginTime=_viewer.getItem("YDJK_STADATE").getValue();
	var endTime=_viewer.getItem("YDJK_ENDDATE").getValue();
    var beginTimes = beginTime.substring(0, 10).split('-');
    var endTimes = endTime.substring(0, 10).split('-');
     beginTime = beginTimes[1] + '-' + beginTimes[2] + '-' + beginTimes[0] + ' ' + beginTime.substring(10, 19);
     endTime = endTimes[1] + '-' + endTimes[2] + '-' + endTimes[0] + ' ' + endTime.substring(10, 19);
    var a = (Date.parse(endTime) - Date.parse(beginTime)) / 3600 / 1000;
    if(a < 0||a == 0){
 		$("#TS_XMGL_YDJK-YDJK_STADATE").addClass("blankError").addClass("errorbox");
 		$("#TS_XMGL_YDJK-YDJK_ENDDATE").addClass("blankError").addClass("errorbox");
		return false;
 	}
};
////针对开始和结束时间的校验
//_viewer.getItem("YDJK_STADATE").obj.unbind("click").bind("click", function() {
//	    WdatePicker({
//		 dateFmt: 'yyyy-MM-dd HH:mm:ss',
//	        maxDate : "#F{$dp.$D('" + _viewer.servId + "-YDJK_ENDDATE')}"
//	    });
//	});
//_viewer.getItem("YDJK_ENDDATE").obj.unbind("click").bind("click", function() {
//
//	    WdatePicker({
//		 dateFmt: 'yyyy-MM-dd HH:mm:ss',
//	        minDate : "#F{$dp.$D('" + _viewer.servId + "-YDJK_STADATE')}"
//	    });
//	});
// 



