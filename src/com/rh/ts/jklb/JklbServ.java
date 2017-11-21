package com.rh.ts.jklb;

import com.icbc.ctp.utility.CollectionUtil;
import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.comm.ConfMgr;
import com.rh.core.icbc.basedata.KSSendTipMessageServ;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.*;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.util.Constant;
import com.rh.ts.util.TsConstant;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class JklbServ extends CommonServ {
    private final static String TSJK_SERVID = "TS_JKLB_JK";
    private final static String TODO_SERVID = "TS_COMM_TODO";
    private final static String DONE_SERVID = "TS_COMM_TODO_DONE";
    private final static String TS_BMSH_PASS_SERVID = "TS_BMSH_PASS";
    private final static String COMM_MIND_SERVID = "TS_COMM_MIND";

    private final static String dateFormatString = "yyyy-MM-dd HH:mm:ss";

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
        String jkCity = paramBean.getStr("jkcity");
        String jkUserCode = paramBean.getStr("user_code");
        String jkBumen = paramBean.getStr("bumen");
        String jkimg = paramBean.getStr("jkimg");
        String jkReason = paramBean.getStr("jkreason");
        String bmidStr = paramBean.getStr("bmids");
        String[] bmids = bmidStr.split(",");
        //获取XM_ID
        String bmid = bmids[0];
        Bean bmBean = ServDao.find("TS_BMLB_BM", bmid);
        String xmId = bmBean.getStr("XM_ID");
        String lbDate = bmBean.getStr("JKLB_DATE");

        //借考
        Bean jkbean = new Bean();
        jkbean.set("JK_TITLE", jkTitle);
        jkbean.set("JK_YJFH", jkYiJi);
        jkbean.set("JK_JKCITY", jkCity);
        jkbean.set("USER_CODE", jkUserCode);
        jkbean.set("JK_REASON", jkReason);
        jkbean.set("JK_DEPT", jkBumen);
        jkbean.set("JK_KSNAME", bmidStr);
        jkbean.set("XM_ID", xmId);
        jkbean.set("JK_NAME", userName);
        // jkbean.set("USER_CODE", userWorkNum);//人力资源编码  todo是不是要改个名称
        jkbean.set("JK_IMG", jkimg);//证明材料（fileId ）
        jkbean.set("JK_STATUS", "1");   //  1"审核中"; 2  "已通过";3 "未通过";
        jkbean.set("JK_DATE", new SimpleDateFormat(dateFormatString).format(new Date()));
        jkbean.set("JK_KSTIME", lbDate);//考试开始时间   todo  TS_BMSH_PASS  BM_ID  TS_BMLB_BM
        jkbean.set("S_DEPT", bmBean.get("S_DEPT"));
        jkbean.set("S_ODEPT", bmBean.get("S_ODEPT"));
        jkbean.set("S_TDEPT", bmBean.get("S_TDEPT"));
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
                shyjBean.set("SH_UCODE", currentUser.getWorkNum());//审核人UID(人力资源编码)
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

                try {
                    if ("2".equals(jk_status) || "3".equals(jk_status)) {
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
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("借考结果提醒失败，" + "JK_ID:" + jkId + ",USER_CODE:" + jkbean.getStr("USER_CODE"));
                }

                if ("2".equals(jk_status)) {
                    //借考已通过 修改 TS_BMSH_PASS BM_STATUS字段信息
                    String jkKsname = jkbean.getStr("JK_KSNAME");
                    String[] bmIds = jkKsname.split(",");
                    StringBuilder shIdStr = new StringBuilder();
                    List<Object> values = new ArrayList<Object>();
                    for (String bmId : bmIds) {
                        ParamBean queryParamBean = new ParamBean();
                        queryParamBean.set("BM_ID", bmId);
                        Bean bean = ServDao.find(TS_BMSH_PASS_SERVID, queryParamBean);
                        if (bean == null) {
                            continue;
                        }
                        if ("1".equals(bean.getStr("BM_STATUS"))) {
                            bean.set("BM_STATUS", "3");
                        } else {
                            bean.set("BM_STATUS", "2");
                        }
                        bean.set("JK_ODEPT", jkbean.getStr("JK_YJFH"));
                        ServDao.update(TS_BMSH_PASS_SERVID, bean);
                        //shIdStr
                        String shId = bean.getStr("SH_ID");
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
                    outBean.setError("数据有误审批失败");
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
        String shUserCode = userBean.getCode();

        ParamBean flowParamBean = new ParamBean();
        //form表单传给后台一个bean，只包括借考一级分行
        flowParamBean.set("form", jkbean);
        flowParamBean.set("examerUserCode", examerUserCode);
        flowParamBean.set("shrUserCode", shUserCode);//起草节点examerWorkNum传shrWorkNum
        flowParamBean.set("level", level);
        flowParamBean.set("deptCode", jkbean.getStr("S_DEPT"));
        flowParamBean.set("odeptCode", jkbean.getStr("S_ODEPT"));
        flowParamBean.set("xmId", xmId);
        flowParamBean.set("flowName", 2); //1:报名审核流程 2:异地借考流程 3:请假审核流程
        OutBean shBean = ServMgr.act("TS_WFS_APPLY", "backFlow", flowParamBean);
        List<Bean> shList = shBean.getList("result");
        outBean.putAll(shBean);
        if (CollectionUtil.isEmpty(shList)) {
            if (shList != null && shList.size() == 0) {
                outBean.setError("没有审核人，请联系管理员！");
            }
        } else {
            int nodeSteps = shBean.getInt("NODE_STEPS");
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
     * 根据bmId获取报名的信息
     * 注：bmids:'id1,id2,id3'
     */
    public OutBean getBmInfoByIds(ParamBean paramBean) {
        String bmidStr = paramBean.getStr("bmids");
        String[] bmids = bmidStr.split(",");
        StringBuilder sbu = new StringBuilder();
        if (bmids.length > 0) {
            sbu.append("'").append(bmids[0]).append("'");
            for (int i = 1; i < bmids.length; i++) {
//                String bmid = bmids[i];
                sbu.append(",'").append(bmids[i]).append("'");
            }
        }
//    "TS_BMSH_PASS"
        List<Bean> tsBmshPassList = ServDao.finds("TS_BMLB_BM", "and BM_ID in(" + sbu.toString() + ")");//userWorkNum
        for (Bean tsBmshPass : tsBmshPassList) {
            //通过TS_BMLB_BM表，获取标题和考试开始时间信息
            String bmId = tsBmshPass.getStr("BM_ID");
            Bean bmBean = ServDao.find("TS_BMLB_BM", bmId);
            String bmTitle = bmBean.getStr("BM_TITLE");
//        String bmLb = (String) bmBean.get("BM_LB");
            String bmXl = bmBean.getStr("BM_XL");
            String bmMk = bmBean.getStr("BM_MK");
            String bmType = bmBean.getStr("BM_TYPE");
            String lbDate = bmBean.getStr("LB_DATE");
            String bm_bt = bmType + "-" + bmXl + "-" + bmMk;
            String title = "";
            if (!"".equals(bmMk)) {
                title = bm_bt;
            } else {
                title = bmTitle;
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
        String userCode = (String) paramBean.get("USER_CODE");

        List<Bean> tsBmshPassList = ServDao.finds("TS_BMSH_PASS", "and BM_CODE='" + userCode + "'");//userCode
        //todo 后续数据量越来越来多 这样每个都判断是不是不好 1、通过sql过滤
        for (Iterator<Bean> iterator = tsBmshPassList.iterator(); iterator.hasNext(); ) {
            Bean tsBmshPass = iterator.next();
            String xmId = (String) tsBmshPass.get("XM_ID");
            String bmId = (String) tsBmshPass.get("BM_ID");

            List<Bean> queryQjList = ServDao.finds(TSJK_SERVID, "and JK_KSNAME like '%" + bmId + "%' and JK_STATUS in('1','2')");
            //项目有报名设置 在项目报名时间内 && 不存在进行中或已通过的报名（是否已经请假）
            if (inApplyTime(xmId) && queryQjList.size() <= 0) {
                //通过TS_BMLB_BM表，获取标题信息
                Bean bmBean = ServDao.find("TS_BMLB_BM", bmId);
                if (bmBean == null) {
                    //报名信息丢失移除（数据错误）
                    iterator.remove();
                    continue;
                }
                String bmTitle = (String) bmBean.get("BM_TITLE");
//        String bmLb = (String) bmBean.get("BM_LB");
                String bmXl = (String) bmBean.get("BM_XL");
                String bmMk = (String) bmBean.get("BM_MK");
                String bmType = (String) bmBean.get("BM_TYPE");
                String lbDate = (String) bmBean.get("LB_DATE");
                String bm_bt = DictMgr.getName("TS_XMGL_BM_KSLBK_LV", bmType) + "-" + bmXl + "-" + bmMk;
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
     * 查询该报名是否有在请假申请时间内
     *
     * @param xmId
     * @return
     */
    private boolean inApplyTime(String xmId) throws ParseException {
        boolean result = false;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatString);

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
                    } else if (new Date().getTime() > sdf.parse(qjStadate).getTime() && new Date().getTime() < sdf.parse(qjEnddate).getTime()) {
                        result = true;
                    }
                }
            }
        }
        return result;
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
        for (String bmId : bmIdList) {
            //根据bmId查找出审核中的借考流程
            List<Bean> queryJkList = ServDao.finds(TSJK_SERVID, "and JK_KSNAME like '%" + bmId + "%' and JK_STATUS in('1')");
            for (Bean jkBean : queryJkList) {
                //每个bmId的处理进行 事务操作
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
                    successIdList.add(bmId);
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