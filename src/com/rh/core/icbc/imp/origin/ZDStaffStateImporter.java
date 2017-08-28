package com.rh.core.icbc.imp.origin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.base.db.RowHandler;
import com.rh.core.base.db.Transaction;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.base.BaseServ;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;
/**
 * 机构状态信息数据导入 （SY_HRM_ZDSTAFFSTATEINFO 表）
 * @author caoyiqing
 *
 */
public class ZDStaffStateImporter extends BaseServ {

	private static final String SERVID = "SY_HRM_ZDSTAFFSTATE";
	// 数据项数量
	private static final int ITEM_COUNT = 22;
	// 更新标志的字段下标
	private static final int UPDATE_FLAG_NO = 15;
	// 增量全量的标志下标
	private static final int UPDATE_TIME_NO = 16;
	private static final int SAVE_COUNT = 5000;

	/**
	 * 将数据文件的数据导入SY_HRM_ZDSTAFFCONTACT表中<br/>
	 *  如果UPDATE_TIME = FULLDATA,则为全量数据；否则为增量数据。
	 * @param paramBean
	 *            参数Bean
	 */
	public String imp(String fileUrl) {
		String result = "ERROR";
		if(CommonImporter.isExistFile(fileUrl)){
			File file = new File(fileUrl);
			// 判断数据（增量、全量），分别做各自的操作
			if (CommonImporter.isFullData(file, UPDATE_TIME_NO)) {
				doFullData(file);
				result = "FULLDATA";
			} else {
				doIncrementalData(file);
				result = "INCREMENT";
			}
		}
		return result;
	}

	/**
	 * 增量数据的处理<br/>
	 * 对数据按照updateflag进行分组处理，放入不同的list中进行处理<br/>
	 * 0：需要添加的数据; 1：需要修改的数据; 2：需要删除的数据; 先增再改后删除
	 * 
	 * @param file
	 *            数据文件
	 */
	private void doIncrementalData(File file) {
		// 新增list，更新list和删除list
		List<Bean> createList = new ArrayList<Bean>();
		List<Bean> updateList = new ArrayList<Bean>();
		List<Bean> deleteList = new ArrayList<Bean>();
		LineIterator it;
		try {
			it = FileUtils.lineIterator(file, CommonImporter.STRU_STAFF_CODE);
			try {
				while (it.hasNext()) {
					String line = it.nextLine();
					String[] items = line.split(
							CommonImporter.STRU_STAFF_SPLIT, -1);
					if (items.length < ITEM_COUNT) {
						log.error("【数据不全!】" + line);
						continue;
					}
					String updateFlag = items[UPDATE_FLAG_NO];
					// 根据updateflag将数据分到不同的List中
					if (updateFlag
							.equalsIgnoreCase(CommonImporter.ZERO)) {
						createList.add(addData(items));
					} else if (updateFlag
							.equalsIgnoreCase(CommonImporter.ONE)) {
						updateList.add(addData(items));
					} else if (updateFlag
							.equalsIgnoreCase(CommonImporter.TWO)) {
						deleteList.add(addDeleteData(items[0], ""));
					} else {
						log.error("【数据更新标志未知】！" + line);
					}
				}
				Transaction.begin();
				// 先增添数据 ；
				if (createList.size() > 0) {
					ServDao.creates(SERVID, createList);
					Transaction.commit();
				}
				// 后更新数据；
				if (updateList.size() > 0) {
					ServDao.updates(SERVID, addFieldList(), updateList);
					Transaction.commit();
				}
				// 最后删除数据,即置S_FLAG=2
				if (deleteList.size() > 0) {
					CommonImporter.deleteData(SERVID, deleteList);
					Transaction.commit();
				}
			} catch(Exception e){
				Transaction.rollback();
				log.error(e.getMessage(), e);
			}finally {
				Transaction.end();
				LineIterator.closeQuietly(it);
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 更新字段设置
	 * 
	 * @return 字段List
	 */
	private List<String> addFieldList() {
		List<String> fieldList = new ArrayList<String>();
		fieldList.add("SSIC_ID");
		fieldList.add("NAME");
		fieldList.add("WORK_STATE_CODE");
		fieldList.add("WORK_STATE");
		fieldList.add("STRU_ID");
		fieldList.add("STRU_NAME");
		fieldList.add("LV1_STRU_ID");
		fieldList.add("LV1_STRU_NAME");
		fieldList.add("LV2_STRU_ID");
		fieldList.add("LV2_STRU_NAME");
		fieldList.add("SUP_BR14_ID");
		fieldList.add("SUP_BR14_NAME");
		fieldList.add("DUTY_LV_CODE");
		fieldList.add("DUTY_LV");
		fieldList.add("UPDATE_TIME");
		fieldList.add("RESERVE1");
		fieldList.add("RESERVE2");
		fieldList.add("RESERVE3");
		fieldList.add("RESERVE4");
		fieldList.add("RESERVE5");
		fieldList.add("S_MTIME");
		return fieldList;
	}

	/**
	 * 全量数据处理 1.清空数据表中的数据 2.添加数据到数据表中
	 * 
	 * @param servId
	 * @param file
	 */
	private void doFullData(File file) {
		Transaction.begin();
		// 清空数据
		ServDefBean servBean = ServUtils.getServDef(SERVID);
		Transaction.getExecutor().execute("TRUNCATE TABLE "+ servBean.getTableAction());
		Transaction.commit();
		// 增加数据
		LineIterator it;
		try {
			it = FileUtils.lineIterator(file, CommonImporter.STRU_STAFF_CODE);
			List<Bean> dataList = new ArrayList<Bean>();
			try {
				while (it.hasNext()) {
					String line = it.nextLine();
					String[] items = line
							.split(CommonImporter.STRU_STAFF_SPLIT, -1);
					if (items.length < ITEM_COUNT) {
						log.error("【数据不全！】" + line);
						continue;
					}
					dataList.add(addData(items));
					// 批量新增数据
					if (dataList.size() >= SAVE_COUNT) {
						ServDao.creates(SERVID, dataList);
						Transaction.commit();
						dataList.clear();
					}
				}
				if (dataList.size() != 0) {
					ServDao.creates(SERVID, dataList);
				}
				Transaction.commit();
			}catch( Exception e){
				log.error(e.getMessage(), e);
				Transaction.rollback();
			} finally {
				Transaction.end();
				LineIterator.closeQuietly(it);
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}	
	}

	/**
	 * 将数据分割后，放入Bean中
	 */
	private Bean addData(String[] items) {
		Bean dataBean = new Bean();
		dataBean.set("PERSON_ID", items[0]).set("SSIC_ID", items[1]);
		dataBean.set("NAME", items[2]).set("WORK_STATE_CODE", items[3]);
		dataBean.set("WORK_STATE", items[4]).set("STRU_ID", items[5]);
		dataBean.set("STRU_NAME", items[6]).set("LV1_STRU_ID", items[7]);
		dataBean.set("LV1_STRU_NAME", items[8]).set("LV2_STRU_ID", items[9]);
		dataBean.set("LV2_STRU_NAME", items[10]).set("SUP_BR14_ID", items[11]);
		dataBean.set("SUP_BR14_NAME", items[12]).set("DUTY_LV_CODE", items[13]);
		dataBean.set("DUTY_LV", items[14]).set("UPDATE_TIME", items[16]);
		dataBean.set("RESERVE1", items[17]).set("RESERVE2", items[18]);
		dataBean.set("RESERVE3", items[19]).set("RESERVE4", items[20]);
		dataBean.set("RESERVE5", items[21]).set("S_MTIME", DateUtils.getDatetimeTS());
		return dataBean;
	}

	/**
	 * 将数据分割后，放入Bean中
	 */
	private Bean addDeleteData(String personId, String smtime) {
		Bean dataBean = new Bean();
		dataBean.set("PERSON_ID", personId);
		dataBean.set("S_FLAG", Constant.NO_INT);
		dataBean.set("S_MTIME", smtime);
		return dataBean;

	}
	
	/**
	 * HRM_ZDSTAFFSTATE ----> SY_HRM_ZDSTAFFSTATE 从增量表中将数据导入到中间表
	 * 
	 *  @return true:导入成功 ；  false：接口表为空
	 */
	public boolean impStaffStateData(final String smtime) throws Exception {
		log.debug("=======impStaffStateData start====");
		ParamBean param = new ParamBean();
		param.setWhere("and 1=1");
		int total = ServDao.count(CommonImporter.HRM_ZDSTAFFSTATE, param);
		//接口表数据为空，返回
		if(total == 0){
			log.info("接口表HRM_ZDSTAFFSTATE无增量数据！");
			return false;
		}
		
		final List<Bean> createList = new ArrayList<Bean>();
		final List<Bean> updateList = new ArrayList<Bean>();
		final List<Bean> deleteList = new ArrayList<Bean>();
		ServDao.findsCall(CommonImporter.HRM_ZDSTAFFSTATE, param, new RowHandler() {
			@Override
			public void handle(List<Bean> columns, Bean data) {
				data.set("S_MTIME", smtime);
				String updateFlag = data.getStr("UPDATE_FLAG");
				// 根据updateflag将数据分到不同的List中
				if (updateFlag.equalsIgnoreCase(CommonImporter.ZERO)) {
					createList.add(data);
				} else if (updateFlag.equalsIgnoreCase(CommonImporter.ONE)) {
					updateList.add(data);
				} else if (updateFlag.equalsIgnoreCase(CommonImporter.TWO)) {
					deleteList.add(addDeleteData(data.getStr("PERSON_ID"), smtime));
				} else {
					throw new TipException("【数据更新标志未知】！" + updateFlag);
				}
			}
		});

		if (createList.size() > 0) {
			ServDao.creates(SERVID, createList);
		}
		if (updateList.size() > 0) {
			ServDao.updates(SERVID, addFieldList(), updateList);
		}
		if (deleteList.size() > 0) {
			CommonImporter.deleteData(SERVID, deleteList);
		}
		Transaction.commit();
		log.debug("=======impStaffStateData end====");
		return true;
	}
	
	/**
	 * 全量数据处理 1.清空数据表中的数据 2.添加数据到数据表中
	 * 
	 *@return true:导入成功 ；  false：接口表为空 
	 */
	public boolean impStaffStateFullData(String smtime) throws Exception {
		log.debug("=======impStaffStateFullData start====");
//		Transaction.begin();
		// 清空数据
		ServDefBean servBean = ServUtils.getServDef(SERVID);
		Transaction.getExecutor().execute("TRUNCATE TABLE "+ servBean.getTableAction());
		Transaction.commit();
		// 增加数据
		
		ParamBean param = new ParamBean();
		param.setWhere("and 1=1");
		int total = ServDao.count(CommonImporter.HRM_ZDSTAFFSTATE, param);
		//接口表数据为空，返回
		if(total == 0){
			log.info("接口表HRM_ZDSTAFFSTATE无全量数据！");
			return false;
		}
		for (int i = 1; i <= total / SAVE_COUNT + 1; i++) {
			ParamBean queryBean = new ParamBean();
			queryBean.set(Constant.PAGE_NOWPAGE, i); // 当前页数
			queryBean.set(Constant.PAGE_SHOWNUM, SAVE_COUNT); // 每页条数
			queryBean.setWhere("and 1=1");
			List<Bean> resultList = ServDao.finds(CommonImporter.HRM_ZDSTAFFSTATE, queryBean);
		    for(Bean data : resultList ){
				// 设置S_MTIME
				data.set("S_MTIME", smtime);
		    }
		    ServDao.creates(SERVID, resultList);
		    Transaction.commit();
		}
		
		log.debug("=======impStaffStateFullData end====");
		return true;
	}

}
