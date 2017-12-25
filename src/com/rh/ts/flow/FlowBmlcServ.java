package com.rh.ts.flow;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.comm.FileMgr;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.ImpUtils;
import com.rh.core.util.Strings;
import com.rh.ts.pvlg.PvlgUtils;
import com.rh.ts.util.TsConstant;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FlowBmlcServ extends CommonServ {
	//private static String CODE_STR = "codeStr";

	/**
	 * @param paramBean
	 *            paramBean G_ID FILE_ID
	 * @return outBean
	 */
	public OutBean saveFromExcel(ParamBean paramBean) throws IOException, BiffException, WriteException {
		OutBean outBean = new OutBean();
		// 获取前端传递参数
		String nodeId = (String) paramBean.get("NODE_ID"), // 节点id

		fileId = (String) paramBean.get("FILE_ID");// 文件id

		// *获取文件内容
		List<Bean> rowBeanList = ImpUtils.getDataFromXls(fileId);

		List<String> codeList = new ArrayList<String>();// 避免重复添加数据

		// 获取流程id（xmId）
		Bean bmGroupBean = ServDao.find(TsConstant.SERV_WFS_NODE_APPLY, nodeId);
		String wfsId = bmGroupBean.getStr("WFS_ID");

		List<Bean> beans = new ArrayList<Bean>();
		for (int index = 1; index < rowBeanList.size(); index++) {
			Bean rowBean = rowBeanList.get(index);
			String colCode = rowBean.getStr(ImpUtils.COL_NAME + "1");
			String colDeptCodes = rowBean.getStr(ImpUtils.COL_NAME + "3");
			Bean userBean = ImpUtils.getUserBeanByString(colCode);
			if (userBean == null) {
				rowBean.set(ImpUtils.ERROR_NAME, "找不到用户");
				continue;
			}

			String code = userBean.getStr("USER_CODE"), name = userBean.getStr("USER_NAME"),
					userDeptCode = userBean.getStr("DEPT_CODE");
			if (codeList.contains(code)) {
				// 已包含 continue ：避免重复添加数据
				rowBean.set(ImpUtils.ERROR_NAME, "重复数据：" + code);
				continue;
			}

			Bean bean = new Bean();
			bean.set("NODE_ID", nodeId);
			bean.set("WFS_ID", wfsId);

			bean.set("SHR_USERCODE", code);// 审核人资源编码
			bean.set("BMSHLC_DEPT", userDeptCode);// 审核人机构

			// bean.set("G_TYPE", 1);//选取类型 1人员

			if (ServDao.count(TsConstant.SERV_WFS_BMSHLC, bean) <= 0) {
				// 先查询避免重复添加col3=总行/广东分行营业部,总行/福建分行,
				bean.set("BMSHLC_SHR", name);
				String[] colDeptCode = colDeptCodes.split(",");
				String deptcode = "";
				for (int i = 0; i < colDeptCode.length; i++) {
					String getDept = colDeptCode[i];
					String[] colDeptNAME = getDept.split("/");
					String deptName = colDeptNAME[1];// 名称
					String where = "AND DEPT_NAME='" + deptName + "'";
					List<Bean> deptBean = ServDao.finds("TS_ORG_DEPT", where);
					if (deptBean != null && !deptBean.isEmpty()) {
						Bean deptCodeBean = deptBean.get(0);

						deptcode += deptCodeBean.getStr("DEPT_CODE");
						deptcode += ",";
					} else {
						rowBean.set(ImpUtils.ERROR_NAME, "找不到审核机构名称对应的编码");
						continue;
					}

				}
				bean.set("DEPT_CODE", deptcode.substring(0, deptcode.length() - 1));
				beans.add(bean);
				codeList.add(code);
			} else {
				rowBean.set(ImpUtils.ERROR_NAME, "重复数据：" + code);
			}
		}
		ServDao.creates(TsConstant.SERV_WFS_BMSHLC, beans);

		// 在excel中设置失败信息
		String errorFileId = ImpUtils.saveErrorAndReturnErrorFile(fileId, rowBeanList);
		outBean.set("FILE_ID", errorFileId);
		return outBean.setCount(codeList.size())
				.setOk("成功导入" + codeList.size() + "条," + "失败条数：" + (rowBeanList.size() - codeList.size() - 1) + "条");
	}

}
