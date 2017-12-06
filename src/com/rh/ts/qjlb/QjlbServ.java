package com.rh.ts.qjlb;

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
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;
import com.rh.core.util.i18n.Language;
import com.rh.ts.util.BMUtil;
import com.rh.ts.util.TsConstant;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class QjlbServ extends CommonServ {

    private final static String TSQJ_SERVID = "TS_QJLB_QJ";
    private final static String TODO_SERVID = "TS_COMM_TODO";
    private final static String DONE_SERVID = "TS_COMM_TODO_DONE";
    private final static String COMM_MIND_SERVID = "TS_COMM_MIND";
    //    private final static String TSQJ_BM_SERVID = "TS_QJLB_BM";
    private final static String TS_BMSH_PASS_SERVID = "TS_BMSH_PASS";
    private final static String TS_BM_QJ_NUM_SERVID = "TS_BM_QJ_NUM";
    private final static String dateFormatString = "yyyy-MM-dd HH:mm:ss";
    private final static String dateFormatString2 = "yyyy-MM-dd";


    /**
     * 获取已请假的列表
     *
     * @param paramBean _extWhere
     * @return
     */
    public OutBean getAppliedLeaveList(ParamBean paramBean) {
              /*分页参数处理*/
//        PageBean page = paramBean.getQueryPage();
//        int rowCount = paramBean.getShowNum(); //通用分页参数优先级最高，然后是查询的分页参数
//        if (rowCount > 0) { //快捷参数指定的分页信息，与finds方法兼容
//            page.setShowNum(rowCount); //从参数中获取需要取多少条记录，如果没有则取所有记录
//            page.setNowPage(paramBean.getNowPage());  //从参数中获取第几页，缺省为第1页
//        } else {
//            if (!page.contains(Constant.PAGE_SHOWNUM)) { //初始化每页记录数设定
//                if (paramBean.getQueryNoPageFlag()) { //设定了不分页参数
//                    page.setShowNum(0);
//                } else { //没有设定不分页，取服务设定的每页记录数
//                    page.setShowNum(50);
//                }
//            }
//        }

        //有审批记录说明已审批 撤回标识为false
        String currentUserCode = Context.getUserBean().getCode();
        String sql = "select a.*,(case when exists(select '' from ts_comm_mind b where b.DATA_ID=QJ_ID) then 'false' else 'true' end) as canRetract from TS_QJLB_QJ a where a.USER_CODE ='" + currentUserCode + "' order by a.QJ_DATE";
        List<Bean> dataList = Transaction.getExecutor().query(sql);//, page.getNowPage(), page.getShowNum(), null, null

//        String countSql = "select count(*) as count " + sql.substring(sql.indexOf("from TS_QJLB_QJ a"));
//        /*设置数据总数*/
//        int count = dataList.size();
//        int showCount = page.getShowNum();
//        boolean bCount; //是否计算分页
//        if ((showCount == 0) || paramBean.getQueryNoPageFlag()) {
//            bCount = false;
//        } else {
//            bCount = true;
//        }
        OutBean outBean = new OutBean();
//        if (bCount) { //进行分页处理
//            if (!page.contains(Constant.PAGE_ALLNUM)) { //如果有总记录数就不再计算
//                int allNum;
//                if ((page.getNowPage() == 1) && (count < showCount)) { //数据量少，无需计算分页
//                    allNum = count;
//                } else {
//                    allNum = Transaction.getExecutor().queryOne(countSql).getInt("COUNT");
//                }
//                page.setAllNum(allNum);
//            }
//            outBean.setCount(page.getAllNum()); //设置为总记录数
//        } else {
//            outBean.setCount(dataList.size());
//        }
        outBean.setData(dataList);
//        outBean.setPage(page);
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
        String qjId = paramBean.getStr("QJ_ID");
        Bean qjBean = ServDao.find(TSQJ_SERVID, qjId);
        Transaction.begin();
        try {
            //删除待办信息
            Bean whereBean = new Bean();
            List<Object> values = new ArrayList<Object>();
            values.add(qjId);
            whereBean.put(Constant.PARAM_PRE_VALUES, values);
            whereBean.put(Constant.PARAM_WHERE, "and DATA_ID =? ");
            ServDao.destroy(TODO_SERVID, whereBean);
            //删除附件
            String qjImg = qjBean.getStr("QJ_IMG");
            if (StringUtils.isNotBlank(qjImg)) {
                List<Bean> fileListBean = FileMgr.getFileListBean(TSQJ_SERVID, qjImg);
                if (fileListBean != null && fileListBean.size() > 0) {
                    for (Bean fileBean : fileListBean) {
                        FileMgr.deleteFile(fileBean.getId());
                    }
                }
            }
            //删除请假数据
            ServDao.destroy(TSQJ_SERVID, qjBean.getId());
            Transaction.commit();
        } catch (Exception e) {
            Transaction.rollback();
        }
        Transaction.end();
        return outBean;
    }

    /**
     * 发起请假申请
     *
     * @param paramBean
     * @return
     */
    public OutBean addData(ParamBean paramBean) throws ParseException {
        Transaction.begin();
        OutBean outBean = new OutBean();
        String servId = paramBean.getStr(Constant.PARAM_SERV_ID);

        //--获取前台传过来的值--
        String userName = paramBean.getStr("user_name");
        String qjTitle = paramBean.getStr("qjtitle");
        String buMen = paramBean.getStr("bumen");
        String qjReason = paramBean.getStr("qjreason");
        String userCode = paramBean.getStr("user_code");
//        String userWorkNum = paramBean.getStr("user_work_num");//人力资源编码
        String bmidStr = paramBean.getStr("bmids");
        String[] bmids = bmidStr.split(",");
        String qjimg = paramBean.getStr("qjimg");

        //获取项目id（xmId）
        String bmid = bmids[0];
        Bean bmBean = ServDao.find("TS_BMLB_BM", bmid);//todo 换个服务查询
        String xmId = (String) bmBean.get("XM_ID");
        String lbDate = (String) bmBean.get("LB_DATE");

        //请假bean
        Bean qjbean = new Bean();
        qjbean.set("QJ_TITLE", qjTitle);
        qjbean.set("QJ_REASON", qjReason);
        qjbean.set("QJ_DANWEI", buMen);
        qjbean.set("QJ_KSNAME", bmidStr);
        qjbean.set("XM_ID", xmId);
        qjbean.set("QJ_NAME", userName);
        qjbean.set("USER_CODE", userCode);//用户编码
        qjbean.set("QJ_IMG", qjimg);//证明材料（fileId ）
        qjbean.set("QJ_STATUS", "1");   //  1"审核中"; 2  "已通过";3 "未通过";
        qjbean.set("QJ_DATE", new SimpleDateFormat(dateFormatString).format(new Date()));
        qjbean.set("QJ_KSTIME", lbDate);//考试开始时间   todo  TS_BMSH_PASS  BM_ID  TS_BMLB_BM
        qjbean.set("S_DEPT", bmBean.get("S_DEPT"));
        qjbean.set("S_ODEPT", bmBean.get("S_ODEPT"));
        qjbean.set("S_TDEPT", bmBean.get("S_TDEPT"));
        Bean qjbd = ServDao.create(servId, qjbean);
        //获取到请假id
        //获取请假表单对象
//        String qjId = qjbd.getStr("QJ_ID");
        //起草人
        UserBean userBean = UserMgr.getUser(userCode);
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
     * 人工审批请假
     *
     * @param paramBean
     * @return
     */
    public OutBean updateData(Bean paramBean) throws ParseException {
        //获取前台传过来的值
//        String qj_status = paramBean.getStr("qjstatus");
//        String qjId = paramBean.getStr("qjid");

//        String sh_status = paramBean.getStr("shstatus");//同意:"1" 不同意:"2"
//        String sh_reason = paramBean.getStr("shreason");//审核内容
//        String isRetreat = paramBean.getStr(" ");//是否被退回
//        String paramTodoId = paramBean.getStr("todoId");//待办id

//        String sh_statusStr = sh_status.equals("1") ? "同意" : "不同意";
//        String user_longin = paramBean.getStr("userloginname");
//        String user_name = paramBean.getStr("username");
//        String s_dname = paramBean.getStr("deptname");
//        String usercode = paramBean.getStr("usercode");
        UserBean currentUser = Context.getUserBean();//当前用户
        return this.updateData2(paramBean, currentUser);
    }

    /**
     * 系统审核请假
     *
     * @param paramBean {shstatus:"2",shreason:...,isRetreat:"true",todoId:"todoId"}
     * @return OutBean
     */
    public OutBean updateDataBySystem(Bean paramBean) throws ParseException {
        //获取前台传过来的值
        /*String sh_status = paramBean.getStr("shstatus");//同意 不同意
        String sh_reason = paramBean.getStr("shreason");//审核内容
        String isRetreat = paramBean.getStr("isRetreat");//是否被退回*/
        String paramTodoId = paramBean.getStr("todoId");//待办id

//        String qj_status = paramBean.getStr("qjstatus");
//        String qjId = paramBean.getStr("qjid");

//        String sh_statusStr = sh_status.equals("1") ? "同意" : "不同意";
//        String user_longin = paramBean.getStr("userloginname");
//        String user_name = paramBean.getStr("username");
//        String s_dname = paramBean.getStr("deptname");
//        String usercode = paramBean.getStr("usercode");
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
    private OutBean updateData2(Bean paramBean, UserBean currentUser) throws ParseException {
        OutBean outBean = new OutBean();
//        UserBean currentUser = Context.getUserBean();//当前用户
        //获取前台传过来的值
//        String qj_status = paramBean.getStr("qjstatus");
//        String qjId = paramBean.getStr("qjid");
        String sh_status = paramBean.getStr("shstatus");//同意 不同意
        String sh_reason = paramBean.getStr("shreason");//审核内容
        String isRetreat = paramBean.getStr("isRetreat");//是否被退回
        String paramTodoId = paramBean.getStr("todoId");//待办id
//        String sh_statusStr = sh_status.equals("1") ? "同意" : "不同意";
//        String user_longin = paramBean.getStr("userloginname");
//        String user_name = paramBean.getStr("username");
//        String s_dname = paramBean.getStr("deptname");
//        String usercode = paramBean.getStr("usercode");
        String[] split = paramTodoId.split(",");
        for (String string : split) {
            if ("".equals(string)) {
                continue;
            }
            Transaction.begin();
            Bean todoBean = ServDao.find(TODO_SERVID, string);
            String nodeSteps;
            if (todoBean == null) {
                outBean.setError("待办已被处理，请返回！");
            } else {
                nodeSteps = (String) todoBean.get("NODE_STEPS");
                String qjId = (String) todoBean.get("DATA_ID");
                Bean qjbean = ServDao.find(TSQJ_SERVID, qjId);


                //1、保存评审意见
                //添加审核意见信息
                Bean shyjBean = new Bean();
                shyjBean.set("DATA_ID", qjId);
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
                List<Bean> todoList = ServDao.finds(TODO_SERVID, "and DATA_ID = '" + qjId + "'");
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
                String qj_status;
                if ("true".equals(isRetreat)) {
                    qj_status = "3";
                } else if ("1".equals(nodeSteps)) {
                    qj_status = "2";
                } else {
                    qj_status = "1";
                }

                afterApply(outBean, qjbean, qj_status);

                if ("1".equals(qj_status)) {
                    int nodeSteps1 = Integer.parseInt((String) todoBean.get("NODE_STEPS"));
                    doFlowTask(outBean, currentUser, qjbean, nodeSteps1);
                }

                if (outBean.get(Constant.RTN_MSG) != null
                        && ((String) outBean.get(Constant.RTN_MSG)).indexOf(Constant.RTN_MSG_ERROR) > 0) {
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
     * 流程结束 进行相关操作
     *
     * @param outBean
     * @param qjbean
     * @param qj_status
     */
    private void afterApply(OutBean outBean, Bean qjbean, String qj_status) throws ParseException {
        String qjId = qjbean.getStr("QJ_ID");
        qjbean.set("QJ_STATUS", qj_status);
        ServDao.update(TSQJ_SERVID, qjbean);
        try {
            if ("2".equals(qj_status) || "3".equals(qj_status)) {
                //流程结束 发送消息
                //TS_QJ_RESULT_TIP 请假结果提醒语
                String qjResultMsg = ConfMgr.getConf("TS_QJ_RESULT_TIP", "您的请假申请，有了审批结果，可登录工商银行考试查看。");
                String qjTitle = qjbean.getStr("QJ_TITLE");
                String qjResult = ("2".equals(qj_status)) ? "通过" : "不通过";
                qjResultMsg = qjResultMsg
                        .replaceAll("#QJ_TITLE#", qjTitle)
                        .replaceAll("#QJ_RESULT#", qjResult);
                Bean qjResultTipBean = new Bean();
                qjResultTipBean.set("USER_CODE", qjbean.getStr("USER_CODE"));
                qjResultTipBean.set("tipMsg", qjResultMsg);
                new KSSendTipMessageServ().sendTipMessageBeanForICBC(qjResultTipBean, "qjResult");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("请假结果提醒失败，" + "QJ_ID:" + qjId + ",USER_CODE:" + qjbean.getStr("USER_CODE"));
        }

        if ("2".equals(qj_status)) {
            //请假已通过 修改 TS_BMSH_PASS BM_STATUS字段信息
            String qjKsname = qjbean.getStr("QJ_KSNAME");
            String[] bmIds = qjKsname.split(",");
            StringBuilder shIdStr = new StringBuilder();
            for (String bmId : bmIds) {
                ParamBean queryParamBean = new ParamBean();
                queryParamBean.set("BM_ID", bmId);
                Bean bean = ServDao.find(TS_BMSH_PASS_SERVID, queryParamBean);
                if (bean == null) {
                    continue;
                }

                shIdStr.append(",").append(bean.getStr("SH_ID"));

                if ("2".equals(bean.getStr("BM_STATUS"))) {
                    bean.set("BM_STATUS", "3");
                } else {
                    bean.set("BM_STATUS", "1");
                }
                ServDao.update(TS_BMSH_PASS_SERVID, bean);
            }
            if (shIdStr.length() > 0) {
                shIdStr = new StringBuilder(shIdStr.substring(1));
            }

            String xmId = qjbean.getStr("XM_ID");
            Bean xmBean = ServDao.find(TsConstant.SERV_XMGL, xmId);
            String xmKsStartDate = xmBean.getStr("XM_KSSTARTDATA"); //项目详情考试开始时间
            Date date = DateUtils.parseDate(xmKsStartDate);
            //如果在提交场次安排前，请假成功删除考位安排
            String[] split = shIdStr.toString().split(",");
            for (String s : split) {
                List<Object> values = new ArrayList<Object>();
                values.add(s);
                Bean whereBean = new Bean();
                whereBean.set(Constant.PARAM_WHERE, " and SH_ID =? and (IS_SUBMIT!='1' or IS_SUBMIT is null)");
                whereBean.set(Constant.PARAM_PRE_VALUES, values);
                ServDao.destroy(TsConstant.SERV_KCAP_YAPZW, whereBean);
            }
//                StringBuilder shIdSql = new StringBuilder();
//                List<Object> values = new ArrayList<Object>();
//                for (String s : split) {
//                    shIdSql.append(",?");
//                    values.add(s);
//                }
//                if (shIdSql.length() > 0) {
//                    shIdSql = new StringBuilder(shIdSql.substring(1));
//                }
//                Bean whereBean = new Bean();
//                whereBean.set(Constant.PARAM_WHERE, " and SH_ID in(" + shIdSql.toString() + ")");
//                whereBean.set(Constant.PARAM_PRE_VALUES, values);
//                ServDao.destroy(TsConstant.SERV_KCAP_YAPZW, whereBean);

            //请假通过 修改请假次数和请假周数
            ParamBean getQxBean = new ParamBean();
            getQxBean.put("bmids", qjKsname);
            getQxBean.put("xm_id", qjbean.getStr("XM_ID"));
            getQxBean.put("user_code", qjbean.getStr("USER_CODE"));
            getQxBean.put("cishu", ConfMgr.getConf("TS_KSQJ_SETCONUTS", "0"));
            getQxBean.put("zhoushu", ConfMgr.getConf("TS_KSQJ_WEEK_MAXNUM", "0"));
            try {
                Bean result = ServMgr.act(TS_BM_QJ_NUM_SERVID, "getQx", getQxBean);
                if (!"true".equals(result.getStr("yes"))) {
                    outBean.setError((String) result.get(Constant.RTN_MSG));
                }
            } catch (Exception e) {
                e.printStackTrace();
                outBean.setError("发起人请假次数已超出规定，审批失败");
            }
        }
    }

//    public OutBean getQx2(ParamBean paramBean){
//        return new OutBean();
//    }

    /**
     * 处理流程
     *
     * @param outBean  outBean
     * @param userBean 当前处理人（一般是当前用户）
     * @param qjbean   请假Bean
     * @param level    流程当前级别
     */

    private void doFlowTask(OutBean outBean, UserBean userBean, Bean qjbean, int level) throws ParseException {
        String qjId = (String) qjbean.get("QJ_ID");
        String qjTitle = (String) qjbean.get("QJ_TITLE");
        String xmId = (String) qjbean.get("XM_ID");
        String examerUserCode = (String) qjbean.get("USER_CODE");//考生工号
        String shrUserCode = userBean.getCode();

        ParamBean flowParamBean = new ParamBean();
        flowParamBean.set("examerUserCode", examerUserCode);
        flowParamBean.set("shrUserCode", shrUserCode);//起草节点 shrWorekNum 传 examerWorekNum
        flowParamBean.set("level", level);//不懂  默认给0吧
        flowParamBean.set("deptCode", qjbean.getStr("S_DEPT"));
        flowParamBean.set("odeptCode", qjbean.getStr("S_ODEPT"));
        flowParamBean.set("xmId", xmId);
        flowParamBean.set("flowName", 3); //1:报名审核流程 2:异地借考流程 3:请假审核流程
        OutBean shBean = ServMgr.act("TS_WFS_APPLY", "backFlow", flowParamBean);
//        new FlowServ().backFlow(flowParamBean);
        List<Bean> shList = shBean.getList("result");
        outBean.putAll(shBean);
        if (shBean.getMsg().contains(Language.trans("未绑定流程"))) {
            //未绑定流程，默认审批通过
            outBean.set("shrNames", "系统");
            outBean.setMsg("");
            //添加审批意见（TS_COMM_MIND）
            Bean shyjBean = new Bean();
            shyjBean.set("DATA_ID", qjId);
            shyjBean.set("SH_TYPE", "1");//审核类别 1 意见 2 审核记录
            shyjBean.set("SH_MIND", "系统默认通过");//意见内容

            shyjBean.set("SH_NODE", "系统环节");//审核层级名称
            shyjBean.set("SH_LEVEL", "1");//审核层级

            shyjBean.set("SH_STATUS", "1");//审核状态// 同意 不同意
            shyjBean.set("SH_UCODE", "");//审核人UID(人力资源编码)
            shyjBean.set("SH_ULOGIN", "");//审核人登陆名
            shyjBean.set("SH_UNAME", "系统");//审核人姓名
            shyjBean.set("S_DNAME", "");//审核人部门名称
            ServDao.save(COMM_MIND_SERVID, shyjBean);

            afterApply(outBean, qjbean, "2");//2 审批通过
        } else if (CollectionUtil.isEmpty(shList)) {
            if (shList != null && shList.size() == 0) {
                outBean.setError("没有审核人，请联系管理员！");
            }
        } else {
            int nodeSteps = shBean.getInt("NODE_STEPS");
            String nodeName = shBean.getStr("NODE_NAME");
            String wfsId = shBean.getStr("WFS_ID");

            StringBuilder shrNames = new StringBuilder();
            for (Bean bean : shList) {
                String shrName = (String) bean.get("SHR_NAME");
                String shrUserCode2 = (String) bean.get("SHR_USERCODE");
                shrNames.append(" ").append(shrName);
                //推送人
                UserBean shrUserBean = UserMgr.getUser(shrUserCode2);
                String shrDeptCode = shrUserBean.getDeptCode();
                String shrUserDeptName = shrUserBean.getDeptName();
//            String shrOdeptCode = shrUserBean.getODeptCode();

                Bean todoBean = new Bean();
                todoBean.set("TITLE", qjTitle);
                todoBean.set("TYPE", "0");//待办类型 0 请假 1借考
                todoBean.set("DATA_ID", qjId);
                todoBean.set("NODE_STEPS", nodeSteps + "");//当前所在的流程级别
                todoBean.set("NODE_NAME", nodeName);//当前所在的流程节点名称
                todoBean.set("WFS_ID", wfsId);//流程id
//            todoBean.set("SERV_ID", "0");//???
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
                //todoBean.set("DONE_TIME", "0");//???
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
                sbu.append(",'").append(bmids[i]).append("'");
            }
        }
//        "TS_BMSH_PASS"
        List<Bean> tsBmshPassList = ServDao.finds("TS_BMLB_BM", "and BM_ID in(" + sbu.toString() + ")");//userCode
        for (Bean tsBmshPass : tsBmshPassList) {
            //通过TS_BMLB_BM表，获取标题和考试开始时间信息
            String bmId = (String) tsBmshPass.get("BM_ID");
            Bean bmBean = ServDao.find("TS_BMLB_BM", bmId);
            String bmTitle = (String) bmBean.get("BM_TITLE");
//            String bmLb = (String) bmBean.get("BM_LB");
            String bmXl = (String) bmBean.get("BM_XL");
            String bmMk = (String) bmBean.get("BM_MK");
            String bmType = (String) bmBean.get("BM_TYPE");
            String lbDate = (String) bmBean.get("LB_DATE");
            String bm_bt = BMUtil.getExaminationName(bmType, bmXl, bmMk);
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
//        String userWorkNum = (String) paramBean.get("USER_WORK_NUM");//用户人力资源编码
        String userCode = paramBean.getStr("USER_CODE");
        String xmId = paramBean.getStr("XM_ID");

        List<Bean> tsBmshPassList = ServDao.finds("TS_BMSH_PASS", "and BM_CODE='" + userCode + "'and  XM_ID ='" + xmId + "'");//userCode
        for (Iterator<Bean> iterator = tsBmshPassList.iterator(); iterator.hasNext(); ) {
            Bean tsBmshPass = iterator.next();
//            String xmId = (String) tsBmshPass.get("XM_ID");
            String bmId = (String) tsBmshPass.get("BM_ID");

            List<Bean> queryQjList = ServDao.finds(TSQJ_SERVID, "and QJ_KSNAME like '%" + bmId + "%' and QJ_STATUS in('1','2')");
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
//            String bmLb = (String) bmBean.get("BM_LB");
                String bmXl = (String) bmBean.get("BM_XL");
                String bmMk = (String) bmBean.get("BM_MK");
                String bmType = (String) bmBean.get("BM_TYPE");
                String lbDate = (String) bmBean.get("LB_DATE");
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
//        String xmId = paramBean.getStr("XM_ID");
        OutBean outBean = new OutBean();
        List<Bean> xmBeanList = Transaction.getExecutor().query("select * from TS_XMGL a" +
                //进行中的项目  &&  项目中存在可以申请请假的报名（存在不在申请中/申请通过的请假 && 由场次安排引入的考试忽略）
                " where now() BETWEEN str_to_date(XM_START,'%Y-%m-%d %H:%i:%s') and str_to_date(XM_END,'%Y-%m-%d %H:%i:%s') " +
                "and exists( " +
                "select * from TS_BMSH_PASS pass " +
                "where not EXISTS(select '' from TS_QJLB_QJ qj where qj.QJ_KSNAME like CONCAT('%',pass.BM_ID,'%') and qj.QJ_STATUS in('1','2')) " +
                "and pass.BM_ID !='' and pass.BM_ID is not null and a.XM_ID =pass.XM_ID and BM_CODE ='" + userCode + "')");

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
     * 查询该报名是否有在请假申请时间内
     *
     * @param xmId
     * @return
     */
    private boolean inApplyTime(String xmId) throws ParseException {
        boolean result = false;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatString);

        List<Bean> xmSzList = ServDao.finds("TS_XMGL_SZ", "and XM_ID ='" + xmId + "' and XM_SZ_NAME ='请假'");
        if (xmSzList.size() > 0) {
            //项目中有请假模块
            Bean xmSz = xmSzList.get(0);
            String xmSzType = xmSz.getStr("XM_SZ_TYPE");
            if ("进行中".equals(xmSzType)) {
                //请假开放中
                String xmSzId = xmSz.getId();
                List<Bean> tsXmglQjglList = ServDao.finds("TS_XMGL_QJGL", "and XM_SZ_ID ='" + xmSzId + "'");
                if (tsXmglQjglList.size() > 0) {
                    String qjStadate = (String) tsXmglQjglList.get(0).get("QJ_STADATE");
                    String qjEnddate = (String) tsXmglQjglList.get(0).get("QJ_ENDDATE");
                    //在申请时间内
                    if (StringUtils.isBlank(qjStadate) || StringUtils.isBlank(qjEnddate)) {
                        result = false;
                    } else if (new Date().getTime() > getTime(qjStadate) && new Date().getTime() < getTime(qjEnddate)) {
                        result = true;
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
     * 请假次数统计
     */
    public OutBean getLeaveCount(ParamBean paramBean) {
        String userCode = paramBean.getStr("USER_CODE");
        //今年审批过的请假
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        String where = "and QJ_CODE = '" + userCode + "'and S_ATIME like '" + year + "%'";
        List<Bean> beanList = ServDao.finds(TS_BM_QJ_NUM_SERVID, where);
        Bean beanNum = null;
        if (beanList != null && beanList.size() > 0) {
            beanNum = beanList.get(0);
        }
        OutBean nBean = new OutBean();
        if (beanNum != null) {
            nBean.set("weekNum", beanNum.getStr("WEEK_NUM"));//获取周数
            nBean.set("ciShuNum", beanNum.getStr("CISHU_NUM"));//获取次数
        } else {
            nBean.set("weekNum", 0);//获取周数
            nBean.set("ciShuNum", 0);//获取次数

        }
        return nBean;

    }

    /**
     * 获取所有的请假待审批
     */
    public OutBean getQjData(Bean paramBean) {
        Bean _PAGE_ = new Bean();
        OutBean outBean = new OutBean();
        String NOWPAGE = paramBean.getStr("nowpage");
        String SHOWNUM = paramBean.getStr("shownum");
        UserBean user = Context.getUserBean();
        String user_code = user.getCode();
        String type = paramBean.getStr("type");
        List<Bean> qjlist = ServDao.finds("TS_COMM_TODO", "AND OWNER_CODE='" + user_code + "' AND TYPE='" + type + "'");
        for (Bean bean : qjlist) {
            String id = bean.getStr("XM_ID");
            if (type.equals("0")) {
                List<Bean> qjdatelist = ServDao.finds("TS_XMGL_QJGL", "and XM_ID='" + id + "'");
                if (qjdatelist.size() != 0) {
                    String start = qjdatelist.get(0).getStr("QJ_STADATE");
                    String end = qjdatelist.get(0).getStr("QJ_ENDDATWE");
                    bean.set("start", start);
                    bean.set("end", end);
                } else {
                    bean.set("start", "");
                    bean.set("end", "");
                }
            } else if (type.equals("1")) {
                //借考数据
                List<Bean> qjdatelist = ServDao.finds("TS_XMGL_YDJK", "and XM_ID='" + id + "'");
                if (qjdatelist.size() != 0) {
                    String start = qjdatelist.get(0).getStr("YDJK_STADATE");
                    String end = qjdatelist.get(0).getStr("YDJK_ENDDATE");
                    bean.set("start", start);
                    bean.set("end", end);
                } else {
                    bean.set("start", "");
                    bean.set("end", "");
                }
            }
        }

        int ALLNUM = qjlist.size();
        // 计算页数
        int meiye = Integer.parseInt(SHOWNUM);
        int yeshu = ALLNUM / meiye;
        int yushu = ALLNUM % meiye;
        // 获取总页数
        if (yushu != 0) {
            yeshu += 1;
        }

        int nowpage = Integer.parseInt(NOWPAGE);
        int showpage = Integer.parseInt(SHOWNUM);
        // 计算第一项 开始
        int chushi = (nowpage - 1) * showpage + 1;
        // 计算结束项
        int jieshu = (nowpage - 1) * showpage + showpage;
        // 放到Array中
        List<Bean> list2 = new ArrayList<Bean>();
        if (ALLNUM == 0) {
            // 没有数据
        } else {

            if (jieshu <= ALLNUM) {
                // 循环将数据放入list2中返回给前台
                for (int i = chushi; i <= jieshu; i++) {
                    list2.add(qjlist.get(i - 1));
                }

            } else {
                for (int j = chushi; j < ALLNUM + 1; j++) {
                    list2.add(qjlist.get(j - 1));
                }
            }
        }
        outBean.set("list", list2);
        _PAGE_.set("ALLNUM", qjlist.size());
        _PAGE_.set("NOWPAGE", NOWPAGE);
        _PAGE_.set("PAGES", yeshu);
        outBean.set("_PAGE_", _PAGE_);
        outBean.set("first", chushi);
        outBean.set("datalist", qjlist);
        return outBean;

    }


    /**
     * 获取所有的请假待审批  首页 展示
     */
    public OutBean getQtData(Bean paramBean) {

        UserBean user = Context.getUserBean();
        String user_code = user.getCode();
        String type = paramBean.getStr("type");
        List<Bean> qjlist = ServDao.finds("TS_COMM_TODO", "AND OWNER_CODE='" + user_code + "' AND TYPE='" + type + "'");
        for (Bean bean : qjlist) {
            String id = bean.getStr("XM_ID");
            if (type.equals("0")) {
                List<Bean> qjdatelist = ServDao.finds("TS_XMGL_QJGL", "and XM_ID='" + id + "'");
                if (qjdatelist.size() != 0) {
                    String start = qjdatelist.get(0).getStr("QJ_STADATE");
                    String end = qjdatelist.get(0).getStr("QJ_ENDDATWE");
                    bean.set("start", start);
                    bean.set("end", end);
                } else {
                    bean.set("start", "");
                    bean.set("end", "");
                }
            } else if (type.equals("1")) {
                //借考数据
                List<Bean> qjdatelist = ServDao.finds("TS_XMGL_YDJK", "and XM_ID='" + id + "'");
                if (qjdatelist.size() != 0) {
                    String start = qjdatelist.get(0).getStr("YDJK_STADATE");
                    String end = qjdatelist.get(0).getStr("YDJK_ENDDATE");
                    bean.set("start", start);
                    bean.set("end", end);
                } else {
                    bean.set("start", "");
                    bean.set("end", "");
                }
            }
        }


        return new OutBean().set("datalist", qjlist);

    }

}