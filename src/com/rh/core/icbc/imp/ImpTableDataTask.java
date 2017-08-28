package com.rh.core.icbc.imp;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.icbc.imp.log.OrgLogMgr;
import com.rh.core.icbc.imp.origin.CommonImporter;
import com.rh.core.icbc.imp.origin.ZDPStruInfoImporter;
import com.rh.core.icbc.imp.origin.ZDStaffBInfoImporter;
import com.rh.core.icbc.imp.origin.ZDStaffContactImporter;
import com.rh.core.icbc.imp.origin.ZDStaffNotesImporter;
import com.rh.core.icbc.imp.origin.ZDStaffStateImporter;
import com.rh.core.icbc.imp.origin.ZDStaffStruImporter;
import com.rh.core.icbc.imp.target.ImpDept;
import com.rh.core.icbc.imp.target.ImpUser;
import com.rh.core.icbc.imp.target.ImpUtils;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.threadpool.RhThreadTask;

public class ImpTableDataTask extends RhThreadTask {

	private static final long serialVersionUID = -5480626945546830257L;
	private ParamBean param = null;

	public ImpTableDataTask(ParamBean param) {
		this.param = param;
	}

	@Override
	public boolean execute() {
		String stepName = param.getStr("step");
		
		String smtime = "";
		if (param.isNotEmpty("S_MTIME")) {
			smtime = param.getStr("S_MTIME");
		} else {
			smtime = getImpDate();
		}
		//状态表置正在导入数据状态
		CommonImporter.updateState(2, smtime);
		
		// 备份SY_BASE_USER_V和SY_ORG_DEPT表
		log.error("备份数据！");
		ImpUtils.bakOrg();
		log.error("导入部门数据！");
		// 导入部门失败后，不继续执行
		if (stepName.indexOf("1") > -1) {
			log.debug("start========step1================");
		boolean deptFlag = impDataToDept(smtime);
		if (!deptFlag) {
			return true;
		}
		log.error("导入用户数据！");
		}
		
		

		if (stepName.indexOf("2") > -1) { 
			log.debug("start========step2============");
		// 导入用户失败后，不继续执行
		boolean userFlag = impDataToUser(smtime);
		if (!userFlag) {
			return true;
		}
		
		log.error("记录数据更新！");
		}
		
	
		if (stepName.indexOf("3") > -1) { 
			log.debug("start========step3============");
		boolean otherFlag = impOtherData(smtime);
		if (!otherFlag) {
			return true;
		}
		}
		
		if (stepName.indexOf("4") > -1) { 
			log.debug("start========step4============");
			// 记录数据更新
			ImpUtils.compareOrgInsertLog();
			// 更新SY_ORG_ADDRESS_V表
			ImpUtils.createTableFromView();
			log.error("更新其他机构用户数据！");
			
		// 置更新成功状态
		CommonImporter.updateState(10, smtime);
		log.error("数据导入完成！");
		}
		
		log.debug("=====done============");
		return true;
	}

	/**
	 * 返回最新的置状态的时间
	 * 
	 * @return
	 */
	private String getImpDate() {
		SqlBean sql = new SqlBean();
		sql.desc("IMP_DATE");
		Bean result = ServDao.find("CC_IMP_STATE", sql);
		return result.getStr("IMP_DATE");
	}

	/**
	 * 导入部门表数据<br/>
	 * 1.将BOM_ZDPSTRUINFO接口表导入到SY_BOM_ZDPSTRUINFO中间表<br/>
	 * 2.将中间表导入数据到部门表 导入失败 返回false
	 * 
	 * @param paramBean
	 */
	private boolean impDataToDept(String smtime) {
		boolean flag = true;
		// 开启事务
		Transaction.begin();
		try {
			if (impDataType()) {
				// 全量导入
				boolean fullDataResult = new ZDPStruInfoImporter().impStruInfoFullData(smtime);
				// 接口表中有数据则更新部门数据
				if (fullDataResult) {
					new ImpDept().recuDept();
				}
			} else {
				// 增量导入
				boolean incDataResut = new ZDPStruInfoImporter().impStruInfoData(smtime);
				if (incDataResut) {
					new ImpDept().addDeptDatas(smtime);
				}
			}
			Transaction.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			Transaction.rollback();
			CommonImporter.updateState(20, smtime);
			OrgLogMgr.orgLogSave("", "CC_DATA_IMPORT", "导入部门数据:"+e.getMessage(), ExceptionUtils.getFullStackTrace(e));
			flag = false;
		} finally {
			Transaction.end();
		}

		return flag;
	}

	/**
	 * 导入数据到部门表<br/>
	 * 1.将接口表HRM_ZDSTAFFCONTACT,HRM_ZDSTAFFCONTACT,HRM_ZDSTAFFSTATE导入中间表 <br/>
	 * 2.将中间表导入到用户表 导入失败 返回false
	 * 
	 * @param paramBean
	 */
	private boolean impDataToUser(String smtime) {
		log.debug("=======impDataToUser start====");
		
		
		boolean flag = true;
		// 开启事务
		Transaction.begin();
		try {
			if (impDataType()) {
				// 全量导入
				boolean StaffContactFull = new ZDStaffContactImporter().impStaffContactFullData(smtime);
				boolean StaffBInfoFull = new ZDStaffBInfoImporter().impStaffBInfoFullData(smtime);
				boolean StaffStateFull = new ZDStaffStateImporter().impStaffStateFullData(smtime);
				boolean StaffNotesFull = new ZDStaffNotesImporter().impStaffNotesFullData(smtime);
				if (StaffContactFull || StaffBInfoFull || StaffStateFull || StaffNotesFull) {
					ImpUser impUser = new ImpUser();
					impUser.recuUser();
					
					// 将指定人员加入管理员组
					impUser.initAdminRole(this.param);
				}
			} else {
				// 增量导入
				boolean StaffContInc = new ZDStaffContactImporter().impStaffContactData(smtime);
				boolean StaffBInfoInc = new ZDStaffBInfoImporter().impStaffBInfoData(smtime);
				boolean StaffStateInc = new ZDStaffStateImporter().impStaffStateData(smtime);
				boolean StaffNotesInc = new ZDStaffNotesImporter().impStaffNotesData(smtime);
				// 三个接口表中只有都没有增量数据才不要导入数据到user表
				if (StaffContInc || StaffBInfoInc || StaffStateInc || StaffNotesInc ) {
					new ImpUser().addUserDatas(smtime);
				}
			}
			Transaction.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			Transaction.rollback();
			CommonImporter.updateState(20, smtime);
			OrgLogMgr.orgLogSave("", "CC_DATA_IMPORT", "导入用户数据:"+e.getMessage(), ExceptionUtils.getFullStackTrace(e));
			flag = false;
		} finally {
			Transaction.end();
		}
		
		log.debug("=======impDataToUser end====");
		return flag;
	}

	/**
	 * 导入其他与机构人员相关的表 导入失败 返回false
	 * 
	 * @param smtime
	 * @return
	 */
	private boolean impOtherData(String smtime) {
		log.debug("=======impOtherData start====");
		boolean flag = true;
		Transaction.begin();
		try {
			if (impDataType()) {
				// 全量导入
				new ZDStaffStruImporter().impStaffStruFullData(smtime);
			} else {
				// 增量导入
				new ZDStaffStruImporter().impStaffStruData(smtime);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			Transaction.rollback();
			CommonImporter.updateState(20, smtime);
			OrgLogMgr.orgLogSave("", "CC_DATA_IMPORT", "导入ZDStaffNotes或ZDStaffStru数据出错",
					ExceptionUtils.getFullStackTrace(e));
			flag = false;
		} finally {
			Transaction.end();
		}
		
		log.debug("=======impOtherData end====");
		return flag;
	}

	/**
	 * 判断接口表的数据类型 -- 全量数据、增量数据
	 * 
	 * @return true：全量数据 <br/>
	 *         false:增量数据
	 */
	private boolean impDataType() {
		// 如果有增量标志则返回false，无则返回true
		if (param.isNotEmpty("INCREMENT")) {
			return false;
		} else {
			return true;
		}
	}
}
