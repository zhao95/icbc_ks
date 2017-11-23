var _viewer = this;
//增加校验后，时间控件不现实分钟
//_viewer.getItem("SJ_START").obj.unbind("click").bind("click", function() {
//	WdatePicker({
//		maxDate : "#F{$dp.$D('" + _viewer.servId + "-SJ_END')}"
//	});
//});
//_viewer.getItem("SJ_END").obj.unbind("click").bind("click", function() {
//	WdatePicker({
//		minDate : "#F{$dp.$D('" + _viewer.servId + "-SJ_START')}"
//	});
//});
针对开始和结束时间的校验
_viewer.beforeSave = function() {
	var beginTime=_viewer.getItem("SJ_START").getValue();
	var endTime=_viewer.getItem("SJ_END").getValue();
    var beginTimes = beginTime.substring(0, 10).split('-');
    var endTimes = endTime.substring(0, 10).split('-');
     beginTime = beginTimes[1] + '-' + beginTimes[2] + '-' + beginTimes[0] + ' ' + beginTime.substring(10, 19);
     endTime = endTimes[1] + '-' + endTimes[2] + '-' + endTimes[0] + ' ' + endTime.substring(10, 19);
    var a = (Date.parse(endTime) - Date.parse(beginTime)) / 3600 / 1000;
    if(a < 0||a == 0){
 		$("#TS_XMGL_KCAP_DAPCC_CCSJ-SJ_START").addClass("blankError").addClass("errorbox");
 		$("#TS_XMGL_KCAP_DAPCC_CCSJ-SJ_END").addClass("blankError").addClass("errorbox");
		return false;
 	}
};

//_viewer.beforeSave = function() {
//var SJCC=_viewer.getItem("SJ_CC").getValue();//场次
//var sjDate=_viewer.getItem("SJ_DATE").getValue();//日期
//var sjKsTime=_viewer.getItem("SJ_KSTIME").getValue();//起始时间
//var sjsc=_viewer.getItem("SJ_SC").getValue();//时长
//var start = sjDate + " " +sjKsTime;//
//var end=addMin(start,sjsc);
//
//_viewer.getItem("SJ_START").setValue(start);
//_viewer.getItem("SJ_END").setValue(end);
////_viewer.getParHandler().refresh();
////_viewer.backA.mousedown();
//}
//_viewer.afterSave = function() {
//_viewer.getParHandler().refresh();
//}
function addMin(a,b){
	b = parseInt(b);
	var atime = a.replace(/-/g, "/");
	var date = new Date(atime);
	date.setMinutes(date.getMinutes()+b);
   	return rhDate.patternData("yyyy-MM-dd HH:mm",date);
}
checkShow();
var xmId = _viewer.getParHandler().getParHandler().getParHandler().getParHandler().getPKCode()
_viewer.getItem("XM_ID").setValue(xmId);

if(_viewer.getItem("SJ_CC").getValue() == ""){
	_viewer.getItem("SJ_ADDTYPE").setValue(1);
}

if(_viewer.getItem("SJ_ADDTYPE").getValue() == 0){
	$("#TS_XMGL_KCAP_DAPCC_CCSJ-SJ_CC").attr("disabled","disabled"); 
	$("#TS_XMGL_KCAP_DAPCC_CCSJ-SJ_START").attr("disabled","disabled"); 
	$("#TS_XMGL_KCAP_DAPCC_CCSJ-SJ_END").attr("disabled","disabled"); 
}

_viewer.getItem("SJ_START").change(function(){
	checkShow();
});
_viewer.getItem("SJ_END").change(function(){
	checkShow();
});

function checkShow(){
	var start = _viewer.getItem("SJ_START").getValue();
	var end = _viewer.getItem("SJ_END").getValue();
	if(start != "" && end != ""){
		if(CountMin(start,end)){
			_viewer.getItem("SJ_XL_NAME").show();
		}else{
			_viewer.getItem("SJ_XL_NAME").hide();
		}
	}else{
		_viewer.getItem("SJ_XL_NAME").hide();
	}
}

function CountMin(val1,val2){
	var val1 = val1.substring(11).split(":");
	var val2 = val2.substring(11).split(":");
	var num1 = parseInt(val1[0])*60 + parseInt(val1[1]);
	var num2 = parseInt(val2[0])*60 + parseInt(val2[1]);
	
	if(num2-num1 >= 120){
		return true;
	}else{
		return false;
	}
}