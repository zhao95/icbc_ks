/*
 * Copyright (c) 2012 Ruaho All rights reserved.
 */
package com.rh.core.comm.schedule.serv;

import java.util.LinkedHashMap;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.util.ServConstant;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.scheduler.IScheduler;
import com.rh.core.util.scheduler.RhJobDetail;
import com.rh.core.util.scheduler.SchedulerMgr;

/**
 * 计划调度服务类
 * 
 * @author liwei
 */
public class SchedJobServ extends SchedBaseServ {

	private static IScheduler scheduler = SchedulerMgr.getGlobalScheduler();

	/**
	 * @see com.rh.core.serv.CommonServ#query(com.rh.core.base.Bean)
	 * @param param
	 *            bean
	 * @return result bean
	 */
	public OutBean query(ParamBean param) {
		// 通知scheduler实现类
		final ServDefBean serv = ServUtils.getServDef(ServMgr.SY_COMM_SCHED);
		final LinkedHashMap<String, Bean> cols = new LinkedHashMap<String, Bean>();

		LinkedHashMap<String, Bean> items = serv.getAllItems();
		boolean bKey = true;
		for (String key : items.keySet()) {
			Bean item = items.get(key);
			int listFlag = item.getInt("ITEM_LIST_FLAG");
			if (bKey && item.getStr("ITEM_CODE").equals(serv.getPKey())) { // 主键无论是否列表显示都输出
				if (listFlag == ServConstant.ITEM_LIST_FLAG_HIDDEN) { // 如果定义为隐藏有数据，则提供给前端时设为不显示
					listFlag = ServConstant.ITEM_LIST_FLAG_NO;
				}
				addCols(cols, item, listFlag);
				bKey = false;
			} else if (listFlag != ServConstant.ITEM_LIST_FLAG_NO) {
				if (item.getInt("ITEM_TYPE") == ServConstant.ITEM_TYPE_TABLE
						|| item.getInt("ITEM_TYPE") == ServConstant.ITEM_TYPE_VIEW) {
				}
				if (listFlag == ServConstant.ITEM_LIST_FLAG_HIDDEN) { // 如果定义为隐藏有数据，则提供给前端时设为不显示
					listFlag = ServConstant.ITEM_LIST_FLAG_NO;
				}
				addCols(cols, item, listFlag);
			}

		} // end for

		OutBean outBean = scheduler.queryJob(param);
		outBean.setCols(cols);
		return outBean;
	}

	/**
	 * 添加一个任务
	 * 
	 * @param param
	 *            参数bean，包含job信息
	 * @return 添加结果
	 */
	public OutBean save(ParamBean param) {
		RhJobDetail detail = new RhJobDetail(param);
		if (!param.getAddFlag()) { // edit & save
			// 通知scheduler实现类
			scheduler.updateJob(detail);
		} else { // add
			// 通知scheduler实现类
			scheduler.addJob(detail);
		}

		return scheduler.getJob(new ParamBean(detail));
	}

	/**
	 * 删除指定job
	 * 
	 * @param param
	 *            参数Bean， 包含JobName
	 * @return 删除结果
	 */
	public OutBean delete(ParamBean param) {
		String[] ids = getIds(param);
		// 通知scheduler实现类
		scheduler.removeJobs(ids);
		return new OutBean().setOk();
	}

	/**
	 * 查询指定job
	 * 
	 * @param param
	 *            参数Bean， 包含JobName
	 * @return 查询结果
	 */
	public OutBean byid(ParamBean param) {
		if (0 == param.getId().length()) {// 如果为新建
			return super.byid(param);
		} else {
			OutBean outBean = scheduler.getJob(param);
			return outBean;
		}
	}

	/**
	 * 暂停job
	 * 
	 * @param param
	 *            参数Bean，包含jobName
	 * @return 处理结果
	 */
	public OutBean pauseJob(ParamBean param) {
		String[] ids = getIds(param);
		scheduler.pauseJobs(ids);

		OutBean outBean = new OutBean(param);
		outBean.setOk(Context.getSyMsg(SY_PAUSE_OPERATION_SUCCESSFUL));
		return outBean;
	}

	/**
	 * 恢复job
	 * 
	 * @param param
	 *            参数Bean， 包含job name
	 * @return 处理结果
	 */
	public OutBean resumeJob(ParamBean param) {
		String[] ids = getIds(param);
		// 通知scheduler实现类
		scheduler.resumeJobs(ids);
		OutBean out = new OutBean();
		out.setOk(Context.getSyMsg(SY_START_OPERATION_SUCCESSFUL));
		return out;
	}

	/**
	 * 终止任务
	 * 
	 * @param param
	 *            参数Bean，包含jobName
	 * @return 处理结果
	 */
	public OutBean interruptJobs(ParamBean param) {
		String[] ids = getIds(param);
		// 通知scheduler实现类
		scheduler.interruptJobs(ids);
		OutBean outBean = new OutBean(param);
		outBean.setOk(Context.getSyMsg(SY_PAUSE_OPERATION_SUCCESSFUL));
		return outBean;
	}

	/**
	 * 任务初始化
	 */
	public void init() {
		scheduler.start();
	}

}
