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

_viewer.getItem("KC_MAX").change(function(){
	var maxValue = _viewer.getItem("KC_MAX").getValue();
	var goodValue = _viewer.getItem("KC_GOOD").getValue();
	if(goodValue != ""){
		maxValue = parseInt(maxValue);
		goodValue = parseInt(goodValue);
		if(maxValue < goodValue){
			alert("最大设备数不能小于最优设备数！");
			_viewer.getItem("KC_MAX").clear();
		}
	}
});
_viewer.getItem("KC_GOOD").change(function(){
	var maxValue = _viewer.getItem("KC_MAX").getValue();
	var goodValue = _viewer.getItem("KC_GOOD").getValue();
	if(maxValue != ""){
		maxValue = parseInt(maxValue);
		goodValue = parseInt(goodValue);
		if(maxValue < goodValue){
			alert("最大设备数不能小于最优设备数！");
			_viewer.getItem("KC_GOOD").clear();
		}
	}
});

_viewer.beforeSave = function() {
	if(checkJKIP){
		Tip.showError("监控机IP格式不正确", true);
		return false;
	}
	
	if($("#TS_KCZGL_KCGL-KC_GLY_div .rhGrid-tbody").find("td").length == 1){
		$("#TS_KCZGL_KCGL-KC_GLY_div").find(".ui-dataservice-container,.fl,.wp").addClass("blankError").addClass("errorbox");
		return false;
	}
	
	if($("#TS_KCZGL_KCGL-KC_JKIP_div .rhGrid-tbody").find("td").length == 1){
		$("#TS_KCZGL_KCGL-KC_JKIP_div").find(".ui-dataservice-container,.fl,.wp").addClass("blankError").addClass("errorbox");
		return false;
	}
	
	//校验最大设备数
	if(_viewer.opts.act == "cardModify"){
		var newKcMax = _viewer.getChangeData().KC_MAX;
		var kcId = _viewer.getItem("KC_ID").getValue();
		if(newKcMax != undefined){
			var num = FireFly.doAct("TS_KCGL_ZWDYB","count",{"_WHERE_":"and kc_id = '"+kcId+"'"})._DATA_;
			if(newKcMax < num){
				var msg = "考场最大设备数小于现有已录入座位数！";
				Tip.showError(msg, true);
				return false;
			}
		}
	}
};

$("#TS_KCGL_GLY-viListViewBatch input[icode='GLY_NAME']").css("width","80px");
$("td[icode='GLY_NUMBER'] input").attr("disabled","disabled");
$("#TS_KCGL_GLY-viListViewBatch-addBatch").click(function(){
	setTimeout(function(){
		$("#TS_KCGL_GLY-viListViewBatch input[icode='GLY_NAME']").css("width","80px");
		$("td[icode='GLY_NUMBER'] input").attr("disabled","disabled");
	},50);
});

//监控机地址校验
$("#TS_KCGL_JKIP-viListViewBatch").find("input[icode='JKIP_IP']").change(function(){
	var ipVal = $(this).val();
	var reg = new RegExp("^([0-9]{1,3})\.([0-9]{1,3})\.([0-9]{1,3})\.([0-9]{1,3})$");
	if(!reg.test(ipVal)){
		alert("IP地址格式不正确");
		$(this).val(""); 
    }
});
$("#TS_KCGL_JKIP-viListViewBatch-addBatch").click(function(){
	setTimeout(function () {
		$("#TS_KCGL_JKIP-viListViewBatch").find("input[icode='JKIP_IP']").change(function(){
			$("#TS_KCGL_JKIP-viListViewBatch").find("input[icode='JKIP_IP']").addClass("tooltip");
			$("#TS_KCGL_JKIP-viListViewBatch").find("input[icode='JKIP_IP']").attr("title","格式规范:1-1,表示第一排第一座");
			var ipVal = $(this).val();
			var reg = new RegExp("^([0-9]{1,3})\.([0-9]{1,3})\.([0-9]{1,3})\.([0-9]{1,3})$");
			if(!reg.test(ipVal)){
				alert("IP地址格式不正确");
				$(this).val(""); 
		    }
		});
    }, 100)
});
$("#TS_KCGL_JKIP-viListViewBatch").find("input[icode='JKIP_IP']").addClass("tooltip");
$("#TS_KCGL_JKIP-viListViewBatch").find("input[icode='JKIP_IP']").attr("title","格式规范:1-1,表示第一排第一座");

function checkJKIP(){
	var flag = false;
	$("input[icode='JKIP_IP']").each(function(){
	    if($(this).val() == ""){
	    	flag = true;
	    }
	});
	return flag;
}
