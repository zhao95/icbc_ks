package com.rh.core.icbc.basedata.impl;

import com.rh.core.base.Bean;
import com.rh.core.icbc.basedata.AbstractTableTask;
import com.rh.core.serv.ParamBean;

public class BomBankTask extends AbstractTableTask {

	
	private static final long serialVersionUID = -7315340251722169954L;
	
	private static final String BOM_ZDPSFBANKLBS = "BOM_ZDPSFBANKLBS";
	private static final String SY_BOM_ZDPSFBANKLBS = "SY_BOM_ZDPSFBANKLBS";

	public BomBankTask(String smtime, boolean flag) {
		super(smtime, flag);
	}

	@Override
	protected String getSourceServId() {
		return BOM_ZDPSFBANKLBS;
	}

	@Override
	public String getTargetServId() {
		return SY_BOM_ZDPSFBANKLBS;
	}

	@Override
	protected String[] getPKCodeItems() {
		String[] pkCodes = {"STRU_ID"};
		return  pkCodes;
	}

	@Override
	protected void parseOneData(Bean data) {
		
	}
	
	@Override
	protected void setQueryWhere(ParamBean param) {
		
	}

}
