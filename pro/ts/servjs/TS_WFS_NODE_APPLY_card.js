var _viewer = this;

var WFS_ID=_viewer.getItem("WFS_ID").getValue();
FireFly.doAct("TS_WFS_APPLY","finds",{"_WHERE_":" and WFS_ID='"+WFS_ID+"'"},true,false,function(data){
	var flowServ = data._DATA_[0].WFS_SERVID;
	if(flowServ=="1"){
		_viewer.tabHide("TS_WFS_QJKLC");
	}else{
		_viewer.tabHide("TS_WFS_BMSHLC");
	}	
});


