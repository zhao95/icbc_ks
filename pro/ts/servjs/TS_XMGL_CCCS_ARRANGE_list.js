var _viewer = this;
$(".rhGrid").find("tr").unbind("dblclick");
_viewer.getBtn("new").unbind("click").bind("click", function(event) {
	var temp = {
		"act" : UIConst.ACT_CARD_ADD,
		"sId" : "TS_XMGL_CCCS_ARRANGE_COND",
		"parHandler" : _viewer,
		"widHeiArray" : [ 980, 380 ],
		"xyArray" : [ 20, 100 ]
	};
	var cardView = new rh.vi.cardView(temp);
	cardView.show();
});

_viewer.getBtn("publish").unbind("click").bind("click", function(event) {
	var pkArray = _viewer.grid.getSelectPKCodes();
	if (pkArray.length == 0) {
		Tip.showError("请选择记录", true);
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

/*
 * 删除前方法执行
 */
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

$("#TS_XMGL_CCCS_ARRANGE").find("th[icode='BUTTONS']").html("操作");
_viewer.grid.getBtn("BUTTONS").unbind("click").bind("click",function() {
	var pk = jQuery(this).attr("rowpk");//获取主键信息
	var configStr = "TS_KCGL_GLJG_ODEPT,{'TYPE':'single'}";
	var options = {
			"config" :configStr,
			"parHandler":_viewer,
			"formHandler":_viewer.form,
			"replaceCallBack":function(idArray,nameArray) {
				//回调，idArray为选中记录的相应字段的数组集合
				var codes = idArray[0];
//				var names = nameArray;
				if(codes == "")return;
				var param = {};
				param["ARR_ODEPT_CODES"] = codes;
				param["_PK_"] = pk;
				FireFly.doAct("TS_XMGL_CCCS_ARRANGE","save",param,true,false,function(data){
					_viewer.refreshGrid();
				});
			}
	};
	
	var queryView = new rh.vi.rhDictTreeView(options);
	queryView.show(event,[],[0,495]);
});