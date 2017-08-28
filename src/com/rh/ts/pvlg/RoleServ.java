package com.rh.ts.pvlg;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.org.UserBean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.dict.DictMgr;
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

		if (userCode == null) {
			UserBean userBean = Context.getUserBean();
			userCode = userBean.getCode();
		}

		if (Strings.isBlank(userCode)) {
			throw new TipException("获取权限失败，用户编码为空!");
		}

		// 获取用户权限
		Bean userOpt = RoleMgr.getRoleOptsByUser(userCode);

		if (userOpt == null) {
			userOpt = new Bean();
		}
		// 用户所有功能权限
		Bean allOpt = new Bean();
		// 字典设置的所有功能权限
		List<Bean> mdList = DictMgr.getItemList(TsConstant.DICT_ROLE_MOD);

		for (Bean md : mdList) {

			String mdCode = md.getStr("ITEM_CODE");

			Bean optBean = new Bean();

			List<Bean> optList = DictMgr.getItemList(mdCode);

			for (Bean opt : optList) {
				// 功能权限编码
				String optCode = opt.getStr("ITEM_CODE");

				if (userOpt.containsKey(mdCode)) {

					String userOptStr = userOpt.getStr(mdCode);
					// 用户已赋予权限
					if (Strings.containsValue(userOptStr, optCode)) {

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

}
