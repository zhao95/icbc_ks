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