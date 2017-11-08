package com.rh.core.icbc.basedata.impl;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.icbc.basedata.AbstractTableTask;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.Constant;
import com.rh.core.util.UserName2PinyinUtils;

public class KSHrmBinfoTableTask extends AbstractTableTask {
	//序列化ID Java自动生成
	private static final long serialVersionUID = 5591709898785203279L;


	public KSHrmBinfoTableTask(String smtime, boolean flag) {
		super(smtime, flag);
	}

	@Override
	protected String getSourceServId() {
		return "HRM_ZDSTAFFBINFO";
	}

	@Override
	public String getTargetServId() {
		return "SY_HRM_ZDSTAFFBINFO";
	}

	@Override
	protected String[] getPKCodeItems() {
		String[] pkCodeItems = new String[1];
		pkCodeItems[0] = "PERSON_ID";
		return pkCodeItems;
	}

	@Override
	protected void parseOneData(Bean data) {
		data.set("ENAME", UserName2PinyinUtils.toPinyin(data.getStr("NAME").trim()).toLowerCase());
		data.set("SNAME", UserName2PinyinUtils.toPinyinHead(data.getStr("NAME").trim()).toLowerCase());
		// 获取更新标志
		if (data.contains("UPDATE_FLAG")) {
			String updateFlag = data.getStr("UPDATE_FLAG");
			// 根据updateFlag将数据整理为删除和非删除
			if (updateFlag.equals("0") || updateFlag.equals("1")) {
				// 暂时不做处理
				data.set("S_FLAG", Constant.YES_INT);
			} else if (updateFlag.equals("2") || updateFlag.equals("F")) {
				data.set("S_FLAG", Constant.NO_INT);
			}else {
				throw new TipException("【数据更新标志未知】！data : " + data + ", updateFlag : " + updateFlag);
			}
		}
	}

	@Override
	protected void setQueryWhere(ParamBean param) {
		param.setOrder("UPDATE_FLAG");
		
	}

}
