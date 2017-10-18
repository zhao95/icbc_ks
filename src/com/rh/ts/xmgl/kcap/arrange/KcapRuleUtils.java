package com.rh.ts.xmgl.kcap.arrange;

import com.rh.core.base.Bean;
import com.rh.ts.xmgl.kcap.KcapResource;

public class KcapRuleUtils {

	public static Bean getArrangeKs(Bean freeZw, KcapResource res) {

		boolean rtn = false;

		Bean rule = res.getRuleBean();

		for (Object key : rule.keySet()) {

			switch (key.toString()) {

			case "R001":
				break;
			case "R002":
				break;
			case "R003":
				break;
			case "R004":
				break;
			case "R005":
				break;
			case "R006":
				break;
			case "R007":
				break;
			case "R008":
				break;
			case "R009":
				break;
			default:
				break;
			}
		}

		return null;
	}

}
