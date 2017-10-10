package com.rh.ts.bmsh.shgz;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
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
}
