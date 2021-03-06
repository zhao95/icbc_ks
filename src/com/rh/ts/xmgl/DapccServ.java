package com.rh.ts.xmgl;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.comm.ConfMgr;
import com.rh.core.icbc.basedata.KSSendTipMessageServ;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.*;
import com.rh.core.serv.bean.PageBean;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;
import com.rh.core.util.ExpUtils;
import com.rh.ts.pvlg.PvlgUtils;
import com.rh.ts.util.BMUtil;
import com.rh.ts.util.KcUtils;
import com.rh.ts.util.TsConstant;
import org.apache.commons.collections4.CollectionUtils;
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
     * 提供导出Excel
     *
     * @param paramBean 参数信息
     * @return 执行结果
     */
    public OutBean expAll(ParamBean paramBean) {
        /*获取beanList信息*/
        //*设置查询条件
        paramBean.set("isArrange", "false");//获取所有安排和未安排的考生
        paramBean.set(ParamBean.QUERY_NOPAGE_FLAG, "true");

        List<Bean> allList = new ArrayList<Bean>();

        //未安排/已安排人员
        OutBean ksOutBean = this.getKsContent(paramBean);
        List<Bean> ksList = ksOutBean.getDataList();
        for (Bean bean : ksList) {
            bean.set("state", "");
        }
        allList.addAll(ksList);

        //请假人员

        paramBean.set("searchDeptCode", "qj");
        OutBean qjKsOutBean = this.getKsContent(paramBean);
        List<Bean> qjKsList = qjKsOutBean.getDataList();
        for (Bean bean : qjKsList) {
            bean.set("state", "已请假");
        }
        allList.addAll(qjKsList);

        //借考人员
        paramBean.set("searchDeptCode", "jk");
        OutBean jkKsOutBean = this.getKsContent(paramBean);
        List<Bean> jkKsList = jkKsOutBean.getDataList();
        for (Bean bean : jkKsList) {
            bean.set("state", "借入考生");
        }
        allList.addAll(jkKsList);

        //设置其他信息
        for (Bean bean : allList) {

            //设置座位安排信息
            String shId = bean.getStr("SH_ID");
            Bean yapzwBean;
            List<Object> values = new ArrayList<Object>();
            values.add(shId);
            List<Bean> yapzwBeanList = Transaction.getExecutor().query(
                    "select * from ts_xmgl_kcap_yapzw a " +
                            " LEFT JOIN ts_kcgl b ON a.KC_ID = b.KC_ID" +
                            " left join ts_xmgl_kcap_dapcc_ccsj c on c.SJ_ID =a.SJ_ID " +
                            " left join ts_kcgl_zwdyb d on d.ZW_ID = a.ZW_ID " +
                            " where SH_ID=?", values);
            if (CollectionUtils.isNotEmpty(yapzwBeanList)) {
                yapzwBean = yapzwBeanList.get(0);
            } else {
                yapzwBean = new Bean();
            }
            bean.set("KC_NAME", yapzwBean.getStr("KC_NAME"));
            bean.set("KC_ADDRESS", yapzwBean.getStr("KC_ADDRESS"));
            bean.set("BM_KS_TIME", yapzwBean.getStr("BM_KS_TIME"));
            bean.set("SJ_START", yapzwBean.getStr("SJ_START"));
            bean.set("ZW_ZWH_SJ", yapzwBean.getStr("ZW_ZWH_SJ"));

            //设置考试名称
            String examinationName = BMUtil.getExaminationName(bean.getStr("BM_TYPE"), bean.getStr("BM_XL"), bean.getStr("BM_MK"));
            bean.set("ksName", examinationName);
        }

        /*设置导出展示信息*/
        LinkedHashMap<String, String> colMap = new LinkedHashMap<String, String>();
        colMap.put("BM_CODE", "人力资源编码");
        colMap.put("BM_NAME", "姓名");
        colMap.put("USER_LOGIN_NAME", "统一认证号");
        colMap.put("org1", "一级机构");
        colMap.put("org2", "二级机构");
        colMap.put("org3", "三级机构");
        colMap.put("COUNT", "报名数");
        colMap.put("ksName", "考试名称");
        colMap.put("BM_TYPE_NAME", "级别");
        colMap.put("KC_NAME", "考场名称");
        colMap.put("KC_ADDRESS", "考场地址");
        colMap.put("SJ_START", "考试时间");
        colMap.put("BM_KS_TIME", "考试时长");
        colMap.put("ZW_ZWH_SJ", "座位号");
        colMap.put("state", "状态");
        colMap.put("", "承办单位备注");

        return ExpUtils.expUtil(allList, colMap, paramBean);
    }


    /**
     * 根据条件 获取考生信息
     *
     * @param paramBean paramBean searchKcId searchSjId searchDeptCode
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

        //kcId  ,
        String[] kcIdSplit = searchKcId.split(",");
        List<Object> kcIdValues = new ArrayList<Object>();
        StringBuilder kcIdSql = new StringBuilder();
        for (String kcId : kcIdSplit) {
            kcIdSql.append("?,");
            kcIdValues.add(kcId);
        }
        kcIdSql = kcIdSql.insert(0, "'',");
        kcIdSql = new StringBuilder(kcIdSql.substring(0, kcIdSql.length() - 1));

        //选中的考场时长
        long l = Long.MAX_VALUE;
        try {
            if (StringUtils.isNotBlank(searchSjId)) {
                Bean sjBean = ServDao.find(TsConstant.SERV_KCAP_CCSJ, searchSjId);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String sjStart = sjBean.getStr("SJ_START");
                String sjEnd = sjBean.getStr("SJ_END");
                Date startDate = dateFormat.parse(sjStart);
                Date endDate = dateFormat.parse(sjEnd);
                l = (endDate.getTime() - startDate.getTime()) / 1000 / 60;
            }
        } catch (ParseException e) {
            l = Long.MAX_VALUE;
        } catch (NullPointerException e) {
            l = Long.MAX_VALUE;
        }
//        paramBean.set("searchKsTime", l);

//        if (hashSet.contains(searchDeptCode)) {
//            paramBean.set("containDeptCode", searchDeptCode);
//        } else {
//            paramBean.set("equalDeptCode", searchDeptCode);
//        }
//

        if (StringUtils.isNotBlank(searchDeptCode) && !"jk".equals(searchDeptCode) && !"qj".equals(searchDeptCode)) {
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
        configMap.put("equalDeptCode", "a.S_DEPT = ?");
        configMap.put("searchName", "USER_NAME like ?");
        configMap.put("searchLoginName", "USER_LOGIN_NAME like ?");
        configMap.put("searchBmXl", "BM_XL like ?");
        configMap.put("searchBmMk", "BM_MK like ?");
        configMap.put("searchBmJb", "BM_TYPE = ?");
//        configMap.put("searchJkCodePath", " a.JK_ODEPT is not null and a.JK_ODEPT !='' " +
//                " and ? like CONCAT('%',substring(d.CODE_PATH , 12, 10),'%')");
//        configMap.put("searchKsTime", "CAST(BM_KS_TIME as SIGNED) < ?");
        configMap.put("searchBmCount", "count = ?");

        String extraWhereSql = "";
        List<Object> extraValues = new ArrayList<Object>();
        //查找借考考生数据
        if ("jk".equals(searchDeptCode)) {
            l = Long.MAX_VALUE;
            String sql = "SELECT b.CODE_PATH FROM TS_KCGL a " +
                    "LEFT JOIN sy_org_dept b ON b.DEPT_CODE = a.KC_ODEPTCODE WHERE KC_ID in(" + kcIdSql + ")";
            List<Object> values1 = new LinkedList<Object>();
            values1.addAll(kcIdValues);
//            values1.add(searchKcId);
            List<Bean> beanList = Transaction.getExecutor().query(sql, values1);
            //substring(b.CODE_PATH , 12, 10) 获取
            if (CollectionUtils.isNotEmpty(beanList)) {
                StringBuilder jkODeptSql = new StringBuilder();

                for (Bean bean : beanList) {
                    jkODeptSql.append(" ? like CONCAT('%',a.JK_ODEPT,'%') or");
                    extraValues.add(bean.getStr("CODE_PATH"));
                }
                extraWhereSql += " and a.BM_STATUS in('2')";
                extraWhereSql += " and a.JK_ODEPT is not null and a.JK_ODEPT !='' " +
                        " and (" + jkODeptSql.substring(0, jkODeptSql.length() - 2) + ")";//substring(d.CODE_PATH , 12, 10)
                //select CODE_PATH,substring(CODE_PATH , LOCATE('^',CODE_PATH,1)+1, LOCATE('^',CODE_PATH,LOCATE('^',CODE_PATH,1)+1)-LOCATE('^',CODE_PATH,1)-1) from sy_org_dept where CODE_PATH is not null;
//                paramBean.set("searchJkCodePath", searchJkCodePath);
            } else {
                extraWhereSql += " and 1 = 2";
            }
        } else if ("qj".equals(searchDeptCode)) {
            //请假数据 不考虑时长
            l = Long.MAX_VALUE;
            extraWhereSql += " and a.BM_STATUS in('1','3')";
            paramBean.set("isArrange", "false");//请假数据获取所有，不考虑是否安排
            extraWhereSql += " AND EXISTS (" +
                    "   select '' from ts_xmgl_kcap_gljg g where g.KC_ID in(" + kcIdSql + ") " +
                    "       AND (" +
                    "       ( INSTR(c.CODE_PATH, g.JG_CODE) > 0 AND g.JG_TYPE = 2)" +
                    "        OR " +
                    "       ( c.ODEPT_CODE = g.JG_CODE  AND g.JG_TYPE = 1 ) " +
                    "       )" +
                    ")";
            extraValues.addAll(kcIdValues);
//            extraValues.add(searchKcId);
        } else {
            //获取的考生 在考场关联机构本级及下级的机构
            //*EXISTS
            extraWhereSql += " and a.BM_STATUS not in('1','2','3')";
            extraWhereSql += " AND EXISTS (" +
                    "   select '' from ts_xmgl_kcap_gljg g where g.KC_ID in(" + kcIdSql + ") " +
                    "       AND (" +
                    "       ( INSTR(c.CODE_PATH, g.JG_CODE) > 0 AND g.JG_TYPE = 2)" +
                    "        OR " +
                    "       ( c.ODEPT_CODE = g.JG_CODE  AND g.JG_TYPE = 1 ) " +
                    "       )" +
                    ")";
            extraValues.addAll(kcIdValues);
//            extraValues.add(searchKcId);

            //*in
//            extraWhereSql += " and c.CODE_PATH in(select c.CODE_PATH from ts_xmgl_kcap_gljg g where g.KC_ID =? and INSTR(c.CODE_PATH ,g.JG_CODE)>0 )";
//            values.add(searchKcId);

            //*deptSql
//            StringBuilder deptSql = new StringBuilder();//" or CODE_PATH like ?";
//            Set<String> hashSet = this.getKcRelateOrgCodeList(searchKcId);
//            for (String s : hashSet) {
//                extraValues.add(s);
//                deptSql.append("INSTR(c.CODE_PATH ,?)>0 or ");
//            }
//            extraWhereSql += " and (" + deptSql.toString().substring(0, deptSql.toString().length() - 3) + ")";
        }
//        Pair<String, List<Object>> extWhereSqlData


        //默认获取未安排的人员,isArrange = 'false'  获取安排人员
        if (!"false".equals(paramBean.getStr("isArrange"))) {
            //默认获取未安排的人员
            extraWhereSql += " and not exists(select 'X' from TS_XMGL_KCAP_YAPZW where SH_ID=a.SH_ID)";
        }

        @SuppressWarnings("rawtypes")
        List extWhereSqlData = this.getExtWhereSqlData(paramBean, configMap);
        @SuppressWarnings("unchecked")
        List<Object> values = (List<Object>) extWhereSqlData.get(1);
        String whereSql = (String) extWhereSqlData.get(0);

        //考试时长
        whereSql += " and CAST(BM_KS_TIME as SIGNED) <= ?";
        values.add(l);
//        configMap.put("isArrange", " not exists(select 'X' from TS_XMGL_KCAP_YAPZW where SH_ID=a.SH_ID) ");
        whereSql += extraWhereSql;
        values.addAll(extraValues);

        String sql = "select a.*,b.USER_NAME,b.USER_LOGIN_NAME,b.DEPT_CODE,c.CODE_PATH"
                + ",countt.count"
//                + ",(select COUNT(*) from TS_BMSH_PASS a2 where a2.BM_CODE=a.BM_CODE and a2.XM_ID=a.XM_ID AND a2.BM_STATUS NOT IN ('1', '3') ) as count" +
                + ",(case a.BM_STATUS when '2' then '借考' else '' end) as status "
                + "from TS_BMSH_PASS a "
                + " LEFT JOIN (SELECT BM_CODE,count(*) AS count FROM ts_bmsh_pass a2 WHERE a2.XM_ID = ? AND a2.BM_STATUS NOT IN ('1', '3') GROUP BY BM_CODE) countt ON countt.BM_CODE = a.BM_CODE"
                + " left join SY_ORG_USER b on b.USER_CODE = a.BM_CODE "
                + " LEFT JOIN SY_ORG_DEPT c ON c.DEPT_CODE = a.S_DEPT "
                + " LEFT JOIN SY_ORG_DEPT d ON d.DEPT_CODE = a.JK_ODEPT "
                + " where 1=1 "
                + whereSql + " order by count desc,a.BM_CODE";//,c.DEPT_CODE
        values.add(0, paramBean.getStr("XM_ID"));
        /*not exists(select 'X' from TS_XMGL_KCAP_YAPZW where SH_ID=a.SH_ID) "
                + " and */
        //where 姓名/登录名/报考类型/报考数  bm_name /login_name?/
        List<Bean> dataList = Transaction.getExecutor().queryPage(
                sql, page.getNowPage(), page.getShowNum(), new ArrayList<Object>(values), null);
//        List<Bean> beanList = ServDao.finds("TS_XMGL_KCAP_DFPKS", paramBean);
        for (Bean bean : dataList) {
            //添加 用户机构信息
            String userCode = bean.getStr("BM_CODE");
            ParamBean userCodeParamBean = new ParamBean();
            userCodeParamBean.set("userCode", userCode);
            bean.putAll(getUserOrg(userCodeParamBean));
        }

        String countSql = "select count(*) as count " + sql.substring(sql.indexOf("from TS_BMSH_PASS a "), sql.indexOf("order by"));
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
                    allNum = Transaction.getExecutor().queryOne(countSql, values).getInt("COUNT");
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
     * @param paramBean paramBean xmId deptCodeStr
     * @return
     */
    public OutBean getKcAndCc(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        String DEPT_CODE = "KC_ODEPTCODE";//KC_ODEPTCODE：根据考场所属机构挂靠考场；DEPT_CODE：根据关联机构挂靠考场
        String xmId = paramBean.getStr("xmId");
        String roleDeptCode = paramBean.getStr("deptCodeStr");

        List<Bean> list = KcUtils.getKcList(xmId, roleDeptCode);
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
     * @param paramBean XM_ID deptCodeStr
     * @return "tj" "publish"
     */
    public OutBean getTjOrPublish(ParamBean paramBean) {
        OutBean outBean = new OutBean();

        String deptCodeStr = paramBean.getStr("deptCodeStr");//用户场次安排权限
//        String deptCode = Context.getUserBean().getODeptCode();
        String xmId = paramBean.getStr("XM_ID");

        Bean xmBean = ServDao.find(TsConstant.SERV_XMGL, xmId);
        String xmFqdwCode = xmBean.getStr("XM_FQDW_CODE");//项目所属机构
        String sql = "select * from sy_org_dept where DEPT_CODE =? ";
        List<Object> values = new ArrayList<Object>();
        values.add(xmFqdwCode);

        //deptSql
        String[] split = deptCodeStr.split(",");
        StringBuilder deptSql = new StringBuilder();//" or CODE_PATH like ?";
        for (String s : split) {
            if (StringUtils.isNotBlank(s)) {
                values.add(s);
                deptSql.append("INSTR(CODE_PATH ,?)>0 or ");
            }
        }
        sql += " and (" + deptSql.toString().substring(0, deptSql.toString().length() - 3) + ")";

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
            queryBean.set("DICT_ID", "TS_ORG_DEPT_ALL");
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
     * @return set
     */
    private Set<String> getKcRelateOrgCodeList(String kcId) {
        List<Object> values = new ArrayList<Object>();
        values.add(kcId);
        List<Bean> list = Transaction.getExecutor().query("select a.*, c.JG_CODE from TS_XMGL_KCAP_DAPCC a " +
//                "INNER JOIN TS_KCGL b on b.KC_ID =a.KC_ID " +
                "INNER JOIN ts_xmgl_kcap_gljg c on c.KC_ID=a.KC_ID " +
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
            result = new Bean(OrgMgr.getDept(code));
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

        Bean bean = OrgMgr.getDept(deptCode);
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
     *
     * @param paramBean {XM_ID, KC_ID_STR(没有取当前登录人权限范围内的考场)}
     * @return outBean
     */
    public OutBean xngs(ParamBean paramBean) {

        //
        String xmId = paramBean.getStr("XM_ID");
        String kcIdStr = paramBean.getStr("KC_ID_STR");

        if (StringUtils.isBlank(kcIdStr)) {
            kcIdStr = KcUtils.getKcIdStr(xmId);
        }

        //update ts_xmgl_kcap_yapzw PUBLICITY
        String kcIdSql = "'" + kcIdStr.replaceAll(",", "','") + "'";
        String sql = "update ts_xmgl_kcap_yapzw set PUBLICITY = '1' where XM_ID = ? and KC_ID in (" + kcIdSql + ")";
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
     * 提交考场安排
     *
     * @param paramBean XM_ID DEPT_CODES KC_ID_STR
     * @return outBean
     */
    public OutBean submit(ParamBean paramBean) {

        String xmId = paramBean.getStr("XM_ID");
        List<String> deptCodes = paramBean.getList("DEPT_CODES");
        String kcIdStr = paramBean.getStr("KC_ID_STR");

        for (String deptCode : deptCodes) {
            ParamBean whereBean = new ParamBean();
            whereBean.setWhere(" and TJ_DEPT_CODE ='" + deptCode + "' and XM_ID ='" + xmId + "'");
            List<Bean> beanList = ServDao.finds("TS_XMGL_KCAP_TJJL", whereBean);
            if (StringUtils.isNotBlank(deptCode)) {
                if (beanList == null || beanList.size() <= 0) {
                    Bean dataBean = new Bean();
                    dataBean.set("XM_ID", xmId);
                    dataBean.set("TJ_DEPT_CODE", deptCode);
                    ServDao.create("TS_XMGL_KCAP_TJJL", dataBean);
                }
            }
        }

        //set PUBLICITY = '1'
        String kcIdSql = "'" + kcIdStr.replaceAll(",", "','") + "'";
        String sql = "update ts_xmgl_kcap_yapzw set IS_SUBMIT = '1' where XM_ID = ? and KC_ID in (" + kcIdSql + ")";
        List<Object> values = new LinkedList<Object>();
        values.add(xmId);
        Transaction.getExecutor().execute(sql, values);

        return new OutBean();
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

        //删除考场安排 脏数据
        String sql = "delete from ts_xmgl_kcap_yapzw " +
                "where YAPZW_ID in ( " +
                "   SELECT t.yapzw_id from ( " +
                "       select a.yapzw_id from ts_xmgl_kcap_yapzw a left join ts_xmgl_kcap_dapcc_ccsj b on a.SJ_ID =b.SJ_ID where b.SJ_ID is null " +
                "   ) t " +
                ")";
        Transaction.getExecutor().execute(sql);

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
     * 根据考场获取未安排人员数/考场考生总数
     *
     * @param paramBean {XM_ID, KC_ID(kcIdStr)}
     * @return outBean
     */
    public OutBean getDeptKcCount(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        String count = "0", remainCount = "0";
        String xmId = paramBean.getStr("XM_ID");
        String kcIdStr = paramBean.getStr("KC_ID");
        if (StringUtils.isBlank(kcIdStr)) {
            kcIdStr = KcUtils.getKcIdStr(xmId);
        }

        if (StringUtils.isNotBlank(kcIdStr)) {
            String[] split = kcIdStr.split(",");

            List<Object> kcIdValues = new ArrayList<Object>();
            StringBuilder kcIdSql = new StringBuilder();
            for (String kcId : split) {
                kcIdSql.append("?,");
                kcIdValues.add(kcId);
            }
            kcIdSql = new StringBuilder(kcIdSql.substring(0, kcIdSql.length() - 1));

            String sql = "SELECT count(*) as count from TS_BMSH_PASS a  " +
                    "left join SY_ORG_USER b on a.BM_CODE = b.USER_CODE " +
                    "LEFT JOIN SY_ORG_DEPT c ON c.DEPT_CODE = a.S_DEPT " +
                    "LEFT JOIN SY_ORG_DEPT d ON d.DEPT_CODE = a.JK_ODEPT " +
                    "WHERE xm_id = ? " +
                    "and (" +
                    //考场关联机构考生
                    "EXISTS (select '' from ts_xmgl_kcap_gljg g " +
                    "where g.KC_ID in(" + kcIdSql + ") " +
                    "AND ( (INSTR(c.CODE_PATH, g.JG_CODE) > 0 AND g.JG_TYPE = 2) OR (c.ODEPT_CODE = g.JG_CODE AND g.JG_TYPE = 1) ) " +
                    "and a.BM_STATUS not in('1','2','3')" +
                    ") or (" +
                    //借考的考生
                    " a.BM_STATUS in('2') and a.JK_ODEPT is not null and a.JK_ODEPT !=''" +
                    "and exists(  " +
                    "SELECT b.CODE_PATH FROM TS_KCGL a " +
                    "LEFT JOIN sy_org_dept b ON b.DEPT_CODE = a.KC_ODEPTCODE WHERE KC_ID in(" + kcIdSql + ")  " +
                    "and b.CODE_PATH like CONCAT('%',substring(d.CODE_PATH , 12, 10),'%'))  " +
                    ")" +
                    ")";
            List<Object> values = new ArrayList<Object>();
            values.add(xmId);
            values.addAll(kcIdValues);
            values.addAll(kcIdValues);

            List<Bean> queryList = Transaction.getExecutor().query(sql, values);
            sql += " and not exists(select 'X' from TS_XMGL_KCAP_YAPZW where SH_ID=a.SH_ID)";
            List<Bean> remainCountList = Transaction.getExecutor().query(sql, values);
            count = queryList.get(0).getStr("COUNT");
            remainCount = remainCountList.get(0).getStr("COUNT");
        }
        outBean.set("count", count);
        outBean.set("remainCount", remainCount);
        return outBean;
    }

    /**
     * 查看借考人员
     *
     * @param paramBean deptCodeStr(userPvlgCode)  xmId
     * @return
     */
    public OutBean getJkKsContent(ParamBean paramBean) {
        final String roleDeptCode = paramBean.getStr("deptCodeStr");
        final String xmId = paramBean.getStr("xmId");
        final String[] split = roleDeptCode.split(",");

        //借出（）
//      table  bmshpass
//      user dept-codepath like roleDeptCode
        String outJkSql = "select b.USER_CODE,b.USER_NAME,c.DEPT_NAME,d.DEPT_NAME as jk_dept_name from ts_bmsh_pass a " +
                " left join sy_org_user b on b.USER_CODE = a.BM_CODE " +
                " left join sy_org_dept c on c.DEPT_CODE = a.S_DEPT " +
                " left join sy_org_dept d on d.DEPT_CODE = a.JK_ODEPT " +
                " where a.XM_ID =? " +
                "and a.BM_STATUS in ('2','3') ";
        String outJkhereSql = "";
        List<Object> values = new ArrayList<Object>();
        values.add(xmId);

        //outJkDeptSql
        StringBuilder outJkDeptSql = new StringBuilder();//" or CODE_PATH like ?";
        for (String s : split) {
            if (StringUtils.isNotBlank(s)) {
                values.add(s);
                outJkDeptSql.append("INSTR(c.CODE_PATH ,?)>0 or ");
            }
        }
        outJkhereSql = " and (" + outJkDeptSql.toString().substring(0, outJkDeptSql.toString().length() - 3) + ")";
        outJkSql += outJkhereSql;
        List<Bean> outJkKsList = Transaction.getExecutor().query(outJkSql, values);

        //借入
        List<Bean> inJkKsList;

        String inJkSql = "SELECT b.USER_CODE,b.USER_NAME,d.DEPT_NAME,c.DEPT_NAME as jk_dept_name FROM ts_bmsh_pass a " +
                "LEFT JOIN sy_org_user b ON b.USER_CODE = a.BM_CODE " +
                "LEFT JOIN sy_org_dept c ON c.DEPT_CODE = a.JK_ODEPT " +
                "LEFT JOIN sy_org_dept d ON d.DEPT_CODE = a.S_DEPT " +

                "where c.CODE_PATH is not null " +
                "and a.XM_ID = ? " +
                "and a.BM_STATUS in ('2','3') ";

        List<Object> inJkValues = new ArrayList<Object>();
        inJkValues.add(xmId);

        //获取当前用户权限的所有一级分行
        List<String> yifenhangList = new ArrayList<String>();
        //是否是总行
        boolean isRootDept = false;
        for (String deptCode : split) {
            Bean deptBean = OrgMgr.getDept(deptCode);
            String codePath = deptBean.getStr("CODE_PATH");
            int i1 = codePath.indexOf("^");
            int i2 = codePath.indexOf("^", (i1 + 1));

            if (StringUtils.isNotBlank(codePath)) {
                if (i1 >= 0 && i2 < 0) {
                    //codePath 不为空，且只有一个 ^  -> 为总行
                    isRootDept = true;
                }
            }
            String substring = null;
            if (i2 >= 0) {
                substring = codePath.substring(i1 + 1, i2);
            }
            if (StringUtils.isNotBlank(substring)) {
                yifenhangList.add(substring);
            }
        }

        if (!isRootDept) {
            //该项目的借入人员 （总行）
//        } else {
            //对应一级分行下面的借入人员
            StringBuilder inJkdDeptSql = new StringBuilder();//" or CODE_PATH like ?";
            for (String s : yifenhangList) {
                if (StringUtils.isNotBlank(s)) {
                    inJkValues.add(s);
                    inJkdDeptSql.append("INSTR(c.CODE_PATH ,?)>0 or ");
                }
            }
            String inJkWhereSql = " and (" + inJkdDeptSql.toString().substring(0, inJkdDeptSql.toString().length() - 3) + ")";
            inJkSql += inJkWhereSql;
        }
        inJkKsList = Transaction.getExecutor().query(inJkSql, inJkValues);

        OutBean outBean = new OutBean();
        outBean.set("inJkKsContent", inJkKsList);
        outBean.set("outJkKsContent", outJkKsList);
        return outBean;
    }

    /**
     * 当前机构是否有安排这个机构的权限
     *
     * @param paramBean paramBean DEPT_CODE
     * @return OutBean flag
     */
    public OutBean deptContainFlag(ParamBean paramBean) {
        String deptCode = paramBean.getStr("DEPT_CODE");
        String roleDcode = KcUtils.getAutoPvlgCode();
        String[] deptCodes = roleDcode.split(",");
        String result = DapccUtils.getDeptContains(deptCode, deptCodes);
        return new OutBean().set("flag", result);
    }


}
