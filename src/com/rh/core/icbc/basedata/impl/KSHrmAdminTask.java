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
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.util.Constant;

/**
 * 更新hrm_zdstaffadmin接口表到sy_hrm_zdstaffadminu全量表的线程任务
 * @author leader
 *
 */
public class KSHrmAdminTask extends AbstractTableTask{

	private static final long serialVersionUID = 773302700991171190L;
	public KSHrmAdminTask(String smtime, boolean flag) {
		super(smtime, flag);
	}

	private String HRM_ZDSTAFFADMIN = "HRM_ZDSTAFFADMIN";
	private String SY_HRM_ZDSTAFFADMIN = "SY_HRM_ZDSTAFFADMIN";
	@Override
	protected String getSourceServId() {
		return HRM_ZDSTAFFADMIN;
	}

	@Override
	public String getTargetServId() {
		return SY_HRM_ZDSTAFFADMIN;
	}

	@Override
	protected String[] getPKCodeItems() {
		String[] pkCodeItems = { "SEQ_NO"};
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
			} else if (updateFlag.equals("2") || updateFlag.equals("F") || updateFlag.equals("")) {
				data.set("S_FLAG", Constant.NO_INT);
			} else {
				log.error("错误:data:"+data+"updateFlag:"+updateFlag);
				throw new TipException("【数据更新标志未知】！data : " + data + ", updateFlag : " + updateFlag);
			}
		}
	}
	
	
	@Override
	protected void setQueryWhere(ParamBean param) {
		param.setOrder("UPDATE_FLAG");
	}
	
	/**
	 * 增量导入数据。特殊处理需要子类重写
	 * @return - 是否导入成功
	 */
	public boolean impIncData() {
		boolean successFlag = true;
		// 目标服务ID
		String targetServId = getTargetServId();
		// 源服务ID
		String sourceServId = getSourceServId();
//		ServDefBean sourceServ = ServUtils.getServDef(sourceServId);
		
		log.error("------------------ import " + targetServId + " increment data begin! ------------------");
		
		try {
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
