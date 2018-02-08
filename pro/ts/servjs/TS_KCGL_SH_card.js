var _viewer = this;

var servId = _viewer.servId 
var pkCode = _viewer.getPKCode();
var user_pvlg=_viewer._userPvlg["TS_KCGL_SH_PVLG"];
//var roleOrgPvlg = user_pvlg.upd.ROLE_ORG_LV;
var roleOrgPvlg = user_pvlg.upd.ROLE_DCODE;
var kcState = _viewer.getItem("KC_STATE").getValue();
var kcState2 = _viewer.getItem("KC_STATE2").getValue();
var userCode = System.getVar("@USER_CODE@");
var c1 = userCode == "admin";
var c2 = roleOrgPvlg != undefined;
var c3 = roleOrgPvlg.indexOf('0010100000') > -1;
var c4 = roleOrgPvlg.indexOf('0010100500') > -1;

//有效考场显示变更列表  
if(kcState < 4 || kcState > 5){
	_viewer.tabHide("TS_KCGL_UPDATE");
}

if(c1 || (c2 && (c3 || c4))){
	//总行 审核通过 或 审核不通过 按钮隐藏
	if(kcState == 5 || kcState == 2 ){
		_viewer.getBtn("yesBtn").hide();
		_viewer.getBtn("noBtn").hide();
	}
}else{
	//一级分行无修改审核权限
	_viewer.tabHide("TS_KCGL_UPDATE");
	//一级分行审核通过 或 审核不通过 按钮隐藏
	if(kcState2 == 1 || kcState == 2){
		_viewer.getBtn("yesBtn").hide();
		_viewer.getBtn("noBtn").hide();
	}
}

//状态 0:新增未保存1:无效(待审核) 2:无效(审核未通过) 3:无效(审核中) 4:无效(扣分超过上限)5:有效
_viewer.getBtn("yesBtn").unbind("click").bind("click", function(event) {
	var param = {"_PK_":pkCode};	
	if(c1 || (c2 && (c3 || c4))){
		param["KC_STATE"] = 5;
		FireFly.doAct(_viewer.servId, "save", param, true,false,function(data){
			alert("审核成功");
			_viewer.refresh();
			_viewer.getParHandler().refresh();
		});
	}else{
		param["KC_STATE2"] = 1;
		FireFly.doAct(_viewer.servId, "save", param, true,false,function(data){
			alert("审核成功,等待总行审核确认");
			_viewer.refresh();
			_viewer.getParHandler().refresh();
		});
	}
});

_viewer.getBtn("noBtn").unbind("click").bind("click", function(event) {
	var param = {"_PK_":pkCode,"KC_STATE":2};
	FireFly.doAct(_viewer.servId, "save", param, true,false,function(data){
		alert("审核成功");
		_viewer.refresh();
		_viewer.getParHandler().refresh();
	});
});

