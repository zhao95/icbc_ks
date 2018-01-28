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
	_viewer.getBtn("saveAndAdd").hide();
}

_viewer.getBtn("saveAndAdd").click(function(){
	if(saveCheck()){
		_viewer._saveForm();
		setTimeout(function(){ 
			jQuery("#" + _viewer.dialogId).dialog("close");
			$("#TS_KCGL_ZWDYB-add").click();
		}, 200);
	} 
});

_viewer.beforeSave= function() {
	if(!saveCheck()){
		return false;
	}
	//验证座位号IP在IP段范围内
	var kcId = _viewer.getItem("KC_ID").getValue();
	var zwIp = _viewer.getItem("ZW_IP").getValue();
	var scope = FireFly.doAct("TS_KCGL_IPSCOPE","finds",{"_SELECT_":"IPS_SCOPE","_WHERE_":"and kc_id = '"+kcId+"'"})._DATA_;
	if(scope.length > 0){
		var fhFlag = false;
		for(var i=0;i<scope.length;i++){
			var tmpScope = scope[i].IPS_SCOPE;
			if(checkScope(tmpScope,zwIp)){
				fhFlag = true;
				break;
			}
		}
		if(!fhFlag){
			var msg = "座位IP未在考场IP段范围内！";
			Tip.showError(msg, true);
			return false;
		}
	}else{
		var msg = "考场IP段未设置！";
		Tip.showError(msg, true);
		return false;
	}
};

var ZW_ZWH_XT_tmp = _viewer.getItem("ZW_ZWH_XT").getValue();
var ZW_IP_tmp = _viewer.getItem("ZW_IP").getValue();

function saveCheck(){
	var ZW_ZWH_XT = _viewer.getItem("ZW_ZWH_XT").getValue();
	var ZW_ZWH_SJ = _viewer.getItem("ZW_ZWH_SJ").getValue();
	var ZW_IP = _viewer.getItem("ZW_IP").getValue();
	
	if(ZW_ZWH_XT == "" || ZW_ZWH_SJ == "" || ZW_IP == "") {
		alert("必填项不能为空");
		return false;
	}
	var reg1 = new RegExp("^([0-9]{1,3})\-([0-9]{1,3})$");
	var reg2 = new RegExp("^([0-9]{1,3})\.([0-9]{1,3})\.([0-9]{1,3})\.([0-9]{1,3})$");
	if(!reg1.test(ZW_ZWH_XT)){
		alert("系统座位号格式不正确");
		return false;
    }
	if(!reg2.test(ZW_IP)){
		alert("IP地址格式不正确");
		return false;
    }
	var kcId = _viewer.getItem("KC_ID").getValue();
	var num1 = FireFly.doAct("TS_KCGL_ZWDYB","count",{"_WHERE_":"and kc_id = '"+kcId+"' and ZW_ZWH_XT = '"+ZW_ZWH_XT+"'"},true,false)._DATA_;
	var num2 = FireFly.doAct("TS_KCGL_ZWDYB","count",{"_WHERE_":"and kc_id = '"+kcId+"' and ZW_IP = '"+ZW_IP+"'"},true,false)._DATA_;
	if(_viewer.opts.act == "cardAdd"){
		if(num1 > 0){
			alert("系统座位号不允许重复");
			return false;
		}
		if(num2 > 0){
			alert("IP地址不允许重复");
			return false;
		}
		
	}else{
		if(ZW_ZWH_XT_tmp != ZW_ZWH_XT && num1 > 0 ){
			alert("系统座位号不允许重复");
			return false;
		}
		
		if(ZW_IP_tmp != ZW_IP && num2 > 0){
			alert("IP地址不允许重复");
			return false;
		}
	}
	return true;
}

/**
 * 校验ip是不是在scope范围内
 * @param scope
 * @param ip
 * @returns {Boolean}
 */
function checkScope(scope,ip){
	var sz = scope.split("-");
	var a = sz[0];
	var b = sz[1];
	var r1 = a.split(".")[0] != b.split(".")[0];
	var r2 = a.split(".")[1] != b.split(".")[1];
	var r3 = a.split(".")[2] != b.split(".")[2];
	var sa4 = a.split(".")[3];
	var sb4 = b.split(".")[3];
	
	var ip_1 = ip.split(".")[0];
	var ip_2 = ip.split(".")[1];
	var ip_3 = ip.split(".")[2];
	var ip_4 = ip.split(".")[3];
	
	if(parseInt(r1) != parseInt(ip_1) || parseInt(r2) != parseInt(ip_2) || parseInt(r3) != parseInt(ip_3)){
		return false;
	}
	
	if(ip_4 >= sa4 && ip_4 < sb4){
		return true;
	}
	return false;
}