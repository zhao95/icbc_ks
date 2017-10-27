_viewer=this;

_viewer.beforeSave = function() {
	var begin=_viewer.getItem("G_DEAD_BEGIN").getValue();//通知开始时间
	var end=_viewer.getItem("G_DEAD_END").getValue();//通知截至时间
	var begins = begin.substring(0,10).split('-');
    var ends = end.substring(0,10).split('-');
    var  beginTime= begins[1] + '-' + begins[2] + '-' + begins[0] +' '+begin.substring(10,19);
    var endTime = ends[1] + '-' + ends[2] + '-' + ends[0] +' '+end.substring(10,19) ;
      //通知开始时间和通知结束时间互斥
    var endBegin = (Date.parse(endTime) - Date.parse(beginTime)) / 3600 / 1000;
    if(endBegin< 0||endBegin== 0){
 		$("#TS_BM_GROUP-G_DEAD_BEGIN").addClass("blankError").addClass("errorbox");
 		$("#TS_BM_GROUP-G_DEAD_END").addClass("blankError").addClass("errorbox");
		return false;
 	}
};   
//时间表单校验
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
