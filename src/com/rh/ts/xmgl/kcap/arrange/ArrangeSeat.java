package com.rh.ts.xmgl.kcap.arrange;

import java.util.ArrayList;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.ts.xmgl.kcap.KcapResource;

public class ArrangeSeat {

	@SuppressWarnings("unchecked")
	public void doArrange(Bean param, KcapResource res) {

		int priority = res.getKsPriority(); // 0 考场优先 1 场次优先

		if (priority == 1) { // 场次优先

			Bean freeBean = new Bean();

			freeBean.putAll(res.getFreeCcZwBean());

			for (Object cc : freeBean.keySet()) { // 遍历场次

				Bean freeCc = freeBean.getBean(cc);

				int sjCC = freeCc.getInt("SJ_CC"); // 场次号

				for (Object kc : freeCc.keySet()) { // 遍历考场

					Bean freeKc = freeCc.getBean(kc);

					for (Object zw : freeKc.keySet()) { // 遍历座位

						Bean freeZw = freeKc.getBean(zw); // 座位信息

						freeZw.set("KC_ID", kc.toString());

						freeZw.set("CC_ID", cc.toString());

						freeZw.set("SJ_CC", sjCC);

						Bean ks = KcapMatch.matchUser(freeZw, res);// 符合座位安排规则的考生

						if (ks != null && !ks.isEmpty()) {

							String shId = ks.getStr("SH_ID"); // 审核id

							String uCode = ks.getStr("U_CODE"); // 考生编码

							String ksOdept = ks.getStr("U_ODEPT"); // 考生机构或借考机构

							res.getFreeCcZwBean().getBean(cc).getBean(kc).remove(zw);// 从未安排座位资源中移除

							Object obj = res.getKsBean().getBean(ksOdept).get(uCode);

							if (obj instanceof Bean) {

								res.getKsBean().getBean(ksOdept).remove(uCode); // 从未安排考生资源中移除

							} else if (obj instanceof List) {

								List<Bean> otherList = new ArrayList<Bean>(); // 考生其他报名list

								for (Bean temp : (List<Bean>) obj) {

									if (!temp.getStr("SH_ID").equals(shId)) {

										otherList.add(temp);
									}
								}

								res.getKsBean().getBean(ksOdept).remove(uCode); // 从未安排考生资源中移除

								if (otherList.size() == 1) {

									res.getKsBean().getBean(ksOdept).set(uCode, otherList.get(0)); // 将当前考生其他报名bean
																									// 放入考生资源bean

								} else if (otherList.size() > 1) {

									res.getKsBean().getBean(ksOdept).set(uCode, otherList); // 将当前考生其他报名list
																							// 放入考生资源bean

								}
							}
						}
					}
				}
			}

		} else { // 考场优先

			Bean freeBean = new Bean();

			freeBean.putAll(res.getFreeKcZwBean());

			for (Object kc : freeBean.keySet()) { // 遍历考场

				Bean freeKc = freeBean.getBean(kc);

				for (Object cc : freeKc.keySet()) { // 遍历场次

					Bean freeCc = freeKc.getBean(cc);

					Bean freeZw = null;

					for (Object zw : freeCc.keySet()) { // 遍历座位

						freeZw = freeCc.getBean(zw); // 座位信息

						freeZw.set("KC_ID", kc.toString());

						freeZw.set("CC_ID", cc.toString());

						Bean ks = KcapMatch.matchUser(freeZw, res);// 符合座位规则的用户

						if (ks != null && !ks.isEmpty()) {

							res.getFreeCcZwBean().getBean(kc).getBean(cc).remove(zw);// 移除未安排座位

							res.getKsBean(); // 移除未安排考生

							ks.getStr("SH_ID");
						}
					}
				}
			}
		}
	}

}
