package com.rh.ts.xmgl.rule.impl;

import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.rule.IRule;

/**
 * 20161231及之后过期相同序列中级及以上证书
 * 
 * @author zjl
 *
 */
public class AfterMiddleCertDue implements IRule {

	public boolean validate(Bean param) {

		// 报名者人力资源编码
		String user = param.getStr("BM_CODE");

		// 报名模块编码
		String mkCde = param.getStr("KSLBK_MKCODE");

		String jsonStr = param.getStr("MX_VALUE2");

		JSONObject obj;

		try {

			obj = new JSONObject(jsonStr);

			String val = obj.getString("val"); // 变量值

			SqlBean sql = new SqlBean();

			sql.and("STU_PERSON_ID", user);// 人员编码

			sql.andGTE("END_DATE", val);// 终止有效期 > val

			sql.and("CERT_MODULE_CODE", mkCde);// 证书模块编号

			sql.andIn("CERT_GRADE_CODE", "2","3");// 证书等级编号
			
			sql.and("QUALFY_STAT", 3);// 获证状态(1-正常;2-获取中;3-过期)

			sql.and("S_FLAG", 1);

			int count = ServDao.count(TsConstant.SERV_ETI_CERT_QUAL, sql);

			if (count > 0) {
				return true;
			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return false;
	}

}
