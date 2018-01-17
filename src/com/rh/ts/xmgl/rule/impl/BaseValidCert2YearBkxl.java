package com.rh.ts.xmgl.rule.impl;

import java.util.List;

import org.json.JSONArray;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
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
		
		String BM_TYPE = param.getStr("BM_TYPE");//等级
		
		String xl = param.getStr("BM_XL");//考试序列非
		
		String lblevel = param.getStr("KSLBK_TYPE_LEVEL");//考试类别层级
		
		if("".equals(lblevel)){
			return false;
		}
		
		int lblevelin = Integer.parseInt(lblevel);
		
		String user_code = param.getStr("BM_CODE");
		
		//获取本人职务层级编码
		
		Bean zwbean = ServDao.find("SY_HRM_ZDSTAFFPOSITION", user_code);
		
		String user_xl = zwbean.getStr("STATION_NO_CODE");
		if("A000000000000000020".equals(user_xl)){//才会资金序列 报名  考试无  资金  默认 匹配 财会
			user_xl="A000000000000000019";
		}
		
		String user_lb = zwbean.getStr("STATION_TYPE_CODE");
		
		String sql = "select * from ts_xmgl_bm_kslbk where kslbk_code ='"+user_lb+"' and kslbk_xl_code is null";
		
		List<Bean> kslbklist = Transaction.getExecutor().query(sql);
		
		String user_lb_level = "";
		if(kslbklist!=null&&kslbklist.size()!=0){
			user_lb_level = kslbklist.get(0).getStr("KSLBK_TYPE_LEVEL");
		}
		if("".equals(user_lb_level)){
			return false;
		}
		int user_lb_level_in = Integer.parseInt(user_lb_level);
		
		if(xl.equals(user_xl)){
			
			//相同序列 不用验证此规则
			
			return true;
			
		}else{
			//跨序列 报考考试 时 先判断  本序列证书是否有效         专业类，销售类人员 持 中级 高级 本序列证书 时可报 运行类客服类的所有跨序列考试       运行类，客服类  最多报专业类，销售类的中级考试
			//1. 相同级别报名本序列证书 本序列证书 需有效
			 if(user_lb_level_in==lblevelin){
				 SqlBean sqllb = new SqlBean();

				 sqllb.and("STU_PERSON_ID", user_code);// 人员编码

				 sqllb.and("STATION_NO", user_xl);// 序列编号

				 sqllb.andGTE("CERT_GRADE_CODE", BM_TYPE);// 证书等级编号

				 sqllb.and("S_FLAG", 1);
				 
				 sqllb.and("QUALFY_STAT", 1);// 获证状态(1-正常;2-获取中;3-过期)

					int count = ServDao.count(TsConstant.SERV_ETI_CERT_QUAL_V, sqllb);

					if (count < 0) {
						return false;
					}
			 }else{
				 //等级不相同
				 if(user_lb_level_in==1){
					 //低级别报高级别   不能报高级
					 if(BM_TYPE.equals("3")){
						return false;
				 }else{
					// 验证Integer.parseInt(BM_TYPE) +1  是否有效  Integer.parseInt(BM_TYPE) +2  验证两种证书 有一种有效即可
					 SqlBean sqllb = new SqlBean();

					 sqllb.and("STU_PERSON_ID", user_code);// 人员编码

					 sqllb.and("STATION_NO", user_xl);// 序列编号

					 sqllb.andGT("CERT_GRADE_CODE", BM_TYPE);// 证书等级编号

					 sqllb.and("S_FLAG", 1);
					 
					 sqllb.and("QUALFY_STAT", 1);// 获证状态(1-正常;2-获取中;3-过期)

						int count = ServDao.count(TsConstant.SERV_ETI_CERT_QUAL_V, sqllb);

						if (count < 0) {
							return false;
						}
				 }
				 }else{
					 //高级报低级
					 if(BM_TYPE.equals("1")){
						 //初级可以不用验证   本序列 
						 
						}else{
						 //先验证高级是否有效  有效 可以报考     比如报中级    初中高  证书 一种证书有效即可 
							// 验证Integer.parseInt(BM_TYPE) +1  是否有效  Integer.parseInt(BM_TYPE) +2  验证两种证书 有一种有效即可
							 SqlBean sqllb = new SqlBean();

							 sqllb.and("STU_PERSON_ID", user_code);// 人员编码

							 sqllb.and("STATION_NO", user_xl);// 序列编号

							 sqllb.andGTE("CERT_GRADE_CODE", Integer.parseInt(BM_TYPE)-1);// 证书等级编号

							 sqllb.and("S_FLAG", 1);
							 
							 sqllb.and("QUALFY_STAT", 1);// 获证状态(1-正常;2-获取中;3-过期)

								int count = ServDao.count(TsConstant.SERV_ETI_CERT_QUAL_V, sqllb);

								if (count < 0) {
									return false;
								}
						 }
				 }
			 }
		}
		
		// 报名者人力资源编码
		String user = param.getStr("BM_CODE");
	
		// 报名序列编码

		// 报名结束时间

		String jsonStr = param.getStr("MX_VALUE2");

		/*String twoYearAgo = ""; // N年前日期yyyy-mm-dd
*/
		JSONArray obj;

		try {
			
			obj = new JSONArray(jsonStr);
			/*
			JSONObject jsonObject = obj.getJSONObject(obj.length()-1);

			Calendar c = Calendar.getInstance();

			int val = jsonObject.getInt("val"); // 变量值

			if (val == 0) {
				val = 2;
			}
			int valfu = -val;*/
			/*String endtime = obj.getJSONObject(obj.length()-2).getString("val");*/
			
			/*SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

			Date endDate = format.parse(endtime);  //指定时间  不用 报名结束时间
*/
			/*c.setTime(endDate);
*/
			/*c.add(Calendar.YEAR, valfu);

			Date y = c.getTime();

			twoYearAgo = format.format(y);*/

			SqlBean bmsql = new SqlBean();
			
			String fuhao2 = obj.getJSONObject(0).getString("code");
			String dengjicode = obj.getJSONObject(1).getString("code"); // 
			if(fuhao2.equals("1")){
				
				bmsql.andGT("CERT_GRADE_CODE", dengjicode);
			}else if(fuhao2.equals("2")){
				//小于
				bmsql.andLT("CERT_GRADE_CODE", dengjicode);
			}else if(fuhao2.equals("3")){
				//大于等于
				bmsql.andGTE("CERT_GRADE_CODE", dengjicode);
			}else if(fuhao2.equals("4")){
				//小于等于
				bmsql.andLTE("CERT_GRADE_CODE", dengjicode);// 证书等级编号
			}else{
				bmsql.and("CERT_GRADE_CODE", dengjicode);//等于
			}

			bmsql.and("STU_PERSON_ID", user);// 人员编码

			/*bmsql.andLTE("BGN_DATE", twoYearAgo);// 起始有效日期 <= dateTime
*/
			bmsql.and("STATION_NO", xl);// 序列编号

			bmsql.and("S_FLAG", 1);
			
			bmsql.and("QUALFY_STAT", 1);// 获证状态(1-正常;2-获取中;3-过期)
			
			int count = ServDao.count(TsConstant.SERV_ETI_CERT_QUAL_V, bmsql);

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
