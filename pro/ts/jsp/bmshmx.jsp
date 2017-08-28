<!DOCTYPE html>
<%@page import="com.rh.core.serv.OutBean"%>
<%@page import="javax.swing.text.StyledEditorKit.ForegroundAction"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	final String CONTEXT_PATH = request.getContextPath();
%>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>报名审核</title>
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">
<!-- 获取后台数据 -->
<%@ include file="/sy/base/view/inHeader.jsp"%>
<%@ page import="com.rh.core.serv.ServDao"%>
<%@ page import="com.rh.core.serv.ServMgr"%>
<%@ page import="com.rh.core.serv.ParamBean"%>
<%@ page import="com.rh.ts.pvlg.mgr.GroupMgr"%>
<%@ page import="com.rh.core.base.Bean"%>
<%@ page import="java.text.SimpleDateFormat"%>
<!-- Bootstrap 3.3.6 -->
<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/bootstrap/css/bootstrap.min.css">
<!-- Font Awesome -->
<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/font-awesome-4.7.0/css/font-awesome.min.css">
<!-- Ionicons -->
<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/ionicons/css/ionicons.min.css">

<script src="<%=CONTEXT_PATH%>/qt/js/html5shiv.min.js"></script>
<script src="<%=CONTEXT_PATH%>/qt/js/respond.min.js"></script>
<!-- Theme style -->
<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/dist/css/AdminLTE.min.css">
<!-- AdminLTE Skins. Choose a skin from the css/skins
       folder instead of downloading all of them to reduce the load. -->
<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/dist/css/skins/_all-skins.min.css">

<body class="hold-transition skin-black sidebar-mini">
<%String bmid = request.getParameter("bmid"); %>
	<div class="" style="padding: 10px">
		<a href="index_qt.jsp"><image style="padding-bottom:10px"
				src="/ts/image/u1155.png" id="shouye"></image></a> <span
			style="color: blue; font-size: 20px">&nbsp;&nbsp;/&nbsp;&nbsp;我的报名</span>
	</div>
	<table id="table" style="margin-left: 10px; margin-top: 20px; background-color: white; width: 98%">
	
	<div  style="padding-left: 50px;padding-top:10px">
		<a href="index_qt.jsp"><image style="padding-bottom:10px"
				src="/ts/image/u1011.png" id="shouye"></image></a> <span
			style="color: black; font-size: 25px">&nbsp;&nbsp;&nbsp;&nbsp;审核明细</span>
	</div>
	<thead id="tem" class="">
						
		<tr style="backGround-color:WhiteSmoke; height: 30px">
			<th style="width: 5%; text-align: center">序号</th>
			<th style="width: 13%;">审核时间</th>
			<th style="width: 13%;">审核人姓名</th>
			<th style="width: 13%; text-align: center">审核人登录名</th>
			<th style="width: 13%; text-align: center">审核级别</th>
			<th style="width: 13%; text-align: center">审核状态</th>
			<th style="width: 13%; text-align: center">审核理由</th>
			<th style="width: 13%; text-align: center">审核地址</th>
			
		</tr>
	</thead>
	<tbody>
	<%-- <%
	ParamBean param = new ParamBean();
	param.set("bmid", bmid);
	OutBean bean = ServMgr.act("", "getxxlist", param);
	List<Bean> list = bean.getDataList();
	for(int i=0;i<list.size();i++){
	int j = i+1;
	%>
		<tr>
		<td><%=j %></td><td><%= %></td><td><%= %></td><td><%= %></td><td><%= %></td><td><%= %></td><td><%= %></td><td><%= %></td>
		</tr>
		<% }%> --%>
	</tbody>
	</table>
	<script type="text/javascript">
	$(function(){
	var table = document.getElementById("table");		
	rowscolor(table);
	});
	</script>
<script src="<%=CONTEXT_PATH%>/shenhe.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/js/index_qt.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/plugins/jQuery/jquery-2.2.3.min.js"></script>
	<!-- Bootstrap 3.3.6 -->
	<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/bootstrap.min.js"></script>
	<!-- FastClick -->
	<script src="<%=CONTEXT_PATH%>/baoming.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/plugins/fastclick/fastclick.js"></script>
	<!-- AdminLTE App -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/app.min.js"></script>
	<!-- AdminLTE for demo purposes -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/demo.js"></script>
</body>
</html>
