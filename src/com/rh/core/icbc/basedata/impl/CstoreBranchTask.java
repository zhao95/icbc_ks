package com.rh.core.icbc.basedata.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.icbc.basedata.AbstractTableTask;
import com.rh.core.icbc.imp.log.OrgLogMgr;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;

/**
 * 金库数据导入到全量数据表中
 * 1.每天全量数据
 * @author caoyiqing
 *
 */
public class CstoreBranchTask extends AbstractTableTask {

	private static final long serialVersionUID = -6765216574161301224L;
	
	private static final String BIO_CSTORE_BRANCH = "BIO_CSTORE_BRANCH";
	private static final String SY_BIO_CSTORE_BRANCH = "SY_BIO_CSTORE_BRANCH";
	private static final String BIO_CSTORE_BRANCH_1 = "BIO_CSTORE_BRANCH_1";

	public CstoreBranchTask(String smtime, boolean flag) {
		super(smtime, flag);
	}

	@Override
	protected String getSourceServId() {
		return "SY_BIO_CSTORE_BRANCH";
	}

	@Override
	public String getTargetServId() {
		return "BIO_CSTORE_BRANCH";
	}

	@Override
	protected String[] getPKCodeItems() {
		String[] items = {"BRANCH_ID"};
		return items;
	}

	@Override
	protected void parseOneData(Bean data) {

	}

	@Override
	protected void setQueryWhere(ParamBean param) {

	}
	
	@Override
	public boolean impFullData() {
		if(Context.getSyConf("SY_CSTORE_NO_DATA", false)){
			return true;
		}
		boolean successFlag = true;
		try {
			Transaction.begin();
			Transaction.getExecutor().execute("truncate table BIO_CSTORE_BRANCH_1");
			Transaction.getExecutor().execute("truncate table BIO_CSTORE_BRANCH_2");
			impStoreBranchData(smtime);
			Transaction.commit();
		} catch (Exception e) {
			Transaction.rollback();
			log.error("import SY_BIO_CSTORE_BRANCH error ! " + e.getMessage());
			OrgLogMgr.orgLogSave("", "CC_DATA_IMPORT", "import SY_BIO_CSTORE_BRANCH error ! " + e.getMessage(),
					ExceptionUtils.getFullStackTrace(e));
			successFlag = false;
		}finally {
			Transaction.end();
		}
		return successFlag;
	}
	
	@Override
	public boolean impIncData() {
		if(Context.getSyConf("SY_CSTORE_NO_DATA", false)){
			return true;
		}
		boolean successFlag = true;
		try {
			Transaction.begin();
			impStoreBranchData(smtime);
			Transaction.commit();
		} catch (Exception e) {
			Transaction.rollback();
			log.error("import SY_BIO_CSTORE_BRANCH error ! " + e.getMessage());
			OrgLogMgr.orgLogSave("", "CC_DATA_IMPORT", "import SY_BIO_CSTORE_BRANCH error ! " + e.getMessage(),
					ExceptionUtils.getFullStackTrace(e));
			successFlag = false;
		}finally {
			Transaction.end();
		}
		return successFlag;
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
	private void impStoreBranchData(String smtime) {
		log.info("------------------ import SY_BIO_CSTORE_BRANCH increment data begin ------------------");
		// 清空 BIO_CSTORE_BRANCH_1 表
		String delSql = "delete from BIO_CSTORE_BRANCH_1";
		Transaction.getExecutor().execute(delSql);
		// 导入BIO_CSTORE_BRANCH_2数据到BIO_CSTORE_BRANCH_1
		String impSql = "insert into BIO_CSTORE_BRANCH_1 select * from BIO_CSTORE_BRANCH_2";
		Transaction.getExecutor().execute(impSql);
		// 清空BIO_CSTORE_BRANCH_2
		delSql = "delete from BIO_CSTORE_BRANCH_2";
		Transaction.getExecutor().execute(delSql);
		// 导入BOM_CDPATMBASICINFO数据到BOM_CDPATMBASICINFO_HIS_2
		impSql = "insert into BIO_CSTORE_BRANCH_2 select * from BIO_CSTORE_BRANCH";
		Transaction.getExecutor().execute(impSql);
		// 剩余为增加数据
		String sql = "delete from BIO_CSTORE_BRANCH h1 where exists (select 1 "
				+ "from BIO_CSTORE_BRANCH_1 h2 where h2.BRANCH_ID = h1.BRANCH_ID and h2.PARTITIONS = h1.PARTITIONS "
				+ "and h2.STORE_NAME = h1.STORE_NAME and h2.SET_TYPE = h1.SET_TYPE and h2.STARTUSE_DATE = h1.STARTUSE_DATE "
				+ "and h2.ADDRESS = h1.ADDRESS and h2.LOCATION = h1.LOCATION and h2.SECSOC = h1.SECSOC)";
		Transaction.getExecutor().execute(sql);
		// 剩余为删除数据
		sql = "delete from BIO_CSTORE_BRANCH_1 h1 where exists (select 1 "
				+ "from BIO_CSTORE_BRANCH_2 h2 where h2.BRANCH_ID = h1.BRANCH_ID)";
		Transaction.getExecutor().execute(sql);
		// 源数据总数
		int total = ServDao.count(BIO_CSTORE_BRANCH, new ParamBean().setWhere(" AND 1=1"));
		for (int i = 1; i <= total / SAVE_COUNT + 1; i++) {
			// 更新和增加List
			List<Bean> updateList = new ArrayList<Bean>();
			List<Bean> addList = new ArrayList<Bean>();
			// 批量更新数据
			ParamBean queryBean = new ParamBean();
			queryBean.setWhere("AND 1=1");
			queryBean.setShowNum(SAVE_COUNT);
			queryBean.setNowPage(i);
			List<Bean> resultList = ServDao.finds(BIO_CSTORE_BRANCH, queryBean);
			for (Bean data : resultList) {
				// 设置S_MTIME
				data.set("S_MTIME", smtime);
				data.set("S_FLAG", Constant.YES_INT);
				// 数据如果存在则更新数据,数据不存在则添加数据
				SqlBean countSql = new SqlBean();
				countSql.and("BRANCH_ID", data.getStr("BRANCH_ID"));
				int count = ServDao.count(SY_BIO_CSTORE_BRANCH, countSql);
				if (count == 0) {
					addList.add(data);
				} else {
					updateList.add(data);
				}
			}
			// 添加新数据
			if (addList.size() > 0) {
				ServDao.creates(SY_BIO_CSTORE_BRANCH, addList);
			}
			// 修改老数据
			if (updateList.size() > 0) {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				List<String> updateFieldList = new ArrayList(updateList.get(0).keySet());
				// 可能有多余字段需要remove
				ServDao.updates(SY_BIO_CSTORE_BRANCH, updateFieldList, updateList);
			}
		}
		//删除数据
		int Deltotal = ServDao.count(BIO_CSTORE_BRANCH_1, new ParamBean().setWhere(" AND 1=1"));
		for (int i = 1; i <= Deltotal / SAVE_COUNT + 1; i++) {
			// 更新和增加List
			List<Bean> updateList = new ArrayList<Bean>();
			// 批量更新数据
			ParamBean queryBean = new ParamBean();
			queryBean.setWhere("AND 1=1");
			queryBean.setShowNum(SAVE_COUNT);
			queryBean.setNowPage(i);
			List<Bean> resultList = ServDao.finds(BIO_CSTORE_BRANCH_1, queryBean);
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
			ServDao.updates(SY_BIO_CSTORE_BRANCH, updateFieldList, updateList);
		}
		
	}
	
}
