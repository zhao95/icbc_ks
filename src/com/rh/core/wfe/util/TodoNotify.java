package com.rh.core.wfe.util;

import com.rh.core.base.Bean;
import com.rh.core.wfe.WfAct;

/**
 * 待办提醒
 *
 */
public interface TodoNotify {

    /**
     * 
     * @param dataBean 数据Bean
     */
    void send(Bean dataBean, WfAct wfAct);
}
