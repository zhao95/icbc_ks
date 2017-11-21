package com.rh.ts.xmgl;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.comm.ConfMgr;
import com.rh.core.icbc.basedata.KSSendTipMessageServ;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.*;
import com.rh.core.serv.bean.PageBean;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;
import com.rh.ts.pvlg.PvlgUtils;
import com.rh.ts.util.TsConstant;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DapccServ extends CommonServ {

    private final static String CHILD = "CHILD";

    protected void beforeQuery(ParamBean paramBean) {
        ParamBean param = new ParamBean();
        String ctlgModuleName = "EXAM_ROOM";
        param.set("paramBean", paramBean);
        param.set("ctlgModuleName", ctlgModuleName);
        param.set("serviceName", paramBean.getServId());
        PvlgUtils.setOrgPvlgWhere(param);
    }


    /**
     * 根据条件 获取考生信息
     *
     * @param paramBean paramBean
     * @return outBean
     */
    public OutBean getKsContent(ParamBean paramBean) {
        OutBean outBean = new OutBean();

        /*分页参数处理*/
        PageBean page = paramBean.getQueryPage();
        int rowCount = paramBean.getShowNum(); //通用分页参数优先级最高，然后是查询的分页参数
        if (rowCount > 0) { //快捷参数指定的分页信息，与finds方法兼容
            page.setShowNum(rowCount); //从参数中获取需要取多少条记录，如果没有则取所有记录
            page.setNowPage(paramBean.getNowPage());  //从参数中获取第几页，缺省为第1页
        } else {
            if (!page.contains(Constant.PAGE_SHOWNUM)) { //初始化每页记录数设定
                if (paramBean.getQueryNoPageFlag()) { //设定了不分页参数
                    page.setShowNum(0);
                } else { //没有设定不分页，取服务设定的每页记录数
                    page.setShowNum(50);
                }
            }
        }

        /*拼sql并查询*/
        String searchDeptCode = paramBean.getStr("searchDeptCode");
        String searchKcId = paramBean.getStr("searchKcId");
        String searchSjId = paramBean.getStr("searchSjId");

        long l;//选中的考场时长
        try {
            Bean sjBean = ServDao.find(TsConstant.SERV_KCAP_CCSJ, searchSjId);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String sjStart = sjBean.getStr("SJ_START");
            String sjEnd = sjBean.getStr("SJ_END");
            Date startDate = dateFormat.parse(sjStart);
            Date endDate = dateFormat.parse(sjEnd);
            l = (endDate.getTime() - startDate.getTime()) / 1000 / 60;
        } catch (ParseException e) {
            l = Long.MAX_VALUE;
        }
        paramBean.set("searchKsTime", "");

//        if (hashSet.contains(searchDeptCode)) {
//            paramBean.set("containDeptCode", searchDeptCode);
//        } else {
//            paramBean.set("equalDeptCode", searchDeptCode);
//        }
//

        if (StringUtils.isNotBlank(searchDeptCode) && !"jk".equals(searchDeptCode)) {
            Bean dept = this.getDeptByCode(searchDeptCode, null);
            String deptLevel = dept.getStr("DEPT_LEVEL");
            if ("3".equals(deptLevel) || "2".equals(deptLevel) || "1".equals(deptLevel)) {
                paramBean.set("containDeptCode", searchDeptCode);
            } else {
                paramBean.set("equalDeptCode", searchDeptCode);
            }
        }

        //拼凑whereSql配置
        Map<String, String> configMap = new HashMap<String, String>();
        configMap.put("XM_ID", "XM_ID = ?");
        configMap.put("containDeptCode", "c.CODE_PATH like ?");
//        configMap.put("containDeptCode", "CODE_PATH like ?");
        configMap.put("equalDeptCode", "b.DEPT_CODE = ?");
        configMap.put("searchName", "USER_NAME like ?");
        configMap.put("searchLoginName", "USER_LOGIN_NAME like ?");
        configMap.put("searchBmXl", "BM_XL like ?");
        configMap.put("searchBmMk", "BM_MK like ?");
        configMap.put("searchBmJb", "BM_TYPE = ?");
//        configMap.put("searchJkCodePath", " a.JK_ODEPT is not null and a.JK_ODEPT !='' " +
//                " and ? like CONCAT('%',substring(d.CODE_PATH , 12, 10),'%')");
//        configMap.put("searchKsTime", "CAST(BM_KS_TIME as SIGNED) < ?");
//        configMap.put("searchBmCount", "count = ?");

        @SuppressWarnings("rawtypes")
		List extWhereSqlData = this.getExtWhereSqlData(paramBean, configMap);
        @SuppressWarnings("unchecked")
		List<Object> values = (List<Object>) extWhereSqlData.get(1);
        String whereSql = (String) extWhereSqlData.get(0);

        //查找借考考生数据
        if ("jk".equals(searchDeptCode)) {
            String searchJkCodePath = "";
            String sql = "SELECT b.CODE_PATH FROM TS_KCGL a " +
                    "LEFT JOIN sy_org_dept b ON b.DEPT_CODE = a.KC_ODEPTCODE WHERE KC_ID = ?";
            List<Object> values1 = new LinkedList<Object>();
            values1.add(searchKcId);
            List<Bean> beanList = Transaction.getExecutor().query(sql, values1);
            //substring(b.CODE_PATH , 12, 10) 获取
            if (beanList != null && beanList.size() > 0) {
                Bean bean = beanList.get(0);
                searchJkCodePath = bean.getStr("CODE_PATH");
                values.add(searchJkCodePath);
                whereSql += " and a.JK_ODEPT is not null and a.JK_ODEPT !='' " +
                        " and ? like CONCAT('%',substring(d.CODE_PATH , 12, 10),'%')";
//                paramBean.set("searchJkCodePath", searchJkCodePath);
            }
        } else {
            //获取的考生 在考场关联机构本级及下级的机构
            //*EXISTS
            whereSql += " AND EXISTS (select '' from TS_KCGL_GLJG g where g.KC_ID =? and INSTR(c.CODE_PATH ,g.JG_CODE)>0 )";
            values.add(searchKcId);

            //*in
//            whereSql += " and c.CODE_PATH in(select c.CODE_PATH from TS_KCGL_GLJG g where g.KC_ID =? and INSTR(c.CODE_PATH ,g.JG_CODE)>0 )";
//            values.add(searchKcId);

            //*deptSql
//            StringBuilder deptSql = new StringBuilder();//" or CODE_PATH like ?";
//            Set<String> hashSet = this.getKcRelateOrgCodeList(searchKcId);
//            for (String s : hashSet) {
//                values.add(s);
//                deptSql.append("INSTR(c.CODE_PATH ,?)>0 or ");
//            }
//            whereSql += " and (" + deptSql.toString().substring(0, deptSql.toString().length() - 3) + ")";
        }
//        Pair<String, List<Object>> extWhereSqlData

        //考试时长
        whereSql += " and CAST(BM_KS_TIME as SIGNED) <= ?";
        values.add(l);

        //默认获取未安排的人员,isArrange = 'false'  获取安排人员
        if (!"false".equals(paramBean.getStr("isArrange"))) {
            //默认获取未安排的人员
            whereSql += " and not exists(select 'X' from TS_XMGL_KCAP_YAPZW where SH_ID=a.SH_ID)";
        }

        configMap.put("isArrange", " not exists(select 'X' from TS_XMGL_KCAP_YAPZW where SH_ID=a.SH_ID) ");


        String sql = "select a.*,b.USER_NAME,b.USER_LOGIN_NAME,b.DEPT_CODE,c.CODE_PATH"
                + ",(select COUNT(*) from TS_BMSH_PASS a2 where a2.BM_CODE=a.BM_CODE and a2.XM_ID=a.XM_ID AND a2.BM_STATUS NOT IN ('1', '3') ) as count" +
                ",(case a.BM_STATUS when '2' then '借考' else '' end) as status "
                + "from TS_BMSH_PASS a "
                + "left join SY_ORG_USER b on a.BM_CODE = b.USER_CODE "
                + "LEFT JOIN SY_ORG_DEPT c ON b.DEPT_CODE = c.DEPT_CODE "
                + "LEFT JOIN SY_ORG_DEPT d ON d.DEPT_CODE = a.JK_ODEPT "
                + "where a.BM_STATUS not in('1','2','3')"
                + whereSql;
        /*not exists(select 'X' from TS_XMGL_KCAP_YAPZW where SH_ID=a.SH_ID) "
                + " and */
        //where 姓名/登录名/报考类型/报考数  bm_name /login_name?/
        List<Bean> dataList = Transaction.getExecutor().queryPage(
                sql, page.getNowPage(), page.getShowNum(), new ArrayList<Object>(values), null);
//        List<Bean> beanList = ServDao.finds("TS_XMGL_KCAP_DFPKS", paramBean);
        for (Bean bean : dataList) {
            String userCode = bean.getStr("BM_CODE");
            ParamBean userCodeParamBean = new ParamBean();
            userCodeParamBean.set("userCode", userCode);
            bean.putAll(getUserOrg(userCodeParamBean));
        }

        /*设置数据总数*/
        int count = dataList.size();
        int showCount = page.getShowNum();
        boolean bCount; //是否计算分页
        if ((showCount == 0) || paramBean.getQueryNoPageFlag()) {
            bCount = false;
        } else {
            bCount = true;
        }
        if (bCount) { //进行分页处理
            if (!page.contains(Constant.PAGE_ALLNUM)) { //如果有总记录数就不再计算
                int allNum;
                if ((page.getNowPage() == 1) && (count < showCount)) { //数据量少，无需计算分页
                    allNum = count;
                } else {
                    allNum = Transaction.getExecutor().count(sql, values);
                }
                page.setAllNum(allNum);
            }
            outBean.setCount(page.getAllNum()); //设置为总记录数
        } else {
            outBean.setCount(dataList.size());
        }
        outBean.setData(dataList);
        outBean.setPage(page);
        return outBean;
    }

    /**
     * 拼接whereSql
     *
     * @param paramBean paramBean
     * @param configMap :{"XM_ID", "XM_ID = ?","searchName", "USER_NAME like ?"}
     * @return List  0;whereSql 1:values
     */
    private List<Object> getExtWhereSqlData(ParamBean paramBean, Map<String, String> configMap) {
        StringBuilder whereSql = new StringBuilder(/*"where "*/);
        List<Object> values = new ArrayList<Object>();
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
        List<Object> result = new ArrayList<Object>();
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
        String roleDeptCode = paramBean.getStr("deptCodeStr");
        List<Object> values = new ArrayList<Object>();
        values.add(xmId);

        //根据用户权限code（deptCodeStr）过滤考场
        String[] splitDeptCode = roleDeptCode.split(",");
        StringBuilder deptBuilder = new StringBuilder("and ( ");
        for (String deptCode : splitDeptCode) {
            values.add("%" + deptCode + "%");
            deptBuilder.append(" e.CODE_PATH like ? ").append("or ");
        }
        String deptSql = deptBuilder.substring(0, deptBuilder.length() - 3) + ")";

        //查询出所有有权限的考场
        // xmId:该项目 a待安排考场 a.XM_ID
        // roleDeptCode:当前用户所拥有的权限  e考场所属机构 e.CODE_PATH like %roleDeptCode%
        //b考场信息 c考场关联机构 d考场关联机构信息
        List<Bean> list = Transaction.getExecutor().query("select a.*,d.DEPT_CODE,b.KC_ODEPTCODE from TS_XMGL_KCAP_DAPCC a " +
                "INNER JOIN TS_KCGL b on b.KC_ID =a.KC_ID " +
                "INNER JOIN TS_KCGL_GLJG c on c.KC_ID=a.KC_ID " +
                "LEFT JOIN SY_ORG_DEPT d on d.DEPT_CODE = c.JG_CODE " +
                "LEFT JOIN SY_ORG_DEPT e on b.KC_ODEPTCODE = e.DEPT_CODE " +
                "where a.XM_ID=? " + deptSql, values);
        Set<String> hashSet = new HashSet<String>();
        for (Bean bean : list) {
            hashSet.add(bean.getStr(DEPT_CODE));
        }

        Map<String, Bean> cache = new HashMap<String, Bean>();
        Bean rootDeptBean = this.getDeptList(hashSet, cache);

//        List<Bean> list = ServDao.finds("TS_XMGL_KCAP_DAPCC", "and XM_ID='" + xmId + "'");
        for (Bean item : list) {
            String ccId = item.getId();
            ParamBean queryParamBean = new ParamBean();
            queryParamBean.set(Constant.PARAM_WHERE, "and CC_ID = '" + ccId + "'");
            queryParamBean.set(Constant.PARAM_ORDER, " SJ_START");
            List<Bean> list2 = ServDao.finds("TS_XMGL_KCAP_DAPCC_CCSJ", queryParamBean);
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
        Set<String> hashSet = this.getKcRelateOrgCodeList(kcId);
        Bean rootDeptBean = getRootDeptBean(hashSet);
        outBean.putAll(rootDeptBean);
        return outBean;
    }

    public OutBean getOrgTreeByDeptCode(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        String deptCodeStr = paramBean.getStr("deptCodeStr");
        String[] codes = deptCodeStr.split(",");
        /*Set<String> hashSet =*/
        List<String> codeStrings = Arrays.asList(codes);
        Bean rootDeptBean = getRootDeptBean(new HashSet<String>(codeStrings));
        outBean.putAll(rootDeptBean);
        return outBean;
    }

    /**
     * 判断当前用户是否可以发布场次安排
     *
     * @param paramBean 当前登录人发布权限编码（多个机构编码逗号隔开）和项目id
     * @return "true" "false"
     */
    public OutBean getCanPublish(ParamBean paramBean) {
        String canPublish = "false";
        String xmId = paramBean.getStr("XM_ID");
        String userYAPPublishCode = paramBean.getStr("UserYAPPublishCode");

        if (StringUtils.isNotBlank(userYAPPublishCode)) {//不为空，为空则没有权限
            //如果用户的发布权限的编码 包括项目所属机构，则有发布权限 （通过sql  from sy_org_dept where DEPT_CODE =? and (CODE_PATH like ? or CODE_PATH like ? ...) ）
            Bean xmBean = ServDao.find(TsConstant.SERV_XMGL, xmId);
            String xmFqdwCode = xmBean.getStr("XM_FQDW_CODE");
            String sql = "select * from sy_org_dept where DEPT_CODE =? ";
            StringBuilder deptSql = new StringBuilder();
            List<Object> values = new ArrayList<Object>();
            values.add(xmFqdwCode);
            for (String s : userYAPPublishCode.split(",")) {
                if (StringUtils.isNotBlank(s)) {
                    values.add("%" + s + "%");
                    deptSql.append("CODE_PATH like ? or ");
                }
            }
            String whereSql = " and (" + deptSql.toString().substring(0, deptSql.toString().length() - 3) + ")";
            List<Bean> beanList = Transaction.getExecutor().query(sql + whereSql, values);
            if (beanList != null && beanList.size() > 0) {
                canPublish = "true";
            }
        }
        OutBean outBean = new OutBean();
        outBean.set("canPublish", canPublish);
        return outBean;
    }

    /**
     * 显示提交还是发布
     *
     * @param paramBean XM_ID
     * @return "tj" "publish"
     */
    public OutBean getTjOrPublish(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        String xmId = paramBean.getStr("XM_ID");
        String deptCode = Context.getUserBean().getDeptCode();
        Bean xmBean = ServDao.find(TsConstant.SERV_XMGL, xmId);
        String xmFqdwCode = xmBean.getStr("XM_FQDW_CODE");
        String sql = "select * from sy_org_dept where DEPT_CODE =? and CODE_PATH like ?";
        List<Object> values = new ArrayList<Object>();
        values.add(xmFqdwCode);
        values.add("%" + deptCode + "%");
        List<Bean> beanList = Transaction.getExecutor().query(sql, values);
        if (beanList != null && beanList.size() > 0) {
            //用户机构 属于 项目所属机构及以上机构，则用户显示发布按钮
            outBean.set("type", "publish");
        } else {
            outBean.set("type", "tj");
        }
        return outBean;
    }

    private Bean getRootDeptBean(Set<String> deptCodeList) {
        Map<String, Bean> cache = new HashMap<String, Bean>();
        Bean rootDeptBean = this.getDeptList(deptCodeList, cache);
        for (String deptCode : deptCodeList) {
            ParamBean queryBean = new ParamBean();
            queryBean.set("DICT_ID", "SY_ORG_ODEPT_ALL");
            queryBean.set("PID", deptCode);
            OutBean bean = ServMgr.act("SY_COMM_INFO", "dict", queryBean);
            List<Bean> child = bean.getList("CHILD");
            for (Bean item : child) {
                this.putChild(cache.get(deptCode), item, false);
            }
        }
        return rootDeptBean;
    }

    /**
     * 获取考场关联机构的编码
     *
     * @param kcId kcId
     * @return
     */
    private Set<String> getKcRelateOrgCodeList(String kcId) {
        List<Object> values = new ArrayList<Object>();
        values.add(kcId);
        List<Bean> list = Transaction.getExecutor().query("select a.*, c.JG_CODE from TS_XMGL_KCAP_DAPCC a " +
//                "INNER JOIN TS_KCGL b on b.KC_ID =a.KC_ID " +
                "INNER JOIN TS_KCGL_GLJG c on c.KC_ID=a.KC_ID " +
//                "LEFT JOIN SY_ORG_DEPT d on d.DEPT_CODE = c.JG_CODE " +
                "where a.KC_ID=?", values);
        Set<String> hashSet = new HashSet<String>();
        for (Bean bean : list) {
            hashSet.add(bean.getStr("JG_CODE"));
        }
        return hashSet;
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
     * @param paramBean paramBean  userCode
     * @return Bean {org1:'',org2:'',org3:'',org4:''}
     */
    public OutBean getUserOrg(ParamBean paramBean) {
        //一级机构  DEPT_LEVEL = '2'
        String userCode = paramBean.getStr("userCode");
        OutBean result = new OutBean();
        result.set("org4", "");
        result.set("org3", "");
        result.set("org2", "");
        result.set("org1", "");

        UserBean userBean = UserMgr.getUser(userCode);
        String deptCode = userBean.getDeptCode();

//        List<Object> values = new ArrayList<Object>();
//        values.add(deptCode);

        Bean bean = ServDao.find("SY_ORG_DEPT", deptCode);
//        Bean bean = Transaction.getExecutor().queryOne("select * from sy_org_dept where DEPT_CODE = ?", values);
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


    /**
     * 更改场次
     *
     * @param paramBean sjId sjId2
     * @return outBean
     */
    public OutBean changeCc(ParamBean paramBean) {
        OutBean outBean = new OutBean();

        String sjId = paramBean.getStr("sjId");
        String sjId2 = paramBean.getStr("sjId2");
        Bean sjBean = ServDao.find(TsConstant.SERV_KCAP_CCSJ, sjId);
        Bean sjBean2 = ServDao.find(TsConstant.SERV_KCAP_CCSJ, sjId2);
        if (sjBean == null || sjBean2 == null) {
            outBean.setError("操作失败！");
        } else if (sjBean.getStr("CC_ID").equals(sjBean2.getStr("CC_ID"))) {
            Transaction.begin();
            try {
                String tempId = "changeCCTempId";
                String sql = "update ts_xmgl_kcap_yapzw set SJ_ID = ? where SJ_ID= ?";
                List<Object> values = new LinkedList<Object>();

                //
                values.add(tempId);
                values.add(sjId);
                Transaction.getExecutor().execute(sql, values);
                //
                values.clear();
                values.add(sjId);
                values.add(sjId2);
                Transaction.getExecutor().execute(sql, values);
                //
                values.clear();
                values.add(sjId2);
                values.add(tempId);
                Transaction.getExecutor().execute(sql, values);

                Transaction.commit();
            } catch (Exception e) {
                Transaction.rollback();
                outBean.setError("操作失败！");
            } finally {
                Transaction.end();
            }
        } else {
            outBean.setError("考场不同，请重新选择！");
        }
        return outBean;
    }

    /**
     * xngs 辖内公示
     */
    public OutBean xngs(ParamBean paramBean) {
        String xmId = paramBean.getStr("XM_ID");
        String kcIdStr = paramBean.getStr("KC_ID_STR");
        String kcIdSql = "'" + kcIdStr.replaceAll(",", "','") + "'";
        String sql = "update ts_xmgl_kcap_yapzw set PUBLICITY = '1 ' where XM_ID = ? and KC_ID in (" + kcIdSql + ")";
        List<Object> values = new LinkedList<Object>();
        values.add(xmId);
        Transaction.getExecutor().execute(sql, values);

        try {
            //考场公示提醒
            //TS_KCGS_START_TIP	考场公示提示语
            Bean xmBean = ServDao.find(TsConstant.SERV_XMGL, xmId);
            String xmName = xmBean.getStr("XM_NAME");
            String whereSql = "and XM_ID = '" + xmId + "' and KC_ID in (" + kcIdSql + ")";
            List<Bean> beanList = ServDao.finds(TsConstant.SERV_KCAP_YAPZW, whereSql);

            String zkzStartTipMsg = ConfMgr.getConf("TS_KCGS_START_TIP", "您报名的考试开始公示考场，请登录工商银行考试系统查看详情。");
            zkzStartTipMsg = zkzStartTipMsg.replaceAll("#XM_NAME#", xmName);
            List<Bean> kczwShowTipList = new LinkedList<Bean>();
            for (Bean bean : beanList) {
                String userCode = bean.getStr("U_CODE");
                Bean kczwShowTip = new Bean();
                kczwShowTip.set("USER_CODE", userCode);
                kczwShowTip.set("tipMsg", zkzStartTipMsg);
                kczwShowTipList.add(kczwShowTip);
            }
            new KSSendTipMessageServ().sendTipMessageListForICBC(kczwShowTipList, "kczwShow");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("考场公示提醒失败，" + "XM_ID:" + xmId + ",USER_CODE:" + Context.getUserBean().getName());
        }
        return new OutBean().setOk();
    }

    /**
     * 场次发布
     *
     * @param paramBean XM_ID
     * @return outBean
     */
    public OutBean publish(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        String xmId = paramBean.getStr("XM_ID");
        if (StringUtils.isNotBlank(xmId)) {
            Bean updateXmBean = new Bean();
            updateXmBean.setId(xmId);
            updateXmBean.set("XM_ID", xmId);
            updateXmBean.set("XM_KCAP_PUBLISH_USER_CODE", Context.getUserBean().getCode());
            updateXmBean.set("XM_KCAP_PUBLISH_TIME", DateUtils.getDatetime());
            ServDao.update(TsConstant.SERV_XMGL, updateXmBean);

            try {
                //准考证开始打印提醒
                //TS_ZKZ_START_TIP 准考证开始打印提示语
                Bean xmBean = ServDao.find(TsConstant.SERV_XMGL, xmId);
                String xmName = xmBean.getStr("XM_NAME");
                String zkzStartTipMsg = ConfMgr.getConf("TS_ZKZ_START_TIP", "您所报名的考试，已可以打印准考证，祝考试顺利！");
                zkzStartTipMsg = zkzStartTipMsg.replaceAll("#XM_NAME#", xmName);

                List<Bean> zkzStarTipBeanList = new LinkedList<Bean>();
                List<Bean> beanList = ServDao.finds(TsConstant.SERV_KCAP_YAPZW, " and XM_ID = '" + xmId + "'");
                for (Bean bean : beanList) {
                    String userCode = bean.getStr("U_CODE");
                    Bean zkzStarTipBean = new Bean();
                    zkzStarTipBean.set("USER_CODE", userCode);
                    zkzStarTipBean.set("tipMsg", zkzStartTipMsg);
                    zkzStarTipBeanList.add(zkzStarTipBean);
                }
                new KSSendTipMessageServ().sendTipMessageListForICBC(zkzStarTipBeanList, "zkzStar");
            } catch (Exception e) {
                e.printStackTrace();
                log.error("准考证开始打印提醒失败，" + "XM_ID:" + xmId);
            }
//            outBean.setOk();
        } else {
            outBean.setError();
        }
        return outBean;
    }

    /**
     * 清除座位安排
     *
     * @param paramBean KC_ID_STR
     * @return outBean
     */
    public OutBean clearYapzw(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        String kcIdStr = paramBean.getStr("KC_ID_STR");
        String[] split = kcIdStr.split(",");
        StringBuilder whereSql = new StringBuilder();
        List<Object> values = new ArrayList<Object>();
        for (String kcId : split) {
            if (StringUtils.isNotBlank(kcId)) {
                values.add(kcId);
                whereSql.append("?,");
            }
        }
        String substring = whereSql.substring(0, whereSql.length() - 1);
        Bean whereBean = new Bean();
        whereBean.put(Constant.PARAM_PRE_VALUES, values);
        whereBean.put(Constant.PARAM_WHERE, "and KC_ID in (" + substring + ")");
        ServDao.destroy(TsConstant.SERV_KCAP_YAPZW, whereBean);

        return outBean;
    }

    /**
     * 获取机构下考场待安排考生数
     *
     * @return outBean
     */
    public OutBean getDeptKcCount(ParamBean paramBean) {
        OutBean outBean = new OutBean();
      /*  String kcIdStr = paramBean.getStr("KC_ID");
        String[] split = kcIdStr.split(",");
//        bean
*/
        return outBean;
    }
}
