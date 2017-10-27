package com.rh.ts.xmgl.kcap.arrange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.ts.xmgl.kcap.KcapResource;

public class KcapMatch {

	/**
	 * 匹配符合规则的考生bean
	 * 
	 * @param freeZw
	 *            单个座位信息
	 * @param ksBean
	 *            待安排考生 {时长：bean{考生编码：单个bean/多个list}}
	 * @param res
	 *            资源
	 * @return Bean 报名考生信息
	 */
	public static Bean matchUser(Bean freeZw, Bean ksBean, KcapResource res, boolean isConstrain) {

		Bean filtBean = new Bean(ksBean);

		Bean rule = res.getRuleBean();

		rtnLoop:

		for (Object key : rule.keySet()) {
			
			Bean constrainFiltBean = new Bean();

			if (isConstrain) {

				constrainFiltBean = (Bean) filtBean.clone();
			}

			switch (key.toString()) {

			case "R001":

				// 相同考试前后左右不相邻
				filtR001(freeZw, res.getBusyZwBean(), filtBean);

				break;
			case "R002":

				// 同一考生同一场场次连排 (同一考生 同一天 同一考场 同一座位 上一场次)
				boolean isRtn = filtR002(freeZw, res.getBusyZwBean(), filtBean);

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
				filtR005(freeZw, res.getBusyZwBean(), filtBean);

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

				filtBean = constrainFiltBean;
			}
		}

		if (!filtBean.isEmpty()) {

			ArrayList<Bean> list = new ArrayList<>();

			for (Object time : filtBean.keySet()) { // 遍历考试时长

				Bean timeBean = filtBean.getBean(time);

				for (Object user : filtBean.keySet()) { // 遍历时长下考生bean

					Object val = timeBean.get(user);

					if (val instanceof Bean) { // 当前考生报考一个考试

						Bean ks = timeBean.getBean(user);

						list.add(ks);

					} else if (val instanceof List) { // 当前考生报考多个考试

						List<Bean> kslist = timeBean.getList(user);

						list.addAll(kslist);
					}
				}
			}

			int index = new Random().nextInt(list.size()); // 随机获取list索引

			return list.get(index);
		}

		return new Bean();
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

			String zwh = freeZw.getStr("ZW_ZWH_XT"); // 座位号 (行-列)

			String[] zwhArray = zwh.split("-");

			int row = Integer.parseInt(zwhArray[0]);

			int col = Integer.parseInt(zwhArray[1]);

			String front = zwhKey + "^" + (row - 1) + "-" + col; // 前

			if (busyZwBean.containsKey(front)) {

				filtR001Ks(filtBean, busyZwBean.getBean(front));
			}

			String back = zwhKey + "^" + (row + 1) + "-" + col; // 后

			if (busyZwBean.containsKey(back)) {

				filtR001Ks(filtBean, busyZwBean.getBean(back));
			}

			String left = zwhKey + "^" + row + "-" + (col - 1); // 左

			if (busyZwBean.containsKey(left)) {

				filtR001Ks(filtBean, busyZwBean.getBean(left));
			}

			String right = zwhKey + "^" + row + "-" + (col + 1); // 右

			if (busyZwBean.containsKey(right)) {

				filtR001Ks(filtBean, busyZwBean.getBean(right));
			}

		} catch (Exception e) {

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

				Bean timeBean = temp.getBean(time);

				for (Object key : timeBean.keySet()) { // 遍历时长下考生bean

					if (ucode.equals(key.toString())) {

						filtBean.getBean(time).remove(ucode); // 移除考生本人

						continue;
					}

					Object val = timeBean.get(key);

					if (val instanceof Bean) { // 当前考生报考一个考试

						Bean ks = timeBean.getBean(key);

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
	 * 筛选 同一天 同一考场 同一座位 上一场次考试的考生 (如果有值)
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

			// String ccId = freeZw.getStr("CC_ID"); // 场次id

			int cc = freeZw.getInt("SJ_CC"); // 场次号

			String ccDate = freeZw.getStr("SJ_DATE"); // 考试日期

			String zwh = freeZw.getStr("ZW_ZWH_XT"); // 座位号 (行-列)

			// String zwhKey = kcId + "^" + cc + "^" + ccDate + "^" + zwh;

			Bean filtTemp = new Bean();

			for (Object key : busyZwBean.keySet()) {

				try {

					String[] array = key.toString().split("^");

					String kcIdTemp = array[0];

					int ccTemp = Integer.parseInt(array[1]);

					String dateTemp = array[2];

					String zwhTemp = array[3];

					// 同一天 同一考场 同一座位 上一场次
					if (dateTemp.equals(ccDate) && kcIdTemp.equals(kcId) && zwhTemp.equals(zwh) && ccTemp == (cc - 1)) {

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
					}
				} catch (Exception e) {

				}
			}

			if (!filtTemp.isEmpty()) {

				filtBean = new Bean(filtTemp);

				return true;
			}

		} catch (Exception e) {

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

			String zwh = freeZw.getStr("ZW_ZWH_XT"); // 座位号 (行-列)

			String[] zwhArray = zwh.split("-");

			int row = Integer.parseInt(zwhArray[0]);

			int col = Integer.parseInt(zwhArray[1]);

			String left = zwhKey + "^" + row + "-" + (col - 1); // 左

			if (busyZwBean.containsKey(left)) {

				filtR005Ks(filtBean, busyZwBean.getBean(left));
			}

			String right = zwhKey + "^" + row + "-" + (col + 1); // 右

			if (busyZwBean.containsKey(right)) {

				filtR005Ks(filtBean, busyZwBean.getBean(right));
			}

		} catch (Exception e) {

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

			String zwh = freeZw.getStr("ZW_ZWH_XT"); // 座位号 (行-列)

			String[] zwhArray = zwh.split("-");

			int row = Integer.parseInt(zwhArray[0]);

			int col = Integer.parseInt(zwhArray[1]);

			String front = zwhKey + "^" + (row - 1) + "-" + col; // 前
			String back = zwhKey + "^" + (row + 1) + "-" + col; // 后
			String left = zwhKey + "^" + row + "-" + (col - 1); // 左
			String right = zwhKey + "^" + row + "-" + (col + 1); // 右

			if (busyZwBean.containsKey(front) || busyZwBean.containsKey(back) || busyZwBean.containsKey(left)
					|| busyZwBean.containsKey(right)) {

				filtBean = null;
			}

		} catch (Exception e) {

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

		String kcLv = freeZw.getStr("KC_LV"); //考场层级 一级考场 二级考场

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

}
