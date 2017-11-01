package com.rh.ts.xmgl;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenh on 2017/10/31.
 */
public class KcapGzServ extends CommonServ {


    /**
     * @param paramBean
     * @return
     */
    public OutBean saveBeanList(ParamBean paramBean) {
        Transaction.begin();
        List<Bean> settings = paramBean.getList("BATCHDATAS");
        String xmId = paramBean.getStr("XM_ID");
        String currUserCode = Context.getUserBean().getCode();

        ParamBean deleteBean = new ParamBean();
        deleteBean.setServId(paramBean.getServId());
        deleteBean.put("_DEL_", true);//真删除
        deleteBean.set(Constant.PARAM_WHERE, " and XM_ID ='" + xmId + "' and S_USER ='" + currUserCode + "'");
        OutBean outBean;
        try {
            this.delete(deleteBean);
            StringBuilder builder = new StringBuilder();
            for (Bean setting : settings) {
                String gzCode = setting.getStr("GZ_CODE");
                builder.append("'").append(gzCode).append("',");
            }
            String sql = "select * from ts_xmgl_kcap_gzk where gz_code in (" + builder.substring(0, builder.length() - 1) + ")";
            List<Bean> beanList = Transaction.getExecutor().query(sql);
            for (Bean bean : beanList) {
                for (Bean setting : settings) {
                    if (bean.getStr("GZ_CODE").equals(setting.get("GZ_CODE"))) {
                        bean.set("GZ_VALUE2", setting.get("GZ_VALUE2"));
                    }
                }
                bean.set("XM_ID", xmId);

                bean.remove("GZ_ID");
                bean.remove("_PK_");
                bean.remove("S_USER");
                bean.remove("S_TDEPT");
                bean.remove("S_ODEPT");
                bean.remove("S_MTIME");
                bean.remove("S_FLAG");
                bean.remove("S_DEPT");
                bean.remove("S_CMPY");
                bean.remove("S_ATIME");
            }
            ParamBean batchSaveBean = new ParamBean();
            batchSaveBean.set("BATCHDATAS", beanList);
            batchSaveBean.setServId(paramBean.getServId());
            outBean = this.batchSave(batchSaveBean);
            Transaction.commit();
        } catch (Exception e) {
            Transaction.rollback();
            throw e;
        } finally {
            Transaction.end();
        }
        return outBean;
    }

}
