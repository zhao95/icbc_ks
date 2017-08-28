package com.rh.core.wfe.util;

import com.rh.core.wfe.WfIntegrationProcessor;

/**
 * 处理完毕界面提交请求之后，工作流会自动处理保存表单数据、保存意见、分发等操作， 如果有特殊业务逻辑要处理，可以实现这个接口，对数据做特殊处理。
 * 
 * @author yangjy
 */
public interface FormDataProcess {
    
    /**
     * 处理特殊业务逻辑。如果有错误，可以直接throw RuntimeException。
     * @param processor 处理完毕对象
     * @param config 配置
     */
    void process(WfIntegrationProcessor processor, String config);
}
