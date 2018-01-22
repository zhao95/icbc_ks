_viewer = this;
$("#TS_BMSH_RULE-GOTO_STAY_label").find("span:first").css("width","250px");
$("#TS_BMSH_RULE-GOTO_STAY_div").css("margin-left","10.5%");
$("#TS_BMSH_RULE-R_ZD_label").find("span:first").css("width","1000px");
$("#TS_BMSH_RULE-R_ZD_div").css("margin-left","2%");
$("#TS_BMSH_RULE-R_TYPE_label").find("span:first").css("width","1000px");
$("#TS_BMSH_RULE-R_TYPE_div").css("margin-left","2%");
$("#TS_BMSH_RULE-CONDITION_TWO_div").find("span:last").find("div").css("width","50px");
$("#TS_BMSH_RULE-CONDITION_ONE_div").find("span:last").find("div").css("width","50px");
$("#TS_BMSH_RULE-VALID_START_div").css("margin-left","-30%");
$("#TS_BMSH_RULE-VALID_END_div").css("margin-left","-30%");
_viewer.beforeSave = function() {
	var beginTime=_viewer.getItem("VALID_START").getValue();
	var endTime=_viewer.getItem("VALID_END").getValue();
	var beginTimes = beginTime.substring(0, 10).split('-');
	var endTimes = endTime.substring(0, 10).split('-');
	 	beginTime = beginTimes[1] + '-' + beginTimes[2] + '-' + beginTimes[0] + ' ' + beginTime.substring(10, 19);
    endTime = endTimes[1] + '-' + endTimes[2] + '-' + endTimes[0] + ' ' + endTime.substring(10, 19);
    var a = (Date.parse(endTime) - Date.parse(beginTime)) / 3600 / 1000;
    if(a < 0||a == 0){
 		//$("#TS_XMGL_BMSH-SH_START").addClass("blankError").addClass("errorbox");
 		//$("#TS_XMGL_BMSH-SH_END").addClass("blankError").addClass("errorbox");
    	$("#TS_BMSH_RULE-VALID_START").parent().showError("证书有效开始时间应早于有效结束时间");
 		$("#TS_BMSH_RULE-VALID_END").parent().showError("证书有效结束时间应晚于有效开始时间");
		return false;
 	}
};