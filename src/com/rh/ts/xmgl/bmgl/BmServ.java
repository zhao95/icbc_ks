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
		if(listbean.size()==0){
			return new OutBean().set("list","");
		}
		Bean bmbean = listbean.get(0);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
		String startTime = bmbean.getStr("BM_START");
		String endTime = bmbean.getStr("BM_END");
		String state ="未开始";
		if(startTime!=""&&endTime!=""){
			Date date1 = sdf.parse(startTime);
			Date date2 = sdf.parse(endTime);
			Date date = new Date();
			if(date.getTime()<date2.getTime()&&date.getTime()>date1.getTime()){
			 state = "待报名";
			}else if(date.getTime()>date2.getTime()){
				state="已结束";
			}
			}
		Bean newBean = new Bean();
		newBean.set("STATE", state);
		newBean.set("START_TIME",startTime);
		newBean.set("END_TIME",endTime);
		List<Bean> list = new ArrayList<Bean>();
		list.add(newBean);
		
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
	       outBean.set("nojson", list);
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
		 shstate = listbean.get(0).getStr("SH_STATE");
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
	 * 获取是否查看辖内所有  报名数据
	 */
	public OutBean getShowLook(Bean paramBean){
		String showlook="2";
		String xmid = paramBean.getStr("xmid");
		String where = "AND XM_ID='"+xmid+"'";
		//获取到项目报名名的数据
		List<Bean> finds = ServDao.finds("TS_XMGL_BMSH",where);
		for (Bean bean : finds) {
			 showlook = bean.getStr("SH_LOOK");
		}
		return new OutBean().set("showlook", showlook);
	}
	
	
}
