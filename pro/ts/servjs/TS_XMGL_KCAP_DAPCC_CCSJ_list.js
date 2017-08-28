var _viewer = this;

var linkWhere = _viewer.opts.linkWhere;
var ccId = linkWhere.split("'")[1];
var xmId = _viewer.getParHandler().getParHandler().getParHandler()._pkCode;
_viewer.getBtn("add").unbind("click").bind("click", function(event) {
	var param = {};
	param["SOURCE"] = "ARR_CC~ARR_START~ARR_END";
	param["TYPE"] = "multi";
	param["EXTWHERE"] = "and ARR_STATE = 1 and XM_ID = '"+xmId+"'";
	var configStr = "TS_XMGL_CCCS_ARRANGE,"+JsonToStr(param);
	var options = {
		"config" :configStr,
		"parHandler":_viewer,
		"formHandler":_viewer.form,
	    "replaceCallBack":function(idArray) {
	    	var starts = idArray.ARR_START.split(",");
	    	var ends = idArray.ARR_END.split(",");
	    	var ccs = idArray.ARR_CC.split(",");
	    	for(var i=0;i<starts.length;i++){
	    		var data = {}
	    		data["CC_ID"] = ccId;
	    		data["SJ_CC"] = ccs[i];
	    		data["SJ_START"] = starts[i];
	    		data["SJ_END"] = ends[i];
	    		FireFly.doAct("TS_XMGL_KCAP_DAPCC_CCSJ", "save", data);
	    	}
	    	_viewer.refresh();
	    }
	};
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);
	
});
