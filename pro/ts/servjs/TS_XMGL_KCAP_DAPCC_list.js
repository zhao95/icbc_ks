var _viewer = this;
$("#TS_XMGL_KCAP_DAPCC .rhGrid").find("th[icode='del']").html("操作");

var xmId = _viewer.getParHandler()._pkCode;
//得到当前机构代码 得到考场
var odeptCode = System.getVar("@ODEPT_CODE@");
//确认当前机构是总行/省级机构/市级机构
var level = System.getVar("@ODEPT_LEVEL@");

_viewer.getBtn("add").unbind("click").bind("click", function(event) {
//	if(level == 1){
//		//总行
//	}else if(level == 2){
//		//省分行
//	}else if(level == 3){
//		//地市
//	}else{
//		
//	}
	
	var param = {};
	param["SOURCE"] = "KC_ID~KC_NAME~KC_ADDRESS~CTLG_PCODE";
	param["TYPE"] = "multi";
	param["HIDE"] = "KC_ID,CTLG_PCODE";
//	param["EXTWHERE"] = "and KC_ODEPTCODE = '"+odeptCode+"'";
	param["EXTWHERE"] = " and GROUP_ID in (select group_id from TS_KCZGL_GROUP b where serv_id = 'ts_kczgl_group' and b.kcz_id in (select kcz_id from TS_KCZGL a where serv_id = 'ts_xmgl_cccs_kczgl' and XM_ID = '"+xmId+"'))";
	var configStr = "TS_XMGL_KCAP_DAPCC_UTIL_V,"+JsonToStr(param);
	var options = {
		"config" :configStr,
		"parHandler":_viewer,
		"formHandler":_viewer.form,
	    "replaceCallBack":function(idArray) {
	    	var ids = idArray.KC_ID.split(",");
	    	var kcNames = idArray.KC_NAME.split(",");
	    	for(var i=0;i<ids.length;i++){
	    		var data = {}
	    		data["XM_ID"] = xmId;
	    		data["KC_ID"] = ids[i];
	    		data["KC_NAME"] = kcNames[i];
	    		FireFly.doAct("TS_XMGL_KCAP_DAPCC", "save", data);
	    	}
	    	_viewer.refresh();
	    }
	};
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);
});

/*
 * 删除前方法执行
 */
_viewer.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

_viewer.getBtn("addOther").unbind("click").bind("click",function(event){
	
	var param = {};
	param["SOURCE"] = "KC_ID~KC_NAME~KC_ADDRESS~KC_ODEPTCODE";
	param["TYPE"] = "multi";
	param["HIDE"] = "KC_ID,KC_ODEPTCODE";
//	param["EXTWHERE"] = "and KC_ODEPTCODE = '"+odeptCode+"'";
	var configStr = "TS_KCGL,"+JsonToStr(param);
	var options = {
		"config" :configStr,
		"parHandler":_viewer,
		"formHandler":_viewer.form,
	    "replaceCallBack":function(idArray) {
	    	var ids = idArray.KC_ID.split(",");
	    	var kcNames = idArray.KC_NAME.split(",");
	    	for(var i=0;i<ids.length;i++){
	    		var data = {}
	    		data["XM_ID"] = xmId;
	    		data["KC_ID"] = ids[i];
	    		data["KC_NAME"] = kcNames[i];
	    		FireFly.doAct("TS_XMGL_KCAP_DAPCC", "save", data);
	    	}
	    	_viewer.refresh();
	    }
	};
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);
});