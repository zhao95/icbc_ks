package com.rh.ts.xmgl.kcap.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.util.Strings;
import com.rh.ts.xmgl.kcap.KcapResource;
import com.rh.ts.xmgl.kcap.arrange.ArrangeSeat;

public class KcapUtils {

	private static Log log = LogFactory.getLog(ArrangeSeat.class);

	/**
	 * 合并bean。key重复，value合并List
	 * 
	 * @param srcBean
	 * @param dstBean
	 * @return
	 */
	public static Bean mergeBean(Bean srcBean, Bean dstBean) {

		try {
			Bean rtnBan = new Bean(dstBean);

			for (Object key : srcBean.keySet()) {

				Object srcObj = srcBean.get(key);

				if (rtnBan.containsKey(key)) { // 如果两个bean都存在相同key则 合并bean

					Object dstObj = rtnBan.get(key);

					List<Bean> dstList = new ArrayList<Bean>();

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

		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	public static String[] sortStr(Bean bean) {
		try {
			String sort[] = new String[bean.keySet().size()];

			int i = 0;

			for (Object key : bean.keySet()) { // 遍历场次号

				if (!Strings.isBlank(key.toString())) {

					sort[i] = key.toString();

					i++;
				}
			}

			Arrays.sort(sort);

			return sort;
		} catch (Exception e) {
			log.error(e);
			return null;
		}

	}

	public static String[] sortZwStr(Bean bean) {

		try {
			String temp[] = new String[bean.keySet().size()];

			String sort[] = new String[bean.keySet().size()];

			int count = 0;

			Bean keyBean = new Bean();

			for (Object key : bean.keySet()) { // 遍历场次号

				if (!Strings.isBlank(key.toString())) {

					String keystr = key.toString();

					String keyarg[] = keystr.split("-");

					if (keyarg != null && keyarg.length == 2) {

						String row = keyarg[0];

						if (keyarg[0].length() == 1) {
							row = "0" + keyarg[0];
						}

						String col = keyarg[1];

						if (keyarg[1].length() == 1) {
							col = "0" + keyarg[1];
						}

						keystr = row + "-" + col;
					}

					sort[count] = keystr;

					count++;

					keyBean.set(keystr, key.toString());
				}
			}

			Arrays.sort(sort);

			for (int j = 0; j < sort.length; j++) {

				String zw = keyBean.getStr(sort[j]);

				if (Strings.isBlank(zw)) {
					temp[j] = sort[j];
				} else {
					temp[j] = zw;
				}
			}

			return temp;
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	public static int[] sortInt(Bean bean) {

		int sort[] = new int[bean.keySet().size()];

		int i = 0;

		for (Object key : bean.keySet()) { // 遍历场次号

			try {

				sort[i] = Integer.parseInt(key.toString());

				i++;

			} catch (Exception e) {

				log.error(e);
			}
		}

		Arrays.sort(sort);

		return sort;
	}

	/**
	 * 考场排序
	 * 
	 * @param bean
	 * @param res
	 * @return
	 */
	public static String[] sortKcIdold(Bean bean, KcapResource res) {

		try {

			Bean kcBean = res.getKcBean();

			String lvSort[] = new String[bean.keySet().size()];

			int i = 0;

			for (Object key : bean.keySet()) { // 考场

				outloop:

				if (!Strings.isBlank(key.toString())) {

					for (Object ssjg : kcBean.keySet()) {// 所属机构

						List<Bean> list = kcBean.getList(ssjg.toString());

						for (Bean kc : list) {// 考场

							Bean info = kc.getBean("INFO");

							if (info.getStr("KC_ID").equals(key.toString())) {

								String kcCode = info.getStr("KC_CODE");

								int lv = OrgMgr.getDept(ssjg.toString()).getLevel();

								lvSort[i] = lv + "^" + kcCode + "##" + key.toString();

								i++;

								break outloop;
							}
						}
					}
				}
			}

			Arrays.sort(lvSort);

			int kk = 0;

			String codeSort[] = new String[lvSort.length];

			for (int k = lvSort.length - 1; k >= 0; k--) {

				String kclv = lvSort[k];

				String[] kclvArg = kclv.split("\\^");

				codeSort[kk] = kclvArg[1];

				kk++;
			}

			String[] sort = new String[codeSort.length];

			// Arrays.sort(codeSort);

			for (int j = 0; j < codeSort.length; j++) {

				String code = codeSort[j];

				String[] codeArray = code.split("##");

				sort[j] = codeArray[1];
			}

			return sort;

		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	public static String[] sortKcId(Bean bean, KcapResource res) {
		try {

			int count = 0;

			Bean kcBean = res.getKcBean();

			TreeMap<String, Map<String, String>> sortMap = new TreeMap<String, Map<String, String>>(
					new Comparator<String>() {
						public int compare(String o1, String o2) {
							return o2.compareTo(o1);
						}
					});

			for (Object key : bean.keySet()) { // 考场

				outloop:

				if (!Strings.isBlank(key.toString())) {

					for (Object ssjg : kcBean.keySet()) {// 所属机构

						List<Bean> list = kcBean.getList(ssjg.toString());

						for (Bean kc : list) {// 考场

							Bean info = kc.getBean("INFO");

							if (info.getStr("KC_ID").equals(key.toString())) {

								String kcCode = info.getStr("KC_CODE");

								int lv = OrgMgr.getDept(ssjg.toString()).getLevel();

								TreeMap<String, String> codeMap = new TreeMap<String, String>();

								if (sortMap.containsKey(String.valueOf(lv))) {

									codeMap = (TreeMap<String, String>) sortMap.get(String.valueOf(lv));
								}

								codeMap.put(kcCode, key.toString());

								sortMap.put(String.valueOf(lv), codeMap);

								count++;

								break outloop;
							}
						}
					}
				}
			}

			int index = 0;

			String[] sort = new String[count];

			for (Object lv : sortMap.keySet()) {

				TreeMap<String, String> codeMap = (TreeMap<String, String>) sortMap.get(lv);

				for (Object code : codeMap.keySet()) {

					String kcId = codeMap.get(code);

					sort[index] = kcId;

					index++;
				}
			}

			return sort;

		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	public static boolean isEmpty(Bean ksBean) {

		if (ksBean == null || ksBean.isEmpty()) {

			return true;
		} else {

			boolean isnull = true;

			for (Object time : ksBean.keySet()) {

				if (!ksBean.getBean(time).isEmpty()) {

					isnull = false;
				}
			}

			if (isnull) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("rawtypes")
	public static void showInfo(Bean ksBean, String msg, Class clazz) {

		try {

			Log log = LogFactory.getLog(clazz);

			if (ksBean == null || ksBean.isEmpty()) {

				log.debug(msg + "|0");

			} else {

				for (Object time : ksBean.keySet()) {

					log.debug(msg + "|time:" + time.toString() + "|" + ksBean.getBean(time).size());
				}
			}
		} catch (Exception e) {

			log.error(e);
		}
	}

}
