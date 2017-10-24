package com.rh.ts.xmgl;

import com.rh.core.serv.CommonServ;
import com.rh.core.serv.ParamBean;
import com.rh.ts.pvlg.PvlgUtils;

public class DapccUtilServ extends CommonServ {
    protected void beforeQuery(ParamBean paramBean) {
	ParamBean param = new ParamBean();
	String ctlgModuleName = "EXAM_GROUP";
	param.set("paramBean", paramBean);
	param.set("ctlgModuleName", ctlgModuleName);
	param.set("serviceName", paramBean.getServId());
	PvlgUtils.setOrgPvlgWhere(param);
    }
}
