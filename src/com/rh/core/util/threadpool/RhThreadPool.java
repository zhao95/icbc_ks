package com.rh.core.util.threadpool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 线程池
 * @author yangjy
 */
public class RhThreadPool {
    /**日志*/
    protected static Log log = LogFactory.getLog(RhThreadPool.class);
    
    /** 最大线程数 **/
    private static int maxThreadNum = 10;

    private static int minThreadNum = 5;

    /** 空闲时间，默认10分钟 **/
    private static int idelTIme = 600;

    /** 队列数量 **/
    private static int queueNum = Integer.MAX_VALUE;

    private static RhThreadPool defaultPool = new RhThreadPool(minThreadNum,
            maxThreadNum, queueNum);

    /** 线程池 */
    private ThreadPoolExecutor threadPool = null;

    /**
     * 
     * @param minThread 最小线程数
     * @param maxThread 最大线程数
     * @param queueNum 队列大小
     */
    public RhThreadPool(int minThread, int maxThread, int queueNum) {
        threadPool = new ThreadPoolExecutor(minThread, maxThread, idelTIme,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(queueNum));
    }

    /**
     * 执行线程任务
     * 
     * @param task 线程任务
     */
    public void execute(RhThreadTask task) {
        while (true) {
            try {
                threadPool.execute(task);
                break;
            } catch (RejectedExecutionException e) {
                try {
                    Thread.sleep(10);
                } catch (Exception ex) {
                    log.warn(ex.getMessage());
                }
            }
        }
    }

    /**
     * 
     * @return 线程池的任务是否执行完成？
     */
    public boolean isFinished() {
        if (threadPool.getActiveCount() == 0) {
            return true;
        }
        return false;
    }
    
    /**
     * 直到线程池的任务都执行完成。
     */
    public void untilFinished() {
        while (!this.isFinished()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                log.debug(e.getMessage(), e);
            }
        }
    }

    /**
     * 终止线程池
     */
    public void shutdown() {
        threadPool.shutdown();
    }

    /**
     * 
     * @return 默认的线程池
     */
    public static RhThreadPool getDefaultPool() {
        return defaultPool;
    }

}
