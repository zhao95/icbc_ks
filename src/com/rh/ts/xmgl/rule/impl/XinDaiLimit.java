package com.rh.ts.xmgl.rule.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.ts.xmgl.rule.IRule;
/**
 * 信贷类从业经验达到多少年时可报考 考试
 * @author ShiYun
 *
 */
public class XinDaiLimit implements IRule {

	@Override
	public boolean validate(Bean param) {
		UserBean userBean = Context.getUserBean();
		String user_code = userBean.getStr("USER_CODE");
		SqlBean sql = new SqlBean();
		sql.and("STATION_NO_CODE","A000000000000000013");//信贷类
		sql.and("PERSON_ID", user_code);//报名人user_code
		
		// 报名结束时间
		String jsonStr = param.getStr("MX_VALUE2");
		
		JSONArray obj;
		try {
			obj = new JSONArray(jsonStr);
		
		
		int year = obj.getJSONObject(0).getInt("val");
		
		//当前时间减去年数
		
		Calendar c = Calendar.getInstance();

		int valfu = -year;
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date newdate  = new Date();
		c.setTime(newdate);

		c.add(Calendar.YEAR, valfu);

		Date y = c.getTime();

		String yearago = format.format(y);
			
		sql.andLTE("HOLD_TIME", yearago);// 终止有效期 >= endDate
		
		int count = ServDao.count("sy_hrm_zdstaffposition", sql);

		if (count > 0) {
			return true;
		}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
}
