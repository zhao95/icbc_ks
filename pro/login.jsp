<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!--index.jsp 平台登录页面-->
<%@page import="com.rh.core.util.AuthUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.rh.core.base.Context" %>

<%
    //统一认证客户机地址
    String clientIP = Context.getSyConf("AAM_AUTH_CLIENT_IP_PORT", "122.22.45.37:9080");
    //site 跳转路径
    String url = "http://" +clientIP+"/login.jsp";
    //统一认证后的跳转路径
    String haveLoginUrlPath = "index_qt.jsp";
    AuthUtil.toAuth(request,response,url,haveLoginUrlPath,true);
%>
<html>

<body>
</body>

</html>

