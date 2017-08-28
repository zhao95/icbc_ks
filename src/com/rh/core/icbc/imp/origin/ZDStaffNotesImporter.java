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

public class ZDStaffNotesImporter extends BaseServ {
	// 服务定义
	private static final String SERVID = "SY_HRM_ZDSTAFFNOTES";
	// 全量、增量数据标志下标
	private static final int UPDATE_TIME_NO = 6;
	// 字段项总数
	private static final int ITEM_COUNT = 12;
	// 增删改标志下标
	private static final int UPDATE_FLAG_NO = 5;
	// 批量保存数
	private static final int SAVE_COUNT = 5000;

	/**
	 * 将数据文件的数据导入SY_HRM_ZDSTAFFNOTES表中 如果UPDATE_TIME = FULLDATA,则为全量数据；否则为增量数据。
	 * 
	 * @param fileUrl
	 *            数据文件路径
	 */
	public void imp(String fileUrl) {
		File file = new File(fileUrl);
		if (CommonImporter.isFullData(file, UPDATE_TIME_NO)) {
			doFullData(file);
		} else {
			doIncrementalData(file);
		}
	}

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
					String[] items = line.split(CommonImporter.STRU_STAFF_SPLIT, -1);
					if (items.length < ITEM_COUNT) {
						log.error("【数据不全!】" + line);
						continue;
					}
					String updateFlag = items[UPDATE_FLAG_NO].trim();
					// 根据updateflag将数据分到不同的List中
					if (updateFlag.equalsIgnoreCase(CommonImporter.ZERO)) {
						createList.add(addData(items));
					} else if (updateFlag.equalsIgnoreCase(CommonImporter.ONE)) {
						updateList.add(addData(items));
					} else if (updateFlag.equalsIgnoreCase(CommonImporter.TWO)) {
						deleteList.add(addDeleteData(items[0], items[1], ""));
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
					ServDao.updates(SERVID, addFieldItem(), updateList);
					Transaction.commit();
				}
				// 最后删除数据
				if (deleteList.size() > 0) {
					CommonImporter.deleteData(SERVID, deleteList);
					Transaction.commit();
				}
			} catch (Exception e) {
				Transaction.rollback();
				log.error(e.getMessage());
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
	 * 更新字段List
	 * 
	 * @return 更新字段List
	 */
	List<String> addFieldItem() {
		List<String> fieldList = new ArrayList<String>();
		fieldList.add("PERSON_ID");
		fieldList.add("FlAG");
		fieldList.add("OPT_TIME");
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
	 * 添加删除信息到dataBean中
	 * 
	 * @param items
	 *            数据数组
	 * @return 删除Bean
	 */
	private Bean addDeleteData(String ssicId, String notes, String smtime) {
		Bean dataBean = new Bean();
		dataBean.set("SSIC_ID", ssicId);
		dataBean.set("NOTES", notes);
		dataBean.set("S_FLAG", Constant.NO_INT);
		dataBean.set("S_MTIME", smtime);
		return dataBean;
	}

	/**
	 * 全量数据操作
	 * 
	 * @param file
	 *            数据文件
	 */
	private void doFullData(File file) {
		Transaction.begin();
		// 清空数据
		ServDefBean servBean = ServUtils.getServDef(SERVID);
		Transaction.getExecutor().execute("TRUNCATE TABLE " + servBean.getTableAction());
		Transaction.commit();
		// 增加数据
		LineIterator it;
		try {
			it = FileUtils.lineIterator(file, CommonImporter.STRU_STAFF_CODE);
			List<Bean> dataList = new ArrayList<Bean>();
			try {
				while (it.hasNext()) {
					String line = it.nextLine();
					String[] items = line.split(CommonImporter.STRU_STAFF_SPLIT, -1);
					if (items.length < ITEM_COUNT) {
						log.error("【数据不全！】" + line);
						continue;
					}
					dataList.add(addData(items));
					// 批量新增数据
					if (dataList.size() >= SAVE_COUNT) {
						ServDao.creates(SERVID, dataList);
						dataList.clear();
					}
				}
				if (dataList.size() != 0) {
					ServDao.creates(SERVID, dataList);
				}
				Transaction.commit();
			} catch (Exception e) {
				Transaction.rollback();
				log.error(e.getMessage());
			} finally {
				LineIterator.closeQuietly(it);
				Transaction.end();
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 将数据分割后，放入Bean中 增加和修改
	 */
	private Bean addData(String[] items) {
		Bean dataBean = new Bean();
		dataBean.set("SSIC_ID", items[0]).set("NOTES", items[1]);
		dataBean.set("PERSON_ID", items[2]).set("FLAG", items[3]);
		dataBean.set("OPT_TIME", items[4]).set("UPDATE_TIME", items[6]);
		dataBean.set("RESERVE1", items[7]).set("RESERVE2", items[8]);
		dataBean.set("RESERVE3", items[9]).set("RESERVE4", items[10]);
		dataBean.set("RESERVE5", items[11]).set("S_MTIME", DateUtils.getDatetimeTS());
		return dataBean;
	}

	/**
	 * HRM_ZDSTAFFNOTES ----> SY_HRM_ZDSTAFFNOTES 从增量表中将数据导入到中间表
	 */
	public boolean impStaffNotesData(final String smtime) throws Exception {
		ParamBean param = new ParamBean();
		param.setWhere("and 1=1");
		int total = ServDao.count(CommonImporter.HRM_ZDSTAFFNOTES, param);
		//接口表数据为空，返回
		if(total == 0){
			log.info("接口表HRM_ZDSTAFFNOTES无增量数据！");
			return false;
		}
		final List<Bean> createList = new ArrayList<Bean>();
		final List<Bean> updateList = new ArrayList<Bean>();
		final List<Bean> deleteList = new ArrayList<Bean>();
		ServDao.findsCall(CommonImporter.HRM_ZDSTAFFNOTES, param, new RowHandler() {
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
					deleteList.add(addDeleteData(data.getStr("SSIC_ID"), data.getStr("NOTES"), smtime));
				} else {
					throw new TipException("【数据更新标志未知】！" + updateFlag);
				}
			}
		});

		if (createList.size() > 0) {
			ServDao.creates(SERVID, createList);
		}
		if (updateList.size() > 0) {
			ServDao.updates(SERVID, addFieldItem(), updateList);
		}
		if (deleteList.size() > 0) {
			CommonImporter.deleteData(SERVID, deleteList);
		}
		Transaction.commit();
		return true;
	}

	/**
	 * 全量数据操作
	 * 
	 * @param file
	 *            数据文件
	 */
	public boolean impStaffNotesFullData(String smtime) throws Exception {
		log.debug("=======impStaffNotesFullData start====");
		Transaction.begin();
		// 清空数据
		ServDefBean servBean = ServUtils.getServDef(SERVID);
		Transaction.getExecutor().execute("TRUNCATE TABLE " + servBean.getTableAction());
		Transaction.commit();
		// 增加数据
		ParamBean param = new ParamBean();
		param.setWhere("and 1=1");
		int total = ServDao.count(CommonImporter.HRM_ZDSTAFFNOTES, param);
		// 接口表数据为空，返回
		if (total == 0) {
			log.info("接口表HRM_ZDSTAFFNOTES无全量数据！");
			return false;
		}
		for (int i = 1; i <= total / SAVE_COUNT + 1; i++) {
			ParamBean queryBean = new ParamBean();
			queryBean.set(Constant.PAGE_NOWPAGE, i); // 当前页数
			queryBean.set(Constant.PAGE_SHOWNUM, SAVE_COUNT); // 每页条数
			queryBean.setWhere("and 1=1");
			List<Bean> resultList = ServDao.finds(CommonImporter.HRM_ZDSTAFFNOTES, queryBean);
			for (Bean data : resultList) {
				
				// 设置S_MTIME
				data.set("S_MTIME", smtime);
			}
			ServDao.creates(SERVID, resultList);
			Transaction.commit();
		}
		
		log.debug("=======impStaffNotesFullData end====");
		return true;
	}
	
}
