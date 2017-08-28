package com.rh.core.icbc.imp.target;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.icbc.imp.log.OrgLogMgr;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.util.Constant;


public class NImpDept {
	private static Log log = LogFactory.getLog(NImpDept.class);
	
	// 单个事务最大提交数
	public static final int QUERY_NUM = 5000;
	
	public static final String ZONG_DEPT_CODE = "icbc0001";
	
	// 创建公司时指定的公司编码
	public static final String CMPY_CODE = "icbc"; 
	
	// 创建公司时自动创建的默认跟节点
	public static final String DEFAULT_ROOT = "icbc0001"; 
	
	// 工行提供的机构类型编码
	public static final String orgTypes = ",01,02,03,04,05,06,07,08,10,11,12,26,29,";
	
	
    /** 部门类型：部门 */
    public static final int DEPT_TYPE_DEPT = 1;
    /** 部门类型：机构 */
    public static final int DEPT_TYPE_ORG = 2;
    
    /** 源数据服务：要求所有字段不能为‘不显示’ */
    public static final String BOM_CMPSTRUINFO = "SY_BOM_ZDPSTRUINFO";
    
    /** 上一条数据的父ID */
//    private String oldParent = "";
    
    
	/**
	 * 同步组织机构数据
	 */
	public void recuDept() throws Exception {
		Bean queryCount = new Bean();
		queryCount.set(Constant.PARAM_WHERE, " and 1=1");
		// 查询此服务中的所有数据
		int count = ServDao.count(BOM_CMPSTRUINFO, queryCount);
		
		// 开启事务
//		Transaction.begin();
		
		//清除SY_ORG_DEPT表中ICBC的数据
		cleanDeptData();
//		Transaction.commit();
		
		for (int i = 1; i <= count / QUERY_NUM + 1; i++) {
			log.debug("开始第 " + i + "页");
			ParamBean queryBean = new ParamBean();

			Bean page = new Bean();
			page.set("NOWPAGE", i); // 当前页数
			page.set("SHOWNUM", QUERY_NUM); // 每页条数
//			page.set("ORDER", "STRU_GRADE , SUP_STRU , STRU_ID"); // 排序,-机构层级/直接上级机构/机构编码 , 父部门变了，就重新开始排序
//			page.set("ORDER", "STRU_GRADE , SUP_STRU , STRU_SNAME");
			queryBean.set("_PAGE_", page);

			//queryBean.set("_extWhere", "");
			Bean resultBean = ServMgr.act(BOM_CMPSTRUINFO, "query", queryBean);

			List<Bean> resultList = resultBean.getList(Constant.RTN_DATA);
	    
			List<Bean> depts = new ArrayList<Bean>(resultList.size());
			for (Bean deptBean: resultList) {
				// 添加部门，做字段对照
				Bean dept = null;
				// 如果是已撤消的部门或者为自助服务类
				if (deptBean.getInt("STRU_STATE") == 3 || deptBean.getInt("STRU_SIGN") == 6) {
					// 不做操作，直接过滤掉无用数据
				} else {
					dept = addDept(deptBean);
				}
				
				// 放入集合中
				if (dept != null) {
					depts.add(dept);
				}
			}
			// 批量新建
			ServDao.creates(ServMgr.SY_ORG_DEPT, depts);

//			Transaction.commit();
		}

		// 删除默认创建的根节点
		deleteDefaultRoot();
//		Transaction.commit();

		// 重建层级树
		rebuildTree();
//		Transaction.commit();
		
		// 重建当前用户所在公司的组织机构数据
		rebuildDept();
//		Transaction.commit();
		
		// 重置系统管理员所在部门
		resetAdminDept();
//		Transaction.commit();
		
//		Transaction.end();
	}
	
	/**
	 * 清除SY_ORG_DEPT表中ICBC的数据
	 */
	public void cleanDeptData() throws Exception {
		 ParamBean whereBean = new ParamBean();
		 whereBean.setWhere(" AND CMPY_CODE = '" + NImpDept.CMPY_CODE + "' AND DEPT_CODE != '" + NImpDept.CMPY_CODE + "0001'");
		 ServDao.destroys(ServMgr.SY_ORG_DEPT, whereBean);
		 log.info("table SY_ORG_DEPT is clean !");
	}
	
	/**
	 * 添加部门  TODO 确定从哪些字段取数据
	 * @param odept
	 */
	private ParamBean addDept(Bean odept) {
		ParamBean newBean = new ParamBean();
		newBean.set("DEPT_CODE", odept.getStr("STRU_ID"));
		newBean.set("DEPT_NAME", odept.getStr("STRU_SNAME"));
		
		newBean.set("DEPT_ENNAME", odept.getStr("STRU_FOREIGN_FNAME"));
		newBean.set("DEPT_SHORT_ENNAME", odept.getStr("STRU_FOREIGN_SNAME"));
		newBean.set("DEPT_PCODE", odept.getStr("SUP_STRU"));
		
		newBean.set("CMPY_CODE", CMPY_CODE);
		newBean.set("DEPT_TYPE", "");
		
		newBean.set("DEPT_SHORT_NAME", odept.getStr("STRU_SNAME"));
		newBean.set("DEPT_FULL_NAME", odept.getStr("STRU_FNAME"));
		newBean.set("DEPT_SORT", odept.getInt("BACK3"));
//		newBean.set("DEPT_SRC_TYPE4", odept.getStr("STRU_LV")); //机构类型
		
		//判断部门还是机构
		setDeptTypeForCochat(newBean, odept);
		//是否有效
		setSFlagForCochat(newBean, odept);
		// 排序
//		setDeptSortForCochat(newBean, odept);
		
		return newBean;
	}
	
	/**
	 * 判断部门还是机构
	 * @param newBean
	 * @param odept
	 */
	private void setDeptTypeForCochat(Bean newBean, Bean odept) {
		int struGrade = odept.getInt("STRU_GRADE"); 	// 机构层级
		int struSign = odept.getInt("STRU_SIGN"); 		// 机构标识
		String struLv = odept.getStr("STRU_LV"); 		// 机构类别
		int manGrade = odept.getInt("MAN_GRADE");		// 管理层级
		
		if (
				(struSign == 1 && struGrade != 5) // 分支机构，STRU_GRADE != 5
				|| struSign == 4 // 直属机构，全是
				|| struSign == 5 // 附属机构，全是
				|| (struSign == 18 && manGrade == 1) // 境内控股机构，man_grade=1，所属行
				|| (struSign == 8 && manGrade == 1)	// 境外机构，man_grade=1肯定是分行
				// 境外机构，除93(事业部/内设部门)，94(分组/分部/分中心)，96(境外本部)，97(境外虚拟机构)外，均为机构
				|| (struSign == 8 && manGrade != 1 && !struLv.equals("93") && !struLv.equals("94") && !struLv.equals("96") && !struLv.equals("97"))
				) {
			newBean.set("DEPT_TYPE", DEPT_TYPE_ORG);
		} else {
			newBean.set("DEPT_TYPE", DEPT_TYPE_DEPT);
		}
		
		// 需要为总行单独做处理，将总行本部设置为机构类型
		String deptCode = newBean.getStr("DEPT_CODE");
		String exceptionCodes = Context.getSyConf("CC_EXCEPTION_CODES", "0010100500,");
		if (exceptionCodes.indexOf(deptCode) > -1) {
			newBean.set("DEPT_TYPE", DEPT_TYPE_ORG);
		}
		
		// 在因私出境系统中，将所有机构本部设置成机构
		if (struSign == 2) {
			newBean.set("DEPT_TYPE", DEPT_TYPE_ORG);
		}
	}
	
//	/**
//	 * 设置DEPT_SORT字段
//	 * @param newBean
//	 * @param odept
//	 */
//	private void setDeptSortForCochat(Bean newBean, Bean odept) {
//		int deptSort = 900;
//		if (odept.getInt("STRU_SIGN") == 2) {
//			deptSort = 999;
//		}
//		newBean.set("DEPT_SORT", deptSort);
//	}
	
	/**
	 * 设置S_FLAG标识
	 * @param newBean - 需要保存的数据
	 * @param odept - 源数据
	 */
	private void setSFlagForCochat(Bean newBean, Bean odept) {
		// 如果为1，正常， 2，临时营业
		if (odept.getInt("STRU_STATE") == 1 || odept.getInt("STRU_STATE") == 2) {
			newBean.set("S_FLAG", 1);
		} else { // 3，撤销
			newBean.set("S_FLAG", 2);
		}
		// 如果为删除数据 // 2,撤消， 3,完全删除
		if (odept.getInt("S_FLAG") == 2 || odept.getInt("S_FLAG") == 3) {
			newBean.set("S_FLAG", 2);
		}
		// 如果为自助服务机构或者为虚拟机构
		if (odept.getInt("STRU_SIGN") == 6 || odept.getInt("STRU_SIGN") == 7) {
			newBean.set("S_FLAG", 2);
		}
	}
	
	/**
	 * 重建层级树
	 */
	public void rebuildTree() throws Exception {
		ParamBean deptDictParam = new ParamBean(ServMgr.SY_SERV_DICT, "rebuildTree", ServMgr.SY_ORG_DEPT);
		// 直接调用字典服务的重建方法	
		ServMgr.act(deptDictParam);
	}
	
	/**
	 * 删除默认创建的根节点
	 */
	public void deleteDefaultRoot() throws Exception {
		ServDao.update(ServMgr.SY_ORG_DEPT, new Bean().setId(DEFAULT_ROOT).set("S_FLAG", "2"));
	}
	
	/**
	 * 重置系统管理员所在部门
	 */
	public void resetAdminDept() {
		// 根部门
		Bean odeptBean = ServDao.find(ServMgr.SY_ORG_DEPT, new ParamBean().setWhere(" AND DEPT_LEVEL = '1' AND S_FLAG = '1' AND CMPY_CODE = '" + CMPY_CODE + "' AND DEPT_PCODE IS NULL"));
		// 管理员
		Bean adminBean = ServDao.find(ServMgr.SY_ORG_USER, new ParamBean().setWhere(" AND USER_LOGIN_NAME = 'admin' AND S_FLAG = '1' AND CMPY_CODE = '" + CMPY_CODE + "'"));
		
		//.set("ODEPT_CODE", odeptBean.getStr("ODEPT_CODE"))
		//.set("TDEPT_CODE", odeptBean.getStr("TDEPT_CODE"));
		try {
			adminBean.set("DEPT_CODE", odeptBean.getStr("DEPT_CODE"));
			ServDao.update(ServMgr.SY_ORG_USER, adminBean);
		} catch (Exception e) {
			log.info("-----resetAdminDept----fail-----");
		}
	}
	
	/**
	 * 重建当前用户所在公司的所有组织机构数据
	 */
	public void rebuildDept() throws Exception {
		// 需要更新ODEPT_CODE和
        ArrayList<Bean> deptList = new ArrayList<Bean>(200000);
        ArrayList<Bean> sortList = new ArrayList<Bean>(200000);
        buildSubList(deptList, new Bean(), sortList);
        
        int count = deptList.size();
        List<String> updateFields = new ArrayList<String>();
    	updateFields.add("ODEPT_CODE");
    	updateFields.add("TDEPT_CODE");
    	updateFields.add("DEPT_LEVEL");
    	updateFields.add("CODE_PATH");
    	
        if (count > 0) {
            count = ServDao.updates(ServMgr.SY_ORG_DEPT, updateFields, deptList);
        }
	}
	
	/**
     * 递归整理部门数据
     * @param list 存放部门数据的列表
     * @param pBean 父部门信息
     */
    private void buildSubList(List<Bean> list, Bean pBean, List<Bean> sortList) throws Exception {
    	String pid = pBean.getId().length() > 0 ? pBean.getId() : null;
    	
    	List<Bean> subList = DictMgr.getTreeList(ServMgr.SY_ORG_DEPT_ALL, pid, 1);
    	
        for (Bean item : subList) {
        	
        	SqlBean sqlBean = new SqlBean();
        	sqlBean.setId(item.getStr("ID"));
        	sqlBean.set("DEPT_CODE", item.getStr("ID"));
        	
            if (pBean.getId().length() == 0) { //根节点
            	sqlBean.set("DEPT_LEVEL", 1).set("ODEPT_CODE", item.getStr("ID")).set("TDEPT_CODE", item.getStr("ID"))
//                    .set("DEPT_TYPE", Constant.DEPT_TYPE_ORG)
                    .set("CODE_PATH", item.getStr("ID") + Constant.CODE_PATH_SEPERATOR);
            } else {
            	sqlBean.set("DEPT_LEVEL", pBean.getInt("DEPT_LEVEL") + 1)
                    .set("CODE_PATH", pBean.getStr("CODE_PATH") + item.getStr("ID") + Constant.CODE_PATH_SEPERATOR);
                if (item.getInt("DEPT_TYPE") == Constant.DEPT_TYPE_ORG) { //子是机构
                	sqlBean.set("ODEPT_CODE", item.getStr("ID")).set("TDEPT_CODE", item.getStr("ID"));
                } else { //子是部门
                	sqlBean.set("ODEPT_CODE", pBean.getStr("ODEPT_CODE"));
                    if (pBean.getInt("DEPT_TYPE") == Constant.DEPT_TYPE_ORG) { //父是机构
                    	sqlBean.set("TDEPT_CODE", item.getStr("ID"));
                    } else {
                    	sqlBean.set("TDEPT_CODE", pBean.getStr("TDEPT_CODE"));
                    }
                }
            }
            sqlBean.set("DEPT_TYPE", item.getStr("DEPT_TYPE"));
            
            buildSubList(list, sqlBean, sortList);
            list.add(sqlBean);
        }
    }
    
    /**
     * 增量导入部门数据
     */
    public void addDeptDatas(String smtime) throws Exception {
    	int count = 0;
    	String where = " AND S_MTIME = '" + smtime + "'";
    	List<String> errorCodes = new ArrayList<String>(); // 出现错误的code
    	List<String> finishCodes = new ArrayList<String>(); // 优先处理完成的code
    	List<ParamBean> saveErrorList = new ArrayList<ParamBean>();
    	List<Bean> incrementDeptDatas = getIncrementDeptDatas(where);
    	for (int i=0; i<incrementDeptDatas.size(); i++) {
    		count += addIncrementDeptBean(incrementDeptDatas.get(i), errorCodes, finishCodes, 0, saveErrorList);
    	}
    	// 将保存时出现父节点错误的数据再保存一次
    	for (ParamBean saveErrorBean : saveErrorList) {
    		log.debug("------校正数据后再次保存前面出错的数据: saveErrorBean = " + saveErrorBean + "------");
    		try {
    			ServMgr.act(ServMgr.SY_ORG_DEPT, ServMgr.ACT_SAVE, saveErrorBean);
    			count++;
    		} catch (TipException e) {
    			log.error("-----import---校正数据出错---saveErrorBean = : " + saveErrorBean + ", error = " + e);
    		}
		}
    	log.info("increment data is : " + count + " !");
    }
    
    /**
     * 删除部门例外表时，当成增量，重置此条数据
     * @param struId - 部门ID - deptCode
     */
    public void resetDeptData(String struId) throws Exception {
    	int count = 0;
    	String where = " AND STRU_ID = '" + struId + "'";
    	List<String> errorCodes = new ArrayList<String>(); 
    	List<String> finishCodes = new ArrayList<String>();
    	List<ParamBean> saveErrorList = new ArrayList<ParamBean>();
    	List<Bean> incrementDeptDatas = getIncrementDeptDatas(where);
    	for (int i=0; i<incrementDeptDatas.size(); i++) {
    		count += addIncrementDeptBean(incrementDeptDatas.get(i), errorCodes, finishCodes, 0, saveErrorList);
    	}
    	// 将保存时出现父节点错误的数据再保存一次
    	for (ParamBean saveErrorBean : saveErrorList) {
    		log.debug("------校正数据后再次保存前面出错的数据: saveErrorBean = " + saveErrorBean + "------");
    		try {
    			ServMgr.act(ServMgr.SY_ORG_DEPT, ServMgr.ACT_SAVE, saveErrorBean);
    			count++;
    		} catch (TipException e) {
    			log.error("-----import---校正数据出错---saveErrorBean = : " + saveErrorBean + ", error = " + e);
    		}
		}
    	log.info("increment data is : " + count + " !");
    }
    
    /**
     * 获取某时间点之后的增量数据
     * @return
     */
    private List<Bean> getIncrementDeptDatas(String where) throws Exception {
    	ParamBean whereBean = new ParamBean();
    	whereBean.setWhere(where);
    	/*
    	 * STRU_STATE : 机构状态; 1／2正常; 3撤销
    	 * STRU_GRADE ：机构层级; 1总行; 2一级分行; 3二级分行; 4支行; 5网点
    	 * SUP_STRU   ：父部门ID
    	 * STRU_ID	  ：部门ID
    	 */
    	whereBean.setOrder("STRU_STATE, STRU_GRADE, SUP_STRU, STRU_ID");
    	List<Bean> incrementDeptDatas = ServDao.finds(BOM_CMPSTRUINFO, whereBean);
    	return incrementDeptDatas;
    }
    /**
     * 处理单条增量数据
     * @param incrementDeptBean - 需要增加的增量数据Bean
     * @param errorCodes - 错误数据集合，数据将不被处理
     * @param count - 总处理量
     * @return - 总处理量
     */
    private int addIncrementDeptBean(Bean incrementDeptBean, List<String> errorCodes, List<String> finishCodes, int count, List<ParamBean> saveErrorList) throws Exception {
    	ParamBean newDeptBean = addDept(incrementDeptBean); // 将增量数据处理成本系统部门Bean
    	
    	newDeptBean = setException(newDeptBean); // 添加例外
    	
    	// 检查当前数据是否已经被处理过
    	if (finishCodes.contains(newDeptBean.getStr("DEPT_CODE"))) { // 如果被处理过
    		return count; // 直接返回
    	}
    	// 递归检查此部门的上级部门是否在错误集合中
    	if (checkCodeInError(errorCodes, newDeptBean)) { // 存在，跳过此条数据
    		log.error("------当前父节点未找到" + incrementDeptBean);
    		OrgLogMgr.orgLogSave(newDeptBean.getStr("DEPT_CODE"), ServMgr.SY_ORG_DEPT, "数据 " + newDeptBean.getStr("DEPT_CODE") + " 未同步成功！", "因父节点未找到，此条数据未同步成功，数据为：" + incrementDeptBean.toString());
    		return count;
    	}
    	
    	// 处理此条数据
		Bean oldDeptBean = ServDao.find(ServMgr.SY_ORG_DEPT, newDeptBean.getStr("DEPT_CODE")); // 查询是否在本系统中存在数据
		if (oldDeptBean == null) { // 不存在，新增状态
			if (newDeptBean.getInt("S_FLAG") == 1) { // 有效数据，处理
				Bean newPDeptBean = ServDao.find(ServMgr.SY_ORG_DEPT, newDeptBean.getStr("DEPT_PCODE"));
				if (newPDeptBean == null) { // 如果新增部门的父部门在本系统中不存在
					Bean incrementPDeptBean = ServDao.find(BOM_CMPSTRUINFO, newDeptBean.getStr("DEPT_PCODE"));
					if (incrementPDeptBean == null) { // 在中间表中也不存在
						errorCodes.add(newDeptBean.getStr("DEPT_CODE")); // 将未能同步的数据ID放入错误集合中
						log.error("------当前父节点未找到" + incrementDeptBean);
						OrgLogMgr.orgLogSave(newDeptBean.getStr("DEPT_CODE"), ServMgr.SY_ORG_DEPT, "数据 " + newDeptBean.getStr("DEPT_CODE") + " 未同步成功！", "因父节点未找到，此条数据未同步成功，数据为：" + incrementDeptBean.toString());
						return count;
					} else { // 在中间表中存在
						count = addIncrementDeptBean(incrementPDeptBean, errorCodes, finishCodes, count, saveErrorList);
						finishCodes.add(newDeptBean.getStr("DEPT_PCODE")); // 将已经处理完成的父部门ID放入集合中，下次遍历到此条数据就跳过
						return addIncrementDeptBean(incrementDeptBean, errorCodes, finishCodes, count, saveErrorList);
					}
				} else { // 新增部门的父部门在本系统中存在
					newDeptBean.setId(newDeptBean.getStr("DEPT_CODE"));
					newDeptBean.setAddFlag(true);
					try {
						ServMgr.act(ServMgr.SY_ORG_DEPT, ServMgr.ACT_SAVE, newDeptBean); // 调用系统方法添加
					} catch (TipException e) {
						count--;
						saveErrorList.add(newDeptBean);
						log.error("------当前节点父节点错误！newDeptBean = " + newDeptBean + "------" + e.getMessage());
					}
					count++;
					return count;
				}
			} else { // 已撤消或完全删除的数据
				// TODO 无效数据，暂不处理
				return count;
			}
		} else { // 存在，修改状态
			if (newDeptBean.getStr("DEPT_PCODE").equals(oldDeptBean.getStr("DEPT_PCODE"))) { // 如果父部门没有改变
				newDeptBean.setId(newDeptBean.getStr("DEPT_CODE"));
				newDeptBean.setAddFlag(false);
				newDeptBean.set("_OLDBEAN", oldDeptBean);
				
				// 去掉参数中的DEPT_CODE和DEPT_PCODE,否则在生成树时会报'自身的子不能作为父'的错误
				newDeptBean.remove("DEPT_CODE");
				newDeptBean.remove("DEPT_PCODE");
				
				ServMgr.act(ServMgr.SY_ORG_DEPT, ServMgr.ACT_SAVE, newDeptBean);
				count++;
				return count;
			} else { // 变更了父部门
				Bean newPDeptBean = ServDao.find(ServMgr.SY_ORG_DEPT, newDeptBean.getStr("DEPT_PCODE"));
				if (newPDeptBean == null) { // 如果新增部门的父部门在本系统中不存在
					Bean incrementPDeptBean = ServDao.find(BOM_CMPSTRUINFO, newDeptBean.getStr("DEPT_PCODE"));
					if (incrementPDeptBean == null) { // 在中间表中也不存在
						errorCodes.add(newDeptBean.getStr("DEPT_CODE")); // 将未能同步的数据ID放入错误集合中
						log.error("------当前父节点未找到" + incrementDeptBean);
						OrgLogMgr.orgLogSave(newDeptBean.getStr("DEPT_CODE"), ServMgr.SY_ORG_DEPT, "数据 " + newDeptBean.getStr("DEPT_CODE") + " 未同步成功！", "因父节点未找到，此条数据未同步成功，数据为：" + incrementDeptBean.toString());
						return count;
					} else { // 在中间表中存在
						count = addIncrementDeptBean(incrementPDeptBean, errorCodes, finishCodes, count, saveErrorList);
						finishCodes.add(newDeptBean.getStr("DEPT_PCODE")); // 将已经处理完成的父部门ID放入集合中，下次遍历到此条数据就跳过
						return addIncrementDeptBean(incrementDeptBean, errorCodes, finishCodes, count, saveErrorList);
					}
				} else { // 新增部门的父部门在本系统中存在
					newDeptBean.setId(newDeptBean.getStr("DEPT_CODE"));
					newDeptBean.setAddFlag(false);
					newDeptBean.set("_OLDBEAN", oldDeptBean);
					try {
						ServMgr.act(ServMgr.SY_ORG_DEPT, ServMgr.ACT_SAVE, newDeptBean);
					} catch (TipException e) {
						count--;
						saveErrorList.add(newDeptBean);
						log.error("------当前节点父节点错误！newDeptBean = " + newDeptBean + "------" + e.getMessage());
					}
					count++;
					return count;
				}
			}
		}
    }
    
    /**
     * 递归检查当前部门Bean的上级部门是否在错误集合中，如果存在返回true
     * @param errorCodes - 当前错误集合
     * @param deptBean - 当前部门Bean
     * @return - 是否存在
     */
    private boolean checkCodeInError(List<String> errorCodes, Bean deptBean) throws Exception {
    	String deptPCode = deptBean.getStr("DEPT_PCODE");
    	if (deptPCode.isEmpty()) {
    		return false;
    	} else {
    		if (errorCodes.contains(deptPCode)) { // 如果父部门存在在错误集合中
    			errorCodes.add(deptBean.getStr("DEPT_CODE")); // 将未能同步的数据ID放入错误集合中
    			return true;
    		} else {
    			Bean deptPBean = ServDao.find(BOM_CMPSTRUINFO, deptPCode);
    			if (deptPBean == null) {
    				deptPBean = ServDao.find(ServMgr.SY_ORG_DEPT, deptPCode);
    				if (deptPBean == null) {
    					return false;
    				} else {
    					return checkCodeInError(errorCodes, deptPBean);
    				}
    			} else {
    				deptPBean = addDept(deptPBean);
    				return checkCodeInError(errorCodes, deptPBean);
    			}
    		}
    	}
    }
    
    /**
	 * 添加部门的例外情况
	 * @param odept - 部门原数据
	 * @return - 根据例外情况重置后的部门数据Bean
	 */
	private ParamBean setException(ParamBean odept) {
		String deptCode = odept.getStr("DEPT_CODE");
		if(deptCode.equals("0001200003")){
			odept.set("DEPT_PCODE", "0010100500");
		}
		return odept;
	}
}
