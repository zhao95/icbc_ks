var _viewer = this;

var XM_SZ_ID = _viewer.opts.XM_SZ_ID;
var XM_ID=_viewer.opts.XM_ID;
var XM_ID=_viewer.opts.XM_ID;
if(_viewer.opts.act == "cardAdd"){
	XM_SZ_ID = _viewer.opts.XM_SZ_ID;
	if(typeof(XM_SZ_ID)!="undefined" &&typeof(XM_ID)!="undefined" ){ 
		_viewer.getItem("XM_SZ_ID").setValue(XM_SZ_ID);
		_viewer.getItem("XM_ID").setValue(XM_ID);
	}
}
_viewer.beforeSave = function() {
	var bmTzStart=_viewer.getItem("BM_TZ_START").getValue();//通知开始时间
	var bmTzEnd=_viewer.getItem("BM_TZ_END").getValue();//通知截至时间
	var bmStart=_viewer.getItem("BM_START").getValue();//报名开始时间
	var bmEnd=_viewer.getItem("BM_END").getValue();//报名截至时间
	var bmTzStarts = bmTzStart.substring(0,10).split('-');
    var bmTzEnds = bmTzEnd.substring(0,10).split('-');
    var bmStarts = bmStart.substring(0,10).split('-');
    var bmEnds = bmEnd.substring(0,10).split('-');
    var bmTzStartStr = bmTzStarts[1] + '-' + bmTzStarts[2] + '-' + bmTzStarts[0] +' '+bmTzStart.substring(10,19);
    var bmTzEndStr = bmTzEnds[1] + '-' + bmTzEnds[2] + '-' + bmTzEnds[0] +' '+bmTzEnd.substring(10,19) ;
    var bmStartStr=bmStarts[1] + '-' + bmStarts[2] + '-' + bmStarts[0] +' '+bmStart.substring(10,19);
    var bmEndStr= bmEnds[1] + '-' + bmEnds[2] + '-' + bmEnds[0] +' '+bmEnd.substring(10,19);
  //通知开始时间和通知结束时间互斥
    var bmTzEndBmTzStart = (Date.parse(bmTzEndStr) - Date.parse(bmTzStartStr)) / 3600 / 1000;
    if(bmTzEndBmTzStart< 0||bmTzEndBmTzStart== 0){
 		$("#TS_XMGL_BMGL-BM_TZ_START").addClass("blankError").addClass("errorbox");
 		$("#TS_XMGL_BMGL-BM_TZ_END").addClass("blankError").addClass("errorbox");
		return false;
 	}
    //报名开始时间和报名结束时间互斥
    var bmEndBmStart = (Date.parse(bmEndStr) - Date.parse(bmStartStr)) / 3600 / 1000;
    if(bmEndBmStart< 0||bmEndBmStart== 0){
 		$("#TS_XMGL_BMGL-BM_START").addClass("blankError").addClass("errorbox");
 		$("#TS_XMGL_BMGL-BM_END").addClass("blankError").addClass("errorbox");
		return false;
 	}
    //报名开始时间和通知时间互斥
    var bmStartBmTzStart = (Date.parse(bmStartStr) - Date.parse(bmTzStartStr)) / 3600 / 1000;
    if(bmStartBmTzStart< 0||bmStartBmTzStart== 0){
 		$("#TS_XMGL_BMGL-BM_START").addClass("blankError").addClass("errorbox");
 		$("#TS_XMGL_BMGL-BM_TZ_START").addClass("blankError").addClass("errorbox");
		return false;
 	}
    //考试结束时间与项目结束时间互斥
    var bmTzEndBmEnd = (Date.parse(bmTzEndStr) - Date.parse(bmEndStr)) / 3600 / 1000;
     if(bmTzEndBmEnd < 0||bmTzEndBmEnd == 0){
 		$("#TS_XMGL_BMGL-BM_END").addClass("blankError").addClass("errorbox");
 		$("#TS_XMGL_BMGL-BM_TZ_END").addClass("blankError").addClass("errorbox");
		return false;
 	}
};
//修改input时间样式
var tzStart=_viewer.getItem("BM_TZ_START");
var tzEnd=_viewer.getItem("BM_TZ_END");
var bmStart=_viewer.getItem("BM_START");
var bmEnd=_viewer.getItem("BM_END");
$('#'+tzStart._opts.id+"_div").css('min-height','32px');
$('#'+tzEnd._opts.id+"_div").css('min-height','32px');
$('#'+bmStart._opts.id+"_div").css('min-height','32px');
$('#'+bmEnd._opts.id+"_div").css('min-height','32px');

//针对通知开始时间的校验与互斥
/**_viewer.getItem("BM_TZ_START").obj.unbind("click").bind("click", function() {
	//获取四个时间段的是
	var bmTzStart=_viewer.getItem("BM_TZ_START").getValue();
	var bmTzEnd=_viewer.getItem("BM_TZ_END").getValue();
	var bmStart=_viewer.getItem("BM_START").getValue();
	var bmEnd=_viewer.getItem("BM_END").getValue();
	var tzStartTime="";
	if(bmStart !=""){
		tzStartTime="BM_START";
	}else if(bmEnd !=""){
		tzStartTime="BM_END";
	}else if(bmTzEnd !=""){
		tzStartTime="BM_TZ_END";
	}
    WdatePicker({
	  dateFmt: 'yyyy-MM-dd HH:mm:ss',
	  maxDate : "#F{$dp.$D('" + _viewer.servId +"-"+tzStartTime+"')}"
	});
});
//针对通知结束时间的校验与互斥
_viewer.getItem("BM_TZ_END").obj.unbind("click").bind("click", function() {
	//获取四个时间段的是
	var bmTzStart=_viewer.getItem("BM_TZ_START").getValue();
	var bmTzEnd=_viewer.getItem("BM_TZ_END").getValue();
	var bmStart=_viewer.getItem("BM_START").getValue();
	var bmEnd=_viewer.getItem("BM_END").getValue();
	var tzEndTime="";
	if(bmEnd !=""){
		tzEndTime="BM_END";
	}else if(bmStart  !=""){
		tzEndTime="BM_START";
	}else if(bmTzStart !=""){
		tzEndTime="BM_TZ_START";
	}
	 WdatePicker({
		dateFmt: 'yyyy-MM-dd HH:mm:ss',
	    minDate : "#F{$dp.$D('" + _viewer.servId + "-"+tzEndTime+"')}"
	 });
});

//针对报名开始时间的校验与互斥
_viewer.getItem("BM_START").obj.unbind("click").bind("click",function(){
	//获取四个时间段的是
	var bmTzStart=_viewer.getItem("BM_TZ_START").getValue();
	var bmTzEnd=_viewer.getItem("BM_TZ_END").getValue();
	var bmStart=_viewer.getItem("BM_START").getValue();
	var bmEnd=_viewer.getItem("BM_END").getValue();
	var bmMixTime="";
	var bmMaxTime="";
	if(bmTzStart !=""){
		bmMixTime="BM_TZ_START";
	}
	if(bmEnd !=""){
		bmMaxTime="BM_END";
	}else if(bmTzEnd !=""){
		bmMaxTime="BM_TZ_END";
	}
	 WdatePicker({
	    dateFmt: 'yyyy-MM-dd HH:mm:ss',
        minDate : "#F{$dp.$D('" + _viewer.servId + "-"+bmMixTime+"')}",
        maxDate : "#F{$dp.$D('" + _viewer.servId + "-"+bmMaxTime+"')}"
    });
});
//针对报名结束时间的校验与互斥
_viewer.getItem("BM_END").obj.unbind("click").bind("click",function(){
	//获取四个时间段的是
	var bmTzStart=_viewer.getItem("BM_TZ_START").getValue();
	var bmTzEnd=_viewer.getItem("BM_TZ_END").getValue();
	var bmStart=_viewer.getItem("BM_START").getValue();
	var bmEnd=_viewer.getItem("BM_END").getValue();
	var bmMixTime="";
	var bmMaxTime="";
	if(bmStart !=""){
		bmMixTime="BM_START";
	}else if(bmTzStart !=""){
		bmMixTime="BM_TZ_START";
	}
	if(bmTzEnd !=""){
		bmMaxTime="BM_TZ_END";
	}
	 WdatePicker({
	    dateFmt: 'yyyy-MM-dd HH:mm:ss',
        minDate : "#F{$dp.$D('" + _viewer.servId + "-"+bmMixTime+"')}",
        maxDate : "#F{$dp.$D('" + _viewer.servId + "-"+bmMaxTime+"')}"
    });
});*/
//返回按钮
//_viewer.getBtn("goback").unbind("click").bind("click", function() {
//	 window.location.href ="stdListView.jsp?frameId=TS_XMGL-tabFrame&sId=TS_XMGL&paramsFlag=false&title=项目管理";
//});

FireFly.doAct("TS_XMGL","finds",{"_WHERE_":" and XM_ID=(select XM_ID from TS_XMGL_SZ where XM_SZ_ID='"+XM_SZ_ID+"')"},true,false,function(data){
		datas = data._DATA_[0].XM_TYPE;
		if(datas=="资格类考试"){
			$("li.rhCard-tabs-topLi[sid='TS_XMGL_BM_FZGKS']").hide();
		}else if(datas=="其他类考试"){
			$("li.rhCard-tabs-topLi[sid='TS_XMGL_BM_KSLB']").hide();
		}
}); 


