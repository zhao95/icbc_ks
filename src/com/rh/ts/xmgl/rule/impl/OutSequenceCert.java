package com.rh.ts.xmgl.rule.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Strings;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.rule.IRule;

/**
 * 跨序列证书规则
 * 
 * @author zjl
 *
 */
public class OutSequenceCert implements IRule {

	public boolean validate(Bean param) {

		// 报名者人力资源编码
		String user = param.getStr("BM_CODE");

		// 报名序列
		String bmxl = param.getStr("BM_XL");

		// 报名等级
		String lvCode = param.getStr("BM_TYPE");

		// 报名结束时间
		String bmEnd = param.getStr("BM_ENDDATE");

		SqlBean sql = new SqlBean();

		sql.and("R_XL", bmxl);

		sql.and("R_LV", lvCode);

		sql.and("R_TYPE", 2);

		sql.and("S_FLAG", 1);
		// 岗位规则设置
		List<Bean> list = ServDao.finds(TsConstant.SERV_BMSH_RULE, sql);

		for (Bean rule : list) {

			String certCode = rule.getStr("R_CERT_CODE"); // 证书内码

			int certYear = rule.getInt("R_YEAR"); // 持证年限

			String certStatus = rule.get("R_CERT_STAT", "1"); // 证书状态

			if (!Strings.isBlank(certCode)) {

				for (String cert : certCode.split(",")) {

					sql = new SqlBean();

					sql.and("STU_PERSON_ID", user);// 人员编码

					sql.and("CERT_ID", cert);// 证书内码

					// sql.and("CERT_GRADE_CODE", ""); //证书等级
					//
					// sql.and("CERT_MODULE_CODE", "");//证书模块编码

					sql.andLTE("BGN_DATE", getYearAgo(bmEnd,certYear));// 起始有效日期 <= dateTime

					sql.andIn("QUALFY_STAT", certStatus.split(","));// 获证状态(1-正常;2-获取中;3-过期)

					sql.and("S_FLAG", 1);

					int count = ServDao.count(TsConstant.SERV_ETI_CERT_QUAL, sql);

					if (count > 0) {
						return true;
					}

				}
			}
		}
		return false;
	}

	private String getYearAgo(String time, int ago) {
		try {
			Calendar c = Calendar.getInstance();

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

			Date endDate = format.parse(time);

			c.setTime(endDate);

			c.add(Calendar.YEAR, ago);

			Date y = c.getTime();

			return format.format(y);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}

}
