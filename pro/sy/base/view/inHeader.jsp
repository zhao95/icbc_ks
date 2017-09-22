<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.rh.core.base.Context" %>
<%@ page import="com.rh.core.base.Bean" %>
<%@ page import="com.rh.core.org.UserBean" %>
<%@ page import="com.rh.core.serv.util.ServUtils" %>
<%@ page import="com.rh.core.util.RequestUtils" %>
<%@ page import="com.rh.core.util.Constant"%>
<%@ page import="com.rh.core.util.JsonUtils"%>
<%@ page import="com.rh.core.util.var.VarMgr"%>
<%@ page import="com.rh.core.util.device.DeviceUtils"%>
<%@ page import="com.rh.core.org.DeptBean"%>
<%@ page import="com.rh.core.org.mgr.OrgMgr"%>

<%
	request.setCharacterEncoding("UTF-8");
	Context.setRequest(request);
	String urlPath = request.getContextPath();
    UserBean userBean = Context.getUserBean(request);
    String devUsers = Context.getSyConf("SY_DEV_USERS","");//开发用户
    String syFont = Context.getSyConf("SY_FONT_BIG","false");//系统大字体启用
    String zipModel = Context.getSyConf("SY_FRONT_ZIP_MODEL","false");//前端文件是否启用压缩模式
    String pjJS = Context.getSyConf("SY_PJ_JS","");//项目级自扩展js
    String pjCSS = Context.getSyConf("SY_PJ_CSS","");//项目级自扩展css
    String pjTop = Context.getSyConf("SY_PJ_TOPVIEW","");//项目级卡片列表顶部的区块显示
    String dev = "";
    if (request != null) {
        dev = DeviceUtils.getCurrentDevice(request).getDeviceType().toString();
    }
    String language = RequestUtils.getStr(request, "language");  // en_US\zh_CN
%>
<meta name="servName" content="<%=System.getProperty("servName")%>"/>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<%if (dev.equals("MOBILE")){%>
	<meta name="viewport" content="width=1280"/>
	<link rel="stylesheet" type="text/css" href="<%=urlPath %>/sy/theme/default/mob-extend.css"/>
<%} else if (dev.equals("TABLET")) {%>
	<meta name="viewport" content="width=1100"/>
	<link rel="stylesheet" type="text/css" href="<%=urlPath %>/sy/theme/default/pad-extend.css"/>
<%} else {%>
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no"/>
	<link rel="stylesheet" type="text/css" href="<%=urlPath %>/sy/theme/default/pc-extend.css"/>
<%}%>
<script type="text/javascript">
	var FireFlyContextPath = "<%=urlPath%>";//虚拟路径
	
<%if(request.getAttribute("RHUS_SESSION") == null) {%>
	var RHUS_SESSION = undefined;
<%} else {%>
	var RHUS_SESSION = '<%=request.getAttribute("RHUS_SESSION")%>';
<%}%>
</script>
<%if (zipModel.equals("true")) {%><!--发布压缩文件模式-->
	<!-- 外部jquery-ui库样式 -->
	<link rel="stylesheet" type="text/css" href="<%=urlPath %>/sy/base/frame/plugs/jquery-ui/rh.jquery-ui.min.css"/>  
	<!-- 平台UI核心样式 -->
	<link rel="stylesheet" type="text/css" href="<%=urlPath %>/sy/theme/default/rh.core.min.css"/> 
	<%if (syFont.equals("true")) {%>
	<link rel="stylesheet" type="text/css" href="<%=urlPath %>/sy/theme/default/fontBig.css"/>
	<%} %>
	<!-- 外部jquery-ui库js脚本 -->
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/jquery-1.8.2.min.js"></script> 
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/plugs/jquery-ui/rh.jquery-ui.min.js"></script>
	<!-- 外部jquerytree库js脚本 -->
	<link rel="stylesheet" type="text/css" href="<%=urlPath %>/sy/base/frame/coms/tree/style.css"/>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/tree/jquery.tree.js"></script>
	<!-- 本地存储 -->
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/store.min.js"></script>
	<!-- 平台UI核心js库 -->
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/rh.core.min.js"></script>
	
	<!-- UEditor -->
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/ueditor/editor_config.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/ueditor/editor_all.js"></script>
	
	<!-- 工商银行JS引用 -->
	<link rel="stylesheet" type="text/css" href="<%=urlPath%>/sy/comm/page/icbc_style.css"/>
	<script type="text/javascript" src="<%=urlPath%>/sy/comm/page/icbc_style.js"></script>
	
	<!-- 打印JS引用 -->
	<script type="text/javascript" src="<%=urlPath%>/sy/print/printThis.js"></script>
	<script type="text/javascript" src="<%=urlPath%>/sy/base/frame/i18n/config.js"></script>
	<script type="text/javascript" src="<%=urlPath%>/sy/base/frame/i18n/language.js"></script>
	<%if (dev.equals("DESKTOP")){%>
		<!-- WdatePicker -->
		<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/DatePicker/WdatePicker.js"></script>
	<%} else {%>
		<!-- MobiScroll-DatePicker -->
		<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/DatePicker/mobileDatePicker/js/mobiscroll.custom-2.6.2.min.js"></script>
		<link rel="stylesheet" type="text/css" href="<%=urlPath %>/sy/base/frame/coms/DatePicker/mobileDatePicker/css/mobiscroll.custom-2.6.2.min.css"/>
		<style>
			.dw-persp {z-index:9999;}
		</style>
	<%}%>
<%} else {%><!--开发模式-->
	<!-- 外部jquery-ui库样式 -->
	<link rel="stylesheet" type="text/css" href="<%=urlPath %>/sy/base/frame/plugs/jquery-ui/jquery.ui.core.css"/>
	<link rel="stylesheet" type="text/css" href="<%=urlPath %>/sy/base/frame/plugs/jquery-ui/jquery.ui.resizable.css"/>
	<link rel="stylesheet" type="text/css" href="<%=urlPath %>/sy/base/frame/plugs/jquery-ui/jquery.ui.theme.css"/>
	<!-- 平台UI核心样式 -->
	<link rel="stylesheet" type="text/css" href="<%=urlPath %>/sy/theme/default/base.css"/>

	<link rel="stylesheet" type="text/css" href="<%=urlPath %>/sy/theme/default/page.css"/>
	<link rel="stylesheet" type="text/css" href="<%=urlPath %>/sy/theme/default/form.css"/>
	<link rel="stylesheet" type="text/css" href="<%=urlPath %>/sy/theme/default/icon.css"/>
	<link rel="stylesheet" type="text/css" href="<%=urlPath %>/sy/theme/default/colortip.css"/><!-- 提示信息 -->
	<link rel="stylesheet" type="text/css" href="<%=urlPath %>/sy/base/frame/coms/tree/style.css"/><!-- 外部jquerytree库js脚本 -->
    <%if (syFont.equals("true")) {%>
	<link rel="stylesheet" type="text/css" href="<%=urlPath %>/sy/theme/default/fontBig.css"/>
	<%} %>
	<!-- 外部jquery-ui库js脚本 -->
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/jquery-1.8.2.min.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/plugs/jquery-ui/a.jquery.ui.core.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/plugs/jquery-ui/b.jquery.ui.widget.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/plugs/jquery-ui/d.jquery.ui.mouse.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/plugs/jquery-ui/e.jquery.ui.draggable.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/plugs/jquery-ui/f.jquery.ui.droppable.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/plugs/jquery-ui/g.jquery.ui.sortable.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/plugs/jquery-ui/h.jquery.ui.position.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/plugs/jquery-ui/i.jquery.ui.resizable.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/plugs/jquery-ui/j.jquery.ui.effect.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/plugs/jquery-ui/k.jquery.ui.accordion.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/plugs/jquery-ui/l.jquery.ui.dialog.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/plugs/jquery-ui/m.jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/plugs/jquery-ui/n.jquery.validate.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/rh.ui.colorTip.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/rh.ui.base64.js"></script>
	<!-- 本地存储 -->
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/store.min.js"></script>
	<!-- 外部jquerytree库js脚本 -->
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/tree/jquery.tree.js"></script>
	<!-- 平台UI核心js库 -->
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/platform.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/tools.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/constant.js"></script>
	<script type="text/javascript" src="<%=urlPath%>/sy/base/frame/i18n/config.js"></script>
	<script type="text/javascript" src="<%=urlPath%>/sy/base/frame/i18n/language.js"></script>
	<!-- 平台UI核心组件js库 -->
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/rh.ui.grid.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/rh.ui.menu.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/rh.ui.loadbar.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/rh.ui.search.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/rh.ui.ccexSearch.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/rh.ui.tree.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/rh.ui.Delegate.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/rh.ui.openTab.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/rh.ui.pop.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/rh.ui.popPrompt.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/rh.ui.card.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/rh.ui.mind.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/rh.ui.next.js"></script>
	<!-- 平台UI核心view部分js库 -->
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/engines/rhListView.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/engines/rhListBatchView.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/engines/rhCardView.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/engines/rhDictTreeView.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/engines/rhSelectListView.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/engines/rhWfCardView.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/engines/rhUserInfoView.js"></script>	
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/engines/rhListExpanderView.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/engines/rhCommentView.js"></script>
	<!-- UEditor -->
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/ueditor/editor_config.js"></script>
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/ueditor/editor_all.js"></script>
	
	<!-- colResizable -->
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/plugs/colResizable/colResizable-1.6.js"></script>
	<link rel="stylesheet" type="text/css" href="<%=urlPath %>/sy/theme/default/common.css"/>
	<!-- 工商银行JS引用 -->
	<link rel="stylesheet" type="text/css" href="<%=urlPath%>/sy/comm/page/icbc_style.css"/>
	<script type="text/javascript" src="<%=urlPath%>/sy/comm/page/icbc_style.js"></script>
	
	<!-- 各个子系统自己的专有样式 -->
	<link rel="stylesheet" type="text/css" href="<%=urlPath%>/product/project.css"/>
	
	<!-- 打印JS引用 -->
	<script type="text/javascript" src="<%=urlPath%>/sy/print/printThis.js"></script>
	
	<!-- 扩展rhListView，添加方法，渲染“调整排序”箭头-->
	<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/engines/extends/rhListViewSortArrow.js"></script>
    <script type="text/javascript" src="<%=urlPath %>/sy/base/frame/engines/extends/rhWfCardViewNodeExtends.js"></script>
    		
	<%if (dev.equals("DESKTOP")){%>
		<!-- WdatePicker -->
		<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/DatePicker/WdatePicker.js"></script>
	<%} else {%>
		<!-- MobiScroll-DatePicker -->
		<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/DatePicker/mobileDatePicker/js/mobiscroll.custom-2.6.2.min.js"></script>
		<link rel="stylesheet" type="text/css" href="<%=urlPath %>/sy/base/frame/coms/DatePicker/mobileDatePicker/css/mobiscroll.custom-2.6.2.min.css"/>
		<style>
			.dw-persp {z-index:9999;}
		</style>
	<%}%>
<%}%>
<!--

//-->
</script>
<!-- 项目自定义js扩展，读取系统配置值的SY_PJ_JS -->
<%if (pjJS.length() > 0) {%>
    <script type="text/javascript" src="<%=urlPath %><%=pjJS %>"></script>
<%} %>
<!-- 项目自定义css扩展，读取系统配置值的SY_PJ_CSS -->
<%if (pjCSS.length() > 0) {%>
	<link rel="stylesheet" type="text/css" href="<%=urlPath %><%=pjCSS %>"/>
<%} %>
<script type="text/javascript">
	var language = "<%=language%>";
	if(language && Cookie.get("RhLanguage") != language) {
		if(language == "en_US") {
			Cookie.set("RhLanguage", "en", "365", "/"); //保存语言信息
		} else {
			Cookie.set("RhLanguage", "zh", "365", "/"); //保存语言信息
		}
	}
	// 设置window上的_parent的引用	
	try {
		// window和window.parent同源
		if (window.parent.document.domain == window.document.domain) {
			window._parent = window.parent;
		} else {
			window._parent = window;
		}
	} catch(e) {
		// 不同源
		window._parent = window;
	}

	var __SERVER_NAME = "<%=System.getProperty("servName")==null?"":System.getProperty("servName")%>";

    GLOBAL.setFrameId(window.name);//设置当前frame的id到公共变量
	FireFly.jsessionid = "<%=request.getSession().getId()%>";
	var devUsers = "<%=devUsers%>";
	<%if (userBean != null) {
		DeptBean deptBean = OrgMgr.getDept(userBean.getDeptCode());
		
	%>
	    var parSysVars = {};
	    try {
		    if (parent && parent.System) {
	            var innerPath = FireFly.getHttpHost() + FireFlyContextPath;
		    	var parPath = parent.FireFly.getHttpHost() + parent.FireFlyContextPath;
		    	if (innerPath == parPath) {
			    	parSysVars = parent.System.getVars();
		    	}
			}
		} catch(e) {
		}
	    if (jQuery.isEmptyObject(parSysVars)) {
			<%
				//用户相关信息、系统配置信息、日期相关信息
			    String sysParams = JsonUtils.mapsToJson(VarMgr.getOrgMap(), VarMgr.getConfMap(), VarMgr.getDateMap());
			%>
			var sysVarParasStr = <%=sysParams%>;
		    var loginName = "<%=userBean.getLoginName()%>";
	
			var devUsersArray = devUsers.split(",");
			if (jQuery.inArray(loginName,devUsersArray) != -1) {
				sysVarParasStr["@DEV_FLAG@"] = "true";
			}
			sysVarParasStr["@CODE_PATH@"] = "<%=deptBean.getCodePath()%>";
			System.setVars(sysVarParasStr);
	    } else {
	    	parSysVars["@CODE_PATH@"] = "<%=deptBean.getCodePath()%>";
	    	System.setVars(parSysVars);
	    }
		GLOBAL.cookieBanner = "rh_banner_" + "<%=userBean.getCode()%>";
	<%}%>
	
	//是否显示右侧模块
	if (!ICBC.showRightContent()) {
		UIConst.PAGE_WID = "85%";
		UIConst.MENU_WID = "15%";
		UIConst.PAGE_MARGIN_LEFT = "15%";
		UIConst.PAGE_MARGIN_RIGHT = "0%";
	}
</script>