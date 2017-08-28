package com.rh.core.icbc.sso;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.org.UserStateBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.util.Constant;
import com.rh.core.util.RequestUtils;

/**
 * @author yangjy
 */
public class NoSessionAuthFilter implements Filter {
    
    
    private static Log log = LogFactory.getLog(HandShakeFilterOis.class);
    
    @Override
    public void destroy() {
        
    }
    
    @Override
    public void doFilter(ServletRequest req ,
        ServletResponse res ,
        FilterChain chain) throws ServletException , IOException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        
        Context.setRequest(request); // 将request放入线程变量供userInfo等session的设置
        Context.setResponse(response); // 将response放入线程变量供下载等调用
        
        log.debug("<--------------NoSessionAuthFilter-- " + RequestUtils.getUrl(request));
        
        String token = request.getHeader(Constant.RHUS_SESSION);
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(Constant.RHUS_SESSION);
        }
        // Session 中记录的Token值
        String sessionToken = (String) RequestUtils.getSession(request,
                Constant.RHUS_SESSION, "");
        log.debug("token;sessionToken=" + token + ";" + sessionToken);
        
        if(Constant.YES.equals(request.getAttribute("_CLEAN_RHUS_FROM_PARAM"))) {
            token = sessionToken;
        }
        
        if (StringUtils.isNotBlank(token)) {
            // 如果Session里的Token与客户端传递的Token不相同，表示不是一个用户，则切换用户。
            if (sessionToken != null && !sessionToken.equals(token)) {
                changeLoginUser(request, response, token);
            }
            request.setAttribute(Constant.RHUS_SESSION, token);
        } else {
            if (StringUtils.isNotBlank(sessionToken)) {
                request.setAttribute(Constant.RHUS_SESSION, sessionToken);
            }
        }
        log.debug("--NoSessionAuthFilter----------------> ");
        chain.doFilter(req, res);
    }
    
    /**
     * 变更登录用户
     * @param request
     * @param response
     * @param token
     */
    private void changeLoginUser(HttpServletRequest request ,
        HttpServletResponse response ,
        String token) {
        try {
            UserStateBean userState = UserMgr.getUserStateByToken(token);
            if (userState == null || userState.isTimeOut()) { // TOKEN不存在或者过期，则要求用户重新登录
                cleanSession(request);
                return;
            }
            
            UserBean currUser = Context.getUserBean();
            // 如果和当前用户相同个，则不做处理
            if (currUser != null
                    && !currUser.getCode().equals(userState.getUserCode())) {
                String userCode = userState.getStr("USER_CODE");
                log.info("---------userCode > " + userCode + "-------------");
                UserBean userBean = UserMgr.getUser(userCode);
                if (userBean != null) {
                    setOnlineUser(request, response, userBean);
                    RequestUtils.setSession(request, Constant.RHUS_SESSION,
                            token);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    
    /**
     * 清除Session
     * 
     * @param request
     */
    private void cleanSession(HttpServletRequest request) {
        Context.clearOnlineUser(request.getSession().getId());
        RequestUtils.removeSession(request);
    }
    
    private void setOnlineUser(HttpServletRequest request ,
        HttpServletResponse response ,
        UserBean userBean) {
        Context.setRequest(request); // 将request放入线程变量供userInfo等session的设置
        Context.setResponse(response); // 将response放入线程变量供下载等调用
        // 设用在线用户信息
        Context.setOnlineUser(userBean);
    }
    
    @Override
    public void init(FilterConfig arg0) throws ServletException {
        
    }
    
}
