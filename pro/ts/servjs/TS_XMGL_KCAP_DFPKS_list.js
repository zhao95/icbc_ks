var _viewer = this;
var height = jQuery(window).height()-200;
var width = jQuery(window).width()-200;

//取消行点击事件
$(".rhGrid").find("tr").unbind("dblclick");

/*
* 业务可覆盖此方法，在导航树的点击事件加载前
*/
_viewer.beforeTreeNodeClickLoad = function(item,id,dictId) {
	var params = {};
	var user_pvlg=_viewer._userPvlg[_viewer.servId+"_PVLG"];
	params["USER_PVLG"] = user_pvlg;
	_viewer.whereData["extParams"] = params;
	 var flag = getListPvlg(item,user_pvlg,"CODE_PATH");
	_viewer.listClearTipLoad();	
	return flag;
};

_viewer.afterTreeNodeClick = function(item,id,dictId) {
	_viewer._transferData["S_ODEPT"] = item.ODEPT_CODE;
	_viewer._transferData["S_ODEPT__NAME"] = item.ODEPT_CODE;
}

/*
 * 删除前方法执行
 */
_viewer.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};