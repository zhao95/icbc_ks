package com.rh.ts.bmsh;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.rh.core.base.Bean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
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
		String xmid = paramBean.getStr("xm_id");
		String slevel = paramBean.getStr("level");
		int level = Integer.parseInt(slevel);
		String bm_code = "888802713";
		String shenuser = "888800172";
		//被选中的id
		String[] ss = s.split(",");
		String liyou = paramBean.getStr("liyou");
		//继续进行审核
		
		for (String id : ss) {
			if(!"".equals(id)){
				//获取下级参数
				String allman ="";
				String nextman = "";
				int nowlevel=level;
				if(level!=1){
				ParamBean parambean = new ParamBean();
				parambean.set("examerWorekNum", bm_code);
				parambean.set("level",level);
				parambean.set("shrWorekNum", shenuser);
				parambean.set("flowName", 1);
				parambean.set("xmId", xmid);
				OutBean outbean = ServMgr.act("TS_WFS_APPLY", "backFlow", parambean);
				List<Bean> list = outbean.getList("result");
				
				for (int l=0;l<list.size();l++) {
					if(l==0){
						nextman = list.get(l).getStr("BMSHLC_SHR");
					}
					if(l==list.size()-1){
						
						allman+= list.get(l).getStr("BMSHLC_SHR");
					}else{
						allman+= list.get(l).getStr("BMSHLC_SHR")+",";
					}
					
				}
			 nowlevel = list.size();
				}
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
			if(level==1){
				//不用再去待审核中直接去  审核通过中 且数据无改动
			}else{
				newBean.set("SH_USER", nextman);
				newBean.set("SH_OTHER", allman);
				newBean.set("SH_LEVEL", nowlevel);
				ServDao.save("TS_BMSH_STAY", newBean);
			}
			ServDao.delete("TS_BMSH_NOPASS", id);
			//审核明细表中插入此次审核数据
			Bean mindbean = new Bean();
			UserBean userbean = UserMgr.getUserByWorkNum(shenuser);
			mindbean.set("SH_LEVEL",slevel);
			mindbean.set("SH_MIND", liyou);
			mindbean.set("DATA_ID",bean.getStr("BM_ID"));
			mindbean.set("SH_STATUS", 1);
			mindbean.set("SH_ULOGIN",userbean.getLoginName());
			mindbean.set("SH_UNAME",userbean.getName());
			mindbean.set("SH_UCODE",shenuser);
			mindbean.set("SH_TYPE", 1);
			ServDao.save("TS_COMM_MIND",mindbean);
		}
		}
	}
	
	/**
	 * 导出事获取 项目下的数据逗号分隔 返回 字符串 导出所有
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
	/**
	 * 提交异议
	 */
	public void yiyi(Bean paramBean){
		String liyou = paramBean.getStr("liyou");
		String bmid = paramBean.getStr("bmid");
		//将异议状态改为1  已提交
		//将提交理由保存到  此表  ts_bmlb_bm
		String where1 =  "AND BM_ID="+"'"+bmid+"'";
		List<Bean> bmlist = ServDao.finds("TS_BMLB_BM", where1);
		Bean bmbean = bmlist.get(0);
		bmbean.set("BM_YIYI_STATE", 1);
		bmbean.set("BM_SS_REASON", liyou);
		bmbean.set("BM_SH_STATE", 0);
		ServDao.save("TS_BMLB_BM",bmbean);
		String shenuser = paramBean.getStr("user_code");
		String where = "AND BM_ID="+"'"+bmid+"'";
		List<Bean> list = ServDao.finds("TS_BMSH_NOPASS", where);
		
		//继续走审核流程
		if(list.size()!=0){
			Bean bean = list.get(0);
			String slevel  = bean.getStr("SH_LEVEL");
			String xmid = bean.getStr("XM_ID");
			String bm_code = bean.getStr("BM_CODE");
			int level = Integer.parseInt(slevel);
			ParamBean parambean = new ParamBean();
			parambean.set("examerWorekNum", bm_code);
			parambean.set("level",level);
			parambean.set("shrWorekNum", shenuser);
			parambean.set("flowName", 1);
			parambean.set("xmId", xmid);
			
			OutBean outbean = ServMgr.act("TS_WFS_APPLY", "backFlow", parambean);
			List<Bean> list1 = outbean.getList("result");
			
			String allman ="";
			String nextman = "";
			int nowlevel=level;
			for (int l=0;l<list1.size();l++) {
				if(l==0){
					nextman = list1.get(l).getStr("BMSHLC_SHR");
				}
				if(l==list1.size()-1){
					
					allman+= list1.get(l).getStr("BMSHLC_SHR");
				}else{
					allman+= list1.get(l).getStr("BMSHLC_SHR")+",";
				}
				
			}
			nowlevel = list1.size();
			
			
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
			if(level==1){
				//不再stay中加数据
				ServDao.save("TS_BMSH_PASS", newBean);
			}else{
				if(level==0){
					
				}else{
					
					ServDao.save("TS_BMSH_PASS", newBean);
				}
				newBean.set("SH_USER", nextman);
				newBean.set("SH_OTHER", allman);
				newBean.set("SH_LEVEL",nowlevel);
				newBean.set("BM_YIYI", bmid);
				ServDao.save("TS_BMSH_STAY", newBean);
			}
			//添加一个字段用来标识异议   显示图标按钮
		}
	}
}