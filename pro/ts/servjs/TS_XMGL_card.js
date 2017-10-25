/** 服务卡片使用的js方法定义：开始fromTable */
var _viewer = this;
//针对项目开始时间的校验与互斥
_viewer.beforeSave = function() {
	var xmStart=_viewer.getItem("XM_START").getValue();//项目开始时间
	var xmEnd=_viewer.getItem("XM_END").getValue();//项目截至时间
	var xmKsStartData=_viewer.getItem("XM_KSSTARTDATA").getValue();//考试开始时间
	var xmKsEndData=_viewer.getItem("XM_KSENDDATA").getValue();//考试截至时间
	var xmStarts = xmStart.substring(0, 10).split('-');
    var xmEnds = xmEnd.substring(0, 10).split('-');
    var xmKsStartDatas = xmKsStartData.substring(0, 10).split('-');
    var xmKsEndDatas = xmKsEndData.substring(0, 10).split('-');
    xmStart = xmStarts[1] + '-' + xmStarts[2] + '-' + xmStarts[0] + ' ' + xmStart.substring(10, 19);
    xmEnd = xmEnds[1] + '-' + xmEnds[2] + '-' + xmEnds[0] + ' ' + xmEnd.substring(10, 19);
    xmKsStartData=xmKsStartDatas[1] + '-' + xmKsStartDatas[2] + '-' + xmKsStartDatas[0] + ' ' + xmKsStartData.substring(10, 19);
    xmKsEndDatas= xmKsEndDatas[1] + '-' + xmKsEndDatas[2] + '-' + xmKsEndDatas[0] + ' ' + xmKsEndData.substring(10, 19);
    //项目开始时间和考试考试时间互斥
    var ksStartXmStart = (Date.parse(xmKsStartData) - Date.parse(xmStart)) / 3600 / 1000;
    if(ksStartXmStart< 0||ksStartXmStart== 0||isNaN(ksStartXmStart)){
 		$("#TS_XMGL-XM_START").addClass("blankError").addClass("errorbox");
 		$("#TS_XMGL-XM_KSSTARTDATA").addClass("blankError").addClass("errorbox");
		return false;
 	}
    //考试开始时间和考试结束时间互斥
    var ksEndKsStart = (Date.parse(xmKsEndDatas) - Date.parse(xmKsStartData)) / 3600 / 1000;
    if(ksEndKsStart< 0||ksEndKsStart== 0||isNaN(ksEndKsStart)){
 		$("#TS_XMGL-XM_KSSTARTDATA").addClass("blankError").addClass("errorbox");
 		$("#TS_XMGL-XM_KSENDDATA").addClass("blankError").addClass("errorbox");
		return false;
 	}
    //考试结束时间与项目结束时间互斥
    var xmEndKsEnd = (Date.parse(xmEnd) - Date.parse(xmKsEndDatas)) / 3600 / 1000;
     if(xmEndKsEnd < 0||xmEndKsEnd == 0||isNaN(xmEndKsEnd)){
 		$("#TS_XMGL-XM_END").addClass("blankError").addClass("errorbox");
 		$("#TS_XMGL-XM_KSENDDATA").addClass("blankError").addClass("errorbox");
		return false;
 	}
};
//修改input时间样式
var xmStart=_viewer.getItem("XM_START");
var xmEnd=_viewer.getItem("XM_END");
var xmKsStartData=_viewer.getItem("XM_KSSTARTDATA");
var xmKsEndData=_viewer.getItem("XM_KSENDDATA");
$('#'+xmStart._opts.id+"_div").css('min-height','32px');
$('#'+xmEnd._opts.id+"_div").css('min-height','32px');
$('#'+xmKsStartData._opts.id+"_div").css('min-height','32px');
$('#'+xmKsEndData._opts.id+"_div").css('min-height','32px');

// 下一步按钮
// 1把数据保存到数据库
_viewer.getBtn("nextbtn").unbind("click").bind("click",function(event) {
	_viewer.doActReload('saveAndToSZ');// 这里不用传参数，这个方法默认是获取所有值
	var XM_ID = _viewer.getItem("XM_ID").getValue();// 执行完保存后，自动把ID回填了
	var XM_TYPE = _viewer.getItem("XM_TYPE").getValue();// 得到类型值
	// 从项目管理到项目设置传参
	var extWhere = "and XM_ID = '" + XM_ID + "'";
	var params = {"XM_ID" : XM_ID,"_extWhere" : extWhere};
	var url = "TS_XMGL_SZ.list.do?&_extWhere=" + extWhere;
	var options = {"url" : url,"params" : params,"menuFlag" : 3,"top" : true};
	Tab.open(options);
});

// 保存后的操作

_viewer.afterSave = function(resultdata) {
	var XM_ID = resultdata.XM_ID;
	var XM_GJ = resultdata.XM_GJ;
	var param = {
		"XM_ID" : XM_ID,
		"XM_GJ" : XM_GJ
	}
	// _viewer.doActReload('saveAfterToSZ','param');//这里不用传参数，这个方法默认是获取所有值
	FireFly.doAct(_viewer.servId, "afterSaveToSz", param);
	_viewer.refresh();
}

// 查看按钮打开的卡片呈现只读样式

// var saveBtn=_viewer.getBtn("save");
// var nextBtn=_viewer.getBtn("nextbtn");
// //var qq=$(".item ui-corner-5").readCard();
// saveBtn.hide();
// nextBtn.hide();
// _viewer.readCard();
//	
if (_viewer.opts.readOnly) {
	_viewer.getBtn("nextbtn").hide();
	_viewer.readCard();
}


//根据选择是否人工审核
_viewer.getItem("XM_TYPE").change(function(){
	var flowSerTmp = _viewer.getItem("XM_TYPE").getValue(); 
	
	if( "资格类考试"==flowSerTmp){
		  _viewer.getItem("XM_KHDKZ").setValue(1);
	}else {
		 _viewer.getItem("XM_KHDKZ").setValue(2);
	}
});	
	