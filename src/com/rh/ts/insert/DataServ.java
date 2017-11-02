package com.rh.ts.insert;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.ServDao;

public class DataServ extends CommonServ {

	
	/**
	 * 插数据
	 */
	public void insertinto(Bean paramBean){
		String xmid = paramBean.getStr("XMID");
		String xmname = paramBean.getStr("XMNAME");
		List<Bean> bmgllist = ServDao.finds("TS_XMGL_BMGL", "AND XM_ID = '"+xmid+"'");
		String start = bmgllist.get(0).getStr("BM_START");
		String end = bmgllist.get(0).getStr("BM_END");
		//项目ID下的  包含的考试类别  
		//高级考试
		List<Bean> kslbklist1 = ServDao.finds("TS_XMGL_BM_KSLB"," AND KSLB_TYPE = '3' AND XM_ID='"+xmid+"'");
		//中级考试
		List<Bean> kslbklist2 = ServDao.finds("TS_XMGL_BM_KSLB"," AND  (KSLB_TYPE = '2' or KSLB_TYPE=1) AND XM_ID='"+xmid+"'");
		
				List<Bean> KSLBLIST = ServDao.finds("TS_XMGL_BM_KSLB", "AND XM_ID='"+xmid+"'");
				String lb_code ="";
				String xl_code = "";
				if(KSLBLIST!=null&&KSLBLIST.size()!=0){
					for (Bean bean : KSLBLIST) {
						 lb_code += bean.getStr("KSLB_CODE")+",";
						 xl_code += bean.getStr("KSLB_XL_CODE")+",";
					}
				}
				if(lb_code!=""){
					lb_code = lb_code.substring(0,lb_code.length()-1);
					xl_code = xl_code.substring(0,xl_code.length()-1);
				}
				
		int i=0;
		int j=0;
		int a = 0;
			List<Bean> finds2 = ServDao.finds("SY_ORG_USER", " AND CODE_PATH LIKE '%0130100000%' group by user_code limit 0,6000" );
			if(finds2!=null&&finds2.size()!=0){
				
				//向报名列表中插数据
				for (Bean userBean : finds2) {
					
					String STATION_TYPE_CODE=userBean.getStr("STATION_TYPE_CODE");
					String STATION_NO=userBean.getStr("STATION_NO_CODE");
					String DUTY_LEVEL_CODE=userBean.getStr("DUTY_LV_CODE");
					String where = "AND POSTION_TYPE="+"'"+STATION_TYPE_CODE+"'"+" AND POSTION_NAME_CODE="+"'"+DUTY_LEVEL_CODE+"'"+" AND POSTION_SEQUENCE_ID='"+STATION_NO+"'";
					List<Bean> POSTIONlist = ServDao.finds("TS_ORG_POSTION",where);
					int cengji = 0;
					if(POSTIONlist!=null&&POSTIONlist.size()!=0){
						cengji=Integer.parseInt(POSTIONlist.get(0).getStr("POSTION_QUALIFICATION"))+1;
					}
					String wherestr = " AND  (KSLB_TYPE = '2' or KSLB_TYPE=1) ";
					if(cengji>=3){
						i++;
						if(i>kslbklist1.size()-3){
							i=0;
						}
						wherestr=" AND KSLB_TYPE = '3' ";
						a = i;
					}else{
						j++;
						if(j>kslbklist2.size()-3){
							j=0;
						}
						a=j;
					}
					List<Bean> kslbklist = ServDao.finds("TS_XMGL_BM_KSLB", wherestr+" AND XM_ID='"+xmid+"' limit "+a+","+2);
					if(cengji>=3&&kslbklist.size()==0){
						wherestr = " AND  (KSLB_TYPE = '2' or KSLB_TYPE=1) ";
						if(a>kslbklist2.size()-3){
							a=0;
						}
						kslbklist = ServDao.finds("TS_XMGL_BM_KSLB", wherestr+" AND XM_ID='"+xmid+"' limit "+a+","+2);
					}
					
					for (Bean kslbkbean : kslbklist) {
						Bean beans = new Bean();
						
						String ks_time = kslbkbean.getStr("KSLBK_TIME");
						beans.set("BM_YIYI_STATE", 0);
						beans.set("RZ_YEAR", "");
						beans.set("BM_CODE", userBean.getId());
						beans.set("BM_NAME", userBean.getStr("USER_NAME"));
						beans.set("BM_SEX", userBean.getStr("USER_SEX"));
						beans.set("ODEPT_NAME", userBean.getStr("ODEPT_NAME"));
						beans.set("BM_OFFICE_PHONE", userBean.getStr("USER_OFFICE_PHONE"));
						beans.set("BM_PHONE", userBean.getStr("USER_MOBILE"));
						beans.set("BM_ATIME", userBean.getStr("USER_CMPY_DATE"));
						beans.set("BM_LB", kslbkbean.getStr("KSLB_NAME"));
						beans.set("BM_XL", kslbkbean.getStr("KSLB_XL"));
						beans.set("BM_MK", kslbkbean.getStr("KSLB_MK"));
						beans.set("BM_TYPE", kslbkbean.getStr("KSLB_TYPE"));
						beans.set("BM_LB_CODE", kslbkbean.getStr("KSLB_CODE"));
						beans.set("BM_XL_CODE", kslbkbean.getStr("KSLB_XL_CODE"));
						beans.set("BM_MK_CODE", kslbkbean.getStr("KSLB_MK_CODE"));
						beans.set("BM_TYPE_NAME", kslbkbean.getStr("KSLB_TYPE_NAME"));
						beans.set("XM_ID", xmid);
						beans.set("BM_TITLE", xmname);
						beans.set("BM_STARTDATE", start);
						beans.set("BM_ENDDATE", end);
						beans.set("KSLBK_ID", kslbkbean.getId());
						beans.set("S_ODEPT",userBean.getStr("ODEPT_CODE"));
						beans.set("S_DEPT", userBean.getStr("DEPT_CODE"));
						beans.set("S_TDEPT", userBean.getStr("TDEPT_CODE"));
						beans.set("BM_SH_STATE", 1);
						Bean create = ServDao.create("TS_BMLB_BM", beans);
						
						
						Bean shBean = new Bean();
						shBean.set("RZ_YEAR", "");
						shBean.set("XM_ID", xmid);
						shBean.set("ODEPT_CODE", userBean.getStr("ODEPT_CODE"));
						shBean.set("BM_ID", create.getId());
						shBean.set("BM_NAME", userBean.getStr("USER_NAME"));
						shBean.set("BM_CODE", userBean.getStr("USER_CODE"));
						shBean.set("KSLBK_ID", kslbkbean.getId());
						shBean.set("BM_LB", kslbkbean.getStr("KSLB_NAME"));
						shBean.set("BM_XL", kslbkbean.getStr("KSLB_XL"));
						shBean.set("BM_MK", kslbkbean.getStr("KSLB_MK"));
						shBean.set("BM_TYPE", kslbkbean.getStr("KSLB_TYPE"));
						shBean.set("BM_LB_CODE", kslbkbean.getStr("KSLB_CODE"));
						shBean.set("BM_XL_CODE", kslbkbean.getStr("KSLB_XL_CODE"));
						shBean.set("BM_MK_CODE", kslbkbean.getStr("KSLB_MK_CODE"));
						shBean.set("BM_TYPE_NAME", kslbkbean.getStr("KSLB_TYPE_NAME"));
						/*shBean.set("SH_NODE", "一级审核级别");// 目前审核节点
						  shBean.set("SH_USER", "0000001111");// 当前办理人
						  shBean.set("SH_OTHER", "0000001111");// 其他办理人*/	
						shBean.set("S_ODEPT",userBean.getStr("ODEPT_CODE"));
						shBean.set("S_TDEPT",userBean.getStr("TDEPT_CODE"));
						shBean.set("S_DEPT",userBean.getStr("DEPT_CODE"));
						shBean.set("BM_KS_TIME", ks_time);
						shBean.set("BM_STATUS", 0);
						ServDao.save("TS_BMSH_PASS", shBean);
						
						Bean mindbean = new Bean();
						mindbean.set("SH_LEVEL", 0);
						mindbean.set("SH_MIND", "");
						mindbean.set("DATA_ID",create.getId());
						mindbean.set("SH_STATUS", "审核通过");
						mindbean.set("SH_ULOGIN", "自动审核");
						mindbean.set("SH_UNAME", "自动审核");
						mindbean.set("SH_UCODE", "");
						mindbean.set("SH_TYPE", 1);
						mindbean.set("SH_NODE", 0);
						mindbean.set("S_ODEPT",userBean.getStr("ODEPT_CODE"));
						mindbean.set("S_DNAME",userBean.getStr("DEPT_NAME"));
						mindbean.set("S_DEPT",userBean.getStr("DEPT_CODE"));
						ServDao.save("TS_COMM_MIND", mindbean);
					}
			}
		}
			
			
			List<Bean> finds3 = ServDao.finds("SY_ORG_USER", " AND CODE_PATH LIKE '%0020000000%' group by user_code  limit 0,6000" );
			if(finds3!=null&&finds3.size()!=0){
				
				//向报名列表中插数据
				for (Bean userBean : finds3) {
					
					String STATION_TYPE_CODE=userBean.getStr("STATION_TYPE_CODE");
					String STATION_NO=userBean.getStr("STATION_NO_CODE");
					String DUTY_LEVEL_CODE=userBean.getStr("DUTY_LV_CODE");
					String where = "AND POSTION_TYPE="+"'"+STATION_TYPE_CODE+"'"+" AND POSTION_NAME_CODE="+"'"+DUTY_LEVEL_CODE+"'"+" AND POSTION_SEQUENCE_ID='"+STATION_NO+"'";
					List<Bean> POSTIONlist = ServDao.finds("TS_ORG_POSTION",where);
					int cengji = 0;
					if(POSTIONlist!=null&&POSTIONlist.size()!=0){
						cengji=Integer.parseInt(POSTIONlist.get(0).getStr("POSTION_QUALIFICATION"))+1;
					}
					String wherestr = " AND  (KSLB_TYPE = '2' or KSLB_TYPE=1) ";
					if(cengji>=3){
						i++;
						if(i>kslbklist1.size()-3){
							i=0;
						}
						wherestr=" AND KSLB_TYPE = '3' ";
						a = i;
					}else{
						j++;
						if(j>kslbklist2.size()-3){
							j=0;
						}
						a=j;
					}
					List<Bean> kslbklist = ServDao.finds("TS_XMGL_BM_KSLB", wherestr+" AND XM_ID='"+xmid+"' limit "+a+","+2);
					if(cengji>=3&&kslbklist.size()==0){
						wherestr = " AND  (KSLB_TYPE = '2' or KSLB_TYPE=1) ";
						if(a>kslbklist2.size()-3){
							a=0;
						}
						kslbklist = ServDao.finds("TS_XMGL_BM_KSLB", wherestr+" AND XM_ID='"+xmid+"' limit "+a+","+2);
					}
					
					for (Bean kslbkbean : kslbklist) {
						Bean beans = new Bean();
						
						String ks_time = kslbkbean.getStr("KSLBK_TIME");
						beans.set("BM_YIYI_STATE", 0);
						beans.set("RZ_YEAR", "");
						beans.set("BM_CODE", userBean.getId());
						beans.set("BM_NAME", userBean.getStr("USER_NAME"));
						beans.set("BM_SEX", userBean.getStr("USER_SEX"));
						beans.set("ODEPT_NAME", userBean.getStr("ODEPT_NAME"));
						beans.set("BM_OFFICE_PHONE", userBean.getStr("USER_OFFICE_PHONE"));
						beans.set("BM_PHONE", userBean.getStr("USER_MOBILE"));
						beans.set("BM_ATIME", userBean.getStr("USER_CMPY_DATE"));
						beans.set("BM_LB", kslbkbean.getStr("KSLB_NAME"));
						beans.set("BM_XL", kslbkbean.getStr("KSLB_XL"));
						beans.set("BM_MK", kslbkbean.getStr("KSLB_MK"));
						beans.set("BM_TYPE", kslbkbean.getStr("KSLB_TYPE"));
						beans.set("BM_LB_CODE", kslbkbean.getStr("KSLB_CODE"));
						beans.set("BM_XL_CODE", kslbkbean.getStr("KSLB_XL_CODE"));
						beans.set("BM_MK_CODE", kslbkbean.getStr("KSLB_MK_CODE"));
						beans.set("BM_TYPE_NAME", kslbkbean.getStr("KSLB_TYPE_NAME"));
						beans.set("XM_ID", xmid);
						beans.set("BM_TITLE", xmname);
						beans.set("BM_STARTDATE", start);
						beans.set("BM_ENDDATE", end);
						beans.set("KSLBK_ID", kslbkbean.getId());
						beans.set("S_ODEPT",userBean.getStr("ODEPT_CODE"));
						beans.set("S_DEPT", userBean.getStr("DEPT_CODE"));
						beans.set("S_TDEPT", userBean.getStr("TDEPT_CODE"));
						beans.set("BM_SH_STATE", 1);
						Bean create = ServDao.create("TS_BMLB_BM", beans);
						
						
						Bean shBean = new Bean();
						shBean.set("RZ_YEAR", "");
						shBean.set("XM_ID", xmid);
						shBean.set("ODEPT_CODE", userBean.getStr("ODEPT_CODE"));
						shBean.set("BM_ID", create.getId());
						shBean.set("BM_NAME", userBean.getStr("USER_NAME"));
						shBean.set("BM_CODE", userBean.getStr("USER_CODE"));
						shBean.set("KSLBK_ID", kslbkbean.getId());
						shBean.set("BM_LB", kslbkbean.getStr("KSLB_NAME"));
						shBean.set("BM_XL", kslbkbean.getStr("KSLB_XL"));
						shBean.set("BM_MK", kslbkbean.getStr("KSLB_MK"));
						shBean.set("BM_TYPE", kslbkbean.getStr("KSLB_TYPE"));
						shBean.set("BM_LB_CODE", kslbkbean.getStr("KSLB_CODE"));
						shBean.set("BM_XL_CODE", kslbkbean.getStr("KSLB_XL_CODE"));
						shBean.set("BM_MK_CODE", kslbkbean.getStr("KSLB_MK_CODE"));
						shBean.set("BM_TYPE_NAME", kslbkbean.getStr("KSLB_TYPE_NAME"));
						/*shBean.set("SH_NODE", "一级审核级别");// 目前审核节点
						  shBean.set("SH_USER", "0000001111");// 当前办理人
						  shBean.set("SH_OTHER", "0000001111");// 其他办理人*/	
						shBean.set("S_ODEPT",userBean.getStr("ODEPT_CODE"));
						shBean.set("S_TDEPT",userBean.getStr("TDEPT_CODE"));
						shBean.set("S_DEPT",userBean.getStr("DEPT_CODE"));
						shBean.set("BM_KS_TIME", ks_time);
						shBean.set("BM_STATUS", 0);
						ServDao.save("TS_BMSH_PASS", shBean);
						
						Bean mindbean = new Bean();
						mindbean.set("SH_LEVEL", 0);
						mindbean.set("SH_MIND", "");
						mindbean.set("DATA_ID",create.getId());
						mindbean.set("SH_STATUS", "审核通过");
						mindbean.set("SH_ULOGIN", "自动审核");
						mindbean.set("SH_UNAME", "自动审核");
						mindbean.set("SH_UCODE", "");
						mindbean.set("SH_TYPE", 1);
						mindbean.set("SH_NODE", 0);
						mindbean.set("S_ODEPT",userBean.getStr("ODEPT_CODE"));
						mindbean.set("S_DNAME",userBean.getStr("DEPT_NAME"));
						mindbean.set("S_DEPT",userBean.getStr("DEPT_CODE"));
						ServDao.save("TS_COMM_MIND", mindbean);
					}
			}
		}
	}
}
