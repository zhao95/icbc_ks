var _viewer = this;
//获取wfs_ID,并保存
if(_viewer.opts.act == "cardAdd"){
	var WFS_ID = _viewer.opts.WFS_ID;
	var NODE_ID = _viewer.opts.NODE_ID;
	if(typeof(WFS_ID)!="undefined"){
		_viewer.getItem("WFS_ID").setValue(WFS_ID);
	}
	if(typeof(NODE_ID)!="undefined"){
		_viewer.getItem("NODE_ID").setValue(NODE_ID);
	}
}


if ((_viewer._actVar == UIConst.ACT_CARD_ADD)) {//添加
	_viewer.backA.unbind("mousedown").bind("mousedown",function(){
		var shr = _viewer.getItem("BMSHLC_SHR").getValue();
		var deptCode = _viewer.getItem("DEPT_CODE").getValue();
		if(shr != "" || deptCode != ""){
			var confirmDel=confirm("新增数据，是否保存？");
			if (confirmDel == true){
				if (_viewer.btns[UIConst.ACT_SAVE]) {
					_viewer.btns[UIConst.ACT_SAVE].click();
				}
			}else{
				jQuery("#" + _viewer.dialogId).dialog("close");
			}
		}else{
			jQuery("#" + _viewer.dialogId).dialog("close");
		}
	});
}


//保存之前做校验						
_viewer.beforeSave = function() {
    if(_viewer._actVar !== UIConst.ACT_CARD_MODIFY){
        // if(_viewer.activeBtnClass)
        var userCode=_viewer.getItem("SHR_USERCODE").getValue();//人力资源编码
        var shrName=_viewer.getItem("BMSHLC_SHR").getValue();//审核人
        var shDepts=_viewer.getItem("DEPT_CODE").getValue();
        var nodeId=_viewer.getItem("NODE_ID").getValue();
        var nodeId=_viewer.getItem("BMSHLC_YESNO").getValue();					
    	var  WHERE="and SHR_USERCODE='"+userCode+"' and DEPT_CODE='"+shDepts+"' and NODE_ID='"+nodeId+"' and BMSHLC_YESNO='"+nodeId+"'";
        //var  WHERE="and SHR_USERCODE='"+userCode+"'and BMSHLC_SHR='"+shrName+"' and DEPT_CODE='"+shDepts+"' and NODE_ID='"+nodeId+"'";
        FireFly.doAct(_viewer.servId,"count",{"_WHERE_":WHERE},true,false,function(data){
            if(data._DATA_ > 0){
                _viewer.getItem("BMSHLC_SHR").clear();//审核人
                _viewer.getItem("DEPT_CODE").clear();
                _viewer.getItem("BMSHLC_YESNO").clear();
                alert("数据有重复");
            }
        });
	}
};





















//if ((_self._actVar == UIConst.ACT_CARD_MODIFY) && (_self.beforeSaveCheck == true)) {//修改
//	   if (jQuery.isEmptyObject(_self.getChangeData())) {
//	   } else {
////		   var confirmDel=confirm("数据有修改，是否保存？");
//		   var confirmDel=confirm(Language.transStatic("rhCardView_string1"));
//		   if (confirmDel == true){
//			   if (_self.btns[UIConst.ACT_SAVE]) {
//				   _self.btns[UIConst.ACT_SAVE].click();
//				   return false;
//			   }
//		   }
//	   }
//}