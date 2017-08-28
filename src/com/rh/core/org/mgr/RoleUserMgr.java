package com.rh.core.org.mgr;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.org.UserBean;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;

/**
 * 
 * @author yangjy
 *
 */
public class RoleUserMgr {
	private static Log log = LogFactory.getLog(RoleUserMgr.class);

	/** 转授权 **/
	private static int AUTH_STATE_ZHUAN = 1;

	/**
	 * 
	 * @param userCode
	 *            授权人ID
	 * @param deptCode
	 *            授权人部门
	 * @param roleCode
	 *            角色编码
	 * @return 取得转授权目标用户
	 */
	public static UserBean getAuthTargetUser(String srcSsicid, String deptCode, String roleCode) {
		SqlBean sql = new SqlBean();
		sql.and("ROLE_ID", roleCode);
		sql.and("SOURCE_USER_ID", srcSsicid);
		sql.and("BNCH_ID", deptCode);
		sql.and("STATE", Constant.YES);
		sql.andGTE("END_DATE", DateUtils.getDatetime("yyyyMMddHHmmss"));
		sql.selects("*");

		Bean bean = ServDao.find("SY_ORG_ROLE_ACCREDIT", sql);
		if (bean == null) {
			return null;
		}
		try {
			return UserMgr.getUser(bean.getStr("CURR_PERSON_ID"));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * 
	 * @param outBean
	 */
	public static void checkAccredit(OutBean outBean) {
		List<Bean> list = outBean.getDataList();
		if (list == null || list.size() == 0) {
			return;
		}

		Bean anyBean = list.get(0);
		if (!anyBean.contains("AUTH_STATE")) {
			throw new TipException("查询结果必须包含字段AUTH_STATE");
		}

		if (!anyBean.contains("USER_LOGIN_NAME")) {
			throw new TipException("查询结果必须包含字段USER_LOGIN_NAME");
		}
		if (!anyBean.contains("DEPT_CODE")) {
			throw new TipException("查询结果必须包含字段DEPT_CODE");
		}
		if (!anyBean.contains("ROLE_CODE")) {
			throw new TipException("查询结果必须包含字段ROLE_CODE");
		}
		if (!anyBean.contains("USER_NAME")) {
			throw new TipException("查询结果必须包含字段USER_NAME");
		}

		for (Bean bean : list) {
			int authState = bean.getInt("AUTH_STATE");
			if (authState == AUTH_STATE_ZHUAN) {
				UserBean ub = getAuthTargetUser(bean.getStr("USER_LOGIN_NAME"), bean.getStr("DEPT_CODE"),
						bean.getStr("ROLE_CODE"));
				if (ub != null) {
					bean.set("USER_CODE", ub.getCode());
					bean.set("USER_LOGIN_NAME", ub.getStr("USER_LOGIN_NAME"));
					StringBuilder name = new StringBuilder();
					name.append(bean.getStr("USER_NAME"));
					name.append(" (转授权给").append(ub.getStr("USER_NAME")).append(")");
					bean.set("USER_NAME", name.toString());
				}
			}

		}
	}
}
