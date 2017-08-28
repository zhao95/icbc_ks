package com.rh.core.comm.schedule.serv;

import java.sql.CallableStatement;
import java.sql.Connection;

import com.rh.core.base.db.Transaction;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;

/**
 * 任务调度扩展功能
 * @author yjzhou
 * modify time 2017.02.23
 */
public class SchedJobSubServ extends SchedJobServ {
	
	/**
	 * 导出任务调度相关的脚本
	 * @param paramBean
	 * @return
	 */
    public OutBean exportSql (ParamBean paramBean){
    	OutBean outBean = new OutBean();
    	
    	String[] ids = paramBean.getStr("JOB_IDS").split(",");
    	if (null == ids || ids.length == 0) {
    		return outBean.setOk();
		}
    	
    	Transaction.begin();
    	
    	try {
			Connection conn = Transaction.getConn();
			for (String jobCode : ids) {
				callExportProc(jobCode, conn);
			}
			
			Transaction.commit();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Transaction.rollback();
		} finally {
			Transaction.end();
		}
    	
    	return outBean.setOk();
    }
    
    /**
     * 调用存储过程导出脚本
     * @param jobCode
     * @param conn
     */
    private void callExportProc (String jobCode,Connection conn){
    	try {
			CallableStatement stmt = conn.prepareCall("{call RHOIS_JOB.PROC_RHOIS_EXPORT_JOB(?)}");
			stmt.setString(1, jobCode);
			stmt.execute();
			stmt.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
    }

}
