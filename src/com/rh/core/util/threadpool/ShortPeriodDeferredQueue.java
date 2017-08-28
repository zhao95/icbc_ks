package com.rh.core.util.threadpool;

import java.util.Timer;

/**
 * 短期延时处理队列
 * 
 * @author yangjy
 */
public class ShortPeriodDeferredQueue {
    
    private static Timer timer = new Timer();
    
    /**
     * 增加延时任务
     * 
     * @param task 任务
     * @param nextTime 下次执行时间，单位为秒
     */
    public static void add(RhThreadTask task , int nextTime) {
        ShortPeriodDeferredTask timeTask = new ShortPeriodDeferredTask(task);
        timer.schedule(timeTask, nextTime * 1000);
    }
}
