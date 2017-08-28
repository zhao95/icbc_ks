package com.rh.ts.pvlg.mgr;

import java.util.ArrayList;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.comm.CacheMgr;
import com.rh.core.serv.ServDao;
import com.rh.core.util.Strings;
import com.rh.ts.pvlg.RoleBean;
import com.rh.ts.util.TsConstant;

/**
 * 工行考试系统 角色管理器
 * 
 * @author zjl
 *
 */
public class RoleMgr {

	/**
	 * 获取用户所有角色
	 * 
	 * @param userCode
	 * @return
	 */
	public static String getRoleCodes(String userCode) {

		String roleCodes = "";

		// 缓存获取角色list 包含功能
		List<RoleBean> list = getRoleList(userCode);

		for (RoleBean role : list) {

			if (Strings.isBlank(roleCodes)) {

				roleCodes = role.getCode();
			} else {
				roleCodes += "," + role.getCode();
			}

		}
		return roleCodes;
	}

	/**
	 * 获取 用户所有角色list 包含功能功能
	 * 
	 * @param userCode
	 *            用户编码
	 * @return
	 */
	public static List<RoleBean> getRoleList(String userCode) {

		List<RoleBean> list = new ArrayList<RoleBean>();

		if (!Strings.isBlank(userCode)) {
			// 缓存获取用户群组
			String groupCodes = GroupMgr.getGroupCodes(userCode);

			for (String group : groupCodes.split(",")) {

				if (!Strings.isBlank(group)) {
					// 缓存获取群组
					Bean groupBean = GroupMgr.getGroup(group);

					if (groupBean != null) {

						String roleCodes = groupBean.getStr("ROLE_CODES");

						for (String role : roleCodes.split(",")) {

							if (!Strings.isBlank(role)) {
								// 缓存获取角色信息
								RoleBean roleBean = RoleMgr.getRole(role);
								if (roleBean != null) {
									list.add(roleBean);
								}
							}
						}
					}
				}
			}
		}
		return list;
	}

	/**
	 * 获取角色包含功能
	 * 
	 * @param roleId
	 * @return
	 */
	public static RoleBean getRole(String roleId) {

		RoleBean roleBean = (RoleBean) getCacheRole(roleId, TsConstant.SERV_ROLE);

		if (roleBean == null || roleBean.isEmpty()) {

			roleBean = new RoleBean(ServDao.find(TsConstant.SERV_ROLE, roleId));

			if (!roleBean.isEmpty()) {

				updateRoleCache(roleId, TsConstant.SERV_ROLE, roleBean);
			}
		}

		if (!roleBean.isEmpty()) {
			// 缓存获取角色功能
			List<Bean> list = getRoleOpts(roleId);

			if (list != null && list.size() > 0) {

				roleBean.set("OPT_LIST", list);
			}
		}

		return roleBean;
	}

	/**
	 * 判断用户是否有权限
	 * 
	 * @param userCode
	 *            用户编码
	 * @param moduleCode
	 *            模块编码
	 * @param optCode
	 *            功能编码
	 * @return true/false
	 */
	public static boolean isRoleOpts(String userCode, String moduleCode, String optCode) {

		Bean optBean = getRoleOptsByUser(userCode);

		if (optBean == null) {
			return false;
		}
		String opts = optBean.getStr(moduleCode);

		return Strings.containsValue(opts, optCode);
	}

	/**
	 * 判断用户是否有权限
	 * 
	 * @param userCode
	 *            用户编码
	 * @param optCode
	 *            功能编码
	 * @return true/false
	 */
	public static boolean isRoleOpts(String userCode, String optCode) {

		Bean optBean = getRoleOptsByUser(userCode);

		if (optBean == null) {
			return false;
		}

		for (Object val : optBean.values()) {
			if (Strings.containsValue(val.toString(), optCode)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 获取用户所有功能 (多角色功能合并)
	 * 
	 * @param userCode
	 * @return
	 */
	public static Bean getRoleOptsByUser(String userCode) {
		// 缓存获取用户 所有角色list 包含功能
		List<RoleBean> rolelist = getRoleList(userCode);

		Bean roleOptBean = new Bean();

		Bean opts = new Bean();

		// 遍历角色
		for (RoleBean role : rolelist) {

			// 缓存获取角色功能
			List<Bean> rolemdList = role.getOptList();

			// 遍历角色功能
			for (Bean md : rolemdList) {

				String mdCode = md.getStr("MD_CODE");

				String mdVal = md.getStr("MD_VAL");

				if (!Strings.isBlank(mdVal)) {

					// 合并角色功能字符串 逗号隔开
					if (roleOptBean.containsKey(mdCode)) {

						mdVal = Strings.mergeStr(roleOptBean.getStr(mdCode), mdVal);
					}

					roleOptBean.put(mdCode, mdVal);

					Bean pvlg = getOptsPvlg(role, opts, mdVal);
					if (!pvlg.isEmpty()) {
						roleOptBean.put("PVLG-" + mdCode, pvlg);
					}
				}
			}
		}
		return roleOptBean;
	}

	/**
	 * 获取角色功能 的 关联部门
	 * @param role
	 * @param opts
	 * @param mdVal
	 * @return Bean  key: PVLG+功能模块编码
	 */
	private static Bean getOptsPvlg(RoleBean role, Bean opts, String mdVal) {

		Bean rtnBean = new Bean();

		String[] valArg = mdVal.split(",");

		for (String opt : valArg) {

			if (!Strings.isBlank(opt)) {

				Bean pvlg = new Bean();

				String orgLv = opts.getBean(opt).getStr("ROLE_ORG_LV");

				String dCode = opts.getBean(opt).getStr("ROLE_DCODE");

				if (role.getRoleType() == 1) {

					if (!Strings.isBlank(orgLv)) {

						if (!Strings.containsValue(orgLv, String.valueOf(role.getOrgLv()))) {

							orgLv += "," + role.getOrgLv();
						}
					} else {
						orgLv = role.getOrgLv() + "";
					}

				} else if(role.getRoleType() == 2) {
					
					if (!Strings.isBlank(dCode)) {

						if (!Strings.containsValue(dCode, role.getDCode())) {

							dCode += "," + role.getDCode();
						}
					} else {
						dCode = role.getDCode();
					}
					
				}

				if (!Strings.isBlank(dCode)) {
					pvlg.set("ROLE_DCODE", dCode);
				}

				if (!Strings.isBlank(orgLv)) {
					pvlg.set("ROLE_ORG_LV", orgLv);
				}

				if (!pvlg.isEmpty()) {
					if (opts.containsKey(opt)) {
						opts.put(opt, pvlg);
					} else {
						opts.put(opt, pvlg);
					}
					rtnBean.set(opt, pvlg);
				}
			}
		}
		return rtnBean;
	}

	/**
	 * 获取 某个角色的功能
	 * 
	 * @param roleCode
	 * @return
	 */
	private static List<Bean> getRoleOpts(String roleCode) {

		@SuppressWarnings("unchecked")
		List<Bean> list = (List<Bean>) getCacheRole(roleCode, TsConstant.SERV_ROLE_MOD);

		if (list == null || list.isEmpty()) {

			Bean param = new Bean().set("ROLE_ID", roleCode).set("S_FLAG", 1);

			list = ServDao.finds(TsConstant.SERV_ROLE_MOD, param);

			if (!list.isEmpty()) {

				updateRoleCache(roleCode, TsConstant.SERV_ROLE_MOD, list);
			}
		}
		return list;
	}

	/**
	 * 重置角色缓存
	 * 
	 * @param roleCode
	 */
	public static void setRoleCache(String roleCode) {

		if (Strings.isBlank(roleCode)) {
			throw new TipException("更新角色缓存失败, 角色编码为空!");
		}

		RoleBean roleBean = new RoleBean(ServDao.find(TsConstant.SERV_ROLE, roleCode));

		if (!roleBean.isEmpty()) {

			updateRoleCache(roleCode, TsConstant.SERV_ROLE, roleBean);
		} else {
			removeRoleCache(roleCode, TsConstant.SERV_ROLE);
		}
	}

	public static void setRoleOptsCache(String roleCode) {

		if (Strings.isBlank(roleCode)) {
			throw new TipException("更新角色功能缓存失败, 角色编码为空!");
		}

		Bean param = new Bean().set("ROLE_ID", roleCode).set("S_FLAG", 1);

		List<Bean> list = ServDao.finds(TsConstant.SERV_ROLE_MOD, param);

		if (!list.isEmpty()) {

			updateRoleCache(roleCode, TsConstant.SERV_ROLE_MOD, list);
		} else {

			removeRoleCache(roleCode, TsConstant.SERV_ROLE_MOD);
		}
	}

	public static void removeRoleCache(String key, String module) {

		CacheMgr.getInstance().remove(module + "-" + key, TsConstant.SERV_ROLE);
	}

	private static void updateRoleCache(String key, String module, Object obj) {

		CacheMgr.getInstance().set(module + "-" + key, obj, TsConstant.SERV_ROLE);
	}

	private static Object getCacheRole(String key, String module) {

		return CacheMgr.getInstance().get(module + "-" + key, TsConstant.SERV_ROLE);
	}

}
