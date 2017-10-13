package com.rh.ts.xmgl.kcap;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

	private static Log log = LogFactory.getLog(KcapResource.class);

	private Bean kcFree = null;

	private Bean kcBusy = null;

	private Bean ksFree = null;

	private Bean ksBusy = null;

	private Bean leaderBean = null;

	private Bean farKsBean = null;

	/**
	 * 引入规则
	 */
	private Bean ruleBean = null;

	/**
	 * 考生list
	 */
	private List<Bean> passList = null;

	/**
	 * 借考考生list
	 */
	private List<Bean> jkList = null;

	/**
	 * 考场list
	 */
	private List<Bean> kcList = null;

	/**
	 * 考场 key:机构 value:bean
	 */
	private Bean kcBean = null;

	/**
	 * 所有考生 key:考生机构 value:list
	 */
	private Bean ksBean = null;

	/**
	 * 机构 key:考生机构，value:下级部门
	 */
	private Bean ksOrg = null;

	public KcapResource(String xmId) {

		// 考场信息
		loadKc(xmId, "");
		// 考生信息
		loadKs(xmId, "");

		loadKcFree();

		loadKsFree();

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

		} else {
			// 考场信息
			loadKc(xmId, "");
			// 考生信息
			loadKs(xmId, "");
		}

		loadKcFree();

		loadKsFree();

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

			if (KcapRuleEnum.R003.equals(ruleCode)) {

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

			} else if (KcapRuleEnum.R007.equals(ruleCode)) {

				leaderBean = new Bean();

			} else if (KcapRuleEnum.R008.equals(ruleCode)) {

			} else if (KcapRuleEnum.R009.equals(ruleCode)) {

			}
		}

	}

	private void loadKs(String xmId, String... odepts) {

		SqlBean sql = new SqlBean().and("XM_ID", xmId).andIn("S_ODEPT", odepts).and("S_FLAG", 1);

		passList = ServDao.finds(TsConstant.SERV_BMSH_PASS, sql);

		sql = new SqlBean().and("XM_ID", xmId).andIn("JK_ODEPT", odepts).and("S_FLAG", 1);

		jkList = ServDao.finds(TsConstant.SERV_BMSH_PASS, sql);

		ksList2Bean();

	}

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

		ksOrg = new Bean();

		ksBean = new Bean();

		for (Bean pass : passList) {

			String oDCode = pass.getStr("S_ODEPT");

			List<Bean> ksList = null;

			if (ksBean.containsKey(oDCode)) {

				ksList = ksBean.getList(ksBean);

				ksList.add(pass);

			} else {

				ksList = new ArrayList<Bean>();

				ksList.add(pass);
			}

			if (ksList != null) {

				ksBean.set(oDCode, ksList);
			}
		}

	}

	private void kcList2Bean() {

		kcBean = new Bean();

		for (Bean item : kcList) {

			String ccId = item.getStr("CC_ID");

			String kcId = item.getStr("KC_ID");

			String odept = item.getStr("KC_ODEPTCODE");

			// 场次
			SqlBean ccsql = new SqlBean().and("CC_ID", ccId).and("S_FLAG", 1).selects("SJ_CC,SJ_START,SJ_END");
			List<Bean> ccList = ServDao.finds("TS_XMGL_KCAP_DAPCC_CCSJ", ccsql);

			// 关联机构
			SqlBean jgsql = new SqlBean().and("KC_ID", kcId).and("S_FLAG", 1).selects("KC_ID,JG_CODE,JG_NAME,JG_FAR");
			List<Bean> jgList = ServDao.finds("TS_KCGL_GLJG", jgsql);

			// 座位号
			SqlBean zwsql = new SqlBean().and("KC_ID", kcId).and("S_FLAG", 1).selects("ZW_ZWH_XT,ZW_ZWH_SJ,ZW_KY");
			List<Bean> zwList = ServDao.finds("TS_KCGL_ZWDYB", zwsql);

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

	private void loadKcFree() {

	}

	private void loadKsFree() {

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

	public Bean getKsOrg() {
		return ksOrg;
	}

	public Bean getKcFree() {
		return kcFree;
	}

	public void setKcFree(Bean kcFree) {
		this.kcFree = kcFree;
	}

	public Bean getKcBusy() {
		return kcBusy;
	}

	public void setKcBusy(Bean kcBusy) {
		this.kcBusy = kcBusy;
	}

	public Bean getKsFree() {
		return ksFree;
	}

	public void setKsFree(Bean ksFree) {
		this.ksFree = ksFree;
	}

	public Bean getKsBusy() {
		return ksBusy;
	}

	public void setKsBusy(Bean ksBusy) {
		this.ksBusy = ksBusy;
	}

}
