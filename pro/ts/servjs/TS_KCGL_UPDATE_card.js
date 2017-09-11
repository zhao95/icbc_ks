var _viewer = this;
if (_viewer._actVar == "cardAdd") {
	var msg = "新建一条申请";
	if (confirm(msg) == true) {
		_viewer._saveForm();
	} else {
		_viewer.backA.mousedown();
	}
}

_viewer.getBtn("save").css("right", "500px");

_viewer.getBtn("commit").unbind("click").bind("click", function(event) {
	if (_viewer.getItem("KC_COMMIT").getValue() == 1) {
		alert("申请单已提交过，无需重复提交");
		return false;
	}
	_viewer.getItem("KC_COMMIT").setValue(1);
	_viewer._saveForm();
});

var servId = _viewer.getParHandler().servId;
if (servId == "TS_KCGL_SH") {
	_viewer.getBtn("commit").hide();
} else {
	_viewer.getBtn("shTgBtn").hide();
	_viewer.getBtn("shBtgBtn").hide();
}

_viewer.getBtn("shTgBtn").unbind("click").bind("click", function(event) {
	if (_viewer.getItem("UPDATE_AGREE").getValue() == 0) {
		_viewer.getItem("UPDATE_AGREE").setValue(1);
		saveChild();
		_viewer._saveForm();
	} else {
		alert("已审核过");
	}

});
_viewer.getBtn("shBtgBtn").unbind("click").bind("click", function(event) {
	if (_viewer.getItem("UPDATE_AGREE").getValue() == 0) {
		_viewer.getItem("UPDATE_AGREE").setValue(2);
		_viewer._saveForm();
	} else {
		alert("已审核过");
	}
});

/**
 * TS_KCGL_IPSCOPE
 */
function saveChild(){
	//主单 TS_KCGL_UPDATE_MX
	//var preesArry = FireFly.doAct("SY_COMM_ENTITY","finds",{"_SELECT_":"TITLE","_WHERE_":" and DATA_ID='"+dataId+"'"});
	FireFly.doAct("TS_KCGL_UPDATE_MX","finds",{"_WHERE_":""},true,false,function(data){
		
	});
	
	
	//相关子表 TS_KCGL_UPDATE_GLY TS_KCGL_UPDATE_IPSCOPE TS_KCGL_UPDATE_IPZWH TS_KCGL_UPDATE_GLJG TS_KCGL_UPDATE_ZWDYB
	var tables1 = new Array()
	tables1[0] = "TS_KCGL_UPDATE_GLY";
	tables1[1] = "TS_KCGL_UPDATE_IPSCOPE";
	tables1[2] = "TS_KCGL_UPDATE_IPZWH";
	tables1[3] = "TS_KCGL_UPDATE_GLJG";
	tables1[4] = "TS_KCGL_UPDATE_ZWDYB";
	var tables2 = new Array()
	tables2[0] = "TS_KCGL_GLY";
	tables2[1] = "TS_KCGL_IPSCOPE";
	tables2[2] = "TS_KCGL_IPZWH";
	tables2[3] = "TS_KCGL_GLJG";
	tables2[4] = "TS_KCGL_ZWDYB";
	
	
	
}


