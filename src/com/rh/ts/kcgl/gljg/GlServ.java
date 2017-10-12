package com.rh.ts.kcgl.gljg;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ServDao;

public class GlServ extends CommonServ {

	public OutBean editfar(Bean paramBean){
		String far = paramBean.getStr("far");
		String dataId = paramBean.getStr("dataId");
		
		Bean find = ServDao.find("TS_KCGL_GLJG", dataId);
		if(find!=null){
			
			find.set("JG_FAR", Integer.parseInt(far));
			ServDao.save("TS_KCGL_GLJG",find);
		}else{
			return new OutBean().setError("保存失败");
		}
		return new OutBean();
	}
	
	public OutBean getData(Bean paramBean){
		
		OutBean out = new OutBean();
		String dataId = paramBean.getStr("dataId");
		Bean find = ServDao.find("TS_KCGL_GLJG", dataId);
	if(find!=null){
			
			int int1 = find.getInt("JG_FAR");
			out.set("far", int1);
	}
	return out;
}
}