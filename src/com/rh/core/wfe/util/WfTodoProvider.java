package com.rh.core.wfe.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.comm.todo.TodoBean;
import com.rh.core.comm.todo.TodoUtils;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.JsonUtils;
import com.rh.core.util.Lang;
import com.rh.core.wfe.WfAct;
import com.rh.core.wfe.WfParam;
import com.rh.core.wfe.WfProcess;
import com.rh.core.wfe.def.WfProcDef;

/**
 * 工作流中处理待办的类
 *
 */
public class WfTodoProvider {
	private static Log log = LogFactory.getLog(WfTodoProvider.class);
	
	/**
	 * 送交下一步的时候 添加 待办
	 * @param wfAct 下一个节点实例
	 * @param doneUser 办理人
	 * @param niCount 未汇合节点数量
	 */
    public static void sendTodoForToNext(WfAct wfAct, UserBean doneUser, int niCount) {
//		int toType = wfAct.getNodeInstBean().getInt("TO_TYPE");
		String servId = wfAct.getProcess().getProcDef().getStr("SERV_ID");
		
//		if (toType == WfeConstant.NODE_INST_TO_MULTI_USER) { // 节点任务送交多人
	        //从节点用户中取发送的用户
	        SqlBean sqlBean = new SqlBean();
	        sqlBean.and("NI_ID", wfAct.getId());
	        List<Bean> nodeUsers = ServDao.finds(ServMgr.SY_WFE_NODE_USERS, sqlBean);
			
			for (Bean user: nodeUsers) {
				final String toUserId = user.getStr("TO_USER_ID");
				if(user.isNotEmpty("AUTH_USER")) { //被授权人
					final String[] authedUser = user.getStr("AUTH_USER").split("\\^");
					// 转授权情况下给被授权人发送待办
					sendOneTodo(wfAct, servId, authedUser[0], toUserId,doneUser, niCount);
				} else {
					sendOneTodo(wfAct, servId, toUserId, null, doneUser, niCount);
				}
			}
//		} else if (toType == WfeConstant.NODE_INST_TO_SINGLE_USER) { // 节点任务送交一个处理人
//			String userCode = wfAct.getNodeInstBean().getStr("TO_USER_ID");
//			sendOneTodo(wfAct, servId, userCode, null, doneUser, niCount);
//		}
    }   
    
	private static UserBean findUser(String userCode) {
		if (StringUtils.isBlank(userCode)) {
			return null;
		}
		
		try {
			UserBean sUserBean = UserMgr.getUser(userCode);
			return sUserBean;
		} catch (Exception e) {
			log.error("用户不存在，userCode=" + userCode);
		}

		return null;
	}

    /**
     * 
     * @param wfAct 当前节点实例
     * @param servId 服务ID
     * @param userCode 待办用户编码
     * @param agentUser 授权人
     * @param sendUser 送交人
     * @param niCount 未汇合节点数量
     */
    private static void sendOneTodo(WfAct wfAct, String servId, String userCode, String agentUser, 
    		UserBean sendUser, int niCount) {
    	TodoBean dataBean = new TodoBean();
		dataBean.set("DEPT_CODE", sendUser.getDeptCode());
		dataBean.set("TDEPT_CODE", sendUser.getTDeptCode());
		dataBean.setOwner(userCode);
		dataBean.setSender(sendUser.getCode());
		String title = wfAct.getProcess().getProcInstTitle();
		if(StringUtils.isBlank(title)) {
			title = "无标题";
		}
		if (wfAct.getNodeInstBean().getBoolean("START_NODE")) {
		    title = "[草稿]" + title;
		}
		
        if (niCount > 0) {
            title = title + " (" + niCount + ")";
        }
        
        Bean servInstBean = wfAct.getProcess().getServInstBean();
        
		appendSUserInfo(dataBean, wfAct, servInstBean);
		
        dataBean.setTitle(title);
		dataBean.setCode(servId);
		if(StringUtils.isNotBlank(agentUser)) {
			dataBean.set("AGT_USER_CODE", agentUser);
		}
		String dataId = servInstBean.getId();
		dataBean.setObjectId1(dataId);
		dataBean.setObjectId2(wfAct.getId());
		dataBean.setDeadline1(wfAct.getNodeInstBean().getStr("NODE_LIMIT_TIME")); 
		if (!wfAct.getProcess().getServInstBean().isEmpty("S_EMERGENCY")) {
			dataBean.setEmergency(servInstBean.getInt("S_EMERGENCY"));
		}
		//如果编号不为null则
		ServDefBean servDef = ServUtils.getServDef(servId);
		dataBean.setCodeName(servDef.getName());
        if (servDef.getDataCode().length() > 0) {
            dataBean.setDataCode(ServUtils.replaceValues(servDef.getDataCode(), servId
                    , wfAct.getProcess().getServInstBean()));
        }
        
        //节点名参数放到待办表
        dataBean.setOperation(wfAct.getNodeDef().getStr("NODE_NAME"));
        //标识待办来自工作流
        dataBean.setFrom("wf"); 
		dataBean.setUrl(servId + ".byid.do?data={_PK_:" + dataId
					+ ",NI_ID:" + wfAct.getId() + "}");
		
		
		if(!wfAct.getNodeDef().isEnableTodoRemind()) {
		    dataBean.set("remindFlag", false);
        }
		
		TodoUtils.insert(dataBean);
		
		String expandClass = Context.getSyConf("SY_TODO_NOTIFY_EXPAND_CLASS", ""); 

        if (!StringUtils.isEmpty(expandClass)) {
            TodoNotify todoNotify = null;
            try {
                todoNotify = (TodoNotify) Lang.loadClass(expandClass).newInstance();
                
                todoNotify.send(dataBean, wfAct);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
    
    /**
     * 追加经办人信息以及上一节点名称
     * @param todoBean 待办Bean
     * @param wfAct 当前节点ID
     * @param servInstBean 审批单数据Bean
     */
	private static void appendSUserInfo(TodoBean todoBean, WfAct wfAct, Bean servInstBean) {
		try {
			if (servInstBean.isNotEmpty("S_USER")) {
				String sUser = servInstBean.getStr("S_USER");
				UserBean sUserBean = findUser(sUser);
				todoBean.setSUser(sUser);

				if (servInstBean.isNotEmpty("S_UNAME")) {
					todoBean.setSUserName(servInstBean.getStr("S_UNAME"));
				} else if (sUserBean != null) {
					todoBean.setSUserName(sUserBean.getName());
				}

				if (servInstBean.isNotEmpty("S_DEPT")) {
					todoBean.setSDept(servInstBean.getStr("S_DEPT"));
				} else if (sUserBean != null) {
					todoBean.setSDept(sUserBean.getDeptCode());
				}

				if (servInstBean.isNotEmpty("S_DNAME")) {
					todoBean.setSDeptName(servInstBean.getStr("S_DNAME"));
				} else if (sUserBean != null) {
					todoBean.setSDeptName(sUserBean.getDeptName());
				}
			}

			WfAct preWfAct = wfAct.getPreWfAct();
			if (preWfAct != null) {
				todoBean.setPreOptName(preWfAct.getNodeDef().getStr("NODE_NAME"));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
    
    
	/**
	 * 送交下一步的时候   将本人的待办变成已办
	 * @param wfAct 节点实例
	 * @param paramBean 参数Bean
	 */
    public static void finishTodo(WfAct wfAct, WfParam paramBean) {
    	log.debug("完成待办  finishTodo wfAct ID " + wfAct.getId());
    	
    	SqlBean sqlBean = new SqlBean();
    	sqlBean.and("TODO_OBJECT_ID2", wfAct.getId());
    	sqlBean.and("TODO_CODE", wfAct.getProcess().getProcDef().getStr("SERV_ID"));
    	List<Bean> todoList = ServDao.finds(ServMgr.SY_COMM_TODO, sqlBean);
    	
    	if (null != todoList) {
            for (Bean todoBean : todoList) {
                TodoUtils.endById(todoBean.getId());
            }
    	}
    }
    
    /**
     * 收回的时候，取消被收回人的待办
     * @param wfAct 节点实例
     */
    public static void destroyTodo(WfAct wfAct) {
        log.debug("删除 被收回的 待办 wfAct ID " + wfAct.getId());
    	
		Bean dataBean = new Bean();
		//dataBean.set("OWNER_CODE", Context.getUserBean().getCode());
		dataBean.set("TODO_OBJECT_ID2", wfAct.getId());

		log.debug("finish todo the proc servID = " + wfAct.getProcess().getProcDef().getStr("SERV_ID"));
		dataBean.set("TODO_CODE", wfAct.getProcess().getProcDef().getStr("SERV_ID"));
		
		TodoUtils.destroys(dataBean);
    }
    
	/**
	 * 
	 * @param sendBean 分发记录
	 * @param userBean 退回人的信息
	 * @param todoContent 退回原因
	 */
    public static void sendToDoForTuihui(Bean sendBean, UserBean userBean, String todoContent) {
    	log.debug("分发之后的退回，添加待办");
        TodoBean todo = new TodoBean();
        todo.setOwner(sendBean.getStr("S_USER"));
        
		todo.setSender(userBean.getCode());
		
		String dataId = sendBean.getStr("DATA_ID");
		String servId = sendBean.getStr("SERV_ID");
		
		ServDefBean servDef = ServUtils.getServDef(servId);
		
		String title = getDataTitle(dataId, servDef);
		
		todo.setTitle("退回:" + title);
		todo.setCode(sendBean.getStr("SERV_ID"));
		todo.setCodeName(servDef.getName());
		todo.setObjectId1(dataId);
		todo.setObjectId2(sendBean.getId());
		todo.setUrl(servId + ".showDialog.do");  // url 中加入区分是只显示dialog的内容
		todo.setContent(todoContent);

		TodoUtils.insert(todo);
    }
    
    /**
     * 取得标题
     * @param dataId 审批单ID
     * @param servDef 服务定义
     * @return 返回标题
     */
    private static String getDataTitle(String dataId, ServDefBean servDef) {
        Bean dataBean = ServDao.find(servDef.getId(), dataId);
		String title = ServUtils.replaceValues(servDef.getDataTitle(), servDef.getId(), dataBean);
        return title;
    }
    
    /**
     * 
     * @param servId 服务ID , 如授权建议 LW_AUTH_SUG_COM
     * @param dataId 数据ID , 如授权建议的 表单ID
     */
    public static void deleteTodo(String servId, String dataId) {
    	Bean delBean = new Bean();
    	delBean.set("TODO_OBJECT_ID1", dataId);
    	
    	TodoUtils.destroys(delBean);
    	TodoUtils.updateAllTodoHis2Deleted(dataId);
    }

    /**
     * 流程定义上定义了，在办结的时候给起草人发待办
     * {'ROLE_CODE':'RBMLD','TOSCOPE':'1'}   1 起草部门
                     DRAFTUSER
     * @param wfProcess 流程实例
     * @param doneUser 办理人
     */
    public static void sendTodoFinish(WfProcess wfProcess, UserBean doneUser) {
        WfProcDef procdef = wfProcess.getProcDef();
        
        if (procdef.isEmpty("FINISH_REMIND")) { //没有定义，直接返回
            return;
        }
        
        Bean config = JsonUtils.toBean(procdef.getStr("FINISH_REMIND"));
        
        String servId = procdef.getServId();
        
        if (config.getStr("ROLE_CODE").equals("DRAFTUSER")) { //给起草人发
            String draftUser = wfProcess.getSUserId();
            
            sendFinishTodo(wfProcess, servId, draftUser, doneUser);
        } else {
            String roleCode = config.getStr("ROLE_CODE");
            
            String depts = "";
            List<UserBean> users;
            
            if (config.isNotEmpty("TOSCOPE")) {
                if (config.getStr("TOSCOPE").equals("1")) { //起草部门
                    String draftUser = wfProcess.getSUserId();
                    
                    String tdeptCode = UserMgr.getUser(draftUser).getTDeptCode();
                    
                    depts = getSubDepts(wfProcess.getCmpyId(), tdeptCode);
                    
                } else { //指定
                    String deptCode = config.getStr("TOSCOPE");
                    
                    depts = getSubDepts(wfProcess.getCmpyId(), deptCode);
                }
                
                users = UserMgr.getUsersByDept(depts, roleCode);
            } else { 
                users = UserMgr.getUsersByRole(roleCode);
            }
            
            for (UserBean userBean: users) {
                sendFinishTodo(wfProcess, servId, userBean.getCode(), doneUser);
            }
        }
    }
    
    /**
     * @param cmpyCode 公司编码
     * @param deptCode 部门编码
     * @return 子部门编码
     */
    private static String getSubDepts(String cmpyCode, String deptCode) {
        String depts = "";
        
        List<DeptBean> subDepts = OrgMgr.getChildDepts(cmpyCode, deptCode);
        
        depts += deptCode;
        for (DeptBean deptBean: subDepts) {
            depts += "," + deptBean.getCode();
        }
        
        return depts;
    }
    
    
    /**
     * 发送办结的通知待办
     * @param wfProcess 流程实例
     * @param servId 服务ID
     * @param userCode 待办用户编码
     * @param doneUser 办理人
     */
    private static void sendFinishTodo(WfProcess wfProcess, String servId, String userCode, UserBean doneUser) {
        TodoBean dataBean = new TodoBean();

        dataBean.setOwner(userCode);
        dataBean.setSender(doneUser.getCode());
        dataBean.setTitle("办结通知:" + wfProcess.getProcInstTitle());
        dataBean.setCode(servId);
        String dataId = wfProcess.getServInstBean().getId();
        dataBean.setObjectId1(dataId);
        if (!wfProcess.getServInstBean().isEmpty("S_EMERGENCY")) {
            dataBean.setEmergency(wfProcess.getServInstBean().getInt("S_EMERGENCY"));
        }
        ServDefBean servDef = ServUtils.getServDef(servId);
        dataBean.setCodeName(servDef.getName());
        if (servDef.getDataCode().length() > 0) {
            dataBean.setDataCode(ServUtils.replaceValues(servDef.getDataCode(), servId
                    , wfProcess.getServInstBean()));
        }
        
        dataBean.setCatalog(TodoUtils.TODO_CATLOG_YUE);
        //标识待办来自工作流
        dataBean.setFrom("wf"); 
        dataBean.setUrl(servId + ".byid.do?data={_PK_:" + dataId + "}");
        
        TodoUtils.insert(dataBean);
    }
}
