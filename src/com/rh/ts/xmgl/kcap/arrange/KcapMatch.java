package com.rh.ts.xmgl.kcap.arrange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.util.DateUtils;
import com.rh.core.util.Strings;
import com.rh.ts.xmgl.kcap.KcapResource;
import com.rh.ts.xmgl.kcap.utils.KcapUtils;

public class KcapMatch {

	private static Log log = LogFactory.getLog(KcapMatch.class);

	/**
	 * 匹配符合规则的考生bean
	 * 
	 * @param freeZw
	 *            单个座位信息
	 * @param ksBean
	 *            待安排考生 {时长：bean{考生编码：单个bean/多个list}}
	 * @param res
	 *            资源
	 * @param isConstrain
	 *            是否强制安排
	 * 
	 * @return Bean 报名考生信息
	 */
	public static Bean matchUser(Bean freeZw, Bean ksBean, KcapResource res, boolean isConstrain) {

		if (KcapUtils.isEmpty(ksBean)) {

			return new Bean();
		}

		Bean busyZwBean = res.getBusyZwBean();

		Bean filtBean = (Bean) ksBean.clone();

		Bean rule = res.getRuleBean();

		Bean constrainFiltBean = (Bean) ksBean.clone();

		rtnLoop:

		for (Object key : rule.keySet()) {

			if (isConstrain) {

				// constrainFiltBean = (Bean) ksBean.clone();
			}
			int tmpValue = 0;
			if (key.toString().equals("R001")) {
				tmpValue = 1;
			} else if (key.toString().equals("R002")) {
				tmpValue = 2;
			} else if (key.toString().equals("R003")) {
				tmpValue = 3;
			} else if (key.toString().equals("R004")) {
				tmpValue = 4;
			} else if (key.toString().equals("R005")) {
				tmpValue = 5;
			} else if (key.toString().equals("R007")) {
				tmpValue = 7;
			} else if (key.toString().equals("R008")) {
				tmpValue = 8;
			} else if (key.toString().equals("R009")) {
				tmpValue = 9;
			}
			switch (tmpValue) {

			case 1:

				// 相同考试前后左右不相邻
				filtBean = filtR001(freeZw, busyZwBean, filtBean);

				break;
			case 2:

				// Bean clone = (Bean) filtBean.clone();

				// 同一考生同一场场次连排 (同一考生 同一天 同一考场 同一座位 上一场次)
				Bean rtnBean = filtR002(freeZw, busyZwBean, filtBean);

				if (!rtnBean.isEmpty() && rtnBean.getBoolean("IS_TRUE")) {

					rtnBean.remove("IS_TRUE");

					filtBean = (Bean) rtnBean.clone();

					break rtnLoop;
				}

				break;
			case 3:

				// 距离远近规则
				filtBean = filtR003(freeZw, res.getFarKsBean(), filtBean);

				break;
			case 4:

				// 同一网点级机构考生均分安排
				filtBean = filtR004(freeZw, res, filtBean);

				break;
			case 5:

				// 来自同一机构考生不连排
				filtBean = filtR005(freeZw, busyZwBean, filtBean);

				break;
			case 7:

				// 领导职务考生座位靠前安排
				filtBean = filtR007(freeZw, res.getLeaderBean(), filtBean);

				break;
			case 8:

				// 特定机构考生场次先后安排
				filtBean = filtR008(freeZw, res, rule.getBean(key), filtBean);

				break;
			case 9:

				// 特定考试仅限于省分行安排
				filtBean = filtR009(freeZw, rule.getBean(key), filtBean);

				break;
			default:
				break;
			}

			if (isConstrain) {

				boolean isEmpty = true;

				if (filtBean.isEmpty() || filtBean == null) {

					isEmpty = true;

				} else {

					for (Object time : filtBean.keySet()) {

						if (!filtBean.getBean(time).isEmpty()) {

							isEmpty = false;
						}
					}
				}

				if (isEmpty) {

					filtBean = (Bean) constrainFiltBean.clone();
				}

				break;
			}
		}

		return randomFiltBean(freeZw.getStr("KC_ID"), filtBean, res);
	}

	/**
	 * 筛选 相同考试前后左右不相邻的考生
	 * 
	 * @param freeZw
	 * @param busyZwBean
	 * @param filtBean
	 *            报名考生信息Bean
	 */
	private static Bean filtR001(Bean freeZw, Bean busyZwBean, Bean filtBean) {

		if (KcapUtils.isEmpty(filtBean)) {

			return filtBean;
		}

		KcapUtils.showInfo(filtBean, "++++++++++++++++++++++++R001-begin:", KcapMatch.class);

		try {

			String kcId = freeZw.getStr("KC_ID"); // 考场id

			// String ccId = freeZw.getStr("CC_ID"); // 场次id

			String cc = freeZw.getStr("SJ_CC"); // 场次号

			String ccDate = freeZw.getStr("SJ_DATE"); // 考试日期

			String zwhKey = kcId + "^" + cc + "^" + ccDate;

			Bean busyZwKc = busyZwBean.getBean(zwhKey);

			String zwh = freeZw.getStr("ZW_ZWH_XT"); // 座位号 (行-列)

			String[] zwhArray = zwh.split("-");

			int row = Integer.parseInt(zwhArray[0]);

			int col = Integer.parseInt(zwhArray[1]);

			String front = (row - 1) + "-" + col; // 前

			if (busyZwKc.containsKey(front)) {

				// log.error("---------前-冲突座位号:" + front + " 当前座位号：" + zwh);
				filtR001Ks(filtBean, busyZwKc.getBean(front));
			}

			String back = (row + 1) + "-" + col; // 后

			if (busyZwKc.containsKey(back)) {

				// log.error("---------后-冲突座位号:" + back + " 当前座位号：" + zwh);
				filtR001Ks(filtBean, busyZwKc.getBean(back));
			}

			String left = row + "-" + (col - 1); // 左

			if (busyZwKc.containsKey(left)) {

				// log.error("---------左-冲突座位号:" + left + " 当前座位号：" + zwh);
				filtR001Ks(filtBean, busyZwKc.getBean(left));
			}

			String right = row + "-" + (col + 1); // 右

			if (busyZwKc.containsKey(right)) {

				// log.error("---------右-冲突座位号:" + right + " 当前座位号：" + zwh);
				filtR001Ks(filtBean, busyZwKc.getBean(right));
			}

		} catch (Exception e) {

			log.error(e);

			filtBean = new Bean();
		}

		KcapUtils.showInfo(filtBean, "++++++++++++++++++++++++R001-end:", KcapMatch.class);

		return filtBean;
	}

	/**
	 * R001 移除相同考试相邻考生
	 * 
	 * @param filtBean
	 * @param busyZwBean
	 */
	private static void filtR001Ks(Bean filtBean, Bean busyZwBean) {

		if (busyZwBean != null && !busyZwBean.isEmpty()) {

			String xl = busyZwBean.getStr("BM_XL");

			String mk = busyZwBean.getStr("BM_MK");

			String lv = busyZwBean.getStr("BM_LV");

			Bean temp = (Bean) filtBean.clone();

			for (Object time : temp.keySet()) { // 遍历考试时长

				Bean timeBean = (Bean) temp.getBean(time).clone();

				for (Object ucode : timeBean.keySet()) { // 遍历时长下考生bean

					Object val = timeBean.get(ucode);

					if (val instanceof Bean) { // 当前考生报考一个考试

						Bean ks = (Bean) timeBean.getBean(ucode).clone();

						String xlCode = ks.getStr("BM_XL_CODE");

						String mKCode = ks.getStr("BM_MK_CODE");

						String lvCode = ks.getStr("BM_TYPE");

						if (xl.equals(xlCode) && mk.equals(mKCode) && lv.equals(lvCode)) {

							filtBean.getBean(time).remove(ucode);
						}

					} else if (val instanceof List) { // 当前考生报考多个考试

						boolean isdel = false;

						List<Bean> kslist = timeBean.getList(ucode);

						List<Bean> tempkslist = new ArrayList<Bean>();

						for (Bean ks : kslist) {

							String xlCode = ks.getStr("BM_XL_CODE");

							String mKCode = ks.getStr("BM_MK_CODE");

							String lvCode = ks.getStr("BM_TYPE");

							if (xl.equals(xlCode) && mk.equals(mKCode) && lv.equals(lvCode)) {

								isdel = true;

							} else {

								tempkslist.add(ks);
							}
						}

						if (isdel) {

							filtBean.getBean(time).remove(ucode);

							if (tempkslist.size() == 1) {

								filtBean.getBean(time).set(ucode, tempkslist.get(0));

							} else if (tempkslist.size() > 1) {

								filtBean.getBean(time).set(ucode, tempkslist);
							}

						} else {
							// log.error("-----------------结束处理List--未处理");
						}

					}
				}
			}
		}
	}

	/**
	 * 筛选 同一天 同一考场 上一场次考试的考生。 移除 同一天 不同考场 上一场次考试的考生。
	 * 
	 * @param freeZw
	 * @param busyZwBean
	 * @param filtBean
	 *            报名考生信息Bean
	 */
	private static Bean filtR002(Bean freeZw, Bean busyZwBean, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {

			return new Bean();
		}

		KcapUtils.showInfo(filtBean, "++++++++++++++++++++++++R002-begin:", KcapMatch.class);

		try {

			String kcId = freeZw.getStr("KC_ID"); // 考场id

			int cc = freeZw.getInt("SJ_CC"); // 场次号

			String date = freeZw.getStr("SJ_DATE"); // 考试日期

			String beforeCcDate = (cc - 1) + "^" + date;

			String beforeKcCcDate = kcId + "^" + (cc - 1) + "^" + date;

			Bean filtTemp = new Bean();

			if (busyZwBean.containsKey(beforeKcCcDate)) {

				filtR002Keep(filtBean, filtTemp, busyZwBean, beforeKcCcDate);// 保留同一考场,上一场次,同一天的安排

			} else {

				filtR002Remove(filtBean, busyZwBean, beforeCcDate); // 清除不同考场,上一场次,同一天的安排考生
			}

			if (!filtTemp.isEmpty()) { // 本场连排上一场次

				filtTemp.set("IS_TRUE", true);

				return filtTemp;
			}

		} catch (Exception e) {

			log.error(e);
		}

		KcapUtils.showInfo(filtBean, "++++++++++++++++++++++++R002-end:", KcapMatch.class);

		return filtBean;
	}

	/**
	 * 筛选同一考场,上一场次,同一天的安排考生
	 * 
	 * @param filtBean
	 * @param filtTemp
	 * @param userBean
	 */
	private static void filtR002Keep(Bean filtBean, Bean filtTemp, Bean busyZwBean, String beforeKcCcDate) {

		Bean userBean = new Bean();

		Bean busyZw = busyZwBean.getBean(beforeKcCcDate);

		for (Object zwh : busyZw.keySet()) { // 遍历场次座位

			String ucode = busyZw.getBean(zwh).getStr("U_CODE"); // 考生编码

			userBean.set(ucode, ucode);
		}

		Bean cloneFiltBean = (Bean) filtBean.clone();

		for (Object time : cloneFiltBean.keySet()) { // 遍历考试时长

			Bean timeFiltBean = cloneFiltBean.getBean(time);

			for (Object u : timeFiltBean.keySet()) {

				String ucode = u.toString();

				Object obj = timeFiltBean.get(u);

				if (userBean.containsKey(ucode)) {

					if (filtTemp.containsKey(time)) {

						filtTemp.getBean(time).set(u, obj);

					} else {
						Bean add = new Bean();

						add.set(u, obj);

						filtTemp.set(time, add);
					}
				}
			}
		}
	}

	/**
	 * 清除不同考场,上一场次,同一天的安排考生
	 * 
	 * @param filtBean
	 * @param filtTemp
	 * @param userBean
	 */
	private static void filtR002Remove(Bean filtBean, Bean busyZwBean, String beforeCcDate) {

		Bean userBean = new Bean();

		for (Object kcCcDate : busyZwBean.keySet()) {

			if (kcCcDate.toString().endsWith(beforeCcDate)) { // 不同考场,上一场次,同一天的安排(清除考生，保证场次联排规则准确)

				Bean busyZw = busyZwBean.getBean(kcCcDate);

				for (Object zwh : busyZw.keySet()) { // 遍历场次座位

					String ucode = busyZw.getBean(zwh).getStr("U_CODE"); // 考生编码

					userBean.set(ucode, ucode);
				}
			}
		}

		if (!userBean.isEmpty()) {

			Bean cloneFiltBean = (Bean) filtBean.clone();

			for (Object time : cloneFiltBean.keySet()) { // 遍历考试时长

				Bean timeFiltBean = cloneFiltBean.getBean(time);

				for (Object u : timeFiltBean.keySet()) {

					String ucode = u.toString();

					if (userBean.containsKey(ucode)) {

						filtBean.getBean(time).remove(ucode);
					}
				}
			}
		}
	}

	/**
	 * 筛选 距离近的考生，移除距离远的考生
	 * 
	 * @param freeZw
	 * @param farBean
	 * @param filtBean
	 *            {考试时长：考生Bean{考生ID:考试list}}
	 */
	private static Bean filtR003(Bean freeZw, Bean farBean, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return filtBean;
		}

		KcapUtils.showInfo(filtBean, "++++++++++++++++++++++++R003-begin:", KcapMatch.class);

		Bean cloneBean = (Bean) filtBean.clone();

		String kcId = freeZw.getStr("KC_ID"); // 考场id

		Bean farKsBean = farBean.getBean(kcId); // 当前考场 距离远的考生

		for (Object ukey : farKsBean.keySet()) { // 遍历 远距离考生机构

			for (Object timeKey : cloneBean.keySet()) { // 遍历考试时长

				Bean ksBean = cloneBean.getBean(timeKey); // 考生

				if (ksBean.containsKey(ukey)) {

					filtBean.getBean(timeKey).remove(ukey); // 移除距离远的考生
				}
			}
		}

		KcapUtils.showInfo(filtBean, "++++++++++++++++++++++++R003-end:", KcapMatch.class);

		return filtBean;
	}

	/**
	 * 筛选 同一网点级机构考生均分安排 考试安排一天 上下午均分; 考试安排多天 隔天均分;
	 * 
	 * @param freeZw
	 * @param res
	 * @param filtBean
	 *            {考试时长：考生Bean{考生ID:考试list}}
	 */
	private static Bean filtR004(Bean freeZw, KcapResource res, Bean filtBean) {

		String kcId = freeZw.getStr("KC_ID"); // 考场id

		int cc = freeZw.getInt("SJ_CC"); // 场次号

		String date = freeZw.getStr("SJ_DATE"); // 考试日期

		List<String> dateList = new ArrayList<String>();

		for (Object key : res.getCcAMPM().keySet()) {

			String str = key.toString();

			String strDate = str.substring(str.lastIndexOf("^") + 1, str.length());

			if (!dateList.contains(strDate)) {

				dateList.add(strDate);
			}
		}

		if (dateList.size() > 1) { // 多天考试

			filtR004MultiDay(filtBean, res, dateList, kcId, cc, date);

		} else if (dateList.size() == 1) { // 一天考试

			filtR004OneDay(filtBean, res, kcId, cc, date);
		}

		return filtBean;
	}

	/**
	 * 一天考试
	 * 
	 * @param filtBean
	 * @param res
	 * @param kcId
	 * @param cc
	 * @param date
	 */
	private static void filtR004OneDay(Bean filtBean, KcapResource res, String kcId, int cc, String date) {

		String curentKey = kcId + "^" + cc + "^" + date; // 当前场次key：考场ID^场次号^日期

		Bean busyZwBean = res.getBusyZwBean();

		String ampm = res.getCcAMPM().getStr(curentKey);

		if (ampm.equals("AM")) { // 只限制上午

			Bean busyKs = new Bean(); // 已安排所有考生

			for (Object key : res.getCcAMPM().keySet()) {

				String ap = res.getCcAMPM().getStr(key);

				if (ampm.equals(ap)) {

					Bean busyZw = busyZwBean.getBean(key);

					for (Object zwkey : busyZw.keySet()) {

						String ucode = busyZw.getBean(zwkey).getStr("U_CODE");

						busyKs.set(ucode, "");
					}
				}
			}

			// for (Object time : filtBean.keySet()) { // 遍历考试时长
			//
			// log.error(curentKey+"-之前-------------time:"+time+"="+filtBean.getBean(time).size());
			// }

			removeR004Ks(filtBean, res, busyKs, kcId, true);

			// for (Object time : filtBean.keySet()) { // 遍历考试时长
			//
			// log.error(curentKey+"-之后-------------time:"+time+"="+filtBean.getBean(time).size());
			// }
		}
	}

	/**
	 * 多天考试
	 * 
	 * @param filtBean
	 * @param res
	 * @param dateList
	 * @param kcId
	 * @param cc
	 * @param date
	 */
	private static void filtR004MultiDay(Bean filtBean, KcapResource res, List<String> dateList, String kcId, int cc,
			String date) {

		String curentKey = kcId + "^" + cc + "^" + date; // 当前场次key：考场ID^场次号^日期

		Bean busyZwBean = res.getBusyZwBean();

		// 考试日期排序
		Collections.sort(dateList, new Comparator<String>() {

			public int compare(String str1, String str2) {

				return str1.compareTo(str2);
			}
		});

		for (int i = 0; i < dateList.size(); i++) {

			String oddEvenDate = dateList.get(i); // 所有考试日期

			if (oddEvenDate.equals(date)) {

				if ((i + 1) % 2 != 0) { // 当前考试日是奇数

					Bean busyKsOdd = new Bean();// 已安排考生

					for (Object key : res.getCcAMPM().keySet()) {

						if (key.toString().indexOf("^" + oddEvenDate) > -1) {

							Bean busyZw = busyZwBean.getBean(key);

							for (Object zwkey : busyZw.keySet()) {

								String ucode = busyZw.getBean(zwkey).getStr("U_CODE");

								busyKsOdd.set(ucode, "");
							}
						}
					}

					for (Object time : filtBean.keySet()) { // 遍历考试时长

						log.error(curentKey + "-奇前-------------time:" + time + "=" + filtBean.getBean(time).size());
					}

					removeR004Ks(filtBean, res, busyKsOdd, kcId, true);

					for (Object time : filtBean.keySet()) { // 遍历考试时长

						log.error(curentKey + "-奇后-------------time:" + time + "=" + filtBean.getBean(time).size());
					}

				} else if ((i + 1) % 2 == 0) { // 当前考试日是偶数

					Bean yesterdayBusyKs = new Bean(); // 昨天安排的考生

					String yesterday = DateUtils.getCertainDate(date, -1); // 昨天日期

					for (Object key : res.getCcAMPM().keySet()) {

						if (key.toString().indexOf("^" + yesterday) > -1) {

							Bean busyZw = busyZwBean.getBean(key); // 昨天考场安排

							for (Object zwkey : busyZw.keySet()) {

								String ucode = busyZw.getBean(zwkey).getStr("U_CODE");

								yesterdayBusyKs.set(ucode, "");
							}
						}
					}

					for (Object time : filtBean.keySet()) { // 遍历考试时长

						log.error(curentKey + "-偶前-------------time:" + time + "=" + filtBean.getBean(time).size());
					}

					removeR004Ks(filtBean, res, yesterdayBusyKs, kcId, false);

					for (Object time : filtBean.keySet()) { // 遍历考试时长

						log.error(curentKey + "-偶后-------------time:" + time + "=" + filtBean.getBean(time).size());
					}
				}
			}
		}
	}

	/**
	 * 移除网点考生
	 * 
	 * @param filtBean
	 * @param res
	 * @param busyKs
	 * @param kcId
	 * @param delFree
	 *            true:删除未安排考生/false:删除已安排考生
	 */
	private static void removeR004Ks(Bean filtBean, KcapResource res, Bean busyKs, String kcId, boolean delFree) {

		Bean allbranch = res.getBranchBean(); // 所有网点

		Bean kcjg = res.getKcOrgBean().getBean(kcId); // 考场的关联机构

		for (Object jg : kcjg.keySet()) { // 遍历关联机构

			Bean branchJG = allbranch.getBean(jg); // 某机构下的网点

			for (Object key : branchJG.keySet()) { // 遍历机构下的网点

				Bean branchKs = branchJG.getBean(key); // 某网点考生

				if (!branchKs.isEmpty() && branchKs.size() > 1) { // 网点考生人数大于1

					int branchBusySize = 0; // 网点下已安排考生数

					int branchFreeSize = 0; // 网点下未安排考生数

					Bean branchFreeKs = new Bean();

					Bean branchBusyKs = new Bean();

					for (Object uKey : branchKs.keySet()) {

						if (busyKs.containsKey(uKey)) {

							branchBusySize++;

							branchBusyKs.set(uKey, "");

						} else {

							branchFreeSize++;

							branchFreeKs.set(uKey, "");
						}
					}

					if (branchBusySize >= branchFreeSize && branchFreeSize > 0) { // 网点下已安排考生大于未安排考生,不在继续安排该网点考生

						try {

							Bean cloneFiltBean = (Bean) filtBean.clone();

							for (Object time : cloneFiltBean.keySet()) { // 遍历考试时长

								Bean timeBean = (Bean) cloneFiltBean.getBean(time).clone();

								for (Object user : timeBean.keySet()) { // 遍历时长下考生bean

									if (delFree) {

										if (branchFreeKs.containsKey(user)) {// 移除该网点未安排考生

											filtBean.getBean(time).remove(user);

											log.error("移除----------------------------------------time:" + time + "="
													+ user);
										}
									} else {

										if (branchBusyKs.containsKey(user)) { // 移除该网点已安排考生

											filtBean.getBean(time).remove(user);

											log.error("移除----------------------------------------time:" + time + "="
													+ user);
										}
									}
								}
							}
						} catch (Exception e) {

							log.error(e.getMessage(), e);
						}
					}
				}
			} // 遍历机构下的网点
		} // 遍历关联机构
	}

	/**
	 * 筛除 相邻座位 来自同一机构考生
	 * 
	 * @param freeZw
	 * @param busyZwBean
	 * @param filtBean
	 *            {考试时长：考生Bean{考生ID:考试list}}
	 */
	private static Bean filtR005(Bean freeZw, Bean busyZwBean, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return filtBean;
		}

		KcapUtils.showInfo(filtBean, "++++++++++++++++++++++++R005-begin:", KcapMatch.class);

		try {

			String kcId = freeZw.getStr("KC_ID"); // 考场id

			String cc = freeZw.getStr("SJ_CC"); // 场次号

			String ccDate = freeZw.getStr("SJ_DATE"); // 考试日期

			String zwhKey = kcId + "^" + cc + "^" + ccDate;

			Bean busyZwKc = busyZwBean.getBean(zwhKey);

			String zwh = freeZw.getStr("ZW_ZWH_XT"); // 座位号 (行-列)

			String[] zwhArray = zwh.split("-");

			int row = Integer.parseInt(zwhArray[0]);

			int col = Integer.parseInt(zwhArray[1]);

			String left = row + "-" + (col - 1); // 左

			if (busyZwKc.containsKey(left)) {

				Bean leftBusy = busyZwKc.getBean(left);

				String odept = leftBusy.getStr("U_ODEPT");

				if (!Strings.isBlank(odept)) {

					filtBean = filtR005Ks(filtBean, odept);
				}
			}

			String right = row + "-" + (col + 1); // 右

			if (busyZwKc.containsKey(right)) {

				Bean rightBusy = busyZwKc.getBean(right);

				String odept = rightBusy.getStr("U_ODEPT");

				if (!Strings.isBlank(odept)) {

					filtBean = filtR005Ks(filtBean, odept);
				}
			}

		} catch (Exception e) {

			log.error(e);

			filtBean = null;
		}

		KcapUtils.showInfo(filtBean, "++++++++++++++++++++++++R005-end:", KcapMatch.class);

		return filtBean;
	}

	/**
	 * 去除 相邻座位 来自同一机构考生
	 * 
	 * @param filtBean
	 * @param busyZwBean
	 * @throws Exception
	 */
	private static Bean filtR005Ks(Bean filtBean, String odept) {

		try {

			Bean cloneFiltBean = (Bean) filtBean.clone();

			for (Object time : cloneFiltBean.keySet()) { // 遍历考试时长

				Bean timeBean = (Bean) cloneFiltBean.getBean(time).clone();

				for (Object user : timeBean.keySet()) { // 遍历时长下考生bean

					Object val = timeBean.get(user);

					if (val instanceof Bean) { // 当前考生报考一个考试

						Bean ks = timeBean.getBean(user);

						if (odept.equals(ks.getStr("S_ODEPT"))) {

							filtBean.getBean(time).remove(user);
						}

					} else if (val instanceof List) { // 当前考生报考多个考试

						List<Bean> kslist = timeBean.getList(user);

						for (Bean ks : kslist) {

							if (odept.equals(ks.getStr("S_ODEPT"))) {

								filtBean.getBean(time).remove(user);

								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {

			log.error(e.getMessage(), e);
		}

		return filtBean;
	}

	/**
	 * 考生人数少于机器数一半时，考生左右间隔不低于2个座位，前后不低于1个
	 * 
	 * @param freeZw
	 * @param res
	 * @param filtBean
	 */
	public static Bean filtR006(Bean freeZw, Bean busyZwBean, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return filtBean;
		}

		KcapUtils.showInfo(filtBean, "++++++++++++++++++++++++R006-begin:", KcapMatch.class);

		try {

			String kcId = freeZw.getStr("KC_ID"); // 考场id

			// String ccId = freeZw.getStr("CC_ID"); // 场次id

			String cc = freeZw.getStr("SJ_CC"); // 场次号

			String ccDate = freeZw.getStr("SJ_DATE"); // 考试日期

			String zwhKey = kcId + "^" + cc + "^" + ccDate;

			Bean busyZwKc = busyZwBean.getBean(zwhKey);

			String zwh = freeZw.getStr("ZW_ZWH_XT"); // 座位号 (行-列)

			String[] zwhArray = zwh.split("-");

			int row = Integer.parseInt(zwhArray[0]);

			int col = Integer.parseInt(zwhArray[1]);

			String front = (row - 1) + "-" + col; // 前
			String back = (row + 1) + "-" + col; // 后
			String left = row + "-" + (col - 1); // 左
			String right = row + "-" + (col + 1); // 右

			if (busyZwKc.containsKey(front) || busyZwKc.containsKey(back) || busyZwKc.containsKey(left)
					|| busyZwKc.containsKey(right)) {

				filtBean = null;

				// if (busyZwKc.containsKey(front))
				// log.error("冲突座位:" + zwh + " 前:" + front + ":" +
				// busyZwKc.getBean(front).getStr("ZW_XT"));
				// if (busyZwKc.containsKey(back))
				// log.error("冲突座位:" + zwh + " 后:" + back + ":" +
				// busyZwKc.getBean(back).getStr("ZW_XT"));
				// if (busyZwKc.containsKey(left))
				// log.error("冲突座位:" + zwh + " 左:" + left + ":" +
				// busyZwKc.getBean(left).getStr("ZW_XT"));
				// if (busyZwKc.containsKey(right))
				// log.error("冲突座位:" + zwh + " 右:" + right + ":" +
				// busyZwKc.getBean(right).getStr("ZW_XT"));
			} else {
				// log.error("安排座位:" + zwh);
			}

		} catch (Exception e) {

			log.error(e);

			filtBean = null;
		}

		KcapUtils.showInfo(filtBean, "++++++++++++++++++++++++R006-end:", KcapMatch.class);

		return filtBean;
	}

	/**
	 * 筛选 领导职务考生座位靠前安排
	 * 
	 * @param freeZw
	 * @param leaderBean
	 * @param filtBean
	 */
	private static Bean filtR007(Bean freeZw, Bean leaderBean, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return filtBean;
		}

		KcapUtils.showInfo(filtBean, "++++++++++++++++++++++++R007-begin:", KcapMatch.class);

		Bean temp = new Bean();

		for (Object time : filtBean.keySet()) { // 遍历考试时长

			Bean timeBean = filtBean.getBean(time); // 该时长考生

			Bean uTemp = new Bean();

			for (Object leader : leaderBean.keySet()) { // 遍历领导

				if (timeBean.containsKey(leader)) {

					uTemp.set(leader, timeBean.get(leader));
				}
			}

			if (!uTemp.isEmpty()) {

				temp.put(time, uTemp);
			}
		}

		if (!temp.isEmpty()) {

			boolean isEmpty = true;

			for (Object time : temp.keySet()) {

				if (!temp.getBean(time).isEmpty()) {
					isEmpty = false;
				}
			}

			if (!isEmpty) {

				filtBean = temp;
			}
		}

		KcapUtils.showInfo(filtBean, "++++++++++++++++++++++++R007-end:", KcapMatch.class);

		return filtBean;
	}

	/**
	 * 筛选 特定机构考生场次先后安排
	 * 
	 * @param freeZw
	 * @param res
	 * @param filtBean
	 */
	private static Bean filtR008(Bean freeZw, KcapResource res, Bean rule, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return filtBean;
		}

		KcapUtils.showInfo(filtBean, "++++++++++++++++++++++++R008-begin:", KcapMatch.class);

		Bean filt = new Bean();

		String jsonStr = rule.getStr("GZ_VALUE2");

		try {

			if (!Strings.isBlank(jsonStr)) {

				JSONObject obj = new JSONObject(jsonStr);

				String type = obj.getString("direction");

				if (!Strings.isBlank(type) && !res.getSpOrgKsBean().isEmpty()) {

					Bean cloneFiltBean = (Bean) filtBean.clone();

					filt = filtR008Ks(freeZw, res, cloneFiltBean, type);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (!filt.isEmpty()) {
			return filt;
		}

		KcapUtils.showInfo(filtBean, "++++++++++++++++++++++++R008-end:", KcapMatch.class);

		return filtBean;
	}

	/**
	 * 过滤考生
	 * 
	 * @param filtBean
	 * @param freeZw
	 * @param dCode
	 * @param type
	 * @return
	 */
	private static Bean filtR008Ks(Bean freeZw, KcapResource res, Bean filtBean, String type) {

		Bean kcBean = res.getKcBean();

		Bean ccBean = new Bean();

		for (Object key : kcBean.keySet()) { // 所有机构下的考场bean

			List<Bean> kcinfoList = kcBean.getList(key); // 某所属机构下的考场list

			for (Bean kcInfo : kcinfoList) {

				Bean info = kcInfo.getBean("INFO");

				if (info.getStr("KC_ID").equals(freeZw.getStr("KC_ID"))) {

					List<Bean> cclist = kcInfo.getList("CC");

					for (Bean cc : cclist) {

						String date = cc.getStr("SJ_START").substring(0, 10); // 考试日期

						ccBean.set(date, date);
					}
					break;
				}
			}
		}

		Bean temp = new Bean();

		Bean spOrgKsBean = res.getSpOrgKsBean();

		Bean cloneFiltBean = (Bean) filtBean.clone();

		for (Object time : cloneFiltBean.keySet()) { // 遍历考试时长

			Bean timeBean = (Bean) filtBean.getBean(time).clone();

			Bean uTemp = new Bean();

			for (Object user : timeBean.keySet()) { // 遍历考生

				if (spOrgKsBean.containsKey(user)) {

					String date = freeZw.getStr("SJ_DATE"); // 考试日期

					if ("forward".equals(type)) { // forward靠前

						String tomorrow = DateUtils.getCertainDate(date, 1); // 明天日期

						if (ccBean.containsKey(tomorrow)) { // 判断今天是否靠前

							uTemp.set(user, timeBean.get(user));
						}

					} else if ("back".equals(type)) { // back靠后

						String yesterday = DateUtils.getCertainDate(date, -1); // 昨天日期

						if (ccBean.containsKey(yesterday)) { // 判断今天是否靠后

							uTemp.set(user, timeBean.get(user));
						}
					}
				} else {
					uTemp.set(user, timeBean.get(user));
				}
			}

			if (!uTemp.isEmpty()) {

				temp.set(time, uTemp);
			}
		}

		boolean isEmpty = true;

		if (!temp.isEmpty()) {

			for (Object time : temp.keySet()) {

				if (!temp.getBean(time).isEmpty()) {

					isEmpty = false;
				}
			}
		}

		if (isEmpty) {

			return filtBean;

		} else {

			return temp;
		}
	}

	/**
	 * 筛选 特定考试仅限于省分行安排
	 * 
	 * @param freeZw
	 * @param res
	 * @param filtBean
	 */
	private static Bean filtR009(Bean freeZw, Bean rule, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return filtBean;
		}

		KcapUtils.showInfo(filtBean, "++++++++++++++++++++++++R009-begin:", KcapMatch.class);

		String kcLv = freeZw.getStr("KC_LV"); // 考场层级 一级考场 二级考场

		String jsonStr = rule.getStr("GZ_VALUE2");

		try {

			if (kcLv.equals("一级") && !Strings.isBlank(jsonStr)) { // 一级考场

				JSONObject obj = new JSONObject(jsonStr);

				String xlCode = obj.getString("values");

				Bean temp = new Bean();

				for (Object time : filtBean.keySet()) { // 遍历考试时长

					Bean timeBean = filtBean.getBean(time);

					Bean uTemp = new Bean();

					for (Object user : timeBean.keySet()) { // 遍历时长下考生bean

						Object val = timeBean.get(user);

						if (val instanceof Bean) { // 当前考生报考一个考试

							Bean ks = timeBean.getBean(user);

							String bmxl = ks.getStr("BM_XL_CODE");

							if (bmxl.equals(xlCode)) {

								uTemp.put(user, ks);
							}

						} else if (val instanceof List) { // 当前考生报考多个考试

							List<Bean> kslist = timeBean.getList(user);

							for (Bean ks : kslist) {

								String bmxl = ks.getStr("BM_XL_CODE");

								if (bmxl.equals(xlCode)) {

									if (uTemp.containsKey(user)) {

										List<Bean> l = new ArrayList<Bean>();

										if (uTemp.get(user) instanceof Bean) {

											l.add(uTemp.getBean(user));

											l.add(ks);

										} else if (uTemp.get(user) instanceof List) {

											l = uTemp.getList(user);

											l.add(ks);
										}

										uTemp.put(user, l);

									} else {
										uTemp.put(user, ks);
									}
								}
							}
						}
					}

					if (!uTemp.isEmpty()) {

						temp.set(time, uTemp);
					}
				}

				if (!temp.isEmpty()) {

					filtBean = new Bean(temp);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		KcapUtils.showInfo(filtBean, "++++++++++++++++++++++++R009-end:", KcapMatch.class);

		return filtBean;

	}

	/**
	 * 随机获取考生
	 * 
	 * @param filtBean
	 *            通过规则筛选的考生
	 * @return
	 */
	public static Bean randomFiltBean(Bean filtBean) {

		if (!filtBean.isEmpty()) {

			ArrayList<Bean> singleList = new ArrayList<Bean>();

			ArrayList<Bean> multiList = new ArrayList<Bean>();

			for (Object time : filtBean.keySet()) { // 遍历考试时长

				Bean timeBean = filtBean.getBean(time);

				for (Object user : timeBean.keySet()) { // 遍历时长下考生bean

					Object val = timeBean.get(user);

					if (val instanceof Bean) { // 当前考生报考一个考试

						Bean ks = timeBean.getBean(user);

						singleList.add(ks);

					} else if (val instanceof List) { // 当前考生报考多个考试

						List<Bean> kslist = timeBean.getList(user);

						multiList.addAll(kslist);
					}
				}
			}

			// 多个考试的考生,优先安排
			if (!multiList.isEmpty()) {

				int index = new Random().nextInt(multiList.size()); // 随机获取list索引

				return multiList.get(index);
			}
			// 单个考试的考生
			if (!singleList.isEmpty()) {

				int index = new Random().nextInt(singleList.size()); // 随机获取list索引

				return singleList.get(index);
			}
		}

		return new Bean();
	}

	/**
	 * 获取 报名生多的考生
	 * 
	 * @param kcId
	 * @param filtBean
	 * @param res
	 * @return
	 */
	public static Bean randomFiltBean(String kcId, Bean filtBean, KcapResource res) {

		if (!filtBean.isEmpty()) {

			Map<String, Integer> ksUcode = new TreeMap<String, Integer>();

			String maxUser = "";

			for (Object time : filtBean.keySet()) { // 遍历考试时长

				Bean timeBean = filtBean.getBean(time);

				for (Object user : timeBean.keySet()) { // 遍历时长下考生bean

					ksUcode.put(user.toString(), 0);

					maxUser = user.toString();
				}
			}

			if (!ksUcode.isEmpty() && ksUcode.size() > 1) { // 多个考生报名多的优先
				Bean odeptKs = res.getGljgKs(kcId); // 考场所有未安排考生

				for (Object time : odeptKs.keySet()) { // 遍历时长

					Bean timeks = odeptKs.getBean(time); // 考生

					for (Object ukey : timeks.keySet()) {

						if (ksUcode.containsKey(ukey)) {

							Object val = timeks.get(ukey);
							
							int count = ksUcode.get(ukey);

							if (val instanceof Bean) { // 当前考生报考一个考试

								ksUcode.put(ukey.toString(), count + 1);

							} else if (val instanceof List) { // 当前考生报考多个考试

								List<Bean> kslist = timeks.getList(ukey);

								ksUcode.put(ukey.toString(), count + kslist.size());
							}
						}
					}
				}

				List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(ksUcode.entrySet());

				Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
					// 升序排序
					public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
						return o1.getValue().compareTo(o2.getValue());
					}
				});

				Entry<String, Integer> uEntry = list.get(0);

				maxUser = uEntry.getKey();
			}

			for (Object time : filtBean.keySet()) { // 遍历考试时长

				Bean timeBean = filtBean.getBean(time);

				Object val = timeBean.get(maxUser);

				if (val instanceof Bean) { // 当前考生报考一个考试

					return timeBean.getBean(maxUser);

				} else if (val instanceof List) { // 当前考生报考多个考试

					List<Bean> kslist = timeBean.getList(maxUser);

					return kslist.get(0);
				}
			}
		}

		return new Bean();
	}

	/**
	 * 根据考试时长 筛选考生 60、90分钟考试混排;120分钟混排;150分钟混排
	 * 
	 * @param freeZw
	 * @param filtBean
	 */
	public static void filtKsTime(Bean freeZw, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return;
		}

		String ksTime = freeZw.getStr("SJ_TIME");

		if (ksTime.contains("60") && !ksTime.contains("90")) { // 考试时长只60分钟

			filtBean.remove("90");
			filtBean.remove("120");
			filtBean.remove("150");

		} else if (ksTime.contains("90")) { // 考试时长90分钟

			filtBean.remove("120");
			filtBean.remove("150");

		} else if (ksTime.contains("120")) { // 考试时长120分钟

			filtBean.remove("60");
			filtBean.remove("90");
			filtBean.remove("150");

		} else if (ksTime.contains("150")) { // 考试时长150分钟

			filtBean.remove("60");
			filtBean.remove("90");
			filtBean.remove("120");
		}
	}

	/**
	 * 过滤 不同考场，相同日期，相同场次的考生
	 * 
	 * @param freeZw
	 * @param busyZwBean
	 * @param filtBean
	 */
	public static void filtKsSameCc(Bean freeZw, Bean busyZwBean, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return;
		}

		String busyKey = freeZw.getStr("SJ_CC") + "^" + freeZw.getStr("SJ_DATE");

		for (Object obj : busyZwBean.keySet()) {// 遍历 考场^场次^日期

			if (obj.toString().contains(busyKey)) { // 判断 是否包含 场次^日期

				Bean busyZwKc = busyZwBean.getBean(obj);

				String ucodeArg[] = new String[busyZwKc.size()];

				int i = 0;

				for (Object zwh : busyZwKc.keySet()) { // 遍历所有座位

					Bean zwinfo = busyZwKc.getBean(zwh);

					String ucode = zwinfo.getStr("U_CODE");

					ucodeArg[i] = ucode;

					i++;
				}

				Arrays.sort(ucodeArg);

				Bean temp = new Bean(filtBean);

				for (Object time : temp.keySet()) { // 考试时长

					Bean timeBean = (Bean) temp.getBean(time).clone();

					for (Object ucode : timeBean.keySet()) {

						if (Arrays.binarySearch(ucodeArg, ucode.toString()) >= 0) {

							filtBean.getBean(time).remove(ucode);

							// log.error("移除相同日期，相同场次考生：" + time + ":" + ucode);
						}
					}

					if (filtBean.getBean(time).isEmpty()) {

						filtBean.remove(time);
					}
				}
			} // 判断 是否包含 场次^日期
		}
	}
}
