var _viewer = this;
$("#TS_KCGL_GLJG .rhGrid").find("tr").unbind("dblclick"); 
$("#TS_KCGL_GLJG .rhGrid").find("th[icode='del']").html("操作");

//删除单行数据
_viewer.grid.getBtn("del").unbind("click").bind("click",function() {
	var pk = jQuery(this).attr("rowpk");//获取主键信息
	rowDelete(pk,_viewer);
});

/*
* 删除前方法执行
*/
_viewer.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};
