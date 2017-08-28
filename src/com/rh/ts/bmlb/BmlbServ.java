package com.rh.ts.bmlb;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;




import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.util.Constant;

public class BmlbServ extends CommonServ {
	/**
	 * 非资格考试的新增
	 * @param paramBean
	 * @return
	 */
	public void addData(Bean paramBean){
		//获取服务ID
		String servId=paramBean.getStr(Constant.PARAM_SERV_ID);
		//获取前台传过来的值
		String user_code = paramBean.getStr("user_code");
		String user_name = paramBean.getStr("user_name");
		String user_sex = paramBean.getStr("user_sex");
		String odept_name = paramBean.getStr("odept_name");
		String user_office_phone = paramBean.getStr("user_office_phone");
		String user_mobile = paramBean.getStr("user_mobile");
		String user_cmpy_date = paramBean.getStr("user_cmpy_date");
		String bmcode = paramBean.getStr("bmCodes");
		String xm_id = paramBean.getStr("xm_id");
		String[] bmcodes = bmcode.split(",");
		for (String string : bmcodes) {
			//根据服务id 主键id获取 当前非资格考试的服务
			Bean bean = ServDao.find("TS_XMGL_BM_FZGKS", string);
			String fzgks_date1 = bean.getStr("FZGKS_STADATE");
			String fzgks_date2 = bean.getStr("FZGKS_ENDDATE");
			String fzgks_name = bean.getStr("FZGKS_NAME");
			Bean beans=new Bean();
			beans.set("BM_CODE", user_code);
			beans.set("BM_NAME", user_name);
			beans.set("BM_SEX", user_sex);
			beans.set("ODEPT_NAME", odept_name);
			beans.set("BM_OFFICE_PHONE", user_office_phone);
			beans.set("BM_PHONE", user_mobile);
			beans.set("BM_ATIME", user_cmpy_date);
			beans.set("BM_STARTDATE",fzgks_date1);
			beans.set("BM_ENDDATE",fzgks_date2);
			beans.set("BM_TITLE",fzgks_name);
			beans.set("XM_ID",xm_id);
			Bean bmbean = ServDao.create(servId, beans);
			//获取到报名id
			String bm_id= bmbean.getStr("BM_ID");
			//根据报名id获取到非资格考试表单
			Bean bmfzgBean=ServDao.find("TS_BMLB_BM", bm_id);
			String bm_name = bmfzgBean.getStr("BM_NAME");//报名人姓名
			String bm_code = bmfzgBean.getStr("BM_CODE");//报名人人力资源编码
			String xm_ids = bmfzgBean.getStr("XM_ID");//获得项目id
			
			ParamBean param=new ParamBean();
			param.set("examercode",bm_code);
			param.set("level",0);
			param.set("xmId",xm_ids);
			param.set("flowName",0);
			param.set("shr","");
			Bean out= ServMgr.act("TS_WFS_APPLY","backFlow", param);
			//审核bean
			Bean shBean =new Bean();
			shBean.set("XM_ID",xm_ids);
			shBean.set("BM_ID",bm_id);
			shBean.set("BM_NAME",bm_name);
			shBean.set("BM_CODE",bm_code);
			ServDao.save("TS_BMSH_STAY", shBean);
			
		}
	}
	/**
	 * 资格考试跨序列的新增
	 * @param paramBean
	 * @return
	 */
	public Bean addZgData(Bean paramBean){
		//获取服务ID
		String servId=paramBean.getStr(Constant.PARAM_SERV_ID);
		//获取前台传过来的值
		String user_code = paramBean.getStr("user_code");
		String user_name = paramBean.getStr("user_name");
		String user_sex = paramBean.getStr("user_sex");
		String odept_name = paramBean.getStr("odept_name");
		String user_office_phone = paramBean.getStr("user_office_phone");
		String user_mobile = paramBean.getStr("user_mobile");
		String user_cmpy_date = paramBean.getStr("user_cmpy_date");
		String xm_name = paramBean.getStr("xm_name");
		String bmcode = paramBean.getStr("BM_IDS");
		String xm_id = paramBean.getStr("XM_ID");
		String bm_start = paramBean.getStr("bm_start");
		String bm_end = paramBean.getStr("bm_end");
		//根据项目id获取项目报名开始时间和结束时间
		String[] bmcodes = bmcode.split(",");
		for (String string : bmcodes) {
			//根据主键id获取 当前考试类别的服务
			Bean bean = ServDao.find("TS_XMGL_BM_KSLB", string);
			String kslb_name = bean.getStr("KSLB_NAME");
			String kslb_xl = bean.getStr("KSLB_XL");
			String kslb_mk = bean.getStr("KSLB_MK");
			String kslb_type = bean.getStr("KSLB_TYPE");
			String kslbk_id = bean.getStr("KSLBK_ID");
			//根据主键id获取 当前考试类别库的服务
			Bean kslbkbean = ServDao.find("TS_XMGL_BM_KSLBK",kslbk_id);
			String kslbk_mkcode = kslbkbean.getStr("KSLB_TYPE");
			Bean beans=new Bean();
			beans.set("BM_CODE", user_code);
			beans.set("BM_NAME", user_name);
			beans.set("BM_SEX", user_sex);
			beans.set("ODEPT_NAME", odept_name);
			beans.set("BM_OFFICE_PHONE", user_office_phone);
			beans.set("BM_PHONE", user_mobile);
			beans.set("BM_ATIME", user_cmpy_date);
			beans.set("BM_LB",kslb_name);
			beans.set("BM_XL",kslb_xl);
			beans.set("BM_MK",kslb_mk);
			beans.set("BM_TYPE",kslb_type);
			beans.set("XM_ID",xm_id);
			beans.set("BM_TITLE",xm_name);
			beans.set("BM_STARTDATE",bm_start);
			beans.set("BM_ENDDATE",bm_end);
			beans.set("KSLBK_ID",kslbk_id);
			beans.set("KSLBK_MKCODE",kslbk_mkcode);
			Bean bmbean = ServDao.create(servId, beans);
			//获取到报名id
			String bm_id= bmbean.getStr("BM_ID");
			//根据报名id获取到跨序列资格考试表单
			Bean bmzgBean=ServDao.find("TS_BMLB_BM", bm_id);
			String bm_name = bmzgBean.getStr("BM_NAME");//报名人姓名
			String bm_code = bmzgBean.getStr("BM_CODE");//报名人人力资源编码
			//审核bean
			Bean shBean =new Bean();
			shBean.set("XM_ID",xm_id);
			shBean.set("BM_ID",bm_id);
			shBean.set("BM_NAME",bm_name);
			shBean.set("BM_CODE",bm_code);
			shBean.set("KSLBK_ID",kslbk_id);
			shBean.set("BM_LB",kslb_name);
			shBean.set("BM_XL",kslb_xl);
			shBean.set("BM_MK",kslb_mk);
			shBean.set("BM_TYPE",kslb_type);
			ServDao.save("TS_BMSH_STAY", shBean);
		}
		String where ="AND BM_CODE="+"'"+user_code+"'";
		List<Bean> list = ServDao.finds("TS_BMLB_BM", where);
		String BM_ID="";
		if(list.size()>0){
			for(int i=0;i<list.size();i++){
				if(i==0){
					BM_ID=list.get(i).getId();
				}else{
					BM_ID+=","+list.get(i).getId();
				}
			}
		}
		Bean outBean=new Bean();
		outBean.set("list", BM_ID);
		return outBean;
	}

	/**
	 * 资格考试必须考试的新增
	 * @param paramBean
	 * @return
	 */
	/*
	public void addZgDataOne(Bean paramBean){
		//获取服务ID
			String servId=paramBean.getStr(Constant.PARAM_SERV_ID);
			//获取前台传过来的值
			String user_code = paramBean.getStr("user_code");
			String user_name = paramBean.getStr("user_name");
			String user_sex = paramBean.getStr("user_sex");
			String odept_name = paramBean.getStr("odept_name");
			String user_office_phone = paramBean.getStr("user_office_phone");
			String user_mobile = paramBean.getStr("user_mobile");
			String user_cmpy_date = paramBean.getStr("user_cmpy_date");
			String bk_id = "1HjT0eSXZ5MauSymtKSE";
			String xm_name = paramBean.getStr("xm_name");
			String bm_start = paramBean.getStr("bm_start");
			String bm_end = paramBean.getStr("bm_end");
			String xm_id = paramBean.getStr("xm_id");
			//根据服务id 主键id获取 当前资格考试的服务
			Bean bean = ServDao.find("TS_XMGL_BM_KSLBK", bk_id);
			String kslbk_name = bean.getStr("KSLBK_NAME");
			String kslbk_xl = bean.getStr("KSLBK_XL");
			String kslbk_mk = bean.getStr("KSLBK_MK");
			String kslbk_type = bean.getStr("KSLBK_TYPE");
			String kslbk_mkcode = bean.getStr("KSLBK_MKCODE");
			Bean beans=new Bean();
			beans.set("BM_CODE", user_code);
			beans.set("BM_NAME", user_name);
			beans.set("BM_SEX", user_sex);
			beans.set("ODEPT_NAME", odept_name);
			beans.set("BM_OFFICE_PHONE", user_office_phone);
			beans.set("BM_PHONE", user_mobile);
			beans.set("BM_RUHANG", user_cmpy_date);
			beans.set("BM_LB",kslbk_name);
			beans.set("BM_XL",kslbk_xl);
			beans.set("BM_MK",kslbk_mk);
			beans.set("BM_TYPE",kslbk_type);
			beans.set("KSLBK_ID",bk_id);
			beans.set("KSLBK_MKCODE",kslbk_mkcode);
			beans.set("XM_ID",xm_id);
			beans.set("BM_TITLE",xm_name);
			beans.set("BM_STARTDATE",bm_start);
			beans.set("BM_ENDDATE",bm_end);
			Bean bmbean = ServDao.create(servId, beans);
			//获取到报名id
			String bm_id= bmbean.getStr("BM_ID");
			//根据报名id获取到跨序列资格考试表单
			Bean bmfzgBean=ServDao.find("TS_BMLB_BM", bm_id);
			String bm_name = bmfzgBean.getStr("BM_NAME");//报名人姓名
			String bm_code = bmfzgBean.getStr("BM_CODE");//报名人人力资源编码
			//待办bean
			Bean shBean =new Bean();
			shBean.set("XM_ID",xm_id);
			shBean.set("BM_ID",bm_id);
			shBean.set("BM_NAME",bm_name);
			shBean.set("BM_CODE",bm_code);
			shBean.set("KSLBK_ID",bk_id);
			shBean.set("BM_LB",kslbk_name);
			shBean.set("BM_XL",kslbk_xl);
			shBean.set("BM_MK",kslbk_mk);
			shBean.set("BM_TYPE",kslbk_type);
			ServDao.save("TS_BMSH_STAY", shBean);
	}
	*/
	/**
	 * 根据name条件查询非资格
	 * @param paramBean
	 * @return
	 */
	public Bean getSelectName(Bean paramBean){
		String servId = paramBean.getStr("servId");
		String id = paramBean.getStr("id");
		String user_code = paramBean.getStr("user_code");
		String name = paramBean.getStr("name");
		String where = "";
		if(name==""||"全部查询".equals(name)){
			where = "AND XM_ID="+"'"+id+"' "+" AND BM_CODE="+"'"+user_code+"'";
		}else{
			
		 where = "AND XM_ID="+"'"+id+"' "+" AND BM_CODE="+"'"+user_code+"' "+"AND BM_NAME like "+"'%"+name+"%'";
		}
		List<Bean> list = ServDao.finds(servId, where);
		Bean outBean =  new Bean();
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
	  * 获取项目下 已经报考的考试
	  * @param paramBean
	  * @return
	  */
	public Bean getList(Bean paramBean){
		String servId = "TS_BMLB_BM";
		String id = paramBean.getStr("id");
		String user_code = paramBean.getStr("user_code");
		String where = "AND XM_ID="+"'"+id+"' "+" AND BM_CODE="+"'"+user_code+"' order by BM_STATE";
		List<Bean> list = ServDao.finds(servId, where);
		Bean outBean = new Bean();
		
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
       outBean.set("OBJECT",w.toString());
       return outBean;
	}
	 /**
	  * 获取 已经报考的考试
	  * @param paramBean
	  * @return
	  */
	public Bean getAllList(Bean paramBean){
		String servId = "TS_BMLB_BM";
		String user_code = paramBean.getStr("user_code");
		String where ="AND BM_CODE="+"'"+user_code+"' order by BM_STATE";
		List<Bean> list = ServDao.finds(servId, where);
		Bean outBean = new Bean();
		outBean.set("list", list);
		return outBean;
	}
	/**
	 * 根据条件  三级联动进行筛选
	 * @param paramBean
	 * @return
	 */
	public Bean getSelectData(Bean paramBean){
		String servId = "TS_BMLB_BM";
		String user_code = paramBean.getStr("user_code");
		String where1 = paramBean.getStr("where");
		String where = "AND BM_CODE="+"'"+user_code+"' "+where1+" order by BM_STATE";
		List<Bean> list = ServDao.finds(servId, where);
		Bean outBean = new Bean();
		
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
	 * 分页查询  有下拉框查询
	 * @param paramBean
	 * @return
	 */
	public Bean getSelectedData(Bean paramBean){
		Bean outBean = new Bean();
		String servId = "TS_BMLB_BM";
		String NOWPAGE = paramBean.getStr("nowpage");
		String SHOWNUM = paramBean.getStr("shownum");
		String user_code = paramBean.getStr("user_code");
		String where1 = paramBean.getStr("where");
		String where ="AND BM_CODE="+"'"+user_code+"' "+where1 +" order by BM_STATE";
		List<Bean> list = ServDao.finds(servId, where);
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
	 * 撤销时将已报名的数据状态 改为已撤销（多条）
	 * @param paramBean
	 */
	public void deletebm(Bean paramBean){
		String servId="TS_BMLB_BM";
		String id = paramBean.getStr("id");
		String user_code = paramBean.getStr("user_code");
		String where = "AND XM_ID="+"'"+id+"' "+"AND BM_CODE="+"'"+user_code+"' order by BM_STATE";
		List<Bean> list = ServDao.finds(servId, where);
		for(int i=0;i<list.size();i++){
			Bean dataBean = list.get(i);
			dataBean.set("BM_STATE", 2);
			ServDao.update(servId, dataBean);
		}
	}
	/**
	 * 撤销时将已报名的数据状态 改为已撤销(单条)
	 * @param paramBean
	 */
	public void deletesingle(Bean paramBean){
		String servId="TS_BMLB_BM";
		String id = paramBean.getStr("id");
		String where = "AND BM_ID="+"'"+id+"'";
		List<Bean> list = ServDao.finds(servId, where);
		for(int i=0;i<list.size();i++){
			Bean dataBean = list.get(i);
			dataBean.set("BM_STATE", 2);
			ServDao.update(servId, dataBean);
		}
		
		
		
	}
	public Bean lookstate(Bean paramBean){
		String xmid = paramBean.getStr("xmid");
		String user_code = paramBean.getStr("user_code");
		String where = "AND XM_ID="+"'"+xmid+"' "+"AND BM_CODE="+"'"+user_code+"' order by BM_STATE";
		List<Bean> list = ServDao.finds("TS_BMLB_BM", where);
		String state = "全部";
		for (Bean bean : list) {
			if(bean.getStr("BM_STATE").equals("1")){
				 state = "部分";
			}
		}
		Bean outBean = new Bean();
		outBean.set("state",state);
		return outBean;
	}
	/**
	 * 分页查询没有  下拉框 查询
	 */
	public OutBean getFenYe(Bean paramBean){
		//{_PAGE_={NOWPAGE=6, PAGES=100, ALLNUM=4993, SHOWNUM=50}, serv=SY_COMM_FILE, act=query, frameId=SY_COMM_FILE-tabFrame, paramsFlag=false, title=系统文件, sId=SY_COMM_FILE, _TRANS_=false}
		OutBean out = new OutBean();
		String NOWPAGE = paramBean.getStr("nowpage");
		String SHOWNUM = paramBean.getStr("shownum");
		String Sid = paramBean.getStr("sid");
		String user_code = paramBean.getStr("user_code");
		String xmid = paramBean.getStr("xmid");
		String where = "AND XM_ID="+"'"+xmid+"'"+" AND BM_CODE="+"'"+user_code+"' order by BM_STATE";
		//获取总记录数
		List<Bean> list = ServDao.finds(Sid, where);
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
		//list2为获取到的  第几页  多少条数据
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
	       out.set("list",w.toString());
	      
	       return out;
		}
	
	//动态生成三级联动  拼接 json 字符串
		public Bean getJsonString(Bean paramBean){
			List<Bean> list = ServDao.finds("TS_XMGL_BM_KSLBKALL", "");
			String s1 = "";
			String s2 = "";
			String s3 = "";
			List<Bean> list1 = new ArrayList<Bean>();
			List<Bean> list2 = new ArrayList<Bean>();
			List<Bean> list3 = new ArrayList<Bean>();
			for (Bean bean : list) {
				//序列为空 为第一层级
				String s = bean.getStr("KSLBK_XL");
				String ss = bean.getStr("KSLBK_MK");
				String sss = bean.getId();
				if(s==""){
					s1 +="{text: '"+bean.getStr("KSLBK_IDVALUE")+"', value: '"+bean.getStr("KSLBK_IDVALUE")+"', extendAttr: { id: "+bean.getId()+" } }, ";
				}else if("".equals(bean.getStr("KSLBK_MK"))){
					s2+="{ "+'"'+"text"+'"'+": "+'"'+bean.getStr("KSLBK_XL")+'"'+","+'"'+"value"+'"'+": "+ '"'+bean.getStr("KSLBK_XL")+'"'+","+ '"'+"extendAttr"+'"'+": {"+'"'+ "id"+'"'+": "+ '"'+bean.getId()+'"'+","+'"'+"parentId"+'"'+":"+'"'+bean.getStr("KSLBK_PID")+'"'+"} }, "; 
				}else if(!"".equals(bean.getStr("KSLBK_XL"))&&"".equals(bean.getStr("KSLBK_TYPE"))&&!"".equals(bean.getStr("KSLBK_MK"))){
					s3+="{ "+'"'+"text"+'"'+": "+'"'+bean.getStr("KSLBK_MK")+'"'+","+'"'+"value"+'"'+": "+ '"'+bean.getStr("KSLBK_MK")+'"'+","+ '"'+"extendAttr"+'"'+": {"+'"'+ "id"+'"'+": "+ '"'+bean.getId()+'"'+","+'"'+"parentId"+'"'+":"+'"'+bean.getStr("KSLBK_PID")+'"'+"} }, ";
				}
					
				}
			String S1 ="["+s1.substring(0,s1.length()-2)+"]";
			String S2 ="["+s2.substring(0,s2.length()-2)+"]";
			String S3 ="["+s3.substring(0,s3.length()-2)+"]";
		
			JSONArray jsonoBJECT;
			JSONArray jsonOBJECT2;
			JSONArray jsonOBJECT3;
		
			Bean out = new Bean();
			try {
				jsonoBJECT = new JSONArray(S1);
				jsonOBJECT2 = new JSONArray(S2);
				jsonOBJECT3 = new JSONArray(S3);
				out.set("s1",jsonoBJECT);
				out.set("s2",jsonOBJECT2);
				out.set("s3",jsonOBJECT3);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
		}
		
		return out;
	
		}
		/**
		 * 查询异议的文件记录 回显
		 * @param paramBean
		 * @return
		 */
	public Bean filehist(Bean paramBean){
		Bean outBean = new Bean();
		String bmid = paramBean.getStr("bmid");
		String where = "AND DATA_ID="+"'"+bmid+"'";
		List<Bean> filelist = ServDao.finds("SY_COMM_FILE", where);
		//转换成json格式字符串
			 ObjectMapper mapper = new ObjectMapper();    
		       StringWriter w = new StringWriter();  
		       try {
				mapper.writeValue(w, filelist);
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
}