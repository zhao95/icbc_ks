package com.rh.core.wfe;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.base.Bean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.wfe.db.ServDataDao;

public class TestWfToNext extends TestEnv {
	/** log */
	private static Log log = LogFactory.getLog(TestWfToNext.class);
	
	@Test
	public void testWfStart() {
		//通过 servid doc 得到node Inst Id 
		Bean servBean = ServDataDao.findServInst("WF_TEST", "123456");
		String nodeInstId = servBean.getStr("WF_INST_ID");
		
		log.debug("serv bean node inst ID = " + nodeInstId);
		
		ParamBean parambean = new ParamBean(ServMgr.SY_WFE_PROC_DEF, "toNext");
		parambean.set("NI_ID", nodeInstId); 
		//下个节点 CODE
		parambean.set("NODE_CODE", "N3"); 
		parambean.set("TO_USERS", "yuananan"); 
		parambean.set("TO_TYPE", WfParam.TYPE_TO_USER); 
		ServMgr.act(parambean);
	}
}
