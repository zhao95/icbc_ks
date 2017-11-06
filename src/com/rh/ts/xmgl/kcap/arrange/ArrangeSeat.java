package com.rh.ts.xmgl.kcap.arrange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.util.OrgConstant;
import com.rh.core.serv.ServDao;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.kcap.KcapResource;
import com.rh.ts.xmgl.kcap.KcapRuleEnum;

public class ArrangeSeat {

	public void doArrange(KcapResource res) {

		int priority = res.getKsPriority(); // 0 最少考场 1 最少场次

		if (priority == 1) { // 最少场次

			arrangeCc(res, false);

		} else { // 最少考场

			arrangeKc(res, false);
		}

		int ksConstrain = res.getKsConstrain(); // 不符合规则考试 是否强制安排

		if (ksConstrain == 1) { // 强制安排

			if (priority == 1) { // 最少场次

				arrangeCc(res, true);

			} else { // 最少考场

				arrangeKc(res, true);
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

					Bean ks = getGljgKs(kc.toString(), res);

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

							Bean filtKs = KcapMatch.matchUser(freeZw, ks, res, isConstrain);// 符合座位安排规则的考生

							resetRes(freeZw, filtKs, res);
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

		Bean freeBean = new Bean();

		freeBean.putAll(res.getFreeKcZwBean());

		for (Object kc : freeBean.keySet()) { // 遍历考场

			Bean freeKc = (Bean) freeBean.getBean(kc).clone();

			Bean ks = getGljgKs(kc.toString(), res);

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

							freeZw.set("CC_ID", freeKc.getStr("CC_ID"));

							freeZw.set("SJ_ID", freeKc.getStr("SJ_ID"));

							freeZw.set("SJ_CC", sjCC); // 场次 1,2,3...

							freeZw.set("SJ_DATE", date); // 考试日期 yyyy-mm-dd

							freeZw.set("GLJG", getGljg(kc.toString(), res));// 关联机构

							Bean filtKs = KcapMatch.matchUser(freeZw, ks, res, isConstrain);// 符合座位规则的考生

							resetRes(freeZw, filtKs, res);
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

		Bean resetKc = new Bean(); // 待重新安排的考场

		Bean resetKs = new Bean(); // 待重新安排的考生

		Bean resetBusyZw = new Bean();

		Bean busyZwClone = (Bean) res.getBusyZwBean().clone();

		for (Object kcKey : busyZwClone.keySet()) {

			// key 考场Id^场次^日期
			String[] keyArray = kcKey.toString().split("^");

			String kcId = keyArray[0];

			String cc = keyArray[1];

			String date = keyArray[2];

			Bean busyZwKc = busyZwClone.getBean(kcKey);

			int busyZwSize = busyZwKc.size();

			for (Object zwhKey : busyZwKc.keySet()) {

				Bean busyZw = busyZwKc.getBean(zwhKey);

				int freeZwSize = 0;

				if (priority == 1) { // 最少场次

					freeZwSize = res.getFreeCcZwBean().getBean(cc).getBean(date).getBean(kcId).size();

					if (busyZwSize < freeZwSize) { // 安排考生人数少于机器数一半

						Bean busyZwKcTemp = resetBusyZw.getBean(kcKey);

						busyZwKcTemp.set(zwhKey, busyZw);

						resetBusyZw.set(kcKey, busyZwKcTemp);

						// 将当前座位从 已安排资源移除
						res.getBusyZwBean().remove(kcKey);

						// 将当前座位添加 至 空闲座位资源
						res.getFreeCcZwBean().getBean(cc).getBean(date).getBean(kcId).set(zwhKey, busyZw);

						resetKc.set(kcId, res.getFreeCcZwBean().getBean(cc).getBean(date).getBean(kcId));

						Bean temp = new Bean();
						temp.set(busyZw.getStr("U_CODE"), busyZw);

						if (resetKs.containsKey(date)) {

							resetKs.set(date, mergeBean(temp, resetKs.getBean(date)));

						} else {
							resetKs.set(date, temp);
						}
					}

				} else { // 最少考场

					freeZwSize = res.getFreeCcZwBean().getBean(kcId).getBean(cc).getBean(date).size();

					if (busyZwSize < freeZwSize) { // 安排考生人数少于机器数一半

						Bean busyZwKcTemp = resetBusyZw.getBean(kcKey);

						busyZwKcTemp.set(zwhKey, busyZw);

						resetBusyZw.set(kcKey, busyZwKcTemp);

						// 将当前座位从 已安排资源移除
						res.getBusyZwBean().remove(kcKey);

						// 将当前座位添加 至 空闲座位资源
						res.getFreeCcZwBean().getBean(kcId).getBean(cc).getBean(date).set(zwhKey, busyZw);

						resetKc.set(kcId, res.getFreeCcZwBean().getBean(kcId).getBean(cc).getBean(date));

						Bean temp = new Bean();
						temp.set(busyZw.getStr("U_CODE"), busyZw);

						if (resetKs.containsKey(date)) {

							resetKs.set(date, mergeBean(temp, resetKs.getBean(date)));

						} else {
							resetKs.set(date, temp);
						}
					}
				}

			}
		}

		for (Object key : resetKc.keySet()) {

			Bean resetZw = resetKc.getBean(key);

			KcapMatch.filtR006(resetZw, resetBusyZw, resetKs);

			resetRes(resetZw, resetKs, res);
		}

	}

	/**
	 * 安排考生和座位并且重置资源
	 * 
	 * @param freeZw
	 * @param filtKs
	 * @param res
	 */
	private void resetRes(Bean freeZw, Bean filtKs, KcapResource res) {

		if (filtKs != null && !filtKs.isEmpty()) {

			boolean delFreeKc = false;

			boolean delFreeKs = false;

			int priority = res.getKsPriority(); // 0 最少考场优先 1 最少场次优先

			String shId = filtKs.getStr("SH_ID"); // 审核id

			String uCode = filtKs.getStr("BM_CODE"); // 考生编码

			String ksTime = filtKs.getStr("BM_KS_TIME");// 考试时长

			int bmStatus = filtKs.getInt("BM_STATUS");

			String ksOdept = filtKs.getStr("S_ODEPT"); // 考生机构

			if (bmStatus == 1) {
				ksOdept = filtKs.getStr("JK_ODEPT"); // 借考机构
			}

			String kcId = freeZw.getStr("KC_ID"); // 考场id

			String cc = freeZw.getStr("SJ_CC"); // 场次

			String date = freeZw.getStr("SJ_DATE"); // 考试日期

			String zwh = freeZw.getStr("ZW_ZWH_XT"); // 座位号

			Bean freeOdeptKcBean = res.getFreeKsBean().getBean(ksOdept).getBean(ksTime);

			// 移除考场资源
			if (priority == 1) {
				// 最少场次资源
				// res.getFreeCcZwBean().getBean(cc).getBean(date).getBean(kcId).remove(zwh);

				Bean temp = res.getFreeCcZwBean().getBean(cc).getBean(date).getBean(kcId);

				if (temp.containsKey(zwh)) {

					delFreeKc = true;

					temp.remove(zwh);
				}

			} else {
				// 最少考场资源
				// res.getFreeKcZwBean().getBean(kcId).getBean(cc).getBean(date).remove(zwh);

				Bean temp = res.getFreeKcZwBean().getBean(kcId).getBean(cc).getBean(date);

				if (temp.containsKey(zwh)) {

					delFreeKc = true;

					temp.remove(zwh);
				}
			}

			// 移除考生资源
			Object obj = freeOdeptKcBean.get(uCode);// 当前考生对象

			if (obj instanceof Bean) {

				if (freeOdeptKcBean.containsKey(uCode)) {

					if (delFreeKc) {// 删除考场资源成功,删除考生资源

						delFreeKs = true;

						freeOdeptKcBean.remove(uCode); // 从未安排考生资源中移除
					}
				}

			} else if (obj instanceof List) {

				List<Bean> otherList = new ArrayList<Bean>(); // 考生其他报名list

				List<Bean> list = freeOdeptKcBean.getList(uCode);

				for (Bean temp : list) {

					if (!temp.getStr("SH_ID").equals(shId)) {

						otherList.add(temp);
					}
				}

				if (list != null && list.size() > 0) {

					if (delFreeKc) {// 删除考场资源成功,删除考生资源

						delFreeKs = true;

						freeOdeptKcBean.remove(uCode); // 从未安排考生资源中移除

						if (otherList.size() == 1) { // 当前考生还有一个考试

							freeOdeptKcBean.set(uCode, otherList.get(0)); // 放入未安排考生资源

						} else if (otherList.size() > 1) { // 当前考生还有多个考试

							freeOdeptKcBean.set(uCode, otherList); // 放入未安排考生资源
						}
					}
				}
			}

			if (delFreeKs && delFreeKc) {// 删除考场考生资源成功

				// 添加已安排考场资源
				Bean busyZw = new Bean();

				busyZw.set("SH_ID", shId);
				busyZw.set("ZW_ID", freeZw.getStr("ZW_ID"));
				busyZw.set("ZW_XT", zwh);
				busyZw.set("SJ_ID", freeZw.getStr("SJ_ID"));
				busyZw.set("SJ_CC", cc);
				busyZw.set("SJ_DATE", date);
				busyZw.set("CC_ID", freeZw.getStr("CC_ID"));
				busyZw.set("KC_ID", kcId);

				busyZw.set("XM_ID", filtKs.getStr("XM_ID"));
				busyZw.set("BM_LB", filtKs.getStr("BM_LB_CODE"));
				busyZw.set("BM_XL", filtKs.getStr("BM_XL_CODE"));
				busyZw.set("BM_MK", filtKs.getStr("BM_MK_CODE"));
				busyZw.set("BM_LV", filtKs.getStr("BM_TYPE"));
				busyZw.set("BM_KS_TIME", ksTime);

				busyZw.set("U_ODEPT", ksOdept);
				busyZw.set("U_CODE", uCode);

				Bean jkKs = res.getJkKsBean().getBean(ksOdept);

				if (jkKs.containsKey(uCode)) { // 判断是否借考

					Object jkKsObj = jkKs.get(uCode);

					if (jkKsObj instanceof Bean) {

						Bean ks = jkKs.getBean(uCode);

						if (ks.getStr("BM_LB_CODE").equals(filtKs.getStr("BM_LB_CODE"))
								&& ks.getStr("BM_XL_CODE").equals(filtKs.getStr("BM_XL_CODE"))
								&& ks.getStr("BM_MK_CODE").equals(filtKs.getStr("BM_MK_CODE"))
								&& ks.getStr("BM_TYPE").equals(filtKs.getStr("BM_TYPE"))
								&& ks.getStr("XM_ID").equals(filtKs.getStr("XM_ID"))) {

							busyZw.set("U_TYPE", 1);
						}

					} else if (jkKsObj instanceof List) {

						List<Bean> list = jkKs.getList(uCode);

						for (Bean ks : list) {

							if (ks.getStr("BM_LB_CODE").equals(filtKs.getStr("BM_LB_CODE"))
									&& ks.getStr("BM_XL_CODE").equals(filtKs.getStr("BM_XL_CODE"))
									&& ks.getStr("BM_MK_CODE").equals(filtKs.getStr("BM_MK_CODE"))
									&& ks.getStr("BM_TYPE").equals(filtKs.getStr("BM_TYPE"))
									&& ks.getStr("XM_ID").equals(filtKs.getStr("XM_ID"))) {

								busyZw.set("U_TYPE", 1);
								break;
							}
						}
					}
				}

				Bean busyKc = res.getBusyZwBean().getBean(kcId + "^" + cc + "^" + date);
				busyKc.set(zwh, busyZw);
				res.getBusyZwBean().set(kcId + "^" + cc + "^" + date, busyKc);

				ServDao.create(TsConstant.SERV_KCAP_YAPZW, busyZw);
			}
		}
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

					filtBean.set(time, mergeBean(ks, (Bean) filtBean.getBean(time).clone()));

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

		for (Object key : srcBean.keySet()) {

			Object srcObj = srcBean.get(key);

			if (dstBean.containsKey(key)) { // 如果两个bean都存在相同key则 合并bean

				Object dstObj = dstBean.get(key);

				List<Bean> dstList = new ArrayList<>();

				if (dstObj instanceof Bean) {

					Bean temp = dstBean.getBean(key);

					dstList.add(temp);

				} else if (dstObj instanceof List) {

					List<Bean> t = dstBean.getList(key);

					dstList.addAll(t);
				}

				if (srcObj instanceof Bean) {

					Bean temp = srcBean.getBean(key);

					dstList.add(temp);

				} else if (srcObj instanceof List) {

					List<Bean> t = srcBean.getList(key);

					dstList.addAll(t);

				}

				dstBean.set(key, dstList);

			} else { // dstBean不存在

				dstBean.set(key, srcObj);
			}

		}

		return dstBean;
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
}
