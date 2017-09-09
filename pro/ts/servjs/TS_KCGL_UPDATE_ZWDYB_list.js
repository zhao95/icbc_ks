var _viewer = this;
var updateId = _viewer.getParHandler().getPKCode();
var kcId = _viewer.getParHandler().getItem("KC_ID").getValue();
_viewer.getBtn("impData").unbind("click").bind("click", function(event) {
	var param = {};
	param["SOURCE"] = "ZW_ZWH_XT~ZW_ZWH_SJ~ZW_ID";
	param["TYPE"] = "multi";
	param["HIDE"] = "ZW_ID";
	param["EXTWHERE"] = "and kc_id = '"+kcId+"'";
	var configStr = "TS_KCGL_ZWDYB,"+JsonToStr(param);
	var options = {
		"config" :configStr,
		"parHandler":_viewer,
		"formHandler":_viewer.form,
	    "replaceCallBack":function(idArray) {
	    	var glyIds = idArray.GLY_ID;
	    	var glyNums = glyIds.split(",").length;
	    	for(var i = 0;i < glyNums;i++){
	    		var glyId = glyIds.split(",")[i];
	    		FireFly.doAct("TS_KCGL_ZWDYB","byid",{"_PK_":glyId},true,false,function(data){
	    			var bean = {};
	    			bean["UPDATE_ID"] = updateId;
	    			bean["ZW_ACTION"] = "update";//引入数据默认为修改
	    			bean["ZW_ZWH_XT"] = data.ZW_ZWH_XT;
	    			bean["ZW_ZWH_SJ"] = data.ZW_ZWH_SJ;
	    			bean["ZW_ID"] = data.ZW_ID;
	    			FireFly.doAct("TS_KCGL_UPDATE_ZWDYB","save",bean);
	    		});
	    	}
		}
	};
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);	 
});