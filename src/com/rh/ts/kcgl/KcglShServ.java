package com.rh.ts.kcgl;

import java.util.LinkedHashMap;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.util.ExportExcel;
import com.rh.core.serv.util.ServUtils;

public class KcglShServ extends CommonServ {
    private static final int ONETIME_EXP_NUM = 20000;

    protected void beforeQuery(ParamBean paramBean) {
//	无权限
//	extParams={USER_PVLG={upd=0}}
//	有总行考场审核人员权限
//	extParams={USER_PVLG={upd={ROLE_DCODE=, ROLE_ORG_LV=2}}}
//	一级分行考场审核人员权限
//	extParams={USER_PVLG={upd={ROLE_DCODE=, ROLE_ORG_LV=3}}}
//	有总行考场审核人员权限和一级分行考场审核人员权限
//	extParams={USER_PVLG={upd={ROLE_DCODE=, ROLE_ORG_LV=2,3}}}
	Bean pvlgBean = paramBean.getBean("extParams").getBean("USER_PVLG");
	UserBean userBean = Context.getUserBean();
	if(userBean.getLoginName().equals("admin")){
	}else if(pvlgBean.getStr("upd").equals("0")){
	    paramBean.setQueryExtWhere(" and 1=2");
	}else{
	    String roleOrgLv = pvlgBean.getBean("upd").getStr("ROLE_ORG_LV");
	    String roleDCode = pvlgBean.getBean("upd").getStr("ROLE_DCODE");
	    
	    StringBuilder sb1 = new StringBuilder(); 
	    if(roleDCode != ""){
		String roleDCodeStr = roleDCode.replace(",", "','");
		sb1.append("KC_ODEPTCODE in('"+roleDCodeStr+"')");
	    }
	    
	    StringBuilder sb2 = new StringBuilder();
	    if(roleOrgLv.indexOf("2") != -1){
		if(roleOrgLv.length() > 2){
		    sb2.append(" (odept_level in (1," + roleOrgLv + ") or KC_STATE2 = 1)");
		}else{
		    sb2.append(" (odept_level < 3 or KC_STATE2 = 1)");
		}
	    }else if(!roleOrgLv.isEmpty()){
		sb2.append("odept_level in (1," + roleOrgLv + ")");
	    }
	    
	    if (sb1.length() > 0 && sb2.length() > 0) {
		paramBean.setQueryExtWhere(" and ("+sb1.toString()+") or ("+sb2.toString()+")");
	    }else if(sb1.length() > 0 && sb2.length() == 0){
		paramBean.setQueryExtWhere(" and "+sb1.toString());
	    }else if(sb1.length() == 0 && sb2.length() > 0){
		paramBean.setQueryExtWhere(" and "+sb2.toString());
	    }else{
		paramBean.setQueryExtWhere(" and 1=2");
	    }
	}
    }

    /**
     * 考场管理审核 通过更新相应信息 KC_STATE=5, _PK_=1k3hWfgP92BE6EV5P2RK,
     * 
     * @param paramBean
     * @return
     */
    public OutBean updateShInfo(ParamBean paramBean) {
	String kcId = paramBean.getStr("kcId");
	String pkCode = paramBean.getStr("updateId");
	// 主单
	List<Bean> list = ServDao.finds("TS_KCGL_UPDATE_MX", "and UPDATE_ID = '" + pkCode + "'");
	if (list.size() > 0) {
	    Bean dataBean = new Bean();
	    dataBean.setId(kcId);
	    for (int i = 0; i < list.size(); i++) {
		String mxCol = list.get(i).getStr("MX_COL");
		String mxData = list.get(i).getStr("MX_DATA");
		String mxData2 = list.get(i).getStr("MX_DATA2");
		String mxData3 = list.get(i).getStr("MX_DATA3");
		String mxData4 = list.get(i).getStr("MX_DATA4");
		if (mxCol.equals("KC_ODEPTCODE")) {
		    dataBean.set("KC_ODEPTCODE", mxData2);
		    dataBean.set("KC_ODEPTNAME", mxData3);
		} else if (mxCol.equals("KC_LEVEL")) {
		    dataBean.set(mxCol, mxData4);
		} else {
		    dataBean.set(mxCol, mxData);
		}
	    }
	    ServDao.save("TS_KCGL", dataBean);
	}

	// 相关子表
	String[] tables1 = { "TS_KCGL_UPDATE_GLY", "TS_KCGL_UPDATE_JKIP", "TS_KCGL_UPDATE_IPSCOPE",
		 "TS_KCGL_UPDATE_GLJG", "TS_KCGL_UPDATE_ZWDYB" };//"TS_KCGL_UPDATE_IPZWH",
	String[] tables2 = { "TS_KCGL_GLY", "TS_KCGL_JKIP", "TS_KCGL_IPSCOPE",  "TS_KCGL_GLJG",
		"TS_KCGL_ZWDYB" };//"TS_KCGL_IPZWH",
	for (int i = 0; i < tables1.length; i++) {
	    List<Bean> listTmp = ServDao.finds(tables1[i], "and UPDATE_ID = '" + pkCode + "'");
	    for (int j = 0; j < listTmp.size(); j++) {
		Bean dataBean = listTmp.get(j);
		String actionCode = "";
		switch (i) {
		case 0:
		    actionCode = "GLY_ACTION";
		    break;
		case 1:
		    actionCode = "JKIP_ACTION";
		    break;
		case 2:
		    actionCode = "IPS_ACTION";
		    break;
//		case 3:
//		    actionCode = "IPZ_ACTION";
//		    break;
		case 3:
		    actionCode = "JG_ACTION";
		    break;
		case 4:
		    actionCode = "ZW_ACTION";
		    break;
		}
		String action = listTmp.get(j).getStr(actionCode);
		if (action.equals("add")) {
		    dataBean.remove(actionCode);
		    dataBean.remove("UPDATE_ID");
		    dataBean.set("KC_ID", kcId);
		    dataBean.remove("_PK_");
		    ServDao.save(tables2[i], dataBean);
		} else if(action.equals("update")){
		    dataBean.remove(actionCode);
		    dataBean.remove("UPDATE_ID");
		    dataBean.set("KC_ID", kcId);
		    dataBean.setId(dataBean.getStr("ROOT_ID"));
		    dataBean.remove("ROOT_ID");
		    ServDao.save(tables2[i], dataBean);
		}else if (action.equals("delete")) {
		    ServDao.delete(tables2[i], dataBean.getStr("ROOT_ID"));
		}
	    }
	}

	OutBean outBean = new OutBean();
	outBean.setOk();
	return outBean;
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
