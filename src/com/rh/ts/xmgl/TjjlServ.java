package com.rh.ts.xmgl;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.DeptBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 场次安排 提交情况Serv
 * servId : TS_XMGL_KCAP_TJJL
 * Created by shenh on 2017/11/4.
 */
public class TjjlServ extends CommonServ {

    /**/

    /**
     * 获取项目场次安排提交情况
     *
     * @param paramBean paramBean {XM_ID pvlgDeptCodeStr}
     * @return outBean
     */
    public OutBean getKcOrgStatus(ParamBean paramBean) {
        List<Bean> result = new ArrayList<Bean>();

        String xmId = paramBean.getStr("XM_ID");
        String pvlgDeptCodeStr = paramBean.getStr("pvlgDeptCodeStr");
        String[] strings = pvlgDeptCodeStr.split(",");

        List<Object> values = new ArrayList<Object>();
        values.add(xmId);
        //根据用户权限code（deptCodeStr）过滤考场
        if (StringUtils.isBlank(pvlgDeptCodeStr)) {
            pvlgDeptCodeStr ="no-pvlgDeptCodeStr";
        }
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
        List<Bean> hasTjBeanList = new ArrayList<Bean>();
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
        Map<String, Boolean> map = new HashMap<String, Boolean>();
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

    /**
     * @param paramBean
     * @return
     */
    public OutBean getCanDraggable(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        String result = "";

        String deptCodeStr = paramBean.getStr("deptCodeStr");
        String xmId = paramBean.getStr("XM_ID");

        if (StringUtils.isBlank(deptCodeStr)) {
            result = "false";
        } else {
            List<String> deptCodeList = Arrays.asList(deptCodeStr.split(","));
            List<String> tjDeptCodeList = new ArrayList<String>();
            SqlBean sqlBean = new SqlBean();
            sqlBean.and("XM_ID", xmId);
            List<Bean> beanList = ServDao.finds(paramBean.getServId(), sqlBean);
            for (Bean bean : beanList) {
                tjDeptCodeList.add(bean.getStr("TJ_DEPT_CODE"));
            }

            boolean containsFlag = getContainsFlag(deptCodeList, tjDeptCodeList);
            result = containsFlag ? "true" : "false";
        }

        outBean.set("flag", result);
        return outBean;
    }

    /**
     * @param deptCodeList
     * @param deptCodeList2
     * @return true/false
     */
    private boolean getContainsFlag(List<String> deptCodeList, List<String> deptCodeList2) {
        boolean result = true;

        for (String deptCode2 : deptCodeList2) {
            //deptCode2  包含(deptCodeList)  ->  false
            boolean bol = true;
            for (String deptCode : deptCodeList) {
                DeptBean dept = OrgMgr.getDept(deptCode);
                if (dept != null) {
                    String codePath = dept.getCodePath();
                    if (codePath.contains(deptCode2)) {
                        bol = true;
                    } else {
                        bol = false;
                        break;
                    }
                }
            }
            if (bol) {
                result = false;
            }
        }
        return result;
    }
}
