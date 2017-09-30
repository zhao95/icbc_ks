package com.rh.ts.xmgl.rule.impl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.rule.IRule;

/**
 * 已获专业类、销售类中级及以上证书且有效  (终止有效期 >= 时间参数)
 * 
 * @author zjl
 *
 */
public class MiddleValidCertZyXs implements IRule {

	public boolean validate(Bean param) {

		// 报名者人力资源编码
		String user = param.getStr("BM_CODE");

		String jsonStr = param.getStr("MX_VALUE2");

		JSONArray obj;

		try {
			
			obj = new JSONArray(jsonStr);
			JSONObject jsonObject = obj.getJSONObject(0);
			String endDate = jsonObject.getString("val"); //有效期时间

			String zyCode = "023002"; // 专业类
			String xsCode = "023003"; // 销售类

			SqlBean sql = new SqlBean();

			sql.and("STU_PERSON_ID", user);// 人员编码

			sql.andGTE("END_DATE", endDate);// 终止有效期 >= endDate

			sql.andIn("STATION_TYPE", zyCode, xsCode);// 类别编号

			sql.andIn("CERT_GRADE_CODE", "2", "3");// 证书等级编号

			sql.and("QUALFY_STAT", 1);// 获证状态(1-正常;2-获取中;3-过期)

			sql.and("S_FLAG", 1);

			int count = ServDao.count(TsConstant.SERV_ETI_CERT_QUAL_V, sql);

			if (count > 0) {
				return true;
			}
		} catch (JSONException e) {

			e.printStackTrace();
		}

		return false;
	}
}
