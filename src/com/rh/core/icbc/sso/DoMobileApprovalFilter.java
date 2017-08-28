package com.rh.core.icbc.sso;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.ParamBean;

public class DoMobileApprovalFilter implements Filter {

	private Logger log = Logger.getLogger(getClass());

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse rep, FilterChain chain)
			throws ServletException, IOException {
		HttpServletRequest request = null;
		HttpServletResponse response = null;
		
		request = (HttpServletRequest) req;
		response = (HttpServletResponse) rep;
		try {
			//获取请求参数
			request.setCharacterEncoding("UTF-8");
			ParamBean paramBean = new ParamBean(request);
			String servId = paramBean.getStr("sId").trim();
			servId = "," + servId + ",";
			int mobileFlag = paramBean.getInt("_SUPMOBILE_");
			String userAgent = request.getHeader("User-Agent");
			if(!(userAgent.contains("cochat:android") || userAgent.contains("cochat:ios"))){
				mobileFlag = 3;
			}
			String approvalServ = Context.getSyConf("SY_MOBILE_APPROVAL_SERV", ",PE_APPLICATION_INFO,");
			//如果是e办公请求的审批单，则跳转到审批单页面
			if (mobileFlag == 1 && approvalServ.indexOf(servId) >= 0) {
				//将用户信息放到上下文中
				if(Context.getSyConf("PE_SET_CURRENT_USER", false)){
					UserBean user = Context.getUserBean();
					if (user == null) {
						String ssicId = paramBean.getStr("ssicId");
						if (StringUtils.isNotEmpty(ssicId)) {
							try{
								user = UserMgr.getUserByLoginName(ssicId);
								Context.setOnlineUser(user);
							}catch(Exception e){
								log.error(e.getMessage(), e);
								errorResponse(response ,"{\"_MSG_\":\"ERROR,获取用户信息失败\"}");
								return;
							}
						}else{
							errorResponse(response ,"{\"_MSG_\":\"ERROR,参数ssicId为空\"}");
							return;
						}
					}
				}
				String url = "/roa/approval.jsp";
				request.getRequestDispatcher(url).forward(request, response);
				return;
			}
			chain.doFilter(req, rep);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			errorResponse(response ,"{\"_MSG_\":\"ERROR,"+e.getMessage()+"\"}");
		}
	}

	private void errorResponse(HttpServletResponse response, String string) {
		try {
			PrintWriter out = response.getWriter();
			out.write(string);
			out.flush();
			out.close();
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
