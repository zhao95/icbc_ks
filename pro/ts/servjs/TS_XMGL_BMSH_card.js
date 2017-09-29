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

//针对开始和结束时间的校验
_viewer.getItem("SH_START").obj.unbind("click").bind("click", function() {
	    WdatePicker({
		    dateFmt: 'yyyy-MM-dd HH:mm:ss',
	        maxDate : "#F{$dp.$D('" + _viewer.servId + "-SH_END')}"
	    });
	});
_viewer.getItem("SH_END").obj.unbind("click").bind("click", function() {
	    WdatePicker({
		    dateFmt: 'yyyy-MM-dd HH:mm:ss',
	        minDate : "#F{$dp.$D('" + _viewer.servId + "-SH_START')}"
	    });
	});


//根据选择是否人工审核
var flowSerTmp = _viewer.getItem("SH_RGSH").getValue(); 
if(flowSerTmp == 1){
	_viewer.getItem("SH_FLOW").show();
}else if (flowSerTmp == 2){
	_viewer.getItem("SH_FLOW").hide();
}

_viewer.getItem("SH_RGSH").change(function(){
	flowSerTmp = _viewer.getItem("SH_RGSH").getValue(); 
	if(flowSerTmp == 1){
		_viewer.getItem("SH_FLOW").show();
	}else if (flowSerTmp == 2){
		_viewer.getItem("SH_FLOW").hide();
	}
});	
	
//根据选择是否自动审核做改变
var autoTmp=_viewer.getItem("SH_ZDSH").getValue();
var showResultTmp=_viewer.getItem("SH_TSY").getValue();
if(autoTmp==1){
	_viewer.getItem("SH_TSY").show();
	showResultTmp=_viewer.getItem("SH_TSY").getValue();
	if(showResultTmp==2){
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
		if(showResultTmp==2){
			_viewer.getItem("SH_TGTSY").show();
			_viewer.getItem("SH_BTGTSY").show();
		}
	}else if(autoTmp==2){
		_viewer.getItem("SH_TSY").hide();
		_viewer.getItem("SH_TGTSY").hide();
		_viewer.getItem("SH_BTGTSY").hide();
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
	if(showResultTmp==1){
		_viewer.getItem("SH_TGTSY").hide();
		_viewer.getItem("SH_BTGTSY").hide();
	}else if(showResultTmp==2){
		_viewer.getItem("SH_TGTSY").show();
		_viewer.getItem("SH_BTGTSY").show();
	}
});