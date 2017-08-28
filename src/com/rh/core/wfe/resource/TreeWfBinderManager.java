package com.rh.core.wfe.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.mgr.RoleMgr;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;
import com.rh.core.wfe.WfAct;
import com.rh.core.wfe.def.WfNodeDef;
import com.rh.core.wfe.util.WfeConstant;

/**
 * 工作流组织资源绑定
 * @author yangjy
 */
public class TreeWfBinderManager extends WfBinderManager {
    private static int SQL_TYPE_DEPT = 1;
    private static int SQL_TYPE_ODEPT = 2;
    private static int SQL_TYPE_NONE = 0;
    
    private static Log log = LogFactory.getLog(TreeWfBinderManager.class);
    
    private WfeBinder binder = new TreeWfeBinder();
    
    
    /**
     * @param aWfNodeDef 节点定义
     * @param aWfAct 节点实例
     * @param aDoUser 办理人对象
     */
	public TreeWfBinderManager(WfNodeDef aWfNodeDef, WfAct aWfAct, UserBean aDoUser) {
		super(aWfNodeDef, aWfAct, aDoUser);
		binder.setDoUserBean(aDoUser);
	}
    
    /**
     * 添加绑定的人
     * 
     * @param resCodes 人员列表
     */
    private void addUser(String resCodes) {
        String definedUsers = "";
        
        if (resCodes.equals(WfeConstant.USER_YUDING_DRAFT_USER)) {
            // 起草人
            WfAct firstAct = this.wfAct.getProcess().getFirstWfAct();
            definedUsers = firstAct.getNodeInstBean().getStr("TO_USER_ID");
        } else if (resCodes.equals(WfeConstant.USER_YUDING_CURRENT_USER)) {
            // 当前用户
            definedUsers = this.doUser.getCode();
        } else if (resCodes.equals(WfeConstant.USER_YUDING_TARGET_NODE_LAST_USER)) {
            // 指定节点的最后一个办理用户。
            WfAct lastAct = this.wfAct.getProcess().getLastDoneWfAct(this.wfNodeDef.getStr("NODE_CODE"));
            if (lastAct != null) {
                Bean nodeInstBean = lastAct.getNodeInstBean();
                
                if (nodeInstBean.isNotEmpty("TO_USER_ID")) {
                    definedUsers = nodeInstBean.getStr("TO_USER_ID");
                }
            } 
            
            // 未找到办理用户则报错
            if (definedUsers.length() == 0) {
                throw new TipException("未找到办理用户");
            }
        } else {
            // 指定用户
            definedUsers = resCodes;
        }
        List<UserBean> userBeanList = new ArrayList<UserBean>();
        for (String userCode : definedUsers.split(",")) {
        	if (excludeUsers.contains(userCode)) { //在排除的人员里面
        		continue;
        	}
        	
            UserBean userBean = UserMgr.getUser(userCode);
            if (userBean.getInt("USER_STATE") > 1) {
                continue;
            }
            userBeanList.add(userBean);
            Bean binderBean = new Bean();
            binderBean.set("CODE", userBean.getCode());
            binderBean.set("NAME", userBean.getName());
            binderBean.set("NODETYPE", WfeBinder.USER_NODE_PREFIX);
            binderBean.set("ID", WfeBinder.USER_NODE_PREFIX + ":" + userBean.getCode() + "^" + userBean.getStr("DEPT_CODE"));
            binderBean.set("SORT", userBean.getSort());
            binderBean.set("LEVEL", 999);
            binderBean.set("PID", WfeBinder.DEPT_NODE_PREFIX + ":" + userBean.getDeptCode());
            
            binder.addTreeBean(binderBean);
            
            this.recursivePDept(userBean.getDeptCode());
        }
        binder.setUserBeanList(userBeanList);
    }
    
    /**
     * 添加 返回树结构的节点
     * 
     * @param userBeanList 用户对象列表
     */
    private void addBindNode(List<Bean> userBeanList) {
        for (Bean userBean : userBeanList) {
        	if (excludeUsers.contains(userBean.getStr("USER_CODE"))) { //在排除的人员里面
        		continue;
        	}
        	
            if (userBean.getInt("USER_STATE") > 1) {
                continue;
            }
            Bean binderBean = new Bean();
            binderBean.set("CODE", userBean.getStr("USER_CODE"));
            binderBean.set("NAME", userBean.getStr("USER_NAME"));
            binderBean.set("NODETYPE", WfeBinder.USER_NODE_PREFIX);
            binderBean.set("ID", WfeBinder.USER_NODE_PREFIX + ":" + userBean.getStr("USER_CODE") + "^" + userBean.getStr("DEPT_CODE"));
            binderBean.set("SORT", userBean.getStr("USER_SORT"));
            binderBean.set("LEVEL", 999);
            binderBean.set("PID", WfeBinder.DEPT_NODE_PREFIX + ":" + userBean.getStr("DEPT_CODE"));
            
            binder.addTreeBean(binderBean);
            
            this.recursivePDept(userBean.getStr("DEPT_CODE"));
        }
    }
    
    
    
    /**
     * @param deptCodeStr 过滤部门编码串
     * @param roleCodeStr 过滤角色串
     * @param isSql 是否sql语句
     */
    private void addUserInDeptRole(String deptCodeStr , String roleCodeStr, int sqlType) {
        List<Bean> userList = new ArrayList<Bean>();
        if (sqlType == SQL_TYPE_ODEPT) {
            userList = UserMgr.getUsersBeanbyDeptSql(deptCodeStr, roleCodeStr);
        } else if (sqlType == SQL_TYPE_DEPT) {
            userList = getUsersBeanbyDeptSql(deptCodeStr, roleCodeStr);
        } else {
            if (!StringUtils.isEmpty(deptCodeStr) && !StringUtils.isEmpty(roleCodeStr)) { // 过滤部门 +角色
                userList = UserMgr.getUserListByDept(deptCodeStr, roleCodeStr);
            } else if (!deptCodeStr.isEmpty()) { // 过滤部门
                userList = UserMgr.getUserListByDept(deptCodeStr);
            } else if (!StringUtils.isEmpty(roleCodeStr)) { // 过滤角色
                userList = UserMgr.getUserListByRole(roleCodeStr, this.cmpyCode);
            } else {
                // 返回所有人
                // List<DeptBean> deptList = OrgMgr.getAllDepts(Context.getUserBean().getCmpyCode());
                DeptBean deptOrg = OrgMgr.getParentOrg(this.doUser.getDeptCode());
                List<DeptBean> deptList = OrgMgr.getChildDepts(this.cmpyCode, deptOrg.getCode());
                StringBuilder deptStr = new StringBuilder();
                for (DeptBean deptBean : deptList) {
                    deptStr.append(deptBean.getCode());
                    deptStr.append(",");
                }
                String deptString = deptStr.toString().substring(0, deptStr.length() - 1);
                
                userList = UserMgr.getUserListByDept(deptString);
            }
        }
        
        addBindNode(userList);
        binder.setUserBeanListByBeanList(userList);
    }
    
    /**
     * @return 节点上定义的部门 串
     */
    private Bean getDeptCodeList() {
        if (deptBinder.getMode() == WfeConstant.NODE_BIND_MODE_ALL) { // 全部部门
            // 本机构下所有部门列表
//            List<DeptBean> deptList = this.getSubDeptList(this.cmpyCode, this.doUser.getODeptCode());
//            
//            return new Bean().set(Constant.RTN_DATA, deptList);
            StringBuilder sql = getSubDeptSql(this.doUser.getODeptCode());
            return new Bean().set("SQL_MODE", SQL_TYPE_DEPT).set("SQL_CONTENT", sql);
        } else if (deptBinder.getMode() == WfeConstant.NODE_BIND_MODE_PREDEF) { // 预定义
            List<DeptBean> deptList = new ArrayList<DeptBean>();
            log.debug("the dept mode is predef " + deptBinder.getResCodes() + deptBinder.getMode());
            
            if (deptBinder.getResCodes().equals(WfeBinder.PRE_DEF_SELF_DEPT)) {
                // 本处室下所有子目录
                log.debug("the dept mode is predef 本部门" + deptBinder.getResCodes());
//                deptList = this.getSubDeptList(this.cmpyCode, this.doUser.getDeptCode());
                StringBuilder sql = getSubDeptSql(this.doUser.getDeptCode());
                return new Bean().set("SQL_MODE", SQL_TYPE_DEPT).set("SQL_CONTENT", sql);
            } else if (deptBinder.getResCodes().equals(WfeBinder.PRE_DEF_SELF_DEPT_LEVEL)) {
                // 本部门下所有的子目录
//                deptList = this.getSubDeptList(this.doUser.getCmpyCode(), this.doUser.getTDeptCode());
                StringBuilder sql = getSubDeptSql(this.doUser.getTDeptCode());
                return new Bean().set("SQL_MODE", SQL_TYPE_DEPT).set("SQL_CONTENT", sql);
            } else if (deptBinder.getResCodes().equals(WfeBinder.PRE_DEF_HIGHER_DEPT_LEVEL)) {
                // 上级机构
                deptList = this.getParentLevelDeptList(this.doUser);
                if (this.topLevel > 1) {
                    topLevel = topLevel - 1;
                }
            } else if (deptBinder.getResCodes().equals(WfeBinder.PRE_DEF_INIT_TOP_DEPT)) {
                // 拟稿部门 通过 起草节点的 DONE_USER_ID 取用户
                UserBean firstUser = getFirstActDoneUser();
                StringBuilder sql = getSubDeptSql(firstUser.getTDeptCode());
                return new Bean().set("SQL_MODE", SQL_TYPE_DEPT).set("SQL_CONTENT", sql);
//                deptList = this.getSubDeptList(firstUser.getCmpyCode(), firstUser.getTDeptCode());
            } else if (deptBinder.getResCodes().equals(WfeBinder.PRE_DEF_INIT_DEPT)) {
                // 拟稿处室
                UserBean firstUser = getFirstActDoneUser();
//                deptList = this.getSubDeptList(firstUser.getCmpyCode(), firstUser.getDeptCode());
                StringBuilder sql = getSubDeptSql(firstUser.getDeptCode());
                return new Bean().set("SQL_MODE", SQL_TYPE_DEPT).set("SQL_CONTENT", sql);
            } else if (deptBinder.getResCodes().equals(WfeBinder.PRE_DEF_INIT_ORG)) {
                //拟稿机构
                UserBean firstUser = getFirstActDoneUser();
//            	deptList = this.getSubDeptList(firstUser.getCmpyCode(), firstUser.getODeptCode());
                StringBuilder sql = getSubDeptSql(firstUser.getODeptCode());
                return new Bean().set("SQL_MODE", SQL_TYPE_DEPT).set("SQL_CONTENT", sql);
            } else if (deptBinder.getResCodes().equals(WfeBinder.PRE_DEF_SUB_ORG)) {
                topLevel = topLevel + 1;
                // 下级机构 , 因为是下级机构，这里就暂时先不加自己
                String sql = OrgMgr.getSubOrgDeptsSql(this.doUser.getCmpyCode(), this.doUser.getODeptCode());
                
//                return new Bean().set("SQL_MODE", sql);
                return new Bean().set("SQL_MODE", SQL_TYPE_ODEPT).set("SQL_CONTENT", sql);
            }
            
            return new Bean().set(Constant.RTN_DATA, deptList);
        } else if (deptBinder.getMode() == WfeConstant.NODE_BIND_MODE_ZHIDING) { // 指定
            List<DeptBean> deptList = new ArrayList<DeptBean>();
            for (String deptCode : deptBinder.getResCodes().split(",")) {
                deptList.add(OrgMgr.getDept(deptCode));
            }
            
            return new Bean().set(Constant.RTN_DATA, deptList);
        }
        
        return null;
    }

    private StringBuilder getSubDeptSql(String deptCode) {
        DeptBean deptBean = OrgMgr.getDept(deptCode);
        StringBuilder sql = new StringBuilder();
        sql.append(" AND CODE_PATH LIKE '").append(deptBean.getCodePath()).append("%'");
        sql.append(" AND ODEPT_CODE = '").append(deptBean.getODeptCode()).append("'");
        return sql;
    }

	/**
     * @param deptBeanList 部门列表
     * @return 部门编码串
     */
    private String getDeptCodeStr(List<DeptBean> deptBeanList) {
        StringBuffer deptCodeStr = new StringBuffer();
        for (DeptBean deptBean : deptBeanList) {
            
            deptCodeStr.append(deptBean.getCode());
            deptCodeStr.append(",");
        }
        return deptCodeStr.toString();
    }
    
    /**
     * 获取 上级部门 子部门列表
     * 
     * @param userBean 用户Bean
     * @return 部门串
     */
    private List<DeptBean> getParentLevelDeptList(UserBean userBean) {
        //本机构
        DeptBean odeptBean = userBean.getODeptBean();
        
        String ppDeptCode = "";
        DeptBean ppdeptBean = null;
        if (null == odeptBean.getPcode() || odeptBean.getPcode() == "") {
            ppDeptCode = odeptBean.getCode();
            ppdeptBean = odeptBean;
        } else {
            // 获取父部门
            ppdeptBean = OrgMgr.getDept(odeptBean.getPcode());
            
            ppDeptCode = ppdeptBean.getCode();
            log.debug("获取用户所在上级部门----getParentSubDeptList-----------" + ppdeptBean.getCode());
        }
        
        // 加上该部门的所有子部门
        List<DeptBean> deptList = OrgMgr.getChildDepts(userBean.getCmpyCode(), ppDeptCode);
        
        deptList.add(0, ppdeptBean);
        
        return deptList;
    }
    
    private static List<Bean> getUsersBeanbyDeptSql(String deptSql, String roleCodeStr) {
        StringBuilder where = new StringBuilder();
        where.append(deptSql);
        
        if (!roleCodeStr.isEmpty()) {
            where.append(" and USER_CODE in (select distinct USER_CODE from SY_ORG_ROLE_USER where ");
            where.append(" ROLE_CODE in ('").append(roleCodeStr.replaceAll(",", "','")).append("')")
            	.append(RoleMgr.authStateSql()).append(")");
        }
        
        SqlBean sql = new SqlBean();
        sql.set(Constant.PARAM_WHERE, where.toString());
        
        return ServDao.finds(ServMgr.SY_ORG_USER, sql);
    }
    
    /**
     * @return 拟稿人信息
     */
    private UserBean getFirstActDoneUser() {
        Bean fistNodeInstBean = getFirstWfAct().getNodeInstBean();
        String doneUserCode = fistNodeInstBean.getStr("TO_USER_ID");
        UserBean doneUserBean = UserMgr.getUser(doneUserCode);
        return doneUserBean;
    }
    
    /**
     * @return 第一个节点实例
     */
    private WfAct getFirstWfAct() {
        return wfAct.getProcess().getFirstWfAct();
    }
    
    @Override
	public WfeBinder getWfeBinder() {
        //取得线定义
        Bean lineDef = this.wfAct.getProcess().getProcDef()
                .findLineDef(this.wfAct.getCode(), this.wfNodeDef.getStr("NODE_CODE"));
        
        // 如果处理类型是并发流则，可以多选
        if (lineDef != null && lineDef.getInt("IF_PARALLEL") == WfeConstant.NODE_IS_PARALLEL) {
            binder.setMutilSelect(true);
        } else if (this.multUser == Constant.YES_INT) { // 同时可以选择多个用户
            binder.setMutilSelect(true);
        }
        
//        if(this.autoSelect == Constant.YES_INT) { // 自动选中所有用户
            binder.setAutoSelect(autoSelect);
//        }
        
        if (extendCls.length() > 0) {
            // 执行扩展组织机构过滤类
            addExtendBinder();
            
        } else if (this.bindMode.equals(WfeBinder.NODE_BIND_ROLE)) {
            //送角色  过滤范围：部门＋角色 ，选取部门+角色
            if (StringUtils.isEmpty(roleBinder.getResCodes())) {
                throw new RuntimeException("流程定义有错误，没有指定角色");
            }

            Bean rtnDept = getDeptCodeList();
            if (rtnDept.isEmpty("SQL_MODE")) {
                String deptCodeStr = getDeptList(rtnDept);
                
                addDeptRole(deptCodeStr, roleBinder.getResCodes(), false);
            } else { //sql语句
                String sql = rtnDept.getStr("SQL_CONTENT");
                
                addDeptRole(sql, roleBinder.getResCodes(), true);
            }
            
        } else if (this.bindMode.equals(WfeBinder.NODE_BIND_USER)) { //绑定的人
            if (userBinder.getMode() == WfeConstant.NODE_BIND_MODE_ZHIDING
                    || userBinder.getMode() == WfeConstant.NODE_BIND_MODE_PREDEF) {
                // 过滤范围：指定用户；选取：用户
                binder.setBinderType(WfeBinder.NODE_BIND_USER);
                addUser(userBinder.getResCodes());
            } else {
                // 过滤范围："部门 ＋ 角色"；选取：用户
                binder.setBinderType(WfeBinder.NODE_BIND_USER);
                
                String roleCodeStr = "";
                if (roleBinder.getMode() == WfeConstant.NODE_BIND_MODE_ZHIDING) {
                    roleCodeStr = roleBinder.getResCodes();
                }
                
                Bean rtnDept = getDeptCodeList();
                if (rtnDept.isEmpty("SQL_MODE")) {
                    String deptCodeStr = getDeptList(rtnDept);
                    addUserInDeptRole(deptCodeStr, roleCodeStr, SQL_TYPE_NONE);
                } else { //sql语句
                    String sql = rtnDept.getStr("SQL_CONTENT");
                    addUserInDeptRole(sql, roleBinder.getResCodes(), rtnDept.getInt("SQL_MODE"));
                }
            }
        }
        
        return binder;
    }

    /**
     * 
     * @param rtnDept 节点上定义的部门信息
     * @return 部门列表
     */
    private String getDeptList(Bean rtnDept) {
        List<DeptBean> deptBeanList = rtnDept.getList(Constant.RTN_DATA);
        String deptCodeStr = "";
        if (null != deptBeanList && deptBeanList.size() > 0) {
            deptCodeStr = getDeptCodeStr(deptBeanList);
        }
        return deptCodeStr;
    }

    
    /**
     * 增加扩展组织资源绑定类的执行结果
     * class,,{'fieldStr':'','roleCodes':'','userIDs':'','bindRole':'false'}
     */
    private void addExtendBinder() {
        String extCls = extendCls;
        
        String[] classes = extCls.split(",,");
        if (classes.length == 2) {
        	extCls = classes[0];
        }
        
        Class<?> clz = null;
        try {
            clz = Class.forName(extCls);
        } catch (Exception e) {
            log.error("Class not found:" + extCls);
            throw new RuntimeException(e.getMessage(), e);
        }
        if (ExtendBinder.class.isAssignableFrom(clz)) {
            try {
                ExtendBinder extBinder = (ExtendBinder) clz.newInstance();
                ExtendBinderResult result = extBinder.run(this.wfAct, this.wfNodeDef, this.doUser);
                binder.setAutoSelect(result.getAutoSelect());
                binder.setIgnoreCurrentUser(result.isIgnoreCurrentUser());
                this.addExcludeUsers(result.getExcludeUsers());
                
                if (result.getBinder() != null) { // 如果有Binder则优先使用。
                    binder = result.getBinder();
                } else if (result.isBindRole()) {
                    // 选取部门+角色
                    addDeptRole(result.getDeptIDs(), result.getRoleCodes(), false);
                } else {
                    // 选取用户
                    binder.setBinderType(WfeBinder.NODE_BIND_USER);
                    if (StringUtils.isNotEmpty(result.getUserIDs())) {
                        // 指定用户
                        addUser(result.getUserIDs());
                    } else if(StringUtils.isNotEmpty(result.getDeptIDs()) 
                            || StringUtils.isNotEmpty(result.getRoleCodes()) ) {
                        // 指定部门或角色
                        addUserInDeptRole(result.getDeptIDs(), result.getRoleCodes(), SQL_TYPE_NONE);
                    }
                }
            } catch (Exception e) {
                log.error("Class not found:" + extCls);
                throw new RuntimeException(e.getMessage(), e);
            }
        } else if (GroupExtendBinder.class.isAssignableFrom(clz)) {
            try {
                GroupExtendBinder groupExtendBinder = (GroupExtendBinder) clz.newInstance();
                List<GroupBean> groupList = groupExtendBinder.run(this.wfAct, this.wfNodeDef);
                binder.setGroupBeanList(groupList);
            } catch (Exception e) {
                log.error("Class not found:" + extCls);
                throw new RuntimeException(e.getMessage(), e);
            } 
        }
        
    }
    
    /**
     * 定义为送角色 ， 通过角色下的人，找到人的部门，也就是哪些部门下有这些角色
     * 
     * @param deptCodeStr 部门串, 或者查询部门的sql
     * @param roleCodeStr 角色编码
     * @param isSql 是否传递的是 sql 语句
     */
    private void addDeptRole(String deptCodeStr , String roleCodeStr, boolean isSql) {
        binder.setMutilSelect(false); // 送角色，单选
        binder.setBinderType(WfeBinder.NODE_BIND_ROLE);
        binder.setRoleCode(roleCodeStr);
        
        List<UserBean> userList = new ArrayList<UserBean>();
        if (isSql) {
            userList = UserMgr.getUsersByDeptSql(deptCodeStr, roleCodeStr);
        } else {
            if (!StringUtils.isEmpty(deptCodeStr)) { // 过滤部门 +角色
                userList = UserMgr.getUsersByDept(deptCodeStr, roleCodeStr);
            } else {
                // 返回角色 下所有的人
                userList = UserMgr.getUsersByRole(roleCodeStr);
            }
        }
        binder.setUserBeanList(userList);

        for (UserBean userBean : userList) {
            recursivePDept(userBean.getDeptCode());
        }
    }
    
    /**
     * 递归父部门
     * 
     * @param deptCode 部门编码
     */
    private void recursivePDept(String deptCode) {
        DeptBean deptBean = OrgMgr.getDept(deptCode);
        
        Bean newDeptBean = new Bean();
        newDeptBean.set("CODE", deptBean.getCode());
        newDeptBean.set("NAME", deptBean.getName());
        newDeptBean.set("NODETYPE", WfeBinder.DEPT_NODE_PREFIX);
        newDeptBean.set("ID", WfeBinder.DEPT_NODE_PREFIX + ":" + deptBean.getCode());
        newDeptBean.set("SORT", deptBean.getSort());
        newDeptBean.set("LEVEL", deptBean.getLevel());
        
        binder.addTreeBean(newDeptBean);
        
        //往上找到机构，就不找了，
        if (deptBean.getType() == Constant.DEPT_TYPE_ORG) {
            //添加顶级的一个ROOT节点
            String rootNodeId = "ROOTNODE";
            newDeptBean.set("PID", WfeBinder.DEPT_NODE_PREFIX + ":" + rootNodeId);
            addRootNode(rootNodeId);
            
            return;
        }
        
        // 如果有父部门，递归
        if (!StringUtils.isEmpty(deptBean.getPcode())) {
            newDeptBean.set("PID", WfeBinder.DEPT_NODE_PREFIX + ":" + deptBean.getPcode());
            recursivePDept(deptBean.getPcode());
        }
    }

    /**
     * 返回的数据添加一个根节点
     * @param rootNodeId 根节点的ID
     */
    private void addRootNode(String rootNodeId) {
        Bean newDeptBean = new Bean();
        newDeptBean.set("CODE", rootNodeId);
        newDeptBean.set("NAME", "工作流人员");
        newDeptBean.set("NODETYPE", WfeBinder.DEPT_NODE_PREFIX);
        newDeptBean.set("ID", WfeBinder.DEPT_NODE_PREFIX + ":" + rootNodeId);
        newDeptBean.set("SORT", 0);
        newDeptBean.set("LEVEL", 0);
        
        binder.addTreeBean(newDeptBean);
    }

    @Override
	public void initBinderResource(Bean orgDefBean) {
        bindMode = (String) orgDefBean.get("NODE_BIND_MODE", bindMode);
        
        deptBinder.setResCodes(orgDefBean.getStr("NODE_DEPT_CODES"));
        deptBinder.setMode(orgDefBean.getInt("NODE_DEPT_MODE"));
        deptBinder.setScripts(orgDefBean.getStr("NODE_DEPT_WHERE"));
        
        userBinder.setResCodes(orgDefBean.getStr("NODE_USER_CODES"));
        userBinder.setMode(orgDefBean.getInt("NODE_USER_MODE"));
        userBinder.setScripts(orgDefBean.getStr("NODE_USER_WHERE"));
        
        roleBinder.setResCodes(orgDefBean.getStr("NODE_ROLE_CODES"));
        roleBinder.setMode(orgDefBean.getInt("NODE_ROLE_MODE"));
        roleBinder.setScripts(orgDefBean.getStr("NODE_ROLE_WHERE"));
        
        extendCls = orgDefBean.getStr("NODE_EXTEND_CLASS");
        
        this.autoSelect = orgDefBean.getInt("AUTO_SELECT");
        this.multUser = orgDefBean.getInt("MULT_USER");
    }
    
	@Override
	public void addExcludeUsers(String userCodes) {
		for (String userCode: userCodes.split(",")) {
			if (StringUtils.isBlank(userCode)) {
				continue;
			}
			
			excludeUsers.add(userCode);
		}
	}
	
    @Override
	public void addExcludeUsers(Collection<String> userIds) {
        this.excludeUsers.addAll(userIds);
    }
}
