package com.rh.core.comm.entity;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;

/**
 * 
 * @author yangjy
 *
 */
public class FinishedServ extends CommonServ{
	@Override
	protected void afterQuery(ParamBean paramBean, OutBean outBean) {
		super.afterQuery(paramBean, outBean);
		
		//返回的列表数据
		List<Bean> dataList = outBean.getDataList();
		
		//循环列表
		for (Bean data : dataList) {
			try {
				//转换经办人
				if (data.isNotEmpty("S_USER")) {
					data.set("S_USER", UserMgr.getUser(data.getStr("S_USER")).getName());
				}

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
		}
	}
}
