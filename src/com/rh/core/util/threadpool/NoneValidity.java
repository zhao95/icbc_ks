package com.rh.core.util.threadpool;

/**
 * 重复执行策略：失败后不重复执行。
 * @author yangjy
 *
 */
public class NoneValidity implements ValidityStrategy {
    
    @Override
    public int getNextTime(RhThreadTask task) {
        return -1;
    }
    
    @Override
    public void enqueue(RhThreadTask task) {
        
    }
    
    @Override
    public void dequeue(RhThreadTask task) {
        
    }
    
}
