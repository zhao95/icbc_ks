package com.rh.ts.bmsh;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ServDao;

public class PassServ extends CommonServ {

	/**
	 * 获取那一页的记录  返回 
	 * @param paramBean
	 * @return
	 */
	public Bean getUncheckList(Bean paramBean){
		Bean outBean = new Bean();
		String servId = "TS_BMSH_PASS";
		String NOWPAGE = paramBean.getStr("nowpage");
		String SHOWNUM = paramBean.getStr("shownum");
		String where1 = paramBean.getStr("where");
		String user_code = paramBean.getStr("user_code");
		List<Bean> list1 = ServDao.finds(servId, where1);
		List<Bean> list = new ArrayList<Bean>();
		for (Bean bean : list1) {
			String other = bean.getStr("SH_OTHER");
			if(other.contains(user_code)){
				list.add(bean);
			}
		}
		int ALLNUM = list.size();
		//计算页数
		int meiye =  Integer.parseInt(SHOWNUM);
		int yeshu = ALLNUM/meiye;
		int yushu = ALLNUM%meiye;
		//获取总页数
		if(yushu==0&&yeshu!=0){
			yeshu+=1;
		}
		
		int nowpage = Integer.parseInt(NOWPAGE);
		int showpage = Integer.parseInt(SHOWNUM);
		//计算第一项 开始
		int chushi = (nowpage-1)*showpage+1;
		//计算结束项
		int jieshu = (nowpage-1)*showpage+showpage;
		//放到Array中
		List<Bean> list2 = new ArrayList<Bean>();
		if(ALLNUM==0){
			//没有数据
		}else{
			
			if(jieshu<=ALLNUM){
				//循环将数据放入list2中返回给前台
				for(int i =chushi;i<=jieshu;i++){
					list2.add(list.get(i-1));
				}
				
			}else{
				for(int j=chushi;j<ALLNUM+1;j++){
					list2.add(list.get(j-1));
				}
			}
		}
		//ObjectMapper和StringWriter都是jackson中的，通过这两个可以实现对list的序列化  
	   ObjectMapper mapper = new ObjectMapper();    
       StringWriter w = new StringWriter();  
       try {
		mapper.writeValue(w, list2);
	} catch (JsonProcessingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	 catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
       outBean.set("list",w.toString());
       return outBean;
	}
	
	/**
	 * 获取项目下所有审核通过的数据
	 * @param paramBean
	 * @return
	 */
	public Bean getAllData(Bean paramBean){
		Bean outBean = new Bean();
		String servId = "TS_BMSH_PASS";
		String where1 = paramBean.getStr("where");
		List<Bean> list1 = ServDao.finds(servId, where1);
		String user_code = paramBean.getStr("user_code");
		List<Bean> list = new ArrayList<Bean>();
		for (Bean bean : list1) {
			String other = bean.getStr("SH_OTHER");
			if(other.contains(user_code)){
				list.add(bean);
			}
		}
		
		//ObjectMapper和StringWriter都是jackson中的，通过这两个可以实现对list的序列化  
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
	   return outBean;
	}
	
	
	/**
	 * 修改 审核状态 
	 * @param paramBean
	 */
	public Bean update(Bean paramBean){
		String user_code = paramBean.getStr("user_code");
		String s = paramBean.getStr("checkedid");
		
	
		String shenuser = "";
		UserBean userBean = Context.getUserBean();
		if(userBean.isEmpty()){
			 return new OutBean().setError("ERROR:user_code 为空");
		}else{
			shenuser=userBean.getStr("USER_CODE");
		}
		//被选中的id
		String[] ss = s.split(",");
		String state = paramBean.getStr("radiovalue");
		String liyou = paramBean.getStr("liyou");
		for (String id : ss) {
			if(!"".equals(id)){
			//获取当前对象
			Bean bean = ServDao.find("TS_BMSH_PASS", id);
			String slevel = bean.getStr("SH_LEVEL");
			int level = Integer.parseInt(slevel);
			//将数据删除 若存在的话在stay中     因为不会再往下推送 所以审核 级数不会变
			String bmid = bean.getStr("BM_ID");
			//获取 stay里对象
			String where = "AND BM_ID="+"'"+bmid+"'";
			List<Bean> list = ServDao.finds("TS_BMSH_STAY", where);
			if(list.size()==0){
				//因为审核到了最高级所以把数据删了  审核级数不会再变
			}else{
				//审核
			ServDao.delete("TS_BMSH_STAY", list.get(0));
			}
			
			bean.remove("SH_ID");
			bean.remove("S_CMPY");
			bean.remove("S_ODEPT");
			bean.remove("S_TDEPT");
			bean.remove("S_DEPT");
			bean.remove("S_ATIME");
			bean.remove("S_MTIME");
			bean.remove("S_USER");
			bean.remove("S_FLAG");
			bean.remove("_PK_");
			bean.remove("ROW_NUM_");
			Bean newBean = new Bean();
			newBean.copyFrom(bean);
			//不再推送    当前审核人就是下级审核人
			newBean.set("SH_USER", user_code);
			newBean.set("SH_OTHER",user_code);
			ServDao.save("TS_BMSH_NOPASS", newBean);
			ServDao.delete("TS_BMSH_PASS", id);
			//审核明细表中插入此次审核数据
			Bean mindbean = new Bean();
			mindbean.set("SH_LEVEL",slevel);
			mindbean.set("SH_MIND", liyou);
			mindbean.set("DATA_ID",bean.getStr("BM_ID"));
			mindbean.set("SH_STATUS", state);
			mindbean.set("SH_ULOGIN",userBean.getLoginName());
			mindbean.set("SH_UNAME",userBean.getName());
			mindbean.set("SH_UCODE",shenuser);
			mindbean.set("SH_TYPE", 1);
			ServDao.save("TS_COMM_MIND",mindbean);
		}
		}
		return new OutBean().setOk();
	}

	/**
	 * 导出事获取 项目下的数据逗号分隔 返回 字符串
	 */
	public Bean reSids(Bean paramBean){
		Bean outBean = new Bean();
		String servId = paramBean.getStr("servId");
		String xmid = paramBean.getStr("xmid");
		String where = "AND XM_ID="+"'"+xmid+"'";
		List<Bean> list1 = ServDao.finds(servId,where );
		String user_code = paramBean.getStr("user_code");
		List<Bean> list = new ArrayList<Bean>();
		for (Bean bean : list1) {
			String other = bean.getStr("SH_OTHER");
			if(other.contains(user_code)){
				list.add(bean);
			}
		}
		String ids = "";
		for(int i=0;i<list.size();i++){
			String id = list.get(i).getId();
			if(i==list.size()-1){
				
				ids+=id;
			}else{
				ids+=id+",";
			}
		}
		   ObjectMapper mapper = new ObjectMapper();    
		   StringWriter w = new StringWriter();  
		   try {
			mapper.writeValue(w, ids);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		 catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   outBean.set("string",w.toString());
		   return outBean;
		
		
	}
}
