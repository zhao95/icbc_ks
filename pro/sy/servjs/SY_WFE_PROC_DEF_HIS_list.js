var _viewer = this;
_viewer.grid.unbindTrdblClick();
_viewer.grid.dblClick(function(value, node) {
	var self = _viewer;
	var title = _viewer.grid.getRowItemValue(value, "PROC_NAME");
	var version = _viewer.grid.getRowItemValue(value, "PROC_VERSION");
	var param = {"url": "SY_WFE_PROC_DEF.card.do?pkCode=" + value, "tTitle":title + "_V" + version};
	Tab.open(param);
}, _viewer);