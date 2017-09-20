package com.rh.ts.xmgl.rule.impl;

import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.rule.IRule;

/**
 * 禁止考试  禁考期限 <= datetime参数
 * 
 * @author
 *
 */
public class BanTest implements IRule {

	public boolean validate(Bean param) {

		// 报名者人力资源编码
		String user = param.getStr("BM_CODE");

		String jsonStr = param.getStr("MX_VALUE2");

		JSONObject obj;

		try {

			obj = new JSONObject(jsonStr);

			String endDate = obj.getString("val"); // 有效期时间
			
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

			SqlBean sql = new SqlBean();

			sql.and("JKGL_USER_CODE", user);// 人员编码

			sql.andGTE("JKGL_END_DATE", sf.format(endDate));// 禁考期限 >= datetime

			sql.and("S_FLAG", 1);

			int count = ServDao.count(TsConstant.SERV_JKGL, sql);

			if (count == 0) {
				return true;
			}
		} catch (JSONException e) {

			e.printStackTrace();
		}

		return false;
	}

}
