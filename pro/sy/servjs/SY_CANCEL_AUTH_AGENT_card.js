var _viewer = this;
jQuery("#ou-topPic").remove();
jQuery(".header").remove();
jQuery("#SY_CANCEL_AUTH_AGENT-winTabs").children().first().remove();
jQuery("#SY_CANCEL_AUTH_AGENT-save").children().first().text("回收");
var souUser = _viewer.getItem("SOURCE_USER_NAME");
var deptName = _viewer.getItem("DEPT_NAME");

//获取当前用户的权限字段信息
var roleCode=System.getVar("@ROLE_CODES@");
var adminRoleCode = System.getVar("@C_SY_ADMIN_ROLE_CODE@");
if (adminRoleCode == null || adminRoleCode == "") {
	adminRoleCode = "RADMIN";
}
if(roleCode.indexOf(adminRoleCode)<0){
	// 非系统管理员
	jQuery("#SY_CANCEL_AUTH_AGENT-save").unbind("click").bind("click", function() {
		alert("非系统管理员,没有权限代回收权限");
	});
} else {
	// 添加回收按钮监听
	jQuery("#SY_CANCEL_AUTH_AGENT-save").unbind("click").bind("click", function() {
		var souUserValue = souUser.getValue();
		var deptNameValue = deptName.getValue();
		var deptCode = "";
		
		if (souUserValue == null || souUserValue == "") {
			alert("获取授权人为空，请检查授权人框的输入！");
			return;
		}
		if (deptNameValue != null && deptNameValue != "") {
			deptCode = JSON.parse(deptName.getValue()).value;
		}
		
		var souUserId = JSON.parse(souUser.getValue()).value;
		var param = {
				SOURCE_USER_ID : souUserId,
				DEPT_CODE : deptCode
		}
		var result = FireFly.doAct("SY_CANCEL_AUTH_AGENT", "recoverDeptAuth", param, true);
		if (result['_MSG_'].indexOf('ERROR') >= 0) {
			var resultStr = result['_MSG_'].split(",");
			if (resultStr.length > 1) {
				alert(resultStr[1]);
			} else {
				alert("回收权限失败！");
			}
			return;
		} 
		_viewer.refresh();
		alert("回收权限成功！");
	});
}