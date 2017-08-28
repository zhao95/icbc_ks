package com.rh.core.icbc.basedata.impl;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.icbc.basedata.AbstractTableTask;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.Constant;

public class HrmCertTask extends AbstractTableTask {

	private static final long serialVersionUID = -289942640299285019L;

	private static String HRM_ZDSTAFFCERT = "HRM_ZDSTAFFCERT";
	private static String SY_HRM_ZDSTAFFCERT = "SY_HRM_ZDSTAFFCERT";

	public HrmCertTask(String smtime, boolean flag) {
		super(smtime, flag);
	}

	@Override
	protected String getSourceServId() {
		return HRM_ZDSTAFFCERT;
	}

	@Override
	public String getTargetServId() {
		return SY_HRM_ZDSTAFFCERT;
	}

	@Override
	protected String[] getPKCodeItems() {
		String[] pkCodeItem = { "PERSON_ID" };
		return pkCodeItem;
	}

	@Override
	protected void parseOneData(Bean data) {
		// 获取更新标志
		if (data.contains("UPDATE_FLAG")) {
			String updateFlag = data.getStr("UPDATE_FLAG");
			// 根据updateFlag将数据整理为删除和非删除
			data.set("S_FLAG", Constant.YES_INT);
			if (updateFlag.equals("0") || updateFlag.equals("1")) {
				// 暂时不做处理
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
