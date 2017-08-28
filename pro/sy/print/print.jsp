<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ include file= "/sy/base/view/inHeader.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<%
    final String CONTEXT_PATH = request.getContextPath();
	String id = RequestUtils.getStr(request, "id");
	String title = RequestUtils.getStr(request, "title");
	if (StringUtils.isEmpty(title)) {
		title = "打印预览";
	}
	String extParams = RequestUtils.getStr(request, "extParams");
%>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><%=title %></title>
    <style type="text/css">
		.div_print{bottom:0px;position:fixed;background-color:#ccc;text-align: center;height: 40px;vertical-align: middle;width: 100%}
	</style>
	<script type="text/javascript">
		jQuery(document).ready(function(){
			var id = "<%=id %>";
			var extParams = "<%=extParams %>";
			if (!id) {
				return;
			}
			var html = FireFly.getPortalArea(id, extParams);
			if (html) {
				jQuery(".print").html(html);
			}
			
			jQuery(".btn_print").click(function(){
				jQuery(".print").printThis({debug:false});
			});
		});
	</script>
</head>
<body style="background:#ccc;">
	<div class="print"></div>
	<div style="height:50px"></div>
	<div class="div_print">
		<button class="btn_print">打印</button>
	</div>
</body>
</html>
