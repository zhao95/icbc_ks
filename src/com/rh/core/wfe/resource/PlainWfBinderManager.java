package com.rh.core.wfe.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;
import com.rh.core.wfe.WfAct;
import com.rh.core.wfe.def.WfNodeDef;
import com.rh.core.wfe.util.WfeConstant;

/**
 * 
 * @author yangjy
 *
 */
public class PlainWfBinderManager extends WfBinderManager {

    private static int SQL_TYPE_DEPT = 1;
    private static int SQL_TYPE_ODEPT = 2;
    private static int SQL_TYPE_NONE = 0;
    /** 转授权  **/
    private static int AUTH_STATE_ZHUAN = 1;
    
    private static Log log = LogFactory.getLog(PlainWfBinderManager.class);
    
    private WfeBinder binder = new PlainWfeBinder();
    
    
    /**
     * @param aWfNodeDef 节点定义
     * @param aWfAct 节点实例
     * @param aDoUser 办理人对象
     */
	public PlainWfBinderManager(WfNodeDef aWfNodeDef, WfAct aWfAct, UserBean aDoUser) {
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
        	UserBean firstUser = this.getFirstActDoneUser();
            definedUsers = firstUser.getUserDeptCode();
        } else if (resCodes.equals(WfeConstant.USER_YUDING_CURRENT_USER)) {
            // 当前用户
            definedUsers = this.doUser.getUserDeptCode();
        } else if (resCodes.equals(WfeConstant.USER_YUDING_TARGET_NODE_LAST_USER)) {
            // 指定节点的最后一个办理用户。
            WfAct lastAct = this.wfAct.getProcess().getLastDoneWfAct(this.wfNodeDef.getStr("NODE_CODE"));
            if (lastAct != null) {
                Bean nodeInstBean = lastAct.getNodeInstBean();
                if (nodeInstBean.isNotEmpty("TO_USER_ID")) {
                    Bean lastUserBean = lastAct.getAvailableDoUser(nodeInstBean.getStr("TO_USER_ID"));
                    definedUsers = lastUserBean.getStr("TO_USER_ID") + "^" + lastUserBean.getStr("TO_DEPT_ID");
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
        	UserBean userBean = null;
        	if(userCode.indexOf("^") > 0 ) {
        		userBean = UserMgr.getUserByUserDept(userCode);
        	} else {
        		userBean = UserMgr.getUser(userCode);
        	}
            if (userBean.getInt("USER_STATE") > 1) {
                continue;
            }
            userBeanList.add(userBean);
            binder.addTreeBean(creadeUserTreeBean(userBean));
        }
        binder.setUserBeanList(userBeanList);
    }
    
    /**
     * 
     * @param userBean 
     * @return 
     */
    private Bean creadeUserTreeBean(Bean userBean) {
        Bean binderBean = new Bean();
        StringBuilder name = new StringBuilder();
        name.append(userBean.getStr("USER_NAME")).append("[").append(userBean.getStr("USER_POST"));
        name.append("-").append(userBean.getStr("DEPT_NAME")).append("]");
//        name.append("|").append(userBean.getStr("USER_LOGIN_NAME"));
        
        StringBuilder code = new StringBuilder();
        code.append(userBean.getStr("USER_CODE")).append("^").append(userBean.getStr("DEPT_CODE"));
        //转授权功能改进。
        if(userBean.getInt("AUTH_STATE") == AUTH_STATE_ZHUAN) { // 已授权给其它人？
        	Bean targetUser = getAuthTargetUser(userBean.getStr("USER_LOGIN_NAME"), userBean.getStr("DEPT_CODE"), userBean.getStr("ROLE_CODE"));
        	if(targetUser != null) {
        		name.append("，转授权给").append(targetUser.getStr("USER_NAME"));
        		code.append("@");
        		code.append(targetUser.getStr("USER_CODE")).append("^");
        		code.append(targetUser.getStr("DEPT_CODE"));
        	} else {
        		log.error("无效的被授权人。" + userBean.getStr("USER_LOGIN_NAME") + ";" + userBean.getStr("ROLE_CODE") + ";" + userBean.getStr("DEPT_CODE"));
        	}
        }
        
        binderBean.set("NAME", name);
        binderBean.set("CODE", code);
        binderBean.set("NODETYPE", WfeBinder.USER_NODE_PREFIX);
        binderBean.set("ID", WfeBinder.USER_NODE_PREFIX + ":" + code);
        binderBean.set("SORT", userBean.getStr("USER_SORT"));
        binderBean.set("LEVEL", 999);
        
        return binderBean;
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
            binder.addTreeBean(creadeUserTreeBean(userBean));
        }
    }
    
    
    /**
     * 根据部门和角色过滤出指定范围内的用户。
     * @param deptCodeStr 过滤部门编码串
     * @param roleCodeStr 过滤角色串
     * @param isSql sql语句范围类型
     */
    private void addUserInDeptRole(String deptCodeStr , String roleCodeStr, int sqlType) {
        List<Bean> userList = new ArrayList<Bean>();
        if (sqlType == SQL_TYPE_ODEPT) {
            userList = getUserListbyOdeptSql(deptCodeStr, roleCodeStr);
        } else if (sqlType == SQL_TYPE_DEPT) {
        	userList = findUserListByDeptRole(roleCodeStr, deptCodeStr);
        } else {
            if (StringUtils.isNotBlank(deptCodeStr) && StringUtils.isNotBlank(roleCodeStr)) { // 部门和角色都不空，根据角色 + 部门过滤
                userList = getUserListByDeptRoleCodes(deptCodeStr, roleCodeStr);
            } else if (StringUtils.isNotBlank(deptCodeStr)) { // 部门不为空，根据部门过滤
                userList = getUserListByDept(deptCodeStr);
            } else if (StringUtils.isNotBlank(roleCodeStr)) { // 角色不为空，过滤当前公司下整个角色内的用户
                userList = getUserListByCmpyRole(roleCodeStr, this.cmpyCode);
            } else {
                // 没有指定任何条件，则抛出错误
            	throw new TipException("未配置下一处理人");
            }
        }
        addBindNode(userList);
        binder.setUserBeanListByBeanList(userList);
    }
    
    /**
     * 
     * @param deptSql
     * @param roleCodeStr
     * @return
     */
    private List<Bean> getUserListbyOdeptSql(String deptSql, String roleCodeStr) {
        StringBuilder where = new StringBuilder();
        where.append(" and a.ODEPT_CODE in (select odept_code from sy_org_dept where 1=1 ")
                .append(deptSql).append(")");

        return findUserListByDeptRole(roleCodeStr, where.toString());
    }   
    
    /**
     * 取得部门用户列表，只取部门和部门和处室
     * 
     * @param deptCodes 部门Code串
     * @return 用户Bean列表
     */
    private List<Bean> getUserListByDept(String deptCodes) {
		if (deptCodes.indexOf(Constant.SEPARATOR) > 0) {
			deptCodes = deptCodes.replaceAll(Constant.SEPARATOR, "'" + Constant.SEPARATOR + "'");
		}
		StringBuilder condition = new StringBuilder(" and (a.DEPT_CODE in ('" + deptCodes + "')");
		condition.append(" or a.TDEPT_CODE in ('" + deptCodes + "'))");
		condition.append(" and a.S_FLAG=1");

		return findUserListByDeptRole("", condition.toString());
    }
    
    
    /**
     * 取得部门中指定角色的用户列表，取两级部门。
     * 
     * @param deptCodes 部门Code串
     * @param roleCodes 角色Code串
     * @return 用户Bean列表
     */
    private List<Bean> getUserListByDeptRoleCodes(String deptCodes, String roleCodes) {
        if (deptCodes.indexOf(Constant.SEPARATOR) > 0) {
            deptCodes = deptCodes.replaceAll(Constant.SEPARATOR, "'" + Constant.SEPARATOR + "'");
        }

        StringBuilder deptWhere = new StringBuilder();
        deptWhere.append(" and (");
        deptWhere.append("a.DEPT_CODE").append(" in ('").append(deptCodes).append("')");
        deptWhere.append(" or ").append("a.TDEPT_CODE").append(" in ('").append(deptCodes).append("')");
        deptWhere.append(" or ").append("a.ODEPT_CODE").append(" in ('").append(deptCodes).append("')");
        deptWhere.append(")");
        deptWhere.append(" and a.S_FLAG = 1");

        return findUserListByDeptRole(roleCodes, deptWhere.toString());
    }
    
    /**
     * 取得角色用户列表
     * 
     * @param cmpyCode 公司编码
     * @param roleCodes 角色Code串
     * @return 用户Bean列表
     */
	private List<Bean> getUserListByCmpyRole(String roleCodes, String cmpyCode) {
		StringBuilder condition = new StringBuilder(" and a.S_FLAG=1 and a.CMPY_CODE = '");
		condition.append(cmpyCode);
		condition.append("'");
		return findUserListByDeptRole(roleCodes, condition.toString());
	}
    
    /**
     * @return 节点上定义的部门 串
     */
    private Bean getDeptCodeList() {
        if (deptBinder.getMode() == WfeConstant.NODE_BIND_MODE_ALL) { // 全部部门
            // 本机构下所有部门列表
            StringBuilder sql = getSubDeptSql(this.doUser.getODeptCode());
            return new Bean().set("SQL_MODE", SQL_TYPE_DEPT).set("SQL_CONTENT", sql);
        } else if (deptBinder.getMode() == WfeConstant.NODE_BIND_MODE_PREDEF) { // 预定义
            List<DeptBean> deptList = new ArrayList<DeptBean>();
            log.debug("the dept mode is predef " + deptBinder.getResCodes() + deptBinder.getMode());
            
            if (deptBinder.getResCodes().equals(WfeBinder.PRE_DEF_SELF_DEPT)) {
                // 本处室下所有子目录
                log.debug("the dept mode is predef 本部门" + deptBinder.getResCodes());
                StringBuilder sql = getSubDeptSql(this.doUser.getDeptCode());
                return new Bean().set("SQL_MODE", SQL_TYPE_DEPT).set("SQL_CONTENT", sql);
            } else if (deptBinder.getResCodes().equals(WfeBinder.PRE_DEF_SELF_DEPT_LEVEL)) {
                // 本部门下所有的子目录
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
            } else if (deptBinder.getResCodes().equals(WfeBinder.PRE_DEF_INIT_DEPT)) {
                // 拟稿处室
                UserBean firstUser = getFirstActDoneUser();
                StringBuilder sql = getSubDeptSql(firstUser.getDeptCode());
                return new Bean().set("SQL_MODE", SQL_TYPE_DEPT).set("SQL_CONTENT", sql);
            } else if (deptBinder.getResCodes().equals(WfeBinder.PRE_DEF_INIT_ORG)) {
                //拟稿机构
                UserBean firstUser = getFirstActDoneUser();
                StringBuilder sql = getSubDeptSql(firstUser.getODeptCode());
                return new Bean().set("SQL_MODE", SQL_TYPE_DEPT).set("SQL_CONTENT", sql);
            } else if (deptBinder.getResCodes().equals(WfeBinder.PRE_DEF_SUB_ORG)) {
                topLevel = topLevel + 1;
                // 下级机构 , 因为是下级机构，这里就暂时先不加自己
                String sql = OrgMgr.getSubOrgDeptsSql(this.doUser.getCmpyCode(), this.doUser.getODeptCode());
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
        sql.append(" AND a.CODE_PATH LIKE '").append(deptBean.getCodePath()).append("%'");
        sql.append(" AND a.ODEPT_CODE = '").append(deptBean.getODeptCode()).append("'");
        return sql;
    }
	
	/**
     * @param deptBeanList 部门列表
     * @return 部门编码串
     */
	private String getDeptCodeStr(List<DeptBean> deptBeanList) {
		StringBuffer deptCodeStr = new StringBuffer();
		for (DeptBean deptBean : deptBeanList) {
			if (deptBean != null) {
				deptCodeStr.append(deptBean.getCode());
				deptCodeStr.append(",");
			}
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
    
    private List<Bean> findUserListByDeptRole(String roleCodeStr, String deptSql) {
		if (!roleCodeStr.isEmpty()) {
			StringBuilder sql = new StringBuilder();
			sql.append("select a.USER_NAME, a.DEPT_NAME, a.USER_CODE, a.DEPT_CODE, a.USER_SORT, a.USER_LOGIN_NAME, a.USER_POST, a.AUTH_STATE, a.ROLE_CODE ");
			sql.append(" FROM SY_ORG_ROLE_USER_V2 a ");
			sql.append(" WHERE 1 = 1");
			if(deptSql != null && deptSql.length() > 0) {
				sql.append(deptSql.toString());
			}
			sql.append(" and a.ROLE_CODE in ('").append(roleCodeStr.replaceAll(",", "','")).append("')");
			sql.append(" and a.AUTH_STATE in(0,1) ");
			sql.append(getOrderByStr());
			return Context.getExecutor().query(sql.toString());
		} else {
			if(deptSql == null || deptSql.length() == 0) {
				throw new TipException("无效的流程过滤条件。");
			}
			
			StringBuilder sql = new StringBuilder();
			sql.append("select a.USER_NAME, a.DEPT_NAME, a.USER_CODE, a.DEPT_CODE, a.USER_SORT, a.USER_LOGIN_NAME, a.USER_POST ");
			sql.append(" FROM SY_ORG_ROLE_USER_V2 a ");
			sql.append(" WHERE 1=1 ");
			sql.append(deptSql.toString());
			sql.append(getOrderByStr());
			return Context.getExecutor().query(sql.toString());
		}
	}
	
	/**
	 * 查找符合条件的用户
	 * @param roleCodeStr 逗号分隔的角色ID
	 * @param deptSql 按部门查询的SQL
	 * @return 符合条件的数据
	 */
	private List<Bean> findUserListByDeptRoleOld(String roleCodeStr, String deptSql) {
		if (!roleCodeStr.isEmpty()) {
			StringBuilder sql = new StringBuilder();
			sql.append("select a.USER_NAME, a.DEPT_NAME, a.USER_CODE, a.DEPT_CODE, a.USER_SORT, a.USER_LOGIN_NAME, a.USER_POST, b.AUTH_STATE, b.ROLE_CODE ");
			sql.append(" FROM SY_BASE_USER_V a, SY_ORG_ROLE_USER b ");
			sql.append(" WHERE ");
			sql.append(" a.USER_CODE = b.USER_CODE ");
			sql.append(" and a.DEPT_CODE = b.DEPT_CODE ");
			if(deptSql != null && deptSql.length() > 0) {
				sql.append(deptSql.toString());
			}
			sql.append(" and b.ROLE_CODE in ('").append(roleCodeStr.replaceAll(",", "','")).append("')");
			sql.append(" and b.AUTH_STATE in(0,1) ");
			sql.append(getOrderByStr());
			return Context.getExecutor().query(sql.toString());
		} else {
			if(deptSql == null || deptSql.length() == 0) {
				throw new TipException("无效的流程过滤条件。");
			}
			
			StringBuilder sql = new StringBuilder();
			sql.append("select a.USER_NAME, a.DEPT_NAME, a.USER_CODE, a.DEPT_CODE, a.USER_SORT, a.USER_LOGIN_NAME, a.USER_POST ");
			sql.append(" FROM SY_BASE_USER_V a ");
			sql.append(" WHERE 1=1 ");
			sql.append(deptSql.toString());
			sql.append(getOrderByStr());
			return Context.getExecutor().query(sql.toString());
		}
	}
	
	private String getOrderByStr() {
		StringBuilder orderby = new StringBuilder();
		orderby.append(" order by ").append("a.DUTY_SORT desc ");
		orderby.append(", a.USER_EDU_MAJOR");
		orderby.append(", a.USER_SORT desc");
		orderby.append(", NLSSORT (USER_NAME, 'NLS_SORT=SCHINESE_PINYIN_M')");
		return orderby.toString();
	}
    
    /**
     * @return 拟稿人信息
     */
    private UserBean getFirstActDoneUser() {
    	WfAct firstWf = getFirstWfAct();
    	List<Bean> list = firstWf.getNodeUserBeanList();
    	Bean bean = list.get(0);
//        Bean fistNodeInstBean = getFirstWfAct().getNodeInstBean();
//        
//        String doneUserCode = fistNodeInstBean.getStr("TO_USER_ID");
        UserBean doneUserBean = UserMgr.getUser(bean.getStr("TO_USER_ID"), bean.getStr("TO_DEPT_ID"));
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
        
        binder.setAutoSelect(autoSelect);
        
        if (extendCls.length() > 0) {
            // 执行扩展组织机构过滤类
            addExtendBinder();
        } else if (this.bindMode.equals(WfeBinder.NODE_BIND_ROLE)) {
            //送角色  过滤范围：部门＋角色 ，选取部门+角色
            if (StringUtils.isEmpty(roleBinder.getResCodes())) {
                throw new TipException("流程定义有错误，没有指定角色");
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
        
        List<Bean> userList = new ArrayList<Bean>();
        if (isSql) {
            userList = getUserListbyOdeptSql(deptCodeStr, roleCodeStr);
        } else {
            if (!StringUtils.isEmpty(deptCodeStr)) { // 过滤部门 +角色
                userList = getUsersByDept(deptCodeStr, roleCodeStr);
            } else {
                // 返回角色 下所有的人
                userList = getUserListByCmpyRole(roleCodeStr, this.cmpyCode);
            }
        }
        
		List<UserBean> userBeanList = new ArrayList<UserBean>();
		for (Bean bean : userList) {
			userBeanList.add(new UserBean(bean));
		}
        
        binder.setUserBeanList(userBeanList);
        
        for (Bean userBean : userList) {
        	addParentDept(userBean.getStr("DEPT_CODE"));
        }
    }
    

    
    /**
     * 取得部门中指定角色的用户列表
     * 
     * @param deptCodes 部门Code串
     * @param roleCodes 角色Code串
     * @return 用户Bean列表
     */
    private List<Bean> getUsersByDept(String deptCodes, String roleCodes) {
        if (deptCodes.indexOf(Constant.SEPARATOR) > 0) {
            deptCodes = deptCodes.replaceAll(Constant.SEPARATOR, "'" + Constant.SEPARATOR + "'");
        }

        StringBuilder condition = new StringBuilder(" ");
        if (!deptCodes.isEmpty()) {
        	condition.append(" and (a.DEPT_CODE in ('" + deptCodes + "'))");
        }
        
        condition.append(" and a.S_FLAG =1");
        return findUserListByDeptRole(roleCodes, condition.toString());
    }
    
    /**
     * 递归父部门
     * 
     * @param deptCode 部门编码
     */
    private void addParentDept(String deptCode) {
        DeptBean deptBean = OrgMgr.getDept(deptCode);
        
        Bean newDeptBean = new Bean();
        newDeptBean.set("CODE", deptBean.getCode());
        newDeptBean.set("NAME", deptBean.getName());
        newDeptBean.set("NODETYPE", WfeBinder.DEPT_NODE_PREFIX);
        newDeptBean.set("ID", WfeBinder.DEPT_NODE_PREFIX + ":" + deptBean.getCode());
        newDeptBean.set("SORT", deptBean.getSort());
        newDeptBean.set("LEVEL", deptBean.getLevel());
        
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
    
    /**
     * 
     * @param userCode 授权人ID
     * @param deptCode 授权人部门
     * @param roleCode 角色编码
     * @return 取得转授权目标用户
     */
	private UserBean getAuthTargetUser(String srcSsicid, String deptCode, String roleCode) {
		SqlBean sql = new SqlBean();
		sql.and("ROLE_ID", roleCode);
		sql.and("SOURCE_USER_ID", srcSsicid);
		sql.and("BNCH_ID", deptCode);
		sql.and("STATE", Constant.YES);
		sql.andGTE("END_DATE", DateUtils.getDatetime("yyyyMMddHHmmss"));
		sql.selects("*");

		Bean bean = ServDao.find("SY_ORG_ROLE_ACCREDIT", sql);
		if (bean == null) {
			return null;
		}
		try {
			return UserMgr.getUser(bean.getStr("CURR_PERSON_ID"));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return null;
	}
}
