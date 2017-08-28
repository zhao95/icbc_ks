package com.rh.ts.xmgl.rule.impl;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Strings;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.rule.IRule;

/**
 * 岗位类规则
 * 
 * @author zjl
 *
 */
public class StaffPost implements IRule {

	public boolean validate(Bean param) {

		// 报名者人力资源编码
		String user = param.getStr("BM_CODE");

		UserBean userBean = UserMgr.getUserByWorkNum(user);

		if (userBean == null) {
			return false;
		}

		// 报名序列
		String bmxl = param.getStr("BM_XL");

		// 报名等级
		String lvCode = param.getStr("BM_TYPE");

		SqlBean sql = new SqlBean();

		sql.and("R_XL", bmxl);

		sql.and("R_LV", lvCode);

		sql.and("R_TYPE", 1);

		sql.and("S_FLAG", 1);
		// 岗位规则设置
		List<Bean> list = ServDao.finds(TsConstant.SERV_BMSH_RULE, sql);

		for (Bean rule : list) {

			String postCode = rule.getStr("R_POST_CODE");

			if (!Strings.isBlank(postCode)) {
				
				for (String post : postCode.split(",")) {
					
					if (userBean.getPost().equals(post)) {
						
						return true;
					}
				}
			}
		}
		return false;
	}

}
