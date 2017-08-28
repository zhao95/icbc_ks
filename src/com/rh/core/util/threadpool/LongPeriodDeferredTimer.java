package com.rh.core.util.threadpool;

import java.util.Timer;

/**
 * 
 * @author yangjy
 *
 */
public class LongPeriodDeferredTimer {
    private static Timer timer = new Timer();
    
    static {
        long period = 60 * 1000;
        LongPeriodDeferredTask timeTask = new LongPeriodDeferredTask();
        timer.schedule(timeTask, period, period);
    }
    
    /**
     * 加载服务监听
     */
    public void start() {
        
    }
    
    /**
     * 销毁
     */
    public void stop() {
        timer.cancel();
    }
}
