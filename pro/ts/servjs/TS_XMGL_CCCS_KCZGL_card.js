var _viewer = this;
if(_viewer.getItem("SERV_ID").getValue() == ""){
	_viewer.getItem("SERV_ID").setValue(_viewer.servId);
}
//设置卡片只读
if(_viewer.opts.readOnly){
	_viewer.readCard();
}

//打开自服务列表
if(typeof(_viewer.opts.paramData) !="undefined"){ 
	var sid = _viewer.opts.paramData.showTab;
	if(sid != ""){
		var topObj = jQuery("li.rhCard-tabs-topLi[sid='" + sid + "']",_viewer.tabs);
		topObj.find("a").click();
	}
}