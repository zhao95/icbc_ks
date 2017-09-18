package com.rh.ts.xmgl.rule.impl;

import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.rule.IRule;

/**
 * 已获运行类、客服类高级证书且有效 (证书终止有效期 >= datetime时间参数)
 * 
 * @author zjl
 *
 */
public class HighValidCertYxKf implements IRule {

	public boolean validate(Bean param) {

		// 报名者人力资源编码
		String user = param.getStr("BM_CODE");

		String jsonStr = param.getStr("MX_VALUE2");

		JSONObject obj;
		
		try {
			
			obj = new JSONObject(jsonStr);
			
			String endDate = obj.getString("val"); //有效期时间
			
			String yxCode = "023004"; // 运行类
			String kfCode = "023005"; // 客服类

			SqlBean sql = new SqlBean();

			sql.and("STU_PERSON_ID", user);// 人员编码

			sql.andGTE("END_DATE", endDate);// 终止有效期 >= endDate

			sql.andIn("STATION_TYPE", yxCode, kfCode);// 类别编号

			sql.andIn("CERT_GRADE_CODE", "3");// 证书等级编号

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
