var _viewer = this;
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


//$(".ui-icon-closethick").unbind("click").bind("click",function(){debugger;
//	var   a=$("#TS_WFS_NODE_APPLY-save").click();
//	console.log(a);
//});