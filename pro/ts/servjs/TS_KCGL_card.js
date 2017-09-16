var _viewer = this;
//查看只读
if(_viewer.opts.readOnly == true){
	_viewer.getBtn("apply").hide();
	_viewer.readCard();
}

if(_viewer.getItem("SERV_ID").getValue() == ""){
	_viewer.getItem("SERV_ID").setValue(_viewer.servId);
}

//打开自服务列表
if(typeof(_viewer.opts.showTab) !="undefined"){ 
	var sid = _viewer.opts.showTab;
	if(sid != ""){
		var topObj = jQuery("li.rhCard-tabs-topLi[sid='" + sid + "']",_viewer.tabs);
		topObj.find("a").click();
	}
}

// 状态 0:新增未保存1:无效(待审核) 2:无效(审核未通过) 3:无效(审核中) 4:无效(扣分超过上限)5:有效
// 11,12,13,14:逻辑删除 
var state = _viewer.getItem("KC_STATE").getValue();
var saveBtn = _viewer.getBtn("save");
var applyBtn = _viewer.getBtn("apply");

// 创建数据机构
var s_odept = _viewer.getItem("S_ODEPT").getValue();
var odept = System.getVar("@ODEPT_CODE@");

// 只有创建机构能够修改数据和提交审批
if (s_odept == odept) {
	if (state == 0) {
		applyBtn.hide();
	} else if (state == 5 || state == 3 || state == 4 || state == 11
			|| state == 12 || state == 13 || state == 14) {
		cardReadOnly();
	}
} else {
	cardReadOnly();
}

function cardReadOnly() {
	saveBtn.hide();
	applyBtn.hide();
	_viewer.form.disabledAll();
}
// 提交审批
applyBtn.click(function() {
	_viewer.getItem("KC_STATE").setValue(3);
	_viewer._saveForm();
});

_viewer.beforeSave = function() {
	if (state == 0) {
		_viewer.getItem("KC_STATE").setValue(1);
	}
	if($("#TS_KCGL-KC_GLY_div .rhGrid-tbody").find("td").length == 1){
		$("#TS_KCGL-KC_GLY_div").find(".ui-dataservice-container,.fl,.wp").addClass("blankError").addClass("errorbox");
		return false;
	}
};