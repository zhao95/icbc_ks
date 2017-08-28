var _viewer = this;

_viewer.grid.unbindTrdblClick();
/*_viewer.grid.dblClick(function(value, node) {
	Todo.dbClickGrid(_viewer,_viewer.grid);
}, _viewer);*/

//工行需求：只有点击标题才弹出页面
$("td[icode='TODO_TITLE'] a").unbind("click").click(function() {
	_viewer.grid.deSelectAllRows();
	$(".checkTD :checkbox", $(this).parent("td").parent("tr")).attr("checked", true);
	Todo.dbClickGrid(_viewer,_viewer.grid);
});