package com.rh.core.wfe;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.wfe.def.WfNodeDef;
import com.rh.core.wfe.util.WfeConstant;

/**
 * 流程节点
 * 
 * @author ananyuan
 */
public abstract class AbstractWfAct {
    /** log */
    protected Log log = LogFactory.getLog(AbstractWfAct.class);
    
    /**
     * 流程实例对象
     */
    private WfProcess process;
    
    /**
     * 节点定义对象
     */
    private WfNodeDef nodeDef = null;

	private Boolean isRunningData = null;
    
    /**
     * @return 数据是否保存在运行数据表？
     */
    public boolean isRunningData() {
        if (isRunningData == null && process == null) {
            throw new RuntimeException("不能确定流程的运行状态。");
        } else if (isRunningData == null) {
            isRunningData = this.getProcess().isRunningData();
        }
        
        return isRunningData;
    }
    
    /**
     * @param running 数据是否保存在运行数据表？
     */
    protected void setIsRunningData(boolean running) {
        this.isRunningData = running;
    }
    
    /**
     * @return 节点实例ID
     */
    public String getId() {
        return getNodeInstBean().getId();
    }
    

    
    /**
     * @return 流程节点实例数据库记录对象
     */
    public abstract Bean getNodeInstBean();
    
    /**
     * 取得工作流流程实例对象
     * 
     * @return 流程实例对象
     */
    public WfProcess getProcess() {
        if (process == null) {
            process = new WfProcess(getNodeInstBean().getStr("PI_ID"),
                    isRunningData());
        }
        
        return process;
    }
    
    /**
     * 设置流程实例对象
     * 
     * @param aProcess 流程实例
     */
    protected void setProcess(WfProcess aProcess) {
        this.process = aProcess;
    }
    
    /**
     * @return 流程节点的NODE_CODE
     */
    public String getCode() {
        return getNodeInstBean().getStr("NODE_CODE");
    }
    
    /**
     * @return 节点定义对象
     */
    public WfNodeDef getNodeDef() {
        if (nodeDef == null) {
            nodeDef = this.getProcess().getProcDef().findNode(getCode());
        }
        
        return nodeDef;
    }
    
    /**
     * 
     * @return 是否 合并节点，
     */
    public boolean isConvergeNode() {
    	if (this.getNodeDef().getInt("NODE_IF_CONVERGE") == WfeConstant.NODE_IS_CONVERGE) {
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * 
     * @return 当前节点是否能办结流程
     */
    public boolean canEndTheProcess() {
    	if (this.nodeDef.getInt("PROC_END_FLAG") == WfeConstant.NODE_CAN_END_PROC) {
    		//添加判断，不能有多个活动节点
    		if (this.getProcess().getRunningWfAct().size() > 1) {
    			return false;
    		}
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * 得到从本节点出去的 活动状态 的节点 列表 , 查活动的节点，就是实例表，不是历史表
     * @return 节点实例列表
     */
    public List<WfAct> getNextLiveNodeInstList() {
    	List<WfAct> wfActList = new ArrayList<WfAct>();
    	
    	List<Bean> allInstList = this.getProcess().getAllNodeInstList();
    	
    	List<Bean> liveNodeList = new ArrayList<Bean>();
    	final String id = this.getId();
    	for (Bean bean : allInstList) {
    	    if (bean.getStr("PRE_NI_ID").equals(id)
    	            && bean.getInt("NODE_IF_RUNNING") == WfeConstant.NODE_IS_RUNNING) {
    	        liveNodeList.add(bean);
    	    }
    	}
    	
    	for (Bean nodeInstBean: liveNodeList) {
    		WfAct wfAct = new WfAct(this.getProcess(), nodeInstBean);
    		wfActList.add(wfAct);
    	}
    	
    	return wfActList;
    }
    
    /**
     * 
     * @return 节点办理用户ID。在多人竞争模式下，该值为空，当用户开始办理之后，该字段值为办理人ID。非竞争模式下，该值一直不为空。
     */
    public String getToUserId() {
        return this.getNodeInstBean().getStr("TO_USER_ID");
    }
    
}
