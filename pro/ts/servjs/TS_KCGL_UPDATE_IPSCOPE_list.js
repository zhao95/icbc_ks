var _viewer = this;
//考场主键
var updateId = _viewer.getParHandler().getPKCode();
var kcId = _viewer.getParHandler().getItem("KC_ID").getValue();
_viewer.getBtn("impData").unbind("click").bind("click", function(event) {
	var param = {};
	param["SOURCE"] = "IPS_TITLE~IPS_SCOPE~IPS_DESC~IPS_ID";
	param["TYPE"] = "multi";
	param["HIDE"] = "IPS_ID";
	param["EXTWHERE"] = "and kc_id = '"+kcId+"'";
	var configStr = "TS_KCGL_IPSCOPE,"+JsonToStr(param);
	var options = {
		"config" :configStr,
		"parHandler":_viewer,
		"formHandler":_viewer.form,
	    "replaceCallBack":function(idArray) {
	    	var glyIds = idArray.GLY_ID;
	    	var glyNums = glyIds.split(",").length;
	    	for(var i = 0;i < glyNums;i++){
	    		var glyId = glyIds.split(",")[i];
	    		FireFly.doAct("TS_KCGL_IPSCOPE","byid",{"_PK_":glyId},true,false,function(data){
	    			var bean = {};
	    			bean["UPDATE_ID"] = updateId;
	    			bean["IPS_ACTION"] = "update";//引入数据默认为修改
	    			bean["IPS_TITLE"] = data.IPS_TITLE;
	    			bean["IPS_SCOPE"] = data.IPS_SCOPE;
	    			bean["IPS_DESC"] = data.IPS_DESC;
	    			bean["IPS_ID"] = data.IPS_ID;
	    			FireFly.doAct("TS_KCGL_UPDATE_IPSCOPE","save",bean);
	    		});
	    	}
		}
	};
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);	 
});