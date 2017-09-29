package com.rh.ts.xmgl.kcap.rule.impl;

import com.rh.ts.xmgl.kcap.KcapResource;
import com.rh.ts.xmgl.kcap.rule.IKcapRule;

/**
 * 相同考试前后左右不相邻
 * @author zjl
 *
 */
public class AroundKeepoff implements IKcapRule {

	
	public void ArrangeSeat(KcapResource resource) {
		
		resource.getKcBean();
		
		resource.getKsBean();
	}

}
