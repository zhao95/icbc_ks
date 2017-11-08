var _viewer = this;
//借考 值为2
var type = _viewer.getParHandler().getParHandler().getParHandler().getParHandler().getItem("WFS_SERVID").getValue();
//获取wfs_ID,并保存
if(_viewer.opts.act == "cardAdd"){
	var WFS_ID = _viewer.opts.WFS_ID;
	var NODE_ID = _viewer.opts.NODE_ID;
	if(typeof(WFS_ID)!="undefined"){ 
		_viewer.getItem("WFS_ID").setValue(WFS_ID);
	}
	if(typeof(NODE_ID)!="undefined"){ 
		_viewer.getItem("NODE_ID").setValue(NODE_ID);
	}
}

var sel = _viewer.getItem("QJKLC_SEL").getValue();
if(sel == 0){ //部门
	_viewer.getItem("QJKLC_SHR").hide();
	_viewer.getItem("QJKLC_SHQZ").hide();
	_viewer.showGroup("dept");
	_viewer.showGroup("post");
	_viewer.getItem("QJKLC_ZDDEPT_COLCODE").hide();
	_viewer.getItem("QJKLC_QZDEPT_CODE").hide();
}else if(sel == 1){//人
	_viewer.getItem("QJKLC_SHR").show();
	_viewer.getItem("QJKLC_SHQZ").hide();
	_viewer.hideGroup("dept");
	_viewer.hideGroup("post");
	_viewer.getItem("QJKLC_ZDDEPT_COLCODE").hide();
	_viewer.getItem("QJKLC_QZDEPT_CODE").hide();
}else{ //群组
	_viewer.getItem("QJKLC_SHR").hide();
	_viewer.getItem("QJKLC_SHQZ").show();
	_viewer.hideGroup("dept");
	_viewer.hideGroup("post");
	
	if(type == 2){ //如果借考
		_viewer.getItem("QJKLC_QZDEPT_CODE").show(); //隐藏机构层级
		_viewer.getItem("QJKLC_ZDDEPT_COLCODE").show();//显示用于被借考审核
	} else {
		_viewer.getItem("QJKLC_ZDDEPT_COLCODE").hide();
		_viewer.getItem("QJKLC_QZDEPT_CODE").show();
	}
}

_viewer.getItem("QJKLC_SEL").change(function(){
	sel = _viewer.getItem("QJKLC_SEL").getValue();
	if(sel == 0){ //部门
		_viewer.getItem("QJKLC_SHR").hide();
		_viewer.getItem("QJKLC_SHQZ").hide();
		_viewer.showGroup("dept");
		_viewer.showGroup("post");
		_viewer.getItem("QJKLC_ZDDEPT_COLCODE").hide();
		_viewer.getItem("QJKLC_QZDEPT_CODE").hide();
	}else if(sel == 1){ //人
		_viewer.getItem("QJKLC_SHR").show();
		_viewer.getItem("QJKLC_SHQZ").hide();
		_viewer.hideGroup("dept");
		_viewer.hideGroup("post");
		_viewer.getItem("QJKLC_ZDDEPT_COLCODE").hide();
		_viewer.getItem("QJKLC_QZDEPT_CODE").hide();
	}else{ //群组
		_viewer.getItem("QJKLC_SHR").hide();
		_viewer.getItem("QJKLC_SHQZ").show();
		_viewer.hideGroup("dept");
		_viewer.hideGroup("post");
		
		if(type == 2){ //如果借考
			_viewer.getItem("QJKLC_QZDEPT_CODE").show(); //隐藏机构层级
			_viewer.getItem("QJKLC_ZDDEPT_COLCODE").show();//显示用于被借考审核
		} else {
			_viewer.getItem("QJKLC_ZDDEPT_COLCODE").hide();
			_viewer.getItem("QJKLC_QZDEPT_CODE").show();
		}
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