_viewer=this;

////针对时间的校验
//_viewer.getItem("JH_CREATEDATE").obj.unbind("click").bind("click", function() {
//	WdatePicker({
//		maxDate : "#F{$dp.$D('" + _viewer.servId + "-JH_ENDDATE')}"
//	});
//});
//_viewer.getItem("JH_ENDDATE").obj.unbind("click").bind("click", function() {
//
//	WdatePicker({
//		minDate : "#F{$dp.$D('" + _viewer.servId + "-JH_CREATEDATE')}"
//	});
//});

//针对开始和结束时间的校验
_viewer.beforeSave = function() {
	var beginTime=_viewer.getItem("JH_CREATEDATE").getValue();
	var endTime=_viewer.getItem("JH_ENDDATE").getValue();
    var beginTimes = beginTime.substring(0, 10).split('-');
    var endTimes = endTime.substring(0, 10).split('-');
     beginTime = beginTimes[1] + '-' + beginTimes[2] + '-' + beginTimes[0] + ' ' + beginTime.substring(10, 19);
     endTime = endTimes[1] + '-' + endTimes[2] + '-' + endTimes[0] + ' ' + endTime.substring(10, 19);
    var a = (Date.parse(endTime) - Date.parse(beginTime)) / 3600 / 1000;
    if(a < 0||a == 0){
 		//$("#TS_JHGL-JH_CREATEDATE").addClass("blankError").addClass("errorbox");
 		//$("#TS_JHGL-JH_ENDDATE").addClass("blankError").addClass("errorbox");
 		$("#TS_JHGL-JH_CREATEDATE").parent().showError("开始时间应早于结束时间");
 		$("#TS_JHGL-JH_ENDDATE").parent().showError("结束时间应晚于开始时间");
		return false;
 	}
};