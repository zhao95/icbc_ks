package com.rh.ts.xmgl.kcap.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.util.Strings;
import com.rh.ts.xmgl.kcap.KcapResource;

public class KcapUtils {

	private static Log log = LogFactory.getLog(KcapUtils.class);

	/**
	 * 合并bean。key重复，value合并List
	 * 
	 * @param srcBean
	 * @param dstBean
	 * @return
	 */
	public static Bean mergeBean(Bean srcBean, Bean dstBean) {

		// log.error("------------------mergeBean前:");
		//
		// for (Object key : srcBean.keySet()) {
		//
		// if (srcBean.get(key) instanceof Bean) {
		//
		// // log.error("------------------srcBeanBean:" +
		// // srcBean.getBean(key).getStr("SH_ID"));
		//
		// } else if (srcBean.get(key) instanceof List) {
		//
		// log.error("------------------srcBeanList:key=" + key + "--" +
		// srcBean.getList(key).size());
		//
		// }
		//
		// }
		//
		// for (Object key : dstBean.keySet()) {
		//
		// if (dstBean.get(key) instanceof Bean) {
		//
		// // log.error("------------------dstBeanBean:" +
		// // dstBean.getBean(key).getStr("SH_ID"));
		//
		// } else if (dstBean.get(key) instanceof List) {
		//
		// if (dstBean.getList(key).size() == 0) {
		// // log.error("------------------dstBeanList:key="+key+"--" +
		// // dstBean.getList(key).toString());
		// }
		//
		// log.error("------------------dstBeanList:key=" + key + "--" +
		// dstBean.getList(key).size());
		//
		// }
		// }

		Bean rtnBan = (Bean) dstBean.clone();

		for (Object key : srcBean.keySet()) {

			Object srcObj = srcBean.get(key);

			if (rtnBan.containsKey(key)) { // 如果两个bean都存在相同key则 合并bean

				Object dstObj = rtnBan.get(key);

				List<Bean> dstList = new ArrayList<>();

				if (dstObj instanceof Bean) {

					Bean temp = rtnBan.getBean(key);

					dstList.add(temp);

				} else if (dstObj instanceof List) {

					List<Bean> tList = rtnBan.getList(key);

					for (Bean t : tList) {

						if (t != null && !t.isEmpty()) {

							dstList.add(t);
						}
					}
				}

				if (srcObj instanceof Bean) {

					Bean temp = srcBean.getBean(key);

					dstList.add(temp);

				} else if (srcObj instanceof List) {

					List<Bean> tList = srcBean.getList(key);

					for (Bean t : tList) {

						if (t != null && !t.isEmpty()) {

							dstList.add(t);
						}
					}

				}

				rtnBan.set(key, dstList);

			} else { // dstBean不存在

				rtnBan.set(key, srcObj);
			}

		}

		Bean clone = (Bean) rtnBan.clone();

		for (Object key : rtnBan.keySet()) {

			if (rtnBan.get(key) instanceof List) {

				List<Bean> list = rtnBan.getList(key);

				List<Bean> tlist = new ArrayList<Bean>();

				Bean temp = new Bean();

				for (Bean bean : list) {

					if (temp.containsKey(bean.get("SH_ID"))) {

					} else {

						temp.set(bean.get("SH_ID"), bean);

						tlist.add(bean);
					}
				}

				clone.set(key, tlist);
			}
		}

		log.error("------------------mergeBean后:");

		for (Object key : clone.keySet()) {

			if (clone.get(key) instanceof Bean) {

				// log.error("------------------rtnBanBean:" +
				// clone.getBean(key).getStr("SH_ID"));

			} else if (clone.get(key) instanceof List) {

				log.error("------------------rtnBanList:key=" + key + "--" + clone.getList(key).size());

//				List<Bean> list = clone.getList(key);
//				for (Bean bean : list) {
//
//					log.error("------------------------------shId:" + bean.getStr("SH_ID") + "--" + bean.toString());
//				}
			}
		}

		return clone;
	}

	public static String[] sortStr(Bean bean) {

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
	}

	public static int[] sortInt(Bean bean) {

		int sort[] = new int[bean.keySet().size()];

		int i = 0;

		for (Object key : bean.keySet()) { // 遍历场次号

			try {

				sort[i] = Integer.parseInt(key.toString());

				i++;

			} catch (Exception e) {

				e.printStackTrace();
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
	public static String[] sortKcId(Bean bean, KcapResource res) {

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

		Arrays.sort(codeSort);

		for (int j = 0; j < codeSort.length; j++) {

			String code = codeSort[j];

			String[] codeArray = code.split("##");

			sort[j] = codeArray[1];
		}

		return sort;
	}

}
