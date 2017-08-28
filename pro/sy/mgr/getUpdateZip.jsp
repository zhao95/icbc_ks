<%@page import="com.rh.core.base.Context"%>
<%@page import="com.rh.core.org.UserBean"%>
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
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>系统升级操作界面</title>
<script type="text/javascript" src="../../sy/base/frame/jquery-1.8.2.min.js"></script>
<script type="text/javascript">
jQuery(function(){
	if(window.sessionStorage){
		$("textarea").val(sessionStorage.getItem("fileList"));
	}
	$("form").submit(function(){
		if(window.sessionStorage){
			sessionStorage.setItem("fileList",$("textarea").val());
		}
	});
	
	$("#file_txt").change(function(){
		var filePath = $(this).val();
		var url = "/CC_UPDATE_FILE_ZIP.readFile.do";
		var showList = function(data){
			if(data["_MSG_"].indexOf("OK,")==0){
				$("textarea").val(data["_DATA_"]);
			}
		}
		jQuery.post(url,{"FILE_PATH":filePath},showList,"json");
	});
});
</script>
</head>
<body style="text-align: -webkit-center; margin-left: auto; margin-right: auto">
	<table width="90%" border="0" cellspacing="0" cellpadding="10px"
		align="center">
		<tr>
			<td class="h4">&nbsp;手工录入更新文件列表 &nbsp;</td>
		</tr>
		<form action="getUpdateResult.jsp" name="updateListForm"
			method="post">
			<tr>
				<td><label>文件路径</label><input type="file" id="file_txt" style="width: 40%" accept="text/plain" /></td>
						
			</tr>
			<tr>
				<td style="text-align: -webkit-center;"><textarea name="fileList" rows="20"
						style="width: 90%"
						placeholder="请输入文件路径，每行一条路径,如:
com/rh/cochat/xxx1.class
com/rh/cochat/xxx2.class "></textarea>
			</tr>
			<tr style="margin-top:10%">
				<td align="center"><input type="submit" name="" value=下一步
					class="button"></td>
			</tr>
		</form>
	</table>
</body>
</html>