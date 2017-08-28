package com.rh.ts.xmglsz;


import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ServDao;

//**
// * 
// * 
// *

public class XmglszServ extends CommonServ {

	public OutBean findByXmid(Bean paramBean){
		OutBean result = new OutBean();
		List<Bean>  list = ServDao.finds("TS_XMGL_SZ", paramBean);
		
		result.set("resList", list);
		return result;
		
	}
	public OutBean  findBmId(Bean paramBean){
		OutBean  out=new OutBean();
		String XM_SZ_ID = paramBean.getId();
		String  where=" and XM_SZ_ID='"+XM_SZ_ID+"'";
		List<Bean> listBmgl=ServDao.finds("TS_XMGL_BMGL", where);
		if(listBmgl.isEmpty()){
			//返回一个可以新建卡片的
		}else{
		      for(int i=0;i<listBmgl.size();i++){
			  String BMID=  listBmgl.get(i).getStr("BM_ID");
		      }
		}
		//ServDao.save(servId, dataBean)
		return null;
		
	}
	
	
}
