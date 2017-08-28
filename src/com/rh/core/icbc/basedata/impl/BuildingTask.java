package com.rh.core.icbc.basedata.impl;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.icbc.basedata.AbstractTableTask;
import com.rh.core.icbc.imp.log.OrgLogMgr;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.Constant;

public class BuildingTask extends AbstractTableTask {

	private static final long serialVersionUID = 9005278422907418215L;

	private static final String SY_BUILDINGINFO = "SY_BUILDINGINFO";

	private static final String BUILDINGINFO = "BUILDINGINFO";


	public BuildingTask(String smtime, boolean flag) {
		super(smtime, flag);
	}	

	@Override
	protected String getSourceServId() {
		return BUILDINGINFO;
	}

	@Override
	public String getTargetServId() {
		return SY_BUILDINGINFO;
	}

	@Override
	protected String[] getPKCodeItems() {
		String[] pkcode = { "ASSETS_NO" };
		return pkcode;
	}

	@Override
	protected void parseOneData(Bean data) {
		// 获取更新标志
		String updateFlag = data.getStr("MOD_TYPE");
		// 根据updateFlag将数据整理为删除和非删除
		if (updateFlag.equals("01") || updateFlag.equals("02")) {
			// 暂时不做处理
			data.set("S_FLAG", Constant.YES_INT);
		} else if (updateFlag.equals("03")) {
			data.set("S_FLAG", Constant.NO_INT);
		} else {
			log.error("【数据更新标志未知】！data : " + data + ", MOD_TYPE : " + updateFlag);
			OrgLogMgr.orgLogSave("", "CC_DATA_IMPORT", "BUILIDNGINFO 数据更新标志未知! ",
					data.toString());
			throw new TipException(
					"【数据更新标志未知】！data : " + data + ", MOD_TYPE : " + updateFlag);
		}
	}
	
	@Override
	protected void setQueryWhere(ParamBean param) {
		
		param.setOrder("WORKDATE,MOD_TYPE"); // 保证更新的顺序是：新增，修改，删除
	}
}
