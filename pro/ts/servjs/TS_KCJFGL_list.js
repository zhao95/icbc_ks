var _viewer = this;

//每一行添加编辑和删除
_viewer.grid._table.find("tr").each(function(index, item) {
	var l = $("#"+_viewer.servId).find('table th').length;
	var  isHavId = $('td[icode="KCJF_ID"]',item).text();
	if (index == 0 && l==6) {
		$(item).append('<th class="rhGrid-thead-th" id="oper" style="width:300px;">操作</th>');
	}
	if(index!=0 && isHavId != ""){
		$(item).append('<td class="rhGrid-td-center">'+
				 '<a class="rh-icon rhGrid-btnBar-a" operCode="optEditBtn"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
				 '<a class="rh-icon rhGrid-btnBar-a" operCode="optDeleteBtn"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'+
				 '</td>');
		// 为每个按钮绑定卡片
		 bindCard();
	}else{
		$(item).append('<td class="rhGrid-td-center"></td>');
	}
});

/*
 * 删除前方法执行  添加验证码
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
		rowEdit(pkCode,_viewer,[1283,588],[200,100]);
	})
}

