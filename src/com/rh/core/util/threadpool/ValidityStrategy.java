package com.rh.core.util.threadpool;

/**
 * 任务有效期策略。根据指定的指定次数，计算下次执行时间。
 * 
 * @author yangjy
 */
public interface ValidityStrategy {
    
    /**
     * @param task 任务
     * @return 下次执行任务的时间，单位为秒。如果为-1，表示已经超过执行期限，不用再执行了。
     */
    int getNextTime(RhThreadTask task);
    
    /**
     * 将任务加入队列
     * 
     * @param task 任务
     */
    void enqueue(RhThreadTask task);
    
    /**
     * 把任务移出队列
     * 
     * @param task 任务
     */
    void dequeue(RhThreadTask task);
}
