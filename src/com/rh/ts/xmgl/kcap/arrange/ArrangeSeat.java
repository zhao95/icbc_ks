package com.rh.ts.xmgl.kcap.arrange;

import com.rh.core.base.Bean;
import com.rh.ts.xmgl.kcap.KcapResource;
import com.rh.ts.xmgl.kcap.KcapRuleEnum;

public class ArrangeSeat {

	public void doArrange(Bean param, KcapResource res) {

		int priority = res.getKsPriority(); // 0 考场优先 1 场次优先

		Bean freeBean = null;

		if (priority == 1) {

			freeBean = res.getFreeZwCcBean();

			Bean freeCc = null;

			for (Object cc : freeBean.keySet()) { // 遍历场次

				freeCc = freeBean.getBean(cc);

				Bean freeKc = null;

				for (Object kc : freeCc.keySet()) { // 遍历考场

					freeKc = freeCc.getBean(kc);

					Bean freeZw = null;

					for (Object zw : freeKc.keySet()) { // 遍历座位

						freeZw = freeZw.getBean(zw); // 座位信息
						
						Bean ks = KcapRuleUtils.getArrangeKs(freeZw, res);
						
						if (ks != null) {
							freeZw.remove(zw); //未安排座位中移除
						}
					}
				}
			}

		} else {

			res.getFreeZwKcBean();
		}

	}

}
