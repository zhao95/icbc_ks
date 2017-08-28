var _viewer = this;
_viewer.getBtn("impDept").click(function(event){
	var configStr = "SY_ORG_DEPT_SUB,{'TARGET':'DEPT_CODE~DEPT_NAME','SOURCE':'DEPT_CODE~DEPT_NAME'," +
	"'HIDE':'','TYPE':'multi','HTMLITEM':''}";
	var options = {
			"config" :configStr,
			"parHandler":_viewer,
			"formHandler":_viewer.form,
			"replaceCallBack":function(idArray) {//回调，idArray为选中记录的相应字段的数组集合
				var codes = idArray.DEPT_CODE.split(",");
				var names = idArray.DEPT_NAME.split(",");

				for(var i=0;i<codes.length;i++){
					var param = {};
					param["QZ_ID"] = _viewer.getParHandler()._pkCode;
					param["DU_CODE"] = codes[i];
					param["DU_NAME"] = names[i];
					param["DU_TYPE"] = 2;
					FireFly.doAct(_viewer.servId, "save", param, true);	
				}
				_viewer.refresh();
			}
	};
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);
});
_viewer.getBtn("impUser").click(function(){
	var configStr = "SY_ORG_USER_SUB,{'TARGET':'USER_CODE~USER_NAME','SOURCE':'USER_CODE~USER_NAME'," +
	"'HIDE':'','TYPE':'multi','HTMLITEM':''}";
	var options = {
			"config" :configStr,
			"parHandler":_viewer,
			"formHandler":_viewer.form,
			"replaceCallBack":function(idArray) {//回调，idArray为选中记录的相应字段的数组集合
				var codes = idArray.USER_CODE.split(",");
				var names = idArray.USER_NAME.split(",");

				for(var i=0;i<codes.length;i++){
					var param = {};
					param["QZ_ID"] = _viewer.getParHandler()._pkCode;
					param["DU_CODE"] = codes[i];
					param["DU_NAME"] = names[i];
					param["DU_TYPE"] = 1;
					FireFly.doAct(_viewer.servId, "save", param, true);	
				}
				_viewer.refresh();
			}
	};
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);
});