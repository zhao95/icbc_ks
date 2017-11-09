package com.rh.core.icbc.basedata.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.comm.ConfMgr;
import com.rh.core.icbc.basedata.AbstractTipTask;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;

public class KSTipBMTask extends AbstractTipTask {

	/**
	 * 报名开始和报名截止的提醒多线程任务
	 */
	private static final long serialVersionUID = 5961514710949260864L;

	@Override
	public boolean execute() {
		
		log.error("-------------开始通知参考人员报名相关信息（开始报名，截止报名）----------------");
		//报名开始提醒
		Date currentDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String bmSql = "select b.bm_id ,b.BM_NAME ,b.BM_TZ_START,b.BM_TZ_END,b.BM_START,b.BM_END,b.XM_SZ_ID,b.BM_KSXZ,b.XM_ID "
						+ "from ts_xmgl_bmgl b INNER JOIN ts_xmgl_sz s "
						+ "on b.XM_SZ_ID=s.XM_SZ_ID "
						+ "where b.XM_ID=s.XM_ID ";
		List<Bean> BMList = Transaction.getExecutor().query(bmSql);
		//创建集合存储要达到通知条件的项目
		List<Bean> BMStartTZList = new ArrayList<Bean>();
		List<Bean> BMEndTZList = new ArrayList<Bean>();
		for (int i = 0; i < BMList.size(); i++) {
			//获取到四个时间
			String bmTzStar = BMList.get(i).getStr("BM_TZ_START");
			String bmTzEnd = BMList.get(i).getStr("BM_TZ_END");
			String bmStart = BMList.get(i).getStr("BM_START");
			String bmEnd = BMList.get(i).getStr("BM_END");
			//获取相关传递数据
			String BM_ID = BMList.get(i).getStr("BM_ID");
			String BM_TITLE = BMList.get(i).getStr("BM_NAME");
			String BM_KSXZ = BMList.get(i).getStr("BM_KSXZ");
			String XM_ID = BMList.get(i).getStr("XM_ID");
			String XM_SZ_ID = BMList.get(i).getStr("XM_SZ_ID");
			try {
				//获取项目的通知开始时间，通知结束时间，报名开始时间，报名截止时间
				Date bmTzStarD = sdf.parse(bmTzStar);
				Date bmTzEndD = sdf.parse(bmTzEnd);
				Date bmStartD = sdf.parse(bmStart);
				Date bmEndD = sdf.parse(bmEnd);
				if(bmTzStarD.before(currentDate)){
					if(currentDate.before(bmTzEndD)){
						//开始通知
						//将当前的项目信息存进集合中，后续取出发给所有报名可见人员
						
						//获取管理员自定义的通知文字，真实数据替换掉占位字符
						String tipConfStartValue = ConfMgr.getConf("TS_BM_START_TIP", "您可以报名的考试已经开启报名了。请登录工商银行考试系统查看。");
						String tipConfStartValueReplaced = tipConfStartValue.replaceAll("#XM_TITLE#", BM_TITLE).replaceAll("#BM_START_DATE#", bmStart).replaceAll("#BM_END_DATE#", bmEnd);

						//获取群组id，用，号拼接成字符串
						String G_ID ="";
						List<Bean> groupIdlist = ServDao.finds("TS_BM_GROUP", "AND XM_ID='"+XM_ID+"'");
						if(groupIdlist.size()>0){
							for (int j = 0; j < groupIdlist.size(); j++) {
								String GID = groupIdlist.get(j).getStr("G_ID");
								if(StringUtils.isNotBlank(GID)){
									G_ID+=GID+",";
								}
							}
							G_ID = G_ID.substring(0, G_ID.length()-1);
//							G_ID = BMList.get(i).getStr("G_ID");
							//通知相关数据bean
							ParamBean tipParamBean = new ParamBean();
							tipParamBean.set("G_ID", G_ID);
							tipParamBean.set("XM_ID", XM_ID);
							tipParamBean.set("XM_SZ_ID", XM_SZ_ID);
							tipParamBean.set("BM_ID", BM_ID);
							tipParamBean.set("BM_NAME", BM_TITLE);
							tipParamBean.set("BM_KSXZ", BM_KSXZ);
							tipParamBean.set("tipMsg", tipConfStartValueReplaced);
							
							//把当前通知信息的bean存储进集合中，取到所有通知人员后再处理
							BMStartTZList.add(tipParamBean);
						}
						
						//获取所有的人员
						//后续调用工行接口传递参数即可。
						
						if(bmStartD.before(currentDate)){
							if(bmEndD.before(currentDate)){
								//报名截止时间到
								//判断是否到了报名截止时间，若是，通知报名截止
								//将当前的项目信息存进集合中，后续取出发给所有报名可见人员
								String tipConfEndValue = ConfMgr.getConf("TS_BM_END_TIP", "您可以报名的考试已经截止报名。请登录工商银行考试系统查看。");
								String tipConfEndValueReplaced = tipConfEndValue.replaceAll("#XM_TITLE#", BM_TITLE).replaceAll("#BM_START_DATE#", bmStart).replaceAll("#BM_END_DATE#", bmEnd);
								
								//获取群组id，用，号拼接成字符串
								String G_ID1 ="";
								List<Bean> groupIdlist1 = ServDao.finds("TS_BM_GROUP", "AND XM_ID='"+XM_ID+"'");
								if(groupIdlist1.size()>0){
									for (int k = 0; k < groupIdlist1.size(); k++) {
										String GID1 = groupIdlist1.get(k).getStr("G_ID");
										if(StringUtils.isNotBlank(GID1)){
											G_ID1+=GID1+",";
										}
									}
									G_ID1 = G_ID1.substring(0, G_ID1.length()-1);
//									G_ID = BMList.get(i).getStr("G_ID");
									//通知相关数据bean
									ParamBean tipBMEndParamBean = new ParamBean();
									tipBMEndParamBean.set("G_ID", G_ID1);
									tipBMEndParamBean.set("XM_ID", XM_ID);
									tipBMEndParamBean.set("XM_SZ_ID", XM_SZ_ID);
									tipBMEndParamBean.set("BM_ID", BM_ID);
									tipBMEndParamBean.set("BM_NAME", BM_TITLE);
									tipBMEndParamBean.set("BM_KSXZ", BM_KSXZ);
									tipBMEndParamBean.set("tipMsg", tipConfEndValueReplaced);
									
									//把当前通知信息的bean存储进集合中，取到所有通知人员后再处理
									BMEndTZList.add(tipBMEndParamBean);
								}
								
								
								
								/*//通知相关数据bean
								ParamBean tipBMEndParamBean = new ParamBean();
								
								tipBMEndParamBean.set("G_ID", G_ID);
								tipBMEndParamBean.set("XM_ID", XM_ID);
								tipBMEndParamBean.set("XM_SZ_ID", XM_SZ_ID);
								tipBMEndParamBean.set("BM_ID", BM_ID);
								tipBMEndParamBean.set("BM_NAME", BM_TITLE);
								tipBMEndParamBean.set("BM_KSXZ", BM_KSXZ);
								tipBMEndParamBean.set("tipMsg", tipConfEndValueReplaced);
								//把当前通知信息的bean存储进集合中，取到所有通知人员后再处理
								BMEndTZList.add(tipBMEndParamBean);*/
							}
						}else{
							//报名期间。啥也不做
						}
					}else{
						//当前时间在通知结束时间之后，啥也不做
					}
				}
				
			} catch (ParseException e) {
				log.error("-----------表中数据日期转换异常-------------"+e);
			}
		}
		//存储的是报名开始的通知信息
		if(BMStartTZList.size()>0){
			for (int k = 0; k < BMStartTZList.size(); k++) {
				String G_IDStr = BMStartTZList.get(k).getStr("G_ID");
				//调用父类方法传入群组ID 获取所有的群组人员
				List<Bean> allUserForTipBMStar = getAllUserForTip(G_IDStr);
				System.out.println(allUserForTipBMStar);
			}
		}
		//存储的是报名截止的通知信息
		if(BMEndTZList.size()>0){
			for (int l = 0; l < BMEndTZList.size(); l++) {
				String G_IDStr = BMEndTZList.get(l).getStr("G_ID");
				//调用父类方法传入群组ID 获取所有的群组人员
				List<Bean> allUserForTipBMEnd = getAllUserForTip(G_IDStr);
				System.out.println(allUserForTipBMEnd);
			}
		}
		
		
		
		
		log.error("-------------完成通知参考人员报名相关信息（开始报名，截止报名）----------------");
		
		return true;
	}

}
