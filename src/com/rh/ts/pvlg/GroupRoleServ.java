package com.rh.ts.pvlg;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.Strings;
import com.rh.ts.pvlg.mgr.GroupMgr;

public class GroupRoleServ extends CommonServ {

	/**
	 * 删除之后执行
	 */
	protected void afterDelete(ParamBean paramBean, OutBean outBean) {

		if (outBean.getStr("_MSG_").startsWith("OK")) {

			List<Bean> list = paramBean.getDeleteDatas();

			if (list != null && list.size() > 0) {

				Bean role = list.get(0);

				GroupMgr.setGroupRoleCache(role.getStr("G_ID"));
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

				if (!Strings.isBlank(bean.getStr("G_ID"))) {

					GroupMgr.setGroupRoleCache(groupId);
				}
			}
		}
	}

}
