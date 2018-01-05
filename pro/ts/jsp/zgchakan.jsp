<!DOCTYPE html>
<%@page import="com.rh.core.serv.ServDao"%>
<%@page import="com.rh.core.org.mgr.UserMgr"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	final String CONTEXT_PATH = request.getContextPath();
%>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>资格报名列表</title>
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">
<%@ include file="../../qt/jsp/header-logo.jsp"%> 
<%@ include file="/sy/base/view/inHeader-icbc.jsp"%>
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
	<style>.a table tr{height:40px;padding-left: 10px;}</style>
	<style>.a table td{padding-left: 20px;}</style>
	<style>.b table tr{height:30px;}</style>
	<style>.zgks table td{padding-left: 5px;}</style>
	<% 

		 String bmid = request.getParameter("bmid4");
		 String shstates = request.getParameter("shstate");
		//获取用户编码
		String user_code = userBean.getStr("USER_CODE");
		//获取用户名称
		String user_name = userBean.getStr("USER_NAME");
		//获取用户性别
		String user_sex = userBean.getStr("USER_SEX");
		//获取用户机构
		Bean bmbean = ServDao.find("TS_BMLB_BM", bmid);
		
		DeptBean deptbean= OrgMgr.getDept(bmbean.getStr("S_ODEPT"));
		String odept_name = deptbean.getName();
		/* String odept_name = userBean.getODeptName(); */
		//获取用户办公电话
		String user_office_phone = userBean.getStr("USER_OFFICE_PHONE");
		//获取用户手机号码
		String user_mobile = userBean.getStr("USER_MOBILE");
		//获取用户入行时间
		String user_cmpy_date =userBean.getStr("USER_CMPY_DATE");
		//职务
		String user_post =userBean.getStr("USER_POST");
		String wheregw = "AND POSTION_NAME="+"'"+user_post+"'";
		List<Bean> gwList = ServDao.finds("TS_ORG_POSTION", wheregw);
		String pt_type="";
		String pt_sequnce="";
		if(gwList.size()!=0){
		 pt_type=gwList.get(0).getStr("POSTION_TYPE");
		 pt_sequnce= gwList.get(0).getStr("POSTION_SEQUENCE");
		}
		
		%>
	
	<div style="background: #dfdfdf;padding-top: 10px"align="center">
       	<div id="" style="background: white;width: 90%;">
       		<div style="background: white;width: 90%;text-align: center">
       		<table  style="height: 50px;width: 90%;">
       			<tr>
       				<td><span id="xmname" style="font-size: 25px;color: #00C2C2;"></span></td>
       			</tr>
       		</table>
       		</div>
       	<div class="a">
       		<table  align="center" style="width: 90%;">
       			<tr>
       				<td colspan="4" height="120px"><p style="font-size: 15px;color:red;">报考须知，请仔细阅读！</p>
       				<p style="color: red;" id="bmtime"> </p>
       				<p style="color: red;" id="ksxzs"></p></td>
       			</tr>
       		</table>
       		<table  align="center" style="width: 90%;background-color: #fed1d1;">
       			<tr>
       				<td><span style="color: #ff0000;">！</span> 温馨提示：</td>
       				<td height="60px" align="left">
       				您当前在 <span style="color: #ff0000;"><%=odept_name%></span> ，将视您办公所在地统一安排考场。如果您发现下面的信息不符，<br>请于借考申请开放期间提交借考申请！
       				</td>
       			</tr>
       		</table>
       		<div style="padding-top: 10px;">
       		<table border="1" align="center" style="width: 90%;padding-top: 10px;">
       			<tr style="background-color: #f2f2f2;">
       				<td colspan="6">个人基本信息</td>
       			</tr>
       			<tr>
       				<td width="16.5%">人力资源编码</td>
       				<td width="16.5%" ><%=user_code %></td>
       				<td width="16.5%">姓名</td>
       				<td width="16.5%"><%=user_name%></td>
       				<td width="16.5%">性别</td>
       				<td width="17.5%">
       				<% if (user_sex == "1") { %>女<% } else { %>男<% } %>
       				</td>
       			</tr>
       			<tr style="background-color: #f7fdff;">
       				<td >所属机构</td>
       				<td colspan="5"><%=odept_name %></td>
       			</tr>
       			<tr>
       				<td width="16.5%">岗位类别</td>
       				<td width="16.5%" id="gwlb"></td>
       				<td width="16.5%">岗位序列</td>
       				<td width="16.5%" id="gwxl"></td>
       				<td width="16.5%">职务层级</td>
       				<td width="17.5%" id="zwcj"></td>
       			</tr>
       			<tr style="background-color: #f7fdff;">
       				<td width="16.5%">入行时间</td>
       				<td width="16.5%"><%=user_cmpy_date%></td>
       				<td width="16.5%">办公电话</td>
       				<td width="16.5%"><%=user_office_phone %></td>
       				<td width="16.5%">手机号码</td>
       				<td width="17.5%"></td>
       			</tr>
       		</table>
       		</div>
       		<div style="padding-top: 10px;">
       					<div style="padding-top: 10px;text-align: left;width:90%;"><span style="color: #ff0000">★ 已报名的考试</span>
       					<span style="color: #fdb64f;">(提示：)</span></div>
       					<table border="1" align="center" style="width: 90%;" id="tableid">
       					<thead>
       						<tr style="background-color: #ffbdbd;">
	       						<td width="10%">岗位类别</td>
       							<td width="15%">序列</td>
       							<td width="27%">模块</td>
       							<td width="10%">级别</td>
       							<td width="15%">验证结果</td>
       						</tr>
       							</thead>
       							<tbody>
	      						
       						</tbody>
       					</table>
       					</div>
       					<div style="padding-top:20px;padding-bottom:20px">
       			</div>
       		</div>
       	</div>
       	<input type="hidden" id="prbmid" value="<%=bmid %>">
       		<input type="hidden" id="shstatus" value="<%=shstates %>">
       	</div>
	<script src="<%=CONTEXT_PATH%>/ts/js/zgchakan.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/js/index_qt.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/plugins/jQuery/jquery-2.2.3.min.js"></script>
	<!-- Bootstrap 3.3.6 -->
	<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/bootstrap.min.js"></script>
	<!-- FastClick -->
	<script src="<%=CONTEXT_PATH%>/qt/plugins/fastclick/fastclick.js"></script>
	<!-- AdminLTE App -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/app.min.js"></script>
	<!-- AdminLTE for demo purposes -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/demo.js"></script>
	<script type="text/javascript">
	function goBack(){
		window.history.go(-1);
	}
	$(function(){
		init();
		debugger;
		var user_code = "<%=user_code%>"
		var result =  FireFly.byId("SY_HRM_ZDSTAFFPOSITION",user_code);
		if(result!=null){
			 STATION_TYPE_CODE=result.STATION_TYPE_CODE;
			 STATION_TYPE = result.STATION_TYPE;
			 STATION_NO = result.STATION_NO;
			 STATION_NO_CODE= result.STATION_NO_CODE;
			 ADMIN_DUTY = result.ADMIN_DUTY;
			 $("#gwlb").html(STATION_TYPE);
			 $("#gwxl").html(STATION_NO);
			 $("#zwcj").html(ADMIN_DUTY);
		}
	});
	</script>
	
</body>
</html>
