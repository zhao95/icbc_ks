package com.rh.ts.xmgl.rule.impl;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.DateUtils;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.rule.IRule;

/**
 * 第一期参考已达60分的考试类别
 * 
 * @author zjl
 *
 */
public class FirstExamScorePass implements IRule {

	public boolean validate(Bean param) {

		// 报名者人力资源编码
		String user = param.getStr("BM_CODE");

		// 模块编码
		String mkCde = param.getStr("KSLBK_MKCODE");

		// 等级编码
		String lvCode = param.getStr("KSLBK_TYPE");

		// String jsonStr = param.getStr("MX_VALUE2");

		SqlBean sql = new SqlBean();

		sql.and("STU_PERSON_ID", user);// 人员编码

		sql.and("CERT_MODULE_CODE", mkCde);// 证书模块编号

		sql.and("CERT_GRADE_CODE", lvCode);// 证书等级编号

		sql.andNot("QUALFY_STAT", 3);// 获证状态(1-正常;2-获取中;3-过期)

		int year = DateUtils.getYear();

		sql.andLTE("ISSUE_DATE", year + "-01-01");// 发证日期 >= 本年1月1日

		sql.and("S_FLAG", 1);
		// 本年是否获得证书
		int count = ServDao.count(TsConstant.SERV_ETI_CERT_QUAL, sql);
		// 没有获得证书 返回true
		if (count == 0) {
			return true;
		}

		return false;
	}

}
