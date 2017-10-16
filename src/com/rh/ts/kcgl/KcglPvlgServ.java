package com.rh.ts.kcgl;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;

public class KcglPvlgServ  extends CommonServ{
	//查询前添加查询条件
	protected void beforeQuery(ParamBean paramBean) {
			ParamBean param = new ParamBean();
			String  ctlgModuleName="EXAM_ROOM";
			String  serviceName="TS_KCGL";
			param.set("paramBean", paramBean);
			param.set("ctlgModuleName", ctlgModuleName);
			param.set("serviceName",serviceName);
			ServMgr.act("TS_UTIL", "userPvlg", param);		
		}
}
