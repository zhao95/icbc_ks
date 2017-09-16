package com.rh.ts.kcgl;

import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;

public class KcglServ  extends CommonServ{
    /**
     * 考场管理审核 通过更新相应信息
     *  KC_STATE=5, _PK_=1k3hWfgP92BE6EV5P2RK,
     * @param paramBean
     * @return
     */
    public OutBean updateShInfo(ParamBean paramBean) {
	OutBean outBean = new OutBean();
	outBean.setOk();
	return outBean;
    }
}
