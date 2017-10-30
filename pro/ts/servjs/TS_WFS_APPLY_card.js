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
	
var stept=_viewer.getItem("WFS_STEPS").getValue();
var  wfsId=_viewer.getItem("WFS_ID").getValue();

if(stept !=''){
	var  wfsId=_viewer.getItem("WFS_ID").getValue();
	
}
//保存后刷新tree和列表
_viewer.afterSave = function() {
	var  wfsId=_viewer.getItem("WFS_ID").getValue();
	alert(wfsId);
};
