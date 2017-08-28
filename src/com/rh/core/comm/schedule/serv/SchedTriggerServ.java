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
import com.rh.core.util.DateUtils;
import com.rh.core.util.scheduler.IScheduler;
import com.rh.core.util.scheduler.RhTrigger;
import com.rh.core.util.scheduler.SchedulerMgr;

/**
 * schedule trigger service
 * 
 * @author liwei
 */
public class SchedTriggerServ extends SchedBaseServ {

	/** current servid **/
	private static final String CURRENT_SERVICE = "SY_COMM_SCHED_TRIGGER";

	private static IScheduler scheduler = SchedulerMgr.getGlobalScheduler();

	/**
	 * @see com.rh.core.serv.CommonServ#query(com.rh.core.base.Bean)
	 * @param param
	 *            bean
	 * @return result bean
	 */
	public OutBean query(ParamBean param) {
		// OutBean result = super.query(param);
		OutBean result = new OutBean();

		// 通知scheduler实现类
		final ServDefBean serv = ServUtils
				.getServDef(ServMgr.SY_COMM_SCHED_TRIGGER);
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

		result.setCols(cols);
		scheduler.queryTriggersOfJob(param, result);
		return result;
	}

	/**
	 * 查询指定trigger
	 * 
	 * @param param
	 *            参数Bean
	 * @return 查询结果
	 */
	public OutBean byid(ParamBean param) {
		if (0 == param.getId().length()) {// 如果为新建
			return super.byid(param);
		} else {
			return scheduler.getTrigger(param);
		}
	}

	/**
	 * 添加一个Trigger 添加后trigger将启动 新增的trigger将自动运行
	 * 
	 * @param param
	 *            参数bean，包含job信息
	 * @return 添加结果
	 */
	public OutBean save(ParamBean param) {
		// TODO param validation
		boolean isModify = false;
		if (!param.getAddFlag()) { // edit & save
			isModify = true;
		} else {
			if (!param.containsKey(RhTrigger.TRIG_START_TIME)) {
				param.set(RhTrigger.TRIG_START_TIME, DateUtils
						.getDatetime(System.currentTimeMillis() + 60 * 1000));
			}
		}
		// param.set("serv", CURRENT_SERVICE);
		// param.set("SCHED_NAME", scheduler.getSchedulerName());
		// param.set(RhTrigger.TRIG_STATE, "RUNNING");
		// OutBean outBean = super.save(param);

		RhTrigger rhTrigger = new RhTrigger(param);
		if (isModify) {
			scheduler.updateTrigger(rhTrigger);
		} else {
			scheduler.addTrigger(rhTrigger);
		}
		return scheduler.getTrigger(new ParamBean(rhTrigger));
//		OutBean outBean = new OutBean().setOk(Context.getSyMsg("SY_SAVE_OK"));
//		return outBean;
	}

	/**
	 * 为job删除指定trigger
	 * 
	 * @param param
	 *            参数Bean， 包含JobName
	 * @return 删除结果
	 */
	public OutBean delete(ParamBean param) {

		String[] ids = getIds(param);
		// OutBean outBean = super.delete(param);
		// 通知scheduler实现类
		scheduler.removeTriggers(ids);

		OutBean outBean = new OutBean().setOk(Context.getSyMsg("SY_DELETE_OK",
				"1"));
		return outBean;
	}

	/**
	 * 暂停trigger
	 * 
	 * @param param
	 *            参数Bean，包含Trigger name
	 * @return 处理结果
	 */
	public OutBean pauseTrigger(ParamBean param) {

		String[] ids = getIds(param);
		scheduler.pauseTriggers(ids);

		OutBean outBean = new OutBean(param);
		outBean.setOk(Context.getSyMsg(SY_PAUSE_OPERATION_SUCCESSFUL));
		return outBean;
	}

	/**
	 * 恢复trigger
	 * 
	 * @param param
	 *            参数Bean， 包含trigger name
	 * @return 处理结果
	 */
	public OutBean resumeTrigger(ParamBean param) {

		String[] ids = getIds(param);
		scheduler.resumeTriggers(ids);
		OutBean outBean = new OutBean(param);
		outBean.setOk(Context.getSyMsg(SY_START_OPERATION_SUCCESSFUL));
		return outBean;
	}

}
