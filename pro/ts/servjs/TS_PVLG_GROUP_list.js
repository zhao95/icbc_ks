var _viewer = this;

//每一行添加编辑和删除
$("#TS_PVLG_GROUP .rhGrid").find("tr").each(function(index, item) {
	if(index != 0) {
		var dataId = item.id;
		
		$(item).find("td[icode='BUTTONS']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_GROUP-upd" actcode="upd" rowpk="'+dataId+'"><span class="rh-icon-inner-notext"></span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_GROUP-delete" actcode="delete" rowpk="'+dataId+'"><span class="rh-icon-inner-notext"></span><span class="rh-icon-img btn-delete"></span></a>'
				);
		// 为每个按钮绑定卡片
		bindCard();
	}
});

//绑定的事件     
function bindCard() {
	//当行删除事件
	jQuery("td [id='TS_PVLG_GROUP-delete'").unbind("click").bind("click", function() {
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
	
	//当行编辑事件
	jQuery("td [id='TS_PVLG_GROUP-upd']").unbind("click").bind("click", function() {
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

/**
 * 目录管理
 */
_viewer.getBtn("ctlgMgr").unbind("click").bind("click",function(event) {
	
	module = 'GROUP';
	
	var params = {"isHide":"true", "CTLG_MODULE":module};
	
	var options = {"url":"TS_COMM_CATALOG.list.do?isHide=true&CTLG_MODULE="+module,"params":params,"menuFlag":3};
	Tab.open(options);

});