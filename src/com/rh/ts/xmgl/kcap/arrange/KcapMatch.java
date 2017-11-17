package com.rh.ts.xmgl.kcap.arrange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.ts.xmgl.kcap.KcapResource;

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

		Bean busyZwBean = res.getBusyZwBean();

		// 根据考试时长筛选考生
		filtKsTime(freeZw, busyZwBean, ksBean);

		// 过滤 相同日期,相同场次的考生
		filtKsSameCc(freeZw, busyZwBean, ksBean);

		if (ksBean == null || ksBean.isEmpty()) {

			return new Bean();
		}

		Bean filtBean = new Bean(ksBean);

		Bean rule = res.getRuleBean();

		Bean constrainFiltBean = null;

		rtnLoop:

		for (Object key : rule.keySet()) {

			if (isConstrain) {

				constrainFiltBean = new Bean(filtBean);
			}

			switch (key.toString()) {

			case "R001":

				// 相同考试前后左右不相邻
				filtR001(freeZw, busyZwBean, filtBean);

				break;
			case "R002":

				// 同一考生同一场场次连排 (同一考生 同一天 同一考场 同一座位 上一场次)
				boolean isRtn = filtR002(freeZw, busyZwBean, filtBean);

				if (isRtn) {
					break rtnLoop;
				}

				break;
			case "R003":

				// 距离远近规则
				filtR003(freeZw, res.getFarKsBean(), filtBean);

				break;
			case "R004":

				// 同一网点级机构考生均分安排
				filtR004(freeZw, res, filtBean);

				break;
			case "R005":

				// 来自同一机构考生不连排
				filtR005(freeZw, busyZwBean, filtBean);

				break;
			case "R007":

				// 领导职务考生座位靠前安排
				filtR007(freeZw, res.getLeaderBean(), filtBean);

				break;
			case "R008":

				// 特定机构考生场次先后安排
				filtR008(freeZw, res, filtBean);

				break;
			case "R009":

				// 特定考试仅限于省分行安排
				filtR009(freeZw, rule.getBean(key), filtBean);

				break;
			default:
				break;
			}

			if (isConstrain && (filtBean.isEmpty() || filtBean == null)) {

				filtBean = new Bean(constrainFiltBean);

				break;
			}
		}

		return randomFiltBean(filtBean);
	}

	/**
	 * 筛选 相同考试前后左右不相邻的考生
	 * 
	 * @param freeZw
	 * @param busyZwBean
	 * @param filtBean
	 *            报名考生信息Bean
	 */
	private static void filtR001(Bean freeZw, Bean busyZwBean, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return;
		}

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

				filtR001Ks(filtBean, busyZwKc.getBean(front));
			}

			String back = (row + 1) + "-" + col; // 后

			if (busyZwKc.containsKey(back)) {

				filtR001Ks(filtBean, busyZwKc.getBean(back));
			}

			String left = row + "-" + (col - 1); // 左

			if (busyZwKc.containsKey(left)) {

				filtR001Ks(filtBean, busyZwKc.getBean(left));
			}

			String right = row + "-" + (col + 1); // 右

			if (busyZwKc.containsKey(right)) {

				filtR001Ks(filtBean, busyZwKc.getBean(right));
			}

		} catch (Exception e) {

			log.error(e);

			filtBean = null;
		}
	}

	/**
	 * R001 移除相同考试相邻考生
	 * 
	 * @param filtBean
	 * @param busyZwBean
	 */
	private static void filtR001Ks(Bean filtBean, Bean busyZwBean) {

		if (busyZwBean != null && !busyZwBean.isEmpty()) {

			String ucode = busyZwBean.getStr("U_CODE");

			String shId = busyZwBean.getStr("SH_ID");

			Bean temp = (Bean) filtBean.clone();

			for (Object time : temp.keySet()) { // 遍历考试时长

				Bean timeBean = (Bean) temp.getBean(time).clone();

				for (Object key : timeBean.keySet()) { // 遍历时长下考生bean

					if (ucode.equals(key.toString())) {

						filtBean.getBean(time).remove(ucode); // 移除考生本人

						continue;
					}

					Object val = timeBean.get(key);

					if (val instanceof Bean) { // 当前考生报考一个考试

						Bean ks = (Bean) timeBean.getBean(key).clone();

						// 判断是否相同考试的考生
						if (shId.equals(ks.getStr("SH_ID"))) {

							filtBean.getBean(time).remove(key); // 移除相同考试考生
						}

					} else if (val instanceof List) { // 当前考生报考多个考试

						List<Bean> kslist = timeBean.getList(key);

						for (Bean ks : kslist) {

							// 判断是否相同考试的考生
							if (shId.equals(ks.getStr("SH_ID"))) {

								filtBean.getBean(time).remove(key); // 移除相同考试考生

								break;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 筛选 同一天 同一考场 上一场次考试的考生 (如果有值)
	 * 
	 * @param freeZw
	 * @param busyZwBean
	 * @param filtBean
	 *            报名考生信息Bean
	 */
	private static boolean filtR002(Bean freeZw, Bean busyZwBean, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return false;
		}

		try {

			String kcId = freeZw.getStr("KC_ID"); // 考场id

			int cc = freeZw.getInt("SJ_CC"); // 场次号

			String date = freeZw.getStr("SJ_DATE"); // 考试日期

//			String zwh = freeZw.getStr("ZW_ZWH_XT"); // 座位号 (行-列)

			String beforeKey = kcId + "^" + (cc - 1) + "^" + date;

			Bean filtTemp = new Bean();

			for (Object key : busyZwBean.keySet()) {

				if (beforeKey.equals(key.toString())) { // 同一天 同一考场 上一场次的安排

					try {
						
						Bean busy = busyZwBean.getBean(key);

						String ucode = busy.getStr("U_CODE"); // 考生编码
						
						for (Object key1 : filtBean.keySet()) { // 遍历考试时长
							
							Object obj = filtBean.getBean(key1).get(ucode);

							if (obj != null) {

								Bean filtUser = new Bean();

								if (obj instanceof Bean) {
									
									Bean temp = filtBean.getBean(key1).getBean(ucode);
									
									if (temp != null && !temp.isEmpty()) {

										filtUser.set(ucode, temp);

										filtTemp.set(key1, filtUser);
									}

								} else if (obj instanceof List) {

									List<Bean> temp = filtBean.getBean(key1).getList(ucode);

									if (temp != null && !temp.isEmpty()) {

										filtUser.set(ucode, temp);

										filtTemp.set(key1, filtUser);
									}
								}
							}
						}
					} catch (Exception e) {

						log.error(e);
					}
				}
			}

			if (!filtTemp.isEmpty()) {

				filtBean = new Bean(filtTemp);

				return true;
			}

		} catch (Exception e) {

			log.error(e);
		}

		return false;
	}

	/**
	 * 筛选 距离近的考生
	 * 
	 * @param freeZw
	 * @param farBean
	 * @param filtBean
	 *            {考生ID:考生bean}
	 */
	private static void filtR003(Bean freeZw, Bean farBean, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return;
		}

		String kcId = freeZw.getStr("KC_ID"); // 考场id

		Bean farKsBean = farBean.getBean(kcId);

		Bean cloneBean = (Bean) filtBean.clone();

		for (Object ukey : farKsBean.keySet()) {

			for (Object timeKey : cloneBean.keySet()) { // 遍历考试时长

				Bean ksBean = cloneBean.getBean(timeKey); // 考生

				if (ksBean.containsKey(ukey)) {

					filtBean.getBean(timeKey).remove(ukey); // 移除距离远的考生
				}
			}
		}

	}

	/**
	 * 筛选 同一网点级机构考生均分安排
	 * 
	 * @param freeZw
	 * @param res
	 * @param filtBean
	 *            {考生ID:考生bean}
	 */
	private static void filtR004(Bean freeZw, KcapResource res, Bean filtBean) {

		SqlBean sql = new SqlBean();

		ServDao.find("", sql);

		if (filtBean == null || filtBean.isEmpty()) {
			return;
		}
	}

	/**
	 * 筛除 相邻座位 来自同一机构考生
	 * 
	 * @param freeZw
	 * @param busyZwBean
	 * @param filtBean
	 *            {考生ID:考生bean}
	 */
	private static void filtR005(Bean freeZw, Bean busyZwBean, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return;
		}

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

				filtR005Ks(filtBean, busyZwKc.getBean(left));
			}

			String right = row + "-" + (col + 1); // 右

			if (busyZwKc.containsKey(right)) {

				filtR005Ks(filtBean, busyZwKc.getBean(right));
			}

		} catch (Exception e) {

			log.error(e);

			filtBean = null;
		}
	}

	/**
	 * 去除 相邻座位 来自同一机构考生
	 * 
	 * @param filtBean
	 * @param busyZwBean
	 */
	private static void filtR005Ks(Bean filtBean, Bean busyZwBean) {

		if (busyZwBean != null && !busyZwBean.isEmpty()) {

			String odept = busyZwBean.getStr("U_ODEPT");

			int jkFlag = busyZwBean.getInt("U_TYPE"); // 1 借考

			Bean cloneFiltBean = (Bean) filtBean.clone();

			for (Object time : cloneFiltBean.keySet()) { // 遍历考试时长

				Bean timeBean = cloneFiltBean.getBean(time);

				for (Object user : timeBean.keySet()) { // 遍历时长下考生bean

					Object val = timeBean.get(user);

					if (val instanceof Bean) { // 当前考生报考一个考试

						Bean ks = timeBean.getBean(user);

						if (jkFlag == 1 && odept.equals(ks.getInt("JK_ODEPT"))) {

							filtBean.getBean(time).remove(user);

						} else if (odept.equals(ks.getInt("S_ODEPT"))) {

							filtBean.getBean(time).remove(user);
						}

					} else if (val instanceof List) { // 当前考生报考多个考试

						List<Bean> kslist = timeBean.getList(user);

						for (Bean ks : kslist) {

							if (jkFlag == 1 && odept.equals(ks.getInt("JK_ODEPT"))) {

								filtBean.getBean(time).remove(user);

								break;

							} else if (odept.equals(ks.getInt("S_ODEPT"))) {

								filtBean.getBean(time).remove(user);

								break;
							}
						}
					}
				}
			}
		}
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

				if (busyZwKc.containsKey(front))
					log.error("冲突座位:" + zwh + " 前:" + front + ":" + busyZwKc.getBean(front));
				if (busyZwKc.containsKey(back))
					log.error("冲突座位:" + zwh + " 后:" + back + ":" + busyZwKc.getBean(back));
				if (busyZwKc.containsKey(left))
					log.error("冲突座位:" + zwh + " 左:" + left + ":" + busyZwKc.getBean(left));
				if (busyZwKc.containsKey(right))
					log.error("冲突座位:" + zwh + " 右:" + right + ":" + busyZwKc.getBean(right));
			} else {
				log.error("安排座位:" + zwh);
			}

		} catch (Exception e) {

			log.error(e);

			filtBean = null;
		}

		return filtBean;
	}

	/**
	 * 筛选 领导职务考生座位靠前安排
	 * 
	 * @param freeZw
	 * @param leaderBean
	 * @param filtBean
	 */
	private static void filtR007(Bean freeZw, Bean leaderBean, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return;
		}

		Bean temp = new Bean();

		for (Object time : filtBean.keySet()) { // 遍历考试时长

			Bean ksBean = filtBean.getBean(time); // 该时长考生

			Bean uTemp = new Bean();

			for (Object leader : leaderBean.keySet()) { // 遍历领导

				if (ksBean.containsKey(leader)) {

					uTemp.set(leader, ksBean.get(leader));
				}
			}

			if (!uTemp.isEmpty()) {

				temp.put(time, uTemp);
			}
		}

		if (!temp.isEmpty()) {

			filtBean = temp;
		}
	}

	/**
	 * 筛选 特定机构考生场次先后安排
	 * 
	 * @param freeZw
	 * @param res
	 * @param filtBean
	 */
	private static void filtR008(Bean freeZw, KcapResource res, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return;
		}
	}

	/**
	 * 筛选 特定考试仅限于省分行安排
	 * 
	 * @param freeZw
	 * @param res
	 * @param filtBean
	 */
	private static void filtR009(Bean freeZw, Bean rule, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return;
		}

		String kcLv = freeZw.getStr("KC_LV"); // 考场层级 一级考场 二级考场

		String jsonStr = rule.getStr("MX_VALUE2");

		JSONArray obj;

		try {

			if (kcLv.equals("一级")) { // 一级考场

				String[] xlArray = {};

				obj = new JSONArray(jsonStr);

				for (int i = 0; i < obj.length() - 1; i++) {

					JSONObject json = obj.getJSONObject(i);

					String xlCode = json.getString("val");

					xlArray[i] = xlCode;
				}

				Arrays.sort(xlArray); // 使用 Arrays.binarySearch
										// 方法前必须调用Arrays.sort

				Bean temp = new Bean();

				for (Object time : filtBean.keySet()) { // 遍历考试时长

					Bean timeBean = filtBean.getBean(time);

					Bean uTemp = new Bean();

					for (Object user : timeBean.keySet()) { // 遍历时长下考生bean

						Object val = timeBean.get(user);

						if (val instanceof Bean) { // 当前考生报考一个考试

							Bean ks = timeBean.getBean(user);

							if (Arrays.binarySearch(xlArray, ks.getStr("BM_XL")) >= 0) {

								uTemp.put(user, ks);
							}

						} else if (val instanceof List) { // 当前考生报考多个考试

							List<Bean> kslist = timeBean.getList(user);

							for (Bean ks : kslist) {

								if (Arrays.binarySearch(xlArray, ks.getStr("BM_XL")) >= 0) {

									if (uTemp.containsKey(user)) {

										List<Bean> l = new ArrayList<>();

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
					temp.set(time, uTemp);
				}

				if (!temp.isEmpty()) {

					filtBean = new Bean(temp);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

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

			ArrayList<Bean> singleList = new ArrayList<>();

			ArrayList<Bean> multiList = new ArrayList<>();

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
	 * 筛选 60、90分钟考试混排;120分钟混排;150分钟混排
	 * 
	 * @param freeZw
	 * @param busyZwBean
	 * @param filtBean
	 */
	private static void filtKsTime(Bean freeZw, Bean busyZwBean, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return;
		}

		String busyKey = freeZw.getStr("KC_ID") + "^" + freeZw.getStr("SJ_CC") + "^" + freeZw.getStr("SJ_DATE");

		if (busyZwBean.containsKey(busyKey)) {

			Bean busyZwKc = busyZwBean.getBean(busyKey);

			if (busyZwKc != null && !busyZwKc.isEmpty()) {

				Bean tb = new Bean();

				for (Object key : busyZwKc.keySet()) {

					String ksTime = busyZwKc.getBean(key).getStr("BM_KS_TIME");

					tb.set(ksTime, ksTime);
				}

				if (tb.containsKey("60") && !tb.containsKey("90")) { // 考试时长只包含60分钟

					filtBean.remove("90");
					filtBean.remove("120");
					filtBean.remove("150");

				} else if (tb.containsKey("90")) { // 考试时长包含90分钟

					filtBean.remove("120");
					filtBean.remove("150");

				} else if (tb.containsKey("120")) { // 考试时长含120分钟

					filtBean.remove("60");
					filtBean.remove("90");
					filtBean.remove("150");

				} else if (tb.containsKey("150")) { // 考试时长含150分钟

					filtBean.remove("60");
					filtBean.remove("90");
					filtBean.remove("120");
				}
			}
		}
	}

	/**
	 * 过滤 不同考场，相同日期，相同场次的考生
	 * 
	 * @param freeZw
	 * @param busyZwBean
	 * @param filtBean
	 */
	private static void filtKsSameCc(Bean freeZw, Bean busyZwBean, Bean filtBean) {

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

							log.error("移除相同日期，相同场次考生：" + time + ":" + ucode);
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
