package com.rh.core.serv.gaveauth;

import org.apache.log4j.Logger;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.DateUtils;
import com.rh.core.util.scheduler.RhJob;
import com.rh.core.util.scheduler.RhJobContext;

public class CleanOverTimeAuthJob extends RhJob {

	private Logger log = Logger.getLogger(getClass());
	
	@Override
	protected void executeJob(RhJobContext context) {
		log.info("----start delete over time auth items job----");
		//获取配置的任务参数
        Bean jobData = context.getRhJobDetail().getJobData();
        int days = jobData.getInt("DAYS");
        if (days == 0) {
        	days = 60;
        }
		String deleteDate = DateUtils.getCertainDate(0 - days);
		if (!delAccrItems(deleteDate)) {
			log.info("----删除SY_ORG_ROLE_ACCREDIT表数据失败----");
		} else {
			log.info("----删除SY_ORG_ROLE_ACCREDIT表数据成功----");
		}
		log.info("----end delete over time auth items job----");
	}
	
	/**
	 * 删除SY_ORG_ROLE_ACCREDIT表中过时的记录
	 * @param deleteDate
	 * @return
	 */
	private boolean delAccrItems(String deleteDate) {
		SqlBean sqlBean = new SqlBean();
		sqlBean.andLTE("END_DATE", deleteDate);
		sqlBean.and("STATE", 0);
		return ServDao.delete("SY_ORG_ROLE_ACCREDIT", sqlBean);
	}
	
	@Override
	public void interrupt() {
		// TODO Auto-generated method stub
		
	}
	
}
