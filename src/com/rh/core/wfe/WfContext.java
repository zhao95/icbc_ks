package com.rh.core.wfe;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.comm.mind.UserMind;
import com.rh.core.org.UserBean;

/**
 * 流程处理操作上下文。要实现的目标： <br>
 * 1，共用流程数据，避免多次从数据库查询相同的流程数据。 <br>
 * 2，审批单数据，多次变更，一次保存。<br>
 * 
 * @author yangjy
 */
public class WfContext extends Bean {
    
    private static final long serialVersionUID = 2370637692153812814L;
    
    /** 办理用户 */
    private static final String DO_USER = "DO_USER";
    
    /** 应用数据 */
    private static final String APP_BEAN = "_APP_BEAN";
    
    /**
     * 线程Bean中存放WfContext对象的KEY
     */
    private static final String THREAD_WF_CONTEXT = "_THREAD_WF_CONTEXT";
    
    /**
     * 线程Bean中存放WfAct对象的KEY
     */
    private static final String WF_ACT = "_WF_ACT";
    
    /**
     * 线程Bean中存放用户意见对象的KEY
     */
    private static final String USER_MIND = "_USER_MIND";
    
    /**
     * 获取线程中的WfContext对象，如果没有则创建。
     * 
     * @return WfContext 对象
     */
    public static WfContext getContext() {
        WfContext wfContext = (WfContext) Context.getThread(THREAD_WF_CONTEXT);
        if (wfContext == null) {
            wfContext = new WfContext();
            Context.setThread(THREAD_WF_CONTEXT, wfContext);
        }
        
        return wfContext;
    }
    
    /**
     * @return 办理用户
     */
    public UserBean getDoUser() {
        return (UserBean) this.getBean(DO_USER);
    }
    
    /**
     * @param userBean 办理用户Bean
     */
    public void setDoUser(UserBean userBean) {
        this.set(DO_USER, userBean);
    }
    
    /**
     * @return 取得应用原始数据
     */
    public Bean getAppBean() {
        return this.getBean(APP_BEAN);
    }
    
    /**
     * @param appBean 设置原始的AppData
     */
    public void setAppBean(Bean appBean) {
        this.set(APP_BEAN, appBean);
    }
    
    /**
     * 设置提交请求时，对应的WfAct对象。
     * 
     * @param wfAct wfAct对象
     */
    public void setCurrentWfAct(WfAct wfAct) {
        this.set(WF_ACT, wfAct);
    }
    
    /**
     * @return 取得提交请求时，对应的WfAct对象。
     */
    public WfAct getCurrentWfAct() {
        return (WfAct) this.get(WF_ACT);
    }
    
    /**
     * @return 用户意见对象
     */
    public UserMind getUserMind() {
        return (UserMind) this.get(USER_MIND);
    }
    
    /**
     * 设置用户意见对象
     * 
     * @param userMind 用户意见对象
     */
    public void setUserMind(UserMind userMind) {
        this.set(USER_MIND, userMind);
    }
}
