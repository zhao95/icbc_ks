<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% if (Context.getSyConf("CM_OFFICT_TYPE", "").equals("ntko_cp")) { //中石油专版%>
<object width="1" height="1" classid="clsid:C9BC4DFF-4248-4a3c-8A49-63A7D317F404" codebase="<%=request.getContextPath()%>/sy/util/office/OfficeControl_cp.cab#version=5,0,0,2" id="TANGER_OCX"><PARAM NAME=BorderStyle VALUE=1>
	<PARAM NAME=BorderColor VALUE=14402205>
	<PARAM NAME=TitlebarColor VALUE=14402205>
	<PARAM NAME=TitlebarTextColor VALUE=0>
	<PARAM NAME=Caption VALUE="Office">
	<PARAM NAME=IsShowToolMenu VALUE=-1>
	<param name="MakerCaption" value="北京中唐网数码科技有限公司">
	<param name="MakerKey" value="588878F712EB1CA11F71EDBCCDACB19ABB3F2B08">
	<param name="ProductCaption" value="中国石油">
	<param name="ProductKey" value="FC06EECC1A0E26786B0AFDF15BCA8047DEE3006D">
	<SPAN id="ntkoTip" STYLE="color:red">该网页需要控件浏览.浏览器无法装载所需要的文档控件.请检查浏览器选项中的安全设置.</SPAN>
	<param name="Hidden2003Menus" value="共享工作簿(&B);保护工作表(&P);允许用户编辑区域(&A);保护工作簿(&B);保护并共享工作簿(&S);">
	<param name="Hidden2003MenuItems" value="共享工作簿(&B);保护工作表(&P);允许用户编辑区域(&A);保护工作簿(&B);保护并共享工作簿(&S);">
</object>
<% } else { %>
<object width="1" height="1" classid="clsid:C9BC4DFF-4248-4a3c-8A49-63A7D317F404" codebase="<%=request.getContextPath()%>/sy/util/office/OfficeControl.cab#version=5,0,1,8" id="TANGER_OCX"><PARAM NAME=BorderStyle VALUE=1>
	<PARAM NAME=BorderColor VALUE=14402205>
	<PARAM NAME=TitlebarColor VALUE=14402205>
	<PARAM NAME=TitlebarTextColor VALUE=0>
	<PARAM NAME=Caption VALUE="Office">
	<PARAM NAME=IsShowToolMenu VALUE=-1>
	<param name="MakerCaption" value="北京中唐网数码科技有限公司">
	<param name="MakerKey" value="588878F712EB1CA11F71EDBCCDACB19ABB3F2B08">
	<param name="ProductCaption" value="软虹科技">
	<param name="ProductKey" value="96A28185A46B3FDA0778D95F8CFBC077ED385BD7">
	<SPAN id="ntkoTip" STYLE="color:red">该网页需要控件浏览.浏览器无法装载所需要的文档控件.<br>请等待安装文件下载完成并安装，并检查浏览器选项中的安全设置.</SPAN>
	<param name="Hidden2003Menus" value="共享工作簿(&B);保护工作表(&P);允许用户编辑区域(&A);保护工作簿(&B);保护并共享工作簿(&S);">
	<param name="Hidden2003MenuItems" value="共享工作簿(&B);保护工作表(&P);允许用户编辑区域(&A);保护工作簿(&B);保护并共享工作簿(&S);">
</object>
<%}%>
