package com.rh.core.wfe.util;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.DateUtils;
import com.rh.core.wfe.WfAct;

/**
 * 流程运行日志记录处理类
 * @author Tanyh 20160607
 *
 */
public class WfLogHelper {

	/** 流程运行日志服务ID **/
	public final static String WFE_LOG_SERV = "SY_WFE_RUN_LOG";
	
	/** 记录日志类型   异常数据 **/
	public final static int LOG_TYPE_EXCEPTION = 1;
	/** 记录日志类型   错误提示 **/
	public final static int LOG_TYPE_ERROR = 2;
	/** 记录日志类型   其它 **/
	public final static int LOG_TYPE_OTHER = 3;
	
	/**
	 * 写入日志
	 * @param wfAct 节点实例对象
	 * @param logDesc 记录描述
	 * @param type 记录日志类型
	 */
	public static void writeLog(WfAct wfAct, String logDesc, int type) {
		//填充数据
		Bean dataBean = null;
		List<Bean> logBeanList = ServDao.finds(WFE_LOG_SERV, (new SqlBean()).set("NI_ID", wfAct.getId()));
		//判断是否已有记录
		if (logBeanList != null && logBeanList.size() > 0) {
			dataBean = logBeanList.get(0);
			//将记录次数加1
			dataBean.set("LOG_COUNT", dataBean.getInt("LOG_COUNT") + 1);
		} else {
			dataBean = fillData(wfAct, logDesc, type);
		}
		//写入日志库
		ServDao.save(WFE_LOG_SERV, dataBean);
	}
	
	/**
	 * 写入日志
	 * @param logDesc 记录描述
	 * @param type 记录日志类型
	 */
	public static void writeLog(String logDesc, int type) {
		//填充数据
		Bean dataBean = fillData(null, logDesc, type);
		//写入日志库
		ServDao.save(WFE_LOG_SERV, dataBean);
	}
	
	/**
	 * 填充日志数据
	 * @param wfAct 节点实例对象
	 * @param logDesc 记录描述
	 * @param type 记录日志类型
	 * @return dataBea 返回数据对象
	 */
	private static Bean fillData(WfAct wfAct, String logDesc, int type) {
		Bean dataBean = new Bean();
		if (wfAct != null) {
			//流程实例ID
			dataBean.set("PI_ID", wfAct.getProcess().getId());
			//流程编码
			dataBean.set("PI_CODE", wfAct.getProcess().getCode());
			//节点实例ID
			dataBean.set("NI_ID", wfAct.getId());
			//节点编码
			dataBean.set("NI_CODE", wfAct.getCode());
			//节点名称
			dataBean.set("NI_NAME", wfAct.getNodeInstBean().getStr("NODE_NAME"));
		}
		//记录次数
		dataBean.set("LOG_COUNT", 1);
		//记录日志类型
		dataBean.set("LOG_TYPE", type);
		//记录时间
		dataBean.set("LOG_TIME", DateUtils.getDatetime());
		//记录描述
		dataBean.set("LOG_DESC", logDesc);
		return dataBean;
	}
}
