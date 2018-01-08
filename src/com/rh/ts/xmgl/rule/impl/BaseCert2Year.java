package com.rh.ts.xmgl.rule.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.rule.IRule;

public class BaseCert2Year implements IRule {

	public boolean validate(Bean param) {

		// 报名者人力资源编码
		String user = param.getStr("BM_CODE");

		// 报名结束时间
		String jsonStr = param.getStr("MX_VALUE2");


		JSONArray obj;

		try {

			obj = new JSONArray(jsonStr);
			String codes = "";
			for(int i=0;i<obj.length()-3;i++){
				if(i==0){
					codes+=obj.getJSONObject(i).getString("code");
				}else{
					codes+=","+obj.getJSONObject(i).getString("code");
				}
				
			}
			SqlBean sql = new SqlBean();
			if(!"".equals(codes)){
				String[] codearr = codes.split(",");
				sql.andIn("STATION_NO",codearr);// 证书序列编号
			}

			int endDate = obj.getJSONObject(obj.length()-1).getInt("val");
			//证书有效期满多少年
			//当前时间减去年数
			
			Calendar c = Calendar.getInstance();

			int valfu = -endDate;
			
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			Date newdate  = new Date();
			c.setTime(newdate);

			c.add(Calendar.YEAR, valfu);

			Date y = c.getTime();

			String yearago = format.format(y);
				
			sql.andLTE("BGN_DATE", yearago);// 有效期 开始时间< endDate
			
			String level =  obj.getJSONObject(obj.length()-2).getString("code");
			//证书等级判断符号
			String levelcode = obj.getJSONObject(obj.length()-3).getString("code");
			if(levelcode.equals("1")){
				sql.andGT("CERT_GRADE_CODE", level);
				
			}else if(levelcode.equals("2")){
				//小于
				sql.andLT("CERT_GRADE_CODE", level);
			}else if(levelcode.equals("3")){
				//大于等于
				sql.andGTE("CERT_GRADE_CODE", level);
			}else if(levelcode.equals("4")){
				//小于等于
				sql.andLTE("CERT_GRADE_CODE", level);
			}else{
				sql.and("CERT_GRADE_CODE", level);  //等于
			}
			sql.and("STU_PERSON_ID", user);// 人员编码

			sql.andGTE("CERT_GRADE_CODE", level);// 证书等级编号

			sql.and("QUALFY_STAT", 1);// 获证状态(1-正常;2-获取中;3-过期)

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

}
