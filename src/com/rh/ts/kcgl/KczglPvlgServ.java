package com.rh.ts.kcgl;

import com.rh.core.serv.CommonServ;
import com.rh.core.serv.ParamBean;
import com.rh.ts.pvlg.PvlgUtils;

public class KczglPvlgServ extends CommonServ {
	// 查询前添加查询条件
	protected void beforeQuery(ParamBean paramBean) {
		ParamBean param = new ParamBean();
		String ctlgModuleName = "EXAM_GROUP";
		param.set("paramBean", paramBean);
		param.set("ctlgModuleName", ctlgModuleName);
		PvlgUtils.setCtlgPvlgWhere(param);
	}
}
