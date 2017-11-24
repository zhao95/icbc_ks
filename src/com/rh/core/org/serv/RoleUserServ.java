package com.rh.core.org.serv;

import java.util.ArrayList;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.QueryCallback;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.RoleUserRelMgr;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Constant;

/**
 * 角色用户关系服务类
 * 
 * @author Jerry Li
 * 
 */
public class RoleUserServ extends CommonServ {
    
    /**
     * 保存之前的拦截方法，由子类重载
     * @param paramBean 参数信息
     */
    protected void beforeSave(ParamBean paramBean) {
        Bean user = paramBean.getSaveFullData();
        //清除菜单信息
        UserMgr.clearMenuByUsers(user.getStr("USER_CODE"));
        //清除缓存中的用户扩展信息
        UserBean userBean = UserMgr.getCacheUser(user.getStr("USER_CODE"));
        if (userBean != null) {
            userBean.clearUserExt();
        }
    }
    
    /**
     * 删除之后的拦截方法，由子类重载
     * @param paramBean 参数信息
     * @param outBean 输出信息
     */
    protected void afterDelete(ParamBean paramBean, OutBean outBean) {
        List<Bean> deletes = outBean.getDataList();
        StringBuilder userCodes = new StringBuilder();
        for (Bean data : deletes) {
            userCodes.append(data.getStr("USER_CODE")).append(",");
            //清除缓存中的用户扩展信息
            UserBean userBean = UserMgr.getCacheUser(data.getStr("USER_CODE"));
            if (userBean != null) {
                userBean.clearUserExt();
            }
        }
        int len = userCodes.length();
        if (len > 0) {
            userCodes.setLength(len - 1);
            UserMgr.clearMenuByUsers(userCodes.toString());
        }
    }

    /**
     * 复制角色用户列表到当前角色的用户列表中
     * @param paramBean 参数
     * @return 执行结果
     */
    public OutBean copyRoleUser(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        String servId = paramBean.getServId();
        final String roleCode = paramBean.getStr("ROLE_CODE");
        String userScope = paramBean.getStr("USER_SCOPE");
        String fromRoleCode = paramBean.getStr("FROM_ROLE_CODE");
        UserBean userBean = Context.getUserBean();
        StringBuilder where = new StringBuilder(" and CMPY_CODE");
        where.append("='").append(userBean.getCmpyCode()).append("' and ROLE_CODE='")
            .append(fromRoleCode).append("'");
        if (userScope.equals("IN")) {
            where.append(" and ODEPT_CODE='").append(userBean.getODeptCode()).append("'");
        } else if (userScope.equals("SUB")) {
            where.append(" and CODE_PATH like '").append(userBean.getODeptCodePath()).append("%'");
        }
        ParamBean param = new ParamBean();
        param.set(Constant.PARAM_WHERE, where.toString());
        List<Bean> roleUserList = ServDao.finds(servId, param, new QueryCallback() {
            public void call(List<Bean> columns, Bean data) {
                data.setId("");
                data.set("RU_ID", "");
                data.set("ROLE_CODE", roleCode);
            }
        });
        if (roleUserList.size() > 0) {
            param = new ParamBean(servId).setAddFlag(true).setBatchSaveDatas(roleUserList);
            outBean = batchSave(param);
        }
        return outBean;
    }
    
    /**
     * 初始化导入角色用户关系
     * @param paramBean
     * @return
     */
    public OutBean initImpRoleUser(ParamBean paramBean) {
    	log.debug("------initImpRoleUser------");
    	
    	String sysCode = Context.getSyConf("PE_SYS_CODE", "RA");

    	OutBean outBean = new OutBean();
    	
    	// 清除老数据
//    	ParamBean delParam = new ParamBean();
//    	delParam.set("CMPY_CODE", "icbc");
//    	ServDao.destroys(servId, delParam);
    	
    	// 统计数量
    	ServDefBean servDefBean = ServUtils.getServDef("DAMS_USER_ROLE_REL");
    	String dsName = servDefBean.getDataSource();
    	int total = 0;
    	try {
    		Transaction.begin(dsName);
    		total = ServDao.count("DAMS_USER_ROLE_REL", new ParamBean().setWhere(" AND 1=1 AND SYS_CODE = '" + sysCode + "'"));
    	} catch(Exception e) {
    		log.error(e.getMessage());
    	}finally {
    		Transaction.end();
    	}
    	
    	for (int i = 1; i <= total / 5000 + 1; i++) {
    		ParamBean param = new ParamBean();
			param.setWhere(" AND 1=1 AND SYS_CODE = '" + sysCode + "'");
			param.setShowNum(5000);
			param.setNowPage(i);
			
			List<Bean> dataList = new ArrayList<Bean>();
			try {
				Transaction.begin(dsName);
				dataList = ServDao.finds("DAMS_USER_ROLE_REL", param);
			} catch(Exception e) {
				log.error(e.getMessage());
			}finally {
				Transaction.end();
			}
			for (int j = 0; j < dataList.size(); j++) {
				dataList.get(j).set("UPDATE_STATE", "1");
			}
			
			try {
				// 导入
				RoleUserRelMgr.impRoleUserRel(dataList);
			} catch (Exception e) {
				outBean.setError(e.getMessage());
				e.printStackTrace();
			}
    	}
    	// 重置USER_CODE
    	setUserCode(paramBean);
    	
    	return outBean;
    }
    
    /**
     * 重置同步角色人员关系时没有找到用户的数据的USER_CODE
     * @param paramBean
     * @return
     */
    public OutBean setUserCode(ParamBean paramBean) {
    	log.debug("------setUserCode------");
  
    	String servId = paramBean.getServId();
    	
    	List<Bean> batchSaveList = new ArrayList<Bean>();
    	
    	// 不使用服务查询，因为服务是视图，过滤掉了userCode不存在的
		List<Bean> dataList = Transaction.getExecutor()
				.query("SELECT RU_ID, SSIC_ID FROM SY_ORG_ROLE_USER WHERE USER_CODE = 'NOT FOUND USER'");
    	
    	for (Bean data : dataList) {
			String ssicId = data.getStr("SSIC_ID");
			UserBean userBean = null;
			try {
				userBean = UserMgr.getUserByLoginName(ssicId);
			} catch (Exception e) {
				log.error("------setUserCode------ : not found user, ssicId = " + ssicId + "!");
			}
			if (userBean != null) {
				data.set("USER_CODE", userBean.getCode());
				batchSaveList.add(data);
				
				//清除菜单信息
		        UserMgr.clearMenuByUsers(userBean.getCode());
		        //清除缓存中的用户扩展信息
		        UserBean cacheUser = UserMgr.getCacheUser(userBean.getCode());
		        if (cacheUser != null) {
		        	cacheUser.clearUserExt();
		        }
			}
		}
    	
    	List<String> updateFields = new ArrayList<String>();
    	updateFields.add("USER_CODE");
    	updateFields.add("RU_ID");
    	ServDao.updates(servId, updateFields, batchSaveList);
    	
    	return new OutBean();
    }
}
