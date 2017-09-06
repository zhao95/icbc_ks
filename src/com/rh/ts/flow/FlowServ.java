package com.rh.ts.flow;

import java.util.ArrayList;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;

public class FlowServ extends CommonServ {

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
		bean.set("NODE_NAME", "一级审核人");
		break;
	    case 2:
		bean.set("NODE_NAME", "二级审核人");
		break;
	    case 3:
		bean.set("NODE_NAME", "三级审核人");
		break;
	    case 4:
		bean.set("NODE_NAME", "四级审核人");
		break;
	    case 5:
		bean.set("NODE_NAME", "五级审核人");
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
	OutBean outBean = new OutBean();
	String examerUserCode = paramBean.getStr("examerUserCode");
	int level = paramBean.getInt("level");
	String xmId = paramBean.getStr("xmId");
	//表单Bean
	Bean formBean = paramBean.getBean("form");
	//1:报名审核流程 2:异地借考流程 3:请假审核流程	
	int flowName = paramBean.getInt("flowName");  
	String shrUserCode = paramBean.getStr("shrUserCode");
	//起草人
	UserBean userBean = UserMgr.getUser(examerUserCode);
	String deptCode = userBean.getDeptCode();
	String odeptCode = userBean.getODeptCode(); 
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
		//逐级审核
		int getStep = 0;
		if(level == 0){
		    getStep = wfsSteps;
		}else{
		    getStep = level - 1;
		}
		List<Bean> bmshList = ServDao.finds("TS_WFS_BMSHLC", "and WFS_ID = '"+wfsId+"' and NODE_STEPS = "+getStep+" and DEPT_CODE like '%"+odeptCode+"%'");
		outBean.set("result", bmshList);
	    }else{
		//越级审核
		int getStep = 0;
		if(level == 0){
		    getStep = wfsSteps + 1;
		}else{
		    getStep = level;
		}
		List<Bean> bmshList = ServDao.finds("TS_WFS_BMSHLC", "AND WFS_ID = '"+wfsId+"' and NODE_STEPS < "+getStep+" and DEPT_CODE like '%"+odeptCode+"%'");
		outBean.set("result", bmshList);
	    }
	}else{
	    //异地借考，请假审核
	    int getStep = 0;
	    if(level == 0){
		getStep = wfsSteps;
	    }else{
		getStep = level - 1;
	    }
	    List<Bean> shList = ServDao.finds("TS_WFS_QJKLC", "AND WFS_ID = '"+wfsId+"' and NODE_STEPS = "+getStep+" and DEPT_CODE like '%"+odeptCode+"%'");
	    List<Bean> resList = new ArrayList<Bean>();
	    for (int k = 0; k < shList.size(); k++) {
		Bean shBean = shList.get(k);
		String shrName = shBean.getStr("QJKLC_SHR");
		String shUserCode = shBean.getStr("SHR_USERCODE");
		String ydyBm = shBean.getStr("QJKLC_YDDEPT");
		String zdyDeptCode = shBean.getStr("DEPT_CODE");
		String shzw = shBean.getStr("QJKLC_SHZW_CODE");
		String colCodel = shBean.getStr("QJKLC_ZDDEPT_COLCODE");
		
		//1.审核人已填写
		if(!shUserCode.equals("")){
		    Bean shUser = new Bean();
		    shUser.set("SHR_NAME", shrName);
		    shUser.set("SHR_USERCODE", shUserCode);
		    resList.add(shUser);
		}
		//2.预定义部门，审核人职位已填写
		if((!ydyBm.equals("")) &&(!shzw.equals(""))){
		    String sqlWhere = "";
		    switch (ydyBm) {
		    case "0":
			//起草人部门
			sqlWhere = "and dept_code = '"+deptCode+"' and USER_POST = '"+shzw+"'";
			break;
		    case "1":
			//起草人机构
			sqlWhere = "and odept_code = '"+odeptCode+"' and USER_POST = '"+shzw+"'";
			break;
		    case "2":
			//推送人部门
			sqlWhere = "and dept_code = '"+shrDeptCode+"' and USER_POST = '"+shzw+"'";
			break;
		    case "3":
			//推送人机构
			sqlWhere = "and odept_code = '"+shrOdeptCode+"' and USER_POST = '"+shzw+"'";
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
		if((!zdyDeptCode.equals("")) &&(!shzw.equals(""))){
		    List<Bean> userlist = ServDao.finds("SY_ORG_USER_ALL", "and dept_code = '"+zdyDeptCode+"' and USER_POST = '"+shzw+"'");
		    for (int i = 0; i < userlist.size(); i++) {
			Bean tmpUser = new Bean();
			tmpUser.set("SHR_NAME", userlist.get(i).getStr("USER_NAME"));
			tmpUser.set("SHR_USERCODE", userlist.get(i).getStr("USER_CODE"));
			resList.add(tmpUser);
		    }
		}
		//4.制定部门编码，审核人职位已填写
		if((!colCodel.equals("")) &&(!shzw.equals(""))){
		    //指定部门
		    String zdDept = formBean.getStr(colCodel);
		    List<Bean> userlist = ServDao.finds("SY_ORG_USER_ALL", "and odept_code = '"+zdDept+"' and USER_POST = '"+shzw+"'");
		    for (int i = 0; i < userlist.size(); i++) {
			Bean tmpUser = new Bean();
			tmpUser.set("SHR_NAME", userlist.get(i).getStr("USER_NAME"));
			tmpUser.set("SHR_USERCODE", userlist.get(i).getStr("USER_CODE"));
			resList.add(tmpUser);
		    }
		}
		
	    }
	    outBean.set("result", resList);
	    outBean.set("NODE_STEPS", getStep);
	}
	
	return outBean;
    }

}