package com.rh.ts.xmgl;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServMgr;
import com.rh.ts.xmgl.kcap.KcapResource;
import com.rh.ts.xmgl.kcap.arrange.ArrangeSeat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenh on 2017/10/10.
 */
public class YapzwServ extends CommonServ {

    /**
     * 根据SJ_ID获取已安排的考生信息
     *
     * @param paramBean
     * @return
     */
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

    /**
     * 根据SJ_ID获取已安排的考生信息（包括ip地址 所属机构 ）
     *
     * @param paramBean
     * @return
     */
    public OutBean getYapzwContent(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        String sql = "SELECT " +
                "a.*,d.IPZ_IP,d.IPZ_ZWH,b.YAPZW_ID " +
                "FROM " +
                "TS_BMSH_PASS a " +
                "LEFT JOIN ts_xmgl_kcap_yapzw b ON a.SH_ID = b.SH_ID " +
                "left join TS_KCGL_ZWDYB c on b.ZW_ID = c.ZW_ID " +
                "left join TS_KCGL_IPZWH d on b.KC_ID=d.KC_ID and d.IPZ_ZWH=c.ZW_ZWH_SJ " +
                "WHERE SJ_ID = ?";
        Object sjId = paramBean.get("SJ_ID");
        List<Object> values = new ArrayList<>();
        values.add(sjId);
        List<Bean> beanList = Transaction.getExecutor().query(sql, values);
        for (Bean bean : beanList) {
            String userCode = bean.getStr("BM_CODE");
            ParamBean userCodeParamBean = new ParamBean();
            userCodeParamBean.set("userCode", userCode);
            OutBean userOrgBean = ServMgr.act("TS_XMGL_KCAP_DAPCC", "getUserOrg", userCodeParamBean);
            bean.putAll(userOrgBean);
        }
        outBean.setData(beanList);
        return outBean;
    }
    
    
    /**
     * 执行考场自动安排座位
     * @param paramBean
     * @return
     */
	public OutBean doArrangeSeat(ParamBean paramBean) {

		OutBean outBean = new OutBean();

		String xmId = paramBean.getStr("XM_ID");

		String odeptId = paramBean.getStr("ODEPT_CODE");

		KcapResource res = new KcapResource(xmId, odeptId);
		
		ArrangeSeat as = new ArrangeSeat();
		
		as.doArrange(res);
		
		outBean.setOk();

		return outBean;
	}

}
