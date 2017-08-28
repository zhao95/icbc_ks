package com.rh.core.wfe.ext;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.ParamBean;

/**
 * 接口，供实现类实现，动态修改流程节点名称
 * @author yjzhou
 * modify time 2017.02.21
 */
public interface WfeNodeChangeInterface {
	
	/**
	 * 过滤节点数据
	 * @param paramBean 包含需要的procCode, nodeCode 等参数
	 * @return List<Bean> 每一个bean中包含字段为NODE_CODE,NODE_NAME,否则会被扩展方法作为无效数据过滤掉
	 */
	public List<Bean> filterNode (ParamBean paramBean);
	
	/**
	 * 获取当前处理节点的待修改的名称，返回结果中必须包含{NODE_CODE，NODE_NAME}
	 * @param procCode
	 * @param nodeCode
	 * @return
	 */
	public Bean getInfoByProcAndNode (String procCode, String nodeCode);

}
