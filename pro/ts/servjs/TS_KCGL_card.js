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
	_viewer._readOnly = true;
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

/**
 * 状态
 */
if(_viewer.getItem("KC_STATE").getValue() < 4 || _viewer.getItem("KC_STATE").getValue() > 5){
	_viewer.tabHide("TS_KCGL_UPDATE");
}

if($("#TS_KCGL-KC_ODEPTCODE__NAME").hasClass("disabled") == false) {
	$("#TS_KCGL-KC_ODEPTCODE__NAME").unbind("click").bind("click", function(event) {
	
		var configStr = "TS_ORG_DEPT_ALL,{'TYPE':'single','sId':'TS_ORG_DEPT','pvlg':'CODE_PATH'}";
	
		var options = {
				"config" :configStr,
				"params" : {"USE_SERV_ID":"TS_ORG_DEPT"},
				"parHandler":_viewer,
				"formHandler":_viewer.form,
				"replaceCallBack":function(idArray,nameArray) {//回调，idArray为选中记录的相应字段的数组集合
					
					var codes = idArray;
					var names = nameArray;
					$("#TS_KCGL-KC_ODEPTCODE__NAME").val(names);
					$("#TS_KCGL-KC_ODEPTCODE").val(codes);
					$("#TS_KCGL-KC_ODEPTNAME").val(names);
					console.log($("#TS_KCGL-KC_ODEPTCODE").val());
					console.log($("#TS_KCGL-KC_ODEPTNAME").val());
				}
		};
		
		var queryView = new rh.vi.rhDictTreeView(options);
		queryView.show(event,[],[0,495]);
	});
}

_viewer.getItem("KC_ODEPTCODE").change(function(){
	check("KC_ODEPTCODE");
});
_viewer.getItem("KC_LEVEL").change(function(){
	check("KC_LEVEL");
});

function check(colName){
	var kcOdeptCode = _viewer.getItem("KC_ODEPTCODE").getValue();
	var kcLevel = _viewer.getItem("KC_LEVEL").getValue();
	if(kcOdeptCode == "") return;
	
	FireFly.doAct("SY_ORG_DEPT","byid",{"_PK_":kcOdeptCode},false,false,function(data){
		var level = data.DEPT_LEVEL;
		if(kcLevel == "一级"){
			if(level == 3){
				if(colName == "KC_ODEPTCODE"){
					_viewer.getItem("KC_ODEPTCODE").clear();
				}else{
					_viewer.getItem("KC_LEVEL").clear();
				}
				_viewer.cardBarTipError("一级考场所属机构为一级机构");
			}
		}else if(kcLevel == "二级"){
			if(level != 3){
				if(colName == "KC_ODEPTCODE"){
					_viewer.getItem("KC_ODEPTCODE").clear();
				}else{
					_viewer.getItem("KC_LEVEL").clear();
				}
				_viewer.cardBarTipError("二级考场所属机构为二级机构");
			}
		}
	});
}


_viewer.getItem("KC_MAX").change(function(){
	var maxValue = _viewer.getItem("KC_MAX").getValue();
	var goodValue = _viewer.getItem("KC_GOOD").getValue();
	if(goodValue != ""){
		if(maxValue < goodValue){
			alert("最大设备数不能小于最优设备数！");
			_viewer.getItem("KC_MAX").clear();
		}
	}
});
_viewer.getItem("KC_GOOD").change(function(){
	var maxValue = _viewer.getItem("KC_MAX").getValue();
	var goodValue = _viewer.getItem("KC_GOOD").getValue();
	if(maxValue != ""){
		if(maxValue < goodValue){
			alert("最大设备数不能小于最优设备数！");
			_viewer.getItem("KC_GOOD").clear();
		}
	}
});





