package com.rh.core.wfe.resource;

import java.util.HashSet;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.wfe.WfAct;
import com.rh.core.wfe.db.WfNodeInstDao;
import com.rh.core.wfe.def.WfLineDef;
import com.rh.core.wfe.def.WfNodeDef;

/**
 * 需求
 * 部门内送交会签， 
 * A节点 可以送交B点(部门内主办人员会签) ， 也可以送交C点(部门内会办人员会签)
 * 在送交C点的时候，去掉已经在B点上办理过的人员 
 * 
 * 解决
 * 在AC的线上启用组织资源过滤，并填上此过滤类  
 * @author anan
 *
 */
public class DelRepeatUserExtWfBinder implements ExtendBinder {

	@Override
	public ExtendBinderResult run(WfAct currentWfAct, WfNodeDef nextNodeDef, UserBean doUser) {
		String piId = currentWfAct.getProcess().getId();
		
		String curNodeCode = currentWfAct.getCode();
		String nextNodeCode = nextNodeDef.getStr("NODE_CODE");
		
		//找到从currentWfAct 出去的线 ， 判断
		List<WfLineDef> nextLineDefBeanList = currentWfAct.getProcess().getProcDef()
                .findLineDefList(curNodeCode);
		
		// currentWfAct 与 nextNodeDef 之间有一条线， 找另外的一条线
		HashSet<String> flowedUsers = getFlowedUsers(piId, curNodeCode, nextNodeCode, nextLineDefBeanList);
		
		//找到本部门内所有人， 减去 已经走过的
		List<UserBean> usersInDept = UserMgr.getUsersInDepts(doUser.getTDeptCode());
		
		StringBuilder sb = new StringBuilder();
		
		for (UserBean userBean: usersInDept) {
			if (!flowedUsers.contains(userBean.getCode())) {
				sb.append(userBean.getCode()).append(",");
			}
		}
		
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}
		
		ExtendBinderResult result = new ExtendBinderResult();
		result.setDeptIDs("");
		result.setRoleCodes("");
		result.setUserIDs(sb.toString());
		
		result.setBindRole(false);
		return result;
	}

	/**
	 * 
	 * @param piId 流程实例ID
	 * @param curNodeCode 当前节点CODE
	 * @param nextNodeCode 下一步的节点CODE
	 * @param nextLineDefBeanList 能送交的线的列表
	 * @return 已经流经过的用户
	 */
	private HashSet<String> getFlowedUsers(String piId, String curNodeCode,
			String nextNodeCode, List<WfLineDef> nextLineDefBeanList) {
		HashSet<String> flowedUsers = new HashSet<String>();
		for (WfLineDef lineDef: nextLineDefBeanList) {
			if (lineDef.getStr("SRC_NODE_CODE").equals(curNodeCode) 
					&& lineDef.getStr("TAR_NODE_CODE").equals(nextNodeCode)) { //当前送的这条线
				continue;
			}
			
			//其他线已经送交了的人
			String lineCode = lineDef.getStr("LINE_CODE");
			
			SqlBean sql = new SqlBean();
			sql.set("PI_ID", piId);
			sql.set("PRE_LINE_CODE", lineCode);
			
			sql.selects("TO_USER_ID");
			
			List<Bean> toUsers = ServDao.finds(WfNodeInstDao.SY_WFE_NODE_INST_SERV, sql);
			
			for (Bean user: toUsers) {
				if (!flowedUsers.contains(user.getStr("TO_USER_ID"))) {
					flowedUsers.add(user.getStr("TO_USER_ID"));
				}
			}
		}
		
		return flowedUsers;
	}
}
