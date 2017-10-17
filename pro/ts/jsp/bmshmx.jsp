<!DOCTYPE html>
<%@page import="com.rh.core.serv.ServDao"%>
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
<title>报名审核</title>
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">
<!-- 获取后台数据 -->
<%@ include file="/qt/jsp/header-logo.jsp"%> 
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
	<div class="" style="padding: 10px">
		<a href="<%=CONTEXT_PATH%>/index_qt.jsp"><img style="padding-bottom: 10px ;color: #388CAE;" src="<%=CONTEXT_PATH%>/ts/image/Home_16x16.png" id="shouye"></a>
		 <span style="color: #909090;font-size: 16px;">&nbsp;&nbsp;/&nbsp;&nbsp;报名明细</span>
	</div>
	<div  style="padding-left: 50px;padding-top:10px">
		<image style="padding-bottom:10px"
				src="/ts/image/u1011.png" id="shouye"></image><span
			style="color: black; font-size: 25px">&nbsp;&nbsp;&nbsp;&nbsp;审核明细</span>
	</div>
	<table id="table" style="margin-left: 10px; margin-top: 20px; background-color: white; width: 98%">
	
	<thead id="tem" class="">
						
		<tr style="backGround-color:WhiteSmoke; height: 30px">
			<th style="width: 5%; text-align: center">序号</th>
			<th style="width: 13%;text-align: center">审核时间</th>
			<th style="width: 13%;text-align: center">审核人姓名</th>
			<th style="width: 13%; text-align: center">审核人登录名</th>
			<th style="width: 13%; text-align: center">审核级别</th>
			<th style="width: 13%; text-align: center">审核状态</th>
			<th style="width: 13%; text-align: center">审核理由</th>
			<th style="width: 13%; text-align: center">审核地址</th>
		</tr>
	</thead>
	<tbody>
	 <%
	 	
		String asign = "";//标记自动审核
		String reasonstr = "";
	 	String bmid = "";
		if(userBean != null) {
	    bmid = request.getParameter("bmidmx"); 
	    String where = "AND DATA_ID="+"'"+bmid+"'"+" AND SH_TYPE='1'";
	    List<Bean> list = ServDao.finds("TS_COMM_MIND",where);
	    for(int i=0;i<list.size();i++){
		Bean bean = list.get(i);
		int j = i+1;
		String shdate = bean.getStr("S_ATIME");
		String shr = bean.getStr("SH_UNAME");
		String shlog = bean.getStr("SH_UCODE");
		String jibie = bean.getStr("SH_LEVEL");
		String state = bean.getStr("SH_STATUS");
		String liyou = "";
		String status = "";
		if("1".equals(state)){
			status = "审核通过";
		}else if("2".equals(state)){
			status="审核未通过";
		}
		if("自动审核".equals(shr)){
			asign=String.valueOf(i);
			reasonstr=bean.getStr("SH_MIND");
		}else{
		 liyou = bean.getStr("SH_MIND");
		}
		String address = bean.getStr("SH_ULOGIN");
	%>
		 <tr height="50px">
		<td align="center"><%=j %></td><td><%=shdate %></td><td align="center"><%=shr %></td><td align="center"><%=shlog %></td><td align="center"><%=jibie %></td><td align="center"><%=status %></td><td id="liyou<%=i %>" align="left"><%=liyou %></td><td align="center"><%=address %></td>
		</tr> 
		<%}}%>
	</tbody>
	</table>
	<script type="text/javascript">
	 var rows = $("#table").find("tr");  
	    for(i = 1; i < rows.length; i++){  
	        if(i % 2 == 0){  
	            rows[i].style.backgroundColor = "Azure";  
	       }  
	    } 
	    	 var bj = <%=asign%>;
	 	 	if(bj!=""||bj==0){
	 	 		var a = "liyou"+bj;
	 	 		//自动审核规则
	 	 		var dataArray =<%=reasonstr%>;
	 	 		for(var j=0;j<dataArray.length;j++){
	 					if(j==0){
	 						$("#"+a).append('<div style="height:5px;"></div>');
	 					}
	 					if(dataArray[j].VLIDATE=="true"){
	                     $("#"+a).append('<div><img src="/ts/image/u4719.png">&nbsp;'+dataArray[j].NAME+'</div>');
	    					
	 				}if(dataArray[j].VLIDATE=="false"){
	 					
	 						$("#"+a).append('<div style="color:red;"><img src="/ts/image/u4721.png">&nbsp;'+dataArray[j].NAME+'</div>');
	 				}
	 				
	 				if(dataArray[j].VLIDATE=="STAY"){
	 					
	 					$("#"+a).append('<div">'+dataArray[j].NAME+'</div>');
				}
	 			
	 				$("#"+a).append('<div style="height:5px;"></div>');
	 				
	 				}
	 	 		
	 	 	}
	   
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
