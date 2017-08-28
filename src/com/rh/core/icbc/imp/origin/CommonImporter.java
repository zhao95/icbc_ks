package com.rh.core.icbc.imp.origin;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;

public class CommonImporter {
	
	private static  Logger log = Logger.getLogger(CommonImporter.class.getClass());
	/**
	 * 通讯录访问权限文件字符分隔符
	 */
	public static final String RIHGT_SPLIT = "\\|\\*\\|";
	/**
	 * 机构、人员文件字符分隔符
	 */
	public static final String STRU_STAFF_SPLIT = String.valueOf('\33');
	/**
	 * 机构、人员文件的编码格式
	 */
	public static final String STRU_STAFF_CODE = "GBK";
	/**
	 * 通讯录访问权限文件的字符编码格式
	 */
	public static final String RIGHT_CODE = "utf-8";
	
	public static final String ZERO = "0";
	
	public static final String ONE = "1";
	
	public static final String TWO = "2";
	
	public static final String BOM_ZDPSTRUINFO = "BOM_ZDPSTRUINFO";
	
	public static final String HRM_ZDSTAFFBINFO = "HRM_ZDSTAFFBINFO";
	
	public static final String HRM_ZDSTAFFSTRU = "HRM_ZDSTAFFSTRU";
	
	public static final String HRM_ZDSTAFFSTATE = "HRM_ZDSTAFFSTATE";
	
	public static final String HRM_ZDSTAFFNOTES = "HRM_ZDSTAFFNOTES";
	
	public static final String HRM_ZDSTAFFCONTACT = "HRM_ZDSTAFFCONTACT";
	
	public static final String CC_IMP_STATE = "CC_IMP_STATE";
	
	

	
	/**
	 * 通讯录访问权限的数据添加
	 * @param servId 服务ID
	 * @param file 数据文件
	 * @param items 字段项数组
	 */
	public static void addData(String servId, File file, String[] items) {
		try {
			Transaction.begin();
			LineIterator it = FileUtils.lineIterator(file, RIGHT_CODE);
			String line = it.nextLine();
			// 判断文件的utf-8的格式，并处理
			if (isUTF8WithBOM(line)) {
				byte[] lineData = line.getBytes(RIGHT_CODE);
				line = new String(lineData, 3, lineData.length - 3, RIGHT_CODE);
			}
			String[] data = line.split(RIHGT_SPLIT, -1);
			if (data.length < items.length) {
				log.error("【数据不完整！】" + line);
			} else {
				saveData(servId, items, data);
				Transaction.commit();
			}
			while (it.hasNext()) {
				line = it.nextLine();
				data = line.split(RIHGT_SPLIT, -1);
				if (data.length < items.length) {
					log.error("【数据不完整！】" + line);
					continue;
				}
				saveData(servId, items, data);
				Transaction.commit();
			}
		} catch (IOException e) {
			Transaction.rollback();
			e.printStackTrace();
			log.error(e.getMessage());
		}finally{
			Transaction.end();
		}
		
	}
	
	/**
	 * 对数据进行增加或更新操作
	 * @param servId  服务定义
	 * @param items  字段项
	 * @param data  字段值
	 */
	private static void saveData(String servId, String[] items, String[] data) {
		//判断数据是否存在，存在则修改数据，不存在则添加数据
		int count = ServDao.count(servId, new ParamBean().set(items[0], data[0].trim()));
		if(count ==0){
			ServDao.create(servId, addDatatoBean(data, items));
		}else{
			ServDao.update(servId, addDatatoBean(data, items));
		}
	}
	
	/**
	 * 将单条数据放入Bean中
	 * 数据Bean中增加"_WHERE_"字段，以主键及其值为内容
	 * @param data 单条数据数组
	 * @param items 数据项数组
	 * @return 数据Bean
	 */
	private static Bean addDatatoBean(String[] data, String[] items) {
		Bean dataBean = new Bean();
		int var;
		int itemsLen =items.length;
		for(var=0;var < itemsLen; var++){
			dataBean.set(items[var],data[var].trim());
		}
		dataBean.set(Constant.PARAM_WHERE, "and "+items[0]+" = "+data[0].trim());
		return dataBean;
	}
	
	/**
	 * 判断字符的编码格式 是为utf-8BOM还是utf-8；<br/>
	 * 是utf-8BOM返回true,不是返回false
	 * @param dataStr
	 * @return 布尔值
	 */
	public static boolean  isUTF8WithBOM(String dataStr){
		boolean result = false;
		byte[] Data;
		try {
			Data = dataStr.getBytes(RIGHT_CODE);
			//前三个字节为-17，-69，-65的文件为utf-8 DOM 文件
			if(Data[0]==-17 && Data[1]==-69 && Data[2] ==-65){
				result = true;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}	
		return result;
	}
	
	/**
	 * 判断数据文件是全量数据还是增量数据<br/>
	 * 如果文件数据的UPDATE_TIME字段值为FULLDATA则为全量数据 ，
	 * 否则为增量数据。<br/>
	 * 全量数据返回true<br/>
	 * 增量数据返回false;
	 */
	public static boolean isFullData(File file, int itemNo) {
		boolean result = false;
		LineIterator it;
		try {
			it = FileUtils.lineIterator(file, STRU_STAFF_CODE);
			try {
				String line = it.nextLine();
				// 根据updateTime的值判断是全量、增量数据
				String[] items = line.split(STRU_STAFF_SPLIT, -1);
				String updateTime = items[itemNo];
				if (updateTime.equalsIgnoreCase("FULLDATA")) {
					result = true;
				}
			} finally {
				LineIterator.closeQuietly(it);
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 删除数据,只是修改S_FLAG和S_MTIME字段<br/>
	 * 1.修改S_FLAG =2;
	 * 2.修改S_MTIME;
	 * @param servId 服务ID
	 * @param deleteList 删除数据List
	 */
	public static void deleteData(String servId, List<Bean> deleteList) {
		List<String> deleteFields = new ArrayList<String>();
		deleteFields.add("S_FLAG");
		deleteFields.add("S_MTIME");
		ServDao.updates(servId, deleteFields, deleteList);
	}
	
	/**
	 * 判断文件路径是否为空或不存在
	 * @param fileUrl 文件路径
	 * @return  路径不为空，返回true
	 */
	public static boolean isExistFile(String fileUrl){
		if(StringUtils.isEmpty(fileUrl)){ 
			log.error("【数据文件路径为空！】");
			return false;
		}
		File file = new File(fileUrl);
		if(!file.exists()){
			log.error("【数据文件不存在！】"+fileUrl);
			return false;
		}
		return true;
	}
	
	/**
	 * 更新状态表的状态位<br/>
	 * 1，准备导入；10，导入成功；2,导入中；20，导入失败
	 * @param state 状态
	 * @param time 时间
	 */
	public static void updateState(int state, String time){
		SqlBean sql = new SqlBean();
		sql.set("IMP_STATE", state);
		sql.and("IMP_DATE", time);
		Transaction.begin();
		ServDao.update(CC_IMP_STATE, sql);
		Transaction.commit();
		Transaction.end();
	}

}
