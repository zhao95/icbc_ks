<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!--index.jsp 平台登录页面-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.rh.core.org.mgr.UserMgr"%>
<%@ page import="com.rh.core.util.RequestUtils"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.rh.core.base.Context"%>
<%@ page import="com.rh.core.comm.ConfMgr"%>
<%@ page import="com.rh.core.org.UserBean"%>
<%@ page import="com.rh.core.util.Lang"%>

<%
	final String CONTEXT_PATH = request.getContextPath();
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>工商银行考试系统</title>
<link rel="apple-touch-icon-precomposed"
	href="/apple-touch-icon-precompose.png" />
<link rel="shortcut icon" href="favicon.ico" />
<!-- 引用公用头部资源文件：开始 -->
<%@ include file="../sy/base/view/inHeader.jsp"%>
<!-- 引用公用头部资源文件：结束 -->
<link rel="stylesheet" type="text/css"
	href="<%=CONTEXT_PATH%>/sy/comm/index/incl-index.css" charset="UTF-8" />
</head>
<body>
	<script type="text/javascript"
		src="<%=CONTEXT_PATH%>/sy/comm/index/incl-index.js" charset="UTF-8"></script>
	<script type="text/javascript"
		src="<%=CONTEXT_PATH%>/sy/base/frame/i18n/language.js" charset="UTF-8"></script>
	<script type="text/javascript">
		/** 增加手机浏览器的判断 */
		Tools.toMbIndex();
		/** 页面初始化方法：开始 */
		$(function() {
			if (self.frameElement && self.frameElement.tagName
					&& self.frameElement.tagName == "IFRAME") {
				//self.parent.window.location.href = FireFly.getContextPath();
			} else {
				loginInit();
			}
			var temp = document.documentElement.clientHeight - 70;//设置背景高度SY_ORG_CMPY
			jQuery("#logo").height(temp);
			//var tempValue = encodeURIComponent(FireFly.getHttpHost() + FireFly.contextPath + "/index_mb.jsp");
			//var imgLink = FireFly.getHttpHost() + FireFly.contextPath + "/file?act=qrCode&value=" + tempValue + "&size=150";
			//jQuery("#mb-link-img").attr("src",imgLink);
			checkLang();
			if(window.screen.height <= 768){
				jQuery("#form-div").css("top","-20px");
			}else if(window.screen.height > 768){
				jQuery("#form-div").css("top","8%");
			}
			
		});
		function zh() {
			//设置cookie
			var date = new Date();
			date.setTime(date.getTime() + 365 * 24 * 60 * 60 * 1000); //设置date为当前时间一年
			document.cookie = "RhLanguage=zh; expires=" + date.toGMTString();
			checkLang();
		}
		function en() {
			var date = new Date();
			date.setTime(date.getTime() + 365 * 24 * 60 * 60 * 1000); //设置date为当前时间为当前时间一年
			document.cookie = "RhLanguage=en; expires=" + date.toGMTString();
			checkLang();
		}

		function checkLang() {
			if (getCookie("RhLanguage") == "zh") {
				document.title = '工商银行考试系统';
				$("#form_title").html("");
				$("#form_cmpy").html("公司");
				//     	$("#form_username").html("用户名：");
				//     	$("#form_password").html("密    码：");
				$("#form_rem").html("记住密码");
				$("#btnLogin").val("登录");

			} else if (getCookie("RhLanguage") == "en") {
				document.title = 'Integration platform';
				$("#form_title").html("Please Login");
				$("#form_cmpy").html("Cmpy");
				$("#form_username").html("UserName");
				$("#form_password").html("Password");
				$("#form_rem").html("Remember");
				$("#btnLogin").val("Login");
			}
		}
		function getCookie(name) {
			var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
			if (arr = document.cookie.match(reg))
				return unescape(arr[2]);
			else
				return "zh";
		}
	</script>
	<%
		if (userBean != null) {
	%>
	<script type="text/javascript">
		var homeUrl = "sy/comm/page/page.jsp";
		if(window.ICBC){
        	homeUrl = FireFly.getContextPath() + "/index_qt.jsp";
        }
		//设置cookie
		document.cookie = "RhClientLogin=true";
		window.location.href = homeUrl;
	</script>
	<%
		} else {
	%>
	<div id="logo">
		<div id="icbc-ks-logo"></div>
		<div id="top_logo"></div>
		<div id="msg"></div>
		<div id="rh-browser-check" class="rh-browser-check"><a style="cursor:point;font-size:14px;color:green;position: relative;top: -30px;text-align: left;left: 15px;" href="javascript:Tools.redirect('/sy/comm/index/activeTest.jsp');">浏览器及插件检测</a></div>
		<div class=""
			style="position: relative; right: 20px; top: -50px; text-align: right;z-index: -1;">
			<p>© 中国工商银行 版权所有 2017</p>
			<p id="notSupportIE6"></p>
		</div>
		<div id="form-div"
			style="height: 150px; position: relative; left: 250px;top: 1%;">
			<div
				style="display: inline-block; background: url(/qt/css/images/welcome.png) no-repeat center; height: 47px; line-height: 150px; width: 214px; position: relative; top: -50px; left: -40px;"></div>
			<div
				style="display: inline-block; border-left: 1px solid red; width: 1px; height: 150px;"></div>
			<div id="form" style="position: absolute; display: inline-block;" class="radius5">
				<div id="form_title" class="form-title textShadow radius5"></div>
				<div class="form-con radius3" style="padding: 8px;">

					<div class="form-table">

						<div style="display:none;">
							<div id="form_cmpy">
								公<span style="padding: 0px 6px;"></span>司:
							</div>
							<div>
								<div id="cmpy">
									<input id="CMPY_CODE" name="CMPY_CODE" type="text"
										class="ipt-t" value='icbc'/><input id="CMPY_CODE__NAME"
										name="CMPY_CODE__NAME" type="text"
										class="ipt-t icon-input-cmpy"
										onfocus="this.className+=' ipt-t-focus icon-input-cmpy'"
										onblur="this.className='ipt-t icon-input-cmpy'" value='icbc'
										size='30' /><span class="dict-cmpy"></span>
								</div>
							</div>
						</div>

						<div style="margin-bottom:6px;">
							<div id="form_username" style="display: inline-block;">用户名:</div>
							<div style="display: inline-block;">
								<div id="user">
									<input id="USER_CODE" name="USER_CODE" type="text"
										class="ipt-t" />
								</div>
							</div>
						</div>

						<div>
							<div id="form_password" style="display: inline-block;">
								密<span style="padding: 0px 8px;"></span>码:
							</div>
							<div style="display: inline-block;">
								<div id="pwd">
									<input id="USER_PASSWORDS" name="USER_PASSWORDS"
										type="password" class="ipt-t"
										onfocus="this.className+=' ipt-t-focus'"
										onblur="this.className='ipt-t'" />
								</div>
							</div>
						</div>
					</div>
					<div id="submit"
						style="border: 0px red solid; margin-top: 5px; margin-left: 65px;">
						<input type="submit" id="btnLogin" class="submit textShadow" style=" width:170px;color: white;text-shadow: 0 0;"
							title="登录" value="登录" />
					</div>
					<!-- 		         <div id="remb" style=""><label id="form_rem" class="loginAuto-lb">记住密码</label><input id="LOGIN_AUTO" name="LOGIN_AUTO" type="checkbox" class="loginAuto-check"/></div> -->
				</div>
			</div>
		</div>
		<div id="bottom_bj"></div>
		<div id="bottom_taizi"></div>
	</div>
	<input type="hidden" id="rhDevIndex" value="true" />
	<!--  core不再提供二维码支持，迁移到opt包
<div class="mb-link">
	<img id="mb-link-img" src='' width='150px' height='150px'></img>
	<div class="mb-link-title">扫描二维码进入手机登录</div>
</div>
-->
	<div class="rh-browser-container" style="display: none;">
		<div>
			<a style='font-size: 14px; color: green;' href="#" onclick="zh()">中文</a>&nbsp&nbsp&nbsp&nbsp<a
				style='font-size: 14px; color: green;' href="#" onclick="en()">English</a>
		</div>
		<div id="rh-browser-check" class="rh-browser-check">
			<a style='font-size: 14px; color: green;'
				href="javascript:Tools.redirect('<%=CONTEXT_PATH%>/sy/comm/index/activeTest.jsp');">系统插件测试页</a>
		</div>
	</div>
	<%
		}
	%>
</body>

</html>

