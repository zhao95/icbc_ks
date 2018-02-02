var _viewer = this;

var servId = _viewer.servId 
var pkCode = _viewer.getPKCode();
var user_pvlg=_viewer._userPvlg["TS_KCGL_SH_PVLG"];
//var roleOrgPvlg = user_pvlg.upd.ROLE_ORG_LV;
var roleOrgPvlg = user_pvlg.upd.ROLE_DCODE;

if(_viewer.getItem("KC_STATE").getValue() < 4 || _viewer.getItem("KC_STATE").getValue() > 5){
	_viewer.tabHide("TS_KCGL_UPDATE");
}

var kcState = _viewer.getItem("KC_STATE").getValue();

if(kcState==2) { //无效
	_viewer.getBtn("noBtn").hide();
} else if(kcState==5){ //有效
	_viewer.getBtn("yesBtn").hide();
}

var kcState2 = _viewer.getItem("KC_STATE2").getValue();

//console.log("KC_STATE2",_viewer.getItem("KC_STATE2").getValue());
//状态 0:新增未保存1:无效(待审核) 2:无效(审核未通过) 3:无效(审核中) 4:无效(扣分超过上限)5:有效
_viewer.getBtn("yesBtn").unbind("click").bind("click", function(event) {
	var param = {"_PK_":pkCode};
	if(roleOrgPvlg != undefined && (roleOrgPvlg.indexOf('0010100000') > -1 || roleOrgPvlg.indexOf('0010100500') > -1)){
		param["KC_STATE"] = 5;
	}else{
		param["KC_STATE2"] = 1;
	}
	FireFly.doAct(_viewer.servId, "save", param, true,false,function(data){
		alert("审核成功");
		_viewer.refresh();
		_viewer.getParHandler().refresh();
		_viewer.getBtn("yesBtn").hide();
		_viewer.getBtn("noBtn").show();
	});
});

_viewer.getBtn("noBtn").unbind("click").bind("click", function(event) {
	var param = {"_PK_":pkCode,"KC_STATE":2};
	FireFly.doAct(_viewer.servId, "save", param, true,false,function(data){
		alert("审核成功");
		_viewer.refresh();
		_viewer.getParHandler().refresh();
		_viewer.getBtn("noBtn").hide();
		_viewer.getBtn("yesBtn").show();
	});
});

