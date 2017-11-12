package com.rh.ts.xmgl.kcap.arrange;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Strings;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.kcap.KcapResource;
import com.rh.ts.xmgl.kcap.KcapRuleEnum;
import com.rh.ts.xmgl.kcap.utils.KcapUtils;

public class ArrangeSeat {

	private static Log log = LogFactory.getLog(ArrangeSeat.class);

	private static boolean showlog = false;

	public void doArrange(KcapResource res) {

		int priority = res.getKsPriority(); // 0 最少考场 1 最少场次

		try {
			// Transaction.begin();

			if (priority == 1) { // 最少场次

				arrangeCc(res, false);

			} else { // 最少考场

				arrangeKc(res, false);
			}

			// Transaction.commit();

		} catch (Exception e) {
			log.error(e);
		} finally {
			// Transaction.end();
		}

		int ksConstrain = res.getKsConstrain(); // 不符合规则考试 是否强制安排

		if (ksConstrain == 1) { // 强制安排

			try {
				Transaction.begin();

				if (priority == 1) { // 最少场次

					arrangeCc(res, true);

				} else { // 最少考场

					arrangeKc(res, true);
				}

				Transaction.commit();

			} catch (Exception e) {
				log.error(e);
			} finally {
				Transaction.end();
			}
		}

		// 考生人数少于机器数一半时，考生左右间隔不低于1个座位，前后不低于1个
		if (res.getRuleBean().containsKey(KcapRuleEnum.R006.getCode())) {

			arrangeR006(res);
		}
	}

	/**
	 * 安排座位 最少场次-场次优先
	 * 
	 * @param res
	 */
	private void arrangeCc(KcapResource res, boolean isConstrain) {

		Bean freeBean = new Bean();

		freeBean.putAll(res.getFreeCcZwBean());

		int ccsort[] = KcapUtils.sortInt(freeBean); // 场次号排序

		for (int cs = 0; cs < ccsort.length; cs++) {

			String cc = String.valueOf(ccsort[cs]);

			if (showlog)
				log.error("-----最少场次---第" + cc + "场");

			Bean freeCc = freeBean.getBean(cc);

			int sjCC = Integer.parseInt(cc.toString());

			String daysort[] = KcapUtils.sortStr(freeCc); // 日期排序

			for (int ds = 0; ds < daysort.length; ds++) {

				String day = daysort[ds];

				if (showlog)
					log.error("----------最少场次---第" + cc + "场 " + day);

				Bean freeDayCc = (Bean) freeCc.getBean(day).clone();

				String date = day.toString();

				Map<String, Bean> kcsort = KcapUtils.sortKc(freeDayCc, res); // 考场排序

				for (Object kc : kcsort.keySet()) { // 遍历考场

					Bean sortBean = (Bean) kcsort.get(kc);

					String kcId = sortBean.getStr("KC_ID");

					if (showlog)
						log.error("--------------------最少场次---第" + cc + "场 " + day + " 考场:" + kcId);

					Bean freeKc = (Bean) freeDayCc.getBean(kcId).clone();

					Bean odeptKs = res.getGljgKs(kcId);

					String zwsort[] = KcapUtils.sortStr(freeKc); // 座位排序

					for (int zs = 0; zs < zwsort.length; zs++) {

						String zwKey = zwsort[zs];

						if (freeKc.get(zwKey) instanceof Bean) {

							Bean freeZw = (Bean) freeKc.getBean(zwKey).clone(); // 座位信息

							freeZw.set("KC_ID", kcId);

							freeZw.set("CC_ID", freeKc.getStr("CC_ID"));

							freeZw.set("SJ_ID", freeKc.getStr("SJ_ID"));

							freeZw.set("SJ_CC", sjCC); // 场次 1,2,3...

							freeZw.set("SJ_DATE", date); // 考试日期 yyyy-mm-dd

							freeZw.set("GLJG", res.getGljg(kcId));// 关联机构

							arrangeAndSave(freeZw, odeptKs, res, isConstrain);
						}
					}
				}
			}
		}

	}

	/**
	 * 安排座位 最少考场-考场优先
	 * 
	 * @param res
	 */
	private void arrangeKc(KcapResource res, boolean isConstrain) {

		Bean freeBean = new Bean(res.getFreeKcZwBean());

		Map<String, Bean> kcsort = KcapUtils.sortKc(freeBean, res); // 考场排序

		for (Object kc : kcsort.keySet()) { // 遍历考场

			Bean sortBean = (Bean) kcsort.get(kc);

			String kcId = sortBean.getStr("KC_ID");

			if (showlog)
				log.error("----------最少考场---考场:" + kcId);

			Bean freeKc = (Bean) freeBean.getBean(kcId).clone();

			Bean odeptKs = res.getGljgKs(kcId);

			int ccsort[] = KcapUtils.sortInt(freeKc); // 场次号排序

			for (int cs = 0; cs < ccsort.length; cs++) {// 遍历场次号

				String cc = String.valueOf(ccsort[cs]);

				if (showlog)
					log.error("----------最少考场---考场:" + kcId + " 第" + cc + "场 ");

				Bean freeCc = (Bean) freeKc.getBean(cc).clone();

				int sjCC = Integer.parseInt(cc.toString());

				String daysort[] = KcapUtils.sortStr(freeCc); // 日期排序

				for (int ds = 0; ds < daysort.length; ds++) {// 遍历场次日期

					String day = daysort[ds];

					if (showlog)
						log.error("--------------------最少考场---考场:" + kcId + " 第" + cc + "场 " + day);

					Bean freeDayCc = (Bean) freeCc.getBean(day).clone();

					String date = day.toString();

					String zwsort[] = KcapUtils.sortStr(freeDayCc); // 座位排序

					for (int zs = 0; zs < zwsort.length; zs++) {// 遍历座位

						String zwKey = zwsort[zs];

						if (freeDayCc.get(zwKey) instanceof Bean) {

							Bean freeZw = (Bean) freeDayCc.getBean(zwKey).clone(); // 座位信息

							freeZw.set("KC_ID", kcId);

							freeZw.set("CC_ID", freeDayCc.getStr("CC_ID"));

							freeZw.set("SJ_ID", freeDayCc.getStr("SJ_ID"));

							freeZw.set("SJ_CC", sjCC); // 场次 1,2,3...

							freeZw.set("SJ_DATE", date); // 考试日期 yyyy-mm-dd

							freeZw.set("GLJG", res.getGljg(kcId));// 关联机构

							arrangeAndSave(freeZw, odeptKs, res, isConstrain);
						}
					}
				}
			}
		}
	}

	private void arrangeAndSave(Bean freeZw, Bean odeptKs, KcapResource res, boolean isConstrain) {

		boolean isNull = true;

		for (Object time : odeptKs.keySet()) {

			if (!odeptKs.getBean(time).isEmpty()) {

				isNull = false;
			}
		}

		if (isNull) {
			return;
		}

		if (showlog) {
			log.error("安排前：odeptKs");

			for (Object key : odeptKs.keySet()) {

				log.error("----" + key.toString() + ":" + odeptKs.getBean(key).size());
			}
		}

		Bean oneKs = KcapMatch.matchUser(freeZw, odeptKs, res, isConstrain);// 符合座位规则的考生

		try {

			if (oneKs != null && !oneKs.isEmpty()) {

				if (showlog)
					log.error("--------安排考生:" + oneKs.getStr("BM_CODE") + ",shId:" + oneKs.getStr("SH_ID") + ",ksTime:"
							+ oneKs.getStr("BM_KS_TIME"));

				saveRes(freeZw, oneKs, odeptKs, res);

			} else {
				if (showlog)
					log.error("----没有符合规则考生");
			}

			if (showlog) {
				log.error("安排后：odeptKs");

				for (Object key : odeptKs.keySet()) {

					log.error("----" + key.toString() + ":" + odeptKs.getBean(key).size());
				}
			}

		} catch (Exception e) {

			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 考生人数少于机器数一半时重新安排座位
	 * 
	 * @param res
	 */
	private void arrangeR006(KcapResource res) {

		int priority = res.getKsPriority();

		Bean busyZwClone = (Bean) res.getBusyZwBean().clone();

		for (Object kcKey : busyZwClone.keySet()) { // 遍历已安排考场

			Bean resetFiltKs = new Bean(); // 待重新安排的考生

			// key 考场Id^场次^日期
			String[] keyArray = kcKey.toString().split("\\^");

			String kcId = keyArray[0];

			String cc = keyArray[1];

			String date = keyArray[2];

			String sjId = "";
			String ccId = "";

			Bean busyZwKc = busyZwClone.getBean(kcKey);// 考场下的所有考生座位安排

			int busyZwSize = busyZwKc.size();

			int freeZwSize = 0;

			if (priority == 1) { // 最少场次

				freeZwSize = res.getFreeCcZwBean().getBean(cc).getBean(date).getBean(kcId).size();

			} else {

				freeZwSize = res.getFreeKcZwBean().getBean(kcId).getBean(cc).getBean(date).size();
			}

			if (busyZwSize < freeZwSize) { // 安排考生人数少于机器数一半

				// 将当前考场的考生安排 从 已安排资源移除
				res.getBusyZwBean().remove(kcKey);

				for (Object zwhKey : busyZwKc.keySet()) { // 遍历座位安排信息

					Bean busyZw = busyZwKc.getBean(zwhKey);

					busyZw.set("BM_CODE", busyZw.getStr("U_CODE"));
					busyZw.set("BM_STATUS", busyZw.getStr("U_TYPE"));
					busyZw.set("S_ODEPT", busyZw.getStr("U_ODEPT"));
					busyZw.set("JK_ODEPT", busyZw.getStr("U_ODEPT"));
					busyZw.set("BM_LB_CODE", busyZw.getStr("BM_LB"));
					busyZw.set("BM_XL_CODE", busyZw.getStr("BM_XL"));
					busyZw.set("BM_MK_CODE", busyZw.getStr("BM_MK"));
					busyZw.set("BM_TYPE", busyZw.getStr("BM_LV"));

					if (Strings.isBlank(sjId) || Strings.isBlank(ccId)) {

						sjId = busyZw.getStr("SJ_ID");
						ccId = busyZw.getStr("CC_ID");
					}

					if (priority == 1) { // 最少场次

						res.getFreeCcZwBean().getBean(cc).getBean(date).getBean(kcId).set(zwhKey, busyZw);// 将当前座位添加至空闲座位资源

					} else { // 最少考场

						res.getFreeKcZwBean().getBean(kcId).getBean(cc).getBean(date).set(zwhKey, busyZw);// 将当前座位添加至空闲座位资源
					}

					Bean temp = new Bean();

					temp.set(busyZw.getStr("U_CODE"), busyZw);

					String time = busyZw.getStr("BM_KS_TIME");// 考试时长

					if (resetFiltKs.containsKey(time)) {
						// 待重新安排的考生资源
						resetFiltKs.set(time, KcapUtils.mergeBean(temp, resetFiltKs.getBean(time)));

					} else {
						// 待重新安排的考生资源
						resetFiltKs.set(time, temp);
					}
				} // busyZwKc.keySet()

				try {

					SqlBean sql = new SqlBean().and("XM_ID", res.getXmId()).and("KC_ID", kcId);
					sql.and("SJ_CC", cc).and("SJ_DATE", date);

					ServDao.delete(TsConstant.SERV_KCAP_YAPZW, sql);

					Bean resetKc = new Bean(); // 待重新安排的考场

					if (priority == 1) { // 最少场次

						resetKc = (Bean) res.getFreeCcZwBean().getBean(cc).getBean(date).getBean(kcId).clone();
					} else {
						resetKc = (Bean) res.getFreeKcZwBean().getBean(kcId).getBean(cc).getBean(date).clone();
					}

					String zwsort[] = KcapUtils.sortStr(resetKc); // 座位排序

					for (int zs = 0; zs < zwsort.length; zs++) {// 遍历座位

						String key = zwsort[zs];

						if (resetKc.get(key) instanceof Bean) {

							Bean resetZw = (Bean) resetKc.getBean(key).clone();

							if (Strings.isBlank(resetZw.getStr("ZW_ZWH_XT"))) {
								resetZw.set("ZW_ZWH_XT", resetZw.getStr("ZW_XT"));
							}

							resetZw.set("KC_ID", kcId);

							resetZw.set("SJ_CC", cc);

							resetZw.set("SJ_DATE", date);

							resetZw.set("SJ_ID", sjId);

							resetZw.set("CC_ID", ccId);

							Bean tempFiltBean = KcapMatch.filtR006(resetZw, res.getBusyZwBean(), resetFiltKs.copyOf());

							if (tempFiltBean != null && !tempFiltBean.isEmpty()) {

								Bean resetOneKs = KcapMatch.randomFiltBean(tempFiltBean);

								try {

									if (resetOneKs != null && !resetOneKs.isEmpty()) {
										// 保存更新 考场、考生的信息
										saveRes(resetZw, resetOneKs, resetFiltKs, res);
									}

								} catch (Exception e) {

									log.error(e.getMessage(), e);
								}
							}
						}
					}
				} catch (Exception e) {

					log.error(e);
				}
			} // busyZwSize < freeZwSize
		}
	}

	/**
	 * 安排考生和座位并且重置资源
	 * 
	 * @param freeZw
	 *            //安排座位
	 * @param oneKs
	 *            //安排考生
	 * @param odeptKs//未安排所有考生
	 * @param res
	 *            //资源
	 */
	private void saveRes(Bean freeZw, Bean oneKs, Bean odeptKs, KcapResource res) {

		String kcId = freeZw.getStr("KC_ID"); // 考场id

		String cc = freeZw.getStr("SJ_CC"); // 场次

		String date = freeZw.getStr("SJ_DATE"); // 考试日期

		String zwh = freeZw.getStr("ZW_ZWH_XT"); // 座位号

		if (oneKs != null && !oneKs.isEmpty()) {

			int priority = res.getKsPriority(); // 0 最少考场优先 1 最少场次优先

			String shId = oneKs.getStr("SH_ID"); // 审核id

			String uCode = oneKs.getStr("BM_CODE"); // 考生编码

			String ksTime = oneKs.getStr("BM_KS_TIME");// 考试时长

			int bmStatus = oneKs.getInt("BM_STATUS");

			String ksOdept = oneKs.getStr("S_ODEPT"); // 考生机构

			if (bmStatus == 1) {

				ksOdept = oneKs.getStr("JK_ODEPT"); // 借考机构
			}

			// 保存座位安排
			Bean addZw = addArrange(freeZw, oneKs, odeptKs, res.getJkKsBean().getBean(ksOdept));

			if (!Strings.isBlank(addZw.getId())) {

				// 移除考场资源
				if (priority == 1) { // 最少场次

					res.getFreeCcZwBean().getBean(cc).getBean(date).getBean(kcId).remove(zwh);

				} else { // 最少考场

					res.getFreeKcZwBean().getBean(kcId).getBean(cc).getBean(date).remove(zwh);
				}

				if (showlog)
					log.error("------------移除res考生资源");
				Bean ksBean = res.getFreeKsBean().getBean(ksOdept).getBean(ksTime);
				// 移除考生资源
				removeKs(ksBean, uCode, shId);

				if (showlog)
					log.error("------------移除odp考生资源");
				Bean odeptTimeks = odeptKs.getBean(ksTime);
				// 从过滤考生(odeptKs)中移除
				removeKs(odeptTimeks, uCode, shId);

				Bean busyKc = res.getBusyZwBean().getBean(kcId + "^" + cc + "^" + date);

				busyKc.set(zwh, addZw);

				res.getBusyZwBean().set(kcId + "^" + cc + "^" + date, busyKc);

			} else {
				log.error("添加失败--保存失败--" + kcId + "^" + cc + "^" + date + "^" + zwh);
			}
		}
	}

	private static Bean addArrange(Bean freeZw, Bean oneKs, Bean odeptKs, Bean jkKs) {

		String shId = oneKs.getStr("SH_ID"); // 审核id

		String uCode = oneKs.getStr("BM_CODE"); // 考生编码

		String ksTime = oneKs.getStr("BM_KS_TIME");// 考试时长

		int bmStatus = oneKs.getInt("BM_STATUS");

		String ksOdept = oneKs.getStr("S_ODEPT"); // 考生机构

		if (bmStatus == 1) {
			ksOdept = oneKs.getStr("JK_ODEPT"); // 借考机构
		}

		String xmId = oneKs.getStr("XM_ID");

		String lb = oneKs.getStr("BM_LB_CODE");

		String xl = oneKs.getStr("BM_XL_CODE");

		String mk = oneKs.getStr("BM_MK_CODE");

		String lv = oneKs.getStr("BM_TYPE");

		String kcId = freeZw.getStr("KC_ID"); // 考场id

		String cc = freeZw.getStr("SJ_CC"); // 场次

		String date = freeZw.getStr("SJ_DATE"); // 考试日期

		String zwh = freeZw.getStr("ZW_ZWH_XT"); // 座位号

		String sjId = freeZw.getStr("SJ_ID");

		String zwId = freeZw.getStr("ZW_ID");

		String ccId = freeZw.getStr("CC_ID");

		// 添加已安排考场资源
		Bean addZw = new Bean();

		addZw.set("SH_ID", shId);
		addZw.set("ZW_ID", zwId);
		addZw.set("ZW_XT", zwh);
		addZw.set("SJ_ID", sjId);
		addZw.set("SJ_CC", cc);
		addZw.set("SJ_DATE", date);
		addZw.set("CC_ID", ccId);
		addZw.set("KC_ID", kcId);
		addZw.set("XM_ID", xmId);
		addZw.set("BM_LB", lb);
		addZw.set("BM_XL", xl);
		addZw.set("BM_MK", mk);
		addZw.set("BM_LV", lv);
		addZw.set("BM_KS_TIME", ksTime);
		addZw.set("U_ODEPT", ksOdept);
		addZw.set("U_CODE", uCode);

		// Bean jkKs = res.getJkKsBean().getBean(ksOdept);

		if (jkKs.containsKey(uCode)) { // 判断是否借考

			Object jkKsObj = jkKs.get(uCode);

			if (jkKsObj instanceof Bean) {

				Bean ks = jkKs.getBean(uCode);

				if (ks.getStr("BM_LB_CODE").equals(lb) && ks.getStr("BM_XL_CODE").equals(xl)
						&& ks.getStr("BM_MK_CODE").equals(mk) && ks.getStr("BM_TYPE").equals(lv)
						&& ks.getStr("XM_ID").equals(xmId)) {

					addZw.set("U_TYPE", 1);
				}

			} else if (jkKsObj instanceof List) {

				List<Bean> list = jkKs.getList(uCode);

				for (Bean ks : list) {

					if (ks.getStr("BM_LB_CODE").equals(lb) && ks.getStr("BM_XL_CODE").equals(xl)
							&& ks.getStr("BM_MK_CODE").equals(mk) && ks.getStr("BM_TYPE").equals(lv)
							&& ks.getStr("XM_ID").equals(xmId)) {

						addZw.set("U_TYPE", 1);
						break;
					}
				}
			}
		}

		try {
			// Transaction.begin();

			addZw = ServDao.create(TsConstant.SERV_KCAP_YAPZW, addZw);

			// log.error("insert---" + addZw.getId());

			// Transaction.commit();

		} catch (Exception e) {
			log.error(e);
		} finally {
			// Transaction.end();
		}

		return addZw;
	}

	/**
	 * 移除 考生信息
	 * 
	 * @param ksBean
	 *            待移除bean
	 * @param ksTime
	 *            考试时长
	 * @param uCode
	 *            考生编码
	 * @param shId
	 *            考生审核ID
	 */
	private void removeKs(Bean ksBean, String uCode, String shId) {

		Object ksObj = ksBean.get(uCode);

		if (ksObj == null) {
			if (showlog)
				log.error("--------------------删除考生--uCode:" + uCode + "考生不存在");
		}

		if (ksObj instanceof Bean) {

			if (showlog)
				log.error(
						"--------------------删除考生--uCode:" + uCode + "--shId:" + ksBean.getBean(uCode).getStr("SH_ID"));

			ksBean.remove(uCode);

		} else if (ksObj instanceof List) {

			List<Bean> ksList = ksBean.getList(uCode);

			List<Bean> tempList = new ArrayList<Bean>();

			for (Bean ks : ksList) {

				if (!ks.getStr("SH_ID").equals(shId)) {

					tempList.add(ks);

				} else {
					if (showlog)
						log.error("--------------------删除考生--uCode:" + uCode + "--shId:" + ks.getStr("SH_ID"));
				}
			}

			if (tempList.size() > 0) {

				ksBean.remove(uCode);

				if (tempList.size() == 1) { // 当前考生还有一个考试

					if (showlog)
						log.error("--------------------保留考生Bean--uCode:" + uCode);

					ksBean.set(uCode, tempList.get(0)); // 放入未安排考生资源

				} else if (tempList.size() > 1) { // 当前考生还有多个考试

					if (showlog)
						log.error("--------------------保留考生List--uCode:" + uCode + ":" + tempList.size());

					ksBean.set(uCode, tempList); // 放入未安排考生资源
				}
			}
		}
	}
}
