package com.rh.ts.zhengshu;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;

public class EtiSerialNum extends CommonServ {
	/**
	 * 保存方法之前调用的方法， 实现流水号的添加，
	 */
	public OutBean uuid(ParamBean paramBean){
		OutBean outBean = new OutBean();
			String serialNum="rh"+getSerialNum();
			return outBean.set("serialNum", serialNum);
	}
	
//	public OutBean modify(ParamBean paramBean){
//			return super.save(paramBean);
//	}
	//抽取后生成流水号的方法
	private String getSerialNum(){
		Date currentime = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
		String sdftime = format.format(currentime);
		int num = (int) (Math.random()*900+100);
		return sdftime+num;  
	}
}
