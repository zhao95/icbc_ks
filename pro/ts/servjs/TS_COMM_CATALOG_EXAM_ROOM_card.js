var _viewer = this;
var ctlgShare = _viewer.getItem("CTLG_SHARE");

//模块CTLG_MODULE赋值
if(_viewer.getItem("CTLG_MODULE").getValue() == "") {
	
	if(_viewer.getParHandler()){
		_viewer.getItem("CTLG_MODULE").setValue(_viewer.getParHandler().getParams().CTLG_MODULE);
	} else {
		_viewer.getItem("CTLG_MODULE").setValue(_viewer.getParams().CTLG_MODULE);
	}
}


var pcode = _viewer.getItem("CTLG_PCODE").getValue();
var pcodeH = _viewer.getItem("CTLG_PCODE_H").getValue();
var module = _viewer.getItem("CTLG_MODULE").getValue();

if(pcode == '' && pcodeH.length > 0 && module.length > 0) {
	pcode = pcodeH.replace(module+"-","");
	_viewer.getItem("CTLG_PCODE").setValue(pcode);
}

if(_viewer.getItem("CTLG_MODULE").getValue() == ""){
	alert("目录所属模块为空，请重新打开！");
} else if(_viewer.getItem("CTLG_PCODE").getValue() == ""){
	alert("上级目录为空，请重新打开！");
}

//编辑时 目录编码只读
if (_viewer.getItem("CTLG_CODE").getValue().length > 0) {
	_viewer.getItem("CTLG_CODE").disabled();
}

//选中当前模块
ctlgShare._obj.find(":checkbox").each(function() {
	var opt = jQuery(this);
	if (_viewer.getItem("CTLG_MODULE").getValue() == opt.val()) {
		opt.attr("checked","true");
	}
});
//已选中模块 只读
ctlgShare.getCheckedCheckbox().each(function() {
	$(this).attr("disabled","disabled");
});

//保存后刷新
_viewer.afterSave = function(){
//	var where = " AND CTLG_MODULE = '"+ module +"'";
//	_viewer.getParHandler().refreshTreeGrid(where,where);
	_viewer.refresh();
};
