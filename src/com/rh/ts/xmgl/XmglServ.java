
package com.rh.ts.xmgl;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.icbc.ctp.utility.StringUtil;
import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.util.Constant;
import com.rh.core.util.Strings;
import com.rh.ts.pvlg.PvlgUtils;

/**
 * 
 * @author
 * @version
 *
 */
public class XmglServ extends CommonServ {
	/** 群组角色服务编码 */
//	private static final String TS_XMGL_BMGL = "TS_XMGL_BMGL";

	/**
	 * 项目管理
	 * 
	 * @author LIAN
	 * @param paramBean
	 * 
	 */
	public  void copy(ParamBean paramBean) {
		 //OutBean outBean = new OutBean();
		// 获取服务ID
		String servId = paramBean.getStr(Constant.PARAM_SERV_ID);
		// 获取 主键id list
		String dataId = paramBean.getStr("pkCodes");
		// 根据服务id 主键id获取 当前对象
		Bean bean = ServDao.find(servId, dataId);
		Bean NBean = new Bean();
		NBean.set("XM_TITLE", bean.getStr("XM_TITLE"));
		NBean.set("XM_NAME", bean.getStr("XM_NAME") + "_复制");
		NBean.set("XM_FQDW_NAME", bean.getStr("XM_FQDW_NAME"));
		NBean.set("XM_TYPE", bean.getStr("XM_TYPE"));
		NBean.set("XM_START", bean.getStr("XM_START"));
		NBean.set("XM_END", bean.getStr("XM_END"));
		NBean.set("XM_KSSTARTDATA", bean.getStr("XM_KSSTARTDATA"));
		NBean.set("XM_KSENDDATA", bean.getStr("XM_KSENDDATA"));
		NBean.set("CTLG_PCODE", bean.getStr("CTLG_PCODE"));
		//NBean.set("XM_STATE", bean.getStr("XM_STATE"));
		NBean.set("XM_STATE", "未发布");
		NBean.set("XM_JD", "0%");
		NBean.set("EXCEL_TEMPLATE_ID", bean.getStr("EXCEL_TEMPLATE_ID"));
		NBean.set("XM_GJ", bean.getStr("XM_GJ"));
		NBean.set("XM_FQDW_CODE", bean.getStr("XM_FQDW_CODE"));
		NBean.set("XM_KHDKZ", bean.getStr("XM_KHDKZ"));
		NBean.set("XM_KCAP_PUBLISH_USER_CODE", bean.getStr("XM_KCAP_PUBLISH_USER_CODE"));
		NBean.set("XM_KCAP_PUBLISH_TIME", bean.getStr("XM_KCAP_PUBLISH_TIME"));
		Bean beanA= ServDao.save(servId, NBean);
		afterSaveToSz( beanA);
		//报名模块
		copyBmgl(dataId,beanA);
       //审核模块
		copybmsh(dataId,beanA);
       //请假模块
		copyqjgl(dataId,beanA);
		//借考管理
		copyjkgl(dataId,beanA);
		//考场
		copyKczgl( dataId, beanA);
	}
	//报名管理      dataId老项目管理
public  void   copyBmgl(String dataId,Bean beanA){
	//根据old项目ID查找老数据
	String bmglservId="TS_XMGL_BMGL";
	String   oldXmidWhere="and  XM_ID='"+dataId+"'";
	List<Bean>  bmglBean= ServDao.finds(bmglservId, oldXmidWhere);//1条
	//得到新数据的xm_id，得到新数据的项目设置id    beanA为新的项目数据
	String  nXmid=beanA.getStr("XM_ID");
	String  bmszServId="TS_XMGL_SZ";
	String  nXmszidWhere="and XM_ID='"+nXmid+"' and   XM_NAME_NUM=1";//新xmid
	List<Bean> xmszBean = ServDao.finds(bmszServId, nXmszidWhere);
	String nXmszid="";
	Bean  beanBmgl=new  Bean();
	if(xmszBean !=null && !xmszBean.isEmpty()){
	 nXmszid=	xmszBean.get(0).getStr("XM_SZ_ID");
	}
	if(bmglBean !=null && !bmglBean.isEmpty()){
			Bean  nbean  =new   Bean();
			nbean.copyFrom(bmglBean.get(0));
			nbean.set("XM_ID", nXmid);
			nbean.set("XM_SZ_ID", nXmszid);
			
			nbean.set("BM_NAME", bmglBean.get(0).getStr("BM_NAME")+"_复制");
			nbean.remove("BM_ID");
			nbean.remove("_PK_");
			 beanBmgl=ServDao.save(bmglservId, nbean);
	}


	   //考试类别组复制	 根据xmid找到老数据
		String   oKsqzServId="TS_XMGL_BM_KSQZ";
		// 考试类别复制
		String  OkslbServId="TS_XMGL_BM_KSLB";
		//Bean  kaqzBean=new  Bean();
		List<Bean>  ksqzBean= ServDao.finds(oKsqzServId, oldXmidWhere);//多条
		Bean   ksqzIdBean   =new   Bean();
		if(ksqzBean !=null && !ksqzBean.isEmpty()){
			for(int i=0;i<ksqzBean.size();i++){
			Bean  nksqzbean  =new   Bean();
			nksqzbean.copyFrom(ksqzBean.get(i));
			nksqzbean.set("XM_ID", nXmid);
			nksqzbean.set("XM_SZ_ID", nXmszid);
			nksqzbean.set("BM_ID", beanBmgl.getId());
			nksqzbean.remove("KSQZ_ID");
			
			nksqzbean.remove("_PK_");
			ksqzIdBean =ServDao.save(oKsqzServId, nksqzbean);
			
			List<Bean>  OkslbBeanList= ServDao.finds(OkslbServId, oldXmidWhere);//1条
			if(OkslbBeanList !=null && !OkslbBeanList.isEmpty()){
				for(int j=0;j<OkslbBeanList.size();j++){
					Bean  kslbbean  =new   Bean();
					kslbbean.copyFrom(OkslbBeanList.get(j));
					kslbbean.set("XM_ID", nXmid);
					kslbbean.set("XM_SZ_ID", nXmszid);
					kslbbean.set("BM_ID", beanBmgl.getId());
					kslbbean.set("KSQZ_ID", ksqzIdBean.getId());
					kslbbean.remove("KSLB_ID");
					kslbbean.remove("_PK_");
					ServDao.save(OkslbServId, kslbbean);
				}
			}
			//copyGZ ( oldXmidWhere,nXmid,ksqzIdBean.getId());2018-1-18
			}
		}
		
		
		
//禁考管理复制，需要oldxmid
	String jkglgzServId="TS_XMGL_BM_JKGZ";
	//List<Bean>  OjkglgzBean= ServDao.finds(jkglgzServId, oldXmidWhere);//1条ts_xmgl_bm_jkglgz
	List<Bean>  OjkglgzBean= ServDao.finds(jkglgzServId, oldXmidWhere);//1条
	if(OjkglgzBean !=null && !OjkglgzBean.isEmpty()){
		Bean  jkglbean  =new   Bean();
		jkglbean.copyFrom(OjkglgzBean.get(0));
		jkglbean.set("XM_ID", nXmid);
		jkglbean.remove("GZ_ID");
		jkglbean.remove("_PK_");
		ServDao.save(jkglgzServId, jkglbean);
	}
//可报名群组       TS_BM_GROUP
	String bmGroupServId="TS_BM_GROUP";
	List<Bean>  ObmGroupBeanList= ServDao.finds(bmGroupServId, oldXmidWhere);//1条
	if(ObmGroupBeanList !=null && !ObmGroupBeanList.isEmpty()){
		for(int i=0;i<ObmGroupBeanList.size();i++){
		Bean  bmgroupbean  =new   Bean();
		bmgroupbean.copyFrom(ObmGroupBeanList.get(i));
		bmgroupbean.set("XM_ID", nXmid);
		bmgroupbean.remove("G_ID");
		bmgroupbean.remove("_PK_");
		ServDao.save(bmGroupServId, bmgroupbean);
		}
	}	
//非资格考试复制xm——id
	String  OfzgksServId="TS_XMGL_BM_FZGKS";
	List<Bean>  OfzgksBeanList= ServDao.finds(OfzgksServId, oldXmidWhere);//1条
	if(OfzgksBeanList !=null && !OfzgksBeanList.isEmpty()){
		for(int i=0;i<OfzgksBeanList.size();i++){
			Bean  fzgksbean  =new   Bean();
			fzgksbean.copyFrom(OfzgksBeanList.get(i));
			fzgksbean.set("XM_ID", nXmid);
			fzgksbean.set("XM_SZ_ID", nXmszid);
			fzgksbean.set("BM_ID", beanBmgl.getId());
			fzgksbean.remove("FZGKS_ID");
			fzgksbean.remove("_PK_");
			ServDao.save(OfzgksServId, fzgksbean);
		}
	}
}
	
////审核规则的复制2018-1-18
//public  void  copyGZ (String oldXmidWhere,String nXmid,String   ksqzId){
//	//审核规则的复制
//		String  ObmshgzkServId="TS_XMGL_BMSH_SHGZ";
//		Bean  GZBean  =new   Bean();
//		List<Bean>  ObmshgzkBeanList= ServDao.finds(ObmshgzkServId, oldXmidWhere);//1条
//		if(ObmshgzkBeanList !=null && !ObmshgzkBeanList.isEmpty()){
//			for(int i=0;i<ObmshgzkBeanList.size();i++){
//				Bean  bmshgzkbean  =new   Bean();
//				bmshgzkbean.copyFrom(ObmshgzkBeanList.get(i));
//				bmshgzkbean.set("XM_ID", nXmid);
//				bmshgzkbean.set("KSQZ_ID", ksqzId);
//				bmshgzkbean.remove("GZ_ID");
//				bmshgzkbean.remove("_PK_");
//				GZBean =ServDao.save(ObmshgzkServId, bmshgzkbean);
//				copyGZMX ( oldXmidWhere, nXmid,   ksqzId, GZBean.getId());
//			}
//		}
//}
	
////规则详细2018-1-18
//public  void  copyGZMX (String oldXmidWhere,String nXmid,String   ksqzId,String  gzid){
//	//审核规则的复制
//		String  ObmshgzkmxServId="TS_XMGL_BMSH_SHGZ_MX";
//		List<Bean>  ObmshgzkBeanList= ServDao.finds(ObmshgzkmxServId, oldXmidWhere);//1条
//		if(ObmshgzkBeanList !=null && !ObmshgzkBeanList.isEmpty()){
//			for(int i=0;i<ObmshgzkBeanList.size();i++){
//				Bean  bmshgzkmxbean  =new   Bean();
//				bmshgzkmxbean.copyFrom(ObmshgzkBeanList.get(i));
//				bmshgzkmxbean.set("XM_ID", nXmid);
//				bmshgzkmxbean.set("KSQZ_ID", ksqzId);
//				bmshgzkmxbean.set("GZ_ID", gzid);//gzid
//				bmshgzkmxbean.remove("MX_ID");
//				bmshgzkmxbean.remove("_PK_");
//				ServDao.save(ObmshgzkmxServId, bmshgzkmxbean);
//			}
//		}
//}
//报名审核复制
public  void  copybmsh(String dataId,Bean beanA){
	String bmshservId="TS_XMGL_BMSH";
	String   oldXmidWhere="and  XM_ID='"+dataId+"'";
	List<Bean>  bmshBean= ServDao.finds(bmshservId, oldXmidWhere);//1条
	//得到新数据的xm_id，得到新数据的项目设置id    beanA为新的项目数据
	String  nXmid=beanA.getStr("XM_ID");
	String  bmszServId="TS_XMGL_SZ";
	String  nXmszidWhere="and XM_ID='"+nXmid+"' and   XM_NAME_NUM=2";//新xmid
	List<Bean> xmszBean = ServDao.finds(bmszServId, nXmszidWhere);
	String nXmszid="";
	
	if(xmszBean !=null && !xmszBean.isEmpty()){
	 nXmszid=	xmszBean.get(0).getStr("XM_SZ_ID");
	}
	if(bmshBean !=null && !bmshBean.isEmpty()){
			Bean  nbean  =new   Bean();
			nbean.copyFrom(bmshBean.get(0));
			nbean.set("XM_ID", nXmid);
			nbean.set("XM_SZ_ID", nXmszid);
			nbean.remove("SH_ID");
			nbean.remove("_PK_");
			ServDao.save(bmshservId, nbean);
	}
	
	}
//请假管理
public  void  copyqjgl(String dataId,Bean beanA){
	String QJGLservId="TS_XMGL_QJGL";
	String   oldXmidWhere="and  XM_ID='"+dataId+"'";
	List<Bean>  bmshBean= ServDao.finds(QJGLservId, oldXmidWhere);//1条
	//得到新数据的xm_id，得到新数据的项目设置id    beanA为新的项目数据
	String  nXmid=beanA.getStr("XM_ID");
	String  bmszServId="TS_XMGL_SZ";
	String  nXmszidWhere="and XM_ID='"+nXmid+"' and   XM_NAME_NUM=3";//新xmid
	List<Bean> xmszBean = ServDao.finds(bmszServId, nXmszidWhere);
	String nXmszid="";
	if(xmszBean !=null && !xmszBean.isEmpty()){
	 nXmszid=	xmszBean.get(0).getStr("XM_SZ_ID");
	}
	if(bmshBean !=null && !bmshBean.isEmpty()){
			Bean  nbean  =new   Bean();
			nbean.copyFrom(bmshBean.get(0));
			nbean.set("XM_ID", nXmid);
			nbean.set("XM_SZ_ID", nXmszid);
			nbean.remove("QJ_ID");
			nbean.remove("_PK_");
			ServDao.save(QJGLservId, nbean);
	}
	
	}
//借考管理
public  void  copyjkgl(String dataId,Bean beanA){
	String jkglservId="TS_XMGL_YDJK";
	String   oldXmidWhere="and  XM_ID='"+dataId+"'";
	List<Bean>  bmshBean= ServDao.finds(jkglservId, oldXmidWhere);//1条
	//得到新数据的xm_id，得到新数据的项目设置id    beanA为新的项目数据
	String  nXmid=beanA.getStr("XM_ID");
	String  bmszServId="TS_XMGL_SZ";
	String  nXmszidWhere="and XM_ID='"+nXmid+"' and   XM_NAME_NUM=4";//新xmid
	List<Bean> xmszBean = ServDao.finds(bmszServId, nXmszidWhere);
	String nXmszid="";
	if(xmszBean !=null && !xmszBean.isEmpty()){
	 nXmszid=xmszBean.get(0).getStr("XM_SZ_ID");
	}
	if(bmshBean !=null && !bmshBean.isEmpty()){
			Bean  nbean  =new   Bean();
			nbean.copyFrom(bmshBean.get(0));
			nbean.set("XM_ID", nXmid);
			nbean.set("XM_SZ_ID", nXmszid);
			nbean.remove("YDJK_ID");
			nbean.remove("_PK_");
			ServDao.save(jkglservId, nbean);
	}
}
//考场组群管理
public  void  copyKczgl(String dataId,Bean beanA){
	String kczglervId="TS_XMGL_CCCS_KCZGL";
	String   oldXmidWhere="and  XM_ID='"+dataId+"'";
	List<Bean>  kczglBeanList= ServDao.finds(kczglervId, oldXmidWhere);//多条
	String  nXmid=beanA.getStr("XM_ID");
	if(kczglBeanList !=null && !kczglBeanList.isEmpty()){
		Bean  kczBean  =new  Bean();
		for(int i=0;i<kczglBeanList.size();i++){
			String oldKczid=kczglBeanList.get(i).getStr("KCZ_ID");
			Bean  bmshgzkmxbean  =new   Bean();
			if(kczglBeanList.get(i).getInt("KCZ_STATE")==1){
			bmshgzkmxbean.copyFrom(kczglBeanList.get(i));
			bmshgzkmxbean.set("XM_ID", nXmid);
			bmshgzkmxbean.remove("KCZ_ID");
			bmshgzkmxbean.remove("_PK_");
			kczBean=ServDao.save(kczglervId, bmshgzkmxbean);
			String   kczid=kczBean.getId();
			copyGroup( kczid ,oldKczid);
		}
	  }	
	}
}

public  void  copyGroup(String kczid ,String oldKczid){
	String kczglGroupervId="TS_KCZGL_GROUP";
	String   oldKczWhere="and  KCZ_ID='"+oldKczid+"'";
	List<Bean>  kczglGroupBeanList= ServDao.finds(kczglGroupervId, oldKczWhere);//多条
	if(kczglGroupBeanList !=null && !kczglGroupBeanList.isEmpty()){
		Bean kczGroupBean=new   Bean();
		for(int i=0;i<kczglGroupBeanList.size();i++){
			String oldGroupId= kczglGroupBeanList.get(i).getStr("GROUP_ID");
			Bean  bmshgzkmxbean  =new   Bean();
			bmshgzkmxbean.copyFrom(kczglGroupBeanList.get(i));
			bmshgzkmxbean.set("KCZ_ID", kczid);
			bmshgzkmxbean.remove("GROUP_ID");
			bmshgzkmxbean.remove("_PK_");
			kczGroupBean=ServDao.save(kczglGroupervId, bmshgzkmxbean);
			String   groupId=kczGroupBean.getId();
			copykcgls( groupId ,oldGroupId);
	  }	
	}
}
public  void  copykcgls(String groupId ,String  oldGroupId){
	String kczglKcglServId="TS_KCGL";
	String   oldGroupWhere="and  GROUP_ID='"+oldGroupId+"'";
	List<Bean>  kczglGroupkcBeanList= ServDao.finds(kczglKcglServId, oldGroupWhere);//多条
	if(kczglGroupkcBeanList !=null && !kczglGroupkcBeanList.isEmpty()){
		Bean kczGroupBean=new   Bean();
		for(int i=0;i<kczglGroupkcBeanList.size();i++){
			if(kczglGroupkcBeanList.get(i).getInt("KC_STATE")==5){
				String  oldkcid=kczglGroupkcBeanList.get(i).getStr("KC_ID");
				Bean  bmshgzkmxbean  =new   Bean();
				bmshgzkmxbean.copyFrom(kczglGroupkcBeanList.get(i));
				bmshgzkmxbean.set("GROUP_ID", groupId);
				bmshgzkmxbean.remove("KC_ID");
				bmshgzkmxbean.remove("_PK_");
				kczGroupBean=ServDao.save(kczglKcglServId, bmshgzkmxbean);
				String   kcId=kczGroupBean.getId();
				copyUpdate( kcId ,oldkcid);
				copyIpscope( kcId ,oldkcid);
				copyIpzwh( kcId ,oldkcid);
				copyGljg( kcId ,oldkcid);
				copyGly( kcId ,oldkcid);
				copyZwdyb( kcId ,oldkcid); 
				copyJkip( kcId ,oldkcid);
				
			}
		  }	
	}
}
//TS_KCGL_UPDATE
public  void  copyUpdate(String kcId ,String  oldkcid){
	String kczglKcglServId="TS_KCGL_UPDATE";
	String   oldGroupWhere="and  KC_ID='"+oldkcid+"'";
	List<Bean>  kczglGroupkcBeanList= ServDao.finds(kczglKcglServId, oldGroupWhere);//多条
	if(kczglGroupkcBeanList !=null && !kczglGroupkcBeanList.isEmpty()){
		for(int i=0;i<kczglGroupkcBeanList.size();i++){
				Bean  bmshgzkmxbean  =new   Bean();
				bmshgzkmxbean.copyFrom(kczglGroupkcBeanList.get(i));
				bmshgzkmxbean.set("KC_ID", kcId);
				bmshgzkmxbean.remove("UPDATE_ID");
				bmshgzkmxbean.remove("_PK_");
				ServDao.save(kczglKcglServId, bmshgzkmxbean);
		  }	
	}
}
	


public  void  copyIpscope(String kcId ,String  oldkcid){
	String kczglKcglServId="TS_KCGL_IPSCOPE";
	String   oldGroupWhere="and  KC_ID='"+oldkcid+"'";
	List<Bean>  kczglGroupkcBeanList= ServDao.finds(kczglKcglServId, oldGroupWhere);//多条
	if(kczglGroupkcBeanList !=null && !kczglGroupkcBeanList.isEmpty()){
		for(int i=0;i<kczglGroupkcBeanList.size();i++){
				Bean  bmshgzkmxbean  =new   Bean();
				bmshgzkmxbean.copyFrom(kczglGroupkcBeanList.get(i));
				bmshgzkmxbean.set("KC_ID", kcId);
				bmshgzkmxbean.remove("IPS_ID");
				bmshgzkmxbean.remove("_PK_");
				ServDao.save(kczglKcglServId, bmshgzkmxbean);
		  }	
	}
}





public  void  copyIpzwh(String kcId ,String  oldkcid){
	String kczglKcglServId="TS_KCGL_IPZWH";
	String   oldGroupWhere="and  KC_ID='"+oldkcid+"'";
	List<Bean>  kczglGroupkcBeanList= ServDao.finds(kczglKcglServId, oldGroupWhere);//多条
	if(kczglGroupkcBeanList !=null && !kczglGroupkcBeanList.isEmpty()){
		for(int i=0;i<kczglGroupkcBeanList.size();i++){
				Bean  bmshgzkmxbean  =new   Bean();
				bmshgzkmxbean.copyFrom(kczglGroupkcBeanList.get(i));
				bmshgzkmxbean.set("KC_ID", kcId);
				bmshgzkmxbean.remove("IPZ_ID");
				bmshgzkmxbean.remove("_PK_");
				ServDao.save(kczglKcglServId, bmshgzkmxbean);
		  }	
	}
}



public  void  copyGljg(String kcId ,String  oldkcid){
	String kczglKcglServId="TS_KCGL_GLJG";
	String   oldGroupWhere="and  KC_ID='"+oldkcid+"'";
	List<Bean>  kczglGroupkcBeanList= ServDao.finds(kczglKcglServId, oldGroupWhere);//多条
	if(kczglGroupkcBeanList !=null && !kczglGroupkcBeanList.isEmpty()){
		for(int i=0;i<kczglGroupkcBeanList.size();i++){
				Bean  bmshgzkmxbean  =new   Bean();
				bmshgzkmxbean.copyFrom(kczglGroupkcBeanList.get(i));
				bmshgzkmxbean.set("KC_ID", kcId);
				bmshgzkmxbean.remove("JG_ID");
				bmshgzkmxbean.remove("_PK_");
				ServDao.save(kczglKcglServId, bmshgzkmxbean);
		  }	
	}
}




public  void  copyGly(String kcId ,String  oldkcid){
	String kczglKcglServId="TS_KCGL_GLY";
	String   oldGroupWhere="and  KC_ID='"+oldkcid+"'";
	List<Bean>  kczglGroupkcBeanList= ServDao.finds(kczglKcglServId, oldGroupWhere);//多条
	if(kczglGroupkcBeanList !=null && !kczglGroupkcBeanList.isEmpty()){
		for(int i=0;i<kczglGroupkcBeanList.size();i++){
				Bean  bmshgzkmxbean  =new   Bean();
				bmshgzkmxbean.copyFrom(kczglGroupkcBeanList.get(i));
				bmshgzkmxbean.set("KC_ID", kcId);
				bmshgzkmxbean.remove("GLY_ID");
				bmshgzkmxbean.remove("_PK_");
				ServDao.save(kczglKcglServId, bmshgzkmxbean);
		  }	
	}
}





public  void  copyZwdyb(String kcId ,String  oldkcid){
	String kczglKcglServId="TS_KCGL_ZWDYB";
	String   oldGroupWhere="and  KC_ID='"+oldkcid+"'";
	List<Bean>  kczglGroupkcBeanList= ServDao.finds(kczglKcglServId, oldGroupWhere);//多条
	if(kczglGroupkcBeanList !=null && !kczglGroupkcBeanList.isEmpty()){
		for(int i=0;i<kczglGroupkcBeanList.size();i++){
				Bean  bmshgzkmxbean  =new   Bean();
				bmshgzkmxbean.copyFrom(kczglGroupkcBeanList.get(i));
				bmshgzkmxbean.set("KC_ID", kcId);
				bmshgzkmxbean.remove("ZW_ID");
				bmshgzkmxbean.remove("_PK_");
				ServDao.save(kczglKcglServId, bmshgzkmxbean);
		  }	
	}
}


public  void  copyJkip(String kcId ,String  oldkcid){
	String kczglKcglServId="TS_KCGL_JKIP";
	String   oldGroupWhere="and  KC_ID='"+oldkcid+"'";
	List<Bean>  kczglGroupkcBeanList= ServDao.finds(kczglKcglServId, oldGroupWhere);//多条
	if(kczglGroupkcBeanList !=null && !kczglGroupkcBeanList.isEmpty()){
		for(int i=0;i<kczglGroupkcBeanList.size();i++){
				Bean  bmshgzkmxbean  =new   Bean();
				bmshgzkmxbean.copyFrom(kczglGroupkcBeanList.get(i));
				bmshgzkmxbean.set("KC_ID", kcId);
				bmshgzkmxbean.remove("JKIP_ID");
				bmshgzkmxbean.remove("_PK_");
				ServDao.save(kczglKcglServId, bmshgzkmxbean);
		  }	
	}
}

//	public OutBean copy(ParamBean paramBean) {
//		OutBean outBean = new OutBean();
//		String servId = paramBean.getStr("serv");
//		//String primaryColCode = paramBean.getStr("primaryColCode");
//		String pkCode = paramBean.getStr("pkCodes");
//		Bean bean = ServDao.find(servId, pkCode);
//		String name=bean.getStr("XM_NAME");
//		
//		//bean.remove(primaryColCode);
//		bean.set("XM_ID","");
//	
//		bean.set("XM_NAME", name+"_复制");
//		bean = delSysCol(bean);
//		Bean newBean = ServDao.create(servId, bean);
//		if (!newBean.getId().equals("")) {
//			//copyLinkData(servId, pkCode, newBean.getId());
//			outBean.setOk();
//		}
//		return outBean;
//	}
	 /**
     * 删除系统字段
     * @param bean
     * @return
     */
    public Bean delSysCol(Bean bean){
	bean.remove("S_USER");
	bean.remove("S_DEPT");
	bean.remove("S_ODEPT");
	bean.remove("S_TDEPT");
	bean.remove("S_ATIME");
	bean.remove("S_MTIME");
	return bean;
    }
	// 下一步
	public OutBean saveAndToSZ(Bean bean) {
		OutBean result = new OutBean();
		// 获取服务ID
		String servId = bean.getStr(Constant.PARAM_SERV_ID);

		// 保存到数据库
		Bean res = ServDao.save(servId, bean);
		// 从数据库得到xm_id和xm_gj；
		String XMID = res.getStr("XM_ID");
		// String XMGJ=res.getStr("XM_GJ");
		result.setSaveIds(XMID);
		afterSaveToSz(bean);
		return result;
	}

	public OutBean afterSaveToSz(Bean bean) {
		String XMID = bean.getStr("XM_ID");
		String XMGJ = bean.getStr("XM_GJ");
		// 根据XM_ID查询，从数据库查询XM _JD
		String where = " and XM_ID='" + XMID + "'";
		List<Bean> szList = ServDao.finds("TS_XMGL_SZ", where);
		if (!StringUtil.isBlank(XMGJ)) {
			String[] gj = XMGJ.split(",");
			// 批量保存项目设置
			List<Bean> beans = new ArrayList<Bean>();
			for (int i = 0; i < gj.length; i++) {
				int j = 0;
				for (; j < szList.size(); j++) {
					if (gj[i].equals(szList.get(j).getStr("XM_SZ_NAME"))) {
						break;
					}
				}
				if (szList.size() == 0 || j == szList.size()) {
					Bean s = new Bean();
					if (gj[i].equals("报名")) {
						s.set("XM_NAME_NUM", 1);
						s.set("XM_SZ_TYPE", "未开启");
					} else if (gj[i].equals("审核")) {
						s.set("XM_NAME_NUM", 2);
						s.set("XM_SZ_TYPE", "未开启");
					} else if (gj[i].equals("请假")) {
						s.set("XM_NAME_NUM", 3);
						s.set("XM_SZ_TYPE", "未开启");
					} else if (gj[i].equals("异地借考")) {
						s.set("XM_NAME_NUM", 4);
						s.set("XM_SZ_TYPE", "未开启");
					} else if (gj[i].equals("试卷")) {
						s.set("XM_NAME_NUM", 5);
						s.set("XM_SZ_TYPE", "未开启");
					} else if (gj[i].equals("场次测算")) {
						s.set("XM_NAME_NUM", 6);
						s.set("XM_SZ_TYPE", "未开启");
					} else if (gj[i].equals("考场安排")) {
						s.set("XM_NAME_NUM", 7);
						s.set("XM_SZ_TYPE", "未开启");
					}
					s.set("XM_SZ_NAME", gj[i]);
					s.set("XM_ID", XMID);
					beans.add(s);
				}
			}
			if (beans.size() > 0) {
				ServDao.creates("TS_XMGL_SZ", beans);
			}
		}
		// 不存在则删除
		String delIds = "";
		String bmid = "";
		for (Bean s : szList) {
			int index = XMGJ.indexOf(s.getStr("XM_SZ_NAME"));
			if (index == -1) {
				delIds += "," + s.getId();
				String wherebmgl = " and XM_SZ_ID='" + s.getId() + "'";
				List<Bean> BMList = ServDao.finds("TS_XMGL_BMGL", wherebmgl);
				if (BMList.size() > 0) {
					for (Bean BM : BMList) {
						bmid = BM.getId();
					}
				}
			}
		}

		if (!StringUtil.isBlank(delIds)) {
			// 删除项目设置
			String sql = "delete from ts_xmgl_sz where XM_SZ_ID in ('" + delIds.substring(1).replace(",", "','") + "')";
			Transaction.getExecutor().execute(sql);
			// 删除报名
			String bmsql = "delete from ts_xmgl_bmgl where XM_SZ_ID in ('" + delIds.substring(1).replace(",", "','")
					+ "')";
			Transaction.getExecutor().execute(bmsql);
			// 删除人员群组
			String ryqz = "delete from ts_xmgl_bmgl where XM_SZ_ID in ('" + delIds.substring(1).replace(",", "','")
					+ "')";
			Transaction.getExecutor().execute(ryqz);
			// 删除审核
			String bmsh = "delete from ts_xmgl_bmsh where XM_SZ_ID in ('" + delIds.substring(1).replace(",", "','")
					+ "')";
			Transaction.getExecutor().execute(bmsh);
			// 删除请假
			String qj = "delete from ts_xmgl_qjgl where XM_SZ_ID in ('" + delIds.substring(1).replace(",", "','")
					+ "')";
			Transaction.getExecutor().execute(qj);
			// 删除异地借考
			String ydjk = "delete from ts_xmgl_ydjk where XM_SZ_ID in ('" + delIds.substring(1).replace(",", "','")
					+ "')";
			Transaction.getExecutor().execute(ydjk);
		}

		if (!StringUtil.isBlank(bmid)) {
			// 删除考试类别
			String kslb = "delete from  ts_xmgl_bm_kslb  where  BM_ID='" + bmid + "'";
			Transaction.getExecutor().execute(kslb);
			// 删除非资格考试
			String fzgks = "delete from  ts_xmgl_bm_fzgks  where  BM_ID='" + bmid + "'";
			Transaction.getExecutor().execute(fzgks);
		}
		return new OutBean();
	}

	// 根据XM_ID删除项目管理设置数据
	public void delSzByXmid(Bean bean) {
		String xmid = bean.getStr("XM_ID");
		String sql = "delete from ts_xmgl_sz where XM_ID='" + xmid + "'";
		Transaction.getExecutor().execute(sql);
	}

	// @Override
	// protected void afterDelete(ParamBean paramBean, OutBean outBean) {
	// String XM_IDs = outBean.getDeleteIds();
	// if (!StringUtil.isBlank(XM_IDs)) {
	// String sql = "delete from ts_xmgl_sz where XM_ID in ('" +
	// XM_IDs.replace(",", "','") + "')";
	// Transaction.getExecutor().execute(sql);
	// }
	// }

	public Bean getXmList(Bean paramBean) {
		List<Bean> list = ServDao.finds("TS_XMGL", "");
		String s = "";
		for (int i = 0; i < list.size(); i++) {
			if (list.size() == 0) {
				s += list.get(i).getId();
			} else if (i == (list.size() - 1)) {
				s += list.get(i).getId();
			} else {
				s += list.get(i).getId() + ",";
			}
		}
		Bean out = new Bean();
		out.set("xid", s);
		return out;
	}

	/**
	 * 显示所有机构能考试的项目
	 * 
	 * @param paramBean
	 * @return
	 * @throws ParseException
	 */
	public Bean getUserXm(Bean paramBean) throws ParseException {
		String xmname = paramBean.getStr("xmname");
		Bean _PAGE_ = new Bean();
		String NOWPAGE = paramBean.getStr("nowpage");
		String SHOWNUM = paramBean.getStr("shownum");
		if("".equals(SHOWNUM)){
			SHOWNUM="10";
		}
		if("".equals(NOWPAGE)){
			NOWPAGE = "1";
		}
		Bean outBean = new Bean();
		UserBean userBean = Context.getUserBean();
		String odeptcode = "";
		List<String> deptcodelist = new ArrayList<String>();
		// 默认主机构报名
		odeptcode = userBean.getDeptCode();
		deptcodelist.add(odeptcode);

		// 本人所在的群组编码
		ParamBean param1 = new ParamBean();
		OutBean act = ServMgr.act("TS_BM_GROUP_USER", "getBmGroupCodes", param1);
		String qz = act.getStr("qzcodes");

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datestr = sdf.format(date);
		// 如果查询本人所在机构 是否 在 某个群组下
		/*String whereqz = "AND G_TYPE=2";
		List<Bean> finds = ServDao.finds("TS_BM_GROUP_DEPT", whereqz);*/
		// 所有机构
		String sql = "SELECT g_id FROM (SELECT DISTINCT`t`.`G_ID`"+
		"AS `g_id`,`b`.`CODE_PATH` AS `code_path` "
		+ "FROM `ts_bm_group_user_dept` `t` LEFT JOIN `sy_org_dept` `b` ON `t`.`USER_DEPT_CODE` = `b`.`DEPT_CODE`"
          +"AND `t`.`G_TYPE` = 2) a WHERE '"+odeptcode+"' IN(SELECT dept_code FROM sy_org_dept WHERE code_path LIKE concat(a.`code_path`,'%')) AND G_ID IN(SELECT G_ID FROM TS_BM_GROUP WHERE '"+datestr+"' between G_DEAD_BEGIN and G_DEAD_END)";
		/*for (Bean bean : finds) {
			String str = bean.getStr("USER_DEPT_CODE");// 机构编码
			if("".equals(str)){
				continue;
			}
			if("0010100000".equals(str)){
				qz += "," + bean.getStr("G_ID");
				continue;
			}
			
			if(deptcodelist.contains(str)){
				qz += "," + bean.getStr("G_ID");
				continue;
			}
			//存 Code_path  和  g_id  的表
			select a.g_id,b.code_path from TS_BM_GROUP_USER_DEPT  a left join sy_org_dept b on a.user_dept_code = a.dept_code;
			"select distinct code_path from sy_org_dept where dept_code in(select user_dept_code from a where g_type='2') "
			String sql1 = "select g_id from TS_BM_GROUP_USER_DEPT  a where exists(select '' from sy_org_dept b where code_path like concat('"+codepath+"','%') and a.user_dept_code=b.dept_code)";
				List<DeptBean> listdept = OrgMgr.getChildDeptsAll(bean.getStr("S_CMPY"),str);
				// 判断此人是否在此机构下
				// 管理员以下的所有机构
				if(listdept==null){
					continue;
				}
				for (Bean deptBean : listdept) {
					if (deptcodelist.contains(deptBean.getStr("DEPT_CODE"))) {
						qz += "," + bean.getStr("G_ID");
						break;
					}
			}
				
		}*/
		List<Bean> query = Transaction.getExecutor().query(sql);	
		for(int i=0;i<query.size();i++){
			if("".equals(qz)){
				if(i==0){
					qz+="'"+query.get(i).getStr("G_ID")+"'";
				}else{
					qz+=",'"  + query.get(i).getStr("G_ID")+"'";
				}
			}else{
					qz+=",'" + query.get(i).getStr("G_ID")+"'";
			}
		}
		
		if (!Strings.isBlank(qz)) {
			// 去掉重复群组
			qz = Strings.removeSame(qz);
		}
		
		String[] qzArray1 = qz.split(",");
		String servId = "TS_BMSH_STAY";
		String where1 = paramBean.getStr("where");
		int ALLNUM = 0;
		int meiye = Integer.parseInt(SHOWNUM);

		int nowpage = Integer.parseInt(NOWPAGE);
		int showpage = Integer.parseInt(SHOWNUM);
		// 计算第一项 开始
		int chushi = (nowpage - 1) * showpage;
		// 计算结束项
		int jieshu = (nowpage - 1) * showpage + showpage;
		/*for (String string : qzArray1) {
			if (!"".equals(string)) {
				Bean find = ServDao.find("TS_BM_GROUP", string);
				if (find != null) {
					Date date = new Date();
					long time = date.getTime();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					if ("".equals(find.getStr("G_DEAD_BEGIN")) || "".equals(find.getStr("G_DEAD_END"))) {
						continue;
					}
					long time2 = sdf.parse(find.getStr("G_DEAD_BEGIN")).getTime();
					long time3 = sdf.parse(find.getStr("G_DEAD_END")).getTime();
					if (time < time2 || time > time3) {
						// 删除此群组
						int indexOf = Arrays.asList(qzArray1).indexOf(string);
						qzArray1[indexOf] = "";
					}

				}
			}
		}*/
		//包含本人所在群组的  项目
		//进行中
		String sql1="select xm_id from (select m.*,n.g_id from (select a.*,d.bm_end from ts_xmgl a left join (select b.bm_start,b.bm_end,b.xm_id,c.xm_sz_type from TS_XMGL_BMGL b left join TS_XMGL_SZ c ON b.xm_sz_id = c.xm_sz_id where c.xm_sz_type='进行中' )d  on a.xm_id = d.xm_id where '"+datestr+"' between  d.BM_START AND d.BM_END and a.xm_state=1 and a.xm_name like '%"+xmname+"%' order by d.bm_end ASC)m left join TS_BM_GROUP n on m.xm_id = n.xm_id)z where z.g_id in("+qz+")";
		//未开始
		String sql2="select xm_id from (select m.*,n.g_id from (select a.*,d.bm_end from ts_xmgl a left join (select b.bm_start,b.bm_end,b.xm_id,c.xm_sz_type from TS_XMGL_BMGL b left join TS_XMGL_SZ c ON b.xm_sz_id = c.xm_sz_id where c.xm_sz_type='已结束' )d  on a.xm_id = d.xm_id where '"+datestr+"' between  d.BM_START AND d.BM_END and a.xm_state=1 and a.xm_name like '%"+xmname+"%' order by d.bm_end ASC)m left join TS_BM_GROUP n on m.xm_id = n.xm_id)z where z.g_id in("+qz+")";
		//已结束
		String sql3="select xm_id from (select m.*,n.g_id from (select a.*,d.bm_end from ts_xmgl a left join (select b.bm_start,b.bm_end,b.xm_id,c.xm_sz_type from TS_XMGL_BMGL b left join TS_XMGL_SZ c ON b.xm_sz_id = c.xm_sz_id where (c.xm_sz_type='' or c.xm_sz_type='未开启' ))d  on a.xm_id = d.xm_id where '"+datestr+"' between  d.BM_START AND d.BM_END and a.xm_state=1 and a.xm_name like '%"+xmname+"%' order by d.bm_end ASC)m left join TS_BM_GROUP n on m.xm_id = n.xm_id)z where z.g_id in("+qz+")";
		//其它
		String sql4="select xm_id from (select m.*,n.g_id from (select a.*,b.bm_end from ts_xmgl a left join ts_xmgl_bmgl b on a.xm_id = b.xm_id where '"+datestr+"' between  b.BM_TZ_START AND b.BM_TZ_END AND ('"+datestr+"'>b.BM_END OR '"+datestr+"'<b.BM_START ) and a.xm_state=1 and a.xm_name like '%"+xmname+"%' order by b.bm_end ASC)m left join TS_BM_GROUP n on m.xm_id = n.xm_id)z where z.g_id in("+qz+")";
		/*List<Bean> list = Transaction.getExecutor().query(sql1);
		List<Bean> list2 = Transaction.getExecutor().query(sql2);
		List<Bean> list3 = Transaction.getExecutor().query(sql3);
		list.addAll(list2);
		list.addAll(list3);
		list.addAll(list4);*/
		String sqlxm = "select a.*,b.BM_END from ts_xmgl a left join ts_xmgl_bmgl b  on a.xm_id = b.xm_id where  a.xm_id in ("+sql1+") or a.xm_id in("+sql2+") or a.xm_id in ("+sql3+") or a.xm_id in ("+sql4+") order by b.bm_end asc"; 
		ALLNUM = Transaction.getExecutor().count(sqlxm);
		 if(jieshu>ALLNUM){
			 showpage=ALLNUM-chushi;
		 }
		 sqlxm+=" limit "+chushi+","+showpage;
		List<Bean> list = Transaction.getExecutor().query(sqlxm);
		
		
		/*String pxsql = "select m.*,n.g_id from (select a.*,d.bm_end from ts_xmgl a left join (select b.bm_start,b.bm_end,b.xm_id,c.xm_sz_type from TS_XMGL_BMGL b left join TS_XMGL_SZ c ON b.xm_sz_id = c.xm_sz_id)d  on a.xm_id = d.xm_id order by d.bm_end ASC)m left join TS_BM_GROUP n on m.xm_id = n.xm_id limit 0,50";*/


		/*String s = "";
		for (int i = 0; i < list.size(); i++) {
			if (i == (list.size() - 1)) {
				s += list.get(i).getId();
			} else {
				s += list.get(i).getId() + ",";
			}
		}
		String[] xmarray = s.split(",");
		 * */
		// 将可见的 项目 ID 放到新的数组中
		List<Bean> lastlist = new ArrayList<Bean>();
		// 遍历项目ID 匹配项目和本人的 群组权限
		for (Bean bean :list) {
			ParamBean param = new ParamBean();
			param.set("xmid", bean.getStr("XM_ID"));
			OutBean act2 = ServMgr.act("TS_XMGL_BMGL", "getBMState", param);
			bean.set("START_TIME_BM", act2.getStr("START_TIME"));
			bean.set("END_TIME_BM", act2.getStr("END_TIME"));
			bean.set("STATE_BM", act2.getStr("state"));
			if("待报名".equals(act2.getStr("state"))){
				lastlist.add(bean);
			}
		}
		for (Bean bean :list) {
			if(!"待报名".equals(bean.getStr("STATE_BM"))){
				lastlist.add(bean);
			}
		}
		// 计算页数
		int yeshu = ALLNUM / meiye;
		int yushu = ALLNUM % meiye;
		// 获取总页数
		if (yushu != 0) {
			yeshu += 1;
		}

		// 计算第一项 开始
		// 放到Array中

		_PAGE_.set("ALLNUM", lastlist.size());
		_PAGE_.set("NOWPAGE", NOWPAGE);
		_PAGE_.set("PAGES", yeshu);
		_PAGE_.set("SHOWNUM", SHOWNUM);
		outBean.set("alllist", lastlist);
		outBean.set("_PAGE_", _PAGE_);
		outBean.set("first", chushi+1);
		outBean.set("list",lastlist);
		return outBean;
	}

	/**
	 * 以某机构报名
	 * 
	 * @param paramBean
	 * @return
	 * @throws ParseException
	 */
	public Bean getUserXm1(Bean paramBean) throws ParseException {
		Bean outBean = new Bean();
		UserBean userBean = Context.getUserBean();
		String slavecode = paramBean.getStr("odept_code");
		String odeptcode = "";
		if (!"".equals(slavecode)) {
			odeptcode = slavecode;
		} else {
			// 默认主机构报名
			odeptcode = userBean.getDeptCode();
		}

		// 本人所在的群组编码
		ParamBean param1 = new ParamBean();
		OutBean act = ServMgr.act("TS_BM_GROUP_USER", "getBmGroupCodes", param1);
		String qz = act.getStr("qzcodes");

		// 如果查询本人所在机构 是否 在 某个群组下
		String whereqz = "AND G_TYPE=2";
		List<Bean> finds = ServDao.finds("TS_BM_GROUP_DEPT", whereqz);
		// 所有机构
		for (Bean bean : finds) {
			String str = bean.getStr("USER_DEPT_CODE");// 机构编码
			// 判断此人是否在此机构下
			// 管理员以下的所有机构
			List<DeptBean> listdept = OrgMgr.getSubOrgAndChildDepts(bean.getStr("S_CMPY"), str);

			for (DeptBean deptBean : listdept) {
				if (deptBean.getStr("DEPT_CODE").equals(odeptcode)) {
					qz += "," + bean.getStr("G_ID");
				}
			}
		}
		if (!Strings.isBlank(qz)) {
			// 去掉重复群组
			qz = Strings.removeSame(qz);
		}
		String[] qzArray1 = qz.split(",");
		for (String string : qzArray1) {
			if (!"".equals(string)) {
				Bean find = ServDao.find("TS_BM_GROUP", string);
				if (find != null) {
					Date date = new Date();
					long time = date.getTime();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					if ("".equals(find.getStr("G_DEAD_BEGIN")) || "".equals(find.getStr("G_DEAD_END"))) {
						continue;
					}
					long time2 = sdf.parse(find.getStr("G_DEAD_BEGIN")).getTime();
					long time3 = sdf.parse(find.getStr("G_DEAD_END")).getTime();
					if (time < time2 || time > time3) {
						// 删除此群组
						int indexOf = Arrays.asList(qzArray1).indexOf(string);
						qzArray1[indexOf] = "";
					}

				}
			}
		}

		List<Bean> list = ServDao.finds("TS_XMGL", "");
		String s = "";
		for (int i = 0; i < list.size(); i++) {
			if (i == (list.size() - 1)) {
				s += list.get(i).getId();
			} else {
				s += list.get(i).getId() + ",";
			}
		}
		String[] xmarray = s.split(",");
		// 将可见的 项目 ID 放到新的数组中
		List<String> kjxm = new ArrayList<String>();
		// 遍历项目ID 匹配项目和本人的 群组权限
		for (int a = 0; a < xmarray.length; a++) {
			ParamBean param = new ParamBean();
			param.set("xmid", xmarray[a]);
			Bean outBeanCode = ServMgr.act("TS_XMGL_RYGL_V", "getCodes", param);
			String codes = outBeanCode.getStr("rycodes");
			Boolean boo = false;
			if ("".equals(codes)) {
			} else {
				// 本人所在的群组编码
				String[] codeArray = codes.split(",");
				for (int b = 0; b < qzArray1.length; b++) {
					if (Arrays.asList(codeArray).contains(qzArray1[b])) {
						boo = true;
					}
				}
			}
			// 可见的项目id
			if (boo == true) {
				kjxm.add(xmarray[a]);
			}
		}

		// kjxm为可见项目idlist stringlist 为已报名的项目idlist
		List<Bean> lastlist = new ArrayList<Bean>();
		for (int i = 0; i < list.size(); i++) {
			Bean bean = list.get(i);
			// 项目中已存在array的 title 数据 将展示在 已报名信息中
			String id = bean.getStr("XM_ID");
			if (!kjxm.contains(id)) {
				// 已报名这个考试之后 或者他不能报名这个考试 中断循环 继续开始
				continue;
			}
			if ("1".equals(bean.getStr("XM_STATE"))) {
				lastlist.add(bean);
			}
		}

		// 将lastlist转换为 json字符串传给前台
		ObjectMapper mapper = new ObjectMapper();
		StringWriter w = new StringWriter();
		try {
			mapper.writeValue(w, lastlist);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		outBean.set("list", w.toString());
		return outBean;
	}

	/**
	 * 获取此人所在节点下 可审核 的 机构 根据机构 筛选可审核的项目
	 */
	public Bean getDshList(Bean paramBean) {
		String user_code = paramBean.getStr("user_code");
		/* ServDao.finds("TS_XMGL", where); */
		List<Bean> list = ServDao.finds("TS_XMGL", "");
		// 可审核的项目list
		List<Bean> SHlist = new ArrayList<Bean>();
		OutBean out = new OutBean();
		for (Bean bean : list) {
			//多个机构不能用字符串逗号拼接   以后改  不能用like
				//当前审核人 待审核的数据
				SHlist = ServDao.finds("TS_BMSH_STAY", "AND XM_ID='"+bean.getStr("XM_ID")+"' AND SH_OTHER LIKE '%"+user_code+"%' limit 0,50");
		
		}

		out.set("list", SHlist);
		return out;
		
		
	}

	/**
	 * 获取此人所在节点下 可审核 的 机构 根据机构 筛选可审核的项目
	 */
	public Bean getShJsonList(Bean paramBean) {
		String where1 = paramBean.getStr("where");
		String user_code = paramBean.getStr("user_code");
		List<Bean> list = ServDao.finds("TS_XMGL", where1);
		// 可审核的项目list
		List<Bean> SHlist = new ArrayList<Bean>();
		for (Bean bean : list) {
			String id = bean.getId();
			// 查询待审核 表 里的other字段判断 是否包含user_code
			String where = "AND XM_ID=" + "'" + id + "'" + " AND SH_OTHER like" + "'%" + user_code + "%'";
			List<Bean> staylist = ServDao.finds("TS_BMSH_STAY", where);
			List<Bean> NOPASSlist = ServDao.finds("TS_BMSH_NOPASS", where);
			List<Bean> PASSlist = ServDao.finds("TS_BMSH_PASS", where);
			if (staylist.size() != 0 || NOPASSlist.size() != 0 || PASSlist.size() != 0) {
				SHlist.add(bean);
			}
		}
		Bean outBean = new Bean();
		ObjectMapper mapper = new ObjectMapper();
		StringWriter w = new StringWriter();
		try {
			mapper.writeValue(w, SHlist);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		outBean.set("list", w.toString());
		return outBean;
	}

	/**
	 * 获取项目下所有 未审核的 报名 (某一页 每页多少条)  审核数据
	 *
	 */
	public Bean getUncheckList(Bean paramBean) {

		Bean outBean = new Bean();
		String zhuangtai = paramBean.getStr("zhuangtai");
		String user_code = paramBean.getStr("user_code");
		Bean _PAGE_ = new Bean();
		String NOWPAGE = paramBean.getStr("nowpage");
		String SHOWNUM = paramBean.getStr("shownum");
		String where1 = paramBean.getStr("where");
		int ALLNUM = 0;
		int meiye = Integer.parseInt(SHOWNUM);

		int nowpage = Integer.parseInt(NOWPAGE);
		int showpage = Integer.parseInt(SHOWNUM);
		// 计算第一项 开始
		int chushi = (nowpage - 1) * showpage;
		// 计算结束项
		int jieshu = (nowpage - 1) * showpage + showpage;
		
		//进行中的项目
		String sql1 = "SELECT a.xm_id FROM ts_xmgl a LEFT JOIN (select b.sh_rgsh,b.sh_start,b.sh_end,b.sh_look,b.xm_id,c.xm_sz_type from TS_XMGL_BMSH b left join TS_XMGL_SZ c ON b.xm_sz_id = c.xm_sz_id where c.xm_sz_type='进行中' )d ON a.xm_id = d.xm_id WHERE d.sh_rgsh=1 AND NOW() BETWEEN STR_TO_DATE(d.sh_start,'%Y-%m-%d %H:%i:%s') AND STR_TO_DATE(d.sh_end,'%Y-%m-%d %H:%i:%s') "
				 +where1+" AND d.SH_LOOK =1 ";
		//已结束的项目
		String sql4 = "SELECT a.xm_id FROM ts_xmgl a LEFT JOIN (select b.sh_rgsh,b.sh_start,b.sh_end,b.sh_look,b.xm_id,c.xm_sz_type from TS_XMGL_BMSH b left join TS_XMGL_SZ c ON b.xm_sz_id = c.xm_sz_id)d ON a.xm_id = d.xm_id WHERE d.sh_rgsh=1 AND NOW() > STR_TO_DATE(d.sh_end,'%Y-%m-%d %H:%i:%s') "
				 +where1+" AND d.SH_LOOK =1 ";
		
		//可审核的项目 
		String sqlxm = "";
		if ("1".equals(zhuangtai)){
			//进行中的项目
			 sqlxm = "select m.xm_id from ts_xmgl_bmsh m where wfs_id in(select wfs_id from TS_WFS_BMSHLC a where a.shr_usercode = '"+user_code+"') and m.xm_id in("+sql1+")";
		}else if("2".equals(zhuangtai)){
			//已结束的项目
			 sqlxm = "select m.xm_id from ts_xmgl_bmsh m where wfs_id in(select wfs_id from TS_WFS_BMSHLC a where a.shr_usercode = '"+user_code+"') and m.xm_id in("+sql4+")";
		}else{
			 sqlxm = "select m.xm_id from ts_xmgl_bmsh m where wfs_id in(select wfs_id from TS_WFS_BMSHLC a where a.shr_usercode = '"+user_code+"') and (m.xm_id in("+sql1+") or m.xm_id in("+sql4+"))";
		}
		
		ALLNUM = Transaction.getExecutor().count(sqlxm);
		 if(jieshu>ALLNUM){
			 showpage=ALLNUM-chushi;
		 }
		 
		 String lastsql = "select * from (select c.*,d.sh_end from (select a.*,b.xm_sz_type,b.xm_sz_id from ts_xmgl a left join ts_xmgl_sz b on a.xm_id = b.xm_id where b.xm_sz_name = '审核')c left join ts_xmgl_bmsh d on c.xm_sz_id = d.xm_sz_id)e where e.xm_id in ("+sqlxm+") order by e.xm_sz_type asc";
		 lastsql+=" limit "+chushi+","+showpage;
		List<Bean> list = Transaction.getExecutor().query(lastsql);
		
		/*String sql = "SELECT * FROM TS_XMGL WHERE XM_ID IN(select XM_ID from TS_XMGL_BMSH WHERE SH_RGSH = '1') "+where1;
		List<Bean> list = Transaction.getExecutor().query(sql);*/
		List<Bean> pxlist = new ArrayList<Bean>();
		List<Bean> jxlist = new ArrayList<Bean>();
		List<Bean> jshlist = new ArrayList<Bean>();
		SimpleDateFormat simp = new SimpleDateFormat("yyyyMMdd");
		for (Bean bean : list) {
			try {
				Date parse = simp.parse(bean.getStr("SH_END"));
				bean.set("SH_END", simp.format(parse));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 根据报名id找到审核数据的状态
			String id = bean.getStr("XM_ID");
			ParamBean paramb = new ParamBean();
			paramb.set("xmid", id);
			OutBean out = ServMgr.act("TS_XMGL_BMGL", "getSHState", paramb);//审核状态数据
			String state =out.getStr("state");
			String END_TIME =out.getStr("END_TIME");
			if("待报名".equals(state)){
				OutBean out1 = ServMgr.act("TS_BMSH_STAY", "getsingxmnum", paramb);//待审核数据量
				String numstr = out1.getStr("num");
				bean.set("numstr", numstr);
				bean.set("endtimestr", END_TIME);
				bean.set("shstatestr", state);
				jxlist.add(bean);
			}else{
				bean.set("numstr", "0");
				bean.set("endtimestr", END_TIME);
				bean.set("shstatestr", state);
				jshlist.add(bean);
			}
		}
		sort(jxlist);
		sort(jshlist);
		pxlist.addAll(jxlist);
		pxlist.addAll(jshlist);
		// 计算页数
		int yeshu = ALLNUM / meiye;
		int yushu = ALLNUM % meiye;
		// 获取总页数
		if (yushu != 0) {
			yeshu += 1;
		}
		// 放到Array中

		_PAGE_.set("ALLNUM", ALLNUM);
		_PAGE_.set("NOWPAGE", NOWPAGE);
		_PAGE_.set("PAGES", yeshu);
		_PAGE_.set("SHOWNUM", SHOWNUM);
		outBean.set("list", pxlist);
		outBean.set("alllist", pxlist);
		outBean.set("_PAGE_", _PAGE_);
		outBean.set("first", chushi);
		return outBean;
	}
	
	/**
	 * 管理员查看辖内报名情况  分页数据
	 * @param paramBean
	 */
	public OutBean getWithInBm(Bean paramBean){
		OutBean outBean = new OutBean();
		Bean _PAGE_ = new Bean();
		String zhuangtai = paramBean.getStr("zhuangtai");
		
		String where1 = paramBean.getStr("where");
		String NOWPAGE = paramBean.getStr("nowpage");
		String SHOWNUM = paramBean.getStr("shownum");
		List<Bean> list = new ArrayList<Bean>();
		/*List<Bean> finds = ServDao.finds("TS_XMGL", where1);*/
		String sql1 = "SELECT * FROM(SELECT a.*,d.sh_end,d.xm_sz_type FROM ts_xmgl a LEFT JOIN (select b.sh_rgsh,b.sh_start,b.sh_end,b.sh_look,b.xm_id,c.xm_sz_type from TS_XMGL_BMSH b left join TS_XMGL_SZ c ON b.xm_sz_id = c.xm_sz_id where c.xm_sz_type='进行中' and b.SH_RGSH=1)d ON a.xm_id = d.xm_id WHERE d.sh_rgsh=1 AND NOW() BETWEEN STR_TO_DATE(d.sh_start,'%Y-%m-%d %H:%i:%s') AND STR_TO_DATE(d.sh_end,'%Y-%m-%d %H:%i:%s') "
				 +where1+" AND d.SH_LOOK =1 ORDER BY d.sh_end ASC)t1 limit 0,50";
		List<Bean> finds = Transaction.getExecutor().query(sql1);
		
		String sql2 = "SELECT * FROM(SELECT a.*,d.sh_end,d.xm_sz_type FROM ts_xmgl a LEFT JOIN (select b.sh_rgsh,b.sh_start,b.sh_end,b.sh_look,b.xm_id,c.xm_sz_type from TS_XMGL_BMSH b left join TS_XMGL_SZ c ON b.xm_sz_id = c.xm_sz_id where (c.xm_sz_type='未开启' or c.xm_sz_type='') and b.SH_RGSH=1)d ON a.xm_id = d.xm_id WHERE d.sh_rgsh=1 AND NOW() BETWEEN STR_TO_DATE(d.sh_start,'%Y-%m-%d %H:%i:%s') AND STR_TO_DATE(d.sh_end,'%Y-%m-%d %H:%i:%s') "
				 +where1+" AND d.SH_LOOK =1 ORDER BY d.sh_end ASC)t1 limit 0,50";
		List<Bean> find2 = Transaction.getExecutor().query(sql2);
		
		String sql4 = "SELECT * FROM(SELECT a.*,d.sh_end,d.xm_sz_type FROM ts_xmgl a LEFT JOIN (select b.sh_rgsh,b.sh_start,b.sh_end,b.sh_look,b.xm_id,c.xm_sz_type from TS_XMGL_BMSH b left join TS_XMGL_SZ c ON b.xm_sz_id = c.xm_sz_id where c.xm_sz_type='已结束' and b.SH_RGSH=1 )d ON a.xm_id = d.xm_id WHERE d.sh_rgsh=1 AND NOW() BETWEEN STR_TO_DATE(d.sh_start,'%Y-%m-%d %H:%i:%s') AND STR_TO_DATE(d.sh_end,'%Y-%m-%d %H:%i:%s') "
				 +where1+" AND d.SH_LOOK =1 ORDER BY d.sh_end ASC)t1 limit 0,50";
		List<Bean> find4 = Transaction.getExecutor().query(sql4);
		
		String sql3 = " SELECT * FROM(SELECT a.*,b.sh_end FROM ts_xmgl a LEFT JOIN TS_XMGL_BMSH b ON a.xm_id = b.xm_id WHERE b.sh_rgsh=1 AND (NOW()< STR_TO_DATE(b.sh_start,'%Y-%m-%d %H:%i:%s') OR NOW()> STR_TO_DATE(b.sh_end,'%Y-%m-%d %H:%i:%s')) "
				+where1+" AND b.SH_LOOK =1 and b.SH_RGSH=1 ORDER BY b.sh_end ASC)t limit 0,50";
		List<Bean> finds3 = Transaction.getExecutor().query(sql3);
		String sql5 = "select a.* from ts_xmgl a left join ts_xmgl_bmsh b on a.xm_id=b.xm_id where b.SH_RGSH=2 and b.sh_look=1 " +where1 +"limit 0,50";
		List<Bean> find5 = Transaction.getExecutor().query(sql5);
		for (Bean bean : find5) {
			bean.set("SH_STATE_STR", "无手动审核");
			bean.set("SH_END", "");
		}
	
		finds.addAll(find2);
		finds.addAll(find4);
		finds.addAll(finds3);
		for (Bean bean : finds) {
			ParamBean param = new ParamBean();
			param.set("xmid", bean.getStr("XM_ID"));
			OutBean out = ServMgr.act("TS_XMGL_BMGL", "getSHState", param);//审核状态数据
			String state =out.getStr("state");
		/*	OutBean act = ServMgr.act("TS_XMGL_BMGL","getShowLook", param);
			int showlook = act.getInt("showlook");*/
			if("1".equals(zhuangtai)&&"待报名".equals(state)){
					bean.set("SH_STATE_STR", "进行中");
					list.add(bean);

			}else if("2".equals(zhuangtai)&&"已结束".equals(state)){
					bean.set("SH_STATE_STR", state);
					list.add(bean);
			}else if("全部".equals(zhuangtai)){
				if("待报名".equals(state)){
					state ="进行中";
				}
					bean.set("SH_STATE_STR", state);
					list.add(bean);
			}
		}
		if("全部".equals(zhuangtai)){
			list.addAll(find5);
		}
		int ALLNUM = list.size();
		// 计算页数
		int meiye = Integer.parseInt(SHOWNUM);
		int yeshu = ALLNUM / meiye;
		int yushu = ALLNUM % meiye;
		// 获取总页数
		if (yushu != 0) {
			yeshu += 1;
		}

		int nowpage = Integer.parseInt(NOWPAGE);
		int showpage = Integer.parseInt(SHOWNUM);
		// 计算第一项 开始
		int chushi = (nowpage - 1) * showpage + 1;
		// 计算结束项
		int jieshu = (nowpage - 1) * showpage + showpage;
		// 放到Array中
		List<Bean> list2 = new ArrayList<Bean>();
		if (ALLNUM == 0) {
			// 没有数据
		} else {

			if (jieshu <= ALLNUM) {
				// 循环将数据放入list2中返回给前台
				for (int i = chushi; i <= jieshu; i++) {
					list2.add(list.get(i - 1));
				}

			} else {
				for (int j = chushi; j < ALLNUM + 1; j++) {
					list2.add(list.get(j - 1));
				}
			}
		}
		_PAGE_.set("ALLNUM", ALLNUM);
		_PAGE_.set("NOWPAGE", NOWPAGE);
		_PAGE_.set("PAGES", yeshu);
		_PAGE_.set("SHOWNUM", SHOWNUM);
		outBean.set("list", list2);
		outBean.set("_PAGE_", _PAGE_);
		outBean.set("first", chushi);
		return outBean;
	}
	

	// 按钮发布的操作 传过来id

	public void UpdateStatusStart(ParamBean paramBean) {
		try {
			String dataId = paramBean.getStr("pkCodes");
			Bean xmBean = ServDao.find("TS_XMGL", dataId);
			if (0 == xmBean.getInt("XM_STATE")) {
				ServDao.save("TS_XMGL", xmBean.set("XM_STATE", 1));
			}
		} catch (Exception e) {
			throw new TipException("服务器异常，发布失败！");
		}
	}


	/**
	 * 批量改变 状态(取消发布)
	 * 
	 * 
	 * @param paramBean
	 * 
	 */
	public void UpdateStatusStop(Bean paramBean) {
		try {
			String dataId = paramBean.getStr("pkCodes");
			Bean xmBean = ServDao.find("TS_XMGL", dataId);
			if (1 == xmBean.getInt("XM_STATE")) {
				ServDao.save("TS_XMGL", xmBean.set("XM_STATE", 0));
			}
		} catch (Exception e) {
			throw new TipException("服务器异常，发布失败！");
		}
	}	
		
	
	
	// 查询前添加查询条件
	protected void beforeQuery(ParamBean paramBean) {
		ParamBean param = new ParamBean();
		String ctlgModuleName = "PROJECT";
		String serviceName = paramBean.getServId();
		param.set("paramBean", paramBean);
		param.set("ctlgModuleName", ctlgModuleName);
		param.set("serviceName", serviceName);
		PvlgUtils.setCtlgPvlgWhere(param);
	}
	/**
	 * 判断此人是否能进行报名审核
	 */
	public OutBean getMyShState(Bean paramBean){
		UserBean userBean = Context.getUserBean();
		String user_code = userBean.getCode();
		String sql = "select * from TS_WFS_BMSHLC where node_id in (select NODE_ID from TS_WFS_NODE_APPLY where wfs_id in(select WFS_ID from ts_xmgl a left join TS_XMGL_BMSH b on a.xm_id=b.xm_id))";
		List<Bean> list = Transaction.getExecutor().query(sql);
		for (Bean codebean : list) {
			if (user_code.equals(codebean.getStr("SHR_USERCODE"))) {
				//可进行审核
				return new OutBean().set("flag","true");
			}
		}
		return new OutBean().set("flag", "false");
	}

	
public Bean delXmAll(Bean paramBean)  {	
		String   delXmidAll=paramBean.getStr("xmpk");
		
		//删除前台
		//删除请假待办
		String  todoqj="delete  from  TS_COMM_TODO  WHERE  DATA_ID  IN (SELECT QJ_ID FROM  TS_QJLB_QJ WHERE XM_ID='"+delXmidAll+"')";
		Transaction.getExecutor().execute(todoqj);
		
		//删除请假已办
		String  tododoneqj="delete   from  TS_COMM_TODO_DONE  WHERE  DATA_ID  IN (SELECT QJ_ID FROM  TS_QJLB_QJ WHERE XM_ID='"+delXmidAll+"')";
		Transaction.getExecutor().execute(tododoneqj);
		//请假审核意见
		String  tododoneMindqj="delete   from  TS_COMM_MIND  WHERE  DATA_ID  IN (SELECT QJ_ID FROM  TS_QJLB_QJ WHERE XM_ID='"+delXmidAll+"')";
		Transaction.getExecutor().execute(tododoneMindqj);
		//删除借考待办
		String  todojk="delete   from  TS_COMM_TODO  WHERE  DATA_ID  IN (SELECT JK_ID FROM  TS_JKLB_JK WHERE XM_ID='"+delXmidAll+"')";
		Transaction.getExecutor().execute(todojk);
		//删除借考已办
		String  tododonejk="delete   from  TS_COMM_TODO_DONE  WHERE  DATA_ID  IN (SELECT JK_ID FROM  TS_JKLB_JK WHERE XM_ID='"+delXmidAll+"')";
		Transaction.getExecutor().execute(tododonejk);
		//删除借考意见
		String  tododoneMindjk="delete   from  TS_COMM_MIND  WHERE  DATA_ID  IN (SELECT JK_ID FROM  TS_JKLB_JK WHERE XM_ID='"+delXmidAll+"')";
		Transaction.getExecutor().execute(tododoneMindjk);
		
		//删除已通过报名
		String sqldel02="delete   from  TS_BMSH_PASS    where   XM_ID='"+delXmidAll+"'";
		Transaction.getExecutor().execute(sqldel02);
		//删除待审核人员
		String sqldel03="delete   from  TS_BMSH_STAY    where   XM_ID='"+delXmidAll+"'";
		Transaction.getExecutor().execute(sqldel03);
		//删除没通关过
		String sqldel04="delete   from  TS_BMSH_NOPASS    where   XM_ID='"+delXmidAll+"'";
		Transaction.getExecutor().execute(sqldel04);
		//删除请假
	    String sqldel05="delete   from  TS_QJLB_QJ    where   XM_ID='"+delXmidAll+"'";
		Transaction.getExecutor().execute(sqldel05);
		//删除借考
	    String sqldel06="delete   from  TS_JKLB_JK     where   XM_ID='"+delXmidAll+"'";
		Transaction.getExecutor().execute(sqldel06);
		//删除准考证
	    String sqldel07="delete   from  TS_XMGL_ADMISSION_FILE     where   XM_ID='"+delXmidAll+"'";
		Transaction.getExecutor().execute(sqldel07);
		//删除后台相关数据
		//报名管理删除机构和报名
		String  sqlDelUserDept="delete   from  TS_BM_GROUP_USER_DEPT   WHERE  G_ID  IN (SELECT G_ID FROM  TS_BM_GROUP WHERE XM_ID='"+delXmidAll+"')";
		Transaction.getExecutor().execute(sqlDelUserDept);
		 String sqlDelGroup="delete   from  TS_BM_GROUP     where   XM_ID='"+delXmidAll+"'";
		Transaction.getExecutor().execute(sqlDelGroup);
		//删除非资格考试
		String sqlDelFzgks="delete   from  TS_XMGL_BM_FZGKS     where   XM_ID='"+delXmidAll+"'";
		Transaction.getExecutor().execute(sqlDelFzgks);
		//禁考规则表
		String sqlDelJkglgz="delete   from  TS_XMGL_BM_JKGLGZ     where   XM_ID='"+delXmidAll+"'";
		Transaction.getExecutor().execute(sqlDelJkglgz);
		//删除考试类别设置	
		String sqlDelKslb="delete   from  TS_XMGL_BM_KSLB     where   XM_ID='"+delXmidAll+"'";
		Transaction.getExecutor().execute(sqlDelKslb);
		//删除考试类别组
	    String sqlDelKsqz="delete   from  TS_XMGL_BM_KSQZ     where   XM_ID='"+delXmidAll+"'";
		Transaction.getExecutor().execute(sqlDelKsqz);
		//删除报管理
		String sqlDelBmgl="delete   from  TS_XMGL_BMGL     where   XM_ID='"+delXmidAll+"'";
		Transaction.getExecutor().execute(sqlDelBmgl);
		//项目管理_场次测算_大时间段安排
		String sqlDelArr="delete   from  TS_XMGL_CCCS_ARRANGE     where   XM_ID='"+delXmidAll+"'";
		Transaction.getExecutor().execute(sqlDelArr);
		//项目管理-考场安排-已安排座位     
		String sqlDelYap="delete   from  TS_XMGL_KCAP_YAPZW     where   XM_ID='"+delXmidAll+"'";
		Transaction.getExecutor().execute(sqlDelYap);
		//场次安排提交记录     (表ts_xmgl_kcap_tjjl       字段 XM_ID
		String sqlDelTjjl="delete   from  TS_XMGL_KCAP_TJJL     where   XM_ID='"+delXmidAll+"'";
		Transaction.getExecutor().execute(sqlDelTjjl);
		//项目管理_考场安排_待安排场次_场次时间 
		String  sqlDelCcsj="delete   from  TS_XMGL_KCAP_DAPCC_CCSJ   WHERE  CC_ID  IN (SELECT CC_ID FROM  TS_XMGL_KCAP_DAPCC  WHERE XM_ID='"+delXmidAll+"')";
		Transaction.getExecutor().execute(sqlDelCcsj);
		//项目管理_考场安排_待安排场次_场次时间 
		String  sqlDelGljg="delete   from  TS_XMGL_KCAP_GLJG   WHERE  CC_ID  IN (SELECT CC_ID FROM  TS_XMGL_KCAP_DAPCC  WHERE XM_ID='"+delXmidAll+"')";
		Transaction.getExecutor().execute(sqlDelGljg);
		//项目管理_考场安排_待安排场次   
		String  sqlDelDap="delete  from  TS_XMGL_KCAP_DAPCC   WHERE   XM_ID='"+delXmidAll+"'";
		Transaction.getExecutor().execute(sqlDelDap);
		
		//报名审核规则明细
		String  sqlDelMx="delete   from  TS_XMGL_BMSH_SHGZ_MX   WHERE   XM_ID='"+delXmidAll+"'";
		Transaction.getExecutor().execute(sqlDelMx);
		
		//场次测算
		delFieldCal(delXmidAll);
		Bean  bean=new  Bean();
	 	return bean ;
	}	
	
//考场测算的删除
public  void delFieldCal(String   delXmidAll)  {	
	//变跟申请记录 TS_KCGL_UPDATE 
    String   delsqlupdate="DELETE     FROM  TS_KCGL_UPDATE    WHERE   KC_ID  IN "
                  +"(SELECT  A.KC_ID  FROM  TS_KCGL  A , TS_KCZGL_GROUP B,TS_KCZGL C   "
		          +"WHERE A.GROUP_ID = B.GROUP_ID   "
		          + "AND  B.KCZ_ID = C.KCZ_ID   "
		          + "AND C.XM_ID ='"+delXmidAll+"')";
    Transaction.getExecutor().execute(delsqlupdate);
   //考场ip段 TS_KCGL_IPSCOPE
    String   delsqlipScope="DELETE     FROM  TS_KCGL_IPSCOPE    WHERE   KC_ID  IN "
                      +"(SELECT  A.KC_ID  FROM  TS_KCGL  A , TS_KCZGL_GROUP B,TS_KCZGL C   "
    		          +"WHERE A.GROUP_ID = B.GROUP_ID   "
    		          + "AND  B.KCZ_ID = C.KCZ_ID   "
    		          + "AND C.XM_ID ='"+delXmidAll+"')";
   Transaction.getExecutor().execute(delsqlipScope); 
  //考场ip段 TS_KCGL_IPZWH
    String   delsqlIPzwh="DELETE     FROM  TS_KCGL_IPZWH   WHERE   KC_ID  IN "
                          +"(SELECT  A.KC_ID  FROM  TS_KCGL  A , TS_KCZGL_GROUP B,TS_KCZGL C   "
        		          +"WHERE A.GROUP_ID = B.GROUP_ID   "
        		          + "AND  B.KCZ_ID = C.KCZ_ID   "
        		          + "AND C.XM_ID ='"+delXmidAll+"')";
    Transaction.getExecutor().execute(delsqlIPzwh); 
    //关联机构  （表TS_KCGL_GLJG         
    String   delsqlGljg="DELETE     FROM  TS_KCGL_GLJG    WHERE   KC_ID  IN "
              +"(SELECT  A.KC_ID  FROM  TS_KCGL  A , TS_KCZGL_GROUP B,TS_KCZGL C   "
	          +"WHERE A.GROUP_ID = B.GROUP_ID   "
	          + "AND  B.KCZ_ID = C.KCZ_ID   "
	          + "AND C.XM_ID ='"+delXmidAll+"')";
    Transaction.getExecutor().execute(delsqlGljg);   
  //考场管理员  （表TS_KCGL_GLY         
    String   delsqlGly="DELETE     FROM  TS_KCGL_GLY    WHERE   KC_ID  IN "
              +"(SELECT  A.KC_ID  FROM  TS_KCGL  A , TS_KCZGL_GROUP B,TS_KCZGL C   "
	          +"WHERE A.GROUP_ID = B.GROUP_ID   "
	          + "AND  B.KCZ_ID = C.KCZ_ID   "
	          + "AND C.XM_ID ='"+delXmidAll+"')";
    Transaction.getExecutor().execute(delsqlGly); 
    //系统座位号对应    TS_KCGL_ZWDYB 
    String   delsqlZwdyb="DELETE     FROM  TS_KCGL_ZWDYB    WHERE   KC_ID  IN "
            +"(SELECT  A.KC_ID  FROM  TS_KCGL  A , TS_KCZGL_GROUP B,TS_KCZGL C   "
	          +"WHERE A.GROUP_ID = B.GROUP_ID   "
	          + "AND  B.KCZ_ID = C.KCZ_ID   "
	          + "AND C.XM_ID ='"+delXmidAll+"')";
  Transaction.getExecutor().execute(delsqlZwdyb);
  //监控地址  （表TS_KCGL_JKIP  
  String   delsqljkip="DELETE     FROM  TS_KCGL_JKIP    WHERE   KC_ID  IN "
          +"(SELECT  A.KC_ID  FROM  TS_KCGL  A , TS_KCZGL_GROUP B,TS_KCZGL C   "
	          +"WHERE A.GROUP_ID = B.GROUP_ID   "
	          + "AND  B.KCZ_ID = C.KCZ_ID   "
	          + "AND C.XM_ID ='"+delXmidAll+"')";
   Transaction.getExecutor().execute(delsqljkip);
   //考场管理  （表TS_KCGL
       String   delsqlkcgl=" DELETE      FROM    TS_KCGL      WHERE   GROUP_ID   IN"
		   +"(SELECT  B.GROUP_ID   FROM  TS_KCZGL_GROUP B,TS_KCZGL C  WHERE B.KCZ_ID = C.KCZ_ID  "
		   + "AND C.XM_ID ='"+delXmidAll+"')";
	   Transaction.getExecutor().execute(delsqlkcgl);
	   //考场组  （表TS_KCZGL_GROUP  
       String   delsqlGroup=" DELETE     FROM   TS_KCZGL_GROUP    WHERE  KCZ_ID  IN (   SELECT   KCZ_ID   FROM    TS_KCZGL   WHERE    XM_ID='"+delXmidAll+"')";
	   Transaction.getExecutor().execute(delsqlGroup);
    //项目管理_场次测算_考场组管理 （表TS_KCZGL 
	   String   delsqlkczgl=" DELETE     FROM   TS_KCZGL    WHERE     XM_ID='"+delXmidAll+"'";
	   Transaction.getExecutor().execute(delsqlkczgl);
	 //删除报名列表
	 		String sqldel01="delete   from  TS_BMLB_BM    where   XM_ID='"+delXmidAll+"'";
	 		Transaction.getExecutor().execute(sqldel01);
  
}











//	public OutBean countNum(Bean paramBean) {
//	OutBean outBean = new OutBean();
//	String  parampk=paramBean.getStr("pks");//项目id",,";
//	String[] pksArray=parampk.split(",");
//	String Str="";
//	for(int  i=0;i<pksArray.length;i++){
//		 Str+="'"+pksArray[i]+"',";
//	}
//	Str = Str.substring(0,Str.length()-1);
//	String sql1="SELECT *  FROM (SELECT xm_id FROM TS_XMGL  WHERE XM_ID IN "
//	        +" ( "+Str+ "))"
//	        +"  a   LEFT JOIN ts_xmgl_BMSH b "
//	        +"  ON  a.xm_id = b.xm_id   "
//	        +"  WHERE  b.SH_RGSH=0   "
//	        +"  AND    b.SH_ZDSH=0  ";
//	 int count1 = Transaction.getExecutor().count(sql1);
//	//String sql2="SELECT COUNT(*)  FROM (SELECT xm_id FROM TS_XMGL  WHERE XM_ID IN "
//	 String sql2="SELECT    *  FROM (SELECT xm_id FROM TS_XMGL  WHERE XM_ID IN "
//			+ "("+Str+ ")) a"
//	        +"  LEFT JOIN ts_xmgl_BMGL b "
//	        +"   ON a.xm_id = b.xm_id  ";
//	int count2=Transaction.getExecutor().count(sql2);
//	outBean.set("sql1", count1);
//	outBean.set("sql2", count2);
//	
//	return  outBean;
//}	
public OutBean getXmType(ParamBean paramBean){
	String xmid = paramBean.getStr("xmid");
	Bean find = ServDao.find("TS_XMGL", xmid);
	String xm_type = "";
	if(find!=null){
		xm_type=find.getStr("XM_TYPE");
	}
	return new OutBean().set("xm_type", xm_type);
}
private static void sort(List<Bean> data) {
    Collections.sort(data, new Comparator<Bean>() {

      /*  public int compare(Map o1, Map o2) {

            Integer a = (Integer) o1.get("PRECOUNTOUT");
            Integer b = (Integer) o2.get("PRECOUNTOUT");

            // 升序
            return a.compareTo(b);

            // 降序
            // return b.compareTo(a);
        }*/

		@Override
		public int compare(Bean arg0, Bean arg1) {
			 Integer a =  arg0.getInt("SH_END");
	         Integer b =  arg1.getInt("SH_END");
			return a.compareTo(b);
		}
    });
}
}