package com.rh.ts.xmgl.rule.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.rule.IRule;

/**
 * 跨序列序列初级证书满N年且有效 (起始有效日期 <= N年前日期)
 * 
 * @author zjl
 *
 */
public class BaseValidCert2YearBkxl implements IRule {

	public boolean validate(Bean param) {
		UserBean userBean = Context.getUserBean();
		//跨序列 报考考试 时 先判断  本序列证书是否有效         专业类，销售类人员 持 中级 高级 本序列证书 时可报 运行类客服类的所有跨序列考试       运行类，客服类  最多报专业类，销售类的中级考试
		
		String BM_TYPE = param.getStr("BM_TYPE");//等级
		
		//获得  类别等级LBLEVEL 本人 类别等级user_code获取类别去数据库查          LBlevel 报考的考试类别等级param中
		/*
		 * if(LBLEVEL.equals(LBlevel)){
		 * 	相同级别 报名 本序列证书  本序列证书 需有效
		 * 	BM_TYPE
		 *	查找本序列 证书  根据user_code  找到本序列  
		 * 
		 * }else{
		 * if(LBLEVEL.equals("1")){
		 * 低类别 报高类别       
		 * 不能报考 高级         if(BM_TYPE.equals("3)){
		 * 	return false;
		 * }else{
		 * 
		 *验证     Integer.parseInt(BM_TYPE) +1  是否有效  Integer.parseInt(BM_TYPE) +2  验证两种证书 有一种有效即可
		 * }
		 * }else{
		 * 高级别  报 低级别        
		 *初级可以不用验证   本序列 
		 *if(BM_TYPE.equals("1")){
		 *
		 *}else{
		 *先验证高级是否有效  有效 可以报考     比如报中级    初中高  证书 一种证书有效即可 
		 *Integer.parseInt(BM_TYPE)-1;是否有效 Integer.parseInt(BM_TYPE)  验证两种 有一种有效即可
		 *}
		 */
		
		//本序列证书 验证必须  上一个等级的证书有效  或者下一个证书 有效    例：报中级考试时   初级或高级证书中级证书任一有效即可
		//初级证书不用验证 
		
		// 报名者人力资源编码
		String user = param.getStr("BM_CODE");
	
		// 报名序列编码
		String xl = param.getStr("BM_XL");

		// 报名结束时间

		String jsonStr = param.getStr("MX_VALUE2");

		String twoYearAgo = ""; // N年前日期yyyy-mm-dd

		JSONArray obj;

		try {
			
			obj = new JSONArray(jsonStr);
			
			JSONObject jsonObject = obj.getJSONObject(obj.length()-1);

			Calendar c = Calendar.getInstance();

			int val = jsonObject.getInt("val"); // 变量值

			if (val == 0) {
				val = 2;
			}
			int valfu = -val;
			String endtime = obj.getJSONObject(obj.length()-2).getString("val");
			
			String level =obj.getJSONObject(0).getString("code");
			
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

			Date endDate = format.parse(endtime);  //指定时间  不用 报名结束时间

			c.setTime(endDate);

			c.add(Calendar.YEAR, valfu);

			Date y = c.getTime();

			twoYearAgo = format.format(y);

			SqlBean sql = new SqlBean();

			sql.and("STU_PERSON_ID", user);// 人员编码

			sql.andLTE("BGN_DATE", twoYearAgo);// 起始有效日期 <= dateTime

			sql.and("STATION_NO", xl);// 序列编号

			sql.andGTE("END_DATE", endtime);
			
			sql.andGTE("CERT_GRADE_CODE", level);// 证书等级编号

			sql.and("S_FLAG", 1);

			int count = ServDao.count(TsConstant.SERV_ETI_CERT_QUAL_V, sql);

			if (count > 0) {
				return true;
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 跨序列证书  验证本序列
	 */
}
