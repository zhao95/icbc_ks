package com.rh.core.icbc.imp.origin;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.base.BaseServ;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;

/**
 * 导入机构信息数据（SY_BOM_ZDPSTRUINFO表）
 * 
 * 全量数据导入为impFullData 增量数据导入为impIncrementalData
 * 
 * @author caoyiqing
 *
 */
public class ZDPStruInfoImporter extends BaseServ {
	// 服务定义
	private static final String SERVID = "SY_BOM_ZDPSTRUINFO";
	// 单条数据的字节长度
	private static final int DATA_CHAR_LENGTH = 2371;
	// 机构撤消状态值
	private static final String STATE_CANCLE = "3";
	private static final int SAVE_COUNT = 5000;
	// 中间表的数据字段和字段长度
	private static ArrayList<Bean> itemDefs = new ArrayList<Bean>();
	
	private static List<String> fields = new ArrayList<String>();
	
	/**
	 * 初始化itemBefs
	 */
	public ZDPStruInfoImporter() {
		final String[] items = { "STRU_ID&10", "STRU_FNAME&80", "STRU_SNAME&80", "OLDSYS_STRUID&15", "FLICENCE_ID&40",
				"STRU_ADDR&120", "ZIPCODE&20", "PHONE&40", "STRU_SIGN&3", "STRU_LV&3", "ADMIN_LV&3", "SUP_STRU&10",
				"SETUP_TIME&6", "LST_ALT_TYPE&3", "LST_ALT_TIME&6", "STRU_STATE&3", "BLICENCE_ID&36", "REVOKE_TIME&6",
				"DIST_SIGN&3", "STRU_GRADE&3", "CODECERT_ID&36", "TOWN_FLAG&3", "BUSI_AREA&9", "BUSI_SITE_USE&3",
				"FEXCHANGE_FLAG&3", "MAN_GRADE&3", "CHARGE_PROP&3", "PROFESSION_LEVEL&3", "NODE_TYPE&3", "ECON_AREA&3",
				"IS_HUN_CITY&3", "IS_HUN_COUNTY&3", "COUNTRY&100", "VILLAGE&40", "NP_OPER_TYPE&3", "MANAGE_STRU_ID&10",
				"SPECIALTY_PROP&80", "FINANCE_STRUID&14", "PBANK_STRUID&14", "ADMIN_CODE&6", "ADMIN_VALUE&100",
				"LST_OPTIMIZE_TYPE&3", "OPTIMIZE_PLAN&4", "BUSI_TYPE&3", "MEMO&100", "IS_NEW_BUSI_NODE&1",
				"CREATE_DATE&8", "STRU_FOREIGN_FNAME&80", "STRU_FOREIGN_SNAME&80", "STRU_CHN_FNAME&80",
				"STRU_CHN_SNAME&80", "BACK1&100", "BACK2&100", "BACK3&100", "BACK4&100", "BACK5&100", "BACK6&100",
				"BACK7&100", "BACK8&100", "BACK9&100", "BACK10&100" };

		for (String item : items) {
			Bean bean = new Bean();
			String[] defs = item.split("&");
			bean.set("ITEM_NAME", defs[0]);
			bean.set("ITEM_LENGTH", Integer.valueOf(defs[1]));
			itemDefs.add(bean);
			fields.add(defs[0]);
		}
		fields.remove(0);
		fields.add("S_MTIME");
		fields.add("S_FLAG");
	}

	/**
	 * 导入增量数据<br/>
	 * 1.数据若存在，根据STRU_STATE的值更新数据；<br/>
	 * 2.数据不存在，根据STRU_STATE的值添加数据；<br/>
	 * STRU_STATE=3,设S_FLAG=2； STRU_STATE!=3,设S_FLAG=1；
	 * 
	 * @param fileUrl
	 *            数据文件路径
	 */
	public String impIncrementalData(String fileUrl) {
		String result = "ERROR";
		if (CommonImporter.isExistFile(fileUrl)) {
			File file = new File(fileUrl);
			LineIterator it;
			try {
				it = FileUtils.lineIterator(file, CommonImporter.STRU_STAFF_CODE);
				try {
					Transaction.begin();
					while (it.hasNext()) {
						String line = it.nextLine();
						String struId = line.substring(0, 10);
						int count = ServDao.count(SERVID, new ParamBean().set("STRU_ID", struId));
						// 表中无此条数据则添加数据
						if (count == 0) {
							ServDao.create(SERVID, addDataToBean(line));
						} else {
							// 更新操作
							Bean dataBean = addDataToBean(line);
							dataBean.set(Constant.PARAM_WHERE, "and STRU_ID =" + struId);
							ServDao.update(SERVID, dataBean);
						}
						Transaction.commit();
					}
					result = "INCREMENT";
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
		return result;
	}

	/**
	 * 全量数据处理<br/>
	 * 
	 * 1.清空数据表中的数据 2.添加数据到数据表中
	 * 
	 * @param fileUrl
	 *            数据文件路径
	 */
	public String impFullData(String fileUrl) {
		String result = "ERROR";
		if (CommonImporter.isExistFile(fileUrl)) {
			File file = new File(fileUrl);
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
						Bean dataBean = addDataToBean(line);
						if (dataBean == null) {
							continue;
						}
						dataList.add(dataBean);
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
					result = "FULLDATA";
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
		return result;
	}

	/**
	 * 数据截取后，放入dataBean中
	 * 
	 * @param line
	 *            数据
	 * @return 数据Bean
	 */
	private Bean addDataToBean(String line) {
		Bean dataBean = new Bean();
		byte[] data;
		try {
			data = line.getBytes(CommonImporter.STRU_STAFF_CODE);
			if (data.length != DATA_CHAR_LENGTH) {
				log.error("【数据不全！】" + line);
				return null;
			}
			// 设置S_FLAG
			dataBean.set("S_FLAG", Constant.YES_INT);
			int start = 0;
			for (Bean itemDef : itemDefs) {
				int itemLen = itemDef.getInt("ITEM_LENGTH");
				String itemName = itemDef.getStr("ITEM_NAME");
				byte[] temp = new byte[itemLen];
				System.arraycopy(data, start, temp, 0, itemLen);
				String value = new String(temp, CommonImporter.STRU_STAFF_CODE).trim();
				// 如果是撤销机构，则将S_FLAG设为2,不是则设为1
				if (itemName.equalsIgnoreCase("STRU_STATE") && value.equalsIgnoreCase(STATE_CANCLE)) {
					dataBean.set("S_FLAG", Constant.NO_INT);
				}
				dataBean.set(itemDef.getStr("ITEM_NAME"), value);
				start += itemLen;
			}
			dataBean.set("S_MTIME", DateUtils.getDatetimeTS());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return dataBean;
	}

	/**
	 * BOM_ZDPSTRUINFO ----> SY_BOM_ZDPSTRUINFO 从增量表中将增量数据导入到中间表
	 * 
	 * @return true 导入数据成功 ； false 数据不存在
	 */
	public boolean impStruInfoData(String smtime) throws Exception {
		//存放部门code
		HashSet<String> dept = new HashSet<String>();
		ParamBean param = new ParamBean();
		param.setWhere("and 1=1");
		int total = ServDao.count(CommonImporter.BOM_ZDPSTRUINFO, param);
		// 接口表数据为空，返回
		if (total == 0) {
			log.info("接口表BOM_ZDPSTRUINFO无增量数据！");
			return false;
		}
		for (int i = 1; i <= total / SAVE_COUNT + 1; i++) {
			ParamBean queryBean = new ParamBean();
			queryBean.set(Constant.PAGE_NOWPAGE, i); // 当前页数
			queryBean.set(Constant.PAGE_SHOWNUM, SAVE_COUNT); // 每页条数
			queryBean.setWhere("and 1=1");
			List<Bean> resultList = ServDao.finds(CommonImporter.BOM_ZDPSTRUINFO, queryBean);
			saveStruInfo(resultList, dept, smtime);
		}
		return true;
	}

	/**
	 * 保持数据到中间表
	 * 
	 * @param data
	 *            数据Bean
	 */
	private void saveStruInfo(List<Bean> resultList, HashSet<String> dept, String smtime) {
		List<Bean> updateList = new ArrayList<Bean>();
		List<Bean> addList = new ArrayList<Bean>();
		for (Bean data : resultList) {
			// 如果是撤销机构，则将S_FLAG设为2,不是则设为1
			String value = data.getStr("STRU_STATE").trim();
			if (value.equalsIgnoreCase(STATE_CANCLE)) {
				data.set("S_FLAG", Constant.NO_INT);
			} else {
				data.set("S_FLAG", Constant.YES_INT);
			}
			// 设置S_MTIME
			data.set("S_MTIME", smtime);
			// 数据如果存在则更新数据，数据不存在则添加数据
			String struId = data.getStr("STRU_ID");
			if(dept.contains(struId)){
				updateList.add(data);
			}else{
				dept.add(struId);
				int count = ServDao.count(SERVID, new ParamBean().set("STRU_ID", struId));
				if (count > 0) {
					updateList.add(data);
//					data.set(Constant.PARAM_WHERE, "and STRU_ID =" + data.getStr("STRU_ID").trim());
//					ServDao.update(SERVID, data);
//					Transaction.commit();
				} else {
					addList.add(data);
//					ServDao.create(SERVID, data);
//					Transaction.commit();
				}
			}	
		}
		ServDao.creates(SERVID, addList);

		ServDao.updates(SERVID, fields, updateList);
		Transaction.commit();
	}

	/**
	 * 部门全量数据导入中间表
	 * 
	 * @return true 导入数据成功 ； false 数据不存在
	 * @throws Exception
	 */
	public boolean impStruInfoFullData(String smtime) throws Exception {

		// 清空数据
		ServDefBean servBean = ServUtils.getServDef(SERVID);
		Transaction.getExecutor().execute("TRUNCATE TABLE " + servBean.getTableAction());
		Transaction.commit();

		ParamBean param = new ParamBean();
		param.setWhere("and 1=1");
		int total = ServDao.count(CommonImporter.BOM_ZDPSTRUINFO, param);
		// 接口表数据为空，返回
		if (total == 0) {
			log.info("接口表BOM_ZDPSTRUINFO无全量数据！");
			return false;
		}
		for (int i = 1; i <= total / SAVE_COUNT + 1; i++) {
			ParamBean queryBean = new ParamBean();
			queryBean.set(Constant.PAGE_NOWPAGE, i); // 当前页数
			queryBean.set(Constant.PAGE_SHOWNUM, SAVE_COUNT); // 每页条数
			queryBean.setWhere("and 1=1");
			List<Bean> resultList = ServDao.finds(CommonImporter.BOM_ZDPSTRUINFO, queryBean);
			for (Bean data : resultList) {

				// 如果是撤销机构，则将S_FLAG设为2,不是则设为1
				String value = data.getStr("STRU_STATE").trim();
				if (value.equalsIgnoreCase(STATE_CANCLE)) {
					data.set("S_FLAG", Constant.NO_INT);
				} else {
					data.set("S_FLAG", Constant.YES_INT);
				}
				// 设置S_MTIME
				data.set("S_MTIME", smtime);
			}
			ServDao.creates(SERVID, resultList);
			Transaction.commit();
		}
		return true;
	}

}
