<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
    final String CONTEXT_PATH = request.getContextPath();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@ include file="../../sy/base/view/inHeader.jsp"%>
</head>
<body>



	<div class="rhGrid-btnBar">
		<a class="rh-icon rhGrid-btnBar-a" id="zdfpcc" actcode="zdfpcc"
			title=""><span class="rh-icon-inner">自动分配场次</span><span
			class="rh-icon-img btn-option"></span></a><a
			class="rh-icon rhGrid-btnBar-a" id="updatecc" actcode="updatecc"
			title=""><span class="rh-icon-inner">更改场次</span><span
			class="rh-icon-img btn-edit"></span></a><a
			class="rh-icon rhGrid-btnBar-a" id="lookJk" actcode="lookJk" title=""><span
			class="rh-icon-inner">查看借考人员</span><span class="rh-icon-img btn-user"></span></a><a
			class="rh-icon rhGrid-btnBar-a" id="xngs" actcode="xngs" title=""><span
			class="rh-icon-inner">辖内公示</span><span class="rh-icon-img btn-report"></span></a>
	</div>
	<script type="text/javascript">
		$(document).ready(function() {
			function getQueryString(name) {
				var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
				var r = window.parent.location.search.substr(1).match(reg);
				if (r != null)
					return unescape(r[2]);
				return null;
			}
			
			var xmId = getQueryString("xmId");
			$("#zdfpcc").click(function() {
				alert(1);
			});
			$("#updatecc").click(function() {
				alert(2);
			});
			$("#lookJk").click(function() {
				alert(3);
			});
			$("#xngs").click(function() {
				alert(4);
			});
		});
	</script>
</body>
</html>
