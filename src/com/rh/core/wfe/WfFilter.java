package com.rh.core.wfe;

import com.rh.core.wfe.serv.WfOut;

/**
 * 表单定义
 * 
 */
public interface WfFilter {

	/**
	 * 
	 * @param wfAct 当前流程节点实例
	 * @param wfOutBean 工作流输出给前台的所有数据Bean。
	 *            
	 */
	void doButtonFilter(WfAct wfAct, WfOut wfOutBean);

}