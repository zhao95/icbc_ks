var _viewer = this;


if(_viewer.opts.readOnly){
	_viewer.readCard();
}

var mxCol = _viewer.getItem("MX_COL").getValue();
if(mxCol == "KC_ODEPTCODE"){
	_viewer.getItem("MX_DATA").hide();
	_viewer.getItem("MX_DATA3").show();
}else if(mxCol == "KC_LEVEL"){
	_viewer.getItem("MX_DATA").hide();
	_viewer.getItem("MX_DATA4").show();
}

_viewer.getItem("MX_COL").change(function(){
	mxCol = _viewer.getItem("MX_COL").getValue();
	if(mxCol == "KC_ODEPTCODE"){
		_viewer.getItem("MX_DATA").hide();
		_viewer.getItem("MX_DATA3").show();
		_viewer.getItem("MX_DATA4").hide();
	}else if(mxCol == "KC_LEVEL"){
		_viewer.getItem("MX_DATA").hide();
		_viewer.getItem("MX_DATA3").hide();
		_viewer.getItem("MX_DATA4").show();
	}else{
		_viewer.getItem("MX_DATA").show();
		_viewer.getItem("MX_DATA3").hide();
		_viewer.getItem("MX_DATA4").hide();
	}
});

_viewer.afterSave = function(resultData) {
	setTimeout(function(){ 
		_viewer._parHandler.refreshGrid(); 
		jQuery("#" + _viewer.dialogId).dialog("close");
	}, 100);
};
