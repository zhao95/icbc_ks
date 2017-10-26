var _viewer = this;

//打开自服务列表
if(typeof(_viewer.opts.paramData) !="undefined"){ 
	var sid = _viewer.opts.paramData.showTab;
	if(sid != ""){
		var topObj = jQuery("li.rhCard-tabs-topLi[sid='" + sid + "']",_viewer.tabs);
		topObj.find("a").click();
	}
}