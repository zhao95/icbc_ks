package com.rh.ts.pvlg;

import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.util.Constant;
import com.rh.ts.pvlg.mgr.GroupMgr;
import com.rh.ts.util.TsConstant;

public class GroupServ extends CommonServ {

	/**
	 * 删除之后执行
	 */
	protected void afterDelete(ParamBean paramBean, OutBean outBean) {

		if (outBean.getStr("_MSG_").startsWith("OK")) {

			List<Bean> list = paramBean.getDeleteDatas();

			if (list != null && list.size() > 0) {

				for (Bean group : list) {
					// 删除群组缓存
					GroupMgr.removeGroupCache(group.getId(), TsConstant.SERV_GROUP);

					ParamBean whereBean = new ParamBean();
					whereBean.set("G_ID", group.getId());

					// 级联删除绑定角色 (如果设置级联删除，不需要执行删除。安全起见代码删除)
					ServMgr.act(TsConstant.SERV_GROUP_ROLE, ServMgr.ACT_DELETE, whereBean);

					// 级联删除绑定用户 (如果设置级联删除，不需要执行删除。安全起见代码删除)
					ServMgr.act(TsConstant.SERV_GROUP_USER, ServMgr.ACT_DELETE, whereBean);
				}
			}
		}
	}

	/**
	 * 保存后执行
	 */
	protected void afterSave(ParamBean paramBean, OutBean outBean) {

		if (outBean.getStr("_MSG_").startsWith("OK")) {

			GroupMgr.setGroupCache(paramBean.getId());
		}
	}

	/**
	 * 批量保存后执行
	 */
	protected void afterBatchSave(ParamBean paramBean, OutBean outBean) {

		if (outBean.getStr("_MSG_").startsWith("OK")) {

			List<Bean> list = paramBean.getBatchSaveDatas();

			if (list != null && list.size() > 0) {
				
				for (Bean bean : list) {
					GroupMgr.setGroupCache(bean.getId());
				}
			}
		}
	}

	//查询前添加查询条件
		protected void beforeQuery(ParamBean paramBean) {
			ParamBean param = new ParamBean();
			String  ctlgModuleName="GROUP";
			String  serviceName="TS_PVLG_GROUP";
			param.set("paramBean", paramBean);
			param.set("ctlgModuleName", ctlgModuleName);
			param.set("serviceName",serviceName);
			ServMgr.act("TS_UTIL", "userPvlg", param);
		}	
}
