package com.rh.ts.xmgl.rule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RuleMgr {

	private static Log log = LogFactory.getLog(RuleServ.class);

	public static IRule getInstance(String implClass) {

		IRule object = null;
		try {
			object = (IRule) Class.forName(implClass).newInstance();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return object;
	}

}
