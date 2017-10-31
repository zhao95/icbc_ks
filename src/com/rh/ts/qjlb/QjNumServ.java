package com.rh.ts.qjlb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ServDao;
/**
 * 请假周数和次数 的判断
 * @author shiyun
 *
 */
public class QjNumServ extends CommonServ {

	/**
	 * 判断请假次数
	 * @param paramBean
	 * @return
	 */
	public OutBean getFlag(Bean paramBean){
		String bmids = paramBean.getStr("bmids");
		
		String[] bmidarr = bmids.split(",");
		//本次想报名的考试次数
		int wannacishu = bmidarr.length;
		//总次数
		int cishu = paramBean.getInt("cishu");
		//总周数
		int zhoushu = paramBean.getInt("zhoushu");
		
		UserBean userBean = Context.getUserBean();
		
		String code = userBean.getCode();
		
		List<Bean> finds = ServDao.finds("TS_BM_QJ_NUM","AND QJ_CODE='"+code+"'");
		
		if(finds!=null&&finds.size()!=0){
			int weeknum = finds.get(0).getInt("WEEK_NUM");
			int cishunum = finds.get(0).getInt("CISHU_NUM");
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:MM:ss");
			
			if(weeknum==zhoushu){
				//判断是否为同一周
				String starttime = finds.get(0).getStr("XM_START_TIME");
				
				String endtime = finds.get(0).getStr("XM_END_TIME");
				
				String ks_time="";
				
				//循环遍历  如果不在同一周 不能  进行请假
				  for (String bmid : bmidarr) {
					List<Bean> shpasslist = ServDao.finds("TS_BMSH_PASS", "AND BM_ID='"+bmid+"'");
					if(shpasslist!=null&&shpasslist.size()!=0){
						Bean shpassbean = shpasslist.get(0);
						List<Bean> kslist = ServDao.finds("ts_xmgl_kcap_yapzw","AND SH_ID='"+shpassbean.getId()+"'");
						if(kslist!=null&&kslist.size()!=0){
							if("".equals(kslist.get(0).getStr("SJ_DATE"))){
								ks_time = kslist.get(0).getStr("SJ_DATE").split("(")[0];
							}else{
								return new OutBean().setError("请假失败,考试时间为空");
							}
						}
						
					}
				
				  try {
					Date stdate = sdf.parse(starttime);
					Date endDate = sdf.parse(endtime);
					
					
					if(!"".equals(ks_time)){
						SimpleDateFormat simp = new SimpleDateFormat("yyy-mm-dd");
						Date ksdate = simp.parse(ks_time);
						
						if(ksdate.getTime()>endDate.getTime()||ksdate.getTime()<stdate.getTime()){
							//不在项目内
							return new  OutBean().setError("考试周次数已达最大数");
						}
					}else{
						return new OutBean().setError("请假失败,考试时间为空");
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  }
				  
			}
			
			if((cishunum+wannacishu)>cishu){
				return new OutBean().setError("考试次数超过上限");
			}
		}
		return new OutBean().set("yes", "true");
	}
	/**
	 * 保存请假次数
	 * @param paramBean
	 * @return
	 */
	public OutBean getQx(Bean paramBean){
		
		String bmids = paramBean.getStr("bmids");
		
		String[] bmidarr = bmids.split(",");
		//本次想报名的考试次数
		int wannacishu = bmidarr.length;
		//总次数
		int cishu = paramBean.getInt("cishu");
		//总周数
		int zhoushu = paramBean.getInt("zhoushu");
		
		UserBean userBean = Context.getUserBean();
		
		String name = userBean.getName();
		
		String code = userBean.getCode();
		
		List<Bean> finds = ServDao.finds("TS_BM_QJ_NUM","AND QJ_CODE='"+code+"'");
		
		if(finds!=null){
			//一周  判断是否为同一周
			Bean bean = finds.get(0);
			
			int pastweeknum = bean.getInt("WEEK_NUM");
			//开始时间
			String startime = bean.getStr("XM_START_TIME");
			//结束时间
			String endtime = bean.getStr("XM_END_TIME");
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:MM:ss");
			
			String newstartdate = "";
			
			String newenddate = "";
			
			String ks_time="";
			
			boolean Flag = false;
			if(pastweeknum<zhoushu){
				   //只报考了一周
					   //不是同一周   判断 已报名次数 是否超过6   和本次想报名次数
					   if((bean.getInt("CISHU_NUM")+wannacishu)>cishu){
						   //不能再报名
						   return new OutBean().setError("请假次数超过上限");
						   
					   }else{
						   for (String bmid : bmidarr) {
								List<Bean> shpasslist = ServDao.finds("TS_BMSH_PASS", "AND BM_ID='"+bmid+"'");
								if(shpasslist!=null&&shpasslist.size()!=0){
									Bean shpassbean = shpasslist.get(0);
									List<Bean> kslist = ServDao.finds("ts_xmgl_kcap_yapzw","AND SH_ID='"+shpassbean.getId()+"'");
									if(kslist!=null&&kslist.size()!=0){
										if("".equals(kslist.get(0).getStr("SJ_DATE"))){
											ks_time = kslist.get(0).getStr("SJ_DATE").split("(")[0];
										}else{
											return new OutBean().setError("请假失败,考试时间为空");
										}
									}
									
								
							}
							  try {
								Date stdate = sdf.parse(startime);
								Date endDate = sdf.parse(endtime);
								if(!"".equals(ks_time)){
									SimpleDateFormat simp = new SimpleDateFormat("yyy-mm-dd");
									Date ksdate = simp.parse(ks_time);
									
									if(ksdate.getTime()>endDate.getTime()||ksdate.getTime()<stdate.getTime()){
										//再加一个考试周
										Flag = true;
										//根据报名ID找XM  开始结束时间
										Bean bmbean = ServDao.find("TS_BMLB_BM", bmid);
										String xmid = bmbean.getStr("XM_ID");
										Bean xmbean = ServDao.find("TS_XMGL", xmid);
										newstartdate = xmbean.getStr("XM_KSSTARTDATA");
										newenddate = xmbean.getStr("XM_KSENDDATA");
									}
								}else{
									return new OutBean().setError("请假失败,考试时间为空");
								}
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							  	   
						   
						   
						   }
						   if(Flag){
							   //可请假  但是第二周了
							   bean.set("WEEK_NUM", "2");
							   bean.set("CISHU_NUM",bean.getInt("CISHU_NUM")+wannacishu);
							   bean.set("XM_START_TIME", newstartdate);
							   bean.set("XM_END_TIME",newenddate);
							   ServDao.save("TS_BM_QJ_NUM", bean);
						   }else{
							   //同一周  可请假 
							   bean.set("CISHU_NUM",bean.getInt("CISHU_NUM")+wannacishu);
							   ServDao.save("TS_BM_QJ_NUM", bean);
						   }
						  
						
						
					   }
			   }else if(pastweeknum==zhoushu){
				   for (String bmid : bmidarr) {
						List<Bean> shpasslist = ServDao.finds("TS_BMSH_PASS", "AND BM_ID='"+bmid+"'");
						if(shpasslist!=null&&shpasslist.size()!=0){
							Bean shpassbean = shpasslist.get(0);
							List<Bean> kslist = ServDao.finds("ts_xmgl_kcap_yapzw","AND SH_ID='"+shpassbean.getId()+"'");
							if(kslist!=null&&kslist.size()!=0){
								if("".equals(kslist.get(0).getStr("SJ_DATE"))){
									ks_time = kslist.get(0).getStr("SJ_DATE").split("(")[0];
								}else{
									return new OutBean().setError("请假失败,考试时间为空");
								}
							}
							
						
					}
					  try {
						Date stdate = sdf.parse(startime);
						Date endDate = sdf.parse(endtime);
						if(!"".equals(ks_time)){
							SimpleDateFormat simp = new SimpleDateFormat("yyy-mm-dd");
							Date ksdate = simp.parse(ks_time);
							
							if(ksdate.getTime()>endDate.getTime()||ksdate.getTime()<stdate.getTime()){
								//再加一个考试周
								Flag = true;
								//根据报名ID找XM  开始结束时间
							}
						}else{
							return new OutBean().setError("请假失败,考试时间为空");
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					  	   
				   
				   
				   }
				   if(Flag){
					  //不可请假
					   return new OutBean().setError("已达最大考试周，不能请假");
				   }else{
					   //同一周  可请假 
					   bean.set("CISHU_NUM",bean.getInt("CISHU_NUM")+wannacishu);
					   ServDao.save("TS_BM_QJ_NUM", bean);
				   }
				  
				
			   }
			
		}else{
			//没有请过假  
			if(wannacishu>cishu){
				   //不能再报名
				   return new OutBean().setError("报名次数超过上限");
				   
			   }else{
				    Bean newbean = new Bean(); 
					newbean.set("QJ_CODE", code);
					newbean.set("WEEK_NUM", "1");
					newbean.set("CISHU_NUMM", wannacishu);
					newbean.set("QJ_NAME", name);
					//根据报名id找项目考试开始结束时间
					String bmid = bmidarr[0];
					Bean bmbean = ServDao.find("TS_BMLB_BM", bmid);
					String xmid = bmbean.getStr("XM_ID");
					Bean xmbean = ServDao.find("TS_XMGL", xmid);
					String starttime  = xmbean.getStr("XM_KSSTARTDATA");
				
					String xmendtime = xmbean.getStr("XM_KSENDDATA");
					
					//开始时间
					newbean.set("XM_START_TIME",starttime);
					//结束时间
					newbean.set("XM_END_TIME",xmendtime);
					ServDao.save("TS_BM_QJ_NUM", newbean);
					
			   }
			
		}
		OutBean out = new OutBean();
		out.set("yes","true");
		return out;
		
	} 
}
