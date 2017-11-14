package com.rh.ts.xmgl.rule.impl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.rule.IRule;

/**
 * 已获运行类、客服类高级证书且有效 (证书终止有效期 >= datetime时间参数)
 * 
 * @author zjl
 *
 */
public class HighValidCertYxKf implements IRule {

	public boolean validate(Bean param) {

		// 报名者人力资源编码
		String user = param.getStr("BM_CODE");

		String jsonStr = param.getStr("MX_VALUE2");

		JSONArray obj;
		
		try {
			obj = new JSONArray(jsonStr);
			
			JSONObject jsonObject = obj.getJSONObject(obj.length()-1);
			
			String endDate = jsonObject.getString("val"); //有效期时间
			
			SqlBean sql = new SqlBean();
			
			String codes = "";
			for(int i=0;i<obj.length()-4;i++){
				if(i==0){
					codes+=obj.getJSONObject(i).getString("code");
				}else{
					codes+=","+obj.getJSONObject(i).getString("code");
				}
				
			}
			sql.andIn("STATION_TYPE", codes.split(","));// 类别编号
			sql.and("STU_PERSON_ID", user);// 人员编码
			//符号变量  1 大于 2 小于  3 = 4大于等于  5小于等于 
			String fuhao =obj.getJSONObject(obj.length()-2).getString("code");
			if(fuhao.equals("1")){
				sql.andLTE("END_DATE", endDate);// 终止有效期 >= endDate
			}else if(fuhao.equals("2")){
				//小于
				sql.andLT("END_DATE", endDate);// 终止有效期 >= endDate
			}else if(fuhao.equals("3")){
				//大于等于
				sql.andGTE("END_DATE", endDate);// 终止有效期 >= endDate
			}else if(fuhao.equals("4")){
				//小于等于
				sql.andGT("END_DATE", endDate);// 终止有效期 >= endDate
			}
			
			String fuhao2 =obj.getJSONObject(obj.length()-4).getString("code");
			String dengjicode = obj.getJSONObject(obj.length()-3).getString("code"); // 类别code
			if(fuhao2.equals("1")){
				sql.andLTE("CERT_GRADE_CODE", dengjicode);// 证书等级编号
			}else if(fuhao2.equals("2")){
				//小于
				sql.andLT("CERT_GRADE_CODE", dengjicode);
			}else if(fuhao2.equals("3")){
				//大于等于
				sql.andGTE("CERT_GRADE_CODE", dengjicode);
			}else if(fuhao2.equals("4")){
				//小于等于
				sql.andGT("CERT_GRADE_CODE", dengjicode);
			}else{
				sql.and("CERT_GRADE_CODE", dengjicode);//等于
			}

			sql.and("QUALFY_STAT", 1);// 获证状态(1-正常;2-获取中;3-过期)

			sql.and("S_FLAG", 1);

			int count = ServDao.count(TsConstant.SERV_ETI_CERT_QUAL_V, sql);

			if (count > 0) {
				return true;
			}
		} catch (JSONException e) {

			e.printStackTrace();
		}

		return false;
	}

}
