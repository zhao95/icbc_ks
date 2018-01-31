package com.rh.ts.xmgl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.Cookie;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.serv.util.ExportExcel;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.JsonUtils;

public class Cccs2Serv extends CommonServ {
    //场次测算
    private static final String VIEW_CCCS = "TS_XMGL_CCCS_2";
    
    protected void beforeQuery(ParamBean paramBean) {
	Cookie[] cookies = Context.getRequest().getCookies();
	String cjVal = "";
	for (Cookie cookie : cookies) {
	    if (cookie.getName().equals("cjVal")) {
		cjVal = cookie.getValue();
		break;
	    }
	}
	paramBean.setQueryNoPageFlag(true);
	if (paramBean.getServId().equals(VIEW_CCCS)) {
	    if (cjVal.isEmpty()) {
		String cjVal_param = paramBean.getStr("cjVal");
		String xmId = paramBean.getStr("XM_ID");
		if(cjVal_param.equals("1")){
		    paramBean.setQueryExtWhere("and XM_ID = '"+xmId+"' and KC_LEVEL in ('一级')");
		}else if(cjVal_param.equals("1,2")){
		    paramBean.setQueryExtWhere("and XM_ID = '"+xmId+"' and KC_LEVEL in ('一级','二级')");
		}else if(!paramBean.getStr("_WHERE_").isEmpty()){
		    System.out.println();
		}else{
		    paramBean.setQueryExtWhere("and 1=2");
		}
	    } else {
		if (cjVal.indexOf("%2C") != -1) {
		    cjVal = "一级','二级";
		} else {
		    cjVal = "一级";
		}
		paramBean.setQueryExtWhere("and KC_LEVEL in ('" + cjVal + "')");
	    }
	}
    }
	
    public OutBean expExcel(ParamBean paramBean){
	OutBean outBean = new OutBean();
	String xmId = paramBean.getStr("xmId");
	UserBean userBean = Context.getUserBean();
	String userCode = userBean.getCode();
		
	List<Bean> explist = ServDao.finds("TS_XMGL_CCCS_EXP", "and xm_id = '"+xmId+"' and S_USER = '"+userCode+"' and type = 1 order by s_mtime desc limit 0,1");
	List<Bean> dataList = new ArrayList<Bean>();
	if(explist.size() > 0){
	    String expStr = explist.get(0).getStr("EXP_STR");
	    
	    List<Bean> list = JsonUtils.toBeanList(expStr);
	    for(int i=0;i<list.size();i++){
		Bean bean = new Bean();
		if(list.get(i).getStr("KC_NAME").isEmpty()){
		    continue;
		}
		bean.set("KC_NAME", list.get(i).getStr("KC_NAME"));
		bean.set("KC_GOOD", list.get(i).getStr("KC_GOOD"));
		bean.set("KC_MAX", list.get(i).getStr("KC_MAX"));
		bean.set("NUM_PEOPLE", list.get(i).getStr("NUM_PEOPLE"));
		bean.set("NUM_GOOD", list.get(i).getStr("NUM_GOOD"));
		bean.set("NUM_MAX", list.get(i).getStr("NUM_MAX"));
		dataList.add(bean);
	    }
	}
	
	ServDefBean serv = ServUtils.getServDef("TS_XMGL_CCCS_2");
	ExportExcel expExcel = new ExportExcel(serv);
	try {
	    LinkedHashMap<String, Bean> lhMap = createLinkedHashMap();
	    expExcel.createHeader(lhMap);
	    expExcel.appendData(dataList, paramBean);
	    expExcel.addSumRow();
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
            expExcel.close();
        }
	
	return outBean.setOk();
    }

    
    public LinkedHashMap<String, Bean> createLinkedHashMap(){
	LinkedHashMap<String, Bean> lhMap = new LinkedHashMap<String, Bean>();
	
	List<Bean> list = ServDao.finds("SY_SERV_ITEM", "and SERV_ID = 'TS_XMGL_CCCS_2' and ITEM_LIST_FLAG = 1 order by ITEM_LIST_ORDER");
	for (int i = 0; i < list.size(); i++) {
	    String  itemCode = list.get(i).getStr("ITEM_CODE");
	    String  itemName = list.get(i).getStr("ITEM_NAME");
	    String  itemListFlag = list.get(i).getStr("ITEM_LIST_FLAG");
	    Bean bean = new Bean();
	    bean.set("ITEM_CODE", itemCode);
	    bean.set("ITEM_NAME", itemName);
	    bean.set("ITEM_LIST_FLAG", itemListFlag);
	    lhMap.put(itemCode, bean);
	}
	return lhMap;
    }
   
    public LinkedHashMap<String, Bean> createLinkedHashMap2(){
	LinkedHashMap<String, Bean> lhMap = new LinkedHashMap<String, Bean>();
	
	List<Bean> list = ServDao.finds("SY_SERV_ITEM", "and SERV_ID = 'TS_XMGL_CCCS_2_EXPUTIL' and ITEM_LIST_FLAG = 1 order by ITEM_LIST_ORDER");
	for (int i = 0; i < list.size(); i++) {
	    String  itemCode = list.get(i).getStr("ITEM_CODE");
	    String  itemName = list.get(i).getStr("ITEM_NAME");
	    String  itemListFlag = list.get(i).getStr("ITEM_LIST_FLAG");
	    Bean bean = new Bean();
	    bean.set("ITEM_CODE", itemCode);
	    bean.set("ITEM_NAME", itemName);
	    bean.set("ITEM_LIST_FLAG", itemListFlag);
	    lhMap.put(itemCode, bean);
	}
	return lhMap;
    }
    
    public OutBean getKsList(ParamBean paramBean){
	OutBean outBean = new OutBean();
	String xmId = paramBean.getStr("xmId");
	String sjVal = paramBean.getStr("sjVal");
	
	ParamBean param1 = new ParamBean();
	param1.setWhere("and xm_id = '"+xmId+"' and bm_ks_time in ("+sjVal+") order by BM_CODE");
	param1.setSelect("BM_CODE,BM_NAME,BM_LB,BM_XL,BM_MK,BM_TYPE_NAME,BM_KS_TIME,CODE_PATH,ODEPT_CODE_V");
	//所有符合考生
	List<Bean> ksList = ServDao.finds("TS_XMGL_CCCS_KSGL", param1);
	ParamBean param2 = new ParamBean();
	param2.setWhere("and xm_id = '"+xmId+"' and bm_ks_time not in ("+sjVal+")");
	param2.setSelect("BM_CODE,BM_NAME,BM_LB,BM_XL,BM_MK,BM_TYPE_NAME,BM_KS_TIME,CODE_PATH,ODEPT_CODE_V");
	//所有不符合的考生
	List<Bean> nonKsList = ServDao.finds("TS_XMGL_CCCS_KSGL", param2);

	for (Bean bean1 : ksList) {
	    String codePath = bean1.getStr("CODE_PATH");
	    if(codePath.split("\\^").length > 1){
		String oneName = DictMgr.getName("TS_ORG_DEPT_ALL", codePath.split("\\^")[1]);
		bean1.set("ONE", oneName);
	    }else{
		String oneName = DictMgr.getName("TS_ORG_DEPT_ALL", codePath.split("\\^")[0]);
		bean1.set("ONE", oneName);
	    }
	}
	for (Bean bean2 : nonKsList) {
	    String codePath = bean2.getStr("CODE_PATH");
	    if(codePath.split("\\^").length > 1){
		String oneName = DictMgr.getName("TS_ORG_DEPT_ALL", codePath.split("\\^")[1]);
		bean2.set("ONE", oneName);
	    }else{
		String oneName = DictMgr.getName("TS_ORG_DEPT_ALL", codePath.split("\\^")[0]);
		bean2.set("ONE", oneName);
	    }
	}
	
	outBean.set("nonKsList", nonKsList);
	outBean.set("all", ksList);
	return outBean;
    }
    
    /**
     * 是否有重复数据
     * @param setA
     * @param setB
     * @return
     */ 
    public static boolean check(List<String> setA,List<String> setB) {
	Iterator<String> a = setA.iterator();
	while (a.hasNext()) {
	    String tmp = a.next();
	    if(setB.contains(tmp))return true;
	}
	return false;
    }
    
    public OutBean expNoExcel(ParamBean paramBean) throws UnsupportedEncodingException{
	OutBean outBean = new OutBean();
	String xmId = paramBean.getStr("xmId");
	UserBean userBean = Context.getUserBean();
	String userCode = userBean.getCode();
	List<Bean> explist = ServDao.finds("TS_XMGL_CCCS_EXP", "and xm_id = '"+xmId+"' and S_USER = '"+userCode+"' and type = 2 order by s_mtime desc limit 0,1");
	List<Bean> dataList = new ArrayList<Bean>();
	
	if(explist.size() > 0){
	    String expStr = explist.get(0).getStr("EXP_STR");
	    
	    List<Bean> list = JsonUtils.toBeanList(expStr);
	    for(int i=0;i<list.size();i++){
		Bean bean = new Bean();
		if(list.get(i).getStr("BM_NAME").isEmpty()){
		    continue;
		}
		bean.set("BM_NAME", list.get(i).getStr("BM_NAME"));
		bean.set("BM_CODE", list.get(i).getStr("BM_CODE"));
		bean.set("BM_LB", list.get(i).getStr("BM_LB"));
		bean.set("BM_XL", list.get(i).getStr("BM_XL"));
		bean.set("BM_MK", list.get(i).getStr("BM_MK"));
		bean.set("BM_TYPE_NAME", list.get(i).getStr("BM_TYPE_NAME"));
		bean.set("BM_KS_TIME", list.get(i).getStr("BM_KS_TIME"));
		bean.set("ONE", list.get(i).getStr("ONE"));
		bean.set("CAUSE", list.get(i).getStr("CAUSE"));
		dataList.add(bean);
	    }
	}
	ServDefBean serv = ServUtils.getServDef("TS_XMGL_CCCS_2_EXPUTIL");
	ExportExcel expExcel = new ExportExcel(serv);
	try {
	    LinkedHashMap<String, Bean> lhMap = createLinkedHashMap2();
	    expExcel.createHeader(lhMap);
	    expExcel.appendData(dataList, paramBean);
	    expExcel.addSumRow();
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
            expExcel.close();
        }
	
	return outBean.setOk();
    }
}
