var _viewer = this;
var width = jQuery(window).width()-200;
var height = jQuery(window).height()-50;
$(".rhGrid").find("tr").unbind("dblclick");
//每一行添加编辑和删除
$("#TS_PVLG_ROLE .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		if(dataId == "") return;
		var roleType = $(item).find("td[icode='ROLE_TYPE']").text();
		
		if(roleType ==1) {
			
			var orgLv = $(item).find("td[icode='ROLE_ORG_LV__NAME']").text();
			
			$(item).find("td[icode='ROLE_DNAME']").text(orgLv);
		}
		
		$(item).find("td[icode='BUTTONS']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_ROLE-upd" actcode="upd" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'
				);
		
		var isdel = $(item).find("td[icode='ROLE_DEL']").text();
		
		if(isdel==0) {
			
			$(item).find("td[icode='BUTTONS']").append(
					'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_ROLE-delete" actcode="delete" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
			);
		} else {
			_viewer.grid.getCheckBoxItem(dataId).remove();
		}

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
		if(width <= "900") {
			var height1 = jQuery(window).height()-10;
			var width1 = jQuery(window).width()-10;
			rowEdit(pkCode,_viewer,[width1,height1],[5,10]);
		} else {
			rowEdit(pkCode,_viewer,[width,height],[100,50]);
		}
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
	
	
	var ctlgPcode = _viewer._transferData["CTLG_PCODE"];
	
	if(ctlgPcode == "" || typeof(ctlgPcode) == "undefined") {
		alert("请选择目录 !");
		return false;
	}
	
	var xyArray = [100,50];
	var widHeiArray = [width,height];
	
	if(width <= "900") {
		var height1 = jQuery(window).height()-10;
		var width1 = jQuery(window).width()-10;
		widHeiArray = [width1,height1];
		xyArray = [5,10];
	}
	
	var temp = {"act":UIConst.ACT_CARD_ADD,
			"sId":_viewer.servId,
			"transferData": _viewer._transferData,
			"links":_viewer.links,
			"parHandler":_viewer,
			"widHeiArray":widHeiArray,
			"xyArray":xyArray
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

//传给后台的数据
/*
* 业务可覆盖此方法，在导航树的点击事件加载前
*/
rh.vi.listView.prototype.beforeTreeNodeClickLoad = function(item,id,dictId) {
	var params = {};
	var user_pvlg=_viewer._userPvlg[_viewer.servId+"_PVLG"];
	params["USER_PVLG"] = user_pvlg;
	_viewer.whereData["extParams"] = params;
	 var flag = getListPvlg(item,user_pvlg);
	_viewer.listClearTipLoad();
	return flag;
};

