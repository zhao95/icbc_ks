<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!--mbDocView.jsp移动端查看文件页面-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>移动端查看文件页面</title>
    <%@ include file= "inHeader-mb.jsp" %>
    <meta name="viewport" content="width=900px"/>
    <style>
    	html,body {
    		width:100%;
    		height:100%;
    		background:none !important;
    	}
    	#frame_con {
    		border:0px solid #000;
    		margin:0px;
    		padding:0px;
    	}
	</style>
</head>
<%
	String dUrl = request.getParameter("dUrl");
%>
<body class="mbCard-body">
	<div class="mbTopBar" style="width:900px;">
		<table class="mbTopBar-table">
			<tbody>
				<tr>
					<td class="mbTopBar-left" onclick="javascript:window.history.go(-1);">
						<div class="mbTopBar-back">返回</div>
					</td>
					<td class="mbTopBar-center">
						<div class="mbTopBar-title" style="width:540px;">预览文件</div>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	<iframe id="frame_con" style="width:100%;height:100%;margin-top:44px;" src="<%=dUrl%>">
	</iframe>
</body>
<script>
	var wHei = jQuery("body").height();
	jQuery("#frame_con").height(wHei - 44);
</script>
</html>