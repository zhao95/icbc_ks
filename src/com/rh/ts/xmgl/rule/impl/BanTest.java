package com.rh.ts.xmgl.rule.impl;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.rule.IRule;

/**
 * 禁止考试
 * 
 * @author
 *
 */
public class BanTest implements IRule {

	public boolean validate(Bean param) {

		// 报名者人力资源编码
		String user = param.getStr("BM_CODE");

		// 报名结束时间
		String bmEnd = param.getStr("BM_ENDDATE");

		SqlBean sql = new SqlBean();

		sql.and("JKGL_RLZY", user);// 人员编码

		sql.andGTE("JKGL_END_DATE", bmEnd);// 禁考期限 >= 报名结束时间

		sql.and("S_FLAG", 1);

		int count = ServDao.count(TsConstant.SERV_JKGL, sql);

		if (count == 0) {
			return true;
		}

		return false;
	}

}
