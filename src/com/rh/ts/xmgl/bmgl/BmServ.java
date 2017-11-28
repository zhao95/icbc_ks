package com.rh.ts.xmgl.bmgl;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ServDao;
/**
 * 与项目一对一的报名管理 管理报名数据
 * @author shiyun
 *
 */
public class BmServ extends CommonServ {
	/**
	 * 获取报名时间报名状态
	 * @param paramBean
	 * @return
	 * @throws ParseException 
	 */
	public Bean getBMState(Bean paramBean) throws ParseException{
		Bean outBean = new Bean();
		String xmid = paramBean.getStr("xmid");
		String where1 = "AND XM_ID="+"'"+xmid+"'";
		List<Bean> listbean = ServDao.finds("TS_XMGL_BMGL",where1);
		
		List<Bean> finds = ServDao.finds("TS_XMGL_SZ", where1+" AND XM_SZ_NAME='报名'");
		String state = "";
		if(finds!=null&&finds.size()!=0){
			 state = finds.get(0).getStr("XM_SZ_TYPE");
		}
		if(listbean.size()==0){
			return new OutBean().set("list","");
		}
		Bean bmbean = listbean.get(0);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		String startTime = bmbean.getStr("BM_START");
		String endTime = bmbean.getStr("BM_END");
	
		//通知结束时间
		Date date = new Date();
		if(startTime!=""&&endTime!=""){
			Date date1 = sdf.parse(startTime);
			Date date2 = sdf.parse(endTime);
			if(date.getTime()<date2.getTime()&&date.getTime()>date1.getTime()){
				if("进行中".equals(state)){
					state = "待报名";
				}else if("已结束".equals(state)){
					state="已结束";
				}else{
					state="未开始";
				}
			}else if(date.getTime()>date2.getTime()){
					state="已结束";
			}else{
					state="未开始";
			}
			}
		
		
		Bean newBean = new Bean();
		newBean.set("STATE", state);
		newBean.set("START_TIME",startTime);
		newBean.set("END_TIME",endTime);
		List<Bean> list = new ArrayList<Bean>();
		list.add(newBean);
		
	       outBean.set("list",list);
	       outBean.set("nojson", list);
	       outBean.set("state", state);
	       return outBean;
		
	}
	/**
	 * 获取项目信息
	 * @param paramBean
	 * @return
	 * @throws ParseException 
	 */
	public Bean getXmInfo(Bean paramBean){
		
		Bean outBean = new Bean();
	String xmid = paramBean.getStr("xmid");
	String where1 = "AND XM_ID="+"'"+xmid+"'";
	//项目bean
	Bean xmbean = ServDao.find("TS_XMGL",xmid);
	List<Bean> listbean = ServDao.finds("TS_XMGL_BMSH",where1);
	List<Bean>   list = ServDao.finds("TS_XMGL_BMGL", where1);
	String SH_TGTSY = "";
	String SH_BTGTSY = "";
	String shstate = "";
	if(listbean.size()!=0){
		//审核通过提示语
		String shtsy = listbean.get(0).getStr("SH_TSY");
		 if("1".equals(shtsy)){
			 //立刻显示  提示语
			 SH_TGTSY = listbean.get(0).getStr("SH_TGTSY");
			 SH_BTGTSY = listbean.get(0).getStr("SH_BTGTSY");
		 }
		 Bean shbean;
		try {
			shbean = getSHState(paramBean);
			shstate =shbean.getStr("state");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	if(xmbean==null){
		 outBean.set("xmname","");
	}
	if(list.size()==0){
		    outBean.set("list","");
		    outBean.set("SH_TGTSY",SH_TGTSY);
		    outBean.set("SH_BTGTSY",SH_BTGTSY);
		    outBean.set("shstate", shstate);
		return outBean;
	}
	ObjectMapper mapper = new ObjectMapper();    
    StringWriter w = new StringWriter();  
    try {
		mapper.writeValue(w, list);
	} catch (JsonProcessingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	 catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    outBean.set("list",w.toString());
	//有的地方需要项目的名称展示  
    outBean.set("xmname",xmbean.getStr("XM_NAME"));
    outBean.set("SH_TGTSY",SH_TGTSY);
    outBean.set("SH_BTGTSY",SH_BTGTSY);
    outBean.set("shstate", shstate);
    return outBean;
	}
	
	/**
	 * 是否允许查看 辖内报名信息  带 审核状态 字段
	 */
	public OutBean getShowLook(Bean paramBean){
		OutBean out = new OutBean();
		String showlook="2";
		String sh_state = "";
		String xmid = paramBean.getStr("xmid");
		String where = "AND XM_ID='"+xmid+"'";
		//获取到项目报名名的数据
		List<Bean> finds = ServDao.finds("TS_XMGL_BMSH",where);
		for (Bean bean : finds) {
			 showlook = bean.getStr("SH_LOOK");
			 sh_state = bean.getStr("SH_STATE");
			 out.set("showlook", showlook);
			 out.set("state", sh_state);
		}
		return out;
	}
	/**
	 * 获取报名时间报名状态
	 * @param paramBean
	 * @return
	 * @throws ParseException 
	 */
	public Bean getSHState(Bean paramBean) throws ParseException{
		Bean outBean = new Bean();
		String xmid = paramBean.getStr("xmid");
		String where1 = "AND XM_ID="+"'"+xmid+"'";
		List<Bean> listbean = ServDao.finds("TS_XMGL_BMSH",where1);
		List<Bean> shlistbean = ServDao.finds("TS_XMGL_SZ",where1+ "and XM_SZ_NAME='审核'");
		if(listbean.size()==0){
			return new OutBean().set("list","");
		}
		Bean bmbean = listbean.get(0);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		String startTime = bmbean.getStr("SH_START");
		String endTime = bmbean.getStr("SH_END");
		String state =shlistbean.get(0).getStr("XM_SZ_TYPE");
		if(!"".equals(startTime)&&!"".equals(endTime)){
			Date date1 = sdf.parse(startTime);
			Date date2 = sdf.parse(endTime);
			Date date = new Date();
			if(date.getTime()<date2.getTime()&&date.getTime()>date1.getTime()){
				if("进行中".equals(state)){
					state = "待报名";
				}else if("已结束".equals(state)){
					state="已结束";
				}else{
					state="未开始";
				}
			}else if(date.getTime()>date2.getTime()){
					state="已结束";
			}else{
					state="未开始";
			}
		}
		
		Bean newBean = new Bean();
		newBean.set("STATE", state);
		newBean.set("START_TIME",startTime);
		newBean.set("END_TIME",endTime);
		List<Bean> list = new ArrayList<Bean>();
		list.add(newBean);
		
		
	       outBean.set("list",list);
	       outBean.set("nojson", list);
	       outBean.set("state", state);
	       return outBean;
		
	}
	
}
