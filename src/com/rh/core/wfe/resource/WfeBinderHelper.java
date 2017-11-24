package com.rh.core.wfe.resource;

import java.lang.reflect.Constructor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.org.UserBean;
import com.rh.core.serv.OutBean;
import com.rh.core.util.Lang;
import com.rh.core.wfe.WfAct;
import com.rh.core.wfe.def.WfNodeDef;

/**
 * 
 * @author yangjy
 *
 */
public class WfeBinderHelper {
	private static Log log = LogFactory.getLog(WfeBinderHelper.class);
	/**
	 * 
	 */
	private static final String CONF_WFE_BINDER_CLS = "sy.wfe.binder.cls";
	
	private static final String CONF_WFE_BINDER_MGR_CLS = "sy.wfe.binder_mgr.cls";
	
    /**
     * 创建一个带根节点的WfeBinder对象
     */
	public static WfeBinder createWfeBinderWithRoot() {
		WfeBinder binder = createWfeBinder();
		binder.setBinderType(WfeBinder.NODE_BIND_USER);
		binder.setRootBean(addRootNode());

		return binder;
	}
    
	/**
	 * 根据系统配置创建WfeBinder，默认为PlainWfeBinder
	 * 
	 * @return 创建一个WfeBinder对象
	 */
	public static WfeBinder createWfeBinder() {
		String cls = Context.getSyConf(CONF_WFE_BINDER_CLS, "com.rh.core.wfe.resource.PlainWfeBinder");
		return Lang.createObject(WfeBinder.class, cls);
	}
	
	/**
	 * 根据系统配置创建WfBinderManager对象，默认为PlainWfBinderManager对象。
	 * 
	 * @return 创建一个WfBinderManager对象
	 */
	@SuppressWarnings("unchecked")
	public static WfBinderManager createWfBinderManager(WfNodeDef aWfNodeDef, WfAct aWfAct, UserBean aDoUser) {
		String cls = Context.getSyConf(CONF_WFE_BINDER_MGR_CLS, "com.rh.core.wfe.resource.PlainWfBinderManager");

		try {
			Class<WfBinderManager> clsObj = Lang.loadClass(cls);
			if (clsObj == null) {
				throw new TipException(cls + "类不存在");
			}
			Constructor<WfBinderManager> constru = clsObj.getConstructor(WfNodeDef.class, WfAct.class, UserBean.class);
			return constru.newInstance(aWfNodeDef, aWfAct, aDoUser);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new TipException(e.getMessage());
		}
	}
	
    
    /**
     * 返回的数据添加一个根节点
     * @param rootNodeId 根节点的ID
     */
    private static Bean addRootNode() {
        final String rootNodeId = "_ROOT";
        Bean newDeptBean = new Bean();
        newDeptBean.set("CODE", rootNodeId);
        newDeptBean.set("NAME", "工作流人员");
        newDeptBean.set("ID", rootNodeId);
        newDeptBean.set("SORT", 0);
        newDeptBean.set("LEVEL", 0);
        
        return newDeptBean;
    }
    
    /**
     * 在WfBinder对象的根节点上增加用户节点
     * @param binder binder对象
     * @param userBean 用户属性Bean
     * @return 通过参数传递进来的WfBinder对象
     */
    public static WfeBinder addUserInRoot(WfeBinder binder, UserBean userBean) {
        Bean binderBean = new Bean();
        binderBean.set("CODE", userBean.getCode());
        binderBean.set("NAME", userBean.getName());
        binderBean.set("NODETYPE", WfeBinder.USER_NODE_PREFIX);
        binderBean.set("ID", WfeBinder.USER_NODE_PREFIX + ":" + userBean.getCode() + "^" + userBean.getDeptCode());
        binderBean.set("SORT", userBean.getSort());
        binderBean.set("LEVEL", 999);
        binderBean.set("PID", binder.getRootBean().getStr("ID"));
        binder.addTreeBean(binderBean);
        
        return binder;
    }
    
    /**
     * 把WfeBinder对象的数据输出到OutBean中，供前台使用。
     * @param binder  WfeBinder对象
     * @return OutBean
     */
    public static OutBean toOutBean(WfeBinder binder) {
        OutBean rtnBean = new OutBean();
        rtnBean.set("treeData", binder.getBinders()); // 树的数据
        rtnBean.set("multiSelect", binder.isMutilSelect()); // 是否多选
        rtnBean.set("binderType", binder.getBinderType()); // 角色还是用户
        rtnBean.set("roleCode", binder.getRoleCode()); // 如果是角色，将角色code 带上
        // 是否需要自动选中用户
        rtnBean.set("autoSelect", binder.getAutoSelect());
        
        return rtnBean;
    }
}
