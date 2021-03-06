package com.rh.ts.xmgl.kcap.arrange;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.comm.ConfMgr;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Strings;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.kcap.KcapResource;
import com.rh.ts.xmgl.kcap.KcapRuleEnum;
import com.rh.ts.xmgl.kcap.utils.KcapUtils;

public class ArrangeSeat {

	private static Log log = LogFactory.getLog(ArrangeSeat.class);

	private boolean isFiltTime = ConfMgr.getConf("TEST_KCAP_FILT_TIME", true);

	private boolean isRepeatUser = ConfMgr.getConf("TEST_KCAP_REPEAT_USER", true);

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

			log.debug("|||||||||||||||||||||||||||||||||--强制安排--||||||||||||||||||||||||||||||||||");

			try {
				// Transaction.begin();

				if (priority == 1) { // 最少场次

					arrangeCc(res, true);

				} else { // 最少考场

					arrangeKc(res, true);
				}

				// Transaction.commit();

			} catch (Exception e) {
				log.error(e);
			} finally {
				// Transaction.end();
			}
		}

		// 考生人数少于机器数一半时，考生左右间隔不低于1个座位，前后不低于1个
		if (res.getRuleBean().containsKey(KcapRuleEnum.R006.getCode())) {

			log.debug("|||||||||||||||||||||||||||||||||--人数少于机器数一半时--||||||||||||||||||||||||||||||||||");

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

		for (int cs = 0; cs < ccsort.length; cs++) { // 遍历场次

			String cc = String.valueOf(ccsort[cs]);

			Bean freeCc = freeBean.getBean(cc);

			int sjCC = Integer.parseInt(cc.toString());

			String daysort[] = KcapUtils.sortStr(freeCc); // 日期排序

			for (int ds = 0; ds < daysort.length; ds++) { // 遍历日期

				String day = daysort[ds];

				Bean freeDayCc = (Bean) freeCc.getBean(day).clone();

				String date = day.toString();

				String kcsort[] = KcapUtils.sortKcId(freeDayCc, res); // 考场排序

				for (int kc = 0; kc < kcsort.length; kc++) { // 遍历考场

					String kcId = kcsort[kc];

					Bean freeKc = (Bean) freeDayCc.getBean(kcId).clone();

					String kcName = freeKc.getStr("KC_NAME");

					String ksTime = freeKc.getStr("SJ_TIME");

					String kcLv = freeKc.getStr("KC_LV");

					String zwsort[] = KcapUtils.sortZwStr(freeKc); // 座位排序

					for (int zs = 0; zs < zwsort.length; zs++) { // 遍历座位

						String zwKey = zwsort[zs];

						if (freeKc.get(zwKey) instanceof Bean) {

							Bean freeZw = (Bean) freeKc.getBean(zwKey).clone(); // 座位信息

							freeZw.set("KC_ID", kcId);

							freeZw.set("CC_ID", freeKc.getStr("CC_ID"));

							freeZw.set("SJ_ID", freeKc.getStr("SJ_ID"));

							freeZw.set("SJ_CC", sjCC); // 场次 1,2,3...

							freeZw.set("SJ_DATE", date); // 考试日期 yyyy-mm-dd

							freeZw.set("SJ_TIME", ksTime);

							freeZw.set("KC_LV", kcLv);

							Bean odeptKs = res.getGljgKs(kcId);

							log.debug("------begin-安排考位-|" + date + "|" + sjCC + "|" + freeZw.getStr("ZW_ZWH_XT") + "|"
									+ kcId + "|" + kcName);

							KcapUtils.showInfo(odeptKs, "------------1-|机构下总报考人数-报名考生人数|", this.getClass());

							if (isFiltTime) {

								// 根据考试时长筛选考生
								KcapMatch.filtKsTime(freeZw, odeptKs);

								KcapUtils.showInfo(odeptKs, "------------2-|过滤考试时长后-报名考生人数|", this.getClass());
							}

							if (isRepeatUser) {

								// 过滤 相同日期,相同场次的考生
								KcapMatch.filtKsSameCc(freeZw, res.getBusyZwBean(), odeptKs);

								KcapUtils.showInfo(odeptKs, "------------3-|过滤相同日期和场次考生后-报名考生人数|", this.getClass());
							}

							try {
								Bean oneKs = KcapMatch.matchUser(freeZw, odeptKs, res, isConstrain);// 符合座位规则的考生

								if (oneKs != null && !oneKs.isEmpty()) {

									log.debug("------------4-|成功找到安排考生-:" + oneKs.toString());

									saveRes(freeZw, oneKs, odeptKs, res);
								}

							} catch (Exception e) {

								log.error(e.getMessage(), e);
							}
						}
					} // 座位
				} // 考场
			} // 考试日期
		} // 场次
	}

	/**
	 * 安排座位 最少考场-考场优先
	 *
	 * @param res
	 */
	private void arrangeKc(KcapResource res, boolean isConstrain) {

		Bean freeBean = new Bean(res.getFreeKcZwBean());

		String kcsort[] = KcapUtils.sortKcId(freeBean, res); // 考场排序

		for (int kc = 0; kc < kcsort.length; kc++) { // 遍历考场

			String kcId = kcsort[kc];

			Bean freeKc = (Bean) freeBean.getBean(kcId).clone();

			int ccsort[] = KcapUtils.sortInt(freeKc); // 场次号排序

			for (int cs = 0; cs < ccsort.length; cs++) {// 遍历场次号

				String cc = String.valueOf(ccsort[cs]);

				Bean freeCc = (Bean) freeKc.getBean(cc).clone();

				int sjCC = Integer.parseInt(cc.toString());

				String daysort[] = KcapUtils.sortStr(freeCc); // 日期排序

				for (int ds = 0; ds < daysort.length; ds++) {// 遍历场次日期

					String day = daysort[ds];

					Bean freeDayCc = (Bean) freeCc.getBean(day).clone();

					String ksTime = freeDayCc.getStr("SJ_TIME");

					String kcName = freeDayCc.getStr("KC_NAME");

					String kcLv = freeKc.getStr("KC_LV");

					String date = day.toString();

					String zwsort[] = KcapUtils.sortZwStr(freeDayCc); // 座位排序

					for (int zs = 0; zs < zwsort.length; zs++) {// 遍历座位

						String zwKey = zwsort[zs];

						if (freeDayCc.get(zwKey) instanceof Bean) {

							Bean freeZw = (Bean) freeDayCc.getBean(zwKey).clone(); // 座位信息

							freeZw.set("KC_ID", kcId);

							freeZw.set("CC_ID", freeDayCc.getStr("CC_ID"));

							freeZw.set("SJ_ID", freeDayCc.getStr("SJ_ID"));

							freeZw.set("SJ_CC", sjCC); // 场次 1,2,3...

							freeZw.set("SJ_DATE", date); // 考试日期 yyyy-mm-dd

							freeZw.set("SJ_TIME", ksTime);

							freeZw.set("KC_LV", kcLv);

							Bean odeptKs = res.getGljgKs(kcId);

							log.debug("------begin-安排考位-|" + date + "|" + sjCC + "|" + freeZw.getStr("ZW_ZWH_XT") + "|"
									+ kcId + "|" + kcName);

							KcapUtils.showInfo(odeptKs, "------------1-|机构下总报考人数-报名考生人数|", this.getClass());

							if (isFiltTime) {

								// 根据考试时长筛选考生
								KcapMatch.filtKsTime(freeZw, odeptKs);

								KcapUtils.showInfo(odeptKs, "------------2-|过滤考试时长后-报名考生人数|", this.getClass());
							}

							if (isRepeatUser) {

								// 过滤 相同日期,相同场次的考生
								KcapMatch.filtKsSameCc(freeZw, res.getBusyZwBean(), odeptKs);

								KcapUtils.showInfo(odeptKs, "------------3-|过滤相同日期和场次考生后-报名考生人数|", this.getClass());

							}

							try {

								Bean oneKs = KcapMatch.matchUser(freeZw, odeptKs, res, isConstrain);// 符合座位规则的考生

								if (oneKs != null && !oneKs.isEmpty()) {

									log.debug("------------4-|成功找到安排考生-:" + oneKs.toString());

									saveRes(freeZw, oneKs, odeptKs, res);
								}

							} catch (Exception e) {

								log.error(e.getMessage(), e);
							}
						}
					} // 座位
				} // 日期
			} // 场次
		} // 考场
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

				Bean freeZw = res.getFreeCcZwBean().getBean(cc).getBean(date).getBean(kcId);

				for (Object key : freeZw.keySet()) {

					if (freeZw.get(key) instanceof Bean) {

						freeZwSize++;
					}
				}

			} else {

				Bean freeZw = res.getFreeKcZwBean().getBean(kcId).getBean(cc).getBean(date);

				for (Object key : freeZw.keySet()) {

					if (freeZw.get(key) instanceof Bean) {

						freeZwSize++;
					}
				}
			}

			if (busyZwSize <= freeZwSize) { // 安排考生人数少于机器数一半

				// 将当前考场的考生安排 从 已安排资源移除
				res.getBusyZwBean().remove(kcKey);

				log.debug("------------00-|待重置考生|" + busyZwKc.size());

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

				KcapUtils.showInfo(resetFiltKs, "------------01-|已重置考生|", this.getClass());

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

					String zwsort[] = KcapUtils.sortZwStr(resetKc); // 座位排序

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

							Bean resetFiltBean = (Bean) resetFiltKs.clone();

							log.debug("------begin-安排考位-|" + date + "|" + cc + "|" + resetZw.getStr("ZW_ZWH_XT") + "|"
									+ kcId + "|");

							KcapUtils.showInfo(resetFiltBean, "------------1-|机构下总报考人数-报名考生人数|", this.getClass());

							Bean tempFiltBean = KcapMatch.filtR006(resetZw, res.getBusyZwBean(), resetFiltBean);

							if (tempFiltBean != null && !tempFiltBean.isEmpty()) {

								Bean resetOneKs = KcapMatch.randomFiltBean(tempFiltBean);

								try {

									if (resetOneKs != null && !resetOneKs.isEmpty()) {

										log.debug("------------4-|成功找到安排考生-:" + resetOneKs.toString());
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

			int bmStatus = oneKs.getInt("BM_STATUS");// 1请假 2借考

			String ksOdept = oneKs.getStr("S_ODEPT"); // 考生机构

			if (bmStatus == 2) {

				ksOdept = oneKs.getStr("JK_ODEPT"); // 借考机构
			}

			Bean jk = res.getJkKsBean().getBean(ksOdept);
			// 保存座位安排
			Bean addZw = addArrange(freeZw, oneKs, odeptKs, jk);

			if (!Strings.isBlank(addZw.getId())) {

				log.debug("------------5-|成功安排座位：time:" + ksTime + "|odept:" + uCode);

				// 移除考场资源
				if (priority == 1) { // 最少场次

					res.getFreeCcZwBean().getBean(cc).getBean(date).getBean(kcId).remove(zwh);

					log.debug("------------6-|移除空闲座位:" + date + "|" + cc + "|" + zwh + "|" + kcId);

				} else { // 最少考场

					res.getFreeKcZwBean().getBean(kcId).getBean(cc).getBean(date).remove(zwh);

					log.debug("------------6-|移除空闲座位:" + date + "|" + cc + "|" + zwh + "|" + kcId);
				}

				Bean ks = res.getFreeKsBean().getBean(ksOdept);
				// 移除考生资源
				removeKs(ks.getBean(ksTime), uCode, shId);

				// 从过滤考生(odeptKs)中移除
				removeKs(odeptKs.getBean(ksTime), uCode, shId);

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

		if (bmStatus == 2) {
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
		addZw.set("ISAUTO", 1);

		addZw.set("U_TYPE", 0);
		addZw.set("ISSUE", 0);
		addZw.set("PUBLICITY", 0);

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

		if (ksObj instanceof Bean) {

			ksBean.remove(uCode);

		} else if (ksObj instanceof List) {

			List<Bean> ksList = ksBean.getList(uCode);

			List<Bean> tempList = new ArrayList<Bean>();

			for (Bean ks : ksList) {

				if (!ks.getStr("SH_ID").equals(shId)) {

					tempList.add(ks);
				}
			}

			ksBean.remove(uCode);

			if (tempList.size() == 1) { // 当前考生还有一个考试
				Bean t = tempList.get(0);

				ksBean.set(uCode, tempList.get(0)); // 放入未安排考生资源

			} else if (tempList.size() > 1) { // 当前考生还有多个考试

				ksBean.set(uCode, tempList); // 放入未安排考生资源
			}
		}
	}
}
