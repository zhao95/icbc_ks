package com.rh.ts.xmgl.rule.impl;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.rule.IRule;

/**
 * 已获专业类、销售类中级及以上证书且有效
 * 
 * @author zjl
 *
 */
public class MiddleValidCertZyXs implements IRule {

	public boolean validate(Bean param) {

		// 报名者人力资源编码
		String user = param.getStr("BM_CODE");

		String[] mkCodes = {};

		SqlBean sql = new SqlBean();

		sql.andIn("KSLBK_NAME", "专业类", "销售类");

		sql.andIn("KSLBK_TYPE", "inter", "high");
		//考试类别库找到编号
		List<Bean> list = ServDao.finds(TsConstant.SERV_BM_KSLBK, sql);

		for (int i = 0; i < list.size(); i++) {

			String mkCode = list.get(i).getStr("KSLBK_MKCODE");

			mkCodes[i] = mkCode;
		}

		sql = new SqlBean();

		sql.and("STU_PERSON_ID", user);// 人员编码

		sql.andIn("CERT_MODULE_CODE", mkCodes);// 证书模块编号

		sql.andIn("CERT_GRADE_CODE", "inter", "high");// 证书等级编号

		sql.andNot("QUALFY_STAT", 3);// 获证状态(1-正常;2-获取中;3-过期)

		sql.and("S_FLAG", 1);

		int count = ServDao.count(TsConstant.SERV_ETI_CERT_QUAL, sql);

		if (count > 0) {
			return true;
		}

		return false;
	}
}
