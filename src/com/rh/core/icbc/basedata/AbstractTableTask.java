package com.rh.core.icbc.basedata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.icbc.imp.log.OrgLogMgr;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Constant;
import com.rh.core.util.threadpool.RhThreadTask;

/**
 * 通用HR数据导入实现类。特殊处理的数据需要继承此类，重写方法
 * @author if
 *
 */
public abstract class AbstractTableTask extends RhThreadTask {
	
	private static final long serialVersionUID = 1L;
	/**
     * 记录日志信息的函数
     */
    protected Log log = LogFactory.getLog(this.getClass());
    
    protected int SAVE_COUNT = 5000;
    
    
    

	// 导入时间
	protected String smtime;
	
	//增量标识
	protected boolean incrementFlag;
	
	/**
	 * 构造方法
	 * @param smtime
	 */
	public AbstractTableTask(String smtime, boolean flag) {
		this.smtime = smtime;
		this.incrementFlag = flag;
	}

	/**
	 * 获取源服务名,子类需要重写此方法
	 * @return
	 */
	protected abstract String getSourceServId();
	
	/**
	 * 获取目标服务名,子类需要重写此方法
	 * @return
	 */
	public abstract String getTargetServId();
	
	/**
	 * 获取服务主键,子类需要重写此方法
	 * @return
	 */
	protected abstract String[] getPKCodeItems();
	
	/**
	 * 特殊处理数据,供子类重写
	 * 
	 */
	protected abstract void parseOneData(Bean data);
	
	/**
	 * 设置查询条件
	 * 主要设置字段排序等特殊条件
	 * @param param
	 * @return
	 */
	protected abstract void setQueryWhere(ParamBean param);
	
	/**
	 * 获取导入时间
	 * @return
	 */
	protected String getSmtime() {
		return smtime;
	}
	
	/**
	 * 全量导入数据。特殊处理需要子类重写
	 * @return - 是否导入成功
	 */
	public boolean impFullData() {
		boolean successFlag = true;
		// 目标服务ID
		String targetServId = getTargetServId();
		// 源服务ID
		String sourceServId = getSourceServId();
		
		log.error("------------------ import " + targetServId + " full data begin! ------------------");
		
		//获取操作表
		ServDefBean targetServ = ServUtils.getServDef(targetServId);
		String targetActionTable = targetServ.getTableAction();
		
		try {
			Transaction.begin();
			// 清空数据
			Transaction.getExecutor().execute("TRUNCATE TABLE " + targetActionTable);
			
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
			log.error("------------------ import " + targetServId + " full data end! ------------------");
			Transaction.end();
		}
		
		return successFlag;
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
	
	/**
	 * 返回上游数据主键值
	 * 
	 * @param data
	 *            数据
	 * @param keys
	 *            主键
	 * @return
	 */
	protected String getPkeyValues(Bean data, String[] keys) {
		String pkValues = "";
		for (String key : keys) {
			pkValues += data.getStr(key)+"^";
		}
		return pkValues;
	}
	
	/**
	 * 返回数据记录条数
	 * @param servId
	 * @param pks
	 * @param data
	 * @return
	 */
	protected int getDataCount(String servId, String[] pks, Bean data) {
		SqlBean csql = new SqlBean();
		for(String pk : pks){
			csql.and(pk, data.getStr(pk));
		}
		int count = ServDao.count(servId, csql);
		return count;
	}
	
	/**
	 * 设置导入状态
	 * @param successFlag
	 */
	protected void setImpFlag(boolean successFlag){
		try{
			Transaction.begin();
			ServDao.deletes("SY_BASEDATA_IMP_STATE", new ParamBean().set("IMP_DATE", smtime).set("SERV_ID", getTargetServId()));
			ParamBean dataBean = new ParamBean();
			dataBean.set("SERV_ID", getTargetServId());
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
		}catch(Exception e){
			Transaction.rollback();
			log.error("设置SY_BASEDATA_IMP_STATE的状态出错  "+getTargetServId());
		}finally{
			Transaction.end();
		}
	}
	
	@Override
	public boolean execute() {
		
		boolean successFlag = true; 
		if (incrementFlag) { // 增量
			successFlag = impIncData();
		} else { // 全量
			successFlag = impFullData();
		}
		setImpFlag(successFlag);
		
		return true;
	}
	
	
	
	
}
