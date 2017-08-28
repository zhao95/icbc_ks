package com.rh.ts.xmgl.rule.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Strings;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.rule.IRule;

/**
 * 投行用-已获对公、营销、信贷初级满2年
 * 
 * @author zjl
 *
 */
public class BaseCert2YearDgYxXd implements IRule {

	public boolean validate(Bean param) {

		// 报名者人力资源编码
		String user = param.getStr("BM_CODE");

		// 报名结束时间
		String bmEnd = param.getStr("BM_ENDDATE");

		String jsonStr = param.getStr("MX_VALUE2");

		String twoYearAgo = ""; // 2年前日期 yyyy-mm-dd

		JSONObject obj;

		try {

			obj = new JSONObject(jsonStr);

			Calendar c = Calendar.getInstance();

			String val = obj.getString("val"); // 变量值

			if (Strings.isBlank(val)) {
				val = "2";
			}

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

			Date endDate = format.parse(bmEnd);

			c.setTime(endDate);

			c.add(Calendar.YEAR, -2);

			Date y = c.getTime();

			twoYearAgo = format.format(y);
			
			SqlBean sql = new SqlBean();

			sql.andIn("KSLBK_XL", "对公客户经理", "营销","信贷");

			sql.andIn("KSLBK_TYPE", "low");// 证书等级编号
			
			sql.andNotNull("KSLBK_MKCODE");
			// 查询考试类别库 找到模块编号
			List<Bean> list = ServDao.finds(TsConstant.SERV_BM_KSLBK, sql);
			
			String mkCodes[] = {};

			for (int i = 0; i < list.size(); i++) {

				String mkCode = list.get(i).getStr("KSLBK_MKCODE");

				mkCodes[i] = mkCode;
			}
			

			sql = new SqlBean();

			sql.and("STU_PERSON_ID", user);// 人员编码

			sql.andLTE("BGN_DATE", twoYearAgo);// 起始有效日期 <= dateTime

			sql.andIn("CERT_MODULE_CODE", mkCodes);// 证书模块编号

			sql.and("CERT_GRADE_CODE", "low");// 证书等级编号
			
			sql.and("QUALFY_STAT", 1);// 获证状态(1-正常;2-获取中;3-过期)

			sql.and("S_FLAG", 1);

			int count = ServDao.count(TsConstant.SERV_ETI_CERT_QUAL, sql);

			if (count > 0) {
				return true;
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return false;
	}
}
