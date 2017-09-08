var _viewer = this;

var servId = _viewer.servId 
var pkCode = _viewer.getPKCode();

//状态 0:新增未保存1:无效(待审核) 2:无效(审核未通过) 3:无效(审核中) 4:无效(扣分超过上限)5:有效
_viewer.getBtn("yesBtn").unbind("click").bind("click", function(event) {
	var param = {"_PK_":pkCode,"KC_STATE":5};
	FireFly.doAct(_viewer.servId, "save", param, true,false,function(data){
		_viewer.refresh();
		_viewer.getParHandler().refresh();
	});
});

_viewer.getBtn("noBtn").unbind("click").bind("click", function(event) {
	var param = {"_PK_":pkCode,"KC_STATE":2};
	FireFly.doAct(_viewer.servId, "save", param, true,false,function(data){
		_viewer.refresh();
		_viewer.getParHandler().refresh();
	});
});