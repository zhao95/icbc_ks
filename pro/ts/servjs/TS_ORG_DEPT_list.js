var _viewer = this;

//传给后台的数据
/*
* 业务可覆盖此方法，在导航树的点击事件加载前
*/
rh.vi.listView.prototype.beforeTreeNodeClickLoad = function(item,id,dictId) {
	var params = {};
	var user_pvlg=_viewer._userPvlg[_viewer.servId+"_PVLG"];
	var  filed="CODE_PATH";
	params["USER_PVLG"] = user_pvlg;
	_viewer.whereData["extParams"] = params;
	var flag = getListPvlg(item,user_pvlg,filed);
	_viewer.listClearTipLoad();
	return flag;
};