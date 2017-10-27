package com.rh.core.icbc.basedata.job;

import java.io.IOException; 

import com.rh.core.base.Context;
import com.rh.core.util.scheduler.RhJob;
import com.rh.core.util.scheduler.RhJobContext;
import com.rh.ts.inputMySQL.inputMySQL;

/**
 * 轮询数据同步状态表，触发同步数据方法
 * 
 * @author leader
 *
 */
public class KSNImpBOMJob extends RhJob {

	@Override
	protected void executeJob(RhJobContext context) {

		Context.getSyConf("CC_DATA_IMP_AUTO", false);
			try {
				new inputMySQL().insertMySQL();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	@Override
	public void interrupt() {
		// TODO Auto-generated method stub
		System.out.println("---interrupt---");
	}

}
