var _viewer = this;

var sel = _viewer.getItem("QJKLC_SEL").getValue();
if(sel == 0){
	_viewer.getItem("QJKLC_SHR").hide();
	_viewer.getItem("QJKLC_SHQZ").hide();
	_viewer.showGroup("dept");
	_viewer.showGroup("post");
}else if(sel == 1){
	_viewer.getItem("QJKLC_SHR").show();
	_viewer.getItem("QJKLC_SHQZ").hide();
	_viewer.hideGroup("dept");
	_viewer.hideGroup("post");
}else{
	_viewer.getItem("QJKLC_SHR").hide();
	_viewer.getItem("QJKLC_SHQZ").show();
	_viewer.hideGroup("dept");
	_viewer.hideGroup("post");
}

_viewer.getItem("QJKLC_SEL").change(function(){
	sel = _viewer.getItem("QJKLC_SEL").getValue();
	if(sel == 0){
		_viewer.getItem("QJKLC_SHR").hide();
		_viewer.getItem("QJKLC_SHQZ").hide();
		_viewer.showGroup("dept");
		_viewer.showGroup("post");
	}else if(sel == 1){
		_viewer.getItem("QJKLC_SHR").show();
		_viewer.getItem("QJKLC_SHQZ").hide();
		_viewer.hideGroup("dept");
		_viewer.hideGroup("post");
	}else{
		_viewer.getItem("QJKLC_SHR").hide();
		_viewer.getItem("QJKLC_SHQZ").show();
		_viewer.hideGroup("dept");
		_viewer.hideGroup("post");
	}
});

var selDept = _viewer.getItem("QJKLC_SEL_DEPT").getValue();
if(selDept == 0){
	_viewer.getItem("QJKLC_YDDEPT").show();
	_viewer.getItem("QJKLC_ZDDEPT").hide();
}else{
	_viewer.getItem("QJKLC_YDDEPT").hide();
	_viewer.getItem("QJKLC_ZDDEPT").show();
}

_viewer.getItem("QJKLC_SEL_DEPT").change(function(){
	selDept = _viewer.getItem("QJKLC_SEL_DEPT").getValue();
	if(selDept == 0){
		_viewer.getItem("QJKLC_YDDEPT").show();
		_viewer.getItem("QJKLC_ZDDEPT").hide();
	}else{
		_viewer.getItem("QJKLC_YDDEPT").hide();
		_viewer.getItem("QJKLC_ZDDEPT").show();
	}
});