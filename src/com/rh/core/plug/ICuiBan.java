package com.rh.core.plug;

import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;

/**
 * 接口：流程跟踪（催办）按钮的其它实现
 * 在变量配置中配置实现该接口的类实现：SY_WF_TRACK_CUIBAN_CLASS
 */
public interface ICuiBan {
	OutBean cuiban(ParamBean paramBean);
}
