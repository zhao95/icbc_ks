package com.rh.ts.ksrl;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.icbc.basedata.KSSendTipMessageServ;
import com.rh.core.org.UserBean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.Constant;
import com.rh.ts.pvlg.PvlgUtils;
import com.rh.ts.util.RoleUtil;


public class GGServ  extends CommonServ{


	public OutBean query(ParamBean paramBean) {
		return super.query(paramBean);
	}
	/**
	 * 过滤没有权限的查询
	 */
	// 查询前添加查询条件
	protected void findMsg(ParamBean paramBean) {
		UserBean userBean = Context.getUserBean();
		String userOdeptCode = userBean.getStr("ODEPT_CODE");
		//用户编码设置进roleParam里面，使其查询处用户所有的权限
		StringBuilder param_where = new StringBuilder();
		param_where.append(" AND EXISTS (SELECT d.code_path FROM sy_org_dept d ");
		param_where.append("WHERE  d.dept_code ='"+userOdeptCode+"' AND INSTR (d.CODE_PATH,S_ODEPT))");
		paramBean.set(Constant.PARAM_WHERE, param_where.toString());
	}
	
	// 查询前添加查询条件
		protected void beforeQuery(ParamBean paramBean) {
			ParamBean param = new ParamBean();
  			param.set("paramBean", paramBean);
//  			param.set("ctlgModuleName", ctlgModuleName);
  			param.set("fieldName","CTLG_PCODE");
  			param.set("serviceName", paramBean.getServId());
  			PvlgUtils.setOrgPvlgWhere(param);	
		}
		
	/**
	 * 前台按钮根据用户的信息判断是否可见方法
	 * 返回值为 1 可见  2 不可见 
	 * @return
	 */
	public OutBean btnRoleFun(){
		OutBean outBean = new OutBean();
		UserBean userBean = Context.getUserBean();
		String userCode = userBean.getCode();
		String userLoginName = userBean.getLoginName();
		Bean userPvlgToHT = RoleUtil.getPvlgRole(userCode,"TS_QT_HT");
		Bean userPvlgToHTBean = (Bean) userPvlgToHT.get("TS_QT_HT_PVLG");
		String userPvlgToHT_Flag = userPvlgToHTBean.getStr("show");
		if(userPvlgToHT_Flag!=""){
			if(userCode.equals("admin")|| userLoginName.equals("admin")){
				outBean.set("hasRole", "1");
			}else{
				if(userPvlgToHTBean.getStr("show").equals("0")){
					outBean.set("hasRole", "2");
				}else{
					outBean.set("hasRole", "1");
				}
			}
		}
		//测试邮件发送
		/*Bean tipBean = new Bean();
		tipBean.set("USER_CODE", "admin");
		tipBean.set("tipMsg", "qingjiattip");
		new KSSendTipMessageServ().sendTipMessageBeanForICBC(tipBean,"qjStar");*/
		
		return outBean;
	}
}
