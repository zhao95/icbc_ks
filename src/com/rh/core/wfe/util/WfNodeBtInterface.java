package com.rh.core.wfe.util;

import com.rh.core.base.Bean;
import com.rh.core.wfe.WfAct;

/**
 * 节点穿透设置
 * 两中配置方式
 * {'CLASS':'','PARAM':''} //自定义实现类
 * {'CONDITION':'','TARGETNODE':''} //简单条件 ， 
 * 
 * @author anan
 *
 */
public interface WfNodeBtInterface {

	/**
	 * 
	 * @param freeConfig 自由节点的定义
	 * @param servData 数据对象
	 * @param wfAct 节点实例
	 * @param nextNodeDef 下个节点的定义信息
	 * @return 是否满足穿透的条件
	 */
	boolean canBreakThrough(Bean freeConfig, Bean servData, WfAct wfAct, Bean nextNodeDef);
	
	/**
	 * @param freeConfig 自由节点的定义
	 * @param servData 数据对象
	 * @param wfAct 节点实例
	 * @return 指定的穿透到的节点
	 */
	String getAssignNode(Bean freeConfig, Bean servData, WfAct wfAct);
}
