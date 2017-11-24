<%@page import="com.rh.core.icbc.basedata.serv.KSNImpDataServ"%>
<%@page import="com.rh.core.util.threadpool.RhThreadTask"%>
<%@page import="com.rh.core.util.threadpool.RhThreadPool"%>
<%@page import="com.rh.core.serv.ParamBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>初始化系统数据</title>
<script type="text/javascript">
	document.onkeydown = function() {
		if ((window.event.keyCode == 116) || //屏蔽 F5  
		(window.event.keyCode == 122) || //屏蔽 F11  
		(window.event.shiftKey && window.event.keyCode == 121) //shift+F10  
		) {
			window.event.keyCode = 0;
			window.event.returnValue = false;
			alert("禁止F5刷新网页！");
		}
		if ((window.event.altKey) && (window.event.keyCode == 115)) {
			//屏蔽Alt+F4  
			window.showModelessDialog("about:blank", "",
					"dialogWidth:1px;dialogheight:1px");
			return false;
		}
	}
</script>
</head>
<body>
	<%!String random;%>
	<%
		String ran = request.getParameter("R");
		if(ran == null){
			out.print("<h5>随机数R参数信息无效 .....</h5>");
			return;
		}
		if(random != null && random.equalsIgnoreCase(ran)){
			out.print("<h5> 随机参数相同.....</h5>");
			return;
		}
		random = ran;
		String act = request.getParameter("ACT");
		if(act == null){
			out.print("<h5>参数信息无效 .....</h5>");
			return;
		}
		if(act.equalsIgnoreCase("impfulldata")){
			out.print("<h5>导入全量数据 .....</h5>");
			ParamBean param = new ParamBean();
			param.set("INCREMNET", false);
			String tables = request.getParameter("TABLES");
			if(tables != null){
				param.set("TABLES", tables);
			}
			String adminUser = request.getParameter("ADMIN");
			if(adminUser != null){
				param.set("userSSICIDs", adminUser);
			}
			new KSNImpDataServ().impDatafromTable(param);
			out.print("<h5>导入全量数据完成！</h5>");
		}
		
	%>
</body>
</html>