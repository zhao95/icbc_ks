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
		if("".equals(str)){
			return false;
		}
		//判断此人入职时长
		
		long time2 = 0;
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		try {
			
		Date parse = sdf.parse(str);
		
		 time2 = parse.getTime();
		 
		} catch (ParseException e) {
			
			e.printStackTrace();
			
		}
		
		
		String jsonStr = param.getStr("MX_VALUE2");
		long gztime = 0;
		JSONArray obj; 
		
		try {
			
			
			obj = new JSONArray(jsonStr);
			

			for(int i=0;i<obj.length();i++){
				
				String dates = obj.getJSONObject(i).getString("val");// 类别code
				
				try {
					if(dates.length()<=4){
						dates+="0101";
					}
					gztime	=sdf.parse(dates).getTime();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(time2<gztime){
			return true;
		}else{
			return false;
		}
	}

}
