package com.rh.core.icbc.imp.origin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.base.Bean;
import com.rh.core.base.db.RowHandler;
import com.rh.core.base.db.Transaction;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;

public class ZDStaffStruImporter extends CommonServ {
	
	private static final String SERVID = "SY_HRM_ZDSTAFFSTRU";
	private static final int ITEM_COUNT = 19;
	private static final int SAVE_COUNT = 5000;

	/**
	 * StaffStru相关操作
	 * 导入数据
	 * @param paramBean
	 */
	public void impData(String fileUrl) {
		if(CommonImporter.isExistFile(fileUrl)){
			File file = new File(fileUrl);
			try {
				LineIterator it = FileUtils.lineIterator(file, CommonImporter.STRU_STAFF_CODE);
				HashMap<String, Boolean> DeleteMap = new HashMap<String, Boolean>();
				try {
					Transaction.begin();
					while (it.hasNext()) {
						String line = it.nextLine();
						String personId = line.substring(0, 10); // 每行数据前十个数是person_id					
						if (!DeleteMap.containsKey(personId)){ // 如果[已删除hasnMap]中没有PersonId，则执行以下操作。					
							deleteData(SERVID, personId); // 执行删除数据的操作。
							Transaction.commit();
							DeleteMap.put(personId, true); // 将personId和布尔值作为K-V存入hashMap。
						}
						AddData(SERVID, line); //无论hashMap中是否有，最后都执行插入数据操作。
						Transaction.commit();
					}
				} catch(Exception e){
					log.error(e.getMessage(),e);
					Transaction.rollback();
				}finally {
					Transaction.end();
					LineIterator.closeQuietly(it);
				}
			} catch (IOException e) {
				e.printStackTrace();
				log.error(e.getMessage(),e);
			}
		}
	}

	/**
	 * 删除操作
	 * 
	 * @param servId
	 *            服务编码
	 * @param person_id
	 *            人员id
	 */
	public void deleteData(String servId, String personId) {
		ServDao.deletes(servId,
				new ParamBean().setWhere(" AND PERSON_ID=" + personId));
	}

	/**
	 * 插入数据，写入adteBean中
	 * 
	 * @param line
	 *            每行数据
	 */
	public void AddData(String servId, String line) {
		String[] items = line.split(CommonImporter.STRU_STAFF_SPLIT, -1);
		if(items.length < ITEM_COUNT){
			log.error("【数据不全】"+line);
			return;
		}
		Bean dataBean = new Bean();
		dataBean.set("PERSON_ID", items[0]);
		dataBean.set("STRU_ID", items[1]);
		dataBean.set("STRU_NAME", items[2]);
		dataBean.set("WORK_STATE_CODE", items[3]);
		dataBean.set("WORK_STATE", items[4]);
		dataBean.set("STRU_FLAG", items[5]);
		dataBean.set("LV1_STRU_ID", items[6]);
		dataBean.set("LV1_STRU_NAME", items[7]);
		dataBean.set("LV2_STRU_ID", items[8]);
		dataBean.set("LV2_STRU_NAME", items[9]);
		dataBean.set("SUP_BR14_ID", items[10]);
		dataBean.set("SUP_BR14_NAME", items[11]);
		dataBean.set("UPDATE_FLAG", items[12]);
		dataBean.set("UPDATE_TIME", items[13]);
		dataBean.set("RESERVE1", items[14]);
		dataBean.set("RESERVE2", items[15]);
		dataBean.set("RESERVE3", items[16]);
		dataBean.set("RESERVE4", items[17]);
		dataBean.set("RESERVE5", items[18]);
		dataBean.set("S_MTIME", DateUtils.getDatetimeTS());
		ServDao.create(servId, dataBean);

	}
	
	/**
	 * HRM_ZDSTAFFSTRU ----> SY_HRM_ZDSTAFFSTRU 从增量表中将数据导入到中间表
	 */
	public void impStaffStruData(final String smtime) throws Exception {
		ParamBean param = new ParamBean();
		param.setWhere("and 1=1");
		int total = ServDao.count(CommonImporter.HRM_ZDSTAFFSTRU, param);
		//接口表数据为空，返回
		if(total == 0){
			log.info("接口表HRM_ZDSTAFFSTRU无增量数据！");
			return;
		}
		final List<Bean> addList = new ArrayList<Bean>();
		final HashMap<String, Boolean> DeleteMap = new HashMap<String, Boolean>();
		ServDao.findsCall(CommonImporter.HRM_ZDSTAFFSTRU, param, new RowHandler() {
			@Override
			public void handle(List<Bean> columns, Bean data) {

				data.set("S_MTIME", smtime);
				String personId = data.getStr("PERSON_ID");

				if (!DeleteMap.containsKey(personId)) { // 如果[已删除hasnMap]中没有PersonId，则执行以下操作。
					deleteData(SERVID, personId); // 执行删除数据的操作。
					Transaction.commit();
					DeleteMap.put(personId, true); // 将personId和布尔值作为K-V存入hashMap。
				}
				addList.add(data);
				if(addList.size() >= 3000){
					ServDao.creates(SERVID, addList);
					Transaction.commit();
					addList.clear();
				}
//				ServDao.create(SERVID, data); // 无论hashMap中是否有，最后都执行插入数据操作。
//				Transaction.commit();
			}
		});
	}
	
	public void impStaffStruFullData(String smtime) throws Exception {
		log.debug("=======impStaffStruFullData start====");
		// 清空数据
				ServDefBean servBean = ServUtils.getServDef(SERVID);
				Transaction.getExecutor().execute("TRUNCATE TABLE "+ servBean.getTableAction());
				Transaction.commit();
				// 增加数据
				ParamBean param = new ParamBean();
				param.setWhere("and 1=1");
				int total = ServDao.count(CommonImporter.HRM_ZDSTAFFSTRU, param);
				//接口表数据为空，返回
				if(total == 0){
					log.info("接口表HRM_ZDSTAFFSTRU无全量数据！");
					return;
				}
				for (int i = 1; i <= total / SAVE_COUNT + 1; i++) {
					ParamBean queryBean = new ParamBean();
					queryBean.set(Constant.PAGE_NOWPAGE, i); // 当前页数
					queryBean.set(Constant.PAGE_SHOWNUM, SAVE_COUNT); // 每页条数
					queryBean.setWhere("and 1=1");
					List<Bean> resultList = ServDao.finds(CommonImporter.HRM_ZDSTAFFSTRU, queryBean);
				    for(Bean data : resultList ){
		
						// 设置S_MTIME
						data.set("S_MTIME", smtime);
				    }
				    ServDao.creates(SERVID, resultList);
				    Transaction.commit();
				}
				
				log.debug("=======impStaffStruFullData end====");
	}
}
