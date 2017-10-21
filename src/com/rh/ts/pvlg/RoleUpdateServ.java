package com.rh.ts.pvlg;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.ParamBean;

public class RoleUpdateServ extends CommonServ{
    @Override
    protected void beforeQuery(ParamBean paramBean) {
        super.beforeQuery(paramBean);
//        Bean extWhereBean = paramBean.getBean("extParams");
        paramBean.setQueryExtWhere(" and 1=2");
        
    }
}
