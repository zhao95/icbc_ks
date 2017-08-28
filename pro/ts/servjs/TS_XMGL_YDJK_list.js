var _viewer = this;
//列表需要建一个code为buttons的自定义字段。
$(".rhGrid").find("tr").each(function(index,item){
	if(index !=0){
		var  dataId=item.id;
		$(item).find("td[icode='BUTTONS']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_YDJK_edit" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_YDJK_delete" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
				)
				//为每个按钮绑定卡片
				bindCard();
	}
});
/*
* 删除前方法执行
*/
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
}
//绑定的事件     
function bindCard(){
	//编辑
	jQuery("td [id='TS_XMGL_YDJK_edit']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowEdit(pkCode,_viewer,[1000,500],[200,100]);
	});
	//当行删除事件
	jQuery("td [id='TS_XMGL_YDJK_delete']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
}



