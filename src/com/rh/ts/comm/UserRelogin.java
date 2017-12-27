package com.rh.ts.comm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rh.core.base.Context;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.Strings;

public class UserRelogin extends CommonServ {

	/**
	 * 多机构用户切换身份，更新session
	 * @param paramBean
	 * @return
	 */
	public OutBean relogin(ParamBean paramBean) {

		OutBean outBean = new OutBean();
		
		HttpServletRequest request = Context.getRequest();
		
		HttpServletResponse response = Context.getResponse();

		UserBean userBean = Context.getUserBean(request);

		if (userBean == null) {
			userBean = Context.getUserBean();
		}

		String allDcodes = userBean.getDeptCodeSecond();

		String dept = paramBean.getStr("DEPT_CODE");

		if (dept.equals(userBean.getDeptCodeM()) || Strings.containsValue(allDcodes, dept)) {

			DeptBean deptBean = OrgMgr.getDept(dept);

			userBean.set("DEPT_CODE", deptBean.getCode());
			userBean.set("TDEPT_CODE", deptBean.getTDeptCode());
			userBean.set("ODEPT_CODE", deptBean.getODeptCode());
			userBean.set("CODE_PATH", deptBean.getCodePath());

			userBean.destroyDeptBean();
			userBean.destroyTDeptBean();
			userBean.destroyODeptBean();
			
			Context.setRequest(request); // 将request放入线程变量供userInfo等session的设置
			Context.setResponse(response); // 将response放入线程变量供下载等调用
			Context.setOnlineUser(userBean);
		}

		return outBean;
	}
}
