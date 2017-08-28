<%@page import="com.rh.core.base.Context"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.rh.core.comm.CacheMgr" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<%
		request.setCharacterEncoding("UTF-8");
		String act = request.getParameter("act");
		
			String cacheName = request.getParameter("cache");
			if (cacheName == null || "".equals(cacheName)) {
				cacheName = "MSG_VALIDATE";
			}
			String cacheKey = request.getParameter("key");
			if (cacheKey == null || "".equals(cacheKey)) {
				cacheKey = "000803837";
			}
			String cacheValue = request.getParameter("value");
			if (cacheValue == null || "".equals(cacheValue)) {
				cacheValue = "1234567890";
			}
			
		if ("get".equals(act)) {
			Object token = CacheMgr.getInstance().get(cacheKey, cacheName);
			if (token == null) {
				out.println("cache is null !");
			} else {
				out.println("cache is " + token.toString());
			}
			
		}
		if ("set".equals(act)) {
			CacheMgr.getInstance().set(cacheKey, cacheValue, cacheName, 6000);
			out.println("cacheName = " + cacheName + ", ");
			out.println("cacheKey = " + cacheKey + ", ");
			out.println("cacheValue = " + cacheValue + ", ");
		}
		if ("rm".equals(act)) {
			CacheMgr.getInstance().remove(cacheKey, cacheName);
		}
		if ("conf".equals(act)) {
			String confValue = Context.getSyConf(cacheKey, "");
			out.println("---系统配置--"+cacheKey+"--"+confValue+"---");
		}
		
	%>
</body>
</html>