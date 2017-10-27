package com.rh.core.icbc.basedata.job;

import com.rh.core.base.Bean; 
import com.rh.core.base.Context;
import com.rh.core.icbc.basedata.serv.KSNImpDataServ;
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

	private final String SERV_ID = "CC_IMP_STATE";

	@Override
	protected void executeJob(RhJobContext context) {
		// 是否手动进行导入数据；手动导入，则返回
//		if (!Context.getSyConf("CC_DATA_IMP_AUTO", false)) {
//			return;
//		}
		Context.getSyConf("CC_DATA_IMP_AUTO", false);
		// step1,检测CC_IMP_STATE
		SqlBean sqlBean = new SqlBean();
		sqlBean.desc("IMP_DATE");
		Bean stateBean = ServDao.find(SERV_ID, sqlBean);
		if (stateBean == null) {
			return;
		}
		// step2,找最大的数据，看状态是否为1
		if (stateBean.getInt("IMP_STATE") == 1) {
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
