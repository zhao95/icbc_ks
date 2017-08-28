package com.rh.core.wfe.resource;

/**
 * 流程办理人
 * @author yangjy
 *
 */
public class WfActor {

	private String userId = null;
	private String deptId = null;
	
	public WfActor() {
		
	}
	
	public WfActor(String userId, String deptId) {
		super();
		this.userId = userId;
		this.deptId = deptId;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
}
