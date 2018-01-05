package com.rh.ts.xmgl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;


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

public class CccsServ extends CommonServ {
    private static final String SERV_ID1 = "TS_KCZGL";
    private static final String SERV_ID2 = "TS_XMGL_CCCS_KCZGL";
    private static final String SERV_ID3 = "TS_KCZGL_GROUP";
    private static final String SERV_ID4 = "TS_KCZGL_KCGL";
    
    private static final int ONETIME_EXP_NUM = 20000;
    //考场关联机构
    private static final String VIEW_GLJG = "TS_KCGL_GLJG_V";
    //场次测算
    private static final String VIEW_CCCS = "TS_XMGL_CCCS_V";
    
	protected void beforeQuery(ParamBean paramBean) {

		if (paramBean.getServId().equals(VIEW_CCCS)) {

			String xmId = paramBean.getStr("XM_ID");
			paramBean.setQueryNoPageFlag(true);
			
//			String where = getWhere(xmId);
			//取关联机构的所有一级机构
//			String where = getWhereNew(xmId);
			String where = getWhereNew2(xmId);
			if (where.length() > 0) {
			    paramBean.setQueryExtWhere(where);
			}else{
			    paramBean.setQueryExtWhere("and 1=2");
			}
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
	UserBean userBean = Context.getUserBean();
	String userCode = userBean.getCode();
		
	List<Bean> explist = ServDao.finds("TS_XMGL_CCCS_EXP", "and xm_id = '"+xmId+"' and S_USER = '"+userCode+"' order by s_mtime desc limit 0,1");
	List<Bean> dataList = new ArrayList<Bean>();
	if(explist.size() > 0){
	    String expStr = explist.get(0).getStr("EXP_STR");
	    
	    List<Bean> list = JsonUtils.toBeanList(expStr);
	    for(int i=0;i<list.size();i++){
		Bean bean = new Bean();
		bean.set("DEPT_NAME", list.get(i).getStr("DEPT_NAME"));
		bean.set("CC_KC_NUM", list.get(i).getStr("CC_KC_NUM"));
		bean.set("CC_COMPUTER_GOODNUM", list.get(i).getStr("CC_COMPUTER_GOODNUM"));
		bean.set("CC_PEOPLE_NUM", list.get(i).getStr("CC_PEOPLE_NUM"));
		bean.set("CC_GOOD_NUM", list.get(i).getStr("CC_GOOD_NUM"));
		bean.set("CC_GOOD_SYNUM", list.get(i).getStr("CC_GOOD_SYNUM"));
		bean.set("CC_COMPUTER_MAXNUM", list.get(i).getStr("CC_COMPUTER_MAXNUM"));
		bean.set("CC_MAX_NUM", list.get(i).getStr("CC_MAX_NUM"));
		bean.set("CC_MAX_SYNUM", list.get(i).getStr("CC_MAX_SYNUM"));
		dataList.add(bean);
		List<Bean> tmpList = list.get(i).getList("childArr");
		for (int j = tmpList.size()-1; j >  -1; j--) {
		    Bean tmpBean = new Bean();
		    tmpBean.set("DEPT_NAME", tmpList.get(j).getStr("DEPT_NAME"));
		    tmpBean.set("CC_KC_NUM", tmpList.get(j).getStr("CC_KC_NUM"));
		    tmpBean.set("CC_PEOPLE_NUM", tmpList.get(j).getStr("CC_PEOPLE_NUM"));
		    tmpBean.set("CC_COMPUTER_GOODNUM", tmpList.get(j).getStr("CC_COMPUTER_GOODNUM"));
		    tmpBean.set("CC_GOOD_NUM", tmpList.get(j).getStr("CC_GOOD_NUM"));
		    tmpBean.set("CC_GOOD_SYNUM", tmpList.get(j).getStr("CC_GOOD_SYNUM"));
		    tmpBean.set("CC_COMPUTER_MAXNUM", tmpList.get(j).getStr("CC_COMPUTER_MAXNUM"));
		    tmpBean.set("CC_MAX_NUM", tmpList.get(j).getStr("CC_MAX_NUM"));
		    tmpBean.set("CC_MAX_SYNUM", tmpList.get(j).getStr("CC_MAX_SYNUM"));
		    dataList.add(tmpBean);
		}
	    }
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

/**    
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
	String odeptStr2 = getOdeptStr2(list2);
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
    
*/    
    /**
     * 根据项目主键取得所有项目相关的一级机构
     */
    /**
    public String getWhereNew(String xmId) {
	ParamBean param = new ParamBean();
	param.set("_SELECT_", "JG_CODE,JG_TYPE");
	param.set("_WHERE_", "and xm_id = '"+xmId+"'");
	List<Bean> list = ServDao.finds(VIEW_GLJG, param);
	List<Bean> list1 = new ArrayList<Bean>();
	List<Bean> list2 = new ArrayList<Bean>();
	for (int i = 0; i < list.size(); i++) {
	    Bean bean = list.get(i);
//	    int type = bean.getInt("JG_TYPE");
//	    if(type == 1){
//		//部门
//		list1.add(bean);
//	    }else{
//		//机构
//		list2.add(bean);
//	    }
	    list1.add(bean);
	}
	
	String odeptStr1 = getOneLevelOdept(list1);
	String odeptStr2 = getOneLevelOdept(list2);
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
    */
    /**
     * 根据项目主键,从考场所属机构 取得所有项目相关的一级机构
     */
    public String getWhereNew2(String xmId) {
	ParamBean param = new ParamBean();
	param.set("_SELECT_", "KC_ODEPTCODE,KC_ODEPTNAME,KC_LEVEL,xm_id");
	param.set("_WHERE_", "and xm_id = '"+xmId+"'");
	List<Bean> list = ServDao.finds("TS_XMGL_CCCS_UTIL_V", param);

	Set<String> set = new  HashSet<String>();
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < list.size(); i++) {
	    Bean bean = list.get(i);
	    if(bean.getStr("KC_LEVEL").equals("一级")){
		if(set.add(bean.getStr("KC_ODEPTCODE"))){
		    sb.append(bean.getStr("KC_ODEPTCODE")+",");
		}
	    }else{
		Bean deptBean = ServDao.find("SY_ORG_DEPT_ALL", bean.getStr("KC_ODEPTCODE"));
		String codePath = deptBean.getStr("CODE_PATH");
		String oneOdeptCode = codePath.split("\\^")[1];
		if(set.add(oneOdeptCode)){
		    sb.append(oneOdeptCode);
		}
	    }
	}
	
	if(sb.length() > 0){
	    String  odeptStr = sb.toString().replace(",", "','");
	    String sqlWhere = " AND DEPT_CODE in ('" + odeptStr + "')";
	    return sqlWhere;
	}
	return "";
    }
    
    /**
     * 根据dept_code 取该部门一级机构的ODEPT_CODE
     * @param list
     * @return
     */
    public String getOneLevelOdept(List<Bean> list) {
	StringBuilder sb = new StringBuilder();
	Set<String> set = new  HashSet<String>(); 
	for (int i = 0; i < list.size(); i++) {
	    String odeptCode = list.get(i).getStr("JG_CODE");
	    Bean bean = ServDao.find("SY_ORG_DEPT_ALL", odeptCode);
	    String codePath = bean.getStr("CODE_PATH");
	    //0010100000^0010100005^0010100317^0010100322^
	    String[] codeArr = codePath.split("\\^");
	    if(codeArr.length == 1){
		//特例：总行
		sb.append(codeArr[0]+",");
	    }else if(codeArr.length > 1 && set.add(codeArr[1])){
		sb.append(codeArr[1]+",");
//		List<Bean> list2 = ServDao.finds("SY_ORG_DEPT", "and dept_code = '"+codeArr[1]+"' and dept_code != odept_code");
//		if (list2.size() > 0) {
//		    sb.append(codeArr[0]+",");
//		}else{
//		    sb.append(codeArr[1]+",");
//		}
	    }
	}
	return sb.toString();
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
        
    /**
     * 根据考场Id 数组 取得影响的部门
     * @param paramBean
     * @return
     */
    public OutBean getOdetpScope(ParamBean paramBean){
	OutBean outBean = new OutBean();
	String kcIds = paramBean.getStr("kcIds");
	String[] kcIdArr = kcIds.split(",");
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < kcIdArr.length; i++) {
	    if (kcIdArr[i].length() > 0) {
//		sb.append(getOdeptStr(kcIdArr[i]));
		sb.append(getPathStr(kcIdArr[i]));
	    }
	}
	
//	outBean.set("odeptCodes", sb.toString());
	outBean.set("paths", sb.toString());
	return outBean;
    }
    
    /**
     * 根据考场Id 取得关联机构的CodePth
     * @param kcId
     * @return
     */
    public String getPathStr(String kcId){
	ParamBean param = new ParamBean();
	param.set("_SELECT_", "JG_CODE,JG_TYPE,CODE_PATH");
	param.set("_WHERE_", "and kc_id = '"+kcId+"'");
	List<Bean> list = ServDao.finds(VIEW_GLJG, param);
	StringBuilder sb = new StringBuilder();
	Set<String> set = new  HashSet<String>(); 
	for (int i = 0; i < list.size(); i++) {
	    Bean tmpBean = list.get(i);
	    String tmpPath = tmpBean.getStr("CODE_PATH");
	    if(set.add(tmpPath)){
		sb.append(tmpPath+",");
	    }
	}
	return sb.toString();
    }
    
    public String getOdeptStr(String kcId){
	ParamBean param = new ParamBean();
	param.set("_SELECT_", "JG_CODE,JG_TYPE");
	param.set("_WHERE_", "and kc_id = '"+kcId+"'");
	List<Bean> list = ServDao.finds(VIEW_GLJG, param);
	String odeptStr = getTwoAllOdept(list);
	return odeptStr;
    }
    
    /********二级考场计算**********************************************************************************************************/
    /**
     * 二级考场计算应用
     * @param paramBean
     * @return
     */
    public OutBean get2OdetpScope(ParamBean paramBean){
	OutBean outBean = new OutBean();
	String kcIds = paramBean.getStr("kcIds");
	String[] kcIdArr = kcIds.split(",");
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < kcIdArr.length; i++) {
	    if (kcIdArr[i].length() > 0) {
		sb.append(get2OdeptStr(kcIdArr[i]));
	    }
	}
	
	outBean.set("odeptCodes", sb.toString());
	return outBean;
    }
    
    public String get2OdeptStr(String kcId){
	ParamBean param = new ParamBean();
	param.set("_SELECT_", "JG_CODE,JG_TYPE");
	param.set("_WHERE_", "and kc_id = '"+kcId+"'");
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
	
	String odeptStr1 = getTwoSelfOdept(list1);
	String odeptStr2 = getTwoAllOdept(list2);
	String odeptStr = odeptStr1 + odeptStr2;
	return odeptStr;
    }
    /**
     * 根据部门取自己机构
     * @param list
     * @return
     */
    public String getTwoSelfOdept(List<Bean> list){
	StringBuilder sb = new StringBuilder();
	Set<String> set = new  HashSet<String>(); 
	for (int i = 0; i < list.size(); i++) {
	    String deptCode = list.get(i).getStr("JG_CODE");
	    Bean bean = ServDao.find("SY_ORG_DEPT_ALL", deptCode);
	    String odeptCode = bean.getStr("ODEPT_CODE");
	    if(set.add(odeptCode)){
		sb.append(odeptCode+",");
	    }
	}
	return sb.toString();
    }
    
    /**
     * 根据机构取下级所有机构
     * @param list
     * @return
     */
    public String getTwoAllOdept(List<Bean> list){
	StringBuilder sb = new StringBuilder();
	Set<String> set = new  HashSet<String>(); 
	for (int i = 0; i < list.size(); i++) {
	    String odeptCode = list.get(i).getStr("JG_CODE");
	    Bean bean = ServDao.find("SY_ORG_DEPT_ALL", odeptCode);
	    String codePath = bean.getStr("CODE_PATH");
	    Bean whereBean = new Bean();
	    whereBean.set("_SELECT_", "DEPT_CODE,ODEPT_CODE");
	    whereBean.set("_WHERE_", "and DEPT_TYPE=2 and CODE_PATH like '" + codePath +"%'");
	    List<Bean> tmpList = ServDao.finds("SY_ORG_DEPT_ALL", whereBean);
	    for (int j = 0; j < tmpList.size(); j++) {
		String tmpOdeptCode = tmpList.get(j).getStr("ODEPT_CODE");
		if (!tmpOdeptCode.isEmpty() && set.add(tmpOdeptCode)) {
		    sb.append(tmpOdeptCode + ",");
		}
	    }
	}
	return sb.toString();
    } 
    
    /*******************************************************************/
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
	/**
	//各种考试，考生的集合
	ArrayList<Bean> arrayList = new ArrayList<Bean>();
	//需要移除的考生
	ArrayList<Bean> rmList = new ArrayList<Bean>();
	for (int i = 0; i < typeList.size(); i++) {
	    String BM_XL_CODE = typeList.get(i).getStr("BM_XL_CODE");
	    String BM_MK_CODE = typeList.get(i).getStr("BM_MK_CODE");
	    String BM_TYPE = typeList.get(i).getStr("BM_TYPE");
	    Bean bean = new Bean();
	    bean.set("BM_XL_CODE", BM_XL_CODE);
	    bean.set("BM_MK_CODE", BM_MK_CODE);
	    bean.set("BM_TYPE", BM_TYPE);
	    ArrayList<Bean> userList = new ArrayList<Bean>();
	    for (int j = 0; j < ksList.size(); j++) {
		String pxl = ksList.get(j).getStr("BM_XL_CODE");
		String pmk = ksList.get(j).getStr("BM_MK_CODE");
		String ptype = ksList.get(j).getStr("BM_TYPE");
		
		if(BM_XL_CODE.equals(pxl) && BM_MK_CODE.equals(pmk) && BM_TYPE.equals(ptype)){
		    userList.add(ksList.get(j));
		    rmList.add(ksList.get(j));
		}
	    }
	    bean.set("userList", userList);
	    arrayList.add(bean);
	}
	
	for (int i = 0; i < rmList.size(); i++) {
	    ksList.remove(rmList.get(i));
	}
	nonKsList.addAll(ksList);
	outBean.set("nonKsList", nonKsList);
	outBean.set("all", arrayList);
	**/
	outBean.set("typeList", typeList);
	outBean.set("nonKsList", nonKsList);
	outBean.set("all", ksList);
	return outBean;
    }
    
    /**
     * sumJgArr 所有的关联机构
     * allks 所有的考生
     * alltype 所有的考试类型
     * @return
     */
    public OutBean oneLevel(ParamBean paramBean){
	OutBean outBean = new OutBean();
	
	return outBean;
    }
    
    public OutBean getDictItemName(ParamBean paramBean){
	OutBean outBean = new OutBean();
	String dict = paramBean.getStr("dictId");
	String itemCode = paramBean.getStr("itemCode");
	String itemName = DictMgr.getFullName(dict, itemCode);
	outBean.set("ITEM_NAME", itemName);
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
