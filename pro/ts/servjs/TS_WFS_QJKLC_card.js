var _viewer = this;
//获取wfs_ID,并保存
if(_viewer.opts.act == "cardAdd"){
	var WFS_ID = _viewer.opts.WFS_ID;
	var NODE_ID = _viewer.opts.WFS_ID;
	if(typeof(WFS_ID)!="undefined"){ 
		_viewer.getItem("WFS_ID").setValue(WFS_ID);
	}
	if(typeof(NODE_ID)!="undefined"){ 
		_viewer.getItem("NODE_ID").setValue(NODE_ID);
	}
}

var sel = _viewer.getItem("QJKLC_SEL").getValue();
if(sel == 0){
	_viewer.getItem("QJKLC_SHR").hide();
	_viewer.getItem("QJKLC_SHQZ").hide();
	_viewer.showGroup("dept");
	_viewer.showGroup("post");
	_viewer.getItem("QJKLC_ZDDEPT_COLCODE").hide();
}else if(sel == 1){
	_viewer.getItem("QJKLC_SHR").show();
	_viewer.getItem("QJKLC_SHQZ").hide();
	_viewer.hideGroup("dept");
	_viewer.hideGroup("post");
	_viewer.getItem("QJKLC_ZDDEPT_COLCODE").hide();
}else{
	_viewer.getItem("QJKLC_SHR").hide();
	_viewer.getItem("QJKLC_SHQZ").show();
	_viewer.hideGroup("dept");
	_viewer.hideGroup("post");
	_viewer.getItem("QJKLC_ZDDEPT_COLCODE").show();
}

_viewer.getItem("QJKLC_SEL").change(function(){
	sel = _viewer.getItem("QJKLC_SEL").getValue();
	if(sel == 0){
		_viewer.getItem("QJKLC_SHR").hide();
		_viewer.getItem("QJKLC_SHQZ").hide();
		_viewer.showGroup("dept");
		_viewer.showGroup("post");
		_viewer.getItem("QJKLC_ZDDEPT_COLCODE").hide();
	}else if(sel == 1){
		_viewer.getItem("QJKLC_SHR").show();
		_viewer.getItem("QJKLC_SHQZ").hide();
		_viewer.hideGroup("dept");
		_viewer.hideGroup("post");
		_viewer.getItem("QJKLC_ZDDEPT_COLCODE").hide();
	}else{
		_viewer.getItem("QJKLC_SHR").hide();
		_viewer.getItem("QJKLC_SHQZ").show();
		_viewer.hideGroup("dept");
		_viewer.hideGroup("post");
		_viewer.getItem("QJKLC_ZDDEPT_COLCODE").show();
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