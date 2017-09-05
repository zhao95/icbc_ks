package com.rh.ts.jklb;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


import com.rh.core.base.Context;
import com.icbc.ctp.utility.CollectionUtil;
import com.icbc.ctp.utility.StringUtil;
import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.util.Constant;
import com.rh.ts.flow.FlowServ;


public class JklbServ extends CommonServ {
	 private final static String TSJK_SERVID = "TS_JKLB_JK";
	 private final static String TODO_SERVID = "TS_COMM_TODO";
	 private final static String DONE_SERVID = "TS_COMM_TODO_DONE";
	 private final static String COMM_MIND_SERVID = "TS_COMM_MIND";
	
	 private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 发起借考申请 
	 * @param paramBean
	 * @return
	 */
	public OutBean  addData(ParamBean paramBean){
		Transaction.begin();
		 OutBean outBean = new OutBean();
		 String servId = paramBean.getStr(Constant.PARAM_SERV_ID);
		//String userWorkNum = paramBean.getStr("user_work_num");
		 String userName = paramBean.getStr("user_name");
		 String jkTitle = paramBean.getStr("jktitle");
		 String jkYiJi = paramBean.getStr("jkyiji");
		 String jkCity = paramBean.getStr("jkcity");
		 String jkUserCode = paramBean.getStr("user_code");//人力资源
		 String jkBumen = paramBean.getStr("bumen");
		 String jkimg = paramBean.getStr("jkimg");
		 String jkReason = paramBean.getStr("jkreason");
		 String bmidStr = paramBean.getStr("bmids");
        String[] bmids = bmidStr.split(",");
        //获取XM_ID
        String bmid = bmids[0];
        Bean bmBean = ServDao.find("TS_BMLB_BM", bmid);
        String xmId =  bmBean.getStr("XM_ID");   
        String lbDate =  bmBean.getStr("JKLB_DATE");		
        
      //借考
        Bean jkbean = new Bean();
        jkbean.set("JK_TITLE", jkTitle);
        jkbean.set("JK_YIFH", jkYiJi);
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
        jkbean.set("JK_DATE", sdf.format(new Date()));
        jkbean.set("JK_KSTIME", lbDate);//考试开始时间   todo  TS_BMSH_PASS  BM_ID  TS_BMLB_BM
        //保存到ts_JKLB_JK库里
        Bean qjbd = ServDao.create(servId, jkbean);
       //起草人
        UserBean  userBean=UserMgr.getUser(jkUserCode);
        doFlowTask(outBean, userBean, qjbd, 0);
        if (outBean.get(Constant.RTN_MSG) != null
                && ((String) outBean.get(Constant.RTN_MSG)).contains(Constant.RTN_MSG_ERROR)) {
            //有错误回滚
            Transaction.rollback();
        } else {
            Transaction.commit();
        }
        Transaction.end();
        return  outBean;
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
	        String  servId=paramBean.getStr(Constant.PARAM_SERV_ID);
	        String sh_status = paramBean.getStr("shstatus");//同意 不同意
	        String sh_reason = paramBean.getStr("shreason");//审核内容
	        String isRetreat = paramBean.getStr("isRetreat");//是否被退回
	        String paramTodoId = paramBean.getStr("todoId");//待办id
	       // sh_status = sh_status.equals("1") ? "同意" : "不同意";
	        UserBean  currentUser= Context.getUserBean();//
	        Bean todoBean = ServDao.find(TODO_SERVID, paramTodoId);
	        String nodeSteps;
	        if (todoBean == null) {
	            outBean.setError("待办已被处理，请返回！");
	            return outBean;
	        } else {
	            nodeSteps = todoBean.getStr("NODE_STEPS");
	        }
	        String jkId =  todoBean.getStr("DATA_ID");
	        Bean jkbean = ServDao.find(TSJK_SERVID, jkId);
	        
	        //1、保存评审意见
	        //添加审核意见信息
	        Bean shyjBean = new Bean();
	        shyjBean.set("DATA_ID", jkId);
	        shyjBean.set("SH_TYPE", "1");//审核类别 1 意见 2 审核记录
	        shyjBean.set("SH_MIND", sh_reason);//意见内容

	        shyjBean.set("SH_NODE", nodeSteps + "todo 获取所在节点名称");//审核层级名称  //todo 获取所在节点名称
	        shyjBean.set("SH_LEVEL", nodeSteps);//审核层级

	        shyjBean.set("SH_STATUS", sh_status);//审核状态// 同意 不同意
	        shyjBean.set("SH_UCODE", currentUser.getWorkNum());//审核人UID(人力资源编码)
	        shyjBean.set("SH_ULOGIN", currentUser.getLoginName());//审核人登陆名
	        shyjBean.set("SH_UNAME", currentUser.getName());//审核人姓名
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
	        ServDao.update(servId, jkbean);

	        if ("1".equals(jk_status)) {
	            int nodeSteps1 = Integer.parseInt( todoBean.getStr("NODE_STEPS"));
	            doFlowTask(outBean, currentUser, jkbean, nodeSteps1);
	        }

	        if (outBean.get(Constant.RTN_MSG) != null
	                && (outBean.getStr(Constant.RTN_MSG)).indexOf(Constant.RTN_MSG_ERROR) > 0) {
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

    private void doFlowTask(OutBean outBean, UserBean userBean, Bean jkbean, int level) {
    	
    	String jkId = jkbean.getStr("JK_ID");
        String jkTitle = jkbean.getStr("JK_TITLE");
        String xmId = jkbean.getStr("XM_ID");
        String examerUserCode = jkbean.getStr("USER_CODE");
        String shUserCode = userBean.getCode();
       
        ParamBean  flowParamBean =new  ParamBean();
        //form表单传给后台一个bean，只包括借考一级分行
        flowParamBean.set("form", jkbean);
        flowParamBean.set("examerUserCode", examerUserCode);
        flowParamBean.set("shrWorekNum", shUserCode);//起草节点examerWorkNum传shrWorkNum
        flowParamBean.set("level", level);
        flowParamBean.set("xmId", xmId);
        flowParamBean.set("flowName", 2); //1:报名审核流程 2:异地借考流程 3:请假审核流程
        OutBean shBean=ServMgr.act("TS_WFS_APPLY","backFlow",flowParamBean);
       List<Bean> shList= shBean.getList("result");
       outBean.putAll(shBean);
       if (CollectionUtil.isEmpty(shList)) {
    	   if (shList != null && shList.size() == 0) {
               outBean.setError("没有审核人，请联系管理员！");
           }
       }  else{
    	   int  nodeSteps=shBean.getInt("NODE_STEPS");
       
       StringBuilder  shrNames=new  StringBuilder();
       for (Bean  bean :shList){
    	   String  shrName= bean.getStr("SHR_NAME");
    	   String shrUserCode2 = bean.getStr("SHR_USERCODE");
    	   shrNames.append(" ").append(shrName);
    	   //推送人
    	   UserBean shrUserBean = UserMgr.getUserByWorkNum(shrUserCode2);
           String shrDeptCode = shrUserBean.getDeptCode();
           String shrUserDeptName = shrUserBean.getDeptName();
           
           Bean todoBean = new Bean();
           todoBean.set("TITLE", jkTitle);
           todoBean.set("TYPE", "2");//待办类型 1 请假 2借考  
           todoBean.set("DATA_ID",jkId);
           todoBean.set("NODE_STEPS", nodeSteps + "");//当前所在的流程级别
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
           todoBean.set("SEND_TIME", sdf.format(new Date()));
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
//    "TS_BMSH_PASS"
    List<Bean> tsBmshPassList = ServDao.finds("TS_BMLB_BM", "and BM_ID in(" + sbu.toString() + ")");//userWorkNum
    for (Bean tsBmshPass : tsBmshPassList) {
        String xmId =  tsBmshPass.getStr("XM_ID");
        //通过TS_BMLB_BM表，获取标题和考试开始时间信息
        String bmId = tsBmshPass.getStr("BM_ID");
        Bean bmBean = ServDao.find("TS_BMLB_BM", bmId);
        String bmTitle =  bmBean.getStr("BM_TITLE");
//        String bmLb = (String) bmBean.get("BM_LB");
        String bmXl =  bmBean.getStr("BM_XL");
        String bmMk =  bmBean.getStr("BM_MK");
        String bmType =  bmBean.getStr("BM_TYPE");
        String lbDate =  bmBean.getStr("LB_DATE");
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

    List<Bean> xmSzList = ServDao.finds("TS_XMGL_SZ", "and XM_ID ='" + xmId + "' and XM_SZ_NAME ='异地借考'");
    if (xmSzList.size() > 0) {
        Bean xmSz = xmSzList.get(0);
        String xmSzId = xmSz.getId();
        List<Bean> tsXmglQjglList = ServDao.finds("TS_XMGL_YDJK", "and XM_SZ_ID ='" + xmSzId + "'");
        if (tsXmglQjglList.size() > 0) {
            String qjStadate = (String) tsXmglQjglList.get(0).get("YDJK_STADATE");
            String qjEnddate = (String) tsXmglQjglList.get(0).get("YDJK_ENDDATE");
            //在申请时间内
            if (new Date().getTime() > sdf.parse(qjStadate).getTime() && new Date().getTime() < sdf.parse(qjEnddate).getTime()) {
                result = true;
            }
        }
    }
    return result;
}

	
//	/**//这个API是考生可以借考的信息
//	 public OutBean toApplyExam(ParamBean Parambean) throws Exception {
//		 OutBean  outbean=new OutBean();
//		List<Bean>  beanlist=new  ArrayList<Bean>();  
//		String worknum= Parambean.getStr("USER_CODE");
//		String  wherebm="and  BM_CODE='"+worknum+"'";
//		 if(!StringUtil.isBlank(worknum)){
//		boolean  booleans=toJieKao( worknum);
//		if(booleans==true){
//			List<Bean>   bmList=ServDao.finds("TS_BMLB_BM", wherebm);
//			if(!bmList.isEmpty()){
//				for(int i=0;i<bmList.size();i++){
//					Bean  bean=new  Bean();
//					Bean bmbean=bmList.get(i);
//					String bm_id = bmbean.getStr("BM_ID");
//					String bm_name = bmbean.getStr("BM_NAME");
//					String bm_type = bmbean.getStr("BM_TYPE");
//					String bm_mk = bmbean.getStr("BM_MK");
//					String bm_xl = bmbean.getStr("BM_XL");
//					String bm_atime = bmbean.getStr("S_ATIME");
//					String bm_bt = bm_type+"-"+bm_xl+"-"+bm_mk;
//					String bm_title = bmbean.getStr("BM_TITLE");
//					String title= "";
//					if(!"".equals(bm_type)){
//						title=bm_bt;
//					}else{
//						title=bm_title;
//					}
//					bean.set("bm_id", bm_id);
//					bean.set("title", title);
//					bean.set("bm_name", bm_name);
//					bean.set("bm_atime", bm_atime);
//					beanlist.add(bean);
//				}
//			
//		}else{
//			throw new Exception("报名列表为空");
//		}
//	
//		
//	 }
//		outbean.setData(beanlist);
//		 return  outbean;
//	 }else{
//		 throw new Exception("工号不能为空");
//	 }
//	 }
//	//获取在项目管理中是否定义了借考功能
//   public boolean toJieKao(String worknum) {
//	 //获取在项目管理中是否定义了借考功能
//	   String  whereworknum="and  BM_CODE='"+worknum+"'";
//	   String xmid="";
//	   List<Bean> userlist=ServDao.finds("TS_BMSH_PASS", whereworknum);
//	   if(userlist.size()>0){
//		   for(int i=0;i<userlist.size();i++){
//			   xmid= userlist.get(i).getStr("XM_ID");
//			   boolean xmszboolean= toxmsz(xmid);
//			   boolean  ydjkboolean=toYdjk(xmid);
//			  if(xmszboolean==true&&ydjkboolean==true){
//				  return  true;
//			  }
//			  
//		   }
//	   }
//	   return  false; 
//   }
//	//获得异地借考的时间
//   public boolean toYdjk(String xmid) {
//	   //获取当前时间
//	   SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd ");//设置日期格式
//	   String str = df.format(new Date());//
//	   //Bean  outbean= new  Bean();
//	   String  whereydjk="and  XM_ID="+xmid+"'";
//	  Bean ydjkbean=ServDao.find("TS_XMGL_YDJK", whereydjk);
//	   if(!ydjkbean.isEmpty()){
//			  String start= ydjkbean.getStr("YDJK_STADATE");
//			  String end= ydjkbean.getStr("YDJK_ENDDATE");
//		        int res1=str.compareTo(start);
//		        int res2=end.compareTo(str);
//		        if(res1>0&&res2>0){
//		        	 return true;
//		        }else if(res1==0||res2==0){
//		        	return true;
//		        }
//		  }
//	   return false;
//   }
// //判断是否选择异地借考
//   public boolean toxmsz(String xmid) {
//	   String  wherexmglsz="and  XM_ID='"+xmid+"'";
//	   List<Bean> mglszlist=ServDao.finds("TS_XMGL_SZ", wherexmglsz);
//	   if(mglszlist.size()>0){
//		   for(int i=0;i<mglszlist.size();i++){
//			 if("异地借考".equals(mglszlist.get(i).getStr("XM_SZ_NAME"))){
//				 return true;
//			 }
//			 
//		   }
//	   }
//	return false;
//	  
//   }**/
}