var _viewer = this;

if(_viewer.opts.act == "cardAdd"){
	var XM_SZ_ID = _viewer.opts.XM_SZ_ID;
	if(typeof(XM_SZ_ID)!="undefined"){ 
		_viewer.getItem("XM_SZ_ID").setValue(XM_SZ_ID);
	}
	
	var XM_ID = _viewer.opts.XM_ID;
	if(typeof(XM_ID)!="undefined"){ 
		_viewer.getItem("XM_ID").setValue(XM_ID);
	}
}


$("#TS_XMGL_BMSH-SH_LOOK_label").css("width","300px");
$("#TS_XMGL_BMSH-SH_LOOK_div").css("padding-left","100px");
//针对开始和结束时间的校验
_viewer.beforeSave = function() {
	
	var  xmRgsh=_viewer.getItem("SH_RGSH").getValue();//人工审核2否
	 var xmZdsh=_viewer.getItem("SH_ZDSH").getValue();//自动审核 
	var beginTime=_viewer.getItem("SH_START").getValue();
	var endTime=_viewer.getItem("SH_END").getValue();
    var beginTimes = beginTime.substring(0, 10).split('-');
    var endTimes = endTime.substring(0, 10).split('-');
     beginTime = beginTimes[1] + '-' + beginTimes[2] + '-' + beginTimes[0] + ' ' + beginTime.substring(10, 19);
     endTime = endTimes[1] + '-' + endTimes[2] + '-' + endTimes[0] + ' ' + endTime.substring(10, 19);
    var a = (Date.parse(endTime) - Date.parse(beginTime)) / 3600 / 1000;
    if(xmRgsh==1){
    if(a < 0||a == 0){
 		//$("#TS_XMGL_BMSH-SH_START").addClass("blankError").addClass("errorbox");
 		//$("#TS_XMGL_BMSH-SH_END").addClass("blankError").addClass("errorbox");
    	$("#TS_XMGL_BMSH-SH_START").parent().showError("审核开始时间应早于审核结束时间");
 		$("#TS_XMGL_BMSH-SH_END").parent().showError("审核结束时间应晚于审核开始时间");
		return false;
 	}
    }
    if(xmRgsh==2 && xmZdsh==2){
	alert("人工审核和自动审核都选择了'否'!");
    }
};
//_viewer.getItem("SH_START").obj.unbind("click").bind("click", function() {
//	    WdatePicker({
//		    dateFmt: 'yyyy-MM-dd HH:mm:ss',
//	        maxDate : "#F{$dp.$D('" + _viewer.servId + "-SH_END')}"
//	    });
//	});
//_viewer.getItem("SH_END").obj.unbind("click").bind("click", function() {
//	    WdatePicker({
//		    dateFmt: 'yyyy-MM-dd HH:mm:ss',
//	        minDate : "#F{$dp.$D('" + _viewer.servId + "-SH_START')}"
//	    });
//	});


//根据选择是否人工审核
var flowSerTmp = _viewer.getItem("SH_RGSH").getValue(); 
if(flowSerTmp == 1){
	_viewer.getItem("SH_FLOW").show();
	_viewer.getItem("SH_START").show();
	_viewer.getItem("SH_END").show();
}else if (flowSerTmp == 2){
	_viewer.getItem("SH_FLOW").hide();
	_viewer.getItem("SH_START").hide();
	_viewer.getItem("SH_END").hide();
}

_viewer.getItem("SH_RGSH").change(function(){
	flowSerTmp = _viewer.getItem("SH_RGSH").getValue(); 
	if(flowSerTmp == 1){
		_viewer.getItem("SH_FLOW").show();
		_viewer.getItem("SH_START").show();
		_viewer.getItem("SH_END").show();
	}else if (flowSerTmp == 2){
		_viewer.getItem("SH_FLOW").hide();
		_viewer.getItem("SH_START").hide();
		_viewer.getItem("SH_END").hide();
	}
});	
	
//根据选择是否自动审核做改变
var autoTmp=_viewer.getItem("SH_ZDSH").getValue();
var showResultTmp=_viewer.getItem("SH_TSY").getValue();
if(autoTmp==1){
	_viewer.getItem("SH_TSY").show();
	showResultTmp=_viewer.getItem("SH_TSY").getValue();
	if(showResultTmp==1){
		_viewer.getItem("SH_TGTSY").show();
		_viewer.getItem("SH_BTGTSY").show();
	}
}else if(autoTmp==2){
	_viewer.getItem("SH_TSY").hide();
	_viewer.getItem("SH_TGTSY").hide();
	_viewer.getItem("SH_BTGTSY").hide();
}
_viewer.getItem("SH_ZDSH").change(function(){
	autoTmp=_viewer.getItem("SH_ZDSH").getValue();
	if(autoTmp==1){
		_viewer.getItem("SH_TSY").show();
		showResultTmp=_viewer.getItem("SH_TSY").getValue();
		if(showResultTmp==1){
			_viewer.getItem("SH_TGTSY").show();
			_viewer.getItem("SH_BTGTSY").show();
		}
		$("a[opercode='optOption']").each(function(){
			$(this).css("display","block");
			$(this).css("margin-left","40%");
		});
	}else if(autoTmp==2){
		_viewer.getItem("SH_TSY").hide();
		_viewer.getItem("SH_TGTSY").hide();
		_viewer.getItem("SH_BTGTSY").hide();
		$("a[opercode='optOption']").each(function(){
			$(this).css("display","none");
		});
	}
});


//根据选择是否立即显示审核结果
//if(showResultTmp==1){
//	_viewer.getItem("SH_TGTSY").hide();
//	_viewer.getItem("SH_BTGTSY").hide();
//}else if(showResultTmp==2){
//	_viewer.getItem("SH_TGTSY").show();
//	_viewer.getItem("SH_BTGTSY").show();
//}
_viewer.getItem("SH_TSY").change(function(){
	showResultTmp=_viewer.getItem("SH_TSY").getValue();
	if(showResultTmp==2){
		_viewer.getItem("SH_TGTSY").hide();
		_viewer.getItem("SH_BTGTSY").hide();
	}else if(showResultTmp==1){
		_viewer.getItem("SH_TGTSY").show();
		_viewer.getItem("SH_BTGTSY").show();
	}
});
//保存自后TS_XMGL_BMSH-SH_END
_viewer.afterSave=function(){
	var XM_SZ_ID = _viewer.opts.XM_SZ_ID;
	var XM_ID=_viewer.opts.XM_ID;
	var shRgsh=_viewer.getItem("SH_RGSH").getValue();
	var bmStartTime=_viewer.getItem("SH_START").getValue();
	var bmEndTime=_viewer.getItem("SH_END").getValue();
	if(shRgsh==2){
	var param={};
	param["xmid"]=XM_ID;
	param["xmszid"]=XM_SZ_ID;
	FireFly.doAct("TS_XMGL_SZ","deleteTimes",param,true,false);
	}
	if(shRgsh==1){
	if(bmStartTime && bmEndTime){
		var bmTime=bmStartTime+"  至  "+bmEndTime;
		var param={};
		//(String xmszExplain,String xmid,String xmszid)
		param["xmszExplain"]=bmTime;
		param["xmid"]=XM_ID;
		param["xmszid"]=XM_SZ_ID;
		FireFly.doAct("TS_XMGL_SZ","getTimes",param,true,false);	
	}
	}
}