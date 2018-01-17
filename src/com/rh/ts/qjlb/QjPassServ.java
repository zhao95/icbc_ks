package com.rh.ts.qjlb;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;
import com.rh.core.util.ExpUtils;
import com.rh.core.util.ImpUtils;
import com.rh.ts.util.BMUtil;
import com.rh.ts.util.TsConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class QjPassServ extends CommonServ {


    private final static String COMM_MIND_SERVID = "TS_COMM_MIND";

    private final static String TSQJ_SERVID = "TS_QJLB_QJ";

    private final static String TODO_SERVID = "TS_COMM_TODO";

    /**
     * 提供导出ExcelexpAll
     *
     * @param paramBean 参数信息
     * @return 执行结果
     */
    public OutBean expAll(ParamBean paramBean) {
        /*获取beanList信息*/
        //*设置查询条件
        //paramBean.set("isArrange", "false");
        paramBean.set(ParamBean.QUERY_NOPAGE_FLAG, "true");

        List<Bean> allList = this.getKsQjContent(paramBean);
       
        /*设置导出展示信息*/
        LinkedHashMap<String, String> colMap = new LinkedHashMap<String, String>();
        colMap.put("QJ_ID", "请假申请单编码");
        colMap.put("QJ_TITLE", "项目名称");
        colMap.put("USER_CODE", "人力资源编码");
        colMap.put("QJ_NAME", "姓名");
        colMap.put("KS_NAME", "考试名称");
        colMap.put("QJ_REASON", "请假事由");
        colMap.put("QJ_STATUS_NAME", "请假状态");

        return ExpUtils.expUtil(allList, colMap, paramBean);
    }

    /**
     * 根据条件 获取请假信息
     *
     * @param paramBean paramBean searchKcId searchSjId searchDeptCode
     * @return outBean
     */
    public List<Bean> getKsQjContent(ParamBean paramBean) {
        String xmId = paramBean.getStr("XM_ID");
        //请假的数据
        SqlBean sqlBean = new SqlBean();
        sqlBean.and("XM_ID", xmId);
        List<Bean> list = ServDao.finds("TS_XMGL_QJPASS", sqlBean);
        if (CollectionUtils.isNotEmpty(list)) {
            for (Bean bean : list) {
                String qjKsName = bean.getStr("QJ_KSNAME");
                String[] shIdArray = qjKsName.split(",");
                StringBuilder ksName = new StringBuilder();
                for (String shId : shIdArray) {
                    Bean bmPassBean = ServDao.find(TsConstant.SERV_BM, shId);
                    if (bmPassBean == null) {
                        bmPassBean = ServDao.find(TsConstant.SERV_BMSH_PASS, shId);
                    }
                    String bmTitle = bmPassBean.getStr("BM_TITLE");
//                    String bmLb = bmPassBean.getStr("BM_LB");
                    String bmXl = bmPassBean.getStr("BM_XL");
                    String bmMk = bmPassBean.getStr("BM_MK");
                    String bmType = bmPassBean.getStr("BM_TYPE");
                    String examinationName = BMUtil.getExaminationName(bmType, bmXl, bmMk, bmTitle);
                    ksName.append(",").append(examinationName);
                }
                if (ksName.length() > 0) {
                    ksName = new StringBuilder(ksName.substring(1));
                }
                bean.set("KS_NAME", ksName.toString());
                bean.set("QJ_STATUS_NAME", QjUtils.getQjStatusName(bean.getStr("QJ_STATUS")));
            }
        }
        return list;
    }

    /**
     * 导入方法开始的入口
     */
    public OutBean saveFromExcel(ParamBean paramBean) {
        String fileId = paramBean.getStr("FILE_ID");
//       //保存方法入口
//       paramBean.set(ImpUtils.SERV_METHOD_NAME, "impDataSave");
//       String finalfileid = ImpUtils.getDataFromXls(fileId, paramBean);
//       return new OutBean().set("FILE_ID", finalfileid);
        // String fileId = paramBean.getStr("FILE_ID");
        //方法入口
        paramBean.set("SERVMETHOD", "savedata");
        OutBean out = ImpUtils.getDataFromXls(fileId, paramBean);
        String failnum = out.getStr("failernum");
        String successnum = out.getStr("oknum");
        //返回导入结果
        return new OutBean().set("FILE_ID", out.getStr("fileid")).set("_MSG_", "导入成功：" + successnum + "条,导入失败：" + failnum + "条");
    }


    /**
     * 导入保存方法
     *
     * @param paramBean XM_ID datalist
     * @return
     */
    public OutBean savedata(ParamBean paramBean) {
        OutBean outBean = new OutBean();

//        String nowTime = DateUtils.getDatetime();
//        UserBean currentUser = Context.getUserBean();
        //获取项目id
        String xmId = paramBean.getStr("XM_ID");
        Bean xmBean = ServDao.find("TS_XMGL", xmId);
//        String xmName = xmBean.getStr("XM_NAME");

        List<Bean> rowBeanList = paramBean.getList(ImpUtils.DATA_LIST);
//        List<String> codeList = new ArrayList<String>();// 避免重复添加数据
//        List<Bean> beans = new ArrayList<Bean>();
        List<Bean> successList = new ArrayList<Bean>();
        for (Bean rowBean : rowBeanList) {
            //*读取一行的值
            String userCode = rowBean.getStr(ImpUtils.COL_NAME + "1");//人力资源编码
            String qjReason = rowBean.getStr(ImpUtils.COL_NAME + "3");//请假事由
            String lbString = rowBean.getStr(ImpUtils.COL_NAME + "4");//岗位类别
            String xlString = rowBean.getStr(ImpUtils.COL_NAME + "5");//序列
            String mkString = rowBean.getStr(ImpUtils.COL_NAME + "6");//模块
            String typeString = rowBean.getStr(ImpUtils.COL_NAME + "7");//级别
            String qjId = rowBean.getStr(ImpUtils.COL_NAME + "8");//系统申请单编码
            String shStatus;
            String shStatusName = rowBean.getStr(ImpUtils.COL_NAME + "9");//审批结果
            if ("同意".equals(shStatusName)) {
                shStatus = "1";
            } else if ("不同意".equals(shStatusName)) {
                shStatus = "2";
            } else {
                rowBean.set(ImpUtils.ERROR_NAME, "审批结果应为“同意”或“不同意”");
                continue;
            }
            String shMind = rowBean.getStr(ImpUtils.COL_NAME + "10");//审批意见

            if (StringUtils.isBlank(qjId)) {
                //*查找人员
                UserBean userBean = null;
                if (StringUtils.isNotBlank(userCode)) {
                    try {
                        userBean = UserMgr.getUser(userCode);//获取人员信息
                    } catch (TipException ignored) {
                    }
                    if (userBean == null) {
                        rowBean.set(ImpUtils.ERROR_NAME, "找不到用户");
                        continue;
                    }
                }

                //*查询考试类别kslb
                Bean kslbBean = ImpUtils.getKsLBKBean(lbString, xlString, mkString, typeString);
                if (kslbBean == null) {
                    rowBean.set(ImpUtils.ERROR_NAME, "找不到相关考试类别");
                    continue;
                }

                //*该考生是否在待安排考生中
                SqlBean sqlBean = new SqlBean();
                sqlBean.and("XM_ID", xmId);
                sqlBean.and("BM_CODE", userCode);
                sqlBean.and("KSLBK_ID", kslbBean.getId());
                Bean bmPass = ServDao.find(TsConstant.SERV_BMSH_PASS, sqlBean);
                if (bmPass == null) {
                    rowBean.set(ImpUtils.ERROR_NAME, "该考生该考试不在待安排考生中");
                    continue;
                }

                //*查询已申请的请假列表
                sqlBean = new SqlBean();
                sqlBean.and("USER_CODE", userCode);
                sqlBean.and("XM_ID", xmId);
                sqlBean.andLike("QJ_KSNAME", bmPass.getStr("SH_ID"));
                sqlBean.and("QJ_STATUS", "2");
                if (ServDao.count(TSQJ_SERVID, sqlBean) > 0) {
                    //已经存在请假信息
                    rowBean.set(ImpUtils.ERROR_NAME, "该考生该考试已经请假");
                    continue;
                } else {
                    //请假bean
                    Bean qjbean = new Bean();
                    qjbean.set("QJ_TITLE", xmBean.getStr("XM_NAME"));
                    qjbean.set("QJ_REASON", qjReason);
                    qjbean.set("QJ_DANWEI", userBean.getDeptName());
                    qjbean.set("QJ_KSNAME", bmPass.getStr("SH_ID"));
                    qjbean.set("XM_ID", xmId);
                    qjbean.set("QJ_NAME", userBean.getName());
                    qjbean.set("USER_CODE", userCode);//用户编码
                    qjbean.set("QJ_IMG", "");//证明材料（fileId ）
                    qjbean.set("QJ_STATUS", "1");   //  1"审核中"; 2  "已通过";3 "未通过";
                    qjbean.set("QJ_DATE", DateUtils.getDatetime());
                    qjbean.set("QJ_KSTIME", xmBean.getStr("XM_KSSTARTDATA"));//考试开始时间
                    qjbean.set("S_DEPT", bmPass.get("S_DEPT"));
                    qjbean.set("S_ODEPT", bmPass.get("S_ODEPT"));
                    qjbean.set("S_TDEPT", bmPass.get("S_TDEPT"));
                    Bean qjbd = null;
                    try {
                        qjbd = ServDao.create(TSQJ_SERVID, qjbean);
                        Transaction.commit();
                    } catch (Exception e) {
                        rowBean.set(ImpUtils.ERROR_NAME, "程序错误：创建请假申请失败，错误信息为" + e.getMessage());
                        continue;
                    }
                    if (qjbd != null) {
                        qjId = qjbd.getId();
                    }
                }
            }

            //处理审批
            try {
                doApplyByQjId(qjId, shStatus, shMind);

            } catch (Exception e) {
                rowBean.set(ImpUtils.ERROR_NAME, "程序错误：审批处理失败，错误信息为" + e.getMessage());
                continue;
            }

            successList.add(rowBean);
        }
        return outBean.set(ImpUtils.ALL_LIST, rowBeanList).set(ImpUtils.SUCCESS_LIST, successList);
    }

    /**
     * 根据qjId shStatus shMind 完成审批
     *
     * @param qjId     qjId
     * @param shStatus shStatus
     * @param shMind   shMind
     * @throws Exception Exception
     */
    private void doApplyByQjId(String qjId, String shStatus, String shMind) throws Exception {
        Transaction.begin();

        try {
            UserBean currentUser = Context.getUserBean();
            //根据请假id处理请假申请
            Bean qjBean = ServDao.find(TSQJ_SERVID, qjId);

            //*保存评审意见
            Bean shyjBean = new Bean();
            shyjBean.set("DATA_ID", qjId);
            shyjBean.set("SH_TYPE", "1");//审核类别 1 意见 2 审核记录
            shyjBean.set("SH_MIND", shMind);//意见内容
            shyjBean.set("SH_NODE", "导入审批");//审核层级名称
            shyjBean.set("SH_LEVEL", "0");//审核层级
            shyjBean.set("SH_STATUS", shStatus);//审核状态// 同意 不同意
            shyjBean.set("SH_UCODE", currentUser.getCode());//审核人UID(人力资源编码)
            shyjBean.set("SH_ULOGIN", currentUser.getLoginName());//审核人登陆名
            shyjBean.set("SH_UNAME", currentUser.getName());//审核人姓名
            shyjBean.set("S_DNAME", currentUser.getDeptName());//审核人部门名称
            ServDao.save(COMM_MIND_SERVID, shyjBean);

            //*删除待办信息
            SqlBean whereBean = new SqlBean();
            whereBean.and("DATA_ID", qjId);
            ServDao.destroy(TODO_SERVID, whereBean);

            //*更改申请状态
            if ("1".equals(shStatus)) {
                qjBean.set("QJ_STATUS", "2");//同意 申请通过
            } else if ("2".equals(shStatus)) {
                qjBean.set("QJ_STATUS", "3");//不同意 申请不通过
            }
            ServDao.update(TSQJ_SERVID, qjBean);

            String qjKsname = qjBean.getStr("QJ_KSNAME");
            String[] shIds = qjKsname.split(",");

            //*变更考试安排信息
            for (String shId : shIds) {
                Bean bean = ServDao.find(TsConstant.SERV_BMSH_PASS, shId);
                if (bean == null) {
                    continue;
                }

                if ("1".equals(shStatus)) {
                    if ("2".equals(bean.getStr("BM_STATUS"))) {
                        bean.set("BM_STATUS", "3");
                    } else {
                        bean.set("BM_STATUS", "1");
                    }
                }
                ServDao.update(TsConstant.SERV_BMSH_PASS, bean);
            }

            String xmId = qjBean.getStr("XM_ID");
            Bean xmBean = ServDao.find(TsConstant.SERV_XMGL, xmId);
            //如果在提交场次安排前，请假成功删除考位安排
            String xmKcapPublishTime = xmBean.getStr("XM_KCAP_PUBLISH_TIME");//项目场次发布时间
            if (StringUtils.isBlank(xmKcapPublishTime)) {
                //项目场次未发布
                for (String s : shIds) {
                    List<Object> values = new ArrayList<Object>();
                    values.add(s);
                    Bean whereBean1 = new Bean();
                    //未提交场次安排
                    whereBean1.set(Constant.PARAM_WHERE, " and SH_ID =? and (IS_SUBMIT!='1' or IS_SUBMIT is null)");
                    whereBean1.set(Constant.PARAM_PRE_VALUES, values);
                    ServDao.destroy(TsConstant.SERV_KCAP_YAPZW, whereBean1);
                }
            }

            Transaction.commit();
        } catch (Exception e) {
            Transaction.rollback();
            throw e;
        } finally {
            Transaction.end();
        }
    }
}