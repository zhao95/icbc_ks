var _viewer = this;

var XM_SZ_ID = _viewer.opts.XM_SZ_ID;
//var XM_ID=_viewer.opts.XM_ID;
var XM_ID=_viewer.opts.XM_ID;


if(_viewer.opts.act == "cardAdd"){
	XM_SZ_ID = _viewer.opts.XM_SZ_ID;
	if(typeof(XM_SZ_ID)!="undefined" &&typeof(XM_ID)!="undefined" ){ 
		_viewer.getItem("XM_SZ_ID").setValue(XM_SZ_ID);
		_viewer.getItem("XM_ID").setValue(XM_ID);
	}
}
//时间校验
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
 		//$("#TS_XMGL_BMGL-BM_TZ_START").addClass("blankError").addClass("errorbox");
 		//$("#TS_XMGL_BMGL-BM_TZ_END").addClass("blankError").addClass("errorbox");
 		$("#TS_XMGL_BMGL-BM_TZ_START").parent().showError("通知开始时间应早于通知结束时间");
 		$("#TS_XMGL_BMGL-BM_TZ_END").parent().showError("通知结束时间应晚于通知开始时间");
		return false;
 	}
    //报名开始时间和报名结束时间互斥
    var bmEndBmStart = (Date.parse(bmEndStr) - Date.parse(bmStartStr)) / 3600 / 1000;
    if(bmEndBmStart< 0||bmEndBmStart== 0){
 		//$("#TS_XMGL_BMGL-BM_START").addClass("blankError").addClass("errorbox");
 		//$("#TS_XMGL_BMGL-BM_END").addClass("blankError").addClass("errorbox");
 		$("#TS_XMGL_BMGL-BM_START").parent().showError("报名开始时间应早于报名结束时间");
 		$("#TS_XMGL_BMGL-BM_END").parent().showError("报名结束时间应晚于报名开始时间");
		return false;
 	}
    //报名开始时间和通知时间互斥
    var bmStartBmTzStart = (Date.parse(bmStartStr) - Date.parse(bmTzStartStr)) / 3600 / 1000;
    if(bmStartBmTzStart< 0){
 		//$("#TS_XMGL_BMGL-BM_START").addClass("blankError").addClass("errorbox");
 		//$("#TS_XMGL_BMGL-BM_TZ_START").addClass("blankError").addClass("errorbox");
 		$("#TS_XMGL_BMGL-BM_START").parent().showError("报名开始时间不应早于通知开始时间");
 		$("#TS_XMGL_BMGL-BM_TZ_START").parent().showError("通知开始时间不应晚于报名开始时间");
		return false;
 	}
    //报名结束时间和通知结束时间互斥
    var bmTzEndBmEnd = (Date.parse(bmTzEndStr) - Date.parse(bmEndStr)) / 3600 / 1000;
     if(bmTzEndBmEnd < 0){
 		//$("#TS_XMGL_BMGL-BM_END").addClass("blankError").addClass("errorbox");
 		//$("#TS_XMGL_BMGL-BM_TZ_END").addClass("blankError").addClass("errorbox");
 		$("#TS_XMGL_BMGL-BM_END").parent().showError("报名结束时间不应晚于通知结束时间");
 		$("#TS_XMGL_BMGL-BM_TZ_END").parent().showError("通知结束时间不应早于报名结束时间");
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

//返回按钮
//_viewer.getBtn("goback").unbind("click").bind("click", function() {
//	 window.location.href ="stdListView.jsp?frameId=TS_XMGL-tabFrame&sId=TS_XMGL&paramsFlag=false&title=项目管理";
//});

FireFly.doAct("TS_XMGL","finds",{"_WHERE_":" and XM_ID=(select XM_ID from TS_XMGL_SZ where XM_SZ_ID='"+XM_SZ_ID+"')"},true,false,function(data){
		var datas = data._DATA_[0].XM_TYPE;
		var xmName=data._DATA_[0].XM_NAME;
		_viewer.getItem("BM_NAME").setValue(xmName);
		if(datas=="资格类考试"){
			$("li.rhCard-tabs-topLi[sid='TS_XMGL_BM_FZGKS']").hide();
		}else if(datas=="其他类考试"){
			$("li.rhCard-tabs-topLi[sid='TS_XMGL_BM_KSQZ']").hide();
		}
		
});

$("#TS_XMGL_BMGL-BM_ODEPTCODE__NAME").unbind("click").bind("click", function(event) {

	var configStr = "TS_ORG_DEPT_ALL,{'TYPE':'single','sId':'TS_ORG_DEPT','pvlg':'CODE_PATH'}";

	var options = {
			"config" :configStr,
			"params" : {"USE_SERV_ID":"TS_ORG_DEPT"},
			"parHandler":_viewer,
			"formHandler":_viewer.form,
			"replaceCallBack":function(idArray,nameArray) {//回调，idArray为选中记录的相应字段的数组集合
				
				var codes = idArray;
				var names = nameArray;
				$("#TS_XMGL_BMGL-BM_ODEPTCODE__NAME").val(names);
				$("#TS_XMGL_BMGL-BM_ODEPTCODE").val(codes);
				$("#TS_XMGL_BMGL-BM_ODEPT").val(names);
				console.log($("#TS_XMGL_BMGL-BM_ODEPT").val());
				console.log($("#TS_XMGL_BMGL-BM_ODEPTCODE").val());
			}
	};
	
	var queryView = new rh.vi.rhDictTreeView(options);
	queryView.show(event,[],[0,495]);
});

//保存自后
_viewer.afterSave=function(){
	var bmStartTime=_viewer.getItem("BM_START").getValue();
	var bmEndTime=_viewer.getItem("BM_END").getValue();
	var bmTime=bmStartTime+"  至  "+bmEndTime;
	var param={};
	//(String xmszExplain,String xmid,String xmszid)
	param["xmszExplain"]=bmTime;
	param["xmid"]=XM_ID;
	param["xmszid"]=XM_SZ_ID;
	FireFly.doAct("TS_XMGL_SZ","getTimes",param,true,false);
}

//UPDATE 表名称 SET 列名称 = 新值 WHERE 列名称 = 某值
