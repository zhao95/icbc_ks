var _viewer = this;
var mdSId = "TS_PVLG_ROLE_MOD";
var actCode = _viewer.opts.act;
var userCode = System.getVar("@USER_CODE@");
var userName = System.getVar("@USER_NAME@");

// 权限模块字典
var mdDict = FireFly.getDict("TS_PVLG_MOD");
var moduleArray = mdDict[0].CHILD;

// 当前角色已赋权限 (所有功能)
var roleChecked = {};
var result = FireFly.doAct(mdSId, "finds", {}, 2, false);
if (result._DATA_) {
	$(result._DATA_).each(function(index, item) {
		roleChecked[item.MD_CODE] = item;
	});
}

var roleCheckedP = {};
var resultP = FireFly.doAct(mdSId, "getPvlgByUser", {
	"USER_CODE" : userCode
}, 2, false);
if (resultP._DATA_) {
	roleCheckedP = resultP._DATA_;
}

/**
 * 渲染角色所有模块 (主方法)
 */
render = function() {
	var formContent = $(_viewer.formCon).find("#MD_OPTS").find(".formContent");

	if (moduleArray) {

		$(moduleArray)
				.each(
						function(index, obj) {
							var row = $("<div id='TS_PVLG_ROLE_UPDATE_FUNS-"
									+ obj.ITEM_CODE
									+ "_div' class='inner' style='width:100%;max-width:1400px;'>");
							// 左侧模块区域
							var name = $("<span class='name'>").text(
									obj.ITEM_NAME);
							var start = $("<span class='star'>");
							var leftContainer = $("<div class='container'>")
									.append(name).append(start);
							var leftDiv = $(
									"<div id='TS_PVLG_ROLE_UPDATE_FUNS-"
											+ obj.ITEM_CODE
											+ "_label' class='ui-label-default'>")
									.append(leftContainer);
							var left = $("<span class='left form__left30'>")
									.append(leftDiv);
							// 右侧功能区域
							var rightDiv = $('<div class="blank fl wp">').css({
								"float" : "left",
								"width" : "98%",
								"clear" : "none",
								"border-left" : "none"
							});
							// var rightDiv1 = $('<div class="blank fl
							// wp">').css({"float":"left","width":"8%","clear":"none","background":"#ECF5FF"});
							// var ckallSpan = $('<span
							// id="TS_PVLG_ROLE-CHECK_ALL_SPAN_'+obj.ITEM_CODE+'"
							// class="ui-checkbox-default">').appendTo(rightDiv1);
							// var ckallObj = $('<input type="checkbox"
							// id="TS_PVLG_ROLE-CHECK_ALL_'+obj.ITEM_CODE+'">').appendTo(ckallSpan);
							// ckallSpan.append($("<label
							// style='padding-left:5px'>全选</label>"));
							// 全选功能
							// ckallObj.change(function() {
							// checkedAll(obj.ITEM_CODE);
							// });

							// 显示功能选项(多选)
							optRender(obj.ITEM_CODE, rightDiv);
							// var right = $("<span class='right
							// form__right70'>").append(rightDiv1).append(rightDiv);
							var right = $("<span class='right form__right70'>")
									.append(rightDiv);
							row.append(left).append(right);
							$(formContent).append(row);
						});
	}
}

/**
 * 渲染模块下所有功能 code 模块编码 rightDiv 显示区域对象
 */
optRender = function(code, rightDiv) {

	var rightSpan = $('<span id="TS_PVLG_ROLE_UPDATE_FUNS-' + obj.ITEM_CODE
			+ '" class="ui-checkbox-default">');
	// 功能权限主键
	var mdId = $('<input type="hidden">').attr("name",
			"TS_PVLG_ROLE-" + code + "_MID").appendTo(rightSpan);

	var optDict = FireFly.getDict(code);
	// 所有功能权限
	var option = optDict[0].CHILD;

	if (option) {

		// 选中的功能权限
		var curChecked = roleChecked[code];
		// 权限数据主键，用作权限修改
		if (typeof (curChecked) != "undefined") {
			mdId.val(curChecked.MD_ID)
		} else {
			curChecked = {};
		}

		// 父级选中的功能权限
		var parChecked = roleCheckedP[code];

		if (actCode == "cardAdd") {
			curChecked = roleCheckedP[code];
		}

		// 选中的功能权限 val数组
		var curVal = {};
		if (curChecked && typeof (curChecked.MD_VAL) != "undefined") {

			var curArray = curChecked.MD_VAL.split(",");
			$(curArray).each(function(i, val) {
				curVal[val] = null;
			});
		}
		// console.log("curVal",curVal);
		// 选中的功能权限 val数组
		var parVal = {};
		if (typeof (parChecked) != "undefined") {
			var parArray = parChecked.split(",");
			$(parArray).each(function(i, val) {
				parVal[val] = null;
			});
		}
		// console.log("parVal",parVal);
		// 遍历字典中所有功能
		$(option).each(
				function(index, item) {

					var ckItem = $('<input type="checkbox">').attr("name",
							"TS_PVLG_ROLE_UPDATE_FUNS-" + code).val(
							item.ITEM_CODE);
					// 功能权限名称 checkbox名称
					var lab = $("<label>").text(item.ITEM_NAME);

					rightSpan.append(ckItem).append(lab);

					if (typeof (curVal[item.ITEM_CODE]) != "undefined") {
						ckItem.attr("checked", true);
					}

					if (typeof (parVal[item.ITEM_CODE]) == "undefined"
							&& userName != '系统管理员') {
						ckItem.attr("disabled", "disabled");
						lab.css("color", "#dddddd");
					}

				});
	}

	rightDiv.append(rightSpan);
};

render();
var selCode = "";
var selName = "";
var selFun = "";

_viewer.getBtn("saveSel").unbind("click").bind("click", function() {
	if(selCode ==""||selFun == ""){
		alert("未勾选功能");
	}else{
		var funName = $("#"+selCode+"_label").find("span.name").text();
		var methName =$("input[name='"+selCode+"'][value='"+selFun+"']").next().text();
		Cookie.set("selCode",selCode, 1);
		Cookie.set("selName",funName, 1);
		Cookie.set("selFun",selFun, 1);
		$("#rh-advanceSearch-TS_PVLG_ROLE_UPDATEROLE_ID__NAME").val(funName+"["+methName+"]");
		_viewer.backA.mousedown();
	}
});

$("#TS_PVLG_ROLE_UPDATE_FUNS-mainTab").find("input[type='checkbox']").change(function(even) {
	var tmpName = $(this).attr("name");
	var tmpFun = $(this).val();
	if(tmpName != selCode || tmpFun != selFun){
		if(selCode != ""&&selFun != "")clearChecked(selCode,selFun);
		selFun = tmpFun;
		selCode = tmpName;
	}else{
		selCode = "";
		selFun = "";
	}
});

/**
 * 清空勾选的多选框
 * 
 * @param name
 * @param func
 */
function clearChecked(name, func) {
	var obj = $("input[name='"+name+"'][value='"+func+"']");
	if (obj.is(':checked')) {
		obj.attr("checked", false);
	}
}

/*
 * Cookie.get(cookName);//读cookie操作,参数：cookie名称 返回值：字符串 
 * Cookie.set(sName,sValue, oExpires, sPath, sDomain, bSecure);
 * //写cookie操作sName：cookie名称.sValue：cookie值,oExpires：过期时间
 * Cookie.del(sName);//删除cookie操作sName ：cookie名称
 */