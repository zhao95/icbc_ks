package com.rh.ts.comm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.util.DateUtils;

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
		List<Bean> addCatalogList = new ArrayList<Bean>();

		String[] moduleArg = ctlgModules.split(",");

		// 获取组织机构
		SqlBean sql = new SqlBean();
		sql.and("CMPY_CODE", cmpyCode);
		sql.and("S_FLAG", 1);
		List<Bean> deptList = ServDao.finds(ServMgr.SY_ORG_DEPT_ALL, sql);

		addCatalogList = new ArrayList<Bean>();

		for (String mod : moduleArg) {
			Bean catlogBean = getCatalogBean(mod);

			if (deptList != null) {

				for (Bean bean : deptList) {
					Bean addCatalog = new Bean();
					addCatalog.set("CTLG_MODULE", mod);

					if (!catlogBean.containsKey(bean.getStr("DEPT_CODE"))) {

						String ctlgCodeH = mod + "-" + bean.getStr("DEPT_CODE");

						String ctlgPCodeH = mod + "-"
								+ bean.getStr("DEPT_PCODE");

						addCatalog.set("CTLG_CODE", bean.getStr("DEPT_CODE"));

						addCatalog.set("CTLG_CODE_H", ctlgCodeH);

						addCatalog.set("CTLG_NAME", bean.getStr("DEPT_NAME"));

						if (StringUtils.isNotBlank(bean.getStr("DEPT_PCODE"))) {

							addCatalog.set("CTLG_PCODE",
									bean.getStr("DEPT_PCODE"));

							addCatalog.set("CTLG_PCODE_H", ctlgPCodeH);
						}
						addCatalog.set("CTLG_SORT", bean.getStr("DEPT_SORT"));

						String codePathH = "";
						String codePath = bean.getStr("CODE_PATH");

						String[] pathArg = codePath.split("\\^");

						for (String path : pathArg) {
							if (StringUtils.isNotBlank(path)) {
								codePathH += mod + "-" + path + "^";
							}
						}
						addCatalog.set("CTLG_PATH_H", codePathH);
						addCatalog.set("CTLG_PATH", codePath);
						addCatalog.set("CTLG_SHARE", ctlgModules);

						addCatalog.set("S_FLAG", 1);
						addCatalog.set("READ_FLAG", 1);
						addCatalog.set("S_ATIME", DateUtils.getDatetime());
						addCatalog.set("S_ODEPT", bean.getStr("ODEPT_CODE"));
						addCatalog.set("S_USER", Context.getUserBean()
								.getUser());
						addCatalogList.add(addCatalog);
					}
				}
			}
		}
		ServDao.creates(servId, addCatalogList);
	}

	/**
	 * 获取指定目录类型的所有信息
	 * 
	 * @param ctlgModule
	 *            目录类型
	 * @return
	 */
	private Bean getCatalogBean(String ctlgModule) {
		Bean result = new Bean();

		ParamBean paramBean = new ParamBean();
		paramBean.set("CTLG_MODULE", ctlgModule);
		paramBean.set("S_FLAG", 1);
		List<Bean> ctlgList = ServDao.finds(servId, paramBean);
		for (Bean ctlg : ctlgList) {
			result.set(ctlg.getStr("CTLG_CODE"), ctlg);
		}
		return result;
	}

	@Override
	protected void beforeSave(ParamBean paramBean) {
		String addCode = paramBean.getStr("CTLG_CODE");
		String addPcode = paramBean.getStr("CTLG_PCODE");
		String addPcodeH = paramBean.getStr("CTLG_PCODE_H");
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
	 * @param modArg
	 * @param parentCtlg
	 * @param paramBean
	 */
	private void addShareCatalog(String[] shareMod, String pCode,Bean paramBean) {
		if (shareMod == null || shareMod.length == 0) {
			return;
		}
		for (String modCode : shareMod) {

			SqlBean sql = new SqlBean();
			sql.and("CTLG_CODE", pCode);
			sql.and("CTLG_MODULE", modCode);
			sql.and("S_FLAG", 1);
			//目标上级目录
			List<Bean> list = ServDao.finds(servId, sql);

			if (list != null) {
				Bean dataBean = new Bean();
				
				dataBean.set("CTLG_MODULE", modCode);
				dataBean.set("CTLG_NAME", paramBean.getStr("CTLG_NAME"));
				dataBean.set("CTLG_SHARE", paramBean.getStr("CTLG_SHARE"));
				dataBean.set("CTLG_SORT", paramBean.getStr("CTLG_SORT"));
				dataBean.set("CTLG_DESC", paramBean.getStr("CTLG_DESC"));
				dataBean.set("CTLG_CODE", paramBean.getStr("CTLG_CODE"));
				dataBean.set("CTLG_CODE_H", modCode+"-"+paramBean.getStr("CTLG_CODE"));
				dataBean.set("CTLG_PCODE", paramBean.getStr("CTLG_PCODE"));
				dataBean.set("CTLG_PCODE_H", modCode+"-"+paramBean.getStr("CTLG_PCODE"));
				dataBean.set("CTLG_PATH", paramBean.getStr("CTLG_PATH"));
				dataBean.set("CTLG_PATH_H", getCtlgPathH(modCode,paramBean.getStr("CTLG_PATH")));
				
				dataBean.set("S_FLAG", 1);
				dataBean.set("READ_FLAG", 2);
				
				ServDao.create(servId, dataBean);
			}
		}
	}
	
	private Bean fillParamBean(ParamBean paramBean){
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
