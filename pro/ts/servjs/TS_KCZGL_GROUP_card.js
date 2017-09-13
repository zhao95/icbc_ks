var _viewer = this;
//设置卡片只读
if(_viewer.opts.readOnly){
	_viewer.readCard();
}

if(_viewer.getItem("SERV_ID").getValue() == ""){
	_viewer.getItem("SERV_ID").setValue(_viewer.servId);
}

var handler = _viewer.getParHandler();

//打开自服务列表
if(typeof(_viewer.opts.showTab) !="undefined"){ 
	var sid = _viewer.opts.showTab;
	if(sid != ""){
		var topObj = jQuery("li.rhCard-tabs-topLi[sid='" + sid + "']",_viewer.tabs);
		topObj.find("a").click();
	}
}

if(_viewer._actVar==UIConst.ACT_CARD_ADD){
	var pdataId =  _viewer.getParHandler().getParHandler().getPKCode();
	_viewer.getItem("KCZ_ID").setValue(pdataId);
}
