package com.rh.core.icbc.basedata.job;

import com.rh.core.icbc.basedata.serv.KSUpdateXmjdJobServ;
import com.rh.core.util.scheduler.RhJob;
import com.rh.core.util.scheduler.RhJobContext;

public class KSUpdateXmjdJob extends RhJob {

	@Override
	protected void executeJob(RhJobContext context) {
		
		KSUpdateXmjdJobServ.startJob();
	}

	@Override
	public void interrupt() {
	}

}
