<!DOCTYPE html>
<%@page import="com.rh.core.serv.ServDao"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	final String CONTEXT_PATH = request.getContextPath();
%>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>代办事务</title>
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">

<%@ include file="/sy/base/view/inHeader.jsp"%>
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
	<% 
	String user_code = userBean.getStr("USER_CODE");
	//获取用户名称
	String user_name = userBean.getStr("USER_NAME");
	//获取用户性别
	String user_sex = userBean.getStr("USER_SEX");
	//获取用户机构
	String dept_name = userBean.getStr("DEPT_NAME");
	//获取用户办公电话
	String user_office_phone = userBean.getStr("USER_OFFICE_PHONE");
	//获取用户手机号码
	String user_mobile = userBean.getStr("USER_MOBILE");
	//获取用户入行时间
	String user_cmpy_date =userBean.getStr("USER_CMPY_DATE");
	
	%>
			<div class="box">
				<div class="box-header with-border">
					<h3 class="box-title">待办 / 提醒 (4)</h3>
				</div>
				<!-- /.box-header -->
				<div class="box-body">
					<table class="table table-bordered" id="tableid">
						<%
					List<Bean> dbList = ServDao.finds("TS_COMM_TODO","");
					for(int i=0;i<dbList.size();i++){
						Bean bean1 = dbList.get(i);
						String todo_id = bean1.getStr("TODO_ID");
						String db_type = bean1.getStr("TYPE");
						String db_title = bean1.getStr("TITLE");
						String data_id = bean1.getStr("DATA_ID");
						String send_name = bean1.getStr("SEND_NAME");
						String send_time = bean1.getStr("SEND_TIME");
					%>
					<tr style="height: 40px">
						<td class="rhGrid-td-hide"><%=todo_id%></td>
						<td class="rhGrid-td-hide"><%=data_id%></td>
						<td ><%=db_type%></td>
						<td ><%=db_title%></td>
						<td ><%=send_name%></td>
						<td ><%=send_time%></td>
					</tr>
					<%
						}
					%>
					</table>
				</div>
				<!-- /.box-body -->
			</div>
						<!-- /.box -->
<form action="qjlb_qj2.jsp" method="post" id="formchakan" style="display: none;">
		<input type = "text" id="qjid" name="qjid" value=""/>
		<input type = "text" id="todoid" name="todoid" value=""/>
		<input type = "submit" value="传递" />
</form>
<script type="text/javascript">
	//页面的跳转
	  $("#tableid tr").click( function() {//给每行绑定了一个点击事件
	        var td = $( this ).find( "td" );//this指向了当前点击的行，通过find我们获得了该行所有的td对象
	        //题中说到某个td，为了演示所以我们假设是要获得第3个td的数据
	        var todo_id = td.eq(0).html();//通过eq可以得到具体的某个td对象，从而得到相应的数据
	        var data_id = td.eq(1).html();//通过eq可以得到具体的某个td对象，从而得到相应的数据
	        alert(data_id)
			document.getElementById("todoid").value = todo_id;
			document.getElementById("qjid").value = data_id;
		   	document.getElementById("formchakan").submit();
	    });
	
	
</script>
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
</body>
</html>
