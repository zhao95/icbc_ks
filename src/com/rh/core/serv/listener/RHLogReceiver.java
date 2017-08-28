package com.rh.core.serv.listener;

import com.rh.core.base.Context;
import com.rh.core.util.msg.listener.BaseBatchSaveListener;

/**
 * 平台通用日志监听器
 */
public class RHLogReceiver extends BaseBatchSaveListener {
    public static final String SY_COMM_LOG = "SY_COMM_LOG";
    
    /** 构建体方法 */
    public RHLogReceiver() {
        int interval =  Context.getSyConf("SY_COMM_BATCH_SAVE_LOG_INTERVAL", 60);  //1分钟
        int maxSize =  Context.getSyConf("SY_COMM_BATCH_SAVE_LOG_MAX_SIZE", 10);  //10条
        init(interval, maxSize);
    }

    @Override
    protected String getServId() {
        return SY_COMM_LOG;
    }
}
