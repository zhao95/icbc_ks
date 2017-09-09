var _viewer = this;

var updateId = _viewer.getParHandler().getPKCode();
var kcId = _viewer.getParHandler().getItem("KC_ID").getValue();
_viewer.getBtn("impData").unbind("click").bind("click", function(event) {
	var param = {};
	param["SOURCE"] = "IPZ_IP~IPZ_ZWH~IPZ_DESC~IPZ_ID";
	param["TYPE"] = "multi";
	param["HIDE"] = "IPZ_ID";
	param["EXTWHERE"] = "and kc_id = '"+kcId+"'";
	var configStr = "TS_KCGL_IPZWH,"+JsonToStr(param);
	var options = {
		"config" :configStr,
		"parHandler":_viewer,
		"formHandler":_viewer.form,
	    "replaceCallBack":function(idArray) {
	    	var glyIds = idArray.GLY_ID;
	    	var glyNums = glyIds.split(",").length;
	    	for(var i = 0;i < glyNums;i++){
	    		var glyId = glyIds.split(",")[i];
	    		FireFly.doAct("TS_KCGL_IPZWH","byid",{"_PK_":glyId},true,false,function(data){
	    			var bean = {};
	    			bean["UPDATE_ID"] = updateId;
	    			bean["IPZ_ACTION"] = "update";//引入数据默认为修改
	    			bean["IPZ_IP"] = data.IPZ_IP;
	    			bean["IPZ_ZWH"] = data.IPZ_ZWH;
	    			bean["IPZ_DESC"] = data.IPZ_DESC;
	    			bean["IPZ_ID"] = data.IPZ_ID;
	    			FireFly.doAct("TS_KCGL_UPDATE_IPZWH","save",bean);
	    		});
	    	}
		}
	};
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);	 
});