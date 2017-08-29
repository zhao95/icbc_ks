package com.rh.ts.xmgl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.icbc.ctp.utility.StringUtil;
import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.util.Constant;
import com.rh.ts.pvlg.mgr.GroupMgr;
/**
 * 
 * @author 
 * @version 
 *
 */
public class XmglServ extends CommonServ {
	/** 群组角色服务编码 */
	private static final String TS_XMGL_BMGL = "TS_XMGL_BMGL";	
/**
 * 项目管理
 * @author LIAN
 * @param paramBean
 * 
 */
public void copy(Bean paramBean){
    //获取服务ID
    String servId=paramBean.getStr(Constant.PARAM_SERV_ID);
    //获取 主键id  list
    String dataId = paramBean.getStr("pkCodes");
  //根据服务id 主键id获取 当前对象
    Bean bean = ServDao.find(servId, dataId);
    Bean NBean = new Bean();
  NBean.set("XM_TITLE", bean.getStr("XM_TITLE"));
  NBean.set("XM_NAME", bean.getStr("XM_NAME"));
  NBean.set("XM_FQDW_NAME",bean.getStr("XM_FQDW_NAME"));
  NBean.set("XM_TYPE",bean.getStr("XM_TYPE"));
  NBean.set("XM_START",bean.getStr("XM_START"));
  NBean.set("XM_END",bean.getStr("XM_END"));
  NBean.set("XM_GJ",bean.getStr("XM_GJ"));
  NBean.set("XM_FQDW_CODE",bean.getStr("XM_FQDW_CODE"));
  //保存到数据库
  ServDao.save(servId, NBean);

}  

//下一步
public OutBean  saveAndToSZ(Bean bean){
	OutBean result = new OutBean();
	//获取服务ID
    String servId=bean.getStr(Constant.PARAM_SERV_ID);
   
	//保存到数据库
	Bean res = ServDao.save(servId, bean);
	//从数据库得到xm_id和xm_gj；
	String XMID=res.getStr("XM_ID");
	//String XMGJ=res.getStr("XM_GJ");
	result.setSaveIds(XMID);
	afterSaveToSz(bean);
	return result;
}
      



public void afterSaveToSz(Bean bean){
	String XMID= bean.getStr("XM_ID");
	String XMGJ= bean.getStr("XM_GJ");
	//根据XM_ID查询，从数据库查询
	String where =" and XM_ID='"+XMID+"'";
	List<Bean> szList = ServDao.finds("TS_XMGL_SZ", where);
	if(!StringUtil.isBlank(XMGJ)){
		String[]   gj=XMGJ.split(",");
		//批量保存项目设置
		List<Bean> beans=new ArrayList<Bean>();
		for(int i=0;i<gj.length;i++){
			int j=0;
			for(;j<szList.size();j++){
				if(gj[i].equals(szList.get(j).getStr("XM_SZ_NAME"))){
					break;
				}
			}
			if(szList.size()==0 || j==szList.size()){
				Bean s=new Bean();
                if(gj[i].equals("报名")){
                    s.set("XM_NAME_NUM", 1);	
				}else if(gj[i].equals("审核")){
					s.set("XM_NAME_NUM", 2);
				}else if(gj[i].equals("请假")){
					s.set("XM_NAME_NUM", 3);
				}else if(gj[i].equals("异地借考")){
					s.set("XM_NAME_NUM", 4);
				}else if(gj[i].equals("试卷")){
					s.set("XM_NAME_NUM", 5);
				}else if(gj[i].equals("场次测算")){
					s.set("XM_NAME_NUM", 6);
				}else if(gj[i].equals("考场安排")){
				    s.set("XM_NAME_NUM", 7);
					}
				s.set("XM_SZ_NAME", gj[i]);
				s.set("XM_ID", XMID);
				beans.add(s);
			}
		}
		if(beans.size()>0){
			ServDao.creates("TS_XMGL_SZ", beans);
		}
	}
	//不存在则删除
	String delIds ="";
	String bmid="";
	for(Bean s:szList){
		int index = XMGJ.indexOf(s.getStr("XM_SZ_NAME"));
		if(index==-1){
			delIds+=","+s.getId();
			String wherebmgl =" and XM_SZ_ID='"+s.getId()+"'";
			List<Bean> BMList = ServDao.finds("TS_XMGL_BMGL", wherebmgl);
			if(BMList.size()>0){
				for(Bean BM:BMList){
					 bmid=BM.getId();
				}
			}
		}
	}
	
	if(!StringUtil.isBlank(delIds)){
		//删除项目设置
		String sql ="delete from ts_xmgl_sz where XM_SZ_ID in ('"+delIds.substring(1).replace(",", "','")+"')";
		Transaction.getExecutor().execute(sql);
		//删除报名
		String bmsql ="delete from ts_xmgl_bmgl where XM_SZ_ID in ('"+delIds.substring(1).replace(",", "','")+"')";
		Transaction.getExecutor().execute(bmsql);
		//删除人员群组
				String ryqz ="delete from ts_xmgl_bmgl where XM_SZ_ID in ('"+delIds.substring(1).replace(",", "','")+"')";
				Transaction.getExecutor().execute(ryqz);
	}
	
	if(!StringUtil.isBlank(bmid)){
		//删除考试类别
		String  kslb="delete from  ts_xmgl_bm_kslb  where  BM_ID='"+bmid+"'";
		Transaction.getExecutor().execute(kslb);
		//删除非资格考试
		String  fzgks="delete from  ts_xmgl_bm_fzgks  where  BM_ID='"+bmid+"'";
		Transaction.getExecutor().execute(fzgks);
	}
}

//根据XM_ID删除项目管理设置数据
public void delSzByXmid(Bean bean){
	  String xmid= bean.getStr("XM_ID");
	  String sql="delete from ts_xmgl_sz where XM_ID='"+xmid+"'";
	  Transaction.getExecutor().execute(sql);
}

@Override
protected void afterDelete(ParamBean paramBean, OutBean outBean) {
    String XM_IDs = outBean.getDeleteIds();
    if (!StringUtil.isBlank(XM_IDs)) {
       String sql ="delete from ts_xmgl_sz where XM_ID in ('"+XM_IDs.replace(",", "','")+"')";
       Transaction.getExecutor().execute(sql);
    }
}

public Bean getXmList(Bean paramBean){
	List<Bean> list = ServDao.finds("TS_XMGL", "");
	String s = "";
	for (int i =0;i<list.size();i++) {
		if(list.size()==0){
			s+=list.get(i).getId();
		}else if(i==(list.size()-1)){
			s+=list.get(i).getId();
		}else{
			s+=list.get(i).getId()+",";
		}
	}
	Bean out = new Bean();
	out.set("xid", s);
	return out;
}
public Bean getUserXm(Bean paramBean){
	Bean outBean = new Bean();
	String user_code = paramBean.getStr("user_code");
	//从已报名的考试中找到已报名的考试信息   判断是否报名了  报的是什么
	String where = "AND BM_CODE="+"'"+user_code+"'";
	List<Bean> baominglist = ServDao.finds("TS_BMLB_BM",where);
	List<String> stringlist = new ArrayList<String>();
	if(baominglist.size()!=0){
		
		for(int a=0;a<baominglist.size();a++){
			//获取报名的 项目信息  的name  将报名项目名称放到array中
	String XM_ID = baominglist.get(a).getStr("XM_ID");
			if(XM_ID!=""){
				
		stringlist.add(XM_ID);
			}
		}
	}
	//本人所在的群组编码
	String qz = GroupMgr.getGroupCodes(user_code);
	List<Bean> list = ServDao.finds("TS_XMGL", "");
	String s = "";
	for (int i =0;i<list.size();i++) {
		if(i==(list.size()-1)){
			s+=list.get(i).getId();
		}else{
			s+=list.get(i).getId()+",";
		}
	}
	String[] xmarray = s.split(",");
	//将可见的 项目 ID 放到新的数组中
	List<String>  kjxm = new ArrayList<String>();
	//遍历项目ID  匹配项目和本人的 群组权限
	for(int a=0;a<xmarray.length;a++){
		ParamBean param = new ParamBean();
		param.set("xmid", xmarray[a]);
		Bean outBeanCode = ServMgr.act("TS_XMGL_RYGL_V","getCodes",param);
		String codes = outBeanCode.getStr("rycodes");
	Boolean boo = false;
	if(codes==""){
	}else{
	//本人所在的群组编码
	String[] codeArray = codes.split(",");
	String[] qzArray = qz.split(",");
	for(int b=0;b<qzArray.length;b++){
		if(Arrays.asList(codeArray).contains(qzArray[b])){
			boo=true;
		}
	}
	}
	//可见的项目id
	if(boo==true){
		kjxm.add(xmarray[a]);
	}
	}
	//kjxm为可见项目idlist   stringlist 为已报名的项目idlist
	List<Bean> lastlist = new ArrayList<Bean>();
	for(int i=0;i<list.size();i++){
		Bean bean = list.get(i);
		//项目中已存在array的  title  数据  将展示在  已报名信息中
		String id = bean.getStr("XM_ID");
		if(stringlist.contains(id)|| !kjxm.contains(id)){
			//已报名这个考试之后  或者他不能报名这个考试 中断循环 继续开始
			continue;
		}else{
			lastlist.add(bean);
		}
	}
	
	//将lastlist转换为 json字符串传给前台
	 ObjectMapper mapper = new ObjectMapper();    
     StringWriter w = new StringWriter();  
     try {
		mapper.writeValue(w, lastlist);
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