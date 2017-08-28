package com.rh.core.icbc.org;

import com.rh.core.util.scheduler.RhJob;
import com.rh.core.util.scheduler.RhJobContext;

/**
 * 任务调度，用于定时执行构建机构隶属关系的表数据
 * @author yjzhou
 * 2016.06.22
 */
public class BuildDeptRelJob extends RhJob {

	@Override
	protected void executeJob(RhJobContext context) {
		/**调用构建的方法**/
        DeptRelMgr deptRelMgr = new DeptRelMgr();
        deptRelMgr.createDeptRel();
	}

	@Override
	public void interrupt() {
		// TODO Auto-generated method stub

	}

}