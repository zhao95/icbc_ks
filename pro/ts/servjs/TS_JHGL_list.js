var _viewer = this;
$(".rhGrid").find("tr").unbind("dblclick");
// 每一行添加编辑和删除
_viewer.grid._table.find("tr").each(function(index, item) {
	if (index != 0) {
		var dataId = item.id;
		$(item).find("td[icode='BUTTONS']").append(
			'<a class="rh-icon rhGrid-btnBar-a" id="TS_JHGL_optEditBtn" operCode="optEditBtn"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'
//			+ '<a class="rh-icon rhGrid-btnBar-a" id="TS_JHGL_delete" operCode="delete"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
			+ '<a class="rh-icon rhGrid-btnBar-a" id="TS_JHGL_optViewBtn" operCode="optViewBtn"><span class="rh-icon-inner">详细</span><span class="rh-icon-img btn-view"></span></a>'
			);
			// 为每个按钮绑定卡片
			bindCard();
	}
});

/*
 * 删除前方法执行
 */
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray, _viewer);
};

// 绑定的事件
function bindCard() {
	// 当行删除事件
	jQuery("td [operCode='delete']").unbind("click").bind("click", function() {
		var pkCode = $(this).parent().parent().attr("id");
		//删除前判断是否已发布
		var paramfb = {};
		paramfb["_extWhere"] = "and JH_ID ='"+pkCode+"'";
		var beanFb = FireFly.doAct(_viewer.servId, "query", paramfb);
		//判断是否已发布，否则提示已经发布 
		if(beanFb._DATA_[0].JH_STATUS=="2"){
			_viewer.listBarTipError("请取消发布后再删除！");
		}else if(beanFb._DATA_[0].JH_STATUS=="1"){
			var paramDele = {};
			paramDele['_extWhere'] = "and JH_ID ='"+pkCode+"'";
			var result1 = FireFly.doAct("TS_JHGL","query",paramDele);
			rowDelete(pkCode, _viewer);
			Tip.show("删除计划成功！");
		}
	});

	// 当行编辑事件
	jQuery("td [operCode='optEditBtn']").unbind("click").bind("click",function() {
		var pkCode = $(this).parent().parent().attr("id");
		//编辑修改前判断是否已发布
		var paramModify = {};
		paramModify["_extWhere"] = "and JH_ID ='"+pkCode+"'";
		var beanFb = FireFly.doAct(_viewer.servId, "query", paramModify);
		//判断是否已发布，否则提示已经发布，不能修改 
		if(beanFb._DATA_[0].JH_STATUS=="2"){
			Tip.show("请取消发布后再编辑！");
		}else if(beanFb._DATA_[0].JH_STATUS=="1"){
			openMyCard(pkCode);
		}
	});

	// 当行详细计划事件
	jQuery("td [operCode='optViewBtn']").unbind("click").bind("click",function() {
		var pkCode = $(this).parent().parent().attr("id");
		var jhTitle = _viewer.grid.getRowItemValue(pkCode, "JH_TITLE");
		// 定义一个对象
		var strwhere = " and JH_PTITLE ='" + pkCode + "' ";
		var params = {"JH_ID" : pkCode,"JH_TITLE" : jhTitle,"_extWhere" : strwhere};
		var url = "TS_JHGL_XX.list.do?&_extWhere=" + strwhere;
		var options = {"url" : url,"params" : params,"menuFlag" : 3,"top" : true};
		Tab.open(options);
	});
}
// 点击时进行发布
_viewer.getBtn("fabu").unbind("click").bind("click", function() {
	var pkAarry = _viewer.grid.getSelectPKCodes();
	if (pkAarry.length == 0) {
		_viewer.listBarTipError("请选择相应记录！");
	} else {
		//遍历所选的所有大计划，依次进行判断执行。
		for (var i = 0; i < pkAarry.length; i++) {
			var paramfb = {};
			paramfb["_extWhere"] = "and JH_ID ='"+pkAarry[i]+"'";
			var beanFb = FireFly.doAct(_viewer.servId, "query", paramfb);
			//判断是否已发布，否则提示已经发布 
			if(beanFb._DATA_[0].JH_STATUS=="2"){
				_viewer.listBarTipError("所选计划已发布！");
			}else if(beanFb._DATA_[0].JH_STATUS=="1"){
				var param = {};
				param["pkCodes"] = pkAarry[i];
				FireFly.doAct(_viewer.servId, "UpdateStatusStart", param,false,false,function(){
					Tip.show("计划发布成功！");
				});
				_viewer.refresh();
			}	
		}
	}
})
// 点击时取消发布
_viewer.getBtn("qxfb").unbind("click").bind("click", function() {
	var pkAarry = _viewer.grid.getSelectPKCodes();
	if (pkAarry.length == 0) {
		_viewer.listBarTipError("请选择相应记录！");
	} else {
		var param = {};
		param["pkCodes"] = pkAarry.join(",");
		FireFly.doAct(_viewer.servId, "UpdateStatusStop", param);
		Tip.show("计划已取消发布！");
		_viewer.refresh();
	}
})

/**
 * 目录管理
 */
_viewer.getBtn("ctlgMgr").unbind("click").bind("click", function(event) {
	module = 'PLAN';
	var params = {"isHide" : "true","CTLG_MODULE" : module};
	var options = {
		"url" : "TS_COMM_CATALOG_PLAN.list.do?isHide=true&CTLG_MODULE=" + module,
		"params" : params,
		"menuFlag" : 3,
		"top" : true
	};
	Tab.open(options);
});

var height = jQuery(window).height()-200;
var width = jQuery(window).width()-200;
//_viewer.getBtn("add").unbind("click").bind("click", function() {
//	var temp = {
//		"act" : UIConst.ACT_CARD_ADD,
//		"sId" : "TS_JHGL",
//		"parHandler" : _viewer,
//		"widHeiArray" : [ width,height ],
//		"xyArray" : [ 100, 100 ]
//	};
//	var cardView = new rh.vi.cardView(temp);
//	cardView.show();
//});

//列表操作按钮 弹dialog
function openMyCard(dataId,readOnly,showTab){
	var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray": [ width,height ],"xyArray":[100,100]};
    temp[UIConst.PK_KEY] = dataId;
    if(readOnly != ""){
    	temp["readOnly"] = readOnly;
    }
    if(showTab != ""){
    	temp["showTab"] = showTab;
    }
    var cardView = new rh.vi.cardView(temp);
    cardView.show();
};


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





