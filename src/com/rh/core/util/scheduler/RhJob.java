/*
 * Copyright (c) 2013 Ruaho All rights reserved.
 */
package com.rh.core.util.scheduler;

import java.util.List;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.rh.core.base.Bean;
import com.rh.core.comm.ScheduleHelper;
import com.rh.core.org.mgr.OrgMgr;

/**
 * @author liwei 计划任务job
 */
public abstract class RhJob implements org.quartz.InterruptableJob {

    /**
     * 任务执行
     * <p>
     * 由触发器触发执行
     * 
     * @param context - job context
     */
    protected abstract void executeJob(RhJobContext context);

    @Override
    public final void execute(JobExecutionContext context) throws JobExecutionException {
    	JobDetail jobDetail = context.getJobDetail();
        // set up rhcontext
        RhJobDetail jobBean =  ScheduleHelper.valueOfBean(jobDetail, null);
        // String classStr = jobBean.getJobClass();
        // RhJob rhJob = (RhJob) Lang.createObject(RhJob.class, classStr);
        RhJobContext rhContext = new RhJobContext();
        rhContext.setRhJobDetail(jobBean);
        
        // execute job
        execute(rhContext);
    }

    /**
     * @param context - 任务上下文
     */
    public final void execute(RhJobContext context) {
        RhJobDetail jobBean = context.getRhJobDetail();
        Bean data = jobBean.getJobData();

        // job cmpys status schema key in bean
        // 所属公司? 1:不指定,2:全公司,3:自定义 <br>
        // 任务所属公司配置,指定后可从任务上下文环境中读取.<br>
        int cmpyStatus = jobBean.get(SchedulerMgr.JOB_CMPYS_STATUS, 1);
        if (1 == cmpyStatus) {
            data.put(RhJobContext.CONTEXT_CMPYS_KEY, null);
            context.set(RhJobContext.CONTEXT_CMPYS_KEY, null);
        } else if (2 == cmpyStatus) {
            List<Bean> orgList = OrgMgr.getAllCmpys();
            String[] cmpys = new String[orgList.size()];
            for (int i = 0; i < orgList.size(); i++) {
                Bean org = orgList.get(i);
                cmpys[i] = org.getStr("CMPY_CODE");
            }
            data.put(RhJobContext.CONTEXT_CMPYS_KEY, cmpys);
            context.put(RhJobContext.CONTEXT_CMPYS_KEY, cmpys);
        } else if (3 == cmpyStatus) {
            String cmpysStr = jobBean.getStr(SchedulerMgr.JOB_CMPYS);
            if (cmpysStr.lastIndexOf(",") == cmpysStr.length()) {
                cmpysStr = cmpysStr.substring(0, cmpysStr.length() - 1);
            }
            String[] cmpys = cmpysStr.split(",");

            data.put(RhJobContext.CONTEXT_CMPYS_KEY, cmpys);
            context.put(RhJobContext.CONTEXT_CMPYS_KEY, cmpys);
        }

        // 执行用户
        String contextUser = jobBean.getStr(SchedulerMgr.JOB_CONTEXT_USER);
        if (0 < contextUser.length()) {
            data.put(SchedulerMgr.JOB_CONTEXT_USER, contextUser);
            context.put(SchedulerMgr.JOB_CONTEXT_USER, contextUser);
        }

        executeJob(context);
    }

    /**
     * 任务停止
     * <p>
     * 当接收到停止任务指令后，该函数触发执行
     * 
     */
    public abstract void interrupt();
}
