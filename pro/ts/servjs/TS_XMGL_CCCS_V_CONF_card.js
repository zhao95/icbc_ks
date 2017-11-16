var _viewer = this;
initValue();
check();
_viewer.getItem("KS_SC").change(function() {
	_viewer.getItem("KS_SJ").clear();
	check();
})

// 层级只能是一级和一级，二级
_viewer.getItem("KC_CJ").change(function() {
	var cjVal = _viewer.getItem("KC_CJ").getValue();
	if (cjVal.indexOf("2") != -1) {
		_viewer.getItem("KC_CJ").setValue("1,2");
	}
})
/*
 * Cookie.get(cookName);//读cookie操作,参数：cookie名称 返回值：字符串 Cookie.set(sName,
 * sValue, oExpires); //写cookie操作 sName：cookie名称.sValue：cookie值,oExpires：过期时间
 * Cookie.del(sName);//删除cookie操作sName ：cookie名称
 */
_viewer.getBtn("save").unbind("click").bind("click", function(event) {
	var scVal = _viewer.getItem("KS_SC").getValue();
	var sjVal = _viewer.getItem("KS_SJ").getValue();
	var cjVal = _viewer.getItem("KC_CJ").getValue();
	if (scVal == "" || sjVal == "" || cjVal == "") {
		alert("测算条件不完整");
	} else {
		Cookie.set("scVal", scVal, 1);
		Cookie.set("sjVal", sjVal, 1);
		Cookie.set("cjVal", cjVal, 1);

		//		_viewer.cardBarTipLoad("提交中...");
		//		Tip.showLoad("提交中...");

		setTimeout(function() {
			_viewer._parHandler.refreshGrid();
		}, 100);
		
		_viewer.setParentNoRefresh();
		_viewer.backA.mousedown();

		//		$("#TS_XMGL_CCCS_V_CONF-winDialog").prev().find("a.ui-dialog-titlebar-close").mousedown();		
	}
});

function check() {
	var scVal = _viewer.getItem("KS_SC").getValue();
	if (scVal == 1) {
		$("#TS_XMGL_CCCS_V_CONF-KS_SJ input[value=60]").attr("disabled", false);
		$("#TS_XMGL_CCCS_V_CONF-KS_SJ input[value=90]").attr("disabled", false);
		$("#TS_XMGL_CCCS_V_CONF-KS_SJ input[value=120]").attr("disabled", true);
		$("#TS_XMGL_CCCS_V_CONF-KS_SJ input[value=150]").attr("disabled", true);
	} else if (scVal == 2) {
		$("#TS_XMGL_CCCS_V_CONF-KS_SJ input[value=60]").attr("disabled", true);
		$("#TS_XMGL_CCCS_V_CONF-KS_SJ input[value=90]").attr("disabled", true);
		$("#TS_XMGL_CCCS_V_CONF-KS_SJ input[value=120]")
				.attr("disabled", false);
		$("#TS_XMGL_CCCS_V_CONF-KS_SJ input[value=150]")
				.attr("disabled", false);
	}
}
/**
 * 如果cookie有值，页面赋值
 */
function initValue() {
	var scVal = Cookie.get("scVal");
	var sjVal = Cookie.get("sjVal");
	var cjVal = Cookie.get("cjVal");
	if (scVal != "" && scVal != null) {
		_viewer.getItem("KS_SC").setValue(scVal);
		_viewer.getItem("KS_SJ").setValue(sjVal);
		_viewer.getItem("KC_CJ").setValue(cjVal);
	}
}