package com.rh.ts.ksrl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.util.Constant;
import com.rh.ts.pvlg.PvlgUtils;
import com.rh.ts.util.RoleUtil;
import org.apache.commons.lang.StringUtils;


public class GGServ extends CommonServ {

    /**
     * 后台管理查询用
     */
    public OutBean query(ParamBean paramBean) {
        return super.query(paramBean);
    }

    /**
     * 前台首页查询用
     */
    public OutBean finds(ParamBean paramBean) {
        return super.finds(paramBean);
    }

    // 查询前添加查询条件
    protected void beforeFinds(ParamBean paramBean) {
        findMsg(paramBean);
    }

    /**
     * 过滤没有权限的查询
     */

    // 查询前添加查询条件
    protected void findMsg(ParamBean paramBean) {
        UserBean userBean = Context.getUserBean();
        String userOdeptCode = userBean.getStr("ODEPT_CODE");
        //用户编码设置进roleParam里面，使其查询处用户所有的权限
        StringBuilder param_where = new StringBuilder();
        param_where.append(" AND EXISTS (SELECT d.code_path FROM sy_org_dept d ");
        param_where.append("WHERE  d.dept_code ='" + userOdeptCode + "' AND INSTR (d.CODE_PATH,CTLG_PCODE)>0)");
        paramBean.set(Constant.PARAM_WHERE, param_where.toString());
    }

    // 查询前添加查询条件
    protected void beforeQuery(ParamBean paramBean) {
        ParamBean param = new ParamBean();
        param.set("paramBean", paramBean);
//  			param.set("ctlgModuleName", ctlgModuleName);
        param.set("fieldName", "CTLG_PCODE");
        param.set("serviceName", paramBean.getServId());
        PvlgUtils.setOrgPvlgWhere(param);
    }

    /**
     * @auth wanglida
     * 前台按钮根据用户的信息判断是否可见方法
     * 返回值为 1 可见  2 不可见
     * @return outBean
     */
    public OutBean btnRoleFun() {
        OutBean outBean = new OutBean();
        UserBean userBean = Context.getUserBean();
        String userCode = userBean.getCode();
        String userLoginName = userBean.getLoginName();
        Bean pvlgRole = RoleUtil.getPvlgRole(userCode);
        Set<String> resultSet = new HashSet<String>();
        for (Object moduleValue : pvlgRole.values()) {
            Bean moduleValueBean = (Bean) moduleValue;
            for (Object optValue : moduleValueBean.values()) {
                Bean optValueBean;
                try {
                    optValueBean = (Bean) optValue;
                } catch (Exception e) {
                    continue;
                }
                String roleDcode = optValueBean.getStr("ROLE_DCODE");
                if (StringUtils.isNotBlank(roleDcode)) {
                    resultSet.add(roleDcode);
                }
            }
        }
        /**
         * 将进入后台的权限配置成默认的方式，只要有角色功能中的一项，就可以进入后台。
         * 如果返回的权限集合中不为空，则说明有进入后台的权限
         */
        if (userCode.equals("admin") || userLoginName.equals("admin")) {
            outBean.set("hasRole", "1");
        } else {
            if (resultSet.size() > 0) {
                outBean.set("hasRole", "1");
            } else {
               /* if(userPvlgToHTBean.getStr("show").equals("0")){*/
                outBean.set("hasRole", "2");
                /*}*/
            }
        }
        //获取权限是否配置的方式判断用户是否可以进后台
		/*Bean userPvlgToHT = RoleUtil.getPvlgRole(userCode,"TS_QT_HT");
        Bean userPvlgToHTBean = (Bean) userPvlgToHT.get("TS_QT_HT_PVLG");
		String userPvlgToHT_Flag = userPvlgToHTBean.getStr("show");
		if(userPvlgToHT_Flag!=""){
			if(userCode.equals("admin")|| userLoginName.equals("admin")){
				outBean.set("hasRole", "1");
			}else{
				if(userPvlgToHTBean.getStr("show").equals("0")){
					outBean.set("hasRole", "2");
				}else{
					outBean.set("hasRole", "1");
				}
			}
		}*/
        return outBean;
    }

    public void setValue(ParamBean paramBean) {
        String ggId = paramBean.getStr("GG_ID");
        Bean ggBean = ServDao.find("TS_GG", ggId);
        if (ggBean != null) {
            ggBean.set("GG_SORT", 0);
            ServDao.save("TS_GG", ggBean);
        }
    }

}
