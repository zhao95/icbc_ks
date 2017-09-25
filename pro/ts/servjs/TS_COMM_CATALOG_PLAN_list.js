var _viewer = this;

var module = "PLAN";

var params = _viewer.getParams();

if(params.CTLG_MODULE) {
//	module = params.CTLG_MODULE;

	//打开页面 处理grid和tree 如(Tab.open)
//	if(params.isHide == "true") {
//		params.isHide = "false";
//		var where = " AND CTLG_MODULE = '"+ module +"'";
//		_viewer.refreshTreeGrid(where,where);
//	}
}

if(module == "" || typeof(module) == "undefined") {
	alert("目录所属模块为空，请重新打开列表！");
}

_viewer.grid._table.find("tr").each(function(index, item) {
	var  readFlag = $('td[icode="READ_FLAG"]',item).text();
	//readFlag值为1 表示只读 不能删除
	if(index != 0 && readFlag == 1) {
		var gtlgId = $('td[icode="CTLG_ID"]',item).text();
		_viewer.grid.getCheckBoxItem(gtlgId).remove();
	}
});

/**
 * 添加按钮
 */
_viewer.getBtn("add").unbind("click").bind("click",function() {
	
	var pcodeh = _viewer._transferData["CTLG_PCODE_H"];
	
	if(pcodeh == "" || typeof(pcodeh) == "undefined") {
		alert("请选择添加目录的层级 !");
		return false;
	}
	
	var width = jQuery(window).width()-200;
	var height = jQuery(window).height()-200;
	
	var temp = {"act":UIConst.ACT_CARD_ADD,
			"sId":_viewer.servId,
			"params":  {
				"CTLG_MODULE" : module,
			},
			"transferData": _viewer._transferData,
			"links":_viewer.links,
			"parHandler":_viewer,
			"widHeiArray":[width,height],
			"xyArray":[100,100]
	};
	var cardView = new rh.vi.cardView(temp);
	cardView.show();
	return false;
});

/**
 * 刷新按钮
 */
//$("a[actcode='refresh']").unbind("mousedown").unbind("click").bind("click",function(event) {
//	var _loadbar = new rh.ui.loadbar();
//	_loadbar.show(true);
//	var where = " AND CTLG_MODULE = '"+ module +"'";
//	_viewer.refreshTreeGrid(where,where);
//	_viewer._transferData = {};
//	_loadbar.hideDelayed();
//});

/**
 * 初始化按钮
 */
_viewer.getBtn("initCatalog").unbind("click").bind("click",function(event) {
	
	var _loadbar = new rh.ui.loadbar();
	_loadbar.show(true);
	
	var param = {};
	
	var odept = System.getVar("@ODEPT_CODE@");
	var cmpyCode = System.getVar("@CMPY_CODE@");
	
	param["ODEPT_CODE"] = odept;
	param["CMPY_CODE"] = cmpyCode;
	param["CTLG_MODULE"] = module;
	
	if(module == "ROOT"){
//		param["INIT_MODULE"] = "all";
		alert("无目录模块，同步失败！");
		_loadbar.hideDelayed();
		return;
	}
	
	var result = FireFly.doAct(_viewer.servId, "initCatalog", param, false);
    if (result[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {//成功后刷新列表
    	_viewer.refresh();
    	_loadbar.hideDelayed();
    }
    
});

/*
 * 删除前方法执行
 */
_viewer.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
}

/*
 * 删除后方法执行
 */
_viewer.afterDelete = function() {
//	var where = " AND CTLG_MODULE = '"+ module +"'";
//	_viewer.refreshTreeGrid(where,where);
	_viewer.refresh();
}