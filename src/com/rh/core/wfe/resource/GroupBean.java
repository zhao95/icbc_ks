/**
 * 
 */
package com.rh.core.wfe.resource;

import java.util.ArrayList;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.org.mgr.UserMgr;

/**
 * 任务分配中的组。每个GroupBean实例作为一个任务实例的抢占式处理人。每个GroupBean中有1到多个用户。
 * @author 郭艳红
 *
 */
public class GroupBean extends Bean {

    /**  */
    private static final long serialVersionUID = 1L;
    
    /** 组名，可以使用角色、部门编码等作为组名。可以为空。 */
    private String name = null;
    
    /** 组中的成员 */
    private List<WfActor> actors = new ArrayList<WfActor>();
    
    /** 是否发送待办，默认为true **/
    private boolean ifSendTodo = true;
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return 返回所有办理人
     */
    public List<WfActor> getAllActors() {
    	return actors;
    }
    
    
    /**
     * @return 逗号分隔的用户Name字符串
     */
    public String getUserNames() {
        StringBuilder sb = new StringBuilder();
        for(WfActor actor: actors) {
        	sb.append(UserMgr.getUser(actor.getUserId()).getName()).append(",");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }   
    
    /**
     * @return 逗号分隔的用户Id字符串
     */
    public String getUserIdStr() {
        StringBuilder sb = new StringBuilder();
        for(WfActor actor: actors) {
        	sb.append(actor.getUserId()).append(",");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
    
    
    /**
     * 向组中加入用户
     * @param userId 用户id
     */
	public void addUser(String userId, String deptId) {
		WfActor actor = new WfActor();
		actor.setDeptId(deptId);
		actor.setUserId(userId);
		this.actors.add(actor);
	}
    
    
    /**
     * 向组中加入用户
     * @param userIds 用户id数组
     */
    public void addUsers(String[] userIds, String[] deptIds) {
		if (userIds == null || deptIds == null) {
			return;
		}
		
		if(userIds.length != deptIds.length){
			throw new TipException("userIds 和 deptIds 的数据长度不一致");
		}
    	
        if (userIds != null && userIds.length > 0) {
            for (int i = 0; i < userIds.length; i++) {
        		WfActor actor = new WfActor();
        		actor.setDeptId(deptIds[i]);
        		actor.setUserId(userIds[i]);
                this.actors.add(actor);
            }
        }
        
    }
    
    /**
     * 
     * @param list
     */
    public void addUsers(List<WfActor> list) {
    	this.actors.addAll(list);
    }
    
    
    /**
     * 向组中加入用户
     * @param userIds 用户id列表
     */
    public void addUsers(List<String> userIds, List<String> deptIds) {
		if (userIds == null || deptIds == null) {
			return;
		}
		
		if(userIds.size() != deptIds.size() ){
			throw new TipException("userIds 和 deptIds 的数据长度不一致");
		}
    	
        for (int i = 0; i < userIds.size(); i++) {
    		WfActor actor = new WfActor();
    		actor.setDeptId(deptIds.get(i));
    		actor.setUserId(userIds.get(i));
            this.actors.add(actor);
        }
    }
    
    
    /**
     * 从组中移除用户
     * @param userId 用户id
     */
	public void removeUser(String userId, String deptId) {
		for (WfActor actor : this.actors) {
			if (actor.getDeptId().equals(deptId) && actor.getUserId().equals(userId)) {
				this.actors.remove(actor);
			}
		}
	}

	public boolean isIfSendTodo() {
		return ifSendTodo;
	}

	public void setIfSendTodo(boolean ifSendTodo) {
		this.ifSendTodo = ifSendTodo;
	}
    
}
