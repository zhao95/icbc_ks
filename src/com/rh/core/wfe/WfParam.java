package com.rh.core.wfe;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.ParamBean;
import com.rh.core.wfe.util.WfeConstant;

/**
 * 工作流 参数
 * 
 * @author ananyuan
 *
 */
public class WfParam extends Bean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3788769525529136356L;

	/**
	 * 节点送交类型：1 送部门+角色
	 */
	public static final int TYPE_TO_DEPT_ROLE = 1;

	/**
	 * 节点送交类型：2 送角色
	 */
	public static final int TYPE_TO_ROLE = 2;

	/**
	 * 节点送交类型：3 送用户
	 */
	public static final int TYPE_TO_USER = 3;

	/**
	 * 是否是代理办理
	 */
	public static final String IS_AGENT = "_IS_AGENT_USER_";

	// 办理人
	private UserBean doneUser;

	// 送交人USER_CODE^DEPT_CODE，多个以逗号分隔
	private String toUser = "";

	// 送交人用户Ids
	private String[] toUserIds = null;

	// 送交人所在部门id
	private String[] toUserDeptIds = null;

	// 送交角色所在部门id，多个id之间以逗号分隔
	private String toRoleDept = "";

	// 送交的角色
	private String toRole = "";

	// 送交类型
	private int typeTo = TYPE_TO_USER;

	// 办理类型：3收回、2终止、1正常结束
	private int doneType = WfeConstant.NODE_DONE_TYPE_END;

	// 办理类型说明
	private String doneDesc = WfeConstant.NODE_DONE_TYPE_END_DESC;
	
	/** 授权人用户信息UserBean **/
	private UserBean authedUserBean = null;

	/**
	 * 
	 * @return 送 交部门
	 */
	public String getToRoleDept() {
		return toRoleDept;
	}

	/**
	 * 
	 * @param aToDept
	 *            送 交部门
	 */
	public void setToRoleDept(String aToDept) {
		this.toRoleDept = aToDept;
	}

	/**
	 * 
	 * @return 送交角色
	 */
	public String getToRole() {
		return toRole;
	}

	/**
	 * 
	 * @param aToRole
	 *            送交角色
	 */
	public void setToRole(String aToRole) {
		this.toRole = aToRole;
	}

	/**
	 * 
	 * @return 办理类型
	 */
	public int getDoneType() {
		return doneType;
	}

	/**
	 * 
	 * @param aDoneType
	 *            办理类型
	 */
	public void setDoneType(int aDoneType) {
		this.doneType = aDoneType;
	}

	/**
	 * 
	 * @return 办理说明
	 */
	public String getDoneDesc() {
		return doneDesc;
	}

	/**
	 * 
	 * @param aDoneDesc
	 *            办理说明
	 */
	public void setDoneDesc(String aDoneDesc) {
		this.doneDesc = aDoneDesc;
	}

	/**
	 * 送交人
	 * 
	 * @return 送交人
	 */
	public String[] getToUserIdArray() {
		if (this.toUserIds != null) {
			return this.toUserIds;
		}

		splitToUsers();

		return this.toUserIds;
	}

	/**
	 * 
	 * @param user
	 *            送交人
	 */
	public void setToUser(String user) {
		toUser = user;
	}

	/**
	 * 
	 * @param type
	 *            送交类型
	 */
	public void setTypeTo(int type) {
		typeTo = type;
	}

	/**
	 * 
	 * @return 是否送交用户
	 */
	public boolean isToUser() {
		if (typeTo == TYPE_TO_USER) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return 是否送交部门+角色
	 */
	public boolean isToDeptRole() {
		if (typeTo == TYPE_TO_DEPT_ROLE) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return 是否送交角色
	 */
	public boolean isToRole() {
		if (typeTo == TYPE_TO_ROLE) {
			return true;
		} else if (typeTo == TYPE_TO_DEPT_ROLE) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return 送交 类型
	 */
	public int getTypeTo() {
		return typeTo;
	}

	/**
	 * 
	 * @return 办理人
	 */
	public UserBean getDoneUser() {
		return doneUser;
	}

	/**
	 * TODO 必须给正确的UserBean
	 * 
	 * @param aDoneUser
	 *            办理人
	 */
	public void setDoneUser(UserBean aDoneUser) {
		this.doneUser = aDoneUser;
	}

	/**
	 * 
	 * @return 是否处于转授权状态
	 */
	public boolean isAuthState() {
		if(this.authedUserBean != null) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param authUserBean
	 *            被授权人用户信息
	 */
	public void setAuthedUserBean(UserBean authUserBean) {
		this.authedUserBean = authUserBean;
	}
	
	/**
	 * 
	 * @return 被授权人用户信息
	 */
	public UserBean getAuthedUserBean() {
		return this.authedUserBean;
	}

	/**
	 * 
	 * @return 办理用户所在部门
	 */
	public String[] getToUserDeptIdArray() {
		if (this.toUserDeptIds != null) {
			return toUserDeptIds;
		}

		splitToUsers();

		return this.toUserDeptIds;
	}

	/**
	 * 分隔ToUser数据
	 */
	private void splitToUsers() {
		final String[] toUserIds = toUser.split(",");
		int len = toUserIds.length;
		this.toUserIds = new String[len];
		this.toUserDeptIds = new String[len];

		for (int i = 0; i < len; i++) {
			String[] users = toUserIds[i].split("@");
			String[] ud = users[0].split("\\^");
			if (ud.length != 2) {
				throw new TipException("错误的办理人参数格式，正确的格式为：USER_CODE^DEPT_CODE");
			}
			this.toUserIds[i] = ud[0];
			if(users.length > 1) {
				this.toUserDeptIds[i] = ud[1] + "@" + users[1];
			} else {
				this.toUserDeptIds[i] = ud[1];
			}
		}
	}
	
    /**
     * 
     * @param paramBean 参数Bean
     * @return 创建流程参数Bean
     */
    public static WfParam createWfParamAndFillDoUser(ParamBean paramBean) {
        WfParam wfParam = new WfParam();
        if(paramBean.isEmpty("DO_USER_DEPT")) {
        	throw new TipException("参数DO_USER_DEPT不能为空。");
        } else {
        	wfParam.set("DO_USER_DEPT", paramBean.getStr("DO_USER_DEPT"));
        }
        
		String doUserDept = paramBean.getStr("DO_USER_DEPT");
		final int pos = doUserDept.indexOf("@");
		if(pos > 0) {
			doUserDept = doUserDept.substring(0, pos);
		}
        
        UserBean doneUser = UserMgr.getUserByUserDept(doUserDept);
        wfParam.setDoneUser(doneUser);
        
        // 有转授权用户，则
        if (pos > 0) {
        	String srcDoUserDept = paramBean.getStr("DO_USER_DEPT");
        	UserBean authUserBean = UserMgr.getUserByUserDept(srcDoUserDept.substring(pos + 1));
            wfParam.setAuthedUserBean(authUserBean);
        }
        return wfParam;
    }
}
