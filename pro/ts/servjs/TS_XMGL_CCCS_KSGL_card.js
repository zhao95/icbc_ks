var _viewer = this;

_viewer.getItem("BM_CODE").change(function(){
	var userCode = _viewer.getItem("BM_CODE").getValue();
	var res = FireFly.byId("SY_ORG_USER_ALL",userCode);
	
	_viewer.getItem("BM_SEX").setValue(res.USER_SEX);
	_viewer.getItem("BM_PHONE").setValue(res.USER_MOBILE);
	_viewer.getItem("BM_LB").setValue(res.USER_POST);
});



