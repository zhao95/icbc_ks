package com.rh.ts.orguser;

import com.rh.core.serv.CommonServ;
import com.rh.core.serv.ParamBean;
import com.rh.ts.pvlg.PvlgUtils;

public class TsUserServ extends CommonServ {
		//查询前添加查询条件
		
	protected void beforeQuery(ParamBean paramBean) {
				ParamBean param = new ParamBean();
				param.set("paramBean", paramBean);
				param.set("fieldName","DEPT_CODE");
				param.set("serviceName", paramBean.getServId());
				PvlgUtils.setOrgPvlgWhere(param);	
			}
	}


