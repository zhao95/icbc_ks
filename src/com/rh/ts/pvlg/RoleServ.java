package com.rh.ts.pvlg;

import java.util.ArrayList;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.util.Constant;
import com.rh.core.util.Strings;
import com.rh.ts.pvlg.mgr.RoleMgr;
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

		// 用户所有功能权限
		Bean allOpt = getPvlgRole(userCode);

		outBean.setData(allOpt);

		return outBean.setOk();
	}

	private Bean getPvlgRole(String userCode) {
		// 用户所有功能及可见权限
		Bean allOpt = new Bean();

		if (userCode == null) {
			UserBean userBean = Context.getUserBean();
			userCode = userBean.getCode();
		}

		if (Strings.isBlank(userCode)) {
			throw new TipException("获取权限失败，用户编码为空!");
		}

		// 获取用户所有角色的功能
		Bean userOpt = RoleMgr.getRoleOptsByUser(userCode);

		if (userOpt == null) {
			userOpt = new Bean();
		}

		// 字典设置的所有模块
		List<Bean> mdList = DictMgr.getItemList(TsConstant.DICT_ROLE_MOD);

		for (Bean md : mdList) {
			// 模块编码 (服务编码_PVLG)
			String mdCode = md.getStr("ITEM_CODE");

			Bean optBean = new Bean();

			List<Bean> optList = DictMgr.getItemList(mdCode);

			for (Bean opt : optList) {
				// 功能编码
				String optCode = opt.getStr("ITEM_CODE");

				if (userOpt.containsKey(mdCode)) {

					String userOptStr = userOpt.getStr(mdCode);
					// 用户已赋予功能
					if (Strings.containsValue(userOptStr, optCode)) {
						// 该功能可见范围
						Bean optPvlg = userOpt.getBean("PVLG-" + mdCode).getBean(optCode);

						optBean.put(optCode, optPvlg);

					} else {
						optBean.put(optCode, 0);
					}
				} else {
					optBean.put(optCode, 0);
				}
			}

			allOpt.put(mdCode, optBean);
		}

		return allOpt;

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
	protected void beforeFinds(ParamBean paramBean) {

		String userCode = paramBean.getStr("USER_CODE");

		// 用户所有功能权限
		Bean allOpt = getPvlgRole(userCode);

		String pvlgkey = "PVLG_" + paramBean.getServId();

		Bean servPvlg = allOpt.getBean(pvlgkey);

		Bean listPvlg = servPvlg.getBean("list");

		if (listPvlg != null && !listPvlg.isEmpty()) {

			String deptLvs = listPvlg.getStr("ROLE_ORG_LV");

			String deptCodes = listPvlg.getStr("ROLE_DCODE");

			SqlBean sql = new SqlBean();

			Bean org = getOrgsByLv(deptLvs,userCode);
			
			Bean org1 = getOrgBydept(deptCodes);

			paramBean.set(Constant.PARAM_WHERE, sql.toString());
		}
	}

	/**
	 * 根据关联机构层级 获取所有机构 (包含下属机构)
	 * @param deptLvs
	 * @param userCode
	 * @return
	 */
	private Bean getOrgsByLv(String deptLvs, String userCode) {

		UserBean userBean = UserMgr.getUser(userCode);

		int curLv = userBean.getODeptLevel() - 1; // 用户机构层级

		String[] deptLvArg = deptLvs.split(",");

		String[] odept = new String[deptLvArg.length];

		for (int i = 0; i < deptLvArg.length; i++) {

			DeptBean odeptBean = userBean.getODeptBean();

			if (!Strings.isBlank(deptLvArg[i])) { // 机构层级

				int lv = Integer.parseInt(deptLvArg[i]);

				int differ = curLv - lv; // 当前机构层级 - 指定机构层级

				for (int j = 1; j <= differ; j++) {

					odeptBean = odeptBean.getParentDeptBean().getODeptBean();

					System.out.println("上级 lv=" + odeptBean.getLevel() + "   curLv=" + curLv);
				}

				odept[i] = odeptBean.getODeptCode();
			}
		}

		Bean org = new Bean();

		for (String dept : odept) {

			List<DeptBean> list = OrgMgr.getSubOrgs(userBean.getCmpyCode(), dept);

			for (DeptBean bean : list) {

				org.set(bean.getCode(), bean.getODeptCode());
			}

		}

		return org;
	}

	/**
	 * 根据关联指定部门 获取部门所有机构(包含下属机构)
	 * @param deptCodes
	 * @return
	 */
	private Bean getOrgBydept(String deptCodes) {
		
		Bean org = new Bean();

		String[] deptArg = deptCodes.split(",");

		for (int i = 0; i < deptArg.length; i++) {

			String deptCode = deptArg[i];

			if (!Strings.isBlank(deptCode)) { // 自定义部门

				DeptBean deptBean = OrgMgr.getOdept(deptCode);

				if (deptBean.getType() == 2) { // 机构

				} else { // 部门

					StringBuffer sub = new StringBuffer();
					sub.append(" and ( S_DEPT = ?");
					sub.append(" or S_TDEPT = ?) ");

				}

			}
		}

		return org;
	}

}
