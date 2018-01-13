var _viewer = this;
//保存之前做校验
_viewer.beforeSave = function() {
	var userCode=_viewer.getItem("ADMINER_UWERCODE").getValue();//人力资源编码
	var shrName=_viewer.getItem("ADMINER_NAME").getValue();//审核人
	var nodeId =_viewer.getItem("NODE_ID").getValue();
	var  WHERE="and ADMINER_UWERCODE='"+userCode+"'and ADMINER_NAME='"+shrName+"' and NODE_ID='"+nodeId+"'";
	FireFly.doAct(_viewer.servId,"count",{"_WHERE_":WHERE},true,false,function(data){
		if(data._DATA_ > 0){
			_viewer.getItem("ADMINER_UWERCODE").clear();//人力资源编码
			_viewer.getItem("ADMINER_NAME").clear();//审核人
			alert("数据有重复");
			
		}
	});
}
//保存之后
_viewer.afterSave=function(){
	var userCode=_viewer.getItem("ADMINER_UWERCODE").getValue();//人力资源编码
	var nodeId =_viewer.getItem("NODE_ID").getValue();
	var param={};
	param["ADMINER_UWERCODE"]=userCode;
	param["NODE_ID"]=nodeId;
	FireFly.doAct(_viewer.servId,"adminerSave",param);
}