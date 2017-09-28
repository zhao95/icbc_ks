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


//针对通知开始时间的校验与互斥
_viewer.getItem("BM_TZ_START").obj.unbind("click").bind("click", function() {
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
});
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


