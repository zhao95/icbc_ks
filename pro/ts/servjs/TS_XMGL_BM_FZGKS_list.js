var _viewer = this;
//返回按钮
_viewer.getBtn("goback").unbind("click").bind("click", function() {
	 window.location.href ="stdListView.jsp?frameId=TS_XMGL-tabFrame&sId=TS_XMGL&paramsFlag=false&title=项目管理";
});
//列表需建一个code为BUTTONS的自定义字段，没行增加1个按钮
$("#TS_XMGL_BM_FZGKS .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		$(item).find("td[icode='buttons']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_BM_FZGKS_delete" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
								);
		// 为按钮绑定卡片
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
	//当行删除事件
	jQuery("td [id='TS_XMGL_BM_FZGKS_delete']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
}