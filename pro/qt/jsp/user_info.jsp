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
			<!-- 用户个人信息主要数据展示区域 -->
			<div id="table_div" class="" style="margin-top:20px;margin-left: 10px; width: 98%; margin:0 auto;text-align: center;">
					
				<div id="main-left" style="display:block;width:30%;float:left; background-color:red; height:20px;">
					<img alt="" src="" id="user_photo">
					<div><a>姓名：</a><a>123</a></div>
					<div><a>职务：</a><a>666</a></div>
					<div><a>部门：</a><a>456</a></div>
					<div><a>上次登录时间：</a><a>8888-88-88</a></div>
				</div>
				<div id="main-data" style="display:block;width:70%;float:left;height:auto;">
					<div>
						<h4 style="text-align:left;">个人基本信息</h4>			
						<div id="user_info_div"></div>		
					</div>
				</div>
		</div>
	</div>	
					
				</div>
	
	<!-- 引入相关js文件 -->
<%-- 	<script src="<%=CONTEXT_PATH%>/qt/js/index_qt.js"></script> --%>
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