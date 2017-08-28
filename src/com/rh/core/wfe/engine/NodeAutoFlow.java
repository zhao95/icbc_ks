package com.rh.core.wfe.engine;

import com.rh.core.base.Bean;
import com.rh.core.wfe.WfAct;

/**
 * 流程节点自动流转处理接口
 * 
 * @author Tanyh 20160531
 */
public interface NodeAutoFlow {
    
    
    /** 当前节点操作码: 人工流转 **/
    public static int OPT_TYPE_MANUAL = 1;
    
    /** 当前节点操作码: 自动流转 **/
    public static int OPT_TYPE_AUTO = 2;
    
    /** 当前节点操作码: 自动办结 **/
    public static int OPT_TYPE_END = 3;
    
    /**
     * 获取当前节点操作码，1：人工流转、2：自动流转、3：自动办结
     * 
     * @param preAct 前一节点实例对象
     * @param currNodeDef 当前节点定义对象
     * @param paramBean 配置参数对象
     * @return int 当前节点操作码
     */
    public int getActOptCode(WfAct preAct , WfAct currWfAct , Bean paramBean);
    
    /**
     * @param currWfAct 当前节点实例对象
     * @param paramBean 参数Bean
     * @return 自动流转时设置的默认意见。
     */
    public Bean getDefaultMindBean(WfAct currWfAct , Bean paramBean);
    
    /**
     * 办结之后的处理方法
     * @param currWfAct 当前节点实例对象
     */
    public void afterFinish(WfAct currWfAct);
}
