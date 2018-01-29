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
	    
	    String myParh = userBean.getODeptCodePath();
	    String myOdept = userBean.getODeptCode();
	    
	    Bean levelBean = ServDao.find("TS_ODEPT_LEVEL_V", new Bean().set("ODEPT_CODE", myOdept));
	    int myLevel = levelBean.getInt("DEPT_LEVEL");
	    
	    StringBuilder sb1 = new StringBuilder(); 
	    if(!roleOrgLv.isEmpty()){
		roleOrgLv = roleOrgLv.substring(0, 1);
		int lev1 = Integer.parseInt(roleOrgLv);
		if(lev1 <= myLevel){
		    String levelOdept = myParh.split("\\^")[lev1];
		    roleDCode = roleDCode + "," + levelOdept;
		}
	    }
	    
	    String[] roleArr = roleDCode.split(",");
	    for (int i = 0; i < roleArr.length; i++) {
		if (roleArr[i].isEmpty()) {
		    continue;
		}
		sb1.append("locate('" + roleArr[i] + "',ODEPT_PATH)");
		if (i < roleArr.length - 1) {
		    sb1.append(" or ");
		}
	    }
	    
	    if(roleDCode.indexOf("0010100000") > -1 || roleDCode.indexOf("0010100500") > -1){
		sb1.append(" or KC_STATE2 = 1 ");
	    }
	    
	    if(sb1.length() > 0 ){
		paramBean.setQueryExtWhere(" and ("+sb1.toString()+")");
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
	    //校验设备数
	    boolean maxFlag = dataBean.contains("KC_MAX");
	    boolean goodFlag = dataBean.contains("KC_GOOD");
	    
	    if(maxFlag || goodFlag){
		int kcMax = dataBean.getInt("KC_MAX");
		int kcGood = dataBean.getInt("KC_GOOD");
		Bean kcBean = ServDao.find("TS_KCGL", kcId);

		if (!maxFlag) {
		    kcMax = kcBean.getInt("KC_MAX");
		}
		if (!goodFlag) {
		    kcGood = kcBean.getInt("KC_GOOD");
		}
		if (kcMax >= kcGood) {
		    int zwNum = ServDao.count("TS_KCGL_ZWDYB", new ParamBean().setWhere("and kc_id = '"+kcId+"'"));
		    if(kcMax >= zwNum){
			ServDao.save("TS_KCGL", dataBean);
		    }
		}
	    }else{
		ServDao.save("TS_KCGL", dataBean);
	    }
	    
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
		    //座位号校验
		    boolean sflag = true;
		    if(tables1[i].equals("TS_KCGL_UPDATE_ZWDYB")){
			List<Bean> ipScopeList = ServDao.finds("TS_KCGL_IPSCOPE", "and kc_id = '" + kcId + "'");
			String IPStr = dataBean.getStr("ZW_IP");
			int c0 = Integer.parseInt(IPStr.split(".")[0]);
			int c1 = Integer.parseInt(IPStr.split(".")[1]);
			int c2 = Integer.parseInt(IPStr.split(".")[2]);
			int c3 = Integer.parseInt(IPStr.split(".")[3]);
			//如果 IPStr 超出IP段范围 则 sflag = false
			boolean tmpFlag = false;
			for (Bean bean : ipScopeList) {
			    String tmpScope = bean.getStr("IPS_SCOPE");
			    String a1 = tmpScope.split("-")[0];
			    String a2 = tmpScope.split("-")[1];
			    int b1_0 = Integer.parseInt(a1.split(".")[0]);
			    int b1_1 = Integer.parseInt(a1.split(".")[1]);
			    int b1_2 = Integer.parseInt(a1.split(".")[2]);
			    int b1_3 = Integer.parseInt(a1.split(".")[3]);
			    int b2_3 = Integer.parseInt(a2.split(".")[3]);
			    
			    if(b1_0 == c0 && b1_1 == c1 && b1_2 == c2){
				if(c3 >= b1_3 && c3 <= b2_3){
				    tmpFlag = true;
				    break;
				}
			    }
			}
			if(!tmpFlag){
			    sflag = false;
			}
		    }
		   
		    if(sflag){
			dataBean.remove(actionCode);
			dataBean.remove("UPDATE_ID");
			dataBean.set("KC_ID", kcId);
			dataBean.remove("_PK_");
			if (tables1[i].equals("TS_KCGL_UPDATE_ZWDYB")) {
			    String zwh = dataBean.getStr("ZW_ZWH_XT");
			    String IPStr = dataBean.getStr("ZW_IP");
			    int num_zwh = ServDao.count("TS_KCGL_ZWDYB", new ParamBean()
				    .setWhere("and kc_id = '" + kcId + "' and ZW_ZWH_XT ='" + zwh + "'"));
			    int num_ip = ServDao.count("TS_KCGL_ZWDYB",
				    new ParamBean().setWhere("and kc_id = '" + kcId + "' and ZW_IP ='" + IPStr + "'"));
			    if (num_zwh > 0 || num_ip > 0) {
				continue;
			    }
			}
			ServDao.save(tables2[i], dataBean);
		    }
		} else if(action.equals("update")){
		    boolean sflag = true;
		    //IP段校验
		    if(tables1[i].equals("TS_KCGL_UPDATE_IPSCOPE")){
			String childId = dataBean.getStr("ROOT_ID");
			List<Bean> ipScopeList = ServDao.finds("TS_KCGL_IPSCOPE", "and kc_id = '" + kcId + "' and IPS_ID != '"+childId+"'");
			ipScopeList.add(dataBean);
			List<Bean> zwList = ServDao.finds("TS_KCGL_ZWDYB", "and kc_id = '" + kcId + "'");
			for (Bean bean : zwList) {
			    String IPStr = bean.getStr("ZW_IP");
			    int c0 = Integer.parseInt(IPStr.split(".")[0]);
			    int c1 = Integer.parseInt(IPStr.split(".")[1]);
			    int c2 = Integer.parseInt(IPStr.split(".")[2]);
			    int c3 = Integer.parseInt(IPStr.split(".")[3]);
			    boolean tmpFlag = false;
			    for (Bean bean2 : ipScopeList) {
				String tmpScope = bean2.getStr("IPS_SCOPE");
				String a1 = tmpScope.split("-")[0];
				String a2 = tmpScope.split("-")[1];
				int b1_0 = Integer.parseInt(a1.split(".")[0]);
				int b1_1 = Integer.parseInt(a1.split(".")[1]);
				int b1_2 = Integer.parseInt(a1.split(".")[2]);
				int b1_3 = Integer.parseInt(a1.split(".")[3]);
				int b2_3 = Integer.parseInt(a2.split(".")[3]);

				if (b1_0 == c0 && b1_1 == c1 && b1_2 == c2) {
				    if (c3 >= b1_3 && c3 <= b2_3) {
					tmpFlag = true;
					break;
				    }
				}
			    }
			    if(!tmpFlag){
				sflag = false;
			    }
			}
			
		    }
		    //座位号校验
		    if(tables1[i].equals("TS_KCGL_UPDATE_ZWDYB")){
			List<Bean> ipScopeList = ServDao.finds("TS_KCGL_IPSCOPE", "and kc_id = '" + kcId + "'");
			String IPStr = dataBean.getStr("ZW_IP");
			int c0 = Integer.parseInt(IPStr.split(".")[0]);
			int c1 = Integer.parseInt(IPStr.split(".")[1]);
			int c2 = Integer.parseInt(IPStr.split(".")[2]);
			int c3 = Integer.parseInt(IPStr.split(".")[3]);
			//如果 IPStr 超出IP段范围 则 sflag = false
			boolean tmpFlag = false;
			for (Bean bean : ipScopeList) {
			    String tmpScope = bean.getStr("IPS_SCOPE");
			    String a1 = tmpScope.split("-")[0];
			    String a2 = tmpScope.split("-")[1];
			    int b1_0 = Integer.parseInt(a1.split(".")[0]);
			    int b1_1 = Integer.parseInt(a1.split(".")[1]);
			    int b1_2 = Integer.parseInt(a1.split(".")[2]);
			    int b1_3 = Integer.parseInt(a1.split(".")[3]);
			    int b2_3 = Integer.parseInt(a2.split(".")[3]);
			    
			    if(b1_0 == c0 && b1_1 == c1 && b1_2 == c2){
				if(c3 >= b1_3 && c3 <= b2_3){
				    tmpFlag = true;
				    break;
				}
			    }
			}
			if(!tmpFlag){
			    sflag = false;
			} 
		    }
		    if(sflag){
			String childId = dataBean.getStr("ROOT_ID");
			    dataBean.remove(actionCode);
			    dataBean.remove("UPDATE_ID");
			    dataBean.set("KC_ID", kcId);
			    dataBean.setId(dataBean.getStr("ROOT_ID"));
			    dataBean.remove("ROOT_ID");
			    if(tables1[i].equals("TS_KCGL_UPDATE_ZWDYB")){
				String zwh = dataBean.getStr("ZW_ZWH_XT");
				String IPStr = dataBean.getStr("ZW_IP");
				int num_zwh = ServDao.count("TS_KCGL_ZWDYB", new ParamBean().setWhere("and kc_id = '"+kcId+"' and ZW_ZWH_XT ='"+zwh+"' and ZW_ID !='"+childId+"'"));
				int num_ip = ServDao.count("TS_KCGL_ZWDYB", new ParamBean().setWhere("and kc_id = '"+kcId+"' and ZW_IP ='"+IPStr+"' and ZW_ID !='"+childId+"'"));
				if(num_zwh > 0 || num_ip > 0){
				    continue;
				}
			    }
			    ServDao.save(tables2[i], dataBean);
		    }
		    
		}else if (action.equals("delete")) {
		    boolean runFlag = true;
		    //IP段校验
		    if(tables1[i].equals("TS_KCGL_UPDATE_IPSCOPE")){
			Bean scopeBean = ServDao.find("TS_KCGL_UPDATE_IPSCOPE", dataBean.getStr("ROOT_ID"));
			String tmpScope = scopeBean.getStr("IPS_SCOPE");
			String a1 = tmpScope.split("-")[0];
			String a2 = tmpScope.split("-")[1];
			List<Bean> zwList = ServDao.finds("TS_KCGL_ZWDYB", "and kc_id = '" + kcId + "'");
			for (Bean bean : zwList) {
			    String tmpZwIp = bean.getStr("ZW_IP");
			    int b1_0 = Integer.parseInt(a1.split(".")[0]);
			    int b1_1 = Integer.parseInt(a1.split(".")[1]);
			    int b1_2 = Integer.parseInt(a1.split(".")[2]);
			    int b1_3 = Integer.parseInt(a1.split(".")[3]);
			    int b2_3 = Integer.parseInt(a2.split(".")[3]);
			    
			    int c0 = Integer.parseInt(tmpZwIp.split(".")[0]);
			    int c1 = Integer.parseInt(tmpZwIp.split(".")[1]);
			    int c2 = Integer.parseInt(tmpZwIp.split(".")[2]);
			    int c3 = Integer.parseInt(tmpZwIp.split(".")[3]);
			    if(b1_0 == c0 && b1_1 == c1 && b1_2 == c2){
				if(c3 >= b1_3 && c3 <= b2_3){
				    runFlag = false;
				    break;
				}
			    }
			}
		    }
		    if(runFlag){
			ServDao.delete(tables2[i], dataBean.getStr("ROOT_ID"));
		    }
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
