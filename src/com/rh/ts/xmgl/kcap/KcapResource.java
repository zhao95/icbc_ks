package com.rh.ts.xmgl.kcap;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.util.OrgConstant;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Strings;
import com.rh.ts.util.TsConstant;

public class KcapResource {

	// private static Log log = LogFactory.getLog(KcapResource.class);

	/**
	 * 领导职务考生
	 */
	private Bean leaderBean = null;

	/**
	 * 考场距离远的考生
	 */
	private Bean farKsBean = null;

	/**
	 * 特定考试仅限于省分行安排
	 */
	private Bean spExamBean = null;

	/**
	 * 未安排座位 考场优先
	 */
	private Bean freeKcZwBean = null;

	/**
	 * 未安排座位 场次优先
	 */
	private Bean freeCcZwBean = null;

	/**
	 * 已安排座位
	 */
	private Bean busyZwBean = null;

	/**
	 * 考生分配优先规则 0 考场优先 1 场次优先
	 */
	private int ksPriority = 0;
	/**
	 * 场次先后安排 1：先，2：后
	 */
	private int ccOrder = 0;

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
	 * 无请假考生 key:考生机构 value:list
	 */
	private Bean ksBean = null;

	/**
	 * 网点考生
	 */
	private Bean branchBean = null;

	public KcapResource(String xmId) {

		// 考场信息
		loadKc(xmId, "");
		// 考生信息
		loadKs(xmId, "");
		// 考场座位安排
		loadZw(xmId, "");
		// 网点
		loadBranch();

		// 加载规则
		loadRule(xmId);
	}

	public KcapResource(String xmId, String odept) {

		if (!Strings.isBlank(odept)) {

			UserBean user = Context.getUserBean();

			String cmpyCode = user.getCmpyCode();

			String codePath = OrgMgr.getOdept(odept).getCodePath();

			SqlBean sql = new SqlBean();
			sql.andLikeRT("CODE_PATH", codePath);
			sql.and("DEPT_TYPE", OrgConstant.DEPT_TYPE_ORG);
			sql.and("CMPY_CODE", cmpyCode);
			sql.and("S_FLAG", 1);

			List<Bean> odeptList = ServDao.finds(ServMgr.SY_ORG_DEPT, sql);

			// 当前机构及下级机构
			String[] odepts = new String[odeptList.size()];

			for (int i = 0; i < odeptList.size(); i++) {

				Bean bean = odeptList.get(i);

				odepts[i] = bean.getStr("DEPT_CODE");
			}

			// 考场信息
			loadKc(xmId, odept);
			// 考生信息
			loadKs(xmId, odept);
			// 考场座位安排
			loadZw(xmId, odept);

		} else {
			// 考场信息
			loadKc(xmId, "");
			// 考生信息
			loadKs(xmId, "");
			// 考场座位安排
			loadZw(xmId, "");
		}

		// 网点
		loadBranch();

		// 加载规则
		loadRule(xmId);
	}

	/**
	 * 加载没有安排座位的考生
	 * 
	 * @param xmId
	 * @param odepts
	 */
	private void loadKs(String xmId, String odept) {

		List<Bean> jkKsList = getJkKsList(xmId, odept);

		jkKsBean = ksList2Bean(jkKsList, "S_ODEPT", "BM_KS_TIME", "BM_CODE");

		// 考试
		List<Bean> ksList = getKsList(xmId, odept);

		ksList.addAll(jkKsList);

		ksBean = ksList2Bean(ksList, "S_ODEPT", "BM_KS_TIME", "BM_CODE");
	}

	/**
	 * 加载考场及场次信息
	 * 
	 * @param xmId
	 * @param odepts
	 */
	private void loadKc(String xmId, String odept) {

		List<Object> values = new ArrayList<Object>();

		values.add(xmId);

		String sql = "select k.KC_ID,K.KC_ODEPTCODE,K.KC_ODEPTNAME,k.KC_SCORE,k.KC_STATE,k.KC_MAX,k.KC_GOOD,k.KC_LEVEL,c.CC_ID,c.XM_ID from TS_XMGL_KCAP_DAPCC c LEFT JOIN ts_kcgl k on k.kc_id = c.kc_id where a.XM_ID=? ";

		if (!Strings.isBlank(odept)) {

			UserBean user = Context.getUserBean();
			String cmpyCode = user.getCmpyCode();
			String codePath = OrgMgr.getOdept(odept).getCodePath();

			String sql1 = " select DEPT_CODE FROM SY_ORG_DEPT where ";

			sql1 += " AND SY_ORG_DEPT.CODE_PATH like CONCAT('" + codePath + "','%') ";

			sql1 += " AND SY_ORG_DEPT.DEPT_TYPE = 2";

			sql1 += " AND SY_ORG_DEPT.CMPY_CODE =  '" + cmpyCode + "'";

			sql1 += " AND SY_ORG_DEPT.S_FLAG = 1";

			sql += " and k.KC_ODEPTCODE in (" + sql1 + ")";
		}

		// 所有考场
		List<Bean> kcList = Transaction.getExecutor().query(sql, values);

		kcList2Bean(kcList);

	}

	/**
	 * 加载未安排考场座位
	 */
	private void loadZw(String xmId, String odept) {

		loadBusyZw(xmId, odept);

		if (ksPriority == 1) { // 场次优先

			fitCcZwBean();

		} else { // 考场优先

			fitKcZwBean();

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

		busyZwBean = new Bean();

		SqlBean sql = new SqlBean().and("XM_ID", xmId);

		if (!Strings.isBlank(odept)) {

			UserBean user = Context.getUserBean();
			String cmpyCode = user.getCmpyCode();
			String codePath = OrgMgr.getOdept(odept).getCodePath();

			SqlBean sqlIn = new SqlBean();
			sqlIn.selects("DEPT_CODE");
			sqlIn.tables(ServMgr.SY_ORG_DEPT);
			sqlIn.andLikeRT(ServMgr.SY_ORG_DEPT + ".CODE_PATH", codePath);
			sqlIn.and(ServMgr.SY_ORG_DEPT + ".DEPT_TYPE", OrgConstant.DEPT_TYPE_ORG);
			sqlIn.and(ServMgr.SY_ORG_DEPT + ".CMPY_CODE", cmpyCode);
			sqlIn.and(ServMgr.SY_ORG_DEPT + ".S_FLAG", 1);

			sql.andInSub("U_ODEPT", sqlIn.toString(), sqlIn.getVars());
		}

		List<Bean> busyZwList = ServDao.finds(TsConstant.SERV_KCAP_YAPZW, sql);

		for (Bean zw : busyZwList) {

			String kcId = zw.getStr("KC_ID"); // 考场
			String sjCc = zw.getStr("SJ_CC"); // 场次
			String zwh = zw.getStr("ZW_XT"); // 座位号
			String date = zw.getStr("SJ_START").substring(0, 9);// 考试日期

			busyZwBean.set(kcId + "^" + sjCc + "^" + date + "^" + zwh, zw);
		}
	}

	/**
	 * 装载考场安排规则
	 * 
	 * @param xmId
	 */

	private void loadRule(String xmId) {

		ruleBean = new Bean();

		UserBean user = Context.getUserBean();

		SqlBean sql = new SqlBean().and("XM_ID", xmId).and("S_USER", user.getCode()).and("S_FLAG", 1);

		List<Bean> ruleList = ServDao.finds(TsConstant.SERV_KCAP_GZ, sql);

		for (Bean rule : ruleList) {

			String ruleCode = rule.getStr("GZ_CODE");

			ruleBean.set(ruleCode, rule);

			if (KcapRuleEnum.R003.equals(ruleCode)) { // 距离远近规则

				fitFarKsBean();

			} else if (KcapRuleEnum.R007.equals(ruleCode)) { // 领导职务考生座位靠前安排

				leaderBean = new Bean();

				for (Object key : ksBean.keySet()) {

					Object obj = ksBean.get(key);

					if (obj instanceof List) {

						List<Bean> kslist = ksBean.getList(key);

						for (Bean bean : kslist) {

							if ("023001".equals(bean.getStr("STATION_TYPE_CODE"))) {// 管理类视为领导职务

								String shId = bean.getStr("SH_ID");

								String userCode = bean.getStr("BM_CODE");

								if (leaderBean.containsKey(userCode)) { // 考生已存在

									String shIds = leaderBean.getStr(userCode);

									shIds += "," + shId;

									leaderBean.set(userCode, shIds);

								} else {

									leaderBean.set(userCode, shId);
								}
							}

						}

					} else if (obj instanceof Bean) {

						Bean ks = ksBean.getBean(key);

						if ("023001".equals(ks.getStr("STATION_TYPE_CODE"))) {// 管理类视为领导职务

							String shId = ks.getStr("SH_ID");

							String userCode = ks.getStr("BM_CODE");

							if (leaderBean.containsKey(userCode)) { // 考生已存在

								String shIds = leaderBean.getStr(userCode);

								shIds += "," + shId;

								leaderBean.set(userCode, shIds);

							} else {

								leaderBean.set(userCode, shId);
							}
						}
					}
				}

			} else if (KcapRuleEnum.R008.equals(ruleCode)) { // 特定机构考生场次先后安排

				if (farKsBean == null) {
					fitFarKsBean();
				}

				String jsonStr = rule.getStr("MX_VALUE2");

				JSONArray objArray;

				try {
					objArray = new JSONArray(jsonStr);

					JSONObject jsonObj = objArray.getJSONObject(0);

					String val = jsonObj.getString("val"); // 场次先后 1先 2后

					if ("1".equals(val)) {

						ccOrder = 1;

					} else if ("2".equals(val)) {

						ccOrder = 2;
					}

				} catch (JSONException e) {

					e.printStackTrace();
				}

			} else if (KcapRuleEnum.R009.equals(ruleCode)) { // 特定考试仅限于省分行安排

				spExamBean = new Bean();

				String jsonStr = rule.getStr("MX_VALUE2");

				JSONArray objArray;

				try {
					objArray = new JSONArray(jsonStr);

					JSONObject jsonObj = objArray.getJSONObject(0);

					String val = jsonObj.getString("val"); // 选择的考试

					for (String kslb : val.split(",")) {

						String[] array = kslb.split("^"); // array[0]序列，array[1]模块，array[2]等级

						spExamBean.set(kslb, array);
					}

				} catch (JSONException e) {

					e.printStackTrace();
				}
			}
		}

	}

	private void loadBranch() {

		Bean gljgBean = new Bean();

		for (Object key : kcBean.entrySet()) { // 所有机构下的考场bean

			List<Bean> kcinfoList = kcBean.getList(key); // 某所属机构下的考场list

			for (Bean kcInfo : kcinfoList) {

				List<Bean> jgList = kcInfo.getList("GLJG"); // 考场的关联机构list

				for (Bean jg : jgList) {

					String dCode = jg.getStr("JG_CODE");

					if (jg.getInt("JG_TYPE") == OrgConstant.DEPT_TYPE_DEPT) { // 关联部门

						dCode = OrgMgr.getOdept(dCode).getODeptCode(); // 查找机构
					}

					if (!gljgBean.containsKey(dCode)) {

						gljgBean.set(dCode, jg);
					}
				}
			}
		}

		SqlBean sql = new SqlBean();

		sql.and("", "");

		ServDao.find(TsConstant.SERV_BOM_ZDPSTRUINFO, sql);
	}

	/**
	 * 组装考场优先 未安排座位
	 * 
	 */
	private void fitKcZwBean() {

		freeKcZwBean = new Bean();

		for (Object key : kcBean.entrySet()) { // 所有机构下的考场bean

			Bean zwhBeanVal = new Bean();

			List<Bean> kcinfoList = kcBean.getList(key); // 某所属机构下的考场list

			for (Bean kcInfo : kcinfoList) { // 遍历考场list
				
				Bean info = kcInfo.getBean("INFO");

				String kcId = info.getStr("KC_ID"); // 考场id
				
				String kcLv = info.getStr("KC_LEVEL"); //考场层级

				String xmId = info.getStr("XM_ID"); // 项目id

				List<Bean> zwhList = kcInfo.getList("ZWH");

				List<Bean> ccList = kcInfo.getList("CC");

				zwhBeanVal = new Bean();

				for (Bean cc : ccList) {// 遍历场次(SJ_ID,CC_ID,SJ_CC,SJ_START,SJ_END)

					String sjCc = cc.getStr("SJ_CC"); // 场次号

					String date = cc.getStr("SJ_START").substring(0, 9); // 考试日期

					Bean zwBean = getZwBean(zwhList, kcId, sjCc, date);

					zwBean.set("SJ_ID", cc.getStr("SJ_ID"));

					zwBean.set("SJ_CC", sjCc);

					zwBean.set("SJ_DATE", date);

					zwBean.set("CC_ID", cc.getStr("CC_ID"));

					zwBean.set("KC_ID", kcId);
					
					zwBean.set("KC_LV", kcLv);

					zwBean.set("XM_ID", xmId);

					zwBean.set("SJ_START", cc.getStr("SJ_START"));

					zwBean.set("SJ_END", cc.getStr("SJ_END"));

					Bean dayBean = new Bean();

					dayBean.set(date, zwBean); // {key日期：val座位Bean}

					zwhBeanVal.set(sjCc, dayBean);
				}

				freeKcZwBean.set(kcId, zwhBeanVal);
			}
		}
	}

	/**
	 * 组装场次优先 未安排座位
	 * 
	 */
	private void fitCcZwBean() {

		freeCcZwBean = new Bean();

		for (Object key : kcBean.entrySet()) { // 所有机构下的考场bean

			List<Bean> kcinfoList = kcBean.getList(key); // 某所属机构下的考场list

			for (Bean kcInfo : kcinfoList) {

				String kcId = kcInfo.getBean("INFO").getStr("KC_ID"); // 考场id

				String xmId = kcInfo.getBean("INFO").getStr("XM_ID"); // 项目id

				List<Bean> zwhList = kcInfo.getList("ZWH"); // 座位号

				List<Bean> ccList = kcInfo.getList("CC"); // 场次

				for (Bean cc : ccList) {// SJ_ID,CC_ID,SJ_CC,SJ_START,SJ_END

					String sjCc = cc.getStr("SJ_CC"); // 场次号

					String date = cc.getStr("SJ_START").substring(0, 9); // 考试日期

					Bean zwBean = getZwBean(zwhList, kcId, sjCc, date);

					zwBean.set("SJ_ID", cc.getStr("SJ_ID"));

					zwBean.set("SJ_DATE", date);

					zwBean.set("CC_ID", cc.getStr("CC_ID"));

					zwBean.set("KC_ID", kcId);

					zwBean.set("XM_ID", xmId);

					zwBean.set("SJ_START", cc.getStr("SJ_START"));

					zwBean.set("SJ_END", cc.getStr("SJ_END"));

					Bean zwhBeanVal = new Bean();

					zwhBeanVal.set(kcId, zwBean);

					Bean dayBean = new Bean();

					dayBean.set(date, zwhBeanVal); // {key日期：val座位Bean}

					freeCcZwBean.set(sjCc, dayBean);
				}
			}
		}
	}

	/**
	 * 考生list转bean,key考生机构
	 */
	private Bean ksList2Bean(List<Bean> ksList, String... keys) {

		Bean ksBean = new Bean();

		if (keys == null || keys.length < 3) {
			return ksBean;
		}

		ksBean = commList2Bean(ksList, keys[0]); // "S_ODEPT"

		Bean temp = new Bean();

		temp.putAll(ksBean);

		for (Object key : temp.keySet()) {

			List<Bean> list = temp.getList(key);

			Bean timeBean = commList2Bean(list, keys[1]);// "BM_KS_TIME"

			Bean temp1 = new Bean();

			temp1.putAll(timeBean);

			for (Object key1 : temp1.keySet()) {

				List<Bean> list1 = temp1.getList(key1);

				timeBean.remove(key1).put(key1, commList2Bean(list1, keys[2]));// "BM_CODE"
			}

			ksBean.remove(key).put(key, timeBean);
		}

		return ksBean;
	}

	/**
	 * 考场资源 list转Bean {所属机构id：考场list<Bean>
	 * {INFO:考场信息Bean,GLJG:关联机构list,ZWH:座位号list,CC:场次list} }
	 */
	private void kcList2Bean(List<Bean> kcList) {

		kcBean = new Bean();

		for (Bean item : kcList) {

			String ccId = item.getStr("CC_ID");

			String kcId = item.getStr("KC_ID");

			String odept = item.getStr("KC_ODEPTCODE");

			// 场次
			SqlBean ccsql = new SqlBean().and("CC_ID", ccId).and("S_FLAG", 1);
			ccsql.selects("SJ_ID,CC_ID,SJ_CC,SJ_START,SJ_END").asc("SJ_CC");
			List<Bean> ccList = ServDao.finds(TsConstant.SERV_KCAP_CCSJ, ccsql);

			// 关联机构
			SqlBean jgsql = new SqlBean().and("KC_ID", kcId).and("S_FLAG", 1).selects("KC_ID,JG_CODE,JG_NAME,JG_FAR");
			List<Bean> jgList = ServDao.finds(TsConstant.SERV_KCGL_GLJG, jgsql);

			// 座位号
			SqlBean zwsql = new SqlBean().and("KC_ID", kcId).and("S_FLAG", 1);
			zwsql.selects("ZW_ID,ZW_ZWH_XT,ZW_ZWH_SJ,ZW_KY");
			List<Bean> zwList = ServDao.finds(TsConstant.SERV_KCGL_ZWDYB, zwsql);

			Bean kcInfo = new Bean();
			kcInfo.set("INFO", item);
			kcInfo.set("GLJG", jgList);
			kcInfo.set("ZWH", zwList);
			kcInfo.set("CC", ccList);

			List<Bean> kcinfoList = null;

			if (kcBean.containsKey(odept)) {

				kcinfoList = kcBean.getList(odept);
			} else {
				kcinfoList = new ArrayList<Bean>();
			}

			kcinfoList.add(kcInfo);

			kcBean.set(odept, kcinfoList);
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

		for (Bean zw : zwhList) {

			String zwh = zw.getStr("ZW_ZWH_XT"); // 座位号

			if (!busyZwBean.containsKey(kcId + "^" + sjCc + "^" + date + "^" + zwh)) { // 判断座位是否已安排,只取未安排座位

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

		for (Object key : kcBean.entrySet()) { // 所有机构下的考场bean

			List<Bean> kcinfoList = kcBean.getList(key); // 某所属机构下的考场list

			for (Bean kcInfo : kcinfoList) {

				Bean farksBeanVal = new Bean();

				String kcId = kcInfo.getBean("INFO").getStr("KC_ID");

				List<Bean> jgList = kcInfo.getList("GLJG"); // 考场的关联机构list

				for (Bean jg : jgList) {

					String dCode = jg.getStr("JG_CODE");

					if (jg.getInt("JG_TYPE") == OrgConstant.DEPT_TYPE_DEPT) { // 关联部门

						dCode = OrgMgr.getOdept(dCode).getODeptCode(); // 获取机构
					}

					List<Bean> jgksList = null;

					if (jg.getInt("JG_FAR") == 1) { // 关联考场距离远的机构

						jgksList = ksBean.getList(dCode);// 机构下考生list

						farksBeanVal = commList2Bean(jgksList, "BM_CODE");

						farKsBean.set(kcId, farksBeanVal);
					}
				}
				// farKsBean.set(kcId, farksBeanVal);
			}
		}
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

				newBean.set(key, bean);
			}
		}
		return newBean;
	}

	/**
	 * 借考考生
	 * 
	 * @param xmId
	 * @param odept
	 * @return
	 */
	private List<Bean> getJkKsList(String xmId, String odept) {

		SqlBean sql = new SqlBean().and("a.XM_ID", xmId).and("a.BM_CODE", "p.PERSON_ID").and("a.S_FLAG", 1);

		sql.selects(" a.*,p.STATION_TYPE,p.STATION_TYPE_CODE,p.STATION_NO,p.STATION_NO_CODE ");

		sql.andIn("a.BM_STATUS", 1);// 借考

		if (!Strings.isBlank(odept)) {

			UserBean user = Context.getUserBean();
			String cmpyCode = user.getCmpyCode();
			String codePath = OrgMgr.getOdept(odept).getCodePath();

			SqlBean sqlIn = new SqlBean();
			sqlIn.selects("DEPT_CODE");
			sqlIn.tables(ServMgr.SY_ORG_DEPT);
			sqlIn.andLikeRT(ServMgr.SY_ORG_DEPT + ".CODE_PATH", codePath);
			sqlIn.and(ServMgr.SY_ORG_DEPT + ".DEPT_TYPE", OrgConstant.DEPT_TYPE_ORG);
			sqlIn.and(ServMgr.SY_ORG_DEPT + ".CMPY_CODE", cmpyCode);
			sqlIn.and(ServMgr.SY_ORG_DEPT + ".S_FLAG", 1);

			sql.andInSub("a.JK_ODEPT", sqlIn.toString(), sqlIn.getVars());
		}

		sql.tables(TsConstant.SERV_BMSH_PASS + " a, SY_HRM_ZDSTAFFPOSITION p");

		String notExistsSql = " select * from " + TsConstant.SERV_KCAP_YAPZW + "b where b.SH_ID = a.SH_ID";

		sql.appendWhere(" AND NOT EXSITS (" + notExistsSql + ")");

		List<Bean> jkList = Transaction.getExecutor().query(sql.toString(), sql.getVars());

		return jkList;
	}

	/**
	 * 无请假借考 考生
	 * 
	 * @param xmId
	 * @param odept
	 * @return
	 */
	private List<Bean> getKsList(String xmId, String odept) {

		SqlBean sql = new SqlBean().and("a.XM_ID", xmId).and("a.BM_CODE", "p.PERSON_ID").and("a.S_FLAG", 1);

		sql.selects(" a.*,p.STATION_TYPE,p.STATION_TYPE_CODE,p.STATION_NO,p.STATION_NO_CODE ");

		sql.andIn("a.BM_STATUS", 0);// 借考

		if (!Strings.isBlank(odept)) {

			UserBean user = Context.getUserBean();
			String cmpyCode = user.getCmpyCode();
			String codePath = OrgMgr.getOdept(odept).getCodePath();

			SqlBean sqlIn = new SqlBean();
			sqlIn.selects("DEPT_CODE");
			sqlIn.tables(ServMgr.SY_ORG_DEPT);
			sqlIn.andLikeRT(ServMgr.SY_ORG_DEPT + ".CODE_PATH", codePath);
			sqlIn.and(ServMgr.SY_ORG_DEPT + ".DEPT_TYPE", OrgConstant.DEPT_TYPE_ORG);
			sqlIn.and(ServMgr.SY_ORG_DEPT + ".CMPY_CODE", cmpyCode);
			sqlIn.and(ServMgr.SY_ORG_DEPT + ".S_FLAG", 1);

			sql.andInSub("a.S_ODEPT", sqlIn.toString(), sqlIn.getVars());
		}

		sql.tables(TsConstant.SERV_BMSH_PASS + " a, SY_HRM_ZDSTAFFPOSITION p");

		String notExistsSql = " select * from " + TsConstant.SERV_KCAP_YAPZW + "b where b.SH_ID = a.SH_ID";

		sql.appendWhere(" AND NOT EXSITS (" + notExistsSql + ")");

		List<Bean> ksList = Transaction.getExecutor().query(sql.toString(), sql.getVars());

		return ksList;
	}

	public Bean getRuleBean() {
		return ruleBean;
	}

	/**
	 * 考场资源bean
	 * 
	 * @return Bean{所属机构id：考场list<Bean>
	 *         {INFO:考场信息Bean,GLJG:关联机构list,ZWH:座位号list,CC:场次list} }
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
	 * 考生分配优先规则
	 * 
	 * @return 0 考场优先 1 场次优先
	 */
	public int getKsPriority() {
		return ksPriority;
	}

	/**
	 * 场次先后安排规则
	 * 
	 * @return 1：先，2：后
	 */
	public int getCcOrder() {
		return ccOrder;
	}

	/**
	 * 未安排座位 考场优先
	 * 
	 * @return Bean{考场id:场次Bean{场次id:Bean{日期:座位Bean}}} 考场 ->场次 ->日期 ->座位
	 */
	public Bean getFreeKcZwBean() {
		return freeKcZwBean;
	}

	/**
	 * 未安排座位 场次优先
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
	 * @return Bean{考场ID^场次号^日期^座位号: 座位安排 Bean}
	 */
	public Bean getBusyZwBean() {
		return busyZwBean;
	}

	/**
	 * 距离考场远的考生
	 * 
	 * @return Bean{考场ID：Bean{用户id: 考生Bean/考生List}}
	 */
	public Bean getFarKsBean() {
		return farKsBean;
	}
}
