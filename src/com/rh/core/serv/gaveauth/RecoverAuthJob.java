package com.rh.core.serv.gaveauth;

import java.util.List;

import org.apache.log4j.Logger;

import com.rh.core.base.Bean;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;
import com.rh.core.util.scheduler.RhJob;
import com.rh.core.util.scheduler.RhJobContext;

public class RecoverAuthJob extends RhJob {

	private Logger log = Logger.getLogger(getClass());
	
	@Override
	protected void executeJob(RhJobContext context) {
		log.info("----start recover auth job----");
		String curDate = DateUtils.getDateTimeHm();
		List<Bean> accBeans = getOutTimeAccData(curDate);
		if (accBeans == null || accBeans.size() == 0) {
			log.info("未找到需要收回的权限");
			log.info("----end recover auth job----");
			return;
		}
		GaveAuthServ gaveAuthServ = new GaveAuthServ();
		ParamBean paramBean = null;
		int count = 0;
		for (Bean bean : accBeans) {
			paramBean = new ParamBean();
			paramBean.set("SOURCE_USER_ID", bean.getStr("SOURCE_USER_ID"));
			paramBean.set("CURRENT_USER_ID", bean.getStr("CURRENT_USER_ID"));
			paramBean.set("DEPT_CODE", bean.getStr("BNCH_ID"));
			paramBean.set("SYS_CODE", bean.getStr("SYS_CODE"));
			paramBean.set("ROLE_CODE", bean.getStr("ROLE_ID"));
			OutBean outBean = gaveAuthServ.recoverRoleAuth(paramBean);
			if (!outBean.getStr(Constant.RTN_MSG).startsWith(Constant.RTN_MSG_ERROR)) {
				count ++;
			}
		}
		log.info("需要收回的权限有：" + accBeans.size() + "条，收回成功：" + count + "条！");
		log.info("----end recover auth job----");
	}
	
	/**
	 * 获取权限到期的授权记录
	 * @param dateTime
	 * @return
	 */
	private List<Bean> getOutTimeAccData(String dateTime) {
		SqlBean sqlBean = new SqlBean();
		sqlBean.limit(5000);
		sqlBean.selects("SYS_CODE,SOURCE_USER_ID,CURRENT_USER_ID,ROLE_ID,BNCH_ID");
		sqlBean.andLTE("END_DATE", dateTime);
		sqlBean.and("STATE", 1);
		List<Bean> accBeans = ServDao.finds("SY_ORG_ROLE_ACCREDIT", sqlBean);
		return accBeans;
	}

	@Override
	public void interrupt() {
		// TODO Auto-generated method stub
		
	}

}
