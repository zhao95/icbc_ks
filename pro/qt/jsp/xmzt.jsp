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
<title>项目状态详情</title>
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">
<!-- 获取后台数据 -->
<%@ include file="/sy/base/view/inHeader.jsp"%>
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
<body class="hold-transition skin-black sidebar-mini">

	<div class="" style="padding: 10px">
		<a href="<%=CONTEXT_PATH%>/index_qt.jsp"><image style="padding-bottom:10px"
				src="<%=CONTEXT_PATH%>/ts/image/u1155.png" id="shouye"></image></a> <span
			style="color: blue; font-size: 20px">&nbsp;&nbsp;/&nbsp;&nbsp;项目进度一览表</span>
	</div>
	<table id="myTab" class="nav nav-tabs"
		style="margin-left: 10px; width: 98%; background-color: white">
		<tr style="height: 70px">
			<td id="keshenqingtd"  class="active"
				style="border-bottom:white solid 1px;width: 50%; text-align: center; font-size: 25px"><image
					style="margin-bottom:10px"
					src="<%=CONTEXT_PATH%>/ts/image/u975.png" id="keshenimage">
				<a id="akeshen" href="#home" data-toggle="tab"><span id="keshen"
					style="color: lightseagreen">进行中的项目</span></a></td>
			<td id="yishenqingtd" class="" style="border-bottom:lightgray solid 1px;width: 50%; text-align: center; font-size: 25px"><image
					style="margin-bottom:10px"
					src="<%=CONTEXT_PATH%>/ts/image/u984.png" id="yishenimage">
				<a id="ayishen" href="#tab2" data-toggle="tab"><span id="yishen"
					style="color: black">已完成的项目</span></a></td>
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
			<div
				style="margin-left: 10px; background-color: white; height: 20px; width: 98%"></div>
			<div id="table1" class="" style="margin-left: 10px; width: 98%">
				<div class="content-main1">
					<table class="rhGrid JPadding JColResizer" id="table">
						<thead id="tem" class="">
							<tr style="backGround-color:WhiteSmoke; height: 30px">
								<th id="BM_XUHAO" class="" style="width: 6.6%; text-align: center">序号</th>
								<th id="BM_NAME" class="" style="width:10%;text-align: center;">名称</th>
								<th id="BM_TYPE" class="" style="width: 10%;text-align: center;">考试类型</th>
								<th id="BM_ODEPT_NAME" class="" style="width: 10%;text-align: center;">组织单位</th>
								<th id="S_ATIME" class="" style="width: 10%;text-align: center;">开始时间</th>
								<th id="S_MTIME" class="" style="width: 10%;text-align: center;">结束时间</th>
								<th id="S_TIME" class="" style="width: 10%;text-align: center;">项目进度</th>
								<th id="BM_STATE_NAME" class="" style="width: 10%;text-align: center;">状态</th>
								<th id="BM_OPTIONS" class="" style="width: 10%; text-align: center;">操作</th>
							</tr>
						</thead>
						<tbody class="">
							<tr class="rhGrid-td-left" style="height: 50px">
								<td class="indexTD" style="text-align: center">1</td>
<!-- 								<td class="rhGrid-td-hide" id="BM_TYPE" ></td> -->
<!-- 								<td class="rhGrid-td-hide" id="BM_ID" ></td> -->
								<td class="rhGrid-td-left " id="BM_NAME"
									style="text-align: center">2017资格考试1</td>
								<td class="rhGrid-td-left " id="BM_ODEPT__NAME"
									style="text-align: center">资格考试</td>
								<td class="rhGrid-td-left " id="BM_ODEPT__NAME"
									style="text-align: center">组织单位</td>
								<td class="rhGrid-td-left " id="S_ATIME"
									style="text-align: center" >2017-8-28</td>
								<td class="rhGrid-td-left " id="S_MTIME"
									style="text-align: center" >2017-8-30</td>
								<td class="rhGrid-td-left " id="S_MTIME"
									style="text-align: center" >60%</td>
								<td class="rhGrid-td-left " id="BM_STATE__NAME"
									style="text-align: center">审核中</td>
								<td id="BM_OPTIONS" style="text-align: center;"><input
									type="button" onclick="tiaozhuan()"
									style="margin:0 auto;display:block;color:white;font-size:15px;background-color:LightSeaGreen;height:35px;width:80px"
									value="首页显示"></input></td>
							</tr>
							<tr class="rhGrid-td-left" 
								style="height: 50px">
								<td class="indexTD" style="text-align: center">1</td>
<!-- 								<td class="" id="BM_TYPE" ></td> -->
<!-- 								<td class="" id="BM_ID" ></td> -->
								<td class=" " id="BM_NAME"
									style="text-align: center">2017资格考试2</td>
								<td class=" " id="BM_ODEPT__NAME"
									style="text-align: center">资格考试</td>
								<td class=" " id="BM_ODEPT__NAME"
									style="text-align: center">组织单位</td>
								<td class=" " id="S_ATIME"
									style="text-align: center" >2017-4-28</td>
								<td class=" " id="S_MTIME"
									style="text-align: center" >2017-5-30</td>
								<td class=" " id="S_MTIME"
									style="text-align: center" >30%</td>
								<td class=" " id="BM_STATE__NAME"
									style="text-align: center">报名中</td>
								<td id="BM_OPTIONS" style="text-align: center"><input
									type="button" onclick="tiaozhuan()"
									style="margin:0 auto;display:block;color:white;font-size:15px;background-color:LightSeaGreen;height:35px;width:80px"
									value="已显示"></input></td>
							</tr>
							
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
                 
			<div id ="qihuan" style="margin-top:20px">
			<div id="table2" class="" style="margin-top:-15px;margin-left: 10px; width: 98%;">
				<div class="content-main2" style="position:relative;">
					<table id="ybmtable" class="rhGrid JPadding JColResizer">
						<thead id="tem" class="">
							<tr style="backGround-color:WhiteSmoke; height: 30px">
								<th id="BM_XUHAO" class="" style="width: 6.6%; text-align: center">序号</th>
								<th id="BM_NAME" class="" style="width:10%;text-align: center;">名称</th>
								<th id="BM_TYPE" class="" style="width: 10%;text-align: center;">考试类型</th>
								<th id="BM_ODEPT_NAME" class="" style="width: 10%;text-align: center;">组织单位</th>
								<th id="S_ATIME" class="" style="width: 10%;text-align: center;">开始时间</th>
								<th id="S_MTIME" class="" style="width: 10%;text-align: center;">结束时间</th>
								<th id="BM_STATE_NAME" class="" style="width: 10%;text-align: center;">状态</th>
								<th id="BM_OPTIONS" class="" style="width: 10%; text-align: center">操作</th>
							</tr>
						</thead>
						<tbody>
							<tr class="rhGrid-td-left" style="height: 50px">
								<td style="text-align: center">1</td>
								<td style="text-align: center">2017资格考试</td>
								<td style="text-align: center">资格类考试</td>
								<td style="text-align: center">一级分行</td>
								<td style="text-align: center">2017-8-28</td>
								<td style="text-align: center">2017-9-28</td>
								<td style="text-align: center">提交审核</td>
								<td><a href="#" style="color:lightseagreen" id="chakan">查看</a>&nbsp;&nbsp;</td>
								<td class="rhGrid-td-hide" id="baomingid"></td>
							</tr>
							<tr class="rhGrid-td-left" style="height: 50px">
								<td style="text-align: center">1</td>
								<td style="text-align: center">2016资格考试</td>
								<td style="text-align: center">资格类考试</td>
								<td style="text-align: center">一级分行</td>
								<td style="text-align: center">2017-8-28</td>
								<td style="text-align: center">2017-9-28</td>
								<td style="text-align: center">提交审核</td>
								<td><a href="#" style="color:lightseagreen" id="chakan">查看</a>&nbsp;&nbsp;</td>
								<td class="rhGrid-td-hide" id="baomingid"></td>
							</tr>
							<tr class="rhGrid-td-left" style="height: 50px">
								<td style="text-align: center">1</td>
								<td style="text-align: center">2015资格考试</td>
								<td style="text-align: center">资格类考试</td>
								<td style="text-align: center">一级分行</td>
								<td style="text-align: center">2017-8-28</td>
								<td style="text-align: center">2017-9-28</td>
								<td style="text-align: center">提交审核</td>
								<td><a href="#" style="color:lightseagreen" id="chakan">查看</a>&nbsp;&nbsp;</td>
								<td class="rhGrid-td-hide" id="baomingid"></td>
							</tr>
							
							
							
						</tbody>
					</table>
			
			<div id="fenyediiv" style="position:absolute;right:5%;bottom:-20;">
			<table class="row">
			<tr>
			<td><ul id="fenyeul" class="pagination">
		    <li><a href="#">&laquo;</a><li ><a  href="#">&raquo;</a></li>
		    </ul>
		    </td>
		    <td style="width:5%"></td>
			</tr>
			</table> 
			</div>
			</div>
			</div>
		
			</div>
			<form id="form1" style="display:none" method="post" action="bmzgks.jsp">
				<input id = "zgtz" name="zgtz"></input>
				<input type="submit" name="Submit" value="提交">
			</form>
			<form id="form2" style="display:none" method="post" action="bmglf.jsp">
				<input id = "fzgtz" name="fzgtz"></input>
			<input type="submit" name="Submit" value="提交">
			</form>
		</div>
		
		<input type="hidden" id="xmid"/>
		<input type="hidden" id="dijige">
	</div>
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
