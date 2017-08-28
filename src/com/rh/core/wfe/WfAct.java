package com.rh.core.wfe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.comm.mind.DisabledMindUpdater;
import com.rh.core.comm.todo.TodoBean;
import com.rh.core.comm.todo.TodoUtils;
import com.rh.core.comm.workday.WorkTime;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;
import com.rh.core.util.JavaScriptEngine;
import com.rh.core.util.JsonUtils;
import com.rh.core.util.Lang;
import com.rh.core.wfe.condition.IfFlowCondition;
import com.rh.core.wfe.condition.LineConditionExt;
import com.rh.core.wfe.condition.MindContextHelper;
import com.rh.core.wfe.condition.SimpleFlowCondition;
import com.rh.core.wfe.db.ServDataDao;
import com.rh.core.wfe.db.WfNodeInstDao;
import com.rh.core.wfe.db.WfNodeInstHisDao;
import com.rh.core.wfe.def.WfLineDef;
import com.rh.core.wfe.def.WfNodeDef;
import com.rh.core.wfe.def.WfProcDef;
import com.rh.core.wfe.engine.NodeAutoFlow;
import com.rh.core.wfe.free.SimpleFreeNode;
import com.rh.core.wfe.resource.GroupBean;
import com.rh.core.wfe.resource.HuiQianExtWfBinder;
import com.rh.core.wfe.resource.WfActor;
import com.rh.core.wfe.resource.WfBinderManager;
import com.rh.core.wfe.resource.WfeBinder;
import com.rh.core.wfe.resource.WfeBinderHelper;
import com.rh.core.wfe.serv.WfSubProcActHandler;
import com.rh.core.wfe.util.AbstractLineEvent;
import com.rh.core.wfe.util.AbstractNodeEvent;
import com.rh.core.wfe.util.ParallelFlag;
import com.rh.core.wfe.util.WfBtnConstant;
import com.rh.core.wfe.util.WfNodeBtInterface;
import com.rh.core.wfe.util.WfTodoProvider;
import com.rh.core.wfe.util.WfUserState;
import com.rh.core.wfe.util.WfUtils;
import com.rh.core.wfe.util.WfeConstant;

/**
 * 工作流节点实例对象
 * 
 * @author ananyuan
 */
public class WfAct extends AbstractWfAct {
	/** 下一环节：排序字段 */
	public static final String NODE_ORDER = "NODE_ORDER";
	
	/** 排序：普通按钮 */
	public static final int NODE_ORDER_COMMON_DEFAULT = 100;
	/** 排序：可同时送交节点 */
	public static final int NODE_ORDER_VP_DEFAULT = 90;
	/** 排序：可送交ACT定义 */
	public static final int NODE_ORDER_ACT_DEFAULT = 80;
	/** 排序：返回经办人 */
	public static final int NODE_ORDER_RETURN2DRAFTER = 50;
	/** 排序：返回上一环节 */
	public static final int NODE_ORDER_RETURN2LAST = 10;
	/** 排序：办结 */
	public static final int NODE_ORDER_FINISH = 8;
	

    private Bean nodeInstBean;

    /**
     * @param nid 流程节点实例
     * @param isRunningData 流程是否已经结束
     */
    public WfAct(String nid, boolean isRunningData) {
        this.setIsRunningData(isRunningData);
        init(nid);
    }

    /**
     * @param wfProcess 流程实例对象
     * @param nodeInst 流程节点实例对象
     */
    protected WfAct(WfProcess wfProcess, Bean nodeInst) {
        this.setProcess(wfProcess);
        this.nodeInstBean = nodeInst;
    }

    /**
     * @param wfProcess 流程实例对象
     * @param nodeInst 流程节点实例对象
     * @param isHisData 数据保存在历史表
     */
    protected WfAct(WfProcess wfProcess, Bean nodeInst, boolean isHisData) {
        this.setIsRunningData(isHisData);
        this.setProcess(wfProcess);
        this.nodeInstBean = nodeInst;
    }
    
    /**
     * @param wfProcess 流程实例对象
     * @param nid 节点实例ID
     */
    public WfAct(WfProcess wfProcess, String nid) {
        this.setProcess(wfProcess);
        init(nid);
    }
    
    /**
     * @param wfProcess 流程实例对象
     * @param nid 节点实例ID
     * @param procRunning 流程是否在运行表中。用于兼容已删除审批单查看流程跟踪功能。
     */
    public WfAct(WfProcess wfProcess, String nid, boolean procRunning) {
        this.setIsRunningData(procRunning);
        this.setProcess(wfProcess);
        init(nid);
    }

    /**
     * @param doUser 办理用户
     * @param lineBean 连线定义
     * @param nodeBean 节点定义
     * @return 检查条件表单式执行结果
     */
    private boolean checkLineCond(UserBean doUser, Bean lineBean, Bean nodeBean) {
        if (!lineBean.isEmpty("LINE_CONDS_SCRIPT")) {
            // 将条件流中设置的串取出来，并做相应的变量替换
            String lineConStr = lineBean.getStr("LINE_CONDS_SCRIPT");
            
			if (lineConStr != null && lineConStr.startsWith(LineConditionExt.PREFIX_CLS)) {
				return execConditionClass(doUser, lineBean, lineConStr);
			}
            
            return execConditionScript(doUser, lineConStr);
        }

        return true;
    }
    
    /**
     * 执行条件判断脚本
     * @param doUser 当前办理人
     * @param lineConStr 条件表达式
     * @return 是否符合条件
     */
	private boolean execConditionScript(UserBean doUser, String lineConStr) {
		try {
			IfFlowCondition flowCondition = new SimpleFlowCondition(this, doUser);
			Bean servDataBean = this.getProcess().getServInstBean();
			//表字段作为条件流变量放到运行环境中
			flowCondition.setVarMap(addServItem2VarMap(flowCondition, servDataBean));
			// 替换表单中的值
			lineConStr = ServUtils.replaceValues(lineConStr, this.getProcess().getServId(), servDataBean);
	
			// 替换 系统 变量
			lineConStr = ServUtils.replaceSysVars(lineConStr);
	
			return flowCondition.check(lineConStr);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        
        return false;
	}
	
	/**
	 * 执行条件判断类。条件判断类后面可以跟","和JSON字符串，json字符串可以作为config传递给条件判断类。 
	 * @param doUser 当前办理人
	 * @param lineBean 线定义bean
	 * @param lineConStr 条件表达式
	 * @return 是否符合条件
	 */
	private boolean execConditionClass(UserBean doUser,Bean lineBean, String lineConStr) {
        try {
            String cls = lineConStr.substring(LineConditionExt.PREFIX_CLS
                    .length());
            String conf = "";
            int pos = cls.indexOf(",");
            if (pos > 0) {
                conf = cls.substring(pos + 1);
                cls = cls.substring(0, pos);
            }
            LineConditionExt cond = Lang.createObject(
                    LineConditionExt.class, cls);
            return cond.check(this, doUser, lineBean, conf);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        
        return false;
	}

    /**
     * 把服务中的数据库字段的名称和值之间放到条件流判断上下文环境中
     * 
     * @param flowCondition 条件流判断对象
     * @param servDataBean 服务实体Bean
     * @return 需要放到条件流判断上下文环境中的HashMap
     */
    private HashMap<String, Object> addServItem2VarMap(IfFlowCondition flowCondition , Bean servDataBean) {
        HashMap<String, Object> varMap = new HashMap<String, Object>();
        Iterator<Object> keys = servDataBean.keySet().iterator();
        
        ServDefBean servDef = ServUtils.getServDef(this.getProcess().getServId());
        List<Bean> itemList = servDef.getViewItems();
        
        while (keys.hasNext()) {
            Object name = keys.next();
            if (name instanceof String) {
                for (Bean item: itemList) {
                    if (item.getStr("ITEM_CODE").equals(name) 
                            && item.get("ITEM_FIELD_TYPE").equals(Constant.ITEM_FIELD_TYPE_STR)) {
                        varMap.put((String) name, servDataBean.getStr(name));
                        break;
                    } 
                    
                    if (item.getStr("ITEM_CODE").equals(name) 
                            && item.get("ITEM_FIELD_TYPE").equals(Constant.ITEM_FIELD_TYPE_NUM)) {
                        varMap.put((String) name, servDataBean.getInt(name));
                        break;
                    }
                }
            }
        }
        
        return varMap;
    }
    
    /**
     * 
     * @return 未汇合节点数量
     */
    private int getNotConvergeNodeCount() {
        if (this.isConvergeNode()) {
            List<Bean> list = getUnconvergeInstList();
            
            int count = list.size();
            if (count > 0) {
                count = count - 1;
            }
            return count;
        }

        return 0;
    }
    
    /**
     * 
     * @return 取得当前节点为参照的未汇合节点实例列表。
     */
    private List<Bean> getUnconvergeInstList() {
        String parallelFlag = this.getNodeInstBean().getStr("PARALLEL_FLAG");
        List<Bean> list = this.getProcess().getRunningNodeInstList();
        List<Bean> result = new ArrayList<Bean>();
        for (Bean bean:list) {
            String pflag = bean.getStr("PARALLEL_FLAG");
            if (pflag.startsWith(parallelFlag)) {
                result.add(bean);
            }
        }
        return result;
    }
    
    /**
     * 
     * @return 取得相对于当前汇合点，未汇合WfAct实例列表
     */
    private List<WfAct> getUncovergeWfActList() {
        List<WfAct> result = new ArrayList<WfAct>();
        List<Bean> list = getUnconvergeInstList();
        for (Bean bean : list) {
            WfAct wfact = new WfAct(this.getProcess(), bean);
            result.add(wfact);
        }
        
        return result;
    }

    /**
     * 自动在合并点合并并发流 并发流，如果是合并节点 ， TO_USER_ID 是同一个， NodeCode是同一个
     */
    private void autoConvergeNewNode() {
        if (this.isConvergeNode()) {
            // 判断是不是有并发来的节点了
            String newPreNid = this.getNodeInstBean().getStr("NODE_CODE");
            String newToUserId = this.getNodeInstBean().getStr("TO_USER_ID");

            List<Bean> nodeInstList = WfNodeInstDao
                    .getListByPreNodeInstAndToUser(this.getProcess().getId(), newPreNid, newToUserId);

            // 将后面的那条节点实例 办结
            if (nodeInstList.size() > 1) {
                WfParam paramBean = new WfParam();
                paramBean.setDoneType(WfeConstant.NODE_DONE_TYPE_CONVERGE);
                paramBean.setDoneDesc(WfeConstant.NODE_DONE_TYPE_CONVERGE_DESC);
                
				UserBean toUserBean = null;
				if (StringUtils.isBlank(newToUserId)) {
					List<Bean> users = this.getNodeUserBeanList();
					if (users == null || users.size() == 0) {
						throw new RuntimeException("数据错误，无效的办理人。");
					}
					toUserBean = UserMgr.getUser(users.get(0).getStr("TO_USER_ID"));
				} else {
					toUserBean = UserMgr.getUser(newToUserId);
				}
				paramBean.setDoneUser(toUserBean);

                for (int i = 1; i < nodeInstList.size(); i++) {
                    Bean nodeInstTemp = nodeInstList.get(i);
                    WfAct wfActTemp = new WfAct(this.getProcess(), nodeInstTemp);

                    wfActTemp.finish(paramBean);
                }
            }
        }
    }

    /**
     * 
     * @return 能否终止并发
     */
    public boolean canStopParallel() {
        if (isConvergeNode() && isRunning()) {
                if (this.getNotConvergeNodeCount() >= 1) { // 多个活动节点
                    return true;
                }
        }

        return false;
    }

    /**
     * 公文打开了，也不能收回，需要记公文查看的记录
     * 
     * @return 是否能够收回
     */
    public List<Bean> getCanWithdrawList() {
        List<Bean> rtn = new ArrayList<Bean>();
        
        if (!this.getProcess().isRunning() 
                && !this.getNodeDef().isFreeNode()) { // 流程已经办结并且不是自由节点则返回
            return rtn;
        }

        // 从本节点出去的节点 , 看这些点的状态有没有已办 , 或者有没有打开时间 ， 如果有， 返回不能收回
        List<Bean> nodeInstFromThis = this.getProcess().getNextNodeInstList(this.getId());

        if (nodeInstFromThis.size() == 0) { // 从本节点还没出去过 , 不显示收回
            return rtn;
        }

        for (Bean aNodeInstBean : nodeInstFromThis) {
            if (aNodeInstBean.getInt("NODE_IF_RUNNING") == WfeConstant.NODE_NOT_RUNNING) {
                continue;
            }

            if (aNodeInstBean.getStr("OPEN_TIME").length() > 0) {
                continue;
            }
            
            rtn.add(aNodeInstBean);
        }

        return rtn;
    }

    /**
     * 修改节点办理类型说明
     * 
     * @param type 办理类型
     * @param desc 说明
     */
    protected void changeDoneType(int type, String desc) {
        Bean updateBean = new Bean();
        updateBean.setId(this.getId());
        updateBean.set("DONE_TYPE", type);
        updateBean.set("DONE_DESC", desc);
        WfNodeInstDao.updateWfNodeInst(updateBean, this.isRunningData());
        this.nodeInstBean.copyFrom(updateBean);
    }

    /**
     * 在合并节点 结束并发流程，添加新的节点实例 状态为正常结束
     * @param doUser 办理用户
     * @return 结束并发流程后的节点实例
     */
    private WfAct completeParallel(UserBean doUser) {
        WfParam paramBean = new WfParam();
        paramBean.setToUser(doUser.getId() + "^" + doUser.getDeptCode());
        paramBean.setTypeTo(WfParam.TYPE_TO_USER);

        // 自己送自己 linecode 为空 
        WfLineDef lineDefBean = new WfLineDef(new Bean());
        lineDefBean.set("LINE_CODE", "");
        
        GroupBean groupUser = createNextActors(getCode(), paramBean).get(0);
        
        WfAct wfAct = createNewNodeIns(paramBean, getNodeDef(),
                lineDefBean, groupUser, -1);

        // 往下送交的时候，发送待办
        wfAct.sendTodo(doUser);
        
        wfAct.updateServWhenEnter(this, paramBean);

        return wfAct;
    }
    
    /**
     * 
     * @return 表单的紧急程度，从字段S_EMERGENCY 中取， 如果没有返回0
     */
    private int getEmergency() {
        if (this.getProcess().getServInstBean().isNotEmpty("S_EMERGENCY")) {
            return this.getProcess().getServInstBean().getInt("S_EMERGENCY");
        }
        
        return 0;
    }

    /**
     * 设置限定时间
     * @param nextNodeDefBean 下一个节点定义
     * @param nextNodeInstBean 下一个节点实例
     */
    private void setTimeLimit(WfNodeDef nextNodeDefBean, Bean nextNodeInstBean) {
        // 限定时间 = 当前的时间 + 流程节点定义的 节点超时时间 NODE_TIMEOUT , 如果设置了超时时间
    	try {
	        int emerValue = getEmergency();
	        
	        Bean timeOutBean = nextNodeDefBean.getEmerGency(emerValue);
	        
	        if (timeOutBean.isNotEmpty("TIMEOUT") && timeOutBean.getInt("TIMEOUT") > 0) { //设置了超时值
	            int timeOut = timeOutBean.getInt("TIMEOUT"); //超时时间  小时
	            
	            WorkTime workTime = new WorkTime();
	            String limitTime = workTime.addMinute(DateUtils.getDatetime(), timeOut * 60);
	            
	            nextNodeInstBean.set("NODE_LIMIT_TIME", limitTime);
	        }
    	} catch (Exception e) {
    		log.error("设置限定完成 时间出错， 请检查工作日是否已经设定", e);
    	}
    }
    
    /**
     * 插入新的节点实例对象。节点处理人用paramBean的TO_USERS属性设置，类型为GroupBean。
     * 
     * @param paramBean 要送交的用户Bean
     * @param nextNodeDefBean 下个节点定义Bean
     * @param lineDefBean 当前节点和下个节点之间的连线对象
     * @param groupUser 任务处理人 
     * @return 节点实例
     */
    private WfAct createNewNodeIns(WfParam paramBean,
            WfNodeDef nextNodeDefBean, WfLineDef lineDefBean, GroupBean groupUser, int parallelIdx) {
        // 添加新的节点实例信息
        Bean nextNodeInstBean = new Bean();

        nextNodeInstBean.set("PRE_NI_ID", this.getId()); // 上一个节点ID
        nextNodeInstBean.set("PI_ID", nodeInstBean.getStr("PI_ID")); // 流程实例ID
        nextNodeInstBean.set("NODE_CODE", nextNodeDefBean.getStr("NODE_CODE"));
        nextNodeInstBean.set("NODE_NAME", nextNodeDefBean.getStr("NODE_NAME"));
        nextNodeInstBean.set("PROC_CODE", nextNodeDefBean.getStr("PROC_CODE"));
        nextNodeInstBean.set("NODE_IF_RUNNING", WfeConstant.NODE_IS_RUNNING);
        nextNodeInstBean.set("HJ", nextNodeDefBean.getHuanJie());
        if (paramBean.isNotEmpty("_START_NODE")) {
            nextNodeInstBean.set("START_NODE", paramBean.get("_START_NODE"));
        }

        // 取消办结的时候，从最后一个点，创建新的，这个时候，没有连线信息
        if (null != lineDefBean && lineDefBean.getStr("LINE_CODE").length() > 0) {
        	nextNodeInstBean.set("CAN_BACK", Constant.YES_INT); //线是可以返回的
        	if (lineDefBean.getInt("LINE_IF_RETURN") == Constant.NO_INT) { 
        		nextNodeInstBean.set("CAN_BACK", Constant.NO_INT);
        	} 
        	
        	String nextNodeCode = nextNodeDefBean.getStr("NODE_CODE");
        	if (paramBean.getStr("NODE_CODE").indexOf(WfeConstant.FREE_SEPARATOR) > 0) {
        		String paramNodeCode = paramBean.getStr("NODE_CODE");
        		nextNodeCode = paramNodeCode.substring(0, paramNodeCode.indexOf(WfeConstant.FREE_SEPARATOR));
        	}
        	
            if (isBack(nextNodeCode, lineDefBean)) {
                nextNodeInstBean.set("PRE_LINE_CODE", "R" + lineDefBean.getStr("LINE_CODE"));
            } else {
                nextNodeInstBean.set("PRE_LINE_CODE", lineDefBean.getStr("LINE_CODE"));
            }
            
            if(lineDefBean.isParallel()) { // 启动并发，记录并发标记
                String parallelFlag = this.getNodeInstBean().getStr("PARALLEL_FLAG");
                String newParaFlag = ParallelFlag.createParallelFlagString(parallelFlag, 
                        nextNodeDefBean.getStr("NODE_CODE"), parallelIdx);
                nextNodeInstBean.set("PARALLEL_FLAG", newParaFlag);
            }
        }
        
        if(nextNodeInstBean.isEmpty("PARALLEL_FLAG")) { // 继承前一个节点的并发标志
            nextNodeInstBean.set("PARALLEL_FLAG", this.getNodeInstBean().getStr("PARALLEL_FLAG"));
        }
        
        // 开始时间为上一个节点结束时间
        nextNodeInstBean.set("NODE_BTIME", DateUtils.getDatetimeTS());
        
        //设置节点限定完成时间
        setTimeLimit(nextNodeDefBean, nextNodeInstBean);

        nextNodeInstBean.set("NODE_IF_RUNNING", WfeConstant.NODE_IS_RUNNING);
        nextNodeInstBean.set("S_CMPY", this.getProcess().getCmpyId());
        
        String niId = Lang.getUUID();
        if(nextNodeDefBean.isFreeNode()) {
            niId = "FREE-" + niId;
            nextNodeInstBean.set("FREE_NODE", Constant.YES_INT);
        }
        
        nextNodeInstBean.set("NI_ID", niId);
        
        //任务处理人
        if (groupUser.getAllActors().size() == 1) {
            //送交给单个用户  设置 TO_USER_ID TO_TYPE=3
        	final WfActor actor = groupUser.getAllActors().get(0);
            nextNodeInstBean.set("TO_TYPE", WfeConstant.NODE_INST_TO_SINGLE_USER);
            nextNodeInstBean.set("TO_USER_ID", actor.getUserId());
            nextNodeInstBean.set("TO_USER_NAME", UserMgr.getUser(actor.getUserId()).getName());
            addNewInstUser(niId, actor);
            setSubUserInfo(nextNodeInstBean, actor);
        } else {
            //送交给多个用户  TO_TYPE=1
            nextNodeInstBean.set("TO_TYPE", WfeConstant.NODE_INST_TO_MULTI_USER);
            addNewInstUsers(niId, groupUser);
        }
        
        mergeParallerlFlag(nextNodeDefBean, nextNodeInstBean);
        
        nextNodeInstBean = WfNodeInstDao.insertWfNodeInst(nextNodeInstBean, this.isRunningData());
        
        WfAct nextWfAct = new WfAct(this.getProcess(), nextNodeInstBean);
        
        //设置下一节点的操作码，1：人工审核、2：自动审核、3：自动办结     added by Tanyh 20160531
        setNextActOptCode(nextWfAct);
        
    	String toGroupUserStr = groupUser.getUserIdStr();
    	paramBean.set("GROUP_USERS_STR", toGroupUserStr);
//        nextWfAct.updateServWhenEnter(this, paramBean);
        
        return nextWfAct;
    }
    
    private void setSubUserInfo(Bean paramBean, WfActor actor) {
    	final String srcDeptId = actor.getDeptId();
    	final int pos = srcDeptId.indexOf("@");
    	if(pos < 0) {
    		return;
    	}
        String subUserInfo = srcDeptId.substring(pos + 1);
        UserBean userBean = UserMgr.getUserByUserDept(subUserInfo);
        if (userBean == null) {
        	return;
        }
        paramBean.set("SUB_USER_ID", userBean.getCode());
        paramBean.set("SUB_USER_NAME", userBean.getName());
    }
    
    /**
     * 设置下一节点操作码
     * @param nextAct 下一节点对象
     * @author Tanyh 20160531
     */
    private void setNextActOptCode(WfAct nextAct) {
    	String autoFlowConfStr = nextAct.getNodeDef().getAutoFlow();
    	if (autoFlowConfStr == null || autoFlowConfStr.length() <= 0) {
    		return;
    	}
    	String cls = "";
        String config = "";
        int pos = autoFlowConfStr.indexOf(",");
        int optType = WfeConstant.NODE_OPT_TYPE_MANUAL_FLOW;
        if (pos > 0) {
            cls = autoFlowConfStr.substring(0, pos);
            config = autoFlowConfStr.substring(pos + 1);
        } else {
        	// 没有配置参数
        	cls = autoFlowConfStr;
            config = "";
        }
        try {
            NodeAutoFlow flowCls = Lang.createObject(
            		NodeAutoFlow.class, cls);
            optType = flowCls.getActOptCode(this, nextAct, JsonUtils.toBean(config));
            if (optType == WfeConstant.NODE_OPT_TYPE_AUTO_FLOW || 
            		optType == WfeConstant.NODE_OPT_TYPE_AUTO_END) {
            	Bean updateBean = new Bean();
            	//自动流转或自动办结，则更新入库
            	updateBean.setId(nextAct.getId());
            	//设置更新字段
            	updateBean.set("OPT_TYPE", optType);
            	WfNodeInstDao.updateWfNodeInst(updateBean, true);
            	
            	nextAct.getNodeInstBean().set("OPT_TYPE", optType);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TipException(e.getMessage());
        }
    }
    
    /**
     * 合并并发标记
     */
    private void mergeParallerlFlag(WfNodeDef nextNodeDef, Bean nextNodeInstBean){
        
        // 是否是汇合点，不是则返回，只能在汇合点汇合标记
        if (!nextNodeDef.getBoolean("NODE_IF_CONVERGE")) {
            return;
        }
        
        String convergeTarget = nextNodeDef.getStr("CONVERGE_TARGET");
        String oldFlag = nextNodeInstBean.getStr("PARALLEL_FLAG");
        
        String newFlag = ParallelFlag.mergeParallelFlag(oldFlag, convergeTarget);
        nextNodeInstBean.set("PARALLEL_FLAG", newFlag);
    }

    
    /**
     * 为任务分配一组用户，执行持久化
     * @param nid 节点实例ID
     * @param users 用户组
     */
    protected void addNewInstUsers(String nid, GroupBean users) {
        List<WfActor> list = users.getAllActors();
        for (WfActor wfActor: list) {
            addNewInstUser(nid, wfActor);
        }
    }
    
    
    
    /**
     * 为任务添加一个处理人，执行持久化
     * @param nid 节点实例ID
     * @param userId 用户
     */
    protected void addNewInstUser(String nid, WfActor actor) {
    	final String srcDeptId = actor.getDeptId();
    	final int pos = srcDeptId.indexOf("@");
    	String deptId = srcDeptId;
    	if(pos > 0) {
    		deptId = srcDeptId.substring(0, pos);
    	}
        UserBean user = UserMgr.getUser(actor.getUserId(), deptId);
        Bean addUser = new Bean();
        addUser.set("TO_USER_ID", actor.getUserId());
        addUser.set("TO_USER_NAME", user.getName());
        addUser.set("TO_DEPT_ID", user.getDeptCode());
        addUser.set("TO_DEPT_NAME", user.getDeptName());
        addUser.set("NI_ID", nid);
        addUser.set("PI_ID", this.getProcess().getId());
        if(pos > 0) {
        	addUser.set("AUTH_USER", srcDeptId.substring(pos + 1));
        }
        ServDao.create(ServMgr.SY_WFE_NODE_USERS, addUser);
    }
    

    /**
     * 结束节点实例，办理类型为：1正常结束。
     * 
     * @param paramBean 参数bean
     */
    public void finish(WfParam paramBean) {
        log.debug("finish node inst " + nodeInstBean.getId());
        Bean updateBean = new Bean();
        updateBean.setId(nodeInstBean.getId());
        updateBean.set("DONE_TYPE", paramBean.getDoneType());
        updateBean.set("DONE_DESC", paramBean.getDoneDesc());

        // 结束当前节点信息 , DONE_USER, NODE_ETIME , DONE_TYPE
        String endTime = DateUtils.getDatetime();
        WorkTime workTime = new WorkTime();
        long nodeMinute = 0; //如果是 起草 和 自动合并的 节点，时间算成0
        if (paramBean.getDoneType() != WfeConstant.NODE_DONE_TYPE_CONVERGE
                || nodeInstBean.isEmpty("PRE_NI_ID")) {
            nodeMinute = workTime.calWorktime("", nodeInstBean.getStr("NODE_BTIME"), endTime);
        }
        long delayMinute = 0; //如果没在 NODE_LIMIT_TIME 以内，则算这个
        if (nodeInstBean.isNotEmpty("NODE_LIMIT_TIME")) { //设置了超期时间
            // 当前时间大于 限定时间
            if (DateUtils.getDiffTime(nodeInstBean.getStr("NODE_LIMIT_TIME"), endTime) > 0) { 
                delayMinute = workTime.calWorktime("", nodeInstBean.getStr("NODE_LIMIT_TIME"), endTime);
            }
        }

        updateBean.set("NODE_ETIME", endTime);
        updateBean.set("NODE_DAYS", nodeMinute);   //办理这个用的总时间
        updateBean.set("DELAY_TIME", delayMinute);   //延期了多长时间

        // 补充办理人的信息
        UserBean doneUser = paramBean.getDoneUser();

        if (paramBean.isAuthState()) { // 转授权状态？
            // 授权人信息
            UserBean authUser = paramBean.getAuthedUserBean();
            updateBean.set("DONE_USER_ID", doneUser.getCode());
            updateBean.set("DONE_USER_NAME", doneUser.getName());
            updateBean.set("DONE_DEPT_IDS", doneUser.getDeptCode());
            updateBean.set("DONE_DEPT_NAMES", doneUser.getDeptName());
            
            // 被授权人
            updateBean.set("SUB_USER_ID", authUser.getCode());
            updateBean.set("SUB_USER_NAME", authUser.getName());
            updateBean.set("SUB_DEPT_IDS", authUser.getDeptCode());
            updateBean.set("SUB_DEPT_NAMES", authUser.getDeptName());
        } else {
            updateBean.set("DONE_USER_ID", doneUser.getCode());
            updateBean.set("DONE_USER_NAME", doneUser.getName());
            // 填入 部门的层级信息
            updateBean.set("DONE_DEPT_IDS", doneUser.getDeptCode());
            updateBean.set("DONE_DEPT_NAMES", doneUser.getDeptName());
        }

        // 设置当前节点为结束
        updateBean.set("NODE_IF_RUNNING", WfeConstant.NODE_NOT_RUNNING);

        // 更新当前节点信息
        WfNodeInstDao.updateWfNodeInst(updateBean, this.isRunningData());
        
        this.nodeInstBean.copyFrom(updateBean);
        
        //清除缓存的数据，避免出现状态不一致的情况。
        this.getProcess().cleanAllNodeInstList();
        
        this.updateServWhenFinish(paramBean);
        
        // 结束待办
        this.finishTodo(paramBean);
    }

    /**
     * 查到并发出来的还在活动的节点实例，将这些实例都 结束
     * @param paramBean 参数Bean
     */
    private void finishParallelNodeInst(WfParam paramBean) {
        List<WfAct> runningWfActs = this.getUncovergeWfActList();
        log.debug("查到并发出来的还在活动的节点实例，将这些实例都 结束 nodeInstBeanList.size = "
                + runningWfActs.size());

        // nodeCode和本节点一样的，就不是终止，是正常结束
        for (WfAct wfAct : runningWfActs) {
            wfAct.finish(paramBean);
        }
    }

    /**
     * 完成待办事件
     * @param paramBean 参数Bean
     */
    public void finishTodo(WfParam paramBean) {
        WfTodoProvider.finishTodo(this, paramBean);
    }

    /**
     * 得到当前人员可走的下一步的节点
     * @param doUser 办理用户
     * @return 下一步节点集合
     */
    public List<Bean> getNextAvailableSteps(UserBean doUser) {
        List<Bean> nextSteps = new ArrayList<Bean>();
        // 流程已经进入到历史表中，且不是自由节点，则不用返回下一步按钮。
        if (!this.isRunningData() && !this.getNodeDef().isFreeNode()) {
            return nextSteps;
        }
        
        // 节点不是活动点，则返回空数组
        if (!this.isRunning()) {
            return nextSteps;
        }
        
        // 增加通过工作流节点定义产生的按钮
        addNodeDefBtns(doUser, nextSteps, this.getCode(), null, "");
        
        // 添加返回xxx的按钮， 因为节点上记载了前一个节点的节点ID
        addReturnBtns(nextSteps);
        
        String confVal = Context.getSyConf(WfeConstant.CONF_WF_BTN_RENDER
                , "");
        if (confVal.indexOf(WfeConstant.CONF_WF_BTN_RENDER_CONFIRM_WINDOW) >= 0) {
            //添加上处理完毕上定义的ACT的数据
            List<Bean> wfSelects = this.getNodeDef().getList("S_WF_SELECT");
            for (Bean wfSelect: wfSelects) {
                List<Bean> actBtns = wfSelect.getList("ACT_BTN_LIST");
                addActBtns(nextSteps, actBtns);
            }
            
            if(this.getNodeDef().getInt("RETURN2DRAFTER") == Constant.YES_INT){
            	Bean nextStep = new Bean();
            	
        		nextStep.set("NODE_CODE", WfeConstant.PREFIX_PROC_ACT + "return2Drafter");
        		nextStep.set("NODE_NAME", this.getNodeDef().getStr("RETURN2DRAFTER_NAME"));
        		nextStep.set("ACT_CSS", nextStep.getStr("NODE_CODE"));
        		nextStep.set(NODE_ORDER, NODE_ORDER_RETURN2DRAFTER);
        		nextSteps.add(nextStep);
            }
            
            //添加上ACT数据， 不存在处理选择的
            List<Bean> deliverActs = this.getNodeDef().getList("DELIVER_ACT_DEF");
            for (Bean deliverAct: deliverActs) {
    			if(!deliverAct.isEmpty("ACT_CONDITION")) {
    				boolean checkResult = checkActNodeCondition(deliverAct.getStr("ACT_CONDITION"), doUser);
    				if(!checkResult) {
    					continue;
    				}
    			}
        		Bean nextStep = new Bean();
        		String nodeCode = deliverAct.getStr("ACT_CODE");
                if (!(nodeCode.startsWith(WfeConstant.PREFIX_SERV_ACT) 
                		|| nodeCode.startsWith(WfeConstant.PREFIX_PROC_ACT)) ) {
                	nodeCode = WfeConstant.PREFIX_SERV_ACT + nodeCode;
                }
        		nextStep.set("NODE_CODE", nodeCode);
        		nextStep.set("NODE_NAME", deliverAct.getStr("ACT_NAME"));
        		nextStep.set("ACT_CSS", nextStep.getStr("NODE_CODE"));
        		nextStep.set(NODE_ORDER, deliverAct.get(NODE_ORDER, NODE_ORDER_ACT_DEFAULT));
        		
        		nextSteps.add(nextStep);
            }
            
            //如果节点上定义了可送交多个节点的数据
            addDevide2NodeDef(doUser, nextSteps);
            
            if (this.getNodeDef().getBoolean("PROC_END_FLAG")) {
                Bean nextStep = new Bean();
                nextStep.set("NODE_CODE", WfeConstant.PREFIX_PROC_ACT + WfBtnConstant.BUTTON_FINISH);
                nextStep.set("NODE_NAME", this.getNodeDef().getStr("PROC_END_NAME"));
                nextStep.set("ACT_CSS", nextStep.getStr("NODE_CODE"));
                nextStep.set(NODE_ORDER, NODE_ORDER_FINISH);
                nextSteps.add(nextStep);
            }
        }
        
        //处理节点重复的情况， 如果有 A节点直接送交到D， 也能通过穿越C_D ， 则，去掉C_D节点, 保留 D。
        //放在这里的原因是 在添加按钮的时候， 穿透按钮的顺序，有可能在执行直接送交的前面，
        checkRepeatSteps(nextSteps);
        
        //排序
        Collections.sort(nextSteps, new Comparator<Bean>() {
            public int compare(Bean item1, Bean item2) {
                return item2.getInt(NODE_ORDER) - item1.getInt(NODE_ORDER);
            }
        });
        
        return nextSteps;
    }
    
    /**
     * 
     * @param nextSteps
     * @param actBtns
     */
	private void addActBtns(List<Bean> nextSteps, List<Bean> actBtns) {
		for (Bean actBtn: actBtns) {
		    Bean nextStep = new Bean();
		    String nodeCode = actBtn.getStr("ACT_CODE");
		    if (nodeCode.indexOf(WfeConstant.PREFIX_SERV_ACT) == -1) {
		    	nodeCode = WfeConstant.PREFIX_SERV_ACT + actBtn.getStr("ACT_CODE");
		    }
		    nextStep.set("NODE_CODE", nodeCode);
		    nextStep.set("NODE_NAME", actBtn.getStr("ACT_NAME"));
		    nextStep.set("ACT_CSS", nextStep.getStr("NODE_CODE"));
		    nextStep.set(NODE_ORDER, actBtn.get(NODE_ORDER, NODE_ORDER_ACT_DEFAULT));
		    nextSteps.add(nextStep);
		}
	}
    
    /**
     * 
     * TODO 判断按钮出现的权限
     * @return
     */
    private boolean checkActNodeCondition(String condition, UserBean doUser) {
		if (StringUtils.isBlank(condition)) {
			return true;
		}
		condition = condition.trim();
		if (condition.startsWith("@")) { // 后面是java类
			return execConditionClass(doUser, new Bean(), LineConditionExt.PREFIX_CLS + condition.substring(1));
		}
		
		return execConditionScript(doUser, condition);
    }
    
    private void addDevide2NodeDef(UserBean doUser, List<Bean> nextSteps) {
        if (this.getNodeDef().isEmpty("DEVIDE_TWO_NODE")) {
        	return;
        }
		Bean virtualNode = new Bean();
		Bean multiConf = JsonUtils.toBean(this.getNodeDef().getStr("DEVIDE_TWO_NODE"));
		
		if (multiConf.contains("FILTER_SCRIPT")) { // 条件判断表达式
			boolean result = execConditionScript(doUser, multiConf.getStr("FILTER_SCRIPT"));
			if (!result) {
				return;
			}
		}
		
		List<Bean> nodes = multiConf.getList("NODES");
		StringBuilder sb = new StringBuilder("VP");
		for (Bean node: nodes) {
			sb.append("-").append(node.getStr("NODE_CODE"));
			
			//从nextSteps中去掉已经在VP-N1-N2中已经存在的
			removeNextStepInVp(nextSteps, node.getStr("NODE_CODE"));
		}
		virtualNode.set("NODE_NAME", multiConf.getStr("NODE_NAME"));
		virtualNode.set("NODE_CODE", sb.toString());
		virtualNode.set("ACT_CSS", virtualNode.getStr("NODE_CODE"));
		virtualNode.set(NODE_ORDER, virtualNode.get(NODE_ORDER, NODE_ORDER_VP_DEFAULT));
		
		nextSteps.add(virtualNode);
    }

    /**
     * 从nextSteps中去掉已经在VP-N1-N2中已经存在的
     * @param nextSteps 下一步能走的节点
     * @param nodeCode 能走节点的编码
     */
    private void removeNextStepInVp(List<Bean> nextSteps, String nodeCode) {
    	Iterator<Bean> itr = nextSteps.iterator();
        while (itr.hasNext()) {
            Bean bBean = itr.next();
            if (bBean.getStr("NODE_CODE").equals(nodeCode)) {
                itr.remove();
            }
        }
    }

    /**
     * 去除由于穿透引起的送交按钮的重复
     * @param nextSteps 送交的步骤  
     */
    private void checkRepeatSteps(List<Bean> nextSteps) {
        List<Bean> delBeans = new ArrayList<Bean>();
        HashMap<String, Bean> allSteps = new HashMap<String, Bean>();
        for(Bean step: nextSteps) {
            String nodeCode = step.getStr("NODE_CODE");
            int pos = nodeCode.indexOf(WfeConstant.FREE_SEPARATOR);
            if(pos >= 0) { // 是要穿过自由节点的节点
                nodeCode = nodeCode.substring(pos + 1);
                if(allSteps.containsKey(nodeCode)) { //有正常节点，则删掉自己
                    delBeans.add(step);
                } else if(allSteps.containsKey(WfeConstant.FREE_SEPARATOR + nodeCode)) { //重复的自由节点，则删掉自己
                    delBeans.add(step);
                } else {
                    allSteps.put(nodeCode, step);
                }
            } else { // 正常节点
                if(allSteps.containsKey(nodeCode)) { //正常节点重复
                    delBeans.add(step);
                } else if(allSteps.containsKey(WfeConstant.FREE_SEPARATOR + nodeCode)) { //重复的自由节点，则删掉自由节点
                    delBeans.add(allSteps.get(WfeConstant.FREE_SEPARATOR + nodeCode));
                } else {
                    allSteps.put(nodeCode, step);
                }
            }
        }
        
        for(Bean bean: delBeans) {
            nextSteps.remove(bean);
        }
	}
    
    /**
     * 根据当前用户信息、当前节点code获取下一步按钮
     * @param doUser 当前用户信息
     * @param curNodeCode 当前节点code
     * @return nextSteps 下一步列表
     */
    public List<Bean> getNodeDefBtns(UserBean doUser, String curNodeCode) {
    	List<Bean> nextSteps = new ArrayList<Bean>();
    	addNodeDefBtns(doUser, nextSteps, curNodeCode, null, "");
    	return nextSteps;
    }
    
    /**
     * 增加通过工作流节点定义产生的按钮
     * @param doUser 办理用户
     * @param nextSteps 工作流按钮集合
     * @param curNodeCode 当前的节点code
     * @param assignNodes 指定的节点
     * @param jumpedNodes 跳过了的点
     */
    private void addNodeDefBtns(UserBean doUser, List<Bean> nextSteps, String curNodeCode, 
    		String assignNodes, String jumpedNodes) {
        List<WfLineDef> nextLineDefBeanList = this.getProcess().getProcDef()
                .findLineDefList(curNodeCode);

        final boolean canStopParl = this.canStopParallel();
        final boolean isFreeNode = this.getNodeDef().isFreeNode();

        for (WfLineDef lineBean : nextLineDefBeanList) {
            WfNodeDef nodeDefBean = this.getProcess().getProcDef()
                    .findNode(lineBean.getStr("TAR_NODE_CODE"));

            // 中止并发流之前，不能送交给非并发点
            if (canStopParl && lineBean.getInt("IF_PARALLEL") != Constant.YES_INT) {
                continue;
            }
            
            // 当前节点是自由节点，如果目标点不是自由节点，则忽略这个节点。即自由节点只能送自由节点。
            if(isFreeNode && !nodeDefBean.isFreeNode()) {
                continue;
            }

            if (null != assignNodes) { //如果指定了送哪个点, 
            	String newAssignNodes = "," + assignNodes + ",";
            	
            	if (newAssignNodes.indexOf("," + nodeDefBean.getStr("NODE_CODE") + ",") < 0) { 
            		continue; //通过线找到的点， 不在指定的点里面，跳出循环
            	}
            }
            
            Bean freeConfig = getFreeConfig(nodeDefBean);
            if (null != freeConfig && jumpedNodes.split(WfeConstant.FREE_SEPARATOR).length <= 3) { //如果存在 自由节点 穿透的变量值，而且穿透小于两次，则去处理这个
            	if (freeConfig.containsKey("CONDITION")) { //存在条件优先
            		WfNodeBtInterface nodeBt = new SimpleFreeNode();
            		
            		this.handleFreeNode(nodeBt, freeConfig, doUser, nextSteps, nodeDefBean, jumpedNodes);
            		continue; 
            	} else if (freeConfig.isNotEmpty("CLASS")) { //存在扩展类
            		String btClass = freeConfig.getStr("CLASS");
            		
            		try {
            			WfNodeBtInterface nodeBt = (WfNodeBtInterface) Lang.loadClass(btClass).newInstance();
            			
            			this.handleFreeNode(nodeBt, freeConfig, doUser, nextSteps, nodeDefBean, jumpedNodes);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
            		
            		continue; 
            	}
            }
            
            Bean stepBean = new Bean();
            
            if (HuiQianExtWfBinder.isNodeHuiShang(nodeDefBean)) { // 页面上没选会商部门的，流程中就不显示会商的节点。
                String huishangField = HuiQianExtWfBinder.getConfigField(nodeDefBean);

                Bean servData = this.getProcess().getServInstBean();

                String depts = servData.getStr(huishangField);
                stepBean.set("HQ_ITEM", huishangField);
                if (depts.length() == 0) {
                	stepBean.set("ERROR_MSG", HuiQianExtWfBinder.getConfigErroeMsg(nodeDefBean));
                }
            }

            // 检查条件表单式执行结果
            if (checkLineCond(doUser, lineBean, nodeDefBean)) {
            	//Bean stepBean = new Bean();
                stepBean.set("NODE_CODE", jumpedNodes + nodeDefBean.getStr("NODE_CODE"));
                if (lineBean.getStr("SEND_TEXT").isEmpty()) {
                    stepBean.set("NODE_NAME", nodeDefBean.getStr("NODE_NAME"));
                } else {
                    stepBean.set("NODE_NAME", lineBean.getStr("SEND_TEXT"));
                }
                stepBean.set("ACT_CSS", nodeDefBean.getStr("NODE_CODE"));
                stepBean.set(NODE_ORDER, lineBean.get("LINE_SORT", NODE_ORDER_COMMON_DEFAULT));
                if (!lineBean.isEmpty("CONFIRM_MSG")) {
                    stepBean.set("CONFIRM_MSG", lineBean.getStr("CONFIRM_MSG"));
                }
                nextSteps.add(stepBean);
            } else {
                if (lineBean.isNotEmpty("COND_MSG")) {
                	//Bean stepBean = new Bean();
                    stepBean.set("NODE_CODE", jumpedNodes + nodeDefBean.getStr("NODE_CODE"));
                    stepBean.set("NODE_NAME", nodeDefBean.getStr("NODE_NAME"));
                    stepBean.set("ACT_CSS", nodeDefBean.getStr("NODE_CODE"));
                    stepBean.set(NODE_ORDER, lineBean.get("LINE_SORT", NODE_ORDER_COMMON_DEFAULT));
                    // 设置不满足条件
                    stepBean.set("NOT_MEET_COND", "true");
                    // 设置提示信息
                    stepBean.set("COND_MSG", lineBean.get("COND_MSG"));
                    nextSteps.add(stepBean);
                }
            }
        }
    }
    
    /**
     * 
     * @param nodeBt 自由节点扩展接口
     * @param freeConfig 自由节点的配置
     * @param doUser 当前办理人
     * @param nextSteps 下一步可走的节点
     * @param curNodeCode 当前的节点code
     * @param nextNodeDef 送交的节点的定义信息
     */
    private void handleFreeNode(WfNodeBtInterface nodeBt, Bean freeConfig, UserBean doUser, 
    		List<Bean> nextSteps, Bean nextNodeDef, String jumpedNodes) {
    	
    	//如果满足了穿透的条件
    	if (nodeBt.canBreakThrough(freeConfig, this.getProcess().getServInstBean(), this, nextNodeDef)) { 
    		String assignNode = nodeBt.getAssignNode(freeConfig, this.getProcess().getServInstBean(), this);
    		
    		jumpedNodes += nextNodeDef.getStr("NODE_CODE") + WfeConstant.FREE_SEPARATOR;
    		addNodeDefBtns(doUser, nextSteps, nextNodeDef.getStr("NODE_CODE"), assignNode, jumpedNodes);
    	}
    }
    
    /**
     * @param nodeDef 节点定义
     * @return 节点是否定义了可穿透的属性
     */
    private Bean getFreeConfig(WfNodeDef nodeDef) {
    	if (nodeDef.isEmpty("BREAK_THROUGH")) {
    		return null;
    	}

    	String freeDef = nodeDef.getStr("BREAK_THROUGH");
    	
    	return JsonUtils.toBean(freeDef);
    }

    /**
     * 往nextSteps变量中增加返回×××按钮
     * @param nextSteps 工作流按钮集合
     */
    private void addReturnBtns(List<Bean> nextSteps) {
        List<Bean> returnNodeBeans = this.getReturns();
        if (null != returnNodeBeans && returnNodeBeans.size() > 0) {
            for (Bean nodeInstTemp : returnNodeBeans) {
                Bean backStepBean = new Bean();
                backStepBean.set("NODE_CODE", "R" + nodeInstTemp.getStr("NODE_CODE"));
                // 获取上一任务的doneUser
                String doneUserName = "";
                String doneUserId = "";
               
                final int doneType = nodeInstTemp.getInt("DONE_TYPE");
                if (doneType == WfeConstant.NODE_DONE_TYPE_WITHDRAW) {
                	if(nodeInstTemp.isEmpty("TO_USER_ID")) {
                		continue;
                	}
                	WfAct wfAct = new WfAct(nodeInstTemp.getId(),true);
                    // 收回
                	Bean bean = wfAct.getNodeUser(nodeInstTemp.getStr("TO_USER_ID"));
                	if(bean == null) { // 找不到用户，则忽略。
                		continue;
                	}
//                  doneUserId = nodeInstTemp.getStr("TO_USER_ID");
//                  doneUserName = nodeInstTemp.getStr("TO_USER_NAME");
                	doneUserId = bean.getStr("TO_USER_ID") + "^" + bean.getStr("TO_DEPT_ID");
                	doneUserName = bean.getStr("TO_USER_NAME");
                } else {
                    if (nodeInstTemp.getInt("NODE_IF_RUNNING") == Constant.YES_INT) { //节点未办结
//                        doneUserId = nodeInstTemp.getStr("TO_USER_ID");
//                        doneUserName = nodeInstTemp.getStr("TO_USER_NAME");
                    	if(nodeInstTemp.isEmpty("TO_USER_ID")) {
                    		continue;
                    	}
                    	WfAct wfAct = new WfAct(nodeInstTemp.getId(),true);
                    	Bean bean = wfAct.getNodeUser(nodeInstTemp.getStr("TO_USER_ID"));
                    	if(bean == null) { // 找不到用户，则忽略。
                    		continue;
                    	}
                    	
                    	doneUserId = bean.getStr("TO_USER_ID") + "^" + bean.getStr("TO_DEPT_ID");
                    	doneUserName = bean.getStr("TO_USER_NAME");
                    } else { //节点已办结
                        doneUserId = nodeInstTemp.getStr("DONE_USER_ID") + "^" + nodeInstTemp.getStr("DONE_DEPT_IDS");
                        doneUserName = nodeInstTemp.getStr("DONE_USER_NAME");
                    }
                }
                //如果用户找不到，则忽略
                if (StringUtils.isEmpty(doneUserId)) {
                    continue;
                }
                // 找不到指定用户，则忽略
                try {
                    UserBean rtnUser = UserMgr.getUserByUserDept(doneUserId);
                    if (rtnUser == null) {
                        continue;
                    }
                } catch (Exception e) {
                    continue;
                }
                
                // 如果返回节点操作类型为自动流转，则找出节点用户映射表中的用户列表
                if (WfeConstant.NODE_OPT_TYPE_AUTO_FLOW == nodeInstTemp.getInt("OPT_TYPE")) {
                	WfAct wfAct = new WfAct(nodeInstTemp.getId(),true);
                	List<Bean> nodeUsers = wfAct.getNodeUserBeanList();
                	if (nodeUsers == null || nodeUsers.isEmpty()) {
                		continue;
                	}
                	StringBuffer userSb = new StringBuffer("");
                	StringBuffer userNameSb = new StringBuffer("");
                	for (Bean nodeUser : nodeUsers) {
						if (nodeUser != null && !nodeUser.isEmpty()
								&& nodeUser.isNotEmpty("TO_USER_ID")) {
							// 找不到指定用户，则忽略
							try {
								if (UserMgr.getUser(nodeUser
										.getStr("TO_USER_ID")) == null) {
									continue;
								} else {
									userSb.append(",");
									userSb.append(nodeUser.getStr("TO_USER_ID"));
									userSb.append("^");
									userSb.append(nodeUser.getStr("TO_DEPT_ID"));
									userNameSb.append(","
											+ nodeUser.getStr("TO_USER_NAME"));
								}
							} catch (Exception e) {
								continue;
							}
						}
                	}
                	if (userSb.length() <= 0) {
                		continue;
                	}
                	
                	doneUserId = userSb.substring(1);
                	
                	if(userNameSb.length() > 0) {
                		doneUserName = userNameSb.substring(1);
                	}
                }
                // 判断上一处理人是否为被授权人
                String subUserId = nodeInstTemp.getStr("SUB_USER_ID");
                String subDeptId = nodeInstTemp.getStr("SUB_DEPT_IDS");
                if (StringUtils.isNotBlank(subUserId) 
                		&& StringUtils.isNotBlank(subDeptId)) {
                	doneUserId += "@" + subUserId + "^" + subDeptId;
                }
                
                //去掉用户姓名签名的空格
                doneUserName = doneUserName.replaceAll(" ", "");
                
                // 设置NODE_NAME，优先取连线上的设定
                Bean lineDef = this.getProcess().getProcDef()
                        .findLineDef(nodeInstTemp.getStr("NODE_CODE"), this.getCode());
                if (lineDef != null && !lineDef.getStr("RETURN_TEXT").isEmpty()) {
                    backStepBean.set("NODE_NAME", lineDef.getStr("RETURN_TEXT"));
                } else {
                    String confBackName = createBackUserBtnName(nodeInstTemp, doneUserName);
                    backStepBean.set("NODE_NAME", confBackName);
                }
                backStepBean.set("NODE_USER", doneUserId);
                backStepBean.set("NODE_USER_NAME", doneUserName);
                backStepBean.set("ACT_CSS", nodeInstTemp.getStr("NODE_CODE"));
                backStepBean.set(NODE_ORDER, NODE_ORDER_RETURN2LAST);

                nextSteps.add(backStepBean);
            }
        }
    }
    
    /**
     * 根据系统配置设置返回按钮
     * @param nodeInstTemp 节点实例名
     * @param doneUserName 办理用户名
     * @return 
     */
	private String createBackUserBtnName(Bean nodeInstTemp, String doneUserName) {
		// 返回#NODE_NAME#(#USER_NAME#)
		String confBackName = Context.getSyConf("SY_WF_BACK_NODE_NAME", "返回#USER_NAME#");
		
		if (confBackName.indexOf("#USER_NAME#") >= 0) {
		    confBackName = confBackName.replaceAll("#USER_NAME#", doneUserName);
		}
		if (confBackName.indexOf("#NODE_NAME#") >= 0) {
		    WfNodeDef nodeDefBean = this.getProcess().getProcDef()
		            .findNode(nodeInstTemp.getStr("NODE_CODE"));
		    String nodeName = nodeDefBean.getStr("NODE_NAME");
		    
		    confBackName = confBackName.replaceAll("#NODE_NAME#", nodeName);
		}
		return confBackName;
	}
    
    

    /**
     * 在返回按钮中 得到能够返回的节点的列表
     * 
     * @return 能够返回的节点实例列表
     */
    private List<Bean> getReturns() {
        List<Bean> rtnWfActBeans = new ArrayList<Bean>();
        // 取得流程定义中能够返回的线的列表
        List<WfLineDef> lineCanReturnList = this.getProcess().getProcDef()
                .findReturnLineDefList(this.getCode());

        if (lineCanReturnList.size() < 1) {
            return null;
        }
        
        // 
        HashSet<String> preLineCodeSet = new HashSet<String>();
        for (Bean lineBean : lineCanReturnList) {
            preLineCodeSet.add(lineBean.getStr("LINE_CODE"));
        }

        // 取得 能够返回的 流程实例中的节点实例
        List<Bean> allInstList = this.getProcess().getAllNodeInstList();
        List<Bean> nodeInstList = new ArrayList<Bean>();
        // 取得所有从可以返回的节点实例对象
        HashSet<String> nodeInstIds = new HashSet<String>();
        for (Bean bean : allInstList) {
            String preLineCode = bean.getStr("PRE_LINE_CODE");
            if(preLineCodeSet.contains(preLineCode)) {
                nodeInstIds.add(bean.getStr("PRE_NI_ID"));
                nodeInstList.add(bean);
            }
        }


        if (nodeInstIds.size() < 1) {
            return null;
        }

        String nodeInstId = null;
        // 判断上一个节点是否在能返回的列表里面 ， 是则返回
        if (nodeInstIds.contains(this.getNodeInstBean().getStr("PRE_NI_ID"))) {
            nodeInstId = this.getNodeInstBean().getStr("PRE_NI_ID");
        } else if (!this.getNodeInstBean().getStr("PRE_NI_ID").isEmpty()) {
            nodeInstId = getValidSrcActID(
                    this.getNodeInstBean().getStr("PRE_NI_ID"), nodeInstIds);
        }

        if (nodeInstId != null) {
            for (Bean bean : allInstList) {
                if (bean.getStr("NI_ID").equals(nodeInstId)) {
                    rtnWfActBeans.add(bean);
                    break;
                }
            }
        }

        return rtnWfActBeans;
    }

    /**
     * 递归查询上一个能返回的节点
     * 
     * @param nodeInstId 节点实例ID
     * @param nodeInstIds 节点实例ID列表
     * @return 有效的节点实例ID
     */
    private String getValidSrcActID(String nodeInstId, HashSet<String> nodeInstIds) {
//        Bean nodeInstTemp = WfNodeInstDao.findNodeInstById(nodeInstId);
        Bean nodeInstTemp = this.getProcess().getNodeInstBean(nodeInstId);

        // 通过srcNodeCode 找到流程中的节点实例
        if (nodeInstIds.contains(nodeInstTemp.getStr("PRE_NI_ID"))) {
            return nodeInstTemp.getStr("PRE_NI_ID");
        } else {
            // 没有前节点，则返回空
            if (nodeInstTemp.isEmpty("PRE_NI_ID")) {
                return null;
            } else {
                return getValidSrcActID(nodeInstTemp.getStr("PRE_NI_ID"),
                        nodeInstIds);
            }
        }

    }

    /**
     * 节点实例对象
     * 
     * @return 节点实例对象
     */
    public Bean getNodeInstBean() {
        return nodeInstBean;
    }

    /**
     * 流程未办结时，通过当前节点实例对象 得到 上一个节点实例
     * 
     * @return 上一个节点实例对象
     */
    public WfAct getPreWfAct() {
        String prevNIID = this.getNodeInstBean().getStr("PRE_NI_ID");
        return this.getProcess().getWfAct(prevNIID);
    }
    
    /**
     * 流程未办结时，通过当前节点实例对象 得到 下一节点实例
     * 
     * @return 下一个节点实例对象
     */
    public List<WfAct> getNextWfAct() {
//        List<Bean> nodeInstList = WfNodeInstDao.getNextNodeInstList(getId());
        List<Bean> nodeInstList = this.getProcess().getNextNodeInstList(getId());
        if (nodeInstList == null || nodeInstList.size() < 1) {
            return null;
        } else {
            List<WfAct> nextWfActs = new ArrayList<WfAct>(nodeInstList.size());
            for (Bean nodeInst : nodeInstList) {
                nextWfActs.add(new WfAct(getProcess(), nodeInst));
            }
            return nextWfActs;
        }
    }
    

    /**
     * 节点实例是否 已经 超过 节点所限定的完成时间
     * 
     * @param strLimitTime 节点限定完成时间
     * @return 是否已经过期
     */
    public boolean hasExpired(String strLimitTime) {
        String nowDate = DateUtils.getDate();

        if ((strLimitTime == null) || (strLimitTime.length() < 10)) {
            return false;
        }

        int iRtn = nowDate.compareTo(strLimitTime.substring(0, 10));

        if (iRtn > 0) {
            return true;
        }
        return false;
    }

    /**
     * 初始化流程节点实例
     * 
     * @param niId 节点实例ID
     */
    private void init(String niId) {
    	if(StringUtils.isBlank(niId)) {
    		throw new TipException("niId 参数不能为空。");
    	}
        if (this.isRunningData()) {
            this.nodeInstBean = WfNodeInstDao.findNodeInstById(niId);
        } else {
            this.nodeInstBean = WfNodeInstHisDao.findNodeInstById(niId);
        }
    }

    /**
     * 节点是否活动
     * 
     * @return 节点是否运行状态
     */
    public boolean isRunning() {
        if (this.getNodeInstBean().getInt("NODE_IF_RUNNING") == WfeConstant.NODE_IS_RUNNING) {
            return true;
        }
        return false;
    }

    /**
     * 发送待办事件
     * @param doneUser 办理人
     */
    public void sendTodo(UserBean doneUserBean) {
        WfTodoProvider.sendTodoForToNext(this, doneUserBean, 0);
    }
    
    /**
     * 发送待办事件
     * @param doneUser 办理人
     * @param niCount 数量
     */
    public void sendTodo(UserBean doneUserBean, int niCount) {
        WfTodoProvider.sendTodoForToNext(this, doneUserBean, niCount);
    }

    /**
     * 发送待办事件
     */
    public void sendTodo() {
        sendTodo(Context.getUserBean());
    }
    
    /**
     * 在合并节点 终止并发流程，没有完全收完，将所有活动节点都结束。 状态为终止
     * @param wfParam 办理用户
     * @return 合并后的节点实例
     */
    public WfAct stopParallel(WfParam wfParam) {
        if (canStopParallel() || wfParam.getBoolean(WfeConstant.FINISH_WF_FORCE_FLAG)) {
            UserBean doUser = wfParam.getDoneUser();
            
            finishParallelNodeInst(wfParam);

            // 结束并发
            WfAct newWfAct = completeParallel(doUser);

            // 回写表单信息 在这里调用 newWfAct.updateServInstWfInfo() 取的活动节点的列表还是所有的并发的
            newWfAct.updateServWfInfo(new WfParam());

            return newWfAct;
        } else {
            throw new TipException("不是合并节点，不能结束并发流程");
        }
    }

    /**
     * 当活动节点大于1， 终止其中一个活动的节点 , 不创建新的节点实例
     * @param doUser 办理用户
     */
    public void stop(UserBean doUser) {
        if (this.getProcess().getRunningWfAct().size() > 1) { // 活动节点数大于1
            stop(doUser, WfeConstant.NODE_DONE_TYPE_STOP, WfeConstant.NODE_DONE_TYPE_STOP_DESC);
        }
    }
    
    /**
     * 强制结束当前节点，不会去判断当前活动节点数是否大于1
     * @param doUser 办理用户
     * @param doneType 节点结束类型
     * @param doneDesc 节点结束说明
     */
    public void stop(UserBean doUser, int doneType, String doneDesc) {
        WfParam paramBean = new WfParam();
        paramBean.setDoneType(doneType);
        paramBean.setDoneDesc(doneDesc);

        paramBean.setDoneUser(doUser);

        this.stop(paramBean);
    }
    
    /**
     * 参数Bean中至少需要包含doneUser,doneType,doneDesc参数
     * @param paramBean 参数Bean
     */
    public void stop(WfParam paramBean) {
        this.finish(paramBean);
        this.updateServWfInfo(paramBean);
    }

    /**
     * TODO 节点的办理类型为手工结束的时候，已经往下下一个节点送文件了，但是当前节点还是活动的，如果自己不需要办理了，则要手工办结。用于同时送不同的节点。
     * 
     * @return 能否手动结束某个节点实例
     */
    public boolean canHandStopNode() {
        if (this.isRunning() && this.getProcess().getRunningWfAct().size() > 1) {
            // 是不是还需要查存在不存在 以这个点 为 pre_node的点存在
            if (this.getNodeDef().getInt("NODE_IF_AUTOEND") == WfeConstant.NODE_AUTO_END_NO) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param nextNodeCode 下个节点编码
     * @param lineDefBean 线定义
     * @return 是否是退回
     */
    private boolean isBack(String nextNodeCode, Bean lineDefBean) {
        String targetNodeCode = lineDefBean.getStr("TAR_NODE_CODE");
        if (targetNodeCode.equals(nextNodeCode)) {
            return false;
        }

        return true;
    }
    
    
    /**
     * 根据paramBean中提供的toUser toDept toRole信息及要送交的节点的信息，解析出要生成的节点实例的任务处理人。
     * @param nextNodeCode 下一节点code
     * @param paramBean 流程实例信息，含有选择的人、部门、角色信息
     * @return 节点实例的任务处理人列表
     */
    public List<GroupBean> createNextActors(String nextNodeCode, WfParam paramBean) {
        int typeTo = paramBean.getTypeTo();
        Bean lineDefBean = this.getProcess().getProcDef().findLineDef(this.getCode(), nextNodeCode);
        
        //解析出处理人
        List<GroupBean> groupList = new ArrayList<GroupBean>();
        if (typeTo == WfParam.TYPE_TO_USER) {
            //选择的是用户 paramBean.getToUserIdArray(); paramBean.getToUserDeptIdArray()
            final String[] nextUserArr = paramBean.getToUserIdArray();
            final String[] nextDeptArr = paramBean.getToUserDeptIdArray();
            
            //取消办结 没有连线信息
            if (lineDefBean == null || lineDefBean.getInt("IF_PARALLEL") != WfeConstant.NODE_IS_PARALLEL) {
                //取消办结 或  非并发 
                GroupBean group = new GroupBean();
                group.addUsers(nextUserArr, nextDeptArr);
                groupList.add(group);
            } else {
                //并发 为每个用户生成一个任务
                for (int i = 0; i < nextUserArr.length; i++) {
                    GroupBean group = new GroupBean();
                    group.addUser(nextUserArr[i], nextDeptArr[i]);
                    groupList.add(group);
                }
            }
        } else {
            //选择的是部门、角色  获取用户 
            String depts = paramBean.getToRoleDept();
            String[] deptArr = depts.split(",");
            List<UserBean> users = UserMgr.getUserListByDeptRole(depts, paramBean.getToRole());
            //并发节点的话，为每个部门生成一个任务
            if (lineDefBean.getInt("IF_PARALLEL") == WfeConstant.NODE_IS_PARALLEL) {
                Map<String, List<WfActor>> deptUserMap = new HashMap<String, List<WfActor>>();
                for (int i = 0; i < deptArr.length; i++) {
                    deptUserMap.put(deptArr[i], new ArrayList<WfActor>());
                }
                for (UserBean user : users) {
                	WfActor actor = new WfActor(user.getId(), user.getDeptCode());
                    if (deptUserMap.containsKey(user.getDeptCode())) {
                        deptUserMap.get(user.getDeptCode()).add(actor);
                    } else if (deptUserMap.containsKey(user.getTDeptCode())) {
                        deptUserMap.get(user.getTDeptCode()).add(actor);
                    }
                }
				for (String deptCode : deptUserMap.keySet()) {
					GroupBean group = new GroupBean();
					List<WfActor> list = deptUserMap.get(deptCode);
					group.addUsers(list);
					groupList.add(group);
				}
            } else {
                GroupBean group = new GroupBean();
                for (UserBean user : users) {
                    group.addUser(user.getId(), user.getDeptCode());
                }
                groupList.add(group);
            }
        }
        
        return groupList;     
    }
    
    

    /**
     * 流程转到下个节点 更新当前节点实例信息， 插入新的节点实例
     * 
     * @param nextNodeCode 下个节点定义编码
     * @param paramBean 参数bean对象. 通过toUser toDept toRole设置了处理人信息。
     * @return 当前节点实例对象
     */
    public WfAct toNext(String nextNodeCode, WfParam paramBean) {
        GroupBean users = createNextActors(nextNodeCode, paramBean).get(0);
        WfAct nextWfAct = toNextSingle(nextNodeCode, paramBean, users, -1);
        nextWfAct.updateServWfInfo(paramBean);
        
        return nextWfAct;
    }
    
    /**
     * 
     * @param nextNodeCode 下一个节点的编号
     * @param paramBean 流程参数
     * @param groupUser 办理用户
     * @return 送交给目标用户之后的流程实例列表
     */
    private WfAct toNextSingle(String nextNodeCode, WfParam paramBean, GroupBean groupUser, int parallelIdx) {
        if(groupUser.getAllActors().size() == 0) {
            throw new TipException("流程办理人不能为空。");
        }
        
    	String nodeForGetLine = nextNodeCode;
    	String nodeInParam = paramBean.getStr("NODE_CODE");
    	if (nodeInParam.indexOf(WfeConstant.FREE_SEPARATOR) > 0) {
    		nodeForGetLine = nodeInParam.substring(0, nodeInParam.indexOf(WfeConstant.FREE_SEPARATOR));
    	}
    	
    	WfLineDef lineDefBean = this.getProcess().getProcDef()
                .findLineDef(this.getCode(), nodeForGetLine);

        WfNodeDef nodeDef = this.getProcess().getProcDef()
                .findNode(nextNodeCode);

        WfAct nextWfInst = createNewNodeIns(paramBean, nodeDef,
                lineDefBean, groupUser, parallelIdx);

        // 送交下个节点的时候，需要判断，下个节点是不是合并点，如果是，调用自动合并的方法
        nextWfInst.autoConvergeNewNode();
        
        // 取得未汇合的节点数量
        int niCount = nextWfInst.getNotConvergeNodeCount();
        
        //根据下一节点的操作码，判断是否需要产生待办    added by Tanyh 20160531
        boolean noTodo = false;
        if (nextWfInst.getNodeInstBean().getInt("OPT_TYPE") == WfeConstant.NODE_OPT_TYPE_AUTO_FLOW || 
        		nextWfInst.getNodeInstBean().getInt("OPT_TYPE") == WfeConstant.NODE_OPT_TYPE_AUTO_END) {
        	//自动审核，或者自动办结,则不需要产生待办
        	noTodo = true;
        } else {
        	//需要产生待办
        	noTodo = false;
        }
        
        //是否只显示汇合后的最后一条待办
        String showLastTodo = Context.getSyConf(WfeConstant.CONF_WF_CONVERGE_LAST_TODO, "false");
        if (!noTodo) {
        	if (showLastTodo.equals("true")) {
                if (niCount == 0) {
                    // 往下送交的时候，发送待办
                    nextWfInst.sendTodo(paramBean.getDoneUser(), niCount);
                }
            } else {
                // 往下送交的时候，发送待办
                nextWfInst.sendTodo(paramBean.getDoneUser(), niCount);
            }
        }
        
        nextWfInst.updateServWhenEnter(this, paramBean);

        if (null != lineDefBean) { 
        	String toGroupUserStr = groupUser.getUserIdStr();
        	paramBean.set("GROUP_USERS_STR", toGroupUserStr);
        	nextWfInst.setWfParam(paramBean);
            if (!isBack(nextNodeCode, lineDefBean)) {        
                // 如果是退回，则不执行更新操作。
                updateServByLineDef(lineDefBean, paramBean);
                callLineEvent(nextWfInst, lineDefBean);
            } else {
                callLineEvent(nextWfInst, lineDefBean);
            }
        }
        
        this.getProcess().cleanAllNodeInstList();
        
        return nextWfInst;
    }
    
    /**
     * 
     * @param nextNodeCode 下个节点ID
     * @param doneUserCode 办理人
     * @param toUserCode 返回到的那个人 （送交之后接收的那个人）
     */
    public void toNextForTuiHui(String nextNodeCode, String doneUserCode, String toUserId, String toUserDeptId) {
    	UserBean doneUser = UserMgr.getUser(doneUserCode);
    	
    	WfParam paramBean = new WfParam();
        paramBean.setDoneUser(doneUser);
        paramBean.setDoneType(WfeConstant.NODE_DONE_TYPE_AUTORTN);
        paramBean.setDoneDesc(WfeConstant.NODE_DONE_TYPE_AUTORTN_DESC);
        
    	GroupBean user = new GroupBean();
        user.addUser(toUserId, toUserDeptId);
    	
        finish(paramBean); //结束当前节点
        
    	this.toNextSingle(nextNodeCode, paramBean, user, -1);  //退回给上个节点
    	
    	updateServWfInfo(paramBean); //更新办理的相关信息
    }
    
    /**
     * 
     * @param nextNodeCode 下一个节点的编号
     * @param wfParam 流程参数
     * @param nextActors 目标用户
     * @return 送交给目标用户之后的任务实例列表
     */
    public List<WfAct> toNext(String nextNodeCode, WfParam wfParam, List<GroupBean> nextActors) {
        List<WfAct> wfActs = new ArrayList<WfAct>();
        if (nextActors == null || nextActors.size() == 0) {
            return wfActs;
        }
        //处理子流程节点
        if (this.getProcess().getProcDef().getSubProcNodeDefBeans().containsKey(nextNodeCode)) {
            //送交子流程节点，送交人：取当前任务办理人
            GroupBean users = new GroupBean();
            users.addUser(wfParam.getDoneUser().getId(), wfParam.getDoneUser().getDeptCode());
            WfAct subProcAct = this.toNextSingle(nextNodeCode, wfParam, users, -1);
            //启动子流程的运行
            WfSubProcActHandler actHandler = new WfSubProcActHandler(subProcAct);
            actHandler.startSubProcess(nextActors);
            //主流程可否继续运行
            if (!actHandler.canHandle()) {
                subProcAct.stop(wfParam.getDoneUser(), WfeConstant.NODE_DONE_TYPE_STOP,
                        WfeConstant.NODE_DONE_TYPE_STOP_DESC);
            }
            //返回值取主流程的任务
            wfActs.add(subProcAct);
        } else {
            for (int i = 0; i < nextActors.size(); i++) {
                GroupBean groupUser = nextActors.get(i);
                if (groupUser.getAllActors().size() > 0) {
                    WfAct nextWfAct = this.toNextSingle(nextNodeCode, wfParam,
                            groupUser, i);
                    wfActs.add(nextWfAct);
                }
            }
        }
        
        if(wfActs.size() > 0) {
            wfActs.get(wfActs.size() - 1).updateServWfInfo(wfParam);
        }
        
        return wfActs;
    }
    
    /**
     * 根据当前节点以及参数信息获取下一送交节点的资源对象
     * @param paramBean 参数对象
     * @return WfeBinder 资源对象
     * @author Tanyh20160601 从ProcServ类的getNextStepUsersForSelect方法提取出来的代码，用于获取指定节点的资源对象
     */
    public WfeBinder getWfBinder(ParamBean paramBean) {
    	String nextNodeCode = paramBean.getStr("NODE_CODE");
    	WfProcDef procDef = this.getProcess().getProcDef();
        
        if (nextNodeCode.indexOf(WfeConstant.FREE_SEPARATOR) > 0) { //存在穿透的
            nextNodeCode = nextNodeCode.substring(nextNodeCode.lastIndexOf(WfeConstant.FREE_SEPARATOR) + 1);
        }
        
        WfNodeDef nextNodeDef = procDef.findNode(nextNodeCode);

        UserBean doUser = WfUtils.getDoUserBean(paramBean);
        
        // 如果用户属于多个部门，则使用流程中指定的用户身份办理
//        doUser = createDoUserBean(doUser.getCode());
        
        WfBinderManager wfBinderManager = WfeBinderHelper.createWfBinderManager(nextNodeDef, this, doUser);
        
        String currNodeCode = this.getCode();
        if (paramBean.getStr("NODE_CODE").indexOf(WfeConstant.FREE_SEPARATOR) > 0) {
            //A_B_C -> B, A_B -> A
            String[] nodes = paramBean.getStr("NODE_CODE").split(WfeConstant.FREE_SEPARATOR);
            
            currNodeCode = nodes[nodes.length - 2];
        }
        
        WfLineDef lineBean = procDef.findLineDef(currNodeCode, nextNodeDef.getStr("NODE_CODE"));
        
        if (lineBean.isEnableOrgDef()) { //如果启动了线组织资源定义，则使用线组织资源定义
            wfBinderManager.initBinderResource(lineBean.getOrgDefBean());
        } else { //使用节点组织资源定义
            wfBinderManager.initBinderResource(nextNodeDef);
        }
        
        String excludeUsers = paramBean.getStr("EXCLUDE_USERS").replaceAll(WfeBinder.USER_NODE_PREFIX + ":", "");
        
        wfBinderManager.addExcludeUsers(excludeUsers);
        
        // 如果目标节点和当前节点相同，则去掉当前用户。
        if (Context.getSyConf("WF_NOT_TO_ONESELF_ON_SAME_NODE", true) 
                && this.getNodeInstBean().getStr("NODE_CODE").equals(nextNodeCode)) {
            wfBinderManager.addExcludeUsers(doUser.getCode());
        }
        return wfBinderManager.getWfeBinder();
    }
    
//    private UserBean createDoUserBean(String userCode) {
//    	List<Bean> users = this.getNodeUserBeanList();
//    	for(Bean bean: users) {
//    		if(bean.getStr("TO_USER_ID").equals(userCode)) {
//    			final String doUserCode = bean.getStr("TO_USER_ID");
//    			final String doDeptCode = bean.getStr("TO_DEPT_ID");
//    			UserBean userBean = UserMgr.getUser(doUserCode, doDeptCode);
//    			if(userBean != null) {
//    				return userBean;
//    			}
//    		}
//    	}
//    	
//    	throw new TipException("无效的办理用户。");
//    }
   

    /**
     * 调用线事件的监听类
     * @param nextWfAct 下一个节点的ID
     * @param lineDefBean 线定义Bean
     */
    private void callLineEvent(WfAct nextWfAct, Bean lineDefBean) {
        if (lineDefBean.isEmpty("LINE_EVENT")) {
            return;
        }
        
        String lineEvent = lineDefBean.getStr("LINE_EVENT");
        AbstractLineEvent lineEventObj = (AbstractLineEvent) Lang.createObject(AbstractLineEvent.class,
                lineEvent);
        if (lineEventObj == null) {
            return;
        }
        if (isBack(nextWfAct.getCode(), lineDefBean)) {
            lineEventObj.backward(this, nextWfAct, lineDefBean);
        } else {
            lineEventObj.forward(this, nextWfAct, lineDefBean);
        }

    }
    
    
    /**
     * 更新意见信息
     * @param doneUser 当前任务办理用户
     * @param toUsers 所有接收用户ID
     * @param nextNodeCode 下个节点编码
     */
    public void updateMind(String doneUser, String[] toUsers, List<String> nextNodeCodes) {
        UserBean doUser = UserMgr.getUser(doneUser);
        boolean isOutDept = false;

        String srcNodeCode = this.getCode();
        for(String nextNodeCode: nextNodeCodes) { // 判断节点是否已经送出部门
            Bean lineDef = this.getProcess().getProcDef().findLineDef(srcNodeCode, nextNodeCode);
            if (null != lineDef && lineDef.getInt("IF_OUT_DEPT") == 1) { // 线上配置了，强制出部门
                isOutDept = true;
                break;
            }
        }
        
        if(!isOutDept) { // 如果节点上没有定义是否送出部门，则从人员上判断是否送出部门。
            String tDeptCode = doUser.getTDeptCode();
            for (String user : toUsers) {
                UserBean toUser = UserMgr.getUser(user.trim());
                if (!tDeptCode.equals(toUser.getTDeptCode())) {
                    isOutDept = true;
                }
            }
        }
        
        DisabledMindUpdater mindUpdate = new DisabledMindUpdater(this, doUser);
        if (isOutDept) {
            mindUpdate.toOtherDept();
        } else {
            mindUpdate.toNextNode();
        }
    }
    
    
    /**
     * 
     * @param doneUser 办理用户
     */
    public void updateServWhenMindSave(UserBean doneUser) {
        List<Bean> list = this.getNodeDef().getUpdateExpressWhenMindSave();
        
        WfParam paramBean = new WfParam();
        paramBean.setDoneUser(doneUser);
        execUpdateExpress(list, paramBean);
    }
    
    /**
     * 
     * @param paramBean 流程办理参数
     */
    protected void updateServWhenEnter(WfAct preWfAct, WfParam paramBean) {
        List<Bean> list = this.getNodeDef().getUpdateExpressWhenEnter();
        execUpdateExpress(list, paramBean);
        
        if(this.getProcess().getProcDef().isNotEmpty("NODE_PUB_LSTN")) {
            //设置在流程定义上的节点事件公共监听类
            String cls = this.getProcess().getProcDef().getStr("NODE_PUB_LSTN");
            execAfterEnterNodeEvent(preWfAct, paramBean, cls);
        }
        
        if (this.getNodeDef().isNotEmpty("EVENT_CLS")) {
            //设置在节点定义上的事件监听类
            String cls = this.getNodeDef().getStr("EVENT_CLS");
            execAfterEnterNodeEvent(preWfAct, paramBean, cls);
        }
    }
    
    private void execAfterEnterNodeEvent(WfAct preWfAct ,
        WfParam paramBean ,
        String confStr) {
        String cls = confStr;
        String config = "";
        int pos = confStr.indexOf(",");
        if (pos > 0) {
            cls = confStr.substring(0, pos);
            config = confStr.substring(pos + 1);
        }
        
        try {
            AbstractNodeEvent eventCls = Lang.createObject(
                    AbstractNodeEvent.class, cls);
            eventCls.afterEnter(preWfAct, this, paramBean, config);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    
    /**
     * 
     * @param paramBean 流程办理参数
     */
    private void updateServWhenFinish(WfParam paramBean) {
        List<Bean> list = this.getNodeDef().getUpdateExpressWhenFinish();
        execUpdateExpress(list, paramBean);
        
        if(this.getProcess().getProcDef().isNotEmpty("NODE_PUB_LSTN")) {
            String cls = this.getProcess().getProcDef().getStr("NODE_PUB_LSTN");
            execAfterFinishNodeEvent(paramBean, cls);
        }
        
        if (this.getNodeDef().isNotEmpty("EVENT_CLS")) {
            String cls = this.getNodeDef().getStr("EVENT_CLS");
            execAfterFinishNodeEvent(paramBean, cls);
        }
    }

    private void execAfterFinishNodeEvent(WfParam paramBean , String confStr) {
        String cls = confStr;
        String config = "";
        int pos = confStr.indexOf(",");
        if (pos > 0) {
            cls = confStr.substring(0, pos);
            config = confStr.substring(pos + 1);
        }
        try {
            AbstractNodeEvent eventCls = Lang.createObject(
                    AbstractNodeEvent.class, cls);
            eventCls.afterFinish(this, paramBean, config);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 根据线上定义的节点，更新服务字段
     * @param lineDefBean 线定义Bean
     * @param paramBean 参数
     */
    private void updateServByLineDef(Bean lineDefBean, WfParam paramBean) {
        if (lineDefBean == null || lineDefBean.isEmpty("UPDATE_EXPRESS_LIST")) {
            return;
        }

        // 更新表达式
        List<Bean> exprList = lineDefBean.getList("UPDATE_EXPRESS_LIST");

        execUpdateExpress(exprList, paramBean);
    }

    /**
     * @param exprList 表达式Bean列表。表达式Bean有3个属性：UPDATE_CONDS（更新条件），UPDATE_FIELD（更新字段名）， UPDATE_VALUE（更新表达式）。
     * @param paramBean 参数
     */
    private void execUpdateExpress(List<Bean> exprList, WfParam paramBean) {
        if (exprList == null || exprList.size() == 0) {
            return;
        }
        boolean needUpdate = false;
        JavaScriptEngine jsEng = new JavaScriptEngine(this.getProcess().getServInstBean());
        jsEng.addVar("param", paramBean);
        
        MindContextHelper mindContext = new MindContextHelper(this, paramBean.getDoneUser());
        jsEng.addVar("mindContext", mindContext);
        Bean dataBean = new Bean();
        for (int i = 0; i < exprList.size(); i++) {
            Bean expBean = exprList.get(i);
            String conds = ServUtils.replaceSysVars(expBean.getStr("UPDATE_CONDS"));
            if ((conds.length() == 0) || jsEng.isTrueScript(conds)) {
                String field = expBean.getStr("UPDATE_FIELD");
                String valueExp = ServUtils.replaceSysVars(expBean.getStr("UPDATE_VALUE"));
                String value = jsEng.runScript(valueExp);

                dataBean.set(field, value);
                needUpdate = true;
            }
        }

        if (needUpdate) {
            updateServInst(dataBean);
        }
    }

    /**
     * 流程转到下个节点 和 完成本节点
     * 
     * @param nextNodeCode 下个节点定义编码
     * @param paramBean 参数bean 通过toUser toDept toRole设置了处理人信息
     * @return 下个节点实例对象
     */
    public WfAct toNextAndEndMe(String nextNodeCode, WfParam paramBean) {
        finish(paramBean);
        return toNext(nextNodeCode, paramBean);
    }

    /**
     * 打开页面的时候，设置上打开时间
     */
    public void updateInstOpenTime() {
        Bean updateBean = new Bean(this.getId());
        updateBean.set("OPEN_TIME", DateUtils.getDatetime());
        if (this.isRunningData()) {
            ServDao.update(ServMgr.SY_WFE_NODE_INST, updateBean);
            this.updateServWfInfo(new WfParam());
        } else {
            ServDao.update(ServMgr.SY_WFE_NODE_INST_HIS, updateBean);
        }
    }
    
    /**
     * 把当前节点的工作流相关信息，更新到业务表单表中。
     * @param wfParam 工作流参数对象  doneUser
     */
    protected void updateServWfInfo(WfParam wfParam) {
        log.debug("update the serv inst , the node inst id is " + this.getId());

        // 新建一个Bean, 只update对应的4个字段
        Bean servInstBean = new Bean();
        // 如果流程正在执行过程中，则修改活动点数据，否则清空活动点数据。
        if (this.getProcess().isRunning()) {
            WfUserState userStatus = new WfUserState(this.getProcess());
            // 将活动的节点 的userCode 填到WF_USER
            servInstBean.set("S_WF_USER", userStatus.getUserCodes());
            // 将活动节点的用户状态放到S_WFE_STATUS字段中
            servInstBean.set("S_WF_USER_STATE", userStatus.getUserState());
            //活动节点名称
            servInstBean.set("S_WF_NODE", userStatus.getNodeNames());
        } else {
            if(wfParam.getDoneUser() != null) {
                //设置办结人姓名
                servInstBean.set("S_WF_USER", wfParam.getDoneUser().getCode());
            }
            //清空数据
            servInstBean.set("S_WF_USER_STATE", "");
            //清空数据
            servInstBean.set("S_WF_NODE", "");
        }

        // 这里记录保存 流程的ID
        servInstBean.set("S_WF_INST", this.getProcess().getId());

        servInstBean.set("S_WF_STATE",
                this.getProcess().getProcInstBean().get("INST_IF_RUNNING"));

        updateServInst(servInstBean);
    }

    /**
     * 更新审批单数据
     * @param servInstBean 审批单服务对应实例数据
     */
    private void updateServInst(Bean servInstBean) {
        try {
            ServDataDao.updateServInstBean(this.getProcess().getServId(), this
                    .getProcess().getDocId(), servInstBean);
        } catch (Exception e) {
            log.debug("update servBean occur problem , servId "
                    + this.getProcess().getServId() + " docId "
                    + this.getProcess().getDocId(), e);
        }
    }
    
    /**
     * 出现异常时，提供自动合并并发节点方法
     * @return boolean 是否自动合并
     */
    public boolean convergeNodeWhenException () {
    	if (this.getNotConvergeNodeCount() > 0) {
    		this.autoConvergeNewNode();
    		return true;
    	}
    	return false;
    }



    /**
     * 流程收回 考虑并发流怎么收回？
     * @param paramBean 参数Bean
     * @param niIds 节点实例ID数组
     * @return 节点实例对象
     */
    public WfAct withdraw(WfParam paramBean, String[] niIds) {
        // 从本节点出去的节点数大于1，则是并发
        // 从本节点出去的下个节点的实例列表
//        List<Bean> nodeInstFromThis = WfNodeInstDao
//                .getNextNodeInstList(this.getId());

        List<Bean> nodeInstFromThis = this.getProcess().getNextNodeInstList(getId());
        
        if (nodeInstFromThis == null || nodeInstFromThis.size() == 0) {
            throw new TipException("没有可供收回的活动点");
        }
        
        // 把节点实例ID放到Set对象中，便于判断是否存在
        HashSet<String> wfActIds = new HashSet<String>();
        if (niIds != null) {
            for (String id : niIds) {
                wfActIds.add(id);
            }
        }
        
        List<Bean> niBeanList = new ArrayList<Bean>();
        // 终止活动点
        for (Bean niBean : nodeInstFromThis) {
            // 节点是否运行，且是用户指定收回的实例
            if (niBean.getInt("NODE_IF_RUNNING") == Constant.YES_INT
                    && wfActIds.contains(niBean.getId()) && niBean.isEmpty("OPEN_TIME")) {
                niBeanList.add(niBean);
            }
        }
        
        if (niBeanList.size() == 0) {
            throw new TipException("节点已办理，不能收回");
        }
        
        // 结束指定节点
        for (Bean niBean : niBeanList) {
            WfAct endWfAct = new WfAct(this.getProcess(), niBean);
            // 办结已有待办
            endWfAct.finish(paramBean);
            // 收回的时候，那个被收回的人，待办硬删除
            WfTodoProvider.destroyTodo(endWfAct);
        }
        
        //如果当前节点活动，且不是自动结束节点，则不新起节点。
        if (this.isRunning() && this.getNodeDef().getInt("NODE_IF_AUTOEND") == Constant.NO_INT) {
            return this;
        }

        // 收回
        WfAct wfAct = new WfAct(this.getProcess(), nodeInstFromThis.get(0));

        // 以当前节点定义为目标节点
        WfAct newWfAct = wfAct.toNext(this.getCode(), paramBean);

        newWfAct.updateServWfInfo(paramBean);

        return newWfAct;
    }

    /**
     * 开始处理文件 ， 送部门和角色的时候，TO_USER_ID 为空的时候
     * 
     * @return 能否独占操作
     */
    public boolean canDuzhan() {
        if (!this.isRunning()) {
            return false;
        }

        /** TO_USER_ID 没值的话， 一定是需要独占才能开始处理工作  */
        if (this.getNodeInstBean().isEmpty("TO_USER_ID")) {
            return true;
        }

        return false;
    }

    /**
     * @param userId 指定用户ID
     * @return 是否当前节点是活动的，且指定用户是此节点的办理人。
     */
    public boolean isUserDoing(String userId) {
        if (!this.isRunning()) {
            return false;
        }

        List<Bean> nodeUsers = getNodeUserBeanList();
        for (Bean nodeUser: nodeUsers) {
            if (nodeUser.getStr("TO_USER_ID").equals(userId)) {
                return true;
            } else if(nodeUser.isNotEmpty("AUTH_USER")) {
            	final String authUser = nodeUser.getStr("AUTH_USER");
            	if(authUser.startsWith(userId + "^")) {
            		return true;
            	}
            }
        }
        return false;
    }
    
    /**
     * 
     * @return 取得当前节点办理用户列表
     */
    public List<Bean> getNodeUserBeanList() {
        SqlBean sqlBean = new SqlBean();
        sqlBean.and("NI_ID", this.getId());
        List<Bean> nodeUsers = null;
        if(this.isRunningData()) {
            nodeUsers = ServDao.finds(ServMgr.SY_WFE_NODE_USERS, sqlBean);
        } else {
            nodeUsers = ServDao.finds(ServMgr.SY_WFE_NODE_USERS_HIS, sqlBean);
        }
        return nodeUsers;
    }
    
    /**
     * 
     * @param userCode 指定用户ID
     * @return 指定办理用户Bean
     */
    public Bean getNodeUser(String userCode) {
    	List<Bean> list = getNodeUserBeanList();
    	for(Bean bean: list) {
    		String toUserId = bean.getStr("TO_USER_ID");
    		if(toUserId.equals(userCode)) {
    			return bean;
    		}
    	}
    	
    	return null;
    }
    
    /**
     * 独占 ，1，将TO_USER_ID 填到节点实例中去,2，修改流程的状态信息， 3，删除其他人的待办信息，
     * @param userBean 办理人
     */
    public void duzhan(UserBean userBean) {
        //填TO_USER_ID
        Bean updateBean = new Bean();
        updateBean.setId(this.getId());
        updateBean.set("TO_USER_ID", userBean.getCode());
        updateBean.set("TO_USER_NAME", userBean.getName());

        // 更新节点实例
        WfNodeInstDao.updateWfNodeInst(updateBean, this.isRunningData());
        this.nodeInstBean.copyFrom(updateBean);
        
        WfParam wfParam = new WfParam();
        this.updateServWfInfo(wfParam);
        
        // 删除其他人的待办
//        SqlBean sqlBean = new SqlBean();
//        sqlBean.and("TODO_OBJECT_ID2", this.getId());
//        sqlBean.andNotIn("OWNER_CODE", userBean.getCode());
//        
//        TodoUtils.destroys(sqlBean);
        
        Bean delBean = new Bean();
        
        String strWhere = " and TODO_OBJECT_ID2 = '" + this.getId() 
                + "' and OWNER_CODE != '" + userBean.getCode() + "'";
        delBean.set(Constant.PARAM_WHERE, strWhere);
        
        ServDao.destroys(ServMgr.SY_COMM_TODO, delBean);
    }
    
    
    /**
     * 流程节点恢复
     * 前提：只有未办结的流程才可以调用；同时，该节点为最新运行的节点
     * 1、更新节点实例表   2、将任务从已办转移到待办
     */
    public void resume() {
        //恢复任务节点
//        nodeInstBean = WfNodeInstDao.findNodeInstById(this.getId());
        Bean updateBean = new Bean();
        updateBean.setId(this.getId());
        updateBean.set("NODE_IF_RUNNING", WfeConstant.NODE_IS_RUNNING);
        updateBean.set("NODE_ETIME", null);
        updateBean.set("NODE_DAYS", null);
        
        updateBean.set("NODE_USER_ID", null);
        updateBean.set("NODE_USER_NAME", null);
        updateBean.set("DONE_DEPT_IDS", null);
        updateBean.set("DONE_DEPT_NAMES", null);
        
        updateBean.set("SUB_USER_ID", null);
        updateBean.set("SUB_USER_NAME", null);
        updateBean.set("SUB_DEPT_IDS", null);
        updateBean.set("SUB_DEPT_NAMES", null);
        
        updateBean.set("DONE_TYPE", null);
        updateBean.set("DONE_DESC", null);
        
        updateBean.set("OPEN_TIME", null);
        
        updateBean.set("REMIND_LOG", null);
        updateBean.set("DELAY_TIME", null);
        
        WfNodeInstDao.updateWfNodeInst(updateBean, this.isRunningData());
        
        // 合并修改后的数据到当前节点
        nodeInstBean.copyFrom(updateBean);
        
        //恢复 待办
        SqlBean todoSql = new SqlBean();
        todoSql.and("TODO_OBJECT_ID2", this.getId())
               .and("OWNER_CODE", this.getNodeInstBean().getStr("TO_USER_ID"));
        Bean todoBean = ServDao.find(ServMgr.SY_COMM_TODO_HIS, todoSql);
        TodoUtils.insert(new TodoBean(todoBean));
        ServDao.destroy(ServMgr.SY_COMM_TODO_HIS, todoBean);
    }
    
    /**
     * 
     * @param paramBean WfParam 送交的参数
     */
    private void setWfParam(WfParam paramBean) {
    	this.wfParam = paramBean;
    }
    
    /**
     * 
     * @return 送交的参数Bean
     */
    public WfParam getWfParam() {
    	return this.wfParam;
    }
    
    /**
     * toNext送交的参数Bean
     */
    private WfParam wfParam;
    
    /**
     * 取得指定用户在当前节点的有效办理人UserBean（用户属于多部门情况）
     * @param userCode 用户编码
     * @return 带有正确部门的UserBean
     */
	public Bean getAvailableDoUser(String userCode) {
		List<Bean> list = this.getNodeUserBeanList();
		for (Bean bean : list) {
			final String toUserId = bean.getStr("TO_USER_ID");
			if (toUserId.equals(userCode)) {
				return bean;
			} else if(bean.isNotEmpty("AUTH_USER")) {
            	final String authUser = bean.getStr("AUTH_USER");
            	if(authUser.startsWith(userCode + "^")) {
            		return bean;
            	}
            }
		}

		return null;
	}
    
}
