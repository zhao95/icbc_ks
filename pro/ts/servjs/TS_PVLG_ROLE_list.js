var _viewer = this;


//每一行添加编辑和删除
$("#TS_PVLG_ROLE .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		
		var roleType = $(item).find("td[icode='ROLE_TYPE']").text();
		
		if(roleType ==1) {
			
			var orgLv = $(item).find("td[icode='ROLE_ORG_LV__NAME']").text();
			
			$(item).find("td[icode='ROLE_DNAME']").text(orgLv);
		}
		
		$(item).find("td[icode='BUTTONS']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_ROLE-upd" actcode="upd" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_ROLE-delete" actcode="delete" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
				);
		// 为每个按钮绑定卡片
		bindCard();
	}
});

//绑定的事件     
function bindCard() {
	//当行删除事件
	jQuery("td [id='TS_PVLG_ROLE-delete']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
	
	//当行编辑事件
	jQuery("td [id='TS_PVLG_ROLE-upd']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		var height = jQuery(window).height()-200;
		var width = jQuery(window).width()-200;
		rowEdit(pkCode,_viewer,[width,height],[100,100]);
	});
	
}

/*
 * 删除前方法执行
 */
_viewer.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

/*
 * 删除后方法执行
 */
_viewer.afterDelete = function() {
	_viewer.refreshTreeAndGrid();
}

/**
 * 添加按钮
 */
_viewer.getBtn("add").unbind("click").bind("click",function() {
	var width = jQuery(window).width()-200;
	var height = jQuery(window).height()-200;
	
	var ctlgPcode = _viewer._transferData["CTLG_PCODE"];
	
	if(ctlgPcode == "" || typeof(ctlgPcode) == "undefined") {
		alert("请选择目录 !");
		return false;
	}
	var temp = {"act":UIConst.ACT_CARD_ADD,
			"sId":_viewer.servId,
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
$("a[actcode='refresh']").unbind("mousedown").unbind("click").bind("click",function(event) {
	var _loadbar = new rh.ui.loadbar();
	_loadbar.show(true);
	
	_viewer.refreshTreeAndGrid();
	
	_loadbar.hideDelayed();
});

/**
 * 目录管理
 */
_viewer.getBtn("ctlgMgr").unbind("click").bind("click",function(event) {
	
	module = 'ROLE';
	
	var params = {"isHide":"true", "CTLG_MODULE":module};
	
	var options = {"tTitle":"目录管理","url":"TS_COMM_CATALOG_ROLE.list.do?isHide=true&CTLG_MODULE="+module,"params":params,"menuFlag":3,"top":true};
	Tab.open(options);

});

