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
<title>前台首页</title>
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">

<%@ include file="sy/base/view/inHeader.jsp"%>
<!-- Bootstrap 3.3.6 -->
<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/bootstrap/css/bootstrap.min.css">
<!-- Font Awesome -->
<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/font-awesome-4.7.0/css/font-awesome.min.css">
<!-- Ionicons -->
<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/ionicons/css/ionicons.min.css">

<!-- 进度条css -->
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/css/jdt.css">

<!-- 考试日历的样式 -->
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/css/ksrl.css">
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/css/jquery-ui-1.10.3.custom-0.css">
<script src="<%=CONTEXT_PATH%>/qt/js/html5shiv.min.js"></script>
<script src="<%=CONTEXT_PATH%>/qt/js/respond.min.js"></script>
<!--代办/提醒列表样式-->
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/css/qt_todo.css">
<!-- Theme style -->
<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/dist/css/AdminLTE.min.css">
<!-- AdminLTE Skins. Choose a skin from the css/skins
       folder instead of downloading all of them to reduce the load. -->
<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/dist/css/skins/_all-skins.min.css">
</head>
<body class="hold-transition skin-black sidebar-mini">
	<div class="wrapper">
		<div style="position: absolute; left: 15px; z-index: 10000;">
			<img alt="中国工商银行" src="./qt/img/u3148.png">
			<img alt="考试系统" src="./qt/img/u3376.png">
		</div>
		<header class="main-header">
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
						<!-- Messages: style can be found in dropdown.less-->
						<li class="dropdown messages-menu"><a href="#"
							class="dropdown-toggle" data-toggle="dropdown"> <i
								class="fa fa-envelope-o"></i> <span class="label label-success">4</span>
						</a>
							<ul class="dropdown-menu">
								<li class="header">You have 4 messages</li>
								<li>
									<!-- inner menu: contains the actual data -->
									<ul class="menu">
										<li>
											<!-- start message --> <a href="#">
												<div class="pull-left">
													<img src="dist/img/user2-160x160.jpg" class="img-circle"
														alt="User Image">
												</div>
												<h4>
													Support Team <small><i class="fa fa-clock-o"></i> 5
														mins</small>
												</h4>
												<p>Why not buy a new awesome theme?</p>
										</a>
										</li>
										<!-- end message -->
										<li><a href="#">
												<div class="pull-left">
													<img src="dist/img/user3-128x128.jpg" class="img-circle"
														alt="User Image">
												</div>
												<h4>
													AdminLTE Design Team <small><i
														class="fa fa-clock-o"></i> 2 hours</small>
												</h4>
												<p>Why not buy a new awesome theme?</p>
										</a></li>
										<li><a href="#">
												<div class="pull-left">
													<img src="qt/dist/img/user4-128x128.jpg" class="img-circle"
														alt="User Image">
												</div>
												<h4>
													Developers <small><i class="fa fa-clock-o"></i>
														Today</small>
												</h4>
												<p>Why not buy a new awesome theme?</p>
										</a></li>
										<li><a href="#">
												<div class="pull-left">
													<img src="qt/dist/img/user3-128x128.jpg" class="img-circle"
														alt="User Image">
												</div>
												<h4>
													Sales Department <small><i class="fa fa-clock-o"></i>
														Yesterday</small>
												</h4>
												<p>Why not buy a new awesome theme?</p>
										</a></li>
										<li><a href="#">
												<div class="pull-left">
													<img src="qt/dist/img/user4-128x128.jpg" class="img-circle"
														alt="User Image">
												</div>
												<h4>
													Reviewers <small><i class="fa fa-clock-o"></i> 2
														days</small>
												</h4>
												<p>Why not buy a new awesome theme?</p>
										</a></li>
									</ul>
								</li>
								<li class="footer"><a href="#">See All Messages</a></li>
							</ul></li>
						<!-- Notifications: style can be found in dropdown.less -->
						<li class="dropdown notifications-menu"><a href="#"
							class="dropdown-toggle" data-toggle="dropdown"> <i
								class="fa fa-bell-o"></i> <span class="label label-warning">10</span>
						</a>
							<ul class="dropdown-menu">
								<li class="header">You have 10 notifications</li>
								<li>
									<!-- inner menu: contains the actual data -->
									<ul class="menu">
										<li><a href="#"> <i class="fa fa-users text-aqua"></i>
												5 new members joined today
										</a></li>
										<li><a href="#"> <i class="fa fa-warning text-yellow"></i>
												Very long description here that may not fit into the page
												and may cause design problems
										</a></li>
										<li><a href="#"> <i class="fa fa-users text-red"></i>
												5 new members joined
										</a></li>
										<li><a href="#"> <i
												class="fa fa-shopping-cart text-green"></i> 25 sales made
										</a></li>
										<li><a href="#"> <i class="fa fa-user text-red"></i>
												You changed your username
										</a></li>
									</ul>
								</li>
								<li class="footer"><a href="#">View all</a></li>
							</ul></li>
						<!-- Tasks: style can be found in dropdown.less -->
						<!-- <li class="dropdown tasks-menu"><a href="#"
							class="dropdown-toggle" data-toggle="dropdown"> <i
								class="fa fa-flag-o"></i> <span class="label label-danger">9</span>
						</a>
							<ul class="dropdown-menu">
								<li class="header">You have 9 tasks</li>
								<li class="footer"><a href="#">View all tasks</a></li>
							</ul></li> -->
						<!-- User Account: style can be found in dropdown.less -->
						<li class="dropdown user user-menu">
							<a id="userInfo2" href="#"
							class="dropdown-toggle" data-toggle="dropdown">
							<img id= "userImg2"
								src="qt/dist/img/user2-160x160.jpg" class="user-image"
								alt="User Image">
								<!-- <span class="hidden-xs">Alexander Pierce</span> -->
						</a>
							<ul class="dropdown-menu">
								<!-- User image -->
								<li class="user-header"><img id = "userImg3"
									src="qt/dist/img/user2-160x160.jpg" class="img-circle"
									alt="User Image">

									<p>
										Alexander Pierce - Web Developer <small>Member since
											Nov. 2012</small>
									</p></li>
								<!-- Menu Body -->
								<li class="user-body">
									<div class="row">
										<div class="col-xs-4 text-center">
											<a href="#">Followers</a>
										</div>
										<div class="col-xs-4 text-center">
											<a href="#">Sales</a>
										</div>
										<div class="col-xs-4 text-center">
											<a href="#">Friends</a>
										</div>
									</div> <!-- /.row -->
								</li>
								<!-- Menu Footer-->
								<li class="user-footer">
									<div class="pull-left">
										<a href="#" class="btn btn-default btn-flat">Profile</a>
									</div>
									<div class="pull-right">
										<a href="#" class="btn btn-default btn-flat">Sign out</a>
									</div>
								</li>
							</ul></li>
						<!-- Control Sidebar Toggle Button -->
						<!-- <li>
            <a href="#" data-toggle="control-sidebar"><i class="fa fa-gears"></i></a>
          </li> -->
					</ul>
				</div>
			</nav>
		</header>

		<aside class="main-sidebar">
			<!-- sidebar: style can be found in sidebar.less -->
			<section class="sidebar">
				<!-- Sidebar user panel -->
				<div class="user-panel">
					<div class="pull-left image" style="padding-left: 15px;">
						<img id="userImg1" style="max-width: 55px;" src="qt/dist/img/user2-160x160.jpg"
							class="img-circle" alt="User Image">
					</div>
					<div id="userInfo" class="pull-left info" style="left: 65px;">
						<!-- <p>刘小雨</p>
						<p>工商银行总行</p> -->
					</div>
				</div>

				<div style="margin-top: 20px;">
					<div id="baoming" style="float: left;">
						<a href="#">
							<div class="box_2"
								style="background-color: rgba(41, 151, 192, 1); width: 44px; height: 44px; border-radius: 24px; margin-left: 20px;">
								<div id="u3273_div" class="" tabindex="0">
									<img id="u3273_img"
										style="width: 24px; height: 24px; margin-left: 10px; margin-top: 10px;"
										class="img" src="./qt/img/u3273.png">
								</div>
								<!-- Unnamed () -->
							</div>
							<div style="width: 100%; margin-top: 5px;">
								<p align="center" style="margin-left: 20px;">
									<span><font color="white">报名</font></span>
								</p>
							</div>
						</a>
					</div>
					<div id="kaoshi" style="float: left;">
						<a href="#">
							<div class="box_2"
								style="background-color: rgba(112, 192, 179, 1); width: 44px; height: 44px; border-radius: 24px; margin-left: 20px;">
								<div id="u3292_div" class="" tabindex="0">
									<img id="u3292_img"
										style="width: 24px; height: 24px; margin-left: 10px; margin-top: 10px;"
										class="img" src="./qt/img/u3292.png">
								</div>
								<!-- Unnamed () -->
							</div>
							<div style="width: 100%; margin-top: 5px;">
								<p align="center" style="margin-left: 20px;">
									<span><font color="white">考试</font></span>
								</p>
							</div>
						</a>
					</div>
					<div id="renzheng" style="float: left;">
						<a href="eti/jsp/rzgj.jsp">
							<div class="box_2"
								style="background-color: rgba(0, 198, 198, 1); width: 44px; height: 44px; border-radius: 24px; margin-left: 20px;">
								<div id="u3294_div" class="" tabindex="0">
									<img id="u3391_img"
										style="width: 24px; height: 24px; margin-left: 10px; margin-top: 10px;"
										class="img" src="./qt/img/u3391.png">
								</div>
								<!-- Unnamed () -->
							</div>
							<div style="width: 100%; margin-top: 5px;">
								<p align="center" style="margin-left: 20px;">
									<span><font color="white">认证</font></span>
								</p>
							</div>
						</a>
					</div>
				</div>

				<div class="user-panel" />
				<!-- 分割线 -->
				<div style="width: 100%; height: 1px; background-color: gray;"></div>
				<ul class="sidebar-menu" id="sdfsdfsf">
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

		<div class="content-wrapper" style="min-height: 916px;">

			<section class="content">
				<div class="row">
					<div class="col-md-8">
																	<!-- /.box -->
						<div class="box">
							<div class="box-header with-border"  style="background-color:#f9f9f9">
								<h3 class="box-title">资格考试项目进展状态</h3>
								<div id="jdtMore" class="text">
									<p>
										<span style="font-family: '微软雅黑';">更多 </span> <span
											style="font-family: '黑体'; color: #FF0000;">></span>
									</p>
								</div>
							</div>
							<!-- /.box-header -->
							<div class="box-body no-padding">
								<div id="jdtTitle" style="height: 50px;">
									<div id="jdtNameT">报考项:</div>
									<div id="jdtName">个人银行专业资格中级</div>
									<div class="jdtNumClass">
										<span id="jdtNum">60</span> <span>%</span>
									</div>
									<br />
								</div>
								<!-- 进度条 -->
								<div id="jdtMain">
									<div id="upDiv">
										<div style="left: -2%">
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
										<div style="left: 55%">
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
								<div style="width:678px;height: 2px;margin: 0 auto;border-bottom: 2px dashed #efefef;"></div>
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
					<div class="panel panel-default" id="todo-panel">
						<div class="panel-heading" style="background-color: transparent">
							<h3 class="panel-title">
								待办 / 提醒 (<span id="todoListSum" style="color:red">0</span>)
								<a href="<%=CONTEXT_PATH%>/qt/jsp/todo.jsp"
								   class="index-list-more-a">
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

					<div class="panel panel-default" id="apply-panel">

						<div class="panel-heading" style="background-color: transparent">
							<h3 class="panel-title">
								可申请的报名 (<span id="keshenqingbaomingSum" style="color:red">0</span>)
								<a href="<%=CONTEXT_PATH%>/ts/jsp/bm.jsp"
								   class="index-list-more-a">
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
									<th class="" style="width: 30%;">报名时间</th>
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
					<div class="col-md-4">
						<div id="announcement-box" class="box">
							<div class="box-header" style="background-color: #f9f9f9;">
								<h3 class="box-title" style="display: block;">
									通知公告
									<a href="/qt/jsp/todo.jsp" class="index-list-more-a">
										更多
										<span style="color:red;">&gt;</span>
									</a>
								</h3>
							</div>
							<!-- /.box-header -->
							<div class="box-body no-padding">
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

						<div class="box">
							<div class="box-header">
								<h3 class="box-title">Q&A 问答</h3>
							</div>
							<!-- /.box-header -->
							<div class="box-body no-padding">
								<table class="table table-striped" id="qItem"></table>
								<div align="center" id="question_div">
									<button type="button" style="width: 40%; margin: 5px;"
										class="btn btn-block btn-success"  >提问</button>
								</div>
							</div>
							<!-- /.box-body -->
						</div>
						<!-- /.box -->
						<div class="box">
						<div class="box-header" icode="ksrl-header">
							<h3 class="box-title">考试日历</h3>
							<p style="float:right;cursor:pointer;">
								<span style="font-family:'微软雅黑';color:#ff0000;">更多</span>
								<span style="font-family:'黑体';color:#ff0000;">></span>
							</p>
						</div>
						<div class="TS_KS_CAL" style="height:250px;">
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
						<div class="box">
							<div class="box-header">
								<h3 class="box-title">问卷调查</h3>
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
						<div class="box">
							<div class="box-header">
								<h3 class="box-title">我的认证轨迹</h3>
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

		<footer class="main-footer">
			<div align="center">
				<strong>中国工商银行考试系统</strong>
			</div>
			<div>
				<table>
					<tr>
						<td width="22%"><span>版权所有：中国工商银行 </span></td>
						<td><span></span></td>
						<td width="25%"><span>地址：北京市复兴门内大街55号</span></td>
						<td><span></span></td>
						<td width="22%"><span>邮编：100140 </span></td>
						<td><span> </span></td>
						<td width="22%" align="right"><span>2017 年 6 月 </span></td>
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
					<form method="post">
						<h3 class="control-sidebar-heading">General Settings</h3>

						<div class="form-group">
							<label class="control-sidebar-subheading"> Report panel
								usage <input type="checkbox" class="pull-right" checked>
							</label>

							<p>Some information about this general settings option</p>
						</div>
						<!-- /.form-group -->

						<div class="form-group">
							<label class="control-sidebar-subheading"> Allow mail
								redirect <input type="checkbox" class="pull-right" checked>
							</label>

							<p>Other sets of options are available</p>
						</div>
						<!-- /.form-group -->

						<div class="form-group">
							<label class="control-sidebar-subheading"> Expose author
								name in posts <input type="checkbox" class="pull-right" checked>
							</label>

							<p>Allow the user to show his name in blog posts</p>
						</div>
						<!-- /.form-group -->

						<h3 class="control-sidebar-heading">Chat Settings</h3>

						<div class="form-group">
							<label class="control-sidebar-subheading"> Show me as
								online <input type="checkbox" class="pull-right" checked>
							</label>
						</div>
						<!-- /.form-group -->

						<div class="form-group">
							<label class="control-sidebar-subheading"> Turn off
								notifications <input type="checkbox" class="pull-right">
							</label>
						</div>
						<!-- /.form-group -->

						<div class="form-group">
							<label class="control-sidebar-subheading"> Delete chat
								history <a href="javascript:void(0)" class="text-red pull-right"><i
									class="fa fa-trash-o"></i></a>
							</label>
						</div>
						<!-- /.form-group -->
					</form>
				</div>
				<!-- /.tab-pane -->
			</div>
		</aside>
		<!-- /.control-sidebar -->
		<!-- Add the sidebar's background. This div must be placed
       immediately after the control sidebar -->
		<div class="control-sidebar-bg"></div>
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
	<script src="<%=CONTEXT_PATH%>/qt/js/wdgl.js"></script>

	<!-- 首页待办/可申请报名js -->
	<script src="<%=CONTEXT_PATH%>/qt/js/index_qt_todo_apply.js"></script>

	<!-- 考试日历模块JS文件 -->
	<script src="<%=CONTEXT_PATH%>/qt/js/jquery-ui-1.10.3.custom.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/js/jquery.ui.datepicker-zh-CN.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/js/ksrl.js"></script>

	<!-- 项目状态进度条展示的js文件 -->
	<script src="<%=CONTEXT_PATH%>/qt/js/xmzt.js"></script>

</body>
</html>
