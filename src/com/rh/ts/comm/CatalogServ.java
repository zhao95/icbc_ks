package com.rh.ts.comm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.util.Strings;
import com.rh.ts.pvlg.PvlgUtils;
import com.rh.ts.util.TsConstant;

public class CatalogServ extends CommonServ {

//	private static Log log = LogFactory.getLog(CatalogServ.class);

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
		Bean dictBean = DictMgr.getDict(TsConstant.DICT_CTLG_MOD);
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

			CtlgSyncUtils.sync(moduleCode, "", cmpyCode);
		} else {

			String dept = ctlgPcode.substring(ctlgPcode.indexOf("-") + 1, ctlgPcode.length());

			CtlgSyncUtils.sync(moduleCode, dept, cmpyCode);
		}

		return outBean.setOk();
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
		List<Bean> ctlgList = ServDao.finds(TsConstant.SERV_CTLG_ALL, sql);

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

		Bean oldBean = ServDao.find(TsConstant.SERV_CTLG_ALL, paramBean.getId());
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
			List<Bean> pctlgList = ServDao.finds(TsConstant.SERV_CTLG_ALL, sql);

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
			List<Bean> list = ServDao.finds(TsConstant.SERV_CTLG_ALL, sql);

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
					ServDao.create(TsConstant.SERV_CTLG_ALL, dataBean);
					DictMgr.clearCache("TS_CTLG_TREE_" + modCode);
				}
			}
		}
	}

	private Bean fillParamBean(ParamBean paramBean) {
		Bean data = ServDao.find(TsConstant.SERV_CTLG_ALL, paramBean.getId());

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
