var _viewer = this;

var sel = _viewer.getItem("QJKLC_SEL").getValue();
if(sel == 0){
	_viewer.getItem("QJKLC_SHR").hide();
	_viewer.getItem("QJKLC_SHQZ").hide();
}else if(sel == 1){
	_viewer.getItem("QJKLC_SHR").show();
	_viewer.getItem("QJKLC_SHQZ").hide();
}else{
	_viewer.getItem("QJKLC_SHR").hide();
	_viewer.getItem("QJKLC_SHQZ").show();
}

_viewer.getItem("QJKLC_SEL").change(function(){
	sel = _viewer.getItem("QJKLC_SEL").getValue();
	if(sel == 0){
		_viewer.getItem("QJKLC_SHR").hide();
		_viewer.getItem("QJKLC_SHQZ").hide();
	}else if(sel == 1){
		_viewer.getItem("QJKLC_SHR").show();
		_viewer.getItem("QJKLC_SHQZ").hide();
	}else{
		_viewer.getItem("QJKLC_SHR").hide();
		_viewer.getItem("QJKLC_SHQZ").show();
	}
});