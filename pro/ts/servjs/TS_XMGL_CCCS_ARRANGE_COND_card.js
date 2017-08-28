var _viewer = this;
$("#TS_XMGL_CCCS_ARRANGE_COND-save").css("right",300);

var xmId = _viewer.getParHandler().getParHandler().getPKCode();

_viewer.getBtn("ok").click(function(){
	var date = _viewer.getItem("CONF_DATE").getValue();
	var am_time = _viewer.getItem("CONF_AM_TIME").getValue();
	var am_inter = _viewer.getItem("CONF_AM_INTER").getValue();
	var am_num = _viewer.getItem("CONF_AM_NUM").getValue();
	var am_info = _viewer.getItem("CONF_AM_INFO").getValue();
	
	var pm_time = _viewer.getItem("CONF_PM_TIME").getValue();
	var pm_inter = _viewer.getItem("CONF_PM_INTER").getValue();
	var pm_num = _viewer.getItem("CONF_PM_NUM").getValue();
	var pm_info = _viewer.getItem("CONF_PM_INFO").getValue();
	
	var nm_time = _viewer.getItem("CONF_NM_TIME").getValue();
	var nm_inter = _viewer.getItem("CONF_NM_INTER").getValue();
	var nm_num = _viewer.getItem("CONF_NM_NUM").getValue();
	var nm_info = _viewer.getItem("CONF_NM_INFO").getValue();
	
	if(date == ""){
		alert("日期未填写");
		return false;
	}
	if(am_time != "" && am_inter != "" && am_num!= "" && am_info != ""){
		am_info = am_info.replace(/，/g, ",");
		js(date,am_time,am_inter,am_num,am_info);
	}
	if(pm_time != "" && pm_inter != "" && pm_num!= "" && pm_info != ""){
		pm_info = pm_info.replace(/，/g, ",");
		js(date,pm_time,pm_inter,pm_num,pm_info);
	}
	if(nm_time != "" && nm_inter != "" && nm_num!= "" && nm_info != ""){
		nm_info = nm_info.replace(/，/g, ",");
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
	var info_num = info.split(",").length;
	var start = date + " " +time;
	var end = "";
	if(num == info_num || num < info_num){
		for(var i=0;i<num;i++){
			end = addMin(start,info.split(",")[i]);
			var param = {};
			param["ARR_START"]=start;
			param["ARR_END"]=end;
			param["ARR_TIME"]=info.split(",")[i];
			param["XM_ID"]=xmId;
			param["ARR_CC"]= getCc();
			param["ARR_STATE"]= 0;
			FireFly.doAct("TS_XMGL_CCCS_ARRANGE", "save", param, false);	
			start = addMin(end,inter);
		}
	}else{
		alert("条件输入不正确");
	}
}

function getCc(){
	var resData = FireFly.doAct("TS_XMGL_CCCS_ARRANGE", "finds", {"_SELECT_":"max(ARR_CC) ARR_CC","_WHERE_":"and xm_id='"+xmId+"'"}, false);	
	debugger;
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
function addMin(a,b) {
	b = parseInt(b);
	var time = new Date(a.replace(/"-"/g, "/"));
	time.setMinutes(time.getMinutes() + b);
	return (time.format("yyyy-mm-dd hh:MM")).toString();
}