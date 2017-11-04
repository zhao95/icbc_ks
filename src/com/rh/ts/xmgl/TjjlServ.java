package com.rh.ts.xmgl;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 场次安排 提交情况Serv
 * servId : TS_XMGL_KCAP_TJJL
 * Created by shenh on 2017/11/4.
 */
public class TjjlServ extends CommonServ {

    public OutBean getKcOrgStatus(ParamBean paramBean) {
        List<Bean> result = new ArrayList<>();

        String xmId = paramBean.getStr("XM_ID");
        String pvlgDeptCodeStr = paramBean.getStr("pvlgDeptCodeStr");
        String[] strings = pvlgDeptCodeStr.split(",");

        List<Object> values = new ArrayList<>();
        values.add(xmId);
        //根据用户权限code（deptCodeStr）过滤考场
        String[] splitDeptCode = pvlgDeptCodeStr.split(",");
        StringBuilder deptBuilder = new StringBuilder(" and ( ");
        for (String deptCode : splitDeptCode) {
            values.add("%" + deptCode + "%");
            deptBuilder.append(" e.CODE_PATH like ? ").append("or ");
        }
        String deptSql = deptBuilder.substring(0, deptBuilder.length() - 3) + ")";
        //用户权限下的考场 所属机构集合
        List<Bean> kcOrgList = Transaction.getExecutor().query("select e.DEPT_CODE,e.DEPT_NAME from TS_XMGL_KCAP_DAPCC a " +
                " INNER JOIN TS_KCGL b on b.KC_ID =a.KC_ID " +
                " LEFT JOIN SY_ORG_DEPT e on b.KC_ODEPTCODE = e.DEPT_CODE " +
                " where a.XM_ID=? " + deptSql, values);

        //用户权限下已经提交的机构信息
        List<Bean> hasTjBeanList = new ArrayList<>();
        for (String deptCode : strings) {
            if (StringUtils.isNotBlank(deptCode)) {
                values.clear();
                values.add("%" + deptCode + "%");
                values.add(xmId);
                List<Bean> list = Transaction.getExecutor().query("SELECT b.DEPT_CODE,b.DEPT_NAME FROM TS_XMGL_KCAP_TJJL a" +
                        " INNER JOIN sy_org_dept b on b.DEPT_CODE = a.TJ_DEPT_CODE" +
                        " where b.CODE_PATH like ?" +
                        " and XM_ID = ?" +
                        " order by b.DEPT_CODE;", values);
                hasTjBeanList.addAll(list);
            }
        }


        int hasCount = 0;//已提交数目
        int noCount = 0;//未提交数目
        Map<String, Boolean> map = new HashMap<>();
        for (Bean bean : hasTjBeanList) {
            hasCount++;
            map.put(bean.getStr("DEPT_CODE"), true);
            bean.set("isTj", true);
            result.add(bean);
        }

        for (Bean bean : kcOrgList) {
            String deptCode = bean.getStr("DEPT_CODE");
            if (map.get(deptCode) == null) {
                noCount++;
                map.put(bean.getStr("DEPT_CODE"), false);
                bean.set("isTj", false);
                result.add(bean);
            }
        }

        //排序
        Collections.sort(result, new Comparator<Bean>() {
            @Override
            public int compare(Bean o1, Bean o2) {
                return o1.getStr("DEPT_CODE").compareTo(o2.getStr("DEPT_CODE"));
            }
        });


        OutBean outBean = new OutBean();
        outBean.setData(result);
        outBean.set("hasCount", hasCount);
        outBean.set("noCount", noCount);
        outBean.set("totalCount", result.size());
        return outBean;
    }
}
