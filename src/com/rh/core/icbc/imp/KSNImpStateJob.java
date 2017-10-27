package com.rh.core.icbc.imp;

import com.rh.core.base.Bean; 
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.scheduler.RhJob;
import com.rh.core.util.scheduler.RhJobContext;

/**
 * 轮询数据同步状态表，触发同步数据方法
 * 
 * @author leader
 *
 */
public class KSNImpStateJob extends RhJob {
	
	/** 上游数据标的数量 */
	private static final int TABLES_NUM = 12;
	private final String SERV_ID = "SY_BASEDATA_IMP_STATE";

	@Override
	protected void executeJob(RhJobContext context) {
		SqlBean sqlBean = new SqlBean();
		sqlBean.selects("IMP_DATE, count(*) SG_NUM");
		sqlBean.desc("IMP_DATE");
		sqlBean.and("IMP_STATE", 10);
//		sqlBean.groups("IMP_DATE");
		Bean stateBean = ServDao.find(SERV_ID, sqlBean);
		if (stateBean == null) {
			return;
		}
		// step2,找最大的数据，看状态是否为1
		if (stateBean.getInt("SG_NUM") == TABLES_NUM) {
			// 调用同步数据程序
			ParamBean param = new ParamBean();
			param.set("S_MTIME", stateBean.getStr("IMP_DATE"));
			// 置增量标志
			param.set("INCREMENT", true);
			
			new KSNImpDataServ().impDatafromTable(param);
		}
	}

	@Override
	public void interrupt() {
		// TODO Auto-generated method stub
		System.out.println("---interrupt---");
	}

}
