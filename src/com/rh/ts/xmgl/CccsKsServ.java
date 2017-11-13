package com.rh.ts.xmgl;

import com.rh.core.serv.CommonServ;
import com.rh.core.serv.ParamBean;
import com.rh.ts.pvlg.PvlgUtils;

public class CccsKsServ extends CommonServ {
	// 查询前添加查询条件
	protected void beforeQuery(ParamBean paramBean) {
		ParamBean param = new ParamBean();
		param.set("paramBean", paramBean);
		param.set("serviceName", paramBean.getServId());
		param.set("fieldName", "S_ODEPT");
		
		PvlgUtils.setOrgPvlgWhere(param);
	}
}
