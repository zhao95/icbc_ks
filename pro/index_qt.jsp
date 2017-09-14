<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
    final String CONTEXT_PATH = request.getContextPath();
%>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<!-- <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE9" > -->
<title>前台首页</title>
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">

<%@ include file="sy/base/view/inHeader-icbc.jsp"%>
<!-- Bootstrap 3.3.6 -->
<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/bootstrap/css/bootstrap.min.css">

<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/font-awesome-4.7.0/css/font-awesome.min.css">
<!-- Ionicons -->
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/ionicons/css/ionicons.min.css">

<!-- 进度条css -->
<%-- <link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/css/jdt.css"> --%>

<!-- 考试日历的样式 -->
<%-- <link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/css/ksrl.css"> --%>
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/css/jquery-ui-1.10.3.custom-0.css">
<script src="<%=CONTEXT_PATH%>/qt/js/html5shiv.min.js"></script>
<script src="<%=CONTEXT_PATH%>/qt/js/respond.min.js"></script>
<!--代办/提醒列表样式-->
<%-- <link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/css/qt_todo.css"> --%>

<!-- 首页menu菜单图标雪碧图css文件 -->
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/css/rh-menu-icons.css">

<!-- Theme style -->
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/dist/css/AdminLTE.min.css">



<!-- AdminLTE Skins. Choose a skin from the css/skins folder instead of downloading all of them to reduce the load. -->
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/dist/css/skins/_all-skins.min.css">
	
<!-- 首页模块css -->
<!-- Font Awesome -->
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/css/index_qt.css">

</head>
<body class="hold-transition skin-black sidebar-mini layout-boxed">
	<div class="wrapper">
		<div style="position: absolute; left: 15px; top:5px; z-index: 10000;">
			<img alt="中国工商银行" src="./qt/img/header-logo.png" id="logo_img1">
			<img alt="考试系统" src="./qt/img/header-logo-text.png"  id="logo_img2">
		</div>
		<header class="main-header" style="height: 60px;/* box-shadow: rgb(190, 190, 190) 0px 0px 20px; */">
			<!-- Logo -->
			<!-- <div class="logo">
				<span class="logo-lg"><b>考试系统</b></span>
			</div> -->
			<!-- Header Navbar: style can be found in header.less -->
			<nav class="navbar navbar-static-top">
				<!-- Sidebar toggle button-->
				<!-- <a href="/" class="sidebar-toggle" data-toggle="offcanvas" role="button">
        <span class="sr-only">Toggle navigation</span>
      </a> -->

				<div class="navbar-custom-menu">
					<ul class="nav navbar-nav">
						<li><a style="height: 60px;border-left: 0px;">
    						<img src="qt/css/images/to_ht.png" style="cursor: pointer;">
    						</a>
    					</li>
						<!-- Notifications: style can be found in dropdown.less -->
						<li class="dropdown notifications-menu"><a href="#"
							class="dropdown-toggle" data-toggle="dropdown" style="height:60px;border-left: 0px;"> 
<!-- 							<i　class="fa fa-bell-o"></i> -->
								<img src="qt/css/images/qt_bell.png">
							<span class="label label-warning" id="tipSum">0</span>
						</a>
							<ul class="dropdown-menu index-qt-tip-list" style="left: -120px;"></ul>
						</li>
						<!-- User Account: style can be found in dropdown.less -->
						<li class="dropdown user user-menu">
							<a id="userInfo2" href="#"
							class="dropdown-toggle" data-toggle="dropdown" style="height:60px;width:160px;text-align: center; border-left:0px">
							<img id= "userImg2"
								src="/sy/theme/default/images/common/user0.png" class="user-image"
								alt="User Image" style="margin-right: 2px;">
								<!-- <span class="hidden-xs">Alexander Pierce</span> -->
						</a>
							<ul class="dropdown-menu" style="width:160px;">
<!-- 							<li class="user-header"></li> -->
<!-- 							<li class="user-body"></li> -->
							<li><a href="#" class="" id="TipUserInfo"><i class="fa fa-user-o fa-fw"></i>个人信息</a></li>
								<li><a href="#" class="" data-layout="layout-boxed"><i class="fa fa-cog fa-fw"></i>简约/平铺</a></li>
<!-- 								<li><input type="checkbox" data-layout="layout-boxed" class="pull-right" checked="checked">简约/平铺</li> -->
								<li><a  class="" id="loginOutBtn"><i class="fa fa-power-off fa-fw"></i>注销用户</a></li>
							
<!-- 							<li class="user-footer"></li> -->
								
							
<!-- 									<a href="#" class="btn btn-default btn-flat">Profile</a> -->
<!-- 									<a href="#" class="btn btn-default btn-flat" data-layout="layout-boxed">简约/平铺</a> -->
<!-- 									<a href="#" class="btn btn-default btn-flat">Sign out</a> -->
<!-- 								<li class="user-header"><img id = "userImg3" -->
<!-- 									src="qt/dist/img/user2-160x160.jpg" class="img-circle" -->
<!-- 									alt="User Image"> -->
<!-- 									<p> -->
<!-- 										Alexander Pierce - Web Developer <small>Member since -->
<!-- 											Nov. 2012</small> -->
<!-- 									</p></li> -->
<!-- 								</li> -->
							</ul></li>
						<!-- Control Sidebar Toggle Button -->
						<!-- <li>
            <a href="#" data-toggle="control-sidebar"><i class="fa fa-gears"></i></a>
          </li> -->
					</ul>
				</div>
			</nav>
		</header>
		<div style="width: 90%;height: 1px;background-color: rgb(230,230,230);position: relative;left: 230px;box-shadow: rgb(190, 190, 190) 1px 3px 6px 0px;"></div>
		<aside class="main-sidebar" style="z-index:0;top:30px;">
			<!-- sidebar: style can be found in sidebar.less -->
			<section class="sidebar">
				<!-- Sidebar user panel -->
				<div class="user-panel text-center">
					<div class=" image" style="position: absolute;padding-left: 10px;">
						<img id="userImg1" style="max-width: 64px;" src="/sy/theme/default/images/common/user_64_64.png" class="img-circle" alt="User Image">
					</div>
					<div id="userInfo" class="text-center" style="line-height: 32px;height: 64px;margin-left: 74px;">
						<!-- <p>刘小雨</p>
						<p>工商银行总行</p> -->
					</div>
				</div>

				<div style="margin-top: 20px; text-align:center;">
					
					<img  id="left-img-renzheng" src="/qt/img/left-logo.png" style="cursor: pointer;">
				</div>

				<div class="user-panel" />
				<!-- 分割线 -->
				<div style="width: 100%; height: 5px; background-color: #063c5d;position: absolute;top:12px;left: 0px;"></div>
				<ul class="sidebar-menu" id="sdfsdfsf" style="position: relative;top: 15px;">
					<!-- <li class="header"><i class="fa fa-book"></i><span>首页</span></li> -->
					<!-- <li><a href="../../documentation/index.html"><i
							class="fa fa-book"></i> <span>我的报名</span><span
							class="pull-right-container"> <span
								class="label label-primary pull-right">4</span>
						</span></a></li>
					<li><a href="../../documentation/index.html"><i
							class="fa fa-book"></i> <span>我的考试</span></a></li>
					<li><a href="../../documentation/index.html"><i
							class="fa fa-book"></i> <span>我的请假</span><span
							class="pull-right-container"> <span
								class="label label-primary pull-right">3</span>
						</span></a></li>
					 -->
				</ul>
			</section>
			<!-- /.sidebar -->
		</aside>

		<div class="content-wrapper" >

			<section class="content">
				<div class="row">
					<div class="col-md-8" id="main_col8">
																	<!-- /.box -->
						<div class="panel panel-default box" style=" border-top: 1px solid #d2d6de;" >
							<div class="box-header with-border"  style="background-color:#f9f9f9;height: 40px;">
								<h3 class="panel-title">资格考试项目进展状态
									<a onclick="window.open('/qt/jsp/xmzt.jsp')" class="index-list-more-a">更多
									<span style="color:red;">&gt;</span>
									</a>
								</h3>
								<div style="width: 160px;height: 3px;position: relative;top: 11px;left: 0px;background-color: #ff0000;"></div>
							</div>
							<!-- /.box-header -->
							<div class="box-body no-padding">
								<div id="jdtTitle" style="height: 50px;">
									<div id="jdtNameT">报考项:</div>
									<div id="jdtName">暂未报名考试项目</div>
									<div class="jdtNumClass">
										<span id="jdtNum">0.0</span> <span style="color:#666666;">%</span>
									</div>
									<br />
								</div>
								<!-- 进度条 -->
								<div id="jdtMain">
									<div id="upDiv">
										<div style="left: -3%">
											报名
											<div class="upSX"></div>
										</div>
										<div style="left: 7%">
											人工审核
											<div class="upSX"></div>
										</div>
										<div style="left: 19%">
											结果公示
											<div class="upSX"></div>
										</div>
										<div style="left: 31%">
											考场安排
											<div class="upSX"></div>
										</div>
										<div style="left: 43%">
											辖内公示
											<div class="upSX"></div>
										</div>
										<div style="left: 54%">
											总行公示
											<div class="upSX"></div>
										</div>
										<div style="left: 70%">
											考试
											<div class="upSX"></div>
										</div>
										<div style="left: 79%">
											成绩公示
											<div class="upSX"></div>
										</div>
										<div style="left: 90%">
											认证公示
											<div class="upSX"></div>
										</div>
									</div>
									<br />
									<div id="jdtDivOut" class="progress">
										<div id="jdtDivInner1" class="progress-bar"
											style="background-color: #f0f0f0"></div>
										<div id="jdtDivInner2" class="progress-bar"
											style="background-color: #f0f0f0"></div>
										<div id="jdtDivInner3" class="progress-bar divColor"
											style="background-color: #f0f0f0"></div>
										<div id="jdtDivInner4" class="progress-bar divColor"
											style="background-color: #f0f0f0"></div>
										<div id="jdtDivInner5" class="progress-bar divColor"
											style="background-color: #f0f0f0"></div>
										<div id="jdtDivInner6" class="progress-bar"
											style="background-color: #f0f0f0"></div>
										<div id="jdtDivInner7" class="progress-bar"
											style="background-color: #f0f0f0"></div>
										<div id="jdtDivInner8" class="progress-bar"
											style="background-color: #f0f0f0"></div>
									</div>

									<div id="downDiv">
										<div style="left: 2%;">
											<div class="downSX">|</div>
											<span>提交报名</span><br /> <span>自动审核</span><br /> <span>异议</span>

										</div>
										<div style="left: 16%">
											<div class="downSX">|</div>
											异议
										</div>
										<div style="left: 28%">
											<div class="downSX">|</div>
											异议
										</div>
										<div style="left: 40%"  id ="jk_sp" >
											<div class="downSX" >|</div>
											借考
										</div>
										<div style="left: 50%">
											<div class="downSX">|</div>
											<span>考场调整</span><br><span id ="qj_sp">请假</span>
										</div>
										<div style="left: 61%">
											<div class="downSX">|</div>
											打印准考证
										</div>
									</div>
									<br />
								</div>
								<!-- 分割线 -->
								<div style="width:100%;height: 2px;margin: 0 auto;border-bottom: 2px dashed #efefef;"></div>
								<div id="jdtComment">
									<div class="jdtColor" style="background-color: #ff0000;"></div>
									<div class="jdtTEXT">进行中</div>
									<div class="jdtColor" style="background-color: #70c0b3;"></div>
									<div class="jdtTEXT">已完成</div>
									<div class="jdtColor" style="background-color: #27b9f8;"></div>
									<div class="jdtTEXT">未完成</div>
									<div class="jdtColor" style="background-color: #f0f0f0;"></div>
									<div class="jdtTEXT">未启用</div>

								</div>
							</div>
							<!-- /.box-body -->
						</div>
						<!-- /.box-div -->
						<!-- /.box -->

					<%--待办/提醒--%>
					<div class="panel panel-default box" id="todo-panel" style="border-top: 1px solid #d2d6de;">
						<div class="panel-heading" style="background-color: #f9f9f9">
							<h3 class="panel-title">
								待办 / 提醒 (<span id="todoListSum" style="color:red">0</span>)
								<a onclick="window.open('/qt/jsp/todo.jsp')" class="index-list-more-a">
									更多
									<span style="color:red;">></span>
								</a>
							</h3>
							<div style="width: 109px;height: 3px;position: relative;top: 11px;left: -5px;background-color: #ff0000;">
							</div>
						</div>
						<div class="panel-body">

							<div class="index-qt-todo-list">
								<div style="" class="todo-item">
									<div style="" class="todo-circle yiyi">
										<div style="padding:10px 4px;color: #fff">异议</div>
									</div>
									<div class="todo-content">
										<div>2017年（第8周）资格考试报名异议申请</div>
										<div style="font-size: 12px;color:#999999">
											河南分行郑州市分行郑东区支行天津路营业部 孙少洋 2017.05.12 16:40
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>

					<div class="panel panel-default box" id="apply-panel" style="border-top: 1px solid #d2d6de;">

						<div class="panel-heading" style="background-color: #f9f9f9">
							<h3 class="panel-title">
								可申请的报名 (<span id="keshenqingbaomingSum" style="color:red">0</span>)
								<a onclick="window.open('/ts/jsp/bm.jsp')" class="index-list-more-a">
									更多
									<span style="color:red;">></span>
								</a>
							</h3>
							<div style="width: 124px;height: 3px;position: relative;top: 11px;left: -5px;background-color: #ff0000;">
							</div>
						</div>
						<div class="panel-body">
                            <table class="rhGrid ss JColResizer" id="apply-table">
								<thead class="">
								<tr style="backGround-color:WhiteSmoke; height: 30px">
									<th class="" style="width: 40%;">名称</th>
									<th class="" style="width: 30%;">组织单位</th>
									<th class="" style="width: 189px;">报名时间</th>
								</tr>
								</thead>
								<tbody class="grid-tbody">
								<tr>
									<td >2017年（第8周）资格考试报名</td>
									<td >中国工商银行总行</td>
									<td>2017-05-07 00:00 － 05-16 00:00</td>
								</tr>
								<tr>
									<td >2017年非资格考试报名</td>
									<td >北京分行</td>
									<td>2017-05-07 00:00 － 05-16 00:00</td>
								</tr>
								</tbody>
							</table>
						</div>
					</div>

						<!-- /.box -->
					</div>
					<!-- /.col -->
					<div class="col-md-4" id="main_col4" >
						<div id="announcement-box" class="panel panel-default box" style="border-top: 1px solid #d2d6de;">
							<div class="box-header" style="background-color: #f9f9f9;">
								<h3 class="box-title" style="display: block; font-size:16px;">
									通知公告
									<a onclick="window.open('/qt/jsp/todo.jsp')" class="index-list-more-a">
										更多
										<span style="color:red;">&gt;</span>
									</a>
								</h3>
							</div>
							<!-- /.box-header -->
							<div class="box-body no-padding" style="margin-bottom: 10px; min-height: 180px;">
								<table class="table no-border">
									<tr>
										<td>
											<span style="width: 16px;height: 16px;display: inline-block;font-size: 13px;text-align: center;color: #398daf;">
												●
											</span>
											关于使用正规浏览器...</td>
										<td>2017-05-31</td>
									</tr>
									<tr>
										<td>
											<span style="width: 16px;height: 16px;display: inline-block;font-size: 13px;text-align: center;color: #b4dbc0;">
												●
											</span>
											个人对公初级专业资...</td>
										<td>2017-05-21</td>
									</tr>
									<tr>
										<td>
											<span style="width: 16px;height: 16px;display: inline-block;font-size: 13px;text-align: center;color: #ff0000;">
												●
											</span>
											综合中级专业资格认</td>
										<td>2017-05-11</td>
									</tr>
								</table>
							</div>
							<!-- /.box-body -->
						</div>
						<!-- /.box -->

						<div class="panel panel-default box" style="border-top: 1px solid #d2d6de;">
							<div class="box-header" style="background-color: #f9f9f9;">
								<h3 class="box-title" style="font-size:16px;">Q&A 问答</h3>
							</div>
							<div style="height:72px;"></div>
							<!-- /.box-header -->
							<div class="box-body no-padding">
								<table class="table table-striped" id="qItem"></table>
								<div align="center" id="question_div">
									<button type="button" style="width: 40%; margin: 5px;"
										class="btn btn-block btn-success"  >我要提问</button>
								</div>
							</div>
							<!-- /.box-body -->
						</div>
						<!-- /.box -->
						<div class="panel panel-default box" style="border-top: 1px solid #d2d6de;">
						<div class="box-header" icode="ksrl-header" style="background-color: #f9f9f9;height: 40px;">
							<h3 class="box-title" style="font-size:16px;">考试日历</h3>
							<a onclick="window.open('/qt/jsp/ksrl.jsp')"  class="index-list-more-a">
									更多
									<span style="color:red;">&gt;</span>
								</a>
						</div>
						<div class="TS_KS_CAL" style="height:245px;">
						<!-- 						<div class="title_top"> -->
						<!-- 		<div style="clear:right;"></div> -->
						<!-- 	</div> -->
							<div iiCode="kaoshi">
								<div class="scalender" style='position:relative;'>
									<div class="TS_KS_CAL_picker"></div>
								</div>
							</div>
						</div>
						</div>
						<!-- /.box -->
						<div class="panel panel-default box" style="border-top: 1px solid #d2d6de;">
							<div class="box-header" style="background-color: #f9f9f9">
								<h3 class="box-title" style="font-size:16px;">问卷调查</h3>
							</div>
							<!-- /.box-header -->
							<div class="box-body no-padding">
								<table class="table table-striped">
									<tr>
										<td style="width: 10px">1
										</th>
										<td>新版考试系统使用问卷调查</td>
										<td>2017-06-01</td>
									</tr>
									<tr>
										<td style="width: 10px">2
										</th>
										<td>新版考试系统使用问卷调查</td>
										<td>2017-06-01</td>
									</tr>
									<tr>
										<td style="width: 10px">3
										</th>
										<td>新版考试系统使用问卷调查</td>
										<td>2017-06-01</td>
									</tr>

								</table>
							</div>
							<!-- /.box-body -->
						</div>
						<!-- /.box -->
						<div class="panel panel-default box" style="border-top: 1px solid #d2d6de;display:none;'">
							<div class="box-header" style="background-color: #f9f9f9">
								<h3 class="box-title" style="font-size:16px;">我的认证轨迹</h3>
							</div>
							<!-- /.box-header -->
							<div class="box-body no-padding">
								<table class="table table-striped">
									<tr>
										<td>111</td>
									</tr>
								</table>
							</div>
							<!-- /.box-body -->
						</div>
						<!-- /.box -->
					</div>
					<!-- /.col -->
				</div>
			</section>

		</div>

		<footer class="main-footer" id="main-fotter-div">
			<div align="center">
				<img id="qt_footer_logo" class="img" src="/qt/img/u996.png">
				<strong>中国工商银行考试系统</strong>
			</div>
			<div>
				<table>
					<tr id="qt_footer_tr" style="width: 90%;">
						<td width="20%"><span>版权所有：中国工商银行 </span></td>
						<td><span>|</span></td>
						<td width="30%"><span>地址：北京市复兴门内大街55号</span></td>
						<td><span></span></td>
						<td width="16%"><span>邮编：100140 </span></td>
						<td><span>|</span></td>
						<td width="14%" align="right"><span style="font-family:'微软雅黑';font-weight: 400;color: #CCCCCC;">2017 年 6 月 </span></td>
					</tr>
				</table>
			</div>
		</footer>

		<aside class="control-sidebar control-sidebar-dark">
			<!-- Create the tabs -->
			<ul class="nav nav-tabs nav-justified control-sidebar-tabs">
				<li><a href="#control-sidebar-home-tab" data-toggle="tab"><i
						class="fa fa-home"></i></a></li>
				<li><a href="#control-sidebar-settings-tab" data-toggle="tab"><i
						class="fa fa-gears"></i></a></li>
			</ul>
			<!-- Tab panes -->
			<div class="tab-content">
				<!-- Home tab content -->
				<div class="tab-pane" id="control-sidebar-home-tab">
					<h3 class="control-sidebar-heading">Recent Activity</h3>
					<ul class="control-sidebar-menu">
						<li><a href="javascript:void(0)"> <i
								class="menu-icon fa fa-birthday-cake bg-red"></i>

								<div class="menu-info">
									<h4 class="control-sidebar-subheading">Langdon's Birthday</h4>

									<p>Will be 23 on April 24th</p>
								</div>
						</a></li>
						<li><a href="javascript:void(0)"> <i
								class="menu-icon fa fa-user bg-yellow"></i>

								<div class="menu-info">
									<h4 class="control-sidebar-subheading">Frodo Updated His
										Profile</h4>

									<p>New phone +1(800)555-1234</p>
								</div>
						</a></li>
						<li><a href="javascript:void(0)"> <i
								class="menu-icon fa fa-envelope-o bg-light-blue"></i>

								<div class="menu-info">
									<h4 class="control-sidebar-subheading">Nora Joined Mailing
										List</h4>

									<p>nora@example.com</p>
								</div>
						</a></li>
						<li><a href="javascript:void(0)"> <i
								class="menu-icon fa fa-file-code-o bg-green"></i>

								<div class="menu-info">
									<h4 class="control-sidebar-subheading">Cron Job 254
										Executed</h4>

									<p>Execution time 5 seconds</p>
								</div>
						</a></li>
					</ul>
					<!-- /.control-sidebar-menu -->

					<h3 class="control-sidebar-heading">Tasks Progress</h3>
					<ul class="control-sidebar-menu">
						<li><a href="javascript:void(0)">
								<h4 class="control-sidebar-subheading">
									Custom Template Design <span
										class="label label-danger pull-right">70%</span>
								</h4>

								<div class="progress progress-xxs">
									<div class="progress-bar progress-bar-danger"
										style="width: 70%"></div>
								</div>
						</a></li>
						<li><a href="javascript:void(0)">
								<h4 class="control-sidebar-subheading">
									Update Resume <span class="label label-success pull-right">95%</span>
								</h4>

								<div class="progress progress-xxs">
									<div class="progress-bar progress-bar-success"
										style="width: 95%"></div>
								</div>
						</a></li>
						<li><a href="javascript:void(0)">
								<h4 class="control-sidebar-subheading">
									Laravel Integration <span
										class="label label-warning pull-right">50%</span>
								</h4>

								<div class="progress progress-xxs">
									<div class="progress-bar progress-bar-warning"
										style="width: 50%"></div>
								</div>
						</a></li>
						<li><a href="javascript:void(0)">
								<h4 class="control-sidebar-subheading">
									Back End Framework <span class="label label-primary pull-right">68%</span>
								</h4>

								<div class="progress progress-xxs">
									<div class="progress-bar progress-bar-primary"
										style="width: 68%"></div>
								</div>
						</a></li>
					</ul>
					<!-- /.control-sidebar-menu -->

				</div>
				<!-- /.tab-pane -->
				<!-- Stats tab content -->
				<div class="tab-pane" id="control-sidebar-stats-tab">Stats Tab
					Content</div>
				<!-- /.tab-pane -->
				<!-- Settings tab content -->
				<div class="tab-pane" id="control-sidebar-settings-tab">
					<!-- <form method="post"> <!-- 废弃区域 -->
						<h3 class="control-sidebar-heading">General Settings</h3>

						<div class="form-group">
							<label class="control-sidebar-subheading"> Report panel
								usage <input type="checkbox" class="pull-right" checked>
							</label>

							<p>Some information about this general settings option</p>
						</div>
						/.form-group

						<div class="form-group">
							<label class="control-sidebar-subheading"> Allow mail
								redirect <input type="checkbox" class="pull-right" checked>
							</label>

							<p>Other sets of options are available</p>
						</div>
						/.form-group

						<div class="form-group">
							<label class="control-sidebar-subheading"> Expose author
								name in posts <input type="checkbox" class="pull-right" checked>
							</label>

							<p>Allow the user to show his name in blog posts</p>
						</div>
						/.form-group

						<h3 class="control-sidebar-heading">Chat Settings</h3>

						<div class="form-group">
							<label class="control-sidebar-subheading"> Show me as
								online <input type="checkbox" class="pull-right" checked>
							</label>
						</div>
						/.form-group

						<div class="form-group">
							<label class="control-sidebar-subheading"> Turn off
								notifications <input type="checkbox" class="pull-right">
							</label>
						</div>
						/.form-group

						<div class="form-group">
							<label class="control-sidebar-subheading"> Delete chat
								history <a href="javascript:void(0)" class="text-red pull-right"><i
									class="fa fa-trash-o"></i></a>
							</label>
						</div>
						/.form-group
					</form> -->
				</div>
				<!-- /.tab-pane -->
			</div>
		</aside>
		<!-- /.control-sidebar -->
		<!-- Add the sidebar's background. This div must be placed
       immediately after the control sidebar -->
       <!-- 右侧隐藏工具栏 -->
<!-- 		<div class="control-sidebar-bg"></div> -->
	</div>
	<script src="<%=CONTEXT_PATH%>/qt/js/index_qt.js"></script>
	<!-- jQuery压缩版的引入 -->
	<script src="<%=CONTEXT_PATH%>/qt/plugins/jQuery/jquery-2.2.3.min.js"></script>
	<!-- Bootstrap 3.3.6 -->
	<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/bootstrap.min.js"></script>
	<!-- FastClick -->
	<script src="<%=CONTEXT_PATH%>/qt/plugins/fastclick/fastclick.js"></script>
	<!-- AdminLTE App -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/app.min.js"></script>
	<!-- AdminLTE for demo purposes -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/demo.js"></script>
	<!-- 问答模块的js文件 -->
<%-- 	<script src="<%=CONTEXT_PATH%>/qt/js/wdgl.js"></script> --%>

	

	<!-- 考试日历模块JS文件 -->
	<script src="<%=CONTEXT_PATH%>/qt/js/jquery-ui-1.10.3.custom.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/js/jquery.ui.datepicker-zh-CN.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/js/ksrl.js"></script>

	<!-- 项目状态进度条展示的js文件 -->
	<script src="<%=CONTEXT_PATH%>/qt/js/xmzt.js"></script>

	<!-- 首页待办/可申请报名js -->
	<script src="<%=CONTEXT_PATH%>/qt/js/index_qt_todo_apply.js"></script>
</body>
</html>
