_viewer=this;
_viewer.beforeSave = function() {debugger;
	var XmId=_viewer.getItem("XM_ID").getValue();
	var param={};
	param["_extWhere"] = "and XM_ID ='"+XmId+"'";
	var beanFb = FireFly.doAct("TS_XMGL_BMGL", "query", param);
	var bmStart=beanFb._DATA_[0].BM_START;//报名设置开始时间
	var bmEnd=beanFb._DATA_[0].BM_END;//报名设置结束时间
	
	var begin=_viewer.getItem("G_DEAD_BEGIN").getValue();//有效期开始
	var end=_viewer.getItem("G_DEAD_END").getValue();//有效期开始
	var begins = begin.substring(0,10).split('-');
    var ends = end.substring(0,10).split('-');
    var bmStartArray = bmStart.substring(0,10).split('-');
    var bmEndArray = bmEnd.substring(0,10).split('-');
    var  beginTime= begins[1] + '-' + begins[2] + '-' + begins[0] +' '+begin.substring(10,19);
    var endTime = ends[1] + '-' + ends[2] + '-' + ends[0] +' '+end.substring(10,19) ;
    
    var  startArrayTimes= bmStartArray[1] + '-' + bmStartArray[2] + '-' + bmStartArray[0] +' '+bmStart.substring(10,19);
    var endArrayTimes = bmEndArray[1] + '-' + bmEndArray[2] + '-' + bmEndArray[0] +' '+bmEnd.substring(10,19) ;
      //通知开始时间和通知结束时间互斥
    var endBegin = (Date.parse(endTime) - Date.parse(beginTime)) / 3600 / 1000;
    if(endBegin< 0||endBegin== 0){
 		//$("#TS_BM_GROUP-G_DEAD_BEGIN").addClass("blankError").addClass("errorbox");
 		//$("#TS_BM_GROUP-G_DEAD_END").addClass("blankError").addClass("errorbox");
 		$("#TS_BM_GROUP-G_DEAD_BEGIN").parent().showError("报名开始时间应早于报名结束时间");
 		$("#TS_BM_GROUP-G_DEAD_END").parent().showError("报名结束时间应晚于报名开始时间");
		return false;
 	}
    
    //
    var beginStart = (Date.parse(beginTime) - Date.parse(startArrayTimes)) / 3600 / 1000;
    if(beginStart< 0){
    	$("#TS_BM_GROUP-G_DEAD_BEGIN").parent().showError("报名开始时间应晚于"+bmStart);
    	//$("#TS_BM_GROUP-G_DEAD_BEGIN").addClass("blankError").addClass("errorbox");报名管理的报名开始时间
    	
    	return false;
    }
    
    var endsTimes = (Date.parse(endArrayTimes) - Date.parse(endTime)) / 3600 / 1000;
    if(endsTimes< 0){
    	$("#TS_BM_GROUP-G_DEAD_END").parent().showError("报名结束时间应早于"+bmEnd);
    	//$("#TS_BM_GROUP-G_DEAD_END").addClass("blankError").addClass("errorbox");报名管理的报名结束时间
    	
    	return false;
    }
    
};   
//时间表单校验moveclass
//_viewer.getItem("G_DEAD_BEGIN").obj.unbind("click").bind("click", function() {
//	WdatePicker({
//		dateFmt: 'yyyy-MM-dd HH:mm:ss',
//		maxDate : "#F{$dp.$D('" + _viewer.servId + "-G_DEAD_END')}"
//	});
//});
//_viewer.getItem("G_DEAD_END").obj.unbind("click").bind("click", function() {
//
//	WdatePicker({
//		dateFmt: 'yyyy-MM-dd HH:mm:ss',
//		minDate : "#F{$dp.$D('" + _viewer.servId + "-G_DEAD_BEGIN')}"
//	});
//});
