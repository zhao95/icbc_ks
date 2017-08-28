/*
 * Copyright (c) 2013 Ruaho All rights reserved.
 */
package com.rh.core.util.scheduler.imp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobPersistenceException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.comm.ScheduleHelper;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.bean.PageBean;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.util.Constant;
import com.rh.core.util.scheduler.IScheduler;
import com.rh.core.util.scheduler.RhJobDetail;
import com.rh.core.util.scheduler.RhSchedulerException;
import com.rh.core.util.scheduler.RhTrigger;

/**
 * 基于<code>quartz</code>任务调度实现
 * @author liwei
 * 
 */
public abstract class QuartzScheduler extends IScheduler {

    /** log */
    private static Log log = LogFactory.getLog(QuartzScheduler.class);

    /** milliseconds scends rate */
    private static final int RATE = 1000;

    /** status */
    protected boolean started = false;
    /** global scheduler */
    protected Scheduler quartzScheduler = null;
    
    @Override
    public void start() {
        started = true;
    }


    @Override
    public void shutdown() {
        if (!started) {
            log.warn(" Is ScheduleMgr running? Stop aborted..");
        } else {
            try {
                quartzScheduler.shutdown();
            } catch (SchedulerException e) {
                throw new RhSchedulerException(e);
            }
            started = false;
        }
    }

    @Override
    public  OutBean queryTriggersOfJob(ParamBean param, OutBean result) {
//        int size = 0;
//        Bean pageBean = result.getPage();
//        size = pageBean.getInt("ALLNUM");
        String jobPK = "";
        if (param.contains("JOB_CODE")) {
        	jobPK = param.getStr("JOB_CODE");
        } else {
        	jobPK =param.getId();
        }
        List<RhTrigger> triggerList = new ArrayList<RhTrigger>();
        try {
            JobKey job = QuartzSchedulerHelper.getJobKey(jobPK);
            List<Trigger> triggersOfJob = getTriggersOfJob(job);
            for (Trigger trigger: triggersOfJob) {
            	RhTrigger rhTrigger = valueOfBean(trigger);
                String statusDisplay = DictMgr.getFullName("SY_COMM_SCHED_STATE", rhTrigger.getState());
                rhTrigger.set("TRIGGER_STATE__NAME", statusDisplay);
            	triggerList.add(rhTrigger);
            }
        } catch (SchedulerException e) {
        	log.error("get triggers error.",e);
        }
        result.setPage(new PageBean().setNowPage(1));
        result.setData(triggerList);
        
        return result;
    }

    @Override
    public String getSchedulerName() {
        String name = "";
        try {
            name = quartzScheduler.getSchedulerName();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return name;
    }

    @Override
	public void addJob(RhJobDetail rhJobDetail) {
		checkStatus();
		// add job
		try {
			JobDetail jobDetail = valueOfJobDetail(rhJobDetail,
					rhJobDetail.getJobClass());
			quartzScheduler.addJob(jobDetail, true);
			log.debug(" add job:" + jobDetail.getKey());

		} catch (SchedulerException e) {
			log.error("add job error", e);
			throw new RhSchedulerException("add job failed", e);
		} catch (Exception e) {
			throw new RhSchedulerException("add job failed", e);
		}

	}

    @Override
    public void updateJob(RhJobDetail jobRhJobDetail) {
    	RhJobDetail exists = getJob(jobRhJobDetail.getId());
    	if (null != exists) {
    		exists.putAll(jobRhJobDetail);
    	}
        addJob(exists);
    }

    public RhJobDetail getJob(String jobCode) {
    	RhJobDetail data = null;
    	try {
			JobKey job = ScheduleHelper.getJobKey(jobCode);
			JobDetail exitsJob = quartzScheduler.getJobDetail(job);
			JobStatus status = getState(job);
			data = valueOfBean(exitsJob, status);
		} catch (JobPersistenceException je) {
			log.warn("job env error:" + je.getMessage(), je);
		} catch (SchedulerException e) {
			throw new RuntimeException(Context.getSyMsg("SY_SCHED_ERROR") + ":"
					+ e.getMessage(), e);
		}
    	return data;
    }
    
    public RhTrigger getTrigger(String triggerCode) {
    	RhTrigger data = null;
		try {

			TriggerKey triggerKey = ScheduleHelper.getTriggerKey(triggerCode);
			Trigger exitsTrig = quartzScheduler.getTrigger(triggerKey);
			data = valueOfBean(exitsTrig);
		} catch (SchedulerException e) {
			throw new RuntimeException("get trigger failed.", e);
		}
		return data;
    }
    
    @Override
    public void removeJob(String job) {
        try {
            JobKey jobKey = ScheduleHelper.getJobKey(job);
            // remove job
            checkStatus();
            quartzScheduler.resumeJob(jobKey);
            quartzScheduler.deleteJob(jobKey);

        } catch (SchedulerException e) {
            throw new RuntimeException("remove job failed", e);
        }

    }

    @Override
    public void removeJobs(String[] jobs) {
        for (String job : jobs) {
            removeJob(job);
        }
    }

    @Override
    public void pauseJobs(String[] keys) {
        checkStatus();
        try {
            JobKey[] jobs = ScheduleHelper.getJobKeyArray(keys);
            for (JobKey job : jobs) {
                quartzScheduler.pauseJob(job);
            }
//            updateJobAndTriggersState(jobs);
        } catch (Exception e) {
            throw new RhSchedulerException("pause job failed", e);
        }
    }

    @Override
    public void resumeJobs(String[] keys) {
        checkStatus();
        try {
            JobKey[] jobs = ScheduleHelper.getJobKeyArray(keys);
            for (JobKey job : jobs) {
                quartzScheduler.resumeJob(job);
            }

//            updateJobAndTriggersState(jobs);
        } catch (Exception e) {
            throw new RhSchedulerException("resume job failed", e);
        }

    }

    @Override
    public void interruptJobs(String[] jobKeys) {
        checkStatus();
        try {
            JobKey[] jobs = ScheduleHelper.getJobKeyArray(jobKeys);

            for (JobKey job : jobs) {
                quartzScheduler.resumeJob(job);
            }

//            updateJobAndTriggersState(jobs);
        } catch (Exception e) {
            throw new RhSchedulerException("interrupt job failed", e);
        }
    }

    @Override
    public void addTrigger(RhTrigger triggerBean) {
        checkStatus();

        try {
            // must has startTime
            Trigger trigger = valueOfTrigger(triggerBean);
            quartzScheduler.scheduleJob(trigger);
//            updateJobAndTriggersState(trigger.getJobKey());
        } catch (SchedulerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void removeTrigger(String trigger) {
        checkStatus();

        try {
            TriggerKey trigKey = ScheduleHelper.getTriggerKey(trigger);
            quartzScheduler.unscheduleJob(trigKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeTriggers(String[] triggers) {

        try {
            TriggerKey[] trigKeys = ScheduleHelper.getTriggerKeyArray(triggers);
            // cache the job
            Trigger target = quartzScheduler.getTrigger(trigKeys[0]);
            JobKey job = null;
            if (null != target) {
                job = target.getJobKey();
            }
            // remove all triggers
            checkStatus();
            for (TriggerKey trigger : trigKeys) {
                quartzScheduler.unscheduleJob(trigger);
            }
            // update job state
            if (null != job) {
                updateState(job);
            }
        } catch (Exception e) {
            throw new RuntimeException("remove trigger failed", e);
        }

    }

    @Override
    public void updateTrigger(RhTrigger triggerBean) {
        checkStatus();
        
        RhTrigger exists = getTrigger(triggerBean.getCode());
    	if (null != exists) {
    		exists.putAll(triggerBean);
    	}
    	
        try {
            TriggerKey key = QuartzSchedulerHelper.getTriggerKey(triggerBean.getCode());
            quartzScheduler.unscheduleJob(key);
            addTrigger(exists);
        } catch (SchedulerException e) {
        	log.error("add trigger error", e);
        }
    }

    @Override
    public void pauseTriggers(String[] triggers) {
        checkStatus();

        try {
            TriggerKey[] trigKeys = ScheduleHelper.getTriggerKeyArray(triggers);
            for (TriggerKey trigger : trigKeys) {
                quartzScheduler.pauseTrigger(trigger);
            }

            // cache the job
            JobKey job = null;

            for (TriggerKey triggerKey : trigKeys) {
                Trigger trigger = quartzScheduler.getTrigger(triggerKey);
                if (null == job) {
                    job = trigger.getJobKey();
                }

                // update trigger state
                updateState(trigger);
            }
            // update jobState
            updateState(job);
        } catch (Exception e) {
            throw new RuntimeException("pause trigger failed", e);
        }

    }

    @Override
    public void resumeTriggers(String[] triggers) {
        checkStatus();
        try {
            TriggerKey[] triggerKeys = ScheduleHelper.getTriggerKeyArray(triggers);
            JobKey job = null;

            for (TriggerKey trigger : triggerKeys) {
                quartzScheduler.resumeTrigger(trigger);
            }

            for (TriggerKey triggerKey : triggerKeys) {
                // get trigger
                Trigger trigger = quartzScheduler.getTrigger(triggerKey);
                if (null == job) {
                    job = trigger.getJobKey();
                }
                // update trigger state
                updateState(trigger);
            }
            // update jobState
            updateState(job);
        } catch (Exception e) {
            throw new RuntimeException("resume job failed", e);
        }

    }

    /**
     * 更新状态
     * @param job - 任务
     * @param state - 状态
     */
    public abstract void updateState(JobKey job, JobStatus state);

    /**
     * 更新状态
     * @param trigger - 触发器
     * @param state - 状态
     */
    public abstract void updateState(Trigger trigger, TriggerStatus state);

    // /////////////////////////protected methed//////////////////////////////////////////////////////

    
    /**
     * value of bean
     * @param job <CODE>JobDetail</CODE>
     * @param state state string
     * @return bean object
     */
    protected RhJobDetail valueOfBean(JobDetail job, JobStatus state) {
        RhJobDetail rhjobDetail = ScheduleHelper.valueOfBean(job, state);
        rhjobDetail.set("SCHED_NAME", getSchedulerName());
        return rhjobDetail;
    }

    /**
     * value of bean
     * @param rhJobDetail <CODE>JobDetail</CODE>
     * @param quartzJobClass - job class
     * @return bean object
     */
    protected JobDetail valueOfJobDetail(RhJobDetail rhJobDetail, String quartzJobClass) {
    	return ScheduleHelper.valueOfJobDetail(rhJobDetail, quartzJobClass);
    }

    /**
     * value of Trigger
     * @param rhTrigger - rhtrigger bean
     * @return - Quartz Trigger
     */
    protected Trigger valueOfTrigger(RhTrigger rhTrigger) {
    	return ScheduleHelper.valueOfTrigger(rhTrigger);
    }

    /**
     * valueOf map
     * @param dataStr format key1=value1,key2=value2
     * @return map
     */
    protected JobDataMap valueOfMap(String dataStr) {
        JobDataMap data = new JobDataMap();
        if (null != dataStr && 0 < dataStr.length()) {
            String[] items = dataStr.split(",");
            for (String item : items) {
                String[] tmp = item.split("=");
                if (2 == tmp.length) {
                    data.put(tmp[0].trim(), tmp[1].trim());
                }
            }
        }
        return data;
    }

    /**
     * 根据参数，提取jobname 并返回<CODE>JobKey</CODE> array
     * @param param 参数Bean
     * @return 根据参数提取到的<CODE>JobKey</CODE> array
     */
    protected JobKey[] getJobKeyArray(Bean param) {
        String[] jobNames = getIds(param);
        if (0 >= jobNames.length) {
            throw new TipException("'PK' can not be null");
        }
        JobKey[] result = null;
        try {
            result = ScheduleHelper.getJobKeyArray(jobNames);
        } catch (SchedulerException e) {
            throw new RuntimeException("get sched id error", e);
        }
        return result;
    }

    /**
     * get ids
     * @param paramBean param bean
     * @return id array
     */
    protected String[] getIds(Bean paramBean) {
        String[] ids = paramBean.getId().trim().split(Constant.SEPARATOR);
        return ids;
    }
    

    /**
     * a'value equals b'value with the key
     * @param a bean
     * @param b bean
     * @param key key
     * @return is matched
     */
    protected boolean equals(Bean a, Bean b, String key) {
        return a.getStr(key).equals(b.getStr(key));
    }


    /**
     * update job state base on it's triggers
     * @param job job key
     * @throws SchedulerException throws this exception,if schedule instance error
     */
    public void updateState(JobKey job) throws SchedulerException {
        JobStatus status = getState(job);
        updateState(job, status);
    }

    /**
     * @param trigger triggerKey
     * @return stateStr
     * @throws SchedulerException throw this exception, if schedule instance error
     */
    public TriggerStatus getState(TriggerKey trigger) throws SchedulerException {
        // 已完成trigger会自动移出，triggers状态仅包括活动、暂停状态
        TriggerStatus state = TriggerStatus.UNKNOW;
        TriggerState trigState = quartzScheduler.getTriggerState(trigger);
        if (trigState == TriggerState.PAUSED) {
            state = TriggerStatus.PAUSED;
        } else if (trigState == TriggerState.NORMAL) {
            state = TriggerStatus.ALIVE;
        } else if (trigState == TriggerState.COMPLETE) {
        	state = TriggerStatus.STOP;
        }
        return state;
    }

    /**
     * @param job jobKey
     * @return stateStr
     * @throws SchedulerException throw this exception, if schedule instance error
     */
    public JobStatus getState(JobKey job) throws SchedulerException {
        // 已完成trigger会自动移出，triggers状态仅包括活动、暂停状态
        List<Trigger> triggers = getTriggersOfJob(job);
        int pausedIndex = 0;
        JobStatus jobState = JobStatus.STOP;
        for (Trigger trig : triggers) {
            // TODO if is recovery trigger set state is recoverying...
            // if (isRecoveryTrigger(trig.getKey())) {
            TriggerState trigState = quartzScheduler.getTriggerState(trig.getKey());
            if (trigState == TriggerState.PAUSED) {
                pausedIndex++;
            } else if (trigState == TriggerState.NORMAL) {
                jobState = JobStatus.ALIVE;
            }
        }
        if (pausedIndex == triggers.size() && 0 < triggers.size()) {
            jobState = JobStatus.PAUSED;
        }
        return jobState;
    }

    /**
     * update trigger state
     * @param trigger Trigger
     * @throws SchedulerException throws this exception,if schedule instance error
     */
    public void updateState(Trigger trigger) throws SchedulerException {
        TriggerStatus stateStr = getState(trigger.getKey());
        updateState(trigger, stateStr);
    }

    /**
     * 获取指定job所关联的trigger
     * @param jobKey jobkey
     * @return 该job所关联的trigger list
     * @throws SchedulerException 若当前scheduler服务异常时会抛出该异常
     */
    private List<Trigger> getTriggersOfJob(JobKey jobKey) throws SchedulerException {
        checkStatus();
        @SuppressWarnings("unchecked")
        List<Trigger> triggers = (List<Trigger>) quartzScheduler.getTriggersOfJob(jobKey);
        if (null == triggers) {
            return triggers;
        }

        // ignore recovery trigger
        List<Trigger> recoTrigs = new ArrayList<Trigger>();
        for (Trigger trig : triggers) {
            if (QuartzSchedulerHelper.isRecoveryTrigger(trig.getKey())) {
                recoTrigs.add(trig);
            }
        }
        // delete from triggers
        if (0 < recoTrigs.size()) {
            triggers.removeAll(recoTrigs);
            recoTrigs.clear();
        }
        return triggers;
    }

    /**
     * 根据参数，提取jobname 并返回<CODE>JobKey</CODE>
     * @param param 参数Bean
     * @return 根据参数提取到的<CODE>JobKey</CODE>
     */
    protected JobKey getJobKey(ParamBean param) {
        // TODO jobGroup
        String jobName = null;
        Map<String, String> paramInLinkData = extractParamFromLink(param);
        if (null != paramInLinkData && 0 < paramInLinkData.size()) {
            jobName = paramInLinkData.get(RhJobDetail.JOB_PK);
        }
        if (null != jobName) {
            JobKey jobKey = null;
            try {
                jobKey = ScheduleHelper.getJobKey(jobName);
            } catch (SchedulerException e) {
                throw new RuntimeException("get sched id error", e);
            }
            return jobKey;
        } else {
            return getJobKeyArray(param)[0];
        }
    }

    /**
     * to Bean
     * @param trig <CODE>Trigger</CODE>
     * @return <CODE>Trigger</CODE> CODE>
     */
    protected RhTrigger valueOfBean(Trigger trig) {
    	TriggerStatus status = null;
		try {
			status = getState(trig.getKey());
		} catch (SchedulerException e) {
			log.error("get trigger state error", e);
		}
        return valueOfBean(trig, status);
    }

    /**
     * to Bean
     * @param trig <CODE>Trigger</CODE>
     * @param state <CODE>TriggerState</CODE>
     * @return <CODE>Trigger</CODE> CODE>
     */
    @SuppressWarnings("static-access")
    protected RhTrigger valueOfBean(Trigger trig, TriggerStatus state) {
        RhTrigger trigBean = new RhTrigger();
        trigBean.setCode(trig.getKey().getName());
        if (null == trig.getKey().getGroup() || 0 == trig.getKey().getGroup().length()) {
            trigBean.setGroup("DEFAULT");
        } else {
            trigBean.setGroup(trig.getKey().getGroup());
        }
        trigBean.setDescription(trig.getDescription());
        if (null != trig.getNextFireTime()) {
            trigBean.setNextFireTime(trig.getNextFireTime());
        }
        if (null != trig.getPreviousFireTime()) {
            trigBean.setPrevFireTime(trig.getPreviousFireTime());
        }
        trigBean.setJobCode(trig.getJobKey().getName());
        if (null == trig.getJobKey().getGroup() || 0 == trig.getJobKey().getGroup().length()) {
            trigBean.setGroup("DEFAULT");
        } else {
            trigBean.setGroup(trig.getJobKey().getGroup());
        }
        trigBean.setStartTime(trig.getStartTime());
        if (null != trig.getEndTime()) {
            trigBean.setEndTime(trig.getEndTime());
        }
        if ( null != state) {
        	trigBean.setState(state.toString());
        }
        trigBean.set("SCHED_NAME", getSchedulerName());

        if (trig instanceof SimpleTrigger) {
            SimpleTrigger sTrig = (SimpleTrigger) trig;
            trigBean.setRepeatCount(sTrig.getRepeatCount());
            // store with milliseconds
            trigBean.setInterval((int) (sTrig.getRepeatInterval() / RATE));
            trigBean.setType(1);
        } else if (trig instanceof CronTrigger) {
            CronTrigger cTrig = (CronTrigger) trig;
            trigBean.setCronExpression(cTrig.getCronExpression());
            trigBean.setType(2);
        }
        return trigBean;
    }

    /**
     * extracting data from link param
     * @param param param of link
     * @return param map
     */
    protected Map<String, String> extractParamFromLink(Bean param) {
        String data = param.getStr("_linkWhere");
        Map<String, String> paramInData = null;
        if (0 < data.length()) {
            paramInData = new HashMap<String, String>();
            String[] paramArray = data.split(" and");
            // for each param item
            for (String item : paramArray) {
                String[] tmp = item.replace("'", "").trim().split("=");
                if (2 == tmp.length) {
                    paramInData.put(tmp[0], tmp[1]);
                }
            }
        }
        return paramInData;
    }

    /**
     * check status
     */
    protected void checkStatus() {
        if (!started) {
            throw new RhSchedulerException("Before using this method ,must be started first by calling start");
        }
    }

    /**
     * get all jobs
     * @return return all job
     * @throws SchedulerException throws this exception, if schedule invalid
     */
    protected Set<JobKey> getAllJobs() throws SchedulerException {
        checkStatus();
        Set<JobKey> result = new HashSet<JobKey>();
        List<String> groupList = quartzScheduler.getJobGroupNames();
        for (String group : groupList) {
            Set<JobKey> jobs = quartzScheduler.getJobKeys(GroupMatcher.jobGroupEquals(group));
            result.addAll(jobs);
            jobs.clear();
        }
        return result;
    }

    /**
     * add job Schedule the given <code>{@link org.quartz.Trigger}</code> with the <code>Job</code> identified by the
     * <code>Trigger</code>'s settings.
     * @param trigger trigger
     * @throws SchedulerException if schedule job failed, throws this exception
     */
    protected void add(Trigger trigger) throws SchedulerException {
        checkStatus();
        quartzScheduler.scheduleJob(trigger);
    }

    /**
     * add job
     * @param jobDetail jobdetail
     * @throws SchedulerException if schedule job failed, throws this exception
     */
    protected void add(JobDetail jobDetail) throws SchedulerException {
        checkStatus();
        quartzScheduler.addJob(jobDetail, true);
        log.debug(" add job:" + jobDetail.getKey());
    }

}
