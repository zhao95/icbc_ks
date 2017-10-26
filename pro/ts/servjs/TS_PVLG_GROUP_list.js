var _viewer = this;
var width = jQuery(window).width()-200;
var height = jQuery(window).height()-100;
//查询选择时呈现的操作.
if(_viewer.params.BUT){
	//取消行点击事件
	$(".rhGrid").find("tr").unbind("dblclick");
	$("#TS_PVLG_GROUP .rhGrid-thead tr").append('<th icode="button" class="rhGrid-thead-th" style="width:18.2%;">操作</th>');
	$("#TS_PVLG_GROUP .rhGrid-tbody ").find("tr").each(function(index, item) {//icode="button" class="rhGrid-thead-th" style="width:18.2%;"
			var dataId = item.id;
			$(item).append('<td icode="button" class="rhGrid-td-center " style="width:18.2%;"></td>');
			$(item).find("td[icode='button']").append(
					'<a class="rh-icon rhGrid-btnBar-a" id="TS_PVLG_GROUP_look" actcode="look" rowpk="'+dataId+'"><span class="rh-icon-inner">详细</span><span class="rh-icon-img btn-view"></span></a>'
			);
			// 为每个按钮绑定卡片
			lookCard();
		
	});
}

function  lookCard(){
	
	jQuery("td [actcode='look']").unbind("click").bind("click",function(){
		var pkCode = jQuery(this).attr("rowpk");
		// 定义一个对象
		var strwhere = "and G_ID ='" + pkCode + "'";
		var params = {"G_ID" : pkCode,"_extWhere" : strwhere};
		var url = "TS_PVLG_GROUP_USER.list.do?&_extWhere=" + strwhere;
		var options = {"url" : url,"params" : params,"menuFlag" : 3,"top" : true};
		Tab.open(options);
	});
	
}
//每一行添加编辑和删除
$("#TS_PVLG_GROUP .rhGrid").find("tr").each(function(index, item) {
	if(index != 0) {
		var dataId = item.id;
		
		$(item).find("td[icode='BUTTONS']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_GROUP-upd" actcode="upd" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_GROUP-delete" actcode="delete" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
//		'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_GROUP-upd" actcode="upd" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'
//				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_ROLE-delete" actcode="delete" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'		
		);
		// 为每个按钮绑定卡片
		bindCard();
	}
});

//绑定的事件     
function bindCard() {
	//当行删除事件
	jQuery("td [id='TS_PVLG_GROUP-delete']").unbind("click").bind("click", function() {
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
	
	//当行编辑事件
	jQuery("td [id='TS_PVLG_GROUP-upd']").unbind("click").bind("click", function() {
		var pkCode = jQuery(this).attr("rowpk");
//		var height = jQuery(window).height()-200;
//		var width = jQuery(window).width()-200;
		rowEdit(pkCode,_viewer,[width,height],[100,50]);
	});
}

/*
 * 删除前方法执行
 */
_viewer.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

/**
 * 目录管理
 */
_viewer.getBtn("ctlgMgr").unbind("click").bind("click",function(event) {
	
	module = 'GROUP';
	
	var params = {"isHide":"true", "CTLG_MODULE":module};
	
	var options = {"url":"TS_COMM_CATALOG_GROUP.list.do?isHide=true&CTLG_MODULE="+module,"params":params,"menuFlag":3,"top":true};
	Tab.open(options);

});

/**
 * 添加按钮
 */
_viewer.getBtn("add").unbind("click").bind("click",function() {
	
	
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
			"xyArray":[100,50]
	};
	
	var cardView = new rh.vi.cardView(temp);
	cardView.show();
	return false;
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

