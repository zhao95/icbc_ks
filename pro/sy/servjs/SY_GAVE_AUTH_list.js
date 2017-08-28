var _viewer =this;
_viewer.grid.unbindTrdblClick();
_viewer.tabClickRefresh = true;
jQuery("#SY_GAVE_AUTH-btn").remove();
jQuery("#SY_GAVE_AUTH-second-tr").remove();
jQuery("#SY_GAVE_AUTH").children().first().children()
	.before("<div class='rhGrid-btnBar' id='SY_GAVE_AUTH-btn'>" +
		"<a class='rh-icon rhGrid-btnBar-a' id='SY_GAVE_AUTH-save' actcode='save' title=''>" +
		"<span class='rh-icon-inner rh-icon-inner-sq'>授权</span>" +
		"<span class='rh-icon-img btn-save'></span>" +
		"</a>" +
		"<a class='rh-icon rhGrid-btnBar-a' id='SY_GAVE_AUTH-reset' actcode='reset' title=''>" +
		"<span class='rh-icon-inner rh-icon-inner-reset'>重置</span>" +
		"<span class='rh-icon-img btn-refresh'></span>" +
		"</a></div>");

jQuery(".rh-advSearch-fieldSet").first().children().first().text("请输入受权人及授权时间");
jQuery(".rh-advSearch-table").first().children().append(
		"<tr height='35px' id='SY_GAVE_AUTH-second-tr'>" +
		"<td class='rh-advSearch-lab-td'>" +
		"<label value='DEPT_NAME' class='rh-advSearch-lab'>" +
		"开始时间" +
		"</label></td>" +
		"<td class='rh-advSearch-inp-td'>" +
		"<input type='text' class='rh-advSearch-val Wdate' id='BEG_DATE' style='width: 170px; border: 1px solid rgb(190, 190, 190); height: 25px;'>" +
		"<span class='rh-advSearch-dao'>-</span>" +
		"<div class='blank ui-time-container wp correctbox' tip='' style='width: 60px; border: 1px solid rgb(190, 190, 190); height: 25px;display: inline-block;'>" +
		"<div class='ui-time-content'>" +
		"<input type='text' class='ui-text-default' id='BEG_HOUR'>" +
		"<div class='ui-time-button'>" +
		"<a class='ui-time-button-up' id='BEG_HOUR_UP'></a>" +
		"<a class='ui-time-button-down' id='BEG_HOUR_DOWN'></a>" +
		"</div></div></div>" +
		"</td>" +
		"<td class='rh-advSearch-lab-td'>" +
		"<label value='DEPT_NAME' class='rh-advSearch-lab'>" +
		"截止时间" +
		"</label></td>" +
		"<td class='rh-advSearch-inp-td'>" +
		"<input type='text' class='rh-advSearch-val Wdate' id='END_DATE' style='width: 170px; border: 1px solid rgb(190, 190, 190); height: 25px;'>" +
		"<span class='rh-advSearch-dao'>-</span>" +
		"<div class='blank ui-time-container wp correctbox' tip='' style='width: 60px; border: 1px solid rgb(190, 190, 190); height: 25px;display: inline-block;'>" +
		"<div class='ui-time-content'>" +
		"<input type='text' class='ui-text-default' id='END_HOUR' >" +
		"<div class='ui-time-button'>" +
		"<a class='ui-time-button-up' id='END_HOUR_UP'></a>" +
		"<a class='ui-time-button-down' id='END_HOUR_DOWN'></a>" +
		"</div></div></div>" +
		"</td></tr>");
jQuery(".rh-advSearch-table").first().parent().children("div").remove();

initData();
// 监听机构下拉框内容变化
jQuery("select").first().bind("change",function(){
	initUserSelect();
	if(!(jQuery("select").first().val() == "")) {
		var deptInfo = {
			DEPT_CODE : $(this).val(),
			SOURCE_USER_ID : System.getVar("@LOGIN_NAME@")
		};
		initcurUserData();		
	} 
});

/*
新时间控件：
开始时间
只显示年月日，且默认为当天
否则显示，文本框中的 时间
*/
jQuery("#BEG_DATE").unbind("click").bind("click", function(){
	WdatePicker({
        startDate:new Date(),
        alwaysUseStartDate:true,
        isShowClear:false,
        readOnly:true,
        onpicked:function() {
            start_time.onchange(); 
        },
        dateFmt:"yyyy-MM-dd"
    });
});
// 监听开始时间的向上按钮
jQuery("#BEG_HOUR_UP").unbind("click").bind("click", function(){
	var hour;
	var data = parseInt(jQuery("#BEG_HOUR").val().substring(0,2)) + 1;
	if (data > 23) {
		hour = "00"
	} else if (data < 10) {
		hour = "0" + data;
	} else {
		hour = data;
	}
	hour += ":00";
	jQuery("#BEG_HOUR").val(hour);
});
//监听开始时间的向下按钮
jQuery("#BEG_HOUR_DOWN").unbind("click").bind("click", function(){
	var hour;
	var data = parseInt(jQuery("#BEG_HOUR").val().substring(0,2)) - 1;
	if (data < 0) {
		hour = "23"
	} else if (data < 10) {
		hour = "0" + data;
	} else {
		hour = data;
	}
	hour += ":00";
	jQuery("#BEG_HOUR").val(hour);
});

/*
新时间控件：
结束时间
只显示年月日，且默认为当天
否则显示，文本框中的 时间
*/
jQuery("#END_DATE").unbind("click").bind("click", function(){
	WdatePicker({
        startDate:new Date(),
        alwaysUseStartDate:true,
        isShowClear:false,
        readOnly:true,
        onpicked:function() {
            start_time.onchange(); 
        },
        dateFmt:"yyyy-MM-dd"
    });
});

//监听结束时间的向上按钮
jQuery("#END_HOUR_UP").unbind("click").bind("click", function(){
	var hour;
	var data = parseInt(jQuery("#END_HOUR").val().substring(0,2)) + 1;
	if (data > 23) {
		hour = "00"
	} else if (data < 10) {
		hour = "0" + data;
	} else {
		hour = data;
	}
	hour += ":00";
	jQuery("#END_HOUR").val(hour);
});
//监听结束时间的向下按钮
jQuery("#END_HOUR_DOWN").unbind("click").bind("click", function(){
	var hour;
	var data = parseInt(jQuery("#END_HOUR").val().substring(0,2)) - 1;
	if (data < 0) {
		hour = "23"
	} else if (data < 10) {
		hour = "0" + data;
	} else {
		hour = data;
	}
	hour += ":00";
	jQuery("#END_HOUR").val(hour);
});

// 监听重置按钮click事件
jQuery("#SY_GAVE_AUTH-reset").unbind("click").bind("click",function(){
	initData();
});

//监听保存按钮click事件
jQuery("#SY_GAVE_AUTH-save").unbind("click").bind("click",function(){
	var deptCode = jQuery("select").first().val();
	var curUserId = _viewer.advSearch.cacheArr.USER_LOGIN_NAME;
	var nowDateParam = getNowFormatDate();
	var nowDateTime = nowDateParam.CUR_DATE + " " + nowDateParam.CUR_HOUR;
	var begDate = jQuery("#BEG_DATE").val();
	var endDate = jQuery("#END_DATE").val();
	var begDateTime = jQuery("#BEG_DATE").val() + " " + jQuery("#BEG_HOUR").val().substring(0,2);
	var endDateTime = jQuery("#END_DATE").val() + " " + jQuery("#END_HOUR").val().substring(0,2);
	
	if (deptCode == null || deptCode == "" || deptCode == "default") {
		alert("获取机构有误，请检查机构框的输入！");
		return;
	}
	if (curUserId == null || curUserId == "" || curUserId == "default") {
		alert("获取受权人有误，请检查受权人框的输入！");
		return;
	}
	if (begDate == null || begDate == "") {
		alert("获取开始日期有误，请检查开始日期框的输入！");
		return;
	}
	if (endDate == null || endDate == "") {
		alert("获取结束日期有误，请检查结束日期框的输入！");
		return;
	}
	if (begDateTime < nowDateTime) {
		alert("开始时间输入有误，请输入当前时间或者之后的时间！");
		return;
	} 
	if (endDateTime <= begDateTime) {
		alert("结束时间输入有误，结束时间要在开始时间之后！");
		return;
	}
	
	var param = {
			SOURCE_USER_ID : System.getVar("@LOGIN_NAME@"),
			DEPT_CODE : deptCode,
			CURRENT_USER_ID : curUserId,
			BEG_DATE : begDateTime,
			END_DATE : endDateTime
	}
	var result = FireFly.doAct("SY_GAVE_AUTH", "gaveDeptAuth", param, true);
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

function initData() {
	initcurUserData();
	initDeptData();
	initDateTime();
}

//填充机构框数据
function initDeptData() {
	jQuery("select").first().children().remove();
	var param = {
		SOURCE_USER_ID : System.getVar("@LOGIN_NAME@")
	};
	var result = FireFly.doAct("SY_GAVE_AUTH", "getDeptInfo", param, true);
	if (result['_MSG_'].indexOf('ERROR') >= 0) {
		alert("获取机构数据失败！");
		return;
	} 
	var depts = result.USER_DEPTS;
	
	if (depts != null && depts.length > 0) {
		jQuery("select").first().append("<option selected='selected' value='"
				 + depts[0].TDEPT_CODE +"'>"+depts[0].TDEPT_NAME + "</option>");
		for (var i = 1; i < depts.length; i++) {
			jQuery("select").first().append("<option value='"
					 + depts[i].TDEPT_CODE +"'>"+depts[i].TDEPT_NAME + "</option>");
		}
	}
}

// 初始化后台静态数据
function initcurUserData() {
	_viewer.advSearch.cacheArr.USER_LOGIN_NAME = "";
	jQuery(".rh-search-suggest-input.rh-advSearch-val").val("");
	var param = {
		DEPT_CODE : jQuery("select").first().val(),
	};
	var result = FireFly.doAct("SY_GAVE_AUTH", "updateDeptCode", param, true);
}

function initDateTime() {
	var nowDateParam = getNowFormatDate();
	jQuery("#BEG_DATE").val(nowDateParam.CUR_DATE);
	jQuery("#END_DATE").val(nowDateParam.CUR_DATE);
	jQuery("#BEG_HOUR").val(nowDateParam.CUR_HOUR + ":00");
	jQuery("#END_HOUR").val(nowDateParam.CUR_HOUR + ":00");
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

