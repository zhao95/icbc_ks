var _viewer = this;
$(".rhGrid").find("tr").unbind("dblclick");

// _viewer._advancedSearchBtn.unbind("click").bind("click", function(event) {
// alert(1);
// });

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
	var value1_1 = Cookie.get("checkName");
	var value1_2 = Cookie.get("checkFun");
	updateRolesFun(pkCodes,value1_1,value1_2,"add");
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
	var value1_1 = Cookie.get("checkName");
	var value1_2 = Cookie.get("checkFun");
	updateRolesFun(pkCodes,value1_1,value1_2,"del");
});

function updateRolesFun(pkCodes,value1,value2,action){
	alert(pkCodes+value1+value2+action);
}

$(".rh-advSearch-table").find("label[value='ROLE_ID']").text("角色功能");
$(".rh-advSearch-table").find("label[value='ROLE_PID']").text("已有功能");
$(".rh-advSearch-table").find("label[value='ROLE_DCODE']").text("机构");

$("#TS_PVLG_ROLE_UPDATE .rh-advSearch-btn").unbind("click").bind("click",function(event) {
	var value1_1  = "";
	var value1_2  = "";
	var value2  = checked;
	var value3  = "";
	if($("#rh-advanceSearch-TS_PVLG_ROLE_UPDATEROLE_DCODE__NAME").val() != ""){
		value3  = deptCodes;
	}
	var value4  = $("#rh-advanceSearch-TS_PVLG_ROLE_UPDATEROLE_NAME").val();
	
	var roleName = $("#rh-advanceSearch-TS_PVLG_ROLE_UPDATEROLE_NAME").val();
	var roleFunName = $("#rh-advanceSearch-TS_PVLG_ROLE_UPDATEROLE_ID__NAME").val();
	if(roleFunName != ""){
		value1_1 = Cookie.get("checkName");
		value1_2 = Cookie.get("checkFun");
	}
	
	if($("input[type='checkbox'][name='ROLE_PID']:checkbox:checked").length > 0){
		value2 = $("input[type='checkbox'][name='ROLE_PID']:checkbox:checked")[0].defaultValue;
	}
	
	alert(value2 + "," + value3 + "," + value4);
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
	var configStr = "TS_ORG_ODEPT_ALL,{'TARGET':'','SOURCE':'DEPT_NAME~DEPT_CODE'," +
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
});