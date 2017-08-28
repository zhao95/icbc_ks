package com.rh.core.wfe.serv;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.BeanUtils;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.base.db.Transaction;
import com.rh.core.comm.CacheMgr;
import com.rh.core.comm.mind.MindUtils;
import com.rh.core.org.UserBean;
import com.rh.core.org.util.OrgConstant;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.WfOutBean;
import com.rh.core.serv.bean.WfParamBean;
import com.rh.core.serv.relate.RelatedServCreator;
import com.rh.core.serv.relate.RelatedServCreatorSetting;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;
import com.rh.core.util.JsonUtils;
import com.rh.core.util.Lang;
import com.rh.core.util.msg.MsgCenter;
import com.rh.core.wfe.WfAct;
import com.rh.core.wfe.WfContext;
import com.rh.core.wfe.WfParam;
import com.rh.core.wfe.WfProcess;
import com.rh.core.wfe.WfProcessFactory;
import com.rh.core.wfe.attention.AttentionMsg;
import com.rh.core.wfe.db.WfProcInstDao;
import com.rh.core.wfe.db.WfProcInstHisDao;
import com.rh.core.wfe.resource.GroupBean;
import com.rh.core.wfe.resource.WfActor;
import com.rh.core.wfe.resource.WfeBinder;
import com.rh.core.wfe.util.WfUtils;
import com.rh.core.wfe.util.WfeConstant;

/**
 * 工作流服务
 * @author yangjy
 * 
 */
public class ProcServ extends CommonServ {
    private static Log log = LogFactory.getLog(ProcDefServ.class);
      
    /**
     * 将工作流的信息加到返回前台的Bean数据中
     * @param servId 服务ID
     * @param outBean 返回前台Bean , 一般是指具体的业务数据Bean
     * @param paramBean 参数Bean
     */
    public void addWfInfoToOut(String servId, Bean outBean, ParamBean paramBean) {
        // 如果有忽略流程参数，则不加载流程信息。
        if (paramBean.isNotEmpty(Constant.IGNORE_WF_INFO)) {
            outBean.set(Constant.IGNORE_WF_INFO, "true");
            return;
        }
    
        // 先判断outBean是挂载流程信息了
        if (outBean.isEmpty("S_WF_INST")) {
            return;
        }
        
        String pid = outBean.getStr("S_WF_INST");
        String procRunning = outBean.getStr("S_WF_STATE");

        boolean procInstIsRunning = true;
        if (outBean.getInt("S_FLAG") == Constant.NO_INT) { // 如果数据被删除，则流程都被移動到歷史表。
            procInstIsRunning = false;
            procRunning = Constant.NO;
        } else {
            procInstIsRunning = this.procIsRunning(procRunning);
        }

        outBean.set("INST_IF_RUNNING", procRunning);
        outBean.set("PI_ID", pid);

        UserBean currentUser = Context.getUserBean();
        if (!Context.isCurrentUser(currentUser)) {
            outBean.set(OrgConstant.AGENT_USER, currentUser.getCode());
            outBean.set(OrgConstant.AGENT_USER_BEAN, currentUser);
        }
        
        
        WfAct wfAct = null;
        if (!paramBean.isEmpty("NI_ID")) { // 从参数中传过来了节点实例ID
            String nid = paramBean.getStr("NI_ID");
            wfAct = new WfAct(nid, procInstIsRunning);
        } else { // 查TO_USER_ID 得到最后的那个节点实例
            wfAct = this.getUserLastTodoWfAct(currentUser, pid, procInstIsRunning);
        }
        
        WfContext wfContext = WfContext.getContext();
        wfContext.setCurrentWfAct(wfAct);
//        wfContext.setDoUser(currentUser);

        WfProcess process;

        if (wfAct != null) {
            process = wfAct.getProcess();
        } else {
            process = this.getWfProcess(pid, procInstIsRunning);
        }

        process.setServInstBean(outBean);
        if (wfAct != null) {
            outBean.set("nodeInstBean", wfAct.getNodeInstBean());
            String huanJie = wfAct.getNodeDef().getHuanJie();
            outBean.getBean("nodeInstBean").set("HUANJIE", huanJie);
        }

        // 设置提醒的标题，在分发到时候，有用到
        String bindTitle = process.getProcInstTitle();
        outBean.set("bindTitle", bindTitle);

        // 根据办理人员的类型，组织相关数据
        WfOut wfOutBean = null;

        if (paramBean.isNotEmpty("SEND_ID")) { // 优先处理分发，因为分发过去的不需要有其他的按钮
            // 分发接收用户，或其他有权限浏览文件的用户
            outBean.set("SEND_ID", paramBean.getStr("SEND_ID"));
            wfOutBean = new SendOutBean(process, outBean, paramBean);
        } else {
            // 流程当前办理人
            if (null != wfAct && wfAct.isUserDoing(currentUser.getCode())) {
                wfOutBean = new DoingOutBean(process, outBean, paramBean);
            } else if (wfAct != null) {
                // 流经
                wfOutBean = new FlowOutBean(process, outBean, paramBean);

                if (process.isProcManage()) {
                    wfOutBean.fillOutBean(wfAct);
                    // 流程管理员
                    wfOutBean = new AdminOutBean(process, outBean, paramBean);
                }
            } else if (process.isProcManage()) {
                // 流程管理员
                wfOutBean = new AdminOutBean(process, outBean, paramBean);
            } else {
                // 分发接收用户，或其他有权限浏览文件的用户
                wfOutBean = new BaseOutBean(process, outBean, paramBean);
            }
        }
        wfOutBean.setDoUser(currentUser);
        wfOutBean.fillOutBean(wfAct);
        wfOutBean.filter(wfAct);
    }

    /**
     * @param paramBean 参数bean
     * @return 下一步的节点列表
     */
    public OutBean getNextSteps(ParamBean paramBean) {
        String procRunning = paramBean.getStr("S_WF_STATE");

        boolean procInstIsRunning = this.procIsRunning(procRunning);

        WfAct wfAct = null;
        if (!paramBean.isEmpty("NI_ID")) { // 从参数中传过来了节点实例ID
            String nid = paramBean.getStr("NI_ID");
            wfAct = new WfAct(nid, procInstIsRunning);
        } else {
            OutBean rtnBean = new OutBean();
            rtnBean.set("rtnStr", "不是当前节点,没有下一步的按钮");

            return rtnBean;
        }
        
        UserBean doUser = WfUtils.getDoUserBean(paramBean);
        List<Bean> nextSteps = wfAct.getNextAvailableSteps(doUser);

        OutBean rtnBean = new OutBean();
        rtnBean.set("rtnStr", "success");
        rtnBean.set("nextSteps", nextSteps);

        return rtnBean;
    }

    /**
     * @param paramBean 参数bean
     * @return 返回前台Bean
     */
    public OutBean cmLockFile(ParamBean paramBean) {
        UserBean userBean = WfUtils.getDoUserBean(paramBean);

        Bean procBean = new Bean();
        procBean.setId(paramBean.getStr("PI_ID"));
        procBean.set("INST_LOCK", WfProcess.PROC_INST_LOCK);
        procBean.set("INST_LOCK_USER", userBean.getCode());
        procBean.set("INST_LOCK_TIME", DateUtils.getDatetime());

        WfProcInstDao.updateWfProcInst(procBean);

        OutBean rtnBean = new OutBean();
        rtnBean.set("rtnstr", "success");

        return rtnBean;
    }

    /**
     * @param paramBean 参数bean
     * @return 返回前台Bean
     */
    public OutBean cmUnLockFile(ParamBean paramBean) {
        Bean procBean = new Bean();
        procBean.setId(paramBean.getStr("PI_ID"));
        procBean.set("INST_LOCK", WfProcess.PROC_INST_LOCK_NO); // 不锁定
        procBean.set("INST_LOCK_USER", "");
        procBean.set("INST_LOCK_TIME", "");

        WfProcInstDao.updateWfProcInst(procBean);

        OutBean rtnBean = new OutBean();
        rtnBean.set("rtnstr", "success");

        return rtnBean;
    }

    /**
     * 如果是发部门/角色， 点进去之后， 将TO_USER_ID 设置成这个人， 其他人进页面的时候，判断TO_USER_ID如果有了，则不出独占的按钮了
     * @param paramBean 参数Bean
     * @return 返回前台结果
     */
    public OutBean duZhan(ParamBean paramBean) {
      String procRunning = paramBean.getStr("INST_IF_RUNNING");
      boolean procIsRunning = this.procIsRunning(procRunning);
      final String niId = paramBean.getStr("NI_ID");
      
      WfUtils.duzhan(niId, WfUtils.getDoUserBean(paramBean), procIsRunning);

      OutBean rtnBean = new OutBean();
      rtnBean.set("rtnstr", "success");

      return rtnBean;
    }
    
    /**
     * 结束自由节点。如果是自由节点办结时，只办结本节点。
     * @param paramBean 参数Bean
     * @param wfAct 流程实例对象
     */
    private void finishFreeNode(ParamBean paramBean, WfAct wfAct) {
        WfParam wfParam = WfParam.createWfParamAndFillDoUser(paramBean);
        wfAct.finish(wfParam);
    }


    /**
     * 办结
     * @param paramBean 参数信息
     * @return 返回前台参数
     */
    public OutBean finish(ParamBean paramBean) {
        UserBean doneUser = WfUtils.getDoUserBean(paramBean);
        WfProcess wfProcess = null;
        if (paramBean.isNotEmpty(WfeConstant.CURR_WF_ACT)) { // 从“处理完毕”页面转发来的请求。
            WfAct wfAct = (WfAct) paramBean.get(WfeConstant.CURR_WF_ACT);
            if (wfAct.getNodeDef().isFreeNode()) {
                finishFreeNode(paramBean, wfAct);
                return new OutBean().setOk();
            }
            WfContext.getContext().setCurrentWfAct(wfAct);
            wfProcess = wfAct.getProcess();
        } else if (paramBean.isNotEmpty("NI_ID")) {
            boolean isRunningData = procIsRunning(paramBean.getStr("INST_IF_RUNNING"));
            String niId = paramBean.getStr("NI_ID");
            WfAct wfAct = new WfAct(niId, isRunningData);
            if (wfAct.isUserDoing(doneUser.getId()) 
                    && wfAct.getNodeDef().isFreeNode()) { 
                // 如果当前用户正在办理中，且是自由节点，则只办结自由节点
                finishFreeNode(paramBean, wfAct);
                return new OutBean().setOk();
            }
            WfContext.getContext().setCurrentWfAct(wfAct);
            wfProcess = wfAct.getProcess();
        } else {
            String pid = paramBean.getStr("PI_ID");
            Bean aProcInstBean = WfProcInstDao.findProcInstById(pid);
            wfProcess = new WfProcess(aProcInstBean);
        }
        
        WfParam wfParam = WfParam.createWfParamAndFillDoUser(paramBean);

        String servId = wfProcess.getServId();
        
        paramBean.set("wfProcess", wfProcess);
        ServMgr.act(servId, "beforeFinish", paramBean);
        
        wfProcess.finish(wfParam);

        // 启用部门内未启用的意见，常用于审批单不出部门就办结的情况。
        MindUtils.enableMindInDept(wfProcess.getDocId(), doneUser.getTDeptCode());

        ServMgr.act(servId, "afterFinish", paramBean);
        
        OutBean rtnBean = new OutBean();
        rtnBean.set("rtnstr", "success");

        return rtnBean;
    }
    
    /**
     * 在 未办结的时候，取TO_USER_ID 的实例
     * @param doUser 办理用户
     * @param pid 节点实例ID
     * @param isRunningData 流程数据是否保存在运行表中
     * @return User 最后办理的节点实例。可能有以下几种情况：1，现在正在办理中的；2，我已经办理完成的、最后一次办理的实例（用于处理收回的情况）；
     */
    private WfAct getUserLastTodoWfAct(UserBean doUser, String pid, boolean isRunningData) {
        WfProcess wfp = this.getWfProcess(pid, isRunningData);
        WfAct wfAct = wfp.getUserDoingWfAct(doUser);
        if (wfAct == null) {
            wfAct = wfp.getUserLastToDoWfAct(doUser);
        }
        return wfAct;
    }

    /**
     * 
     * @param piId 流程实例ID
     * @param isRunningData 流程数据是否保存在运行表中
     * @return 流程实例
     */
    private WfProcess getWfProcess(String piId, boolean isRunningData) {
        WfProcess process = new WfProcess(piId, isRunningData);

        return process;
    }

    /**
     * 流程跟踪列表
     * @param paramBean 参数对象
     * @return 流程跟踪列表
     */
    public OutBean getWfTracking(ParamBean paramBean) {
        String pid = paramBean.getStr("PI_ID");
        String procRunning = paramBean.getStr("INST_IF_RUNNING");

        WfProcess wfProcess = new WfProcess(pid, procIsRunning(procRunning));

        List<Bean> trackingList = wfProcess.wfTracking();
        log.debug("流程跟踪 trackingList size " + trackingList.size());

        OutBean rtnBean = new OutBean();
        rtnBean.set("rtnstr", BeanUtils.toLinkedMap(trackingList, "NI_ID"));

        return rtnBean;
    }

    /**
     * @param procRunning 从页面传来的 WF_INST_ID 字符串类型的 流程是否运行
     * @return 流程是否运行
     */
    private boolean procIsRunning(String procRunning) {
        boolean procIsRunning = true;

        if (procRunning.equals(String.valueOf(WfeConstant.PROC_NOT_RUNNING))) {
            procIsRunning = false;
        }

        return procIsRunning;
    }

    /**
     * 启动流程服务
     * @param paramBean 参数信息，包含要启动的流程定义和数据信息 及起草节点处理人信息 TO_USERS(GroupBean类型),为空时，取当前用户
     * @return outBean 包含流程实例和节点实例
     */
    public OutBean start(ParamBean paramBean) {
        WfParamBean wfParam = (WfParamBean) paramBean;
        GroupBean users = null;
        if (paramBean.contains("TO_USERS")) {
            users = (GroupBean) paramBean.get("TO_USERS");
        }

        Bean dataBean = wfParam.getDataBean();
		if (StringUtils.isBlank(dataBean.getId())) {
			throw new TipException("无效的_WF_DATA_BEAN_，_PK_不能为null。");
		}
		
		//取得完成的数据Bean
		Bean bean = ServDao.find(wfParam.getDataServId(), dataBean.getId());
		dataBean.copyFrom(bean);
		
        beforeStart(paramBean, dataBean);

        WfAct wfAct = WfProcessFactory.startProcess(wfParam.getDataServId(), dataBean, users);
        WfOutBean outBean = new WfOutBean();
        if (null != wfAct) {
            outBean.setWfProcInst(wfAct.getProcess().getProcInstBean()).setWfActInst(wfAct.getNodeInstBean());
            outBean.setOk();
        }

        afterStart(paramBean, outBean);

        return outBean;
    }

    /**
     * 
     * @param paramBean 前台参数Bean
     * @return 合并后的新的节点实例
     */
    public OutBean stopParallelWf(ParamBean paramBean) {
        String pid = paramBean.getStr("PI_ID");
        WfAct wfAct = null;

        if (!paramBean.isEmpty("NI_ID")) {
            String nid = paramBean.getStr("NI_ID");
            wfAct = new WfAct(nid, true);
        } else {
            wfAct = this.getUserLastTodoWfAct(WfUtils.getDoUserBean(paramBean), pid, true);
        }
        
        UserBean doUserBean = WfUtils.getDoUserBean(paramBean);

        WfParam wfParam = new WfParam();
        wfParam.setDoneUser(doUserBean);
        wfParam.setDoneType(WfeConstant.NODE_DONE_TYPE_STOP);
        wfParam.setDoneDesc(WfeConstant.NODE_DONE_TYPE_STOP_DESC);
        wfParam.set(WfeConstant.FINISH_WF_FORCE_FLAG, 
        		paramBean.getBoolean(WfeConstant.FINISH_WF_FORCE_FLAG));

        WfAct newWfAct = wfAct.stopParallel(wfParam);

        OutBean rtnBean = new OutBean();
        rtnBean.set("rtnstr", "success" + newWfAct.getId());

        return rtnBean;      
    }

    /**
     * 当活动节点大于1， 终止其中一个活动的节点 , 不创建新的节点实例
     * 
     * @param paramBean 从前台传来参数
     * @return 终止流程中节点
     */
    public OutBean stopWfNode(ParamBean paramBean) {
        String nid = paramBean.getStr("NI_ID");

        WfAct wfAct = new WfAct(nid, true);

        if (!wfAct.isRunning()) {
            throw new TipException("节点已经办结状态，不允许此操作");
        }

        WfParam wfParam = WfParam.createWfParamAndFillDoUser(paramBean);
        wfParam.setDoneType(WfeConstant.NODE_DONE_TYPE_STOP);
        wfParam.setDoneDesc(WfeConstant.NODE_DONE_TYPE_STOP_DESC);


        if (wfAct.getNodeDef().getInt("NODE_IF_AUTOEND") == WfeConstant.NODE_AUTO_END_NO
                && wfAct.getNodeInstBean().getStr("TO_USER_ID")
                        .equals(wfParam.getDoneUser().getCode())) {
            wfAct.stop(wfParam);
        } else if (wfAct.getProcess().isProcManage()) { // 流程管理员 TODO
            wfAct.stop(wfParam);
        } else {
            throw new TipException("没有权限终止此节点");
        }

        OutBean rtnBean = new OutBean();
        rtnBean.set("rtnstr", "success");

        return rtnBean;      
    }

    /**
     * 送交下一节点
     * @param paramBean 参数
     * @return 节点实例ID
     */
    public OutBean toNext(ParamBean paramBean) {
        removePrefixOfSendUsers(paramBean);  //删除送交的时候用户和部门的前缀
        
        boolean isRunningData = procIsRunning(paramBean.getStr("INST_IF_RUNNING"));
        final String piId = paramBean.getStr("PI_ID");
        
        // 目标节点对应的参数
        List<WfParam> paramList = new ArrayList<WfParam>();
        if (paramBean.isNotEmpty(WfeConstant.MULT_NODE)) { // 如果同时送多个节点则
            List<Bean> list = paramBean.getList(WfeConstant.MULT_NODE);
            for (Bean bean : list) {
                WfParam wfParam = WfParam.createWfParamAndFillDoUser(paramBean);
                fillWfParam(wfParam, bean);
                paramList.add(wfParam);
            }
        } else {
            WfParam wfParam = WfParam.createWfParamAndFillDoUser(paramBean);
            fillWfParam(wfParam, paramBean);
            paramList.add(wfParam);
        }
        
        WfProcess process = null;
        WfAct wfAct = null;
        
        if (paramBean.isNotEmpty(WfeConstant.CURR_WF_ACT)) { // 处理完毕请求已经准备好了WfAct，则不用重新构造
            wfAct = (WfAct) paramBean.get(WfeConstant.CURR_WF_ACT);
            process = wfAct.getProcess();
        } else {
            process = new WfProcess(piId, isRunningData);
            final String niId = paramBean.getStr("NI_ID");
            wfAct = process.getWfAct(niId);
        }
        
        List<GroupBean> allToUsers = new ArrayList<GroupBean>();
        // 为WfParam设置TO_USERS 参数
        for (WfParam param : paramList) {
            List<GroupBean> toUsers = wfAct.createNextActors(param.getStr("NODE_CODE"), param);
            param.set("TO_USERS", toUsers);
            allToUsers.addAll(toUsers);
        }
        
        OutBean out = new OutBean();
        if (wfAct == null) {
            out.setError("无效的参数NI_ID");
            return out;
        }
        
        List<WfAct> nextWfActs = process.toNext(wfAct, paramList);   // 送指定节点
        StringBuilder nextNiIds = new StringBuilder();
        for (WfAct nextWfAct: nextWfActs) {
            nextNiIds.append(nextWfAct.getId()).append(",");
        }
        if (nextNiIds.length() > 0) {
            nextNiIds.setLength(nextNiIds.length() - 1);
        }
        
        // 发送关注消息
        final String title = process.getProcInstTitle();
        for (WfParam param : paramList) { // 发送关注信息
            final String nodeCode = param.getStr("NODE_CODE");
            addMsg(piId, allToUsers, nodeCode, title);
        }
        
        out.setOk().set(WfeConstant.NEXT_NI_ID, nextNiIds.toString());
        // 如果节点不是自动办结，则
        if (wfAct.getNodeDef().getInt("NODE_IF_AUTOEND") == Constant.NO_INT) {
            out.set("_closeDlg", "false");
        }
        
        return out; 
    }
    
    /**
     * 往wfParam对象中设置流程参数
     * @param wfParam 流程参数对象
     * @param paramBean 参数Bean
     */
    private void fillWfParam(WfParam wfParam , Bean paramBean) {
        removePrefixOfSendUsers(paramBean);  //删除送交的时候用户和部门的前缀
        
        int typeTo = paramBean.getInt("TO_TYPE");
        wfParam.setTypeTo(typeTo);
        wfParam.setToUser(paramBean.getStr("TO_USERS"));
        wfParam.setToRoleDept(paramBean.getStr("TO_DEPT"));
        wfParam.setToRole(paramBean.getStr("TO_ROLE"));
        if (typeTo != WfParam.TYPE_TO_USER && typeTo != WfParam.TYPE_TO_DEPT_ROLE) {
            throw new TipException("需要设置送交类型");
        } else if (typeTo == WfParam.TYPE_TO_USER) {
            if (paramBean.getStr("TO_USERS").isEmpty()) {
                throw new TipException("需要设置送交用户");
            }
        } else if (typeTo == WfParam.TYPE_TO_DEPT_ROLE) {
            if (paramBean.getStr("TO_DEPT").isEmpty() || paramBean.getStr("TO_ROLE").isEmpty()) {
                throw new TipException("需要设置送交部门、角色");
            }
        }
        if (paramBean.isEmpty("NODE_CODE")) {
            throw new TipException("无效的参数NODE_CODE");
        }
        wfParam.set("NODE_CODE", paramBean.getStr("NODE_CODE"));
    }
    
    /**
     * 删除送交时候，用户和部门的前缀
     * @param paramBean 参数Bean
     */
    private void removePrefixOfSendUsers(Bean paramBean) {
        if (paramBean.contains("TO_USERS")) {
            String toUsers = paramBean.getStr("TO_USERS").replaceAll(WfeBinder.USER_NODE_PREFIX + ":", "");
            
            paramBean.set("TO_USERS", toUsers);
        }
        
        if (paramBean.contains("TO_DEPT")) {
            String toDepts = paramBean.getStr("TO_DEPT").replaceAll(WfeBinder.DEPT_NODE_PREFIX + ":", "");
            
            paramBean.set("TO_DEPT", toDepts);
        }
    }
    /**
     * 启动流程之后的拦截方法，由子类重载
     * @param paramBean 参数信息
     * @param outBean 输出信息
     */
    protected void afterStart(ParamBean paramBean, OutBean outBean) {
        WfParamBean wfParam = (WfParamBean) paramBean;

        ServDefBean servDef = ServUtils.getServDef(wfParam.getDataServId());
        String servSrcId = servDef.getSrcId();

        Bean whereBean = new Bean();
        whereBean.set("SERV_ID", servSrcId);
        whereBean.set("DATA_ID", wfParam.getDataBean().getId());

        Bean wfActBean = outBean.getBean("_WF_ACT_INST_");
        Bean setBean = new Bean().set("WF_NI_ID", wfActBean.getId());

        ServDao.updates(ServMgr.SY_COMM_FILE, setBean, whereBean);
    }

    /**
     * 取消办结
     * @param paramBean 参数信息
     * @return 返回前台参数
     */
    public OutBean undoFinish(ParamBean paramBean) {
        String pid = paramBean.getStr("PI_ID");

        // 取消办结，流程就是不在运行状态
        WfProcess wfProc = this.getWfProcess(pid, false);

        if (wfProc.canUndoFinish()) {
            wfProc.undoFinish();
        } else {
            throw new TipException("没有权限取消办结");
        }

        OutBean rtnBean = new OutBean();
        rtnBean.set("rtnstr", "success");

        return rtnBean;
    }

    /**
     * 
     * 
     * @param paramBean 前台传来
     * @return 返回前台信息
     */
    public OutBean withdraw(ParamBean paramBean) {
      if (paramBean.isEmpty("NI_ID")) {
          throw new TipException("参数不能为空：NI_ID");
      }
      String nid = paramBean.getStr("NI_ID");
      
      boolean isRunningData = this.procIsRunning(paramBean.getStr("INST_IF_RUNNING"));
      if (!isRunningData) { // 如果是已经办结的数据，那么只有自由节点才能执行收回操作
          if (!nid.startsWith(WfeConstant.FREE_NODE_ID_PREFIX)) {
              throw new TipException("没有此操作权限。");
          }
      }

      WfAct wfAct = new WfAct(nid, isRunningData);
      WfParam wfParam = new WfParam();
      wfParam.setDoneType(WfeConstant.NODE_DONE_TYPE_WITHDRAW); // 设置办理类型是收回
      wfParam.setDoneDesc(WfeConstant.NODE_DONE_TYPE_WITHDRAW_DESC);
      wfParam.setTypeTo(WfeConstant.NODE_TO_USER);

      UserBean currentUser = Context.getUserBean();
      
      Bean servInstBean = wfAct.getProcess().getServInstBean();
      //若用户为拟稿用户，且部门信息不是表单中的虚拟信息，则取得当前用户为角色表虚拟出的挂职机构，否则会导致“取回”跟踪信息和处理信息不一致
      if (null!=servInstBean && currentUser.getCode().equals(servInstBean.getStr("S_USER")) 
    		  && !currentUser.getDeptCode().equals(servInstBean.getStr("S_DEPT"))) {
		currentUser = (UserBean) CacheMgr.getInstance().get(currentUser.getCode()+"^" + servInstBean.getStr("S_DEPT"), "SY_ORG_USER");
		if (null == currentUser) {
			currentUser = Context.getUserBean();
		}
	  }
      
      String lastDoUserId = wfAct.getNodeInstBean().getStr("DONE_USER_ID");
      String lastDoUserDeptId = wfAct.getNodeInstBean().getStr("DONE_DEPT_IDS");
      wfParam.setToUser(lastDoUserId + "^" + lastDoUserDeptId);
      wfParam.setDoneUser(currentUser);
      
      //当前用户和办理用户不一致，则设置成代理
//      if (!currentUser.getCode().equals(wfParam.getDoneUser().getCode())) {
//          wfParam.setIsAgent(true);
//      }
      
      log.debug("收回，当前ni_id = " + wfAct.getId());
      
      String strNextWfActIds = paramBean.getStr("nextNiIds");
      String[] wfActIds =  strNextWfActIds.split(",");
      
      WfAct newWfAct = wfAct.withdraw(wfParam, wfActIds);

      // 意见收回
//      MindUtils.withDrawMind(wfAct.getId(), newWfAct.getId());

      OutBean rtnBean = new OutBean();
      rtnBean.set("rtnstr", newWfAct.getId());

      return rtnBean;
    }

    /**
     * 删除流程实例服务，需要提供流程实例ID和流程状态参数，不删除流程对应的数据信息 TODO 增加验证参数的机制
     * @param param 流程参数
     * @return 执行结果
     */
    public OutBean delete(ParamBean param) {
        WfParamBean wfParam = (WfParamBean) param;
        WfOutBean out = new WfOutBean();
        
        boolean isRunningData = wfParam.getProcSateRunning();
        //是否是回收站数据
        if (wfParam.getInt("S_FLAG") == Constant.NO_INT) {
            isRunningData = false; //运行数据为null
        }
        
        boolean falseDel = true;
        if (wfParam.isNotEmpty("falseDel")) {
            falseDel = wfParam.getBoolean("falseDel");
        }
        
        WfProcess wfProcess = new WfProcess(wfParam.getProcInstCode(), isRunningData);
        if (wfProcess != null) {
        	UserBean userBean = Context.getUserBean();
            if (wfProcess.canDelete(userBean) 
                    || wfParam.getBoolean(WfeConstant.DEL_WF_IGNORE_RIGHT)) { // 有删除权限
                if (falseDel) { // 假删除
                    wfProcess.delete();
                } else {
                    wfProcess.destory();
                }
            } else {
                throw new RuntimeException("没有删除权限，不能执行删除操作");
            }
        }
        return out;
    }

    /**
     * 删除流程，同时删除流程对应的数据信息
     * @param paramBean 参数Bean
     * @return 删除状态
     */
    public OutBean deleteDoc(ParamBean paramBean) {
        Bean proInstBean = new Bean();
        if (paramBean.getInt("INST_IF_RUNNING") == 1) { // 运行
            proInstBean = WfProcInstDao.findProcInstById(paramBean.getStr("PI_ID"));
        } else {
            proInstBean = WfProcInstHisDao.findProcInstById(paramBean.getStr("PI_ID"));
        }

        ParamBean servBean = new ParamBean(proInstBean.getStr("SERV_ID"), ServMgr.ACT_DELETE);
        servBean.set(Constant.PARAM_SERV_ID, proInstBean.getStr("SERV_ID"));
        servBean.setId(proInstBean.getStr("DOC_ID"));

        OutBean rtnBean = new OutBean();
        rtnBean.set("rtnstr", "success");

        OutBean actBack = ServMgr.act(servBean);
        if (!actBack.isOk()) {
            rtnBean.set("rtnstr", actBack.getMsg());
        }

        return rtnBean;
    }
    
    /**
     * 如果处理完毕页面设置了，获取用户通过Act取得的用户
     * @param paramBean 参数Bean
     * @return 处理结果
     */
    private OutBean getNextStepUsersForAct(ParamBean paramBean) {
        String actCode = paramBean.getStr("NODE_CODE");
        final String niId = paramBean.getStr("NI_ID");
        final String servId = paramBean.getStr("SERV_ID");
        final String dataId = paramBean.getStr("DATA_ID");
        
        ParamBean actParam = new ParamBean();
        
        if (actCode.startsWith(WfeConstant.PREFIX_SERV_ACT)) { // 本服务的操作
            actParam.setServId(servId);
            actCode = actCode.substring(WfeConstant.PREFIX_SERV_ACT.length());
        } else if (actCode.startsWith(WfeConstant.PREFIX_PROC_ACT)) { // 流程操作
//            actParam.setServId(ServMgr.SY_WFE_PROC_DEF);
//            actCode = actCode.substring(WfeConstant.PREFIX_PROC_ACT.length());
            return new OutBean().setOk("ERROR,getNextStepUsersForAct: ");
        }
        
        actParam.set("NI_ID", niId);
        actParam.set("INST_IF_RUNNING", paramBean.getStr("INST_IF_RUNNING"));
        actParam.setId(dataId);
        actParam.setAct(actCode + "_doUsers");
        
        OutBean outBean = new OutBean();
        try {
            outBean = ServMgr.act(actParam);    
        } catch (Exception e) {
            outBean.setOk("ERROR,getNextStepUsersForAct: " + e);
        }
        if (outBean.getMsg().startsWith("ERROR,")) {
            outBean.setOk("ERROR,getNextStepUsersForAct: " + outBean.getMsg());
        }
        
        return outBean;
    }

    /**
     * @param paramBean 参数Bean
     * @return 返回页面的树结构 串
     */
    public OutBean getNextStepUsersForSelect(ParamBean paramBean) {
        String nextNodeCode = paramBean.getStr("NODE_CODE");
        if (nextNodeCode.startsWith(WfeConstant.PREFIX_SERV_ACT) 
                || nextNodeCode.startsWith(WfeConstant.PREFIX_PROC_ACT)) {
            return getNextStepUsersForAct(paramBean);
        }
        
        WfAct currWfAct = WfUtils.createWfAct(paramBean);
        
        WfeBinder wfBinder = currWfAct.getWfBinder(paramBean);
        //优先取按组过滤任务处理人
        if (wfBinder.getGroupBeanList().size() > 0) { // 如果指定了接收人，那么直接送接收人。
            WfProcess process = currWfAct.getProcess();
            WfParam param = new WfParam();
            param.set("NI_ID", paramBean.getStr("NI_ID"));
            param.set("NODE_CODE", paramBean.getStr("NODE_CODE"));
            param.set("TO_USERS", wfBinder.getGroupBeanList());
            param.setDoneUser(WfUtils.getDoUserBean(paramBean));
            
            List<WfParam> paramList = new ArrayList<WfParam>();
            paramList.add(param);
            
            process.toNext(currWfAct, paramList);
            
            //设置返回值中的送交人
            StringBuffer buffer = new StringBuffer();
            OutBean outBean = new OutBean();
            for (GroupBean groupBean : wfBinder.getGroupBeanList()) {
                buffer.append(groupBean.getUserNames()).append(",");
            }
            if (buffer.length() > 0) {
                buffer.setLength(buffer.length() - 1);
                outBean.set("TO_USERS", buffer);
            }
            
            return outBean.setOk();
            
        } else {
            String rtnTreeStr = wfBinder.getBinders();
            OutBean rtnBean = new OutBean();
            rtnBean.set("treeData", rtnTreeStr); // 树的数据
            rtnBean.set("multiSelect", wfBinder.isMutilSelect()); // 是否多选
            rtnBean.set("binderType", wfBinder.getBinderType()); // 角色还是用户
            rtnBean.set("roleCode", wfBinder.getRoleCode()); // 如果是角色，将角色code 带上
            rtnBean.set("autoSelect", wfBinder.getAutoSelect()); // 是否需要自动选中用户
            rtnBean.set("displayType", wfBinder.getDisplayType()); 

            return rtnBean;
        }
    }   
    
    /**
     * 
     * @param pid 流程实例ID
     * @param list 具体送交人
     * @param nodeCode 下个节点ID
     * @param title 标题
     */
    private void addMsg(String pid, List<GroupBean> list, String nodeCode, String title) {
        StringBuilder userIds = new StringBuilder();

        for (GroupBean groupBean : list) {
            List<WfActor> actors = groupBean.getAllActors();

            for (WfActor actor : actors) {
                userIds.append(actor.getUserId()).append(",");
            }
        }
        if (userIds.length() > 0) {
            userIds.setLength(userIds.length() - 1);
        }

        Bean msgBean = new Bean();
        msgBean.set("PI_ID", pid);
        msgBean.set("TO_USERS", userIds.toString());
        msgBean.set("NEXT_NODE", nodeCode);
        msgBean.set("TITLE", title);

        AttentionMsg attentionMsg = new AttentionMsg(msgBean);
        MsgCenter.getInstance().addMsg(attentionMsg);
    }

    /**
     * 起草相关审批单
     * @param paramBean 客户端传递的参数
     * @return 如果成功则返回新服务的实例数据，否则返回错误消息。
     */
    public OutBean createRelatedServ(ParamBean paramBean) {
        final String oldServId = paramBean.getStr("oldServId");
        final String oldDataId = paramBean.getStr("oldDataId");
        final String newServId = paramBean.getStr("newServId");
        
        OutBean outBean = null;
        RelatedServCreatorSetting setting = RelatedServCreatorSetting.getSetting(oldServId, newServId);
        if (setting == null) {
            outBean = new OutBean();
            outBean.setError("无效的服务设置");
            return outBean;
        }
        
        RelatedServCreator creator = null;
        if (StringUtils.isNotEmpty(setting.getExtendCls())) {
            // 如果存在扩展类，则使用扩展类处理创建相关文件功能。
            creator = Lang.createObject(RelatedServCreator.class, setting.getExtendCls());
        } else {
            creator = new RelatedServCreator();
        }
        creator.setSetting(setting);
        
        Bean oldBean = ServDao.find(oldServId, oldDataId);
        outBean = creator.create(paramBean, oldBean, newServId);
        
        return outBean;
    }
    
    /**
     * 返回拟稿人方法
     * @param paramBean
     * @return
     */
    public OutBean return2Drafter(ParamBean paramBean) {
        boolean isRunningData = procIsRunning(paramBean.getStr("INST_IF_RUNNING"));
        
        WfProcess process = null;
        WfAct wfAct = null;
        
        if (paramBean.isNotEmpty(WfeConstant.CURR_WF_ACT)) { // 处理完毕请求已经准备好了WfAct，则不用重新构造
            wfAct = (WfAct) paramBean.get(WfeConstant.CURR_WF_ACT);
            process = wfAct.getProcess();
        } else {
        	final String niId = paramBean.getStr("NI_ID");
        	wfAct = new WfAct(niId, isRunningData);
            process = wfAct.getProcess();
        }
        
        // 目标节点对应的参数
        List<WfParam> paramList = new ArrayList<WfParam>();
        WfParam wfParam = WfParam.createWfParamAndFillDoUser(paramBean);
        wfParam.setTypeTo(WfParam.TYPE_TO_USER);
        
        WfAct firstWfAct = process.getFirstWfAct(); // 取第一个办理节点
        // 取拟稿人节点的办理用户
        final String toUser = firstWfAct.getNodeInstBean().getStr("DONE_USER_ID");
        final String toDept = firstWfAct.getNodeInstBean().getStr("DONE_DEPT_IDS");
        wfParam.setToUser(toUser + "^" + toDept);
        // 设置送交节点
        wfParam.set("NODE_CODE", firstWfAct.getCode());
        paramList.add(wfParam);
        
        List<GroupBean> allToUsers = new ArrayList<GroupBean>();
        // 为WfParam设置TO_USERS 参数
        for (WfParam param : paramList) {
            List<GroupBean> toUsers = wfAct.createNextActors(param.getStr("NODE_CODE"), param);
            param.set("TO_USERS", toUsers);
            allToUsers.addAll(toUsers);
        }
        
        OutBean out = new OutBean();
//        if (wfAct == null) {
//            out.setError("无效的参数NI_ID");
//            return out;
//        }
        
        List<WfAct> nextWfActs = process.toNext(wfAct, paramList);   // 送指定节点
        StringBuilder nextNiIds = new StringBuilder();
        for (WfAct nextWfAct: nextWfActs) {
            nextNiIds.append(nextWfAct.getId()).append(",");
        }
        if (nextNiIds.length() > 0) {
            nextNiIds.setLength(nextNiIds.length() - 1);
        }
        
        // 发送关注消息
        final String title = process.getProcInstTitle();
        for (WfParam param : paramList) { // 发送关注信息
            final String nodeCode = param.getStr("NODE_CODE");
            addMsg(process.getId(), allToUsers, nodeCode, title);
        }
        
        out.setOk().set(WfeConstant.NEXT_NI_ID, nextNiIds.toString());
        // 如果节点不是自动办结，则
        if (wfAct.getNodeDef().getInt("NODE_IF_AUTOEND") == Constant.NO_INT) {
            out.set("_closeDlg", "false");
        }
        
        return out; 
    }
    
    /**
     * 获取当前节点可退回的直接关联节点步骤
     * @param paramBean
     * @return
     */
    public OutBean getPreStepNodeDoUser(ParamBean paramBean){
    	List<Object> paramList = new ArrayList<Object>();
    	String piId = paramBean.getStr("PI_ID");
    	String currentNodeCode = paramBean.getStr("NODE_CODE");
    	if (StringUtils.isEmpty(piId) || StringUtils.isEmpty(currentNodeCode)) {
    		log.info("------------search pre node fail  empty pi_id or node_code--------------");
    		return new OutBean().setData(new ArrayList<Bean>()).setOk();
		}
    	
    	paramList.add(piId);
    	paramList.add(currentNodeCode);
    	
/*    	String sqlNodeDef = "select * from  SY_WFE_NODE_DEF where PROC_CODE in (select PROC_CODE from SY_WFE_PROC_INST where PI_ID = ?) and NODE_CODE = ? ";
    	Bean data = Transaction.getExecutor().queryOne(sqlNodeDef,paramList);*/
    	
    	try {
	    	String sql = "select NI_ID,NODE_CODE,NODE_NAME,PROC_CODE,DONE_USER_ID,DONE_USER_NAME,DONE_DEPT_IDS,DONE_DEPT_NAMES from SY_WFE_NODE_INST where 1=1 "
	    			+ " and NODE_CODE in (select T.SRC_NODE_CODE from SY_WFE_LINE_DEF t where T.PROC_CODE in (select PROC_CODE  from  SY_WFE_PROC_INST where PI_ID = ?) and T.TAR_NODE_CODE = ?) "
	    			+ " and PI_ID = ? and NODE_IF_RUNNING = 2 order by S_MTIME desc";
	    	paramList.clear();
	    	paramList.add(piId);
	    	paramList.add(currentNodeCode);
	    	paramList.add(piId);
	    	List<Bean> list = Transaction.getExecutor().query(sql,paramList);
	    	for (Bean bean : list) {
				bean.set("ACT_CSS", bean.getStr("NODE_CODE"));
				bean.set("NODE_USER", bean.getStr("DONE_USER_ID")+"^"+bean.getStr("DONE_DEPT_IDS"));
				bean.set("NODE_USER_NAME",bean.getStr("DONE_USER_NAME"));
				bean.set("NODE_CODE", "R"+bean.getStr("NODE_CODE"));
			}
	    	return new OutBean().setData(list).setOk();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return new OutBean().setData(new ArrayList<Bean>()).setOk();
		}
    }
    
    /**
     * 校验退回按钮是否需要校验
     * @param paramBean
     * @return
     */
    public OutBean isBackBtnNeedCheck (ParamBean paramBean){
    	OutBean outBean = new OutBean();
    	String rtnKey = "IS_VALIDATE";
    	String sql = "select T.WF_BUTTONS,T.BUTTON_PARAMS from SY_WFE_NODE_ACT T "
    			+ "where T.PROC_CODE = ? and T.NODE_CODE = ? and SERV_ID = ? and S_FLAG = 1 and WF_BUTTONS like '%cmBack%'";
    	List<Object> paramList = new ArrayList<Object>();
    	paramList.add(paramBean.get("PROC_CODE"));
    	paramList.add(paramBean.get("NODE_CODE"));
    	paramList.add(paramBean.get("SERV_ID"));
    	List<Bean> result = Transaction.getExecutor().query(sql, paramList);
    	if (result == null || result.isEmpty()) {
			return outBean.set(rtnKey, 2).setOk();
		}
        
    	try {
			List<Bean> list = JsonUtils.toBeanList(result.get(0).getStr("BUTTON_PARAMS"));
			for (Bean bean : list) {
				if (StringUtils.equals(bean.getStr("name"), "cmBack") 
						&& bean.getStr("value").contains(rtnKey)) {
			        if (isValidate(bean, rtnKey)) {
						return outBean.set(rtnKey, 1).setOk();
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return outBean.set(rtnKey, 2).setOk();
		}
    	
    	return outBean.set(rtnKey, 2).setOk();
    }
    
    /**
     * 解析参数是否符合条件
     * @param param
     * @param rtnKey
     * @return
     */
    private boolean isValidate (Bean param,String rtnKey){
    	if (null == param) {
			return false;
		}
    	
		String[] paramArr = param.getStr("value").split(",");
		for (String keyValue : paramArr) {
			if (keyValue.contains(rtnKey)){
				String[] arr = keyValue.split("=");
				if (arr.length == 2 
						&& StringUtils.equals(arr[1].trim(), Constant.YES)) {
					return true;
				}
			}
		}
		
		return false;
    }
}
