var _viewer = this;

_viewer.getBtn("new").unbind("click").bind("click", function(event) {
	var temp = {
		"act" : UIConst.ACT_CARD_ADD,
		"sId" : "TS_XMGL_CCCS_ARRANGE_COND",
		"parHandler" : _viewer,
		"widHeiArray" : [ 1200, 380 ],
		"xyArray" : [ 100, 100 ]
	};
	var cardView = new rh.vi.cardView(temp);
	cardView.show();
});

_viewer.getBtn("publish").unbind("click").bind("click", function(event) {
	var pkArray = _viewer.grid.getSelectPKCodes();
	if (pkArray.length == 0) {
		alert("请选择记录");
		return;
	}
	for (var i = 0; i < pkArray.length; i++) {
		var dataId = pkArray[i];
		FireFly.doAct("TS_XMGL_CCCS_ARRANGE", "save", {
			"_PK_" : dataId,
			"ARR_STATE" : 1
		}, true);
	}
	alert("发布成功");
	_viewer.refresh();
});
