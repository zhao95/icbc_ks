package com.rh.core.wfe.resource;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.org.UserBean;

/**
 * 
 * @author yangjinyun
 *
 */
public interface WfeBinder {

	/**
	 * 用户ID的前缀
	 */
	String USER_NODE_PREFIX = "usr";
	/**
	 * 部门ID前缀
	 */
	String DEPT_NODE_PREFIX = "dept";
	/**
	 * 角色ID前缀
	 */
	String ROLE_NODE_PREFIX = "role";
	/**
	 * 本部门
	 */
	String PRE_DEF_SELF_DEPT = "s";
	/**
	 * 本级部门
	 */
	String PRE_DEF_SELF_DEPT_LEVEL = "s0";
	/**
	 * 上级部门
	 */
	String PRE_DEF_HIGHER_DEPT_LEVEL = "higher";
	/**
	 * 拟稿部门
	 */
	String PRE_DEF_INIT_TOP_DEPT = "INIT_TOP_DEPT";
	/**
	 * 拟稿处室
	 */
	String PRE_DEF_INIT_DEPT = "INIT_DEPT";
	/**
	 * 拟稿机构
	 */
	String PRE_DEF_INIT_ORG = "INIT_ORG";
	/** 下级机构 **/
	String PRE_DEF_SUB_ORG = "SUB_ORG";
	/**
	 * 绑定类型 人
	 */
	String NODE_BIND_USER = "USER";
	/**
	 * 绑定类型  角色
	 */
	String NODE_BIND_ROLE = "ROLE";
	/**
	 * 自动选中并可以修改选中结果
	 */
	int AUTO_SELECT_SELECTED = 1;
	/**
	 * 自动选中并且不能修改选中的结果
	 */
	int AUTO_SLECT_NO_MODIFY = 3;
	
	/**
	 * 
	 * @return 办理用户是否忽略当前用户
	 */
	boolean isIgnoreCurrentUser();
	
	/**
	 * 
	 * @param ignore 办理用户是否忽略当前用户，true忽略，false不忽略。默认为true。
	 */
	void setIgnoreCurrentUser(boolean ignore);
	
	/**
	 * 
	 * @return 显示方式，目前支持的方式有TREE和COMBOBOX
	 */
	String getDisplayType();
	
	/**
	 * 设置当前办理用户
	 * @param doUserBean 当前办理用户Bean
	 */
	void setDoUserBean(UserBean doUserBean);

	/**
	 * 添加节点
	 * @param binderBean 节点数据， 需要包含的数据：CODE（用户ID）、NAME（姓名）、NODETYPE（节点类型）、ID（user_ + 用户ID）、SORT（排序号）、LEVEL（级别）、PID（父节点ID）。
	 */
	void addTreeBean(Bean binderBean);

	/**
	 * @return 是否自动选中用户
	 */
	int getAutoSelect();

	/**
	 * 将对象转成json串给前台，用于树的显示
	 * @return 树上组织资源的字符串
	 */
	String getBinders();

	/**
	 * @return 绑定类型
	 */
	String getBinderType();

	/**
	 * @return 处理人组（用户自动送交下一个节点，不用选择，例如：返回XXX）
	 */
	List<GroupBean> getGroupBeanList();

	/**
	 * 
	 * @return 角色编码
	 */
	String getRoleCode();

	/**
	 * @return 获取根节点Bean
	 */
	Bean getRootBean();

	/**
	 * 
	 * @return 树的节点list
	 */
	List<Bean> getTreeBeanList();

	/**
	 * 
	 * @return 可选择送交的用户列表（用于自动流转）
	 */
	List<UserBean> getUserBeanList();

	/**
	 * 
	 * @return 能否多选
	 */
	boolean isMutilSelect();

	/**
	 * @param aAutoSelect 是否自动选中用户
	 */
	void setAutoSelect(int autoSelect);

	/**
	 * @param aBinderType 绑定类型
	 */
	void setBinderType(String aBinderType);

	/**
	 * 设置处理人组
	 * @param groupBeanList 处理人组
	 */
	void setGroupBeanList(List<GroupBean> groupBeanList);

	/**
	 * 
	 * @param mutilSelect 能否多选
	 */
	void setMutilSelect(boolean mutilSelect);

	/**
	 * 
	 * @param aRoleCode 角色编码
	 */
	void setRoleCode(String aRoleCode);

	/**
	 * 设置根节点Bean
	 * @param root 根节点
	 */
	void setRootBean(Bean root);

	/**
	 * 设置可选择送交的用户列表
	 * @param userBeanList 可选择送交的用户列表
	 */
	void setUserBeanList(List<UserBean> userBeanList);

	/**
	 * 设置可选择送交的用户列表
	 * @param userList 可选择送交的用户列表
	 */
	void setUserBeanListByBeanList(List<Bean> userList);

}