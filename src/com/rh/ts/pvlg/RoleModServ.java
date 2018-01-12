package com.rh.ts.pvlg;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.Strings;
import com.rh.ts.pvlg.mgr.RoleMgr;

public class RoleModServ extends CommonServ {

	public OutBean saveAll(ParamBean paramBean) {
		OutBean outBean = new OutBean();

		this.batchSave(paramBean);

		return outBean.setOk();
	}

	/**
	 * 批量保存之后执行
	 */
	protected void afterBatchSave(ParamBean paramBean, OutBean outBean) {

//		if (outBean.getStr("_MSG_").startsWith("OK")) {
			
			List<Bean> list = paramBean.getBatchSaveDatas();

			if (list != null && list.size() > 0) {
				Bean save = list.get(0);
				// 重置功能权限缓存
				if (!Strings.isBlank(save.getStr("ROLE_ID"))) {
					RoleMgr.setRoleOptsCache(save.getStr("ROLE_ID"));
				}
			}
//		}
	}

	/**
	 * 获取用户所有角色 权限
	 * 
	 * @param paramBean
	 * @return
	 */
	public Bean getPvlgByUser(ParamBean paramBean) {
		OutBean outBean = new OutBean();

		String userCode = paramBean.getStr("USER_CODE");

		List<RoleBean> list = RoleMgr.getRoleList(userCode);

		Bean roleOptBean = new Bean();

		for (RoleBean role : list) {

			for (Bean md : role.getOptList()) {

				String mdCode = md.getStr("MD_CODE");

				String mdVal = md.getStr("MD_VAL");

				if (roleOptBean.containsKey(mdCode)) {

					mdVal = Strings.mergeStr(roleOptBean.getStr(mdCode), mdVal);
				}
				roleOptBean.put(mdCode, mdVal);
			}
		}

		outBean.setData(roleOptBean);

		return outBean.setOk();
	}

}
