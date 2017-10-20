package com.rh.ts.pvlg;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Constant;

public class PvlgUtils {

	/**
	 * 
	 * 登陆人对应列表数据的权限
	 * 
	 * @param servId
	 */
	@SuppressWarnings("rawtypes")
	public static void setCtlgPvlgWhere(ParamBean param) {

		ParamBean paramBean = (ParamBean) param.getBean("paramBean");

		String ctlgModuleName = param.getStr("ctlgModuleName");

		String serviceName = param.getStr("serviceName");

		ServDefBean servDef = ServUtils.getServDef(serviceName);

		String tableView = servDef.getTableView();

		// tree的编码 机构编码
		Bean extParams = paramBean.getBean("extParams");
		// 用户权限 所有权限的机构编码
		Bean userPvlg = extParams.getBean("USER_PVLG");

		if (!userPvlg.isEmpty() && userPvlg != null) {

			try {

				int treeWhereSize = paramBean.getList("_treeWhere").size();

				if (treeWhereSize == 0) {

					JSONObject jsonObject = new JSONObject(userPvlg);
					String result = null;
					Iterator iterator = jsonObject.keys();
					String key;
					while (iterator.hasNext()) {
						key = (String) iterator.next();
						try {
							JSONObject object = (JSONObject) jsonObject.get(key);
							String object2 = (String) object.get("ROLE_DCODE");
							String[] object3 = object2.split(",");
							if (result != null) {
								for (int i = 0; i < object3.length; i++) {
									if (result.indexOf(object3[i]) < 0) {
										result += "," + object3[i];
									}
								}
							} else {
								result = object2;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					if (result != null) {
						// result 排序
						String[] roles = result.split(",");

						StringBuilder param_where = new StringBuilder();
						param_where.append(" AND  EXISTS ( ");
						param_where.append(" SELECT CTLG_CODE FROM TS_COMM_CATALOG  A ");
						param_where.append(" WHERE  A.CTLG_MODULE='" + ctlgModuleName + "' AND " + tableView
								+ ".CTLG_PCODE = A.CTLG_CODE_H ");
						param_where.append(" and INSTR (A.CTLG_PATH," + "'" + roles[0] + "') ");

						param_where.append(") ");
						paramBean.set(Constant.PARAM_WHERE, param_where.toString());
						System.out.println("param_where:" + param_where.toString());
					} else {
						// 无权限
						paramBean.set(Constant.PARAM_WHERE, " and 1=2 ");
					}
				}
			} catch (Exception e) {
				// 无权限
				paramBean.set(Constant.PARAM_WHERE, " and 1=2 ");
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static void setOrgPvlgWhere(ParamBean param) {

		ParamBean paramBean = (ParamBean) param.getBean("paramBean");

		String serviceName = param.getStr("serviceName");

		ServDefBean servDef = ServUtils.getServDef(serviceName);

		String tableView = servDef.getTableView();

		// tree的编码 机构编码
		Bean extParams = paramBean.getBean("extParams");
		// 用户权限 所有权限的机构编码
		Bean userPvlg = extParams.getBean("USER_PVLG");

		try {
			int treeWhereSize = paramBean.getList("_treeWhere").size();

			if (treeWhereSize == 0) {

				JSONObject jsonObject = new JSONObject(userPvlg);
				String result = null;
				Iterator iterator = jsonObject.keys();
				String key;
				while (iterator.hasNext()) {
					key = (String) iterator.next();
					try {
						JSONObject object = (JSONObject) jsonObject.get(key);
						String object2 = (String) object.get("ROLE_DCODE");
						String[] object3 = object2.split(",");
						if (result != null) {
							for (int i = 0; i < object3.length; i++) {
								if (result.indexOf(object3[i]) < 0) {
									result += "," + object3[i];
								}
							}
						} else {
							result = object2;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				if (result != null) {
					// result 排序
					String[] roles = result.split(",");

					StringBuilder param_where = new StringBuilder();
					param_where.append(" AND  EXISTS ( ");
					param_where.append(" SELECT DEPT_CODE FROM SY_ORG_DEPT  A ");

					param_where.append(" WHERE " + tableView + ".CTLG_PCODE = A.DEPT_CODE ");

					param_where.append(" and INSTR (A.CODE_PATH," + "'" + roles[0] + "') ");

					param_where.append(") ");
					paramBean.set(Constant.PARAM_WHERE, param_where.toString());
				} else {
					// 无权限
					paramBean.set(Constant.PARAM_WHERE, " and 1=2 ");
				}
			}
		} catch (Exception e) {
			// 无权限
			paramBean.set(Constant.PARAM_WHERE, " and 1=2 ");
		}
	}

}
