_viewer=this;
//有效期和有效期结束校验
_viewer.beforeSave = function() {
	var bmStart=_viewer.getItem("G_DEAD_BEGIN").getValue();//有效期开始时间
	var bmEnd=_viewer.getItem("G_DEAD_END").getValue();//有效期结束时间
	var bmStarts = bmStart.split('-');
    var bmEnds = bmEnd.split('-');
    bmStart = bmStarts[1] + '-' + bmStarts[2] + '-' + bmStarts[0] ;
    bmEnd = bmEnds[1] + '-' + bmEnds[2] + '-' + bmEnds[0] ;
    //报名开始时间和报名结束时间互斥
    var bmEndbmStart = (Date.parse(bmEnd) - Date.parse(bmStart)) / 3600 / 1000;
    if(bmEndbmStart< 0||bmEndbmStart== 0||isNaN(bmEndbmStart)){
 		$("#TS_PVLG_GROUP-G_DEAD_BEGIN").addClass("blankError").addClass("errorbox");
 		$("#TS_PVLG_GROUP-G_DEAD_END").addClass("blankError").addClass("errorbox");
		return false;
 	}
};