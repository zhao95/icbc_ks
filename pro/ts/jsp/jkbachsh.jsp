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
<title>借考审批</title>
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">
<!-- 获取后台数据 -->
<%@ include file="../../qt/jsp/header-logo.jsp"%> 
<%@ include file="/sy/base/view/inHeader-icbc.jsp"%>
<%@ page import="com.rh.core.serv.ServDao"%>
<%@ page import="com.rh.core.serv.ServMgr"%>
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
<%
String username=userBean.getStr("USER_NAME");
String loginname=userBean.getStr("USER_LOGIN_NAME");
%>

	<div class="" style="padding: 10px">
		<a href="<%=CONTEXT_PATH%>/index_qt.jsp"><img style="padding-bottom: 10px ;color: #388CAE;" src="<%=CONTEXT_PATH%>/ts/image/Home_16x16.png" id="shouye"></a>
		 <span style="color: #909090;font-size: 16px;">&nbsp;&nbsp;/&nbsp;&nbsp;批量审核</span>
	</div>
	<div id="cuxian1"
		style="margin-left: 10px; margin-top: 20px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
		<span style="margin-left: 50px; padding-top: 10px">批量审批</span>
	</div>
	<div style="background-color:lightseagreen;margin-left:5%;color:white;margin-top:10px" id = 'bachsh' class="btn">批量审核</div>
	<div
		style="margin-left: 10px; background-color: white; height: 20px; width: 98%"></div>
	<div id="table1" class=""
		style="margin-left: 10px; position: relative; width: 98%">
		<table id="qjtable" border="solid 1px" width="100%">
		<thead>
<tr style="background:whitesmoke;height:40px"><td width="3%" align='center'><input type="checkbox" id="checkall"/></td><td width="5%" align="left">序号</td><td width="35%" align="center">名称</td><td width="15%" align="center">审核开始时间</td><td width="15%" align="center">审核截止日期</td><td width="15%" align="center">状态</td><td width="15%" align="center">操作</td></tr>
</thead>
<tbody>
</tbody>
</table>
			
		<div id="fenyediiv"
			style="position: absolute; right: 5%; bottom: -20;">
			<table class="row">
				<tr>
					<td><!-- 分页展示 -->
					<div class="rhGrid-page">
		            <span class="disabled ui-corner-4">上一页</span>
		            <span class="current ui-corner-4">1</span>
		            <span class="disabled ui-corner-4">下一页</span>
		            <span class="allNum">共15条</span>
        </div></td>
					<td style="width: 5%"></td>
					<td><select id="yema" onchange="fenyeselect()">
							<option value="10" selected="selected">10条/页</option>
							<option value="1">1条/页</option>
							<option value="20">20条/页</option>
							<option value="50">50条/页</option>
							<option value="100">100条/页</option>
					</select></td>
				</tr>
			</table>
		</div>
	</div>
	<div class="modal fade" id="tiJiao" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static"  aria-hidden="true" style="padding-top:5%">
		<div class="modal-dialog" style="width:50%">
			<div class="modal-content">
				<div class="modal-header" style="line-height:20px;font-size:16px;height:50px;background-color: #00c2c2;color: white">
						批量审核
				</div>
				<form id="formmotai" method="post" action="bmshzg.jsp">
				<div>
				<table style="height:125px;font-size:14px;">
				<tr style="height:25%">
				<td style="text-align:right;width:20%">审核人姓名</td><td style="width:5%"></td><td><input style="height:30px" type="text" value="<%=username %>" name="shren"/></td>
				<td style="width:3%"></td>
				<td style="text-align:right">审核人登录名</td><td style="width:5%"></td><td><input style="height:30px" type="text" value="<%=loginname %>" name="shdlming"/></td>
				</tr>
				<tr style="height:25%">
				<td style="text-align:right">审核状态</td><td style="width:5%"></td><td><span id="radiospan1"><input style="vertical-align:text-bottom; margin-bottom:-3;" name="state" type="radio" value="1" checked>审核通过&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
													<span id="radiospan2"><input name="state" style="vertical-align:text-bottom; margin-bottom:-4;" type="radio" value="2">审核不通过</span>
				</td>
				</tr>
				</table>
				<table  style="height:100px;width:700px">
				<tr>
				<td style="text-align:right;width:17.5%;vertical-align:top">审核理由</td><td style="width:4%"></td><td style="width:75%;vertical-align:top"><textarea id="liyou" style="border:solid 1px lightseagreen;height:90%;width:88%" wrap="soft"></textarea></td>
				</tr>
				</table>
				</div>
				<input type="hidden" id="mokuai"></input>
				</form>
				<div class="modal-footer" style="text-align:center;height:60px">
					<button type="button" class="btn btn-primary" style="height:35px;background:lightseagreen;width:80px" onclick="mttijiao()">审核</button>
					<button type="button" class="btn btn-default" style="background:lightseagreen;margin-left:100px;color:white;height:35px;width:80px" data-dismiss="modal">关闭
					</button>
				</div>
			</div><!-- /.modal-content -->
		</div><!-- /.modal -->
	</div>
	<form id="tiaozhuanKCform" target="_blank" style="display:none" method="post" action="/ts/jsp/jklb_jk2.jsp">
<input id="todoId" name ="todoId" />
<input id="jkid" name ="jkid" />
<input id="hidden" name ="hidden" />
</form>
	<script>

	
	</script>

	<script src="<%=CONTEXT_PATH%>/qt/plugins/jQuery/jquery-2.2.3.min.js"></script>
	<!-- Bootstrap 3.3.6 -->
	<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/bootstrap.min.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/js/index_qt.js"></script>
	<script src="<%=CONTEXT_PATH%>/ts/js/jkbach.js"></script>
	<!-- FastClick -->
	<script src="<%=CONTEXT_PATH%>/qt/plugins/fastclick/fastclick.js"></script>
	<!-- AdminLTE  -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/app.min.js"></script>
	<!-- AdminLTE for demo purposes -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/demo.js"></script>
</body>
</html>