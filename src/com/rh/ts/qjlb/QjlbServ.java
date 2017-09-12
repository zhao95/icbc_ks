package com.rh.ts.qjlb;

import com.icbc.ctp.utility.CollectionUtil;
import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.*;
import com.rh.core.util.Constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class QjlbServ extends CommonServ {

    private final static String TSQJ_SERVID = "TS_QJLB_QJ";
    private final static String TODO_SERVID = "TS_COMM_TODO";
    private final static String DONE_SERVID = "TS_COMM_TODO_DONE";
    private final static String COMM_MIND_SERVID = "TS_COMM_MIND";
    private final static String TSQJ_BM_SERVID="TS_QJLB_BM";
    private final static String dateFormatString = "yyyy-MM-dd HH:mm:ss";

    /**
     * 发起请假申请
     *
     * @param paramBean
     * @return
     */
    public OutBean addData(ParamBean paramBean) {
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
     * 审核请假
     *
     * @param paramBean
     * @return
     */
    public OutBean updateData(Bean paramBean) {
        Transaction.begin();
        OutBean outBean = new OutBean();

        String servId = paramBean.getStr(Constant.PARAM_SERV_ID);
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
        UserBean currentUser = Context.getUserBean();//当前用户
        Bean todoBean = ServDao.find(TODO_SERVID, paramTodoId);
        String nodeSteps;
        if (todoBean == null) {
            outBean.setError("待办已被处理，请返回！");
            return outBean;
        } else {
            nodeSteps = (String) todoBean.get("NODE_STEPS");
        }
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
        qjbean.set("QJ_STATUS", qj_status);
        ServDao.update(servId, qjbean);

        if ("1".equals(qj_status)) {
            int nodeSteps1 = Integer.parseInt((String) todoBean.get("NODE_STEPS"));
            doFlowTask(outBean, currentUser, qjbean, nodeSteps1);
        }

        if (outBean.get(Constant.RTN_MSG) != null
                && ((String) outBean.get(Constant.RTN_MSG)).indexOf(Constant.RTN_MSG_ERROR) > 0) {
            //有错误回滚
            Transaction.rollback();
        } else {
            Transaction.commit();
        }
        Transaction.end();
        return outBean;
    }

    /**
     * 处理流程
     *
     * @param outBean  outBean
     * @param userBean 当前处理人（一般是当前用户）
     * @param qjbean   请假Bean
     * @param level    流程当前级别
     */

    private void doFlowTask(OutBean outBean, UserBean userBean, Bean qjbean, int level) {
        String qjId = (String) qjbean.get("QJ_ID");
        String qjTitle = (String) qjbean.get("QJ_TITLE");
        String xmId = (String) qjbean.get("XM_ID");
        String examerUserCode = (String) qjbean.get("USER_CODE");//考生工号
        String shrUserCode = userBean.getCode();

        ParamBean flowParamBean = new ParamBean();
        flowParamBean.set("examerUserCode", examerUserCode);
        flowParamBean.set("shrUserCode", shrUserCode);//起草节点 shrWorekNum 传 examerWorekNum
        flowParamBean.set("level", level);//不懂  默认给0吧

        flowParamBean.set("xmId", xmId);
        flowParamBean.set("flowName", 3); //1:报名审核流程 2:异地借考流程 3:请假审核流程
        OutBean shBean = ServMgr.act("TS_WFS_APPLY", "backFlow", flowParamBean);
//        new FlowServ().backFlow(flowParamBean);
        List<Bean> shList = (List<Bean>) shBean.get("result");
        outBean.putAll(shBean);
        if (CollectionUtil.isEmpty(shList)) {
            if (shList != null && shList.size() == 0) {
                outBean.setError("没有审核人，请联系管理员！");
            }
        } else {
            int nodeSteps = (int) shBean.get("NODE_STEPS");
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
                todoBean.set("TYPE", "0");//待办类型 1 请假 2借考  (? 0 请假)
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
                String bmid = bmids[i];
                sbu.append(",'").append(bmids[i]).append("'");
            }
        }
//        "TS_BMSH_PASS"
        List<Bean> tsBmshPassList = ServDao.finds("TS_BMLB_BM", "and BM_ID in(" + sbu.toString() + ")");//userCode
        for (Bean tsBmshPass : tsBmshPassList) {
            String xmId = (String) tsBmshPass.get("XM_ID");
            //通过TS_BMLB_BM表，获取标题和考试开始时间信息
            String bmId = (String) tsBmshPass.get("BM_ID");
            Bean bmBean = ServDao.find("TS_BMLB_BM", bmId);
            String bmTitle = (String) bmBean.get("BM_TITLE");
//            String bmLb = (String) bmBean.get("BM_LB");
            String bmXl = (String) bmBean.get("BM_XL");
            String bmMk = (String) bmBean.get("BM_MK");
            String bmType = (String) bmBean.get("BM_TYPE");
            String lbDate = (String) bmBean.get("LB_DATE");
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
//        String userWorkNum = (String) paramBean.get("USER_WORK_NUM");//用户人力资源编码
        String userCode = (String) paramBean.get("USER_CODE");

        List<Bean> tsBmshPassList = ServDao.finds("TS_BMSH_PASS", "and BM_CODE='" + userCode + "'");//userCode
        //todo 后续数据量越来越来多 这样每个都判断是不是不好 1、通过sql过滤
        for (Iterator<Bean> iterator = tsBmshPassList.iterator(); iterator.hasNext(); ) {
            Bean tsBmshPass = iterator.next();
            String xmId = (String) tsBmshPass.get("XM_ID");
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
                String bm_bt = bmType + "-" + bmXl + "-" + bmMk;
                String title = "";
                if (!"".equals(bmMk)) {
                    title = bm_bt;
                } else {
                    title = bmTitle;
                }
                tsBmshPass.set("lbDate", lbDate);
                tsBmshPass.set("title", title);
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        List<Bean> xmSzList = ServDao.finds("TS_XMGL_SZ", "and XM_ID ='" + xmId + "' and XM_SZ_NAME ='请假'");
        if (xmSzList.size() > 0) {
            Bean xmSz = xmSzList.get(0);
            String xmSzId = xmSz.getId();
            List<Bean> tsXmglQjglList = ServDao.finds("TS_XMGL_QJGL", "and XM_SZ_ID ='" + xmSzId + "'");
            if (tsXmglQjglList.size() > 0) {
                String qjStadate = (String) tsXmglQjglList.get(0).get("QJ_STADATE");
                String qjEnddate = (String) tsXmglQjglList.get(0).get("QJ_ENDDATE");
                //在申请时间内
                if (new Date().getTime() > sdf.parse(qjStadate).getTime() && new Date().getTime() < sdf.parse(qjEnddate).getTime()) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * 请假次数统计
     */
    public int getLeaveCount(String userCode) {
        //今年审批过的请假
        String where = "and USER_CODE = '" + userCode + "' and QJ_STATUS = '2'" +
                " and to_date(QJ_DATE,'yyyy-MM-dd hh24:mi:ss') between to_date(to_char(sysdate, 'yyyy' )||'-01-01','yyyy-mm-dd') and to_date((to_char(sysdate, 'yyyy' )+1)||'-01-01','yyyy-mm-dd')";
        List<Bean> queryQjList = ServDao.finds(TSQJ_SERVID, where);//获得当前已经请假的数据
        //2个考试周   请假场次6   6个考试   考前、考后多个考试请假算一次
       //1遍历是否超过请假两个周期
//        //if(){
//        	
//        }
//        for (Bean queryQj : queryQjList) {
//        	
//            String qjDate = queryQj.getStr("QJ_ID");
//
//        }
//
        return 0;
    }
//    
    /**
     *通过QJ_ID获取考试名称日期
     */
    public  boolean getBooleanWeek(String qjid){
    	String  whereTsqjBm="and QJ_ID='"+qjid+"'";
    	List<Bean> ksList=ServDao.finds(TSQJ_BM_SERVID,whereTsqjBm);
    	List<String>   dateList=new ArrayList();
    	for(Bean ks: ksList ){
    		String lbDate=ks.getStr("LB_DATE");
    		dateList.add(lbDate);
    	}
    	return  true;
    }
    
    
}