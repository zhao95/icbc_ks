package com.rh.ts.orgdept;

import com.rh.core.serv.CommonServ;
import com.rh.core.serv.ParamBean;
import com.rh.ts.pvlg.PvlgUtils;

public class TsDeptServ extends CommonServ {
		//查询前添加查询条件
		
	protected void beforeQuery(ParamBean paramBean) {
				ParamBean param = new ParamBean();
				param.set("paramBean", paramBean);
				param.set("fieldName","DEPT_PCODE");
				param.set("serviceName", paramBean.getServId());
				PvlgUtils.setOrgPvlgWhere(param);	
			}
	}


