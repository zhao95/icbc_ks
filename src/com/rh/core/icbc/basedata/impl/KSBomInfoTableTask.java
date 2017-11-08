package com.rh.core.icbc.basedata.impl;

import com.rh.core.base.Bean;
import com.rh.core.icbc.basedata.AbstractTableTask;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.Constant;

/**
 * SY_BOM_ZDPSTRUINFO
 * 
 * @author leader
 *
 */
public class KSBomInfoTableTask extends AbstractTableTask {

	private static final long serialVersionUID = 5809953607337190393L;

	public KSBomInfoTableTask(String smtime, boolean flag) {
		super(smtime, flag);
	}

	@Override
	protected String getSourceServId() {
		return "BOM_ZDPSTRUINFO";
	}

	@Override
	public String getTargetServId() {
		return "SY_BOM_ZDPSTRUINFO";
	}

	@Override
	protected String[] getPKCodeItems() {
		String[] pkCodeItems = new String[1];
		pkCodeItems[0] = "STRU_ID";
		return pkCodeItems;
	}

	@Override
	protected void parseOneData(Bean data){
		String value = data.getStr("STRU_STATE").trim();
		if (value.equals("3")) { //
			data.set("S_FLAG", Constant.NO_INT);
		} else {
			data.set("S_FLAG", Constant.YES_INT);
		}
	}
	
	@Override
	protected void setQueryWhere(ParamBean param) {
		
	}

}
