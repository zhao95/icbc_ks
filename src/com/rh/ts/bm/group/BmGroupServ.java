package com.rh.ts.bm.group;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ServDao;
import com.rh.core.util.Strings;
/**
 * 报名群组
 * @author root
 *
 */
public class BmGroupServ extends CommonServ {

	

/**
 * 获取用户所有报名群组编码 (逗号相隔)
 * 
 * @param userCode
 * @return
 */
public OutBean getBmGroupCodes(Bean paramBean) {
		UserBean userBean = Context.getUserBean();
		String userCode = userBean.getStr("USER_CODE");
		String groupCodes = "";
		Bean queryUser = new Bean().set("USER_DEPT_CODE", userCode).set("S_FLAG", 1);
		// 查询用户所有群组
		List<Bean> userList = ServDao.finds("TS_BM_GROUP_USER", queryUser);

		for (Bean user : userList) {
			// 用户群组id
			String groupCode = user.getStr("G_ID");

			if (Strings.isBlank(groupCodes)) {

				groupCodes = groupCode;
			} else {
				groupCodes += "," + groupCode;
			}
		}

		if (!Strings.isBlank(groupCodes)) {
			// 去掉重复群组
			groupCodes = Strings.removeSame(groupCodes);
		}

	return new OutBean().set("qzcodes", groupCodes);
}
}
