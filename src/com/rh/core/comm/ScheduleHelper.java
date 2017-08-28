/*
 * Copyright (c) 2011 Ruaho All rights reserved.
 */
package com.rh.core.comm;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.DateBuilder.futureDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.quartz.CronTrigger;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.util.JsonUtils;
import com.rh.core.util.scheduler.IScheduler.JobStatus;
import com.rh.core.util.scheduler.RhJobDetail;
import com.rh.core.util.scheduler.RhTrigger;
import com.rh.core.util.scheduler.SchedulerMgr;
import com.rh.core.util.scheduler.imp.QuartzSchedulerHelper;

/**
 * schedule 帮助类
 * @author liwei
 * 
 */
public class ScheduleHelper {

	/**
	 * can not new instanll
	 */
	private ScheduleHelper() {
	}

	/** if durability = false , when job done we remove it */
	private static final boolean DURABLY = true;

	/**
	 * 根据参数构造一个JobDetail
	 * @param classStr 类路径，format:com.rh.core.job.TestJob, 该类必须extends org.quartz.Job
	 * @param jobName job name jobName 在数据库中是唯一标识
	 * @param jobDesc job描述信息
	 * @param data 为job传入的数据参数
	 * @param recovery 当job执行过程中意外中断，服务重启后是否重新执行该任务
	 * @return jobDetail
	 * @throws ClassNotFoundException 如果classStr指定的不正确会抛出该异常
	 * @throws SchedulerException 如果scheduler instacell异常会抛出该异常
	 */
	public static JobDetail buildJob(String classStr, String jobName, String jobDesc, JobDataMap data,
			boolean recovery) throws ClassNotFoundException, SchedulerException {
		@SuppressWarnings("unchecked")
		Class<Job> jobClass = (Class<Job>) Class.forName(classStr);
		JobDetail jobDetail = null;
		JobBuilder builder = newJob(jobClass).storeDurably(DURABLY) // default false
				.withIdentity(jobName) // put triggers in group named after the cluster node instance
										// just to distinguish (in logging) what was scheduled from
										// where
				.requestRecovery(recovery); // ask scheduler to re-execute this job if it was in progress when
											// the scheduler went down...
		if (null != data) {
			builder.usingJobData(data);
		}
		if (null != jobDesc) {
			builder.withDescription(jobDesc);
		}
		jobDetail = builder.build();
		return jobDetail;
	}

	/**
	 * 构造一个trigger
	 * @param trigerName triger唯一标识
	 * @param trigerDesc 说明
	 * @param triggerStartTime 首次启动时间，如果该参数为null表示立即启动
	 * @param repeatCount 重复执行次数
	 * @param interval 执行间隔，单位为秒
	 * @return 生成trigger
	 * @throws SchedulerException 如果scheduler instacell异常会抛出该异常
	 */
	public static Trigger buildTrigger(String trigerName, String trigerDesc, Date triggerStartTime,
			int repeatCount, int interval) throws SchedulerException {
		return buildTrigger(trigerName, trigerDesc, null, null, triggerStartTime, repeatCount, interval);
	}

	/**
	 * 构造一个trigger
	 * @param triggerName triger唯一标识
	 * @param triggerDesc 说明
	 * @param forJob 该trigger所属job
	 * @param triggerStartTime 首次启动时间，如果该参数为null表示立即启动
	 * @param triggerEndTime 截至时间，如果该参数为null该参数不生效
	 * @param repeatCount 重复执行次数 如果该参数为-1 将会一致循环执行至triggerEndTime
	 * @param interval 执行间隔，单位为秒
	 * @return 生成trigger
	 * @throws SchedulerException 如果scheduler instacell异常会抛出该异常
	 */
	public static Trigger buildTrigger(String triggerName, String triggerDesc, JobKey forJob,
			Date triggerStartTime, Date triggerEndTime, int repeatCount, int interval)
			throws SchedulerException {
//		if (0 > repeatCount) {
//			repeatCount = 0;
//		}
		SimpleTrigger trigger = null;
		SimpleScheduleBuilder ssb = simpleSchedule().withIntervalInSeconds(interval);
		if (repeatCount <= -1) {
			ssb.repeatForever();
		} else {
			ssb.withRepeatCount(repeatCount);
		}
		TriggerBuilder<SimpleTrigger> builder = newTrigger().withIdentity(triggerName).withSchedule(ssb);
		// if trggerStartTime not setup, this trigger start at now
		if (null == triggerStartTime) {
			builder.startAt(futureDate(1, IntervalUnit.SECOND));
		} else {
			builder.startAt(triggerStartTime);
		}

		if (null != forJob) {
			builder.forJob(forJob);
		}
		if (null != triggerDesc) {
			builder.withDescription(triggerDesc);
		}
		if (null != triggerEndTime) {
			builder.endAt(triggerEndTime);
		}
		trigger = builder.build();
		return trigger;
	}

	/**
	 * @param triggerName trigger 名称， triger唯一标识
	 * @param forJob 该trigger所属job
	 * @param triggerDesc 说明
	 * @param expression unix crontab 时间表达式 example: 0 0/5 * * * ?
	 * @param triggerStartTime 首次启动时间，如果该参数为null表示立即启动
	 * @param triggerEndTime 截至时间，如果该参数为null该参数不生效
	 * @return crontab Trigger
	 * @throws ParseException 表达式解析错误时抛出该异常
	 * @throws SchedulerException 如果scheduler instacell异常会抛出该异常
	 */
	public static Trigger buildTrigger(String triggerName, JobKey forJob, String triggerDesc,
			String expression, Date triggerStartTime, Date triggerEndTime) throws ParseException,
			SchedulerException {
		// CronExpression cronExpr = new CronExpression(expression);
		// CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule(cronExpr);

		TriggerBuilder<CronTrigger> builder = newTrigger().withIdentity(triggerName).withSchedule(
				cronSchedule(expression));
		if (null == triggerStartTime) {
			builder.startAt(futureDate(1, IntervalUnit.SECOND));
		} else {
			builder.startAt(triggerStartTime);
		}
		if (null != triggerEndTime) {
			builder.endAt(triggerEndTime);
		}
		if (null != forJob) {
			builder.forJob(forJob);
		}
		if (null != triggerDesc) {
			builder.withDescription(triggerDesc);
		}
		return builder.build();
	}

	/**
	 * 获取 <CODE>JobKey</CODE>
	 * 
	 * @param jobNameArray job name array
	 * @return 返回<CODE>JobKey</CODE>Array
	 * @throws SchedulerException 如果scheduler instacell异常会抛出该异常
	 */
	public static JobKey[] getJobKeyArray(String[] jobNameArray) throws SchedulerException {
		JobKey[] result = new JobKey[jobNameArray.length];
		for (int i = 0; i < jobNameArray.length; i++) {
			String jobName = jobNameArray[i];
			result[i] = getJobKey(jobName);
		}
		return result;
	}
	
	/**
	 * 获取 <CODE>TriggerKey</CODE>
	 * 
	 * @param triggerNames job name array
	 * @return 返回<CODE>TriggerKey</CODE> array
	 * @throws SchedulerException 如果scheduler instacell异常会抛出该异常
	 */
	public static TriggerKey[] getTriggerKeyArray(String[] triggerNames) throws SchedulerException {
		TriggerKey[] result = new TriggerKey[triggerNames.length];
		for (int i = 0; i < triggerNames.length; i++) {
			String trigName = triggerNames[i];
			result[i] = getTriggerKey(trigName);
		}
		return result;
	}


	/**
	 * 获取 <CODE>JobKey</CODE>
	 * 
	 * @param jobName job name
	 * @return 返回<CODE>JobKey</CODE>
	 * @throws SchedulerException 如果scheduler instacell异常会抛出该异常
	 */
	public static JobKey getJobKey(String jobName) throws SchedulerException {
		return new JobKey(jobName);
	}

	/**
	 * 获取 <CODE>TriggerKey</CODE>
	 * 
	 * @param triggerName job name
	 * @return 返回<CODE>TriggerKey</CODE>
	 * @throws SchedulerException 如果scheduler instacell异常会抛出该异常
	 */
	public static TriggerKey getTriggerKey(String triggerName) throws SchedulerException {
		return new TriggerKey(triggerName);
	}
	
	
	/**
     * value of bean
     * @param job <CODE>JobDetail</CODE>
     * @param state state string
     * @return bean object
     */
    public static  RhJobDetail valueOfBean(JobDetail job, JobStatus state) {
        RhJobDetail rhjobDetail = new RhJobDetail();
        rhjobDetail.setId(job.getKey().getName());
        rhjobDetail.setJobCode(job.getKey().getName());
        rhjobDetail.setJobGroup(job.getKey().getGroup());
        rhjobDetail.setJobClass(job.getJobClass().toString().replace("class ", ""));
        rhjobDetail.setJobDesc(job.getDescription());
        
        if (job.getJobDataMap().containsKey("CONFIG_DATAS") && job.getJobDataMap().getString("CONFIG_DATAS").length() > 0) {
        	String configJson = job.getJobDataMap().getString("CONFIG_DATAS");
        	Bean configBean = JsonUtils.toBean(configJson);
        	String jobData = configBean.toString();
        	jobData = jobData.substring(1, jobData.length() - 1);
            rhjobDetail.setJobData(jobData);
        } else {
            String jobData = job.getJobDataMap().getWrappedMap().toString();
            jobData = jobData.substring(1, jobData.length() - 1);
            rhjobDetail.setJobData(jobData);
        }
        
        //解析自定义公司&用户
        if (job.getJobDataMap().containsKey("INTERNAL_DATAS") && job.getJobDataMap().getString("INTERNAL_DATAS").length() > 0) {
        	String internalJson = job.getJobDataMap().getString("INTERNAL_DATAS");
        	Bean internalBean = JsonUtils.toBean(internalJson);
        	
        	rhjobDetail.set(SchedulerMgr.JOB_CMPYS_STATUS, internalBean.getStr(SchedulerMgr.JOB_CMPYS_STATUS));
        	rhjobDetail.set(SchedulerMgr.JOB_CMPYS, internalBean.getStr(SchedulerMgr.JOB_CMPYS));
        	rhjobDetail.set(SchedulerMgr.JOB_CONTEXT_USER, internalBean.getStr(SchedulerMgr.JOB_CONTEXT_USER));
        }
        
   
        if (job.requestsRecovery()) {
            rhjobDetail.setRecovery(1);
        } else {
            rhjobDetail.setRecovery(2);
        }
        job.requestsRecovery();
        if (null != state) {
            rhjobDetail.set("JOB_STATE", state.toString());
        }
        return rhjobDetail;

    }
    
    
    public static JobDetail valueOfJobDetail(RhJobDetail rhJobDetail, String quartzJobClass) {
        String jobCode = rhJobDetail.getJobCode();
        // String jobClass = rhJobDetail.getJobClass();
        String jobDesc = rhJobDetail.getJobDesc();
        Bean dataMap = rhJobDetail.getJobData();
        JobDataMap data = new JobDataMap();
//        data.putAll((Map) dataMap);
        
        data.put("CONFIG_DATAS", JsonUtils.toJson(dataMap));
        
        //保存自定义公司&用户
        Bean internalData = new Bean();
        if (rhJobDetail.getStr(SchedulerMgr.JOB_CMPYS_STATUS).length() > 0) {
        	internalData.put(SchedulerMgr.JOB_CMPYS_STATUS, rhJobDetail.getStr(SchedulerMgr.JOB_CMPYS_STATUS));
        }
        if (rhJobDetail.getStr(SchedulerMgr.JOB_CMPYS).length() > 0) {
        	internalData.put(SchedulerMgr.JOB_CMPYS, rhJobDetail.getStr(SchedulerMgr.JOB_CMPYS));
        }
        if (rhJobDetail.getStr(SchedulerMgr.JOB_CONTEXT_USER).length() > 0) {
        	internalData.put(SchedulerMgr.JOB_CONTEXT_USER, rhJobDetail.getStr(SchedulerMgr.JOB_CONTEXT_USER));
        }
        data.put("INTERNAL_DATAS", JsonUtils.toJson(internalData));

        boolean recovery = rhJobDetail.getRecovery();
        JobDetail job = null;
        try {
            job = ScheduleHelper.buildJob(quartzJobClass, jobCode, jobDesc, data, recovery);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return job;
    }
    
    public static Trigger valueOfTrigger(RhTrigger rhTrigger) {
        String jobPk = rhTrigger.getJobCode();
        // if param not has pk, we create and set it
        String trigCode = rhTrigger.getCode();

        String trigDesc = rhTrigger.getDescription();
        int repeatCount = rhTrigger.getRepeatCount();
        int trigInterval = rhTrigger.getInterval();
        Date startTime = rhTrigger.getStartTime();
        Date endTime = rhTrigger.getEndTime();
        String cronExpr = rhTrigger.getCronExpression();
        // 1:simple trigger,2:cronatab trigger
        int type = rhTrigger.getType();

        Trigger trigger = null;
        // build trigger
        try {
            JobKey jobKey = QuartzSchedulerHelper.getJobKey(jobPk);
            if (type == 1) {
                trigger = ScheduleHelper.buildTrigger(trigCode, trigDesc, jobKey, startTime, endTime, repeatCount,
                        trigInterval);
            } else {
                trigger = ScheduleHelper.buildTrigger(trigCode, jobKey, trigDesc, cronExpr, startTime, endTime);
            }
        } catch (SchedulerException se) {
            throw new RuntimeException("add trigger for job failed.", se);
        } catch (ParseException e) {
            throw new TipException(" unix crontab expresstion format error.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("save failed.", e);
        }
        return trigger;
    }

}
