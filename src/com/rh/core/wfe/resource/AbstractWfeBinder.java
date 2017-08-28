package com.rh.core.wfe.resource;

import java.util.ArrayList;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.org.UserBean;

/**
 * 
 * @author yangjy
 *
 */
public abstract class AbstractWfeBinder implements WfeBinder {
	
	protected String binderType; // role, user
    
	protected String roleCode; //如果binderType是角色，就将角色带上
    
    /** 是否需要自动选中用户 */
	protected int autoSelect = 2;
    
	/** 能否多选 */
	protected boolean multiSelect = false;
	
	protected boolean ignoreCurrentUser = false;
	
	protected String displayType = null;
    
    /** 返回的过滤之后的树的节点列表 */
	protected List<Bean> treeBeanList = new ArrayList<Bean>();
	
    /** 返回过滤后的组的信息 由GroupExtendBinder返回 */
    private List<GroupBean> groupBeanList = new ArrayList<GroupBean>();
    
    /** 可选择的用户列表 **/
    private List<UserBean> userBeanList = new ArrayList<UserBean>();
    
    /** 根节点  **/
    protected Bean rootBean = null;
    
    /** 当前办理用户  **/
    protected UserBean doUserBean = null;
    
    @Override
	public int getAutoSelect() {
        return autoSelect;
    }

    @Override
	public void setAutoSelect(int autoSelect) {
        this.autoSelect = autoSelect;
    }
    
    @Override
	public String getRoleCode() {
        return roleCode;
    }

    @Override
	public void setRoleCode(String aRoleCode) {
        this.roleCode = aRoleCode;
    }

    @Override
	public String getBinderType() {
        return binderType;
    }

    @Override
	public void setBinderType(String aBinderType) {
        this.binderType = aBinderType;
    }
    
    @Override
	public boolean isMutilSelect() {
        return multiSelect;
    }

    @Override
	public void setMutilSelect(boolean mutilSelect) {
        this.multiSelect = mutilSelect;
    }
    
    @Override
	public List<Bean> getTreeBeanList() {
        return treeBeanList;
    }
    
    @Override
	public void setRootBean(Bean root) {
        rootBean = root;
        addTreeBean(root);
    }
    
    @Override
	public Bean getRootBean() {
        return rootBean;
    }

    @Override
	public List<GroupBean> getGroupBeanList() {
        return groupBeanList;
    }


    @Override
	public void setGroupBeanList(List<GroupBean> groupBeanList) {
        this.groupBeanList = groupBeanList;
    }

	@Override
	public List<UserBean> getUserBeanList() {
		return userBeanList;
	}

	@Override
	public void setUserBeanList(List<UserBean> userBeanList) {
		this.userBeanList = userBeanList;
	}
	
    @Override
	public void setUserBeanListByBeanList(List<Bean> userList) {
    	if (userList != null && userList.size() > 0) {
    		for (Bean user : userList) {
        		this.userBeanList.add(new UserBean(user));
        	}
    	}
    }

	@Override
	public boolean isIgnoreCurrentUser() {
		return ignoreCurrentUser;
	}

	@Override
	public void setIgnoreCurrentUser(boolean ignore) {
		ignoreCurrentUser = ignore;
	}
	
	@Override
	public void setDoUserBean(UserBean doUserBean) {
		this.doUserBean = doUserBean;
	}
}
