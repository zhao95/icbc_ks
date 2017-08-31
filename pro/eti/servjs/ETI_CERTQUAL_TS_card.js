var _viewer = this;
//页面加载时执行
$(function(){
	var CERT_MODULE = _viewer.getItem("CERT_MODULE").getValue();// 获得证书等级模块名称
	 _viewer.getItem("CERT_MODULE_CODE").setValue(CERT_MODULE);// 添加证书模块编号
	 var CERT_GRADE = _viewer.getItem("CERT_GRADE").getValue();// 获得证书等级模块名称
		_viewer.getItem("CERT_GRADE_CODE").setValue(CERT_GRADE);// 添加证书等级编号
});
// 自动计算合计费用
function total(){
			var EXAM_FEE = $("#ETI_CERTQUAL_TS-EXAM_FEE").val();
			var TRANS_FEE =$("#ETI_CERTQUAL_TS-TRANS_FEE").val();
			var HOTEL_FEE =$("#ETI_CERTQUAL_TS-HOTEL_FEE").val();
			var num = parseFloat(EXAM_FEE) + parseFloat(TRANS_FEE)
					+ parseFloat(HOTEL_FEE);
			_viewer.getItem("TOTLE_FEE").setValue(num);
}
$("#ETI_CERTQUAL_TS-EXAM_FEE").change(function(){
	total();
});
$("#ETI_CERTQUAL_TS-TRANS_FEE").change(function(){
	total();
});
$("#ETI_CERTQUAL_TS-HOTEL_FEE").change(function(){
	total();
});
// 修改证书模块名称的时候自动修改编码
_viewer.getItem("CERT_MODULE").change(
		function() {
			var CERT_MODULE = _viewer.getItem("CERT_MODULE").getValue();// 获得证书等级模块名称
			 _viewer.getItem("CERT_MODULE_CODE").setValue(CERT_MODULE);// 添加证书模块编号
		});
// 修改证书等级名称的时候自动修改编码
_viewer.getItem("CERT_GRADE").change(function() {
	var CERT_GRADE = _viewer.getItem("CERT_GRADE").getValue();// 获得证书等级模块名称
	_viewer.getItem("CERT_GRADE_CODE").setValue(CERT_GRADE);// 添加证书等级编号
});
// 获取证书验证码
$("#ETI_CERTQUAL_TS-CERT_CHECK_CD").click(function() {
	var codes = "abcdefghijklmnopqrstuvwxyz";
	var checkCode = "";
	for (var j = 0; j < 4; j++) {
		var i = Math.random() * 25;
		checkCode = checkCode + codes.charAt(i);
	}
	_viewer.getItem("CERT_CHECK_CD").setValue(checkCode);
});
// 时间的比较:主要是针对起始时间与失效时间进行校验比较,以及发证日期
// 起始时间
_viewer.getItem("BGN_DATE").obj.unbind("click").bind("click", function() {
	WdatePicker({
		maxDate : "#F{$dp.$D('" + _viewer.servId + "-END_DATE')}"
	});
});
//失效日期
_viewer.getItem("END_DATE").obj.unbind("click").bind("click", function() {
	WdatePicker({
		minDate : "#F{$dp.$D('" + _viewer.servId + "-BGN_DATE')}"
	});
});
//发证日期
_viewer.getItem("ISSUE_DATE").obj.unbind("click").bind("click",function(){
WdatePicker({
      maxDate : "#F{$dp.$D('" + _viewer.servId + "-BGN_DATE')}"//可选择的最大日期
    });
});
