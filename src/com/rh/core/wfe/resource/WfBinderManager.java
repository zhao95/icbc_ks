package com.rh.core.wfe.resource;

import java.util.Collection;
import java.util.HashSet;

import com.rh.core.base.Bean;
import com.rh.core.org.UserBean;
import com.rh.core.util.Constant;
import com.rh.core.wfe.WfAct;
import com.rh.core.wfe.def.WfNodeDef;
import com.rh.core.wfe.util.WfeConstant;

/**
 * 
 * @author yangjy
 *
 */
public abstract class WfBinderManager {
	
    // 节点定义
    protected WfNodeDef wfNodeDef;
    
    // 节点实例
    protected WfAct wfAct;
    
    // 办理人对象
    protected UserBean doUser;
    
    //办理人所在公司
    protected String cmpyCode = "";
    
    /** 显示组织机构树是默认向上取几级 **/
    protected int topLevel = 1;
    
    
    //资源绑定类型：ROLE角色 ROLE用户
    protected String bindMode = WfeBinder.NODE_BIND_USER;
    
    protected BinderResource deptBinder = new BinderResource();
    
    protected BinderResource userBinder = new BinderResource();
    
    protected BinderResource roleBinder = new BinderResource();
    
    protected String extendCls = "";
    
    protected HashSet<String> excludeUsers = new HashSet<String>();
    
    protected int multUser = Constant.NO_INT; // 多人竞争
    
    protected int autoSelect = Constant.NO_INT; // 自动选择
	
    /**
     * @param aWfNodeDef 节点定义
     * @param aWfAct 节点实例
     * @param aDoUser 办理人对象
     */
    public WfBinderManager(WfNodeDef aWfNodeDef , WfAct aWfAct, UserBean aDoUser) {
        this.wfNodeDef = aWfNodeDef;
        this.wfAct = aWfAct;
        this.doUser = aDoUser;
        this.cmpyCode = this.doUser.getCmpyCode();
        
        if (this.doUser.getODeptLevel() > 1) {
            topLevel = this.doUser.getODeptLevel() - 1;
        }
    }
    
	/**
     * 绑定资源
     */
    protected class BinderResource {
        
        private String resCodes = "";
        
        private int mode = WfeConstant.NODE_BIND_MODE_ALL; // 默认给全部
        
        private String scripts = "";
        
        /**
         * @return 绑定类型
         */
        public int getMode() {
            return mode;
        }
        
        /**
         * @return 是否存在
         */
        public String getResCodes() {
            return resCodes;
        }
        
        /**
         * @return 过滤条件
         */
        public String getScripts() {
            return scripts;
        }
        
        /**
         * 绑定类型
         * 
         * @param bindMode 绑定类型
         */
        public void setMode(int bindMode) {
            this.mode = bindMode;
        }
        
        /**
         * @param resCode 是否存在
         */
        public void setResCodes(String resCode) {
            this.resCodes = resCode;
        }
        
        /**
         * @param script 过滤条件
         */
        public void setScripts(String script) {
            this.scripts = script;
        }
    }
	

	/**
	 * @return 节点绑定
	 */
    public abstract WfeBinder getWfeBinder();

	/**
	 * 初始化绑定资源信息
	 * @param orgDefBean 组织资源定义Bean（可能来自线定义，也可能来自节点定义）
	 */
	public abstract void initBinderResource(Bean orgDefBean);

	/**
	 * 添加排除的用户
	 * @param userCode 用户编码 , 多个以逗号分隔
	 */
	public abstract void addExcludeUsers(String userCodes);

	/**
	 * 添加排除的用户
	 * @param userCode 用户编码 , 多个以逗号分隔
	 */
	public abstract void addExcludeUsers(Collection<String> userIds);

}