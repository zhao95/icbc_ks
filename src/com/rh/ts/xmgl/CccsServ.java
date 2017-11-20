package com.rh.ts.xmgl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.util.ExportExcel;
import com.rh.core.serv.util.ServUtils;

public class CccsServ extends CommonServ {
    private static final String SERV_ID1 = "TS_KCZGL";
    private static final String SERV_ID2 = "TS_XMGL_CCCS_KCZGL";
    private static final String SERV_ID3 = "TS_KCZGL_GROUP";
    private static final String SERV_ID4 = "TS_KCZGL_KCGL";
    
    //考场关联机构
    private static final String VIEW_GLJG = "TS_KCGL_GLJG_V";
    //场次测算
    private static final String VIEW_CCCS = "TS_XMGL_CCCS_V";
    
	protected void beforeQuery(ParamBean paramBean) {

		if (paramBean.getServId().equals(VIEW_CCCS)) {

			String xmId = paramBean.getStr("XM_ID");
			paramBean.setQueryNoPageFlag(true);
			
			String where = getWhere(xmId);
			if (where.length() > 0) {
			    paramBean.setQueryExtWhere(where);
			}
//			sb.append(" AND DEPT_CODE in (SELECT JG_CODE FROM ").append(VIEW_GLJG);
//			sb.append(" WHERE XM_ID = '").append(xmId).append("')");
//			paramBean.setQueryExtWhere(sb.toString());
		}
	}
	
	
    /**
     * 找到当前机构
     * @param list
     * @return
     */
    public String getOdeptStr1(List<Bean> list) {
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < list.size(); i++) {
	    String detpCode = list.get(i).getStr("JG_CODE");
	    Bean bean = ServDao.find("SY_ORG_DEPT_ALL", detpCode);
	    String odeptCode = bean.getStr("ODEPT_CODE");
	    if(!odeptCode.isEmpty()){
		sb.append(odeptCode + ",");
	    }
	}
	return sb.toString();
    }
    /**
     * 找到当前机构及下级机构
     * @param list
     * @return
     */
    public String getOdeptStr2(List<Bean> list) {
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < list.size(); i++) {
	    String odeptCode = list.get(i).getStr("JG_CODE");
	    Bean bean = ServDao.find("SY_ORG_DEPT_ALL", odeptCode);
	    String codePath = bean.getStr("CODE_PATH");
	    Bean whereBean = new Bean();
	    whereBean.set("_SELECT_", "DEPT_CODE");
	    whereBean.set("_WHERE_", "and DEPT_CODE = ODEPT_CODE and CODE_PATH like '" + codePath +"%'");
	    List<Bean> tmpList = ServDao.finds("SY_ORG_DEPT_ALL", whereBean);
	    for (int j = 0; j < tmpList.size(); j++) {
		String tmpOdeptCode = tmpList.get(j).getStr("ODEPT_CODE");
		if (!tmpOdeptCode.isEmpty()) {
		    sb.append(tmpOdeptCode + ",");
		}
	    }
	}
	return sb.toString();
    }
	
     /**
     * 场次管理 倒入考试组管理
     * @param paramBean
     * @return
     */
    public OutBean xmAddKcz(ParamBean paramBean) {
	OutBean outBean = new OutBean();
	String ids = paramBean.getStr("ids");
	String xmId = paramBean.getStr("xmId");
	for (int i = 0; i < ids.split(",").length; i++) {
	    String dataId = ids.split(",")[i];
	    Bean kczBean = ServDao.find(SERV_ID1, dataId);
	    kczBean = delSysCol(kczBean);
	    kczBean.setId("");
	    kczBean.remove("KCZ_ID");
	    kczBean.set("SERV_ID", SERV_ID2);
	    kczBean.set("XM_ID", xmId);
	    Bean newKcz = ServDao.save(SERV_ID2, kczBean);
	    String kczId = newKcz.getId();
	    List<Bean> list = ServDao.finds(SERV_ID3, "and KCZ_ID = '"+dataId+"'");
	    for (int j = 0; j < list.size(); j++) {
		Bean groupBean = list.get(j);
		groupBean = delSysCol(groupBean);
		String groupId = groupBean.getId();
		groupBean.setId("");
		groupBean.remove("GROUP_ID");
		groupBean.set("SERV_ID", SERV_ID3);
		groupBean.set("KCZ_ID", kczId);
		Bean newGroupBean = ServDao.save(SERV_ID3, groupBean);
		String newGroupId = newGroupBean.getId();
		List<Bean> kcList = ServDao.finds(SERV_ID4, "and GROUP_ID = '"+groupId+"'");
		for (int k = 0; k < kcList.size(); k++) {
		    Bean kcBean = kcList.get(k);
		    String kcId = kcBean.getId();
		    kcBean.setId("");
		    kcBean.remove("KC_ID");
		    kcBean.remove("S_ATIME");
		    kcBean.remove("S_MTIME");
		    kcBean.set("SERV_ID", SERV_ID4);
		    kcBean.set("GROUP_ID", newGroupId);
		    Bean newKcBean = ServDao.save(SERV_ID4, kcBean);
		    String newKcId = newKcBean.getId();
		    //子表数据复制	 
		    List<Bean> linkServList = ServDao.finds("SY_SERV_LINK", "and SERV_ID = 'TS_KCZGL_KCGL' and S_FLAG = 1");
		    for (int l = 0; l < linkServList.size(); l++) {
			String linkServId = linkServList.get(l).getStr("LINK_SERV_ID");
			List<Bean> dataList = ServDao.finds(linkServId, "and KC_ID = '"+kcId+"'");
			for (int m = 0; m < dataList.size(); m++) {
			    Bean dataBean = dataList.get(m);
			    dataBean = delSysCol(dataBean);
			    dataBean.setId("");
			    dataBean.remove(primaryCode(linkServId));
			    dataBean.set("KC_ID", newKcId);
			    ServDao.save(linkServId, dataBean);
			}
		    }		    
		}
	    }
	}
	outBean.setOk();
	return outBean;
    }
    
    /**
     * 根据服务ID 取得主键编码
     * @param servId
     * @return
     */
    public String primaryCode(String servId){
	Bean bean = ServDao.find("SY_SERV", servId);
	return bean.getStr("SERV_KEYS");
    }
    
    /**
     * 删除系统字段
     * @param bean
     * @return
     */
    public Bean delSysCol(Bean bean){
	bean.remove("S_USER");
	bean.remove("S_DEPT");
	bean.remove("S_TDEPT");
	bean.remove("S_ODEPT");
	bean.remove("S_ATIME");
	bean.remove("S_MTIME");
	return bean;
    }
    
    public OutBean expExcel(ParamBean paramBean){
	OutBean outBean = new OutBean();
	String xmId = paramBean.getStr("xmId");
	//时间
	String sjVal = paramBean.getStr("sjVal");
	//层级
	String cjVal = paramBean.getStr("cjVal");
	if(sjVal == ""||cjVal == ""){
	    return outBean.setError();
	}
		
	String sqlWhere = getWhere(xmId);
	List<Bean> list = ServDao.finds("TS_XMGL_CCCS_V", sqlWhere);
	List<Bean> dataList = new ArrayList<Bean>();
	for (int i = 0; i < list.size(); i++) {
	    String deptCode = list.get(i).getId();
	    String deptName = list.get(i).getStr("DEPT_NAME");
	    dataList = getData(xmId,dataList,deptCode,deptName,cjVal,sjVal);
	}
	
	ServDefBean serv = ServUtils.getServDef("TS_XMGL_CCCS_V");
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
    
    public String getWhere(String xmId) {
	ParamBean param = new ParamBean();
	param.set("_SELECT_", "JG_CODE,JG_TYPE");
	param.set("_WHERE_", "and xm_id = '"+xmId+"'");
	List<Bean> list = ServDao.finds(VIEW_GLJG, param);
	List<Bean> list1 = new ArrayList<Bean>();
	List<Bean> list2 = new ArrayList<Bean>();
	for (int i = 0; i < list.size(); i++) {
	    Bean bean = list.get(i);
	    int type = bean.getInt("JG_TYPE");
	    if(type == 1){
		//部门
		list1.add(bean);
	    }else{
		//机构
		list2.add(bean);
	    }
	}
	String odeptStr1 = getOdeptStr1(list1);
	String odeptStr2 = getOdeptStr1(list2);
	String odeptStr = odeptStr1 + odeptStr2;
	if (odeptStr2.length() > 0) {
	    odeptStr = odeptStr.substring(0, odeptStr.length() - 1);
	}
	
	if(odeptStr.length() > 0){
	    odeptStr = odeptStr.replace(",", "','");
	    StringBuffer sb = new StringBuffer();
	    sb.append(" AND DEPT_CODE in ('" + odeptStr + "')");
	    return sb.toString();
	}
	return "";
    }
    public LinkedHashMap<String, Bean> createLinkedHashMap(){
	LinkedHashMap<String, Bean> lhMap = new LinkedHashMap<String, Bean>();
	
	List<Bean> list = ServDao.finds("SY_SERV_ITEM", "and SERV_ID = 'TS_XMGL_CCCS_V' and ITEM_LIST_FLAG = 1 order by ITEM_LIST_ORDER");
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
    
    public List<Bean> getData(String xmId,List<Bean> list,String deptCode,String deptName,String cjVal, String sjVal){
	if(cjVal.equals("1")){
	    Bean bean = new Bean();
	    bean.set("DEPT_NAME", deptName);
	    String whereSql = "and xm_id = '"+ xmId + "' and KC_LEVEL = '一级' and KC_ODEPTCODE='"+ deptCode + "'";
	    List<Bean> kcList = ServDao.finds("TS_XMGL_CCCS_UTIL_V", whereSql);
	    int kcNum = kcList.size();
	    bean.set("CC_KC_NUM", kcNum);
	    if(kcNum > 0){
		Bean result = getResult(kcList,cjVal,sjVal);
		bean.set("CC_COMPUTER_GOODNUM", result.getInt("CC_COMPUTER_GOODNUM"));
		bean.set("CC_COMPUTER_MAXNUM", result.getInt("CC_COMPUTER_MAXNUM"));
		bean.set("CC_PEOPLE_NUM", result.getInt("CC_PEOPLE_NUM"));
		bean.set("CC_GOOD_NUM", result.getInt("CC_GOOD_NUM"));
		bean.set("CC_MAX_NUM", result.getInt("CC_MAX_NUM"));
		bean.set("CC_GOOD_SYNUM", result.getInt("CC_GOOD_SYNUM"));
		bean.set("CC_MAX_SYNUM", result.getInt("CC_MAX_SYNUM"));
	    }else{
		bean.set("CC_COMPUTER_GOODNUM", 0);
		bean.set("CC_COMPUTER_MAXNUM", 0);
		bean.set("CC_PEOPLE_NUM", 0);
		bean.set("CC_GOOD_NUM", 0);
		bean.set("CC_MAX_NUM", 0);
		bean.set("CC_GOOD_SYNUM", 0);
		bean.set("CC_MAX_SYNUM", 0);
	    }
	    list.add(bean);
	}else{
	    //考场数
	    int kcNumSum = 0;
	    //报考人数
	    int peopleNumSum = 0;
	    //最优计算机数
	    int computerGoodNumSum = 0;
	    //最优计算机剩余数
	    int goodSyNumSum = 0;
	    //最大计算机数
	    int computerMaxNumSum = 0;
	    //最大计算机剩余数
	    int maxSyNumSum = 0;
	    //最优计算机场次
	    int CcGood = 0;
	    //最大计算机场次
	    int CcMax = 0;
	    Bean bean = new Bean();
	    bean.set("DEPT_NAME", deptName);
	    //1级考场
	    String sqlWhere = "and xm_id = '" + xmId+ "' and KC_LEVEL = '一级' and KC_ODEPTCODE='" + deptCode + "'";
	    List<Bean> kcList = ServDao.finds("TS_XMGL_CCCS_UTIL_V", sqlWhere);
	    Bean dept1Bean = new Bean();
	    dept1Bean.set("DEPT_NAME", deptName);
	    dept1Bean.set("CC_KC_NUM", kcList.size());
	    if(kcList.size() > 0){
		kcNumSum += kcList.size();
		Bean result = getResult(kcList,cjVal,sjVal);
		peopleNumSum += result.getInt("CC_PEOPLE_NUM");
		computerGoodNumSum += result.getInt("CC_COMPUTER_GOODNUM");
		goodSyNumSum += result.getInt("CC_GOOD_SYNUM");
		computerMaxNumSum += result.getInt("CC_COMPUTER_MAXNUM");
		maxSyNumSum += result.getInt("CC_MAX_SYNUM");
		CcGood = result.getInt("CC_GOOD_NUM");
		CcMax = result.getInt("CC_MAX_NUM");
		
		dept1Bean.set("CC_PEOPLE_NUM", result.getInt("CC_PEOPLE_NUM"));
		dept1Bean.set("CC_COMPUTER_GOODNUM", result.getInt("CC_COMPUTER_GOODNUM"));
		dept1Bean.set("CC_GOOD_NUM", result.getInt("CC_GOOD_SYNUM"));
		dept1Bean.set("CC_GOOD_SYNUM", result.getInt("CC_GOOD_SYNUM"));
		dept1Bean.set("CC_COMPUTER_MAXNUM", result.getInt("CC_MAX_SYNUM"));
		dept1Bean.set("CC_MAX_NUM", result.getInt("CC_GOOD_NUM"));
		dept1Bean.set("CC_MAX_SYNUM", result.getInt("CC_MAX_NUM"));
	    }else{
		dept1Bean.set("CC_PEOPLE_NUM", 0);
		dept1Bean.set("CC_COMPUTER_GOODNUM", 0);
		dept1Bean.set("CC_GOOD_NUM", 0);
		dept1Bean.set("CC_GOOD_SYNUM", 0);
		dept1Bean.set("CC_COMPUTER_MAXNUM", 0);
		dept1Bean.set("CC_MAX_NUM", 0);
		dept1Bean.set("CC_MAX_SYNUM", 0);
	    }
		
	    //2级考场
	    List<Bean> odept3Arr = ServDao.finds("TS_ORG_DEPT","AND DEPT_PCODE = '" + deptCode+ "'");
	    ArrayList<Bean> dept2List = new ArrayList<Bean>();
	    if(odept3Arr.size() > 0){
		for(int i=0;i<odept3Arr.size();i++){
		    Bean tmpBean = new Bean();
		    String dept2Code = odept3Arr.get(i).getStr("DEPT_CODE");
		    String dept2Name = odept3Arr.get(i).getStr("DEPT_NAME");
		    tmpBean.set("DEPT_NAME", dept2Name);
		    List<Bean> kcArrTemp = ServDao.finds("TS_XMGL_CCCS_UTIL_V", "and xm_id = '" + xmId+ "' and KC_LEVEL = '二级' and KC_ODEPTCODE='" + dept2Code + "'");
		    tmpBean.set("CC_KC_NUM", kcArrTemp.size());
		    if(kcArrTemp.size() > 0){
			kcNumSum += kcArrTemp.size();
			Bean result = getResult(kcArrTemp,cjVal,sjVal);
			peopleNumSum += result.getInt("CC_PEOPLE_NUM");
			computerGoodNumSum += result.getInt("CC_COMPUTER_GOODNUM");
			goodSyNumSum += result.getInt("CC_GOOD_SYNUM");
			computerMaxNumSum += result.getInt("CC_COMPUTER_MAXNUM");
			maxSyNumSum += result.getInt("CC_MAX_SYNUM");
			if(result.getInt("CC_GOOD_NUM") > CcGood){
			    CcGood = result.getInt("CC_GOOD_NUM");
			}
			if(result.getInt("CC_MAX_NUM") > CcMax){
			    CcMax = result.getInt("CC_MAX_NUM");
			}
			
			tmpBean.set("CC_PEOPLE_NUM", result.getInt("CC_PEOPLE_NUM"));
			tmpBean.set("CC_COMPUTER_GOODNUM", result.getInt("CC_COMPUTER_GOODNUM"));
			tmpBean.set("CC_GOOD_NUM", result.getInt("CC_GOOD_NUM"));
			tmpBean.set("CC_GOOD_SYNUM", result.getInt("CC_GOOD_SYNUM"));
			tmpBean.set("CC_COMPUTER_MAXNUM", result.getInt("CC_COMPUTER_MAXNUM"));
			tmpBean.set("CC_MAX_NUM", result.getInt("CC_MAX_NUM"));
			tmpBean.set("CC_MAX_SYNUM", result.getInt("CC_MAX_SYNUM"));
			dept2List.add(tmpBean);
		    }
		}
	    }
	    
	    bean.set("CC_KC_NUM", kcNumSum);
	    bean.set("CC_PEOPLE_NUM", peopleNumSum);
	    bean.set("CC_COMPUTER_GOODNUM", computerGoodNumSum);
	    bean.set("CC_GOOD_NUM", CcGood);
	    bean.set("CC_GOOD_SYNUM", goodSyNumSum);
	    bean.set("CC_COMPUTER_MAXNUM", computerMaxNumSum);
	    bean.set("CC_MAX_NUM", CcMax);
	    bean.set("CC_MAX_SYNUM", maxSyNumSum);
	
	    list.add(bean);
	    if(dept2List.size() > 0){
		list.add(dept1Bean);
		    for (int i = 0; i < dept2List.size(); i++) {
			list.add(dept2List.get(i));
		    }
	    }
	}
	return list;
    }

    private Bean getResult(List<Bean> kcList, String cjVal, String sjVal) {
	//返回结果
	Bean res = new Bean();
	int goodSumNum = 0;
	int maxSumNum = 0;
	String jgSum = "";
	for (int i = 0; i < kcList.size(); i++) {
	    int goodNum = kcList.get(i).getInt("KC_GOOD");
	    int maxNum = kcList.get(i).getInt("KC_MAX");
	    goodSumNum += goodNum;
	    maxSumNum += maxNum;
	    //根据考场，得到机构的范围
	    List<Bean> jgList = ServDao.finds("TS_KCGL_GLJG", "and KC_ID = '"+kcList.get(i).getStr("KC_ID")+"'");
	    for(int j=0;j<jgList.size();j++){
		jgSum = jgSum + "," + jgList.get(j).getStr("JG_CODE");
	    }
	}
	if(!jgSum.equals("")){
	    jgSum = jgSum.substring(1);
	    jgSum = jgSum.replaceAll(",", "','");
	}
	sjVal = sjVal.replaceAll(",", "','");
	int poepleNum = ServDao.count("TS_XMGL_CCCS_KSGL", new ParamBean().setWhere("and BM_KS_TIME in ('"+sjVal+"') and S_ODEPT in ('"+jgSum+"')"));
	
	res.set("CC_PEOPLE_NUM", poepleNum);
	res.set("CC_COMPUTER_GOODNUM", goodSumNum);
	res.set("CC_COMPUTER_MAXNUM", maxSumNum);
	if (goodSumNum != 0 && maxSumNum != 0) {
	    //最优场次数
	    int goodCCNum = (int) Math.ceil((double)poepleNum/goodSumNum);
	    //最大场次数
	    int maxCCNum = (int) Math.ceil((double)poepleNum/maxSumNum);
	    //最优剩余机器数
	    int goodSyNum = goodCCNum * goodSumNum - poepleNum;
	    //最大剩余机器数
	    int maxSyNum = maxCCNum * maxSumNum - poepleNum;
	    res.set("CC_GOOD_NUM", goodCCNum);
	    res.set("CC_GOOD_SYNUM", goodSyNum);
	    res.set("CC_MAX_NUM", maxCCNum);
	    res.set("CC_MAX_SYNUM", maxSyNum);
	}else{
	    res.set("CC_GOOD_NUM", 0);
	    res.set("CC_GOOD_SYNUM", 0);
	    res.set("CC_MAX_NUM", 0);
	    res.set("CC_MAX_SYNUM", 0);
	}
	return res;
    }
}
