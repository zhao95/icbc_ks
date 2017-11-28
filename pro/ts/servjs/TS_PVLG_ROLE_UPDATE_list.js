var _viewer = this;
$(".rhGrid").find("tr").unbind("dblclick");

_viewer.getBtn("addFun").unbind("click").bind("click", function(event) {
	var pkCodes = _viewer.grid.getSelectPKCodes();//获取主键值
	if($("#rh-advanceSearch-TS_PVLG_ROLE_UPDATEROLE_ID__NAME").val() == ""){
		alert("未选择要修改的功能");
		return;
	}
	if(pkCodes == ""){
		alert("未选择要修改的角色");
		return;
	}
	var value1_1 = Cookie.get("selCode");
	var value1_2 = Cookie.get("selFun");
	var value1_3 = Cookie.get("selName");
	updateRolesFun(pkCodes,value1_1,value1_2,value1_3,"add");
});

_viewer.getBtn("delFun").unbind("click").bind("click", function(event) {
	var pkCodes = _viewer.grid.getSelectPKCodes();//获取主键值
	if($("#rh-advanceSearch-TS_PVLG_ROLE_UPDATEROLE_ID__NAME").val() == ""){
		alert("未选择要修改的功能");
		return;
	}
	if(pkCodes == ""){
		alert("未选择要修改的角色");
		return;
	}
	var value1_1 = Cookie.get("selCode");
	var value1_2 = Cookie.get("selFun");
	var value1_3 = Cookie.get("selName");
	updateRolesFun(pkCodes,value1_1,value1_2,value1_3,"del");
});

function updateRolesFun(pkCodes,value1,value2,value3,action){
	for(var i=0;i<pkCodes.length;i++){
		var roleId = pkCodes[i];
		var mdCode = value1.replace("TS_PVLG_ROLE_UPDATE_FUNS-","");
		var param = {"_WHERE_":"and ROLE_ID = '"+roleId+"' and MD_CODE = '"+mdCode+"'"};
		FireFly.doAct("TS_PVLG_ROLE_MOD","finds",param,true,false,function(data){
			if(data._DATA_ && data._DATA_.length > 0){
				var dataId = data._DATA_[0].MD_ID;
				var mdVal = data._DATA_[0].MD_VAL;
				//数据库存在相关的数据
				if(action == "add"){
					//添加功能  
					if(mdVal.indexOf(value2) == -1){
						var paramBean = {};
						
						mdVal = value2 + "," + mdVal;
						
						mdVal = cutStartWith(mdVal,",");
						
						mdVal = cutEndWith(mdVal,",");
						
						paramBean["MD_VAL"] = mdVal;
						paramBean["_PK_"] = dataId;
						FireFly.doAct("TS_PVLG_ROLE_MOD","save",paramBean,true,false,function(data){
							if(data._MSG_.indexOf("OK") != -1){
								FireFly.doAct("TS_PVLG_ROLE_UPDATE","removeRoleCache",{"roleId":roleId,"module":"TS_PVLG_ROLE_MOD"});
							}
						});
					}
				}else{
					//删除功能	
					if(mdVal.indexOf(value2) != -1){
						
						mdVal = mdVal.replace(value2,"");
						mdVal = mdVal.replace(",,",",");
						
						mdVal = cutStartWith(mdVal,",");
						
						mdVal = cutEndWith(mdVal,",");
						
						var paramBean = {};
						paramBean["MD_VAL"] = mdVal;
						paramBean["_PK_"] = dataId;
						FireFly.doAct("TS_PVLG_ROLE_MOD","save",paramBean,true,false,function(data){
							if(data._MSG_.indexOf("OK") != -1){
								FireFly.doAct("TS_PVLG_ROLE_UPDATE","removeRoleCache",{"roleId":roleId,"module":"TS_PVLG_ROLE_MOD"});
							}
						});
					}
				}
			}else{
				if (action == "add") {
					// 添加功能
					var paramBean = {};
					paramBean["ROLE_ID"] = roleId;
					paramBean["MD_CODE"] = mdCode;
					paramBean["MD_NAME"] = value3;
					paramBean["MD_VAL"] = value2;
					// 数据库添加新数据
					FireFly.doAct("TS_PVLG_ROLE_MOD", "save", paramBean,true, false,function(data){
						if(data._MSG_.indexOf("OK") != -1){
							FireFly.doAct("TS_PVLG_ROLE_UPDATE","removeRoleCache",{"roleId":roleId,"module":"TS_PVLG_ROLE_MOD"});
						}
					});
				} else {
					//删除功能 因数据库中无相关数据，所以不需要做任何处理
				}
			}
		});
	}
}

$(".rh-advSearch-table").find("label[value='ROLE_ID']").text("角色功能").prepend("<span style='color:#F00'>*</span>");
$(".rh-advSearch-table").find("label[value='ROLE_PID']").text("已有功能").prepend("<span style='color:#F00'>*</span>");;
$(".rh-advSearch-table").find("label[value='ROLE_DCODE']").text("机构");

$("#TS_PVLG_ROLE_UPDATE .rh-advSearch-btn").unbind("click").bind("click",function(event) {
	var value1_1  = "";
	var value1_2  = "";
	var value2  = "";
	if($("input[type='checkbox'][name='ROLE_PID']:checkbox:checked").length > 0){
		value2  = checked;
	}
	var value3  = "";
	if($("#rh-advanceSearch-TS_PVLG_ROLE_UPDATEROLE_DCODE__NAME").val() != ""){
		value3  = deptCodes;
	}
	var value4  = $("#rh-advanceSearch-TS_PVLG_ROLE_UPDATEROLE_NAME").val();
	
	var roleName = $("#rh-advanceSearch-TS_PVLG_ROLE_UPDATEROLE_NAME").val();
	var roleFunName = $("#rh-advanceSearch-TS_PVLG_ROLE_UPDATEROLE_ID__NAME").val();
	if(roleFunName != ""){
		value1_1 = Cookie.get("selCode");
		value1_1 = value1_1.replace("TS_PVLG_ROLE_UPDATE_FUNS-","")
		value1_2 = Cookie.get("selFun");
	}
	
	if($("input[type='checkbox'][name='ROLE_PID']:checkbox:checked").length > 0){
		value2 = $("input[type='checkbox'][name='ROLE_PID']:checkbox:checked")[0].defaultValue;
	}
	
	if(value1_1==""||value2 == ""){
		alert("请填写必要的查询条件");
		return;
	}
	var param = {};
	param["value1_1"] = value1_1;
	param["value1_2"] = value1_2;
	param["value2"] = value2;
	param["value3"] = value3;
	param["value4"] = value4;
	FireFly.doAct(_viewer.servId,"myFind",param,true,false,function(data){
		var where = "";
		if(data.roleIds != ""){
			var where = " and ROLE_ID in ('"+data.roleIds.replace(/,/g,"','")+"')";
		}
		_viewer.setSearchWhereAndRefresh(where);
	});
});

var height = jQuery(window).height()-50;
var width = jQuery(window).width()-100;
//角色功能 【选择】链接
$("#TS_PVLG_ROLE_UPDATE .rh-advSearch-table").find("a.rh-advSearch-sel").eq(0).unbind("click").bind("click",function(event){
    var temp = {"act":UIConst.ACT_CARD_ADD,"sId":"TS_PVLG_ROLE_UPDATE_FUNS","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[50,50]};
    var cardView = new rh.vi.cardView(temp);
    cardView.show();
});

var deptCodes = "";
//机构 【选择】链接
$("#TS_PVLG_ROLE_UPDATE .rh-advSearch-table").find("a.rh-advSearch-sel").eq(1).unbind("click").bind("click",function(event){
	//1.构造查询选择参数，其中参数【HTMLITEM】非必填，用以标识返回字段的值为html标签类的
	var configStr = "TS_ORG_DEPT,{'TARGET':'','SOURCE':'DEPT_NAME~DEPT_CODE~DEPT_TYPE'," +
			"'HIDE':'','TYPE':'multi','HTMLITEM':''}";
	var options = {
		"config" :configStr,
		"parHandler":_viewer,
		"formHandler":_viewer.form,
	    "replaceCallBack":function(idArray) {//回调，idArray为选中记录的相应字段的数组集合
	    	var names = idArray.DEPT_NAME;
	    	var codes = idArray.DEPT_CODE;
	    	deptCodes = codes;
	    	$("#rh-advanceSearch-TS_PVLG_ROLE_UPDATEROLE_DCODE__NAME").val(names);
		}
	};
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);
});

//高级查询项 多选框最多只能选一个
var checked = "";
$("#TS_PVLG_ROLE_UPDATE .rh-advSearch-table").find("input[type='checkbox']").change(function(event){
	if(checked == ""){
		checked = $(this).attr("id");
	}else if(checked == $(this).attr("id")){
		checked = "";
	}else{
		$("input[id='"+checked+"']").attr("checked", false);
		checked = $(this).attr("id");
	}
	
	if(checked == ""){
		_viewer.getBtn("addFun").show();
		_viewer.getBtn("delFun").show();
	}else if(checked == "ROLE_PID-1"){
		_viewer.getBtn("addFun").hide();
		_viewer.getBtn("delFun").show();
	}else if(checked == "ROLE_PID-2"){
		_viewer.getBtn("addFun").show();
		_viewer.getBtn("delFun").hide();
	}
});


$("#TS_PVLG_ROLE_UPDATE .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		if(dataId == "") return;
		var roleType = $(item).find("td[icode='ROLE_TYPE']").text();
		if(roleType ==1) {
			var orgLv = $(item).find("td[icode='ROLE_ORG_LV__NAME']").text();
			$(item).find("td[icode='ROLE_DNAME']").text(orgLv);
		}
	}
});

function cutStartWith(val,str) {
	
	var reg=new RegExp("^"+str);
	
	if(reg.test(val)) {
		val = val.substring(1,val.length);
	}
	
	return val;  
}

function cutEndWith(val,str) {
	
	var reg=new RegExp(str+"$");  
	
	if(reg.test(val)) {
		val = val.substring(0,val.length-1);
	}
	
	return val;
}
