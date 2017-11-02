var _viewer = this;

$(".rhGrid").find("tr").unbind("dblclick");
//每一行添加编辑和删除
$("#TS_COMM_CATALOG .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		
		$(item).find("td[icode='BUTTONS']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_COMM_CATALOG-upd" actcode="upd" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'
				);
		// 为每个按钮绑定卡片
		bindCard();
	}
});

//绑定的事件     
function bindCard() {	
	//当行编辑事件
	jQuery("td [id='TS_COMM_CATALOG-upd']").unbind("click").bind("click", function() {
		var pkCode = jQuery(this).attr("rowpk");
		var height = jQuery(window).height()-200;
		var width = jQuery(window).width()-200;
		rowEdit(pkCode,_viewer,[width,height],[100,100]);
	});
}

var module = "ROOT";

var params = _viewer.getParams();

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
	console.log(temp);
	var cardView = new rh.vi.cardView(temp);
	cardView.show();
	return false;
});

/**
 * 刷新按钮
 */
$("a[actcode='refresh']").unbind("mousedown").unbind("click").bind("click",function(event) {
	var _loadbar = new rh.ui.loadbar();
	_loadbar.show(true);
	var where = " AND CTLG_MODULE = '"+ module +"'";
	_viewer.refreshTreeGrid(where,where);
	_viewer._transferData = {};
	_loadbar.hideDelayed();
});

/**
 * 初始化按钮
 */
_viewer.getBtn("initCatalog").unbind("click").bind("click",function(event) {
	
	var _loadbar = new rh.ui.loadbar();
	_loadbar.show(true);
	
	var param = {};
	
	var cmpyCode = System.getVar("@CMPY_CODE@");
	
	param["CMPY_CODE"] = cmpyCode;
	param["CTLG_MODULE"] = module;
	
	var result = FireFly.doAct(_viewer.servId, "initCatalog", param, false);
    if (result[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {//成功后刷新列表
    	_viewer.refresh();
    	_loadbar.hideDelayed();
    }
    
});

/**
 * 目录管理1 tab.open
 */
_viewer.getBtn("ctlgMgr1").unbind("click").bind("click",function(event) {
	
	module = 'USER';
	
	var params = {"isHide":"true", "CTLG_MODULE":module};
	
	var options = {"url":"TS_COMM_CATALOG.list.do?isHide=true&CTLG_MODULE="+module,"params":params,"menuFlag":3,"top":true};
	Tab.open(options);

});

/**
 * 目录管理2 listview
 */
_viewer.getBtn("ctlgMgr2").unbind("click").bind("click",function(event) {
	
	module = 'USER';
	
	var width = jQuery(window).width()-200;
	var height = jQuery(window).height()-200;
	
	getServListDialog(event,"catalog_manager","",width,height,[100,100]);
	
	var params = {
			"sId":"TS_COMM_CATALOG",
		    "pCon":jQuery("#catalog_manager"),
	        "resetHeiWid":_viewer._resetHeiWid,
	        "parHandler":_viewer,
	        "treeExtWhere":" AND CTLG_MODULE = '"+ module +"'" ,
	        "extWhere":" AND CTLG_MODULE = '"+ module +"'",
	        "paramsFlag":"true",
	        "transParams" : {
				"CTLG_MODULE" : module
			},
			"params" : {
				"CTLG_MODULE" : module,
			}
	    };
	var listView = new rh.vi.listView(params);
	listView.show();
	
//	listView.afterSave = function(datas) {
//		alert(1);
//		_viewer.refresh();
//	};

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
	_viewer.refresh();
}