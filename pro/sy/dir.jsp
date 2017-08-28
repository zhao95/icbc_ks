<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.rh.core.util.RequestUtils"%>
<%@page import="com.rh.core.util.DateUtils"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="org.apache.commons.io.comparator.LastModifiedFileComparator"%>
<%@page import="java.util.*" %>
<%@page import="java.io.*" %>
<%@page import="com.rh.core.base.Context" %>
<%
	request.setCharacterEncoding("UTF-8");
	String legalDirs = Context.getSyConf("CC_DOWNLOAD_LEGAL_DIRS", "/opt");
	String[] legalDirArray = legalDirs.split(",");
	String fileDir = request.getParameter("dir");
	File dir = null;
	File files[] = null;
	if (fileDir != null && !"".equals(fileDir)) {
		boolean legalFlag = false;
		for (String legalDir : legalDirArray) {
			if (fileDir.indexOf(legalDir) > -1) {
				legalFlag = true;
			} 
		}
		if (legalFlag) {
			dir = new File(fileDir);
			if (dir.exists()) {
				files = dir.listFiles();
				// 按时间排序
				Arrays.sort(files, new LastModifiedFileComparator());
			}
		} else {
			out.println("<h3>只能显示指定文件夹下的文件!</h3>");
		}
	} else {
		out.println("<h3>只能显示指定文件夹下的文件!</h3>");
	}
%>	
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>目录文件</title>
    <link rel="apple-touch-icon-precomposed" href="/apple-touch-icon-precompose.png"/>
    <link rel="shortcut icon" href="favicon.ico"/>
</head>
<body>
	<div style="width:100%;height:auto;border: 2px solid #e3e3e3;overflow:hidden;margin-bottom:30px;">
		<h5 style="margin:5px 0;padding:5px 0;">允许访问的文件夹</h5>
		<%
			for (String legalDir : legalDirArray) {
		%>
			<h6 style="margin:5px 0;padding:5px 0;width:30%; float:left;">
				<a href="./dir.jsp?dir=<%=legalDir %>"><%=legalDir %></a>
			</h6>
		<%
			}
		%>
	</div>
	<%
		if (files != null) {
			DateFormat df = new SimpleDateFormat(DateUtils.FORMAT_DATETIME);
			int len = files.length;
			while (--len >= 0) {
				File f = files[len];
				if (f.exists()) {
					String fileName = f.getName();
					if (fileName.length() > 0 && !".".equals(fileName) && !"..".equals(fileName)) {
						String lastModified = df.format(new Date(f.lastModified()));
						String fullDir = fileDir + "/" + fileName;
						if (f.isDirectory()) {
							out.println("文件夹：<a href='./dir.jsp?dir=" + fullDir + "'>" + fileName + "</a>&nbsp;&nbsp;&nbsp;&nbsp;" + lastModified + "<br />");
						} else {
							out.println("文件：<a href='./download.jsp?fileName=" + fullDir + "'>" + fileName + "</a>&nbsp;&nbsp;&nbsp;&nbsp;" + lastModified + "&nbsp;&nbsp;&nbsp;&nbsp;"+f.length()/1024+"&nbsp;KB<br />");	
						}
					}
				}  
			}
		}
	%>
</body>
</html>

