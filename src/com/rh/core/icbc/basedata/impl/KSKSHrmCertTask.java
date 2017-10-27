package com.rh.core.icbc.basedata.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.base.db.Transaction;
import com.rh.core.icbc.basedata.AbstractTableTask;
import com.rh.core.icbc.imp.log.OrgLogMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.util.Constant;

public class KSKSHrmCertTask extends AbstractTableTask{

	public KSKSHrmCertTask(String smtime, boolean flag) {
		super(smtime, flag);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7349931667093448949L;
	private String HRM_ZDSTAFFCERT = "HRM_ZDSTAFFCERT";
	private String SY_HRM_ZDSTAFFCERT = "SY_HRM_ZDSTAFFCERT";
	
	@Override
	protected String getSourceServId() {
		return HRM_ZDSTAFFCERT;
	}

	@Override
	public String getTargetServId() {
		return SY_HRM_ZDSTAFFCERT;
	}

	@Override
	protected String[] getPKCodeItems() {
		String[] pkCodeItems = { "PERSON_ID"};
		return pkCodeItems;
	}

	@Override
	protected void parseOneData(Bean data) {
		// 获取更新标志
		if (data.contains("UPDATE_FLAG")) {
			String updateFlag = data.getStr("UPDATE_FLAG");
			// 根据updateFlag将数据整理为删除和非删除
			if (updateFlag.equals("0") || updateFlag.equals("1")) {
				// 暂时不做处理
				data.set("S_FLAG", Constant.YES_INT);
			} else if (updateFlag.equals("2")) {
				data.set("S_FLAG", Constant.NO_INT);
			} else {
				throw new TipException("【数据更新标志未知】！data : " + data + ", updateFlag : " + updateFlag);
			}
		}
	}

	@Override
	protected void setQueryWhere(ParamBean param) {
		param.setOrder("UPDATE_FLAG");
	}
	
	/**
	 * 重写父类的增量更新方法
	 */
	@Override
	public boolean impIncData() {
		boolean successFlag = true;
		// 目标服务ID
		String targetServId = getTargetServId();
		// 源服务ID
		String sourceServId = getSourceServId();
//		ServDefBean sourceServ = ServUtils.getServDef(sourceServId);
		
		log.error("------------------ import " + targetServId + " increment data begin! ------------------");
		
		try {
			/*
			//Transaction.begin();
			//内存限制接口表大量数据增量更新导致内存溢出。
			//HashSet<String> addCodes = new HashSet<String>();
			//返回接口表中的数据总量
			List<Bean> createList = new ArrayList<Bean>();
			List<Bean> updateList = new ArrayList<Bean>();
			List<Bean> sameResultList = Transaction.getExecutor().query("SELECT a.PERSON_ID,a.SSIC_ID,a.NAME,a.CERT_TYPE_CODE,a.CERT_TYPE,a.CERT,a.CERT_NO,a.WORK_STATE_CODE,a.WORK_STATE,a.CLIENT_ID,a.UPDATE_TIME,a.RESERVE1,a.RESERVE2,a.RESERVE3,a.RESERVE4,a.RESERVE5 "
					+ "FROM HRM_ZDSTAFFCERT a, SY_HRM_ZDSTAFFCERT b WHERE a.PERSON_ID = b.PERSON_ID");
			List<Bean> allResultList = Transaction.getExecutor().query("SELECT a.PERSON_ID,a.SSIC_ID,a.NAME,a.CERT_TYPE_CODE,a.CERT_TYPE,a.CERT,a.CERT_NO,a.WORK_STATE_CODE,a.WORK_STATE,a.CLIENT_ID,a.UPDATE_TIME,a.RESERVE1,a.RESERVE2,a.RESERVE3,a.RESERVE4,a.RESERVE5 "
					+ "FROM HRM_ZDSTAFFCERT a ");
			allResultList.removeAll(sameResultList);
			List<Bean> diffResultList = allResultList;
			int sameListSize = sameResultList.size();
			int diffListSize = diffResultList.size();
			for (int i = 0; i < diffListSize; i++) {
				createList.add(diffResultList.get(i));
			}
			for (int j = 0; j < sameListSize; j++) {
				updateList.add(sameResultList.get(j));
			}
			if (createList.size() > 0) {
				ServDao.creates(targetServId, createList);
			}
			if (updateList.size() > 0) {
				@SuppressWarnings({ "rawtypes", "unchecked" })
				List<String> updateFieldList = new ArrayList(updateList.get(0).keySet());
				// TODO 可能有多余字段需要remove
				ServDao.updates(targetServId, updateFieldList, updateList);
			}
			// 整体提交数据
			Transaction.commit();*/
			
			Transaction.begin();
			HashSet<String> addCodes = new HashSet<String>();
			
			int total = ServDao.count(sourceServId, new ParamBean().setWhere(" AND 1 = 1"));
			
			for (int i = 1; i <= total / SAVE_COUNT + 1; i++) {
				List<Bean> createList = new ArrayList<Bean>();
				List<Bean> updateList = new ArrayList<Bean>();
				
				ParamBean param = new ParamBean();
				param.setWhere(" AND 1 = 1");
				param.setShowNum(SAVE_COUNT);
				param.setNowPage(i);
				setQueryWhere(param);
				List<Bean> resultList = ServDao.finds(sourceServId, param);
				
				int resultLen = resultList.size();
				for (int j = 0; j < resultLen; j++) {
					Bean data = resultList.get(j);
					String[] pkCodeItems = getPKCodeItems();
					// 获取接口数据主键
					String oPKs = getPkeyValues(data, pkCodeItems);
					
					//处理时间
					data.set("S_MTIME", smtime);
					// 处理数据
					parseOneData(data);
					
					if (addCodes.contains(oPKs)) {
						// 同一批数据中已经有新增的了，第二条记录处理成更新的
						updateList.add(data);
					}else{
						addCodes.add(oPKs);
						int existCount = getDataCount(targetServId, pkCodeItems, data);
						if (existCount == 0) {
							createList.add(data);
						}else{
							updateList.add(data);
						}
					}
				}
				if (createList.size() > 0) {
					ServDao.creates(targetServId, createList);
				}
				if (updateList.size() > 0) {
					@SuppressWarnings({ "rawtypes", "unchecked" })
					List<String> updateFieldList = new ArrayList(updateList.get(0).keySet());
					// TODO 可能有多余字段需要remove
					ServDao.updates(targetServId, updateFieldList, updateList);
				}
			}
			// 整体提交数据
			Transaction.commit();
			
		} catch (Exception e) {
			Transaction.rollback();
			log.error("import " + targetServId + " error ! " + e.getMessage());
			OrgLogMgr.orgLogSave("", "CC_DATA_IMPORT", "import " + targetServId + " error ! " + e.getMessage(),
					ExceptionUtils.getFullStackTrace(e));
			successFlag = false;
		} finally {
			log.info("------------------ import " + targetServId + " increment data end! ------------------");
			Transaction.end();
		}
		
		return successFlag;
	}

}
