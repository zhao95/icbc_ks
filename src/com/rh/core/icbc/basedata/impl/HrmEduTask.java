package com.rh.core.icbc.basedata.impl;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.base.db.Transaction;
import com.rh.core.icbc.basedata.AbstractTableTask;
import com.rh.core.icbc.imp.log.OrgLogMgr;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.util.Constant;

public class HrmEduTask extends AbstractTableTask {

	private static final long serialVersionUID = 3854638617843286078L;

	private String HRM_ZDSTAFFEDU = "HRM_ZDSTAFFEDU";
	private String SY_HRM_ZDSTAFFEDU = "SY_HRM_ZDSTAFFEDU";

	public HrmEduTask(String smtime, boolean flag) {
		super(smtime, flag);
	}

	@Override
	protected String getSourceServId() {
		return HRM_ZDSTAFFEDU;
	}

	@Override
	public String getTargetServId() {
		return SY_HRM_ZDSTAFFEDU;
	}

	@Override
	protected String[] getPKCodeItems() {
		String[] pkCodeItem = { "PERSON_ID" };
		return pkCodeItem;
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
	
	@Override
	public boolean impIncData() {
		boolean successFlag = true;
		// 目标服务ID
		String targetServId = getTargetServId();
		// 源服务ID
		String sourceServId = getSourceServId();
		
		log.error("------------------ import " + targetServId + " increment data begin! ------------------");
		
		try {
			Transaction.begin();
			// 获取全量数据总数
			int total = ServDao.count(sourceServId, new ParamBean());
			// 接口表数据为空
			if (total == 0) {
				log.info("----------- interface " + targetServId + " no full data -------------");
			} else {
				
				// 分页5000条处理一次
				for (int i = 1; i <= total / SAVE_COUNT + 1; i++) {
					ParamBean queryBean = new ParamBean();
					queryBean.set(Constant.PAGE_NOWPAGE, i); // 当前页数
					queryBean.set(Constant.PAGE_SHOWNUM, SAVE_COUNT); // 每页条数
					queryBean.setWhere(" AND 1 = 1");
					List<Bean> resultList = ServDao.finds(sourceServId, queryBean);
					for (int j = 0; j < resultList.size(); j++) {
						
						// 设置S_MTIME
						resultList.get(j).set("S_MTIME", smtime);
						
						// 特殊处理数据，供子类重写 
						parseOneData(resultList.get(j));
					}
					ServDao.creates(targetServId, resultList);
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
			log.error("------------------ import " + targetServId + " increment data end! ------------------");
			Transaction.end();
		}
		
		return successFlag;
	}
}
