var _viewer = this;

if(_viewer.opts.act == "cardAdd"){
	var handler = _viewer.getParHandler();
	var extWhere = handler._extWhere;
	if(typeof(extWhere.split("'")[1])!="undefined"){ 
		_viewer.getItem("KC_ID").setValue(extWhere.split("'")[1]);
	}
}

if (_viewer.opts.readOnly) {
	_viewer.readCard();
	}

_viewer.getBtn("saveAndAdd").click(function(){
	if(saveCheck()){
		_viewer._saveForm();
		setTimeout(function(){ 
			jQuery("#" + _viewer.dialogId).dialog("close");
			$("#TS_KCGL_ZWDYB-add").click();
		}, 200);
	} else{
		_viewer.cardBarTipError("校验未通过");
	}
});

function saveCheck(){
	var ZW_ZWH_XT = _viewer.getItem("ZW_ZWH_XT").getValue();
	var ZW_ZWH_SJ = _viewer.getItem("ZW_ZWH_SJ").getValue();
	if(ZW_ZWH_XT == "" || ZW_ZWH_SJ == "") return false;
	var reg = new RegExp("^([0-9]{1,3})\-([0-9]{1,3})$");
	if(!reg.test(ZW_ZWH_XT)){
		return false;
    }
	var kcId = _viewer.getItem("KC_ID").getValue();
	var num = FireFly.doAct("TS_KCGL_ZWDYB","count",{"_WHERE_":"and kc_id = '"+kcId+"' and ZW_ZWH_XT = '"+ZW_ZWH_XT+"'"},true,false)._DATA_;
	if(num > 0) return false;
	return true;
}