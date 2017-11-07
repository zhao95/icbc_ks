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
<title>工商银行考试系统首页</title>
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
						<li id="btnToHT" style="display:none"><a href="#" onclick="window.open('/sy/comm/page/page.jsp?rhDevFlag=true','_blank');" style="height: 60px;border-left: 0px;">
    						<img title="进入后台管理" src="qt/css/images/to_ht.png" style="cursor: pointer;">
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
							<img id= "userImg2" onerror="this.src='/sy/theme/default/images/common/user0.png'" 
								class="user-image"
								alt="User Image" style="margin-right: 2px;">
								<!-- <span class="hidden-xs">Alexander Pierce</span> -->
						</a>
							<ul class="dropdown-menu" style="width:160px;">
<!-- 							<li class="user-header"></li> -->
<!-- 							<li class="user-body"></li> -->
							<li><a href="#" class="" id="TipUserInfo"><i class="fa fa-user-o fa-fw"></i>个人信息</a></li>
								<li><a href="#" class="" data-layout="layout-boxed"><i class="fa fa-cog fa-fw"></i>简约/平铺</a></li>
<!-- 								<li><input type="checkbox" data-layout="layout-boxed" class="pull-right" checked="checked">简约/平铺</li> -->
								<li><a  class="" id="loginOutBtn" style="cursor:pointer;"><i class="fa fa-power-off fa-fw"></i>退出系统</a></li>

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
						<img id="userImg1" style="min-width: 64px; max-width: 65px;"  onerror="this.src='/sy/theme/default/images/common/user_64_64.png'" class="img-circle" alt="User Image">
<!-- 						<img id="userImg1" style="max-width: 64px;" src="/sy/theme/default/images/common/user_64_64.png" onerror="this.src='/sy/theme/default/images/common/user_64_64.png'" class="img-circle" alt="User Image"> -->
					</div>
					<div id="userInfo" class="text-center" style="line-height: 32px;height: 64px;margin-left: 74px;">
						<!-- <p>刘小雨</p>
						<p>工商银行总行</p> -->
					</div>
				</div>

				<div style="margin-top: 20px; text-align:center;">

					<img  id="left-img-renzheng"  onclick="window.open('/eti/jsp/rzgj.jsp')" src="/qt/img/left-logo.png" style="cursor: pointer;">
				</div>

				<div class="user-panel" />
				<!-- 分割线 -->
				<div style="width: 100%; height: 5px; background-color: #063c5d;position: absolute;top:12px;left: 0px;"></div>
				<ul class="sidebar-menu" id="sdfsdfsf" style="position: relative;top: 15px;">
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
									<span style="background: url(/qt/img/more_12x11.png) no-repeat;width: 12px;height: 11px;top: 30%;right: -15px;position: absolute;"></span>
									</a>
								</h3>
<!-- 								<div style="width: 160px;height: 3px;position: relative;top: 11px;left: 0px;background-color: #ff0000;"></div> -->
							</div>
							<!-- /.box-header -->
							<div class="box-body no-padding">
								<div id="jdtTitle" style="height: 50px;line-height:50px;">
									<div id="jdtNameT">报考项:</div>
									<div id="jdtName"></div>
									<div class="jdtNumClass">
										<span id="jdtNum"></span> <span style="color:#666666;">%</span>
									</div>
									<br />
								</div>
								<!-- 进度条提示信息框 -->
								<div id="jdtMsg"></div>
								<!-- 进度条 -->
								<div id="jdtMain">
									
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
									</div>
									
									<div id="upDiv">
										<div style="left: -3%">
											报名
											<div class="upSX"></div>
										</div>
										<div style="left: 12.5%">
											审核
											<div class="upSX"></div>
										</div>
										<div style="left: 26%">
											考场安排
											<div class="upSX"></div>
										</div>
										<div style="left: 44%">
											考试
											<div class="upSX"></div>
										</div>
										<div style="left: 60%">
											阅卷
											<div class="upSX"></div>
										</div>
										<div style="left: 75.7%">
											认证
											<div class="upSX"></div>
										</div>
										<div style="left: 92%">
											完成
											<div class="upSX"></div>
										</div>
									</div>
									
								</div>
							</div>
							<!-- /.box-body -->
						</div>
						<!-- /.box-div -->
						<!-- /.box -->

					<!-- 可参加的考试 -->
					<div class="panel panel-default box" style=" border-top: 1px solid #d2d6de;">
						<div class="panel-heading qt_ks_title" style="padding:5px 15px;">
							<ul class="panel-title" id="ksTable">
								<li class="active"><a href="#tab-zgks" data-toggle="tab" aria-expanded="true">资格考试(<span id=""  >6</span>) </a></li><li style="color:#e2e4e7"> |  </li>
								<li ><a href="#tab-zsjs" data-toggle="tab" aria-expanded="true">知识竞赛(<span id="" >3</span>) </a> </li><li style="color:#e2e4e7">|  </li>
								<li ><a href="#tab-qtks" data-toggle="tab" aria-expanded="true">其他考试(<span id="" >6</span>) </a></li>
								
								<li class="index-list-more-a" >
									更多
									<a style="background: url(/qt/img/more_12x11.png) no-repeat;width: 12px;height: 11px;top: 30%;right: -15px;position: absolute;"></a>
								</li>
							</ul>
						</div>
						<div class="panel-body">
						
							<div class="tab-pane fade active in" id="tab-zgks" style="position: absolute;">
							<table>
								<thead class="">
								<tr style="backGround-color:WhiteSmoke; height: 30px">
									<th class="" style="width: 40%;">试卷名称</th>
									<th class="" style="width: 30%;">考场名称</th>
									<th class="" style="width: 189px;">考试时间</th>
								</tr>
								</thead>
								<tbody class="grid-tbody">
								<tr>
									<td >2017年（第8周）资格考试</td>
									<td >中国工商银行总行</td>
									<td>2017-05-07  － 2017-05-16 </td>
								</tr>
								<tr>
									<td >2017年（第8周）资格考试</td>
									<td >北京分行</td>
									<td>2017-05-07  － 2017-05-16 </td>
								</tr>
								</tbody>
							</table>
							</div>
							
							<div class="tab-pane fade" id="tab-zsjs" style="position: absolute;">
							<table  >
								<thead class="">
								<tr style="backGround-color:WhiteSmoke; height: 30px">
									<th class="" style="width: 40%;">试卷名称</th>
									<th class="" style="width: 30%;">考场名称</th>
									<th class="" style="width: 189px;">考试时间</th>
								</tr>
								</thead>
								<tbody class="grid-tbody">
								<tr>
									<td >信息技术知识竞赛</td>
									<td >中国工商银行总行</td>
									<td>2017-05-07 － 2017-05-16 </td>
								</tr>
								<tr>
									<td >2017综合知识竞赛</td>
									<td >北京分行</td>
									<td>2017-05-07  － 2017-05-16 </td>
								</tr>
								</tbody>
							</table>
							</div>
							
							<div class="tab-pane fade" id="tab-qtks">
							<table>
								<thead class="">
								<tr style="backGround-color:WhiteSmoke; height: 30px">
									<th class="" style="width: 40%;">试卷名称</th>
									<th class="" style="width: 30%;">考场名称</th>
									<th class="" style="width: 189px;">考试时间</th>
								</tr>
								</thead>
								<tbody class="grid-tbody">
								<tr>
									<td >岗位培训结业考试</td>
									<td >中国工商银行总行</td>
									<td>2017-05-07  － 2017-05-16 </td>
								</tr>
								<tr>
									<td >消防安全知识考试</td>
									<td >北京分行</td>
									<td>2017-05-07  － 2017-05-16 </td>
								</tr>
								</tbody>
							</table>
							</div>
						</div>
					</div>

					<!-- 可申请的报名 -->
					<div class="panel panel-default box" id="apply-panel" style="border-top: 1px solid #d2d6de;">
						<div class="panel-heading" style="background-color: #f9f9f9">
							<h3 class="panel-title">
								可申请的报名 (<span id="keshenqingbaomingSum" style="color:red">0</span>)
								<a onclick="window.open('/ts/jsp/bm.jsp')" class="index-list-more-a">
									更多
									<span style="background: url(/qt/img/more_12x11.png) no-repeat;width: 12px;height: 11px;top: 30%;right: -15px;position: absolute;"></span>
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
<!-- 								<tr> -->
<!-- 									<td >2017年（第8周）资格考试报名</td> -->
<!-- 									<td >中国工商银行总行</td> -->
<!-- 									<td>2017-05-07 00:00 － 05-16 00:00</td> -->
<!-- 								</tr> -->
<!-- 								<tr> -->
<!-- 									<td >2017年非资格考试报名</td> -->
<!-- 									<td >北京分行</td> -->
<!-- 									<td>2017-05-07 00:00 － 05-16 00:00</td> -->
<!-- 								</tr> -->
								</tbody>
							</table>
						</div>
					</div>


					<%--待办/提醒--%>
					<div class="panel panel-default box" id="todo-panel" style="border-top: 1px solid #d2d6de;">
						<div class="panel-heading" style="background-color: #f9f9f9">
							<h3 class="panel-title">
								待办 / 提醒 (<span id="todoListSum" style="color:red">0</span>)
								<a onclick="window.open('/qt/jsp/todo.jsp')" class="index-list-more-a">
									更多
									<span style="background: url(/qt/img/more_12x11.png) no-repeat;width: 12px;height: 11px;top: 30%;right: -15px;position: absolute;"></span>
								</a>
							</h3>
							<div style="width: 109px;height: 3px;position: relative;top: 11px;left: -5px;background-color: #ff0000;">
							</div>
						</div>
						<div class="panel-body">

							<div class="index-qt-todo-list">
								<div style="" class="todo-item">
									<div style="" class="">
										<div ></div>
									</div>
									<div class="todo-content">
										<div></div>
										<div>
										</div>
									</div>
								</div>
							</div>
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
									<a onclick="window.open('/qt/jsp/ggList.jsp')" class="index-list-more-a">
										更多
										<span style="background: url(/qt/img/more_12x11.png) no-repeat;width: 12px;height: 11px;top: 30%;right: -15px;position: absolute;"></span>
									</a>
								</h3>
							</div>
							<!-- /.box-header -->
							<div class="box-body no-padding" style="margin-bottom: 10px; min-height: 180px;">
								<table class="table no-border">
								<tbody></tbody>
								</table>
							</div>
							<!-- /.box-body -->
						</div>
						<!-- /.box -->

						<div class="panel panel-default box" style="border-top: 1px solid #d2d6de;">
							<div class="box-header" style="background-color: #f9f9f9;">
								<h3 class="box-title" style="font-size:16px;">Q&A 问答</h3>
							</div>
							<div style="min-height:64px;"></div>
							<!-- /.box-header -->
<!-- 							<div class="box-body no-padding"> -->
							<div class="box-body">
								<table class="table table-striped" id="qItem"></table>
								<div align="center" id="question_div" style="/* padding-bottom:10px; */">
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
									<span style="background: url(/qt/img/more_12x11.png) no-repeat;width: 12px;height: 11px;top: 30%;right: -15px;position: absolute;"></span>
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
						<div class="panel panel-default box" style="border-top: 1px solid #d2d6de;margin-top: 15px;">
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
<!-- 			<div align="center" style="color: white;font-size: 20px;"> -->
<!-- 				<strong>中国工商银行考试系统</strong> -->
<!-- 			</div> -->
			<div>
				<table>
					<tr id="qt_footer_tr" style="width: 90%;">
						<td width="20%"><span>版权所有：中国工商银行 </span></td>
						<td><span>|</span></td>
						<td width="30%"><span>地址：北京市复兴门内大街55号</span></td>
						<td><span></span></td>
						<td width="16%"><span>邮编：100140 </span></td>
						<td><span>|</span></td>
						<td width="14%" align="right"><span>2017 年 12 月 </span></td>
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
