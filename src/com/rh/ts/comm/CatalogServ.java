package com.rh.ts.comm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.org.DeptBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.util.OrgConstant;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.util.Strings;
import com.rh.ts.pvlg.PvlgUtils;
import com.rh.ts.util.TsConstant;

public class CatalogServ extends CommonServ {

	private static Log log = LogFactory.getLog(CatalogServ.class);

	final String servId = "TS_COMM_CATALOG";

	final String moduleDictId = "TS_MODULE_CATALOG";

	/**
	 * 同步组织机构到目录， INIT_MODULE值是all表示同步至所有目录类型，CTLG_MODULE值是指定目录类型同步
	 * 
	 * @param paramBean
	 * @return
	 */
	public OutBean initCatalog(ParamBean paramBean) {
		OutBean outBean = new OutBean();

		String ctlgPcode = paramBean.getStr("CTLG_PCODE_H");
		String cmpyCode = paramBean.getStr("CMPY_CODE");
		String moduleCode = paramBean.getStr("CTLG_MODULE");
		String initModule = paramBean.getStr("INIT_MODULE");

		if (!initModule.equals("all") && StringUtils.isBlank(moduleCode)) {
			return outBean.setError("CTLG_MODULE is null,目录类型不能为空!");
		}

		// 获取所有目录类型
		Bean dictBean = DictMgr.getDict("TS_MODULE_CATALOG");
		List<Bean> dictList = dictBean.getList("SY_SERV_DICT_ITEM");

		if (initModule.equals("all")) {
			moduleCode = "";
			for (Bean dict : dictList) {
				if (StringUtils.isBlank(moduleCode)) {
					moduleCode = dict.getStr("ITEM_CODE");
				} else {
					moduleCode += "," + dict.getStr("ITEM_CODE");
				}
			}
		}

		if (Strings.isBlank(ctlgPcode)) {

			syncCatalog(moduleCode, "", cmpyCode);
		} else {

			String dept = ctlgPcode.substring(ctlgPcode.indexOf("-") + 1, ctlgPcode.length());

			syncCatalog(moduleCode, dept, cmpyCode);
		}

		return outBean.setOk();
	}

	/**
	 * 同步组织机构到指定目录类型，已存在的目录忽略
	 * 
	 * @param mods
	 *            同步模块 逗哥以逗号隔开
	 * @param odept
	 * @param cmpyCode
	 */
	private void syncCatalog(String mods, String dept, String cmpyCode) {

		String[] modsArg = mods.split(",");

		StopWatch sw = new StopWatch();

		sw.start();
		
		DeptBean deptBean = null;

		if (Strings.isBlank(dept)) {
			
			List<DeptBean> list = OrgMgr.getTopDepts(cmpyCode);
			
			deptBean = list.get(0);

		} else {
			deptBean = OrgMgr.getDept(dept);
		}
		

		for (String mod : modsArg) {

			if (deptBean.getODeptCode().equals(dept) || Strings.isBlank(dept)) {

				syncOdeptCatalog(deptBean, mod, mods);

			} else {

				syncDeptCatalog(deptBean, mod, mods);
			}

			DictMgr.clearCache("TS_CTLG_TREE_" + mod);
		}

		System.out.println(sw.toString());
		sw.stop();

	}

	/**
	 * 同步机构下的目录
	 * 
	 * @param odept
	 *            同步机构
	 * @param mod
	 *            同步模块
	 * @param shareMods
	 *            共享模块
	 */
	private void syncOdeptCatalog(DeptBean odept, String mod, String shareMods) {

		SqlBean sql = new SqlBean();

		sql.andLikeRT("CODE_PATH", odept.getCodePath());

		sql.and("DEPT_TYPE", OrgConstant.DEPT_TYPE_ORG);

		sql.and("S_FLAG", 1);

		List<Bean> odeptList = ServDao.finds(ServMgr.SY_ORG_DEPT, sql);

		if (odeptList == null) {

			return;
		}

		for (Bean bean : odeptList) {

			DeptBean deptBean = new DeptBean(bean);

			syncDeptCatalog(deptBean, mod, shareMods);
		}

	}

	/**
	 * 同步部门下的目录
	 * 
	 * @param dept
	 * @param mod
	 * @param shareMods
	 */
	private void syncDeptCatalog(DeptBean dept, String mod, String shareMods) {

		SqlBean sql = new SqlBean();

		sql.and("ODEPT_CODE", dept.getODeptCode());

		sql.and("S_FLAG", 1);

		StringBuffer sb = new StringBuffer();

		sb.append(" AND NOT EXISTS (SELECT CTLG_CODE FROM ").append(TsConstant.SERV_CTLG_ALL);

		sb.append(" WHERE CTLG_MODULE = ?");

		sb.append(" AND S_ODEPT = ?");

		// sb.append(" AND CTLG_PATH like CONCAT(?,'%')");

		sb.append(" AND CTLG_CODE = ").append(ServMgr.SY_ORG_DEPT).append(".DEPT_CODE");

		sb.append(" AND S_FLAG = ?)");

		sql.appendWhere(sb.toString(), mod, dept.getODeptCode(), 1);

//		StopWatch sw = new StopWatch();
//		sw.start();
//		log.error("---------start------------");
//		log.error(sql.getWhere());
//		log.error(sql.get("_PREVALUES_").toString());

		List<Bean> list = ServDao.finds(ServMgr.SY_ORG_DEPT, sql);

//		log.error("----------stop-----------" + sw.toString());
//		sw.stop();

		if (list == null) {

			list = new ArrayList<Bean>();
		}

		createCatalog(list, mod, shareMods);

	}

	private void createCatalog(List<Bean> list, String mod, String shareMods) {

		List<Bean> addCatalogList = new ArrayList<Bean>();

		for (Bean dept : list) {

			Bean addCatalog = new Bean();

			addCatalog.set("CTLG_MODULE", mod);

			// if (!catlogBean.containsKey(dept.getStr("DEPT_CODE"))) {

			String ctlgCodeH = mod + "-" + dept.getStr("DEPT_CODE");

			String ctlgPCodeH = mod + "-" + dept.getStr("DEPT_PCODE");

			addCatalog.set("CTLG_CODE", dept.getStr("DEPT_CODE")); // 机构编码

			addCatalog.set("CTLG_CODE_H", ctlgCodeH); // 模块+机构编码

			addCatalog.set("CTLG_NAME", dept.getStr("DEPT_NAME"));// 目录名称(部门名称)

			if (StringUtils.isNotBlank(dept.getStr("DEPT_PCODE"))) {

				addCatalog.set("CTLG_PCODE", dept.getStr("DEPT_PCODE")); // 上级机构编码

				addCatalog.set("CTLG_PCODE_H", ctlgPCodeH); // 模块 + 上级机构编码
			}
			addCatalog.set("CTLG_SORT", dept.getStr("DEPT_SORT")); // 机构排序

			String codePathH = "";
			String codePath = dept.getStr("CODE_PATH"); // 机构path

			String[] pathArg = codePath.split("\\^");

			for (String path : pathArg) {
				if (StringUtils.isNotBlank(path)) {
					codePathH += mod + "-" + path + "^";
				}
			}
			addCatalog.set("CTLG_PATH_H", codePathH); // 模块+机构path
			addCatalog.set("CTLG_PATH", codePath); // 机构path
			addCatalog.set("CTLG_SHARE", shareMods); // 共享模块

			addCatalog.set("CTLG_LEVEL", dept.getStr("DEPT_LEVEL"));// 目录层级

			addCatalog.set("CTLG_TYPE", dept.getStr("DEPT_TYPE")); // 目录类型

			addCatalog.set("S_ODEPT", dept.getStr("ODEPT_CODE")); // 所属机构

			addCatalog.set("READ_FLAG", 1); // 只读

			addCatalogList.add(addCatalog);

		}

		if (addCatalogList.size() > 0) {
			ServDao.creates(servId, addCatalogList);
		}
	}

	/**
	 * 获取指定目录类型的所有信息
	 * 
	 * @param ctlgModule
	 *            目录类型
	 * @param odept
	 *            机构id
	 * @return
	 */
	public Bean getCatalogBean(String ctlgModule, List<Bean> list) {
		Bean result = new Bean();

		List<String> dlist = new ArrayList<String>();

		for (Bean dept : list) {

			dlist.add(dept.getStr("DEPT_CODE"));
		}

		SqlBean sql = new SqlBean();
		sql.and("CTLG_MODULE", ctlgModule);
		sql.andIn("CTLG_CODE", dlist.toArray());
		sql.and("S_FLAG", 1);
		List<Bean> ctlgList = ServDao.finds(servId, sql);

		for (Bean ctlg : ctlgList) {

			result.set(ctlg.getStr("CTLG_CODE"), "");
		}
		return result;
	}

	@Override
	protected void beforeSave(ParamBean paramBean) {
		String addCode = paramBean.getStr("CTLG_CODE");
		String addPcode = paramBean.getStr("CTLG_PCODE");
		String addModule = paramBean.getStr("CTLG_MODULE");
		String addShare = paramBean.getStr("CTLG_SHARE");
		String oldShare = "";
		String codePath = "";

		// 上级目录
		Bean parentCtlg = null;

		Bean oldBean = ServDao.find(servId, paramBean.getId());
		// 修改目录
		if (oldBean != null) {
			oldShare = oldBean.getStr("CTLG_SHARE");
			addPcode = oldBean.getStr("CTLG_PCODE");
		} else { // 新增目录
			if (StringUtils.isNotBlank(addCode)) {
				paramBean.set("CTLG_CODE_H", addModule + "-" + addCode);
			}

			SqlBean sql = new SqlBean();
			sql.and("CTLG_CODE", addPcode);
			sql.and("CTLG_MODULE", addModule);
			sql.and("S_FLAG", 1);
			System.out.println(sql.toString());
			// 上级目录
			List<Bean> pctlgList = ServDao.finds(servId, sql);

			if (pctlgList != null && pctlgList.size() == 1) {
				parentCtlg = pctlgList.get(0);
			}

			if (parentCtlg != null) {

				if (StringUtils.isBlank(addModule)) {
					addModule = parentCtlg.getStr("CTLG_MODULE");
					paramBean.set("CTLG_MODULE", addModule);
				}

				if (StringUtils.isBlank(codePath)) {
					String pCodePath = parentCtlg.getStr("CTLG_PATH");
					codePath = pCodePath + addCode + "^";
					paramBean.set("CTLG_PATH", codePath);

					String pathH = getCtlgPathH(addModule, codePath);
					paramBean.set("CTLG_PATH_H", pathH);
				}

				if (StringUtils.isBlank(addShare)) {
					paramBean.set("CTLG_SHARE", addModule);
				}

				paramBean.set("S_ODEPT", parentCtlg.getStr("S_ODEPT"));// 所属机构

				paramBean.set("CTLG_LEVEL", parentCtlg.getInt("CTLG_LEVEL") + 1);
				paramBean.set("CTLG_TYPE", 3);

				paramBean.set("READ_FLAG", 2);
			}
		}

		/**
		 * 创建至其他模块，复制当前目录 至 目标模块
		 */
		if (addShare.length() > oldShare.length()) {

			String[] newArg = addShare.split(",");
			String[] oldArg = oldShare.split(",");
			String[] modArg = {};

			LinkedList<String> modList = new LinkedList<String>();
			for (String newMod : newArg) {
				modList.add(newMod);
			}

			for (String oldMod : oldArg) {
				if (modList.contains(oldMod)) {
					modList.remove(oldMod);
				}
			}

			if (modList.contains(addModule)) {
				modList.remove(addModule);
			}

			if (modList.size() > 0) {
				modArg = modList.toArray(modArg);
			}

			addShareCatalog(modArg, addPcode, fillParamBean(paramBean));
		}
	}

	/**
	 * 创建至其他模块,复制当前目录 至 目标模块
	 * 
	 * @param modArg
	 * @param parentCtlg
	 * @param paramBean
	 */
	private void addShareCatalog(String[] shareMod, String pCode, Bean paramBean) {
		if (shareMod == null || shareMod.length == 0) {
			return;
		}
		for (String modCode : shareMod) {

			SqlBean sql = new SqlBean();
			sql.and("CTLG_CODE", pCode);
			sql.and("CTLG_MODULE", modCode);
			sql.and("S_FLAG", 1);
			// 目标上级目录
			List<Bean> list = ServDao.finds(servId, sql);

			if (list != null) {
				Bean dataBean = new Bean();

				dataBean.set("CTLG_MODULE", modCode);
				dataBean.set("CTLG_NAME", paramBean.getStr("CTLG_NAME"));
				dataBean.set("CTLG_SHARE", paramBean.getStr("CTLG_SHARE"));
				dataBean.set("CTLG_SORT", paramBean.getStr("CTLG_SORT"));
				dataBean.set("CTLG_DESC", paramBean.getStr("CTLG_DESC"));
				dataBean.set("CTLG_CODE", paramBean.getStr("CTLG_CODE"));
				dataBean.set("CTLG_CODE_H", modCode + "-" + paramBean.getStr("CTLG_CODE"));
				dataBean.set("CTLG_PCODE", paramBean.getStr("CTLG_PCODE"));
				dataBean.set("CTLG_PCODE_H", modCode + "-" + paramBean.getStr("CTLG_PCODE"));
				dataBean.set("CTLG_PATH", paramBean.getStr("CTLG_PATH"));
				dataBean.set("CTLG_PATH_H", getCtlgPathH(modCode, paramBean.getStr("CTLG_PATH")));

				dataBean.set("CTLG_LEVEL", paramBean.getStr("CTLG_LEVEL"));
				dataBean.set("CTLG_TYPE", paramBean.getStr("CTLG_TYPE"));

				dataBean.set("S_ODEPT", paramBean.getStr("S_ODEPT"));
				dataBean.set("S_FLAG", 1);
				dataBean.set("READ_FLAG", 2);

				Bean query = new Bean();
				query.set("CTLG_MODULE", modCode);
				query.set("CTLG_CODE", paramBean.getStr("CTLG_CODE"));
				int count = ServDao.count(TsConstant.SERV_CTLG_ALL, query);

				if (count == 0) {
					ServDao.create(servId, dataBean);
					DictMgr.clearCache("TS_CTLG_TREE_" + modCode);
				}
			}
		}
	}

	private Bean fillParamBean(ParamBean paramBean) {
		Bean data = ServDao.find(servId, paramBean.getId());

		if (data != null) {
			if (StringUtils.isNotBlank(paramBean.getStr("CTLG_MODULE"))) {
				data.set("CTLG_MODULE", paramBean.getStr("CTLG_MODULE"));
			}

			if (StringUtils.isNotBlank(paramBean.getStr("CTLG_NAME"))) {
				data.set("CTLG_NAME", paramBean.getStr("CTLG_NAME"));
			}

			if (StringUtils.isNotBlank(paramBean.getStr("CTLG_SHARE"))) {
				data.set("CTLG_SHARE", paramBean.getStr("CTLG_SHARE"));
			}

			if (StringUtils.isNotBlank(paramBean.getStr("CTLG_SORT"))) {
				data.set("CTLG_SORT", paramBean.getStr("CTLG_SORT"));
			}

			if (StringUtils.isNotBlank(paramBean.getStr("CTLG_DESC"))) {
				data.set("CTLG_DESC", paramBean.getStr("CTLG_DESC"));
			}

			if (StringUtils.isNotBlank(paramBean.getStr("CTLG_CODE"))) {
				data.set("CTLG_CODE", paramBean.getStr("CTLG_CODE"));
			}

			if (StringUtils.isNotBlank(paramBean.getStr("CTLG_CODE_H"))) {
				data.set("CTLG_CODE_H", paramBean.getStr("CTLG_CODE_H"));
			}

			if (StringUtils.isNotBlank(paramBean.getStr("CTLG_PCODE"))) {
				data.set("CTLG_PCODE", paramBean.getStr("CTLG_PCODE"));
			}

			if (StringUtils.isNotBlank(paramBean.getStr("CTLG_PCODE_H"))) {
				data.set("CTLG_PCODE_H", paramBean.getStr("CTLG_PCODE_H"));
			}

			if (StringUtils.isNotBlank(paramBean.getStr("CTLG_PATH"))) {
				data.set("CTLG_PATH", paramBean.getStr("CTLG_PATH"));
			}

			if (StringUtils.isNotBlank(paramBean.getStr("CTLG_PATH_H"))) {
				data.set("CTLG_PATH_H", paramBean.getStr("CTLG_PATH_H"));
			}

			if (StringUtils.isNotBlank(paramBean.getStr("S_FLAG"))) {
				data.set("S_FLAG", paramBean.getStr("S_FLAG"));
			}

			if (StringUtils.isNotBlank(paramBean.getStr("READ_FLAG"))) {
				data.set("READ_FLAG", paramBean.getStr("READ_FLAG"));
			}

			if (StringUtils.isNotBlank(paramBean.getStr("S_ATIME"))) {
				data.set("S_ATIME", paramBean.getStr("S_ATIME"));
			}

			if (StringUtils.isNotBlank(paramBean.getStr("ODEPT_CODE"))) {
				data.set("ODEPT_CODE", paramBean.getStr("ODEPT_CODE"));
			}

			if (StringUtils.isNotBlank(paramBean.getStr("S_ODEPT"))) {
				data.set("S_ODEPT", paramBean.getStr("S_ODEPT"));
			}

			if (StringUtils.isNotBlank(paramBean.getStr("S_USER"))) {
				data.set("S_USER", paramBean.getStr("S_USER"));
			}

			if (StringUtils.isNotBlank(paramBean.getStr("CTLG_LEVEL"))) {
				data.set("CTLG_LEVEL", paramBean.getStr("CTLG_LEVEL"));
			}

			if (StringUtils.isNotBlank(paramBean.getStr("CTLG_TYPE"))) {
				data.set("CTLG_TYPE", paramBean.getStr("CTLG_TYPE"));
			}
		} else {
			data = new Bean(paramBean);
		}

		return data;
	}

	private String getCtlgPathH(String mod, String path) {
		String codePathH = "";
		String[] pathArg = path.split("\\^");

		for (String p : pathArg) {
			if (StringUtils.isNotBlank(p)) {
				codePathH += mod + "-" + p + "^";
			}
		}
		return codePathH;
	}

	// 查询前添加查询条件
	protected void beforeQuery(ParamBean paramBean) {
		ParamBean param = new ParamBean();
		String serviceName = paramBean.getServId();
		String ctlgModuleName = serviceName.substring(16);
		param.set("paramBean", paramBean);
		param.set("ctlgModuleName", ctlgModuleName);
		param.set("serviceName", serviceName);
		PvlgUtils.setCtlgPvlgWhere(param);
	}

}
