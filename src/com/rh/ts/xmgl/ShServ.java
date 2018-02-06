package com.rh.ts.xmgl;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;

public class ShServ extends CommonServ {
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
	String ksqzId = paramBean.getStr("ksqzId");
	for (int i = 0; i < ids.split(",").length; i++) {
	    String dataId = ids.split(",")[i];
	    Bean gzkBean = ServDao.find("TS_XMGL_BMSH_SHGZK", dataId);
    	Bean dataBean = new Bean();
    	dataBean.set("XM_ID", xmId);
    	dataBean.set("GZK_ID", gzkBean.getStr("GZ_ID"));
    	dataBean.set("GZ_TYPE", gzkBean.getStr("GZ_TYPE"));
    	dataBean.set("GZ_NAME", gzkBean.getStr("GZ_NAME"));
    	dataBean.set("GZ_INFO", gzkBean.getStr("GZ_INFO"));
    	dataBean.set("GZ_SORT", gzkBean.getStr("GZ_SORT"));
    	dataBean.set("KSQZ_ID", ksqzId);
    	Bean gzBean = ServDao.save("TS_XMGL_BMSH_SHGZ", dataBean);
    	String gzId = gzBean.getId();
    	
	    if("Y05".equals(dataId)){
	    	//证书规则和岗位规则复制
	    	List<Bean> zslist = ServDao.finds("TS_BMSH_RULE", " AND G_ID =''");
	    	for (Bean bean : zslist) {
	    		Bean newBean = new Bean();
				bean.remove("_PK_");
				bean.remove("R_ID");
				bean.remove("S_CMPY");
				bean.remove("S_ATIME");
				bean.remove("S_MTIME");
				bean.remove("S_FLAG");
				bean.remove("ROW_NUM_");
				newBean.copyFrom(bean);
				newBean.set("KSQZ_ID", ksqzId);
				newBean.set("G_ID", gzId);
				ServDao.create("TS_BMSH_RULE_GZK", newBean);
			}
	    	List<Bean> gwlist = ServDao.finds("TS_BMSH_RULE_POST_GZK", " AND GZ_ID=''");
	    	for (Bean bean : gwlist) {
	    		Bean newBean = new Bean();
				bean.remove("_PK_");
				bean.remove("ROLE_DEPT_ID");
				bean.remove("S_CMPY");
				bean.remove("S_ATIME");
				bean.remove("S_MTIME");
				bean.remove("S_FLAG");
				bean.remove("ROW_NUM_");
				newBean.copyFrom(bean);
				newBean.set("KSQZ_ID", ksqzId);
				newBean.set("GZ_ID", gzId);
				ServDao.create("TS_BMSH_RULE_POST_GZK", newBean);
			}
	    }else if("Y09".equals(dataId)){
	    	//证书规则和岗位规则复制
	    	List<Bean> zslist = ServDao.finds("TS_BMSH_RULE_KXLGZ_GZK", " AND GZ_ID =''");
	    	for (Bean bean : zslist) {
	    		Bean newBean = new Bean();
				bean.remove("_PK_");
				bean.remove("POSTION_ID");
				bean.remove("S_CMPY");
				bean.remove("S_ATIME");
				bean.remove("S_MTIME");
				bean.remove("S_FLAG");
				bean.remove("ROW_NUM_");
				newBean.copyFrom(bean);
				newBean.set("KSQZ_ID", ksqzId);
				newBean.set("GZ_ID", gzId);
				ServDao.create("TS_BMSH_RULE_KXLGZ_GZK", newBean);
			}
	    }
	    	
	    	//TS_XMGL_BMSH_SHGZK_MX TS_XMGL_BMSH_SHGZK_MX
	    	List<Bean> gzkMxList = ServDao.finds("TS_XMGL_BMSH_SHGZK_MX", "and GZ_ID = '"+dataId+"'");
	    	for (int j = 0; j < gzkMxList.size(); j++) {
	    		Bean gzkMxBean = gzkMxList.get(j);
	    		String gzk_mx_id = gzkMxBean.getStr("MX_ID");
	    		gzkMxBean.setId("");
	    		gzkMxBean.remove("MX_ID");
	    		gzkMxBean.set("GZ_ID", gzId);
	    		gzkMxBean.set("XM_ID", xmId);
	    		gzkMxBean.set("KSQZ_ID", ksqzId);
	    		gzkMxBean.remove("S_CMPY");
	    		gzkMxBean.remove("S_USER");
	    		gzkMxBean.remove("S_DEPT");
	    		gzkMxBean.remove("S_TDEPT");
	    		gzkMxBean.remove("S_ODEPT");
	    		gzkMxBean.remove("S_MTIME");
	    		gzkMxBean.remove("S_ATIME");
	    		gzkMxBean.set("GZK_MX_ID", gzk_mx_id);
	    		ServDao.save("TS_XMGL_BMSH_SHGZ_MX", gzkMxBean);
	    }
	}
	outBean.setOk();
	return outBean;
    }
}
