package com.rh.ts.xmgl.kcap;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.serv.ServDao;
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
	 * 待安排座位
	 */
	private Bean zwapBean = null;

	/**
	 * 考生分配规则 0 考场优先 1 场次优先
	 */
	private int distRule = 0;
	/**
	 * 场次先后安排 1：先，2：后
	 */
	private int priority = 0;

	/**
	 * 引入规则
	 */
	private Bean ruleBean = null;

	/**
	 * 考生list
	 */
	private List<Bean> ksList = null;

	/**
	 * 借考考生list
	 */
	private List<Bean> jkList = null;

	/**
	 * 借考考生 key:考生机构 value:list
	 */
	private Bean jkBean = null;

	/**
	 * 考场list
	 */
	private List<Bean> kcList = null;

	/**
	 * 考场 key:机构 value:bean
	 */
	private Bean kcBean = null;

	/**
	 * 无请假借考考生 key:考生机构 value:list
	 */
	private Bean ksBean = null;

	public KcapResource(String xmId) {

		// 考场信息
		loadKc(xmId, "");
		// 考生信息
		loadKs(xmId, "");
		// 考场座位安排
		loadZwh(xmId, "");

		// 加载规则
		loadRule(xmId);

	}

	public KcapResource(String xmId, String odept) {

		if (!Strings.isBlank(odept)) {

			UserBean user = Context.getUserBean();

			String cmpyCode = user.getCmpyCode();

			List<DeptBean> odeptList = OrgMgr.getSubOrgs(cmpyCode, odept);

			// 当前机构及下级机构
			String[] odepts = new String[odeptList.size()];

			for (int i = 0; i < odeptList.size(); i++) {

				DeptBean bean = odeptList.get(i);

				odepts[i] = bean.getCode();
			}
			// 考场信息
			loadKc(xmId, odepts);
			// 考生信息
			loadKs(xmId, odepts);
			// 考场座位安排
			loadZwh(xmId, odepts);

		} else {
			// 考场信息
			loadKc(xmId, "");
			// 考生信息
			loadKs(xmId, "");
			// 考场座位安排
			loadZwh(xmId, "");
		}

		// 加载规则
		loadRule(xmId);

	}

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

				for (Bean ks : ksList) {

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

						priority = 1;

					} else if ("2".equals(val)) {

						priority = 2;
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

	/**
	 * 加载没有安排座位的考生
	 * 
	 * @param xmId
	 * @param odepts
	 */
	private void loadKs(String xmId, String... odepts) {
		// 所有考生
		SqlBean sql = new SqlBean().and("a.XM_ID", xmId).and("a.BM_CODE", "p.PERSON_ID").and("a.S_FLAG", 1);

		sql.selects(" a.*,p.STATION_TYPE,p.STATION_TYPE_CODE,p.STATION_NO,p.STATION_NO_CODE ");

		sql.andIn("a.BM_STATUS", 0, 1);// 1 借考 2请假 3 1+2

		if (odepts != null && odepts.length > 0) {

			sql.andIn("a.S_ODEPT", odepts);
		}

		sql.tables(TsConstant.SERV_BMSH_PASS + " a, SY_HRM_ZDSTAFFPOSITION p");

		String notExistsSql = " select * from " + TsConstant.SERV_KCAP_YAPZW + "b where b.SH_ID = a.SH_ID";

		sql.appendWhere(" AND NOT EXSITS (" + notExistsSql + ")");

		// ksList = ServDao.finds(TsConstant.SERV_BMSH_PASS, sql);

		ksList = Transaction.getExecutor().query(sql.toString(), sql.getVars());

		ksList2Bean();

	}

	/**
	 * 加载考场及场次信息
	 * 
	 * @param xmId
	 * @param odepts
	 */
	private void loadKc(String xmId, String... odepts) {

		List<Object> values = new ArrayList<>();

		values.add(xmId);

		String sql = "select k.KC_ID,K.KC_ODEPTCODE,K.KC_ODEPTNAME,k.KC_SCORE,k.KC_STATE,k.KC_MAX,k.KC_GOOD,k.KC_LEVEL,c.CC_ID,c.XM_ID from TS_XMGL_KCAP_DAPCC c LEFT JOIN ts_kcgl k on k.kc_id = c.kc_id where a.XM_ID=? ";

		if (odepts != null && odepts.length > 0) {

			sql += " and k.KC_ODEPTCODE in (" + Strings.toString(odepts) + ")";

		}

		// 所有考场
		kcList = Transaction.getExecutor().query(sql, values);

		kcList2Bean();

	}

	/**
	 * 考生list转bean,key考生机构
	 */
	private void ksList2Bean() {
		jkList = new ArrayList<Bean>();

		ksBean = new Bean();

		for (Bean pass : ksList) {

			String jkoCode = pass.getStr("JK_ODEPT");

			int bmStatus = pass.getInt("BM_STATUS");

			String oDCode = pass.getStr("S_ODEPT");

			List<Bean> tempList = null;

			if (bmStatus == 0) {

				if (ksBean.containsKey(oDCode)) {

					tempList = ksBean.getList(ksBean);

					tempList.add(pass);

				} else {

					tempList = new ArrayList<Bean>();

					tempList.add(pass);
				}

				if (tempList != null) {

					ksBean.set(oDCode, tempList);
				}

			} else if (bmStatus == 1) { // 借考

				jkList.add(pass);

				if (jkBean.containsKey(jkoCode)) {

					tempList = jkBean.getList(jkBean);

					tempList.add(pass);

				} else {

					tempList = new ArrayList<Bean>();

					tempList.add(pass);
				}

				if (tempList != null) {

					jkBean.set(jkoCode, tempList);
				}

			} else { // 2请假, 3请假+借考

			}
		}

		ksList.remove(jkList); // 去掉借考考生

	}

	/**
	 * 考场 基本信息，关联机构，座位号，场次 list转bean, key 考场所属机构 value 考场信息list
	 */
	private void kcList2Bean() {

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
	 * 组装考场座位号
	 */
	private void loadZwh(String xmId, String... odepts) {

		Bean already = getAlreadyZw(xmId, odepts);

		zwapBean = new Bean();

		for (Object key : kcBean.entrySet()) { // 所有机构下的考场bean

			List<Bean> kcinfoList = kcBean.getList(key); // 某所属机构下的考场list

			Bean zwhBeanVal = new Bean();

			for (Bean kcInfo : kcinfoList) {

				String kcId = kcInfo.getBean("INFO").getStr("KC_ID"); // 考场id

				List<Bean> zwhList = kcInfo.getList("ZWH");

				List<Bean> ccList = kcInfo.getList("CC");

				if (distRule == 1) { // 场次优先 zwhBean{场次id : zwhBeanVal{考场id:座位}}

					for (Bean cc : ccList) {// SJ_ID,CC_ID,SJ_CC,SJ_START,SJ_END

						String sjCc = cc.getStr("SJ_CC"); // 场次号

						if (!zwhBeanVal.containsKey(kcId)) {

							Bean zwBean = getZwBean(already, zwhList, kcId, sjCc);

							zwhBeanVal.set(kcId, zwBean);
						}

						zwapBean.set(cc.getStr("SJ_CC"), zwhBeanVal);
					}

				} else { // 考场优先 zwhBean{ 考场id : zwhBeanVal{场次id: 座位}}

					zwhBeanVal = new Bean();

					for (Bean cc : ccList) {// SJ_ID,CC_ID,SJ_CC,SJ_START,SJ_END

						String sjCc = cc.getStr("SJ_CC"); // 场次号

						Bean zwBean = getZwBean(already, zwhList, kcId, sjCc);

						zwhBeanVal.set(sjCc, zwBean);

					}

					zwapBean.set(kcId, zwhBeanVal);
				}

			}
		}

	}

	/**
	 * 过滤已安排座位，只保留未安排座位
	 * @param already 已安排座位bean
	 * @param zwhList 座位list
	 * @param kcId 考场id
	 * @param sjCc 场次号
	 * @return
	 */
	private Bean getZwBean(Bean already, List<Bean> zwhList, String kcId, String sjCc) {

		Bean zwBean = new Bean(); // 考场座位bean {系统座位号：zwbean}

		for (Bean zw : zwhList) {

			String zwh = zw.getStr("ZW_ZWH_XT"); // 座位号

			if (!already.containsKey(kcId + "^" + sjCc + "^" + zwh)) { // 判断座位是否已安排,只取未安排座位

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

					List<Bean> jgksList = null;

					if (jg.getInt("JG_FAR") == 1) { // 关联考场距离远的机构

						jgksList = ksBean.getList(jg.getStr("JG_CODE"));// 关联机构下的考生

						for (Bean jgks : jgksList) {

							String shId = jgks.getStr("SH_ID");

							String userCode = jgks.getStr("BM_CODE");

							if (farksBeanVal.containsKey(userCode)) { // 考生已存在

								String shIds = farksBeanVal.getStr(userCode);

								shIds += "," + shId;

								farksBeanVal.set(userCode, shIds);

							} else {

								farksBeanVal.set(userCode, shId);
							}
						}
					}
				}
				farKsBean.set(kcId, farksBeanVal);
			}
		}
	}

	/**
	 * 获取已安排座位
	 * 
	 * @param xmId
	 * @param odepts
	 */
	private Bean getAlreadyZw(String xmId, String... odepts) {

		Bean already = new Bean();

		SqlBean sql = new SqlBean().and("a.XM_ID", xmId);

		if (odepts != null && odepts.length > 0) {

			sql.andIn("U_ODEPT", odepts);
		}

		List<Bean> alreadyZwList = ServDao.finds(TsConstant.SERV_KCAP_YAPZW, sql);

		for (Bean zw : alreadyZwList) {

			String kcId = zw.getStr("KC_ID"); // 考场
			String sjCc = zw.getStr("SJ_CC"); // 场次
			String zwh = zw.getStr("ZW_XT"); // 座位id

			already.set(kcId + "^" + sjCc + "^" + zwh, zw.getStr("ZW_ID"));
		}

		return already;

	}

	public Bean getRuleBean() {
		return ruleBean;
	}

	public Bean getKcBean() {
		return kcBean;
	}

	public Bean getKsBean() {
		return ksBean;
	}

	public void setDistRule(int distRule) {
		this.distRule = distRule;
	}

	public int getPriority() {
		return priority;
	}

}
