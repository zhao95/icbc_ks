package com.rh.ts.orguser;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.Strings;
import com.rh.ts.pvlg.PvlgUtils;

public class TsUserServ extends CommonServ {
	// 查询前添加查询条件

	protected void beforeQuery(ParamBean paramBean) {
		ParamBean param = new ParamBean();
		param.set("paramBean", paramBean);
		param.set("fieldName", "DEPT_CODE");
		param.set("serviceName", paramBean.getServId());
		PvlgUtils.setOrgPvlgWhere(param);
	}

	protected void afterQuery(ParamBean paramBean, OutBean outBean) {

		List<Bean> dataList = outBean.getDataList();

		for (Bean bean : dataList) {

			String deptCode = bean.getStr("DEPT_CODE");

			if (Strings.isBlank(deptCode)) {

				String userCode = bean.getStr("USER_CODE");
				UserBean userBean = UserMgr.getUser(userCode);
				if (userBean == null || userBean.isEmpty()) {
					continue;
				}
				deptCode = userBean.getDeptCode();
			}

			DeptBean oeptBean = null;
			DeptBean pOdeptBean = null;
			DeptBean ppODeptBean = null;

			try {
				DeptBean deptBean = OrgMgr.getDept(deptCode);
				oeptBean = deptBean.getODeptBean();
				pOdeptBean = oeptBean.getParentDeptBean().getODeptBean();
				ppODeptBean = pOdeptBean.getParentDeptBean().getODeptBean();
			} catch (Exception e) {
			}

			String oDC1 = "";
			String oDN1 = "";
			String oDC2 = "";
			String oDN2 = "";
			String oDC3 = "";
			String oDN3 = "";

			if (oeptBean != null && oeptBean.getLevel() == 1) { // 当前用户是总行

				oDC1 = oeptBean.getCode();
				oDN1 = oeptBean.getName();

			} else if (oeptBean != null && oeptBean.getLevel() == 2) { // 当前用户是一级机构

				oDC1 = oeptBean.getCode();
				oDN1 = oeptBean.getName();

			} else if (oeptBean != null && oeptBean.getLevel() == 3) { // 当前用户是二级机构

				if (pOdeptBean != null && !pOdeptBean.isEmpty()) {
					oDC1 = pOdeptBean.getCode();
					oDN1 = pOdeptBean.getName();
				}
				oDC2 = oeptBean.getCode();
				oDN2 = oeptBean.getName();

			} else if (oeptBean != null && oeptBean.getLevel() == 4) { // 当前用户是三级机构

				if (ppODeptBean != null && !ppODeptBean.isEmpty()) {
					oDC1 = ppODeptBean.getCode();
					oDN1 = ppODeptBean.getName();
				}

				if (pOdeptBean != null && !pOdeptBean.isEmpty()) {
					oDC2 = pOdeptBean.getCode();
					oDN2 = pOdeptBean.getName();
				}
				oDC3 = oeptBean.getCode();
				oDN3 = oeptBean.getName();

			} else { // 总行
				oDC1 = oeptBean.getCode();
				oDN1 = oeptBean.getName();
				// oDC2 = oeptBean.getCode();
				// oDN2 = oeptBean.getName();
				// oDC3 = oeptBean.getCode();
				// oDN3 = oeptBean.getName();
			}

			bean.set("ODEPT_CODE_LV1", oDC1);
			bean.set("ODEPT_NAME_LV1", oDN1);
			bean.set("ODEPT_CODE_LV2", oDC2);
			bean.set("ODEPT_NAME_LV2", oDN2);
			bean.set("ODEPT_CODE_LV3", oDC3);
			bean.set("ODEPT_NAME_LV3", oDN3);
		}

		outBean.setData(dataList);
	}
}
