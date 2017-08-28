package com.rh.core.comm.todo;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.comm.entity.EntityMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.util.Constant;

/**
 * 
 * @author yangjy
 *
 */
public class TodoHisServ extends CommonServ{

	/**
	 * 查询前添加查询条件
	 * 
	 * @param paramBean
	 */
	protected void beforeQuery(ParamBean paramBean) {
		StringBuilder strWhere = new StringBuilder();
		// 查询委托
		if (paramBean.containsKey("agentFlag") && paramBean.getBoolean("agentFlag")) {
			String servCode = paramBean.getServId();
			// 指定待办类型
			OutBean agtWhereBean = ServMgr.act(ServMgr.SY_ORG_USER_TYPE_AGENT, "getTodoAgentWhereByUserCode",
					paramBean);
			if (!agtWhereBean.isOk()) {
				throw new TipException("获取委办记录失败");
			}
			strWhere.append(agtWhereBean.getData());
			paramBean.setServId(servCode);
			// 查询本人
		} else {
			String currUserCode = Context.getUserBean().getCode();
			// 指定本人
			strWhere.append(" and OWNER_CODE = '" + currUserCode + "'");
		}
		strWhere.append(" and TODO_CATALOG <= 1");

		String extWhere = paramBean.getStr("_extWhere");
		if (extWhere.startsWith("{")) {
			extWhere = strWhere.toString();
		} else {
			extWhere = extWhere + strWhere.toString();
		}
		paramBean.set("_extWhere", extWhere);
	}

	/**
	 * 查询后添加查询条件
	 * 
	 * @param paramBean
	 * @param outBean
	 *            查询结果
	 */
	protected void afterQuery(ParamBean paramBean, OutBean outBean) {
		List<Bean> dataList = outBean.getList(Constant.RTN_DATA);
		for (Bean data : dataList) {
			// 另外的处理
			Bean eb = EntityMgr.getEntity(data.getStr("TODO_OBJECT_ID1"));
			if (eb != null) {
				data.set("S_WF_USER_STATE", eb.getStr("S_WF_USER_STATE"));
			}
		}
	}
}
