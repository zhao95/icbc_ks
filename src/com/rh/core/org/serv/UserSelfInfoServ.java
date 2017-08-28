package com.rh.core.org.serv;

import com.rh.core.base.Context;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.rh.core.base.BaseContext.APP;
import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.base.db.Transaction;
import com.rh.core.comm.CacheMgr;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.UserStateBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.EncryptUtils;
import com.rh.core.util.var.VarMgr;

/**
 * 
 * @author chujie
 * 
 */
public class UserSelfInfoServ extends CommonServ {

    @Override
    protected void beforeByid(ParamBean paramBean) {
        UserBean userBean = Context.getUserBean();
        paramBean.setId(userBean.getId());
        super.beforeByid(paramBean);
    }

    /**
     * 个人信息功能 点击菜单弹出当前用户信息的卡片
     * @param paramBean 参数信息
     * @return outBean
     */
    public OutBean show(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        String url = Context.appStr(APP.CONTEXTPATH) + "/sy/base/view/stdCardView.jsp?sId=" + paramBean.getServId() 
                + "&pkCode=" + Context.getUserBean().getCode();
        // URL跳转
        outBean.setToDispatcher(url).setOk();
        return outBean;
    }
    
    /**
     * 只能用SY_ORG_USER 服务来修改用户数据，便于用户监听。
     * @param paramBean 参数
     * @return 操作结果
     */
    @Override
    public OutBean save(ParamBean paramBean) {
        UserBean userBean = Context.getUserBean();
        // 缺省只能保存本人的信息
        paramBean.setId(userBean.getCode());
        paramBean.setServId(ServMgr.SY_ORG_USER);
        paramBean.setAct(ServMgr.ACT_SAVE);
        // yangjinyun: 如果要修改密码，判断用户是否没有登录过，或者强制要求用户输入密码，否则不允许用户修改密码，如果要修改密码，必须先验证老密码是否正确。
        // 这段程序只是为了兼容前台某些情况不能提交旧密码情况，其实应该要求前台输入老密码，如果老密码正确才能修改。
        if (paramBean.isNotEmpty("USER_PASSWORD")) {
            UserStateBean userStateBean = UserMgr.getUserState(userBean.getCode());
            // 如果登录过，且不强制要求用户修改密码，则不允许用户修改密码。
            if (userStateBean != null && userStateBean.getInt("MODIFY_PASSWORD") != 1) {
                paramBean.remove("USER_PASSWORD");
            }
        }
        boolean modifyPassword = false;
        if (paramBean.isNotEmpty("USER_PASSWORD")) {
            modifyPassword = true;
        }
        
        OutBean outBean = ServMgr.act(paramBean);
        
        // 用户修改密码之后，修改UserState表数据。
        if (outBean.isOk() && modifyPassword) {
            saveUserState(userBean);
        }
        
        return outBean;
    }
    
    /**
     * 修改UserState，把Modify_password字段的值改成2。
     * @param userBean 被修改的用户UserBean 对象
     */
    private void saveUserState(UserBean userBean) {
        try {
            ParamBean userStateParam = new ParamBean(ServMgr.SY_ORG_USER_STATE, ServMgr.ACT_SAVE);
            userStateParam.set("MODIFY_PASSWORD", 2);
            userStateParam.setId(userBean.getCode());
            ServMgr.act(userStateParam);
        } catch (Exception e) {
            // 忽略错误
        }
    }
    
    /**
     * 修改用户密码
     * @param paramBean 参数信息
     * @return outBean
     */
    public OutBean saveInfo(ParamBean paramBean) {
        boolean modifyPassword = false;
        OutBean outBean = new OutBean();
        String oldPassword = paramBean.getStr("OLD_PASSWORD"); // 输入的旧密码
        String newPassword = paramBean.getStr("USER_PASSWORD"); // 新密码
        UserBean userBean = Context.getUserBean();
        //如果输入旧密码就进行加密判断正确性    否则直接保存修改的数据
        if (!newPassword.equals("")) {
            // 对输入的旧密码进行加密对比
            String enOldPswd = EncryptUtils.encrypt(oldPassword,
                    Context.getSyConf("SY_USER_PASSWORD_ENCRYPT", EncryptUtils.DES));
            if (!enOldPswd.equals(userBean.getPassword())) {
                throw new TipException("输入的旧密码错误。");
            } 
            paramBean.set("USER_PASSWORD", newPassword);
            modifyPassword = true;
        } 
        paramBean.setId(userBean.getCode());
        paramBean.set("USER_CODE", userBean.getCode());
        outBean = ServMgr.act(ServMgr.SY_ORG_USER, "save", paramBean);
        outBean.setOk();
        if (modifyPassword) {
            saveUserState(userBean);
        }
        return outBean;
    }
    /**
     * @param paramBean 参数信息
     * @param outBean 参数信息
     */
    protected void afterSave(ParamBean paramBean, OutBean outBean) {
        if (outBean.isOk()) {
            UserMgr.clearSelfUserCache();
            ServDefBean userDef = ServUtils.getServDef(ServMgr.SY_ORG_USER); //清除用户服务对应的字典
            userDef.clearDictCache();
        }
    }
    
    /**
	 * 获取当前用户的相关系统变量
	 * @param paramBean - 空
	 * @return - 系统变量
	 */
	public OutBean getUserInfo(ParamBean paramBean) {
		OutBean outBean = new OutBean();
		outBean.set("ORG_VARS", VarMgr.getOrgMap());
		return outBean;
	}
	
	/**
	 * 
	 * @param paramBean
	 * @return
	 */
    public OutBean getMultiDept(ParamBean paramBean) {
    	UserBean currBean = Context.getUserBean();
    	List<Bean> list = UserMgr.findUsers(currBean.getId());

    	for(Bean bean: list) {
    		bean.remove("USER_PASSWORD");
    	}
    	
    	OutBean out = new OutBean();
    	out.setData(list);    	
    	return out;
    }
    
	/**
	 * 通过服务ID获取当前用户流程节点起始的多机构
	 * @param paramBean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public OutBean getMultiDeptContainsRoleByServId(ParamBean paramBean){
	    try {
	    	OutBean nodeRoleBean = getCurrProcStartNodeRoleByServId(paramBean);
			List<Bean> list = (List<Bean>)nodeRoleBean.getData();
			StringBuffer roles = new StringBuffer("");
			for (Bean bean : list) {
				String tempStr = bean.getStr("NODE_ROLE_CODES");
				if (StringUtils.isNotEmpty(tempStr)) {
					roles.append(tempStr);
					if (!tempStr.endsWith(",")) {
						roles.append(",");
					}
				}
			}
			paramBean.set("CURR_FUNC_ROLE", roles.toString());
			return getMultiDeptContainsRoleUserDept(paramBean);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
            return getMultiDept(paramBean);
		}
	}
    
    /**
     * 获取用户多机构信息，包括用户基础信息中挂职的多机构，和角色用户表中的信息
     * @param paramBean
     * @return
     */
    @SuppressWarnings("unchecked")
	public OutBean getMultiDeptContainsRoleUserDept(ParamBean paramBean){
		OutBean outBean = getMultiDept(paramBean);
		UserBean currBean = Context.getUserBean();
		String currFuncRole = paramBean.getStr("CURR_FUNC_ROLE");
		if (StringUtils.isEmpty(currFuncRole)) {
			return outBean;
		}
		String[] roleArr = currFuncRole.split(",");
		StringBuffer sqlBuffer = new StringBuffer(
				" select ROLE_CODE,USER_CODE,SSIC_ID,DEPT_CODE from SY_ORG_ROLE_USER where 1=1 ");
		sqlBuffer.append("and USER_CODE = '" + currBean.getId() + "'");
		StringBuffer tempBuff = new StringBuffer(" and ROLE_CODE in ( ");
		for (int i = 0; i < roleArr.length; i++) {
			tempBuff.append("'");
			tempBuff.append(roleArr[i]);
			tempBuff.append("',");
		}
		
		sqlBuffer.append(tempBuff.substring(0, tempBuff.length()-1));
		sqlBuffer.append(" ) ");

		List<Bean> list = Transaction.getExecutor().query(sqlBuffer.toString());
		if (null != list && !list.isEmpty()) {
			// 查询用户的基本信息和部门信息
			List<Bean> results = new ArrayList<Bean>();
            for (Bean bean : list) {
            	DeptBean tempDeptBean = OrgMgr.getDept(bean.getStr("DEPT_CODE"));
            	tempDeptBean.set("TDEPT_NAME", tempDeptBean.getTDeptBean().getName());
            	tempDeptBean.set("ODEPT_NAME", tempDeptBean.getODeptBean().getName());
            	results.add(tempDeptBean);
			}
			try {
				List<Bean> sourceList = (List<Bean>) outBean.getData();
				// 过滤已经存在于原数据中的部门信息，将过滤完的数据合并
				List<Bean> filterResults = filterDept(sourceList, results);
				sourceList.addAll(filterResults);
				outBean.setData(sourceList);
				
				//构造用户的缓存信息
				UserBean userBean = Context.getUserBean();
				String[] deptItems = {"DEPT_CODE","DEPT_NAME","TDEPT_CODE","TDEPT_NAME","ODEPT_CODE","ODEPT_NAME","CODE_PATH"};
				String userInfoKey = "";
				for (Bean bean : filterResults) {
					UserBean tempUserBean = new UserBean(userBean);
					for (String key : deptItems) {
						tempUserBean.set(key, bean.get(key));
					}
					//标记该用户信息来源于角色用户表
					tempUserBean.set("IS_FROM_ROLE_USER", "1");
					userInfoKey = tempUserBean.getStr("USER_CODE")+"^"+tempUserBean.getStr("DEPT_CODE");
					CacheMgr.getInstance().set(userInfoKey, tempUserBean, "SY_ORG_USER");
				}
				
				return outBean;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				outBean.setData(results);
			}
			return outBean;
		}
		
		return outBean;
    }
    
    /**
     * 根据原数据，过滤掉已经存在于原数据中的部门
     * @param sourceList 原数据
     * @param filterList 待过滤的数据
     * @return
     */
    private List<Bean> filterDept(List<Bean> sourceList, List<Bean> filterList){
		List<Bean> resultList = new ArrayList<Bean>();
		if (null == sourceList && null != filterList) {
			//没有原数据，只有待过滤的数据
			return filterList;
		} else if ((null != sourceList && null == filterList) 
				|| (null == sourceList && null == filterList)) {
			//有原数据，但是没有待过滤的数据  或者全部为null
			return resultList;
		} else {
			List<String> depts = getDeptCodes(sourceList);
			for (Bean bean : filterList) {
				//既不在原数据中，也不再过滤后的新数据中
				if (!depts.contains(bean.getStr("DEPT_CODE"))
						&& !isInSourceDepts(resultList, bean.getStr("DEPT_CODE"))) {
					resultList.add(bean);
				}
			}
			return resultList;
		}
    }
    
    /**
     * 判断部门编码是否存在与列表中
     * @param sourceList
     * @param deptCode
     * @return
     */
    private boolean isInSourceDepts(List<Bean> sourceList, String deptCode){
    	List<String> depts = getDeptCodes(sourceList);
    	if (depts.contains(deptCode)) {
			return true;
		}
    	return false;
    }
    
    /**
     * 从列表中获取部门编码
     * @param sourceList
     * @return
     */
    private List<String> getDeptCodes(List<Bean> sourceList){
    	List<String> depts = new ArrayList<String>();
    	if (null == sourceList || sourceList.isEmpty()) {
			return depts;
		}
    	for (Bean deptBean : sourceList) {
			depts.add(deptBean.getStr("DEPT_CODE"));
		}
    	return depts;
    }
    
    /**
     * 通过服务ID获取起草节点的角色
     * @param paramBean
     * @return
     */
	public OutBean getCurrProcStartNodeRoleByServId(ParamBean paramBean){
		OutBean outBean = new OutBean();
    	String procServId = paramBean.getStr("PROC_SERV_ID");
    	if (StringUtils.isEmpty(procServId)) {
			return outBean.setOk();
		}
		String sql = " select NODE_CODE,NODE_ROLE_CODES from SY_WFE_NODE_DEF where 1=1 and NODE_TYPE=1 and PROC_CODE in (select PROC_CODE from SY_WFE_PROC_DEF where SERV_ID = '"
				+ procServId + "' and PROC_IS_LATEST=1)";
		List<Bean> list = Transaction.getExecutor().query(sql);
		outBean.setData(list);
		return outBean.setOk();
    }
}
 