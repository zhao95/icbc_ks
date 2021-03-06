_viewer=this;
//查看只读
if(_viewer.opts.readOnly == true){
	_viewer.readCard();
}

//打开自服务列表
if(typeof(_viewer.opts.showTab) !="undefined"){ 
	var sid = _viewer.opts.showTab;
	if(sid != ""){
		var topObj = jQuery("li.rhCard-tabs-topLi[sid='" + sid + "']",_viewer.tabs);
		topObj.find("a").click();
	}
}

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
     beginTime = beginTimes[1] + '-' + beginTimes[2] + '-' + beginTimes[0];
     endTime = endTimes[1] + '-' + endTimes[2] + '-' + endTimes[0];
    var a = (Date.parse(endTime) - Date.parse(beginTime)) / 3600 / 1000;
    if(a < 0||a == 0){
 		//$("#TS_JHGL-JH_CREATEDATE").addClass("blankError").addClass("errorbox");
 		//$("#TS_JHGL-JH_ENDDATE").addClass("blankError").addClass("errorbox");
 		$("#TS_JHGL-JH_CREATEDATE").parent().showError("开始时间应早于结束时间");
 		$("#TS_JHGL-JH_ENDDATE").parent().showError("结束时间应晚于开始时间");
		return false;
 	}
};
//保存按钮之后，关闭卡片
_viewer.afterSave = function(){
	setTimeout(function(){
		_viewer.backA.mousedown();
	},100);
};

//_viewer.getBtn("save").click(function(){
//	setTimeout(function(){
//		_viewer.backA.mousedown();
//	},500);
//});