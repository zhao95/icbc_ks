package com.rh.core.icbc.basedata.job;

import com.rh.core.icbc.basedata.serv.KSOuttimeApproveServ;
import com.rh.core.util.scheduler.RhJob;
import com.rh.core.util.scheduler.RhJobContext;
/**
 * 系统定时处理超时未审批的请假/借考的人员
 * @author leader
 *
 */
public class KSOuttimeApproveJob extends RhJob {
	
	@Override
	protected void executeJob(RhJobContext context) {
		// 开启任务，后续可加上线程池优化
		new KSOuttimeApproveServ().startJob();
	}

	@Override
	public void interrupt() {
		System.out.println("---KSOuttimeApproveJob interrupt---");
	}

}
