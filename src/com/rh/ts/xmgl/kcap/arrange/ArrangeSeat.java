package com.rh.ts.xmgl.kcap.arrange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.util.OrgConstant;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Strings;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.kcap.KcapResource;
import com.rh.ts.xmgl.kcap.KcapRuleEnum;

public class ArrangeSeat {

	private static Log log = LogFactory.getLog(ArrangeSeat.class);

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

		int ccsort[] = sortInt(freeBean); // 场次号排序

		for (int cs = 0; cs < ccsort.length; cs++) {

			String cc = String.valueOf(ccsort[cs]);

			Bean freeCc = freeBean.getBean(cc);

			int sjCC = Integer.parseInt(cc.toString());

			String daysort[] = sortStr(freeCc); // 日期排序

			for (int ds = 0; ds < daysort.length; ds++) {

				String day = daysort[ds];

				Bean freeDayCc = (Bean) freeCc.getBean(day).clone();

				String date = day.toString();

				for (Object kc : freeDayCc.keySet()) { // 遍历考场

					Bean freeKc = (Bean) freeDayCc.getBean(kc).clone();

					Bean odeptKs = getGljgKs(kc.toString(), res);

					String zwsort[] = sortStr(freeKc); // 座位排序

					for (int zs = 0; zs < zwsort.length; zs++) {

						String zwKey = zwsort[zs];

						if (freeKc.get(zwKey) instanceof Bean) {

							Bean freeZw = (Bean) freeKc.getBean(zwKey).clone(); // 座位信息

							freeZw.set("KC_ID", kc.toString());

							freeZw.set("CC_ID", freeKc.getStr("CC_ID"));

							freeZw.set("SJ_ID", freeKc.getStr("SJ_ID"));

							freeZw.set("SJ_CC", sjCC); // 场次 1,2,3...

							freeZw.set("SJ_DATE", date); // 考试日期 yyyy-mm-dd

							freeZw.set("GLJG", getGljg(kc.toString(), res));// 关联机构

							for (Object key : odeptKs.keySet()) {

								log.error(kc + "^" + sjCC + "^" + date + " start--odeptKs：  " + key.toString() + "="
										+ odeptKs.getBean(key).size());
							}

							Bean oneKs = KcapMatch.matchUser(freeZw, odeptKs, res, isConstrain);// 符合座位规则的考生

							try {

								if (oneKs != null && !oneKs.isEmpty()) {

									saveRes(freeZw, oneKs, odeptKs, res);
								}

							} catch (Exception e) {

								log.error(e.getMessage(), e);
							}
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

		for (Object kc : freeBean.keySet()) { // 遍历考场

			Bean freeKc = (Bean) freeBean.getBean(kc).clone();

			Bean odeptKs = getGljgKs(kc.toString(), res);

			int ccsort[] = sortInt(freeKc); // 场次号排序

			for (int cs = 0; cs < ccsort.length; cs++) {

				String cc = String.valueOf(ccsort[cs]);

				// for (Object cc : freeKc.keySet()) { // 遍历场次号

				Bean freeCc = (Bean) freeKc.getBean(cc).clone();

				int sjCC = Integer.parseInt(cc.toString());

				String daysort[] = sortStr(freeCc); // 日期排序

				for (int ds = 0; ds < daysort.length; ds++) {

					String day = daysort[ds];

					// for (Object day : freeCc.keySet()) { // 遍历场次日期

					Bean freeDayCc = (Bean) freeCc.getBean(day).clone();

					String date = day.toString();

					String zwsort[] = sortStr(freeDayCc); // 座位排序

					for (int zs = 0; zs < zwsort.length; zs++) {

						String zwKey = zwsort[zs];

						// for (Object zwKey : freeDayCc.keySet()) { // 遍历座位

						if (freeDayCc.get(zwKey) instanceof Bean) {

							Bean freeZw = (Bean) freeDayCc.getBean(zwKey).clone(); // 座位信息

							freeZw.set("KC_ID", kc.toString());

							freeZw.set("CC_ID", freeDayCc.getStr("CC_ID"));

							freeZw.set("SJ_ID", freeDayCc.getStr("SJ_ID"));

							freeZw.set("SJ_CC", sjCC); // 场次 1,2,3...

							freeZw.set("SJ_DATE", date); // 考试日期 yyyy-mm-dd

							freeZw.set("GLJG", getGljg(kc.toString(), res));// 关联机构

							for (Object key : odeptKs.keySet()) {

								log.error(kc + "^" + sjCC + "^" + date + " start--odeptKs：  " + key.toString() + "="
										+ odeptKs.getBean(key).size());
							}

							Bean oneKs = KcapMatch.matchUser(freeZw, odeptKs, res, isConstrain);// 符合座位规则的考生

							try {

								if (oneKs != null && !oneKs.isEmpty()) {

									saveRes(freeZw, oneKs, odeptKs, res);

								}

							} catch (Exception e) {

								log.error(e.getMessage(), e);
							}
						}
					}
				}
			}
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
						resetFiltKs.set(time, mergeBean(temp, resetFiltKs.getBean(time)));

					} else {
						// 待重新安排的考生资源
						resetFiltKs.set(time, temp);
					}
				} // busyZwKc.keySet()

				try {

					// Transaction.begin();

					/****************** begin-删除已安排考生座位 *****************/

					SqlBean sql = new SqlBean().and("XM_ID", res.getXmId()).and("KC_ID", kcId);
					sql.and("SJ_CC", cc).and("SJ_DATE", date);

					ServDao.delete(TsConstant.SERV_KCAP_YAPZW, sql);

					// log.error("delete---" + sql.toString());

					/****************** begin-重新安排考生座位 *******************/
					Bean resetKc = new Bean(); // 待重新安排的考场

					if (priority == 1) { // 最少场次

						resetKc = (Bean) res.getFreeCcZwBean().getBean(cc).getBean(date).getBean(kcId).clone();
					} else {
						resetKc = (Bean) res.getFreeKcZwBean().getBean(kcId).getBean(cc).getBean(date).clone();
					}

					for (Object key : resetKc.keySet()) { // 遍历座位

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
					// Transaction.commit();

				} catch (Exception e) {

					log.error(e);
				} finally {
					// Transaction.end();
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

				for (Object key : res.getFreeKsBean().getBean(ksOdept).keySet()) {

					log.error(" 删除前--res.getFreeKsBean()：  " + key.toString() + "="
							+ res.getFreeKsBean().getBean(ksOdept).getBean(key).size());
				}
				// 移除考生资源
				removeKs(res.getFreeKsBean().getBean(ksOdept).getBean(ksTime), uCode, shId);

				for (Object key : res.getFreeKsBean().getBean(ksOdept).keySet()) {

					log.error(" 删除后--res.getFreeKsBean()：  " + key.toString() + "="
							+ res.getFreeKsBean().getBean(ksOdept).getBean(key).size());
				}

				for (Object key : odeptKs.keySet()) {
					log.error(" 删除前--odeptKs：  " + key.toString() + "=" + odeptKs.getBean(key).size());
				}

				// 从过滤考生(odeptKs)中移除
				removeKs(odeptKs.getBean(ksTime), uCode, shId);

				for (Object key : odeptKs.keySet()) {
					log.error(" 删除后--odeptKs：  " + key.toString() + "=" + odeptKs.getBean(key).size());
				}

				Bean busyKc = res.getBusyZwBean().getBean(kcId + "^" + cc + "^" + date);

				busyKc.set(zwh, addZw);

				res.getBusyZwBean().set(kcId + "^" + cc + "^" + date, busyKc);

			} else {
				log.error("添加失败--保存失败--" + kcId + "^" + cc + "^" + date + "^" + zwh);
			}
		} else {
			log.error("添加失败--没有考生--" + kcId + "^" + cc + "^" + date + "^" + zwh);
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

			log.error("insert---" + addZw.getId());

			// Transaction.commit();

		} catch (Exception e) {
			log.error(e);
		} finally {
			// Transaction.end();
		}

		return addZw;
	}

	/**
	 * 获取考场下关联机构
	 * 
	 * @param kcId
	 * @param res
	 * @return
	 */
	private static Bean getGljg(String kcId, KcapResource res) {

		Bean gljg = new Bean();

		Bean allKcBean = res.getKcBean();

		for (Object key : allKcBean.keySet()) {

			List<Bean> kcList = allKcBean.getList(key);

			for (Bean kc : kcList) {

				if (kcId.equals(kc.getBean("INFO").getStr("KC_ID"))) {

					return kc.getBean("GLJG");
				}
			}
		}

		return gljg;
	}

	/**
	 * 考场关联的机构下考生
	 * 
	 * @param kcId
	 * @param res
	 * @return Bean{考试时长：考生Bean{考生编码：考生bean/list}}
	 */
	private static Bean getGljgKs(String kcId, KcapResource res) {

		Bean filtBean = new Bean();

		Bean gljg = getGljg(kcId, res);

		Bean ksBean = res.getFreeKsBean();

		for (Object key : gljg.keySet()) {

			Bean jg = gljg.getBean(key);

			String dCode = jg.getStr("JG_CODE");

			if (jg.getInt("JG_TYPE") == OrgConstant.DEPT_TYPE_DEPT) { // 关联部门

				dCode = OrgMgr.getOdept(dCode).getODeptCode(); // 获取机构
			}

			Bean timeKs = ksBean.getBean(dCode);

			for (Object time : timeKs.keySet()) { // 遍历时长

				Bean ks = timeKs.getBean(time); // 考生

				if (filtBean.containsKey(time)) {

					filtBean.set(time, mergeBean(ks, filtBean.getBean(time)));

				} else {

					filtBean.set(time, ks);
				}
			}
		}

		return filtBean;

	}

	/**
	 * 合并bean。key重复，value合并List
	 * 
	 * @param srcBean
	 * @param dstBean
	 * @return
	 */
	private static Bean mergeBean(Bean srcBean, Bean dstBean) {

		Bean rtnBan = new Bean(dstBean);

		for (Object key : srcBean.keySet()) {

			Object srcObj = srcBean.get(key);

			if (rtnBan.containsKey(key)) { // 如果两个bean都存在相同key则 合并bean

				Object dstObj = rtnBan.get(key);

				List<Bean> dstList = new ArrayList<>();

				if (dstObj instanceof Bean) {

					Bean temp = rtnBan.getBean(key);

					dstList.add(temp);

				} else if (dstObj instanceof List) {

					List<Bean> t = rtnBan.getList(key);

					dstList.addAll(t);
				}

				if (srcObj instanceof Bean) {

					Bean temp = srcBean.getBean(key);

					dstList.add(temp);

				} else if (srcObj instanceof List) {

					List<Bean> t = srcBean.getList(key);

					dstList.addAll(t);

				}

				rtnBan.set(key, dstList);

			} else { // dstBean不存在

				rtnBan.set(key, srcObj);
			}

		}

		return rtnBan;
	}

	private String[] sortStr(Bean bean) {

		String sort[] = new String[bean.keySet().size()];

		int i = 0;

		for (Object key : bean.keySet()) { // 遍历场次号

			sort[i] = key.toString();
			i++;
		}

		Arrays.sort(sort);

		return sort;
	}

	private int[] sortInt(Bean bean) {

		int sort[] = new int[bean.keySet().size()];

		int i = 0;

		for (Object key : bean.keySet()) { // 遍历场次号

			sort[i] = Integer.parseInt(key.toString());
			i++;
		}

		Arrays.sort(sort);

		return sort;
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

			log.error("最终删除考生List--uCode:" + uCode + "--shId:" + ksBean.getBean(uCode).getStr("SH_ID"));

			ksBean.remove(uCode);

		} else if (ksObj instanceof List) {

			List<Bean> ksList = ksBean.getList(uCode);

			List<Bean> tempList = new ArrayList<Bean>();

			for (Bean ks : ksList) {

				if (!ks.getStr("SH_ID").equals(shId)) {

					tempList.add(ks);
					log.error("保留考生List--uCode:" + uCode + "--shId:" + ks.getStr("SH_ID"));
				} else {
					log.error("移除考生List--uCode:" + uCode + "--shId:" + shId);
				}
			}

			if (tempList.size() > 0) {

				log.error("最终删除考生List--uCode:" + uCode + "--shId:" + shId);

				ksBean.remove(uCode);

				if (tempList.size() == 1) { // 当前考生还有一个考试

					Bean t = tempList.get(0);

					log.error("最终保留考生List--uCode:" + uCode + "--shId:" + t.getStr("SH_ID"));

					ksBean.set(uCode, tempList.get(0)); // 放入未安排考生资源

				} else if (tempList.size() > 1) { // 当前考生还有多个考试

					for (Bean t : tempList) {
						log.error("最终保留考生List--uCode:" + uCode + "--shId:" + t.getStr("SH_ID"));
					}

					ksBean.set(uCode, tempList); // 放入未安排考生资源
				}
			}
		}
	}
}
