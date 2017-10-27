package com.rh.ts.xmgl.rule.impl;



import com.rh.core.base.Bean;
import com.rh.ts.xmgl.rule.IRule;

/**
 * 禁止考试  禁考期限 <= datetime参数
 * 
 * @author
 *
 */
public class BanTest implements IRule {

	public boolean validate(Bean param) {

		/*// 报名者人力资源编码
		String user = param.getStr("BM_CODE");

		String jsonStr = param.getStr("MX_VALUE2");

		JSONArray obj;

		try {

			obj = new JSONArray(jsonStr);
			JSONObject jsonObject = obj.getJSONObject(0);
			String endDate = jsonObject.getString("val"); // 有效期时间
			
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
			
			Date date  = sf.parse(endDate);

			SqlBean sql = new SqlBean();

			sql.and("JKGL_USER_CODE", user);// 人员编码

			sf = new SimpleDateFormat("yyyy-MM-dd");
			
			sql.andGTE("JKGL_END_DATE", sf.format(date));// 禁考期限 >= datetime

			sql.and("S_FLAG", 1);

			int count = ServDao.count(TsConstant.SERV_JKGL, sql);

			if (count == 0) {
				return true;
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
*/
		return true;
	}

}
