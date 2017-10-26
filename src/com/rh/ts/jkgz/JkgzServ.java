package com.rh.ts.jkgz;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;

public class JkgzServ extends CommonServ {
	/**
     * 从规则库中倒入规则  注：TS_XMGL_BMSH_SHGZK中的主键GZ_ID
     * 保存到TS_XMGL_BMSH_SHGZ的字段GZK_ID非GZ_ID
     * @param paramBean
     * @return
     */
    public OutBean impShgz(ParamBean paramBean) {
	OutBean outBean =new OutBean();
	String xmId = paramBean.getStr("xmId");
	String ids = paramBean.getStr("ids");
	for (int i = 0; i < ids.split(",").length; i++) {
	    String dataId = ids.split(",")[i];
	    Bean bean = ServDao.find("TS_XMGL_BMSH_SHGZK", dataId);
	    Bean dataBean = new Bean();
	    dataBean.set("XM_ID", xmId);
	    dataBean.set("GZK_ID", bean.getStr("GZ_ID"));
	    dataBean.set("GZ_TYPE", bean.getStr("GZ_TYPE"));
	    dataBean.set("GZ_NAME", bean.getStr("GZ_NAME"));
	    dataBean.set("GZ_INFO", bean.getStr("GZ_INFO"));
	    dataBean.set("GZ_SORT", bean.getStr("GZ_SORT"));
	    Bean gzBean = ServDao.save("TS_XMGL_BM_JKGZ", dataBean);
	    String gzId = gzBean.getId();
	    //TS_XMGL_BMSH_SHGZK_MX TS_XMGL_BMSH_SHGZK_MX
	    List<Bean> gzkMxList = ServDao.finds("ts_xmgl_bm_jkglgzk_mx", "and GZ_ID = '"+dataId+"'");
	    for (int j = 0; j < gzkMxList.size(); j++) {
		Bean gzkMxBean = gzkMxList.get(j);
		gzkMxBean.setId("");
		gzkMxBean.remove("MX_ID");
		gzkMxBean.set("GZ_ID", gzId);
		gzkMxBean.set("XM_ID", xmId);
		gzkMxBean.remove("S_CMPY");
		gzkMxBean.remove("S_USER");
		gzkMxBean.remove("S_DEPT");
		gzkMxBean.remove("S_TDEPT");
		gzkMxBean.remove("S_ODEPT");
		gzkMxBean.remove("S_MTIME");
		gzkMxBean.remove("S_ATIME");
		ServDao.save("ts_xmgl_bm_jkglgz_mx", gzkMxBean);
	    }
	}
	outBean.setOk();
	return outBean;
    }
}
