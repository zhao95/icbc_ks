var _viewer = this;
if(_viewer.opts.act == "cardAdd"){
	var handler = _viewer.getParHandler();
	var extWhere = handler._extWhere;
	if(typeof(extWhere.split("'")[1])!="undefined" && _viewer.getItem("KC_ID").getValue() == ""){ 
		_viewer.getItem("KC_ID").setValue(extWhere.split("'")[1]);
	}
}

if (_viewer.opts.readOnly) {
_viewer.readCard();
}

_viewer.beforeSave= function() {
	if(checkScope()){
		var msg = "操作IP区段格式不正确！";
		Tip.showError(msg, true);
		return false;
	}
	
	if(_viewer.opts.act == "cardModify"){
		var pkcode = _viewer._pkCode;
		var kcId = _viewer.getItem("KC_ID").getValue();
		var newScope = _viewer.getChangeData().IPS_SCOPE;
		if(newScope != undefined){
			var scope = FireFly.doAct("TS_KCGL_IPSCOPE","finds",{"_SELECT_":"IPS_SCOPE","_WHERE_":"and kc_id = '"+kcId+"' and IPS_ID != '"+pkcode+"'"})._DATA_;
			scope.push({"IPS_SCOPE":newScope});
			
			var zw = FireFly.doAct("TS_KCGL_ZWDYB","finds",{"_SELECT_":"ZW_IP","_WHERE_":"and kc_id = '"+kcId+"'"})._DATA_;
			var flag = true;
			for(var j=0;j<zw.length;j++){
				var tmpIp = zw[j].ZW_IP;
				var tmpFlag = true;
				for(var i=0;i<scope.length;i++){
					var tmpScope = scope[i].IPS_SCOPE;
					if(checkScope(tmpScope,tmpIp)){
						tmpFlag = false;
						break;
					}
				}
				if(flag){
					flag = false;
					break;
				}
			}
			
			if(!flag){
				var msg = "考场IP段范围变更后存在超出存在IP范围的座位IP！";
				Tip.showError(msg, true);
				return false;
			}
		}
	}
};

/**
 * IP段校验  符合要求 return false
 * 不符合要求 return true
 * @returns {Boolean}
 */
function checkScope(){
	var scope = $("#TS_KCGL_IPSCOPE-IPS_SCOPE").val();
	var sz = scope.split("-");
	if(sz.length != 2){
		return true;
	}
	var a = sz[0];
	var b = sz[1];
	if(a.split(".").length != 4 || b.split(".").length != 4){
		return true;
	}
	var r1 = a.split(".")[0] != b.split(".")[0];
	var r2 = a.split(".")[1] != b.split(".")[1];
	var r3 = a.split(".")[2] != b.split(".")[2];
	if(r1 || r2 || r3){
		return true;
	}
	var sa4 = a.split(".")[3];
	var sb4 = b.split(".")[3];
	
	if(parseInt(sa4) > parseInt(sb4)){
		return true;
	}
	return false;
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