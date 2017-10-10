package com.rh.ts.xmgl;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenh on 2017/10/10.
 */
public class YapzwServ extends CommonServ {

    public OutBean getYapZw(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        Object sjId = paramBean.get("SJ_ID");
        String sql = "select * from TS_XMGL_KCAP_YAPZW a left join TS_BMSH_PASS b on a.SH_ID = b.SH_ID where a.SJ_ID = ?";
        List<Object> values = new ArrayList<>();
        values.add(sjId);
        List<Bean> beanList = Transaction.getExecutor().query(sql, values);
        outBean.setData(beanList);
        return outBean;
    }
}
