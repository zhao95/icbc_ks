package com.rh.core.icbc.imp.origin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;

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
 * 数据导入 SY_HRM_ZDSTAFFCONTACT
 * 
 * @author caoyiqing
 *
 */
public class ZDStaffContactImporter extends BaseServ {

	private Logger log = Logger.getLogger(this.getClass());
	private static final String SERVID = "SY_HRM_ZDSTAFFCONTACT";
	public static final int SAVE_COUNT = 5000;
	private static final int ITEM_COUNT = 27;
	private static final int UPDATE_TIME_NO = 21;
	private static final int UPDATE_FLAG_NO = 20;

	/**
	 * 将数据文件的数据导入SY_HRM_ZDSTAFFCONTACT表中 如果UPDATE_TIME =
	 * FULLDATA,则为全量数据；否则为增量数据。
	 * 
	 * @param fileUrl
	 *            数据文件路径
	 */
	public String imp(String fileUrl) {
		String result = "ERROR";
		if (CommonImporter.isExistFile(fileUrl)) {
			File file = new File(fileUrl);
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
	 * 0：需要添加的数据; 1：需要修改的数据; 2：需要删除的数据;
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
						deleteList.add(addDeleteData(items[0], items[22], ""));
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
				log.error(e.getMessage(), e);
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
	 * 全量数据处理 1.清空数据表中的数据 2.添加数据到数据表中
	 * 
	 * @param servId
	 * @param file
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
						Transaction.commit();
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
		dataBean.set("PERSON_ID", items[0]).set("NAME", items[1]);
		dataBean.set("OFFICE_PHONE1", items[2]).set("OFFICE_PHONE2", items[3]);
		dataBean.set("FAX", items[4]).set("OFFICE_ADDRESS", items[5]);
		dataBean.set("OFFICE_ROOMNO", items[6]).set("OFFICE_ZIPCODE", items[7]);
		dataBean.set("VEDIO_PHONE", items[8]).set("MOBILE_PHONE1", items[9]);
		dataBean.set("MOBILE_PHONE2", items[10]).set("HOME_PHONE1", items[11]);
		dataBean.set("HOME_PHONE2", items[12]).set("ADDRESS", items[13]);
		dataBean.set("ZIPCODE", items[14]).set("EMAIL", items[15]);
		dataBean.set("MSN", items[16]).set("PERSON_URL", items[17]);
		dataBean.set("MEMO", items[18]).set("MODIFY_TIME", items[19]);
		dataBean.set("UPDATE_TIME", items[21]).set("STRU_ID", items[22]);
		dataBean.set("SORT", items[23]).set("RESERVE3", items[24]);
		dataBean.set("RESERVE4", items[25]).set("RESERVE5", items[26]);
		dataBean.set("S_MTIME", DateUtils.getDatetimeTS());
		return dataBean;

	}

	/**
	 * 将数据分割后，放入Bean中
	 */
	private Bean addDeleteData(String personId, String struId, String smtime) {
		Bean dataBean = new Bean();
		dataBean.set("PERSON_ID", personId);
		dataBean.set("STRU_ID", struId);
		dataBean.set("S_FLAG", Constant.NO_INT);
		dataBean.set("S_MTIME", smtime);
		return dataBean;

	}

	/**
	 * 更新字段
	 */
	private List<String> addFieldItem() {

		List<String> fieldList = new ArrayList<String>();
		fieldList.add("NAME");
		fieldList.add("OFFICE_PHONE1");
		fieldList.add("OFFICE_PHONE2");
		fieldList.add("FAX");
		fieldList.add("OFFICE_ADDRESS");
		fieldList.add("OFFICE_ROOMNO");
		fieldList.add("OFFICE_ZIPCODE");
		fieldList.add("VEDIO_PHONE");
		fieldList.add("MOBILE_PHONE1");
		fieldList.add("MOBILE_PHONE2");
		fieldList.add("HOME_PHONE1");
		fieldList.add("HOME_PHONE2");
		fieldList.add("ADDRESS");
		fieldList.add("ZIPCODE");
		fieldList.add("EMAIL");
		fieldList.add("MSN");
		fieldList.add("PERSON_URL");
		fieldList.add("MEMO");
		fieldList.add("MODIFY_TIME");
		fieldList.add("UPDATE_TIME");
		fieldList.add("SORT");
		fieldList.add("RESERVE3");
		fieldList.add("RESERVE4");
		fieldList.add("RESERVE5");
		fieldList.add("S_MTIME");
		return fieldList;
	}

	/**
	 * HRM_ZDSTAFFCONTACT ----> SY_HRM_ZDSTAFFCONTACT 从增量表中增量将数据导入到中间表
	 * 
	 * @return true 导入数据成功 ； false 数据不存在
	 */
	public boolean impStaffContactData(final String smtime) throws Exception {
		log.debug("=======impStaffContactData start====");
		ParamBean param = new ParamBean();
		param.setWhere("and 1=1");

		int total = ServDao.count(CommonImporter.HRM_ZDSTAFFCONTACT, param);
		// 接口表数据为空，返回
		if (total == 0) {
			log.info("接口表HRM_ZDSTAFFCONTACT无增量数据！");
			return false;
		}

		final List<Bean> createList = new ArrayList<Bean>();
		final List<Bean> updateList = new ArrayList<Bean>();
		final List<Bean> deleteList = new ArrayList<Bean>();
		ServDao.findsCall(CommonImporter.HRM_ZDSTAFFCONTACT, param, new RowHandler() {
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
					deleteList.add(addDeleteData(data.getStr("PERSON_ID"), data.getStr("STRU_ID"), smtime));
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
		
		log.debug("=======impStaffContactData end====");
		return true;
	}

	/**
	 * 全量数据处理 1.清空数据表中的数据 2.添加数据到数据表中
	 * 
	 * @return true 导入数据成功 ； false 数据不存在
	 */
	public boolean impStaffContactFullData(String smtime) throws Exception {
		log.debug("=======impStaffContactFullData start====");
		// 清空数据
		ServDefBean servBean = ServUtils.getServDef(SERVID);
		Transaction.getExecutor().execute("TRUNCATE TABLE " + servBean.getTableAction());
		Transaction.commit();
		// 增加数据
		ParamBean param = new ParamBean();
		param.setWhere("and 1=1");
		int total = ServDao.count(CommonImporter.HRM_ZDSTAFFCONTACT, param);
		// 接口表数据为空，返回
		if (total == 0) {
			log.info("接口表HRM_ZDSTAFFCONTACT无全量数据！");
			return false;
		}
		for (int i = 1; i <= total / SAVE_COUNT + 1; i++) {
			ParamBean queryBean = new ParamBean();
			queryBean.set(Constant.PAGE_NOWPAGE, i); // 当前页数
			queryBean.set(Constant.PAGE_SHOWNUM, SAVE_COUNT); // 每页条数
			queryBean.setWhere("and 1=1");
			List<Bean> resultList = ServDao.finds(CommonImporter.HRM_ZDSTAFFCONTACT, queryBean);
			for (Bean data : resultList) {
				// 设置S_MTIME
				data.set("S_MTIME", smtime);
			}
			ServDao.creates(SERVID, resultList);
			Transaction.commit();
		}
		
		log.debug("=======impStaffContactFullData end====");
		return true;
	}

}
