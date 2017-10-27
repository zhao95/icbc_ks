package com.rh.ts.kcgl;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.ParamBean;

public class KcglJyServ extends CommonServ{
    protected void beforeQuery(ParamBean paramBean) {
	Bean pvlgBean = paramBean.getBean("extParams").getBean("USER_PVLG");
	if (pvlgBean.isEmpty()) {
	    paramBean.setQueryExtWhere(" and (odept_level < 3 or KC_STATE2 = 1)");
	}
    }
}
