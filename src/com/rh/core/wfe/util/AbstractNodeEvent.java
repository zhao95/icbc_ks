package com.rh.core.wfe.util;

import com.rh.core.wfe.WfAct;
import com.rh.core.wfe.WfParam;

/**
 * 节点事件监听类
 * 
 * @author yangjy
 */
public abstract class AbstractNodeEvent {
    
    /**
     * 当流程进入了配置监听类的节点，则会触发此方法。
     * 
     * @param preWfAct 前一节点实例对象
     * @param currWfAct 当前节点实例对象
     * @param wfParam 参数Bean
     * @param config 配置在类名+逗号后面的字符串。
     */
    public abstract void afterEnter(WfAct preWfAct ,
        WfAct currWfAct ,
        WfParam wfParam, String config);
    
    /**
     * 当配置监听类的节点办理完成时，触发此方法。
     * 
     * @param currWfAct 当前节点
     * @param wfParam 参数Bean
     * @param config 配置在类名+逗号后面的字符串。
     */
    public abstract void afterFinish(WfAct currWfAct ,
        WfParam wfParam, String config);
}
