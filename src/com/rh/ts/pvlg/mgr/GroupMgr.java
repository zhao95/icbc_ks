package com.rh.ts.pvlg.mgr;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.comm.CacheMgr;
import com.rh.core.comm.ConfMgr;
import com.rh.core.serv.ServDao;
import com.rh.core.util.DateUtils;
import com.rh.core.util.Strings;
import com.rh.ts.util.TsConstant;

/**
 * 工行考试系统 群组管理器
 * 
 * @author zjl
 *
 */
public class GroupMgr {

	private static Log log = LogFactory.getLog(GroupMgr.class);

	/**
	 * 获取用户所有群组编码 (逗号相隔)
	 * 
	 * @param userCode
	 * @return
	 */
	public static String getGroupCodes(String userCode) {

		String groupCodes = (String) getGroupCache(userCode, TsConstant.SERV_GROUP);

		if (Strings.isBlank(groupCodes)) {

			Bean queryUser = new Bean().set("USER_CODE", userCode).set("S_FLAG", 1);
			// 查询用户所有群组
			List<Bean> userList = ServDao.finds(TsConstant.SERV_GROUP_USER, queryUser);

			for (Bean user : userList) {
				// 用户群组id
				String groupCode = user.getStr("G_ID");

				if (Strings.isBlank(groupCodes)) {

					groupCodes = groupCode;
				} else {
					groupCodes += "," + groupCode;
				}
			}

			if (!Strings.isBlank(groupCodes)) {
				// 去掉重复群组
				groupCodes = Strings.removeSame(groupCodes);

				String[] gCArrg = groupCodes.split(",");

				String dlGcodes = "";

				for (String gcode : gCArrg) {

					if (groupInDeadLine(gcode)) {

						if (Strings.isBlank(dlGcodes)) {

							dlGcodes = gcode;
						} else {
							dlGcodes += "," + gcode;
						}
					}
				}

				updateGroupCache(userCode, TsConstant.SERV_GROUP, dlGcodes);
			}
		}

		if (groupCodes == null) {
			groupCodes = "";
		}

		return groupCodes;
	}

	/**
	 * 获取群组信息 包含 绑定角色编码(逗号相隔) 绑定人员编码(逗号相隔)
	 * 
	 * @param groupId
	 * @return
	 */
	public static Bean getGroup(String groupId) {

		Bean groupBean = (Bean) getGroupCache(groupId, TsConstant.SERV_GROUP);

		if (groupBean == null || groupBean.isEmpty()) {

			groupBean = ServDao.find(TsConstant.SERV_GROUP, groupId);

			if (groupBean != null && !groupBean.isEmpty()) {

				if (!inDeadLine(groupBean.getStr("G_DEAD_BEGIN"), groupBean.getStr("G_DEAD_END"))) {
					return null;
				}

				updateGroupCache(groupId, TsConstant.SERV_GROUP, groupBean);
			}
		} else {

			if (!inDeadLine(groupBean.getStr("G_DEAD_BEGIN"), groupBean.getStr("G_DEAD_END"))) {
				return null;
			}
		}

		String roleCodes = getGroupRoleCodes(groupId);
		if (!Strings.isBlank(roleCodes)) {
			// 缓存获取群组绑定角色
			groupBean.set("ROLE_CODES", roleCodes);
		}

		String userCodes = getGroupUserCodes(groupId);
		if (!Strings.isBlank(userCodes)) {
			// 缓存获取群组绑定人员
			groupBean.set("USER_CODES", userCodes);
		}

		return groupBean;
	}

	/**
	 * 更新群组缓存
	 * 
	 * @param groupId
	 */
	public static void setGroupCache(String groupId) {

		if (Strings.isBlank(groupId)) {

			throw new TipException("更新群组缓存失败, 角色编码为空!");
		}

		Bean param = ServDao.find(TsConstant.SERV_GROUP, groupId);

		if (param != null && !param.isEmpty()) {

			updateGroupCache(groupId, TsConstant.SERV_GROUP, param);
		} else {

			removeGroupCache(groupId, TsConstant.SERV_GROUP);
		}

	}

	/**
	 * 获取群组绑定用户编码(逗号相隔)
	 * 
	 * @param groupId
	 * @return
	 */
	public static String getGroupUserCodes(String groupId) {

		if (!groupInDeadLine(groupId)) { // 判断群组是否在有效期
			return "";
		}

		String userCodes = (String) getGroupCache(groupId, TsConstant.SERV_GROUP_USER);

		if (Strings.isBlank(userCodes)) {

			Bean param = new Bean();
			param.set("G_ID", groupId);
			param.set("S_FLAG", 1);

			// 查询群组关联的用户
			List<Bean> userList = ServDao.finds(TsConstant.SERV_GROUP_USER, param);

			for (Bean user : userList) {

				if (Strings.isBlank(userCodes)) {

					userCodes = user.getStr("USER_CODE");
				} else {
					userCodes += "," + user.getStr("USER_CODE");
				}
			}

			if (!Strings.isBlank(userCodes)) {
				// 去掉重复用户
				userCodes = Strings.removeSame(userCodes);

				updateGroupCache(groupId, TsConstant.SERV_GROUP_USER, userCodes);
			} else {
				userCodes = "";
			}
		}

		return userCodes;

	}

	/**
	 * 获取群组绑定角色编码 (逗号相隔)
	 * 
	 * @param groupId
	 * @return
	 */
	public static String getGroupRoleCodes(String groupId) {

		if (!groupInDeadLine(groupId)) { // 判断群组是否在有效期
			return "";
		}

		String roleCodes = (String) getGroupCache(groupId, TsConstant.SERV_GROUP_ROLE);

		if (Strings.isBlank(roleCodes)) {

			Bean param = new Bean();
			param.set("G_ID", groupId);
			param.set("S_FLAG", 1);

			// 查询群组关联的角色
			List<Bean> roleList = ServDao.finds(TsConstant.SERV_GROUP_ROLE, param);

			for (Bean role : roleList) {

				if (Strings.isBlank(roleCodes)) {

					roleCodes = role.getStr("ROLE_CODE");
				} else {
					roleCodes += "," + role.getStr("ROLE_CODE");
				}
			}

			if (!Strings.isBlank(roleCodes)) {
				// 去掉重复角色
				roleCodes = Strings.removeSame(roleCodes);

				updateGroupCache(groupId, TsConstant.SERV_GROUP_ROLE, roleCodes);
			} else {
				roleCodes = "";
			}
		}

		return roleCodes;

	}

	/**
	 * 当前时间是否在有效期
	 * 
	 * @param begin
	 * @param end
	 * @return true 有效/ false 过期
	 */
	private static boolean inDeadLine(String begin, String end) {

		String current = DateUtils.getDatetime();

		boolean deadline = true; // 权限有效期

		try {
			
			boolean isTrue = ConfMgr.getConf("TS_PVLG_GROUP_DEADLINE", true);//是否启用 群组有效期

			if (!Strings.isBlank(begin) && begin.length() == 10) {
				begin = begin.trim() + " 00:00:00";
			}

			if (!Strings.isBlank(end) && end.length() == 10) {
				end = end.trim() + " 23:59:59";
			}

			if (isTrue && !Strings.isBlank(begin)) { // 是否 begin早于current
				deadline = DateUtils.isBefore(begin, current);
			}

			if (isTrue && !Strings.isBlank(end)) { // 是否 current早于end
				deadline = DateUtils.isBefore(current, end);
			}

		} catch (Exception e) {
			log.error("群组有效期格式错误:" + e.getMessage());
			deadline = false;
		}

		return deadline;
	}

	/**
	 * 群组是否在有效期
	 * 
	 * @param groupCode
	 * @return
	 */
	private static boolean groupInDeadLine(String groupCode) {

		boolean deadline = true;

		Bean groupBean = (Bean) getGroupCache(groupCode, TsConstant.SERV_GROUP);

		if (groupBean != null && !groupBean.isEmpty()) {

			String begin = groupBean.getStr("G_DEAD_BEGIN");
			String end = groupBean.getStr("G_DEAD_END");

			deadline = inDeadLine(begin, end);
		}

		return deadline;
	}
	
	/**
	 * 更新群组用户缓存
	 * 
	 * @param groupId
	 */
	public static void setGroupUserCache(String groupId) {

		if (Strings.isBlank(groupId)) {
			throw new TipException("更新群组人员缓存失败, 群组编码为空!");
		}

		// 清除缓存
		removeGroupCache(groupId, TsConstant.SERV_GROUP_USER);
		// 重新添加缓存
		getGroupUserCodes(groupId);
	}

	public static void setGroupRoleCache(String groupId) {

		if (Strings.isBlank(groupId)) {
			throw new TipException("更新群组角色缓存失败, 群组编码为空!");
		}

		// 清除缓存
		removeGroupCache(groupId, TsConstant.SERV_GROUP_ROLE);
		// 重新添加缓存
		getGroupRoleCodes(groupId);
	}

	/**
	 * 删除缓存
	 * 
	 * @param key  群组ID
	 * @param type 群组 TsConstant.SERV_GROUP,群组角色TsConstant.SERV_GROUP_ROLE,群组资源TsConstant.SERV_GROUP_USER 
	 * @return
	 */
	public static void removeGroupCache(String key, String type) {

		CacheMgr.getInstance().remove(type + "-" + key, TsConstant.SERV_GROUP);
	}

	/**
	 * 更新缓存
	 * 
	 * @param key  群组ID
	 * @param type 群组 TsConstant.SERV_GROUP,群组角色TsConstant.SERV_GROUP_ROLE,群组资源TsConstant.SERV_GROUP_USER 
	 * @param obj  Bean
	 */
	public static void updateGroupCache(String key, String type, Object obj) {

		CacheMgr.getInstance().set(type + "-" + key, obj, TsConstant.SERV_GROUP);
	}

	/**
	 * 获取缓存
	 * 
	 * @param key  群组ID
	 * @param type 群组 TsConstant.SERV_GROUP,群组角色TsConstant.SERV_GROUP_ROLE,群组资源TsConstant.SERV_GROUP_USER 
	 * @return
	 */
	public static Object getGroupCache(String key, String type) {
		return CacheMgr.getInstance().get(type + "-" + key, TsConstant.SERV_GROUP);
	}

}
