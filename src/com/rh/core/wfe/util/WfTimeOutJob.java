package com.rh.core.wfe.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.comm.remind.RemindMgr;
import com.rh.core.comm.workday.WorkTime;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;
import com.rh.core.util.Strings;
import com.rh.core.util.scheduler.RhJob;
import com.rh.core.util.scheduler.RhJobContext;
import com.rh.core.wfe.WfAct;
import com.rh.core.wfe.db.WfNodeInstDao;
import com.rh.core.wfe.db.WfNodeUserDao;
import com.rh.core.wfe.db.WfProcInstDao;
import com.rh.core.wfe.def.WfNodeDef;
import com.rh.core.wfe.def.WfProcDef;
import com.rh.core.wfe.def.WfProcDefManager;
import com.rh.core.wfe.remind.RemindServ;
import com.rh.core.wfe.remind.RemindUtils;
import com.rh.core.wfe.remind.WfeRemindItem;

/**
 * 节点超时处理
 * 
 * @author anan
 * 
 */
public class WfTimeOutJob extends RhJob {

	private Log log = LogFactory.getLog(WfTimeOutJob.class);

	/** 节点超时提醒 */ 
	public static final String SY_WF_TIMEOUT_REMIND = "SY_WF_TIMEOUT_REMIND";
	
	/**节点超时提醒内容*/
	public static final String OA_TIMEOUT_REMIND_CONTENT = "OA_TIMEOUT_REMIND_CONTENT";
	
	/**领导角色配置*/
	public static final String OA_TIMEOUT_ROLE_CODES = "OA_TIMEOUT_ROLE_CODES";
	
	/**强制提醒类型*/
	public static final String SY_FORCE_REMIND_TYPE = "SY_FORCE_REMIND_TYPE";
	
	/**
	 * 
	 */
    public void doWorks() {
        Transaction.begin();
        
        try {
            StringBuilder baseWhere = getBaseWhere();
    
            List<Bean> procInstList = getProcList(baseWhere); //查到需要催办的流程
            List<Bean> nodeInstList = getNodeInstList(baseWhere); //查到需要催办的节点
    
            //流程实例ID, 流程实例Bean 
            Map<String, Bean> procInstMap = getProcInstMap(procInstList);
            
            Map<String, Bean> entiryMap = getEntityMap(procInstMap);
            
            for (Bean nodeInst: nodeInstList) {
                String procCode = nodeInst.getStr("PROC_CODE");
                
                WfProcDef procDef = WfProcDefManager.getWorkflowDef(procCode);
                
                if (procDef != null) {
                    String nodeCode = nodeInst.getStr("NODE_CODE");
                    
                    try {
                        WfNodeDef nodeDef = procDef.findNode(nodeCode);
                        
                        Bean procInst = procInstMap.get(nodeInst.getStr("PI_ID"));
                        
                        Bean entity = entiryMap.get(nodeInst.getStr("PI_ID"));
                        
                        doRemind(procDef, procInst, nodeDef, nodeInst, entity);
                        
                        Transaction.commit();
                    } catch (RuntimeException e) {
                        log.debug("没找到节点定义", e);
                    }
                }
            }
        } finally {
            Transaction.end();
        }
    }


    /**
	 * @param procDef 流程定义
	 * @param procInst 流程实例ID
	 * @param nodeDef 节点定义
	 * @param nodeInst 节点实例
	 * @param entity 实体表
	 */
	private void doRemind(WfProcDef procDef, Bean procInst, WfNodeDef nodeDef, Bean nodeInst, Bean entity) {
	    if (null == entity) { //没对应上实体表
	        return;
	    }
	    
	    int emercyValue = entity.getInt("S_EMERGENCY");
        
	    Bean emergenBean = nodeDef.getEmerGency(emercyValue);
	    
	    //节点上定义的超时时间
	    if (null == emergenBean || emergenBean.isEmpty("TIMEOUT") || emergenBean.getInt("TIMEOUT") == 0) {
	        return;
	    }
	    
	    String userTodo = nodeInst.getStr("TO_USER_ID");
	    
	    String remindTypes = emergenBean.getStr("REMIND"); //提醒
	    
	    String procName = procDef.getStr("PROC_NAME");
	    String nodeName = nodeDef.getStr("NODE_NAME");
	    addRemind(procName, nodeName, entity, remindTypes, userTodo, procInst.getId(), nodeInst.getId() , nodeDef);
	    
	    String operTypes = emergenBean.getStr("OPER"); //操作
	    String[] operTypeArray = operTypes.split(",");
        for (int i = 0; i < operTypeArray.length; i++) {
            if (operTypeArray[i].equals("CUIBAN")) { //催办
                addCuiBan(entity, nodeInst);
                
                
            } else if (operTypeArray[i].equals("BACK")) { //退回
                doTuiHui(procInst, nodeInst); 
            }
        }
        
        String remindLog = "{'REMIND':'" + remindTypes + "','OPER':'" + operTypes + "'}";
        updateNodeRemindState(nodeInst.getId(), remindLog);
    }

	/**
	 * 自动退回
	 * @param procInst 流程实例
	 * @param nodeInst 节点实例
	 */
	private void doTuiHui(Bean procInst, Bean nodeInst) {
		if (nodeInst.isNotEmpty("PRE_NI_ID")) { //前面的节点不为空
			WfAct wfAct = new WfAct(nodeInst.getId(), true);  //需要退回的，一定是未办结的数据
			
			final String preNiId = nodeInst.getStr("PRE_NI_ID");
//			Bean preNiBean = WfNodeInstDao.findNodeInstById(preNiId);
			Bean preNiBean = wfAct.getProcess().getNodeInstBean(preNiId);
			
			final String nextNodeCode = preNiBean.getStr("NODE_CODE");
			
			if (preNiBean.isEmpty("DONE_USER_ID")) {
				log.debug("自动退回， 上个节点没有办理人");
				
				return;
			}
			final String toUserCode = preNiBean.getStr("DONE_USER_ID"); //上个节点的办理人
			final String toDeptCode = preNiBean.getStr("DONE_DEPT_IDS");
			
			String doneUserCode = nodeInst.getStr("TO_USER_ID"); //当前这条记录的to_user_id
			if (doneUserCode.length() == 0) { //从nodeinst没取到 ， 从node_users里面去取第一个
				List<Bean> users = WfNodeUserDao.getUserList(nodeInst.getId());
				
				if (users.size() > 0) {
					doneUserCode = users.get(0).getStr("TO_USER_ID");
				} else {
					log.debug("自动退回， 当前节点没有找到对应的人");
					
					return;
				}
			}
			
			wfAct.toNextForTuiHui(nextNodeCode, doneUserCode, toUserCode, toDeptCode);
		}
	}


	/**
	 * 
	 * @param nid 节点实例ID
	 * @param remindLog 记录已经提醒的串 
	 */
	private void updateNodeRemindState(String nid, String remindLog) {
	    Bean nodeInst = new Bean(nid);
	    nodeInst.set("REMIND_LOG", remindLog);
	    ServDao.update(ServMgr.SY_WFE_NODE_INST, nodeInst);
	}
	
	
	/**
	 * @param entity 实体表信息
	 * @param nodeInst 节点实例
	 */
	private void addCuiBan(Bean entity, Bean nodeInst) {
	    String userTodo = nodeInst.getStr("TO_USER_ID");
	    
        ParamBean paramBean = new ParamBean();
        UserBean userBean = UserMgr.getUser(userTodo);
        
        String cuiBanCode = Context.getSyConf("SY_WFE_REMIND_CODE", "催办");
        paramBean.set("REMD_CODE", cuiBanCode);
        paramBean.set("REMD_YEAR", DateUtils.getYear());
        
        Bean codeBean = RemindUtils.getMaxCode(RemindServ.SERV_ID, paramBean, WfeRemindItem.REMD_NUM);
        paramBean.set("REMD_NUM", codeBean.getStr(WfeRemindItem.REMD_NUM));
        
        paramBean.set("ACPT_USER", userTodo);
        paramBean.set("ACPT_DEPT", userBean.getDeptCode());
        paramBean.set("ACPT_TDEPT", userBean.getTDeptCode());
        paramBean.set("ACPT_PHONE", userBean.getMobile());
        paramBean.set("REMD_TITLE", "催办-" + entity.getStr("TITLE"));
        paramBean.set("REMD_REASON", "超时"); 
        paramBean.set("DEADLINE", nodeInst.getStr("NODE_LIMIT_TIME")); 
        paramBean.set("S_EMERGENCY", entity.getInt("S_EMERGENCY")); 
        paramBean.set("REMD_STATUS", RemindServ.STATE_NOSEND); 
        paramBean.set("DATA_ID", entity.getStr("DATA_ID")); 
        paramBean.set("SERV_ID", entity.getStr("SERV_ID")); 
        paramBean.set("WF_NODE_ID", nodeInst.getStr("NI_ID")); 
        
        String userLoginName = Context.getSyConf("SY_WFE_REMIND_USER", "admin");
        UserBean adminUser = UserMgr.getUserByLoginName(userLoginName, entity.getStr("S_CMPY"));
        paramBean.set("S_USER", adminUser.getCode());
        
        paramBean.set(Constant.PARAM_SERV_ID, RemindServ.SERV_ID);
        paramBean.set(Constant.PARAM_ACT_CODE, "save");
        
        //添加催办
        OutBean outBean = ServMgr.act(paramBean);
        
        //发送
        paramBean.setId(outBean.getId());
        paramBean.set(Constant.PARAM_ACT_CODE, "sendTodo");
        ServMgr.act(paramBean);
    }


    /**
	 * @param procName 流程定义
	 * @param nodeName 节点定义
	 * @param entity 实体表
	 * @param remindType 提醒方式
	 * @param userTodo 需要办理的用户
	 * @param procInstId 流程实例ID
	 * @param nodeInstId 节点实例ID
	 * @param nodeDef 节点定义
	 */
	private void addRemind(String procName, String nodeName, Bean entity, 
			String remindType, String userTodo, String procInstId, String nodeInstId, WfNodeDef nodeDef) {
		int remindCount = getRemindCount(entity, userTodo, procInstId, nodeInstId);
		
		if (remindType.lastIndexOf(",") == 0) {
			remindType = remindType.substring(0, remindType.length() - 1);
		}
		
		String forceTypes = Context.getSyConf(SY_FORCE_REMIND_TYPE, "");
		for (String force: forceTypes.split(",")) {
			if (!Strings.isEmpty(force) && remindType.indexOf(force) < 0) {
				remindType += "," + force;
			}
		}
		
		entity.set("CONTENT", getRemindContent(userTodo, entity.getStr("TITLE")));
		int emercyValue = entity.getInt("S_EMERGENCY");
	    Bean emergenBean = nodeDef.getEmerGency(emercyValue);
	    
		if (remindCount == 0) { //之前还没提醒过， 第一次添加
			addRemind(entity, remindType, userTodo);
            addRemindExt(procName, nodeName, entity,
					remindType, userTodo, procInstId, nodeInstId, Constant.NO_INT, getNextTimeOut(emergenBean));
    	} else if (remindCount == 1) { //之前提醒过一次， 第二次添加
    			Bean remindBean = getRemindBean(entity, userTodo, procInstId, nodeInstId, 1);
    			String nextTimeOut = remindBean.getStr("NEXT_REMIND_TIME");
    			if (StringUtils.isBlank(nextTimeOut)) {
    				return;
    			}
    			if (DateUtils.getDiffTime(nextTimeOut, DateUtils.getDatetime()
    			        , DateUtils.FORMAT_DATETIME) >= 0) {
    				addRemind(entity, remindType, userTodo);
    				addRemindExt(procName, nodeName, entity,
    						remindType, userTodo, procInstId, nodeInstId, 
    						Constant.YES_INT, getNextTimeOut(emergenBean));
    	    		//修改领导entity提醒内容
    	    		sendRemindToLeader(entity, userTodo);
    			}
    		}
    	}


	/**
	 * 获取下一个超时时间
	 * @param emergenBean 通过紧急程度获取工作流定义超时配置
	 * @return 超时时间
	 */
	private String getNextTimeOut(Bean emergenBean) {
		//节点上定义的超时时间
	    if (null == emergenBean || emergenBean.isEmpty("TIMEOUT") || emergenBean.getInt("TIMEOUT") == 0) {
	        return null;
	    }
	    int timeOut = emergenBean.getInt("TIMEOUT"); //超时时间  小时
        WorkTime workTime = new WorkTime();
        String secondLimitTime = workTime.addMinute(DateUtils.getDatetime(), timeOut * 60); //第二次超时
		return secondLimitTime;
	}
	
	/**
	 * 替换提醒配置
	 * @param userCode 提醒人编码
	 * @param remindTitle 文件标题
	 * @return 转换后的提醒内容
	 */
	private String getRemindContent(String userCode, String remindTitle) {
		String configContent = Context.getSyConf(OA_TIMEOUT_REMIND_CONTENT, "");
		StringBuffer remindContent = new StringBuffer("");
		
		if (configContent.length() > 0) {
			String[] contentArray = configContent.split("#"); //获取分组数组
			for (int i = 0; i < contentArray.length; i++) {
				if (contentArray[i].equals("USER_NAME")) { //替换用户名
					remindContent.append(UserMgr.getUser(userCode).getName());
					continue;
				}
				if (contentArray[i].equals("TITLE")) { //替换标题
					remindContent.append(remindTitle);
					continue;
				}
				remindContent.append(contentArray[i]);
			}
		}
		return remindContent.toString();
	}
	
	/**
	 * 
	 * @param entity 实体信息
	 * @param remindType 提醒类型
	 * @param userTodo 提醒的人
	 */
	private void addRemind(Bean entity, String remindType, String userTodo) {
		Bean attBean = new Bean();
        attBean.set("TYPE", remindType);
        attBean.set("REM_CONTENT", entity.getStr("CONTENT"));
        String content = entity.getStr("CONTENT");
        if (StringUtils.isBlank(content)) {
        	attBean.set("REM_CONTENT", getRemindContent(userTodo, entity.getStr("TITLE")));
        }
        attBean.set("REM_TITLE", "[文件办理提醒]" + entity.getStr("TITLE"));
        attBean.set("SERV_ID", entity.getStr("SERV_ID"));
        attBean.set("DATA_ID", entity.getStr("DATA_ID"));
        
        attBean.set("EXECUTE_TIME", DateUtils.getDatetime());
        
        String urlStr = entity.getStr("SERV_ID") + ".byid.do?data={_PK_:" + entity.getStr("DATA_ID") + "}";
        attBean.set("REM_URL", urlStr);
        
        RemindMgr.add(attBean, userTodo);
	}

	/**
	 * 
	 * @param entity 实例记录
	 * @param userTodo 用户
	 * @param procInstId 流程实例
	 * @param nodeInstId 节点实例
	 * @return 提醒的次数
	 */
	private int getRemindCount(Bean entity, String userTodo, String procInstId, String nodeInstId) {
		//先去查记录表中有值了没
    	SqlBean sql = new SqlBean();
    	sql.and("REMIND_USER", userTodo);
    	sql.and("DATA_ID", entity.getStr("DATA_ID"));
    	sql.and("NODE_INST", nodeInstId);
    	
    	return ServDao.count(SY_WF_TIMEOUT_REMIND, sql);
	}
	
	/**
	 * 获取数据对象
	 * @param entity 实体
	 * @param userTodo 接受者
	 * @param procInstId 流程实例
	 * @param nodeInstId 节点
	 * @param thCount 第几次超时记录
	 * @return 当前记录对象
	 */
	private Bean getRemindBean(Bean entity, String userTodo, String procInstId, String nodeInstId, int thCount) {
		//先去查记录表中有值了没
    	SqlBean sql = new SqlBean();
    	sql.and("REMIND_USER", userTodo);
    	sql.and("DATA_ID", entity.getStr("DATA_ID"));
    	sql.and("NODE_INST", nodeInstId);
    	sql.asc("NEXT_REMIND_TIME");
    	
    	List<Bean> list = ServDao.finds(SY_WF_TIMEOUT_REMIND, sql);
    	if (list.size() >= thCount && thCount > 0) { //合法数据
    		return list.get(thCount - 1);
    	}
    	return null;
	}
	
	/**
	 * 添加扩展的提醒记录
	 * @param procName 流程名
	 * @param nodeName 接触点名
	 * @param entity 实体
	 * @param remindType 提醒类型
	 * @param userTodo 提醒人
	 * @param procInstId 流程实例
	 * @param nodeInstId 节点实例
	 * @param levelFlag 是否发送给领导
	 * @param timeOut 超时时限
	 */
	private void addRemindExt(String procName, String nodeName, Bean entity,
			String remindType, String userTodo, String procInstId, String nodeInstId, int levelFlag, String timeOut) {
		Bean newBean = new Bean();
		newBean.set("REMIND_USER", userTodo);
		newBean.set("DATA_ID", entity.getStr("DATA_ID"));
		newBean.set("SERV_ID", entity.getStr("SERV_ID"));
		newBean.set("NODE_INST", nodeInstId);
		newBean.set("PROC_INST", procInstId);
		newBean.set("TITLE", entity.getStr("TITLE"));
		
		//newBean.set("REMIND_TYPE", "EMAIL");
		newBean.set("NODE_INST_NAME", nodeName);
		newBean.set("PROC_INST_NAME", procName);
		
		newBean.set("LEVEL_FLAG", levelFlag);
		newBean.set("NEXT_REMIND_TIME", timeOut);
		ServDao.create(SY_WF_TIMEOUT_REMIND, newBean);
	}
	
	/**
	 * 
	 * @param entity 实体信息表
	 * @param userTodo 提醒人
	 */
	private void sendRemindToLeader(Bean entity, String userTodo) {
		//找到领导，
		UserBean currUser = UserMgr.getUser(userTodo);
		String[] roleCodes = currUser.getRoleCodes();
		
		//获取配置领导角色
		String confRoleCodes = Context.getSyConf(OA_TIMEOUT_ROLE_CODES, "");
		if (StringUtils.isBlank(confRoleCodes)) { //没有配置领导角色，发送本人
			//addRemind(entity, "EMAIL", currUser.getCode());
			return;
		}
		boolean isLevel = false; //当前超时人员是否存在对应领导角色
		for (int i = 0; i < roleCodes.length; i++) {
			if (confRoleCodes.contains(roleCodes[i])) {
				isLevel = true;
				break;
			}
		}
		if (isLevel) { //当前人是配置领导角色，则发送本人
			//addRemind(entity, "EMAIL", currUser.getCode());
			return;
		}
		
		//处室
		//List<UserBean> users = UserMgr.getUsersByDept(currUser.getDeptCode(), confRoleCodes);
		
		//部门 TDEPT_CODE
		List<UserBean> users = UserMgr.getUsersByDept(currUser.getTDeptCode(), confRoleCodes);
		
		for (UserBean leader: users) {
			addRemind(entity, "EMAIL", leader.getCode());
		}
	}
	
	
	/**
	 * 
	 * @param procInstMap 流程实例列表
	 * @return 实体表信息
	 */
    private Map<String, Bean> getEntityMap(Map<String, Bean> procInstMap) {
        StringBuilder pids = new StringBuilder();
        Iterator<String> it = procInstMap.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            
            pids.append(key).append(",");
        }
        if (pids.length() > 0) {
            pids.setLength(pids.length() - 1);
        }
        
        Bean queryBean = new Bean();
        queryBean.set(Constant.PARAM_WHERE, " and S_WF_INST in ('" + pids.toString().replace(",", "','") + "')");
        List<Bean> entitys = ServDao.finds(ServMgr.SY_COMM_ENTITY, queryBean);
        
        Map<String, Bean> entityMap = new HashMap<String, Bean>();
        for (Bean entity: entitys) {
            entityMap.put(entity.getStr("S_WF_INST"), entity);
        }
        
        return entityMap;
    }
	
    /**
	 * 
	 * @param procInstList 流程实例列表
	 * @return <流程ID, 流程实例Bean> 
	 */
	private Map<String, Bean> getProcInstMap(List<Bean> procInstList) {
		// 找到 有 值的 列表
		Map<String, Bean> procInstMap = new HashMap<String, Bean>();
		for (Bean procInst : procInstList) {
		    procInstMap.put(procInst.getStr("PI_ID"), procInst);
		}
		return procInstMap;
	}


	/**
	 * 
	 * @param baseWhere 查询条件
	 * @return 获取需要提醒的节点列表
	 */
	private List<Bean> getNodeInstList(StringBuilder baseWhere) {
		Bean nodeQuery = new Bean();
		nodeQuery.set(Constant.PARAM_WHERE, baseWhere);
		List<Bean> nodeInstList = ServDao.finds(WfNodeInstDao.SY_WFE_NODE_INST_SERV, nodeQuery);
		return nodeInstList;
	}

	/**
	 * 
	 * @param baseWhere 查询条件
	 * @return 获取需要提醒的流程列表
	 */
	private List<Bean> getProcList(StringBuilder baseWhere) {
		Bean paramBean = new Bean();
		StringBuilder procWhere = new StringBuilder();
		procWhere.append(" and PI_ID in (");
		procWhere.append(" select PI_ID from SY_WFE_NODE_INST where 1 = 1");
		procWhere.append(baseWhere);
		procWhere.append(")");

		paramBean.set(Constant.PARAM_WHERE, procWhere.toString());

		List<Bean> procInstList = ServDao.finds(
				WfProcInstDao.SY_WFE_PROC_INST_SERV, paramBean);
		return procInstList;
	}
	
	/**
	 * @return 查询需要提醒的语句
	 */
	private StringBuilder getBaseWhere() {
		String dateStr = DateUtils.getDatetime();
		
		StringBuilder baseWhere = new StringBuilder();
		baseWhere.append(" and NODE_IF_RUNNING =").append(WfeConstant.NODE_IS_RUNNING); //运行状态的节点
		//baseWhere.append(" and NODE_ETIME is null"); // 结束时间还没有
		//baseWhere.append(" and NODE_LIMIT_TIME is not null"); //限定时间不为空
		baseWhere.append(" and NODE_LIMIT_TIME < '").append(dateStr).append("'"); //当前时间大于限定的时间
		//baseWhere.append(" and REMIND_LOG is null"); //没被提醒过
		
		//baseWhere.append(" and NI_ID = '2F_P-mmjt1vXbQp59NMtVe'"); //临时测试添加条件
		return baseWhere;
	}



    @Override
    public void interrupt() {
        // Auto-generated method stub
    }


    @Override
    protected void executeJob(RhJobContext context) {
        doWorks();
    }
}
