package com.rh.core.util.threadpool;

import org.apache.commons.lang.StringUtils;

import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServMgr;

/**
 * 长有效期任务策略。可以重发10次，每次重发的时间间隔：<br>
 * 第一次:5秒<br>
 * 第二次:10秒<br>
 * 第三次：1分钟<br>
 * 第四次：10分钟<br>
 * 第五次：30分钟<br>
 * 第六次：2小时<br>
 * 第七次：6小时<br>
 * 第8次：18小时<br>
 * 
 * @author yangjy
 */
public class LongValidity implements ValidityStrategy {
    /** 长期和短期延迟任务的阈值  **/
    public static final int TS = 90;
    // 时间间隔数
    private static final int[] TIMES = new int[9];
    
    static {
        TIMES[0] = 0;
        TIMES[1] = 6;
        TIMES[2] = 15;
        TIMES[3] = 60;
        TIMES[4] = 600;
        TIMES[5] = 1800;
        TIMES[6] = 2 * 3600;
        TIMES[7] = 6 * 3600;
        TIMES[8] = 18 * 3600;
    }
    
    @Override
    public int getNextTime(RhThreadTask task) {
        if (TIMES.length > task.getExecCount()) {
            return TIMES[task.getExecCount()];
        }
        return -1;
    }

    @Override
    public void enqueue(RhThreadTask task) {
        int nextTime = this.getNextTime(task);
        if (nextTime <= 0) {
            return;
        }
        
        if (nextTime <= TS) { // 小于90秒的任务都放到短期延时执行池中
            // 如果任务没有保存过，则持久化到数据库，但并不设置执行时间。
            if (StringUtils.isEmpty(task.getSerialId())) {
                LongPeriodDeferredQueue queue = new LongPeriodDeferredQueue();
                queue.add(task, -1);
            }
            
            ShortPeriodDeferredQueue.add(task, nextTime);
        } else {
            // 加入到长期延时队列
            LongPeriodDeferredQueue queue = new LongPeriodDeferredQueue();
            queue.add(task, nextTime);
        }
    }

    @Override
    public void dequeue(RhThreadTask task) {
        String serialId = task.getSerialId();
        if (StringUtils.isBlank(serialId)) { // 如果没有持久化，则不处理
            return;
        }
        try {
            ParamBean delBean = new ParamBean();
            delBean.setId(serialId);
            delBean.setServId(LongPeriodDeferredQueue.CC_OPEN_APP_FAILED_TASK);
            delBean.setAct(ServMgr.ACT_DELETE);
            ServMgr.act(delBean);
        } catch(Exception e) {
            
        }
        LongPeriodDeferredTask.removeRunningFlag(serialId);
    }
}
