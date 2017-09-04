<%@page import="java.sql.Array"%>
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
<title>个人资料</title>
<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
<!-- 获取后台数据 -->
<%@ include file="../../sy/base/view/inHeader.jsp"%>
<%@ page import="com.rh.core.serv.ServDao"%>
<%@ page import="com.rh.core.serv.ServMgr"%>
<%@ page import="com.rh.ts.pvlg.mgr.GroupMgr"%>
<%@ page import="com.rh.core.base.Bean"%>
<%@ page import="java.text.SimpleDateFormat"%>

<!-- Bootstrap 3.3.6 -->
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/bootstrap/css/bootstrap.min.css">
<!-- Font Awesome -->
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/font-awesome-4.7.0/css/font-awesome.min.css">
<!-- Ionicons -->
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/ionicons/css/ionicons.min.css">

<script src="<%=CONTEXT_PATH%>/qt/js/html5shiv.min.js"></script>
<script src="<%=CONTEXT_PATH%>/qt/js/respond.min.js"></script>
<!-- Theme style -->
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/dist/css/AdminLTE.min.css">
<!-- AdminLTE Skins. Choose a skin from the css/skins
       folder instead of downloading all of them to reduce the load. -->
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/dist/css/skins/_all-skins.min.css">
<style type="text/css">
#kstable > tbody > tr:nth-of-type(even) {
            background-color: Azure;
        }
#kstable > tbody > tr { 
            font-family: 'Arial Normal', 'Arial'; 
 		    font-weight: 400; 
 		    font-style: normal; 
 		    height:50px;
 		    font-size: 14px; 
 		    color: #333333; 
		    text-align: center; 
 		    line-height: normal; 
         } 
</style>
</head>
<body class="hold-transition skin-black sidebar-mini" style="height: auto;">
<!-- 	<div class="wrapper" style="background-color: rgb(6, 77, 120); height: auto;"> -->
		<div style="position: absolute; left: 15px; z-index: 10000;">
			<img alt="中国工商银行" src="<%=CONTEXT_PATH%>/qt/img/u3148.png"> 
			<img alt="考试系统" src="<%=CONTEXT_PATH%>/qt/img/u3376.png">
		</div>
		<header class="main-header" style="background-color: white;">
			<!-- Logo -->
			<!-- <div class="logo">
				<span class="logo-lg"><b>考试系统</b></span>
			</div> -->
			<!-- Header Navbar: style can be found in header.less -->
			<nav class="navbar navbar-static-top">
			</nav>
		</header>
<!-- 	</div> -->



	<!-- 首页/返回 -->
	<div class="" style="padding: 10px">
		<a href="<%=CONTEXT_PATH%>/index_qt.jsp"><img style="padding-bottom:10px" src="/ts/image/u1155.png" id="shouye"></a> <span style="color: blue; font-size: 20px">&nbsp;&nbsp;/&nbsp;&nbsp;个人资料</span>
	</div>
	<!-- tab标签  主标题 考试详情 -->
	<div>
		<table id="myTab" class="nav nav-tabs"
		style="margin-left: 10px; width: 98%; background-color: white">
<!-- 		<tr style="height: 70px"> -->
<!-- 			<td id="ksrltd"  class="active" -->
<!-- 				style="border-bottom:white solid 1px;width: 33.3%; text-align: center; font-size: 25px"> -->
<!-- 				<a id="ksrla" href="#home" data-toggle="tab"><span id="ksrlsp" -->
<!-- 					style="color: lightseagreen">考试日历详情</span> -->
<!-- 				</a> -->
<!-- 			</td> -->
<!-- 		</tr> -->
	</table>
	</div>

	<!-- 显示主模块 -->
	<div id="myTabContent" class="tab-content">
		<div class="tab-pane fade active in" style="position:relative;" id="home">
			<div style="margin-top: -5px; margin-left: 100%; height: 5px; width: 20%; background-color: LightSeaGreen"></div>
				<div id="cuxian1" style="margin-left: 10px; margin-top: 0px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
				<span style="margin-left: 50px; padding-top: 10px">用户个人资料</span>
			</div>
			<!-- 分割线 -->
			<div id="xixian" style="margin-left: 10px; margin-top: 20px; background-color: lightgray; height:1px; width: 98%">
			</div>
			<!-- 考试信息主要数据展示区域 -->
			<div id="kstable_div" class="" style="margin-top:20px;margin-left: 10px; width: 98%; margin:0 auto;text-align: center;">
					<table class="rhGrid JPadding JColResizer" id="kstable" border="1">
						<thead id="tem" class="">
							
						</thead>
					<tbody id="tbody_data" ></tbody>
					</table>
		</div>
	</div>	
					
				</div>
	</tbody></table>
	
	<!-- 引入相关js文件 -->
	<script src="<%=CONTEXT_PATH%>/qt/js/index_qt.js"></script>
	<!-- Bootstrap 3.3.6 -->
	<!-- FastClick -->
	<script src="<%=CONTEXT_PATH%>/qt/plugins/fastclick/fastclick.js"></script>
	<!-- AdminLTE App --> 
<%-- 	<script src="<%=CONTEXT_PATH%>/qt/dist/js/app.min.js"></script> --%>
	<!-- AdminLTE for demo purposes -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/demo.js"></script>	
	<!-- 	当前登录用户信息展示JS引入 -->
	<script src="<%=CONTEXT_PATH%>/qt/js/user_info.js"></script>	
	
</body>

<script type="text/javascript">

</script>
</html>