var _viewer = this;
//每一行添加编辑和删除
_viewer.grid._table.find("tr").each(function(index, item) {
	var l = $("#TS_JHGL").find('table th').length;
	var isHavId = $("#TS_JHGL table").find("tbody").find("tr").eq(1).attr("id");
	if (index == 0 && l==10) {
		$(item).append('<th class="rhGrid-thead-th" id="oper" style="width:300px;">操作</th>');
	}
	if(index!=0 && isHavId != undefined){
		$(item).append('<td class="rhGrid-td-center">'+
				 '<a class="rh-icon rhGrid-btnBar-a" operCode="optEditBtn"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
				 '<a class="rh-icon rhGrid-btnBar-a" operCode="optDeleteBtn"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'+
				 '<a class="rh-icon rhGrid-btnBar-a" operCode="optViewBtn"><span class="rh-icon-inner">详细</span><span class="rh-icon-img btn-view"></span></a>'+
				 '</td>');
		// 为每个按钮绑定卡片
		 bindCard();
	}else{
		$(item).append('<td class="rhGrid-td-center"></td>');
	}
});

/*
 * 删除前方法执行
 */
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

//绑定的事件     
function bindCard(){
	//当行删除事件
	jQuery("td [operCode='optDeleteBtn']").unbind("click").bind("click", function(){
		var pkCode = $(this).parent().parent().attr("id");
		rowDelete(pkCode,_viewer);
	});
	
	//当行编辑事件
	jQuery("td [operCode='optEditBtn']").unbind("click").bind("click", function(){
		var pkCode = $(this).parent().parent().attr("id");
	    rowEdit(pkCode,_viewer,[1283,498],[200,100]);
	});
	
	//当行详细计划事件
	jQuery("td [operCode='optViewBtn']").unbind("click").bind("click", function(){
		var pkCode = $(this).parent().parent().attr("id");
		var jhTitle = _viewer.grid.getRowItemValue(pkCode,"JH_TITLE");
		//定义一个对象，将要传递的数据放入(isStop的值是随意定义的)
		var params = {"JH_ID":pkCode,"IS_HIDE":true,"isStop":1,"JH_TITLE":jhTitle};
		var url ="TS_JHGL_XX.list.do";
		var options = {
			"url":url,
			"params":params,
			"menuFlag":2,
		};
		Tab.open(options);
	});	

}
//点击时进行发布
_viewer.getBtn("fabu").unbind("click").bind("click", function() {
	var pkAarry = _viewer.grid.getSelectPKCodes();
	if (pkAarry.length == 0) {
		_viewer.listBarTipError(Language.transStatic("rhListView_string6"));
	} else {
			var param = {};
			param["pkCodes"] = pkAarry.join(",");
			FireFly.doAct(_viewer.servId, "UpdateStatusStart", param);
			_viewer.refresh();
		} 
})
//点击时取消发布
_viewer.getBtn("qxfb").unbind("click").bind("click", function() {
	var pkAarry = _viewer.grid.getSelectPKCodes();
	if (pkAarry.length == 0) {
		_viewer.listBarTipError(Language.transStatic("rhListView_string6"));
	} else {
			var param = {};
			param["pkCodes"] = pkAarry.join(",");
			FireFly.doAct(_viewer.servId, "UpdateStatusStop", param);
			_viewer.refresh();
		} 
})

/**
 * 目录管理
 */
_viewer.getBtn("ctlgMgr").unbind("click").bind("click",function(event) {
	
	module = 'PLAN';
	
	var params = {"isHide":"true", "CTLG_MODULE":module};
	
	var options = {"url":"TS_COMM_CATALOG.list.do?isHide=true&CTLG_MODULE="+module,"params":params,"menuFlag":3};
	Tab.open(options);

});
