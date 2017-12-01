var _viewer = this;
//针对开始和结束时间的校验
//_viewer.getItem("FZGKS_STADATE").obj.unbind("click").bind("click", function() {
//	    WdatePicker({
//	        maxDate : "#F{$dp.$D('" + _viewer.servId + "-FZGKS_ENDDATE')}"
//	    });
//	});
//_viewer.getItem("FZGKS_ENDDATE").obj.unbind("click").bind("click", function() {
//
//	    WdatePicker({
//	        minDate : "#F{$dp.$D('" + _viewer.servId + "-FZGKS_STADATE')}"
//	    });
//	});
 
//针对开始和结束时间的校验_viewer.afterSave
_viewer.beforeSave = function() {
	var beginTime=_viewer.getItem("FZGKS_STADATE").getValue();
	var endTime=_viewer.getItem("FZGKS_ENDDATE").getValue();
    var beginTimes = beginTime.substring(0, 10).split('-');
    var endTimes = endTime.substring(0, 10).split('-');
     beginTime = beginTimes[1] + '-' + beginTimes[2] + '-' + beginTimes[0] + ' ' + beginTime.substring(10, 19);
     endTime = endTimes[1] + '-' + endTimes[2] + '-' + endTimes[0] + ' ' + endTime.substring(10, 19);
    var a = (Date.parse(endTime) - Date.parse(beginTime)) / 3600 / 1000;
    if(a < 0||a == 0){
    	$("#TS_XMGL_BM_FZGKS-FZGKS_STADATE").parent().showError("开始时间应早于结束时间");
 		$("#TS_XMGL_BM_FZGKS-FZGKS_ENDDATE").parent().showError("结束时间应晚于开始时间");
 		//$("#TS_XMGL_BM_FZGKS-FZGKS_STADATE").addClass("blankError").addClass("errorbox");
 		//$("#TS_XMGL_BM_FZGKS-FZGKS_ENDDATE").addClass("blankError").addClass("errorbox");
		return false;
 	}
};


