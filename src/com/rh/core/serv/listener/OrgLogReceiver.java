package com.rh.core.serv.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Context;
import com.rh.core.util.msg.listener.BaseBatchSaveListener;

public class OrgLogReceiver extends BaseBatchSaveListener{
	/** log */
    private static Log log = LogFactory.getLog(OrgLogReceiver.class);
    /** service id */
    private static final String SERV_ID = "SY_ORG_MIGRATE_LOG";
    
    /** 构建体方法 */
    public OrgLogReceiver() {
        int interval =  Context.getSyConf("SY_ORG_MIGRATE_LOG_BATCH_INTERVAL", 60);  //1分钟
        int maxSize =  Context.getSyConf("SY_ORG_MIGRATE_LOG_BATCH_MAX_SIZE", 10);  //10条
        super.init(interval, maxSize);
        log.info("OrgLogReceiver start...");
    }

    @Override
    protected String getServId() {
        return SERV_ID;
    }
}
