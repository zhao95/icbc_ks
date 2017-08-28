package com.rh.core.wfe.db;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Constant;
import com.rh.core.util.lang.Assert;
import com.rh.core.wfe.util.WfeConstant;

/**
 * 节点实例 数据库操作类
 * 
 * @author ananyuan
 * 
 */
public class WfNodeInstDao {

	private static Log log = LogFactory.getLog(WfNodeInstDao.class);

	/**
	 * 工作流节点实例 服务 code
	 */
	public static final String SY_WFE_NODE_INST_SERV = "SY_WFE_NODE_INST";
	
	/**
	 * 办结的时候，通过流程实例对象 删除节点实例对象 ， 真删
	 * 
	 * @param procInstId
	 *            流程实例对象ID
	 */
	public static void destroyNodeInstBeans(String procInstId) {
		Bean paramBean = new Bean();
		paramBean.set("PI_ID", procInstId);

		ServDao.destroys(SY_WFE_NODE_INST_SERV, paramBean);
	}
	
	/**
	 * @param pid 流程实例ID
	 * @param nodeCode
	 *            节点编码
	 * @param toUserId 接收人           
	 * @return 指定节点和办理人活动的节点列表
	 */
	public static List<Bean> getListByPreNodeInstAndToUser(String pid, String nodeCode, String toUserId) {
		Assert.hasText(pid, "参数pid不能为空");
		Assert.hasText(nodeCode, "参数nodeCode不能为空");
		Bean paramBean = new Bean();
		paramBean.set("PI_ID", pid);
		paramBean.set("NODE_CODE", nodeCode);
		if(StringUtils.isNotBlank(toUserId)) {
			paramBean.set("TO_USER_ID", toUserId);
		}
		paramBean.set("NODE_IF_RUNNING", WfeConstant.NODE_IS_RUNNING);
		paramBean.set(Constant.PARAM_ORDER, " NODE_BTIME DESC");

		List<Bean> nodeInstList = ServDao.finds(SY_WFE_NODE_INST_SERV,
				paramBean);

		return nodeInstList;
	}

	/**
	 * 未办结 取得 节点实例的 历史信息
	 * 
	 * @param piId
	 *            流程实例ID
	 * @return 节点实例的 历史信息
	 */
	private static List<Bean> getNodeInstHisByNoFinishPiId(String piId) {
		Bean paramBean = new Bean();
		paramBean.set("PI_ID", piId);
		paramBean.set(Constant.PARAM_ORDER, " NODE_ETIME DESC, NODE_BTIME DESC");

		return ServDao.finds(SY_WFE_NODE_INST_SERV, paramBean);
	}

	/**
	 * 插入流程节点实例信息
	 * 
	 * @param nodeBean
	 *            节点信息
	 * @return Bean 节点实例
	 */
	public static Bean insertWfNodeInst(Bean nodeBean, boolean isRunningData) {
	    Bean nodeInstBean = null;
	    if(isRunningData) {
	        nodeInstBean = ServDao.create(SY_WFE_NODE_INST_SERV, nodeBean);
	    } else {
	        nodeInstBean = ServDao.create(WfNodeInstHisDao.SY_WFE_NODE_INST_HIS_SERV, nodeBean);
	    }

		return nodeInstBean;
	}

	/**
	 * 通过节点实例ID 取得 节点实例对象
	 * 
	 * @param niId
	 *            节点实例ID
	 * @return 节点实例对象
	 */
	public static Bean findNodeInstById(String niId) {
		Bean aNodeInstBean = ServDao.find(SY_WFE_NODE_INST_SERV, niId);

		if (null == aNodeInstBean) {
			String errorMsg = Context
					.getSyMsg("SY_WF_NODE_INST_ID_ERROR", niId);

			throw new RuntimeException(errorMsg);
		}

		return aNodeInstBean;
	}

	/**
	 * 更新流程节点实例信息
	 * 
	 * @param nodeBean
	 *            节点信息
	 */
	public static void updateWfNodeInst(Bean nodeBean, boolean isRunningData) {
	    if (isRunningData) {
	        ServDao.update(SY_WFE_NODE_INST_SERV, nodeBean);
	    } else {
	        ServDao.update(WfNodeInstHisDao.SY_WFE_NODE_INST_HIS_SERV, nodeBean);
	    }
	}
	
	/**
	 * 
	 * @return 取得表里所有的字段
	 */
	private static String getInserItemNames() {
		ServDefBean sdb = ServUtils.getServDef(SY_WFE_NODE_INST_SERV);
		return sdb.getTalbeItemNames();
	}
	

	/**
	 * 办结的时候，复制节点实例列表 到 节点实例历史表
	 * 
	 * @param procInstId
	 *            节点实例对象ID
	 */
	public static void copyNodeInstBeansToHis(String procInstId) {
		log.debug("copy the node inst data to the history table");
		final String fields = getInserItemNames(); 
		
		StringBuilder sqlStr = new StringBuilder();
		sqlStr.append("insert into ");
		sqlStr.append(WfNodeInstHisDao.SY_WFE_NODE_INST_HIS_SERV);
		sqlStr.append(" (").append(fields).append(")");
		sqlStr.append(" select ").append(fields).append(" from ").append(SY_WFE_NODE_INST_SERV);
		sqlStr.append(" where PI_ID = '").append(procInstId).append("'");

		Context.getExecutor().execute(sqlStr.toString());
	}

	/**
	 * 取得流程的节点实例历史列表
	 * 
	 * @param procInstId
	 *            流程实例ID
	 * @param isRunningData
	 *            是否读取活动表数据。true读取活动表数据，否则读取历史表数据。
	 * @return 流程实例历史表
	 */
	public static List<Bean> findNodeInstHisList(String procInstId,
			boolean isRunningData) {

		// 未办结查询实例表，已办结查询实例历史表
		if (isRunningData) {
			return getNodeInstHisByNoFinishPiId(procInstId);
		} else {
			return WfNodeInstHisDao.getNodeInstHisByFinishPiId(procInstId);
		}
	}
}
