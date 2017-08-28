<%@page import="com.rh.core.base.Context"%>
<%@page import="com.rh.core.org.UserBean"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.rh.core.icbc.mgr.updateFileZipServ"%>
<%@page import="com.rh.core.serv.OutBean"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>

<%
	//判断用户是否是管理员
	UserBean userBean = Context.getUserBean(request);
	boolean isAdmin = false;
	if(userBean != null){
		isAdmin = userBean.isAdminRole();
	}
	if(userBean == null || !isAdmin){
		out.print("您不是管理员，无权限访问！");
		return;
	}
	String absolutePath = getServletContext().getRealPath("/");
	String jsabsPath = absolutePath.replaceAll("\\\\", "\\\\\\\\");
	String fileList = request.getParameter("fileList");
	OutBean outBean = new updateFileZipServ().checkFiles(fileList, absolutePath);
	Map<String, String> map = new HashMap<String, String>();
	if (outBean.isOkOrWarn()) {
		map = (Map<String, String>) outBean.getData();
	} else {
		map = null;
	}
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>升级文件打包</title>
<script type="text/javascript" src="../../sy/base/frame/jquery-1.8.2.min.js"></script>
<style type="text/css">
tr{height:35px}
</style>
</head>
<body style="text-align: -webkit-center;">
	<div style="text-align:center; font-size: x-large; margin-left: auto; margin-right: auto">升级文件预览</div>
	<table rules="rows" style="width: 70%; margin-left: auto; margin-right: auto">
		<thead>
			<tr>
				<th style="text-align: -webkit-left;" ><h4 style="margin-left: 20%;">文件路径</h4></th>
				<th><h4>是否存在</h4></th>
			</tr>
		</thead>
		<tbody>
			<%
				if (map == null || map.size() == 0) {
			%>
			<tr style="height:35px">
				<td colspan="2" align="center">无升级文件！</td>
			</tr>
			<%
				} else {
					int id = 1;
					for (Map.Entry<String, String> entry : map.entrySet()) {
			%><tr>
				<td id="file_input_<%=id%>" class="file_input"><%=entry.getKey()%></td>
				<td id="Show_td_<%=id%>" class="show_td" style="text-align: center;"><%=entry.getValue()%></td>
			</tr>
			<%
				id++;
					}
			%>
			<tr class="text-right" style="height:75px">
				<td colspan="2"><button id="btn_back">返回修改文件路径</button>
					<button id="btn_zip" style="margin-left: 10%;">打包升级文件</button></td>
			</tr>
			<%
				}
			%>
		</tbody>
	</table>
</body>
<script type="text/javascript">
	//文件保存的系统根路径
	var absPath = "<%=jsabsPath %>";
	
	//错误路径显示红色，内部类显示黄色
	$("td.show_td").each(function() {
		var value = $(this).html();
		if (value == "否") {
			$(this).css("background-color", "red");
			$(this).prev().css("background-color", "red");

		}
		if (value == "内部类") {
			$(this).css("background-color", "yellow");
			$(this).prev().css("background-color", "yellow");

		}
	});
	
	//打包文件
	$("#btn_zip").click(function() {
		//获取所有的input的文件路径
		var filepath = [];
		$("td.file_input").each(function() {
			var value = $(this).html();
			filepath.push(value);
		});

		var data = {
			"ABS_PATH" : absPath,
			"FILE_PATH" : filepath.join(",")
		}
		var url = "/CC_UPDATE_FILE_ZIP.doZip.do"

		jQuery.post(url, data, function(result) {
			if(result["_MSG_"].indexOf("OK,")==0){
				alert("打包成功！文件在"+absPath);
			}
		}, "json");
	});
	
	//返回
	$("#btn_back").click(function() {
		history.back();
	});
	
	
</script>
</html>