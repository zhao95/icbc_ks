package com.rh.ts.shgzk;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ServDao;

public class ShGzkServ extends CommonServ {

	
	/**
	 * 保存提示信息  审核规则库 管理类 提示信息保存
	 */
	public OutBean saveinfo(Bean paramBean){
		String ids = paramBean.getStr("id");
		String mx_name = paramBean.getStr("mx_name");
		Bean find = ServDao.find("TS_XMGL_BMSH_SHGZK_MX",ids);
		if(find!=null){
			
			find.set("MX_NAME", mx_name);
		}
		ServDao.save("TS_XMGL_BMSH_SHGZK_MX", find);
		return new OutBean().setOk();
	}
	/**
	 * 复制规则库明细
	 * @param paramBean
	 * @return
	 */
	public OutBean CopyMx(Bean paramBean){
		String dataId = paramBean.getStr("dataId");
		
		Bean find = ServDao.find("TS_XMGL_BMSH_SHGZK_MX", dataId);
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
			ServDao.save("TS_XMGL_BMSH_SHGZK_MX", newbean);
		}
		return new OutBean().setOk("复制成功");
	}
}
