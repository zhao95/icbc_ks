package com.rh.core.icbc.imp;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Lang;
import com.rh.core.util.scheduler.RhJob;
import com.rh.core.util.scheduler.RhJobContext;

/**
 * 定时备份人力资源每日批量数据
 * 
 * @author if
 *
 */
public class NImpBakJob extends RhJob {
	/** log. */
	private static Log log = LogFactory.getLog(NImpBakJob.class);
	
	// 每次保存条数
	private int SAVE_COUNT = 5000;

	@Override
	protected void executeJob(RhJobContext context) {
		log.debug("-------------NImpBakJob is begin!-------------------");
		
		// 需要备份的表
		String bakServs = Context.getSyConf("CC_IMPDATE_BAK_SERVS", "");
		// 备份数据过期时间，-1不过期，0不备份(保存0天)，1保存一天
		int expiresDays = Context.getSyConf("CC_IMPDATE_BAK_EXPIRES_DAYS", -1);
		
		log.debug("-------------NImpBakJob bakServs is : " + bakServs + "-------------------");
		log.debug("-------------NImpBakJob expiresDays is : " + expiresDays + "-------------------");
		
		String[] servs = bakServs.split(",");
		// 循环每张表
		for (String serv : servs) {
			if (expiresDays < 0) { // 过期时间为负数：不过期
				
				// 只备份
				bakData(serv);
			} else if (expiresDays == 0) { // 过期时间为零：不备份
				
				// 不做操作
			} else { // 过期时间为正数：保存指定天数数据
				
				// 先删除，在备份
				delData(serv, expiresDays);
				bakData(serv);
			}
		}
		log.debug("-------------NImpBakJob is end!-------------------");
	}

	@Override
	public void interrupt() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 删除过期数据
	 * @param table - 服务名
	 * @param expiresDays - 过期时间
	 */
	private void delData(String serv, int expiresDays) {
		// 备份服务的服务名
		String servBak = serv + "_BAK";
		
		// 获取同步数据最后指定天数的日期
		ParamBean delParam = new ParamBean().setSelect("IMP_DATE")
				.setOrder("IMP_DATE DESC").setShowNum(expiresDays).setNowPage(1);
		List<Bean> impStateList = ServDao.finds("CC_IMP_STATE", delParam);
		List<String> impDateArr = new ArrayList<String>();
		for (Bean impStateBean : impStateList) {
			impDateArr.add(impStateBean.getStr("IMP_DATE"));
		}
		
		// 删除指定天数之外的数据
		SqlBean delSql = new SqlBean();
		delSql.andNotIn("S_MTIME", impDateArr.toArray());
		ServDao.destroys(servBak, delSql);
	}
	
	/**
	 * 备份今日增量数据
	 * @param serv
	 */
	private void bakData(String serv) {
		// 备份服务的服务名
		String servBak = serv + "_BAK";
		
		// 获取这批数据的导入时间
		String smtime = "";
		SqlBean sql = new SqlBean();
		sql.desc("IMP_DATE");
		Bean result = ServDao.find("CC_IMP_STATE", sql);
		smtime = result.getStr("IMP_DATE");
		
		// 备份今天的增量数据
		int total = ServDao.count(serv, new ParamBean().setWhere(" AND 1=1"));
		for (int i = 1; i <= total / SAVE_COUNT + 1; i++) {
			ParamBean queryBean = new ParamBean();
			queryBean.setWhere(" AND 1=1");
			queryBean.setShowNum(SAVE_COUNT);
			queryBean.setNowPage(i);
			List<Bean> resultList = ServDao.finds(serv, queryBean);
			for (int j = 0; j < resultList.size(); j++) {
				resultList.get(j).set("BAK_ID", Lang.getUUID());
				resultList.get(j).set("S_MTIME", smtime);
			}
			ServDao.creates(servBak, resultList);
		}
	}
}
