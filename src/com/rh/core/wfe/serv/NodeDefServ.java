package com.rh.core.wfe.serv;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.JsonUtils;

/**
 * 流程节点定义 的 服务类
 * @author anan
 *
 */
public class NodeDefServ extends CommonServ {
	
	/** 节点能通过线到达的点 */
	public static final String QUERY_NODE_CAN_TO = "QUERY_NODE_CAN_TO";
	
    @Override
    public OutBean query(ParamBean paramBean) {
    	
    	if (paramBean.containsKey(QUERY_NODE_CAN_TO)) { //查询节点能到的， 即从当前点能到 + 能直接返回的
    		String procCode = paramBean.getStr("PROC_CODE");
    		String nodeCode = paramBean.getStr("NODE_CODE");
    		
    		String strWhere = " and PROC_CODE = '" + procCode
    		        + "' and NODE_CODE in (select TAR_NODE_CODE  from SY_WFE_LINE_DEF where proc_code = '"
		               + procCode + "' and SRC_NODE_CODE = '" + nodeCode + "' union "
		               + "select SRC_NODE_CODE from SY_WFE_LINE_DEF where proc_code = '"
		               + procCode + "' and TAR_NODE_CODE = '" + nodeCode + "' and LINE_IF_RETURN = 1)";

    		paramBean.set("_extWhere", strWhere);
    		OutBean out = super.query(paramBean);
    		
    		List<Bean> nodeList = out.getDataList();
    		
    		String multiNodeAttr = paramBean.getStr("MULTI_NODE_ATTR");
    		
    		/**
    		 * 
	    		 送交两个节点的改成配置成这种
				{"NODE_NAME":"送主办会办", "NODES":[{"NODE_CODE":"N1", "USER_LABEL":"处理人"},
				{"NODE_CODE":"N2", "USER_LABEL":"会办人员"}]}
				送交前台的下一步的节点数据
				{"NODE_NAME":"送主办会办", "NODE_CODE":"VP-N1-N2"}
    		 * 
    		 */
    		if (multiNodeAttr.length() > 0) {
        		Bean virtualNode = new Bean();
        		Bean multiConf = JsonUtils.toBean(multiNodeAttr);
        		List<Bean> nodes = multiConf.getList("NODES");
        		StringBuilder sb = new StringBuilder("VP");
        		for (Bean node: nodes) {
        			sb.append("-").append(node.getStr("NODE_CODE"));
        		}
        		virtualNode.set("NODE_NAME", multiConf.getStr("NODE_NAME"));
        		virtualNode.set("NODE_CODE", sb.toString());
        		
        		nodeList.add(virtualNode);
    		}
            
            return out;
    	} 
    	
    	
    	return super.query(paramBean);
    }
	
}
