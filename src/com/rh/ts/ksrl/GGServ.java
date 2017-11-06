package com.rh.ts.ksrl;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.Constant;
import com.rh.ts.util.RoleUtil;


public class GGServ  extends CommonServ{


	public OutBean query(ParamBean paramBean) {
		return super.query(paramBean);
	}
	/**
	 * 过滤没有权限的查询
	 */
	// 查询前添加查询条件
	protected void beforeQuery(ParamBean paramBean) {
		UserBean userBean = Context.getUserBean();
		String userOdeptCode = userBean.getStr("ODEPT_CODE");
		//用户编码设置进roleParam里面，使其查询处用户所有的权限
		StringBuilder param_where = new StringBuilder();
		param_where.append(" AND EXISTS (SELECT d.code_path FROM sy_org_dept d ");
		param_where.append("WHERE  d.dept_code ='"+userOdeptCode+"' AND INSTR (d.CODE_PATH,S_ODEPT))");
		paramBean.set(Constant.PARAM_WHERE, param_where.toString());
	}
	/**
	 * 前台按钮根据用户的信息判断是否可见方法
	 * 
	 * @return
	 */
	public OutBean btnRoleFun(){
		OutBean outBean = new OutBean();
		UserBean userBean = Context.getUserBean();
		String userCode = userBean.getCode();
		Bean userRole = RoleUtil.getPvlgRole(userCode);
		Bean userPvlgToHT = (Bean) userRole.get("TS_QT_HT_PVLG");
		if(userPvlgToHT.getStr("show").equals("0")){
			outBean.set("hasRole", "2");
		}else{
			outBean.set("hasRole", "1");
		}
		return outBean;
		
	}
	
	
}
