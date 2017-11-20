package com.rh.core.icbc.basedata.job;

import com.rh.core.icbc.basedata.serv.KSTipMessageServ;
import com.rh.core.util.scheduler.RhJob;
import com.rh.core.util.scheduler.RhJobContext;

public class KSTipMessageJob extends RhJob{
	/**
	 * 考试系统相关提醒的任务类
	 */
	@Override
	protected void executeJob(RhJobContext context) {
		//启动提醒的总服务
		new KSTipMessageServ().startJob();
	}

	@Override
	public void interrupt() {
		System.out.println("---KSTipMessageJob interrupt---");
	}

}
