package com.rh.ts.bmsh;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.ServDao;
/**
 * 没有通过的审核
 * @author shiyun
 *
 */
public class NoPassServ extends CommonServ {
	
	/**
	 * 获取那一页的记录  返回 
	 * @param paramBean
	 * @return
	 */
	public Bean getUncheckList(Bean paramBean){
		Bean outBean = new Bean();
		String servId = "TS_BMSH_NOPASS";
		String NOWPAGE = paramBean.getStr("nowpage");
		String SHOWNUM = paramBean.getStr("shownum");
		String where1 = paramBean.getStr("where");
		List<Bean> list = ServDao.finds(servId, where1);
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
	 * 获取项目下所有审核不通过的数据
	 * @param paramBean
	 * @return
	 */
	public Bean getAllData(Bean paramBean){
		Bean outBean = new Bean();
		String servId = "TS_BMSH_NOPASS";
		String where1 = paramBean.getStr("where");
		List<Bean> list = ServDao.finds(servId, where1);
		
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
	public void update(Bean paramBean){
		String s = paramBean.getStr("checkedid");
		//被选中的id
		String[] ss = s.split(",");
		String state = paramBean.getStr("radiovalue");
		String liyou = paramBean.getStr("liyou");
		for (String id : ss) {
			if(!"".equals(id)){
			Bean bean = ServDao.find("TS_BMSH_NOPASS", id);
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
				ServDao.save("TS_BMSH_PASS", newBean);
			
			ServDao.delete("TS_BMSH_NOPASS", id);
		}
		}
	}
	
	/**
	 * 导出事获取 项目下的数据逗号分隔 返回 字符串
	 */
	public Bean reSids(Bean paramBean){
		Bean outBean = new Bean();
		String servId = paramBean.getStr("servId");
		String xmid = paramBean.getStr("xmid");
		String where = "AND XM_ID="+"'"+xmid+"'";
		List<Bean> list = ServDao.finds(servId,where );
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
