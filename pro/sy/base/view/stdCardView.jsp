<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!--StdCardView.jsp列表页面-->
<%@page import="com.rh.core.comm.ConfMgr"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.rh.core.util.Strings"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ page import="com.rh.core.serv.ServMgr" %>
<%@ page import="com.rh.core.base.Bean" %>
<%@ page import="com.rh.core.serv.ParamBean" %>
<%@ page import="com.rh.core.util.JsonUtils" %>
<%@ page import="com.rh.core.base.Context" %>

<%--所有的页面上需要留出一行ICBC的图片--%>
<%--<%@ taglib uri="ctp-tag" prefix="ctp"%> --%>  
<%
	String min = request.getParameter("min-width")==null?"950":request.getParameter("min-width");//页面最小宽度
	String max = request.getParameter("max-width")==null?"":request.getParameter("max-width");//页面最大宽度
%>


<%@ include file= "inHeader.jsp" %>
<%
	//必须参数
	String sId = RequestUtils.getStr(request, "sId");
	sId=Strings.escapeHtml(sId);
	String pkCode = RequestUtils.getStr(request, "pkCode");
	//可选参数
	String frameId = RequestUtils.getStr(request, "frameId");
	
	String replaceUrl = RequestUtils.getStr(request, "replaceUrl");
	String paramsFlag = RequestUtils.getStr(request, "paramsFlag");
	String readOnly = RequestUtils.getStr(request, "readOnly");
	String areaId = RequestUtils.getStr(request, "areaId");
	
	String title = Strings.escapeHtml(RequestUtils.getStr(request,"title"));

	
	//转换参数对象
	ParamBean transBean = new ParamBean(request);
	//装载服务定义
	Bean servBean = ServMgr.servDef(sId);
	//取得服务中配置的HTML代码
	String strHtml = "";
	if(servBean.isNotEmpty("SERV_JS")){
	    strHtml = servBean.getStr("SERV_JS");
	    strHtml = strHtml.replaceAll("@urlPath@", urlPath);
	    out.println(strHtml);
	}
	String servDef = JsonUtils.toJson(servBean,false);
	String transParams = JsonUtils.toJson(transBean,false);
	String enJson = servBean.getStr("EN_JSON");
	if(StringUtils.isEmpty(enJson)) {
		enJson = "{}";
	}
	if (StringUtils.isEmpty(title)) {
		title = servBean.getStr("SERV_NAME");
	}
%>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><%=title %></title>
    <script type="text/javascript" src="<%=urlPath%>/sy/base/frame/coms/file/swfupload.js"></script>
	<script type="text/javascript" src="<%=urlPath%>/sy/base/frame/coms/file/js/swfupload.queue.js"></script>
	<script type="text/javascript" src="<%=urlPath%>/sy/base/frame/coms/file/js/fileprogress.js"></script>
	<script type="text/javascript" src="<%=urlPath%>/sy/base/frame/coms/file/js/handlers.js"></script>
</head>
<%
if (pjTop.length() > 0) {
%>
<jsp:include page="<%=pjTop %>" flush="true" /> 
<% 
}
%>
<body class="bodyBack bodyBackPad stdCardView icbcBody">


<div id="ou-topPic" class="header Head" style='background:url(<%=urlPath%>/sy/icbc/Head_bg.png) top repeat-x; height:53px; position:relative;min-width:<%=min %>px;max-width:<%=max %>px;'>
	<div class="Logo" style="position:absolute; background:url(<%=urlPath%>/sy/icbc/Logo.png) center no-repeat; width:287px; height:53px;"></div>
	<div class="pattern" style="position:absolute; right:0px; background:url(<%=urlPath%>/sy/icbc/Head_pattern.png) center no-repeat; width:640px; height:53px;"></div>
</div>
<div class="header" style='background:url(<%=urlPath%>/sy/icbc/leftmenu_cur.png) repeat-x center 50%;height:3px;min-width:<%=min %>px;max-width:<%=max %>px; margin-bottom:10px'>  </div>


<%-- <%@ include file="/sy/tmpl/PAGE_TOP_LOGO_card.html" %>  --%>
<input type="hidden" id="viewPage" value="card"/>
<div id="cardCon"></div>
<script type="text/javascript">
var __serv_def = <%=servDef%>;
var __transPrams = <%=transParams%>;
document.title = Language.transDynamic("SERV_NAME", <%=enJson%>, "<%=title%>"); 
GLOBAL.servStyle["<%=sId%>"] = "tru"; //服务级html缓存
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
        var act = UIConst.ACT_CARD_MODIFY;
        var pkCode = "<%=pkCode%>";
        if (pkCode == null || (pkCode == "null") || (pkCode.length == 0)) {
	        act = UIConst.ACT_CARD_ADD;
	        pkCode = "";
        }
	    var temp = {"act":act,"replaceUrl":"<%=replaceUrl%>","sId":"<%=sId%>","parHandler":null,"parTabClose":"true",
	                "paramsFlag":"<%=paramsFlag%>","readOnly":"<%=readOnly%>","areaId":"<%=areaId%>",
	                "servDef":__serv_def,"transParams":__transPrams, "pCon":jQuery("#cardCon")};
	    temp[UIConst.PK_KEY] = pkCode;
	    var cardView = new rh.vi.cardView(temp);
	    cardView.show();
	    RHFile.bldDestroyBase(cardView);
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
</body>
</html>