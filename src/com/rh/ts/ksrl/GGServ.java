package com.rh.ts.ksrl;

import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.Constant;


public class GGServ  extends CommonServ{


	public OutBean query(ParamBean paramBean) {
		return super.query(paramBean);
	}
	// 查询前添加查询条件
	protected void beforeFinds(ParamBean paramBean) {
		
		UserBean userBean = Context.getUserBean();
		String userOdeptCode = userBean.getStr("ODEPT_CODE");
		//用户编码设置进roleParam里面，使其查询处用户所有的权限
		StringBuilder param_where = new StringBuilder();
		param_where.append(" AND  (SELECT d.code_path from sy_org_dept d  ");
		param_where.append(" WHERE d.dept_code ='"+userOdeptCode+"') like CONCAT('%',S_ODEPT,'^%') ");
		paramBean.set(Constant.PARAM_WHERE, param_where.toString());
	}
}
