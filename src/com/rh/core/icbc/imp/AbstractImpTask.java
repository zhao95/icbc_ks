package com.rh.core.icbc.imp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.base.db.RowHandler;
import com.rh.core.base.db.Transaction;
import com.rh.core.icbc.imp.log.OrgLogMgr;
import com.rh.core.icbc.imp.target.NImpDept;
import com.rh.core.icbc.imp.target.NImpUser;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;
import com.rh.core.util.UserName2PinyinUtils;
import com.rh.core.util.threadpool.RhThreadTask;

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class AbstractImpTask extends RhThreadTask {

	private static final long serialVersionUID = 1L;
	
	private static final String CC_IMP_STATE = "SY_IMP_STATE";

	protected static final int SAVE_COUNT = 5000;

	private static final String STATE_CANCLE = "3";

	/**
	 * 获取导入时间
	 * 
	 * @return
	 */
	protected String getImpDate() {
		log.info("----------------- getImpDate ------------------");
		String smtime = "";
		SqlBean sql = new SqlBean();
		sql.desc("IMP_DATE");
		Bean result = ServDao.find("CC_IMP_STATE", sql);
		smtime = result.getStr("IMP_DATE");
		log.info("-----------------IMP_DATE : " + smtime + "-----------------------");
		return smtime;
	}

	protected void setStateIng(String smtime) {
		log.info("--------------- setStateIng -----------------");
		updateState(2, smtime);
	}

	protected void setStateOk(String smtime) {
		log.info("--------------- setStateOk -----------------");
		updateState(10, smtime);
	}

	protected void setStateError(String smtime) {
		log.info("--------------- setStateError -----------------");
		updateState(20, smtime);
	}
	
	/**
	 * 更新状态表的状态位
	 * 1，准备导入；10，导入成功；2,导入中；20，导入失败
	 * @param state - 状态
	 * @param smtime - 时间
	 */
	private void updateState(int state, String smtime) {
		SqlBean sql = new SqlBean();
		sql.set("IMP_STATE", state);
		sql.and("IMP_DATE", smtime);
		Transaction.begin();
		ServDao.update(CC_IMP_STATE, sql);
		Transaction.commit();
		Transaction.end();
	}
	
	protected void impBomStruData(final String smtime ){
		log.info("------------------ import SY_BOM_ZDPSTRUINFO increment data begin ------------------");

		// 存放部门code
		HashSet<String> dept = new HashSet<String>();
		int total = ServDao.count("BOM_ZDPSTRUINFO", new ParamBean().setWhere(" AND 1=1"));
		for (int i = 1; i <= total / SAVE_COUNT + 1; i++) {
			ParamBean queryBean = new ParamBean();
			queryBean.setWhere(" AND 1=1");
			queryBean.setShowNum(SAVE_COUNT);
			queryBean.setNowPage(i);
			List<Bean> resultList = ServDao.finds("BOM_ZDPSTRUINFO", queryBean);
			List<Bean> updateList = new ArrayList<Bean>();
			List<Bean> addList = new ArrayList<Bean>();

			for (Bean data : resultList) {
				// 如果是撤消机构,则将S_FLAG设置为2,不是则设置为1
				String value = data.getStr("STRU_STATE").trim();
				if (value.equals("3")) { //
					data.set("S_FLAG", Constant.NO_INT);
				} else {
					data.set("S_FLAG", Constant.YES_INT);
				}

				// 设置S_MTIME
				data.set("S_MTIME", smtime);
				// 数据如果存在则更新数据,数据不存在则添加数据
				String struId = data.getStr("STRU_ID");
				if (dept.contains(struId)) {
					updateList.add(data);
					log.warn("----------机构增量数据中有多条数据相同ID数据,struId : " + struId + "-----------");
				} else {
					dept.add(struId);
					int count = ServDao.count("SY_BOM_ZDPSTRUINFO", new ParamBean().set("STRU_ID", struId));
					if (count > 0) {
						updateList.add(data);
					} else {
						addList.add(data);
					}
				}
			}
			// 添加新数据
			if (addList.size() > 0) {
				ServDao.creates("SY_BOM_ZDPSTRUINFO", addList);
			}
			// 修改老数据
			if (updateList.size() > 0) {
				List<String> updateFieldList = new ArrayList(updateList.get(0).keySet());
				// 可能有多余字段需要remove
				ServDao.updates("SY_BOM_ZDPSTRUINFO", updateFieldList, updateList);
			} 
		}
		log.info("------------------ import SY_BOM_ZDPSTRUINFO increment data end ------------------");
	}
	
	/*
	 * step2.增量同步SY_HRM_ZDSTAFFBINFO数据
	 */
	protected void impStaffBinfo(final String smtime){
		log.info("------------------ import SY_HRM_ZDSTAFFBINFO increment data begin ------------------");
		final HashSet<String> addCodes = new HashSet<String>();
		int total = ServDao.count("HRM_ZDSTAFFBINFO", new ParamBean().setWhere(" AND 1=1"));
		for (int i = 1; i <= total / SAVE_COUNT + 1; i++) {
			final List<Bean> createList = new ArrayList<Bean>();
			final List<Bean> updateList = new ArrayList<Bean>();
			ParamBean param = new ParamBean();
			param.setWhere(" AND 1=1");
			param.setShowNum(SAVE_COUNT);
			param.setNowPage(i);
			param.setOrder("UPDATE_FLAG"); // 保证更新的顺序是：新增，修改，删除
			ServDao.findsCall("HRM_ZDSTAFFBINFO", param, new RowHandler() {

				@Override
				public void handle(List<Bean> columns, Bean data) {
					String pkCode = data.getStr("PERSON_ID");
					data.set("ENAME",
							UserName2PinyinUtils.toPinyin(data.getStr("NAME").trim()).toLowerCase());
					data.set("SNAME",
							UserName2PinyinUtils.toPinyinHead(data.getStr("NAME").trim()).toLowerCase());
					data.set("S_MTIME", smtime);
					// 获取更新标志
					String updateFlag = data.getStr("UPDATE_FLAG");
					// 根据updateFlag将数据整理为删除和非删除
					if (updateFlag.equals("0") || updateFlag.equals("1")) {
						// 暂时不做处理
					} else if (updateFlag.equals("2")) {
						data.set("S_FLAG", Constant.NO_INT);
					} else {
						throw new TipException(
								"【数据更新标志未知】！data : " + data + ", updateFlag : " + updateFlag);
					}
					// 根据实际是否存在，将数据放入新增或修改集合中
					SqlBean existSql = new SqlBean();
					existSql.and("PERSON_ID", pkCode);
					int existCount = ServDao.count("SY_HRM_ZDSTAFFBINFO", existSql);
					if (existCount == 0) {
						// 不存在
						if (addCodes.contains(pkCode)) {
							// 同一批数据中已经有新增的了，第二条记录处理成更新的
							updateList.add(data);
						} else {
							// 同一批数据中没有新增的，加入新增集合并记录到addCodes中
							createList.add(data);
							addCodes.add(pkCode);
						}
					} else {
						// 存在，修改
						updateList.add(data);
					}
				}
			});
			if (createList.size() > 0) {
				ServDao.creates("SY_HRM_ZDSTAFFBINFO", createList);
			}
			if (updateList.size() > 0) {
				List<String> updateFieldList = new ArrayList(updateList.get(0).keySet());
				//  可能有多余字段需要remove
				ServDao.updates("SY_HRM_ZDSTAFFBINFO", updateFieldList, updateList);
			}
		}
		log.info("------------------ import SY_HRM_ZDSTAFFBINFO increment data end ------------------");
	}

	/*
	 * step3.增量同步SY_HRM_ZDSTAFFCONTACT数据
	 */
	protected void impStaffContact(final String smtime){
		log.info("------------------ import SY_HRM_ZDSTAFFCONTACT increment data begin ------------------");
		final HashSet<String> addCodes = new HashSet<String>();
		int total = ServDao.count("HRM_ZDSTAFFCONTACT", new ParamBean().setWhere(" AND 1=1"));
		for (int i = 1; i <= total / SAVE_COUNT + 1; i++) {
			final List<Bean> createList = new ArrayList<Bean>();
			final List<Bean> updateList = new ArrayList<Bean>();
			ParamBean param = new ParamBean();
			param.setWhere(" AND 1=1");
			param.setShowNum(SAVE_COUNT);
			param.setNowPage(i);
			param.setOrder("UPDATE_FLAG");
			ServDao.findsCall("HRM_ZDSTAFFCONTACT", param, new RowHandler() {

				@Override
				public void handle(List<Bean> columns, Bean data) {
					String[] pkCodes = { data.getStr("PERSON_ID"), data.getStr("STRU_ID") }; // 主键
					data.set("S_MTIME", smtime);
					// 更新标志
					String updateFlag = data.getStr("UPDATE_FLAG");
					// 根据updateFlag将数据整理为删除和非删除
					if (updateFlag.equals("0") || updateFlag.equals("1")) {
						// 暂时不做处理
					} else if (updateFlag.equals("2")) {
						data.set("S_FLAG", Constant.NO_INT);
					} else {
						throw new TipException(
								"【数据更新标志未知】！data : " + data + ", updateFlag : " + updateFlag);
					}

					// 根据实际是否存在，将数据放入新增或修改集合中
					SqlBean existSql = new SqlBean();
					existSql.and("PERSON_ID", pkCodes[0]);
					existSql.and("STRU_ID", pkCodes[1]);
					int existCount = ServDao.count("SY_HRM_ZDSTAFFCONTACT", existSql);
					if (existCount == 0) {
						// 不存在
						if (addCodes.contains(StringUtils.join(pkCodes))) {
							// 同一批数据中已经有新增的了，第二条记录处理成更新的
							updateList.add(data);
						} else {
							// 同一批数据中没有新增的，加入新增集合并记录到addCodes中
							createList.add(data);
							addCodes.add(StringUtils.join(pkCodes));
						}
					} else {
						// 存在，修改
						updateList.add(data);
					}
				}
			});
			if (createList.size() > 0) {
				ServDao.creates("SY_HRM_ZDSTAFFCONTACT", createList);
			}
			if (updateList.size() > 0) {
				List<String> updateFieldList = new ArrayList(updateList.get(0).keySet());
				// 可能有多余字段需要remove
				ServDao.updates("SY_HRM_ZDSTAFFCONTACT", updateFieldList, updateList);
			}
		}
		log.info("------------------ import SY_HRM_ZDSTAFFCONTACT increment data end ------------------");
	}

	/*
	 * step4.增量同步SY_HRM_ZDSTAFFNOTES数据
	 */
	protected void impStaffNotes(final String smtime){
		log.info("------------------ import SY_HRM_ZDSTAFFNOTES increment data begin ------------------");
		final HashSet<String> addCodes = new HashSet<String>();
		int total = ServDao.count("HRM_ZDSTAFFNOTES", new ParamBean().setWhere(" AND 1=1"));
		for (int i = 1; i <= total / SAVE_COUNT + 1; i++) {
			final List<Bean> createList = new ArrayList<Bean>();
			final List<Bean> updateList = new ArrayList<Bean>();
			ParamBean param = new ParamBean();
			param.setWhere(" AND 1=1");
			param.setShowNum(SAVE_COUNT);
			param.setNowPage(i);
			param.setOrder("UPDATE_FLAG");
			ServDao.findsCall("HRM_ZDSTAFFNOTES", param, new RowHandler() {

				@Override
				public void handle(List<Bean> columns, Bean data) {
					String[] pkCodes = { data.getStr("SSIC_ID"), data.getStr("NOTES") };

					data.set("S_MTIME", smtime);

					String updateFlag = data.getStr("UPDATE_FLAG");
					// 根据updateFlag将数据整理为删除和非删除
					if (updateFlag.equals("0") || updateFlag.equals("1")) {
						// 暂时不做处理
					} else if (updateFlag.equals("2")) {
						data.set("S_FLAG", Constant.NO_INT);
					} else {
						throw new TipException(
								"【数据更新标志未知】！data : " + data + ", updateFlag : " + updateFlag);
					}
					// 根据实际是否存在，将数据放入新增或修改集合中
					SqlBean existSql = new SqlBean();
					existSql.and("SSIC_ID", pkCodes[0]);
					existSql.and("NOTES", pkCodes[1]);
					int existCount = ServDao.count("SY_HRM_ZDSTAFFNOTES", existSql);
					if (existCount == 0) {
						// 不存在
						if (addCodes.contains(StringUtils.join(pkCodes))) {
							// 同一批数据中已经有新增的了，第二条记录处理成更新的
							updateList.add(data);
						} else {
							// 同一批数据中没有新增的，加入新增集合并记录到addCodes中
							createList.add(data);
							addCodes.add(StringUtils.join(pkCodes));
						}
					} else {
						// 存在，修改
						updateList.add(data);
					}
				}
			});
			if (createList.size() > 0) {
				ServDao.creates("SY_HRM_ZDSTAFFNOTES", createList);
			}
			if (updateList.size() > 0) {
				List<String> updateFieldList = new ArrayList(updateList.get(0).keySet());
				// 可能有多余字段需要remove
				ServDao.updates("SY_HRM_ZDSTAFFNOTES", updateFieldList, updateList);
			}
		}
		log.info("------------------ import SY_HRM_ZDSTAFFNOTES increment data end ------------------");
	}

	/*
	 * step5.增量同步SY_HRM_ZDSTAFFSTATE数据
	 */
	protected void impStaffState(final String smtime){
		log.info("------------------ import SY_HRM_ZDSTAFFSTATE increment data begin ------------------");
		final HashSet<String> addCodes = new HashSet<String>();
		int total = ServDao.count("HRM_ZDSTAFFSTATE", new ParamBean().setWhere(" AND 1=1"));
		for (int i = 1; i <= total / SAVE_COUNT + 1; i++) {
			final List<Bean> createList = new ArrayList<Bean>();
			final List<Bean> updateList = new ArrayList<Bean>();
			ParamBean param = new ParamBean();
			param.setWhere(" AND 1=1");
			param.setShowNum(SAVE_COUNT);
			param.setNowPage(i);
			param.setOrder("UPDATE_FLAG");
			ServDao.findsCall("HRM_ZDSTAFFSTATE", param, new RowHandler() {

				@Override
				public void handle(List<Bean> columns, Bean data) {
					String pkCode = data.getStr("PERSON_ID");

					data.set("S_MTIME", smtime);
					String updateFlag = data.getStr("UPDATE_FLAG").trim();
					// 根据updateFlag将数据整理为删除和非删除
					if (updateFlag.equals("0") || updateFlag.equals("1")) {
						// 暂时不做处理
					} else if (updateFlag.equals("2")) {
						data.set("S_FLAG", Constant.NO_INT);
					} else {
						throw new TipException(
								"【数据更新标志未知】！data : " + data + ", updateFlag : " + updateFlag);
					}
					// 根据实际是否存在，将数据放入新增或修改集合中
					SqlBean existSql = new SqlBean();
					existSql.and("PERSON_ID", pkCode);
					int existCount = ServDao.count("SY_HRM_ZDSTAFFSTATE", existSql);
					if (existCount == 0) {
						// 不存在
						if (addCodes.contains(pkCode)) {
							// 同一批数据中已经有新增的了，第二条记录处理成更新的
							updateList.add(data);
						} else {
							// 同一批数据中没有新增的，加入新增集合并记录到addCodes中
							createList.add(data);
							addCodes.add(pkCode);
						}
					} else {
						// 存在，修改
						updateList.add(data);
					}
				}
			});
			if (createList.size() > 0) {
				ServDao.creates("SY_HRM_ZDSTAFFSTATE", createList);
			}
			if (updateList.size() > 0) {
				List<String> updateFieldList = new ArrayList(updateList.get(0).keySet());
				// 可能有多余字段需要remove
				ServDao.updates("SY_HRM_ZDSTAFFSTATE", updateFieldList, updateList);
			}
		}
		log.info("------------------ import SY_HRM_ZDSTAFFSTATE increment data end ------------------");
	}

	/*
	 * step6.增量同步SY_HRM_ZDSTAFFSTRU数据
	 */
	protected void impStaffStru(final String smtime){
		log.info("------------------ import SY_HRM_ZDSTAFFSTRU increment data begin ------------------");
		final HashMap<String, Boolean> deleteMap = new HashMap<String, Boolean>();
		int total = ServDao.count("HRM_ZDSTAFFSTRU", new ParamBean().setWhere(" AND 1=1"));
		for (int i = 1; i <= total / SAVE_COUNT + 1; i++) {
			final List<Bean> addList = new ArrayList<Bean>();
			ParamBean param = new ParamBean();
			param.setWhere(" AND 1=1");
			param.setShowNum(SAVE_COUNT);
			param.setNowPage(i);
			ServDao.findsCall("HRM_ZDSTAFFSTRU", param, new RowHandler() {

				@Override
				public void handle(List<Bean> columns, Bean data) {
					data.set("S_MTIME", smtime);
					String personId = data.getStr("PERSON_ID");

					// 如果[已删除hashMap]中没有PersonId,则执行删除操作,并将PersonId加入map中
					if (!deleteMap.containsKey(personId)) {
						ServDao.deletes("SY_HRM_ZDSTAFFSTRU",
								new ParamBean().setWhere(" AND PERSON_ID = '" + personId + "'"));
						deleteMap.put(personId, true);
					}
					// 添加如增加集合中
					addList.add(data);
				}
			});
			// 批量添加入数据库
			ServDao.creates("SY_HRM_ZDSTAFFSTRU", addList);
		}
		log.info("------------------ import SY_HRM_ZDSTAFFSTRU increment data end ------------------");
	}

	/*
	 * 增量同步SY_HRM_ZDSTAFFFAMILY
	 */
	protected void impStffFamily(final String smtime){
		log.info("------------------- import SY_HRM_ZDSTAFFFAMILY increment data begin ---------------");
		final Set<String> addCodes = new HashSet<String>();
		int total = ServDao.count("HRM_ZDSTAFFFAMILY", new ParamBean().setWhere(" AND 1=1 "));
		for (int i = 0; i < total / SAVE_COUNT + 1; i++) {
			final List<Bean> createList = new ArrayList<Bean>();
			final List<Bean> updateList = new ArrayList<Bean>();
			ParamBean param = new ParamBean();
			param.setWhere(" AND 1=1 ");
			param.setShowNum(SAVE_COUNT);
			param.setNowPage(i);
			param.setOrder("UPDATE_FLAG");
			ServDao.findsCall("HRM_ZDSTAFFFAMILY", param, new RowHandler() {

				@Override
				public void handle(List<Bean> columns, Bean data) {
					String pkCode = data.getStr("PERSON_ID");

					data.set("S_MTIME", smtime);
					String updateFlag = data.getStr("UPDATE_FLAG").trim();
					// 根据updateFlag将数据整理为删除和非删除
					if ("0".equals(updateFlag) || "1".equals(updateFlag)) {
						// 暂无处理
					} else if ("2".equals(updateFlag)) {
						data.set("S_FLAG", Constant.NO_INT);
					} else {
						throw new TipException(
								"【数据更新标志未知】！data : " + data + ", updateFlag : " + updateFlag);
					}

					// 根据表中是否存在，将数据放入新增或者修改的list中
					SqlBean existSql = new SqlBean();
					existSql.and("PERSON_ID", pkCode);
					int existCount = ServDao.count("SY_HRM_ZDSTAFFFAMILY", existSql);
					if (0 == existCount) {
						// 不存在
						if (addCodes.contains(pkCode)) {
							// 同一批数据中已经有新增的，则第二条数据处理为更新
							updateList.add(data);
						} else {
							addCodes.add(pkCode);
							createList.add(data);
						}
					} else {
						updateList.add(data);
					}
				}
			});

			if (!createList.isEmpty()) {
				ServDao.creates("SY_HRM_ZDSTAFFFAMILY", createList);
			}

			if (!updateList.isEmpty()) {
				List<String> updateFieldList = new ArrayList(updateList.get(0).keySet());
				ServDao.updates("SY_HRM_ZDSTAFFFAMILY", updateFieldList, updateList);
			}
		}
		log.info("------------------ import SY_HRM_ZDSTAFFFAMILY increment data end ------------------");
	}

	/*
	 * 增量同步SY_HRM_ZDSTAFFPOSITION
	 */
	protected void impStaffPosition(final String smtime){
		log.info("----------------- import SY_HRM_ZDSTAFFPOSITION increment data begin--------");
		final Set<String> addCodes = new HashSet<String>();
		int total = ServDao.count("HRM_ZDSTAFFPOSITION", new ParamBean().setWhere(" AND 1=1"));
		for (int i = 0; i < total / SAVE_COUNT + 1; i++) {
			final List<Bean> createList = new ArrayList<Bean>();
			final List<Bean> updateList = new ArrayList<Bean>();
			ParamBean param = new ParamBean();
			param.setWhere(" AND 1=1");
			param.setOrder("UPDATE_FLAG");
			param.setShowNum(SAVE_COUNT);
			param.setNowPage(i);
			ServDao.findsCall("HRM_ZDSTAFFPOSITION", param, new RowHandler() {

				@Override
				public void handle(List<Bean> columns, Bean data) {
					String pkCode = data.getStr("PERSON_ID");

					data.set("S_MTIME", smtime);
					String updateFlag = data.getStr("UPDATE_FLAG").trim();
					// 区分数据为删除和费删除
					if ("0".equals(updateFlag) || "1".equals(updateFlag)) {
						// 暂时不做处理
					} else if ("2".equals(updateFlag)) {
						data.set("S_FLAG", Constant.NO_INT);
					} else {
						throw new TipException(
								"【数据更新标志未知】！data : " + data + ", updateFlag : " + updateFlag);
					}

					// 查询数据是否存在，将数据整理为更新或者新增
					SqlBean sqlBean = new SqlBean();
					sqlBean.set("PERSON_ID", pkCode);
					int eixstCount = ServDao.count("SY_HRM_ZDSTAFFPOSITION", sqlBean);
					//表中未查到
					if (0 == eixstCount) {
						if (addCodes.contains(pkCode)) {
							//同一批数据中出现相同的
							updateList.add(data);
						}else{
							addCodes.add(pkCode);
							createList.add(data);
						}
					}else {
						//表中查到
						updateList.add(data);
					}
				}
			});
			
			if (!createList.isEmpty()) {
				ServDao.creates("SY_HRM_ZDSTAFFPOSITION", createList);
			}
			
			if (!updateList.isEmpty()) {
				List<String> fields = new ArrayList(updateList.get(0).keySet());
				ServDao.updates("SY_HRM_ZDSTAFFPOSITION", fields, updateList);
			}
		}
		log.info("----------------- import SY_HRM_ZDSTAFFPOSITION increment data end-----------");
	}

	/*
	 * step7.增量同步SY_ORG_DEPT数据
	 */
	protected void impDept(final String smtime) throws Exception{
		log.info("------------------ import SY_ORG_DEPT increment data begin ------------------");
		new NImpDept().addDeptDatas(smtime);
		log.info("------------------ import SY_ORG_DEPT increment data end ------------------");
	}

	/*
	 * step8.增量同步SY_ORG_USER数据
	 */
	protected void impUser(final String smtime) throws Exception{
		log.info("------------------ import SY_ORG_USER increment data begin ------------------");
		new NImpUser().addUserDatas(smtime);
		log.info("------------------ import SY_ORG_USER increment data end ------------------");
	}

	/*
	 * step9.增量同步SY_HRM_ZDSTAFFAD数据
	 * 
	 * 咨询了下上游应用的同事，机构文件里每个机构当天只会出现一次的，不会有像人员这种又有新增又有修改的情况
	 */
	protected void impStaffAD(final String smtime){
		log.info("------------------ import SY_HRM_ZDSTAFFAD increment data begin ------------------");
		final HashSet<String> addCodes = new HashSet<String>();
		int total = ServDao.count("HRM_ZDSTAFFAD", new ParamBean().setWhere(" AND 1=1"));
		for (int i = 1; i <= total / SAVE_COUNT + 1; i++) {
			final List<Bean> createList = new ArrayList<Bean>();
			final List<Bean> updateList = new ArrayList<Bean>();
			ParamBean param = new ParamBean();
			param.setWhere(" AND 1=1");
			param.setShowNum(SAVE_COUNT);
			param.setNowPage(i);
			param.setOrder("UPDATE_FLAG"); // 保证更新的顺序是：新增，修改，删除
			ServDao.findsCall("HRM_ZDSTAFFAD", param, new RowHandler() {
				
				@Override
				public void handle(List<Bean> columns, Bean data) {
					String[] pkCodes = { data.getStr("SSIC_ID"), data.getStr("AD") }; // 主键
					data.set("S_MTIME", smtime);
					// 获取更新标志
					String updateFlag = data.getStr("UPDATE_FLAG");
					// 根据updateFlag将数据整理为删除和非删除
					if (updateFlag.equals("0") || updateFlag.equals("1")) {
						// 暂时不做处理
					} else if (updateFlag.equals("2")) {
						data.set("S_FLAG", Constant.NO_INT);
					} else {
						throw new TipException(
								"【数据更新标志未知】！data : " + data + ", updateFlag : " + updateFlag);
					}
					// 根据实际是否存在，将数据放入新增或修改集合中
					SqlBean existSql = new SqlBean();
					existSql.and("SSIC_ID", pkCodes[0]);
					existSql.and("AD", pkCodes[1]);
					int existCount = ServDao.count("SY_HRM_ZDSTAFFAD", existSql);
					if (existCount == 0) {
						// 不存在
						if (addCodes.contains(pkCodes)) {
							// 同一批数据中已经有新增的了，第二条记录处理成更新的
							updateList.add(data);
						} else {
							// 同一批数据中没有新增的，加入新增集合并记录到addCodes中
							createList.add(data);
							addCodes.add(StringUtils.join(pkCodes));
						}
					} else {
						// 存在，修改
						updateList.add(data);
					}
				}
			});
			if (createList.size() > 0) {
				ServDao.creates("SY_HRM_ZDSTAFFAD", createList);
			}
			if (updateList.size() > 0) {
				List<String> updateFieldList = new ArrayList(updateList.get(0).keySet());
				// 可能有多余字段需要remove
				ServDao.updates("SY_HRM_ZDSTAFFAD", updateFieldList, updateList);
			}
		}
		log.info("------------------ import SY_HRM_ZDSTAFFAD increment data end ------------------");
	}
	
	/**
	 * 导入全量数据
	 * @param tables 错误重新执行的表
	 * @param servId 导入数据的服务定义
	 * @param smtime 更新时间
	 * @param errorFlag 错误标志
	 * @return
	 */
	protected boolean impFullData(String tables, String servId, String smtime, boolean errorFlag){
		if (!errorFlag && ("".equals(tables) || tables.indexOf("SY_"+servId) > -1)) {
			log.info("------------------ import SY_"+servId+" full data begin ------------------");
			Transaction.begin();
			try {
				Transaction.getExecutor().execute("TRUNCATE TABLE SY_"+servId);
				int total = ServDao.count(servId, new ParamBean());
				// 接口表数据为空
				if (total == 0) {
					log.info("----------- interface "+servId+" no full data -------------");
				} else {
					for (int i = 1; i <= total / SAVE_COUNT + 1; i++) {
						ParamBean queryBean = new ParamBean();
						queryBean.set(Constant.PAGE_NOWPAGE, i); // 当前页数
						queryBean.set(Constant.PAGE_SHOWNUM, SAVE_COUNT); // 每页条数
						queryBean.setWhere("and 1=1");
						List<Bean> resultList = ServDao.finds(servId, queryBean);
						for (Bean data : resultList) {
							handleData(servId, data);
							// 设置S_MTIME
							data.set("S_MTIME", smtime);
						}
						ServDao.creates("SY_"+servId, resultList);
					}
				}
				Transaction.commit();
			} catch (Exception e) {
				Transaction.rollback();
				log.error("import SY_"+servId+" error ! " + e.getMessage());
				errorFlag = true;
				OrgLogMgr.orgLogSave("", "CC_DATA_IMPORT", "import SY_"+servId+" error ! " + e.getMessage(),
						ExceptionUtils.getFullStackTrace(e));
			} finally {
				log.info("------------------ import SY_"+servId+" full data end ------------------");
				Transaction.end();
			}
		}
		return errorFlag;
	}
	
	protected boolean impDeptFullData(String tables, String smtime, boolean errorFlag){
		if (!errorFlag && ("".equals(tables) || tables.indexOf("SY_ORG_DEPT") > -1)) {
			log.info("------------------ import SY_ORG_DEPT full data begin ------------------");
			Transaction.begin();
			try {
				new NImpDept().recuDept();

				Transaction.commit();
			} catch (Exception e) {
				Transaction.rollback();
				log.error("import SY_ORG_DEPT error ! " + e.getMessage());
				errorFlag = true;
				OrgLogMgr.orgLogSave("", "CC_DATA_IMPORT", "import SY_ORG_DEPT error ! " + e.getMessage(),
						ExceptionUtils.getFullStackTrace(e));
			} finally {
				log.info("------------------ import SY_ORG_DEPT full data end ------------------");
				Transaction.end();
			}
		}
		return errorFlag;
	}
	
	protected boolean impUserFullData(String tables, ParamBean param, String smtime, boolean errorFlag){
		if (!errorFlag && ("".equals(tables) || tables.indexOf("SY_ORG_USER") > -1)) {
			log.info("------------------ import SY_ORG_USER full data begin ------------------");
			Transaction.begin();
			try {
				NImpUser impUser = new NImpUser();
				impUser.recuUser();

				// 将指定人员加入管理员组
				impUser.initAdminRole(param);

				Transaction.commit();
			} catch (Exception e) {
				Transaction.rollback();
				log.error("import SY_ORG_USER error ! " + e.getMessage());
				errorFlag = true;
				OrgLogMgr.orgLogSave("", "CC_DATA_IMPORT", "import SY_ORG_USER error ! " + e.getMessage(),
						ExceptionUtils.getFullStackTrace(e));
			} finally {
				log.info("------------------ import SY_ORG_USER full data end ------------------");
				Transaction.end();
			}
		}
		return errorFlag;
	}
	
	/**
	 * 处理数据
	 * 每一个表处理各自的逻辑
	 * @param servId
	 */
	private void handleData(String servId, Bean data) {
		if(servId.equals("BOM_ZDPSTRUINFO")){
			String value = data.getStr("STRU_STATE").trim();
			if (value.equalsIgnoreCase(STATE_CANCLE)) {
				data.set("S_FLAG", Constant.NO_INT);
			} else {
				data.set("S_FLAG", Constant.YES_INT);
			}
		}else if(servId.equals("HRM_ZDSTAFFBINFO")){
			data.set("ENAME",
					UserName2PinyinUtils.toPinyin(data.getStr("NAME").trim()).toLowerCase());
			data.set("SNAME",
					UserName2PinyinUtils.toPinyinHead(data.getStr("NAME").trim()).toLowerCase());
		}
		
	}

}
