package com.rh.core.icbc.basedata.serv;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.icbc.basedata.impl.BomATMTask;
import com.rh.core.icbc.basedata.impl.BomBankTask;
import com.rh.core.icbc.basedata.impl.BomInfoTableTask;
import com.rh.core.icbc.basedata.impl.BuildingTask;
import com.rh.core.icbc.basedata.impl.CstoreBranchTask;
import com.rh.core.icbc.basedata.impl.HrmADTask;
import com.rh.core.icbc.basedata.impl.HrmBinfoTableTask;
import com.rh.core.icbc.basedata.impl.HrmCertTask;
import com.rh.core.icbc.basedata.impl.HrmContactTask;
import com.rh.core.icbc.basedata.impl.HrmEduTask;
import com.rh.core.icbc.basedata.impl.HrmFamilyTask;
import com.rh.core.icbc.basedata.impl.HrmNotesTask;
import com.rh.core.icbc.basedata.impl.HrmPositionTask;
import com.rh.core.icbc.basedata.impl.HrmStateTask;
import com.rh.core.icbc.basedata.impl.HrmStruTask;
import com.rh.core.icbc.imp.origin.NCommonImporter;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.threadpool.RhThreadPool;

/**
 * 数据导入服务扩展类
 * @author if
 *
 */
public class NImpDataServ extends CommonServ {
	
	//上游数据表的数量
	private static int TABLES_NUMBER = 12;

	/** log. */
	private static Log log = LogFactory.getLog(NImpDataServ.class);
	
	private final String CC_IMP_STATE = "CC_IMP_STATE";
	
    private final String SY_BASEDATA_IMP_STATE = "SY_BASEDATA_IMP_STATE";
	
	/**
	 * 从接口表中同步组织机构数据
	 * @param param
	 * @return
	 */
	public OutBean impDatafromTable(ParamBean param) {
		log.info("-------------- impDatafromTable ---------------");
		//
		TABLES_NUMBER = Context.getSyConf("SY_DATA_TABLE_NUM", TABLES_NUMBER);
		// 获取同步数据的时间,SMTIME
		String smtime = "";
		if (param.isNotEmpty("S_MTIME")) {
			smtime = param.getStr("S_MTIME");
		} else {
			smtime = getImpDate();
			param.set("S_MTIME", smtime);
		}
		// 导入数据时支持断点导入
		String tables = param.getStr("TABLES");
		
		boolean incrementFlag = param.getBoolean("INCREMENT");
		
		// 修改状态表为正在导入...
		Transaction.begin();
		NCommonImporter.setCCStateIng(smtime);
		Transaction.commit();
		Transaction.end();
		
		// 将接口表中数据导入至全量数据表
		log.info("------------------ import interface data ------------------");
		RhThreadPool threadPool = new RhThreadPool(2, 5, 20);
		
		if ("".equals(tables) || tables.indexOf("SY_BOM_ZDPSTRUINFO") > -1) {
			
			BomInfoTableTask bomInfoTask = new BomInfoTableTask(smtime, incrementFlag);
			threadPool.execute(bomInfoTask);
		}
		
		if ("".equals(tables) || tables.indexOf("SY_HRM_ZDSTAFFBINFO") > -1) {
			HrmBinfoTableTask hrmBinfoTask = new HrmBinfoTableTask(smtime, incrementFlag);
			threadPool.execute(hrmBinfoTask);
		}
		
		if ("".equals(tables) || tables.indexOf("SY_BOM_CDPATMBASICINFO") > -1) {
			BomATMTask bomatmTask = new BomATMTask(param);
			threadPool.execute(bomatmTask);
		}
		
		if ("".equals(tables) || tables.indexOf("SY_BUILDINGINFO") > -1) {
			BuildingTask buildingTask = new BuildingTask(smtime, incrementFlag);
			threadPool.execute(buildingTask);
		}
		
		if ("".equals(tables) || tables.indexOf("SY_BOM_ZDPSFBANKLBS") > -1) {
			BomBankTask bombankTask = new BomBankTask(smtime, incrementFlag);
			threadPool.execute(bombankTask);
		}
		
		if ("".equals(tables) || tables.indexOf("SY_HRM_ZDSTAFFAD") > -1) {
			HrmADTask adTask = new HrmADTask(smtime, incrementFlag);
			threadPool.execute(adTask);
		}
		
		if ("".equals(tables) || tables.indexOf("SY_HRM_ZDSTAFFCERT") > -1) {
			HrmCertTask hrmcerttask = new HrmCertTask(smtime, incrementFlag);
			threadPool.execute(hrmcerttask);
		}
		
		if ("".equals(tables) || tables.indexOf("SY_HRM_ZDSTAFFEDU") > -1) {
			HrmEduTask hrmeduTask = new HrmEduTask(smtime, incrementFlag);
			threadPool.execute(hrmeduTask);
		}
		
		if ("".equals(tables) || tables.indexOf("SY_HRM_ZDSTAFFCONTACT") > -1) {
			HrmContactTask hrmcontactTask = new HrmContactTask(smtime, incrementFlag);
			threadPool.execute(hrmcontactTask);
		}
		
		if ("".equals(tables) || tables.indexOf("SY_HRM_ZDSTAFFFAMILY") > -1) {
			HrmFamilyTask hrmfamilyTask = new HrmFamilyTask(smtime, incrementFlag);
			threadPool.execute(hrmfamilyTask);
		}
		
		if ("".equals(tables) || tables.indexOf("SY_HRM_ZDSTAFFNOTES") > -1) {
			HrmNotesTask hrmnotesTask = new HrmNotesTask(smtime, incrementFlag);
			threadPool.execute(hrmnotesTask);
		}
		
		if ("".equals(tables) || tables.indexOf("SY_HRM_ZDSTAFFPOSITION") > -1) {
			HrmPositionTask hrmpositionTask = new HrmPositionTask(smtime, incrementFlag);
			threadPool.execute(hrmpositionTask);
		}
		
		if ("".equals(tables) || tables.indexOf("SY_HRM_ZDSTAFFSTATE") > -1) {
			HrmStateTask hrmstateTask = new HrmStateTask(smtime, incrementFlag);
			threadPool.execute(hrmstateTask);
		}
		
		if ("".equals(tables) || tables.indexOf("SY_HRM_ZDSTAFFSTRU") > -1) {
			HrmStruTask hrmstruTask = new HrmStruTask(smtime, incrementFlag);
			threadPool.execute(hrmstruTask);
		}
		
		if ("".equals(tables) || tables.indexOf("SY_BIO_CSTORE_BRANCH") > -1) {
			CstoreBranchTask cstoreTask = new CstoreBranchTask(smtime, incrementFlag);
			threadPool.execute(cstoreTask);
		}
		
		StopWatch sw = new StopWatch();
		sw.start();
		while(true) {
			// 线程结束 或者 超过2小时
			if (threadPool.isFinished() || sw.getTime() >= 2 * 60 * 60 * 1000) {
				
				// 检测是否成功，设置状态值
				int totalCount = ServDao.count(SY_BASEDATA_IMP_STATE, new SqlBean().and("IMP_DATE", smtime));
				int successCount = ServDao.count(SY_BASEDATA_IMP_STATE, new SqlBean().and("IMP_DATE", smtime).and("IMP_STATE", 10));
				//threadPool.isFinished()判断线程不准确，如果没有线程在运行返回继续
				if(totalCount == 0){
					continue;
				}
				//关闭线程池
				threadPool.shutdown();
				sw.stop();
				
				updateUserPostLevelField();
				
				updateUserIfPresideWork();
				
				updateDutySort();
				
				if (totalCount == successCount && totalCount == TABLES_NUMBER) { // TODO 一共有几张表
					// 修改状态表为导入成功
					NCommonImporter.setCCStateOk(smtime);
				} else {
					// 修改状态表为导入失败
					NCommonImporter.setCCStateError(smtime);
				}
				
				break;
			}
		}
		return new OutBean().setOk("正在进行从增量表导入数据!");
	}
	
	/**
	 * 更新SY_ORG_USER的USER_POST和USER_POST_LEVEL字段值
	 */
	public void updateUserPostLevelField() {
		log.info("####updateUserPostLevelField---> start");
		try{
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE SY_ORG_USER a SET (USER_POST, USER_POST_LEVEL) =");
			sql.append(" (SELECT DUTY_LV, CASE WHEN DUTY_LV_CODE IS NULL THEN 'A999999999999999999'");
			sql.append(" ELSE DUTY_LV_CODE END");
			sql.append(" FROM SY_HRM_ZDSTAFFPOSITION ");
			sql.append(" WHERE PERSON_ID = A.USER_CODE)");
			sql.append(" WHERE CMPY_CODE = 'icbc' AND REGEXP_LIKE (USER_CODE, '^[[:digit:]]+$')");
			
			Context.getExecutor().execute(sql.toString());
		} catch (Exception e) {
			log.error(e.getMessage() ,e);
		}
		log.info("####updateUserPostLevelField---> end");
	}
	
	/**
	 * 更新SY_ORG_USER的USER_EDU_MAJOR字段值。
	 */
	public void updateUserIfPresideWork() {
		log.info("####updateUserIfPresideWork---> start");
		try{
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE SY_ORG_USER a SET USER_EDU_MAJOR = (SELECT CASE");
			sql.append(" WHEN IF_PRESIDE_WORK IS NULL THEN '999999' ELSE IF_PRESIDE_WORK");
			sql.append(" END from SY_HRM_ZDSTAFFPOSITION where PERSON_ID = a.USER_CODE)");
			sql.append(" WHERE CMPY_CODE = 'icbc' AND REGEXP_LIKE (USER_CODE, '^[[:digit:]]+$')");
			Context.getExecutor().execute(sql.toString());
		} catch (Exception e) {
			log.error(e.getMessage() ,e);
		}
		log.info("####updateUserPostLevelField---> end");
	}
	
	public void updateDutySort() {
		log.info("####updateDutySort---> start");
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE SY_ORG_USER a SET USER_HEIGHT = ");
			sql.append("(SELECT NVL(DUTY_SORT, 0) FROM SY_ORG_DUTY_SORT");
			sql.append(" WHERE DUTY_LV_CODE = A.USER_POST_LEVEL) ");
			Context.getExecutor().execute(sql.toString());
			 		             
			StringBuilder sql1 = new StringBuilder();
			sql1.append("update SY_ORG_USER set USER_HEIGHT = 0 where USER_HEIGHT is null");
			Context.getExecutor().execute(sql1.toString());
		} catch (Exception e) {
			log.error(e.getMessage() ,e);
		}
		log.info("####updateDutySort---> end");
	}
	
	/**
	 * 返回最后一条同步数据的时间
	 * 
	 * @return
	 */
	private String getImpDate() {
		log.info("----------------- getImpDate ------------------");
		String smtime = "";
		SqlBean sql = new SqlBean();
		sql.desc("IMP_DATE");
		Bean result = ServDao.find(CC_IMP_STATE, sql);
		smtime = result.getStr("IMP_DATE");
		log.info("-----------------IMP_DATE : " + smtime + "-----------------------");
		return smtime;
	}
}
