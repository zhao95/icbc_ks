package com.rh.ts.util;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;

public class ObjUtil {
	
	/**
	 * 通过用户 编码/工号 获取融e联手机号
	 * @param userCode
	 * @return
	 */
	public static String getUsersEphone(String userCode) {
		String ephone = "";
		SqlBean sqlBean = new SqlBean();
		sqlBean.and("SERV_ID", TsConstant.SERV_BM);
		sqlBean.and("TYPE", "E_PHOME");
		sqlBean.and("STR1", userCode);
		List<Bean> list = ServDao.finds(TsConstant.SERV_OBJECT, sqlBean);
		if (list != null && list.size() > 0) {
			ephone = list.get(0).getStr("STR2");
		}
		return ephone;
	}

}
