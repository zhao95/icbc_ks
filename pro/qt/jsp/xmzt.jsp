<%@page import="com.rh.core.serv.OutBean"%>
<%@page import="javax.swing.text.StyledEditorKit.ForegroundAction"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
	final String CONTEXT_PATH = request.getContextPath();
%>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>项目进度一览表</title>
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">
<!-- 获取后台数据 -->
<%@ include file="../../sy/base/view/inHeader.jsp"%>
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
	 <link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/bootstrap/js/site.css">
<!--工具方法--> 
<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/global.js"></script> 
<!--插件--> 
<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/jquery.smart-form.js"></script> 
 <style>
        /*tab标签页样式*/
        #myTab .nav-name {
            font-size: 25px;
            color: black;
        }

        #myTab .active .nav-name {
            color: lightseagreen;
        }

        #myTab .active div {
            border: none;
            border-bottom: 5px solid LightSeaGreen;
        }

        #myTab .active .todo-nav-tab-item {
            border: none;
            border-bottom: 5px solid LightSeaGreen;
        }

        #myTab .todo-nav-tab-item {
            width: 200px;
            text-align: center;
            cursor: pointer;
        }
</style>


<body class="hold-transition skin-black sidebar-mini">
<%@ include file="header-logo.jsp"%>
	<div class="" style="padding: 10px">
		<a href="<%=CONTEXT_PATH%>/index_qt.jsp"><img style="padding-bottom: 10px ;color: #388CAE;" src="<%=CONTEXT_PATH%>/ts/image/Home_16x16.png" id="shouye"></a>
		 <span style="color: #909090;font-size: 16px;">&nbsp;&nbsp;/&nbsp;&nbsp;项目进度一览表</span>
	</div>
	
	<table id="myTab" class="nav nav-tabs " style="margin-left: 10px; width: 98%; background-color: white">
		<tr style="height: 70px">
			<td id="keshenqingtd"  class="active" style="border-bottom:white solid 1px;width: 50%; text-align: center; font-size: 25px">
				<image style="margin-bottom:10px" src="<%=CONTEXT_PATH%>/ts/image/u975.png" id="keshenimage">
				<a id="akeshen" href="#home" data-toggle="tab"><span id="keshen" style="color: lightseagreen" class="nav-name">进行中的项目</span></a>
			</td>
			<td id="yishenqingtd" class="" style="border-bottom:lightgray solid 1px;width: 50%; text-align: center; font-size: 25px">
				<image style="margin-bottom:10px" src="<%=CONTEXT_PATH%>/ts/image/u984.png" id="yishenimage">
				<a id="ayishen" href="#tab2" data-toggle="tab"><span id="yishen" style="color: black" class="nav-name">已完成的项目</span></a>
			</td>
		</tr>
	</table>
	<div id="myTabContent" class="tab-content">
		<div class="tab-pane fade in active" id="home">
			<div
				style="margin-top: -5px; margin-left: 19%; height: 5px; width: 190px; background-color: LightSeaGreen"></div>
			<div id="cuxian1"
				style="margin-left: 10px; margin-top: 20px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
				<span style="margin-left: 50px; padding-top: 10px">进行中的项目</span>
			</div>
			<div style="margin-left: 10px; background-color: white; height: 20px; width: 98%"></div>
			<div id="table1" class="" style="margin-left: 10px; width: 98%">
				<div class="content-main1">
					<table class="rhGrid JPadding JColResizer" id="table1">
						<thead id="tem" class="">
							<tr style="backGround-color:WhiteSmoke; height: 30px">
								<th id="XM_XUHAO" class="" style="width: 6.6%; text-align: center">序号</th>
								<th id="XM_NAME" class="" style="width:10%;text-align: center;">名称</th>
								<th id="XM_TYPE" class="" style="width: 10%;text-align: center;">考试类型</th>
								<th id="XM_ODEPT_NAME" class="" style="width: 10%;text-align: center;">组织单位</th>
								<th id="XM_START" class="" style="width: 10%;text-align: center;">开始时间</th>
								<th id="XM_END" class="" style="width: 10%;text-align: center;">结束时间</th>
								<th id="XM_JD" class="" style="width: 10%;text-align: center;">项目进度</th>
								<th id="XM_STATE_NAME" class="" style="width: 10%;text-align: center;">状态</th>
								<th id="XM_OPTIONS" class="" style="width: 10%; text-align: center;">操作</th>
							</tr>
						</thead>
						<tbody  id ="table1_tbody" class="">
							
						</tbody>
					</table>
				</div>
		</div>
		</div>
		<div class="tab-pane fade"  id="tab2">
			<div
				style="margin-top: -6px; margin-left: 68%; height: 5px; width: 190px; background-color: LightSeaGreen"></div>
			<div id="cuxian2"
				style="margin-left: 10px; margin-top: 20px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
				<span style="margin-left: 50px; padding-top: 10px">已完成的项目</span>
			</div>
                 
			<div id ="qihuan" style="margin-top:40px">
			<div id="table2" class="" style="margin-top:-15px;margin-left: 10px; width: 98%;">
				<div class="content-main2" style="position:relative;">
					<table id="ybmtable" class="rhGrid JPadding JColResizer">
						<thead id="tem" class="">
							<tr style="backGround-color:WhiteSmoke; height: 30px">
								<th id="XM_XUHAO" class="" style="width: 6.6%; text-align: center">序号</th>
								<th id="XM_NAME" class="" style="width:10%;text-align: center;">名称</th>
								<th id="XM_TYPE" class="" style="width: 10%;text-align: center;">考试类型</th>
								<th id="XM_ODEPT_NAME" class="" style="width: 10%;text-align: center;">组织单位</th>
								<th id="XM_START" class="" style="width: 10%;text-align: center;">开始时间</th>
								<th id="XM_END" class="" style="width: 10%;text-align: center;">结束时间</th>
								<th id="XM_JD" class="" style="width: 10%;text-align: center;">项目进度</th>
								<th id="BM_STATE_NAME" class="" style="width: 10%;text-align: center;">状态</th>
<!-- 								<th id="BM_OPTIONS" class="" style="width: 10%; text-align: center">操作</th> -->
							</tr>
						</thead>
						<tbody id="table2_tbody">
							
						</tbody>
					</table>
			
<!-- 			<div id="fenyediiv" style="position:absolute;right:5%;bottom:-20;"> -->
<!-- 			<table class="row"> -->
<!-- 			<tr> -->
<!-- 			<td><ul id="fenyeul" class="pagination"> -->
<!-- 		    <li><a href="#">&laquo;</a><li ><a  href="#">&raquo;</a></li> -->
<!-- 		    </ul> -->
<!-- 		    </td> -->
<!-- 		    <td style="width:5%"></td> -->
<!-- 			</tr> -->
<!-- 			</table>  -->
<!-- 			</div> -->
			</div>
			</div>
		
			</div>
		</div>
		
<!-- 		<input type="hidden" id="xmid"/> -->
<!-- 		<input type="hidden" id="dijige"> -->
	</div>
<%-- 	<script src="<%=CONTEXT_PATH%>/qt/js/index_qt.js"></script> --%>
	<script src="<%=CONTEXT_PATH%>/qt/plugins/jQuery/jquery-2.2.3.min.js"></script>
	<!-- Bootstrap 3.3.6 -->
	<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/bootstrap.min.js"></script>
	<!-- FastClick -->
	<script src="<%=CONTEXT_PATH%>/qt/js/xmzt_info.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/plugins/fastclick/fastclick.js"></script>
	<!-- AdminLTE App -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/app.min.js"></script>
	<!-- AdminLTE for demo purposes -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/demo.js"></script>
</body>
</html>
