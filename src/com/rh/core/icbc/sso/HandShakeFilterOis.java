package com.rh.core.icbc.sso;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icbc.ssic.base.Credentials;
import com.icbc.ssic.base.PKIException;
import com.icbc.ssic.base.SSICService;
import com.rh.core.base.Context;
import com.rh.core.comm.post.PostMultiServ;
import com.rh.core.org.UserBean;
import com.rh.core.org.UserStateBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.Constant;
import com.rh.core.util.RequestUtils;

/**
 * 老OIS系统点击待办时使用的过滤器，用作身份认证
 * @author if
 *
 */
public class HandShakeFilterOis extends HttpServlet implements Filter {

	/** serialVersionUID */
	private static final long serialVersionUID = -6328449615764352937L;
	/** log */
    private static Log log = LogFactory.getLog(HandShakeFilterOis.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws ServletException, IOException {
		HttpServletRequest request = null;
		HttpServletResponse response = null;
		UserBean userBean = null;
		
		try {
			request = (HttpServletRequest)req;
			response = (HttpServletResponse)res;
			request.setCharacterEncoding("UTF-8");
			ParamBean paramBean = new ParamBean(request);
			boolean needHandShake = Context.getSyConf("PE_NEED_HANDSHAKE_FLAG", true);
			
			// 需要握手
			if (needHandShake) {
				String ssicId = null;
				
				// 如果是使用ssiAuth和ssiSign认证方式
				final String ssiAuth = paramBean.getStr("ssiAuth");
				final String ssiSign = paramBean.getStr("ssiSign");
				log.info("-----HandShakeFilterOis----- ssiAuth = " + ssiAuth + ", ssiSign = " + ssiSign);
				
				if (StringUtils.isNotEmpty(ssiAuth) && StringUtils.isNotEmpty(ssiSign)) {
					// 统一认证验证
					String clientKeyName = SsicManager.getInstance().getSsicClientKeyName();
					SSICService client = new SSICService(clientKeyName);
					try {
						client.initialize(SsicManager.getInstance().getPrivKeyPasswd(), SsicManager.getInstance().getSsicKeyPath());
					} catch (PKIException e) {
						log.error("-----HandShakeFilterOis----- : 统一认证出错, e = " + e.getMessage(), e );
					}
					PublicKey publicKey = client.getPublicKey();
					PrivateKey privateKey = client.getPrivateKey();
					
					log.info("-----HandShakeFilterOis----- : publicKey = " + publicKey.toString());
					log.info("-----HandShakeFilterOis----- : privateKey = " + privateKey.toString());
					
					Credentials localCredentials = new Credentials(ssiAuth, ssiSign);
					boolean isValid = localCredentials.isvalidate(publicKey, privateKey);
					
					log.info("-----HandShakeFilterOis----- : isValid = " + isValid);
					
					if (!isValid) {
						log.error("-----HandShakeFilterOis----- : 统一认证不通过，请确认密钥文件是否一致！");
					} else {
						ssicId = localCredentials.getSSICUser().getUserName();
						//保存登录时从父系统传递的参数
						HandShakeFilterUtil.saveLoginParam(paramBean, ssicId);
						log.info("-----HandShakeFilterOis----- : 获取统一认证号成功！");
					}
				}
				
				// 如果是使用加密loginName认证方式
				String encUserInfo = paramBean.getStr("encUserInfo");
				if (StringUtils.isNotEmpty(encUserInfo)) {
					if(paramBean.isNotEmpty("_SUPMOBILE_") && encUserInfo.indexOf("%25") > 0 ){
						log.info("---encUserInfo will urldecode----");
						encUserInfo = URLDecoder.decode(URLDecoder.decode(encUserInfo, "utf-8"), "utf-8");
						log.info("------encUserInfo------"+encUserInfo);
					}
					ssicId = CryptString.decryptUserInfo(encUserInfo);
				}
				
				// 如果前面都没有，尝试从Session中获取
//				if (StringUtils.isEmpty(ssicId)) {
//					ssicId = (String) RequestUtils.getSession(request, "SSIC_ID", null);
//				}
				
				if (StringUtils.isNotEmpty(ssicId)) {
					log.info("-----HandShakeFilterOis----- : SSIC_ID = " + ssicId);
					try {
						userBean = UserMgr.getUserByLoginName(ssicId);
						
					} catch (Exception e) {
						log.error(e.getMessage());
						userBean = null;
					}
					
					if (null != userBean) {
					    createRhusSession(request, userBean);
					    
						Context.setRequest(request); // 将request放入线程变量供userInfo等session的设置
						Context.setResponse(response); // 将response放入线程变量供下载等调用
						//设用在线用户信息
						Context.setOnlineUser(userBean);
//						RequestUtils.setSession(request, "SSIC_ID", ssicId);
						log.info("-----HandShakeFilterOis----- : 统一认证成功！");
					}
				}
			}
			new PostMultiServ().changeMultiPost(request, response, paramBean);
			chain.doFilter(req, res);
		} catch (Exception e) {
		    log.error(e.getMessage(), e);
		}
	}

    private void createRhusSession(HttpServletRequest request, UserBean userBean) {
        UserStateBean userState = UserMgr.getUserState(userBean.getCode());
        String userToken = null;
        if (userState != null && !userState.isTimeOut()
                && userState.isNotEmpty("USER_TOKEN")) {
            userToken = userState.getStr("USER_TOKEN");
        } else {
            userToken = RandomStringUtils.randomAlphanumeric(15);
        }
        RequestUtils.setSession(request, Constant.RHUS_SESSION, userToken);
        request.setAttribute(Constant.RHUS_SESSION, userToken);
        request.setAttribute("_CLEAN_RHUS_FROM_PARAM", Constant.YES);
    }
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
