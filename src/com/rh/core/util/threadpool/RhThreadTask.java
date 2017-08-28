package com.rh.core.util.threadpool;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 线程任务对象。先做简单封装，避免破坏线程池中的线程，以后可以做更多的扩展，例如执行日志，执行时间。
 * 
 * @author yangjy
 */
public abstract class RhThreadTask implements Runnable, Serializable {
    
    private static final long serialVersionUID = 7747349680562759879L;

    /** 日志处理类 **/
    protected static Log log = LogFactory.getLog(RhThreadTask.class);
    
    /**
     * 重复执行策略：短时间内有效，重复执行多次，直至成功为止。失败后再执行2次，每次间隔时间为5秒。
     */
    public static final String SHORT = "SHORT";
    
    /**
     * 重复执行策略：长时间有效，重复执行多次，直至成功为止。失败后重新执行8次，有效时间为1天。
     */
    public static final String LONG = "LONG";
    
    /**
     * 重复执行策略：失败后不重复执行。
     */
    public static final String NONE = "NONE";
    
    private String strategy = NONE;
    
    private int execCount = 0;
    
    /** 串行化数据ID **/
    private String serialId = null;
    
    @Override
    public final void run() {
        try {
            boolean ok = execute();
            if (ok) { // 任务执行失败，则进入延期处理队列
                removeFromDeferredQueue();
            } else {
                addToDeferredQueue();
            }
        } catch (Throwable e) {
            // 避免异常终止了线程池中的线程，因此catch所有的错误。
            log.error(e.getMessage(), e);
            addToDeferredQueue();
        }
    }
    
    /**
     * 加入到延迟处理队列
     */
    private void addToDeferredQueue() {
        ValidityStrategy validity = getStrategyInst();
        this.execCount++;
        if (validity.getNextTime(this) > 0) {
            log.debug("enqueue -----> " + this.getStrategy() + ", "
                    + this.getSerialId() + ", " + this.getExecCount() + ","
                    + validity.getNextTime(this));
            validity.enqueue(this);
        } else {
            removeFromDeferredQueue();
        }
    }
    
    /**
     * 从延迟处理队列中移除任务
     */
    private void removeFromDeferredQueue() {
        ValidityStrategy validity = getStrategyInst();
        log.debug("dequeue <----- " + this.getStrategy() + ", "
                + this.getSerialId() + "," + this.getExecCount() + ", "
                + validity.getNextTime(this));
        validity.dequeue(this);
    }
    
    /**
     * 
     * @return 获取策略对象
     */
    protected ValidityStrategy getStrategyInst() {
        ValidityStrategy validity = null;
        if (NONE.equals(this.strategy)) {
            validity = new NoneValidity();
        } else if (SHORT.equals(this.strategy)) {
            validity = new ShortValidity();
        } else if (LONG.equals(this.strategy)) {
            validity = new LongValidity();
        }
        return validity;
    }
    
    /**
     * @return 执行是否成功
     */
    public abstract boolean execute();
    
    /**
     * 
     * @return 是否还要放到队列中继续执行。执行失败后，可以调用此方法，判断任务是否还会重新执行。
     */
    public boolean continueExec() {
        if (getStrategyInst().getNextTime(this) > 0) {
            return true;
        }
        return false;
    }
    
    /**
     * @return 取得有效期策略
     */
    public String getStrategy() {
        return strategy;
    }
    
    /**
     * @param strategy 有效期策略
     */
    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }
    
    /**
     * @return 执行次数
     */
    public int getExecCount() {
        return execCount;
    }
    
    /**
     * @param execCount 执行次数
     */
    public void setExecCount(int execCount) {
        this.execCount = execCount;
    }
    
    /**
     * @return 串行化数据ID
     */
    public String getSerialId() {
        return serialId;
    }
    
    /**
     * @param serialId 串行化数据ID
     */
    public void setSerialId(String serialId) {
        this.serialId = serialId;
    }
}
