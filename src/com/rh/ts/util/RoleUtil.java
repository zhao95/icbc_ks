package com.rh.ts.util;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.util.Strings;
import com.rh.ts.pvlg.mgr.RoleMgr;

public class RoleUtil {

	public static String getPvlgSql(String servId, String userCode) {

		if (Strings.isBlank(userCode)) {
			UserBean userBean = Context.getUserBean();
			userCode = userBean.getCode();
		}

		boolean ismgr = com.rh.core.org.mgr.UserMgr.existInRoles(userCode, "RADMIN");

		if (ismgr) {
			return "";
		}

		// 用户所有功能权限
		Bean allOpt = getPvlgRole(userCode);

		String pvlgkey = servId +"_PVLG";

		Bean servPvlg = allOpt.getBean(pvlgkey);

		SqlBean sql = new SqlBean();

		if (servPvlg != null && !servPvlg.isEmpty()) {
			
			Bean listPvlg = servPvlg.getBean("list"); //查询的权限

			String deptCodes = listPvlg.getStr("ROLE_DCODE");

			if (!Strings.isBlank(deptCodes)) {

				Bean param = new Bean().set("SERV_ID", servId);

				List<Bean> list = ServDao.finds(TsConstant.SERV_CTLG_CONF, param);

				if (list != null && !list.isEmpty()) {

					Bean conf = list.get(0);

					String mod = conf.getStr("SERV_MOD");

					String col = conf.getStr("SERV_ITEM");

					String[] dcodes = deptCodes.split(",");

					if (!Strings.isBlank(mod)) {

						for (String dcode : dcodes) {
							dcode = mod + "_" + dcode;
						}
					}
					sql.andIn(col, dcodes);
				}
			}
		}

		if (sql.isEmpty()) { // 无权限访问
			return " and 1 = 2";
		}
		return sql.toString();
	}

	public static Bean getPvlgRole(String userCode) {
		// 用户所有功能及可见权限
		Bean allOpt = new Bean();

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
						// 关联机构层级的机构编码
						String orgsLv = getOrgsByLv(userCode, optPvlg.getStr("ROLE_ORG_LV"));
						// 关联部门和机构层级合并
						String orgs = Strings.mergeStr(orgsLv, optPvlg.getStr("ROLE_DCODE"));

						optPvlg.set("ROLE_DCODE", orgs);

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
	 * 根据关联机构层级 获取机构
	 * 
	 * @param deptLvs
	 * @param userCode
	 * @return
	 */
	private static String getOrgsByLv(String userCode, String deptLvs) {

		if (Strings.isBlank(deptLvs) || Strings.isBlank(userCode)) {
			return "";
		}

		UserBean userBean = Context.getUserBean();

		int curLv = userBean.getODeptLevel() - 1; // 用户机构层级

		String[] deptLvArg = deptLvs.split(",");

		String orgs = "";

		for (int i = 0; i < deptLvArg.length; i++) {

			DeptBean odeptBean = null;

			if (!Strings.isBlank(deptLvArg[i])) { // 机构层级

				int lv = Integer.parseInt(deptLvArg[i]);

				int differ = curLv - lv; // 当前机构层级 - 指定机构层级

				for (int j = 1; j <= differ; j++) {

					odeptBean = userBean.getODeptBean().getParentDeptBean().getODeptBean();

					System.out.println("上级 lv=" + odeptBean.getLevel() + "   curLv=" + curLv);
				}

				if (odeptBean != null && !odeptBean.isEmpty()) {

					Strings.addValue(orgs, odeptBean.getODeptCode());
				}
			}
		}

		return orgs;
	}

}
