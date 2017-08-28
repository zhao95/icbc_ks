package com.rh.core.icbc.monitor;

import com.rh.core.base.Context;
import com.rh.core.util.scheduler.RhJobDetail;
import com.rh.core.util.scheduler.RhTrigger;
import com.rh.core.util.scheduler.SchedulerMgr;

public class MonitorScheduleLoader {
	/**
     * 任务计划管理器启动 启动失败抛出异常
     */
    public void start() {
        // 添加本地任务
        //
        try {
            //心跳
            RhJobDetail jobver = new RhJobDetail();
            jobver.setJobCode("outMonitorJob");
            jobver.setJobClass("com.rh.core.icbc.monitor.SystemMonitor");

            RhTrigger trigger = new RhTrigger();
            trigger.setCode("5minMonitorTrigger");
            trigger.setDescription("every 5 min");
            trigger.setJobCode(jobver.getJobCode());
            trigger.setRepeatCount(-1);
            trigger.setInterval(Context.getSyConf("CC_MONITOR_TIMESPACE",300));
            SchedulerMgr.getLocalScheduler().addJob(jobver);
            SchedulerMgr.getLocalScheduler().addTrigger(trigger);
    
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 任务计划管理器停止 停止失败抛出异常
     */
    public void stop() {
     
    }
}
