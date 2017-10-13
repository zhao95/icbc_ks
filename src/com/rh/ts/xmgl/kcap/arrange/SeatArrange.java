package com.rh.ts.xmgl.kcap.arrange;

import com.rh.core.base.Bean;
import com.rh.ts.xmgl.kcap.KcapResource;

public class SeatArrange {
	
	public Bean getSeatArrange(Bean param,KcapResource resource) {
		
		Bean rule = resource.getRuleBean();
		
		for (Object key : rule.keySet()) {
			
			if(key.equals("")) {
				
			}
			
		}
		
		return null;
	}
	
}
