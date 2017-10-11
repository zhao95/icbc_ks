package com.rh.ts.xmgl.rule.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.serv.ServDao;
import com.rh.ts.xmgl.rule.IRule;

public class YearLimit implements IRule {

	@Override
	public boolean validate(Bean param){
		
		UserBean userBean = Context.getUserBean();
		String id = userBean.getStr("_PK_");
		Bean find = ServDao.find("SY_ORG_USER",id);
		String str = find.getStr("USER_WORK_DATE");
		
		//判断此人入职时长
		
		long time2 = 0;
		
		Date date = new Date();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		try {
			
		Date parse = sdf.parse(str);
		
		 time2 = parse.getTime();
		 
		} catch (ParseException e) {
			
			e.printStackTrace();
			
		}
		
		long time = date.getTime();
		
		long gztime  = 0;
		
		//入职时长毫秒数
		
		long l = time-time2;
		
		//查询规则 规则定的时长
		
		String jsonStr = param.getStr("MX_VALUE2");
		
		JSONArray obj; 
		
		try {
			
			
			obj = new JSONArray(jsonStr);
			

			for(int i=0;i<obj.length();i++){
				
				String dates = obj.getJSONObject(i).getString("val");// 类别code
				if(i==0){
				if(!"".equals(dates)){
					Long parseInt = (long) Integer.parseInt(dates);
					gztime+= parseInt*365*24*60*60*1000;
				}
				}else if(i==1){
					if(!"".equals(dates)){
						Long parseInt = (long) Integer.parseInt(dates);
						gztime+= parseInt*30*24*60*60*1000;
					}
				}else{
					if(!"".equals(dates)){
						Long parseInt = (long) Integer.parseInt(dates);
						gztime+= parseInt*24*60*60*1000;
					}
				}
			}
			
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(l>gztime){
			return true;
		}else{
			return false;
		}
	}

}
