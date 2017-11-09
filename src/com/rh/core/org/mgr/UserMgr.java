package com.rh.core.org.mgr;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.base.db.Transaction;
import com.rh.core.comm.CacheMgr;
import com.rh.core.comm.MenuServ;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.UserStateBean;
import com.rh.core.org.serv.UserAgentServ;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;
import com.rh.core.util.Lang;
import com.rh.core.util.Strings;
import com.rh.core.util.lang.Assert;
import com.rh.core.wfe.WfProcess;

/**
 * 用户管理器
 * 
 * @author cuihf
 */
public class UserMgr {
	private static Log log = LogFactory.getLog(WfProcess.class);
	
    /** 用户缓存类型 */
    private static final String CACHE_TYPE_USER = "SY_ORG_USER";
    /** 用户菜单缓存类型 */
    private static final String CACHE_TYPE_USER_MENU = "SY_ORG_USER__MENU";
    /** 用户变量缓存类型 */
    private static final String CACHE_TYPE_USER_VAR = "SY_ORG_USER__VAR";

    private static final String TBL_SY_ORG_USER_RELATION = "SY_ORG_USER_RELATION";

    private static final String TBL_SY_ORG_ROLE_USER = "SY_ORG_ROLE_USER";

    private static final String COL_ORIGIN_USER_CODE = "ORIGIN_USER_CODE";

    private static final String COL_RELATION_TYPE = "RELATION_TYPE";

    private static final String COL_ROLE_CODE = "ROLE_CODE";

    private static final String COL_USER_CODE = "USER_CODE";
    
    /** 授权标志：0，正常； 1，授权出去的角色；2，被授权的角色；3，失效的角色；*/
    public static final String AUTH_STATE = "AUTH_STATE";

    /** 用户关系是 领导-秘书 */
    private static final int USER_RELATE_LEAD_SEC = 3;

    /**
     * 有效部门编码
     */
    private static final String COL_TDEPT_CODE = "TDEPT_CODE";

    private static final String COL_DEPT_CODE = "DEPT_CODE";

    private static final String COL_S_FLAG = "S_FLAG";

    private static final String COL_CMPY_CODE = "CMPY_CODE";

    private static final String COL_USER_LOGIN_NAME = "USER_LOGIN_NAME";
    
    private static final String COL_USER_WORK_NUM = "USER_WORK_NUM";

    /**
     * 取得用户Bean
     * 
     * @param userCode 用户Code
     * @return 用户Bean对象
     */
    public static UserBean getUser(String userCode) {
        UserBean userBean = getCacheUser(userCode);
        if (userBean == null) {
        	Assert.hasText(userCode, "Argument 'userCode' can not be empty.");
        	
        	List<Bean> userList = findUsers(userCode);
            if (userList == null || userList.size() == 0) {
                throw new TipException(Context.getSyMsg("SY_USER_NOT_FOUND", userCode));
            } else {
				userBean = new UserBean(userList.get(0));
				if (userList.size() > 1) {
					log.debug("Set user _MULTI_DEPT, " + userCode);
					userBean.set("_MULTI_DEPT", 1);
				}
                updateUserBeanCache(userCode, userBean);
            }
        }
        return userBean;
    }
    
    public static List<Bean> findUsers(String userCode) {
		SqlBean sqlBean = new SqlBean();
		sqlBean.and("USER_CODE", userCode);
		sqlBean.orders("STRU_FLAG");
		return ServDao.finds(ServMgr.SY_ORG_USER, sqlBean);
    }
    
    /**
     * 
     * @param userAndDept 用户部门数据，格式为：参数格式应为：USER_CODE^DEPT_CODE
     * @return 用户Bean
     */
    public static UserBean getUserByUserDept(String userAndDept) {
    	if(StringUtils.isBlank(userAndDept)) {
    		throw new TipException("userAndDept 参数不能为空。");
    	}
    	String[] userArr = userAndDept.split("\\^");
    	if(userArr.length != 2) {
    		throw new TipException("无效的参数，参数格式应为：USER_CODE^DEPT_CODE");
    	}
    	
    	return getUser(userArr[0], userArr[1]);
    }
    
    /**
     * 
     * @param userCode 用户ID
     * @param deptCode 部门ID
     * @return 用户Bean
     */
	public static UserBean getUser(String userCode, String deptCode) {
		final String key = userCode + "^" + deptCode;
		UserBean userBean = getCacheUser(key);
		if (userBean == null) {
			Assert.hasText(userCode, "Argument 'userCode' can not be empty.");
			Assert.hasText(deptCode, "Argument 'deptCode' can not be empty.");
			List<Bean> users = findUsers(userCode);

			if (users == null || users.size() == 0) {
				throw new TipException(Context.getSyMsg("SY_USER_NOT_FOUND", userCode));
			} else {
				for(Bean user:users) {
					if(user.getStr("DEPT_CODE").equals(deptCode)) {
						userBean = new UserBean(user);
						updateUserBeanCache(key, userBean);
					} else {
						//userCode用户 不在 deptCode部门下
						userBean = new UserBean(user);
						DeptBean deptBean = OrgMgr.getDept(deptCode);
						userBean.set("DEPT_CODE", deptBean.getCode());
						userBean.set("TDEPT_CODE", deptBean.getTDeptCode());
						userBean.set("ODEPT_CODE", deptBean.getODeptCode());
						userBean.set("CODE_PATH", deptBean.getCodePath());
					}
				}
			}
			
			if(userBean == null) {
				throw new TipException(Context.getSyMsg("SY_USER_NOT_FOUND", key));
			}
		}
		return userBean;
	}
	
	/**
	 * 
	 * @param key
	 * @param userBean
	 */
	private static void updateUserBeanCache(String key, UserBean userBean) {
		CacheMgr.getInstance().set(key, userBean, CACHE_TYPE_USER);
	}

    /**
     * 取得缓存中的用户Bean
     * 
     * @param userCode 用户Code
     * @return 用户Bean对象，缓存中不存在返回null
     */
    public static UserBean getCacheUser(String userCode) {
        return (UserBean) CacheMgr.getInstance().get(userCode, CACHE_TYPE_USER);
    }

    /**
     * 取得用户Bean
     * 
     * @param loginName 用户登录名称
     * @param cmpyCode 公司Code
     * @return 用户Bean对象
     */
    public static UserBean getUserByLoginName(String loginName, String cmpyCode) {
        SqlBean sql = new SqlBean();
        sql.and(COL_USER_LOGIN_NAME, loginName).and(COL_CMPY_CODE, cmpyCode).and(COL_S_FLAG, 1);
        sql.orders("STRU_FLAG");
        Bean bean = ServDao.find(ServMgr.SY_ORG_USER, sql);
        if (bean == null) {
            throw new TipException(Context.getSyMsg("SY_USER_NOT_FOUND", loginName + ":" + cmpyCode));
        }
        return getUser(bean.getStr("USER_CODE"));
    }

    /**
     * 取得用户Bean
     * 
     * @param moblieOrMail 手机或邮箱
     * @return 用户Bean对象
     */
    public static UserBean getUserByMobileOrMail(String moblieOrMail) {
        SqlBean sql = new SqlBean();
        sql.appendWhere("and (USER_MOBILE=? or USER_EMAIL=?)", moblieOrMail, moblieOrMail).and(COL_S_FLAG, 1);
        Bean bean = ServDao.find(ServMgr.SY_ORG_USER, sql);
        if (bean == null) {
            throw new TipException(Context.getSyMsg("SY_USER_NOT_FOUND", moblieOrMail));
        }
        return getUser(bean.getStr("USER_CODE"));
    }
    
    /**
     * 取得用户Bean
     * @param loginName 登录名
     * @return 用户对象
     */
    public static UserBean getUserByLoginName(String loginName) {
    	Bean bean = findUserByLoginName(loginName);
    	if (bean == null) {
    		throw new TipException(Context.getSyMsg("SY_USER_NOT_FOUND", loginName));
    	}
    	return new UserBean(bean);
    }
    
    /**
     * 根据登录名获取用户Bean
     * @param loginName
     * @return - 没有返回null
     */
    public static Bean findUserByLoginName(String loginName) {
    	SqlBean sql = new SqlBean();
    	sql.appendWhere("and USER_LOGIN_NAME=?", loginName).and(COL_S_FLAG, 1);
    	sql.orders("STRU_FLAG");
    	Bean bean = ServDao.find(ServMgr.SY_ORG_USER, sql);
    	if(bean != null) {
    		return getUser(bean.getStr("USER_CODE"));
    	}
    	return null;
    }

    /**
     * 
     * @param deptCodes 部门编码，多个部门之间使用英文逗号分隔
     * @return 指定部门下，且在同一个机构内的用户列表
     */
    public static List<UserBean> getUsersInDepts(String deptCodes) {
        if (StringUtils.isEmpty(deptCodes)) {
            return new ArrayList<UserBean>();
        }
        String[] depts = Strings.splitIgnoreBlank(deptCodes);
        if (depts.length == 1) {
            DeptBean deptBean = OrgMgr.getDept(depts[0]);
            SqlBean sqlBean = new SqlBean();
            sqlBean.and("ODEPT_CODE", deptBean.getODeptCode());
            sqlBean.andLikeRT("CODE_PATH", deptBean.getCodePath());
            return getUsersByCondition(sqlBean);
        }

        SqlBean sqlBean = new SqlBean();

        StringBuilder sql = new StringBuilder();
        List<Object> vars = new ArrayList<Object>();
        for (String dept : depts) {
            DeptBean deptBean = OrgMgr.getDept(dept);
            if (sql.length() > 0) {
                sql.append(" union ");
            }

            sql.append("select user_code from SY_BASE_USER_V where ");
            sql.append("CODE_PATH like ? || '%'  and ODEPT_CODE = ? ");
            vars.add(deptBean.getCodePath());
            vars.add(deptBean.getODeptCode());
        }

        sqlBean.andInSub("USER_CODE", sql.toString(), vars.toArray());

        return getUsersByCondition(sqlBean);
    }

    /**
     * 取得部门用户列表
     * 
     * @param deptCodes 部门Code串
     * @return 用户Bean列表
     */
    public static ArrayList<UserBean> getUsersByDept(String deptCodes) {
        if (deptCodes.indexOf(Constant.SEPARATOR) > 0) {
            deptCodes = deptCodes.replaceAll(Constant.SEPARATOR, "'" + Constant.SEPARATOR + "'");
        }
        StringBuilder condition = new StringBuilder(" and (" + COL_DEPT_CODE + " in ('" + deptCodes + "')");
        condition.append(" or " + COL_TDEPT_CODE + " in ('" + deptCodes + "'))");
        condition.append(" and " + COL_S_FLAG + "=1");

        return getUsersByCondition(condition.toString());
    }

    
    /**
     * 取得部门用户列表
     * 
     * @param deptCodes 部门Code串
     * @return 用户Bean列表
     */
    public static List<Bean> getUserListByDept(String deptCodes) {
        if (deptCodes.indexOf(Constant.SEPARATOR) > 0) {
            deptCodes = deptCodes.replaceAll(Constant.SEPARATOR, "'" + Constant.SEPARATOR + "'");
        }
        StringBuilder condition = new StringBuilder(" and (" + COL_DEPT_CODE + " in ('" + deptCodes + "')");
        condition.append(" or " + COL_TDEPT_CODE + " in ('" + deptCodes + "'))");
        condition.append(" and " + COL_S_FLAG + "=1");
        return ServDao.finds(ServMgr.SY_ORG_USER, condition.toString());
    }
/**
 * 取得机构下所有用户列表（包含下级机构）
 * @param odeptCodes 机构编码
 * @return 机构下所有用户数据集合（包含下级机构）
 */
    public static List<Bean> getUserListByOdept(String odeptCodes) {
    	if (odeptCodes.indexOf(Constant.SEPARATOR) > 0) {
    		odeptCodes = odeptCodes.replaceAll(Constant.SEPARATOR, "'" + Constant.SEPARATOR + "'");
    	}
    	String sqlForDeptPath ="SELECT CODE_PATH FROM SY_ORG_DEPT WHERE DEPT_CODE='"+odeptCodes+"'";
    	Bean deptPathBean = Transaction.getExecutor().queryOne(sqlForDeptPath);
    	String paramForDeptPath = deptPathBean.getStr("CODE_PATH");
    	StringBuilder condition = new StringBuilder("  AND DEPT_CODE IN (SELECT DEPT_CODE FROM SY_ORG_DEPT WHERE 1=1 AND CODE_PATH LIKE '"+paramForDeptPath+"%') ");
    	condition.append(" and " + COL_S_FLAG + "=1");
    	return ServDao.finds(ServMgr.SY_ORG_USER, condition.toString());
    }

    /**
     * 
     * @param sqlBean SQLBean
     * @return 用户列表
     */
    private static List<UserBean> getUsersByCondition(SqlBean sqlBean) {
        sqlBean.orders(" DEPT_LEVEL ,DEPT_SORT ,USER_SORT ");
        ArrayList<UserBean> userBeanList = new ArrayList<UserBean>();
        List<Bean> beanList = ServDao.finds(ServMgr.SY_ORG_USER, sqlBean);
        if (beanList != null) {
            for (Bean bean : beanList) {
                userBeanList.add(new UserBean(bean));
            }
        }
        return userBeanList;
    }

    /**
     * 根据指定的条件获取用户列表
     * 
     * @param condition 查询条件
     * @return 用户Bean列表
     */
    private static ArrayList<UserBean> getUsersByCondition(String condition) {
        ArrayList<UserBean> userBeanList = new ArrayList<UserBean>();
        Bean paramBean = new Bean();
        paramBean.set(Constant.PARAM_WHERE, condition);
        paramBean.set(Constant.PARAM_ORDER, " DEPT_LEVEL ,DEPT_SORT ,USER_SORT ");
        List<Bean> beanList = ServDao.finds(ServMgr.SY_ORG_USER, paramBean);
        if (beanList != null) {
            for (Bean bean : beanList) {
                userBeanList.add(new UserBean(bean));
            }
        }
        return userBeanList;
    }
    
    /**
     * 取得角色用户的“授权”过滤条件
     */
    public static String authStateSql() {
    	return " and AUTH_STATE in(0,2)";
    }
    public static void authStateSql(SqlBean sqlBean) {
    	if (sqlBean == null) {
    		return;
    	}
    	sqlBean.andIn("AUTH_STATE", new Object[]{0, 2});
    }

    /**
     * 
     * @param deptSql 部门的查询条件
     * @param roleCodeStr 角色
     * @return 满足条件的用户的 列表
     */
    public static List<UserBean> getUsersByDeptSql(String deptSql, String roleCodeStr) {
        StringBuilder where = new StringBuilder();
        where.append(" and ODEPT_CODE in (select odept_code from sy_org_dept where 1=1 ")
                .append(deptSql).append(")");

        if (!roleCodeStr.isEmpty()) {
            where.append(" and USER_CODE in (select distinct USER_CODE from SY_ORG_ROLE_USER where ");
            where.append(" ROLE_CODE in ('").append(roleCodeStr.replaceAll(",", "','")).append("')")
            .append(authStateSql()).append(")");
        }

        return UserMgr.getUsersByCondition(where.toString());
    }

    /**
     * 
     * @param deptSql 部门的查询条件
     * @param roleCodeStr 角色
     * @return 满足条件的用户的 列表
     */
    public static List<Bean> getUsersBeanbyDeptSql(String deptSql, String roleCodeStr) {
        StringBuilder where = new StringBuilder();
        where.append(" and ODEPT_CODE in (select odept_code from sy_org_dept where 1=1 ")
                .append(deptSql).append(")");

        if (!roleCodeStr.isEmpty()) {
            where.append(" and USER_CODE in (select distinct USER_CODE from SY_ORG_ROLE_USER where ");
            where.append(" ROLE_CODE in ('").append(roleCodeStr.replaceAll(",", "','")).append("')")
            .append(authStateSql()).append(")");
        }

        SqlBean sql = new SqlBean();
        sql.set(Constant.PARAM_WHERE, where.toString());

        return ServDao.finds(ServMgr.SY_ORG_USER, sql);
    }
    
    /**
     * 取得部门中指定角色的用户列表，取两级部门。
     * 
     * @param deptCodes 部门Code串
     * @param roleCodes 角色Code串
     * @return 用户Bean列表
     */
    public static List<UserBean> getUserListByDeptRole(String deptCodes , String roleCodes) {
        if (deptCodes.indexOf(Constant.SEPARATOR) > 0) {
            deptCodes = deptCodes.replaceAll(Constant.SEPARATOR, "'" + Constant.SEPARATOR + "'");
        }
        
        if (roleCodes.indexOf(Constant.SEPARATOR) > 0) {
            roleCodes = roleCodes.replaceAll(Constant.SEPARATOR, "'" + Constant.SEPARATOR + "'");
        }
        
        StringBuilder condition = new StringBuilder(" and ");
        condition.append(COL_DEPT_CODE + " in (");
        condition.append("select deptA.DEPT_CODE from SY_ORG_DEPT  deptA, (");
        condition.append("select ODEPT_CODE, CODE_PATH from SY_ORG_DEPT where DEPT_CODE in ('");
        condition.append(deptCodes).append("')) deptB "); 
        condition.append(" where deptA.CODE_PATH like deptB.CODE_PATH || '%' and deptA.ODEPT_CODE = deptB.ODEPT_CODE");
        condition.append(") and " + COL_S_FLAG + "=1");
        condition.append(" and " + COL_USER_CODE + " in (select distinct " + COL_USER_CODE + " from "
                + TBL_SY_ORG_ROLE_USER + " where " + COL_ROLE_CODE + " in ('" + roleCodes + "')");
        condition.append(" and " + COL_S_FLAG + "=1").append(authStateSql()).append(")");
        
        return getUsersByCondition(condition.toString());
    }
    
    
    /**
     * 通过用户工号取得用户Bean
     * @param workNum 用户工号
     * @return 用户Bean对象
     */
    public static UserBean getUserByWorkNum(String workNum) {
        SqlBean sql = new SqlBean();
        sql.and(COL_USER_WORK_NUM, workNum).and(COL_S_FLAG, 1);
        Bean bean = ServDao.find(ServMgr.SY_ORG_USER, sql);
        if (bean == null) {
            throw new TipException(Context.getSyMsg("SY_USER_NOT_FOUND", workNum));
        }
        return getUser(bean.getStr("USER_CODE"));
    }

    /**
     * 取得部门中指定角色的用户列表，取两级部门。
     * 
     * @param deptCodes 部门Code串
     * @param roleCodes 角色Code串
     * @return 用户Bean列表
     */
    public static List<Bean> getUserListByDept(String deptCodes, String roleCodes) {
        if (deptCodes.indexOf(Constant.SEPARATOR) > 0) {
            deptCodes = deptCodes.replaceAll(Constant.SEPARATOR, "'" + Constant.SEPARATOR + "'");
        }

        if (roleCodes.indexOf(Constant.SEPARATOR) > 0) {
            roleCodes = roleCodes.replaceAll(Constant.SEPARATOR, "'" + Constant.SEPARATOR + "'");
        }

        StringBuilder condition = new StringBuilder();
        condition.append(" and (");
        condition.append(COL_DEPT_CODE).append(" in ('").append(deptCodes).append("')");
        condition.append(" or ").append(COL_TDEPT_CODE).append(" in ('").append(deptCodes).append("')");
        condition.append(" or ").append("ODEPT_CODE").append(" in ('").append(deptCodes).append("')");
        condition.append(")");

        condition.append(" and " + COL_S_FLAG + "=1");
        condition.append(" and " + COL_USER_CODE + " in (select distinct " + COL_USER_CODE + " from "
                + TBL_SY_ORG_ROLE_USER + " where " + COL_ROLE_CODE + " in ('" + roleCodes + "')");
        condition.append(" and " + COL_S_FLAG + "=1").append(authStateSql()).append(")");
        return ServDao.finds(ServMgr.SY_ORG_USER, condition.toString());
    }

    /**
     * 取得部门中指定角色的用户列表
     * 
     * @param deptCodes 部门Code串
     * @param roleCodes 角色Code串
     * @return 用户Bean列表
     */
    public static ArrayList<UserBean> getUsersByDept(String deptCodes, String roleCodes) {
        if (deptCodes.indexOf(Constant.SEPARATOR) > 0) {
            deptCodes = deptCodes.replaceAll(Constant.SEPARATOR, "'" + Constant.SEPARATOR + "'");
        }

        if (roleCodes.indexOf(Constant.SEPARATOR) > 0) {
            roleCodes = roleCodes.replaceAll(Constant.SEPARATOR, "'" + Constant.SEPARATOR + "'");
        }

        StringBuilder condition = new StringBuilder(" ");
        if (!deptCodes.isEmpty()) {
            condition.append("and (" + COL_DEPT_CODE + " in ('" + deptCodes + "'))");
        }
        condition.append(" and " + COL_S_FLAG + "=1");
        condition.append(" and " + COL_USER_CODE + " in (select distinct " + COL_USER_CODE + " from "
                + TBL_SY_ORG_ROLE_USER + " where 1=1 ");
        if (!roleCodes.isEmpty()) {
            condition.append(" and " + COL_ROLE_CODE + " in ('" + roleCodes + "')");
        }
        condition.append(" and " + COL_S_FLAG + "=1").append(authStateSql()).append(")");

        return getUsersByCondition(condition.toString());
    }

    /**
     * 查找指定机构、部门 + 指定角色下的所有用户。给出了部门ID，可以取出处室内的用户，给出了机构ID，可以取出本机构下角色内所有用户。
     * @param deptCodes 部门CODE字符串，多个Code之间使用英文逗号分隔
     * @param roleCodes 角色CODE字符串，多个Code之间使用英文逗号分隔
     * @return 符合条件的用户列表
     */
    public static List<UserBean> getUsersInOdept(String deptCodes, String roleCodes) {
        if (deptCodes.indexOf(Constant.SEPARATOR) > 0) {
            deptCodes = deptCodes.replaceAll(Constant.SEPARATOR, "'" + Constant.SEPARATOR + "'");
        }

        if (roleCodes.indexOf(Constant.SEPARATOR) > 0) {
            roleCodes = roleCodes.replaceAll(Constant.SEPARATOR, "'" + Constant.SEPARATOR + "'");
        }

        StringBuilder sql = new StringBuilder();
        sql.append(" and DEPT_CODE IN(select a.DEPT_CODE from SY_ORG_DEPT a ,");
        sql.append("  (SELECT ODEPT_CODE, CODE_PATH FROM SY_ORG_DEPT WHERE DEPT_CODE IN ('");
        sql.append(deptCodes);
        sql.append("')) b WHERE a.ODEPT_CODE = b.ODEPT_CODE ");
        sql.append(" AND A.CODE_PATH LIKE b.CODE_PATH || '%')");
        sql.append(" AND S_FLAG = 1  AND USER_CODE IN(");
        sql.append(" SELECT DISTINCT USER_CODE FROM SY_ORG_ROLE_USER");
        sql.append(" WHERE 1 = 1 AND ").append(COL_ROLE_CODE).append(" IN ('");
        sql.append(roleCodes);
        sql.append("') AND ").append(COL_S_FLAG).append(" = 1").append(authStateSql()).append(")");

        return getUsersByCondition(sql.toString());
    }

    /**
     * 查找指定机构、部门 + 指定角色下的所有用户。给出了部门ID，可以取出处室内的用户，给出了机构ID，可以取出本机构下角色内所有用户。
     * @param odept 部门CODE字符串
     * @return 符合条件的用户列表
     */
    public static List<Bean> getUsersInOdept(String odept) {
        SqlBean sql = new SqlBean();
        sql.and("ODEPT_CODE", odept);
        sql.and(COL_S_FLAG, 1).and("USER_STATE", 1);
        sql.selects("USER_CODE");

        return ServDao.finds(ServMgr.SY_ORG_USER, sql);
    }

    /**
     * 取得角色用户列表
     * 
     * @param cmpyCode 公司编码
     * @param roleCodes 角色Code串
     * @return 用户Bean列表
     */
    public static List<Bean> getUserListByRole(String roleCodes, String cmpyCode) {
        if (roleCodes.indexOf(Constant.SEPARATOR) > 0) {
            roleCodes = roleCodes.replaceAll(Constant.SEPARATOR, "'" + Constant.SEPARATOR + "'");
        }

        StringBuilder condition = new StringBuilder(" and S_FLAG=1 and CMPY_CODE = '");
        condition.append(cmpyCode);
        condition.append("'");
        condition.append(" and " + COL_USER_CODE + " in (select distinct " + COL_USER_CODE + " from "
                + TBL_SY_ORG_ROLE_USER + " where " + COL_ROLE_CODE + " in ('" + roleCodes + "')");
        condition.append(" and " + COL_S_FLAG + "=1").append(authStateSql()).append(")");
        return ServDao.finds(ServMgr.SY_ORG_USER, condition.toString());
    }

    /**
     * 取得角色用户列表
     * 
     * @param roleCodes 角色Code串
     * @return 用户Bean列表
     */
    public static ArrayList<UserBean> getUsersByRole(String roleCodes) {
        if (roleCodes.indexOf(Constant.SEPARATOR) > 0) {
            roleCodes = roleCodes.replaceAll(Constant.SEPARATOR, "'" + Constant.SEPARATOR + "'");
        }

        StringBuilder condition = new StringBuilder(" and S_FLAG=1");
        condition.append(" and " + COL_USER_CODE + " in (select distinct " + COL_USER_CODE + " from "
                + TBL_SY_ORG_ROLE_USER + " where " + COL_ROLE_CODE + " in ('" + roleCodes + "')");
        condition.append(" and " + COL_S_FLAG + "=1").append(authStateSql()).append(")");

        return getUsersByCondition(condition.toString());
    }

    /**
     * 判断用户是否具有某些角色
     * 
     * @param userCode 用户Code
     * @param roleCodes 角色Code串
     * @return true/false
     */
    public static boolean existInRoles(String userCode, String roleCodes) {
        try {
            UserBean userBean = UserMgr.getUser(userCode);
            roleCodes = RoleMgr.convertNameToCode(userBean.getCmpyCode(), roleCodes);
        } catch (Exception e) {
            return false;
        }
        SqlBean sql = new SqlBean();
        sql.and("S_FLAG", Constant.YES_INT).and("USER_CODE", userCode)
                .andIn("ROLE_CODE", roleCodes.split(Constant.SEPARATOR));
//        authStateSql(sql);
        return (ServDao.count(ServMgr.SY_ORG_ROLE_USER, sql) > 0);
    }
    
    /**
     * 判断指定的部门 + 角色中，是否包含用户
     * @param deptCode 单个部门CODE
     * @param roleCodes 角色Code，多个角色之间使用逗号分隔。
     * @return 是否用户在指定角色 + 部门中
     */
    public static boolean existUserInDeptRole(String deptCode , String roleCodes) {
        DeptBean dept = OrgMgr.getDept(deptCode);
        
        if (dept != null) {
            roleCodes = RoleMgr.convertNameToCode(dept.getCmpyCode(), roleCodes);
            SqlBean sql = new SqlBean();
            if (dept.getType() == Constant.DEPT_TYPE_ORG) {
                sql.and("ODEPT_CODE", dept.getCode());
            } else {
                sql.andLikeRT("CODE_PATH", dept.getCodePath());
            }
            sql.andIn("ROLE_CODE", roleCodes.split(","));
            authStateSql(sql);
            int count = ServDao.count(ServMgr.SY_ORG_ROLE_USER, sql);
            if (count > 0) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 判断指定的部门 + 角色中，除了指定用户之外还有其他人。
     * @param deptCode 单个部门CODE
     * @param roleCodes 角色Code，多个角色之间使用逗号分隔。
     * @param ignoreUser 需要忽略的用户Code
     * @return 判断指定的部门 + 角色中，除了指定用户之外还有其他人。
     */
    public static boolean existUserInDeptRole(String deptCode , String roleCodes, String ignoreUser) {
        DeptBean dept = OrgMgr.getDept(deptCode);
        
        if (dept != null) {
            roleCodes = RoleMgr.convertNameToCode(dept.getCmpyCode(), roleCodes);
            SqlBean sql = new SqlBean();
            if (dept.getType() == Constant.DEPT_TYPE_ORG) {
                sql.and("ODEPT_CODE", dept.getCode());
            } else {
                sql.andLikeRT("CODE_PATH", dept.getCodePath());
            }
            sql.andIn("ROLE_CODE", roleCodes.split(","));
            sql.andNot("USER_CODE", ignoreUser);
            authStateSql(sql);
            int count = ServDao.count(ServMgr.SY_ORG_ROLE_USER, sql);
            if (count > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断用户是否在某些部门中
     * 
     * @param userCode 用户Code
     * @param deptCodes 部门Code串
     * @return true/false
     */
    public static boolean existInDepts(String userCode, String deptCodes) {
        Bean param = new Bean();
        StringBuilder sb = new StringBuilder("and S_FLAG=1 and USER_CODE='");
        sb.append(userCode).append("' and (DEPT_CODE in('").append(deptCodes.replaceAll(",", "','")).append("')");
        sb.append(" or " + COL_TDEPT_CODE + " in ('").append(deptCodes.replaceAll(",", "','")).append("'))");
        param.set(Constant.PARAM_WHERE, sb.toString());

        return (ServDao.count(ServMgr.SY_ORG_USER, param) > 0);
    }

    /**
     * 获得代替用户
     * 
     * @param userCode 用户Code
     * @return 代替用户的Bean
     */
    public static UserBean getBench(String userCode) {
        Bean paramBean = new Bean();
        StringBuilder condition = new StringBuilder(" and " + COL_S_FLAG + "=1");
        condition.append(" and " + COL_USER_CODE + " in (select distinct " + COL_USER_CODE + " from "
                + TBL_SY_ORG_USER_RELATION + " where " + COL_ORIGIN_USER_CODE + " = '" + userCode + "'");
        paramBean.set(Constant.PARAM_WHERE, condition.toString());

        if (null == ServDao.find(ServMgr.SY_ORG_USER, paramBean)) {
            return null;
        }

        return new UserBean(ServDao.find(ServMgr.SY_ORG_USER, paramBean));
    }

    /**
     * 获取在用户关系中定义的 领导-秘书 的领导列表
     * 
     * @param userCode 用户编码
     * @return 领导的userbean 列表
     */
    public static List<UserBean> getLeaders(String userCode) {
        List<UserBean> leaders = new ArrayList<UserBean>();
        Bean paramBean = new Bean();
        StringBuilder condition = new StringBuilder(" and " + COL_S_FLAG + "=1");
        condition.append(" and " + COL_USER_CODE + " in (select distinct " + COL_USER_CODE + " from "
                + TBL_SY_ORG_USER_RELATION + " where " + COL_ORIGIN_USER_CODE + " = '" + userCode + "'" + " and "
                + COL_RELATION_TYPE + " = " + USER_RELATE_LEAD_SEC + ")");
        paramBean.set(Constant.PARAM_WHERE, condition.toString());
        List<Bean> leaderUsers = ServDao.finds(ServMgr.SY_ORG_USER, paramBean);

        for (Bean user : leaderUsers) {
            leaders.add(new UserBean(user));
        }

        return leaders;
    }

    /**
     * 获取在用户关系中定义的 领导-秘书 的领导列表
     * 
     * @param userCode 用户编码-领导
     * @return 秘书userbean 列表
     */
    public static List<UserBean> getSecretor(String userCode) {
        SqlBean sql = new SqlBean().and(COL_S_FLAG, Constant.YES_INT);
        sql.andInSub(COL_USER_CODE, "select distinct " + COL_USER_CODE + " from " + TBL_SY_ORG_USER_RELATION
                + " where " + COL_USER_CODE + "=? and " + COL_RELATION_TYPE + "=?", userCode, USER_RELATE_LEAD_SEC);

        List<UserBean> secretorUsers = new ArrayList<UserBean>();
        List<Bean> secretors = ServDao.finds(ServMgr.SY_ORG_USER, sql);
        for (Bean user : secretors) {
            secretorUsers.add(new UserBean(user));
        }
        return secretorUsers;
    }

    /**
     * 更新数据的方法
     * 
     * @param userCode 用户编码
     * @param key 字段名称
     * @param obj 字段值
     */
    public static void update(String userCode, String key, Object obj) {
        Bean userBean = new Bean(userCode);
        userBean.set(key, obj);
        ServDao.update(ServMgr.SY_ORG_USER, userBean);
    }

    /**
     * 批量修改用户信息
     * 
     * @param cmpyCode 公司编码
     * @param key 字段名称
     * @param obj 字段值
     */
    public static void batchUpdate(String cmpyCode, String key, Object obj) {
        Bean setBean = new Bean().set(key, obj);
        SqlBean sql = new SqlBean().and("CMPY_CODE", cmpyCode);
        ServDao.updates(ServMgr.SY_ORG_USER, setBean, sql);
    }

    /**
     * 用户批量添加角色
     * 
     * @param cmpyCode 公司编码
     * @param userCode 用户编码
     * @param roleCodes 角色编码（支持多个角色批量添加）
     * @return 添加成功数量
     */
    public static int addRoles(String cmpyCode, String userCode, String... roleCodes) {
        int count = 0;
        List<Bean> roleUsers = new ArrayList<Bean>();
        UserBean userBean = getUser(userCode);
        String[] curRoles = userBean.getRoleCodes();
        for (String role : roleCodes) {
            if (!Lang.arrayHas(curRoles, role)) { // 只添加不再已有角色中的
                Bean roleUser = new Bean();
                roleUser.set("CMPY_CODE", cmpyCode);
                roleUser.set("USER_CODE", userCode);
                roleUser.set("ROLE_CODE", role);
                roleUsers.add(roleUser);
            }
        }
        if (roleUsers.size() > 0) {
            count = ServDao.creates(ServMgr.SY_ORG_ROLE_USER, roleUsers);
        }
        return count;
    }

    /**
     * 获取委托给当前用户的用户编码列表字符串，逗号分隔。
     * 
     * @param userCode 当前用户编码
     * @return 委托用户编码列表，逗号分隔
     */
    public static String getAgentCodesStr(String userCode) {
        return Lang.arrayJoin(getAgentCodes(userCode));
    }

    /**
     * 获取委托给当前用户的用户编码列表
     * 
     * @param userCode 当前用户编码
     * @return 委托用户编码列表
     */
    public static String[] getAgentCodes(String userCode) {
        SqlBean sql = new SqlBean();
        sql.and("TO_USER_CODE", userCode).and("AGT_STATUS", UserAgentServ.AGT_STATUS_RUNNING)
                .andLTE("AGT_BEGIN_DATE", DateUtils.getDate());
        List<Bean> agtList = ServDao.finds(ServMgr.SY_ORG_USER_AGENT, sql);
        String[] codes = new String[agtList.size()];
        int i = 0;
        for (Bean agt : agtList) {
            codes[i] = agt.getStr("USER_CODE");
            i++;
        }
        return codes;
    }

    /**
     * 获取用户状态，不存在返回null
     * 
     * @param token 用户令牌
     * @return 最新的用户状态信息
     */
    public static UserStateBean getUserStateByToken(String token) {
        SqlBean where = new SqlBean().and("USER_TOKEN", token);
        Bean userState = ServDao.find(ServMgr.SY_ORG_USER_STATE, where);
        if (userState != null) {
            return new UserStateBean(userState);
        } else {
            return null;
        }
    }

    /**
     * 获取用户状态，不存在返回null
     * 
     * @param userCode 用户编码
     * @return 最新的用户状态信息
     */
    public static UserStateBean getUserState(String userCode) {
        Bean userState = ServDao.find(ServMgr.SY_ORG_USER_STATE, userCode);
        if (userState != null) {
            return new UserStateBean(userState);
        } else {
            return null;
        }
    }

    /**
     * 获取用户状态，不存在创建或返回null
     * 
     * @param userCode 用户编码
     * @return 最新的用户状态信息
     */
    public static UserStateBean getUserStateOrCreate(String userCode) {
        Bean userState = ServDao.find(ServMgr.SY_ORG_USER_STATE, userCode);
        if (userState != null) {
            return new UserStateBean(userState);
        } else {
            Bean userBean = ServDao.find(ServMgr.SY_ORG_USER_ALL, userCode);
            if (userBean != null) {
                Bean state = new Bean();
                state.set("USER_CODE", userCode);
                Bean user = ServDao.save(ServMgr.SY_ORG_USER_STATE, state);
                return new UserStateBean(user);
            } else {
                return null;
            }
        }
    }

    /**
     * 更新用户状态，缺省检查用户信息是否存在
     * 
     * @param state 用户状态信息,要求必须有USER_CODE参数
     */
    public static void saveUserState(Bean state) {
        saveUserState(state, true);
    }

    /**
     * 更新用户状态
     * 
     * @param state 用户状态信息,要求必须有USER_CODE参数
     * @param checkExists 是否检查用户状态信息存在否
     * @return 是否为新用户
     */
    public static boolean saveUserState(Bean state, boolean checkExists) {
        boolean isNew = true;
        if (checkExists) { // 检查用户状态信息
            String userCode = state.getStr("USER_CODE");
            SqlBean param = new SqlBean().and("USER_CODE", userCode);
            if (ServDao.count(ServMgr.SY_ORG_USER_STATE, param) > 0) { // 如果数据已存在调整为更新模式
                isNew = false;
                state.setId(userCode);
            }
        }
        ServDao.save(ServMgr.SY_ORG_USER_STATE, state);
        return isNew;
    }

    /**
     * 清除指定公司下用户列表的菜单时间，确保菜单重新生成
     * @param cmpyCode 公司编码
     */
    public static void clearMenuByCmpy(String cmpyCode) {
        SqlBean sql = new SqlBean();
        sql.andNotNull("MENU_TIME")
                .andSub("USER_CODE", "in", "select USER_CODE from SY_ORG_USER where CMPY_CODE=?", cmpyCode);
        Bean set = new Bean().set("MENU_TIME", "");
        set.set("R_MTIME", DateUtils.getDatetimeTS());
        ServDao.updates(ServMgr.SY_ORG_USER_STATE, set, sql);
    }

    /**
     * 清除指定角色下用户列表的菜单时间，确保菜单重新生成
     * @param roleCode 角色编码
     */
    public static void clearMenuByRole(String roleCode) {
        clearMenuByRole(roleCode, null);
    }

    /**
     * 清除指定角色下用户列表的菜单时间，确保菜单重新生成
     * @param roleCode 角色编码
     * @param cmpyCode 公司编码，null为不判断公司编码
     */
    public static void clearMenuByRole(String roleCode, String cmpyCode) {
        Object[] subVars;
        String subSql = "select USER_CODE from SY_ORG_ROLE_USER where ROLE_CODE=?";
        if (cmpyCode != null) {
            subSql = subSql + " and CMPY_CODE=?";
            subVars = new Object[] { roleCode, cmpyCode };
        } else {
            subVars = new Object[] { roleCode };
        }
        SqlBean sql = new SqlBean();
        sql.andInSub("USER_CODE", subSql, subVars).andNotNull("MENU_TIME");
        Bean set = new Bean().set("MENU_TIME", "");
        set.set("R_MTIME", DateUtils.getDatetimeTS());
        ServDao.updates(ServMgr.SY_ORG_USER_STATE, set, sql);
    }

    /**
     * 清除部门下指定用户列表的菜单时间，确保菜单重新生成
     * @param deptCode 部门编码
     */
    public static void clearMenuByDept(String deptCode) {
        SqlBean sql = new SqlBean();
        sql.andInSub("USER_CODE", "select USER_CODE from SY_BASE_USER_V where S_FLAG=1 and CODE_PATH like '%'||?||'%'",
                deptCode).andNotNull("MENU_TIME");
        Bean set = new Bean().set("MENU_TIME", "");
        set.set("R_MTIME", DateUtils.getDatetimeTS());
        ServDao.updates(ServMgr.SY_ORG_USER_STATE, set, sql);
    }

    /**
     * 清除指定用户列表的菜单时间，确保菜单重新生成
     * @param userCodes 用户编码列表，多个逗号分隔
     */
    public static void clearMenuByUsers(String userCodes) {
        SqlBean sql = new SqlBean();
        sql.andIn("USER_CODE", userCodes.split(Constant.SEPARATOR));
        Bean set = new Bean().set("MENU_TIME", "");
        set.set("R_MTIME", DateUtils.getDatetimeTS());
        ServDao.updates(ServMgr.SY_ORG_USER_STATE, set, sql);
    }

    /**
     * 获取用户头像路径
     * @param imgSrc 原始用户头像文件名
     * @param sex 性别
     * @return 从根开始的用户头像路径
     */
    public static String getUserImg(String imgSrc, int sex) {
        return getUserImg(imgSrc, sex, "");
    }

    /**
     * 获取用户头像路径
     * @param imgSrc 原始用户头像文件名
     * @param sex 性别
     * @param timestamp 时间戳，确保修改后不缓存
     * @return 从根开始的用户头像路径
     */
    public static String getUserImg(String imgSrc, int sex, String timestamp) {
        String img;
        if (imgSrc.length() > 0) {
            int pos = imgSrc.indexOf(",");
            if (pos >= 0) {
                img = imgSrc.substring(0, pos);
            } else {
                img = imgSrc;
            }
            img = "/file/ICON_" + img + "?t=" + timestamp;
        } else {
            img = Context.app("USER_PNG_DEFAULT", "/sy/theme/default/images/common/user") + sex + ".png";
        }
        return img;
    }
   
    /**
     * 清除当前用户缓存，包含当前在线用户缓存变量信息
     */
    public static void clearSelfUserCache() {
        UserBean userBean = Context.getUserBean();
        clearSelfUserCache(userBean);
    }
    
    /**
     * 清除当前用户缓存，包含当前在线用户缓存变量信息
     * @param userBean 用户Bean
     */
    public static void clearSelfUserCache(UserBean userBean) {
        clearUserCache(userBean.getCode());
        userBean.clearUserExt();
    }

    /**
     * 清除指定用户的UserBean cache。
     * @param userCode 用户ID
     */
    public static void clearUserCache(String userCode) {
        CacheMgr.getInstance().remove(userCode, UserMgr.CACHE_TYPE_USER);
    }

    /**
     * 从缓存中获取用户对应菜单信息
     * @param userCode 用户编码
     * @return 菜单列表，如果不存在则返回null
     */
    @SuppressWarnings("unchecked")
    public static List<Bean> getCacheMenuList(String userCode) {
        List<Bean> menuTree = (List<Bean>) CacheMgr.getInstance().get(userCode, CACHE_TYPE_USER_MENU);
        if (menuTree == null) { // 缓存中不存在菜单
            // 从文件中获取菜单
            menuTree = MenuServ.menuFromFile(userCode);
            if (menuTree == null) { // 文件中不存在菜单则生成菜单文件
                menuTree = MenuServ.menuToFile(userCode);
                clearMenuByUsers(userCode); // 更新菜单时间
            }
            setCacheMenuList(userCode, menuTree); // 设置缓存
        }
        return menuTree;
    }

    /**
     * 将用户对应菜单信息设置到缓存中
     * @param userCode 用户编码
     * @param menuList 菜单列表
     */
    public static void setCacheMenuList(String userCode, List<Bean> menuList) {
        CacheMgr.getInstance().set(userCode, menuList, CACHE_TYPE_USER_MENU); // 设置缓存
    }

    /**
     * 清除缓存中获取用户对应菜单信息
     * @param userCode 用户编码
     */
    public static void clearCacheMenuList(String userCode) {
        CacheMgr.getInstance().remove(userCode, CACHE_TYPE_USER_MENU); // 清除菜单缓存
    }

    /**
     * 特殊处理用户标识字段，一旦设定用户标识字段，会自动获取用户名称、在线状态、图标等信息
     * @param itemCode 用户字段编码
     * @param bean 一条记录的对应的数据bean
     */
    public static void appendUserItemInfo(String itemCode, Bean bean) {
        String userCode = bean.getStr(itemCode);
        if (userCode.length() == 0) {
            return;
        }
        try{
	        Bean user = getUser(userCode);
	        if (null == user) {
	            return;
	        }
	        int userStatus = Context.getUserSessionId(userCode) == null ? Constant.NO_INT : Constant.YES_INT;
	        bean.set(itemCode + "__NAME", user.getStr("USER_NAME"));
	        bean.set(itemCode + "__STATUS", userStatus);
	        String img = UserMgr.getUserImg(user.getStr("USER_IMG_SRC"), user.getInt("USER_SEX"));
	        bean.set(itemCode + "__IMG", img);
        } catch (Exception e) {
        	
        }
    }

    /**
     * 兼岗：判断是否是主用户
     * @param userCode 主用户编码
     * @return boolean 是/否
     */
    public static boolean isMainUser(String userCode) {
        Bean mainUserBean = getUser(userCode);
        if (mainUserBean == null) {
            return true;
        }
        int jiangangFlag = mainUserBean.getInt("JIANGANG_FLAG");
        if (jiangangFlag == Constant.YES_INT) {
            return false;
        }
        return true;
    }

    /**
     * 兼岗：根据主用户编码获得兼岗记录
     * @param mainCode 主用户编码
     * @return List<Bean> 兼岗记录
     */
    public static List<Bean> getJiangangListByMainUser(String mainCode) {
        // 获取兼岗表的bean
        List<Bean> records;
        SqlBean sql = new SqlBean();
        sql.and("RELATION_TYPE", Constant.YES_INT);
        sql.and("S_FLAG", Constant.YES_INT);
        sql.and("ORIGIN_USER_CODE", mainCode);
        records = ServDao.finds("SY_ORG_USER_JIANGANG", sql);
        if (records == null) {
            records = new ArrayList<Bean>();
        }
        return records;
    }

    /**
     * 兼岗：根据主用户编码获得兼岗用户Bean列表
     * @param mainCode 主用户编码
     * @return List<Bean> 兼岗用户列表
     */
    public static List<Bean> getAuxiliaryUserBeansByMainUser(String mainCode) {
        // 根据主用户编码获得兼岗记录
        List<Bean> records = getJiangangListByMainUser(mainCode);
        // 转换成userbean
        List<Bean> users = new ArrayList<Bean>();
        if (records.size() > 0) {
            for (Bean rec : records) {
                String userCode = rec.getStr("USER_CODE");
                Bean userBean = getUser(userCode);
                if (userBean != null && userBean.getInt("USER_STATE") == 1) {
                    users.add(getUser(userCode));
                }
            }
        }
        return users;
    }

    /**
     * 兼岗：根据主用户编码获得兼岗用户组以string的形式（包括主用户）
     * @param mainCode 主用户编码
     * @return List<Bean> 兼岗用户编码（逗号分隔）
     */
    public static String getJiangangUserStrByMainUser(String mainCode) {
        // 根据主用户编码获得兼岗记录
        List<Bean> records = getJiangangListByMainUser(mainCode);
        // 转换成userbean
        String userCodeStr = "";
        if (records.size() > 0) {
            for (Bean rec : records) {
                String userCode = rec.getStr("USER_CODE");
                UserBean user = getUser(userCode);
                if (user == null) {
                    continue;
                }
                if (!userCode.isEmpty() && user.getInt("USER_STATE") == 1) {
                    userCodeStr = Strings.addValue(userCodeStr, userCode);
                }
            }
        }
        userCodeStr = Strings.addValue(userCodeStr, mainCode);
        return userCodeStr;
    }

    /**
     * 兼岗：根据主用户编码获得兼岗用户组以string的形式（不包括主用户）
     * @param mainCode 主用户编码
     * @return List<Bean> 兼岗用户编码（逗号分隔）
     */
    public static String getJiangangUserStrWithoutMainUser(String mainCode) {
        // 根据主用户编码获得兼岗记录
        List<Bean> records = getJiangangListByMainUser(mainCode);
        // 转换成userbean
        String userCodeStr = "";
        if (records.size() > 0) {
            for (Bean rec : records) {
                String userCode = rec.getStr("USER_CODE");
                UserBean user = getUser(userCode);
                if (user == null) {
                    continue;
                }
                if (!userCode.isEmpty() && user.getInt("USER_STATE") == 1) {
                    userCodeStr = Strings.addValue(userCodeStr, userCode);
                }
            }
        }
        return userCodeStr;
    }

    /**
     * 兼岗：根据工号获得兼岗用户组以userbean的形式
     * @param workNum 主用户编码
     * @return List<Bean> 兼岗用户列表
     */
    public static List<Bean> getJiangangUserGroupByWorkNum(String workNum) {
        List<Bean> records;
        SqlBean sql = new SqlBean();
        sql.and("USER_WORK_NUM", workNum);
        sql.and("S_FLAG", Constant.YES_INT);
        sql.and("USER_STATE", Constant.YES_INT);
        records = ServDao.finds("SY_ORG_USER_ALL", sql);
        return records;
    }

    /**
     * 兼岗：根据工号获得兼岗用户组以string的形式
     * @param workNum 主用户编码
     * @return List<Bean> 兼岗用户列表
     */
    public static String getJiangangUserGroupStrByWorkNum(String workNum) {
        // 根据工号获得兼岗用户组
        List<Bean> records = getJiangangUserGroupByWorkNum(workNum);
        // 转换成userbean
        String userCodeStr = "";
        if (records.size() > 0) {
            for (Bean rec : records) {
                String userCode = rec.getStr("USER_CODE");
                if (!userCode.isEmpty()) {
                    userCodeStr = Strings.addValue(userCodeStr, userCode);
                }
            }
        } else {
            userCodeStr = "''";
        }
        return userCodeStr;
    }

    /**
     * 兼岗：根据工号获取主用户
     * @param workNum 用户工号
     * @return UserBean
     */
    public static UserBean getMainUserByWorkNum(String workNum) {
        List<Bean> records;
        SqlBean sql = new SqlBean();
        sql.and("USER_WORK_NUM", workNum);
        sql.and("JIANGANG_FLAG", Constant.NO_INT);
        sql.and("S_FLAG", Constant.YES_INT);
        sql.and("USER_STATE", Constant.YES_INT);
        records = ServDao.finds("SY_ORG_USER_ALL", sql);
        String userCode = "";
        if (records.size() > 0) {
            userCode = records.get(0).getId();
        } else {
            return null;
        }
        return getUser(userCode);
    }
}
