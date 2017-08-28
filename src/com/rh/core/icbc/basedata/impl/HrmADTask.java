package com.rh.core.icbc.basedata.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.base.db.RowHandler;
import com.rh.core.base.db.Transaction;
import com.rh.core.icbc.basedata.AbstractTableTask;
import com.rh.core.icbc.imp.log.OrgLogMgr;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.util.Constant;

public class HrmADTask extends AbstractTableTask {

	private static final long serialVersionUID = -1653452688177629948L;

	private static String HRM_ZDSTAFFAD = "HRM_ZDSTAFFAD";
	private static String SY_HRM_ZDSTAFFAD = "SY_HRM_ZDSTAFFAD";

	public HrmADTask(String smtime, boolean flag) {
		super(smtime, flag);
	}

	@Override
	protected String getSourceServId() {
		return HRM_ZDSTAFFAD;
	}

	@Override
	public String getTargetServId() {
		return SY_HRM_ZDSTAFFAD;
	}

	@Override
	protected String[] getPKCodeItems() {
		String[] pkCodeItems = { "SSIC_ID", "AD" };
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
	 * 处理增量
	 */
	@Override
	public boolean impIncData() {
		log.info("------------------ import SY_HRM_ZDSTAFFAD increment data begin ------------------");
		boolean successFlag = true;
		final HashMap<String, Boolean> deleteMap = new HashMap<String, Boolean>();
		try {
			Transaction.begin();
			int total = ServDao.count("HRM_ZDSTAFFAD", new ParamBean().setWhere(" AND 1=1"));
			for (int i = 1; i <= total / SAVE_COUNT + 1; i++) {
				final List<Bean> addList = new ArrayList<Bean>();
				ParamBean param = new ParamBean();
				param.setWhere(" AND 1=1");
				param.setShowNum(SAVE_COUNT);
				param.setNowPage(i);
				ServDao.findsCall("HRM_ZDSTAFFAD", param, new RowHandler() {

					@Override
					public void handle(List<Bean> columns, Bean data) {
						data.set("S_MTIME", smtime);
						String ssicId = data.getStr("SSIC_ID");

						// 如果[已删除hashMap]中没有ssicId,则执行删除操作,并将ssicId加入map中
						if (!deleteMap.containsKey(ssicId)) {
							ServDao.deletes("SY_HRM_ZDSTAFFAD",
									new ParamBean().setWhere(" AND SSIC_ID = '" + ssicId + "'"));
							deleteMap.put(ssicId, true);
						}
						// 添加如增加集合中
						addList.add(data);
					}
				});
				// 批量添加入数据库
				ServDao.creates("SY_HRM_ZDSTAFFAD", addList);
			}
			Transaction.commit();
		} catch (Exception e) {
			Transaction.rollback();
			log.error("import SY_HRM_ZDSTAFFAD increment error ! " + e.getMessage());
			OrgLogMgr.orgLogSave("", "CC_DATA_IMPORT", "import SY_HRM_ZDSTAFFAD increment error ! " + e.getMessage(),
					ExceptionUtils.getFullStackTrace(e));
			successFlag = false;
		} finally{
			Transaction.end();
		}
		log.info("------------------ import SY_HRM_ZDSTAFFAD increment data end ------------------");
		return successFlag;
	}

}
