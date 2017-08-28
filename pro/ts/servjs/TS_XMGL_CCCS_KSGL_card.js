var _viewer = this;

var bmCode = _viewer.getItem("BM_CODE").getValue();
if(bmCode != ""){
	var res = FireFly.doAct("SY_ORG_USER_ALL","finds", {"_SELECT_":"USER_CODE","_WHERE_":"and USER_WORK_NUM = '"+bmCode+"'"})._DATA_;
	if(res.length > 0){
		_viewer.getItem("USER_CODE").setValue(res[0].JG_CODE);
	}
}

_viewer.getItem("USER_CODE").change(function(){
	var userCode = _viewer.getItem("USER_CODE").getValue();
	var res = FireFly.byId("SY_ORG_USER_ALL",userCode);
	
	_viewer.getItem("BM_NAME").setValue(res.USER_NAME);
	_viewer.getItem("BM_CODE").setValue(res.USER_WORK_NUM);
	_viewer.getItem("BM_SEX").setValue(res.USER_SEX);
	_viewer.getItem("BM_PHONE").setValue(res.USER_MOBILE);
	_viewer.getItem("BM_LB").setValue(res.USER_POST);
});