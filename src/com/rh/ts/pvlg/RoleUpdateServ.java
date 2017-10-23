package com.rh.ts.pvlg;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.ts.pvlg.mgr.RoleMgr;

public class RoleUpdateServ extends CommonServ{
    @Override
    protected void beforeQuery(ParamBean paramBean) {
        super.beforeQuery(paramBean);
        String extWhereBean = paramBean.getStr("_searchWhere");
        if(extWhereBean.equals("")){
            paramBean.setQueryExtWhere(" and 1=2");
        }
    }
    
    /**
     * 服务高级查询 对应的查询方法
     * @param paramBean
     * @return
     */
    public OutBean myFind(ParamBean paramBean) {
	OutBean outBean = new OutBean();
	String value1_1 = paramBean.getStr("value1_1");
	String value1_2 = paramBean.getStr("value1_2");
	String value2 = paramBean.getStr("value2");
	String value3 = paramBean.getStr("value3");
	String value4 = paramBean.getStr("value4");

	String where = "";
	if(!value3.isEmpty()){
	    value3 = value3.replace(",", "','");
	    where += "and ROLE_DCODE in ('" +value3+"')";
	}
	if(!value4.isEmpty()){
	    where += "and ROLE_NAME like '%"+value4+"%'";
	}
	
	ParamBean roleParamBean = new ParamBean();
	roleParamBean.setSelect("ROLE_ID");
	roleParamBean.setWhere(where);
	List<Bean> list = ServDao.finds("ts_pvlg_role", roleParamBean);
	
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < list.size(); i++) {
	    String roleId = list.get(i).getStr("ROLE_ID");
	    if(!value1_1.isEmpty() && !value1_2.isEmpty() && value2.equals("1")){
		//包含功能
		ParamBean whereBean = new ParamBean();
		whereBean.setWhere("and ROLE_ID = '"+roleId+"' and MD_CODE = '"+value1_1+"' and MD_VAL like '%"+value1_2+"%'");
		int num = ServDao.count("TS_PVLG_ROLE_MOD", whereBean);
		if(num > 0) sb.append(roleId+",");
	    }else if(!value1_1.isEmpty() && !value1_2.isEmpty() && value2.equals("2")){
		//不包含功能
		ParamBean whereBean = new ParamBean();
		whereBean.setWhere("and ROLE_ID = '"+roleId+"' and MD_CODE = '"+value1_1+"' and MD_VAL like '%"+value1_2+"%'");
		int num = ServDao.count("TS_PVLG_ROLE_MOD", whereBean);
		if(num < 1) sb.append(roleId+",");
	    }else{
		sb.append(roleId+",");
	    }
	}
	//判断sb最后一个字符是否为逗号，如果是删除
	String roleIds = "";
	if(sb.length() > 0){
	    roleIds = sb.substring(0, sb.length()-1);
	}
	outBean.set("roleIds", roleIds);
	return outBean;
    }
    
    public OutBean removeRoleCache(ParamBean paramBean) {
	String roleId = paramBean.getStr("roleId");
	String module = paramBean.getStr("module");
	OutBean outBean = new OutBean();
	RoleMgr.removeRoleCache(roleId, module);
	return outBean;
    } 
}
