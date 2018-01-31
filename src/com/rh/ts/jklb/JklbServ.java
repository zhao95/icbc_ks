package com.rh.ts.jklb;

import com.icbc.ctp.utility.CollectionUtil;
import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.comm.ConfMgr;
import com.rh.core.comm.FileMgr;
import com.rh.core.icbc.basedata.KSSendTipMessageServ;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.*;
import com.rh.core.serv.bean.PageBean;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;
import com.rh.ts.util.BMUtil;
import com.rh.ts.util.TsConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class JklbServ extends CommonServ {
    private final static String TSJK_SERVID = "TS_JKLB_JK";
    private final static String TSQJ_SERVID = "TS_QJLB_QJ";
    private final static String TODO_SERVID = "TS_COMM_TODO";
    private final static String DONE_SERVID = "TS_COMM_TODO_DONE";
    private final static String TS_BMSH_PASS_SERVID = "TS_BMSH_PASS";
    private final static String COMM_MIND_SERVID = "TS_COMM_MIND";

    private final static String dateFormatString = "yyyy-MM-dd HH:mm:ss";

    private final static String dateFormatString2 = "yyyy-MM-dd";


    /**
     * 获取已借考的列表
     *
     * @param paramBean _extWhere
     * @return
     */
    public OutBean getAppliedJkList(ParamBean paramBean) {
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

//        select count
        String currentUserCode = Context.getUserBean().getCode();
        String lookCountSql = "select count(*) as COUNT from ts_jklb_jk where LOOK_FLAG ='0' and USER_CODE ='" + currentUserCode + "'";
        int lookCount = Transaction.getExecutor().queryOne(lookCountSql).getInt("COUNT");//, page.getNowPage(), page.getShowNum(), null, null

        //有审批记录，说明已审批 撤回标识为false 不可撤回
        String sql = "select a.*,(case when exists(select '' from ts_comm_mind b where b.DATA_ID=JK_ID) then 'false' else 'true' end) as canRetract " +
                " from TS_JKLB_JK a where a.USER_CODE ='" + currentUserCode + "' " +
                " order by a.JK_DATE desc";
        List<Bean> dataList = Transaction.getExecutor().query(sql);//, page.getNowPage(), page.getShowNum(), null, null

        String countSql = "select count(*) as count " + sql.substring(sql.indexOf("from TS_JKLB_JK a"));
        /*设置数据总数*/
        int count = dataList.size();
        int showCount = page.getShowNum();
        boolean bCount; //是否计算分页
        if ((showCount == 0) || paramBean.getQueryNoPageFlag()) {
            bCount = false;
        } else {
            bCount = true;
        }
        OutBean outBean = new OutBean();
        if (bCount) { //进行分页处理
            if (!page.contains(Constant.PAGE_ALLNUM)) { //如果有总记录数就不再计算
                int allNum;
                if ((page.getNowPage() == 1) && (count < showCount)) { //数据量少，无需计算分页
                    allNum = count;
                } else {
                    allNum = Transaction.getExecutor().queryOne(countSql).getInt("COUNT");
                }
                page.setAllNum(allNum);
            }
            outBean.setCount(page.getAllNum()); //设置为总记录数
        } else {
            outBean.setCount(dataList.size());
        }
        outBean.setData(dataList);
        outBean.set("FLAG_COUNT", lookCount);
        outBean.setPage(page);
        return outBean;
    }

    /**
     * 撤回请假申请
     *
     * @param paramBean
     * @return
     */
    public OutBean retract(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        String jkId = paramBean.getStr("JK_ID");
        Bean jkBean = ServDao.find(TSJK_SERVID, jkId);
        Transaction.begin();
        try {
            //删除待办信息
            Bean whereBean = new Bean();
            List<Object> values = new ArrayList<Object>();
            values.add(jkId);
            whereBean.put(Constant.PARAM_PRE_VALUES, values);
            whereBean.put(Constant.PARAM_WHERE, "and DATA_ID =? ");
            ServDao.destroy(TODO_SERVID, whereBean);
            //删除附件
            String jkImg = jkBean.getStr("JK_IMG");
            if (StringUtils.isNotBlank(jkImg)) {
                List<Bean> fileListBean = FileMgr.getFileListBean(TSJK_SERVID, jkImg);
                if (fileListBean != null && fileListBean.size() > 0) {
                    for (Bean fileBean : fileListBean) {
                        FileMgr.deleteFile(fileBean.getId());
                    }
                }
            }
            //删除借考数据
            ServDao.destroy(TSJK_SERVID, jkBean.getId());
            Transaction.commit();
        } catch (Exception e) {
            Transaction.rollback();
        }
        Transaction.end();
        return outBean;
    }

    /**
     * 发起借考申请
     *
     * @param paramBean
     * @return
     */
    public OutBean addData(ParamBean paramBean) {
        Transaction.begin();
        OutBean outBean = new OutBean();
        String servId = paramBean.getStr(Constant.PARAM_SERV_ID);
        //String userWorkNum = paramBean.getStr("user_work_num");
        String userName = paramBean.getStr("user_name");
        String jkTitle = paramBean.getStr("jktitle");
        String jkYiJi = paramBean.getStr("jkyiji");
        String jkerji = paramBean.getStr("jkerji");
        String jkCity = paramBean.getStr("jkcity");
        String jkUserCode = paramBean.getStr("user_code");
        String jkBumen = paramBean.getStr("bumen");
        String jkimg = paramBean.getStr("jkimg");
        String jkReason = paramBean.getStr("jkreason");
        String shidStr = paramBean.getStr("shids");
        String[] shids = shidStr.split(",");
        //获取XM_ID
        String shid = shids[0];
        Bean bmPassBean = ServDao.find(TsConstant.SERV_BMSH_PASS, shid);
        String xmId = bmPassBean.getStr("XM_ID");
        String lbDate = bmPassBean.getStr("JKLB_DATE");

        //借考
        Bean jkbean = new Bean();
        jkbean.set("JK_TITLE", jkTitle);
        if (StringUtils.isBlank(jkerji)) {
            jkbean.set("JK_YJFH", jkYiJi);
        } else {
            jkbean.set("JK_YJFH", jkerji);
        }
        jkbean.set("JK_JKCITY", jkCity);
        jkbean.set("USER_CODE", jkUserCode);
        jkbean.set("JK_REASON", jkReason);
        jkbean.set("JK_DEPT", jkBumen);
        jkbean.set("JK_KSNAME", shidStr);
        jkbean.set("XM_ID", xmId);
        jkbean.set("JK_NAME", userName);
        // jkbean.set("USER_CODE", userWorkNum);//人力资源编码  todo是不是要改个名称
        jkbean.set("JK_IMG", jkimg);//证明材料（fileId ）
        jkbean.set("JK_STATUS", "1");   //  1"审核中"; 2  "已通过";3 "未通过";
        jkbean.set("JK_DATE", new SimpleDateFormat(dateFormatString).format(new Date()));
        jkbean.set("JK_KSTIME", lbDate);//考试开始时间   todo  TS_BMSH_PASS  SH_ID  TS_BMLB_BM
        jkbean.set("S_DEPT", bmPassBean.get("S_DEPT"));
        jkbean.set("S_ODEPT", bmPassBean.get("S_ODEPT"));
        jkbean.set("S_TDEPT", bmPassBean.get("S_TDEPT"));
        //保存到ts_JKLB_JK库里
        Bean qjbd = ServDao.create(servId, jkbean);
        //起草人
        UserBean userBean = UserMgr.getUser(jkUserCode);
        doFlowTask(outBean, userBean, qjbd, 0);
        if (outBean.get(Constant.RTN_MSG) != null
                && ((String) outBean.get(Constant.RTN_MSG)).contains(Constant.RTN_MSG_ERROR)) {
            //有错误回滚
            Transaction.rollback();

        } else {
            Transaction.commit();
        }
        Transaction.end();
        return outBean;
    }

    /**
     * 人工审核请假
     *
     * @param paramBean
     * @return
     */
    public OutBean updateData(Bean paramBean) {
       /* String servId = paramBean.getStr(Constant.PARAM_SERV_ID);
        String sh_status = paramBean.getStr("shstatus");//同意 不同意
        String sh_reason = paramBean.getStr("shreason");//审核内容
        String isRetreat = paramBean.getStr("isRetreat");//是否被退回
        String paramTodoId = paramBean.getStr("todoId");//待办id*/

        // sh_status = sh_status.equals("1") ? "同意" : "不同意";
        UserBean currentUser = Context.getUserBean();//
        return this.updateData2(paramBean, currentUser);
    }

    /**
     * 系统审核请假
     *
     * @param paramBean {shstatus:"2",shreason:...,isRetreat:"true",todoId:"todoId"}
     * @return OutBean
     */
    public OutBean updateDataBySystem(Bean paramBean) {
       /* String servId = paramBean.getStr(Constant.PARAM_SERV_ID);
        String sh_status = paramBean.getStr("shstatus");//同意 不同意
        // sh_status = sh_status.equals("1") ? "同意" : "不同意";
        String sh_reason = paramBean.getStr("shreason");//审核内容
        String isRetreat = paramBean.getStr("isRetreat");//是否被退回id*/

        String paramTodoId = paramBean.getStr("todoId");//待办
        Bean todoBean = ServDao.find(TODO_SERVID, paramTodoId);
        String ownerCode = todoBean.getStr("OWNER_CODE");
        UserBean currentUser = UserMgr.getUser(ownerCode);
        return this.updateData2(paramBean, currentUser);
    }

    /**
     * 审核请假
     *
     * @param paramBean
     * @return
     */
    private OutBean updateData2(Bean paramBean, UserBean currentUser) {
        OutBean outBean = new OutBean();
//        String servId = paramBean.getStr(Constant.PARAM_SERV_ID);
        String sh_status = paramBean.getStr("shstatus");//同意 不同意
        String sh_reason = paramBean.getStr("shreason");//审核内容
        String isRetreat = paramBean.getStr("isRetreat");//是否被退回
        String paramTodoId = paramBean.getStr("todoId");//待办id
        // sh_status = sh_status.equals("1") ? "同意" : "不同意";
        String[] split = paramTodoId.split(",");
        for (String string : split) {
            if ("".equals(string)) {
                continue;
            }
            Transaction.begin();
            Bean todoBean = ServDao.find(TODO_SERVID, paramTodoId);
            String nodeSteps;
            if (todoBean == null) {
                outBean.setError("待办已被处理，请返回！");
            } else {
                nodeSteps = todoBean.getStr("NODE_STEPS");

                String jkId = todoBean.getStr("DATA_ID");
                Bean jkbean = ServDao.find(TSJK_SERVID, jkId);

                //1、保存评审意见
                //添加审核意见信息
                Bean shyjBean = new Bean();
                shyjBean.set("DATA_ID", jkId);
                shyjBean.set("SH_TYPE", "1");//审核类别 1 意见 2 审核记录
                shyjBean.set("SH_MIND", sh_reason);//意见内容

                shyjBean.set("SH_NODE", todoBean.getStr("NODE_NAME"));//审核层级名称
                shyjBean.set("SH_LEVEL", nodeSteps);//审核层级

                shyjBean.set("SH_STATUS", sh_status);//审核状态// 同意 不同意
                shyjBean.set("SH_UCODE", currentUser.getCode());//审核人UID(人力资源编码)
                shyjBean.set("SH_ULOGIN", currentUser.getLoginName());//审核人登陆名
                shyjBean.set("SH_UNAME", currentUser.getName());//审核人姓名
                shyjBean.set("S_DNAME", currentUser.getDeptName());//审核人部门名称
                ServDao.save(COMM_MIND_SERVID, shyjBean);

                //3、删除待办 插入已办
                //获取改请假的所有待办
                List<Bean> todoList = ServDao.finds(TODO_SERVID, "and DATA_ID = '" + jkId + "'");
                for (Bean bean : todoList) {
                    String todoId = (String) bean.get("TODO_ID");
                    if (todoId.equals(paramTodoId)) {
                        Bean doneBean = new Bean();
                        doneBean.putAll(bean);
                        doneBean.remove("TODO_ID");
                        doneBean.remove("_PK_");
                        ServDao.create(DONE_SERVID, doneBean);
                    }
                    ServDao.destroy(TODO_SERVID, todoId);
                }
                //2、修改请假状态，并产生待办
                //被退回 3 、 流程结束 2 、 其他 1
                String jk_status;
                if ("true".equals(isRetreat)) {
                    jk_status = "3";
                } else if ("1".equals(nodeSteps)) {
                    jk_status = "2";
                } else {
                    jk_status = "1";
                }
                jkbean.set("JK_STATUS", jk_status);
                ServDao.update(TSJK_SERVID, jkbean);


                if ("2".equals(jk_status) || "3".equals(jk_status)) {
                    try {
                        //流程结束 发送消息
                        //TS_JK_RESULT_TIP	借考结果提醒语
                        String jkResultMsg = ConfMgr.getConf("TS_JK_RESULT_TIP", "您的借考申请，有了审批结果，可登录工商银行考试查看。");
                        String jkTitle = jkbean.getStr("JK_TITLE");
                        String jkResult = ("2".equals(jk_status)) ? "通过" : "不通过";
                        jkResultMsg = jkResultMsg
                                .replaceAll("#JK_TITLE#", jkTitle)
                                .replaceAll("#JK_RESULT#", jkResult);
                        Bean jkResultTipBean = new Bean();
                        jkResultTipBean.set("USER_CODE", jkbean.getStr("USER_CODE"));
                        jkResultTipBean.set("tipMsg", jkResultMsg);
                        new KSSendTipMessageServ().sendTipMessageBeanForICBC(jkResultTipBean, "jkResult");
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("借考结果提醒失败，" + "JK_ID:" + jkId + ",USER_CODE:" + jkbean.getStr("USER_CODE"));
                    }

                    //修改借考记录状态为未读
                    jkbean.set("LOOK_FLAG", "0");
                    ServDao.update(TSJK_SERVID, jkbean);

                    try {
                        //记录流程节点信息
                        SqlBean sqlBean = new SqlBean();
                        sqlBean.and("DATA_ID", jkId);
                        List<Bean> tsCommTodoDoneList = ServDao.finds("TS_COMM_TODO_DONE", sqlBean);

                        String wfsId = null;
                        if (CollectionUtils.isNotEmpty(tsCommTodoDoneList)) {
                            wfsId = tsCommTodoDoneList.get(0).getStr("WFS_ID");
                        }
                        if (StringUtils.isNotBlank(wfsId)) {
                            //String getStep = todoBean.getStr("NODE_STEPS");
                            List<Bean> nodeApplyList = ServDao.finds("TS_WFS_NODE_APPLY", "AND WFS_ID='" + wfsId + "'");// and NODE_STEPS = " + getStep
                            if (CollectionUtils.isNotEmpty(nodeApplyList)) {
                                List<Bean> saveNodeHistoryBeanList = new ArrayList<Bean>();
                                for (Bean nodeApply : nodeApplyList) {
                                    Bean nodeHistoryBean = new Bean();
                                    nodeHistoryBean.set("DATA_ID", jkId);
                                    nodeHistoryBean.set("NODE_NAME", nodeApply.getStr("NODE_NAME"));
                                    nodeHistoryBean.set("NODE_NUM", nodeApply.getStr("NODE_NUM"));
                                    nodeHistoryBean.set("WFS_ID", nodeApply.getStr("WFS_ID"));
                                    nodeHistoryBean.set("NODE_STEPS", nodeApply.getStr("NODE_STEPS"));
                                    saveNodeHistoryBeanList.add(nodeHistoryBean);
                                }
                                ServDao.creates(TsConstant.SERV_WFS_NODE_HISTORY, saveNodeHistoryBeanList);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("请假节点保存失败，" + "QJ_ID:" + jkId + ",USER_CODE:" + jkbean.getStr("USER_CODE"));
                    }
                }


                if ("2".equals(jk_status)) {
                    //借考已通过 修改 TS_BMSH_PASS BM_STATUS字段信息
                    String jkKsname = jkbean.getStr("JK_KSNAME");
                    String[] shIds = jkKsname.split(",");
                    StringBuilder shIdStr = new StringBuilder();
                    List<Object> values = new ArrayList<Object>();
                    for (String shId : shIds) {
                        ParamBean queryParamBean = new ParamBean();
                        queryParamBean.set("SH_ID", shId);
                        Bean bean = ServDao.find(TS_BMSH_PASS_SERVID, queryParamBean);
                        if (bean == null) {
                            continue;
                        }
                        if ("1".equals(bean.getStr("BM_STATUS")) || "3".equals(bean.getStr("BM_STATUS"))) {
                            //1 3
                            bean.set("BM_STATUS", "3");
                        } else {
                            //0
                            bean.set("BM_STATUS", "2");
                        }
                        bean.set("JK_ODEPT", jkbean.getStr("JK_YJFH"));
                        ServDao.update(TS_BMSH_PASS_SERVID, bean);
                        //shIdStr
                        shIdStr.append("?,");
                        values.add(shId);
                    }

                    //借考通过，删除已安排的座位信息
                    Bean whereBean = new Bean();
                    whereBean.set(Constant.PARAM_WHERE, " and SH_ID in(" + shIdStr.substring(0, shIdStr.length() - 1) + ")");
                    whereBean.set(Constant.PARAM_PRE_VALUES, values);
                    ServDao.destroy(TsConstant.SERV_KCAP_YAPZW, whereBean);
                }

                if ("1".equals(jk_status)) {
                    int nodeSteps1 = Integer.parseInt(todoBean.getStr("NODE_STEPS"));
                    doFlowTask(outBean, currentUser, jkbean, nodeSteps1);
                }

                if (outBean.get(Constant.RTN_MSG) != null
                        && (outBean.getStr(Constant.RTN_MSG)).contains(Constant.RTN_MSG_ERROR)) {
                    //有错误回滚
                    Transaction.rollback();
//                    outBean.setError("数据有误审批失败");
                } else {
                    Transaction.commit();
                }
                Transaction.end();
            }
        }
        return outBean;
    }

    /**
     * 处理流程
     *
     * @param outBean  outBean
     * @param userBean 当前处理人（一般是当前用户）
     * @param jkbean   请假Bean
     * @param level    流程当前级别
     */

    private void doFlowTask(OutBean outBean, UserBean userBean, Bean jkbean, int level) {

        String jkId = jkbean.getStr("JK_ID");
        String jkTitle = jkbean.getStr("JK_TITLE");
        String xmId = jkbean.getStr("XM_ID");
        String examerUserCode = jkbean.getStr("USER_CODE");
        String shrUserCode = userBean.getCode();

        ParamBean flowParamBean = new ParamBean();
        //form表单传给后台一个bean，只包括借考一级分行
        flowParamBean.set("form", jkbean);
        flowParamBean.set("examerUserCode", examerUserCode);
        flowParamBean.set("shrUserCode", shrUserCode);//起草节点examerWorkNum传shrWorkNum
        flowParamBean.set("level", level);
        flowParamBean.set("deptCode", jkbean.getStr("S_DEPT"));
        flowParamBean.set("odeptCode", jkbean.getStr("S_ODEPT"));
        flowParamBean.set("xmId", xmId);
        flowParamBean.set("flowName", 2); //1:报名审核流程 2:异地借考流程 3:请假审核流程
        OutBean shBean = ServMgr.act("TS_WFS_APPLY", "backFlow", flowParamBean);
        String result = shBean.getStr("result");

        String[] split;
        if (StringUtils.isNotBlank(result)) {
            split = result.split(",");
        } else {
            split = new String[]{};
        }
        List<Bean> shList = new ArrayList<Bean>();
        for (String shUserCode : split) {
            Bean bean = new Bean();
            if (StringUtils.isBlank(shUserCode)) {
                continue;
            }
            bean.set("SHR_USERCODE", shUserCode);
            bean.set("SHR_NAME", UserMgr.getUser(shUserCode).getName());
            shList.add(bean);
        }
        outBean.putAll(shBean);
        if (CollectionUtil.isEmpty(shList)) {
            if (shList.size() == 0) {
                outBean.setError("没有审核人，请联系管理员！");
            }
        } else {
            int nodeSteps = shBean.getInt("SH_LEVEL");//shBean.getInt("NODE_STEPS");
            String nodeName = shBean.getStr("NODE_NAME");
            String wfsId = shBean.getStr("WFS_ID");
            StringBuilder shrNames = new StringBuilder();
            for (Bean bean : shList) {
                String shrName = bean.getStr("SHR_NAME");
                String shrUserCode2 = bean.getStr("SHR_USERCODE");
                shrNames.append(" ").append(shrName);
                //推送人
                UserBean shrUserBean = UserMgr.getUser(shrUserCode2);
                String shrDeptCode = shrUserBean.getDeptCode();
                String shrUserDeptName = shrUserBean.getDeptName();

                Bean todoBean = new Bean();
                todoBean.set("TITLE", jkTitle);
                todoBean.set("TYPE", "1");//待办类型 0 请假 1借考
                todoBean.set("DATA_ID", jkId);
                todoBean.set("NODE_STEPS", nodeSteps + "");//当前所在的流程级别
                todoBean.set("NODE_NAME", nodeName);//当前所在的流程节点名称
                todoBean.set("WFS_ID", wfsId);//流程id
                //发送人
                todoBean.set("SEND_NAME", userBean.getName());
                todoBean.set("SEND_USER", userBean.getCode());//发送人编码
                todoBean.set("SEND_DEPT", userBean.getDeptCode());
                todoBean.set("SEND_DEPT_NAME", userBean.getDeptName());
                //办理人
                todoBean.set("OWNER_NAME", shrName);
                todoBean.set("OWNER_CODE", shrUserCode2);
                todoBean.set("OWNER_DEPT", shrDeptCode);
                todoBean.set("OWNER_DEPT_NAME", shrUserDeptName);
                todoBean.set("SEND_TIME", new SimpleDateFormat(dateFormatString).format(new Date()));
                ServDao.save(TODO_SERVID, todoBean);
            }
            outBean.set("shrNames", shrNames.toString());
        }
    }

    /**
     * 根据shId获取报名的信息
     * 注：shids:'id1,id2,id3'
     */
    public OutBean getBmInfoByIds(ParamBean paramBean) {
        String shidStr = paramBean.getStr("shids");
        String[] shids = shidStr.split(",");
        StringBuilder sbu = new StringBuilder();
        if (shids.length > 0) {
            sbu.append("'").append(shids[0]).append("'");
            for (int i = 1; i < shids.length; i++) {
//                String shid = shids[i];
                sbu.append(",'").append(shids[i]).append("'");
            }
        }
//    "TS_BMSH_PASS"
        List<Bean> tsBmshPassList = ServDao.finds(TsConstant.SERV_BMSH_PASS, "and SH_ID in(" + sbu.toString() + ")");//userWorkNum
        for (Bean tsBmshPass : tsBmshPassList) {
            //通过TS_BMLB_BM表，获取标题和考试开始时间信息
            String shId = tsBmshPass.getStr("SH_ID");
            String bmTitle = tsBmshPass.getStr("BM_TITLE");
//        String bmLb = (String) bmBean.get("BM_LB");
            String bmXl = tsBmshPass.getStr("BM_XL");
            String bmMk = tsBmshPass.getStr("BM_MK");
            String bmType = tsBmshPass.getStr("BM_TYPE");
            String lbDate = tsBmshPass.getStr("LB_DATE");
            String bm_bt = BMUtil.getExaminationName(bmType, bmXl, bmMk);
            String title = "";
            if (StringUtils.isBlank(bmMk)) {
                title = bmTitle;
            } else {
                title = bm_bt;
            }
            tsBmshPass.set("lbDate", lbDate);
            tsBmshPass.set("title", title);
        }
        return new OutBean().setData(tsBmshPassList);
    }


    /**
     * 获取可申请的请假列表
     */
    public OutBean getUserCanLeaveList(ParamBean paramBean) throws ParseException {
        OutBean outBean = new OutBean();
//    String userWorkNum = (String) paramBean.get("USER_WORK_NUM");//用户人力资源编码
        String userCode = paramBean.getStr("USER_CODE");
        String xmId = paramBean.getStr("XM_ID");

        List<Bean> tsBmshPassList = ServDao.finds("TS_BMSH_PASS", "and BM_CODE='" + userCode + "' and  XM_ID ='" + xmId + "'");//userCode
        for (Iterator<Bean> iterator = tsBmshPassList.iterator(); iterator.hasNext(); ) {
            Bean tsBmshPass = iterator.next();
//            String xmId = (String) tsBmshPass.get("XM_ID");
            String shId = (String) tsBmshPass.get("SH_ID");

            List<Bean> queryJkList = ServDao.finds(TSJK_SERVID, "and JK_KSNAME like '%" + shId + "%' and JK_STATUS in('1','2') ");
            List<Bean> queryQjList = ServDao.finds(TSQJ_SERVID, "and QJ_KSNAME like '%" + shId + "%' and QJ_STATUS in('1','2')");

            //项目有报名设置 在项目报名时间内 && 不存在进行中或已通过的报名（是否已经请假）（是否已经借考）
            if (inApplyTime(xmId) && queryJkList.size() <= 0 && queryQjList.size() <= 0) {
                //通过TS_BMLB_BM表，获取标题信息
                String bmTitle = (String) tsBmshPass.get("BM_TITLE");
//        String bmLb = (String) bmBean.get("BM_LB");
                String bmXl = (String) tsBmshPass.get("BM_XL");
                String bmMk = (String) tsBmshPass.get("BM_MK");
                String bmType = (String) tsBmshPass.get("BM_TYPE");
                String lbDate = (String) tsBmshPass.get("LB_DATE");
                String bm_bt = BMUtil.getExaminationName(bmType, bmXl, bmMk);
                String title = "";
                if (!"".equals(bmMk)) {
                    title = bm_bt;
                } else {
                    title = bmTitle;
                }
                tsBmshPass.set("lbDate", lbDate);
                tsBmshPass.set("title", title);
                //获取项目名称 临时方法
                OutBean xmBean = ServMgr.act(TsConstant.SERV_XMGL, "byid", new ParamBean().setId(xmId));
                String xmName = xmBean.getStr("XM_NAME");
                tsBmshPass.set("XM_NAME", xmName);

            } else {
                iterator.remove();
            }
        }
        outBean.setData(tsBmshPassList);
        return outBean;
    }

    /**
     * 获取可申请的请假列表
     */
    public OutBean getUserCanLeaveXmList(ParamBean paramBean) throws ParseException {
        String userCode = paramBean.getStr("USER_CODE");
        if (StringUtils.isBlank(userCode)) {
            userCode = Context.getUserBean().getCode();
        }
//        String xmId = paramBean.getStr("XM_ID");
        OutBean outBean = new OutBean();
        List<Bean> xmBeanList = Transaction.getExecutor().query("select * from TS_XMGL a" +
                //进行中的项目  &&  项目中存在可以申请借考的报名（存在不在申请中/申请通过的借考 && 由场次安排引入的考试忽略）
                //请假了不能借考
                " where now() BETWEEN str_to_date(XM_START,'%Y-%m-%d %H:%i:%s') and str_to_date(XM_END,'%Y-%m-%d %H:%i:%s') " +
                "and exists( " +
                " select * from TS_BMSH_PASS pass " +
                " where pass.SH_ID !='' and pass.SH_ID is not null and a.XM_ID =pass.XM_ID and BM_CODE ='" + userCode + "'" +
                "  and not EXISTS(select '' from ts_jklb_jk jk where jk.JK_KSNAME like CONCAT('%',pass.SH_ID,'%') and jk.JK_STATUS in('1','2')) " +
                "  and not EXISTS(select '' from TS_QJLB_QJ qj where qj.QJ_KSNAME like CONCAT('%',pass.SH_ID,'%') and qj.QJ_STATUS in('1','2')) " +
                " )");

        for (Iterator<Bean> iterator = xmBeanList.iterator(); iterator.hasNext(); ) {
            Bean xmBean = iterator.next();
            if (inApplyTime(xmBean.getId())) {

            } else {
                iterator.remove();
            }
        }
        outBean.setData(xmBeanList);
        return outBean;
    }

    /**
     * 该机构下是否有启用的考场
     *
     * @param paramBean jkyiji jkerji XM_ID
     * @return outBean flag
     */
    public OutBean checkHasKc(ParamBean paramBean) {
        boolean result = false;

        String jkyiji = paramBean.getStr("jkyiji");
        String jkerji = paramBean.getStr("jkerji");

        String deptCode = "";
        if (StringUtils.isBlank(jkerji)) {
            deptCode = jkyiji;
        } else {
            deptCode = jkerji;
        }
        String xmId = paramBean.getStr("XM_ID");
        String sql = "SELECT b.KC_ODEPTCODE FROM `ts_xmgl_kcap_dapcc` a\n" +
                " left join ts_kcgl b on a.KC_ID =b.KC_ID\n" +
                " where a.XM_ID =? and b.KC_ODEPTCODE =?";
        List<Object> values = new ArrayList<Object>();
        values.add(xmId);
        values.add(deptCode);
        List<Bean> list = Transaction.getExecutor().query(sql, values);
        if (CollectionUtils.isNotEmpty(list)) {
            result = true;
        }

        return new OutBean().set("flag", result);
    }

    /**
     * 查询该报名是否有在请假申请时间内
     *
     * @param xmId
     * @return
     */
    private boolean inApplyTime(String xmId) throws ParseException {
        boolean result = false;

        List<Bean> xmSzList = ServDao.finds("TS_XMGL_SZ", "and XM_ID ='" + xmId + "' and XM_SZ_NAME ='异地借考'");
        if (xmSzList.size() > 0) {
            //项目中有借考模块
            Bean xmSz = xmSzList.get(0);
            String xmSzType = xmSz.getStr("XM_SZ_TYPE");
            if ("进行中".equals(xmSzType)) {
                //异地借考开放中
                String xmSzId = xmSz.getId();
                List<Bean> tsXmglQjglList = ServDao.finds("TS_XMGL_YDJK", "and XM_SZ_ID ='" + xmSzId + "'");
                if (tsXmglQjglList.size() > 0) {
                    String qjStadate = (String) tsXmglQjglList.get(0).get("YDJK_STADATE");
                    String qjEnddate = (String) tsXmglQjglList.get(0).get("YDJK_ENDDATE");
                    //在申请时间内
                    if (StringUtils.isBlank(qjStadate) || StringUtils.isBlank(qjEnddate)) {
                        result = false;
                    } else {
                        long nowTime = new Date().getTime();
                        if (nowTime > getTime(qjStadate) && nowTime < getTime(qjEnddate)) {
                            result = true;
                        }
                    }
                }
            }
        }
        return result;
    }

    public long getTime(String dateStr) throws ParseException {
        long time;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatString);
        try {
            time = sdf.parse(dateStr).getTime();
            return time;
        } catch (ParseException e) {
            sdf = new SimpleDateFormat(dateFormatString2);
            try {
                time = sdf.parse(dateStr).getTime();
            } catch (ParseException e1) {
                e1.printStackTrace();
                throw e1;
            }
        }
        return time;
    }

    /**
     * 报名被设置不通过后 取消流转中的流程
     *
     * @param paramBean
     * @return
     * @throws ParseException
     */
    public OutBean cancelFlow(ParamBean paramBean) throws ParseException {
        OutBean outBean = new OutBean();
        List<String> successIdList = new ArrayList<String>();

        List<String> bmIdList = paramBean.getList("bmIdList");//通过后 又被审批为不通过的报名Id
        //移除空字符串
        for (Iterator<String> iterator = bmIdList.iterator(); iterator.hasNext(); ) {
            String bmId = iterator.next();
            if (StringUtils.isBlank(bmId)) {
                iterator.remove();
            }
        }

        SqlBean sqlBean = new SqlBean();
        sqlBean.andIn("BM_ID", bmIdList);
        List<Bean> bmPassBeanList = ServDao.finds(TsConstant.SERV_BMSH_PASS, sqlBean);

        for (Bean bmPass : bmPassBeanList) {
            //根据bmId查找出审核中的借考流程
            List<Bean> queryJkList = ServDao.finds(TSJK_SERVID, "and JK_KSNAME like '%" + bmPass.getStr("SH_ID") + "%' and JK_STATUS in('1')");
            for (Bean jkBean : queryJkList) {
                //每个shId的处理进行 事务操作
                Transaction.begin();
                try {
                    String jkId = jkBean.getId();
                    List<Bean> todoList = ServDao.finds(TODO_SERVID, "and DATA_ID = '" + jkId + "'");
                    if (todoList.size() > 0) {//待办是否存在
                        //删除待办
                        for (Bean todoBean : todoList) {
                            ServDao.delete(TODO_SERVID, todoBean.getId());
                        }
                        //添加审核意见信息
                        Bean shyjBean = new Bean();
                        shyjBean.set("DATA_ID", jkId);
                        shyjBean.set("SH_TYPE", "1");//审核类别 1 意见 2 审核记录
                        shyjBean.set("SH_MIND", "该借考中的报名被修改为审核不通过，改借考流程中止");//意见内容
                        shyjBean.set("SH_NODE", "系统处理");//审核层级名称
                        shyjBean.set("SH_LEVEL", "");//审核层级
                        shyjBean.set("SH_STATUS", "2");//审核状态// 1 同意 2 不同意
                        shyjBean.set("SH_UCODE", "");//审核人UID(人力资源编码)
                        shyjBean.set("SH_ULOGIN", "");//审核人登陆名
                        shyjBean.set("SH_UNAME", "系统");//审核人姓名
                        shyjBean.set("S_DNAME", "");//审核人部门名称
                        ServDao.save(COMM_MIND_SERVID, shyjBean);
                        //修改为不通过
                        jkBean.set("JK_STATUS", "3");
                        ServDao.update(TSJK_SERVID, jkBean);
                    }
                    Transaction.commit();
                    successIdList.add(bmPass.getStr("BM_ID"));
                } catch (Exception e) {
                    Transaction.rollback();
                } finally {
                    Transaction.end();
                }
            }
        }
        outBean.set("successIdList", successIdList);
        return outBean;
    }
}