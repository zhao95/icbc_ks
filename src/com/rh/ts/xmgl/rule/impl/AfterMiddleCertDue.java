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
 * datetime及之后过期相同序列中级及以上证书 (终止有效期 >= datetime时间参数)
 * 
 * @author zjl
 *
 */
public class AfterMiddleCertDue implements IRule {

	public boolean validate(Bean param) {

		// 报名者人力资源编码
		String user = param.getStr("BM_CODE");

		// 报名序列编码
		String xl = param.getStr("BM_XL");

		String jsonStr = param.getStr("MX_VALUE2");

		JSONArray obj;

		try {

			obj = new JSONArray(jsonStr);
			JSONObject jsonObject = obj.getJSONObject(0);
			String val = jsonObject.getString("val"); // 变量值

			SqlBean sql = new SqlBean();

			sql.and("STU_PERSON_ID", user);// 人员编码

			sql.andGTE("END_DATE", val);// 终止有效期 >= val

			sql.and("STATION_NO", xl);// 序列编号

			sql.andIn("CERT_GRADE_CODE", "2","3");// 证书等级编号
			
			sql.and("QUALFY_STAT", 3);// 获证状态(1-正常;2-获取中;3-过期)

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
