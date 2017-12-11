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
 * datetime之前过期相同序列高级证书 (终止有效期 <= datetime时间参数)   
 *   now:不考虑时间值判断状态
 * @author zjl
 *
 */
public class BeforeHighCertDue implements IRule {

	public boolean validate(Bean param) {

		// 报名者人力资源编码
		String user = param.getStr("BM_CODE");

		// 报名序列编码
		String xl = param.getStr("BM_XL");

		String jsonStr = param.getStr("MX_VALUE2");

		JSONArray obj;

		try {
			SqlBean sql = new SqlBean();

			obj = new JSONArray(jsonStr);
			/*JSONObject jsonObject = obj.getJSONObject(1);

			String endDate = jsonObject.getString("val"); // 变量值
			
			String fuhao =obj.getJSONObject(0).getString("code");
			if(fuhao.equals("1")){
				sql.andGT("END_DATE", endDate);// 终止有效期 >= endDate
				
			}else if(fuhao.equals("2")){
				//小于
				sql.andLT("END_DATE", endDate);// 终止有效期 >= endDate
			}else if(fuhao.equals("3")){
				//大于等于
				sql.andGTE("END_DATE", endDate);// 终止有效期 >= endDate
			}else if(fuhao.equals("4")){
				//小于等于
				sql.andLTE("END_DATE", endDate);// 终止有效期 >= endDate
			}
			*/
			
			//等级level
			
			String fuhao2 = obj.getJSONObject(0).getString("code");
			String dengjicode = obj.getJSONObject(1).getString("code"); // 
			if(fuhao2.equals("1")){
				
				sql.andGT("CERT_GRADE_CODE", dengjicode);
			}else if(fuhao2.equals("2")){
				//小于
				sql.andLT("CERT_GRADE_CODE", dengjicode);
			}else if(fuhao2.equals("3")){
				//大于等于
				sql.andGTE("CERT_GRADE_CODE", dengjicode);
			}else if(fuhao2.equals("4")){
				//小于等于
				sql.andLTE("CERT_GRADE_CODE", dengjicode);// 证书等级编号
			}else{
				sql.and("CERT_GRADE_CODE", dengjicode);//等于
			}
			
			sql.and("STU_PERSON_ID", user);// 人员编码

			sql.and("STATION_NO", xl);// 序列编号

			
			sql.and("S_FLAG", 1);

			int count = ServDao.count(TsConstant.SERV_ETI_CERT_QUAL_V, sql);

			if (count > 0) {
				return true;
			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		// JSONArray jsonArg;
		// try {
		// jsonArg = new JSONArray(jsonStr);
		//
		// for (int i = 0; i < jsonArg.length(); i++) {
		// JSONObject obj = jsonArg.getJSONObject(i);
		// String var = obj.getString("var"); // 变量名
		// String val = obj.getString("val"); // 变量值
		// String type = obj.getString("type");// 值类型：date、int、str
		// String oper = obj.getString("oper");// 操作 '>', '<',
		// '=','!=','>=','<='
		// }
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }

		return false;
	}

}
