package com.rh.ts.job;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.comm.ConfMgr;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.util.scheduler.RhJob;
import com.rh.core.util.scheduler.RhJobContext;
import com.rh.ts.comm.CtlgSyncUtils;
import com.rh.ts.util.TsConstant;

public class InitCtlgJob extends RhJob {

	@Override
	protected void executeJob(RhJobContext context) {

		// 获取所有目录类型
		Bean dictBean = DictMgr.getDict(TsConstant.DICT_CTLG_MOD);
		List<Bean> modList = dictBean.getList("SY_SERV_DICT_ITEM");

		String modCode = "";
		for (Bean dict : modList) {
			if (StringUtils.isBlank(modCode)) {
				modCode = dict.getStr("ITEM_CODE");
			} else {
				modCode += "," + dict.getStr("ITEM_CODE");
			}
		}
		CtlgSyncUtils.sync(modCode, "", ConfMgr.getConf("TS_COMM_CATALOG_INIT_CMPYCODE", "icbc"));

	}

	@Override
	public void interrupt() {
		// TODO Auto-generated method stub

	}

}
