package com.rh.core.icbc.org;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.util.ServUtils;

/**
 * 根据工行的计算方式计算工行的机构隶属关系
 * @author if
 *
 */
public class DeptRelMgr {
	/** log. */
	private static Log log = LogFactory.getLog(DeptRelMgr.class);
	
	Map<String, Bean> map = new HashMap<String, Bean>();
	List<Bean> resultList = new ArrayList<Bean>();
	private String specialStru = ",0450008887,";
	private Map<String,String> SPE_DEPT_MAP = null;
	
	/**
	 * 创建关系
	 */
	public void createDeptRel() {
		
		specialStru = Context.getSyConf("SY_SPECIAL_STRU", ",0450008887,");
		
		String ROADeptChange = Context.getSyConf("SY_DEPT_CHANGE", "0001200003-0010100500");
		String[] speDepts = ROADeptChange.split(";");
		SPE_DEPT_MAP = new HashMap<String,String>();
		for(String sepeDept : speDepts){
			String[] deptInfo = sepeDept.split("-");
			if(deptInfo.length > 1){
				SPE_DEPT_MAP.put(deptInfo[0], deptInfo[1]);
			}
		}
		
		List<Bean> dataList = new ArrayList<Bean>();
		List<String> updateFields = new ArrayList<String>();
		
		// 删除临时使用的SY_ODEPT_DEPT_REL表中数据
		Transaction.getExecutor().execute("TRUNCATE TABLE SY_ODEPT_DEPT_REL");
		
		
		//*********** findsMajor ***********//
		log.info("--------- start build major stru ----------");
		dataList.clear();
		initData();
		for (Bean struBean : resultList) {
			Bean param = new Bean();
			param.set("STRU_ID", struBean.getStr("STRU_ID"));
			param.set("STRU_NAME", struBean.getStr("STRU_SNAME"));

			try{
				param = findsMajor(struBean, param);
				dataList.add(param);
			}catch(Exception e){
				e.printStackTrace();
				log.error("findsMajor同步数据异常，错误为："+e+",错误的数据为："+struBean);	
			}
//			log.info(param);
		}
		ServDao.creates("SY_ODEPT_DEPT_REL", dataList);
		dataList.clear();
		for (Bean struBean : resultList) {
			Bean param = new Bean();
			param.set("STRU_ID", struBean.getStr("STRU_ID"));
			param.set("STRU_NAME", struBean.getStr("STRU_SNAME"));

			setSpecialMajor(struBean, param , dataList);
		}
		updateFields.add("STRU_ID");
		updateFields.add("MAJOR_STRU_ID");
		updateFields.add("MAJOR_STRU_NAME");
		ServDao.updates("SY_ODEPT_DEPT_REL", updateFields, dataList);
		log.info("--------- end build major stru ----------");
		
		//*********** findsLead ***********//
		log.info("--------- start build lead stru ----------");
		dataList.clear();
		updateFields.clear();
		initData();
		
		for (Bean struBean : resultList) {
			Bean param = new Bean();
			param.set("STRU_ID", struBean.getStr("STRU_ID"));
			param.set("STRU_NAME", struBean.getStr("STRU_SNAME"));
			
			param = findsLead(struBean, param);
			
//			log.info(param);
			dataList.add(param);
		}
		updateFields.add("STRU_ID");
		updateFields.add("LEAD_STRU_ID");
		updateFields.add("LEAD_STRU_NAME");
		ServDao.updates("SY_ODEPT_DEPT_REL", updateFields, dataList);
		log.info("--------- end build lead stru ----------");
		
		//*********** findsDept ***********//
		
		log.info("--------- start build dept stru ----------");
		dataList.clear();
		updateFields.clear();
		initData();
		
		for (Bean struBean : resultList) {
			Bean param = new Bean();
			param.set("STRU_ID", struBean.getStr("STRU_ID"));
			param.set("STRU_NAME", struBean.getStr("STRU_SNAME"));
			
			param = findsDept(struBean, param);
			
//			log.info(param);
			dataList.add(param);
		}
		updateFields.add("STRU_ID");
		updateFields.add("DEPT_STRU_ID");
		updateFields.add("DEPT_STRU_NAME");
		ServDao.updates("SY_ODEPT_DEPT_REL", updateFields, dataList);
		log.info("--------- end build dept stru ----------");
		
		// 删除真正使用的SY_ODEPT_DEPT表中数据
		Transaction.getExecutor().execute("TRUNCATE TABLE SY_ODEPT_DEPT");
		
		// 将临时SY_ODEPT_DEPT_REL中的数据全量导入SY_ODEPT_DEPT中
		Transaction.getExecutor().execute("INSERT INTO SY_ODEPT_DEPT SELECT * FROM SY_ODEPT_DEPT_REL");
		
		// 更新SY_ORG_DEPT表中ODEPT_CODE和TDEPT_CODE的内容
		Transaction.getExecutor().execute("UPDATE SY_ORG_DEPT D SET D.ODEPT_CODE = "
			+ "(SELECT MAJOR_STRU_ID FROM SY_ODEPT_DEPT R WHERE R.STRU_ID = D.DEPT_CODE), "
			+ "D.TDEPT_CODE = (SELECT DEPT_STRU_ID FROM SY_ODEPT_DEPT R WHERE R.STRU_ID = D.DEPT_CODE)");
		
		//更新DEPT_TYPE的值
		Transaction.getExecutor().execute("update SY_ORG_DEPT set DEPT_TYPE = 1");
		Transaction.getExecutor().execute("update SY_ORG_DEPT set DEPT_TYPE =2 where DEPT_CODE in (select MAJOR_STRU_ID from SY_ODEPT_DEPT)");		
		// 强制清除SY_ORG_DEPT_ALL字典的缓存，否则会受脏数据影响
//		CacheMgr.getInstance().remove(Context.getCmpy(), "_CACHE_C_" + ServMgr.SY_ORG_DEPT_ALL);
//		DictMgr.clearCache("SY_ORG_DEPT", cmpyCode);
		ServDefBean sdb = ServUtils.getServDef("SY_ORG_DEPT");
		sdb.clearDictCache("icbc");
		
	}

	/**
	 * 重新初始化数据
	 */
	public void initData() {
		map.clear();
		resultList.clear();
		
//		resultList = ServDao.finds("SY_BOM_ZDPSTRUINFO_REL", new ParamBean());
		
		List<Bean> bomList = new ArrayList<Bean>();
		List<Bean> relList = new ArrayList<Bean>();
		Map<String, Bean> relMap = new HashMap<String, Bean>();
		
		// 跨数据源获取机构数据
		String dataSource = ServUtils.getServDef("SY_BOM_ZDPSTRUINFO").getDataSource();
		try {
			Transaction.begin(dataSource);
			SqlBean queryBean = new SqlBean();
			queryBean.selects("STRU_ID, STRU_SNAME, SUP_STRU, STRU_SIGN, MAN_GRADE, STRU_GRADE,STRU_LV");
			bomList = ServDao.finds("SY_BOM_ZDPSTRUINFO", queryBean);
			Transaction.commit();
		} catch (Exception e) {
			log.error(e);
		} finally {
			Transaction.end();
		}
		// 从本地获取机构隶属关系数据
		relList = ServDao.finds("SY_ODEPT_DEPT_REL", new ParamBean());
		for (Bean relBean : relList) {
			relMap.put(relBean.getStr("STRU_ID"), relBean);
		}
		// 将两种数据拼装
		for (int i = 0; i < bomList.size(); i++) {
			Bean bomBean = bomList.get(i);
			String struId = bomBean.getStr("STRU_ID");
			if (relMap.containsKey(struId)) {
				Bean relBean = relMap.get(struId);
				bomBean.set("STRU_NAME", relBean.getStr("STRU_NAME"));
				bomBean.set("DEPT_STRU_ID", relBean.getStr("DEPT_STRU_ID"));
				bomBean.set("DEPT_STRU_NAME", relBean.getStr("DEPT_STRU_NAME"));
				bomBean.set("LEAD_STRU_ID", relBean.getStr("LEAD_STRU_ID"));
				bomBean.set("LEAD_STRU_NAME", relBean.getStr("LEAD_STRU_NAME"));
				bomBean.set("MAJOR_STRU_ID", relBean.getStr("MAJOR_STRU_ID"));
				bomBean.set("MAJOR_STRU_NAME", relBean.getStr("MAJOR_STRU_NAME"));
			}
			handleSpecialStru(bomBean);
			resultList.add(bomBean);
		}
		
		// 创建Map stru_id - struBean
		for (Bean struBean : resultList) {
			map.put(struBean.getStr("STRU_ID"), struBean);
		}
		// 添加CHILDS
		for (Bean struBean : resultList) {
			String supStru = struBean.getStr("SUP_STRU");
			Bean pStruBean = map.get(supStru);
			if (pStruBean != null) {
				if (pStruBean.contains("CHILDS")) {
					pStruBean.getList("CHILDS").add(struBean);
				} else {
					List<Bean> childs = new ArrayList<Bean>();
					childs.add(struBean);
					pStruBean.set("CHILDS", childs);
				}
			}
		}
	}
	
	/**
	 * 找dept_stru_id，部级ID
	 * @param struBean
	 * @param param
	 * @return
	 */
	public Bean findsDept(Bean struBean, Bean param) {
		/*
		 * 隶属机构下一层的机构为部门级机构
		 * 如果隶属机构下一层为本部，则本部下一层的机构为部门级机构
		 */
		Bean pStruBean = getPStruBean(struBean); // 获取该机构的上级机构
		if (pStruBean == null) {
			return param;
		}
		
		if (pStruBean.getStr("STRU_ID").equals(pStruBean.getStr("MAJOR_STRU_ID"))) { // 上级机构为隶属机构
			return setDeptParam(struBean, param);
		} else if (pStruBean.getInt("STRU_SIGN") == 2) { // 上级机构为机构本部
			return setDeptParam(struBean, param);
		} else if (pStruBean.getInt("STRU_SIGN") == 8 && pStruBean.getStr("STRU_NAME").toUpperCase().indexOf("ADMIN.OFFICE") > -1) { // 上级机构为境外机构机构本部
			return setDeptParam(struBean, param);
		} else if (pStruBean.getInt("STRU_SIGN") == 18 && pStruBean.getStr("STRU_NAME").toUpperCase().indexOf("ADMIN.OFFICE") > -1) { // 上级机构为境内机构机构本部
			return setDeptParam(struBean, param);
		}
		return findsDept(pStruBean, param);
	}
	
	/**
	 * 找lead_stru_id，行级ID
	 * @param struBean
	 * @param param
	 * @return
	 */
	public Bean findsLead(Bean struBean, Bean param) {
		/*
		 * 找到隶属机构
		 * 隶属机构下面找到机构本部
		 * 机构本部下找到行长室或总裁办
		 */
		String majorStruId = struBean.getStr("MAJOR_STRU_ID");
		Bean majorStruBean = map.get(majorStruId);
		if (majorStruBean == null) {
			return param;
		}
		List<Bean> majorChilds = majorStruBean.getList("CHILDS");
		for (Bean majorChildBean : majorChilds) {
			if (majorChildBean.getInt("STRU_SIGN") == 2) { // 机构本部
				List<Bean> majorChildChilds = map.get(majorChildBean.getStr("STRU_ID")).getList("CHILDS");
				for (Bean majorChildChildBean : majorChildChilds) {
					if (majorChildChildBean.getInt("STRU_LV") == 66) { // 行长室
						return setLeadParam(majorChildChildBean, param);
					}
				}
			} else if (majorChildBean.getInt("STRU_SIGN") == 8 && majorChildBean.getStr("STRU_NAME").toUpperCase().indexOf("ADMIN.OFFICE") > -1) {
				List<Bean> majorChildChilds = map.get(majorChildBean.getStr("STRU_ID")).getList("CHILDS");
				for (Bean majorChildChildBean : majorChildChilds) {
					if (majorChildChildBean.getStr("STRU_NAME").indexOf("高管层") > -1) { // 行长室
						return setLeadParam(majorChildChildBean, param);
					}
				}
			} else if (majorChildBean.getInt("STRU_SIGN") == 18 && majorChildBean.getStr("STRU_NAME").toUpperCase().indexOf("ADMIN.OFFICE") > -1) {
				List<Bean> majorChildChilds = map.get(majorChildBean.getStr("STRU_ID")).getList("CHILDS");
				for (Bean majorChildChildBean : majorChildChilds) {
					if (majorChildChildBean.getStr("STRU_NAME").indexOf("高管层") > -1) { // 行长室
						return setLeadParam(majorChildChildBean, param);
					}
				}
			}
		}
		return param;
	}
	
	/**
	 * 找major_stru_id，隶属机构
	 * @param struBean
	 * @param param
	 * @return
	 * 
	 * 直属机构：就是自己。
	 * 
	 * 境外机构：机构层级为2的，不在指定范围内的就是自己；在指定范围内的就找机构层级为1的。
	 * 
	 * 境内机构：找机构层级为1的。
	 * 
	 * 分支机构：总行、一级、二级行就是自己；二级行下一层级的支行为自己；支行下的支行为最顶级支行。
	 */
	public Bean findsMajor(Bean struBean, Bean param) {
		if (struBean == null) {
			return param;
		}
		int struSign = struBean.getInt("STRU_SIGN");
		int manGrade = struBean.getInt("MAN_GRADE");
		int struGrade = struBean.getInt("STRU_GRADE");
		int struLv = struBean.getInt("STRU_LV");
		
		//机构层级为4（一级支行的）
		if (struGrade == 4) {
				Bean pStruBean = getPStruBean(struBean);
				if (pStruBean.getInt("STRU_GRADE") != 4 || struBean.getStr("STRU_ID").equals(struBean.getStr("SUP_STRU"))) {
					return setMajorParam(struBean, param);
				}
		}
		
		if (struSign == 4 || struSign == 5) { // 直属机构
			
			if(manGrade < 3){
				Bean pStruBean = getPStruBean(struBean);
				if(pStruBean.getInt("MAN_GRADE") !=1 || pStruBean.getInt("STRU_SIGN")!=1 ){
					return setMajorParam(struBean, param);
				}
			}
			/*
			 * 直属机构就是自己
			 */
			//return setMajorParam(struBean, param);
		} else if (struSign == 8) { // 境外机构
			/*
			 * 层级为2的，不在“事业部/内设部门”、“分组/分部/分中心”、“境外本部”和“境外虚拟机构”的范围内的就是自己，
			 * 在的找上级manGrade=1的
			 */
			if (manGrade == 2) {
				if (",93,94,96,97,".indexOf("," + struLv + ",") < 0) { // 
					String struName = struBean.getStr("STRU_SNAME");
					if((struName.indexOf("本部") < 0) && (struName.toUpperCase().indexOf("ADMIN.OFFICE") < 0)){
						return setMajorParam(struBean, param);
					}
				}
			}else if(manGrade == 1){
				String struName = struBean.getStr("STRU_SNAME");
				if(",93,94,96,97,".indexOf("," + struLv + ",") >= 0 ||(struName.indexOf("本部") < 0) || (struName.toUpperCase().indexOf("ADMIN.OFFICE") < 0)){
					return setMajorParam(struBean, param);
				}
			}
			
//			
//			else  {
//				return findsMajor(getPStruBean(struBean), param);
//			}
			
		} else if (struSign == 18) { // 境内控股机构
			/*
			 * 境内控股机构找第一级
			 */
			if (manGrade == 1) {
				return setMajorParam(struBean, param);
			} else {
				return findsMajor(getPStruBean(struBean), param);
			}
		} else if (struSign == 1) { // 分支机构
			/*
			 * 总行，一级、二级行为自己
			 * 二级行下一层级的支行为自己
			 * 支行下的支行为最顶级支行
			 */
			if (struGrade == 1 ||struGrade == 2 || struGrade == 3) { // 总行，一级分行，二级分行
				return setMajorParam(struBean, param);
			} else if (struGrade == 4) { // 支行
				Bean pStruBean = getPStruBean(struBean);
				if (pStruBean.getInt("STRU_SIGN") == 1 && pStruBean.getInt("STRU_GRADE") == 3) {
					return setMajorParam(struBean, param);
				} else {
					return findsMajor(getPStruBean(struBean), param);
				}
			} else { // 网点或其他
				return findsMajor(getPStruBean(struBean), param);
			}
		}
		return findsMajor(getPStruBean(struBean), param);
	}
	
	private void setSpecialMajor(Bean struBean, Bean data, List<Bean> dataList){
		
		if(struBean == null){
			return;
		}
		int struLv = struBean.getInt("STRU_LV");
		
		if(struLv == 59 || struLv == 63 ||(specialStru.indexOf("," + struBean.getStr("STRU_ID") + ",") >= 0 )){
			Bean pBean = getPStruBean(struBean);
			if(pBean.getInt("STRU_LV") == 59 || pBean.getInt("STRU_LV") == 63){
				setSpecialMajor(pBean, data, dataList);
			}else{
				dataList.add(setMajorParam(struBean, data));
				return;
			}
		}
		setSpecialMajor(getPStruBean(struBean), data, dataList);
	}
	
	
	/**
	 * 找父
	 * @param struBean
	 * @return
	 */
	public Bean getPStruBean(Bean struBean) {
		String supStru = struBean.getStr("SUP_STRU");
		Bean pStruBean = map.get(supStru);
		return pStruBean;
	}
	
	/**
	 * 设置Major值
	 * @param struBean
	 * @param param
	 * @return
	 */
	public Bean setMajorParam(Bean struBean, Bean param) {
		param.set("MAJOR_STRU_ID", struBean.getStr("STRU_ID"));
		param.set("MAJOR_STRU_NAME", struBean.getStr("STRU_SNAME"));
		return param;
	}
	
	/**
	 * 设置Lead值
	 * @param struBean
	 * @param param
	 * @return
	 */
	public Bean setLeadParam(Bean struBean, Bean param) {
		param.set("LEAD_STRU_ID", struBean.getStr("STRU_ID"));
		param.set("LEAD_STRU_NAME", struBean.getStr("STRU_SNAME"));
		return param;
	}
	
	/**
	 * 设置Dept值
	 * @param struBean
	 * @param param
	 * @return
	 */
	public Bean setDeptParam(Bean struBean, Bean param) {
		param.set("DEPT_STRU_ID", struBean.getStr("STRU_ID"));
		param.set("DEPT_STRU_NAME", struBean.getStr("STRU_SNAME"));
		return param;
	}
	
	/**
	 * 对特殊机构进行处理：
	 * 国际单证中心本部 --- 总行本部
	 * @param bomBean
	 */
	private void handleSpecialStru(Bean bomBean) {
		String deptCode = bomBean.getStr("STRU_ID");
		if(SPE_DEPT_MAP.containsKey(deptCode)){
			bomBean.set("SUP_STRU", SPE_DEPT_MAP.get(deptCode));
		}
	}
}
