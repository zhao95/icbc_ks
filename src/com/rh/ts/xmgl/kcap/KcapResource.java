package com.rh.ts.xmgl.kcap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.base.db.Transaction;
import com.rh.core.comm.ConfMgr;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.util.OrgConstant;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;
import com.rh.core.util.Strings;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.kcap.utils.KcapUtils;

public class KcapResource {

	private static Log log = LogFactory.getLog(KcapResource.class);

	private String currentXmId = "";

	/**
	 * 特定机构考生
	 */
	private Bean spOrgKsBean = null;

	/**
	 * 领导职务考生
	 */
	private Bean leaderBean = null;

	/**
	 * 考场距离远的机构
	 */
	private Bean farKsBean = null;

	/**
	 * 未安排座位 最少考场
	 */
	private Bean freeKcZwBean = null;

	/**
	 * 未安排座位 最少场次
	 */
	private Bean freeCcZwBean = null;

	/**
	 * 已安排座位
	 */
	private Bean busyZwBean = null;

	/**
	 * 考生分配优先规则 0 最少考场 1 最少场次
	 */
	private int ksPriority = 0;

	/**
	 * 不符合规则考试 是否强制安排
	 */
	private int ksConstrain = 0;

	/**
	 * 引入规则
	 */
	private Bean ruleBean = null;

	/**
	 * 借考考生 key:考生机构 value:list
	 */
	private Bean jkKsBean = null;

	/**
	 * 考场 key:机构 value:bean
	 */
	private Bean kcBean = null;

	/**
	 * 考场关联机构
	 */
	private Bean kcOrgBean = null;

	/**
	 * 无请假考生 key:考生机构 value:list
	 */
	private Bean ksBean = null;

	/**
	 * 网点考生
	 */
	private Bean branchBean = null;

	private Bean ccAMPM = null;

	public KcapResource(String xmId) {

		this.currentXmId = xmId;

		// 加载规则
		loadRule(xmId);
		// 考场信息
		loadKc(xmId, "");

		// 清空自动安排的考生
		cleanBusyZw(xmId);

		// 考生信息
		loadKs(xmId, "");

		// 考场座位安排
		loadZw(xmId, "");

		// 加载其他资源
		loadOther(xmId, "");

		// showResLog();
	}

	public KcapResource(String xmId, String odept) {

		this.currentXmId = xmId;

		// 加载规则
		loadRule(xmId);

		// 考场信息
		loadKc(xmId, odept);

		// 清空自动安排的考生
		cleanBusyZw(xmId);

		// 考生信息
		loadKs(xmId, odept);

		// 考场座位安排
		loadZw(xmId, odept);

		// 加载其他资源
		loadOther(xmId, odept);

		// showResLog();
	}

	/**
	 * 加载没有安排座位的考生
	 *
	 * @param xmId
	 * @param odepts
	 */
	private void loadKs(String xmId, String odept) {

		List<Bean> jkKsList = getKsList(xmId, odept, 2);

		for (Bean jkBean : jkKsList) {

			jkBean.set("S_ODEPT", jkBean.getStr("JK_ODEPT"));
		}

		jkKsBean = ksList2Bean(jkKsList, "S_ODEPT", "BM_KS_TIME", "BM_CODE");

		// 考试
		// List<Bean> ksList = getKsList(xmId, odept, 0);

		List<Bean> ksList = getKsListNew(xmId, odept, 0);

		if (jkKsList != null) {

			ksList.addAll(jkKsList);
		}

		ksBean = ksList2Bean(ksList, "S_ODEPT", "BM_KS_TIME", "BM_CODE");
	}

	/**
	 * 加载考场及场次信息
	 *
	 * @param xmId
	 * @param odepts
	 */
	private void loadKc(String xmId, String odept) {

		try {

			List<Object> values = new ArrayList<Object>();

			values.add(xmId);

			String sql = "select k.KC_ID,k.KC_NAME,k.KC_CODE,K.KC_ODEPTCODE,K.KC_ODEPTNAME,k.KC_SCORE,k.KC_STATE,k.KC_MAX,k.KC_GOOD,k.KC_LEVEL,c.CC_ID,c.XM_ID "
					+ "from TS_XMGL_KCAP_DAPCC c LEFT JOIN ts_kcgl k on k.kc_id = c.kc_id where c.XM_ID=? ";

			if (!Strings.isBlank(odept)) {

				UserBean user = Context.getUserBean();
				String cmpyCode = user.getCmpyCode();
				String codePath = OrgMgr.getOdept(odept).getCodePath();

				String sql1 = " select DEPT_CODE FROM SY_ORG_DEPT where ";

				sql1 += " SY_ORG_DEPT.CODE_PATH like CONCAT('" + codePath + "','%') ";

				// sql1 += " AND SY_ORG_DEPT.DEPT_TYPE = 2";

				sql1 += " AND SY_ORG_DEPT.CMPY_CODE =  '" + cmpyCode + "'";

				sql1 += " AND SY_ORG_DEPT.S_FLAG = 1";

				sql += " and k.KC_ODEPTCODE in (" + sql1 + ")";
			}

			// 所有考场
			List<Bean> kcList = Transaction.getExecutor().query(sql, values);

			kcList2Bean(kcList);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new TipException(e.getMessage());
		}

	}

	/**
	 * 加载未安排考场座位
	 */
	private void loadZw(String xmId, String odept) {

		try {

			loadBusyZw(xmId, odept);

			if (ksPriority == 1) { // 最少场次

				fitCcZwBean();

			} else { // 最少考场

				fitKcZwBean();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new TipException(e.getMessage());
		}
	}

	/**
	 * 获取已安排座位
	 *
	 * @param xmId
	 * @param odepts
	 *
	 */
	private void loadBusyZw(String xmId, String odept) {

		try {

			busyZwBean = new Bean();

			SqlBean sql = new SqlBean().and("XM_ID", xmId);

			List<Bean> busyZwList = ServDao.finds(TsConstant.SERV_KCAP_YAPZW, sql);

			for (Bean zw : busyZwList) {

				String kcId = zw.getStr("KC_ID"); // 考场
				String sjCc = zw.getStr("SJ_CC"); // 场次
				String date = zw.getStr("SJ_DATE").substring(0, 10);// 考试日期
				String key = kcId + "^" + sjCc + "^" + date;

				String zwh = zw.getStr("ZW_XT"); // 座位号

				Bean zwBean = null;

				if (busyZwBean.containsKey(key)) {

					zwBean = busyZwBean.getBean(key);

					zwBean.set(zwh, zw);

				} else {

					zwBean = new Bean();

					zwBean.set(zwh, zw);
				}

				busyZwBean.set(key, zwBean);
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new TipException(e.getMessage());
		}
	}

	/**
	 * 装载考场安排规则
	 *
	 * @param xmId
	 * @throws Exception
	 */

	private void loadRule(String xmId) throws TipException {

		try {

			ruleBean = new Bean();

			UserBean user = Context.getUserBean();

			SqlBean sql = new SqlBean().and("XM_ID", xmId).and("S_USER", user.getCode()).and("S_FLAG", 1);

			sql.asc("GZ_VER_SORT");

			List<Bean> ruleList = ServDao.finds(TsConstant.SERV_KCAP_GZ, sql);

			for (Bean rule : ruleList) {

				String ruleCode = rule.getStr("GZ_CODE");

				ruleBean.set(ruleCode, rule);

				if (KcapRuleEnum.S001.getCode().equals(ruleCode)) { // 最少考场，最少场次

					String val = rule.getStr("GZ_VALUE2");

					if ("1".equals(val)) { // 0最少考场，1最少场次

						ksPriority = 1;
					}

				} else if (KcapRuleEnum.S002.getCode().equals(ruleCode)) { // 无符合规则考生
					// 是否强制安排
					ksConstrain = 1; // 1 强制安排
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new TipException(e.getMessage());
		}
	}

	private void loadOther(String xmId, String odept) {

		try {

			for (Object ruleKey : ruleBean.keySet()) {

				Bean rule = ruleBean.getBean(ruleKey);

				String ruleCode = rule.getStr("GZ_CODE");

				if (KcapRuleEnum.R003.getCode().equals(ruleCode)) { // 距离远近规则

					fitFarKsBean();

				} else if (KcapRuleEnum.R007.getCode().equals(ruleCode)) { // 领导职务考生座位靠前安排

					fitLeaderBean(rule);

				} else if (KcapRuleEnum.R008.getCode().equals(ruleCode)) { // 特定机构考生场次先后安排

					fitSpOrgKsBean(rule);

				} else if (KcapRuleEnum.R004.getCode().equals(ruleCode)) { // 同一网点级机构考生均分安排

					loadBranch(xmId, odept);
				}
			}
		} catch (

				Exception e) {
			log.error(e.getMessage(), e);
			throw new TipException(e.getMessage());
		}
	}

	/**
	 * 装载领导 R007
	 *
	 * @param rule
	 */
	private void fitLeaderBean(Bean rule) {

		leaderBean = new Bean();

		String leaderCodes = ConfMgr.getConf("TS_KCAP_LEADER_LB", "023001");// 默认管理类视为领导职务

		for (Object odept : ksBean.keySet()) { // 遍历考生机构

			Bean dateBean = ksBean.getBean(odept);

			for (Object date : dateBean.keySet()) { // 遍历考试时长

				Bean fksBean = dateBean.getBean(date);

				for (Object key : fksBean.keySet()) { // 遍历所有考生

					Object obj = fksBean.get(key);

					if (obj instanceof List) {

						List<Bean> kslist = fksBean.getList(key);

						for (Bean bean : kslist) {

							for (String lbCode : leaderCodes.split(",")) {

								if (lbCode.equals(bean.getStr("STATION_TYPE_CODE"))) {

									String shId = bean.getStr("SH_ID");

									String userCode = bean.getStr("BM_CODE");

									if (leaderBean.containsKey(userCode)) { // 考生已存在

										String shIds = leaderBean.getStr(userCode);

										shIds += "," + shId;

										leaderBean.set(userCode, shIds);

									} else {

										leaderBean.set(userCode, shId);
									}

									break;
								}
							}
						}
					} else if (obj instanceof Bean) {

						Bean ks = fksBean.getBean(key);

						for (String lbCode : leaderCodes.split(",")) {

							if (lbCode.equals(ks.getStr("STATION_TYPE_CODE"))) {

								String shId = ks.getStr("SH_ID");

								String userCode = ks.getStr("BM_CODE");

								if (leaderBean.containsKey(userCode)) { // 考生已存在

									String shIds = leaderBean.getStr(userCode);

									shIds += "," + shId;

									leaderBean.set(userCode, shIds);

								} else {

									leaderBean.set(userCode, shId);
								}

								break;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 装载特定机构考生 R008
	 *
	 * @param rule
	 */
	private void fitSpOrgKsBean(Bean rule) {

		spOrgKsBean = new Bean();

		String jsonStr = rule.getStr("GZ_VALUE2");

		try {

			String[] odeptArg = null;

			if (!Strings.isBlank(jsonStr)) {

				JSONObject obj = new JSONObject(jsonStr);

				String odepts = obj.getString("values"); // 特定机构,多个逗号隔开

				if (!Strings.isBlank(odepts)) {

					odeptArg = new String[odepts.split(",").length];

					odeptArg = odepts.split(",");
				}
			}

			if (odeptArg != null && odeptArg.length > 0) {

				for (String dCode : odeptArg) {

					String codePath = OrgMgr.getDept(dCode).getCodePath();

					SqlBean sql = new SqlBean();

					sql.andLikeRT("CODE_PATH", codePath);

					sql.and("DEPT_TYPE", OrgConstant.DEPT_TYPE_ORG);

					sql.and("S_FLAG", 1);

					List<Bean> odeptList = ServDao.finds(ServMgr.SY_ORG_DEPT, sql);

					for (Bean odeptBean : odeptList) {

						String spOdept = odeptBean.getStr("DEPT_CODE");

						Bean orgKsBean = ksBean.getBean(spOdept);

						for (Object time : orgKsBean.keySet()) { // 遍历考试时长

							Bean timeBean = orgKsBean.getBean(time);

							for (Object user : timeBean.keySet()) { // 遍历所有考生

								Object uobj = timeBean.get(user);

								if (uobj instanceof Bean) { // 当前考生报考一个考试

									Bean ks = timeBean.getBean(user);

									String ksOdept = ks.getStr("S_ODEPT");

									if (spOdept.equals(ksOdept)) {

										spOrgKsBean.set(user, user);
									}

								} else if (uobj instanceof List) { // 当前考生报考多个考试

									List<Bean> kslist = timeBean.getList(user);

									for (Bean ks : kslist) {

										String ksOdept = ks.getStr("S_ODEPT");

										if (spOdept.equals(ksOdept)) {

											spOrgKsBean.set(user, user);

											break;
										}
									}
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 同一网点考生资源
	 *
	 * @param xmId
	 * @param odept
	 */
	private void loadBranch(String xmId, String odept) {

		List<Object> kcvars = new ArrayList<Object>();

		String kcIds = "";

		for (Object kcid : kcOrgBean.keySet()) {

			if (Strings.isBlank(kcIds)) {

				// kcIds += "'" + kcid + "'";

				kcIds += "?";

				kcvars.add(kcid);

			} else {

				// kcIds += ",'" + kcid + "'";
				kcIds += ",?";

				kcvars.add(kcid);
			}

		}

		List<Object> vars = new ArrayList<Object>();

		// 考场关联机构sql
		StringBuffer gljgSql = new StringBuffer(" SELECT '' FROM ");
		gljgSql.append(TsConstant.SERV_KCAP_GLJG);
		gljgSql.append(" g WHERE g.KC_ID IN (").append(kcIds).append(") ");
		gljgSql.append(" AND ((INSTR(a.CODE_PATH, g.JG_CODE) > 0");
		gljgSql.append(" AND g.JG_TYPE = 2 )");
		gljgSql.append(" or (");
		gljgSql.append("  a.ODEPT_CODE = g.JG_CODE ");
		gljgSql.append(" AND g.JG_TYPE = 1 ))");

		String baseSel = " SELECT a.ODEPT_CODE,b.STRU_ID,u.SH_ID,u.BM_CODE FROM ";
		String baseTab = " sy_org_dept a, ts_bmsh_pass u , sy_bom_zdpstruinfo b ";
		StringBuffer baseWhere = new StringBuffer();
		baseWhere.append(" WHERE u.s_dept = b.stru_id AND b.node_type IS NOT NULL AND b.node_type != '' ");
		baseWhere.append(" AND u.XM_ID =? ");
		baseWhere.append(" AND u.BM_STATUS = 0 ");

		// // 网点下下级部门考生
		// StringBuffer sql1 = new StringBuffer();
		// sql1.append(baseSel);
		// sql1.append(baseTab);
		// sql1.append(", sy_org_dept c ");
		// sql1.append(baseWhere.toString());
		// vars.add(xmId);
		// sql1.append(" AND a.dept_pcode = b.stru_id AND c.dept_pcode =
		// a.DEPT_CODE ");
		// sql1.append(" AND EXISTS (").append(gljgSql.toString()).append(")");
		// // 关联机构
		// vars.addAll(kcvars);
		//
		// // 网点下级部门考生
		// StringBuffer sql2 = new StringBuffer();
		// sql2.append(baseSel);
		// sql2.append(baseTab);
		// sql2.append(baseWhere);
		// vars.add(xmId);
		// sql2.append(" AND a.dept_pcode = b.stru_id ");
		// sql2.append(" AND EXISTS (").append(gljgSql.toString()).append(")");
		// // 关联机构
		// vars.addAll(kcvars);

		// 网点考生
		StringBuffer sql3 = new StringBuffer();
		sql3.append(baseSel);
		sql3.append(baseTab);
		sql3.append(baseWhere.toString());
		vars.add(xmId);
		sql3.append(" AND a.dept_code = b.stru_id  ");
		sql3.append(" AND EXISTS (").append(gljgSql.toString()).append(")"); // 关联机构
		vars.addAll(kcvars);

		// String sql = sql1.toString() + " UNION " + sql2.toString() + " UNION
		// " + sql3.toString();

		// 所有考场
		List<Bean> branchKsList = Transaction.getExecutor().query(sql3.toString(), vars);

		fitBranchBean(branchKsList);

	}

	/**
	 * 装载网点考生bean
	 *
	 * @param branchKsList
	 */
	private void fitBranchBean(List<Bean> branchKsList) {

		branchBean = new Bean();

		for (Bean branch : branchKsList) {

			String odeptCode = branch.getStr("ODEPT_CODE");
			String branchCode = branch.getStr("STRU_ID");
			String ksCode = branch.getStr("BM_CODE");
			String shId = branch.getStr("SH_ID");

			Bean branchKs = branchBean.getBean(odeptCode); // 机构下所有网点bean

			Bean ksBean = branchKs.getBean(branchCode); // 网点下考生bean

			List<String> shlist = ksBean.getList(ksCode); // 考生报名list

			shlist.add(shId);

			ksBean.set(ksCode, shlist);

			branchKs.set(branchCode, ksBean);

			branchBean.set(odeptCode, branchKs);
		}
	}

	/**
	 * 组装最少考场 未安排座位
	 *
	 */
	@SuppressWarnings("deprecation")
	private void fitKcZwBean() {

		freeKcZwBean = new Bean();

		ccAMPM = new Bean();

		for (Object key : kcBean.keySet()) { // 所有机构下的考场bean

			Bean zwhBeanVal = new Bean();

			List<Bean> kcinfoList = kcBean.getList(key); // 某所属机构下的考场list

			for (Bean kcInfo : kcinfoList) { // 遍历考场list

				Bean info = kcInfo.getBean("INFO");

				String kcId = info.getStr("KC_ID"); // 考场id

				String kcLv = info.getStr("KC_LEVEL"); // 考场层级

				String xmId = info.getStr("XM_ID"); // 项目id

				String kcName = info.getStr("KC_NAME");

				List<Bean> zwhList = kcInfo.getList("ZWH");

				List<Bean> ccList = kcInfo.getList("CC");

				zwhBeanVal = new Bean();

				for (Bean cc : ccList) {// 遍历场次(SJ_ID,CC_ID,SJ_CC,SJ_START,SJ_END)

					String sjCc = cc.getStr("SJ_CC"); // 场次号

					String date = cc.getStr("SJ_START").substring(0, 10); // 考试日期

					Bean zwBean = getZwBean(zwhList, kcId, sjCc, date);

					zwBean.set("SJ_ID", cc.getStr("SJ_ID"));

					zwBean.set("SJ_CC", sjCc);

					zwBean.set("SJ_DATE", date);

					zwBean.set("CC_ID", cc.getStr("CC_ID"));

					zwBean.set("KC_ID", kcId);

					zwBean.set("KC_LV", kcLv); // 考场层级 一级考场 二级考场

					zwBean.set("XM_ID", xmId);

					zwBean.set("SJ_START", cc.getStr("SJ_START"));

					String ampm = "AM";

					Date sTime = DateUtils.getDateFromString(cc.getStr("SJ_START"), "yyyy-MM-dd HH:mm");

					if (sTime.getHours() > 12) {
						ampm = "PM";
					}

					ccAMPM.set(kcId + "^" + sjCc + "^" + date, ampm);

					zwBean.set("SJ_END", cc.getStr("SJ_END"));

					long difftime = DateUtils.getDiffTime(cc.getStr("SJ_START"), cc.getStr("SJ_END"),
							"yyyy-MM-dd HH:mm");

					int minute = (int) Math.ceil(difftime / 60000);

					zwBean.set("SJ_TIME", minute);

					zwBean.set("KC_NAME", kcName);

					Bean dayBean = new Bean();

					dayBean.set(date, zwBean); // {key日期：val座位Bean}

					zwhBeanVal.set(sjCc, dayBean);
				}

				freeKcZwBean.set(kcId, zwhBeanVal);
			}
		}
	}

	/**
	 * 组装最少场次 未安排座位
	 *
	 */
	private void fitCcZwBean() {

		freeCcZwBean = new Bean();

		for (Object key : kcBean.keySet()) { // 所有机构下的考场bean

			List<Bean> kcinfoList = kcBean.getList(key); // 某所属机构下的考场list

			for (Bean kcInfo : kcinfoList) {

				Bean info = kcInfo.getBean("INFO");

				String kcId = info.getStr("KC_ID"); // 考场id

				String kcLv = info.getStr("KC_LEVEL"); // 考场层级

				String xmId = info.getStr("XM_ID"); // 项目id

				String kcName = info.getStr("KC_NAME");

				List<Bean> zwhList = kcInfo.getList("ZWH"); // 座位号

				List<Bean> ccList = kcInfo.getList("CC"); // 场次

				for (Bean cc : ccList) {// SJ_ID,CC_ID,SJ_CC,SJ_START,SJ_END

					String sjCc = cc.getStr("SJ_CC"); // 场次号

					String date = cc.getStr("SJ_START").substring(0, 10); // 考试日期

					Bean zwBean = getZwBean(zwhList, kcId, sjCc, date);

					zwBean.set("SJ_ID", cc.getStr("SJ_ID"));

					zwBean.set("SJ_CC", sjCc);

					zwBean.set("SJ_DATE", date);

					zwBean.set("CC_ID", cc.getStr("CC_ID"));

					zwBean.set("KC_ID", kcId);

					zwBean.set("KC_LV", kcLv); // 考场层级 一级考场 二级考场

					zwBean.set("XM_ID", xmId);

					zwBean.set("SJ_START", cc.getStr("SJ_START"));

					zwBean.set("SJ_END", cc.getStr("SJ_END"));

					long difftime = DateUtils.getDiffTime(cc.getStr("SJ_START"), cc.getStr("SJ_END"),
							"yyyy-MM-dd HH:mm");

					int minute = (int) Math.ceil(difftime / 60000);

					zwBean.set("SJ_TIME", minute);

					zwBean.set("KC_NAME", kcName);

					Bean zwhBeanVal = new Bean();

					zwhBeanVal.set(kcId, zwBean);

					Bean dateBean = new Bean();// {key日期：val座位Bean}

					if (freeCcZwBean.containsKey(sjCc)) {

						dateBean = (Bean) freeCcZwBean.getBean(sjCc).clone();

						if (dateBean.containsKey(date)) {

							dateBean.getBean(date).putAll(zwhBeanVal);

						} else {

							dateBean.set(date, zwhBeanVal);
						}
					} else {

						dateBean.set(date, zwhBeanVal);
					}
					freeCcZwBean.set(sjCc, dateBean);
				}
			}
		}
	}

	/**
	 * 考生list转bean,key考生机构
	 */
	private Bean ksList2Bean(List<Bean> ksList, String... keys) {

		Bean aksBean = new Bean();

		if (ksList == null || ksList.isEmpty() || keys == null || keys.length < 3) {
			return aksBean;
		}

		aksBean = commList2Bean(ksList, keys[0]); // "S_ODEPT"

		Bean cloneAskBean = (Bean) aksBean.clone();

		for (Object odept : cloneAskBean.keySet()) {

			boolean ishas = false;

			for (Object kc : kcOrgBean.keySet()) {

				if (kcOrgBean.getBean(kc).containsKey(odept)) {
					ishas = true;
				}
			}

			if (!ishas) {
				aksBean.remove(odept);
			}
		}

		Bean temp = new Bean();

		temp.putAll(aksBean);

		for (Object key : temp.keySet()) {

			Object tobj = temp.get(key);

			Bean timeBean = new Bean();

			if (tobj instanceof Bean) {

				timeBean.putAll(timeBean);

			} else if (tobj instanceof List) {
				List<Bean> list = temp.getList(key);

				timeBean = commList2Bean(list, keys[1]);// "BM_KS_TIME"

				// log.error("BM_KS_TIME----------S_ODEPT=" + key.toString() +
				// ": " + aksBean.toString());
			}

			Bean temp1 = new Bean();

			temp1.putAll(timeBean);

			for (Object key1 : temp1.keySet()) {

				Object obj = temp1.get(key1);

				if (obj instanceof Bean) {

					timeBean.remove(key1).put(key1, temp1.getBean(key1));// "BM_CODE"

				} else if (obj instanceof List) {

					List<Bean> list1 = temp1.getList(key1);

					timeBean.remove(key1).put(key1, commList2Bean(list1, keys[2]));// "BM_CODE"
				}
			}

			aksBean.remove(key).put(key, timeBean);
		}

		return aksBean;
	}

	/**
	 * 考场资源 list转Bean {所属机构id：考场list<Bean>
	 * {INFO:考场信息Bean,GLJG:关联机构Bean,ZWH:座位号list,CC:场次list} }
	 */
	private void kcList2Bean(List<Bean> kcList) {

		kcBean = new Bean();

		kcOrgBean = new Bean();

		for (Bean item : kcList) {

			String ccId = item.getStr("CC_ID");

			String kcId = item.getStr("KC_ID");

			// String kcName = item.getStr("KC_NAME");

			String kcOdept = item.getStr("KC_ODEPTCODE");

			// 场次
			SqlBean ccsql = new SqlBean().and("CC_ID", ccId).and("S_FLAG", 1);
			ccsql.selects("SJ_ID,CC_ID,SJ_CC,SJ_START,SJ_END").asc("SJ_CC");
			List<Bean> ccList = ServDao.finds(TsConstant.SERV_KCAP_CCSJ, ccsql);

			// 关联机构
			SqlBean jgsql = new SqlBean().and("KC_ID", kcId).and("S_FLAG", 1)
					.selects("KC_ID,JG_CODE,JG_NAME,JG_FAR,JG_TYPE");
			List<Bean> jgList = ServDao.finds(TsConstant.SERV_KCAP_GLJG, jgsql);

			Bean gljg = new Bean();

			for (Bean jg : jgList) { // 遍历机构下的机构

				Bean temp = new Bean(jg);

				String dCode = jg.getStr("JG_CODE");

				String dName = jg.getStr("JG_NAME");

				String jgFar = jg.getStr("JG_FAR");

				if (jg.getInt("JG_TYPE") == OrgConstant.DEPT_TYPE_DEPT) { // 关联部门

					DeptBean odeptBean = OrgMgr.getOdept(dCode);// 获取机构

					dCode = odeptBean.getCode();

					dName = odeptBean.getName();

					temp.set("JG_CODE", dCode);

					temp.set("JG_NAME", dName);

					temp.set("JG_FAR", jgFar);

					gljg.set(dCode, temp.clone());

				} else {

					gljg.set(dCode, temp.clone());

					UserBean user = Context.getUserBean();

					String cmpyCode = user.getCmpyCode();

					String codePath = OrgMgr.getDept(dCode).getCodePath();

					SqlBean sql = new SqlBean();
					sql.andLikeRT("CODE_PATH", codePath);
					sql.and("DEPT_TYPE", OrgConstant.DEPT_TYPE_ORG);
					sql.and("CMPY_CODE", cmpyCode);
					sql.and("S_FLAG", 1);

					List<Bean> odeptList = ServDao.finds(ServMgr.SY_ORG_DEPT, sql);

					for (Bean odeptBean : odeptList) {

						String childCode = odeptBean.getStr("DEPT_CODE");

						String childName = odeptBean.getStr("DEPT_NAME");

						Bean childTemp = new Bean();

						childTemp.set("JG_CODE", childCode);

						childTemp.set("JG_NAME", childName);

						childTemp.set("JG_FAR", jgFar);

						gljg.set(childCode, childTemp);
					}
				}
			}

			kcOrgBean.set(kcId, gljg);

			// 座位号
			SqlBean zwsql = new SqlBean().and("KC_ID", kcId).and("S_FLAG", 1).and("ZW_KY", 1);
			zwsql.selects("ZW_ID,ZW_ZWH_XT,ZW_ZWH_SJ,ZW_KY");
			List<Bean> zwList = ServDao.finds(TsConstant.SERV_KCGL_ZWDYB, zwsql);

			Bean kcInfo = new Bean();
			kcInfo.set("INFO", item);
			kcInfo.set("GLJG", gljg);
			kcInfo.set("ZWH", zwList);
			kcInfo.set("CC", ccList);

			List<Bean> kcinfoList = null;

			if (kcBean.containsKey(kcOdept)) {

				kcinfoList = kcBean.getList(kcOdept);
			} else {
				kcinfoList = new ArrayList<Bean>();
			}

			kcinfoList.add(kcInfo);

			kcBean.set(kcOdept, kcinfoList);
		}

	}

	/**
	 * 获取未安排座位
	 *
	 * @param zwhList
	 *            考场座位list
	 * @param kcId
	 *            考场id
	 * @param sjCc
	 *            场次
	 * @param date
	 *            考试日期
	 * @return Bean{座位号:座位bean}
	 */
	private Bean getZwBean(List<Bean> zwhList, String kcId, String sjCc, String date) {

		Bean zwBean = new Bean(); // 考场座位bean {系统座位号：zwbean}

		String key = kcId + "^" + sjCc + "^" + date;

		for (Bean zw : zwhList) {

			String zwh = zw.getStr("ZW_ZWH_XT"); // 座位号

			if (!busyZwBean.getBean(key).containsKey(zwh)) { // 判断座位是否已安排,只取未安排座位

				zwBean.set(zwh, zw);// ZW_ID,ZW_ZWH_XT,ZW_ZWH_SJ,ZW_KY
			}
		}

		return zwBean;
	}

	/**
	 * 组装考场远近 farKsBean
	 */
	private void fitFarKsBean() {

		farKsBean = new Bean();

		for (Object key : kcBean.keySet()) { // 所有机构下的考场bean

			List<Bean> kcinfoList = kcBean.getList(key); // 某所属机构下的考场list

			for (Bean kcInfo : kcinfoList) {

				String kcId = kcInfo.getBean("INFO").getStr("KC_ID");

				// String kcName = kcInfo.getBean("INFO").getStr("KC_NAME");

				Bean jgBean = kcInfo.getBean("GLJG"); // 考场的关联机构Bean

				Bean farKs = new Bean();

				for (Object jgkey : jgBean.keySet()) {

					Bean jg = jgBean.getBean(jgkey);

					if (jg.getInt("JG_FAR") == 1) { // 关联考场距离远的机构

						// log.error("-----" + kcName + "|" +
						// jg.getStr("JG_NAME") + " JG_FAR:" +
						// jg.getInt("JG_FAR"));

						String dCode = jg.getStr("JG_CODE");

						if (jg.getInt("JG_TYPE") == OrgConstant.DEPT_TYPE_DEPT) { // 关联部门

							dCode = OrgMgr.getOdept(dCode).getODeptCode(); // 获取机构

							Bean farUcodes = getFarKsCode(ksBean.getBean(dCode));

							if (!farUcodes.isEmpty()) {

								// log.error("-----------code:" +
								// farUcodes.size());

								farKs.putAll(farUcodes);
							}

						} else {

							String codePath = OrgMgr.getDept(dCode).getCodePath();

							SqlBean sql = new SqlBean();

							sql.andLikeRT("CODE_PATH", codePath);

							sql.and("DEPT_TYPE", OrgConstant.DEPT_TYPE_ORG);

							sql.and("S_FLAG", 1);

							List<Bean> odeptList = ServDao.finds(ServMgr.SY_ORG_DEPT, sql);

							for (Bean odeptBean : odeptList) {

								String oDeptCode = odeptBean.getStr("DEPT_CODE");

								Bean childFarUcodes = getFarKsCode(ksBean.getBean(oDeptCode));

								if (!childFarUcodes.isEmpty()) {

									// log.error("-----------code:" +
									// childFarUcodes.size());

									farKs.putAll(childFarUcodes);
								}
							}
						}
					}
				}

				if (!farKs.isEmpty()) {

					farKsBean.set(kcId, farKs);
				}
			}
		}
	}

	private static Bean getFarKsCode(Bean ksBean) {

		Bean user = new Bean();

		for (Object time : ksBean.keySet()) {

			for (Object ucode : ksBean.getBean(time).keySet()) {

				user.set(ucode, ucode);
			}
		}

		return user;
	}

	/**
	 * list 转 Bean
	 *
	 * @param list
	 *            待转换list
	 * @param keyName
	 *            字段名称
	 * @return {keyName值:单个bean/多个list}
	 */
	private Bean commList2Bean(List<Bean> list, String keyName) {

		Bean newBean = new Bean();

		if (list == null || list.isEmpty()) {
			return newBean;
		}

		for (Bean bean : list) {

			String key = bean.getStr(keyName);

			if (newBean.containsKey(key)) { // 考生已存在

				Object temp = newBean.get(key);

				List<Bean> tempList = null;

				if (temp instanceof Bean) {

					tempList = new ArrayList<Bean>();

					tempList.add(newBean.getBean(key));

				} else if (temp instanceof List) {

					tempList = newBean.getList(key);
				}

				tempList.add(bean);

				newBean.set(key, tempList);

			} else {

				List<Bean> blist = new ArrayList<Bean>();

				blist.add(bean);

				newBean.set(key, blist);
			}
		}
		return newBean;
	}

	/**
	 * 获取考生
	 *
	 * @param xmId
	 * @param odept
	 * @param status
	 *            0正常 1请假 2借考
	 * @return
	 */
	private List<Bean> getKsList(String xmId, String odept, int status) {

		String odeptField = "S_DEPT";

		if (status == 2) {
			odeptField = "JK_ODEPT";
		}

		SqlBean sql = new SqlBean().and("a.XM_ID", xmId).and("a.S_FLAG", 1);

		sql.selects(" a.*,p.STATION_TYPE,p.STATION_TYPE_CODE,p.STATION_NO,p.STATION_NO_CODE ");

		sql.and("a.BM_STATUS", status);

		sql.appendWhere(" AND a.BM_CODE = p.PERSON_ID");

		String allOdept = "'0'";

		for (Object kcid : kcOrgBean.keySet()) {

			Bean kcjg = kcOrgBean.getBean(kcid);

			for (Object jg : kcjg.keySet()) {

				allOdept += ",'" + jg + "'";
			}
		}

		StringBuffer sb = new StringBuffer();

		sb.append(" AND  EXISTS  ( SELECT DEPT_CODE FROM " + ServMgr.SY_ORG_DEPT + " WHERE 1=1");
		// sb.append(" AND SY_ORG_DEPT.DEPT_TYPE = " +
		// OrgConstant.DEPT_TYPE_ORG);
		sb.append(" AND SY_ORG_DEPT.S_FLAG = 1");

		if (status == 2) {
			sb.append(" AND SY_ORG_DEPT.ODEPT_CODE = a." + odeptField);
		} else {
			sb.append(" AND SY_ORG_DEPT.DEPT_CODE = a." + odeptField);
		}
		sb.append(" AND SY_ORG_DEPT.ODEPT_CODE in (" + allOdept + ")");
		sb.append(" )");
		sql.appendWhere(sb.toString());

		sql.tables(TsConstant.SERV_BMSH_PASS + " a, SY_HRM_ZDSTAFFPOSITION p");

		String notExistsSql = " select * from " + TsConstant.SERV_KCAP_YAPZW + " b where b.SH_ID = a.SH_ID";

		sql.appendWhere(" AND NOT EXISTS (" + notExistsSql + ")");

		String where = "select " + sql.getStr(Constant.PARAM_SELECT) + " from " + sql.getStr(Constant.PARAM_TABLE);

		where += " where 1=1 " + sql.getWhere();

		List<Bean> ksList = Transaction.getExecutor().query(where, sql.getVars());

		return ksList;
	}

	private List<Bean> getKsListNew(String xmId, String odept, int status) {

		String kcIds = "";

		for (Object kcid : kcOrgBean.keySet()) {

			if (Strings.isBlank(kcIds)) {

				kcIds += "'" + kcid + "'";

			} else {

				kcIds += ",'" + kcid + "'";
			}

		}

		StringBuffer sb = new StringBuffer();

		sb.append("SELECT  a.*,p.STATION_TYPE,p.STATION_TYPE_CODE,p.STATION_NO,p.STATION_NO_CODE FROM ");

		sb.append("TS_BMSH_PASS a ");

		sb.append("LEFT JOIN SY_ORG_USER b ON a.BM_CODE = b.USER_CODE ");

		if (status == 2) {
			sb.append("LEFT JOIN SY_ORG_DEPT c ON c.DEPT_CODE = a.JK_ODEPT ");
		} else {
			sb.append("LEFT JOIN SY_ORG_DEPT c ON c.DEPT_CODE = a.S_DEPT ");
		}
		sb.append("LEFT JOIN SY_HRM_ZDSTAFFPOSITION p ON a.BM_CODE = p.PERSON_ID ");

		sb.append("WHERE 1 = 1 AND XM_ID = '").append(xmId);

		if (status == 2) {

			sb.append("' AND a.BM_STATUS = 2 ");

		} else {

			sb.append("' AND a.BM_STATUS = 0 ");
		}

		StringBuffer exists = new StringBuffer(" SELECT '' FROM ");
		exists.append(TsConstant.SERV_KCAP_GLJG);
		exists.append(" g WHERE g.KC_ID IN (").append(kcIds).append(") ");
		exists.append(" AND ((INSTR(c.CODE_PATH, g.JG_CODE) > 0");
		exists.append(" AND g.JG_TYPE = 2 )");
		exists.append(" or (");
		exists.append("  c.ODEPT_CODE = g.JG_CODE ");
		exists.append(" AND g.JG_TYPE = 1 ))");

		sb.append(" AND EXISTS (").append(exists.toString()).append(")");

		String notExistsSql = " select * from " + TsConstant.SERV_KCAP_YAPZW + " b where b.SH_ID = a.SH_ID";

		sb.append(" AND NOT EXISTS (").append(notExistsSql).append(")");

		List<Bean> ksList = Transaction.getExecutor().query(sb.toString(), new ArrayList<Object>());

		return ksList;
	}

	/**
	 * 获取考场下关联机构
	 *
	 * @param kcId
	 * @param res
	 * @return
	 */
	public Bean getGljg(String kcId) {

		for (Object key : kcBean.keySet()) { // 遍历所属机构

			List<Bean> kcList = kcBean.getList(key);

			for (Bean kc : kcList) { // 遍历所属机构下考场

				if (kcId.equals(kc.getBean("INFO").getStr("KC_ID"))) {

					return kc.getBean("GLJG");
				}
			}
		}

		return new Bean();
	}

	/**
	 * 考场关联的机构下考生
	 *
	 * @param kcId
	 * @param res
	 * @return Bean{考试时长：考生Bean{考生编码：考生bean/list}}
	 */
	public Bean getGljgKs(String kcId) {
		try {
			Bean filtBean = new Bean();

			Bean gljg = getGljg(kcId);

			// Bean ksBean = res.getFreeKsBean();

			for (Object key : gljg.keySet()) {

				Bean jg = gljg.getBean(key);

				String dCode = jg.getStr("JG_CODE");

				if (jg.getInt("JG_TYPE") == OrgConstant.DEPT_TYPE_DEPT) { // 关联部门

					dCode = OrgMgr.getOdept(dCode).getODeptCode(); // 获取机构
				}

				Bean timeKs = (Bean) ksBean.getBean(dCode).clone();

				// log.error("-----------" + timeKs.toString());

				for (Object time : timeKs.keySet()) { // 遍历时长

					Bean ks = timeKs.getBean(time); // 考生

					if (filtBean.containsKey(time)) {

						filtBean.set(time, KcapUtils.mergeBean(ks, filtBean.getBean(time)));

					} else {

						filtBean.set(time, ks.clone());
					}
				}
			}

			return filtBean;

		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	public Bean getRuleBean() {
		return ruleBean;
	}

	/**
	 * 考场资源bean
	 *
	 * @return Bean{所属机构id：考场list<Bean>
	 *         {INFO:考场信息Bean,GLJG:关联机构Bean,ZWH:座位号list,CC:场次list} }
	 */
	public Bean getKcBean() {
		return kcBean;
	}

	/**
	 * 不含请假考生
	 *
	 * @return Bean{ 机构ID : 考试时长Bean {时长：考生Bean { 考生ID ： 单个报名Bean/多个报名List} } }
	 */
	public Bean getFreeKsBean() {
		return ksBean;
	}

	/**
	 * 借考考生
	 *
	 * @return Bean{ 机构ID : 考试时长Bean {时长：考生Bean { 考生ID ： 单个报名Bean/多个报名List} } }
	 */
	public Bean getJkKsBean() {
		return jkKsBean;
	}

	/**
	 * 考生分配最少考场场次规则
	 *
	 * @return 0 最少考场 1 最少场次
	 */
	public int getKsPriority() {
		return ksPriority;
	}

	/**
	 * 不符合规则考试 是否强制安排 1强制
	 */
	public int getKsConstrain() {
		return ksConstrain;
	}

	/**
	 * 未安排座位 最少考场
	 *
	 * @return Bean{考场id:场次Bean{场次id:Bean{日期:座位Bean}}} 考场 ->场次 ->日期 ->座位
	 */
	public Bean getFreeKcZwBean() {
		return freeKcZwBean;
	}

	/**
	 * 未安排座位 最少场次
	 *
	 * @return {场次id:Bean{日期:考场Bean{考场id:Bean座位}}} 场次 ->日期 ->考场 ->座位
	 */
	public Bean getFreeCcZwBean() {
		return freeCcZwBean;
	}

	/**
	 * 领导职务考生
	 *
	 * @return Bean{userCode:审核id 多个报名逗号隔开}
	 */
	public Bean getLeaderBean() {
		return leaderBean;
	}

	/**
	 * 已安排座位
	 *
	 * @return Bean{考场ID^场次号^日期: 座位安排 Bean}
	 */
	public Bean getBusyZwBean() {
		return busyZwBean;
	}

	/**
	 * 距离考场远的考生
	 *
	 * @return Bean{考场ID：Bean{用户id: 用户id}}
	 */
	public Bean getFarKsBean() {
		return farKsBean;
	}

	public String getXmId() {
		return currentXmId;
	}

	/**
	 * 考场关联机构
	 *
	 * @return Bean{kcid:关联机构bean}
	 */
	public Bean getKcOrgBean() {
		return kcOrgBean;
	}

	/**
	 * 特殊机构考生 靠前靠后安排
	 *
	 * @return Bean{考生code:考生code}
	 */
	public Bean getSpOrgKsBean() {
		return spOrgKsBean;
	}

	/**
	 * 网点考生
	 *
	 * @return Bean{网点所属机构：网点Bean{网点code：考生Bean{考生编码：报名list}}}
	 */
	public Bean getBranchBean() {
		return branchBean;
	}

	/**
	 * 场次 上午/下午
	 *
	 * @return Bean{考场ID^场次号^日期: AM/PM}
	 */
	public Bean getCcAMPM() {
		return ccAMPM;
	}

	public void showResLog() {

		log.debug(">>>>>>>>>>>>>>>>>kcBean>>>>>>>>>>" + this.getKcBean().toString());

		log.debug(">>>>>>>>>>>>>>>>>freeKsBean>>>>>>>>>>" + this.getFreeKsBean().toString());

		if (this.getFreeKsBean() != null) {

			for (Object o : this.getFreeKsBean().keySet()) {

				Bean jg = this.getFreeKsBean().getBean(o);

				for (Object time : jg.keySet()) {

					Bean timeb = jg.getBean(time);

					log.debug("------------------freeKsBean-----" + o + "--------" + time + "------" + timeb.size());
				}
			}
		}

		log.debug(">>>>>>>>>>>>>>>>JkKsBean>>>>>>>>>" + this.getJkKsBean().toString());

		// if (this.getJkKsBean() != null) {
		//
		// for (Object o : this.getFreeKsBean().keySet()) {
		//
		// Bean jg = this.getFreeKsBean().getBean(o);
		//
		// for (Object time : jg.keySet()) {
		//
		// Bean timeb = jg.getBean(time);
		//
		// log.error("------------------JkKsBean-----" + o + "--------" + time +
		// "------" + timeb.toString());
		// }
		// }
		// }
	}

	private void cleanBusyZw(String xmId) {

		ParamBean param = new ParamBean();

		param.set("XM_ID", xmId);

		ServMgr.act(TsConstant.SERV_KCAP_YAPZW, "clearDirtyData", param);

		boolean isdel = ConfMgr.getConf("TS_KCAP_YAPZW_AUTO_DEL", false);

		if (isdel) { // 自动安排考场前 清空已安排的考生

			List<String> kcList = new ArrayList<String>();

			List<String> jglist = new ArrayList<String>();

			for (Object kcid : kcOrgBean.keySet()) {

				kcList.add(kcid.toString());

				Bean kcjg = kcOrgBean.getBean(kcid);

				for (Object jg : kcjg.keySet()) {

					jglist.add(jg.toString());
				}
			}

			SqlBean sql = new SqlBean().and("XM_ID", xmId);

			// sql.and("S_USER", Context.getUserBean().getCode());

			sql.and("ISAUTO", 1);// 自动安排

			sql.andIn("U_ODEPT", jglist.toArray());

			sql.andIn("KC_ID", kcList.toArray());

			ServDao.delete(TsConstant.SERV_KCAP_YAPZW, sql);

		}
	}
}
