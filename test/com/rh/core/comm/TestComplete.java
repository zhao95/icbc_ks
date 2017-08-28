package com.rh.core.comm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServMgr;

/**
 * 测试完整度功能模块
 * @author wangchen
 */
public class TestComplete extends TestEnv{
    /** log */
    private static Log log = LogFactory.getLog(TestComplete.class);
    
    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        TestEnv.start();
        log.debug("TestComplete begin!");
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        log.debug("TestComplete end!");
        TestEnv.stop();       
    }
    
    @Test
    public void testComplete() {
        CompleteDegreeMgr.initCompleteDegSettings();
        ParamBean param = new ParamBean();
        param.setServId("SY_COMM_COMPLETE_DATA");
        param.setAct("getDeg");
        param.set("SRC_SERV_CODE", "USERINFO");
        param.set("DATA_ID", "wangchen");
        ServMgr.act(param);
    }

}
