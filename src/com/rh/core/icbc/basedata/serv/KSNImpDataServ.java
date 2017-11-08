package com.rh.core.icbc.basedata.serv;

import org.apache.commons.lang.time.StopWatch;  
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.icbc.basedata.impl.EtiCertInfo;
import com.rh.core.icbc.basedata.impl.EtiCertQual;
import com.rh.core.icbc.basedata.impl.KSBomInfoTableTask;
import com.rh.core.icbc.basedata.impl.KSHrmAdminTask;
import com.rh.core.icbc.basedata.impl.KSHrmBinfoTableTask;
import com.rh.core.icbc.basedata.impl.KSHrmCertTask;
import com.rh.core.icbc.basedata.impl.KSHrmContactTask;
import com.rh.core.icbc.basedata.impl.KSHrmNotesTask;
import com.rh.core.icbc.basedata.impl.KSHrmPositionTask;
import com.rh.core.icbc.basedata.impl.KSHrmStaffPhoto;
import com.rh.core.icbc.basedata.impl.KSHrmStateTask;
import com.rh.core.icbc.basedata.impl.KSHrmStruTask;
import com.rh.core.icbc.imp.origin.NCommonImporter;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.threadpool.RhThreadPool;

/**
 * 数据导入服务扩展类
 * @author leader
 *
 */
public class KSNImpDataServ extends CommonServ {
	
	//上游数据表的数量
	private static int TABLES_NUMBER = 12;

	/** log. */
	private static Log log = LogFactory.getLog(KSNImpDataServ.class);
	
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
			//返回最后一条数据的同步时间
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
		//ok
		if ("".equals(tables) || tables.indexOf("SY_BOM_ZDPSTRUINFO") > -1) {
			KSBomInfoTableTask bomInfoTask = new KSBomInfoTableTask(smtime, incrementFlag);
			threadPool.execute(bomInfoTask);
		}
		
		//OK
		if ("".equals(tables) || tables.indexOf("SY_HRM_ZDSTAFFBINFO") > -1) {
			KSHrmBinfoTableTask hrmBinfoTask = new KSHrmBinfoTableTask(smtime, incrementFlag);
			threadPool.execute(hrmBinfoTask);
		}

		//OK
		if ("".equals(tables) || tables.indexOf("SY_HRM_ZDSTAFFADMIN") > -1) {
			KSHrmAdminTask adTask = new KSHrmAdminTask(smtime, incrementFlag);
			threadPool.execute(adTask);
		}
		//OK
		if ("".equals(tables) || tables.indexOf("SY_HRM_ZDSTAFFCERT") > -1) {
			KSHrmCertTask hrmcerttask = new KSHrmCertTask(smtime, incrementFlag);
			threadPool.execute(hrmcerttask);
		}
		//OK
		if ("".equals(tables) || tables.indexOf("SY_HRM_ZDSTAFFCONTACT") > -1) {
			KSHrmContactTask hrmcontactTask = new KSHrmContactTask(smtime, incrementFlag);
			threadPool.execute(hrmcontactTask);
		}
		//OK
		if ("".equals(tables) || tables.indexOf("SY_HRM_ZDSTAFFNOTES") > -1) {
			KSHrmNotesTask hrmnotesTask = new KSHrmNotesTask(smtime, incrementFlag);
			threadPool.execute(hrmnotesTask);
		}
		
		//OK
		if ("".equals(tables) || tables.indexOf("SY_HRM_ZDSTAFFPOSITION") > -1) {
			KSHrmPositionTask hrmpositionTask = new KSHrmPositionTask(smtime, incrementFlag);
			threadPool.execute(hrmpositionTask);
		}
		//OK
		if ("".equals(tables) || tables.indexOf("SY_HRM_ZDSTAFFSTATE") > -1) {
			KSHrmStateTask hrmstateTask = new KSHrmStateTask(smtime, incrementFlag);
			threadPool.execute(hrmstateTask);
		}
		//OK
		if ("".equals(tables) || tables.indexOf("SY_HRM_ZDSTAFFSTRU") > -1) {
			KSHrmStruTask hrmstruTask = new KSHrmStruTask(smtime, incrementFlag);
			threadPool.execute(hrmstruTask);
		}
//		
		//OK
		if("".equals(tables) || tables.indexOf("SY_HRM_ZDSTAFFPHOTO") > -1){
			KSHrmStaffPhoto hrmStaffPhoto = new KSHrmStaffPhoto(smtime, incrementFlag);
			threadPool.execute(hrmStaffPhoto);
		}
		//OK
		if("".equals(tables) || tables.indexOf("TS_ETI_CERT_INFO") > -1){
			EtiCertInfo etiCertInfo = new EtiCertInfo(smtime, incrementFlag);
			threadPool.execute(etiCertInfo);
		}
		//OK
		if("".equals(tables) || tables.indexOf("TS_ETI_CERT_QUAL") > -1){
			EtiCertQual etiCertQual = new EtiCertQual(smtime, incrementFlag);
			threadPool.execute(etiCertQual);
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
				//更新SY_ORG_USER中的 user_post 和  user_post_level 用户职位和职位级别
				updateUserPostLevelField();
				//更新SY_ORG_USER的USER_EDU_MAJOR 专业
				updateUserIfPresideWork();
				//更新用户的身高
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
			sql.append("UPDATE SY_ORG_USER a LEFT JOIN (SELECT DUTY_LV,DUTY_LV_CODE,PERSON_ID");
			sql.append(" FROM SY_HRM_ZDSTAFFPOSITION) b ON b.PERSON_ID = a.USER_CODE ");
			sql.append(" SET USER_POST =b.DUTY_LV , USER_POST_LEVEL = IFNULL(b.DUTY_LV_CODE,'A999999999999999999') ");
			sql.append(" WHERE CMPY_CODE = 'icbc' AND  USER_CODE REGEXP ( '^[[:digit:]]+$')");
			
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
			sql.append(" UPDATE SY_ORG_USER a SET USER_EDU_MAJOR = (SELECT CASE");
			sql.append(" WHEN IF_PRESIDE_WORK IS NULL THEN '999999' ELSE IF_PRESIDE_WORK");
			sql.append(" END from SY_HRM_ZDSTAFFPOSITION where PERSON_ID = a.USER_CODE)");
			sql.append(" WHERE CMPY_CODE = 'icbc' AND USER_CODE REGEXP('^[[:digit:]]+$')");
			Context.getExecutor().execute(sql.toString());
		} catch (Exception e) {
			log.error(e.getMessage() ,e);
		}
		log.info("####updateUserPostLevelField---> end");
	}
	
	/**
	 * 更新SY_ORG_USER的USER_HEIGHT字段值
	 */
	public void updateDutySort() {
		log.info("####updateDutySort---> start");
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE SY_ORG_USER a SET USER_HEIGHT = ");
//			sql.append("(SELECT NVL(DUTY_SORT, 0) FROM SY_ORG_DUTY_SORT");
			sql.append("(SELECT IFNULL(DUTY_SORT, 0) FROM SY_ORG_DUTY_SORT");
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
