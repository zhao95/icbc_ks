var _viewer = this;
//获取wfs_ID,并保存
if(_viewer.opts.act == "cardAdd"){
	var WFS_ID = _viewer.opts.WFS_ID;
	if(typeof(WFS_ID)!="undefined"){ 
		_viewer.getItem("WFS_ID").setValue(WFS_ID);
	}
}