package com.rh.ts.xmgl;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServMgr;
import com.rh.ts.util.TsConstant;

public class XmglMgr {

	/**
	 * 判断项目挂接模块是否存在
	 * @param xmId 项目id
	 * @param szName 模块名称
	 * @return boolean
	 */
	@SuppressWarnings("deprecation")
	public static boolean existModule(String xmId, String szName) {

		Bean param = new Bean();
		param.set("XM_ID", xmId);
		param.set("XM_SZ_NAME", xmId);
		Bean outBean = ServMgr.act(TsConstant.SERV_XMGL_SZ, "existModule", param);

		int count = outBean.getInt("_OKCOUNT_");

		if (count > 0) {
			return true;
		}

		return false;

	}
	
	/**
	 * 是否审核  1自动审核, 2人工审核, 3自动+人工审核
	 * @param xmId 项目id
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static int existSh(String xmId) {

		Bean param = new Bean();
		param.set("XM_ID", xmId);
		Bean outBean = ServMgr.act(TsConstant.SERV_XMGL_SZ, "existSH", param);

		int count = outBean.getInt("_OKCOUNT_");

		return count;
	}

}
