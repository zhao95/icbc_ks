package com.rh.ts.xmgl;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;

public class DapccServ extends CommonServ{
    
    public OutBean getKcAndCc(ParamBean paramBean){
	OutBean outBean = new OutBean();
	String xmId = paramBean.getStr("xmId");
	List<Bean> list = ServDao.finds("TS_XMGL_KCAP_DAPCC", "and XM_ID='"+xmId+"'");
	for (int i = 0; i < list.size(); i++) {
	    String ccId = list.get(i).getId();
	    List<Bean> list2 = ServDao.finds("TS_XMGL_KCAP_DAPCC_CCSJ", "and CC_ID = '"+ccId+"'");
	    list.get(i).set("ccList", list2);
	}
	outBean.set("list", list);
	return outBean;
    }

}
