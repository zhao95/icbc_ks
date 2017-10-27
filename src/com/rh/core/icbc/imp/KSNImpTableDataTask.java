package com.rh.core.icbc.imp;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.comm.CacheMgr;
import com.rh.core.icbc.imp.log.OrgLogMgr;
import com.rh.core.icbc.imp.origin.NCommonImporter;
import com.rh.core.icbc.imp.target.KSNImpDept;
import com.rh.core.icbc.imp.target.KSNImpUser;
import com.rh.core.icbc.imp.target.NImpDept;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.threadpool.RhThreadTask;

public class KSNImpTableDataTask extends RhThreadTask {

	/**
	 * 
	 */

	/** log. */
	private static Log log = LogFactory.getLog(KSNImpTableDataTask.class);

	private static final long serialVersionUID = -9049566367350416434L;

	private ParamBean param = null; // 传入的参数

	public static final String CMPY_CODE = "icbc";
	
	public final String SY_BASESATA_IMP_STATE = "SY_BASESATA_IMP_STATE";

	/**
	 * 构造函数,接收传入的参数
	 * 
	 * @param param
	 */
	public KSNImpTableDataTask(ParamBean param) {
		log.info("--------------- init ImpTableDataTask , param : " + param + "----------------");
		this.param = param;
	}

	@Override
	public boolean execute() {
		// 设置上下文信息
		String onlineUserCode = Context.getSyConf("CC_IMPDATE_ONLINE_USER", "admin");
		try {
			// 设置上下文环境
//			Context.setThreadUser(UserMgr.getUserState(onlineUserCode));
			Context.setThread(Context.THREAD.CMPYCODE, CMPY_CODE);
			// 如果无此人，抛异常
			UserBean userBean = UserMgr.getUser(onlineUserCode);
			Context.setThread(Context.THREAD.USERBEAN, userBean);
		} catch (Exception e) {
			log.error("------------------ import setThreadUser error! " + e.getMessage());
		}

		// 获取同步数据的时间,SMTIME
		String smtime = "";
		if (param.isNotEmpty("S_MTIME")) {
			smtime = param.getStr("S_MTIME");
		} else {
			smtime = getImpDate();
		}
		// 获取是增量还是全量数据
		boolean incrementFlag = param.get("INCREMENT", false);
		// 全量导入数据时支持断点导入
		String tables = param.getStr("TABLES");
		// 导入数据时出错标志
		boolean errorFlag = false;

		// 修改状态表为正在导入...
		Transaction.begin();
		NCommonImporter.setStateIng(smtime);
		Transaction.commit();
		Transaction.end();

		// 将接口表中数据导入至全量数据表
		log.info("------------------ import interface data ------------------");
		if (incrementFlag) { // 增量走这里
			/**
			 * 增量导入是以整体为事务单位的,支持整体事务回滚
			 */
			final String finalSmtime = smtime;
			log.info("------------------ import increment data begin ------------------");
			try {
//				Transaction.begin();

				/*
				 * 增量同步SY_ORG_DEPT数据
				 */
				{
					log.info("------------------ import SY_ORG_DEPT increment data begin ------------------");
					new KSNImpDept().addDeptDatas(finalSmtime);
					log.info("------------------ import SY_ORG_DEPT increment data end ------------------");
				}

				/*
				 * 增量同步SY_ORG_USER数据
				 */
				{
					log.info("------------------ import SY_ORG_USER increment data begin ------------------");
					new KSNImpUser().addUserDatas(finalSmtime);
					log.info("------------------ import SY_ORG_USER increment data end ------------------");
				}

//				Transaction.commit();
			} catch (Exception e) {
				Transaction.rollback();
				log.error("import increment data error ! " + e.getMessage());
				errorFlag = true;
				OrgLogMgr.orgLogSave("", "CC_DATA_IMPORT", "import increment data error ! " + e.getMessage(),
						ExceptionUtils.getFullStackTrace(e));
			} finally {
				log.info("------------------ import increment data end ------------------");
				Transaction.end();
			}

		} else { // 全量走这里
			/**
			 * 全量导入是以表为事务单位的,支持断点重导
			 * 处理SY_ORG_DEPT数据
			 */
			if (!errorFlag && ("".equals(tables) || tables.indexOf("SY_ORG_DEPT") > -1)) {
				log.info("------------------ import SY_ORG_DEPT full data begin ------------------");
				Transaction.begin();
				try {
					new NImpDept().recuDept();

					Transaction.commit();
				} catch (Exception e) {
					Transaction.rollback();
					log.error("import SY_ORG_DEPT error ! " + e.getMessage());
					errorFlag = true;
					OrgLogMgr.orgLogSave("", "CC_DATA_IMPORT", "import SY_ORG_DEPT error ! " + e.getMessage(),
							ExceptionUtils.getFullStackTrace(e));
				} finally {
					log.info("------------------ import SY_ORG_DEPT full data end ------------------");
					Transaction.end();
				}
				
				// 另外开启事务，重建层级树，重置机构编码
				log.info("------------------ import SY_ORG_DEPT rebuild begin ------------------");
				Transaction.begin();
				try {
					NImpDept nImpDept = new NImpDept();
					nImpDept.rebuildTree();
					
					// 强制清除SY_ORG_DEPT_ALL字典的缓存，否则会受脏数据影响
					CacheMgr.getInstance().remove(Context.getCmpy(), "_CACHE_C_" + ServMgr.SY_ORG_DEPT_ALL);
					
					nImpDept.rebuildDept();
					
					Transaction.commit();
				} catch (Exception e) {
					Transaction.rollback();
					log.error("import SY_ORG_DEPT error ! " + e.getMessage());
					errorFlag = true;
					OrgLogMgr.orgLogSave("", "CC_DATA_IMPORT", "import SY_ORG_DEPT error ! " + e.getMessage(),
							ExceptionUtils.getFullStackTrace(e));
				} finally {
					log.info("------------------ import SY_ORG_DEPT rebulid end ------------------");
					Transaction.end();
				}
			}

			/*
			 * 处理SY_ORG_USER数据
			 */
			if (!errorFlag && ("".equals(tables) || tables.indexOf("SY_ORG_USER") > -1)) {
				log.info("------------------ import SY_ORG_USER full data begin ------------------");
				Transaction.begin();
				try {
					KSNImpUser impUser = new KSNImpUser();
					impUser.recuUser();

					// 将指定人员加入管理员组
					impUser.initAdminRole(param);

					Transaction.commit();
				} catch (Exception e) {
					Transaction.rollback();
					log.error("import SY_ORG_USER error ! " + e.getMessage());
					errorFlag = true;
					OrgLogMgr.orgLogSave("", "CC_DATA_IMPORT", "import SY_ORG_USER error ! " + e.getMessage(),
							ExceptionUtils.getFullStackTrace(e));
				} finally {
					log.info("------------------ import SY_ORG_USER full data end ------------------");
					Transaction.end();
				}
			}

		}
		
		// 重新导入SY_ORG_DEPT_USER表中数据
		if (!errorFlag) {
			Transaction.begin();
			try {
				
				Transaction.getExecutor().execute("TRUNCATE TABLE SY_ORG_DEPT_USER");
				Transaction.getExecutor().execute("INSERT INTO SY_ORG_DEPT_USER SELECT PERSON_ID || '^' || STRU_ID, PERSON_ID, STRU_ID, 0, '" + smtime + "', '" + smtime + "' FROM SY_HRM_ZDSTAFFSTRU");
				
				Transaction.commit();
			} catch (Exception e) {
				Transaction.rollback();
			} finally {
				Transaction.end();
			}
		}
		

		// 检测是否出错,修改状态表为导入成功或导入出错
		Transaction.begin();
		if (errorFlag) {
			NCommonImporter.setStateError(smtime);
		} else {
			NCommonImporter.setStateOk(smtime);
		}
		Transaction.commit();
		Transaction.end();

		// 清空上下文信息
		Context.removeThreadUser();

		return false;
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
		Bean result = ServDao.find("SY_BASEDATA_IMP_STATE", sql);
		smtime = result.getStr("IMP_DATE");
		log.info("-----------------IMP_DATE : " + smtime + "-----------------------");
		return smtime;
	}

}
