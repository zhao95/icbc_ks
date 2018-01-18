package com.rh.ts.ksrl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.UserBean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;
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
     * 前台按钮根据用户的信息判断是否可见方法
     * 返回值为 1 可见  2 不可见
     *
     * @return outBean
     * @author wanglida
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
        /*
          将进入后台的权限配置成默认的方式，只要有角色功能中的一项，就可以进入后台。
          如果返回的权限集合中不为空，则说明有进入后台的权限
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
    
    public void getValue(ParamBean paramBean) {
        String ggId = paramBean.getStr("GG_ID");
        Bean ggBean = ServDao.find("TS_GG", ggId);
       String aTime= ggBean.getStr("S_ATIME");
        if (ggBean != null) {
            ggBean.set("S_MTIME", aTime);
            ggBean.set("GG_SORT", 10);
            Bean ggBeanS = save("TS_GG", ggBean);
        
            String aTimeS= ggBeanS.getStr("S_MTIME");
           String a=aTimeS;
        }
    }
    
    public  Bean save(String servId, Bean dataBean) {
        if (dataBean.getId().length() > 0) {
            return update(servId, dataBean);
        } else {
            return create(servId, dataBean);
        }
    }
    
    public  Bean update(String servId, Bean dataBean) {
        ServDefBean servDef = ServUtils.getServDef(servId);

        Bean updateBean = dataBean.copyOf(); //复制一份参数确保原参数不被修改
        //updateBean.set("S_MTIME", DateUtils.getDatetimeTS());
        List<Object> preValue = getPreValueClone(updateBean);
        String psql = Transaction.getBuilder().update(servDef, updateBean, preValue);
        boolean result = Transaction.getExecutor().execute(psql, preValue) > 0 ? true : false;
        if (result) {
            String key = servDef.getPKey();
            if (updateBean.contains(key)) { //设置主键字段
                updateBean.setId(updateBean.getStr(key));
            }
            servDef.clearDataCache(updateBean.getId()); //清除缓存
            return updateBean;
        } else {
            return null;
        }
    }
    
    public  Bean create(String servId, Bean dataBean) {
        ServDefBean servDef = ServUtils.getServDef(servId);
        Bean createBean = dataBean.copyOf(); //复制一份确保参数信息不会被修改
        List<Object> preValue = new ArrayList<Object>(dataBean.size());
        String psql = Transaction.getBuilder().insert(servDef, createBean, preValue);
        //进行唯一组约束的判断
        String uniqueStr = ServUtils.checkUniqueExists(servDef, createBean, true);
        if (uniqueStr != null) {
            throw new TipException(Context.getSyMsg("SY_SAVE_UNIQUE_EXISTS", uniqueStr));
        }
        boolean result = Transaction.getExecutor().execute(psql, preValue) > 0 ? true : false;
        if (result) {
            createBean.setId(createBean.getStr(servDef.getPKey())); //设置主键字段
            return createBean;
        } else {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    private  List<Object> getPreValueClone(Bean dataBean) {
        List<Object> preValue; //处理prevalue,存在就复制一份
        if (dataBean.contains(Constant.PARAM_PRE_VALUES)) {
            preValue = (List<Object>) ((ArrayList<Object>) dataBean.get(Constant.PARAM_PRE_VALUES)).clone();
        } else {
            preValue = new ArrayList<Object>();
        }
        return preValue;
    }

}
