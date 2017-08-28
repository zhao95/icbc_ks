package com.rh.core.org.mgr;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.RoleBean;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;

/**
 * 角色管理器
 * 
 * @author cuihf
 * 
 */
public class RoleMgr {

    private static final String COL_S_FLAG = "S_FLAG";
    private static final String COL_USER_CODE = "USER_CODE";
    private static final String COL_CMPY_CODE = "CMPY_CODE";
    private static final String TBL_SY_ORG_ROLE_USER = "SY_ORG_ROLE_USER";
    private static final String COL_ROLE_CODE = "ROLE_CODE";

    /**
     * 取得角色Bean
     * 
     * @param roleCode 角色ID
     * @return 角色Bean对象
     */
    public static RoleBean getRole(String roleCode) {
        ParamBean paramBean = new ParamBean(ServMgr.SY_ORG_ROLE, ServMgr.ACT_BYID, roleCode);
        OutBean bean = ServMgr.act(paramBean);
//        if (!bean.isOk()) {
//            throw new TipException(Context.getSyMsg("SY_ROLE_NOT_FOUND", roleCode));
//        }
        return new RoleBean(bean);
    }

    /**
     * 获取查询角色的SQL
     * 
     * @param userCode 用户编码
     * @param cmpyCode 公司编码
     * @return 查询角色的SQL
     */
    public static String getRoleListSql(String userCode, String cmpyCode) {
        StringBuilder roleListSql = new StringBuilder("select distinct " + COL_ROLE_CODE + " from "
                + TBL_SY_ORG_ROLE_USER);
        roleListSql.append(" where " + COL_USER_CODE + "='" + userCode + "'");
        roleListSql.append(" and " + COL_CMPY_CODE + "='" + cmpyCode + "'");
        roleListSql.append(authStateSql());
        roleListSql.append(" and " + COL_S_FLAG + "=1");

        return roleListSql.toString();
    }

    /**
     * 
     * 取得指定用户的角色编码
     * @param userCode 用户编码
     * @param cmpyCode 公司编码
     * @param oDeptLevel 机构层级
     * @return 角色编码串
     */
    public static String[] getRoleCodes(String userCode, String cmpyCode, int oDeptLevel) {
        String publicRole = Context.getSyConf("SY_ORG_ROLE_PUBLIC", "");
        List<Bean> list = Context.getExecutor().query(getRoleListSql(userCode, cmpyCode));
        int size = list.size();
        if (publicRole.length() > 0) { //设置了公共角色
            size += 2;
        }
        String[] rtn = new String[size];
        int i = 0;
        for (Bean bean : list) {
            rtn[i] = bean.getStr(COL_ROLE_CODE);
            i++;
        }
        if (i == (size - 2)) {
            rtn[i] = publicRole;
            rtn[i + 1] = publicRole + oDeptLevel;
        }
        return rtn;
    }
    
    /**
     * 将角色名称转成角色ID
     * @param cmpyCode 机构名称
     * @param roleNames 角色名称
     * @return 转换角色名称成角色Id
     */
    public static String convertNameToCode(String cmpyCode, String roleNames) {
        // 如果参数为空，则返回原值
        if (StringUtils.isBlank(cmpyCode) || StringUtils.isBlank(roleNames)) {
            return roleNames;
        }
        
        String[] names = roleNames.split(",");
        
        HashMap<String, String> map = new HashMap<String, String>();
        
        for (String name: names) {
            map.put(name, name);
        }
        
        SqlBean sql = new SqlBean();
        sql.andIn("ROLE_NAME", names);
        sql.andIn("CMPY_CODE", cmpyCode);
        List<Bean> list = ServDao.finds(ServMgr.SY_ORG_ROLE, sql);
        
        for (Bean bean : list) {
            String id = bean.getStr("ROLE_CODE");
            String roleName = bean.getStr("ROLE_NAME");
            map.put(roleName, id);
        }
        
        Collection<String> coll = map.values();
        return StringUtils.join(coll.toArray(new String[0]), ',');
    }
    
    
    /**
     * 取得角色用户的“授权”过滤条件
     */
    public static String authStateSql() {
    	return " and AUTH_STATE in(0, 1, 2)";
    }
    
    public static void authStateSql(SqlBean sqlBean) {
    	if (sqlBean == null) {
    		return;
    	}
    	sqlBean.andIn("AUTH_STATE", new Object[]{0, 1, 2});
    }
}
