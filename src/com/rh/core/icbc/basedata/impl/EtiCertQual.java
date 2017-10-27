package com.rh.core.icbc.basedata.impl;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.icbc.basedata.AbstractTableTask;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.Constant;

public class EtiCertQual extends AbstractTableTask{

	public EtiCertQual(String smtime, boolean flag) {
		super(smtime, flag);
	}

	private static final long serialVersionUID = 7751402011576220614L;
	private String ETI_CERTQUAL = "ETI_CERTQUAL";
	private String SY_ETI_CERTQUAL = "TS_ETI_CERTQUAL";
	@Override
	protected String getSourceServId() {
		return ETI_CERTQUAL;
	}

	@Override
	public String getTargetServId() {
		return SY_ETI_CERTQUAL;
	}

	@Override
	protected String[] getPKCodeItems() {
		String[] pkItem = {"STU_PERSON_ID", "CERT_ID","CERT_GRADE_CODE","CERT_MODULE_CODE"};
		return pkItem;
	}

	@Override
	protected void parseOneData(Bean data) {
		// 获取更新标志
		if (data.contains("UPD_TYPE")) {
			String updateFlag = data.getStr("UPD_TYPE");
			// 根据updateFlag将数据整理为删除和非删除
			if (updateFlag.equals("1") || updateFlag.equals("2")) {
				// 暂时不做处理
				data.set("S_FLAG", Constant.YES_INT);
			} else if (updateFlag.equals("3")) {
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