package com.rh.core.icbc.basedata.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.comm.ConfMgr;
import com.rh.core.icbc.basedata.AbstractTipTask;
import com.rh.core.icbc.basedata.KSSendTipMessageServ;

public class KSTipQJ_JKTask extends AbstractTipTask {

	/**
	 * 报名开始和报名截止的提醒多线程任务
	 */
	private static final long serialVersionUID = 5961514710949260864L;

	@Override
	public boolean execute() {
		
		log.error("-------------开始通知参考人员请假相关信息（开始请假）----------------");
		Date currentDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		String qjsql = "select p.BM_CODE AS USER_CODE,p.XM_ID,p.BM_LB ,x.XM_NAME,qj.QJ_STADATE,qj.QJ_ENDDATE "
				+ "from (ts_bmsh_pass p INNER JOIN ts_xmgl_sz s on s.XM_ID=p.XM_ID AND s.XM_NAME_NUM=1 )  "
				+ "LEFT JOIN ts_xmgl_qjgl qj ON s.XM_ID =qj.XM_ID  LEFT JOIN ts_xmgl x on x.XM_ID=s.xm_id  ";
		List<Bean> qjList = Transaction.getExecutor().query(qjsql);
		List<Bean> qjStarTZList = new ArrayList<Bean>();
		for (int i = 0; i < qjList.size(); i++) {
			Date qjStart=null;
			Date qjEnd=null;
			String USER_CODE = qjList.get(i).getStr("USER_CODE");
			String XM_ID = qjList.get(i).getStr("XM_ID");
			String XM_NAME = qjList.get(i).getStr("XM_NAME");
			String BM_LB = qjList.get(i).getStr("BM_LB");
			String QJ_STADATE = qjList.get(i).getStr("QJ_STADATE");
			String QJ_ENDDATE = qjList.get(i).getStr("QJ_ENDDATE");
			Bean qjUserBean = new Bean();
				try {
					qjStart = sdf.parse(qjList.get(i).getStr("QJ_STADATE"));
					qjEnd = sdf.parse(qjList.get(i).getStr("QJ_ENDDATE"));
				} catch (ParseException e) {
					try {
						qjStart = sdf1.parse(qjList.get(i).getStr("QJ_STADATE"));
						qjEnd = sdf1.parse(qjList.get(i).getStr("QJ_ENDDATE"));
						log.error("请假时间转换异常，已使用备用方案处理。"+e);
					} catch (ParseException e1) {
						log.error("请假时间转换异常，请清除垃圾数据。"+e1);
					}
				}
				if(currentDate.before(qjStart)){
					//未开启请假
				}else{
					if(currentDate.before(qjEnd)){
						//请假开启中,发提醒
						qjUserBean.set("USER_CODE", USER_CODE);
						qjUserBean.set("XM_ID", XM_ID);
						qjUserBean.set("XM_NAME", XM_NAME);
						qjUserBean.set("BM_LB", BM_LB);
						qjUserBean.set("QJ_STADATE", QJ_STADATE);
						qjUserBean.set("QJ_ENDDATE", QJ_ENDDATE);
						
						//获取管理员自定义配置的请假开启提醒语
						String qjStartMsg = ConfMgr.getConf("TS_QJ_START_TIP", "您报名的考试开启请假了，如有需要请登录工商银行考试系统申请请假。");
						qjStartMsg = qjStartMsg.replaceAll("#XM_NAME#",XM_NAME ).replaceAll("#BM_LB#", BM_LB).replaceAll("#QJ_STARTDATE#", QJ_STADATE).replaceAll("#QJ_ENDDATE#", QJ_ENDDATE);
						qjUserBean.set("tipMsg", qjStartMsg);
						
						qjStarTZList.add(qjUserBean);
					}
					else if(qjEnd.before(currentDate)){
						//请假已结束
					}
				}
		}
		if(qjStarTZList.size()>0){
			//调用接口发送通知
			for (int j = 0; j < qjStarTZList.size(); j++) {
				//获取用户编码和数据信息，调用接口发送
				//遍历获取用户编码和数据信息，调用接口发送
				Bean qjBean = qjStarTZList.get(j);
				new KSSendTipMessageServ().sendTipMessageBeanForICBC(qjBean, "qjStar");
			}
		}
		log.error("-------------完成通知参考人员请假相关信息（开始请假）----------------");
		
		log.error("-------------开始通知参考人员借考相关信息（开始借考）----------------");
		String jksql = "select p.BM_CODE AS USER_CODE,p.XM_ID,p.BM_LB ,x.XM_NAME,jk.YDJK_STADATE,jk.YDJK_ENDDATE "
				+ "from (ts_bmsh_pass p INNER JOIN ts_xmgl_sz s on s.XM_ID=p.XM_ID AND s.XM_NAME_NUM= 4 ) "
				+ " LEFT JOIN ts_xmgl_ydjk jk ON s.XM_ID =jk.XM_ID  LEFT JOIN ts_xmgl x on x.XM_ID=s.xm_id  ";
		List<Bean> jkList = Transaction.getExecutor().query(jksql);
		List<Bean> jkStarTZList = new ArrayList<Bean>();
		for (int i = 0; i < jkList.size(); i++) {
			Date jkStart=null;
			Date jkEnd=null;
			String USER_CODE = jkList.get(i).getStr("USER_CODE");
			String XM_ID = jkList.get(i).getStr("XM_ID");
			String XM_NAME = jkList.get(i).getStr("XM_NAME");
			String BM_LB = jkList.get(i).getStr("BM_LB");
			String YDJK_STADATE = jkList.get(i).getStr("YDJK_STADATE");
			String YDJK_ENDDATE = jkList.get(i).getStr("YDJK_ENDDATE");
			Bean jkUserBean = new Bean();
				try {
					jkStart = sdf.parse(jkList.get(i).getStr("JK_STADATE"));
					jkEnd = sdf.parse(jkList.get(i).getStr("JK_ENDDATE"));
				} catch (ParseException e) {
					try {
						jkStart = sdf1.parse(qjList.get(i).getStr("JK_STADATE"));
						jkEnd = sdf1.parse(qjList.get(i).getStr("JK_ENDDATE"));
						log.error("借考时间转换异常，已使用备用方案处理。"+e);
					} catch (ParseException e1) {
						log.error("借考时间转换异常，请清除垃圾数据。"+e1);
					}
				}
				if(currentDate.before(jkStart)){
					//未开启借考
				}else{
					if(currentDate.before(jkEnd)){
						//借考开启中,发提醒
						jkUserBean.set("USER_CODE", USER_CODE);
						jkUserBean.set("XM_ID", XM_ID);
						jkUserBean.set("XM_NAME", XM_NAME);
						jkUserBean.set("BM_LB", BM_LB);
						jkUserBean.set("QJ_STADATE", YDJK_STADATE);
						jkUserBean.set("QJ_ENDDATE", YDJK_ENDDATE);
						
						//获取管理员自定义配置的借考开启提醒语
						String jkStartMsg = ConfMgr.getConf("TS_JK_START_TIP", "您报名的考试开启借考了，如有需要请登录工商银行考试系统申请请假。");
						jkStartMsg = jkStartMsg.replaceAll("#XM_NAME#",XM_NAME ).replaceAll("#BM_LB#", BM_LB).replaceAll("#JK_STADATE#", YDJK_STADATE).replaceAll("#JK_ENDDATE#", YDJK_ENDDATE);
						jkUserBean.set("tipMsg", jkStartMsg);
						
						jkStarTZList.add(jkUserBean);
					}
					else if(jkEnd.before(currentDate)){
						//借考申请已截止
					}
				}
		}
		if(jkStarTZList.size()>0){
			//调用接口发送通知
			for (int k = 0; k < jkStarTZList.size(); k++) {
				//遍历获取用户编码和数据信息，调用接口发送
				Bean jkBean = jkStarTZList.get(k);
				new KSSendTipMessageServ().sendTipMessageBeanForICBC(jkBean, "jkStar");
			}
		}
		log.error("-------------完成通知参考人员借考相关信息（开始借考）----------------");
		
		
		
		
		
		return true;
	}

}
