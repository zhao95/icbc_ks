package com.rh.core.wfe;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.util.Constant;
import com.rh.core.wfe.def.WfNodeDef;
import com.rh.core.wfe.util.FormDataProcess;
import com.rh.core.wfe.util.WfExtObjectCreator;
import com.rh.core.wfe.util.WfeConstant;

/**
 * 处理前端发送的处理完毕页面请求。
 * @author yangjy
 *
 */
public class WfIntegrationProcessor {


    
    /** 操作类型：服务 **/
    public static final int ACT_TYPE_SERV = 1;
    
    /** 操作类型：服务 **/
    public static final int ACT_TYPE_PROC = 2;
    
    private ParamBean paramBean = null;
    
    private String niId = null;
    
    private String instIfRunning = null;
    
    private boolean procIsRunning = true;
    
    private WfAct wfAct = null;
    
    private String dataId = null;
    
    private String servId = null;
    
    public WfIntegrationProcessor(ParamBean paramBean) {
        super();
        this.paramBean = paramBean;
        this.niId = paramBean.getStr("NI_ID");
        this.instIfRunning = paramBean.getStr("INST_IF_RUNNING");
        this.procIsRunning = procIsRunning(this.instIfRunning);
        this.dataId = paramBean.getStr("DATA_ID");
        this.servId = paramBean.getStr("SERV_ID");
        
        this.wfAct = new WfAct(this.niId, procIsRunning);
        
        WfContext.getContext().setCurrentWfAct(wfAct);
    }
    
    /**
     * @param paramBean
     * @return
     */
    protected OutBean saveFormData() {
        Bean dataBean = paramBean.getBean("FORM_DATA");
        
        // TODO by yangjinyun 为提高开发速度先写死，以后可以改到工作流定义上。
        if (servId.equals("OA_ICBC_QIANBAO")) {
            String clsConf = Context.getSyConf("WF_INT_LSN_ICBC_QB", "");
            if (!StringUtils.isEmpty(clsConf)) {
                WfExtObjectCreator crt = new WfExtObjectCreator(clsConf);
                FormDataProcess fdp = crt.create(FormDataProcess.class);
                fdp.process(this, crt.getConfig());
            }
        }
        
        if (dataBean == null || dataBean.isEmpty()) {
            return null;
        }
        
        // 从工作流中取得定义
        WfNodeDef nodeDef = wfAct.getNodeDef();
        
        // 取得需要保持的应用字段，并保存
        ParamBean saveParam = new ParamBean(dataBean);

        if (paramBean.isNotEmpty(Constant.AGENT_USER)) {
            saveParam.set(Constant.AGENT_USER,
                    paramBean.get(Constant.AGENT_USER));
        }
        
        // 保存数据
        saveParam.setId(dataId);
        OutBean outBean = ServMgr.act(servId, ServMgr.ACT_SAVE, saveParam);
        if (outBean.isError()) { // 保存数据错误，则
            return new OutBean().setError(outBean.getMsg());
        }
        
        // 取得并执行扩展类
        if (nodeDef.isNotEmpty("FORM_DATA_EXT_CLS")) {
            WfExtObjectCreator crt = new WfExtObjectCreator(
                    nodeDef.getStr("FORM_DATA_EXT_CLS"));
            FormDataProcess fdp = crt.create(FormDataProcess.class);
            fdp.process(this, crt.getConfig());
        }
        
        return new OutBean().setOk();
    }
    
    /**
     * 保存意见
     * 
     * @param paramBean 参数Bean
     * @return 意见保存结果
     */
    protected OutBean saveMind(ParamBean paramBean) {
        Bean mindDataBean = paramBean.getBean("MIND_DATA");
        // 没有意见数据则返回
        if (mindDataBean == null || mindDataBean.isEmpty()) {
            return null;
        }
        
        // 取出意见数据并保存
        String[] keys = mindDataBean.keySet().toArray(new String[0]);
        List<OutBean> outList = new ArrayList<OutBean>();
        for (String key : keys) {
            Bean mindBean = mindDataBean.getBean(key);
            // 如果不是最终意见，且意见内容为空，则不允许保存。
            if (!key.equals("TERMINAL") && mindBean.isEmpty("MIND_CONTENT")) {
                continue;
            }
            
            ParamBean mindParam = new ParamBean(mindBean);
            if (paramBean.isNotEmpty(Constant.AGENT_USER)) {
                mindParam.set(Constant.AGENT_USER,
                        paramBean.get(Constant.AGENT_USER));
            }
            
            OutBean out = ServMgr.act("SY_COMM_MIND", "save", mindParam);
            outList.add(out);
        }
        
        for (OutBean out : outList) {
            if (out.isError()) {
                return out;
            }
        }
        
        return new OutBean().setOk();
    }
    
    /**
     * 保存审批单传阅记录
     * 
     * @param paramBean 参数
     * @return 处理结果
     */
    protected OutBean saveSendData() {
        Bean sendBean = paramBean.getBean("SEND_DATA");
        if (sendBean == null || sendBean.isEmpty()) {
            return null;
        }
        sendBean.set("SERV_ID", this.servId);
        sendBean.set("DATA_ID", this.dataId);
        
        ParamBean sendParam = new ParamBean(sendBean);
        
        if (paramBean.isNotEmpty(Constant.AGENT_USER)) {
            sendParam.set(Constant.AGENT_USER,
                    paramBean.get(Constant.AGENT_USER));
        }
        
        return ServMgr.act("SY_COMM_SEND_SHOW_USERS", "autoSend", sendParam);
    }
    
    /**
     * 工作流送下一个节点
     * 
     * @param paramBean 参数bean
     * @return 执行结果
     */
    protected OutBean toNext(ParamBean paramBean) {
    	
        List<Bean> list = paramBean.getList("WF_DATA");
        if (list == null || list.size() == 0) {
            return null;
        }

        List<ParamBean> toNextList = new ArrayList<ParamBean>();
        List<ParamBean> actList = new ArrayList<ParamBean>();
        
        for (int i = 0; i < list.size(); i++) {
            Bean wfBean = list.get(i);
            ParamBean nextParam = new ParamBean(wfBean);
            if (paramBean.isNotEmpty(Constant.AGENT_USER)) {
                nextParam.set(Constant.AGENT_USER,
                        paramBean.get(Constant.AGENT_USER));
            }
            nextParam.set(WfeConstant.CURR_WF_ACT, this.wfAct);
            String nodeCode = nextParam.getStr("NODE_CODE");
            
            if (nodeCode.startsWith(WfeConstant.PREFIX_SERV_ACT)) { // 操作
                nextParam.set(WfeConstant.ACT_TYPE, ACT_TYPE_SERV);
                actList.add(nextParam);
            } else if (nodeCode.startsWith(WfeConstant.PREFIX_PROC_ACT)) { // 操作
                nextParam.set(WfeConstant.ACT_TYPE, ACT_TYPE_PROC);
                actList.add(nextParam);
            } else if (nodeCode.startsWith("R")) { // 工作流返回操作（逆向送交）
                nodeCode = nodeCode.substring(1);
                nextParam.set("NODE_CODE", nodeCode);
                toNextList.add(nextParam);
            } else { // 普通的工作流送交（正向送交）
                toNextList.add(nextParam);
            }
        }
        
        OutBean out = null;
        
        if (toNextList.size() > 0) { // 送下一个节点
            out = toNextNode(toNextList);
        }
        this.checkExecResult(out);
        
        OutBean outAct = null;
        if (actList.size() > 0) { // 处理工作流或服务定义的操作
        	outAct = doAct(paramBean, actList);
        }
        this.checkExecResult(outAct);
        
        OutBean result = new OutBean();
        result.set("WF_NEXT", out);
        result.set("ACT_NEXT", outAct);
        result.setOk();
        
        return result;
    }
    
    /**
     * 执行客户端“处理完毕”界面提交的Act请求
     * @param paramBean 参数Bean
     * @param list 操作Bean列表
     * @return 执行结果
     */
    private OutBean doAct(ParamBean paramBean , List<ParamBean> list) {
    	OutBean result = new OutBean();
        for (ParamBean bean : list) {
            ParamBean actParam = new ParamBean();
            
            if (paramBean.isNotEmpty(Constant.AGENT_USER)) {
                actParam.set(Constant.AGENT_USER,
                        paramBean.get(Constant.AGENT_USER));
            }
            
            String actCode = null;
            if(bean.getInt(WfeConstant.ACT_TYPE) == ACT_TYPE_SERV) {
                actParam.setServId(this.servId);
                actCode = bean.getStr("NODE_CODE").substring(
                        WfeConstant.PREFIX_SERV_ACT.length());
            } else {
                actParam.setServId(ServMgr.SY_WFE_PROC);
                actCode = bean.getStr("NODE_CODE").substring(
                        WfeConstant.PREFIX_PROC_ACT.length());
            }
            actParam.set("NI_ID", this.niId);
            actParam.set("INST_IF_RUNNING", this.instIfRunning);
            actParam.set(WfeConstant.CURR_WF_ACT, this.wfAct);
            actParam.setId(this.dataId);
            actParam.setAct(actCode);
            actParam.set("DO_USER_DEPT", paramBean.getStr("DO_USER_DEPT"));
            
            OutBean out = ServMgr.act(actParam);
            if (out.isError()) {
                return out;
            }
            result.set(actCode, out);
        }
        return result.setOk();
    }
    
    /**
     * 工作流送下一个节点，允许一次送多个目标点。
     * @param list 目标节点参数列表。
     * @return 执行结果
     */
    private OutBean toNextNode(List<ParamBean> list) {
        OutBean out = null;
        if (list.size() == 1) {
            ParamBean nextParam = list.get(0);
            nextParam.set(WfeConstant.CURR_WF_ACT, this.wfAct);
            out = ServMgr.act(ServMgr.SY_WFE_PROC, "toNext", nextParam);
        } else {
        	ParamBean nextParam = list.get(0);
            ParamBean multNodeParam = new ParamBean();
            multNodeParam.set("PI_ID", this.wfAct.getProcess().getId());
            multNodeParam.set("NI_ID", this.niId);
            multNodeParam.set("INST_IF_RUNNING", this.instIfRunning);
            multNodeParam.set(WfeConstant.CURR_WF_ACT, this.wfAct);
            multNodeParam.set(WfeConstant.MULT_NODE, list);
            multNodeParam.set("DO_USER_DEPT", nextParam.getStr("DO_USER_DEPT"));
            out = ServMgr.act(ServMgr.SY_WFE_PROC, "toNext", multNodeParam);
        }
        
        if (out.isError()) {
            return new OutBean().setError(out.getMsg());
        }
        
        return out; //new OutBean().setOk();
    }
    
    /**
     * 检查OutBean是否包含错误信息
     * 
     * @param out 输出Bean
     */
    protected void checkExecResult(OutBean out) {
        if (out == null) {
            return;
        }
        
        if (out.isError()) {
            throw new TipException(out.getMsg().substring(Constant.RTN_MSG_ERROR.length()));
        }
    }
    
    /**
     * @param procRunning 从页面传来的 WF_INST_ID 字符串类型的 流程是否运行
     * @return 流程是否运行
     */
    protected boolean procIsRunning(String procRunning) {
        boolean procIsRunning = true;
        
        if (procRunning.equals(String.valueOf(WfeConstant.PROC_NOT_RUNNING))) {
            procIsRunning = false;
        }
        
        return procIsRunning;
    }

    
    public ParamBean getParamBean() {
        return paramBean;
    }

    
    public WfAct getWfAct() {
        return wfAct;
    }

    
    public String getDataId() {
        return dataId;
    }

    
    public String getServId() {
        return servId;
    }
    
}