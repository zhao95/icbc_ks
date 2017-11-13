package com.rh.ts.xmgl.rule.impl;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.rule.IRule;

public class EverHasCret implements IRule {

	@Override
	public boolean validate(Bean param) {
		
		String user = param.getStr("BM_CODE");

		// 报名序列编码
		String LB = param.getStr("BM_LB");

			SqlBean sql = new SqlBean();

			sql.and("STU_PERSON_ID", user);// 人员编码

			sql.and("STATION_TYPE", LB);// 序列编号

			sql.and("S_FLAG", 1);

			sql.and("QUALFY_STAT", 3);// 获证状态(1-正常;2-获取中;3-过期)
			
			int count = ServDao.count(TsConstant.SERV_ETI_CERT_QUAL_V, sql);

			if (count > 0) {
				return true;
			}

	
		return false;
	}
	
}
