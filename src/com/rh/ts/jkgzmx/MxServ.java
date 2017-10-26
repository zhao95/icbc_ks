package com.rh.ts.jkgzmx;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ServDao;

public class MxServ extends CommonServ {
	public OutBean getJkgz(Bean paramBean){
		OutBean out = new OutBean();
		String gzid = paramBean.getStr("GZ_ID");
		String where = "AND GZ_ID='"+gzid+"'";
		List<Bean> finds = ServDao.finds("TS_XMGL_BM_JKGLGZK_MX", where);
		if(finds.size()!=0){
			Bean bean = finds.get(0);
			out.set("gzbean", bean.getStr("MX_NAME"));
		}
		return out;
	}
}
