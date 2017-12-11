package com.rh.core.util;

import com.icbc.ssic.base.*;
import com.rh.core.base.BaseContext;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 统一认证帮助类 Created by liyongfei_rh on 2016/7/5.
 */
public class AuthUtil {

	private static Logger log = Logger.getLogger(AuthUtil.class);

	/**
	 * 进行认证
	 * 
	 * @param request
	 * @param response
	 * @param url
	 *            第一次统一认证后跳转的地址
	 * @param haveLoginUrlPath
	 *            第二次统一认证通过后跳转的路径
	 * @param isTran
	 *            跳转标志
	 */
	public static void toAuth(HttpServletRequest request, HttpServletResponse response, String url,
			String haveLoginUrlPath, boolean isTran) throws IOException, ServletException {
		boolean debugMode = Context.getSyConf("PE_LOGIN_DEBUG_MODE", false); // 是否登录方式为开发模式
		if (!debugMode) {
			final String SSI_AUTH = "SSIAuth";
			final String SSI_SIGN = "SSISign";
			final String OPERATOR_SIGN_IN = "signIn";
			final String SSI_CREDENTIALS = "ssiCredentials";

			UserBean userBean = null;
			String SSIAuth = request.getParameter(SSI_AUTH);// paramBean.getStr().length()==0?null:paramBean.getStr(SSI_AUTH);
			String SSISign = request.getParameter(SSI_SIGN);// paramBean.getStr(SSI_SIGN).length()==0?null:paramBean.getStr(SSI_SIGN);

			String queryString = request.getQueryString();
			HttpSession httpSession = request.getSession();
			if (queryString != null && queryString.indexOf("todoServId") > 0) {
				log.info("---set--" + queryString + "----");
				httpSession.setAttribute("QUERY_STRING", queryString);
			}

			ServerSideAuthenticator logonsign = new ServerSideAuthenticator();
			// 统一认证服务器地址
			String serverIP = Context.getSyConf("AAM_AUTH_SERVER_IP_PORT", "122.16.173.106:11688");

			// site url 跳转地址
			String siteURL = url;
			// 统一认证版本
			String ssicVersion = Context.getSyConf("AAM_AUTH_SSIC_VERSION", "1.0");
			// 统一认证客户端密钥文件名
			String ssicClientKeyName = Context.getSyConf("AAM_AUTH_SSIC_C_KEY", "NOES");
			// 统一认证服务器端密钥文件名
			String ssicServerkeyName = Context.getSyConf("AAM_AUTH_SSIC_S_KEY", "SSIC");
			// 密钥密码
			String privKeyPasswd = Context.getSyConf("AAM_AUTH_PRIV_KEY_PASSWD", "1a2b3c");
			// 密钥路径
			String ssicKeyPath = Context.getSyConf("AAM_AUTH_KEY_PATH",
					BaseContext.app(BaseContext.APP.WEBINF).toString() + "/config");
			SSICService ssic = new SSICService(ssicServerkeyName);
			SSICService client = new SSICService(ssicClientKeyName);
			try {
				client.initialize(privKeyPasswd, ssicKeyPath);
				ssic.initialize(ssicKeyPath);
			} catch (PKIException e) {
				log.error(e.getMessage(), e);
			}
			PublicKey publicKey = ssic.getPublicKey();
			PrivateKey privateKey = client.getPrivateKey();
			logonsign.setServerName(serverIP);
			logonsign.setVersion(ssicVersion);
			logonsign.setServiceName(ssicClientKeyName);
			logonsign.setServiceURL(siteURL);
			logonsign.setOperation(OPERATOR_SIGN_IN);
			logonsign.setSSIPublicKey(publicKey);
			logonsign.setPrivateKey(privateKey);

			if (!logonsign.execute(request, response, SSIAuth, SSISign)) {
				log.info("----AAM 验证失败----");
				return;
			}
			Credentials cred = null;
			SSICUser user = null;
			Object obj = null;
			obj = request.getAttribute(SSI_CREDENTIALS);
			if (obj != null) {
				cred = (Credentials) obj;
				user = cred.getSSICUser();
				String userName = user.getUserName();
				userBean = UserMgr.getUserByLoginName(userName);
				if (null != userBean) {
					Context.setRequest(request); // 将request放入线程变量供userInfo等session的设置
					Context.setResponse(response); // 将response放入线程变量供下载等调用

					// 设用在线用户信息
					Context.setOnlineUser(request, userBean);
					if (isTran) {
						if (httpSession.getAttribute("QUERY_STRING") != null) {
							log.info("---get--" + queryString + "----");
							haveLoginUrlPath += "?" + httpSession.getAttribute("QUERY_STRING");
							httpSession.removeAttribute("QUERY_STRING");
						}
						response.sendRedirect(haveLoginUrlPath);
					}
				} else {
					throw new RuntimeException("无此用户！" + userName);
				}
			}
		} else {
			request.getRequestDispatcher("ksgogogo.jsp").forward(request, response);
			// if(isTran){
			// request.getRequestDispatcher("index.jsp").forward(request,response);
			// }
		}
	}
}
