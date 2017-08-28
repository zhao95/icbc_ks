package com.rh.core.util.threadpool;

import java.util.TimerTask;

/**
 * 延时调度任务
 * @author yangjy
 * 
 */
public class ShortPeriodDeferredTask extends TimerTask {
    private RhThreadTask rhTask = null;

    /**
     * 
     * @param rhTask 任务
     */
    public ShortPeriodDeferredTask(RhThreadTask rhTask) {
        super();
        this.rhTask = rhTask;
    }

    @Override
    public void run() {
        RhThreadPool.getDefaultPool().execute(rhTask);
    }

    /**
     * @return 任务对象
     */
    public RhThreadTask getRhTask() {
        return rhTask;
    }
}
