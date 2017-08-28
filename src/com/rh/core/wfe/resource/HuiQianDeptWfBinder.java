package com.rh.core.wfe.resource;

import java.util.HashSet;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.RoleMgr;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;
import com.rh.core.util.JsonUtils;
import com.rh.core.wfe.WfAct;
import com.rh.core.wfe.def.WfNodeDef;

/**
 * 
 * @author anan
 *
 */
public class HuiQianDeptWfBinder implements ExtendBinder {

	@Override
	public ExtendBinderResult run(WfAct currentWfAct, WfNodeDef nextNodeDef, UserBean doUser) {
		Bean dataBean = currentWfAct.getProcess().getServInstBean();
        String extCls = nextNodeDef.getStr("NODE_EXTEND_CLASS");
        String configStr = "";
        
        String[] classes = extCls.split(",,");
        if (classes.length == 2) {
            configStr = classes[1];
        }
		Bean configBean = JsonUtils.toBean(configStr);
		
		//由于选择的是有效部门，但是在过滤人的时候，查询的是DEPT_CODE in () ， 所以在这块查一下这些有效部门下的部门
		String depts = dataBean.getStr(configBean.getStr("fieldStr"));
		String roleCode = configBean.getStr("roleCodes");
		String roleName = RoleMgr.getRole(roleCode).getName();
		
		SqlBean sql = new SqlBean();
		sql.andIn("DEPT_CODE", depts.split(","));
		sql.and("S_FLAG", Constant.YES_INT);
		sql.selects("DEPT_CODE, DEPT_NAME, DEPT_PCODE, DEPT_SORT");
		sql.limit(10000);
		
		List<Bean> deptCodeList = ServDao.finds(ServMgr.SY_ORG_DEPT, sql);
		
		HashSet<String> deptsHasRole = existUserInDeptRole(depts, roleCode);
		
		ExtendBinderResult result = new ExtendBinderResult();
		WfeBinder binder = new TreeWfeBinder();
		binder.setAutoSelect(WfeBinder.AUTO_SLECT_NO_MODIFY);
		binder.setBinderType(WfeBinder.NODE_BIND_ROLE);
		binder.setRoleCode(roleCode);
		binder.setMutilSelect(true);
		
		final String rootNodeId = "ROOTNODE";
		addRootNode(binder, rootNodeId);
		
		for (Bean deptBean: deptCodeList) {
	        Bean binderBean = new Bean();
	        String deptCode = deptBean.getStr("DEPT_CODE");
	        String deptName = deptBean.getStr("DEPT_NAME");
	        
	        binderBean.set("CODE", deptCode);
	        binderBean.set("NAME", deptName);
	        binderBean.set("NODETYPE", WfeBinder.DEPT_NODE_PREFIX);
	        binderBean.set("ID", WfeBinder.DEPT_NODE_PREFIX + ":" + deptBean.getStr("DEPT_CODE"));
	        binderBean.set("SORT", deptBean.getStr("DEPT_SORT"));
	        binderBean.set("LEVEL", 999);
	        binderBean.set("PID", WfeBinder.DEPT_NODE_PREFIX + ":" + rootNodeId);
	        binderBean.set("LEAF", 1);
	        
	        if (!deptsHasRole.contains(deptCode)) {
	        	binderBean.set("ERR_MSG", deptName + " 无角色为 " + roleName +  " 的人员");	
	        }
	        binder.addTreeBean(binderBean);
        } 
        
		result.setBinder(binder);
		return result;
	}
	
	/**
	 * 
	 * @param depts 部门编码以逗号分隔
	 * @param roleCode 角色编码
	 * @return 部门内存在该角色
	 */
    private HashSet<String> existUserInDeptRole(String depts, String roleCode) {
    	HashSet<String> deptSet = new HashSet<String>();
    	
    	SqlBean sql = new SqlBean();
    	sql.andIn("TDEPT_CODE", depts.split(","));
    	sql.and("ROLE_CODE", roleCode);
    	sql.and("S_FLAG", Constant.YES_INT);
    	
    	sql.selects("TDEPT_CODE");
    	RoleMgr.authStateSql(sql);
    	
    	List<Bean> roleUsers = ServDao.finds(ServMgr.SY_ORG_ROLE_USER, sql);
    	for (Bean roleUser: roleUsers) {
    		deptSet.add(roleUser.getStr("TDEPT_CODE"));
    	}
    	
		return deptSet;
	}

	/**
     * 返回的数据添加一个根节点
     * @param rootNodeId 根节点的ID
     */
    private void addRootNode(WfeBinder binder, String rootNodeId) {
        Bean newDeptBean = new Bean();
        newDeptBean.set("CODE", rootNodeId);
        newDeptBean.set("NAME", "工作流人员");
        newDeptBean.set("NODETYPE", WfeBinder.DEPT_NODE_PREFIX);
        newDeptBean.set("ID", WfeBinder.DEPT_NODE_PREFIX + ":" + rootNodeId);
        newDeptBean.set("SORT", 0);
        newDeptBean.set("LEVEL", 0);
        
        binder.addTreeBean(newDeptBean);
    }
	
	/**
	 * 
	 * @param nodeDef 节点定义
	 * @return 字段定义
	 */
	public static String getConfigField(WfNodeDef nodeDef) {
        String extCls = nodeDef.getStr("NODE_EXTEND_CLASS");
        String configStr = "";
        
        String[] classes = extCls.split(",,");
        if (classes.length == 2) {
            configStr = classes[1];
        }
		Bean configBean = JsonUtils.toBean(configStr);
		
		String fieldStr = configBean.getStr("fieldStr").trim();
		
		return fieldStr;
	}
	
	/**
	 * 获取工作流节点配置错误信息
	 * @param nodeDef 节点定义
	 * @return 字段定义
	 */
	public static String getConfigErroeMsg(WfNodeDef nodeDef) {
        String extCls = nodeDef.getStr("NODE_EXTEND_CLASS");
        String configStr = "";
        
        String[] classes = extCls.split(",,");
        if (classes.length == 2) {
            configStr = classes[1];
        }
		Bean configBean = JsonUtils.toBean(configStr);
		
		String errorMsg = configBean.get("errorMsg", "empty").trim();
		
		return errorMsg;
	}
	
	/**
	 * 
	 * @param nodeDef 节点定义
	 * @return 节点是否会商节点
	 */
	public static boolean isNodeHuiShang(WfNodeDef nodeDef) {
//		String extCls = nodeDef.getStr("NODE_EXTEND_CLASS");
//		if (extCls.indexOf("HuiQianExtWfBinder") != -1) {
//			return true;
//		}
		
		return false;
	}
}
