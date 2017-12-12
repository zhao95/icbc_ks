package com.rh.ts.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.util.Strings;
import com.rh.ts.pvlg.mgr.RoleMgr;

public class RoleUtil {

	public static Bean getPvlgRole(String userCode) {
		return getPvlgRole(userCode, "");
	}
	
	public static Bean getPvlgRole(String userCode,String servId) {
		// 用户所有功能及可见权限
		Bean allOpt = new Bean();

		if (Strings.isBlank(userCode)) {
			throw new TipException("获取权限失败，用户编码为空!");
		}

		// 获取用户所有角色的功能
		Bean userOpt = RoleMgr.getRoleOptsByUser(userCode,servId);

		if (userOpt == null) {
			userOpt = new Bean();
		}

		// 字典设置的所有模块
		List<Bean> mdList = null;
		
		if (Strings.isBlank(servId)) {
			mdList = DictMgr.getItemList(TsConstant.DICT_ROLE_MOD);
		} else {
			mdList = new ArrayList<Bean>();
			Bean bean = new Bean().set("ITEM_CODE", servId + "_PVLG");
			mdList.add(bean);
		}

		for (Bean md : mdList) {
			// 模块编码 (服务编码_PVLG)
			String mdCode = md.getStr("ITEM_CODE");

			Bean optBean = new Bean();

			List<Bean> optList = DictMgr.getItemList(mdCode);

			for (Bean opt : optList) {
				// 功能编码
				String optCode = opt.getStr("ITEM_CODE");

				if (userOpt.containsKey(mdCode)) {

					Bean userOptBean = userOpt.getBean(mdCode);
					
					if (userOptBean == null || userOptBean.isEmpty()) {
						
						optBean.put(optCode, 0);
						
					} else if (userOptBean.containsKey(optCode)) {// 用户已赋予功能
						// 该功能可见范围
//						Bean optPvlg = userOpt.getBean("PVLG-" + mdCode).getBean(optCode);
						Bean optPvlg = userOpt.getBean(mdCode).getBean(optCode);
						// 关联机构层级的机构编码
						String orgsLv = getOrgsByLv(userCode, optPvlg.getStr("ROLE_ORG_LV"));
						// 关联部门和机构层级合并
						String orgs = Strings.mergeStr(orgsLv, optPvlg.getStr("ROLE_DCODE"));
						
						if (!Strings.isBlank(orgs) && !orgs.equals("0")) {
							
							String[] orgsArgs = orgs.split(",");
							
							Arrays.sort(orgsArgs);
							
							orgs = Strings.toString(orgsArgs);
							
						}

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

		int curLv = userBean.getODeptLevel(); // 用户机构层级 总行1，分行2，支行3

		String[] deptLvArg = deptLvs.split(",");

		String orgs = "";

		for (int i = 0; i < deptLvArg.length; i++) {

			DeptBean odeptBean = null;

			if (!Strings.isBlank(deptLvArg[i])) { // 机构层级

				int lv = Integer.parseInt(deptLvArg[i]);

				int differ = curLv - lv; // 当前机构层级 - 指定机构层级
				
				if(differ == 0 ) {
					odeptBean = userBean.getODeptBean();
				} else {
					
					odeptBean = userBean.getODeptBean();

					for (int j = 1; j <= differ; j++) {
	
						odeptBean = odeptBean.getParentDeptBean().getODeptBean();
	
//						System.out.println("上级 lv=" + odeptBean.getLevel() + "   curLv=" + curLv);
					}
				}

				if (odeptBean != null && !odeptBean.isEmpty()) {

					orgs = Strings.addValue(orgs, odeptBean.getODeptCode());
				}
			}
		}

		return orgs;
	}
}
