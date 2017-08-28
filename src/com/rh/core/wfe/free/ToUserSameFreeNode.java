package com.rh.core.wfe.free;

import com.rh.core.base.Bean;
import com.rh.core.wfe.WfAct;
import com.rh.core.wfe.util.WfNodeBtInterface;


/**
 * 当前人 要 送交 的 人 就是他 自己， 则跳过该节点，直接往下送交 
 * @author anan
 *
 */
public class ToUserSameFreeNode implements WfNodeBtInterface {

	@Override
	public boolean canBreakThrough(Bean freeConfig, Bean servData, WfAct wfAct, Bean nextNodeDef) {
		
		//获取到要送交的人 ，和当前人比较，如果是一个人 ， 则返回true
		
		
		
		return false;
	}

	@Override
	public String getAssignNode(Bean freeConfig, Bean servData, WfAct wfAct) {
		
		
		
		
		return null;
	}

}
