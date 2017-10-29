package com.rh.ts.flow;

import java.util.ArrayList;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.util.Constant;

public class FlowServ extends CommonServ {
	/**
	 * COPY
	 * @param paramBean
	 * @return
	 */
	 public OutBean copy(ParamBean paramBean) {
			OutBean outBean = new OutBean();
			String servId = paramBean.getStr("servId");
			String primaryColCode = paramBean.getStr("primaryColCode");
			String pkCode = paramBean.getStr("pkCode");
			Bean bean = ServDao.find(servId, pkCode);
			bean.remove(primaryColCode);
			bean.setId("");
			bean = delSysCol(bean);
			Bean newBean = ServDao.create(servId, bean);
			if(!newBean.getId().equals("")){
			    copyLinkData(servId,pkCode,newBean.getId());
			    outBean.setOk();
			}
			return outBean;
		    }

	 
	  /**
	     * 删除系统字段
	     * @param bean
	     * @return
	     */
	    public Bean delSysCol(Bean bean){
		bean.remove("S_USER");
		bean.remove("S_DEPT");
		bean.remove("S_ODEPT");
		bean.remove("S_TDEPT");
		bean.remove("S_ATIME");
		bean.remove("S_MTIME");
		return bean;
	    }
	    
	    /**
	     * 拷贝关联表数据
	     * @param servId
	     * @param oldDataId 拷贝数据主键
	     * @param newDataId 保存数据主键
	     */
	    public void copyLinkData(String servId,String oldDataId,String newDataId){
		List<Bean> list = linkServ(servId);
		for (int i = 0; i < list.size(); i++) {
		    String linkServ = list.get(i).getStr("LINK_SERV_ID");
		    String primaryCode = primaryCode(linkServ);
		    String LINK_ITEM_CODE = list.get(i).getStr("LINK_ITEM_CODE");
		    List<Bean> linkDatalist = ServDao.finds(linkServ, "and " + LINK_ITEM_CODE +" = '"+oldDataId+"'");
		    for (int j = 0; j < linkDatalist.size(); j++) {
			Bean bean = linkDatalist.get(j);
			//旧数据主键
			String pkCode = bean.getId();
			bean.set(LINK_ITEM_CODE, newDataId);
			bean.setId("");
			bean.remove(primaryCode);
			bean = delSysCol(bean);
			Bean newBean = ServDao.save(linkServ, bean);
			if (newBean.getId() != "") {
			    copyLinkData(linkServ,pkCode,newBean.getId());
			}
		    }
		}
	    }
	    
	    /**
	     * 取得服务的主键编码
	     * @param servId
	     * @return
	     */
	    public String primaryCode(String servId){
		Bean bean = ServDao.find("SY_SERV", servId);
		return bean.getStr("SERV_KEYS");
	    }
	    
	    /**
	     * 注意：程序只考虑了最普通的主服务和自服务有一个字段有关系的情况，如果使用中情况不只一条绑定主键外键这种情况，需要自己另作处理
	     * 取得关联服务
	     * @param servId
	     */
	    public List<Bean> linkServ(String servId){
		ArrayList<Bean> list = new ArrayList<Bean>();
		List<Bean> linkList = ServDao.finds("SY_SERV_LINK", "and SERV_ID = '"+servId+"' and S_FLAG = 1");
		for (int i = 0; i < linkList.size(); i++) {
		    Bean bean = new Bean();
		    String linkId = linkList.get(i).getId();
		    String LINK_SERV_ID = linkList.get(i).getStr("LINK_SERV_ID");
		    bean.set("LINK_SERV_ID", LINK_SERV_ID);
		    List<Bean> itemList = ServDao.finds("SY_SERV_LINK_ITEM", "and LINK_ID = '"+linkId+"' and LINK_VALUE_FLAG = 1");
		    if(itemList.size() > 0){
			String ITEM_CODE = itemList.get(0).getStr("ITEM_CODE");
			String LINK_ITEM_CODE = itemList.get(0).getStr("LINK_ITEM_CODE");
			bean.set("ITEM_CODE", ITEM_CODE);
			bean.set("LINK_ITEM_CODE", LINK_ITEM_CODE);
		    }
		    list.add(bean);
		}
		return list;
	    }
	 
	    
    /**
     * 保存之后的拦截方法，由子类重载
     *
     * @param paramBean
     *            参数信息 可以通过paramBean获取数据库中的原始数据信息： Bean oldBean =
     *            paramBean.getSaveOldData();
     *            可以通过方法paramBean.getFullData()获取数据库原始数据加上修改数据的完整的数据信息： Bean
     *            fullBean = paramBean.getSaveFullData();
     *            可以通过paramBean.getAddFlag()是否为true判断是否为添加模式
     * @param outBean
     *            输出信息 可以通过outBean.getSaveIds()获取实际插入的数据主键
     */
    public void afterSave(ParamBean paramBean, OutBean outBean) {
	String dataId = paramBean.getId();
	Boolean flag = paramBean.getAddFlag();
	// 添加
	if (flag) {
	    int wfsSteps = paramBean.getInt("WFS_STEPS");
	    createStep(dataId, wfsSteps);
	} else {

	    // 得到新级数
	    int wfsSteps = paramBean.getInt("WFS_STEPS");
	    // 得到旧级数
	    int oldStept = paramBean.getSaveOldData().getInt("WFS_STEPS");

	    if (wfsSteps != 0) {
		if (wfsSteps != oldStept) {
		    ServDao.deletes("TS_WFS_NODE_APPLY", new Bean().set("WFS_ID", dataId));
		}
		createStep(dataId, wfsSteps);
	    }
	}
    }

    public void createStep(String dataId, int step) {
	for (int a = 0; a < step; a++) {
	    Bean bean = new Bean();
	    bean.set("WFS_ID", dataId);
	    bean.set("NODE_STEPS", a + 1);
	    switch (a + 1) {
	    case 1:
		bean.set("NODE_NAME", "最终审核级别");
		break;
	    case 2:
		bean.set("NODE_NAME", "一级审核级别");
		break;
	    case 3:
		bean.set("NODE_NAME", "二级审核级别");
		break;
	    case 4:
		bean.set("NODE_NAME", "三级审核级别");
		break;
	    case 5:
		bean.set("NODE_NAME", "最低级审核级别");
		break;
	    default:
		break;
	    }
	    ServDao.save("TS_WFS_NODE_APPLY", bean);
	}
    }

    /**
     * 提供报名审核流程的方法.
     * examerUserCode是起草人的用户编码，level为层级，xmId为ID,flowName为流程名字，shrUserCode为审核人用户编码
     * 注：起草节点 shrUserCode 传 examerUserCode
     * @param paramBean
     * @return
     */
    public OutBean backFlow(ParamBean paramBean){
    	UserBean userBean = Context.getUserBean();
	OutBean outBean = new OutBean();
	int level = paramBean.getInt("level");
	String xmId = paramBean.getStr("xmId");
	//表单Bean
	Bean formBean = paramBean.getBean("form");
	//1:报名审核流程 2:异地借考流程 3:请假审核流程
	int flowName = paramBean.getInt("flowName");
	String shrUserCode = paramBean.getStr("shrUserCode");
	String deptCode = paramBean.getStr("deptCode");
	String odeptCode = paramBean.getStr("odeptCode");
	//起草人
	/*String deptCode = userBean.getDeptCode();
	String odeptCode = userBean.getODeptCode();*/
	//推送人
	UserBean shrUserBean = UserMgr.getUser(shrUserCode);
	String shrDeptCode = shrUserBean.getDeptCode();
	String shrOdeptCode = shrUserBean.getODeptCode();


	String wfsId = "";
	List<Bean> list = ServDao.finds("TS_XMGL_FLOW_UTIL_V", "and xm_id ='"+xmId+"' and FLOW_TYPE = " + flowName);
	if(list.size() > 0){
	    wfsId = list.get(0).getStr("WFS_ID");
	}else{
	    outBean.setError("未绑定流程");
	    return outBean;
	}

	Bean wfsBean = ServDao.find("TS_WFS_APPLY", wfsId);
	//审核类型  1:逐级审核  2:越级审核
	int wfsType = wfsBean.getInt("WFS_TYPE");
	int wfsSteps = wfsBean.getInt("WFS_STEPS");
	if (flowName == 1) {
	    //报名审核
	    if(wfsType == 1){
	    	String s = "";
	    	String node_name = "";
		//逐级审核
		int getStep = 0;
		if(level == 0){
		    getStep = wfsSteps;
		}else{
		    getStep = level - 1;
		}
			//节点
			List<Bean> finds2 = ServDao.finds("TS_WFS_NODE_APPLY", "AND WFS_ID='"+wfsId+"' and NODE_STEPS = "+getStep);
			for (Bean bean2 : finds2) {
				String str = bean2.getStr("NODE_ID");
				List<Bean> finds = ServDao.finds("TS_WFS_BMSHLC", "and NODE_ID='"+str+"'");
				for (Bean bean : finds) {
					List<DeptBean> childDepts = OrgMgr.getChildDepts(bean2.getStr("CMPY_CODE"), bean.getStr("DEPT_CODE"));
					for (DeptBean deptBean : childDepts) {
						if(deptBean.getCode().equals( userBean.getDeptCode())){
							s+=bean.getStr("SHR_USERCODE")+",";
							node_name = bean2.getStr("NODE_NAME");
						}
					}
					 if(bean.getStr("DEPT_CODE").equals(userBean.getDeptCode())){
							s+=bean.getStr("SHR_USERCODE")+",";
							node_name = bean2.getStr("NODE_NAME");
						}
				}
			
		}
			outBean.set("SH_LEVEL",getStep);
			outBean.set("NODE_NAME", node_name);
		outBean.set("result", s);
	    }else{
	    	String s ="";
	    	String node_name = "";
		//越级审核
		int getStep = 0;
		if(level == 0){
		    getStep = wfsSteps + 1;
		}else{
		    getStep = level;
		}
			//节点
			List<Bean> finds2 = ServDao.finds("TS_WFS_NODE_APPLY", "AND WFS_ID='"+wfsId+"' and NODE_STEPS < "+getStep);
			for (Bean bean2 : finds2) {
				String str = bean2.getStr("NODE_ID");
				List<Bean> finds = ServDao.finds("TS_WFS_BMSHLC", "and NODE_ID='"+str+"'");
				for (Bean bean : finds) {
					List<DeptBean> childDepts = OrgMgr.getChildDepts(bean2.getStr("S_CMPY"), bean.getStr("DEPT_CODE"));
					for (DeptBean deptBean : childDepts) {
						if(deptBean.getCode().equals( userBean.getDeptCode())){
							s+=bean.getStr("SHR_USERCODE")+",";
							node_name = bean2.getStr("NODE_NAME");
						}
					}
					 if(bean.getStr("DEPT_CODE").equals(userBean.getDeptCode())){
						s+=bean.getStr("SHR_USERCODE")+",";
						node_name = bean2.getStr("NODE_NAME");
					}
				}
		}
			outBean.set("SH_LEVEL",getStep-1);
			outBean.set("NODE_NAME", node_name);
		outBean.set("result", s);
	    }
	}else{
	    //异地借考，请假审核
	    int getStep = 0;
	    if(level == 0){
		getStep = wfsSteps;
	    }else{
		getStep = level - 1;
	    }
	    String nodeName = "";
	    List<Bean> shList = ServDao.finds("TS_WFS_QJKLC", "AND WFS_ID = '"+wfsId+"' and NODE_STEPS = "+getStep);
	    List<Bean> resList = new ArrayList<Bean>();
	    for (int k = 0; k < shList.size(); k++) {
		Bean shBean = shList.get(k);
		if(nodeName.equals("")){nodeName = shBean.getStr("NODE_NAME");}
		
		int selType = shBean.getInt("QJKLC_SEL");
		int selDept = shBean.getInt("QJKLC_SEL_DEPT");
		String shrName = shBean.getStr("QJKLC_SHR");
		String shqzCode = shBean.getStr("QJKLC_SHQZ_CODE");
		String shUserCode = shBean.getStr("SHR_USERCODE");
		String ydyBm = shBean.getStr("QJKLC_YDDEPT");
		String zdyDeptCode = shBean.getStr("DEPT_CODE");
		String shzw = shBean.getStr("QJKLC_SHZW_CODE");
		String colCodel = shBean.getStr("QJKLC_ZDDEPT_COLCODE");

		//1.审核人已填写
		if(selType == 1){
		    if(!shUserCode.equals("")){
			Bean shUser = new Bean();
			shUser.set("SHR_NAME", shrName);
			shUser.set("SHR_USERCODE", shUserCode);
			resList.add(shUser);
			continue;
		    }
		}else if(selType == 2){
		    String sqlWhere = "";
		    if(colCodel.equals("")){
			sqlWhere = "and G_ID = '"+shqzCode+"' and ODEPT_CODE='"+shrOdeptCode+"'";
		    }else{
			String formOdept = formBean.getStr(colCodel);
			sqlWhere = "and G_ID = '"+shqzCode+"' and ODEPT_CODE='"+formOdept+"'";
		    }
		    
		    List<Bean> list2 = ServDao.finds("TS_PVLG_GROUP_USER",sqlWhere );
		    for (int i = 0; i < list2.size(); i++) {
			Bean tmpUser = new Bean();
			tmpUser.set("SHR_NAME", list2.get(i).getStr("USER_NAME"));
			tmpUser.set("SHR_USERCODE", list2.get(i).getStr("USER_CODE"));
			resList.add(tmpUser);
		    }
		    continue;
		}
		//2.预定义部门，审核人职位已填写
		if(selDept == 0 && (!ydyBm.equals("")) &&(!shzw.equals(""))){
		    String sqlWhere = "";
		    switch (ydyBm) {
		    case "0":
			//起草人部门
			sqlWhere = "and dept_code = '"+deptCode+"' and DUTY_LV_CODE = '"+shzw+"'";
			break;
		    case "1":
			//起草人机构
			sqlWhere = "and odept_code = '"+odeptCode+"' and DUTY_LV_CODE = '"+shzw+"'";
			break;
		    case "2":
			//推送人部门
			sqlWhere = "and dept_code = '"+shrDeptCode+"' and DUTY_LV_CODE = '"+shzw+"'";
			break;
		    case "3":
			//推送人机构
			sqlWhere = "and odept_code = '"+shrOdeptCode+"' and DUTY_LV_CODE = '"+shzw+"'";
			break;
		    default:
			break;
		    }
		    if(!sqlWhere.equals("")){
			List<Bean> userlist = ServDao.finds("SY_ORG_USER_ALL", sqlWhere);
			    for (int i = 0; i < userlist.size(); i++) {
				Bean tmpUser = new Bean();
				tmpUser.set("SHR_NAME", userlist.get(i).getStr("USER_NAME"));
				tmpUser.set("SHR_USERCODE", userlist.get(i).getStr("USER_CODE"));
				resList.add(tmpUser);
			    }
		    }
		}
		//3.自定义部门，审核人职位已填写
		if(selDept == 1 && (!zdyDeptCode.equals("")) &&(!shzw.equals(""))){
		    List<Bean> userlist = ServDao.finds("SY_ORG_USER_ALL", "and dept_code = '"+zdyDeptCode+"' and DUTY_LV_CODE = '"+shzw+"'");
		    for (int i = 0; i < userlist.size(); i++) {
			Bean tmpUser = new Bean();
			tmpUser.set("SHR_NAME", userlist.get(i).getStr("USER_NAME"));
			tmpUser.set("SHR_USERCODE", userlist.get(i).getStr("USER_CODE"));
			resList.add(tmpUser);
		    }
		}
		//4.制定部门编码，审核人职位已填写
//		if((!colCodel.equals("")) &&(!shzw.equals(""))){
//		    //指定部门
//		    String zdDept = formBean.getStr(colCodel);
//		    List<Bean> userlist = ServDao.finds("SY_ORG_USER_ALL", "and odept_code = '"+zdDept+"' and DUTY_LV_CODE = '"+shzw+"'");
//		    for (int i = 0; i < userlist.size(); i++) {
//			Bean tmpUser = new Bean();
//			tmpUser.set("SHR_NAME", userlist.get(i).getStr("USER_NAME"));
//			tmpUser.set("SHR_USERCODE", userlist.get(i).getStr("USER_CODE"));
//			resList.add(tmpUser);
//		    }
//		}
	    }
	    outBean.set("result", resList);
	    outBean.set("WFS_ID", wfsId);
	    outBean.set("NODE_STEPS", getStep);
	    outBean.set("NODE_NAME", nodeName);
	}
	return outBean;
    }
}