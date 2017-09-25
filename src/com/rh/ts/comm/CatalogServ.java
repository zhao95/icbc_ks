package com.rh.ts.comm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.dict.DictMgr;
import com.rh.ts.util.TsConstant;

public class CatalogServ extends CommonServ {

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

		// String odept = paramBean.getStr("ODEPT_CODE");
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

		syncCatalog(moduleCode, cmpyCode);

		return outBean.setOk();
	}

	/**
	 * 同步组织机构到指定目录类型，已存在的目录忽略
	 * 
	 * @param ctlgModules
	 *            同步的目录类型 多个类型逗号隔开
	 * @param cmpyCode
	 *            公司编码
	 */
	private void syncCatalog(String ctlgModules, String cmpyCode) {

		String[] moduleArg = ctlgModules.split(",");

		// 获取机构
		SqlBean sql = new SqlBean();
		sql.and("CMPY_CODE", cmpyCode);
		sql.and("DEPT_TYPE", 2); // 2 机构
		sql.and("S_FLAG", 1);
		List<Bean> deptList = ServDao.finds(ServMgr.SY_ORG_DEPT_ALL, sql);

		if (deptList == null) {
			return;
		}

		StopWatch sw = new StopWatch();
		sw.start();

		for (String mod : moduleArg) {

			for (Bean bean : deptList) {

				syncCatalog(bean, mod, ctlgModules);
			}

		}

		System.out.println(sw.toString());
		sw.stop();

	}

	private void syncCatalog(Bean odept, String mod, String ctlgModules) {

		List<Bean> addCatalogList = new ArrayList<Bean>();

		SqlBean sql = new SqlBean();

		sql.and("ODEPT_CODE", odept.getStr("DEPT_CODE"));

//		sql.and("DEPT_TYPE", OrgConstant.DEPT_TYPE_DEPT);

		sql.and("S_FLAG", 1);
		
		StringBuffer sb = new StringBuffer();
		
		sb.append(" AND NOT EXISTS (SELECT CTLG_CODE FROM ").append(TsConstant.SERV_CTLG_ALL);
		
		sb.append(" WHERE CTLG_MODULE = ? AND S_ODEPT = ?");
		
		sb.append(" AND CTLG_CODE = ").append(ServMgr.SY_ORG_DEPT).append(".DEPT_CODE");
		
		sb.append(" AND S_FLAG = ?)");
		
		sql.appendWhere( sb.toString(), mod,odept.getStr("DEPT_CODE"),1);

		List<Bean> list = ServDao.finds(ServMgr.SY_ORG_DEPT, sql);

		if (list == null) {
			list = new ArrayList<Bean>();
		}

//		list.add(odept);

//		Bean catlogBean = getCatalogBean(mod, list);

		for (Bean dept : list) {

			Bean addCatalog = new Bean();

			addCatalog.set("CTLG_MODULE", mod);

//			if (!catlogBean.containsKey(dept.getStr("DEPT_CODE"))) {

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
				addCatalog.set("CTLG_SHARE", ctlgModules); // 共享模块

				addCatalog.set("CTLG_LEVEL", dept.getStr("DEPT_LEVEL"));// 目录层级

				addCatalog.set("CTLG_TYPE", dept.getStr("DEPT_TYPE")); // 目录类型
				
				addCatalog.set("S_ODEPT", dept.getStr("ODEPT_CODE")); // 所属机构

				addCatalog.set("READ_FLAG", 1); // 只读

				addCatalogList.add(addCatalog);
//			}

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
	private Bean getCatalogBean(String ctlgModule, List<Bean> list) {
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
				
				dataBean.set("S_FLAG", 1);
				dataBean.set("READ_FLAG", 2);

				ServDao.create(servId, dataBean);
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

}
