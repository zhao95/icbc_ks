var _viewer = this;

//_viewer.getItem("BM_CODE").change(function(){
//var userCode = _viewer.getItem("BM_CODE").getValue();
//FireFly.doAct("SY_ORG_USER_ALL","byid",{"_PK_":userCode},true,false,function(data){
//	_viewer.getItem("BM_SEX").setValue(data.USER_SEX);
//	_viewer.getItem("BM_PHONE").setValue(data.USER_MOBILE);
//	_viewer.getItem("BM_LB").setValue(data.USER_POST);
//});
//});

//_viewer.getItem("KSLBK_ID").change(function(){
//var kslb = _viewer.getItem("KSLBK_ID").getValue();
//
//});

//审核状态 1。审核通过 2。审核不通过
if(_viewer.getItem("SH_LEVEL").getValue() == ""){
	_viewer.getItem("SH_LEVEL").setValue(1)
}
//考生状态 1请假，2借考，3请假借考
if(_viewer.getItem("BM_STATUS").getValue() == ""){
	_viewer.getItem("BM_STATUS").setValue(0)
}

_viewer.getBtn("addNext").unbind("click").bind("click",function(){
	var bmName = _viewer.getItem("BM_NAME").getValue();
	var bmlb = _viewer.getItem("BM_LB").getValue();
	if(bmName == "" || bmlb == ""){
		_viewer.cardBarTipError("校验未通过");
	}else{
		_viewer._saveForm();
		setTimeout(function(){ 
			jQuery("#" + _viewer.dialogId).dialog("close");
			$("#TS_XMGL_CCCS_KSGL-add").click();
		}, 200);
	}
});
