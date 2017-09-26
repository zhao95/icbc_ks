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

if(autoTmp==1){
	_viewer.getItem("SH_TSY").show();
}else if(autoTmp==2){
	_viewer.getItem("SH_TSY").hide();
}

_viewer.getItem("SH_ZDSH").change(function(){
	autoTmp=_viewer.getItem("SH_ZDSH").getValue();
	if(autoTmp==1){
		_viewer.getItem("SH_TSY").show();
	}else if(autoTmp==2){
		_viewer.getItem("SH_TSY").hide();
	}
});