package com.rh.core.org.serv;

import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.UserBean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;

/**
 * 
 * @author yangjy
 * 
 */
public class RoleServ extends CommonServ {
    private static final String ROLE_SCOPE_DEF = "ROLE_SCOPE_DEF";

    @Override
    protected void beforeSave(ParamBean paramBean) {
        super.beforeSave(paramBean);
        if (paramBean.isNotEmpty(ROLE_SCOPE_DEF)) { // 根据前台数据，合并角色显示范围
            String scopeDef = paramBean.getStr(ROLE_SCOPE_DEF);
            String[] scopes = scopeDef.split(",");

            int scopeVal = 0;

            for (String scope : scopes) {
                scopeVal += Integer.parseInt(scope);
            }

            if (scopeVal > 511) {
                scopeVal = 511;
            }

            paramBean.set("ROLE_SCOPE", scopeVal);
        }
    }

    @Override
    protected void afterByid(ParamBean paramBean, OutBean outBean) {
        super.afterByid(paramBean, outBean);

        int scope = outBean.getInt("ROLE_SCOPE");
        if (scope > 0) { // 根据合并的数据拆分出可供多选框反选的值。
            String scopeDef = "0";

            for (int i = 0; i < 10; i++) {
                int pos = (int) Math.pow(2, i);
                if ((scope & pos) > 0) {
                    scopeDef += "," + pos;
                }
            }

            outBean.set(ROLE_SCOPE_DEF, scopeDef);
        }
    }

    @Override
    protected void beforeQuery(ParamBean paramBean) {
        super.beforeQuery(paramBean);
        
        // 如果是全部角色，则不按照级别过滤
        if (paramBean.getServId().equals("SY_ORG_ROLE_ALL")) {
            return;
        }

        UserBean user = Context.getUserBean();

        int level = user.getODeptLevel();
        if (level >= 1) {
            level = level - 1;
        }
        
        //增加按照级别过滤角色的代码
        StringBuilder where = new StringBuilder();
        where.append(" and ")
            .append(Transaction.getBuilder().bitand("ROLE_SCOPE", String.valueOf((int) Math.pow(2, level))))
            .append(" > 0");

        paramBean.setQueryExtWhere(where.toString());

    }

}
