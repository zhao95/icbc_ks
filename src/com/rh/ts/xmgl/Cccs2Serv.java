package com.rh.ts.xmgl;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.Cookie;

import org.json.JSONArray;
import org.json.JSONException;

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
		
	List<Bean> explist = ServDao.finds("TS_XMGL_CCCS_EXP", "and xm_id = '"+xmId+"' and S_USER = '"+userCode+"' order by s_mtime desc limit 0,1");
	List<Bean> dataList = new ArrayList<Bean>();
	if(explist.size() > 0){
	    String expStr = explist.get(0).getStr("EXP_STR");
	    
	    List<Bean> list = JsonUtils.toBeanList(expStr);
	    for(int i=0;i<list.size();i++){
		Bean bean = new Bean();
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
   
    public OutBean getKsList(ParamBean paramBean){
	OutBean outBean = new OutBean();
	String xmId = paramBean.getStr("xmId");
	String sjVal = paramBean.getStr("sjVal");
	//所有符合考生
	List<Bean> ksList = ServDao.finds("TS_XMGL_CCCS_KSGL", "and xm_id = '"+xmId+"' and bm_ks_time in ("+sjVal+")");
	//所有不符合的考生
	List<Bean> nonKsList = ServDao.finds("TS_XMGL_CCCS_KSGL", "and xm_id = '"+xmId+"' and bm_ks_time not in ("+sjVal+")");
	//所有考试
	List<Bean> typeList = ServDao.finds("TS_XMGL_CCCS_UTIL_TYPE_V", "and xm_id = '"+xmId+"'");
	outBean.set("typeList", typeList);
	outBean.set("nonKsList", nonKsList);
	outBean.set("all", ksList);
	return outBean;
    }
    
    @SuppressWarnings("deprecation")
    public OutBean getCC(ParamBean paramBean) throws JSONException{
	OutBean outBean = new OutBean();
	int kcGood = paramBean.getInt("kcGood");
	int kcMax = paramBean.getInt("kcMax");
	String myArr = paramBean.getStr("myArr");
	String myArrStr=URLDecoder.decode(myArr);
	JSONArray list = new JSONArray(myArrStr);
	
	int ccGood = 0;
	int ccMax = 0;
	
	// 总体已占用
	ArrayList<Integer> list0 = new ArrayList<Integer>();
	for (int j = 0; j < list.length(); j++) {
	    if (list0.contains(j)) {
		continue;
	    }
	    // 本次计算已占用
	    ArrayList<Integer> list3 = new ArrayList<Integer>();
	    list0.add(j);
	    list3.add(j);
	    int i = j + 1;
	    ArrayList<String> setA = new ArrayList<String>();
	    JSONArray listChildA = new JSONArray(list.get(j).toString());
	    for(int k=0;k<listChildA.length();k++){
		setA.add(listChildA.getString(k));
	    }
	    
	    while (i < list.length()) {
		if (list0.contains(i) || list3.contains(i)) {
		    i++;
		    continue;
		}
		ArrayList<String> setB = new ArrayList<String>();
		JSONArray listChildB = new JSONArray(list.get(i).toString());
		for(int k=0;k<listChildB.length();k++){
		    setB.add(listChildB.getString(k));
		}
		
		boolean flag = check(setA, setB);
		if (!flag) {
		    setA.addAll(setB);
		    list0.add(i);
		    list3.add(i);
		}
		i++;
	    }
//	    System.out.println("本次计算==="+list3);
	    int tmpPeopleNum = setA.size();
//	    System.out.println("本次计算人数==="+tmpPeopleNum);
	    int cc1 = tmpPeopleNum/kcGood;
	    int cc2 = tmpPeopleNum/kcMax;
	    if(tmpPeopleNum%kcGood > 0){ cc1++; }
	    if(tmpPeopleNum%kcMax > 0){ cc2++; }
	    
	    ccGood += cc1;
	    ccMax += cc2;
	}
	System.out.println("总计算=" + list0);
	outBean.set("ccGood", ccGood);
	outBean.set("ccMax", ccMax);
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
}
