package com.rh.ts.xmgl;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.*;
import com.rh.ts.util.TsConstant;
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
     * 返回前端的已安排座位信息（包含姓名等其他信息）
     */
    private final static String yapzwInfoSql = "select * from TS_XMGL_KCAP_YAPZW a left join TS_BMSH_PASS b on a.SH_ID = b.SH_ID ";

    /**
     * 根据SJ_ID获取已安排的考生信息
     *
     * @param paramBean
     * @return
     */
    public OutBean getYapZw(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        Object sjId = paramBean.get("SJ_ID");
        String sql = yapzwInfoSql + " where a.SJ_ID = ?";
        List<Object> values = new ArrayList<>();
        values.add(sjId);
        List<Bean> beanList = Transaction.getExecutor().query(sql, values);
        outBean.setData(beanList);
        return outBean;
    }

    private Bean getYapZwById(String yapZwId) {
        Bean result = null;
        String sql = yapzwInfoSql + " where a.YAPZW_ID = ?";
        List<Object> values = new ArrayList<>();
        values.add(yapZwId);
        List<Bean> beanList = Transaction.getExecutor().query(sql, values);
        if (beanList != null && beanList.size() > 0) {
            result = beanList.get(0);
        }
        return result;
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
                ",(select COUNT(*) from TS_BMSH_PASS a2 where a2.BM_CODE=a.BM_CODE and a2.XM_ID=a.XM_ID AND a2.BM_STATUS NOT IN ('1', '3') ) as count " +
                " FROM TS_BMSH_PASS a " +
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

    /**
     * @param paramBean
     * @return
     */
    public OutBean saveBean(ParamBean paramBean) {
        //ZW_ID:zwId,
        //SJ_ID:sjid,
        //SJ_CC:sjcc,
        //SJ_DATE:sjDate,
        //CC_ID:ccid,
        //KC_ID:kcid,
        //SH_ID:shId
        String shId = paramBean.getStr("SH_ID");
        String zwId = paramBean.getStr("ZW_ID");
        Bean bmshPassBean = ServDao.find(TsConstant.SERV_BMSH_PASS, shId);

        String userCode = bmshPassBean.getStr("BM_CODE");//类别编码
        UserBean userBean = UserMgr.getUser(userCode);

        Bean zwBean = ServDao.find(TsConstant.SERV_KCGL_ZWDYB, zwId);
        String zwZwhXt = zwBean.getStr("ZW_ZWH_XT");

        String bmLb = bmshPassBean.getStr("BM_LB");//类别编码
        String bmXl = bmshPassBean.getStr("BM_XL");//序列编码
        String bmMk = bmshPassBean.getStr("BM_MK");//模块编码
        String bmType = bmshPassBean.getStr("BM_TYPE");//BM_LV BM_TYPE //级别编码
        String bmKsTime = bmshPassBean.getStr("BM_KS_TIME");//模块编码

        paramBean.put("BM_LB", bmLb);
        paramBean.put("BM_XL", bmXl);
        paramBean.put("BM_MK", bmMk);
        paramBean.put("BM_LV", bmType);
        paramBean.put("BM_KS_TIME", bmKsTime);

        paramBean.put("U_CODE", userCode);//U_CODE	考生编码
        paramBean.put("U_ODEPT", userBean.getODeptCode());//U_ODEPT 考生机构

        //SJ_CC	时间场次
        paramBean.put("ZW_XT", zwZwhXt);

        paramBean.put("U_TYPE", 0);//是否借考 1借考
        paramBean.put("ISSUE", 0);//是否 提交/发布

        OutBean yapzwBean = super.save(paramBean);
        String yapzwId = yapzwBean.getStr("YAPZW_ID");
        yapzwBean.putAll(getYapZwById(yapzwId));
        return yapzwBean;
    }
}
