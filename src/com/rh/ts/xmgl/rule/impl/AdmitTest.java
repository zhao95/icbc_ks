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

		// 报名者人力资源编码
		String user = param.getStr("BM_CODE");

		String jsonStr = param.getStr("MX_VALUE2");

		JSONArray obj;

		try {

			obj = new JSONArray(jsonStr);
			JSONObject jsonObject = obj.getJSONObject(0);
			int grade = jsonObject.getInt("val");// 通过成绩
			//是否精确到模块 进行验证
			int  flag= jsonObject.getInt("code");//0否 1是
			// 序列
			String bmXl = param.getStr("BM_XL_CODE");
			//模块
			String bmMk = param.getStr("BM_MK_CODE");

			SqlBean sql = new SqlBean();

			if(flag==1){
				//精确到模块
				sql.and("AD_MK",bmMk);
			}
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

		return false;
	}

}
