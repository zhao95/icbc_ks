<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.rh.core.util.RequestUtils"%>
<%@page import="java.io.*"%>
<%@page import="org.apache.commons.io.IOUtils"%>
<%@page import="com.rh.core.base.Context" %>
<%
	request.setCharacterEncoding("UTF-8");
	String legalDirs = Context.getSyConf("CC_DOWNLOAD_LEGAL_DIRS", "/opt");
	String[] legalDirArray = legalDirs.split(",");
	String logFileName = request.getParameter("fileName");
	
	boolean legalFlag = false;
	for (String legalDir : legalDirArray) {
		if (logFileName.indexOf(legalDir) > -1) {
			legalFlag = true;
		} 
	}
	
	if (legalFlag) {
		// 返回文件流到客户端
		response.reset();
		RequestUtils.setDownFileName(request, response, logFileName.substring(logFileName.lastIndexOf("/") + 1)+".zip");
		OutputStream output = response.getOutputStream();
		IOUtils.copy(new FileInputStream(logFileName), output);
		output.flush();
		response.flushBuffer();
	} else {
		out.println("<h3>文件路径不合法!</h3>");
	}
%>