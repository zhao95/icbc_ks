var _viewer = this;
jQuery("#ou-topPic").remove();
jQuery(".header").remove();
jQuery("#SY_GAVE_AUTH_AGENT-winTabs").children().first().remove();
jQuery("#SY_GAVE_AUTH_AGENT-save").children().first().text("授权");

var souUser = _viewer.getItem("SOURCE_USER_NAME");
var deptName = _viewer.getItem("DEPT_NAME");
var curUser = _viewer.getItem("CURRENT_USER_NAME");
var begDate = _viewer.getItem("BEG_DATE");
var begHour = _viewer.getItem("BEG_HOUR");
var endDate = _viewer.getItem("END_DATE");
var endHour = _viewer.getItem("END_HOUR");

initHourData();
 
// 获取当前用户的权限字段信息
var roleCode=System.getVar("@ROLE_CODES@");
var adminRoleCode = System.getVar("@C_SY_ADMIN_ROLE_CODE@");
if (adminRoleCode == null || adminRoleCode == "") {
	adminRoleCode = "RADMIN";
}
if(roleCode.indexOf(adminRoleCode)<0){
	// 非系统管理员,弹出提示
	jQuery("#SY_GAVE_AUTH_AGENT-save").unbind("click").bind("click", function() {
		alert("非系统管理员,没有权限代授权");
	});
} else {
	// 添加授权按钮监听
	jQuery("#SY_GAVE_AUTH_AGENT-save").unbind("click").bind("click", function() {
		var souUserValue = souUser.getValue();
		var deptNameValue = deptName.getValue();
		var curUserValue = curUser.getValue();
		var begDateValue = begDate.getValue();
		var begHourValue = begHour.getValue();
		var endDateValue = endDate.getValue();
		var endHourValue = endHour.getValue();
		
		if (souUserValue == null || souUserValue == "") {
			alert("获取授权人为空，请检查授权人框的输入！");
			return;
		}
		if (deptNameValue == null || deptNameValue == "") {
			alert("获取机构为空，请检查机构框的输入！");
			return;
		}
		if (curUserValue == null || curUserValue == "") {
			alert("获取受权人为空，请检查受权人框的输入！");
			return;
		}
		if ((begDateValue == null || begDateValue == "") || 
				(begHourValue == null || begHourValue.length < 2)) {
			alert("获取开始时间有误，请检查开始时间框的输入！");
			return;
		}
		if ((endDateValue == null || endDateValue == "") || 
				(endHourValue == null || endHourValue.length < 2)) {
			alert("获取结束时间有误，请检查开始时间框的输入！");
			return;
		}
		var nowDateParam = getNowFormatDate();
		var nowDateTime = nowDateParam.CUR_DATE + " " + nowDateParam.CUR_HOUR;
		var begDateTime = begDateValue + " " + begHourValue.substring(0,2);
		var endDateTime = endDateValue + " " + endHourValue.substring(0,2);
		if (begDateTime < nowDateTime) {
			alert("开始时间输入有误，请输入当前时间或者之后的时间！");
			return;
		} 
		if (endDateTime <= begDateTime) {
			alert("结束时间输入有误，结束时间要在开始时间之后！");
			return;
		}
		
		var souUserId = JSON.parse(souUser.getValue()).value;
		var deptCode = JSON.parse(deptName.getValue()).value;
		var curUserId = JSON.parse(curUser.getValue()).value;
		
		var param = {
				SOURCE_USER_ID : souUserId,
				DEPT_CODE : deptCode,
				CURRENT_USER_ID : curUserId,
				BEG_DATE : begDateTime,
				END_DATE : endDateTime
		}
		var result = FireFly.doAct("SY_GAVE_AUTH_AGENT", "gaveDeptAuth", param, true);
		if (result['_MSG_'].indexOf('ERROR') >= 0) {
			var resultStr = result['_MSG_'].split(",");
			if (resultStr.length > 1) {
				alert(resultStr[1]);
			} else {
				alert("授权失败！");
			}
			return;
		} 
		_viewer.refresh();
		alert("授权成功！");
	});
}

function getNowFormatDate() {
	var date = new Date();
	var seperator = "-";
	var month = date.getMonth() + 1;
    var strDate = date.getDate();
    var day = date.getDay();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = date.getFullYear() + seperator + month + seperator + strDate;
    var currentHour = date.getHours();
    if (currentHour >= 0 && currentHour <= 9) {
    	currentHour = "0" + currentHour;
    }
    var param = {
    		CUR_DATE : currentdate,
    		CUR_HOUR : currentHour
    }     
    return param;
}

function initHourData(){
	var hour = getNowFormatDate().CUR_HOUR;
	begHour.setValue(hour + ":00");
	endHour.setValue(hour + ":00");
}
