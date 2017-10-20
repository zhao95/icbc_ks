package com.rh.ts.xmgl.kcap.arrange;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rh.core.base.Bean;
import com.rh.ts.xmgl.kcap.KcapResource;

public class KcapMatch {

	/**
	 * 匹配符合规则的考生bean
	 * 
	 * @param freeZw
	 *            单个座位信息
	 * @param res
	 *            资源
	 * @return Bean 报名考生信息
	 */
	public static Bean matchUser(Bean freeZw, KcapResource res) {

		Bean filtBean = new Bean(); // {考生编码：单个bean/多个list}

		Bean ksBean = res.getKsBean(); // {机构ID:考生Bean{考生ID报名Bean/报名List}}

		List<Bean> jgList = freeZw.getList("GLJG");// 关联机构

		for (Bean jg : jgList) {

			String dCode = jg.getStr("JG_CODE");

			filtBean.putAll(ksBean.getBean(dCode));
		}

		Bean rule = res.getRuleBean();

		for (Object key : rule.keySet()) {

			switch (key.toString()) {

			case "R001":

				// 相同考试前后左右不相邻
				filtR001(freeZw, res, filtBean);

				break;
			case "R002":

				// 同一考生同一场场次连排
				filtR002(freeZw, res, filtBean);

				break;
			case "R003":

				// 距离远近规则
				filtR003(freeZw, res, filtBean);

				break;
			case "R004":

				// 同一网点级机构考生均分安排
				filtR004(freeZw, res, filtBean);

				break;
			case "R005":

				// 来自同一机构考生不连排
				filtR005(freeZw, res, filtBean);

				break;
			case "R006":

				// 考生人数少于机器数一半时，考生左右间隔不低于1个座位，前后不低于1个
				filtR006(freeZw, res, filtBean);

				break;
			case "R007":

				// 领导职务考生座位靠前安排
				filtR007(freeZw, res, filtBean);

				break;
			case "R008":

				// 特定机构考生场次先后安排
				filtR008(freeZw, res, filtBean);

				break;
			case "R009":

				// 特定考试仅限于省分行安排
				filtR009(freeZw, res, filtBean);

				break;
			default:
				break;
			}
		}

		if (!filtBean.isEmpty()) {

			List<Object> keys = new ArrayList<Object>(filtBean.keySet());

			int index = new Random().nextInt(keys.size()); // 随机获取考生编码

			String key = (String) keys.get(index);

			return filtBean.getBean(key);
		}

		return null;
	}

	/**
	 * 筛选 相同考试前后左右不相邻的考生
	 * 
	 * @param freeZw
	 * @param res
	 * @param filtBean
	 *            报名考生信息Bean
	 */
	private static void filtR001(Bean freeZw, KcapResource res, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return;
		}

		try {

			Bean busyZwBean = res.getBusyZwBean();

			String kcId = freeZw.getStr("KC_ID"); // 考场id

			String ccId = freeZw.getStr("CC_ID"); // 场次id

			String ccDate = freeZw.getStr("SJ_DATE"); // 考试日期

			String zwhKey = ccDate + "^" + kcId + "^" + ccId + "^";

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
	 * @param zwInfo
	 */
	private static void filtR001Ks(Bean filtBean, Bean zwInfo) {

		if (zwInfo != null && !zwInfo.isEmpty()) {

			String ucode = zwInfo.getStr("U_CODE");

			String shId = zwInfo.getStr("SH_ID");

			if (filtBean.containsKey(ucode)) {

				filtBean.remove(ucode); // 移除考生本人
			}

			Bean temp = new Bean();

			temp.putAll(filtBean);

			for (Object key : temp.keySet()) {

				Object val = temp.get(key);

				if (val instanceof Bean) {

					Bean ks = (Bean) val;

					// 判断是否相同考试的考生
					if (shId.equals(ks.getStr("SH_ID"))) {

						filtBean.remove(key); // 移除相同考试考生
					}
				}
			}
		}
	}

	/**
	 * 筛选 同一考生同一场场次的考生
	 * 
	 * @param freeZw
	 * @param res
	 * @param filtBean
	 *            报名考生信息Bean
	 */
	private static void filtR002(Bean freeZw, KcapResource res, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return;
		}

		try {

			Bean busyZwBean = res.getBusyZwBean();

			String kcId = freeZw.getStr("KC_ID"); // 考场id

			String ccId = freeZw.getStr("CC_ID"); // 场次id

			// int sjCC = freeZw.getInt("SJ_CC"); // 场次号

			String zwh = freeZw.getStr("ZW_ZWH_XT"); // 座位号 (行-列)

			// String key = kcId + "^" + ccId + "^" + zwh;

			Bean filtTempBean = new Bean();

			for (Object key : busyZwBean.keySet()) {

				try {

					String[] array = key.toString().split("^");

					String kcIdTemp = array[0];

					String ccIdTemp = array[1];

					String zwhTemp = array[2];

					if (kcIdTemp.equals(kcId) && !ccIdTemp.equals(ccId) && zwhTemp.equals(zwh)) {

						Bean busy = busyZwBean.getBean(key);// 相同考场相同座位不同场次的座位安排bean

						String ucode = busy.getStr("U_CODE"); // 考生编码

						Bean filtUserBean = filtBean.getBean(ucode);

						if (filtUserBean != null && !filtUserBean.isEmpty()) {

							filtTempBean.set(ucode, filtUserBean);
						}
					}
				} catch (Exception e) {

				}
			}

			if (!filtTempBean.isEmpty()) {

				filtBean = new Bean();

				filtBean.putAll(filtTempBean);
			}

		} catch (Exception e) {

			filtBean = null;
		}
	}

	/**
	 * 筛选 距离近的考生
	 * 
	 * @param freeZw
	 * @param res
	 * @param filtBean
	 *            {考生ID:考生bean}
	 */
	private static void filtR003(Bean freeZw, KcapResource res, Bean filtBean) {

		String kcId = freeZw.getStr("KC_ID"); // 考场id

		Bean farKsBean = res.getFarKsBean().getBean(kcId);

		// Bean cloneBean = (Bean) filtBean.clone();

		for (Object key : farKsBean.keySet()) {

			if (filtBean.containsKey(key)) {

				filtBean.remove(key); // 移除距离远的考生
			}
		}

		if (filtBean == null || filtBean.isEmpty()) {
			return;
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

		if (filtBean == null || filtBean.isEmpty()) {
			return;
		}
	}

	/**
	 * 筛选 相邻座位 来自同一机构考生不连排
	 * 
	 * @param freeZw
	 * @param res
	 * @param filtBean
	 *            {考生ID:考生bean}
	 */
	private static void filtR005(Bean freeZw, KcapResource res, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return;
		}
	}

	private static void filtR006(Bean freeZw, KcapResource res, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return;
		}
	}

	private static void filtR007(Bean freeZw, KcapResource res, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return;
		}
	}

	private static void filtR008(Bean freeZw, KcapResource res, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return;
		}
	}

	private static void filtR009(Bean freeZw, KcapResource res, Bean filtBean) {

		if (filtBean == null || filtBean.isEmpty()) {
			return;
		}
	}

}
