package com.rh.core.icbc.imp.origin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;

/**
 * 
 * @author if
 *
 */
public class NCommonImporter {

	/** log. */
	private static Log log = LogFactory.getLog(NCommonImporter.class);
	
	
	public static void setStateIng(String smtime) {
		log.info("--------------- setStateIng -----------------");
		updateState(2, smtime);
	}
	public static void setStateOk(String smtime) {
		log.info("--------------- setStateOk -----------------");
		updateState(10, smtime);
	}
	public static void setStateError(String smtime) {
		log.info("--------------- setStateError -----------------");
		updateState(20, smtime);
	}
	/**
	 * 更新状态表的状态位
	 * 1，准备导入；10，导入成功；2,导入中；20，导入失败
	 * @param state - 状态
	 * @param smtime - 时间
	 */
	private static void updateState( int state, String smtime) {
		SqlBean sql = new SqlBean();
		sql.set("ROA_STATE", state);
		sql.and("IMP_DATE", smtime);
		ServDao.update("SY_BASEDATA_IMP_STATE", sql);
	}
	
	public static void setCCStateIng(String smtime) {
		log.info("--------------- setStateIng -----------------");
		updateCCState(2, smtime);
	}
	public static void setCCStateOk(String smtime) {
		log.info("--------------- setStateOk -----------------");
		updateCCState(10, smtime);
	}
	public static void setCCStateError(String smtime) {
		log.info("--------------- setStateError -----------------");
		updateCCState(20, smtime);
	}
	/**
	 * 更新状态表的状态位
	 * 1，准备导入；10，导入成功；2,导入中；20，导入失败
	 * @param state - 状态
	 * @param smtime - 时间
	 */
	private static void updateCCState( int state, String smtime) {
		SqlBean sql = new SqlBean();
		sql.set("IMP_STATE", state);
		sql.and("IMP_DATE", smtime);
		ServDao.update("CC_IMP_STATE", sql);
	}
	
}
