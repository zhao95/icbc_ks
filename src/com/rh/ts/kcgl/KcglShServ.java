package com.rh.ts.kcgl;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;

public class KcglShServ extends CommonServ {

    protected void beforeQuery(ParamBean paramBean) {
//	无权限
//	extParams={USER_PVLG={upd=0}}
//	有总行考场审核人员权限
//	extParams={USER_PVLG={upd={ROLE_DCODE=, ROLE_ORG_LV=2}}}
//	一级分行考场审核人员权限
//	extParams={USER_PVLG={upd={ROLE_DCODE=, ROLE_ORG_LV=3}}}
//	有总行考场审核人员权限和一级分行考场审核人员权限
//	extParams={USER_PVLG={upd={ROLE_DCODE=, ROLE_ORG_LV=2,3}}}
	String roleOrgLv = paramBean.getBean("extParams").getBean("USER_PVLG").getBean("upd").getStr("ROLE_ORG_LV");
//	String roleDcode = paramBean.getBean("extParams").getBean("USER_PVLG").getBean("upd").getStr("ROLE_DCODE");
	
	if(roleOrgLv.indexOf("2") != -1){
	    if(roleOrgLv.length() > 2){
		paramBean.setQueryExtWhere(" and odept_level in (1," + roleOrgLv + ")");
	    }else{
		paramBean.setQueryExtWhere(" and (odept_level < 3 or KC_STATE2 = 1)");
	    }
	}else if(!roleOrgLv.isEmpty()){
	    UserBean userBean = Context.getUserBean();
	    String odeptCodePath = userBean.getODeptCodePath();
	    paramBean.setQueryExtWhere(" and odept_level in (" + roleOrgLv + ") and odept_path like '"+odeptCodePath+"%'");
	}else{
	    paramBean.setQueryExtWhere(" and 1=2");
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
		"TS_KCGL_UPDATE_IPZWH", "TS_KCGL_UPDATE_GLJG", "TS_KCGL_UPDATE_ZWDYB" };
	String[] tables2 = { "TS_KCGL_GLY", "TS_KCGL_JKIP", "TS_KCGL_IPSCOPE", "TS_KCGL_IPZWH", "TS_KCGL_GLJG",
		"TS_KCGL_ZWDYB" };
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
		case 3:
		    actionCode = "IPZ_ACTION";
		    break;
		case 4:
		    actionCode = "JG_ACTION";
		    break;
		case 5:
		    actionCode = "ZW_ACTION";
		    break;
		}
		String action = listTmp.get(j).getStr(actionCode);
		if (action.equals("add") || action.equals("update")) {
		    dataBean.remove(actionCode);
		    dataBean.remove("UPDATE_ID");
		    dataBean.set("KC_ID", kcId);
		    ServDao.save(tables2[i], dataBean);
		} else if (action.equals("delete")) {
		    ServDao.delete(tables2[i], dataBean.getId());
		}
	    }
	}

	OutBean outBean = new OutBean();
	outBean.setOk();
	return outBean;
    }
}
