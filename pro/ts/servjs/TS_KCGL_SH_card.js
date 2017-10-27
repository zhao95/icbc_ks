var _viewer = this;

var servId = _viewer.servId 
var pkCode = _viewer.getPKCode();
var user_pvlg=_viewer._userPvlg["TS_KCGL_SH_PVLG"];
var roleOrgPvlg = user_pvlg.upd.ROLE_ORG_LV;
var odeptLevel = System.getVar("@ODEPT_LEVEL@");
if(odeptLevel != 1){
	_viewer.tabHide("TS_KCGL_UPDATE");
}

//状态 0:新增未保存1:无效(待审核) 2:无效(审核未通过) 3:无效(审核中) 4:无效(扣分超过上限)5:有效
_viewer.getBtn("yesBtn").unbind("click").bind("click", function(event) {
	var param = {"_PK_":pkCode};
	if(roleOrgPvlg == "3"){
		param["KC_STATE2"] = 1;
	}else{
		param["KC_STATE"] = 5;
	}
	FireFly.doAct(_viewer.servId, "save", param, true,false,function(data){
		alert("审核成功");
		_viewer.refresh();
		_viewer.getParHandler().refresh();
	});
});

_viewer.getBtn("noBtn").unbind("click").bind("click", function(event) {
	var param = {"_PK_":pkCode,"KC_STATE":2};
	FireFly.doAct(_viewer.servId, "save", param, true,false,function(data){
		alert("审核成功");
		_viewer.refresh();
		_viewer.getParHandler().refresh();
	});
});

