package com.rh.ts.pvlg;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;
import com.rh.core.util.Strings;
import com.rh.ts.pvlg.mgr.RoleMgr;
import com.rh.ts.util.RoleUtil;
import com.rh.ts.util.TsConstant;

public class RoleServ extends CommonServ {

	/**
	 * 获取用户所有功能权限,无权限值为0,有权限值为关联部门
	 * 
	 * @param paramBean
	 *            必填参数USER_CODE
	 * @return
	 */
	public OutBean getPvlgRole(ParamBean paramBean) {

		OutBean outBean = new OutBean();

		String userCode = paramBean.getStr("USER_CODE");

		if (Strings.isBlank(userCode)) {
			UserBean userBean = Context.getUserBean();
			userCode = userBean.getCode();
		}

		// 用户所有功能权限
		Bean allOpt = RoleUtil.getPvlgRole(userCode);

		outBean.setData(allOpt);

		return outBean.setOk();
	}

	/**
	 * 删除之后执行
	 */
	protected void afterDelete(ParamBean paramBean, OutBean outBean) {

		if (outBean.getStr("_MSG_").startsWith("OK")) {
			// 删除角色缓存
			RoleMgr.removeRoleCache(paramBean.getId(), TsConstant.SERV_ROLE);
			// 删除功能权限缓存
			RoleMgr.removeRoleCache(paramBean.getId(), TsConstant.SERV_ROLE_MOD);

			Bean whereBean = new Bean();
			whereBean.set("ROLE_ID", paramBean.getId());
			// 删除功能权限
			ServDao.delete(TsConstant.SERV_ROLE_MOD, whereBean);
		}
	}

	/**
	 * 保存后执行
	 */
	protected void afterSave(ParamBean paramBean, OutBean outBean) {

		if (outBean.getStr("_MSG_").startsWith("OK")) {
			// 重置角色缓存
			if (!Strings.isBlank(paramBean.getId())) {
				RoleMgr.setRoleCache(paramBean.getId());
			}
		}
	}

	/**
	 * 查询之前的拦截方法，由子类重载
	 * 
	 * @param paramBean
	 *            参数信息
	 */
	protected void beforeQuery(ParamBean paramBean) {
		
		String userCode = paramBean.getStr("USER_CODE");
		
		String sqlStr = RoleUtil.getPvlgSql(paramBean.getServId(), userCode);
		
		if(!Strings.isBlank(sqlStr)) {
			
//			paramBean.set(Constant.PARAM_WHERE, sqlStr);
//			paramBean.setQueryExtWhere(sqlStr);
		}
		
	}

}
