var _viewer = this;
var flowServ = _viewer.getItem("WFS_SERVID").getValue();
if(flowServ == 1){
	_viewer.getItem("WFS_TYPE").show();
}
_viewer.getItem("WFS_SERVID").change(function(){
	var flowSerTmp = _viewer.getItem("WFS_SERVID").getValue(); 
	if(flowSerTmp == 1){
		_viewer.getItem("WFS_TYPE").show();
	}else{
		_viewer.getItem("WFS_TYPE").hide();
	}
});	
	
	