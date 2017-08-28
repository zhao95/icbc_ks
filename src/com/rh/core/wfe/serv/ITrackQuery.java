package com.rh.core.wfe.serv;

import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;

/**
 * 流程跟踪接口：
 * 对流程跟踪处理类TrackServ的补充处理类
 * 配置：SY_TRACK_QUERY_INTERFACE
 */
public interface ITrackQuery {
	public static final String SY_TRACK_QUERY_INTERFACE = "SY_TRACK_QUERY_INTERFACE";
	
	void beforeQuery(ParamBean paramBean);
	void afterQuery(ParamBean paramBean , OutBean dataBean);
}
