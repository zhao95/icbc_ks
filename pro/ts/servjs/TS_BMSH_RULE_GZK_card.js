_viewer = this;
$("#TS_BMSH_RULE_GZK-RULE_END_SCOPE_label").find("span:first").css("width","1000px");
$("#TS_BMSH_RULE_GZK-START_TIME_END_label").find("span:first").css("width","1000px");
$("#TS_BMSH_RULE_GZK-R_ZD_label").find("span:first").css("width","1000px");
$("#TS_BMSH_RULE_GZK-R_ZD_div").css("margin-left","4%");
$("#TS_BMSH_RULE_GZK-R_ZD_MK_div").css("margin-left","4%");
$("#TS_BMSH_RULE_GZK-R_ZD_MK_label").find("span:first").css("width","1000px");
$("#TS_BMSH_RULE_GZK-R_TYPE_label").find("span:first").css("width","1000px");
$("#TS_BMSH_RULE_GZK-R_TYPE_div").css("margin-left","2%");
$("#TS_BMSH_RULE_GZK-CONDITION_TWO_div").find("span:last").find("div").css("width","50px");
$("#TS_BMSH_RULE_GZK-CONDITION_ONE_div").find("span:last").find("div").css("width","50px");
$("#TS_BMSH_RULE_GZK-VALID_START_div").css("width","20%");
$("#TS_BMSH_RULE_GZK-VALID_START_div").css("margin-left","-28%");
$("#TS_BMSH_RULE_GZK-VALID_END_div").css("width","30%");
$("#TS_BMSH_RULE_GZK-RULE_CENGJI_div").css("width","30%");
$("#TS_BMSH_RULE_GZK-RULE_LASTYEAR_div").css("width","50%");
$("#TS_BMSH_RULE_GZK-RULE_LASTYEAR_div").css("margin-left","-32%");
$("#TS_BMSH_RULE_GZK-RULE_LASTYEAR").parent().css("width","20%");
$("#TS_BMSH_RULE_GZK-RULE_NOWTIME_div").css("width","50%");
$("#TS_BMSH_RULE_GZK-RULE_NOWTIME_div").css("margin-left","-32%");
$("#TS_BMSH_RULE_GZK-CONDITION_ONE_END_div").find("span:last").find("div").css("width","50px");
$("#TS_BMSH_RULE_GZK-CONDITION_ONE_TIME_div").css("width","50%");
$("#TS_BMSH_RULE_GZK-CONDITION_ONE_TIME_div").css("margin-left","-32%");
$("#TS_BMSH_RULE_GZK-CONDITION_YEAR_div").css("width","50%");
$("#TS_BMSH_RULE_GZK-CONDITION_YEAR_div").css("margin-left","-32%");
$("#TS_BMSH_RULE_GZK-CONDITION_YEAR").parent().css("width","20%");
$("#TS_BMSH_RULE_GZK-CONDITION_TIME_div").css("width","20%");
$("#TS_BMSH_RULE_GZK-CONDITION_TIME_div").css("margin-left","-28%");
$("#TS_BMSH_RULE_GZK-CONDITION_ONE_TYPE_div").css("width","30%");

$("#TS_BMSH_RULE_GZK-CONDITION_TWO_div").find("span:last").find("div").css("width","50px");
$("#TS_BMSH_RULE_GZK-VALID_END_div").css("width","50%");
$("#TS_BMSH_RULE_GZK-VALID_END_div").css("margin-left","-32%");
$("#TS_BMSH_RULE_GZK-CONDITION_TWO_YEAR_div").css("width","50%");
$("#TS_BMSH_RULE_GZK-CONDITION_TWO_YEAR_div").css("margin-left","-32%");
$("#TS_BMSH_RULE_GZK-CONDITION_TWO_YEAR").parent().css("width","20%");
$("#TS_BMSH_RULE_GZK-CONDITION_TWO_TIME_div").css("width","20%");
$("#TS_BMSH_RULE_GZK-CONDITION_TWO_TIME_div").css("margin-left","-28%");
$("#TS_BMSH_RULE_GZK-CONDITION_TWO_TYPE_div").css("width","30%");

$("#TS_BMSH_RULE_GZK-CONDITION_TWO_END_div").find("span:last").find("div").css("width","50px");
$("#TS_BMSH_RULE_GZK-CONDITION_TWO_TIMETW_div").css("width","50%");
$("#TS_BMSH_RULE_GZK-CONDITION_TWO_TIMETW_div").css("margin-left","-32%");
$("#TS_BMSH_RULE_GZK-CONDITION_TWO_END_YEAR_div").css("width","50%");
$("#TS_BMSH_RULE_GZK-CONDITION_TWO_END_YEAR_div").css("margin-left","-32%");
$("#TS_BMSH_RULE_GZK-CONDITION_TWO_END_YEAR").parent().css("width","20%");
$("#TS_BMSH_RULE_GZK-CONDITION_TWO_END_TIME_div").css("width","20%");
$("#TS_BMSH_RULE_GZK-CONDITION_TWO_END_TIME_div").css("margin-left","-28%");
$("#TS_BMSH_RULE_GZK-CONDITION_TWO_END_TYPE_div").css("width","30%");
_viewer.beforeSave = function() {
	var con_one = _viewer.getItem("CONDITION_ONE").getValue("");//条件一
	var con_two = _viewer.getItem("CONDITION_TWO").getValue("");//条件二
	var start_time = _viewer.getItem("VALID_START").getValue("");//开始时间
	var end_time = _viewer.getItem("VALID_END").getValue("");//开始时间
	if(end_time!=""&&start_time!=""){
		if(con_one=="<"&&con_two==">"){
			$("#TS_BMSH_RULE_GZK-CONDITION_ONE").parent().showError("两种条件不能同时存在");
	 		$("#TS_BMSH_RULE_GZK-CONDITION_TWO").parent().showError("两种条件不能同时存在");
		}
	}
	var beginTime=_viewer.getItem("VALID_START").getValue();
	var endTime=_viewer.getItem("VALID_END").getValue();
	var beginTimes = beginTime.substring(0, 10).split('-');
	var endTimes = endTime.substring(0, 10).split('-');
	 	beginTime = beginTimes[1] + '-' + beginTimes[2] + '-' + beginTimes[0] + ' ' + beginTime.substring(10, 19);
    endTime = endTimes[1] + '-' + endTimes[2] + '-' + endTimes[0] + ' ' + endTime.substring(10, 19);
    var a = (Date.parse(endTime) - Date.parse(beginTime)) / 3600 / 1000;
    if(a < 0||a == 0){
 		//$("#TS_XMGL_BMSH-SH_START").addClass("blankError").addClass("errorbox");
 		//$("#TS_XMGL_BMSH-SH_END").addClass("blankError").addClass("errorbox");
    	$("#TS_BMSH_RULE_GZK-VALID_START").parent().showError("证书有效开始时间应早于有效结束时间");
 		$("#TS_BMSH_RULE_GZK-VALID_END").parent().showError("证书有效结束时间应晚于有效开始时间");
		return false;
 	}
    
};

$("#TS_BMSH_RULE_GZK-R_MK").css("cursor","pointer");
    $("#TS_BMSH_RULE_GZK-R_MK").unbind('click').bind('click',function(){
    var xls = $("#TS_BMSH_RULE_GZK-R_XL__NAME").val();
    if(xls!=""){
    	var arr = xls.split(",");
    	xls = '';
    	for(var i=0;i<arr.length;i++){
    		var arrstr = arr[i].split("/");
    		if(arrstr.length>1){
    			arr[i]=arrstr[1];
    		}
    		if(i==0){
    			xls += "^"+arr[i]+"^";
    		}else{
    			xls +=",^"+arr[i]+"^";
    		}
    	}
    }else{
    	alert("请先选序列");
    	return false;
    }
	var configStr = "TS_XMGL_BM_KSLBK,{'TARGET':'','SOURCE':'KSLBK_XL~KSLBK_MK'," +
	"'HIDE':'','EXTWHERE':'AND KSLBK_XL IN("+xls+") AND KSLBK_MK !=^无模块^ GROUP by KSLBK_MK','TYPE':'multi','HTMLITEM':''}";
	var options = {
			"config" :configStr,
			"parHandler":_viewer,
			"formHandler":_viewer.form,
		    "replaceCallBack":function(idArray) {//回调，idArray为选中记录的相应字段的数组集合
		    	var ids = idArray.KSLBK_MK;
		    	$("#TS_BMSH_RULE_GZK-R_MK").val(ids);
			}
		};
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event,[],[0,495]);
    })
    