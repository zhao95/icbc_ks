package com.rh.ts.xmgl.rule.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.ts.xmgl.rule.IRule;
/**
 * 
 * @author shiyun
 *管理类在报名考试时  证书验证规则 都不通过时  自动手动审核
 */
public class GlVerifying implements IRule {

	@Override
	public boolean validate(Bean param) {

		// 报名者人力资源编码
		String user = param.getStr("BM_CODE");
		
		//岗位序列code
		String station_no_code = "";
		
		Bean find = ServDao.find("SY_HRM_ZDSTAFFPOSITION", user);
		
		if(find!=null){
			
			station_no_code=find.getStr("STATION_NO_CODE");
			
			if("A000000000000000020".equals(station_no_code)){//才会资金序列 报名  考试无  资金  默认 匹配 财会
				station_no_code="A000000000000000019";
			}
			
		}
		
		String jsonStr = param.getStr("MX_VALUE2");

		JSONArray obj;
		
		List<String> codelist=new ArrayList<String>();
		try {
			obj = new JSONArray(jsonStr);
			
			for(int i=0;i<obj.length();i++){
				
				String yxCode = obj.getJSONObject(i).getString("code"); // 序列code
				codelist.add(yxCode);
			}
			
			if(codelist.contains(station_no_code)){
				return true;
			}
			
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return false;
	}

}
