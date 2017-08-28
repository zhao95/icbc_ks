package com.rh.core.wfe.free;

import com.rh.core.base.Bean;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Lang;
import com.rh.core.wfe.WfAct;
import com.rh.core.wfe.util.WfNodeBtInterface;

/**
 * 自由送交 (可送交也可跳过) 的跳过  基础类
 * 实现简单条件 ， 如当前人的环境变量 或者 表单的变量值满足 某条件， 则该点跳过
 * 
 * @author anan
 *
 */
public class SimpleFreeNode implements WfNodeBtInterface {

	@Override
	public boolean canBreakThrough(Bean freeConfig, Bean servData, WfAct wfAct, Bean nextNodeDef) {
		
		String condition = freeConfig.getStr("CONDITION");
    	condition = ServUtils.replaceSysAndData(condition, servData); //替换表单数据和系统变量
    	
    	if (condition.length()==0 || Lang.isTrueScript(condition)) { 
    		return true;
    	}
		
		
		return false;
	}

	@Override
	public String getAssignNode(Bean freeConfig, Bean servData, WfAct wfAct) {
		
		if (freeConfig.isNotEmpty("TARGETNODE") && freeConfig.getStr("TARGETNODE").length() > 0) {
			return freeConfig.getStr("TARGETNODE");
		}
		
		return null;
	}

}
