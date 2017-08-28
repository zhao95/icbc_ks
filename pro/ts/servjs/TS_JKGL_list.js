var _viewer = this;
$(".rhGrid").find("tr").unbind("dblclick"); 
$(".rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		$(item).find("td[icode='BUTTONS']").append(
				 '<a class="rh-icon rhGrid-btnBar-a" operCode="optEditBtn"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
				 '<a class="rh-icon rhGrid-btnBar-a" operCode="optDeleteBtn"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
				 );
		// 为每个按钮绑定卡片
		bindCard();
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
		rowEdit(pkCode,_viewer,[1000,600],[200,100]);
	});
}