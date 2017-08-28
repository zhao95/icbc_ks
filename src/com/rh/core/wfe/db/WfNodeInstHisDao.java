package com.rh.core.wfe.db;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.serv.ServDao;
import com.rh.core.util.Constant;

/**
 * 节点实例历史表 数据库操作
 * @author ananyuan
 *
 */
public class WfNodeInstHisDao {
	
	private static Log log = LogFactory.getLog(WfNodeInstHisDao.class);
	
	/**
	 * 工作流节点实例历史 服务 code
	 */
	public static final String SY_WFE_NODE_INST_HIS_SERV = "SY_WFE_NODE_INST_HIS";
	
	/**
	 * 取消办结的时候，通过流程实例对象 删除节点实例历史对象 ， 真删
	 * 
	 * @param procInstId
	 *            流程实例对象ID
	 */
	public static void destroyNodeInstHisBeans(String procInstId) {
		Bean paramBean = new Bean();
		paramBean.set("PI_ID", procInstId);

		ServDao.destroys(SY_WFE_NODE_INST_HIS_SERV, paramBean);
	}
    
	/**
	 * 已办结 取得 节点实例的 历史信息
	 * 
	 * @param piId
	 *            流程实例ID
	 * @return 节点实例的 历史信息
	 */
	public static List<Bean> getNodeInstHisByFinishPiId(String piId) {
		Bean paramBean = new Bean();
		paramBean.set("PI_ID", piId);

		paramBean.set(Constant.PARAM_ORDER, " NODE_ETIME DESC, NODE_BTIME DESC");

		return ServDao.finds(SY_WFE_NODE_INST_HIS_SERV, paramBean);

	}
	
	/**
	 * 取消办结的时候，复制节点实例历史表 列表 到 节点实例表
	 * 
	 * @param procInstId
	 *            节点实例对象ID
	 */
	public static void copyNodeInstHisBeansToInst(String procInstId) {
		log.debug("copy the node inst history data to the inst table");

		String sqlStr = "insert into "
				+ WfNodeInstDao.SY_WFE_NODE_INST_SERV
				+ " (select * from " + SY_WFE_NODE_INST_HIS_SERV
				+ " where PI_ID = '" + procInstId + "')";

		Context.getExecutor().execute(sqlStr);
		
	}
	
    /**
     * 通过节点实例ID 取得 节点实例对象
     * 
     * @param niId
     *            节点实例ID
     * @return 节点实例对象
     */
    public static Bean findNodeInstById(String niId) {
        Bean aNodeInstBean = ServDao.find(SY_WFE_NODE_INST_HIS_SERV, niId);
        
        if (null == aNodeInstBean) {
            String errorMsg = Context.getSyMsg("SY_WF_NODE_INSTHIS_ID_ERROR", niId);
        	
            throw new RuntimeException(errorMsg);
        }

        return aNodeInstBean;
    }
}
