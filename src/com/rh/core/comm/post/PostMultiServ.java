package com.rh.core.comm.post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.UserStateBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.util.Constant;
import com.rh.core.util.RequestUtils;

public class PostMultiServ extends CommonServ {
	
	public OutBean getPostRoleByServId(ParamBean paramBean) {
		OutBean outBean = new OutBean();
		try {
			UserBean userBean = Context.getUserBean();
			
			String servId = paramBean.getStr("SERV_ID");

			ParamBean bean = new ParamBean();
			bean.set("SERV_ID", servId);
			bean.set("USER_CODE", userBean.getCode());
			List<Bean> linkList = ServDao.finds("SY_COMM_POST_SERV", bean);

			if (linkList.size() == 1) {
				Bean data = (Bean) linkList.get(0);
				Bean postBean = ServDao.find("SY_COMM_POST_ROLE",
						data.getStr("POST_ID"));
				if (postBean != null) {
					outBean.setData(postBean);
				}
			}
		} catch (Exception e) {
			log.error(e);
		}
		return outBean;
	}

	/**
	 * 更新用户缓存&兼岗系统变量赋值
	 * @param request
	 * @param response
	 * @param paramBean
	 */
	public void changeMultiPost(HttpServletRequest request,
			HttpServletResponse response, ParamBean paramBean) {
		try {
			String uri=request.getRequestURI();
			
			String servId = paramBean.getStr("sId");

			UserBean userBean = Context.getUserBean(request);

			if (userBean == null) {
				userBean = Context.getUserBean();
			}

			ParamBean bean = new ParamBean();
			bean.set("SERV_ID", servId);
			bean.set("USER_CODE", userBean.getCode());
			
			//查询列表更新userBean的条件
			if (uri.indexOf("stdListView.jsp") > -1) {
				bean.set("LIST_FLAG", 1);
			}
			
			List<Bean> linkList = ServDao.finds("SY_COMM_POST_SERV", bean);
			
			if (linkList.size() == 1) {
				Bean data = (Bean) linkList.get(0);
				Bean postBean = ServDao.find("SY_COMM_POST_ROLE", data.getStr("POST_ID"));
				if (postBean != null) {
					DeptBean deptBean = null;
					
					if (!postBean.getStr("DEPT_CODE").equals("")) {
						
						deptBean = OrgMgr.getDept(postBean.getStr("DEPT_CODE"));
						
					} else if (!postBean.getStr("ROLE_CODE").equals("")) {
						
						StringBuffer where = new StringBuffer();
						where.append(" AND ROLE_CODE = '").append(postBean.getStr("ROLE_CODE")).append("'");
						where.append(" AND USER_CODE = '").append(userBean.getCode()).append("'");
						
						List<Bean> ruList = ServDao.finds("SY_ORG_ROLE_USER", where.toString());
						
						if (ruList != null && ruList.size() == 1) {
							Bean ruBean = ruList.get(0);
							deptBean = OrgMgr.getDept(ruBean.getStr("DEPT_CODE"));
						}
					}

					if (deptBean != null && !userBean.getDeptCode().equals(deptBean.getCode())) {

						userBean.set("DEPT_CODE", deptBean.getCode());
						userBean.set("TDEPT_CODE", deptBean.getTDeptCode());
						userBean.set("ODEPT_CODE", deptBean.getODeptCode());
						userBean.set("CMPY_CODE", deptBean.getCmpyCode());
						userBean.set("CODE_PATH", deptBean.getCodePath());
						userBean.set("DEPT_NAME", deptBean.getName());
						userBean.set("ODEPT_NAME", deptBean.getODeptBean().getName());
						userBean.set("TDEPT_NAME", deptBean.getTDeptBean().getName());
						
						userBean.destroyDeptBean();
						userBean.destroyODeptBean();
						userBean.destroyTDeptBean();
						userBean.destroyCmpyBean();
					}
				}
			}
			
			List<Bean> multiList = new ArrayList<Bean>();
//	    	List<Bean> list = UserMgr.findUsers(userBean.getId());
			List<Bean> list = ServDao.finds("SY_ORG_ROLE_USER", " AND USER_CODE = '" + userBean.getId() + "'");
			Map<String, String> map = new HashMap<String, String>();
			for (Bean roleUser : list) {
				map.put(roleUser.getStr("DEPT_CODE"), "");
			}
			
			for (String deptCode : map.keySet()) {

				if (!userBean.getDeptCode().equals(deptCode)) {
					
					DeptBean dept = OrgMgr.getDept(deptCode);
					
					if (dept != null && dept.getId().length() > 0) {
						multiList.add(dept);
					}
				}
			} 
			
	    	userBean.set("MULTI_DEPT_LIST", multiList);
			
			createRhusSession(request, userBean);
			Context.setRequest(request); // 将request放入线程变量供userInfo等session的设置
			Context.setResponse(response); // 将response放入线程变量供下载等调用
			Context.setOnlineUser(userBean);
		} catch (Exception e) {
			log.error(e);
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
}
