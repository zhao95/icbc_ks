package com.rh.core.icbc.basedata.impl;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.icbc.basedata.AbstractTableTask;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.Constant;

public class EtiCertInfo extends AbstractTableTask {

	private static final long serialVersionUID = 3313965585868296193L;

	public EtiCertInfo(String smtime, boolean flag) {
		super(smtime, flag);
	}

	private String ETI_CERTINFO = "ETI_CERTINFO";
	private String SY_ETI_CERTINFO = "TS_ETI_CERT_INFO";
	
	@Override
	protected String getSourceServId() {
		return ETI_CERTINFO;
	}

	@Override
	public String getTargetServId() {
		return SY_ETI_CERTINFO;
	}

	@Override
	protected String[] getPKCodeItems() {
		String[] pkCodeItems = new String[1];
		pkCodeItems[0] = "CERT_ID";
		return pkCodeItems;
	}

	@Override
	protected void parseOneData(Bean data) {
//		// 获取更新标志
		if (data.contains("UPD_TYPE")) {
			String updateFlag = data.getStr("UPD_TYPE");
			// 根据updateFlag将数据整理为删除和非删除
			if (updateFlag.equals("1") || updateFlag.equals("2")) {
				// 暂时不做处理
				data.set("S_FLAG", Constant.YES_INT);
			} else if (updateFlag.equals("3") || updateFlag.equals("F")) {
				data.set("S_FLAG", Constant.NO_INT);
			} else {
				throw new TipException("【数据更新标志未知】！data : " + data + ", updateFlag : " + updateFlag);
			}
		}
	}

	@Override
	protected void setQueryWhere(ParamBean param) {
		param.setOrder("UPD_TYPE");
	}

}
