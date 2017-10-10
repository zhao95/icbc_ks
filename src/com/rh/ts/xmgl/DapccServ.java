package com.rh.ts.xmgl;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.*;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class DapccServ extends CommonServ {

    private final static String CHILD = "CHILD";

//    public OutBean getZwByKcId(ParamBean paramBean) {
//        OutBean outBean = new OutBean();
//        String kcId = paramBean.getStr("kcId");
//
//        kcId
//        outBean.set("", );
//
//        return outBean;
//    }


//    TS_XMGL_KCAP_DFPKS

    /**
     * 根据条件 获取考生信息
     *
     * @param paramBean paramBean
     * @return outBean
     */
    public OutBean getKsContent(ParamBean paramBean) {
        OutBean outBean = new OutBean();

        String searchDeptCode = paramBean.getStr("searchDeptCode");
        Bean dept = this.getDeptByCode(searchDeptCode, null);
        String deptLevel = dept.getStr("DEPT_LEVEL");
        if ("3".equals(deptLevel) || "2".equals(deptLevel)) {
            paramBean.set("containDeptCode", searchDeptCode);
        } else {
            paramBean.set("equalDeptCode", searchDeptCode);
        }

        //拼凑whereSql配置
        Map<String, String> configMap = new HashMap<>();
        configMap.put("XM_ID", "XM_ID = ?");
        configMap.put("containDeptCode", "CODE_PATH like ?");
//        configMap.put("containDeptCode", "CODE_PATH like ?");
        configMap.put("equalDeptCode", "b.DEPT_CODE = ?");
        configMap.put("searchName", "USER_NAME like ?");
        configMap.put("searchLoginName", "USER_LOGIN_NAME like ?");
        configMap.put("searchBmXl", "BM_XL like ?");
        configMap.put("searchBmMk", "BM_MK like ?");
        configMap.put("searchBmJb", "BM_TYPE = ?");
        //configMap.put("searchBmCount", " and USER_LOGIN_NAME like '%?%'"); //todo

        List extWhereSqlData = this.getExtWhereSqlData(paramBean, configMap);
        String whereSql = (String) extWhereSqlData.get(0);
        List<Object> values = (List<Object>) extWhereSqlData.get(1);
//        Pair<String, List<Object>> extWhereSqlData
        String sql = "select " +
                "a.*,b.USER_NAME,b.USER_LOGIN_NAME,b.DEPT_CODE,c.CODE_PATH " +
                "from TS_BMSH_PASS a " +
                "left join SY_ORG_USER b on a.BM_CODE = b.USER_CODE " +
                "LEFT JOIN SY_ORG_DEPT c ON b.DEPT_CODE = c.DEPT_CODE "
                +"where not exists(select 'X' from TS_XMGL_KCAP_YAPZW where SH_ID=a.SH_ID)"
                + whereSql;
        //where 姓名/登录名/报考类型/报考数  bm_name /login_name?/
        List<Bean> beanList = Transaction.getExecutor().query(sql, values);
//        List<Bean> beanList = ServDao.finds("TS_XMGL_KCAP_DFPKS", paramBean);
        for (Bean bean : beanList) {
            String userCode = bean.getStr("BM_CODE");
            bean.putAll(getUserOrg(userCode));
        }
        outBean.setData(beanList);
        return outBean;
    }

    /**
     * 拼接whereSql
     *
     * @param paramBean paramBean
     * @param configMap :{"XM_ID", "XM_ID = ?","searchName", "USER_NAME like ?"}
     * @return Pair<String, List<Object>>  whereSql,values
     */
    private List getExtWhereSqlData(ParamBean paramBean, Map<String, String> configMap) {
        StringBuilder whereSql = new StringBuilder(/*"where "*/);
        List<Object> values = new ArrayList<>();
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            String key = entry.getKey();
            String sql = entry.getValue();
            String value = paramBean.getStr(key);
            if (StringUtils.isNotEmpty(value)) {
//                if (!"where ".equals(whereSql.toString())) {
                    whereSql.append(" and ");
//                }
                if (sql.contains("like")) {
                    value = "%" + value + "%";
                }
                whereSql.append(sql);
                values.add(value);
            }
        }
        List result = new ArrayList();
        result.add(whereSql.toString());
        result.add(values);
        return result;
    }

    /**
     * 获取考场和场次
     *
     * @param paramBean paramBean xmId
     * @return
     */
    public OutBean getKcAndCc(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        String DEPT_CODE = "KC_ODEPTCODE";//KC_ODEPTCODE：根据考场所属机构挂靠考场；DEPT_CODE：根据关联机构挂靠考场
        String xmId = paramBean.getStr("xmId");
        List<Object> values = new ArrayList<>();
        values.add(xmId);
        List<Bean> list = Transaction.getExecutor().query("select a.*,d.DEPT_CODE,b.KC_ODEPTCODE from TS_XMGL_KCAP_DAPCC a " +
                "INNER JOIN TS_KCGL b on b.KC_ID =a.KC_ID " +
                "INNER JOIN TS_KCGL_GLJG c on c.KC_ID=a.KC_ID " +
                "LEFT JOIN SY_ORG_DEPT d on d.DEPT_CODE = c.JG_CODE " +
                "where a.XM_ID=?", values);
        Set<String> hashSet = new HashSet<>();
        for (Bean bean : list) {
            hashSet.add(bean.getStr(DEPT_CODE));
        }

        Map<String, Bean> cache = new HashMap<>();
        Bean rootDeptBean = this.getDeptList(hashSet, cache);

//        List<Bean> list = ServDao.finds("TS_XMGL_KCAP_DAPCC", "and XM_ID='" + xmId + "'");
        for (Bean item : list) {
            String ccId = item.getId();
            List<Bean> list2 = ServDao.finds("TS_XMGL_KCAP_DAPCC_CCSJ", "and CC_ID = '" + ccId + "'");
            item.set(CHILD, list2);
            this.putChild(cache.get(item.getStr(DEPT_CODE)), item, true);
        }
        outBean.set("root", rootDeptBean);
//        outBean.set(CHILD, list);
        return outBean;
    }

    public OutBean getKsOrgTree(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        String kcId = paramBean.getStr("kcId");
        List<Object> values = new ArrayList<>();
        values.add(kcId);
        List<Bean> list = Transaction.getExecutor().query("select a.*, c.JG_CODE from TS_XMGL_KCAP_DAPCC a " +
//                "INNER JOIN TS_KCGL b on b.KC_ID =a.KC_ID " +
                "INNER JOIN TS_KCGL_GLJG c on c.KC_ID=a.KC_ID " +
//                "LEFT JOIN SY_ORG_DEPT d on d.DEPT_CODE = c.JG_CODE " +
                "where a.KC_ID=?", values);
        Set<String> hashSet = new HashSet<>();
        for (Bean bean : list) {
            hashSet.add(bean.getStr("JG_CODE"));
        }

        Map<String, Bean> cache = new HashMap<>();
        Bean rootDeptBean = this.getDeptList(hashSet, cache);
        outBean.putAll(rootDeptBean);
        for (String deptCode : hashSet) {
            ParamBean queryBean = new ParamBean();
            queryBean.set("DICT_ID", "SY_ORG_ODEPT_ALL");
            queryBean.set("PID", deptCode);
            OutBean bean = ServMgr.act("SY_COMM_INFO", "dict", queryBean);
            List<Bean> child = bean.getList("CHILD");
            for (Bean item : child) {
                this.putChild(cache.get(deptCode), item, false);
            }
        }

        return outBean;
    }

    private Bean getDeptList(Set<String> deptCodes, Map<String, Bean> cache) {
//        String DEPT_CODE = "DEPT_CODE";
        String DEPT_PCODE = "DEPT_PCODE";
        Bean dept = null;
        for (String deptCode : deptCodes) {
            dept = getDeptByCode(deptCode, cache);
            if (dept == null) {
                continue;
            }
            cache.put(deptCode, dept);
            while (StringUtils.isNotEmpty((dept.getStr(DEPT_PCODE)))) {
                Bean parentDept = getDeptByCode(dept.getStr(DEPT_PCODE), cache);
                this.putChild(parentDept, dept, false);
                cache.put(dept.getStr(DEPT_PCODE), parentDept);
                dept = parentDept;
            }
        }
        return dept;
    }

    /**
     * putChild考场安排
     *
     * @param dept  父级机构
     * @param child dept or kc（机构或考场）
     * @param first 是否添加到第一个
     */
    private void putChild(Bean dept, Bean child, boolean first) {
        List<Bean> list = dept.getList(CHILD);
        boolean isExist = false;
        if (StringUtils.isNotEmpty(child.getStr("KC_ID"))) {
            //child为考场
            for (Bean bean : list) {
                String kcId = bean.getStr("KC_ID");
                if (kcId.equals(child.get("KC_ID"))) {
                    isExist = true;
                }
            }
        } else if (StringUtils.isNotEmpty(child.getStr("DEPT_CODE"))) {
            //child为dept
            for (Bean bean : list) {
                String deptCode = bean.getStr("DEPT_CODE");
                if (deptCode.equals(child.get("DEPT_CODE"))) {
                    isExist = true;
                }
            }
        }
        if (!isExist) {
            //不存在，添加（避免重复添加）
            if (first) {
                list.add(0, child);
            } else {
                list.add(child);
            }
        }
        dept.set(CHILD, list);
    }

    /**
     * 根据deptCode获取机构
     *
     * @param code
     * @param cache
     * @return
     */
    private Bean getDeptByCode(String code, Map<String, Bean> cache) {
        Bean result;
        if (cache == null || cache.get(code) == null) {
            result = new Bean(ServDao.find("SY_ORG_DEPT", code));
        } else {
            result = cache.get(code);
        }
        return result;
    }

    /**
     * 获取用户的一级机构、二级机构、三级机构、四级机构
     *
     * @param userCode userCode
     * @return Bean {org1:'',org2:'',org3:'',org4:''}
     */
    private Bean getUserOrg(String userCode) {
        //一级机构  DEPT_LEVEL = '2'
        Bean result = new Bean();
        result.set("org4", "");
        result.set("org3", "");
        result.set("org2", "");
        result.set("org1", "");

        UserBean userBean = UserMgr.getUser(userCode);
        String deptCode = userBean.getDeptCode();
        List<Object> values = new ArrayList<>();
        values.add(deptCode);
        Bean bean = Transaction.getExecutor().queryOne("select * from TS_ODEPT_LEVEL_V where DEPT_CODE = ?", values);
        String codePath = bean.getStr("CODE_PATH");

        String[] splits = codePath.split("\\^");//0010100000^0020000000^0020000087^
        int length = splits.length;
        if (length > 4) {
            String split = splits[4];
            Bean deptByCode = this.getDeptByCode(split, null);
            String deptName = deptByCode.getStr("DEPT_NAME");
            result.set("org4", deptName);
        }
        if (length > 3) {
            String split = splits[3];
            Bean deptByCode = this.getDeptByCode(split, null);
            String deptName = deptByCode.getStr("DEPT_NAME");
            result.set("org3", deptName);
        }
        if (length > 2) {
            String split = splits[2];
            Bean deptByCode = this.getDeptByCode(split, null);
            String deptName = deptByCode.getStr("DEPT_NAME");
            result.set("org2", deptName);
        }
        if (length > 1) {
            String split = splits[1];
            Bean deptByCode = this.getDeptByCode(split, null);
            String deptName = deptByCode.getStr("DEPT_NAME");
            result.set("org1", deptName);
        }
        return result;
    }

}
