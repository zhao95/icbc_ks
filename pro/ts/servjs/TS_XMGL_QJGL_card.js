var _viewer = this;
var XM_SZ_ID = _viewer.opts.XM_SZ_ID;
if(_viewer.opts.act == "cardAdd"){
	XM_SZ_ID = _viewer.opts.XM_SZ_ID;
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
	var beginTime=_viewer.getItem("QJ_STADATE").getValue();
	var endTime=_viewer.getItem("QJ_ENDDATE").getValue();
    var beginTimes = beginTime.substring(0, 10).split('-');
    var endTimes = endTime.substring(0, 10).split('-');
     beginTime = beginTimes[1] + '-' + beginTimes[2] + '-' + beginTimes[0] + ' ' + beginTime.substring(10, 19);
     endTime = endTimes[1] + '-' + endTimes[2] + '-' + endTimes[0] + ' ' + endTime.substring(10, 19);
    var a = (Date.parse(endTime) - Date.parse(beginTime)) / 3600 / 1000;
    if(a < 0||a == 0){
 		$("#TS_XMGL_QJGL-QJ_STADATE").addClass("blankError").addClass("errorbox");
 		$("#TS_XMGL_QJGL-QJ_ENDDATE").addClass("blankError").addClass("errorbox");
		return false;
 	}
};





