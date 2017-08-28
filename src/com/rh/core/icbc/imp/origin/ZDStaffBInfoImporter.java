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
import com.rh.core.util.UserName2PinyinUtils;

/**
 * 员工基本信息导入 （SY_HRM_ZDSTAFFBINFO）
 * 
 * @author caoyiqing
 *
 */
public class ZDStaffBInfoImporter extends BaseServ {

	private static final String SERVID = "SY_HRM_ZDSTAFFBINFO";
	// 数据类型判断项下标
	private static final int UPDATE_TIME_NO = 17;
	// 数据字段总计数
	private static final int ITEM_COUNT = 34;
	// 数据处理类型下标
	private static final int UPDATE_FLAG_NO = 16;
	private static final int SAVE_COUNT = 5000;

	/**
	 * 将数据文件的数据导入SY_HRM_ZDSTAFFCONTACT表中<br/>
	 * 如果UPDATE_TIME = FULLDATA,则为全量数据；否则为增量数据。
	 * 
	 * @param fileUrl
	 *            文件路径
	 */
	public String imp(String fileUrl) {
		String result = "ERROR";
		if (CommonImporter.isExistFile(fileUrl)) {
			File file = new File(fileUrl);
			// 根据全量、增量标志，处理数据
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
					String updateFlag = items[UPDATE_FLAG_NO];
					// 根据updateflag将数据分到不同的List中
					if (updateFlag.equalsIgnoreCase(CommonImporter.ZERO)) {
						createList.add(addData(items));
					} else if (updateFlag.equalsIgnoreCase(CommonImporter.ONE)) {
						updateList.add(addData(items));
					} else if (updateFlag.equalsIgnoreCase(CommonImporter.TWO)) {
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
					ServDao.updates(SERVID, addFieldItem(), updateList);
					Transaction.commit();
				}
				// 最后删除数据,即置S_FLAG=2
				if (deleteList.size() > 0) {
					CommonImporter.deleteData(SERVID, deleteList);
					Transaction.commit();
				}
			} catch (Exception e) {
				Transaction.rollback();
				log.error(e.getMessage(), e);
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
	 * 全量数据处理 1.清空数据表中的数据 2.添加数据到数据表中
	 * 
	 * @param servId
	 * @param file
	 */
	private void doFullData(File file) {
		Transaction.begin();
		// 清空数据
		ServDefBean servBean = ServUtils.getServDef(SERVID);
		Transaction.getExecutor().execute("truncate table " + servBean.getTableAction());
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
				if (dataList.size() > 0) {
					ServDao.creates(SERVID, dataList);
				}
				Transaction.commit();
			} catch (Exception e) {
				Transaction.rollback();
				log.error(e.getMessage(), e);
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
	 * 将数据分割后，放入Bean中 增加和修改
	 */
	private Bean addData(String[] items) {
		Bean dataBean = new Bean();
		dataBean.set("PERSON_ID", items[0]).set("NAME", items[1]);
		dataBean.set("SEX", items[2]).set("BIRTHDAY", items[3]);
		dataBean.set("PERSON_TYPE_CODE", items[4]).set("PERSON_TYPE", items[5]);
		dataBean.set("POLITY_CODE", items[6]).set("POLITY", items[7]);
		dataBean.set("FOLK_CODE", items[8]).set("FOLK", items[9]);
		dataBean.set("WORING_DATE", items[10]).set("INTERRUPTED_MONTH", items[11]);
		dataBean.set("ECONOMY_DATE", items[12]).set("FINANCE_DATE", items[13]);
		dataBean.set("ICBC_DATE", items[14]).set("STRU_DATE", items[15]);
		dataBean.set("UPDATE_TIME", items[17]);
		dataBean.set("PRE_NAME", items[18]).set("NATIVE", items[19]);
		dataBean.set("BORN_PLACE", items[20]).set("HEALTH_CODE", items[21]);
		dataBean.set("HEALTH", items[22]).set("MARRIAGE_CODE", items[23]);
		dataBean.set("MARRIAGE", items[24]).set("NATIONALITY_CODE", items[25]);
		dataBean.set("NATIONALITY", items[26]).set("BLOOD_CODE", items[27]);
		dataBean.set("BLOOD", items[28]).set("S_MTIME", DateUtils.getDatetimeTS());
		dataBean.set("RESERVE1", items[29]).set("RESERVE2", items[30]);
		dataBean.set("RESERVE3", items[31]).set("RESERVE4", items[32]);
		dataBean.set("RESERVE5", items[33]).set("S_MTIME", DateUtils.getDatetimeTS());
		dataBean.set("ENAME", UserName2PinyinUtils.toPinyin(items[1]).toLowerCase());
		dataBean.set("SNAME", UserName2PinyinUtils.toPinyinHead(items[1]).toLowerCase());
		return dataBean;
	}

	/**
	 * 将数据分割后，放入Bean中
	 */
	private Bean addDeleteData(String value, String smtime) {
		Bean dataBean = new Bean();
		dataBean.set("PERSON_ID", value);
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
		fieldList.add("ENAME");
		fieldList.add("SNAME");
		fieldList.add("SEX");
		fieldList.add("BIRTHDAY");
		fieldList.add("PERSON_TYPE_CODE");
		fieldList.add("PERSON_TYPE");
		fieldList.add("POLITY_CODE");
		fieldList.add("POLITY");
		fieldList.add("FOLK_CODE");
		fieldList.add("FOLK");
		fieldList.add("WORING_DATE");
		fieldList.add("INTERRUPTED_MONTH");
		fieldList.add("ECONOMY_DATE");
		fieldList.add("FINANCE_DATE");
		fieldList.add("ICBC_DATE");
		fieldList.add("STRU_DATE");
		fieldList.add("UPDATE_TIME");
		fieldList.add("PRE_NAME");
		fieldList.add("NATIVE");
		fieldList.add("BORN_PLACE");
		fieldList.add("HEALTH_CODE");
		fieldList.add("HEALTH");
		fieldList.add("MARRIAGE_CODE");
		fieldList.add("MARRIAGE");
		fieldList.add("NATIONALITY_CODE");
		fieldList.add("NATIONALITY");
		fieldList.add("BLOOD_CODE");
		fieldList.add("BLOOD");
		fieldList.add("RESERVE1");
		fieldList.add("RESERVE2");
		fieldList.add("RESERVE3");
		fieldList.add("RESERVE4");
		fieldList.add("RESERVE5");
		fieldList.add("S_MTIME");
		return fieldList;
	}

	/**
	 * HRM_ZDSTAFFBINFO ----> SY_HRM_ZDSTAFFBINFO 从增量表中将增量数据导入到中间表
	 * 
	 * @return true:导入成功 ； false：接口表为空
	 */
	public boolean impStaffBInfoData(final String smtime) throws Exception {
		log.debug("=======impStaffBInfoData start====");
		ParamBean param = new ParamBean();
		param.setWhere("and 1=1");
		int total = ServDao.count(CommonImporter.HRM_ZDSTAFFBINFO, param);

		// 接口表数据为空，返回
		if (total == 0) {
			log.info("接口表HRM_ZDSTAFFBINFO无增量数据！");
			return false;
		}

		final List<Bean> createList = new ArrayList<Bean>();
		final List<Bean> updateList = new ArrayList<Bean>();
		final List<Bean> deleteList = new ArrayList<Bean>();
		ServDao.findsCall(CommonImporter.HRM_ZDSTAFFBINFO, param, new RowHandler() {
			@Override
			public void handle(List<Bean> columns, Bean data) {
				data.set("ENAME", UserName2PinyinUtils.toPinyin(data.getStr("NAME").trim()).toLowerCase());
				data.set("SNAME", UserName2PinyinUtils.toPinyinHead(data.getStr("NAME").trim()).toLowerCase());
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
			ServDao.updates(SERVID, addFieldItem(), updateList);
		}
		if (deleteList.size() > 0) {
			CommonImporter.deleteData(SERVID, deleteList);
		}
		Transaction.commit();
		
		log.debug("=======impStaffBInfoData end====");
		return true;
	}

	/**
	 * 全量数据处理 1.清空数据表中的数据 2.添加数据到数据表中
	 * 
	 * @return true:导入成功 ； false：接口表为空
	 */
	public boolean impStaffBInfoFullData(String smtime) throws Exception {
		log.debug("=======impStaffBInfoFullData start====");
//		Transaction.begin();
		// 清空数据
		ServDefBean servBean = ServUtils.getServDef(SERVID);
		Transaction.getExecutor().execute("truncate table " + servBean.getTableAction());
		Transaction.commit();
		// 增加数据

		ParamBean param = new ParamBean();
		param.setWhere("and 1=1");
		int total = ServDao.count(CommonImporter.HRM_ZDSTAFFBINFO, param);
		// 接口表数据为空，返回
		if (total == 0) {
			log.info("接口表HRM_ZDSTAFFBINFO无全量数据！");
			return false;
		}
		for (int i = 1; i <= total / SAVE_COUNT + 1; i++) {
			ParamBean queryBean = new ParamBean();
			queryBean.set(Constant.PAGE_NOWPAGE, i); // 当前页数
			queryBean.set(Constant.PAGE_SHOWNUM, SAVE_COUNT); // 每页条数
			queryBean.setWhere("and 1=1");
			List<Bean> resultList = ServDao.finds(CommonImporter.HRM_ZDSTAFFBINFO, queryBean);
			for (Bean data : resultList) {

				data.set("ENAME", UserName2PinyinUtils.toPinyin(data.getStr("NAME").trim()).toLowerCase());
				data.set("SNAME", UserName2PinyinUtils.toPinyinHead(data.getStr("NAME").trim()).toLowerCase());
				// 设置S_MTIME
				data.set("S_MTIME", smtime);
			}
			ServDao.creates(SERVID, resultList);
			Transaction.commit();
		}
		
		log.debug("=======impStaffBInfoFullData end====");
		return true;
	}

}
