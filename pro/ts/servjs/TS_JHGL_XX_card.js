var _viewer = this;
//针对项目开始时间的校验与互斥
_viewer.beforeSave = function() {
	var bmStart=_viewer.getItem("JH_BM_STARTDATE").getValue();//报名开始时间
	var bmEnd=_viewer.getItem("JH_BM_ENDDATE").getValue();//报名结束时间
	var KsStartData=_viewer.getItem("JH_CREATEDATE").getValue();//考试开始时间
	var KsEndData=_viewer.getItem("JH_ENDDATE").getValue();//考试截至时间
	var bmStarts = bmStart.split('-');
    var bmEnds = bmEnd.split('-');
    var KsStartDatas = KsStartData.split('-');
    var KsEndDatas = KsEndData.split('-');
    bmStart = bmStarts[1] + '-' + bmStarts[2] + '-' + bmStarts[0] ;
    bmEnd = bmEnds[1] + '-' + bmEnds[2] + '-' + bmEnds[0] ;
    KsStartData=KsStartDatas[1] + '-' + KsStartDatas[2] + '-' + KsStartDatas[0];
    KsEndData= KsEndDatas[1] + '-' + KsEndDatas[2] + '-' + KsEndDatas[0] ;
    //报名开始时间和报名结束时间互斥
    var bmEndbmStart = (Date.parse(bmEnd) - Date.parse(bmStart)) / 3600 / 1000;
    if(bmEndbmStart< 0||bmEndbmStart== 0){
 		//$("#TS_JHGL_XX-JH_BM_STARTDATE").addClass("blankError").addClass("errorbox");
 		//$("#TS_JHGL_XX-JH_BM_ENDDATE").addClass("blankError").addClass("errorbox");
 		$("#TS_JHGL_XX-JH_BM_STARTDATE").parent().showError("报名开始时间应早于报名结束时间");
 		$("#TS_JHGL_XX-JH_BM_ENDDATE").parent().showError("报名结束时间应晚于报名开始时间");
		return false;
 	}
    //考试开始时间和考试结束时间互斥
    var ksEndKsStart = (Date.parse(KsEndData) - Date.parse(KsStartData)) / 3600 / 1000;
    if(ksEndKsStart< 0||ksEndKsStart== 0){
 		//$("#TS_JHGL_XX-JH_CREATEDATE").addClass("blankError").addClass("errorbox");
 		//$("#TS_JHGL_XX-JH_ENDDATE").addClass("blankError").addClass("errorbox");
 		$("#TS_JHGL_XX-JH_CREATEDATE").parent().showError("考试开始时间应早于考试结束时间");
 		$("#TS_JHGL_XX-JH_ENDDATE").parent().showError("考试结束时间应晚于考试开始时间");
		return false;
 	}
    //报名结束时间和考试开始时间互斥
    var ksStartbmEnd = (Date.parse(KsStartData) - Date.parse(bmEnd)) / 3600 / 1000;
    if(ksStartbmEnd< 0){
 		//$("#TS_JHGL_XX-JH_CREATEDATE").addClass("blankError").addClass("errorbox");
 		//$("#TS_JHGL_XX-JH_BM_ENDDATE").addClass("blankError").addClass("errorbox");
    	$("#TS_JHGL_XX-JH_CREATEDATE").parent().showError("考试开始时间不应早于报名结束时间");
    	$("#TS_JHGL_XX-JH_BM_ENDDATE").parent().showError("报名结束时间不应晚于考试开始时间");
		return false;
 	}
   
};