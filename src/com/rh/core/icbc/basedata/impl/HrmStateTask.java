package com.rh.core.icbc.basedata.impl;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.icbc.basedata.AbstractTableTask;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.Constant;

public class HrmStateTask extends AbstractTableTask {

	private static final long serialVersionUID = -4242592420451676426L;
	
	private String HRM_ZDSTAFFSTATE = "HRM_ZDSTAFFSTATE";
	private String SY_HRM_ZDSTAFFSTATE = "SY_HRM_ZDSTAFFSTATE";

	public HrmStateTask(String smtime, boolean flag) {
		super(smtime, flag);
	}

	@Override
	protected String getSourceServId() {
		return HRM_ZDSTAFFSTATE;
	}

	@Override
	public String getTargetServId() {
		return SY_HRM_ZDSTAFFSTATE;
	}

	@Override
	protected String[] getPKCodeItems() {
		String[] pkItem = {"PERSON_ID"};
		return pkItem;
	}

	@Override
	protected void parseOneData(Bean data) {
		// 获取更新标志
		if (data.contains("UPDATE_FLAG")) {
			String updateFlag = data.getStr("UPDATE_FLAG");
			// 根据updateFlag将数据整理为删除和非删除
			if (updateFlag.equals("0") || updateFlag.equals("1")) {
				// 暂时不做处理
				data.set("S_FLAG", Constant.YES_INT);
			} else if (updateFlag.equals("2")) {
				data.set("S_FLAG", Constant.NO_INT);
			} else {
				throw new TipException("【数据更新标志未知】！data : " + data + ", updateFlag : " + updateFlag);
			}
		}

	}

	@Override
	protected void setQueryWhere(ParamBean param) {

		param.setOrder("UPDATE_FLAG");

	}

}
