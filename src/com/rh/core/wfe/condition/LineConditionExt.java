/**
 * 
 */
package com.rh.core.wfe.condition;

import com.rh.core.base.Bean;
import com.rh.core.org.UserBean;
import com.rh.core.wfe.WfAct;

/**
 * 线定义条件扩展接口，作为简单条件表达式，复制条件表达式的补充。
 * 
 * @author yangjy
 */
public interface LineConditionExt {
    
    /**
     * 线定义条件扩展接口实现类定义前缀
     */
    public static final String PREFIX_CLS = "//#CLS#";
    
    /**
     * @param wfAct 当前节点实例
     * @param doUser 办理用户
     * @param config 类名后面配置的字符串
     * @return 检验是否能通过当前点
     */
    public boolean check(WfAct wfAct ,
        UserBean doUser ,
        Bean lineBean ,
        String config);
    
}
