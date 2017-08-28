var _viewer =this;
_viewer.grid.unbindTrdblClick();
var currDate = new Date();

jQuery("#SY_QUERY_AUTH").children().remove();
jQuery("#SY_QUERY_AUTH").append(
		"<div class='rhGrid-btnBar'>" + 
		"<a id='LAST_WEEK' class='rh-icon rhGrid-btnBar-a-refresh'>" + 
		"<span class='rh-icon-inner rh-icon-inner-refresh' style='text-align:center; width:100%'>上一周</span>" + 
		"</a>" +
		"<a id='NEXT_WEEK' class='rh-icon rhGrid-btnBar-a-refresh' style='margin-left:20px'>" + 
		"<span class='rh-icon-inner rh-icon-inner-refresh' style='text-align:center; width:100%'>下一周</span>" + 
		"</a>" +
		"<a id='CURR_WEEK' class='rh-icon rhGrid-btnBar-a-refresh' style='margin-left:20px'>" + 
		"<span class='rh-icon-inner rh-icon-inner-refresh' style='text-align:center; width:100%;'>本周</span>" + 
		"</a>" +
		"</div>");
jQuery("#SY_QUERY_AUTH").append(
		"<div class='content-mainCount fr wp'>" +
		"<table style='left:0px;margin-top:0px' width='100%'>" + 
		"<tbody id='SHOW_DATA'>" +	
		"</tbody></table></div>");

initTableData();

//初始化表格数据
function initTableData() {
	jQuery("#SHOW_DATA").children().remove();
	var dateArray = getMonday(currDate);
	var param = {
			SPEC_DATE : dateArray[0],
			SOURCE_USER_ID : System.getVar("@LOGIN_NAME@")
	};
	var outBean = FireFly.doAct("SY_QUERY_AUTH", "getSpeDateAccBeans", param, true);
	addTableData(dateArray, outBean);
}

// 将授权信息添加到子表格中
function addTableData(dateArray, outBean) {
	for (var i = 0; i < 7; i++) {
		var accBeans = outBean['ACC_BEANS' + i];
		var tr_id = "PARENT_TR_" + Math.floor(i/2);
		var table_id = "TABLE_" + i;
		var weekCN = getWeekCN(i);
		var weekDate = dateArray[i];
		if (i%2 == 0) {
			var b = "<tr id=" + ("'" + tr_id + "'") + "></tr>";
			jQuery("#SHOW_DATA").append("<tr id='" + tr_id + "'></tr>");
		}
		var a = jQuery("#SHOW_DATA");
		drawTable(tr_id, table_id, accBeans, weekCN, weekDate);
	}
}

// 获取指定日期所在周周一的时间串
function getMonday(theDate) {
	var mondaytime = theDate.getTime();
	var day = theDate.getDay();
	var oneDayLong = 24*60*60*1000;
	mondaytime = mondaytime - (day-1) * oneDayLong;
	var dateAaary = getWeekFormatDates(new Date(mondaytime));
	return dateAaary;
}

// 获取中文标识的星期字段
function getWeekCN (i) {
	var weekCN = "";
	switch (i) {
	case 0: weekCN = "星期一"; break;
	case 1: weekCN = "星期二"; break;
	case 2: weekCN = "星期三"; break;
	case 3: weekCN = "星期四"; break;
	case 4: weekCN = "星期五"; break;
	case 5: weekCN = "星期六"; break;
	case 6: weekCN = "星期日"; break;
	default : weekCN = ""; break;
	}
	return weekCN;
}
// 获取一周的日期数组 yyyy-MM-dd
function getWeekFormatDates(theDate) {
	var dateArray = new Array(7);
	var dateTime = theDate.getTime();
	var oneDayLong = 24*60*60*1000;
	var seperator = "-";
	for (var i = 0; i < 7; i ++) {
		var useredTime = dateTime + i * oneDayLong;
		var date = new Date(useredTime);
		var month = date.getMonth() + 1;
	    var strDate = date.getDate();
	    if (month >= 1 && month <= 9) {
	        month = "0" + month;
	    }
	    if (strDate >= 0 && strDate <= 9) {
	        strDate = "0" + strDate;
	    }
	    var currentdate = date.getFullYear() + seperator + month + seperator + strDate;
	    dateArray[i] = currentdate;
	}
	return dateArray;
}

// 绘制子表格，并填充查询的数据
function drawTable(tr_id, table_id, accBeans, weekCN, weekDate) {
	jQuery("#" + tr_id).append(
			"<td style='margin-left:20px;' width='48%'>" +
			"<div style='margin-left:20px;width:100%;height:20px'>" + weekCN + ":" + weekDate + "</div>" + 
			"<div style='margin-left:20px;width:99%;height:200px;overflow:scroll;border:1px solid #c5c5c5'>" + 
			"<table id='" + table_id + "' style='background-color:white;width:100%;border:none;table-layout:fixed;'>" + 
			"<tr style='background-color:#c5c5c5;height:20px;padding:5px 0px;border:1px solid #c5c5c5;font-size:13px'>" + 
			"<th width='18%' style='text-align:center;height:20px;'>授权人</th>" + 
			"<th width='32%' style='text-align:center;height:20px;'>授权机构</th>" + 
			"<th width='18%' style='text-align:center;height:20px;'>受权人</th>" + 
			"<th width='32%' style='text-align:center;height:20px;'>授权子系统</th>" + 
			"</tr></table></div>" + 
			"<div style='margin-top:0px;margin-left:20px;width:100%;height:20px;background-color:#c5c5c5'>总记录数:"+ accBeans.length +"</div>" + 
			"<td>");
	if (accBeans != null && accBeans.length == 0) {
		jQuery("#" + table_id).append(
			"<tr style='height:160px;width:100%'>" +
			"<td style='text-align:center;height:160px;width:100%;font-size:13px;' colspan='4'><font color='#c5c5c5'>没有数据</font></td>" + 
			"</tr>");
	} else {
		for (var j=0; j<accBeans.length; j++) {
			jQuery("#" + table_id).append(
				"<tr style='height:20px;padding:5px 0px;border:1px solid #c5c5c5;font-size:12px'>" + 
				"<td width='18%' style='height:20px;'>" + accBeans[j].SOURCE_USER_NAME + "</td>" + 
				"<td width='32%' style='height:20px;'>" + accBeans[j].DEPT_NAME + "</td>" + 
				"<td width='18%' style='height:20px;'>" + accBeans[j].CURRENT_USER_NAME + "</td>" + 
				"<td width='32%' style='height:20px;'>" + accBeans[j].SYS_NAME + "</td>" +
				"</tr>");
		}
	}
}
/** 设置需要查询授权的日期
 *  flag=-1 表示上一周
 *  flag=0 表示本周
 *  flag=1 表示下一周
 */
function setCurrDate(flag) {
	var dateTime = currDate.getTime();
	var oneDayLong = 24*60*60*1000;
	if (flag == -1) {
		dateTime = dateTime - 24*60*60*1000*7;
	} else if (flag == 0) {
		dateTime = new Date().getTime();
	} else if (flag == 1) {
		dateTime = dateTime + 24*60*60*1000*7;
	}
	currDate = new Date(dateTime);
}

jQuery("#LAST_WEEK").unbind("click").bind("click", function() {
	setCurrDate(-1);
	initTableData();
});

jQuery("#NEXT_WEEK").unbind("click").bind("click", function() {
	setCurrDate(1);
	initTableData();
});

jQuery("#CURR_WEEK").unbind("click").bind("click", function() {
	setCurrDate(0);
	initTableData();
});
