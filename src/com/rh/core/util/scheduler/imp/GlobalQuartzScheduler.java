/*
 * Copyright (c) 2013 Ruaho All rights reserved.
 */

package com.rh.core.util.scheduler.imp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import com.rh.core.base.BaseContext.APP;
import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.bean.PageBean;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.util.scheduler.RhJobDetail;
import com.rh.core.util.scheduler.RhSchedulerException;

/**
 * @author liwei
 * 
 */
public class GlobalQuartzScheduler extends QuartzScheduler {

	/** log */
	private static Log log = LogFactory.getLog(GlobalQuartzScheduler.class);

	@Override
	public void start() {
		// start
		if (started) {
			log.warn(" start failed, the scheduleMgr is already running.");
		} else {
			Properties prop = null;
			prop = Context.getProperties(Context.app(APP.WEBINF)
					+ "quartz.properties");
			String scStartFlag = prop.getProperty("com.rh.sc.startflag");
			if (null == scStartFlag || !scStartFlag.equalsIgnoreCase("false")) {
				// 动态指定系统缺省数据源为任务调度使用的数据源
				// prop.setProperty("org.quartz.dataSource.myDS.jndiURL",
				// Context.getDSBean().getStr(DS.FULL_NAME));
				// First we must get a reference to a scheduler
				try {
					SchedulerFactory sf = new StdSchedulerFactory(prop);
					quartzScheduler = sf.getScheduler();
					quartzScheduler.start();
				} catch (SchedulerException e) {
					e.printStackTrace();
					throw new RhSchedulerException(e);
				}
				log.info("the global scheduler started");
				// scheduler listener
				// sched.getListenerManager().addSchedulerListener(schedulerListener)
				super.start();
			}
		}

	}

	

	/**
	 * 获取任务列表
	 * 
	 * @param param
	 *            - 参数bean
	 * @return - out bean
	 */
	@Override
	public OutBean queryJob(ParamBean param) {
		checkStatus();

		List<Bean> datas = new ArrayList<Bean>();
		List<String> groupList;
		try {
			groupList = quartzScheduler.getJobGroupNames();
		} catch (SchedulerException e) {
			log.error("load groupNames error", e);
			return new OutBean().setError("load groupNames failed");
		}
		
		for (String group : groupList) {
			Set<JobKey> jobs;
			try {
				jobs = quartzScheduler.getJobKeys(GroupMatcher
						.jobGroupEquals(group));
			} catch (SchedulerException e) {
				log.error("load JobKeys error", e);
				return new OutBean().setError("load JobKeys failed");
			}
			if (null != jobs) {
				for (JobKey key : jobs) {
					try {
						JobDetail exitsJob = quartzScheduler.getJobDetail(key);
						JobStatus status = getState(key);
						RhJobDetail detail = valueOfBean(exitsJob, status);
						
		                String statusDisplay = DictMgr.getFullName("SY_COMM_SCHED_STATE", status.toString());
		                detail.set("JOB_STATE__NAME", statusDisplay);
		                
						datas.add(detail);
					} catch (SchedulerException e) {
						log.warn("load job error", e);
					}

				}
			}
		}

		OutBean outBean = new OutBean();
	    outBean.setPage(new PageBean().setNowPage(1));
	    //过滤查询
	    datas = filterDataBySearchWhere(param, datas);
	    outBean.setData(datas);
	    return outBean;
	}
	
	@Override
	public OutBean getJob(ParamBean param) {
		checkStatus();
		
		String jobCode = "";
		if (param.containsKey("JOB_CODE")) {
			jobCode = param.getStr("JOB_CODE");
		} else {
			jobCode = param.getId();
		}
		
		RhJobDetail data = getJob(jobCode);
		return new OutBean(data).setOk();
	}
	


	@Override
	public OutBean getTrigger(ParamBean param) {
		String pk = param.getId();
		Bean data = getTrigger(pk);
		OutBean outBean = new OutBean(data).setOk();
		return outBean;
	}

	/**
	 * update all of job state
	 * 
	 * @param job
	 *            <CODE>JobKey</CODE>
	 * @param state
	 *            the state String
	 */
	@Override
	public void updateState(JobKey job, JobStatus state) {
	}

	/**
	 * update trigger state
	 * 
	 * @param trigger
	 *            Trigger
	 * @param state
	 *            state String
	 */
	@Override
	public void updateState(Trigger trigger, TriggerStatus state) {
	}


	/**
	 * 按查询条件过滤job数据
	 * @param paramBean
	 * @param datas
	 * @return
	 */
	private List<Bean> filterDataBySearchWhere (ParamBean paramBean, List<Bean> datas){
		List<Bean> resultList = new ArrayList<Bean>();
		Map<String, String> searchBean = getSearchConditionsFromParam(paramBean);
		if (searchBean.isEmpty()) {
			return datas;
		}
		for (Bean data : datas) {
            if (isMatchedAllSearchConditions(searchBean, data)) {
				resultList.add(data);
			}
		}
		return resultList;
	}
	
	/**
	 * 判断数据是够匹配所有的过滤条件，只要一个条件不满足返回false
	 * @param searchBean
	 * @param data
	 * @return
	 */
	private boolean isMatchedAllSearchConditions (Map<String, String> searchBean, Bean data){
		if (searchBean == null || data == null) {
			return false;
		}
		for (Entry<String, String> entry: searchBean.entrySet()) {
			String key = entry.getKey();
			if (entry.getKey().contains("##LIKE") || entry.getKey().contains("##EQUAL")) {
				if (!(data.getStr(key.split("##")[0]).contains(entry.getValue()))) {
					return false;
				}
			}else if (entry.getKey().contains("##GE")) {
				if ((data.getStr(key.split("##")[0]).compareTo(entry.getValue())) >= 0) {
					return false;
				}
			}else if (entry.getKey().contains("##LE")) {
				if ((data.getStr(key.split("##")[0]).compareTo(entry.getValue())) <= 0) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * 从param中获取查询条件的键值对，模糊搜索的
	 * @param paramBean
	 * @return
	 */
	private Map<String, String> getSearchConditionsFromParam (ParamBean paramBean){
		Map<String, String> searchBean = new HashMap<String, String>();
		String searchWhere = paramBean.getQuerySearchWhere();
		if (StringUtils.isEmpty(searchWhere)) {
			return searchBean;
		}
		searchWhere = searchWhere.trim();
		String[] arr = searchWhere.split("and");
		//key-value的条件分隔符，顺序很重要，严苛的条件靠前
		String[] keyValueSplit = {"like","<=",">=","="};
		Map<String, String> actMap = new HashMap<String, String>();
		actMap.put("like", "LIKE");
		actMap.put("<=", "LE");
		actMap.put(">=", "GE");
		actMap.put("=", "EQUAL");
		for (String strTemp : arr) {
			if (StringUtils.isEmpty(strTemp)) {
				continue;
			}
            
            for (String split : keyValueSplit) {
    			if (strTemp.contains(split)) {
    				String[] KeyValue = strTemp.split(split);
    				if (KeyValue.length !=2 ) {
    					continue;
    				}
    				searchBean.put(KeyValue[0].trim()+"##"+actMap.get(split), KeyValue[1].replaceAll("%", "").replaceAll("'", "").trim());
    				
    				break;
    			}	
			}	
		}
		
		return searchBean;
	}
}
