package com.rh.ts.xmgl;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import org.apache.commons.lang.StringUtils;

/**
 * Created by shenh on 2018/1/15.
 */
public class DapccUtils {

    /**
     * 权限deptCodes中是否有包含deptCode的权限
     *
     * @param deptCode  权限机构
     * @param deptCodes 权限机构数组
     * @return String  "true" / "false"
     */
    public static String getDeptContains(String deptCode, String[] deptCodes) {
        String result = "false";
        SqlBean sqlBean = new SqlBean();
//        sqlBean.and("DEPT_CODE", deptCode);
        Bean bean = ServDao.find(ServMgr.SY_ORG_DEPT, deptCode);
        if (bean != null) {
            String codePath = bean.getStr("CODE_PATH");
            for (String code : deptCodes) {
                if (StringUtils.isNotBlank(code) && codePath.contains(code)) {
                    result = "true";
                    break;
                }
            }
        }
        return result;
    }
}
