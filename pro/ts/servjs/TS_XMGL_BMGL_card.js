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


//针对通知时间的校验
//针对时间的校验
_viewer.getItem("BM_TZ_START").obj.unbind("click").bind("click", function() {
	    WdatePicker({
	        maxDate : "#F{$dp.$D('" + _viewer.servId + "-BM_TZ_END')}"
	    });
	});
_viewer.getItem("BM_TZ_END").obj.unbind("click").bind("click", function() {

	    WdatePicker({
	        minDate : "#F{$dp.$D('" + _viewer.servId + "-BM_TZ_START')}"
	    });
	});



//针对报名时间的校验
_viewer.getItem("BM_START").obj.unbind("click").bind("click", function() {
	    WdatePicker({
	        maxDate : "#F{$dp.$D('" + _viewer.servId + "-BM_END')}"
	    });
	});
_viewer.getItem("BM_END").obj.unbind("click").bind("click", function() {

	    WdatePicker({
	        minDate : "#F{$dp.$D('" + _viewer.servId + "-BM_START')}"
	    });
	});

//返回按钮
_viewer.getBtn("goback").unbind("click").bind("click", function() {
	 window.location.href ="stdListView.jsp?frameId=TS_XMGL-tabFrame&sId=TS_XMGL&paramsFlag=false&title=项目管理";
});




FireFly.doAct("TS_XMGL","finds",{"_WHERE_":" and XM_ID=(select XM_ID from TS_XMGL_SZ where XM_SZ_ID='"+XM_SZ_ID+"')"},true,false,function(data){
		datas = data._DATA_[0].XM_TYPE;
		if(datas=="资格类考试"){
			$("li.rhCard-tabs-topLi[sid='TS_XMGL_BM_FZGKS']").hide();
		}else if(datas=="其他类考试"){
			$("li.rhCard-tabs-topLi[sid='TS_XMGL_BM_KSLB']").hide();
		}
}); 


