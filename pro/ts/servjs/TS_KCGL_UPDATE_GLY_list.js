var _viewer = this;
//考场主键
var updateId = _viewer.getParHandler().getPKCode();
var kcId = _viewer.getParHandler().getItem("KC_ID").getValue();
_viewer.getBtn("impData").unbind("click").bind("click", function(event) {
	var param = {};
	param["SOURCE"] = "GLY_NUMBER~GLY_NAME~GLY_PHONE~GLY_MOBILE~GLY_EMAIL~GLY_ID";
	param["TYPE"] = "multi";
	param["HIDE"] = "GLY_ID";
	param["EXTWHERE"] = "and kc_id = '"+kcId+"'";
	var configStr = "TS_KCGL_GLY,"+JsonToStr(param);
	var options = {
		"config" :configStr,
		"parHandler":_viewer,
		"formHandler":_viewer.form,
	    "replaceCallBack":function(idArray) {
	    	var glyIds = idArray.GLY_ID;
	    	var glyNums = glyIds.split(",").length;
	    	for(var i = 0;i < glyNums;i++){
	    		var glyId = glyIds.split(",")[i];
	    		FireFly.doAct("TS_KCGL_GLY","byid",{"_PK_":glyId},true,false,function(data){
	    			var bean = {};
	    			bean["UPDATE_ID"] = updateId;
	    			bean["GLY_ACTION"] = "update";//引入数据默认为修改
	    			bean["GLY_NUMBER"] = data.GLY_NUMBER;
	    			bean["GLY_NAME"] = data.GLY_NAME;
	    			bean["GLY_PHONE"] = data.GLY_PHONE;
	    			bean["GLY_MOBILE"] = data.GLY_MOBILE;
	    			bean["GLY_EMAIL"] = data.GLY_EMAIL;
	    			bean["GLY_ID"] = data.GLY_ID;
	    			FireFly.doAct("TS_KCGL_UPDATE_GLY","save",bean);
	    		});
	    	}
		}
	};
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);	 
});