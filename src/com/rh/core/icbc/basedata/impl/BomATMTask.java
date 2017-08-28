package com.rh.core.icbc.basedata.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.icbc.imp.log.OrgLogMgr;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;
import com.rh.core.util.threadpool.RhThreadTask;

public class BomATMTask extends RhThreadTask {

	private static final long serialVersionUID = -235904202862181408L;
	private static final String BOM_CDPATMBASICINFO = "BOM_CDPATMBASICINFO";
	private static final String SY_BOM_CDPATMBASICINFO = "SY_BOM_CDPATMBASICINFO";
	private static final String BOM_CDPATMBASICINFO_1 = "BOM_CDPATMBASICINFO_1";
	private static final int SAVE_COUNT = 5000;
	private ParamBean param;

	public BomATMTask(ParamBean paramBean) {
		this.param = paramBean;
	}

	@Override
	public boolean execute() {
		String smtime = param.getStr("S_MTIME");
		boolean flag = param.getBoolean("INCREMENT");
		boolean successFlag = true;
		log.info("------------------ import SY_BOM_CDPATMBASICINFO data begin ------------------");
		try {
			Transaction.begin();	
			if (flag) {
				impBOMATMData(smtime);
			} else {
				Transaction.getExecutor().execute("truncate table BOM_CDPATMBASICINFO_1");
				Transaction.getExecutor().execute("truncate table BOM_CDPATMBASICINFO_2");
				impBOMATMData(smtime);
			}
			
			ServDao.deletes("SY_BASEDATA_IMP_STATE", new ParamBean().set("IMP_DATE", smtime).set("SERV_ID", SY_BOM_CDPATMBASICINFO));
			ParamBean dataBean = new ParamBean();
			dataBean.set("SERV_ID", SY_BOM_CDPATMBASICINFO);
			dataBean.set("IMP_DATE", smtime);
			if (successFlag) {
				dataBean.set("IMP_STATE", 10);
				dataBean.set("SG_STATE", 1);
				dataBean.set("ROA_STATE", 1);
				dataBean.set("RDMS_STATE", 1);
			} else {
				dataBean.set("IMP_STATE", 20);
			}
			ServDao.create("SY_BASEDATA_IMP_STATE", dataBean);
			
			Transaction.commit();
		} catch (Exception e) {
			Transaction.rollback();
			log.error("import SY_BOM_CDPATMBASICINFO  increment error ! " + e.getMessage(), e);
			successFlag = false;
			OrgLogMgr.orgLogSave("", "CC_DATA_IMPORT", "import SY_BOM_CDPATMBASICINFO increment error ! " + e.getMessage(),
					ExceptionUtils.getFullStackTrace(e));
		}finally{
			Transaction.end();
		}
		
		log.info("------------------ import SY_BOM_CDPATMBASICINFO data end ------------------");
		return true;
	}
	
	
	/**
	 * 自动柜员机数据从接口表导入到全量表 自动柜员机的接口表每天都是全量数;
	 * 1.将BOM_CDPATMBASICINFO_HIS_2表的数据导入到BOM_CDPATMBASICINFO_HIS_1
	 * 2.将BOM_CDPATMBASICINFO表的数据导入到BOM_CDPATMBASICINFO_HIS_2
	 * 3.删除BOM_CDPATMBASICINFO中与BOM_CDPATMBASICINFO_HIS_1相同的数据，剩余数据为添加数据
	 * 4.删除BOM_CDPATMBASICINFO_HIS_1中与BOM_CDPATMBASICINFO_HIS_2相同的数据，剩下的为删除数据
	 * 
	 * @param smtime
	 */
	private void impBOMATMData(String smtime) {
		log.info("------------------ import SY_BOM_CDPATMBASICINFO increment data begin ------------------");
		// 清空 BOM_CDPATMBASICINFO_HIS_1 表
		String delSql = "delete from BOM_CDPATMBASICINFO_1 ";
		Transaction.getExecutor().execute(delSql);
		// 导入BOM_CDPATMBASICINFO_HIS_2数据到BOM_CDPATMBASICINFO_HIS_1
		String impSql = "insert into BOM_CDPATMBASICINFO_1 select * from BOM_CDPATMBASICINFO_2";
		Transaction.getExecutor().execute(impSql);
		// 清空BOM_CDPATMBASICINFO_HIS_2
		delSql = "delete from BOM_CDPATMBASICINFO_2";
		Transaction.getExecutor().execute(delSql);
		// 导入BOM_CDPATMBASICINFO数据到BOM_CDPATMBASICINFO_HIS_2
		impSql = "insert into BOM_CDPATMBASICINFO_2 select * from BOM_CDPATMBASICINFO";
		Transaction.getExecutor().execute(impSql);
		// 剩余为增加数据
		String sql = "delete from BOM_CDPATMBASICINFO h1 where exists (select 1 "
				+ "from BOM_CDPATMBASICINFO_1 h2 where h2.ATM_NO = h1.Atm_No "
				+ "and h2.equ_type = h1.equ_type and nvl(h2.self_bank_no,0) = nvl(h1.self_bank_no,0) "
				+ "and nvl(h2.self_bank_type,0) = nvl(h1.self_bank_type,0) "
				+ "and nvl(h2.fix_way,0) = nvl(h1.fix_way, 0) and nvl(h2.fix_addr,0) = nvl(h1.fix_addr,0) "
				+ "and nvl(h2.if_24hour,0) = nvl(h1.if_24hour,0))";
		Transaction.getExecutor().execute(sql);
		// 剩余为删除数据
		sql = "delete from BOM_CDPATMBASICINFO_1 h1 where exists (select 1 "
				+ "from BOM_CDPATMBASICINFO_2 h2 where h2.ATM_NO = h1.ATM_NO)";
		Transaction.getExecutor().execute(sql);
		// 源数据总数
		int total = ServDao.count(BOM_CDPATMBASICINFO, new ParamBean().setWhere(" AND 1=1"));
		if(total == 0){
			log.info("----ATM no increment update data ----");			
		} else {
			for (int i = 1; i <= total / SAVE_COUNT + 1; i++) {
				// 更新和增加List
				List<Bean> updateList = new ArrayList<Bean>();
				List<Bean> addList = new ArrayList<Bean>();
				// 批量更新数据
				ParamBean queryBean = new ParamBean();
				queryBean.setWhere("AND 1=1");
				queryBean.setShowNum(SAVE_COUNT);
				queryBean.setNowPage(i);
				List<Bean> resultList = ServDao.finds(BOM_CDPATMBASICINFO, queryBean);
				for (Bean data : resultList) {
					// 设置S_MTIME
					data.set("S_MTIME", smtime);
					data.set("S_FLAG", Constant.YES_INT);
					// 数据如果存在则更新数据,数据不存在则添加数据
					SqlBean countSql = new SqlBean();
					countSql.and("ATM_NO", data.getStr("ATM_NO"));
					int count = ServDao.count(SY_BOM_CDPATMBASICINFO, countSql);
					if (count == 0) {
						addList.add(data);
					} else {
						updateList.add(data);
					}
				}
				// 添加新数据
				if (addList.size() > 0) {
					ServDao.creates(SY_BOM_CDPATMBASICINFO, addList);
				}
				// 修改老数据
				if (updateList.size() > 0) {
					@SuppressWarnings({ "unchecked", "rawtypes" })
					List<String> updateFieldList = new ArrayList(updateList.get(0).keySet());
					// 可能有多余字段需要remove
					ServDao.updates(SY_BOM_CDPATMBASICINFO, updateFieldList, updateList);
				}
			}
		}
		
		//删除数据
		int Deltotal = ServDao.count(BOM_CDPATMBASICINFO_1, new ParamBean().setWhere(" AND 1=1"));
		if(Deltotal == 0){
			log.info("----ATM no increment delete data ---");
		}else{
			for (int i = 1; i <= Deltotal / SAVE_COUNT + 1; i++) {
				// 更新和增加List
				List<Bean> updateList = new ArrayList<Bean>();
				// 批量更新数据
				ParamBean queryBean = new ParamBean();
				queryBean.setWhere("AND 1=1");
				queryBean.setShowNum(SAVE_COUNT);
				queryBean.setNowPage(i);
				List<Bean> resultList = ServDao.finds(BOM_CDPATMBASICINFO_1, queryBean);
				if(resultList.size() == 0){
					continue;
				}
				for (Bean data : resultList) {
					// 设置S_MTIME
					data.set("S_MTIME", smtime);
					data.set("S_FLAG", 2);
					// 数据如果存在则更新数据,数据不存在则添加数据
					updateList.add(data);
				}
				@SuppressWarnings({ "unchecked", "rawtypes" })
				List<String> updateFieldList = new ArrayList(updateList.get(0).keySet());
				// 可能有多余字段需要remove
				ServDao.updates(SY_BOM_CDPATMBASICINFO, updateFieldList, updateList);
			}
		}
	}
}
