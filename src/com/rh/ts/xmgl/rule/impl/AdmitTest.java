package com.rh.ts.xmgl.rule.impl;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.rule.IRule;

/**
 * 准入测试规则
 * 
 * @author zjl
 *
 */
public class AdmitTest implements IRule {

	public boolean validate(Bean param) {

		String kslbk_id = param.getStr("ID");
		String ksqz_id = param.getStr("KSQZ_ID");
		//找到匹配的准入测试类别 进行匹配
		List<Bean> finds = ServDao.finds("TS_XMGL_BM_KSLB_ADMIT_GRADE", " AND KSLBK_ID='"+kslbk_id+"' AND KSQZ_ID='"+ksqz_id+"'");
		if(finds!=null&&finds.size()!=0){
			//设置了准入测试成绩
			String xl = "";
			String mk = "";
			String type = "";
			for(int i=0;i<finds.size();i++){
				if(i==0){
					xl+=finds.get(0).getStr("KSLB_XL");
					xl+=finds.get(0).getStr("KSLB_MK");
					xl+=finds.get(0).getStr("KSLB_TYPE");
				}else{
					xl+=finds.get(0).getStr("KSLB_XL")+",";
					xl+=finds.get(0).getStr("KSLB_MK")+",";
					xl+=finds.get(0).getStr("KSLB_TYPE")+",";
				}
			}
			
			//查找准入测试成绩
			//没有设置准入测试成绩
			// 报名者人力资源编码
			String user = param.getStr("BM_CODE");

			String jsonStr = param.getStr("MX_VALUE2");

			JSONArray obj;

			try {

				obj = new JSONArray(jsonStr);
				JSONObject jsonObject = obj.getJSONObject(0);
				int grade = jsonObject.getInt("val");// 通过成绩
				// 序列
				SqlBean sql = new SqlBean();

				sql.and("USER_CODE", user);

				sql.andIn("AD_XL", xl.split(","));
				sql.andIn("AD_MK", mk.split(","));
				sql.andIn("AD_TYPE", type.split(","));

				sql.desc("AD_TIME");

				List<Bean> list = ServDao.finds(TsConstant.SERV_BMSH_ADMIT, sql);

				if (list != null && list.size() > 0) {
					int adGrade=0;
					//取最大分数      (上个版本是取最近一次准入测试成绩)
					for (Bean bean : list) {
					int nowgrade = bean.getInt("AD_GRADE");
					if(nowgrade>adGrade){
						adGrade = nowgrade;
					}
					}
					if (adGrade >= grade) {
						return true;
					}
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}else{
			//没有设置准入测试成绩
			// 报名者人力资源编码
			String user = param.getStr("BM_CODE");

			String jsonStr = param.getStr("MX_VALUE2");

			JSONArray obj;

			try {

				obj = new JSONArray(jsonStr);
				JSONObject jsonObject = obj.getJSONObject(0);
				int grade = jsonObject.getInt("val");// 通过成绩
				// 序列
				String bmXl = param.getStr("BM_XL_CODE");

				SqlBean sql = new SqlBean();

				sql.and("USER_CODE", user);

				sql.and("AD_XL", bmXl);

				sql.desc("AD_TIME");

				List<Bean> list = ServDao.finds(TsConstant.SERV_BMSH_ADMIT, sql);

				if (list != null && list.size() > 0) {
					int adGrade=0;
					//取最大分数      (上个版本是取最近一次准入测试成绩)
					for (Bean bean : list) {
					int nowgrade = bean.getInt("AD_GRADE");
					if(nowgrade>adGrade){
						adGrade = nowgrade;
					}
					}
					if (adGrade >= grade) {
						return true;
					}
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
		return false;
	}

}
