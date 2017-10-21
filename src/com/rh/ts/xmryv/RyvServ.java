package com.rh.ts.xmryv;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ServDao;

/**
 * 项目报名关联群组编码视图
 * @author shiyun
 *
 */
public class RyvServ extends CommonServ {
	/**
	 * 根据 项目id获取 群组编码
	 * @param paramBean
	 * @return outbean
	 */
	public OutBean getCodes(Bean paramBean){
		OutBean out = new OutBean();
		String xmid = paramBean.getStr("xmid");
		String where = "AND XM_ID="+"'"+xmid+"'";
		List<Bean> list = ServDao.finds("TS_BM_GROUP", where);
		String s = "";
		for (int i=0;i<list.size();i++) {
			if(list.size()==1 || i==list.size()-1){
				
				s+=list.get(i).getId();
			}else{
				s+=list.get(i).getId()+",";
			}
		}
		out.set("rycodes", s);
		return out;
	}
}
