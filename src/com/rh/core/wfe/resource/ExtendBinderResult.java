package com.rh.core.wfe.resource;

import java.util.HashSet;

import org.apache.commons.lang.StringUtils;

import com.rh.core.util.Constant;


/**
 * 记录组织资源过滤的结果。class中有两个属性，deptIDs和userIDs，分别表示部门ID和用户ID，多个ID之间使用逗号","分隔。
 * 
 * @author yangjy
 */
public class ExtendBinderResult {
    
    private boolean bindRole = false;
    
    private String roleCodes = "";
    
    private String deptIDs = "";
    
    private String userIDs = "";
    
    // 是否自动选中用户
    private int autoSelect = Constant.NO_INT;
    
    // 组织资源选取对象
    private WfeBinder binder = null;
    
    // 需要去掉的用户
    private HashSet<String> excludeUsers = new HashSet<String>();
    
    // 
    protected boolean ignoreCurrentUser = false;

    /**
     * @return 部门ID，多个ID之间使用逗号","分隔。
     */
    public String getDeptIDs() {
        return deptIDs;
    }
    
    /**
     * @param aDeptIDs 部门ID，多个ID之间使用逗号","分隔。
     */
    public void setDeptIDs(String aDeptIDs) {
        this.deptIDs = aDeptIDs;
    }
    
    /**
     * @return 用户ID，多个ID之间使用逗号","分隔。
     */
    public String getUserIDs() {
        return userIDs;
    }
    
    /**
     * @param aUserIDs 用户ID，多个ID之间使用逗号","分隔。
     */
    public void setUserIDs(String aUserIDs) {
        this.userIDs = aUserIDs;
    }
    
    /**
     * @return 角色编号，多个编号之间使用英文逗号分隔。
     */
    public String getRoleCodes() {
        return roleCodes;
    }
    
    /**
     * @param aRoleCodes 角色编号，多个编号之间使用英文逗号分隔。
     */
    public void setRoleCodes(String aRoleCodes) {
        this.roleCodes = aRoleCodes;
    }
    
    /**
     * @return 覆盖父类的toString方法
     */
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("deptIDs:").append(this.deptIDs);
        str.append("; userIDs:").append(this.userIDs);
        str.append("; roleCodes:").append(this.roleCodes);
        str.append("; bindRole:").append(this.bindRole);
        return str.toString();
    }
    
    /**
     * @return 是否送角色，默认为不送
     */
    public boolean isBindRole() {
        return bindRole;
    }
    
    /**
     * @param aBindRole 是否送角色，默认为不送
     */
    public void setBindRole(boolean aBindRole) {
        this.bindRole = aBindRole;
    }
    
    /**
     * @return 是否自动选中用户
     */
    public int getAutoSelect() {
        return autoSelect;
    }
    
    /**
     * @param aAutoSelect 是否自动选中用户
     */
    public void setAutoSelect(int aAutoSelect) {
        this.autoSelect = aAutoSelect;
    }

    /**
     * 
     * @return 组织资源选取对象
     */
    public WfeBinder getBinder() {
        return binder;
    }

    /**
     * 
     * @param binder 组织资源选取对象
     */
    public void setBinder(WfeBinder binder) {
        this.binder = binder;
    }
    
    
    /**
     * 添加需要排除的用户ID
     * @param userIds 用户ID，多个ID之间使用逗号分隔
     */
    public void addExcludeUsers(String userIds) {
        if(userIds == null) {
            return;
        }
        for (String userCode: userIds.split(",")) {
            if (StringUtils.isBlank(userCode)) {
                continue;
            }
            
            excludeUsers.add(userCode);
        }
    }
    
    /**
     * @return 需要排除的用户ID
     */
    public HashSet<String> getExcludeUsers() {
        return this.excludeUsers;
    }
    
	public boolean isIgnoreCurrentUser() {
		return ignoreCurrentUser;
	}

	public void setIgnoreCurrentUser(boolean ignore) {
		ignoreCurrentUser = ignore;
	}
}
