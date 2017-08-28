package com.rh.core.wfe.db;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.util.ServUtils;

/**
 * 节点用户实例
 * @author anan
 *
 */
public class WfNodeUserDao {
    private static Log log = LogFactory.getLog(WfNodeUserDao.class);

    /**
     * 节点用户实例 服务 code
     */
    public static final String SY_WFE_NODE_USERS = "SY_WFE_NODE_USERS";
    
    /**
     * 节点实例用户 视图， 视图上有 是否运行的状态
     */
    public static final String SY_WFE_NODE_USERS_V = "SY_WFE_NODE_USERS_V";

    /**
     * 
     * @param nid 节点实例ID
     * @return 节点上的人的列表
     */
    public static List<Bean> getUserList(String nid) {
        SqlBean sqlBean = new SqlBean();
        sqlBean.and("NI_ID", nid);
        
        return ServDao.finds(SY_WFE_NODE_USERS, sqlBean);
    }
    
    /**
     * @param userId 用户ID
     * @return 通过用户ID找到节点列表
     */
    public static List<Bean> getUserList(String piId, String userId) {
        SqlBean sqlBean = new SqlBean();
        sqlBean.and("PI_ID", piId);
        sqlBean.and("TO_USER_ID", userId);
        sqlBean.orders("S_ATIME desc");
        
        return ServDao.finds(SY_WFE_NODE_USERS, sqlBean);
    }
    
	/**
	 * 
	 * @return 取得表里所有的字段
	 */
	private static String getInserItemNames() {
		ServDefBean sdb = ServUtils.getServDef(SY_WFE_NODE_USERS);
		return sdb.getTalbeItemNames();
	}
    
    /**
     * 办结时，将节点用户信息复制到历史表
     * @param pid 流程实例ID
     */
    public static void copyNodeUserToHis(String pid) {
        log.debug("copy the node user inst data to the history table");
        final String fields = getInserItemNames();
        StringBuilder sqlStr = new StringBuilder();
        sqlStr.append("insert into ").append(WfNodeUserHisDao.SY_WFE_NODE_USERS_HIS);
        sqlStr.append(" (").append(fields).append(") ");
        sqlStr.append(" select ").append(fields).append(" from ");
        sqlStr.append(SY_WFE_NODE_USERS).append(" where PI_ID = '");
        sqlStr.append(pid).append("'");
        
//        String sqlStr = "insert into "
//                + WfNodeUserHisDao.SY_WFE_NODE_USERS_HIS
//                + " (select * from " + SY_WFE_NODE_USERS
//                + " where PI_ID = '" + pid + "')";

        Context.getExecutor().execute(sqlStr.toString());
    }

    /**
     * 办结时，将节点用户信息复制到里似乎表, 之后，删除实例表中的数据
     * @param pid 流程实例ID
     */
    public static void destroyNodeUserToHis(String pid) {
        Bean paramBean = new Bean();
        paramBean.set("PI_ID", pid);

        ServDao.destroys(SY_WFE_NODE_USERS, paramBean);
    }
}
