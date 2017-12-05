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
		}
	});
}
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