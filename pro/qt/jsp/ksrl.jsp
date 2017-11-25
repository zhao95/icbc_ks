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
<title>考试日历详情</title>
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
<%@ include file="header-logo.jsp"%>
	<!-- 首页/返回 -->
	<div class="" style="padding: 10px">
		<a href="<%=CONTEXT_PATH%>/index_qt.jsp"><img style="padding-bottom: 10px ;color: #388CAE;" src="<%=CONTEXT_PATH%>/ts/image/Home_16x16.png" id="shouye"></a> <span style="color: #909090;font-size: 16px;">&nbsp;&nbsp;/&nbsp;&nbsp;考试日历详情</span>
	</div>
	<!-- tab标签  主标题 考试详情 -->
	<div>
		<table id="myTab" class="nav nav-tabs"
		style="margin-left: 10px; width: 98%; background-color: white">
	</table>
	</div>

	<!-- 显示主模块 -->
	<div id="myTabContent" class="tab-content">
		<div class="tab-pane fade active in" style="position:relative;" id="home">
			<div style="margin-top: -5px; margin-left: 100%; height: 5px; width: 20%; background-color: LightSeaGreen"></div>
				<div id="cuxian1" style="margin-left: 10px; margin-top: 0px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
				<span style="margin-left: 50px; padding-top: 10px" id="initData">考试相关安排</span>
			</div>
			<!-- 查询框 -->
			<table id="cxkuang" style="margin-top:20px">
				<tbody>
					<tr style="height:20px">
						<td style="width:30px;text-align:center">考试名称&nbsp;&nbsp;<input style="height:30px;width:50%" id="ks_name" type="text"></td>
						<td style="width:20%;text-align:center">考试年份&nbsp;&nbsp;<input style="height:30px;width:50%" id="ks_year" type="text"></td>
				        <td style="width:12%;"><button style="border:none;color:white;height:30px;width:35%;background:DarkTurquoise"  id="search">查询</button></td>
					</tr>
				</tbody>
			</table>
			<!-- 分割线 -->
			<div id="xixian" style="margin-left: 10px; margin-top: 20px; background-color: lightgray; height:1px; width: 98%">
			</div>
			<!-- 考试信息主要数据展示区域 -->
			<div id="kstable_div" class="" style="margin-top:20px;margin-left: 10px; width: 98%; margin:0 auto;text-align: center;">
					<table class="rhGrid JPadding JColResizer" id="kstable" border="0">
						<thead id="tem" class="">
							<tr style="backGround-color:WhiteSmoke; height: 30px; font-size:14px;">
								<th style="width: 3%; text-align: center">序号</th>
								<th style="width: 30%;	text-align: center">考试名称</th>
								<th style="width: 8%;	text-align: center">考试类别</th>
								<th style="width: 8%;	text-align: center">考试级别</th>
								<th style="width: 10%; text-align: center">报名开始时间</th>
								<th style="width: 10%; text-align: center">报名结束时间</th>
								<th style="width: 10%; text-align: center">考试开始时间</th>
								<th style="width: 10%; text-align: center">考试结束时间</th>
								<th style="width: 10%; text-align: center">备注</th>
							</tr>
						</thead>
					<tbody id="tbody_data" ></tbody>
					</table>
					
					<!-- 分页展示 -->
					<div class="rhGrid-page">
		            <span class="disabled ui-corner-4">上一页</span>
		            <span class="current ui-corner-4">1</span>
		            <span class="disabled ui-corner-4">下一页</span>
		            <span class="allNum">共15条</span>
        </div>
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
	<!-- 	考试详情展示JS引入 -->
	<script src="<%=CONTEXT_PATH%>/qt/js/ksrl_info.js"></script>	
	
</body>

<script type="text/javascript">

</script>
</html>