package com.rh.core.icbc.basedata.serv;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;

public class KSOuttimeApproveServ extends CommonServ {
	private static Log log = LogFactory.getLog(KSOuttimeApproveServ.class);
	/**
	 * 系统批量处理超时未审批的请假和借考人员，设置为不同意
	 * @return
	 */
	public OutBean startJob() {
		// 检测所有的数据是否到达请假结束日期，批量修改状态，并更改待办的记录
		log.error("---------------系统开始处理请假超时未审批数据！---------------");
		Date currentDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String qjglSql = "select sz.XM_ID,sz.XM_SZ_ID,qj.QJ_ENDDATE from ts_xmgl_sz sz RIGHT JOIN ts_xmgl_qjgl qj on sz.xm_id=qj.XM_ID "
				+ "where 1=1 and sz.XM_NAME_NUM=3; ";
		List<Bean> qjList = Transaction.getExecutor().query(qjglSql);
		List<String> qjOverList = new ArrayList<String>();
		for (int i = 0; i < qjList.size(); i++) {
			Date qjEndDate=null;
			try {
				qjEndDate = sdf.parse(qjList.get(i).getStr("QJ_ENDDATE"));
			} catch (ParseException e) {
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
				String ERRORXM_ID = qjList.get(i).getStr("XM_ID");
				try {
					qjEndDate = sdf1.parse(qjList.get(i).getStr("QJ_ENDDATE"));
				} catch (ParseException e1) {
					log.error("数据错误，请检查是否有异常数据，XM_ID:"+ERRORXM_ID);
				}
				log.error("时间数据转换异常，表中数据无法转换为时间格式！已使用异常方案解决,错误数据 XM_ID 为："+ERRORXM_ID+"。"+e);
//				e.printStackTrace();
			}
			if (qjEndDate.before(currentDate)) {
				qjOverList.add(qjList.get(i).getStr("XM_ID"));
				changeTypeForQJ(qjList.get(i).getStr("XM_SZ_ID"));
				countProcess(qjList.get(i).getStr("XM_ID"),qjList.get(i).getStr("XM_SZ_ID"));
			}
		}
		if (qjOverList.size() > 0) {
			// 将所有超时的项目id全存进集合中，待传递给请假管理处理数据
			for (int j = 0; j < qjOverList.size(); j++) {
				// 调用类方法，传递参数，修改请假状态
				ParamBean SysQJParam = new ParamBean();
				String xm_id = qjOverList.get(j);
				String sqlForTodoId = "select a.TODO_ID,b.USER_CODE from ts_comm_todo a "
						+ "left join ts_qjlb_qj b on a.DATA_ID= b.QJ_ID where b.XM_ID ='"+xm_id+"'";
				List<Bean> todoBeanList = Transaction.getExecutor().query(sqlForTodoId);

				for (int m = 0; m < todoBeanList.size(); m++) {
					String todoid = todoBeanList.get(m).getStr("TODO_ID");
					SysQJParam.set("TODO_ID", todoid);
					SysQJParam.set("isRetreat", "true");
					SysQJParam.set("shstatus", "2");
					SysQJParam.set("shreason", "(超时未审，系统默认不通过)");
					SysQJParam.set("todoId", todoid);
					ServMgr.act("TS_QJLB_QJ", "updateDataBySystem", SysQJParam);
				}
			}
		}
		log.error("---------------系统处理请假超时未审批数据结束！---------------");
		

		// TS_JKLB_JK
		// 检测所有的数据是否到达借考结束日期，批量修改状态，并更改待办的记录
		log.error("---------------系统开始处理借考超时未审批数据！---------------");
		Date currentDate1 = new Date();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String jkglSql = "select sz.XM_ID,sz.XM_SZ_ID,jk.YDJK_ENDDATE from ts_xmgl_sz sz RIGHT JOIN ts_xmgl_ydjk jk on sz.xm_id=jk.XM_ID "
				+ "where 1=1 and sz.XM_NAME_NUM=4; ";
		List<Bean> jkList = Transaction.getExecutor().query(jkglSql);
		List<String> jkOverList = new ArrayList<String>();
		for (int i = 0; i < jkList.size(); i++) {
			Date jkEndDate=null;
			try {
				jkEndDate = sdf.parse(jkList.get(i).getStr("YDJK_ENDDATE"));
			} catch (ParseException e) {
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
				String ERRORXM_ID = jkList.get(i).getStr("XM_ID");
				try {
					jkEndDate = sdf2.parse(jkList.get(i).getStr("YDJK_ENDDATE"));
				} catch (ParseException e1) {
					log.error("数据错误，请检查是否有异常数据，XM_ID:"+ERRORXM_ID);
				}
				log.error("时间数据转换异常，表中数据无法转换为时间格式！已使用异常方案解决,错误数据 XM_ID 为："+ERRORXM_ID+"。"+e);
//				e.printStackTrace();
			} 
			if (jkEndDate.before(currentDate1)) {
				jkOverList.add(jkList.get(i).getStr("XM_ID"));
				changeTypeForQJ(qjList.get(i).getStr("XM_SZ_ID"));
				countProcess(qjList.get(i).getStr("XM_ID"),qjList.get(i).getStr("XM_SZ_ID"));
			}
		}
		if (jkOverList.size() > 0) {
			// 将所有超时的项目id全存进集合中，待传递给借考管理处理数据
			for (int j = 0; j < jkOverList.size(); j++) {
				// 调用类方法，传递参数，修改借考状态
				ParamBean SysJKParam = new ParamBean();
				String xm_id = jkOverList.get(j);
				String sqlForTodoId = "select a.TODO_ID,b.USER_CODE from ts_comm_todo a "
						+ "left join ts_jklb_jk b on a.DATA_ID= b.JK_ID where b.XM_ID ='"+xm_id+"' ";
				List<Bean> todoBeanList = Transaction.getExecutor().query(sqlForTodoId);
				for (int k = 0; k < todoBeanList.size(); k++) {
					String todoid = todoBeanList.get(k).getStr("TODO_ID");
					String USER_CODE = todoBeanList.get(k).getStr("USER_CODE");
					SysJKParam.set("TODO_ID", todoid);
					SysJKParam.set("USER_CODE", USER_CODE);
					SysJKParam.set("isRetreat", "true");
					SysJKParam.set("shstatus", "2");
					SysJKParam.set("shreason", "(超时未审，系统默认不通过)");
					SysJKParam.set("todoId", todoid);
					ServMgr.act("TS_JKLB_JK", "updateDataBySystem", SysJKParam);
				}
			}
		}
		log.error("---------------系统处理借考超时未审批数据结束！---------------");
		changTypeForBM();
		changTypeForSH();
		changTypeForSJ();
		changTypeForCCCS();
		changTypeForKCAP();
		
		return new OutBean().setOk("超时未审批请假/借考处理结束!");
	}	
	private void changTypeForKCAP() {
		// TODO Auto-generated method stub
		
	}
	private void changTypeForCCCS() {
		// TODO Auto-generated method stub
		
	}
	private void changTypeForSJ() {
		// TODO Auto-generated method stub
		
	}
	private void changTypeForSH() {
		// TODO Auto-generated method stub
		
	}
	private void changTypeForBM() {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 计算项目进度,更新项目进度
	 * @param xmId
	 * @param xmSzId
	 */
	public void countProcess(String xmId, String xmSzId) {
		Bean xmIdBean = new Bean();
		xmIdBean.set("_WHERE_", "AND XM_ID='"+xmId+"'");
		int allSZNum = ServDao.count("TS_XM_SZ", xmIdBean);
		Bean xmIdFinishBean = new Bean();
		xmIdFinishBean.set("_WHERE_", "AND XM_SZ_TYPE like '%已结束' and XM_ID='"+xmId+"'");
		int endSZNum = ServDao.count("TS_XMGL_SZ", xmIdFinishBean);
		int processNum = endSZNum%allSZNum;
		Bean newNumBean = new Bean();
		newNumBean.set("XM_JD",processNum);
		ServDao.updates("TS_XMGL_SZ", newNumBean, xmIdBean);
	}
	/**
	 * 更改项目模块状态
	 * @param xmSzId
	 */
	public void changeTypeForQJ(String xmSzId) {
		Bean xmBean = new Bean();
		xmBean.set("XM_SZ_ID", xmSzId);
		Bean updateFields = new Bean();
		updateFields.set("XM_SZ_TYPE", "已结束");
		int i = ServDao.updates("TS_XMGL_SZ", updateFields, xmBean);
//		log.error("-----------成功更新状态记录"+i+"条。-----------");
	}
}
