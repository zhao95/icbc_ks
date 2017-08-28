<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!--StdListView.jsp列表页面-->
<%@page import="com.rh.core.util.Strings"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ page import="com.rh.core.serv.ServMgr" %>
<%@ page import="com.rh.core.base.Bean" %>
<%@ page import="com.rh.core.serv.ParamBean" %>
<%@ page import="com.rh.core.util.JsonUtils" %>
<%@ page import="com.rh.core.base.Context" %>

<%@ include file= "inHeader.jsp" %>
<%
	String frameId = RequestUtils.getStr(request,"frameId");
	String sId = RequestUtils.getStr(request,"sId");
	sId=Strings.escapeHtml(sId);
	String extWhere = RequestUtils.getStr(request,"extWhere");
	String paramsFlag = RequestUtils.getStr(request,"paramsFlag");
	String readOnly = RequestUtils.getStr(request,"readOnly");
	String dataFlag = RequestUtils.getStr(request,"dataFlag");
	String parVar = RequestUtils.getStr(request,"parVar");
	String parWhere = RequestUtils.getStr(request,"parWhere");
	String type = RequestUtils.getStr(request,"type");
	String title = Strings.escapeHtml(RequestUtils.getStr(request,"title"));

	//转换参数对象
	ParamBean transBean = new ParamBean(request);
	//装载服务定义
	Bean servBean = ServMgr.servDef(sId);
	String strHtml = "";
	if(servBean.isNotEmpty("SERV_JS")){
	    strHtml = servBean.getStr("SERV_JS");
	    strHtml = strHtml.replaceAll("@urlPath@", urlPath);
	    out.println(strHtml);
	}
	String servDef = JsonUtils.toJson(servBean,false);
	if (StringUtils.isEmpty(title)) {
		title = servBean.getStr("SERV_NAME");
	}
	String enJson = servBean.getStr("EN_JSON");
	if(StringUtils.isEmpty(enJson)) {
		enJson = "{}";
	}
	String transParams = JsonUtils.toJson(transBean,false);
%>
	
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><%=title %></title>
    
    <script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/file/swfupload.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/file/js/swfupload.queue.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/file/js/fileprogress.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/file/js/handlers.js"></script>
	
</head>
<%
if (pjTop.length() > 0) {
%>
<jsp:include page="<%=pjTop %>" flush="true" /> 
<% 
}
%>
<body class="bodyBack bodyBackPad">
</body>
<input type="hidden" id="viewPage" value="list"/>
<script type="text/javascript">
if (window.self == window.top) { //顶层页面，去掉上面的margin
	jQuery("body").css({"padding-top":"0"});
}

var __serv_def = <%=servDef%>;
var __transPrams = <%=transParams%>;
document.title = Language.transDynamic("SERV_NAME", <%=enJson%>, "<%=title%>"); 
GLOBAL.servStyle["<%=sId%>"] = "true"; //服务级html缓存
<%
if(userBean != null) {
%>
try{
if (parent.GLOBAL.style.SS_STYLE_MENU) {//有外层风格设定
	  jQuery("body").addClass(parent.GLOBAL.style.SS_STYLE_MENU);
}
} catch (e) {}
(function() {
    jQuery(document).ready(function(){
      var temp = {"sId":"<%=sId%>","pCon":jQuery("body"),"extWhere":"<%=extWhere%>","title":"<%=title%>",
                  "paramsFlag":"<%=paramsFlag%>","readOnly":"<%=readOnly%>","dataFlag":"<%=dataFlag%>",
                  "parVar":StrToJson("<%=parVar%>"),"parWhere":"<%=parWhere%>","type":"<%=type%>",
                  "servDef":__serv_def,"transParams":__transPrams};
      //初始化列表view
      var listView = new rh.vi.listView(temp);
      listView.show();
      window.listView = listView;
    });
})();
<%
} else {
%>
    FireFly.jumpToIndex();
<%
}
%>
</script>
</html>