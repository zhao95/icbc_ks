package com.rh.core.icbc.imp.target;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.icbc.imp.log.OrgLogMgr;
import com.rh.core.serv.ServDao;
import com.rh.core.util.DateUtils;
import com.rh.core.util.Lang;

/**
 * 同步组织机构工具类
 * @author zhangjx
 *
 */
public class ImpUtils {
	// log
	private static final Log log = LogFactory.getLog(ImpUtils.class);
	// 机构变更日志
	private static final String SY_ORG_CHANGE_LOG = "SY_ORG_CHANGE_LOG";
	

	/**
	 * 备份当前的SY_BASE_USER_V和SY_ORG_DEPT表数据
	 */
	public static void bakOrg() {
		// 获取昨天是星期几，作为备份表的后缀
		String suffix = getBakTableSuffix();
		
		// 删除旧备份表
		try {
			Transaction.getExecutor().execute("DROP TABLE SY_BASE_USER_V_" + suffix);
		} catch (Exception e) {
			log.error("删除SY_BASE_USER_V的备份表错误！");
		}
		try {
			Transaction.getExecutor().execute("DROP TABLE SY_ORG_DEPT_" + suffix);
		} catch (Exception e) {
			log.error("删除SY_ORG_DEPT的备份表错误！");
		}
		
		// 备份新备份表
		try {
			Transaction.getExecutor().execute("CREATE TABLE SY_BASE_USER_V_" + suffix + " AS SELECT * FROM SY_BASE_USER_V");
		} catch (Exception e) {
			log.error("创建SY_BASE_USER_V的备份表错误！");
		}
		try {
			Transaction.getExecutor().execute("CREATE TABLE SY_ORG_DEPT_" + suffix + " AS SELECT * FROM SY_ORG_DEPT");
		} catch (Exception e) {
			log.error("创建SY_ORG_DEPT的备份表错误！");
		}
	}
	
	/**
	 * 比较当前组织机构表数据和前一天备份的数据，找出所以变更ODEPT_CODE的数据，存入LOG表中
	 */
	public static void compareOrgInsertLog() {
		// 获取昨天是星期几，作为备份表的后缀
		String suffix = getBakTableSuffix();
		
		// 记录部门的机构变更日志
		try {
			String deptQuery = "SELECT O.DEPT_CODE DATA_CODE, 'DEPT' DATA_TYPE, O.ODEPT_CODE OLD_ODEPT, N.ODEPT_CODE NEW_ODEPT FROM SY_ORG_DEPT_" + suffix + " O INNER JOIN SY_ORG_DEPT N ON N.DEPT_CODE = O.DEPT_CODE AND N.ODEPT_CODE != O.ODEPT_CODE";
			List<Bean> deptBeanList = Transaction.getExecutor().query(deptQuery);
			
			for (int i = 0; i < deptBeanList.size(); i++) {
				deptBeanList.get(i).set("LID", Lang.getUUID());
			}
			ServDao.creates(SY_ORG_CHANGE_LOG, deptBeanList);
		} catch (Exception e) {
			log.error("记录部门的机构变更日志错误！");
			OrgLogMgr.orgLogSave("", "CC_DATA_IMPORTER", "记录部门的机构变更日志出错", ExceptionUtils.getFullStackTrace(e));
		}
		
		// 记录用户的机构变更日志错误！
		try {
			// 查询user数据
			String userQuery = "SELECT O.USER_CODE || '_' || O.DEPT_CODE DATA_CODE, 'USER' DATA_TYPE, O.ODEPT_CODE OLD_ODEPT, N.ODEPT_CODE NEW_ODEPT FROM SY_BASE_USER_V_" + suffix + " O INNER JOIN SY_BASE_USER_V N ON N.USER_CODE = O.USER_CODE AND N.ODEPT_CODE != O.ODEPT_CODE";
			List<Bean> userBeanList = Transaction.getExecutor().query(userQuery);
			// 做主键
			for (int i = 0; i < userBeanList.size(); i++) {
				userBeanList.get(i).set("LID", Lang.getUUID());
			}
			// 保存至LOG服务中
			ServDao.creates(SY_ORG_CHANGE_LOG, userBeanList);
		} catch (Exception e) {
			log.error("记录用户的机构变更日志错误！");
			OrgLogMgr.orgLogSave("", "CC_DATA_IMPORTER", "记录部门的机构变更日志出错", ExceptionUtils.getFullStackTrace(e));
		}
	}
	
	/**
	 * 根据当前时间获取昨天是星期几，作为备份表名
	 * @return - 昨天是星期几
	 */
	public static String getBakTableSuffix() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(DateUtils.addDays(new Date(), -1));
	    int week = calendar.get(Calendar.DAY_OF_WEEK);
	    String strWeek = "";

        switch (week) {
        case Calendar.SUNDAY:
            strWeek = "SUNDAY";
            break;
        case Calendar.MONDAY:
            strWeek = "MONDAY";
            break;
        case Calendar.TUESDAY:
            strWeek = "TUESDAY";
            break;
        case Calendar.WEDNESDAY:
            strWeek = "WEDNESDAY";
            break;
        case Calendar.THURSDAY:
            strWeek = "THURSDAY";
            break;
        case Calendar.FRIDAY:
            strWeek = "FRIDAY";
            break;
        case Calendar.SATURDAY:
            strWeek = "SATURDAY";
            break;
        default:
            strWeek = "MONDAY";
            break;
        }
        return strWeek;
	}
	
	/**
	 * 将通讯录人员表生成的视图导入到一个实体表中
	 */
	public static void createTableFromView() {
		int tableCount = Transaction
				.getExecutor()
				.count("SELECT TABLE_NAME FROM USER_TABLES WHERE TABLE_NAME = 'SY_ORG_ADDRESS_V'");
		if (tableCount == 0) { // 表不存在
			Transaction
					.getExecutor()
					.execute(
							"CREATE TABLE SY_ORG_ADDRESS_V AS SELECT T.PERSON_ID || '_' || S.STRU_ID ID,B.NAME NAME,(C.SORT + 10000) SORT,S.STRU_ID PID,3 OTYPE,D.ODEPT_CODE ODEPT,GREATEST (S.S_MTIME, B.S_MTIME) S_MTIME,S.STRU_NAME DEPT_NAME,B.SEX SEX,C.EMAIL EMAIL,C.OFFICE_PHONE1 OPHONE,B.SNAME SNAME,B.S_FLAG S_FLAG,C.HOME_PHONE1 HPHONE,T.DUTY_LV POST,C.MOBILE_PHONE1 MOBILE,B.ENAME ENAME,T.SSIC_ID SSIC_ID,C.FAX FAX,C.OFFICE_ROOMNO OROME,C.OFFICE_ADDRESS OADDR,C.OFFICE_ZIPCODE OPCODE,C.OFFICE_PHONE2 OPHONE2,C.MOBILE_PHONE2 MOBILE2,C.HOME_PHONE2 HPHONE2,C.ADDRESS HADDR,C.ZIPCODE HPCODE FROM SY_HRM_ZDSTAFFSTRU S LEFT JOIN SY_HRM_ZDSTAFFCONTACT C ON S.PERSON_ID = C.PERSON_ID AND S.STRU_ID = C.STRU_ID LEFT JOIN SY_HRM_ZDSTAFFBINFO B ON S.PERSON_ID = B.PERSON_ID LEFT JOIN SY_ORG_DEPT D ON S.STRU_ID = D.DEPT_CODE LEFT JOIN SY_HRM_ZDSTAFFSTATE T ON S.PERSON_ID = T.PERSON_ID WHERE T.SSIC_ID IS NOT NULL AND T.WORK_STATE != '离职' AND T.WORK_STATE != '死亡'");
			Transaction
					.getExecutor()
					.execute(
							"ALTER TABLE SY_ORG_ADDRESS_V ADD( UF_FLAG NUMBER(1), DS VARCHAR2(20) , FMAIL NUMBER(1), SEC_FLAG NUMBER(1))");
			Transaction
					.getExecutor()
					.execute(
							"CREATE UNIQUE INDEX PK_SY_ORG_ADDRESS_V ON SY_ORG_ADDRESS_V(ID)");
			Transaction
					.getExecutor()
					.execute(
							"ALTER TABLE SY_ORG_ADDRESS_V ADD CONSTRAINT PK_SY_ORG_ADDRESS_V PRIMARY KEY (ID)");
		} else { // 表存在
			Transaction.getExecutor()
					.execute("TRUNCATE TABLE SY_ORG_ADDRESS_V");
			Transaction
					.getExecutor()
					.execute(
							"INSERT INTO SY_ORG_ADDRESS_V (ID,NAME,SORT,PID,OTYPE,ODEPT,S_MTIME,DEPT_NAME,SEX,EMAIL,OPHONE,SNAME,S_FLAG,HPHONE,POST,MOBILE,ENAME,SSIC_ID,FAX,OROME,OADDR,OPCODE,OPHONE2,MOBILE2,HPHONE2,HADDR,HPCODE) SELECT T.PERSON_ID || '_' || S.STRU_ID ID,B.NAME NAME,(C.SORT + 10000) SORT,S.STRU_ID PID,3 OTYPE,D.ODEPT_CODE ODEPT,GREATEST (S.S_MTIME, B.S_MTIME) S_MTIME,S.STRU_NAME DEPT_NAME,B.SEX SEX,C.EMAIL EMAIL,C.OFFICE_PHONE1 OPHONE,B.SNAME SNAME,B.S_FLAG S_FLAG,C.HOME_PHONE1 HPHONE,T.DUTY_LV POST,C.MOBILE_PHONE1 MOBILE,B.ENAME ENAME,T.SSIC_ID SSIC_ID,C.FAX FAX,C.OFFICE_ROOMNO OROME,C.OFFICE_ADDRESS OADDR,C.OFFICE_ZIPCODE OPCODE,C.OFFICE_PHONE2 OPHONE2,C.MOBILE_PHONE2 MOBILE2,C.HOME_PHONE2 HPHONE2,C.ADDRESS HADDR,C.ZIPCODE HPCODE FROM SY_HRM_ZDSTAFFSTRU S LEFT JOIN SY_HRM_ZDSTAFFCONTACT C ON S.PERSON_ID = C.PERSON_ID AND S.STRU_ID = C.STRU_ID LEFT JOIN SY_HRM_ZDSTAFFBINFO B ON S.PERSON_ID = B.PERSON_ID LEFT JOIN SY_ORG_DEPT D ON S.STRU_ID = D.DEPT_CODE LEFT JOIN SY_HRM_ZDSTAFFSTATE T ON S.PERSON_ID = T.PERSON_ID WHERE T.SSIC_ID IS NOT NULL AND T.WORK_STATE != '离职' AND T.WORK_STATE != '死亡'");
		}
	}
}
