package com.rh.ts.pvlg;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.Strings;
import com.rh.ts.pvlg.mgr.GroupMgr;
import com.rh.ts.util.TsConstant;

public class GroupUserServ extends CommonServ {

	/**
	 * 删除之后执行
	 */
	protected void afterDelete(ParamBean paramBean, OutBean outBean) {

		if (outBean.getStr("_MSG_").startsWith("OK")) {

			List<Bean> list = paramBean.getDeleteDatas();

			if (list != null && list.size() > 0) {

				Bean user = list.get(0);

				String groupId = user.getStr("G_ID");

				if (!Strings.isBlank(groupId)) {

					// 更新群组对应所有用户缓存
					setGroupToUserCache(list, false);
					// 更新用户对应所有群组缓存
					setUserToGroupsCache(list, false);
				}
			}
		}
	}

	/**
	 * 批量保存后执行
	 */
	protected void afterBatchSave(ParamBean paramBean, OutBean outBean) {

		if (outBean.getStr("_MSG_").startsWith("OK")) {

			List<Bean> list = paramBean.getBatchSaveDatas();

			if (list != null && list.size() > 0) {

				Bean bean = list.get(0);

				String groupId = bean.getStr("G_ID");

				if (!Strings.isBlank(groupId)) {

					// 更新群组对应所有用户缓存
					setGroupToUserCache(list, true);
					// 更新用户对应所有群组缓存
					setUserToGroupsCache(list, true);
				}
			}
		}
	}

	/**
	 * 重置用户的所有群组缓存, key：用户 val：群组
	 * 
	 * @param userCode
	 * @param groupId
	 * @param list
	 * @param addORdel
	 *            true 添加群组/false 删除群组
	 */
	private void setUserToGroupsCache(List<Bean> list, boolean addORdel) {

		for (Bean user : list) {

			String groupId = user.getStr("G_ID");

			String userCode = user.getStr("USER_CODE");

			String groupCodes = GroupMgr.getGroupCodes(userCode);

			if (addORdel) {
				groupCodes = Strings.mergeStr(groupCodes, groupId);
			} else {
				groupCodes = Strings.removeValue(groupCodes, groupId);
			}

			if (Strings.isBlank(groupCodes)) {
				GroupMgr.removeGroupCache(userCode, TsConstant.SERV_GROUP);
			} else {
				GroupMgr.updateGroupCache(userCode, TsConstant.SERV_GROUP, groupCodes);
			}

		}
	}

	/**
	 * 重置群组的所有用户缓存, key：群组 val：用户
	 * 
	 * @param userCode
	 * @param groupId
	 * @param list
	 * @param addORdel
	 *            true 添加群组/false 删除群组
	 */
	private void setGroupToUserCache(List<Bean> list, boolean addORdel) {

		for (Bean user : list) {

			String groupId = user.getStr("G_ID");

			String userCode = user.getStr("USER_CODE");

			String userCodes = GroupMgr.getGroupUserCodes(groupId);

			if (addORdel) {
				userCodes = Strings.mergeStr(userCodes, userCode);
			} else {
				userCodes = Strings.removeValue(userCodes, userCode);
			}

			if (Strings.isBlank(userCodes)) {
				GroupMgr.removeGroupCache(userCode, TsConstant.SERV_GROUP_USER);
			} else {
				GroupMgr.updateGroupCache(groupId, TsConstant.SERV_GROUP_USER, userCodes);
			}

		}
	}

}
