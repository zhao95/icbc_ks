<%@page import="java.sql.Array"%>
<%@page import="com.rh.core.serv.OutBean"%>
<%@page import="javax.swing.text.StyledEditorKit.ForegroundAction"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	final String CONTEXT_PATH = request.getContextPath();
%>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>个人资料</title>
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">
<!-- 获取后台数据 -->
<%@ include file="../../sy/base/view/inHeader-icbc.jsp"%>

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
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/dist/css/AdminLTE.min.css">
	
<!-- AdminLTE App -->
<script src="<%=CONTEXT_PATH%>/qt/dist/js/app.min.js"></script>	
<!-- 用户个人信息css -->
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/css/user_info.css">

<!-- AdminLTE Skins. Choose a skin from the css/skins
       folder instead of downloading all of them to reduce the load. -->
<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/dist/css/skins/_all-skins.min.css">
</head>
<body class="hold-transition skin-black sidebar-mini"
	style="height: auto;">
	<%-- <%@ include file="header-logo.jsp"%> --%>
	<!-- 首页/返回 -->
	<div class="" style="padding: 10px">
		<a href="<%=CONTEXT_PATH%>/index_qt.jsp"><img
			style="padding-bottom: 10px ;color: #388CAE;" src="/ts/image/u1155.png" id="shouye"></a>
		<span style="color: #388CAE; font-size: 20px">&nbsp;&nbsp;/&nbsp;&nbsp;个人信息</span>
	</div>
	<!-- 显示主模块 -->
	<div id="myTabContent" class="tab-content">
		<div class="tab-pane fade active in" style="position: relative;"
			id="home">
			<div id="cuxian1"
				style="margin-left: 10px; margin-top: 0px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
				<span style="margin-left: 50px; padding-top: 10px">个人基本信息</span>
			</div>
			<!-- 分割线 -->
<!-- 			<div id="xixian" -->
<!-- 				style="margin-left: 10px; margin-top: 20px; background-color: lightgray; height: 1px; width: 98%"> -->
<!-- 			</div> -->
			<!-- 用户个人信息主要数据展示区域 -->
			<div id="table_div" class="container-fluid"
				style="margin-top: 20px; margin-left: 10px; width: 98%; margin: 0 auto; text-align: center;">
				<div class="row">
					<div class="col-xs-12">
						<div id="main-left" class="col-xs-4"
							style="float: left; background-color: rgba(255, 255, 255, 1); /* height: 100%; */ padding-top:20px;">
							<img alt="" src="/qt/img/u844.jpg" id="user_photo" style="width: 60%;">
							<div id="img-bottom-div" >
							<div class="img-bottom-info">
								<span>姓名：</span><span id="USER_NAME">信息为空</span>
							</div>
							<div class="img-bottom-info">
								<span>用户编码：</span><span id="USER_CODE">信息为空</span>
							</div>
							<div class="img-bottom-info">
								<span>登录名：</span><span id="USER_LOGIN_NAME">信息为空</span>
							</div>
							<div class="img-bottom-info">
								<span>所在公司：</span><span id="CMPY_CODE">信息为空</span>
							</div>
							<div class="img-bottom-info">
								<span>当前机构：</span><span id="DEPT_CODE">信息为空</span>
							</div>
							<div class="img-bottom-info">
								<span>办公电话：</span><span id="USER_OFFICE_PHONE">信息为空</span>
							</div>
							<div class="img-bottom-info">
								<span>手机：</span><span id="USER_MOBILE">信息为空</span>
							</div>
							<div class="img-bottom-info">
								<span>融e联绑定手机：</span><span id="RYL_MOBILE">信息为空</span>
							</div>
							<div class="img-bottom-info">
								<span>邮箱：</span><span id="USER_EMAIL">信息为空</span>
							</div>
							</div>
							
						</div>
						<div id="main-data" style="float: left; height: auto; border: 1px solid #e7e6e3;" class="col-xs-8">
						
							<div class="row" style="border-bottom: 1px solid #e7e6e3;left: -2.2%; padding-left: 6px;background-color:#ffffff;">
								<img src="/qt/img/u892.png" style="float: left;top: 7px;position: relative;">
								<div style="float: left;">基本信息</div>
							</div>

							<div class="row" style="">
								<div class="col-xs-4 info-left" style="float: left;">工位号</div>
								<div class="col-xs-8 info-right" id="USER_WORK_LOC" style="float: left;">暂无数据</div>
							</div>

							<div class="row" style="">
								<div class="col-xs-4 info-left" style="float: left;">职位</div>
								<div class="col-xs-8 info-right" id="USER_POST" style="float: left;">暂无数据</div>
							</div>

							<div class="row" style="">
								<div class="col-xs-4 info-left" style="float: left;">职位级别</div>
								<div class="col-xs-8 info-right" id="USER_POST_LEVEL" style="float: left;">暂无数据</div>
							</div>

							<div class="row" style="">
								<div class="col-xs-4 info-left" style="float: left;">工号</div>
								<div class="col-xs-8 info-right" id="USER_WORK_NUM" style="float: left;">暂无数据</div>
							</div>
							<div class="row" style="">
								<div class="col-xs-4 info-left" style="float: left;">身份证号</div>
								<div class="col-xs-8 info-right" id="USER_IDCARD" style="float: left;">暂无数据</div>
							</div>
							<div class="row" style="">
								<div class="col-xs-4 info-left" style="float: left;">出生日期</div>
								<div class="col-xs-8 info-right" id="USER_BIRTHDAY" style="float: left;">暂无数据</div>
							</div>
							<div class="row" style="">
								<div class="col-xs-4 info-left" style="float: left;">民族</div>
								<div class="col-xs-8 info-right" id="USER_NATION" style="float: left;">暂无数据</div>
							</div>
							<div class="row" style="">
								<div class="col-xs-4 info-left" style="float: left;">性别</div>
								<div class="col-xs-8 info-right" id="USER_SEX" style="float: left;">暂无数据</div>
							</div>
							<div class="row" style="">
								<div class="col-xs-4 info-left" style="float: left;">籍贯</div>
								<div class="col-xs-8 info-right" id="USER_HOME_LAND" style="float: left;">暂无数据</div>
							</div>
							<div class="row" style="">
								<div class="col-xs-4 info-left" style="float: left;">政治面貌</div>
								<div class="col-xs-8 info-right" id="USER_POLITICS" style="float: left;">暂无数据</div>
							</div>
							<div class="row" style="">
								<div class="col-xs-4 info-left" style="float: left;">学历</div>
								<div class="col-xs-8 info-right" id="USER_EDU_LEVLE" style="float: left;">暂无数据</div>
							</div>
							<div class="row" style="">
								<div class="col-xs-4 info-left" style="float: left;">毕业学校</div>
								<div class="col-xs-8 info-right" id="USER_EDU_SCHOOL" style="float: left;">暂无数据</div>
							</div>
							<div class="row" style="">
								<div class="col-xs-4 info-left" style="float: left;">专业</div>
								<div class="col-xs-8 info-right" id="USER_EDU_MAJOR" style="float: left;">暂无数据</div>
							</div>
							<div class="row" style="">
								<div class="col-xs-4 info-left" style="float: left;">职称</div>
								<div class="col-xs-8 info-right" id="USER_TITLE" style="float: left;">暂无数据</div>
							</div>
							<div class="row" style="">
								<div class="col-xs-4 info-left" style="float: left;">职称日期</div>
								<div class="col-xs-8 info-right" id="USER_TITLE_DATE" style="float: left;">暂无数据</div>
							</div>
							<div class="row" style="">
								<div class="col-xs-4 info-left" style="float: left;">参加工作日期</div>
								<div class="col-xs-8 info-right" id="USER_WORK_DATE" style="float: left;">暂无数据</div>
							</div>
							<div class="row" style="">
								<div class="col-xs-4 info-left" style="float: left;">入职日期</div>
								<div class="col-xs-8 info-right" id="USER_CMPY_DATE" style="float: left;">暂无数据</div>
							</div>
							<div class="row" style="">
								<div class="col-xs-4 info-left" style="float: left;border-bottom:0px solid #333333;">状态</div>
								<div class="col-xs-8 info-right" id="USER_STATE" style="float: left;">暂无数据</div>
							</div>

						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- jQuery压缩版的引入 -->
	<script src="<%=CONTEXT_PATH%>/qt/plugins/jQuery/jquery-2.2.3.min.js"></script>
	<!-- Bootstrap 3.3.6 -->
	<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/bootstrap.min.js"></script>
	<!-- FastClick -->
	<script src="<%=CONTEXT_PATH%>/qt/plugins/fastclick/fastclick.js"></script>
	<!-- AdminLTE for demo purposes -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/demo.js"></script>
	<!-- 	当前登录用户信息展示JS引入 -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/app.min.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/js/user_info.js"></script>
</body>