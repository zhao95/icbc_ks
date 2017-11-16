package com.rh.ts.bmsh.shgz;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
/**
 * 
 * @author shiyun
 *审核规则明细  
 */
public class MxServ extends CommonServ {

	public OutBean CopyMx(Bean paramBean){
		String dataId = paramBean.getStr("dataId");
		
		Bean find = ServDao.find("TS_XMGL_BMSH_SHGZ_MX", dataId);
		if(find!=null){
			Bean newbean = new Bean();
			newbean.copyFrom(find);
			newbean.remove("MX_ID");
			newbean.remove("S_CMPY");
			newbean.remove("S_TDEPT");
			newbean.remove("S_ODEPT");
			newbean.remove("S_ATIME");
			newbean.remove("S_MTIME");
			newbean.remove("S_USER");
			newbean.remove("S_FLAG");
			newbean.remove("_PK_");
			newbean.remove("ROW_NUM_");
			ServDao.save("TS_XMGL_BMSH_SHGZ_MX", newbean);
		}
		return new OutBean().setOk("复制成功");
	}
	public OutBean getJkgz(Bean paramBean){
		OutBean out = new OutBean();
		String gzid = paramBean.getStr("GZ_ID");
		String where = "AND GZ_ID='"+gzid+"'";
		List<Bean> finds = ServDao.finds("TS_XMGL_BMSH_SHGZK_MX", where);
		if(finds.size()!=0){
			Bean bean = finds.get(0);
			out.set("gzbean", bean.getStr("MX_NAME"));
		}
		return out;
	}
	/**
	 * 保存提示信息
	 */
	public OutBean saveinfo(Bean paramBean){
		String ids = paramBean.getStr("id");
		String mx_name = paramBean.getStr("mx_name");
		Bean find = ServDao.find("TS_XMGL_BMSH_SHGZ_MX",ids);
		if(find!=null){
			
			find.set("MX_NAME", mx_name);
		}
		ServDao.save("TS_XMGL_BMSH_SHGZ_MX", find);
		return new OutBean().setOk();
	}
	
	/**
	 * 取信息 重置
	 */
	public OutBean chongzhi(Bean paramBean){
		OutBean out = new OutBean();
		Bean find = ServDao.find("TS_XMGL_BMSH_SHGZK_MX", "Y01109");
		if(find!=null){
			out.set("gzbean", find.getStr("MX_NAME"));
		}
		return out;
	}
	
	 /**
     * 从规则库中倒入规则  注：TS_XMGL_BMSH_SHGZK中的主键GZ_ID
     * 保存到TS_XMGL_BMSH_SHGZ的字段GZK_ID非GZ_ID
     * @param paramBean
     * @return
     */
    public OutBean impShgzMx(ParamBean paramBean) {
	OutBean outBean =new OutBean();
	String xmId = paramBean.getStr("xmId");
	String ids = paramBean.getStr("ids");
	String gz_id = paramBean.getStr("GZ_ID");
	String ksqzId = paramBean.getStr("ksqzId");
	for (int i = 0; i < ids.split(",").length; i++) {
	    String dataId = ids.split(",")[i];
	    if(dataId.equals("Y01108")){
	    	List<Bean> GZMXLIST = ServDao.finds("TS_XMGL_BMSH_SHGZ_MX", "AND KSQZ_ID='"+ksqzId+"' AND GZK_MX_ID='Y01109'");
	    	if(GZMXLIST!=null&&GZMXLIST.size()!=0){
	    		
	    	}else{
	    		Bean bean = ServDao.find("TS_XMGL_BMSH_SHGZK_MX", "Y01109");
	    		Bean dataBean = new Bean();
	    		dataBean.set("XM_ID", xmId);
	    		dataBean.set("MX_NAME", bean.getStr("MX_NAME"));
	    		dataBean.set("MX_VALUE1", bean.getStr("MX_VALUE1"));
	    		dataBean.set("MX_VALUE2", bean.getStr("MX_VALUE2"));
	    		dataBean.set("MX_IMPL", bean.getStr("MX_IMPL"));
	    		dataBean.set("KSQZ_ID", ksqzId);
	    		dataBean.set("GZ_ID", gz_id);
	    		dataBean.set("GZK_MX_ID", bean.getStr("MX_ID"));
	    		ServDao.save("TS_XMGL_BMSH_SHGZ_MX", dataBean);
	    	}
	    }
	    Bean bean = ServDao.find("TS_XMGL_BMSH_SHGZK_MX", dataId);
	    Bean dataBean = new Bean();
	    dataBean.set("XM_ID", xmId);
	    dataBean.set("MX_NAME", bean.getStr("MX_NAME"));
	    dataBean.set("MX_VALUE1", bean.getStr("MX_VALUE1"));
	    dataBean.set("MX_VALUE2", bean.getStr("MX_VALUE2"));
	    dataBean.set("MX_IMPL", bean.getStr("MX_IMPL"));
	    dataBean.set("KSQZ_ID", ksqzId);
	    dataBean.set("GZ_ID", gz_id);
	    dataBean.set("GZK_MX_ID", bean.getStr("MX_ID"));
	    ServDao.save("TS_XMGL_BMSH_SHGZ_MX", dataBean);
	}
	outBean.setOk();
	return outBean;
    }
}
