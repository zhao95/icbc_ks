package com.rh.ts.xmgl;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.*;
import com.rh.ts.util.BMUtil;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.kcap.KcapResource;
import com.rh.ts.xmgl.kcap.arrange.ArrangeSeat;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Created by shenh on 2017/10/10.
 */
public class YapzwServ extends CommonServ {

    /**
     * 返回前端的已安排座位信息（包含姓名等其他信息）
     */
//    private final static String yapzwInfoSql = "select * from TS_XMGL_KCAP_YAPZW a " +
//            "left join TS_BMSH_PASS b on a.SH_ID = b.SH_ID ";

    // jkflag 1 一般 2 已经申请借考 3 借入状态
    private final static String yapzwInfoSql = "SELECT *, " +
            "( CASE WHEN (" +
            " EXISTS ( SELECT 'X' FROM ts_jklb_jk c WHERE INSTR(c.JK_KSNAME, b.BM_ID) > 0 and b.BM_ID !='' and b.BM_ID is not null)" +
            " AND b.BM_STATUS IN ('2', '3') " +
            ") THEN '3'" +
            " WHEN  EXISTS ( SELECT 'X' FROM ts_jklb_jk c WHERE INSTR(c.JK_KSNAME, b.BM_ID) > 0 and b.BM_ID !='' and b.BM_ID is not null and c.JK_STATUS ='1' )  THEN '2'" +
            " ELSE '1' END ) AS jk_flag " +
            "FROM TS_XMGL_KCAP_YAPZW a LEFT JOIN TS_BMSH_PASS b ON a.SH_ID = b.SH_ID";
    //座位显示 已借入/已经申请借考 标识

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
        List<Object> values = new ArrayList<Object>();
        values.add(sjId);
        List<Bean> beanList = Transaction.getExecutor().query(sql, values);
        outBean.setData(beanList);
        return outBean;
    }

    private Bean getYapZwById(String yapZwId) {
        Bean result = null;
        String sql = yapzwInfoSql + " where a.YAPZW_ID = ?";
        List<Object> values = new ArrayList<Object>();
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
                "a.*,d.IPZ_IP,d.IPZ_ZWH,b.YAPZW_ID,c.ZW_ZWH_XT,c.ZW_ZWH_SJ " +
                ",(select COUNT(*) from TS_BMSH_PASS a2 where a2.BM_CODE=a.BM_CODE and a2.XM_ID=a.XM_ID AND a2.BM_STATUS NOT IN ('1', '3') ) as count " +
                " FROM TS_BMSH_PASS a " +
                "LEFT JOIN ts_xmgl_kcap_yapzw b ON a.SH_ID = b.SH_ID " +
                "left join TS_KCGL_ZWDYB c on b.ZW_ID = c.ZW_ID " +
                "left join TS_KCGL_IPZWH d on b.KC_ID=d.KC_ID and d.IPZ_ZWH=c.ZW_ZWH_SJ " +
                "WHERE SJ_ID = ? order by cast(c.ZW_ZWH_SJ AS UNSIGNED)";
        Object sjId = paramBean.get("SJ_ID");
        List<Object> values = new ArrayList<Object>();
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
     *
     * @param paramBean
     * @return
     */
    public OutBean doArrangeSeat(ParamBean paramBean) {

        //清除场次安排脏数据
        clearDirtyData(paramBean);

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
     * 清除场次安排脏数据
     *
     * @param paramBean XM_ID
     * @return outBean
     */
    public OutBean clearDirtyData(ParamBean paramBean) {
        String xmId = paramBean.getStr("XM_ID");

        //删除考场安排 脏数据
        String sql = "delete from ts_xmgl_kcap_yapzw " +
                "where YAPZW_ID in ( " +
                "   SELECT t.yapzw_id from ( " +
                "       select a.yapzw_id from ts_xmgl_kcap_yapzw a left join ts_xmgl_kcap_dapcc_ccsj b on a.SJ_ID =b.SJ_ID where b.SJ_ID is null " +
                "   ) t " +
                ") and XM_ID ='" + xmId + "'";
        Transaction.getExecutor().execute(sql);
        return new OutBean().setOk();
    }

    public OutBean getIndexInfo(ParamBean paramBean) {
        OutBean outBean = new OutBean();

        String shId = paramBean.getStr("SH_ID");
        String sjDate = paramBean.getStr("SJ_DATE");
        String sjCc = paramBean.getStr("SJ_CC");
//        String xmId = paramBean.getStr("XM_ID");
        Bean bmshPassBean = ServDao.find(TsConstant.SERV_BMSH_PASS, shId);
        String userCode = bmshPassBean.getStr("BM_CODE");//类别编码

        String msgStr = "";//"' and XM_ID ='" + xmId +
        List<Bean> beanList = ServDao.finds(TsConstant.SERV_KCAP_YAPZW, " and SJ_CC ='" + sjCc + "' and U_CODE ='" + userCode + "' and SJ_DATE ='" + sjDate + "'");
        if (beanList != null && beanList.size() > 0 && !beanList.get(0).getStr("SJ_CC").equals(sjCc)) {
            Bean bean = beanList.get(0);
            Bean xmBean = ServDao.find(TsConstant.SERV_XMGL, bean.getStr("XM_ID"));
            Bean kcBean = ServDao.find("TS_KCGL", bean.getStr("KC_ID"));
            Bean sjBean = ServDao.find(TsConstant.SERV_KCAP_CCSJ, bean.getStr("SJ_ID"));
            String kcName = kcBean.getStr("KC_NAME");
            String xmName = xmBean.getStr("XM_NAME");
            String sjStart = sjBean.getStr("SJ_START");
            msgStr = xmName + " " + kcName + " " + sjStart + " 已安排";
            outBean.set("XM_NAME", xmName);
            outBean.set("KC_NAME", kcName);
            outBean.set("SJ_START", sjStart);
        }
        outBean.set("MSG_STR", msgStr);
        return outBean;
    }

    /**
     * @param paramBean
     * @return
     */
    public OutBean saveBean(ParamBean paramBean) {
        clearDirtyData(paramBean);

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

        //1 请假 2 借考 3 请假 + 借考
        int uType = (bmshPassBean.getStr("BM_STATUS").equals("2") || bmshPassBean.getStr("BM_STATUS").equals("3")) ? 1 : 0;
        paramBean.put("U_TYPE", uType);//是否借考 1借考
        paramBean.put("ISSUE", 0);//是否 提交/发布
        paramBean.put("ISAUTO", 2);//1自动 2手动
        paramBean.put("S_USER", Context.getUserBean().getCode());//安排人

        OutBean outBean = new OutBean();
        try {
            outBean = super.save(paramBean);
        } catch (Exception e) {
            if (e.getCause().getMessage().contains("IDX_DATE_CC_USER")) {
                outBean.setError("IDX_DATE_CC_USER");
            }
        }
        String yapzwId = outBean.getStr("YAPZW_ID");
        Bean yapZw = getYapZwById(yapzwId);
        if (yapZw != null) {
            outBean.putAll(yapZw);
        }

        return outBean;
    }


    /**
     * @param paramBean
     * @return
     */
    public OutBean saveBeanFromList(ParamBean paramBean) {
        clearDirtyData(paramBean);

//        String sjId = paramBean.getStr("SJ_ID");
//        String sjCc = paramBean.getStr("SJ_CC");
//        String sjDate = paramBean.getStr("SJ_DATE");
        String ccId = paramBean.getStr("CC_ID");
        String kcId = paramBean.getStr("KC_ID");
        String shIdStr = paramBean.getStr("SH_ID_STR");
        String xmId = paramBean.getStr("XM_ID");
        String[] shIds = shIdStr.split(",");
        List<String> shIdList = new ArrayList<String>(Arrays.asList(shIds));

        Bean queryBean = new Bean();
        queryBean.set("_NOPAGE_", true);
        queryBean.set("_WHERE_", "and KC_ID = '" + kcId + "'");
        queryBean.set("_ORDER_", " ZW_ZWH_XT asc");
        List<Bean> zwList = ServDao.finds(TsConstant.SERV_KCGL_ZWDYB, queryBean);
        List<Integer> rows = new ArrayList<Integer>();
        List<Integer> cols = new ArrayList<Integer>();
        Map<String, Bean> zwMap = new HashMap<String, Bean>();
        for (Bean zwBean : zwList) {
            String zwZwhXt = zwBean.getStr("ZW_ZWH_XT");
            String[] splits = zwZwhXt.split("-");
            try {
                String row = splits[0];
                rows.add(Integer.parseInt(row));
                String col = splits[1];
                cols.add(Integer.parseInt(col));
                zwMap.put(row + "-" + col, zwBean);
            } catch (Exception e) {
                //错误信息跳过
            }
        }
        //有正确的座位数据  有要安排的考生
        if (!rows.isEmpty() && !cols.isEmpty() && !shIdList.isEmpty()) {
            Integer maxRowIndex = Collections.max(rows);
            Integer maxColIndex = Collections.max(cols);
            for (int i = 1; i <= maxRowIndex; i++) {
                for (int j = 1; j <= maxColIndex; j++) {
                    Bean zwBean = zwMap.get(i + "-" + j);
                    //有座位并启用
                    if (zwBean != null && "1".equals(zwBean.getStr("ZW_KY"))) {
                        String zwId = zwBean.getStr("ZW_ID");

                        List<Bean> beans = ServDao.finds(TsConstant.SERV_KCAP_YAPZW, " and ZW_ID='" + zwId + "' and KC_ID='" + kcId + "' and CC_ID='" + ccId + "' and XM_ID='" + xmId + "'");
                        if (beans.size() > 0) {
                            //座位已安排 下一个座位
                            continue;
                        }
                        for (Iterator<String> iterator = shIdList.iterator(); iterator.hasNext(); ) {
                            String shId = iterator.next();
                            paramBean.set("ZW_ID", zwId);
                            paramBean.set("SH_ID", shId);
                            ParamBean newParamBean = new ParamBean();
                            newParamBean.putAll(paramBean);
                            OutBean outBean = saveBean(newParamBean);
                            if (StringUtils.isNotBlank(outBean.getStr("YAPZW_ID"))) {
                                iterator.remove();
                                break;
                            }
                        }
                    }
                }
                if (shIdList.isEmpty()) {
                    break;//没有安排的考生跳过
                }
            }
        } /*else {
            //没有座位 跳过
        }*/

        return new OutBean();
    }

    /**
     * 获取考场安排情况（已安排座位数/考场座位数）
     *
     * @param paramBean KC_ID SJ_ID
     * @return {total,yapNum}
     */
    public OutBean getKcZwInfo(ParamBean paramBean) {
        String kcId = paramBean.getStr("KC_ID");
        String sjId = paramBean.getStr("SJ_ID");

        List<Bean> zwList = ServDao.finds(TsConstant.SERV_KCGL_ZWDYB, " and KC_ID = '" + kcId + "'");//座位数
        List<Bean> yapList = ServDao.finds(TsConstant.SERV_KCAP_YAPZW, " and SJ_ID = '" + sjId + "'");//已安排座位数

        OutBean outBean = new OutBean();
        outBean.set("total", zwList.size());
        outBean.set("yapNum", yapList.size());
        return outBean;
    }

    /**
     * 根据BM_ID获取考生座位信息
     *
     * @param paramBean BM_ID
     * @return outBean
     */
    public OutBean getKsZWInfoByBmId(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        String bmId = paramBean.getStr("BM_ID");

        String sql = "select c.BM_TYPE,c.BM_MK,c.BM_XL,f.BM_TITLE,b.KC_NAME,b.KC_ADDRESS,d.SJ_START,c.BM_KS_TIME  " +/*e.ZW_ZWH_SJ,*/
                "from ts_xmgl_kcap_yapzw a " +
                "left join ts_kcgl b on b.KC_ID = a.KC_ID " +
                "left join ts_bmsh_pass c on c.SH_ID = a.SH_ID " +
                "left join ts_xmgl_kcap_dapcc_ccsj d on d.SJ_ID = a.SJ_ID " +
                "left join ts_kcgl_zwdyb e on e.ZW_ID = a.ZW_ID " +
                "left join ts_bmlb_bm f on f.BM_ID = c.BM_ID " +
                "where f.BM_ID =? order by d.SJ_START";
        List<Object> values = new ArrayList<Object>();
        values.add(bmId);
        List<Bean> ksDataList = Transaction.getExecutor().query(sql, values);
        if (ksDataList != null && ksDataList.size() > 0) {
            Bean ksDataBean = ksDataList.get(0);

            String bmTitle = (String) ksDataBean.get("BM_TITLE");
            String bmXl = (String) ksDataBean.get("BM_XL");
            String bmMk = (String) ksDataBean.get("BM_MK");
            String bmType = (String) ksDataBean.get("BM_TYPE");
            String bm_bt = BMUtil.getExaminationName(bmType, bmXl, bmMk);
            String title;
            if (!"".equals(bmMk)) {
                title = bm_bt;
            } else {
                title = bmTitle;
            }
            ksDataBean.set("title", title);
            outBean.putAll(ksDataBean);
        }
        return outBean;
    }
}
