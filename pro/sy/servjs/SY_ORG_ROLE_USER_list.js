var _viewer = this;
var pServId = "";
try {pServId = _viewer.getParHandler().servId} catch (e) {}
if (!pServId) {
	pServId = "";
}
var linkUserservFlag = _viewer.links.LINK_USERSERV_FLAG; //关联服务配置的地方传递的常量值
//用户服务  下批量添加角色
if (pServId.indexOf("_ORG_USER") >= 0 || (linkUserservFlag && linkUserservFlag == 'true')) {
	_viewer.getBtn("selectAdd").unbind("click").bind("click",function(event) {
	    var extWhereStr = "and (S_PUBLIC=1 or CMPY_CODE=^@CMPY_CODE@^) and S_FLAG=1";
		var inputName = "roleCodes";
		var configStr = "SY_ORG_ROLE,{'TARGET':'ROLE_CODE~','SOURCE':'ROLE_CODE~ROLE_NAME','PKHIDE':true,'EXTWHERE':'"+extWhereStr+"','TYPE':'multi'}";
		var options = {"itemCode":inputName,
		"config" :configStr,
		"rebackCodes":inputName,
		"parHandler":this,
		"formHandler":this,
		"replaceCallBack":function(roleObjs){
				batchAddRoles(roleObjs.ROLE_CODE);
			}
		};
		var queryView = new rh.vi.rhSelectListView(options);
		queryView.show(event);	
	});
	_viewer.getBtn("copyRoleUser").hide();
} else {  //选择添加用户按钮的方法绑定,   角色服务  下批量添加用户
	var queryServID = "SY_ORG_USER";
	if (pServId.indexOf("_ALL") > 0) {
		queryServID = queryServID + "_ALL";
	} else if (pServId.indexOf("_SUB") > 0) {
		queryServID = queryServID + "_SUB";
	}
	_viewer.getBtn("selectAdd").unbind("click").bind("click",function(event) {
	    //var extWhereStr = null; // 'EXTWHERE':'"+extWhereStr+"',
		var inputName = "roleCodes";
		var configStr = queryServID + ",{'TARGET':'~USER_CODE~~~','SOURCE':'DEPT_NAME~USER_CODE~USER_NAME~USER_POST~DEPT_CODE~USER_LOGIN_NAME','HIDE':'DEPT_CODE','PKHIDE':true,'TYPE':'multi'}";
		var options = {"itemCode":inputName,
		"config" :configStr,
		"rebackCodes":inputName,
		"parHandler":this,
		"formHandler":this,
		"replaceCallBack":function(objs){
				if(objs == undefined || objs.length == 0) {
					alert("请选择用户！");
					return;
				}
				
				var roleCode = _viewer.getParHandler().getItem("ROLE_CODE").getValue();
				var userCodes = objs.USER_CODE.split(",");
				var deptCodes = objs.DEPT_CODE.split(",");
				var ssicIds = objs.USER_LOGIN_NAME.split(",");
				
				if(userCodes.length != deptCodes.length) {
					alert("错误的数据，部门ID与用户ID不一致！");
					return ;
				}
				
				var tempArray = [];
				var len = userCodes.length;
				for(var i=0;i<len;i++){
					var temp = {
						"ROLE_CODE" : roleCode,
						"USER_CODE" : userCodes[i],
						"DEPT_CODE" : deptCodes[i],
						"SSIC_ID" : ssicIds[i]
					};
					tempArray.push(temp);
				}

				var batchData = {};
				batchData["BATCHDATAS"] = tempArray;
				var resultData = FireFly.batchSave(_viewer.servId,batchData,null,_viewer.getNowDom());
				_viewer.refreshGrid();				
			}
		};
		var queryView = new rh.vi.rhSelectListView(options);
		queryView.show(event);	
	});	
	
	_viewer.getBtn("copyRoleUser").unbind("click").bind("click",function(event) {//复制角色用户
		var inputName = "roleCodes";
		
		var configStr = pServId + ",{'TARGET':'ROLE_CODE~','SOURCE':'ROLE_CODE~ROLE_NAME','TYPE':'single'}";
		var options = {"itemCode":inputName,
		"config" :configStr,
		"rebackCodes":inputName,
		"parHandler":this,
		"formHandler":this,
		"replaceCallBack":function(roleObjs){
				copyRoleUser(roleObjs.ROLE_CODE, userScope);
			}
		};
		var queryView = new rh.vi.rhSelectListView(options);
		queryView.show(event);	
	});
	_viewer.getBtn("add").hide();
}

/**
 * 批量添加角色
 */
function batchAddRoles(roleObjs) {
	var userCode = _viewer.getParHandler().getItem("USER_CODE").getValue();
	if (roleObjs.length > 0) {
	   var batchData = {};
	   var tempArray = [];
	   jQuery.each(roleObjs.split(","),function(i,n) {
		   var temp = {"USER_CODE":userCode,"ROLE_CODE":n};
		   tempArray.push(temp);
	   });
	   batchData["BATCHDATAS"] = tempArray;
	   
	   var resultData = FireFly.batchSave(_viewer.servId,batchData,null,_viewer.getNowDom());
	   _viewer.refreshGrid();
	}	
}

/**
 * 批量复制角色下用户
 */
function copyRoleUser(roleObjs, userScope) {
	if (roleObjs.length > 0) {
		var data = {};
		data["ROLE_CODE"] = _viewer.getParHandler().getItem("ROLE_CODE").getValue();
		data["USER_SCOPE"] = userScope;
		data["FROM_ROLE_CODE"] = roleObjs;
		var resultData = FireFly.doAct(_viewer.servId, "copyRoleUser", data);
		_viewer.refreshGrid();
	}	
}