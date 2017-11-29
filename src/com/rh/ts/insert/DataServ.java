package com.rh.ts.insert;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;

public class DataServ extends CommonServ {

	
	/**
	 * 插数据
	 */
	public OutBean insertinto(Bean paramBean){
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
		String sql = "SELECT DISTINCT DEPT_CODE FROM SY_ORG_DEPT WHERE DEPT_LEVEL = '2'";
		List<Bean> odeptlist = Transaction.getExecutor().query(sql);
		for (Bean ODEPTBEAN : odeptlist) {
			Transaction.begin();
			String deptcode = ODEPTBEAN.getStr("DEPT_CODE");
		
		List<Bean> DEPTLIST = ServDao.finds("SY_ORG_DEPT", "AND CODE_PATH LIKE CONCAT('%','"+deptcode+"','%') and DEPT_LEVEL =3");
		for (Bean bean : DEPTLIST) {
			String odept_code = bean.getStr("DEPT_CODE");
			List<Bean> finds2 = ServDao.finds("SY_ORG_USER", " AND ODEPT_CODE =  '"+bean.getStr("DEPT_CODE")+"' limit 0,300" );
			if(finds2!=null&&finds2.size()!=0){
				
				//向报名列表中插数据
				for (Bean userBean : finds2) {
					String user_code = userBean.getStr("USER_CODE");
					String dept_code = userBean.getStr("dept_code");
					ParamBean param = new ParamBean();
					param.set("examerUserCode", user_code);
					param.set("level", 0);
					param.set("deptCode", dept_code);
					param.set("odeptCode", odept_code);
					param.set("xmId", xmid);
					param.set("flowName", 1);
					param.set("shrUserCode", user_code);
					OutBean out = ServMgr
							.act("TS_WFS_APPLY", "backFlow", param);
					String blist = out.getStr("result");
					String allman = "";
					String node_name = "";
					if (!"".equals(blist)) {
						allman = blist.substring(0, blist.length() - 1);
						node_name = out.getStr("NODE_NAME");
					}
					
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
						
						String ks_time = kslbkbean.getStr("KSLB_TIME");
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
						beans.set("BM_SH_STATE", 0);
						Bean create = ServDao.create("TS_BMLB_BM", beans);
						
						
						/*Bean shBean = new Bean();
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
						shBean.set("SH_NODE", "一级审核级别");// 目前审核节点
						  shBean.set("SH_USER", "0000001111");// 当前办理人
						  shBean.set("SH_OTHER", "0000001111");// 其他办理人	
						shBean.set("S_ODEPT",userBean.getStr("ODEPT_CODE"));
						shBean.set("S_TDEPT",userBean.getStr("TDEPT_CODE"));
						shBean.set("S_DEPT",userBean.getStr("DEPT_CODE"));
						shBean.set("BM_KS_TIME", ks_time);
						shBean.set("BM_STATUS", 0);
						ServDao.save("TS_BMSH_PASS", shBean);
						*/
						/*Bean mindbean = new Bean();
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
						ServDao.save("TS_COMM_MIND", mindbean);*/
						//待审核数据
						
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
						shBean.set("SH_NODE", node_name);// 目前审核节点
						shBean.set("SH_USER", "");// 当前办理人
						shBean.set("SH_OTHER", allman);// 其他办理人
						shBean.set("S_ODEPT",userBean.getStr("ODEPT_CODE"));
						shBean.set("S_TDEPT",userBean.getStr("TDEPT_CODE"));
						shBean.set("S_DEPT",userBean.getStr("DEPT_CODE"));
						shBean.set("BM_KS_TIME", ks_time);
						shBean.set("BM_STATUS", 0);
						ServDao.save("TS_BMSH_STAY", shBean);
						
					}
				}
			}
			
		}
			
		/*List<Bean> DEPTLIST1 = ServDao.finds("SY_ORG_DEPT", "AND CODE_PATH LIKE '%0020000000%' AND DEPT_LEVEL =3");
		for (Bean bean : DEPTLIST1) {
			String odept_code = bean.getStr("dept_codee");
			List<Bean> finds3 = ServDao.finds("SY_ORG_USER", " AND ODEPT_CODE =  '"+bean.getStr("DEPT_CODE")+"' limit 0,15000" );
			if(finds3!=null&&finds3.size()!=0){
				
				//向报名列表中插数据
				for (Bean userBean : finds3) {
					String user_code = userBean.getStr("USER_CODE");
					String dept_code = userBean.getStr("dept_code");
					ParamBean param = new ParamBean();
					param.set("examerUserCode", user_code);
					param.set("level", 0);
					param.set("deptCode", dept_code);
					param.set("odeptCode", odept_code);
					param.set("xmId", xmid);
					param.set("flowName", 1);
					param.set("shrUserCode", user_code);
					OutBean out = ServMgr
							.act("TS_WFS_APPLY", "backFlow", param);
					String blist = out.getStr("result");
					String allman = "";
					String node_name = "";
					int SH_LEVEL = 0;
					if (!"".equals(blist)) {
						allman = blist.substring(0, blist.length() - 1);
						node_name = out.getStr("NODE_NAME");
						SH_LEVEL = out.getInt("SH_LEVEL");
					}
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
					List<Bean> kslbklist = ServDao.finds("TS_XMGL_BM_KSLB", wherestr+" AND XM_ID='"+xmid+"'  group by KSLB_XL_CODE limit "+a+","+2);
					if(cengji>=3&&kslbklist.size()==0){
						wherestr = " AND  (KSLB_TYPE = '2' or KSLB_TYPE=1) ";
						if(a>kslbklist2.size()-3){
							a=0;
						}
						kslbklist = ServDao.finds("TS_XMGL_BM_KSLB", wherestr+" AND XM_ID='"+xmid+"'  group by KSLB_XL_CODE limit "+a+","+2);
					}
					
					for (Bean kslbkbean : kslbklist) {
						Bean beans = new Bean();
						
						String ks_time = kslbkbean.getStr("KSLB_TIME");
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
						shBean.set("SH_NODE", "一级审核级别");// 目前审核节点
						  shBean.set("SH_USER", "0000001111");// 当前办理人
						  shBean.set("SH_OTHER", "0000001111");// 其他办理人	
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
						shBean.set("SH_NODE", node_name);// 目前审核节点
						shBean.set("SH_USER", "");// 当前办理人
						shBean.set("SH_OTHER", allman);// 其他办理人
						shBean.set("S_ODEPT",userBean.getStr("ODEPT_CODE"));
						shBean.set("S_TDEPT",userBean.getStr("TDEPT_CODE"));
						shBean.set("S_DEPT",userBean.getStr("DEPT_CODE"));
						shBean.set("BM_KS_TIME", ks_time);
						shBean.set("BM_STATUS", 0);
						ServDao.save("TS_BMSH_STAY", shBean);
					}
			}
		}
		}*/
		Transaction.commit();
		Transaction.end();
		}
			return new OutBean().setOk();
	}
	/**
	 * 插入考场  机构 系统座位号
	 */
	public OutBean insertkc(Bean paramBean){
		String dept_code = paramBean.getStr("DEPT_CODE");
		Bean fhbean = ServDao.find("SY_ORG_DEPT", dept_code);
		List<Bean> odeptlist = ServDao.finds("SY_ORG_DEPT", "AND CODE_PATH LIKE '%"+dept_code+"%' AND DEPT_LEVEL='3' GROUP BY ODEPT_CODE ");
		
		if(odeptlist!=null&&odeptlist.size()!=0){
			int ipi =1;
			int bianhao = 1;
			for (Bean bean2 : odeptlist) {
				String ip = "172."+ ipi;
				
				//添加考场
				int max = (int) (Math.random()*70)+30;
				int good = (int) (max*0.8);
				//本部
				int indexOf = bean2.getStr("DEPT_NAME").indexOf("本部");
				if(indexOf>-1){
					
					for(int i=0;i<4;i++){
						Bean bean = new Bean();
						int j=i+1;
						//考场编号
						String kc_code = "AH10000"+bianhao;
						String kcname = bean2.getStr("DEPT_NAME");
						String KCNAME = kcname.substring(0, 2)+"一级考场";
						String address = kcname.substring(0, 2)+"市区";
						//一级
						bean.set("KC_NAME", KCNAME+j);
						bean.set("KC_ADDRESS", address);
						bean.set("KC_ODEPTNAME", fhbean.getStr("DEPT_NAME"));
						bean.set("KC_ODEPTCODE", fhbean.getStr("DEPT_CODE"));
						bean.set("KC_LEVEL", "一级");
						bean.set("KC_CREATOR", "系统管理员");
						bean.set("KC_MAX", max);
						bean.set("KC_GOOD", good);
						bean.set("KC_USE_NUM", "1");
						bean.set("SERV_ID","TS_KCGL");
						bean.set("KC_STATE",5);
						bean.set("CTLG_PCODE",fhbean.getStr("DEPT_CODE"));
						bean.set("KC_CODE", kc_code);
					Bean kcbean = ServDao.create("TS_KCGL", bean);
						String kcid =kcbean.getId();
						//本部下关联机构
						if(odeptlist.size()!=0){
							//管联机构
							int a=1;
							for (Bean bean3 : odeptlist) {
								Bean glbean = new Bean();
									//机构
									glbean.set("JG_TYPE", "2");
								glbean.set("KC_ID", kcid);
								glbean.set("JG_NAME", bean3.getStr("DEPT_NAME"));
								glbean.set("JG_CODE", bean3.getStr("DEPT_CODE"));
								glbean.set("JG_FAR", a);
								ServDao.save("TS_KCGL_GLJG", glbean);
								a++;
							}
							
						}
						
						//系统座位号
						int hangshu = max/10+1; //行数
						int xthao = 1;
						String first  = "";
						String last  = "";
						for(int z=1;z<=hangshu;z++){
							if(xthao>max){
								break;
							}
							for(int y=1;y<10;y++){
								if(xthao>max){
									break;
								}
								Bean zwbean  = new Bean();
								zwbean.set("KC_ID", kcid);
								zwbean.set("ZW_ZWH_XT", z+"-"+y);//系统座位号
								zwbean.set("ZW_ZWH_SJ", xthao);//实际座位号
								zwbean.set("ZW_KY", 1);//可用
								ServDao.save("TS_KCGL_ZWDYB", zwbean);
								
								Bean ipbean = new Bean();
								ipbean.set("IPZ_IP", ip+"."+j+"."+xthao);
								ipbean.set("IPZ_ZWH", z+"-"+y);
								if(xthao==1){
									first=ip+"."+j+"."+xthao;
								}
								ipbean.set("KC_ID", kcid);
								ServDao.save("TS_KCGL_ipzwh", ipbean);
								xthao++;
							}
						}
						last=ip+"."+j+"."+max;
						Bean ipscopebean = new  Bean();
						ipscopebean.set("IPS_TITLE", KCNAME+j+"区段");
						ipscopebean.set("KC_ID", kcid);
						ipscopebean.set("IPS_SCOPE",first+"——"+last);
						ServDao.save("ts_kcgl_ipscope", ipscopebean);
						bianhao++;
					}
					ipi++;
				}else{
					String kc_code = "AH10000"+bianhao;
					//二级
					String dept_name = bean2.getStr("DEPT_NAME");
					String KCNAME = dept_name.substring(0, dept_name.length()-2)+"二级考场";
					 dept_name = bean2.getStr("DEPT_NAME");
					dept_name = dept_name.substring(0, dept_name.length()-2)+"区";
						Bean bean = new Bean();
						bean.set("KC_NAME", KCNAME);
						bean.set("KC_ADDRESS", dept_name);
						bean.set("KC_ODEPTNAME", bean2.getStr("DEPT_NAME"));
						bean.set("KC_ODEPTNAME", bean2.getStr("DEPT_NAME"));
						bean.set("KC_ODEPTCODE", bean2.getStr("DEPT_CODE"));
						bean.set("KC_LEVEL", "二级");
						bean.set("KC_CREATOR", "系统管理员");
						bean.set("KC_MAX", max);
						bean.set("KC_GOOD", good);
						bean.set("KC_USE_NUM", "1");
						bean.set("SERV_ID","TS_KCGL");
						bean.set("KC_STATE",5);
						bean.set("CTLG_PCODE",bean2.getStr("DEPT_CODE"));
						bean.set("KC_CODE", kc_code);
						Bean kcbean = ServDao.create("TS_KCGL", bean);
						String kcid =kcbean.getId();
						//分行下
						
							//管联机构
								Bean glbean = new Bean();
								//判断机构 或是部门
									//机构
								glbean.set("JG_TYPE", "2");
								glbean.set("KC_ID", kcid);
								glbean.set("JG_NAME", bean2.getStr("DEPT_NAME"));
								glbean.set("JG_CODE", bean2.getStr("DEPT_CODE"));
								glbean.set("JG_FAR", "0");
								ServDao.save("TS_KCGL_GLJG", glbean);
							
						
						//系统座位号
						int hangshu = max/10+1; //行数
						int xthao = 1;
						String first  = "";
						String last = ip+".1."+max;
						for(int z=1;z<=hangshu;z++){
							if(xthao>max){
								break;
							}
							for(int y=1;y<10;y++){
								if(xthao>max){
									break;
								}
								Bean zwbean  = new Bean();
								zwbean.set("KC_ID", kcid);
								zwbean.set("ZW_ZWH_XT", z+"-"+y);//系统座位号
								zwbean.set("ZW_ZWH_SJ", xthao);//实际座位号
								zwbean.set("ZW_KY", 1);//可用
								ServDao.save("TS_KCGL_ZWDYB", zwbean);
								
								Bean ipbean = new Bean();
								ipbean.set("IPZ_IP", ip+".1."+xthao);
								ipbean.set("IPZ_ZWH", z+"-"+y);
								if(xthao==1){
									first=ip+".1."+xthao;
								}
								ipbean.set("KC_ID", kcid);
								ServDao.save("TS_KCGL_ipzwh", ipbean);
								 xthao++;
							}
						}
						Bean ipscopebean = new  Bean();
						ipscopebean.set("IPS_TITLE", KCNAME+"区段");
						ipscopebean.set("KC_ID", kcid);
						ipscopebean.set("IPS_SCOPE",first+"——"+last);
						ServDao.save("ts_kcgl_ipscope", ipscopebean);
						bianhao++;
					}
			
			}
		}
		
	return new OutBean().setOk();
	} 
	
}
