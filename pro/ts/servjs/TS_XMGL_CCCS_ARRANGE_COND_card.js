var _viewer = this;
$("#TS_XMGL_CCCS_ARRANGE_COND-save").css("right",300);
//取消行点击事件

var xmId = _viewer.getParHandler().getParHandler().getPKCode();

_viewer.getBtn("ok").click(function(){
	//上午
	var date = _viewer.getItem("CONF_DATE").getValue();//日期
	var am_time = _viewer.getItem("CONF_AM_TIME").getValue();//开始时间（上午）
	var am_inter = _viewer.getItem("CONF_AM_INTER").getValue();//间隔（分钟）
	var am_num = _viewer.getItem("CONF_AM_NUM").getValue();//场次数
	var am_info = _viewer.getItem("CONF_AM_INFO").getValue();//时长
	//下午
	var pm_time = _viewer.getItem("CONF_PM_TIME").getValue();
	var pm_inter = _viewer.getItem("CONF_PM_INTER").getValue();
	var pm_num = _viewer.getItem("CONF_PM_NUM").getValue();
	var pm_info = _viewer.getItem("CONF_PM_INFO").getValue();
	//晚上
	var nm_time = _viewer.getItem("CONF_NM_TIME").getValue();
	var nm_inter = _viewer.getItem("CONF_NM_INTER").getValue();
	var nm_num = _viewer.getItem("CONF_NM_NUM").getValue();
	var nm_info = _viewer.getItem("CONF_NM_INFO").getValue();
	
	if(date == ""){
		alert("日期未填写");
		return false;
	}
	
	//场次数	
	var amTimes = parseInt((am_num==""? "0":am_num));
	var pmTimes = parseInt((pm_num==""? "0":pm_num));
	var nmTimes = parseInt((nm_num==""? "0":nm_num));
	
	//时长替换中文逗号
	am_info = am_info.replace(/，/g, ",");
	pm_info = pm_info.replace(/，/g, ",");
	nm_info = nm_info.replace(/，/g, ",");
	
	//上午判断如果开始时间有，场次有，时长为空
	
	if(am_time != "" &&  am_num !="" && am_info!=""){
		var amTimesNum = am_info.split(",").length;
		if(amTimes !=amTimesNum){
			alert("请正确匹配场次数和时长段");
			return false;
		}
	}

	if(pm_time != "" &&  pm_num !="" && pm_info!=""){
		var pmTimesNum = pm_info.split(",").length;
		if(pmTimes !=pmTimesNum){
			alert("请正确匹配场次数和时长段");
			return false;
		}
	}
	//晚上判断如果开始时间有，场次有，时长为空
	if(nm_time != "" &&  nm_num !="" && nm_info!=""){
		var nmTimesNum = nm_info.split(",").length;
		if(nmTimes !=nmTimesNum){
			alert("请正确匹配场次数和时长段");
			return false;
		}
	}
	
	if(am_time != "" && am_inter != "" &&  am_num!= "" && am_info != ""){//am_inter != "" &&
		js(date,am_time,am_inter,am_num,am_info);//	日期、开始时间、间隔、场次数、时长
	}
	if(pm_time != "" && am_inter != "" &&  pm_num!= "" && pm_info != ""){//pm_inter != "" &&
		js(date,pm_time,pm_inter,pm_num,pm_info);
	}
	if(nm_time != "" && am_inter != "" &&  nm_num!= "" && nm_info != ""){//nm_inter != "" &&
		js(date,nm_time,nm_inter,nm_num,nm_info);
	}
	_viewer.getParHandler().refresh();
	_viewer.backA.mousedown();
});
_viewer.getBtn("close").click(function(){
	_viewer.backA.mousedown();
});

function js(date,time,inter,num,info){
	info = info.replace(/，/g, ",");
	var info_num = info.split(",").length;//
	var start = date + " " +time;
	var end = "";
	if(num == info_num || num > info_num){
		for(var i=0;i<num;i++){
			end = addMin(start,info.split(",")[i]);
			var param = {};
			param["ARR_START"]=start;//开始时间
			param["ARR_END"]=end;//结束时间
			param["ARR_TIME"]=info.split(",")[i];;//考试时长
			param["XM_ID"]=xmId;
			param["ARR_CC"]= getCc();//场次
			param["ARR_STATE"]= 0;
			FireFly.doAct("TS_XMGL_CCCS_ARRANGE", "save", param, false);	
			start = addMin(end,inter);
		}
	}else{
		alert("条件输入不正确");
	}
}
//function js(date,time,inter,num,info){debugger;
////info = info.replace(/，/g, ",");
//var info_num = info.split(",").length;//永远是1
//var start = date + " " +time;
//var end = "";
//if(num == info_num || num > info_num){
//	for(var i=0;i<num;i++){
//		end = addMin(start,info.split(",")[i]);
//		var param = {};
//		param["ARR_START"]=start;
//		param["ARR_END"]=end;
//		param["ARR_TIME"]=info.split(",")[i];
//		param["XM_ID"]=xmId;
//		param["ARR_CC"]= getCc();
//		param["ARR_STATE"]= 0;
//		FireFly.doAct("TS_XMGL_CCCS_ARRANGE", "save", param, false);	
//		start = addMin(end,inter);
//	}
//}else{
//	alert("条件输入不正确");
//}
//}
function getCc(){
	var resData = FireFly.doAct("TS_XMGL_CCCS_ARRANGE", "finds", {"_SELECT_":"max(ARR_CC) ARR_CC","_WHERE_":"and xm_id='"+xmId+"'"}, false);	
	if(resData._DATA_.length != 0){
		var max = resData._DATA_[0].ARR_CC - 0 + 1;
		return max;
	}
	return 1;
}

/**
 * 计算 时间+分钟
 * @param a 时间
 * @param b 分钟
 * @returns
 */
function addMin(a,b){
	
	b = parseInt(b);
	var atime = a.replace(/-/g, "/");
	var date = new Date(atime);
	
	date.setMinutes(date.getMinutes()+b);
    
	return rhDate.patternData("yyyy-MM-dd HH:mm",date);
}