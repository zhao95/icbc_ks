package com.rh.ts.comm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.org.DeptBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.util.OrgConstant;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.util.Strings;
import com.rh.ts.util.TsConstant;

public class CtlgSyncUtils {
	
	private static Log log = LogFactory.getLog(CtlgSyncUtils.class);
	
	/**
	 * 同步组织机构到指定目录类型，已存在的目录忽略
	 * 
	 * @param mods
	 *            同步模块 逗哥以逗号隔开
	 * @param odept
	 * @param cmpyCode
	 */
	public static void sync(String mods, String dept, String cmpyCode) {

		String[] modsArg = mods.split(",");

		DeptBean deptBean = null;

		if (Strings.isBlank(dept)) {

			List<DeptBean> list = OrgMgr.getTopDepts(cmpyCode);

			deptBean = list.get(0);

		} else {
			deptBean = OrgMgr.getDept(dept);
		}

		for (String mod : modsArg) {
			
			StopWatch sw = new StopWatch();
			sw.start();
			log.error("---------start------------" + mod);

			if (deptBean.getODeptCode().equals(dept) || Strings.isBlank(dept)) {

				syncOdeptCatalog(deptBean, mod, mods);

			} else {

				syncDeptCatalog(deptBean, mod, mods);
			}

			DictMgr.clearCache("TS_CTLG_TREE_" + mod);
			
			log.error("----------stop-----------" + mod + "-" + sw.toString());
			sw.stop();
		}

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
	public static void syncOdeptCatalog(DeptBean odept, String mod, String shareMods) {

		List<Bean> odeptList = null;

		SqlBean sql1 = new SqlBean();

		sql1.andLikeRT("CODE_PATH", odept.getCodePath());

		sql1.and("DEPT_TYPE", OrgConstant.DEPT_TYPE_ORG);

		sql1.and("S_FLAG", 1);

		odeptList = ServDao.finds(ServMgr.SY_ORG_DEPT, sql1);

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
	public static void syncDeptCatalog(DeptBean dept, String mod, String shareMods) {

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

		List<Bean> list = ServDao.finds(ServMgr.SY_ORG_DEPT, sql);

		if (list == null) {

			list = new ArrayList<Bean>();
		}

		createCatalog(list, mod, shareMods);

	}

	public static void createCatalog(List<Bean> list, String mod, String shareMods) {

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
			ServDao.creates(TsConstant.SERV_CTLG_ALL, addCatalogList);
		}
	}

}
