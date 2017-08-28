package com.rh.core.util.threadpool;

/**
 * 短有效期策略。执行3次，每隔5秒执行一次。
 * 
 * @author yangjy
 */
public class ShortValidity implements ValidityStrategy {
    
    private static final int[] TIMES = new int[3];
    
    static {
        TIMES[0] = 0;
        TIMES[1] = 5;
        TIMES[2] = 10;
    }
    
    @Override
    public int getNextTime(RhThreadTask task) {
        int count = task.getExecCount();
        if (TIMES.length > count) {
            return TIMES[count];
        }
        return -1;
    }
    
    @Override
    public void enqueue(RhThreadTask task) {
        int nextVal = getNextTime(task);
        if (nextVal > 0) {
            ShortPeriodDeferredQueue.add(task, nextVal);
        }
    }

    @Override
    public void dequeue(RhThreadTask task) {
        
    }
}
