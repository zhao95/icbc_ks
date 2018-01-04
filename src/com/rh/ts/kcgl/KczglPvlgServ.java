package com.rh.ts.kcgl;

import java.util.LinkedHashMap;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.util.ExportExcel;
import com.rh.core.serv.util.ServUtils;
import com.rh.ts.pvlg.PvlgUtils;

public class KczglPvlgServ extends CommonServ {
    private static final int ONETIME_EXP_NUM = 20000;
    // 查询前添加查询条件
    protected void beforeQuery(ParamBean paramBean) {
	ParamBean param = new ParamBean();
	String ctlgModuleName = "EXAM_GROUP";
	param.set("paramBean", paramBean);
	param.set("ctlgModuleName", ctlgModuleName);
	param.set("serviceName", paramBean.getServId());
	PvlgUtils.setOrgPvlgWhere(param);
    }

    public OutBean exp(ParamBean paramBean) {
	String servId = paramBean.getServId();
	ServDefBean serv = ServUtils.getServDef(servId);
	long count = 0;
	long times = 0;
	paramBean.setQueryPageShowNum(ONETIME_EXP_NUM); // 设置每页最大导出数据量
	beforeExp(paramBean); // 执行监听方法
	if (paramBean.getId().length() > 0) { // 支持指定记录的导出（支持多选）
	    String searchWhere = " and " + serv.getPKey() + " in ('" + paramBean.getId().replaceAll(",", "','") + "')";
	    paramBean.setQuerySearchWhere(searchWhere);
	}
	ExportExcel expExcel = new ExportExcel(serv);
	try {
	    OutBean outBean = queryExp(paramBean);
	    count = outBean.getCount();
	    // 导出第一次查询数据
	    paramBean.setQueryPageNowPage(1); // 导出当前第几页
	    afterExp(paramBean, outBean); // 执行导出查询后扩展方法
	    LinkedHashMap<String, Bean> cols = outBean.getCols();
	    cols.remove("BUTTONS");
	    expExcel.createHeader(cols);
	    expExcel.appendData(outBean.getDataList(), paramBean);

	    // 存在多页数据
	    if (ONETIME_EXP_NUM < count) {
		times = count / ONETIME_EXP_NUM;
		// 如果获取的是整页数据
		if (ONETIME_EXP_NUM * times == count && count != 0) {
		    times = times - 1;
		}
		for (int i = 1; i <= times; i++) {
		    paramBean.setQueryPageNowPage(i + 1); // 导出当前第几页
		    OutBean out = query(paramBean);
		    afterExp(paramBean, out); // 执行导出查询后扩展方法
		    expExcel.appendData(out.getDataList(), paramBean);
		}
	    }
	    expExcel.addSumRow();
	} catch (Exception e) {
	    log.error("导出Excel文件异常" + e.getMessage(), e);
	} finally {
	    expExcel.close();
	}
	return new OutBean().setOk();
    }
}
