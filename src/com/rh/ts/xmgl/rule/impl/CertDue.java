package com.rh.ts.xmgl.rule.impl;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.rule.IRule;

/**
 * 曾获报考考试类别证书但已过期
 * 
 * @author zjl
 *
 */
public class CertDue implements IRule {

	public boolean validate(Bean param) {

		// 报名者人力资源编码
		String user = param.getStr("BM_CODE");

		// 报名模块
		String mkCde = param.getStr("KSLBK_MKCODE");

		// 报名等级
		String lvCode = param.getStr("BM_TYPE");

		// String jsonStr = param.getStr("MX_VALUE2");

		SqlBean sql = new SqlBean();

		sql.and("STU_PERSON_ID", user);// 人员编码

		sql.and("CERT_MODULE_CODE", mkCde);// 证书模块编号

		sql.and("CERT_GRADE_CODE", lvCode);// 证书等级编号

		sql.and("QUALFY_STAT", 3);// 获证状态(1-正常;2-获取中;3-过期)

		sql.and("S_FLAG", 1);

		int count = ServDao.count(TsConstant.SERV_ETI_CERT_QUAL, sql);

		if (count > 0) {
			return true;
		}

		return false;
	}

}
