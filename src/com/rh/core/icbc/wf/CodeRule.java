package com.rh.core.icbc.wf;

import com.rh.core.serv.ParamBean;

/**
 * 编号规则类，负责根据上下文产生编号
 * 
 * @author yangjy
 *
 */
public abstract class CodeRule {
	
	/**
	 * 创建编号
	 * @param paramBean 参数Bean
	 * @return 编号
	 */
	public abstract String createCode(ParamBean paramBean);
}
