var _viewer =this;
_viewer.grid.unbindTrdblClick();

jQuery("#SY_CANCEL_AUTH-recover").remove();
jQuery("#SY_CANCEL_AUTH").children().first().children().first()
		.before("<a class=\"rh-icon rhGrid-btnBar-a\" id=\"SY_CANCEL_AUTH-recover\" actcode=\"save\" title=\"\">" +
		"<span class=\"rh-icon-inner rh-icon-inner-recover\">回收</span>" +
		"<span class=\"rh-icon-img btn-save\"></span>" +
		"</a>");

jQuery("#SY_CANCEL_AUTH-recover").unbind("click").bind("click", function(){
	var param = {
			SOURCE_USER_ID : System.getVar("@LOGIN_NAME@")
	}
	var result = FireFly.doAct("SY_CANCEL_AUTH", "recoverDeptsAuth", param, true);
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
