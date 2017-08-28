var _viewer = this;

_viewer.getBtn("impFrom").click(function(event){
	//1.构造查询选择参数，其中参数【HTMLITEM】非必填，用以标识返回字段的值为html标签类的
	var configStr = "TS_XMGL_BMGL_BMGZK,{'TARGET':'BM_TITLE~BM_LEVEL~BM_WAY_CODE~BM_INFO~BM_ID','SOURCE':'BM_TITLE~BM_LEVEL~BM_WAY_CODE~BM_INFO'," +
			"'HIDE':'BM_ID,BM_WAY_CODE','TYPE':'multi','HTMLITEM':''}";
	var options = {
		"config" :configStr,
		"parHandler":_viewer,
		"formHandler":_viewer.form,
	    "replaceCallBack":function(idArray) {//回调，idArray为选中记录的相应字段的数组集合
	    	var titles = idArray.BM_TITLE.split(",");
	    	var levels = idArray.BM_LEVEL.split(",");
	    	var waycodes = idArray.BM_WAY_CODE.split(",");
	    	var infos = idArray.BM_INFO.split(",");
	    	for(var i=0;i<titles.length;i++){
	    		var param = {};
	    		param["BM_ID"] = _viewer.getParHandler()._pkCode;
		    	param["GZ_TITLE"] = titles[i];
		    	param["GZ_LEVEL"] = levels[i];
		    	param["GZ_WAY_CODE"] = waycodes[i];
		    	param["GZ_INFO"] = infos[i];
		    	FireFly.doAct(_viewer.servId, "save", param, true);	
		    	
	    	}
	    	_viewer.refresh();
		}
	};
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);
});


