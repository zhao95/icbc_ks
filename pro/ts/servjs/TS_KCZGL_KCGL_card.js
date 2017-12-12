var _viewer = this;
//设置卡片只读
if(_viewer.opts.readOnly){
	_viewer.readCard();
}
//打开自服务列表
if(typeof(_viewer.opts.showTab) !="undefined"){ 
	var sid = _viewer.opts.showTab;
	if(sid != ""){
		var topObj = jQuery("li.rhCard-tabs-topLi[sid='" + sid + "']",_viewer.tabs);
		topObj.find("a").click();
	}
}

if(_viewer.getItem("SERV_ID").getValue() == ""){
	_viewer.getItem("SERV_ID").setValue(_viewer.servId);
}

_viewer.getItem("KC_ODEPTCODE").change(function(){
	check("KC_ODEPTCODE");
});
_viewer.getItem("KC_LEVEL").change(function(){
	check("KC_LEVEL");
});

function check(colName){
	var kcOdeptCode = _viewer.getItem("KC_ODEPTCODE").getValue();
	var kcLevel = _viewer.getItem("KC_LEVEL").getValue();
	if(kcOdeptCode == "") return;
	
	FireFly.doAct("SY_ORG_DEPT","byid",{"_PK_":kcOdeptCode},false,false,function(data){
		var level = data.DEPT_LEVEL;
		if(kcLevel == "一级"){
			if(level != 2){
				if(colName == "KC_ODEPTCODE"){
					_viewer.getItem("KC_ODEPTCODE").clear();
				}else{
					_viewer.getItem("KC_LEVEL").clear();
				}
				_viewer.cardBarTipError("一级考场所属机构为一级机构");
			}
		}else if(kcLevel == "二级"){
			if(level != 3){
				if(colName == "KC_ODEPTCODE"){
					_viewer.getItem("KC_ODEPTCODE").clear();
				}else{
					_viewer.getItem("KC_LEVEL").clear();
				}
				_viewer.cardBarTipError("二级考场所属机构为二级机构");
			}
		}
	});
}
