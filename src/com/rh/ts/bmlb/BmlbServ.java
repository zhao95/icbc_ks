package com.rh.ts.bmlb;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


import com.rh.core.base.db.Transaction;
import com.rh.core.serv.bean.PageBean;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.comm.FileMgr;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;
import com.rh.ts.util.RoleUtil;
import com.rh.ts.xmgl.XmglMgr;

public class BmlbServ extends CommonServ {
	 protected Log log = LogFactory.getLog(this.getClass());
	/**
	 * 非资格考试的新增
	 *
	 * @param paramBean
	 * @return
	 */
	public void addData(Bean paramBean) {
		UserBean userBean = Context.getUserBean();
		String odept_code = "";
		String dept_code = "";
		if (userBean.isEmpty()) {

		} else {
			odept_code = userBean.getODeptCode();
			dept_code =userBean.getDeptCode();
		}
		DeptBean deptbean = OrgMgr.getDept(odept_code);
		String dept_name = deptbean.getName();
		// 获取服务ID
		String servId = paramBean.getStr(Constant.PARAM_SERV_ID);
		// 获取前台传过来的值
		String user_code = paramBean.getStr("USER_CODE");
		String user_name = paramBean.getStr("USER_NAME");
		String user_sex = paramBean.getStr("USER_SEX");
		String odept_name = paramBean.getStr("ODEPT_NAME");
		String ryl_mobile = paramBean.getStr("USER_OFFICE_PHONE");
		String user_mobile = paramBean.getStr("USER_MOBILE");
		String user_cmpy_date = paramBean.getStr("USER_CMPY_DATE");
		String xm_id = paramBean.getStr("XM_ID");
		String bmcode = paramBean.getStr("bmCodes");
		String[] bmcodes = bmcode.split(",");
		for (String string : bmcodes) {
			// 根据服务id 主键id获取 当前非资格考试的服务
			Bean bean = ServDao.find("TS_XMGL_BM_FZGKS", string);
			String fzgks_date1 = bean.getStr("FZGKS_STADATE");
			String fzgks_date2 = bean.getStr("FZGKS_ENDDATE");
			String fzgks_name = bean.getStr("FZGKS_NAME");
			Bean beans = new Bean();
			beans.set("KSLBK_ID", string);
			beans.set("ODEPT_CODE", odept_code);
			beans.set("BM_CODE", user_code);
			beans.set("BM_NAME", user_name);
			beans.set("BM_SEX", user_sex);
			beans.set("ODEPT_NAME", odept_name);
			beans.set("BM_OFFICE_PHONE", ryl_mobile);
			beans.set("BM_PHONE", user_mobile);
			beans.set("BM_ATIME", user_cmpy_date);
			beans.set("BM_STARTDATE", fzgks_date1);
			beans.set("BM_ENDDATE", fzgks_date2);
			beans.set("BM_TITLE", fzgks_name);
			beans.set("XM_ID", xm_id);
			Bean bmbean = ServDao.create(servId, beans);
			int count = XmglMgr.existSh(xm_id);

			if (count == 0 || count == 1) {

				beans.set("BM_SH_STATE", 1);
				// 新增到数据库
				String bm_id = bmbean.getStr("BM_ID");
				Bean mindbean = new Bean();
				mindbean.set("SH_LEVEL", 0);
				mindbean.set("DATA_ID", bm_id);
				mindbean.set("SH_ULOGIN", "自动审核");
				mindbean.set("SH_UNAME", "自动审核");
				mindbean.set("SH_UCODE", "");
				mindbean.set("SH_TYPE", 1);
				mindbean.set("SH_NODE", 0);
				mindbean.set("S_ODEPT", bmbean.getStr("S_ODEPT"));
				mindbean.set("S_DNAME", dept_name);
				mindbean.set("S_DEPT", bmbean.getStr("S_DEPT"));
				ServDao.save("TS_COMM_MIND", mindbean);

				String bm_idS = bmbean.getStr("BM_ID");
				// 添加到审核表中
				Bean shBean = new Bean();
				shBean.set("XM_ID", xm_id);
				shBean.set("BM_ID", bm_idS);
				shBean.set("BM_NAME", user_name);
				shBean.set("BM_CODE", user_code);
				shBean.set("ODEPT_CODE", odept_code);
				ServDao.save("TS_BMSH_PASS", shBean);

			} else {
				// 获取到报名id
				String bm_id = bmbean.getStr("BM_ID");
				beans.set("BM_SH_STATE", 0);
				ParamBean param = new ParamBean();
				param.set("examerUserCode", user_code);
				param.set("level", 0);
				param.set("xmId", xm_id);
				param.set("flowName", 1);
				param.set("shrUserCode", user_code);
				/* List<Bean> blist = (List<Bean>) out.get("result"); */
				String allman = "";
				String node_name = "";
				OutBean out = ServMgr.act("TS_WFS_APPLY", "backFlow", param);
				String blist = out.getStr("result");
				if (!"".equals(blist)) {
					allman = blist.substring(0, blist.length() - 1);
					node_name = out.getStr("NODE_NAME");
				}
				// 添加到审核表中
				Bean shBean = new Bean();
				shBean.set("XM_ID", xm_id);
				shBean.set("BM_ID", bm_id);
				shBean.set("BM_NAME", user_name);
				shBean.set("BM_CODE", user_code);
				shBean.set("ODEPT_CODE", odept_code);
				shBean.set("SH_NODE", node_name);// 目前审核节点
				shBean.set("SH_USER", allman);// 当前办理人
				shBean.set("SH_OTHER", allman);// 其他办理人
				ServDao.save("TS_BMSH_STAY", shBean);
				// 自动审核保存到 报名明细中
				Bean mindbean = new Bean();
				mindbean.set("SH_LEVEL", 0);
				mindbean.set("DATA_ID", bm_id);
				mindbean.set("SH_ULOGIN", "自动审核");
				mindbean.set("SH_UNAME", "自动审核");
				mindbean.set("SH_UCODE", "");
				mindbean.set("SH_TYPE", 1);
				mindbean.set("SH_NODE", 0);
				mindbean.set("S_ODEPT", bmbean.getStr("S_ODEPT"));
				mindbean.set("S_DNAME", dept_name);
				mindbean.set("S_DEPT", bmbean.getStr("S_DEPT"));
				ServDao.save("TS_COMM_MIND", mindbean);
			}

		}
		// 添加公共表
		String struc = "AND DATA_ID=" + "'" + user_code
				+ "' AND SERV_ID = 'ts_bmlb_bm'";
		List<Bean> ucList = ServDao.finds("TS_OBJECT", struc);
		if (ucList != null && ucList.size() > 0) {
			Bean objBean = ucList.get(0);
			objBean.set("STR1", ryl_mobile);
			ServDao.save("TS_OBJECT", objBean);
		} else {
			Bean objBean = new Bean();
			objBean.set("SERV_ID", "TS_BMLB_BM");
			objBean.set("DATA_ID", user_code);
			objBean.set("STR1", ryl_mobile);
			ServDao.save("TS_OBJECT", objBean);
		}

	}

	/**
	 * 资格考试的新增
	 *
	 * @param paramBean
	 * @return
	 */
	public Bean addZgData(Bean paramBean) {
		// 获取服务ID
		String servId = paramBean.getStr(Constant.PARAM_SERV_ID);
		// 获取前台传过来的值
		String user_code = paramBean.getStr("USER_CODE");
		String user_name = paramBean.getStr("USER_NAME");
		String user_sex = paramBean.getStr("USER_SEX");
		String user_office_phone = paramBean.getStr("USER_OFFICE_PHONE");
		String ryl_mobile = paramBean.getStr("USER_MOBILE");
		String user_cmpy_date = paramBean.getStr("USER_CMPY_DATE");
		String xm_id = paramBean.getStr("XM_ID");
		String bm_start = paramBean.getStr("BM_START");
		String bm_end = paramBean.getStr("BM_END");
		String xm_name = paramBean.getStr("XM_NAME");
		String liststr = paramBean.getStr("BM_LIST");
		String yzgzstr = paramBean.getStr("YZGZ_LIST");
		if("".equals(yzgzstr)){
			yzgzstr="{'none':'true'}";
		}
		String dept_code = paramBean.getStr("DEPT_CODE");
		DeptBean deptbean = OrgMgr.getDept(dept_code);
		String odept_code = deptbean.getODeptCode();
		String t_dept_code = deptbean.getTDeptCode();
		String odept_name = deptbean.getODeptBean().getName();
		String dept_name = deptbean.getName();
		OutBean outBean = new OutBean();
		JSONArray json;
		JSONObject yzgzstrjson;
		try {
			json = new JSONArray(liststr);
			yzgzstrjson = new JSONObject(yzgzstr);
			if (json.length() > 0) {
				for (int i = 0; i < json.length(); i++) {
					JSONObject job = json.getJSONObject(i); // 遍历 jsonarray
					String ks_time = (String) job.get("BM_KS_TIME"); // 对象
					String kslb_code = (String) job.get("BM_LB");
					String kslb_xl_code = (String) job.get("BM_XL");
					String kslb_mk_code = (String) job.get("BM_MK");
					String kslb_type = (String) job.get("BM_TYPE");
					String kslb_id = (String) job.get("ID");
					String rz_year = (String) job.get("YEAR");
					String BM_YIYI_STATE = (String) job.get("YIYIST");
					String wherelbk = "";
					if (!kslb_mk_code.equals("")) {
						wherelbk = "AND KSLBK_CODE=" + "'" + kslb_code + "'"
								+ " AND KSLBK_XL_CODE=" + "'" + kslb_xl_code
								+ "'" + " AND KSLBK_MKCODE=" + "'"
								+ kslb_mk_code + "'" + " AND KSLBK_TYPE=" + "'"
								+ kslb_type + "'";
					}
					if (kslb_mk_code.equals("")) {
						wherelbk = "AND KSLBK_CODE=" + "'" + kslb_code + "'"
								+ " AND KSLBK_XL_CODE=" + "'" + kslb_xl_code
								+ "'" + " AND KSLBK_MK='无模块'"
								+ " AND KSLBK_TYPE=" + "'" + kslb_type + "'";
					}
					List<Bean> lbkList = ServDao.finds("TS_XMGL_BM_KSLBK",
							wherelbk);
					String kslbk_id = "";
					String kslb_name = "";
					String kslb_xl = "";
					String kslb_mk = "";
					String kslb_type_name = "";
					if (lbkList != null && lbkList.size() > 0) {
						kslbk_id = lbkList.get(0).getStr("KSLBK_ID");
						kslb_name = lbkList.get(0).getStr("KSLBK_NAME");
						kslb_xl = lbkList.get(0).getStr("KSLBK_XL");
						kslb_mk = lbkList.get(0).getStr("KSLBK_MK");
						kslb_type_name = lbkList.get(0).getStr(
								"KSLBK_TYPE_NAME");
					}
					// 获取到考试名称
					String back_All = kslb_name + "-" + kslb_xl + "-" + kslb_mk
							+ "-" + kslb_type;
					int flag = 0;
					String mind = "";
					int count = XmglMgr.existSh(xm_id);
					String ad_rule = "";
					String ad_result = "";
					
					if ("true".equals(yzgzstrjson.get("none"))) {
						ad_result = "1";
					} else {
						JSONArray yzgzArg = (JSONArray) yzgzstrjson
								.get(kslb_id);
						// 获取资格验证信息以及验证结果
						for (int j = 0; j < yzgzArg.length(); j++) {
							JSONObject object = (JSONObject) yzgzArg.get(j);
							String sname = (String) object.get("NAME");
							String svlidate = (String) object.get("VLIDATE");
							ad_rule += sname + ":" + svlidate + ",";
							if (svlidate.equals("false")) {
								flag += 1;
							}
						}
						if (flag != 0) {
							// 验证不通过
							ad_result = "2";
						}
						if (flag == 0) {
							// 验证通过
							ad_result = "1";
						}
						// 0无审核,1自动审核, 2人工审核, 3自动+人工审核
						mind = yzgzArg.toString();
						if (!"".equals(rz_year)) {
							if (yzgzArg.length() > 0) {
								mind = mind.substring(0, mind.length() - 1)
										+ ",{'VLIDATE':'STAY','TISHI':'','NAME':'管理任职已满"
										+ rz_year + "年'}]";
								mind = mind.replaceAll("\'", "\"");
								ad_result = "0";
							}
						}
					}
					Bean beans = new Bean();

					beans.set("BM_YIYI_STATE", BM_YIYI_STATE);
					beans.set("RZ_YEAR", rz_year);
					beans.set("BM_CODE", user_code);
					beans.set("BM_NAME", user_name);
					beans.set("BM_SEX", user_sex);
					beans.set("ODEPT_NAME", odept_name);
					beans.set("BM_OFFICE_PHONE", user_office_phone);
					beans.set("BM_PHONE", ryl_mobile);
					beans.set("BM_ATIME", user_cmpy_date);
					beans.set("BM_LB", kslb_name);
					beans.set("BM_XL", kslb_xl);
					beans.set("BM_MK", kslb_mk);
					beans.set("BM_TYPE", kslb_type);
					beans.set("BM_LB_CODE", kslb_code);
					beans.set("BM_XL_CODE", kslb_xl_code);
					beans.set("BM_MK_CODE", kslb_mk_code);
					beans.set("BM_TYPE_NAME", kslb_type_name);
					beans.set("XM_ID", xm_id);
					beans.set("BM_TITLE", xm_name);
					beans.set("BM_STARTDATE", bm_start);
					beans.set("BM_ENDDATE", bm_end);
					beans.set("KSLBK_ID", kslbk_id);
					beans.set("S_ODEPT", odept_code);
					beans.set("S_DEPT", dept_code);
					beans.set("S_TDEPT", t_dept_code);
					if (count == 0) {
						beans.set("BM_SH_STATE", 1);
					}
					if (count == 1) {
						if (ad_result.equals("1")) {
							beans.set("BM_SH_STATE", 1);
						}
						if (ad_result.equals("2")) {
							beans.set("BM_SH_STATE", 2);
						}
						if (ad_result.equals("0")) {
							// 通过
							beans.set("BM_SH_STATE", 2);
						}
					}
					if (count == 2) {
						beans.set("BM_SH_STATE", 0);
					}
					if (count == 3) {
						if (ad_result.equals("1")) {
							beans.set("BM_SH_STATE", 1);
						}
						if (ad_result.equals("2")) {
							beans.set("BM_SH_STATE", 3);
						}
						if (ad_result.equals("0")) {
							beans.set("BM_SH_STATE", 0);
						}
					}
					Bean bmbean = ServDao.create(servId, beans);

					// 获取到报名主键id
					String bm_id = bmbean.getStr("BM_ID");
					// 验证信息添加
					Bean yzBean = new Bean();
					yzBean.set("BM_ID", bm_id);
					yzBean.set("AD_NAME", back_All);
					yzBean.set("AD_UCODE", user_code);
					yzBean.set("AD_RULE", ad_rule);
					yzBean.set("AD_RESULT", ad_result);
					yzBean.set("AD_UNAME", user_name);
					ServDao.save("TS_BMSH_AUDIT", yzBean);

					// 添加公共表
					String struc = "AND DATA_ID=" + "'" + user_code
							+ "' AND SERV_ID = 'ts_bmlb_bm'";
					List<Bean> ucList = ServDao.finds("TS_OBJECT", struc);
					if (ucList != null && ucList.size() > 0) {
						Bean objBean = ucList.get(0);
						objBean.set("STR1", ryl_mobile);
						ServDao.save("TS_OBJECT", objBean);
					} else {
						Bean objBean = new Bean();
						objBean.set("SERV_ID", "TS_BMLB_BM");
						objBean.set("DATA_ID", user_code);
						objBean.set("STR1", ryl_mobile);
						ServDao.save("TS_OBJECT", objBean);
					}

					// 获取流程相关信息
					ParamBean param = new ParamBean();
					param.set("examerUserCode", user_code);
					param.set("level", 0);
					param.set("deptCode", dept_code);
					param.set("odeptCode", odept_code);
					param.set("xmId", xm_id);
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

					// 添加到审核表中
					beans.set("SH_LEVEL", SH_LEVEL);
					Bean shBean = new Bean();
					shBean.set("RZ_YEAR", rz_year);
					shBean.set("XM_ID", xm_id);
					shBean.set("ODEPT_CODE", odept_code);
					shBean.set("BM_ID", bm_id);
					shBean.set("BM_NAME", user_name);
					shBean.set("BM_CODE", user_code);
					shBean.set("KSLBK_ID", kslbk_id);
					shBean.set("BM_LB", kslb_name);
					shBean.set("BM_XL", kslb_xl);
					shBean.set("BM_MK", kslb_mk);
					shBean.set("BM_TYPE", kslb_type);
					shBean.set("BM_LB_CODE", kslb_code);
					shBean.set("BM_XL_CODE", kslb_xl_code);
					shBean.set("BM_MK_CODE", kslb_mk_code);
					shBean.set("BM_TYPE_NAME", kslb_type_name);
					shBean.set("SH_NODE", node_name);// 目前审核节点
					shBean.set("SH_USER", allman);// 当前办理人
					shBean.set("SH_OTHER", allman);// 其他办理人
					shBean.set("S_ODEPT", bmbean.getStr("S_ODEPT"));
					shBean.set("S_TDEPT", bmbean.getStr("S_TDEPT"));
					shBean.set("S_DEPT", bmbean.getStr("S_DEPT"));
					shBean.set("BM_KS_TIME", ks_time);
					shBean.set("BM_STATUS", 0);
					if (count == 0) {
						shBean.set("SH_OTHER", user_code);
						ServDao.save("TS_BMSH_PASS", shBean);
					}
					// 自动审核 无手动
					if (count == 1) {
						if (ad_result.equals("1")) {
							shBean.set("SH_OTHER", "");// 其他办理人
							ServDao.save("TS_BMSH_PASS", shBean);
						} else if (ad_result.equals("2")) {
							shBean.set("SH_OTHER", "");// 其他办理人
							ServDao.save("TS_BMSH_NOPASS", shBean);
						} else if (ad_result.equals("0")) {
							ServDao.save("TS_BMSH_PASS", shBean);
						}
					}
					// 只有手动审核
					if (count == 2) {
						if ("".equals(allman)) {
							return new OutBean().setError("报名失败没有审核人");
						}
						ServDao.save("TS_BMSH_STAY", shBean);
					}
					// 自动加手动
					if (count == 3) {
						if (ad_result.equals("1")) {
							shBean.set("SH_OTHER", "");// 其他办理人
							ServDao.save("TS_BMSH_PASS", shBean);
						}
						if (ad_result.equals("2")) {
							shBean.set("SH_OTHER", "");// 其他办理人

							ServDao.save("TS_BMSH_NOPASS", shBean);
						}
						if (ad_result.equals("0")) {
							ServDao.save("TS_BMSH_STAY", shBean);
						}
					}
					// 自动审核保存到 报名明细中
					Bean mindbean = new Bean();
					mindbean.set("SH_LEVEL", "自动审核");
					mindbean.set("SH_MIND", mind);
					mindbean.set("DATA_ID", bm_id);
					mindbean.set("SH_STATUS", ad_result);
					mindbean.set("SH_ULOGIN", "");
					mindbean.set("SH_UNAME", "");
					mindbean.set("SH_UCODE", "");
					mindbean.set("SH_TYPE", 1);
					mindbean.set("SH_NODE", 0);
					mindbean.set("S_ODEPT", bmbean.getStr("S_ODEPT"));
					mindbean.set("S_DNAME", dept_name);
					mindbean.set("S_DEPT", bmbean.getStr("S_DEPT"));
					ServDao.save("TS_COMM_MIND", mindbean);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		outBean.set("strresult", "提交成功");
		outBean.setOk("报名成功");
		return outBean;
	}

	/**
	 * 资格考试报名模块与等级的设置
	 *
	 * @param paramBean
	 * @return
	 */
	public Bean getMkvalue(Bean paramBean) {
		String MK = paramBean.getStr("MK");
		String XL = paramBean.getStr("xlname");
		String LB = paramBean.getStr("lbname");
		String XM_ID = paramBean.getStr("xm_id");
		String belongnum = paramBean.getStr("cengji");
		int dengji = 10;
		if (!"".equals(belongnum)) {
			dengji = Integer.parseInt(belongnum);
		}
		String wheremk = "";
		if (!MK.equals("")) {
			wheremk = "AND KSLB_NAME=" + "'" + LB + "'" + " AND KSLB_XL=" + "'"
					+ XL + "'" + " AND KSLB_MK_CODE=" + "'" + MK + "'"
					+ " AND XM_ID=" + "'" + XM_ID + "' AND KSLB_TYPE<="
					+ dengji + " order by cast(KSLB_TYPE as SIGNED) desc";
		}
		if (MK.equals("")) {
			wheremk = "AND KSLB_NAME=" + "'" + LB + "'" + " AND KSLB_XL=" + "'"
					+ XL + "'" + " AND KSLB_MKE='无模块' AND KSLB_TYPE<=" + dengji
					+ " AND XM_ID=" + "'" + XM_ID
					+ "' order by cast(KSLB_TYPE as SIGNED) desc";
		}
		List<Bean> list = ServDao.finds("TS_XMGL_BM_KSLB", wheremk);
		
		String STATION_TYPE_CODE = paramBean.getStr("typecode");
		String STATION_NO_CODE = paramBean.getStr("xlcode");
		UserBean userBean = Context.getUserBean();
		String code = userBean.getCode();
		// 获取到所有的模块 但可能重复
		List<Bean> finds = ServDao.finds("TS_BMLB_BM", "and BM_XL_CODE='"+STATION_NO_CODE+"' and BM_LB_CODE='"+STATION_TYPE_CODE+"' and xm_id='"+XM_ID+"' and BM_STATE='1' and BM_CODE ='"+code+"'");
		for (Bean bean : finds) {
			for(int i=0;i<list.size();i++){
				if(bean.getStr("KSLBK_ID").equals(list.get(i).getStr("KSLBK_ID"))){
					list.remove(i);
					break;
				}
			}
		}
		
		
		String KSLB_TYPE = "";
		String ks_time = "";
		String ids = "";
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Bean find = ServDao.find("TS_XMGL_BM_KSLBK", list.get(i)
						.getStr("KSLBK_ID"));
				if (i == 0) {
					KSLB_TYPE = list.get(i).getStr("KSLB_TYPE");

					ks_time = find.getStr("KSLBK_TIME");

					ids = list.get(i).getStr("KSLBK_ID");
				} else {
					KSLB_TYPE += "," + list.get(i).getStr("KSLB_TYPE");
					ks_time += "," + find.getStr("KSLBK_TIME");
					ids += "," + list.get(i).getStr("KSLBK_ID");
				}
			}
		}
		Bean outBean = new Bean();
		outBean.set("list", KSLB_TYPE);
		outBean.set("KS_TIME", ks_time);
		outBean.set("ids", ids);
		return outBean;
	}

	public OutBean getFzgValue(Bean paramBean) {
		String wherestr = paramBean.getStr("str");
		String STATION_TYPE = paramBean.getStr("STATION_TYPE");
		String STATION_NO = paramBean.getStr("STATION_NO");
		String xm_id = paramBean.getStr("xm_id");
		String where2 = "";
		if (!STATION_TYPE.equals("") || !STATION_NO.equals("")) {
			where2 = " AND KSLB_XL<>" + "'" + STATION_NO + "'" + " AND XM_ID="
					+ "'" + xm_id + "'";
		}
		if (STATION_TYPE.equals("") || STATION_NO.equals("")) {
			where2 = " AND XM_ID=" + "'" + xm_id + "'";
		}
		where2 += wherestr;
		List<Bean> zgList = ServDao.finds("TS_XMGL_BM_KSLB", where2);
		if (zgList != null && zgList.size() > 0) {
			zgList = ServDao.finds("TS_XMGL_BM_KSLB", where2);
		}
		OutBean outBean = new OutBean();
		outBean.setData(zgList);
		return outBean;

	}

	/*
	 * public Bean getSelectName(Bean paramBean) { String servId =
	 * paramBean.getStr("servId"); String id = paramBean.getStr("id"); String
	 * user_code = paramBean.getStr("user_code"); String name =
	 * paramBean.getStr("name"); String where = ""; if (name == "" ||
	 * "全部查询".equals(name)) { where = "AND XM_ID=" + "'" + id + "' " +
	 * " AND BM_CODE=" + "'" + user_code + "'"; } else {
	 * 
	 * where = "AND XM_ID=" + "'" + id + "' " + " AND BM_CODE=" + "'" +
	 * user_code + "' " + "AND BM_NAME like " + "'%" + name + "%'"; } List<Bean>
	 * list = ServDao.finds(servId, where); Bean outBean = new Bean();
	 * ObjectMapper mapper = new ObjectMapper(); StringWriter w = new
	 * StringWriter(); try { mapper.writeValue(w, list); } catch
	 * (JsonProcessingException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } outBean.set("list", w.toString());
	 * return outBean;
	 * 
	 * }
	 */

	/**
	 * 获取项目下 已经报考的考试
	 * @param paramBean id(项目id),user_code(用户编码)
	 * @return
	 */
	public Bean getList(Bean paramBean) {
		String servId = "TS_BMLB_BM";
		String id = paramBean.getStr("id");
		String user_code = paramBean.getStr("user_code");
		String where = "AND XM_ID=" + "'" + id + "' " + " AND BM_CODE=" + "'"
				+ user_code + "' order by BM_STATE";
		List<Bean> list = ServDao.finds(servId, where);
		Bean outBean = new Bean();

		// ObjectMapper和StringWriter都是jackson中的，通过这两个可以实现对list的序列化
		ObjectMapper mapper = new ObjectMapper();
		StringWriter w = new StringWriter();
		try {
			mapper.writeValue(w, list);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		outBean.set("OBJECT", w.toString());
		return outBean;
	}

	/**
	 * 获取 已经报考的考试
	 *
	 * @param paramBean
	 * @return
	 */
	public Bean getAllList(Bean paramBean) {
		String servId = "TS_BMLB_BM";
		String user_code = paramBean.getStr("user_code");
		String where = "AND BM_CODE=" + "'" + user_code + "' order by BM_STATE";
		List<Bean> list = ServDao.finds(servId, where);
		Bean outBean = new Bean();
		outBean.set("list", list);
		return outBean;
	}

	/**
	 * 根据条件 三级联动进行筛选
	 *
	 * @param paramBean
	 * @return
	 */
	public Bean getSelectData(Bean paramBean) {
		String servId = "TS_BMLB_BM";
		String user_code = paramBean.getStr("user_code");
		String where1 = paramBean.getStr("where");
		String where = "AND BM_CODE=" + "'" + user_code + "' " + where1
				+ " order by BM_STATE";
		List<Bean> list = ServDao.finds(servId, where);
		Bean outBean = new Bean();

		// ObjectMapper和StringWriter都是jackson中的，通过这两个可以实现对list的序列化
		ObjectMapper mapper = new ObjectMapper();
		StringWriter w = new StringWriter();
		try {
			mapper.writeValue(w, list);
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
	 * 分页查询 有下拉框查询
	 * @param paramBean
	 * @return
	 */
	public OutBean getSelectedData(ParamBean paramBean) {
		OutBean outBean = new OutBean();

		/* 分页参数处理 */
		PageBean page = paramBean.getQueryPage();
		int rowCount = paramBean.getShowNum(); // 通用分页参数优先级最高，然后是查询的分页参数
		if (rowCount > 0) { // 快捷参数指定的分页信息，与finds方法兼容
			page.setShowNum(rowCount); // 从参数中获取需要取多少条记录，如果没有则取所有记录
			page.setNowPage(paramBean.getNowPage()); // 从参数中获取第几页，缺省为第1页
		} else {
			if (!page.contains(Constant.PAGE_SHOWNUM)) { // 初始化每页记录数设定
				if (paramBean.getQueryNoPageFlag()) { // 设定了不分页参数
					page.setShowNum(0);
				} else { // 没有设定不分页，取服务设定的每页记录数
					page.setShowNum(50);
				}
			}
		}

		// Bean _PAGE_ = new Bean();
		// String servId = "TS_BMLB_BM";
		// String NOWPAGE = paramBean.getStr("nowpage");
		// String SHOWNUM = paramBean.getStr("shownum");
		String user_code = paramBean.getStr("user_code");
		String ppname = paramBean.getStr("ppname");
		String where1 = paramBean.getStr("where");
		where1 = where1.replaceAll("AND ", "AND a.");
		String whereSql = " where a.BM_TITLE LIKE '%"+ppname+"%' AND a.BM_CODE=" + "'" + user_code + "' " + where1
				+ " order by s_atime desc";

		String sql = "select a.*,c.PUBLICITY," +
				"(case when exists(select * from ts_xmgl_sz sz where sz.XM_ID=a.XM_ID and sz.XM_SZ_NAME ='考场安排' and (sz.XM_SZ_TYPE = '未开启' or sz.XM_SZ_TYPE ='' )) then 'true' else 'false' end) as canRetract" +
				" from TS_BMLB_BM a left join ts_bmsh_pass b on b.BM_ID = a.BM_ID "
				+ "left join ts_xmgl_kcap_yapzw c on c.SH_ID = b.SH_ID "
				+ whereSql;
		List<Object> values = new LinkedList<Object>();
		List<Bean> dataList = Transaction.getExecutor().queryPage(sql,
				page.getNowPage(), page.getShowNum(), null, null);
		
		int count = dataList.size();
		int showCount = page.getShowNum();
		boolean bCount; // 是否计算分页
		if ((showCount == 0) || paramBean.getQueryNoPageFlag()) {
			bCount = false;
		} else {
			bCount = true;
		}
		if (bCount) { // 进行分页处理
			if (!page.contains(Constant.PAGE_ALLNUM)) { // 如果有总记录数就不再计算
				int allNum;
				if ((page.getNowPage() == 1) && (count < showCount)) { // 数据量少，无需计算分页
					allNum = count;
				} else {
					allNum = Transaction.getExecutor().count(sql, values);
				}
				page.setAllNum(allNum);
			}
			outBean.setCount(page.getAllNum()); // 设置为总记录数
		} else {
			outBean.setCount(dataList.size());
		}
		for (Bean bean : dataList) {
			String xmid = bean.getStr("XM_ID");
			int countstr = XmglMgr.existSh(xmid);
			bean.set("countstr", countstr);
			ParamBean paramb = new ParamBean();
			paramb.set("xmid", xmid);
			OutBean act = ServMgr.act("TS_XMGL_BMGL", "getXmInfo", paramb);
			bean.set("SH_TGTSY", act.getStr("SH_TGTSY"));
			bean.set("SH_BTGTSY", act.getStr("SH_BTGTSY"));
			bean.set("shstate", act.getStr("shstate"));
			String createdate =  bean.getStr("S_ATIME");
			SimpleDateFormat simp = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			try {
				Date newdate = simp.parse(createdate);
				String date = simp.format(newdate);
				bean.set("S_ATIME", date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		outBean.setData(dataList);
		outBean.setPage(page);
		return outBean;
	}

	/**
	 * 撤销时将已报名的数据状态 改为已撤销（多条）
	 *
	 * @param paramBean
	 */
	public void deletebm(Bean paramBean) {
		String servId = "TS_BMLB_BM";
		String id = paramBean.getStr("id");
		String user_code = paramBean.getStr("user_code");
		String where = "AND XM_ID=" + "'" + id + "' " + "AND BM_CODE=" + "'"
				+ user_code + "' order by BM_STATE";
		List<Bean> list = ServDao.finds(servId, where);
		for (int i = 0; i < list.size(); i++) {
			Bean dataBean = list.get(i);
			dataBean.set("BM_STATE", 2);
			ServDao.update(servId, dataBean);
		}
	}

	/**
	 * 撤销时将已报名的数据状态 改为已撤销(单条)
	 *
	 * @param paramBean
	 * @return OutBean 删除成功信息
	 */
	public OutBean cxupdate(Bean paramBean) {
		
		OutBean out = new OutBean();
		
		// 判断是否已经手动审核
		String servId = "TS_BMLB_BM";
		
		String id = paramBean.getStr("id");
		
		
		String where1 = "AND DATA_ID = '" + id + "' AND SH_TYPE='1'";
		List<Bean> relist = ServDao.finds("TS_COMM_MIND", where1);//单条数据审核记录
		
		String where = "AND BM_ID=" + "'" + id + "'";
		List<Bean> list = ServDao.finds(servId, where);//data数据
		
		for (int i = 0; i < list.size(); i++) {
			
			boolean flag = false;
			
			Bean queryUser = new Bean().set("DATA_ID", id).set("S_FLAG", 1);
			
			for (Bean bean : relist) {
				
				if (!"".equals(bean.getStr("SH_UCODE"))) {//判断审核人字段是否 为空  空：没有手工审核过   不空：手工审核过
					
					flag = true;
					
				}
				
			}

			if (flag) {// 已经审核过 修改状态；
				
				Bean dataBean = list.get(i);
				
				dataBean.set("BM_STATE", 2);
				
				ServDao.update(servId, dataBean);
				
			} else {//未审核数据
				
				ServDao.delete(servId, id);// 删除 审核明细中数据
				
				ServDao.delete("TS_COMM_MIND", queryUser);
			}
		}
		
			Bean queryUserbm = new Bean().set("BM_ID", id);//删除条件的bean :报名数据
			// 删除 审核中 审核通过 审核未通过数据
			ServDao.delete("TS_BMSH_STAY", queryUserbm);
			ServDao.delete("TS_BMSH_PASS", queryUserbm);
			ServDao.delete("TS_BMSH_NOPASS", queryUserbm);
			
		return out.setOk("撤销成功");
	}

	public Bean lookstate(Bean paramBean) {
		String xmid = paramBean.getStr("xmid");
		String user_code = paramBean.getStr("user_code");
		String where = "AND XM_ID=" + "'" + xmid + "' " + "AND BM_CODE=" + "'"
				+ user_code + "' order by BM_STATE";
		List<Bean> list = ServDao.finds("TS_BMLB_BM", where);
		String state = "全部";
		for (Bean bean : list) {
			if (bean.getStr("BM_STATE").equals("1")) {
				state = "部分";
			}
		}
		Bean outBean = new Bean();
		outBean.set("state", state);
		return outBean;
	}

	/**
	 * 分页查询没有 下拉框 查询
	 */
	public OutBean getFenYe(Bean paramBean) {
		// {_PAGE_={NOWPAGE=6, PAGES=100, ALLNUM=4993, SHOWNUM=50},
		// serv=SY_COMM_FILE, act=query, frameId=SY_COMM_FILE-tabFrame,
		// paramsFlag=false, title=系统文件, sId=SY_COMM_FILE, _TRANS_=false}
		OutBean out = new OutBean();
		String NOWPAGE = paramBean.getStr("nowpage");
		String SHOWNUM = paramBean.getStr("shownum");
		String Sid = paramBean.getStr("sid");
		String user_code = paramBean.getStr("user_code");
		String xmid = paramBean.getStr("xmid");
		String where = "AND XM_ID=" + "'" + xmid + "'" + " AND BM_CODE=" + "'"
				+ user_code + "' order by BM_STATE";
		// 获取总记录数
		List<Bean> list = ServDao.finds(Sid, where);
		int ALLNUM = list.size();
		// 计算页数
		int meiye = Integer.parseInt(SHOWNUM);
		int yeshu = ALLNUM / meiye;
		int yushu = ALLNUM % meiye;
		// 获取总页数
		if (yushu == 0 && yeshu != 0) {
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
		// list2为获取到的 第几页 多少条数据
		// ObjectMapper和StringWriter都是jackson中的，通过这两个可以实现对list的序列化
		ObjectMapper mapper = new ObjectMapper();
		StringWriter w = new StringWriter();
		try {
			mapper.writeValue(w, list2);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.set("list", w.toString());

		return out;
	}

	// 动态生成三级联动 拼接 json 字符串
	public Bean getJsonString(Bean paramBean) {
		List<Bean> list = ServDao.finds("TS_XMGL_BM_KSLBKALL", "");
		String s1 = "";
		String s2 = "";
		String s3 = "";
		for (Bean bean : list) {
			// 序列为空 为第一层级
			String s = bean.getStr("KSLBK_XL");
			if (s == "" && !bean.getStr("KSLBK_CODE").equals("023001")) {
				s1 += "{text: '" + bean.getStr("KSLBK_NAME") + "', value: '"
						+ bean.getStr("KSLBK_CODE") + "', extendAttr: { id: "
						+ bean.getId() + " } }, ";
			} else if ("".equals(bean.getStr("KSLBK_MK"))
					&& !"".equals(bean.getStr("KSLBK_XL_CODE"))) {
				s2 += "{ " + '"' + "text" + '"' + ": " + '"'
						+ bean.getStr("KSLBK_XL") + '"' + "," + '"' + "value"
						+ '"' + ": " + '"' + bean.getStr("KSLBK_XL_CODE") + '"'
						+ "," + '"' + "extendAttr" + '"' + ": {" + '"' + "id"
						+ '"' + ": " + '"' + bean.getId() + '"' + "," + '"'
						+ "parentId" + '"' + ":" + '"'
						+ bean.getStr("KSLBK_PID") + '"' + "} }, ";
			} else if (!"".equals(bean.getStr("KSLBK_XL"))
					&& "".equals(bean.getStr("KSLBK_TYPE"))
					&& !"".equals(bean.getStr("KSLBK_MK"))) {
				s3 += "{ " + '"' + "text" + '"' + ": " + '"'
						+ bean.getStr("KSLBK_MK") + '"' + "," + '"' + "value"
						+ '"' + ": " + '"' + bean.getStr("KSLBK_MKCODE") + '"'
						+ "," + '"' + "extendAttr" + '"' + ": {" + '"' + "id"
						+ '"' + ": " + '"' + bean.getId() + '"' + "," + '"'
						+ "parentId" + '"' + ":" + '"'
						+ bean.getStr("KSLBK_PID") + '"' + "} }, ";
			}

		}
		String S1 = "[" + s1.substring(0, s1.length() - 2) + "]";
		String S2 = "[" + s2.substring(0, s2.length() - 2) + "]";
		String S3 = "[" + s3.substring(0, s3.length() - 2) + "]";

		JSONArray jsonoBJECT;
		JSONArray jsonOBJECT2;
		JSONArray jsonOBJECT3;

		Bean out = new Bean();
		try {
			jsonoBJECT = new JSONArray(S1);
			jsonOBJECT2 = new JSONArray(S2);
			jsonOBJECT3 = new JSONArray(S3);
			out.set("s1", jsonoBJECT);
			out.set("s2", jsonOBJECT2);
			out.set("s3", jsonOBJECT3);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		return out;

	}

	/**
	 * 查询异议的文件记录 回显
	 *
	 * @param paramBean
	 * @return
	 */
	public Bean filehist(Bean paramBean) {
		Bean outBean = new Bean();
		String bmid = paramBean.getStr("bmid");
		Bean bmbean = ServDao.find("TS_BMLB_BM", bmid);
		String where = "AND DATA_ID=" + "'" + bmid + "'";
		List<Bean> filelist = ServDao.finds("SY_COMM_FILE", where);

		outBean.set("list", filelist);
		outBean.set("liyou", bmbean.getStr("BM_SS_REASON"));
		return outBean;

	}

	/**
	 * 获取上诉理由 异议原因
	 *
	 * @param paramBean
	 * @return
	 */
	public Bean getLiyou(Bean paramBean) {
		Bean outBean = new Bean();
		String bmid = paramBean.getStr("bmid");
		Bean databean = ServDao.find("TS_BMLB_BM", bmid);
		outBean.set("liyou", databean.getStr("BM_SS_REASON"));
		return outBean;
	}

	/**
	 * 获取单条 根据id
	 */
	public Bean getSingle(Bean paramBean) {
		UserBean userBean = Context.getUserBean();
		String user_code = userBean.getCode(); 
		String phone_num = userBean.getStr("USER_OFFICE_PHONE");
		String compy_date = userBean.getStr("USER_CMPY_DATE");
		//获取融易联
		String rylphone = "";
		List<Bean> finds = ServDao.finds("TS_OBJECT",
				"AND SERV_ID='ts_bmlb_bm' AND DATA_ID = '" + user_code + "'");
		if (finds != null && finds.size() != 0) {
			rylphone = finds.get(0).getStr("STR1");
		}
		String id = paramBean.getStr("bmid");
		String where = "AND BM_ID=" + "'" + id + "'";
		List<Bean> list = ServDao.finds("TS_BMLB_BM", where);
		if (list==null||list.size() == 0) {
			return new OutBean().setError("数据错误，数据不存在").set("list", list);
		}
		list.get(0).set("rylphone", rylphone);
		list.get(0).set("phone_num", phone_num);
		list.get(0).set("compy_date", compy_date);
		Bean outBean = new Bean();

		outBean.set("list", list);
		return outBean;
	}

	/**
	 * 通过excl文件获取试卷相关信息
	 *
	 * @param fileId
	 *            文件id
	 */
	public OutBean getDataFromXls(Bean paramBean) throws IOException,
			BiffException {
		List<Bean> result = new ArrayList<Bean>();
		String fileId = paramBean.getStr("fileId");
		String servid = paramBean.getStr("serv_id");
		String where = "AND SERV_ID=" + "'" + servid + "'";
		List<Bean> listcolumn = ServDao.finds("SY_SERV_ITEM", where);
		// 查询表字段和注释
		/* String where = "AND " */
		Bean fileBean = FileMgr.getFile(fileId);
		InputStream in = FileMgr.download(fileBean);
		Workbook workbook = Workbook.getWorkbook(in);
		try {
			// 查找服务名称
			String whereser = "AND SERV_ID=" + "'" + servid + "'";
			List<Bean> serlist = ServDao.finds("SY_SERV", whereser);
			Sheet sheet1 = workbook
					.getSheet(serlist.get(0).getStr("SERV_NAME"));
			int rows = sheet1.getRows();
			List<String> newlist = new ArrayList<String>();
			for (int i = 0; i < rows; i++) {
				if (i == 0) {
					// 获取第一行单元格
					Cell[] cells = sheet1.getRow(0);
					for (Cell cell : cells) {
						int zz = newlist.size();
						for (Bean columnbean : listcolumn) {
							if (cell.getContents().equals(
									columnbean.getStr("ITEM_NAME"))) {
								newlist.add(columnbean.getStr("ITEM_CODE"));
							}
						}
						if (zz == newlist.size()) {
							newlist.add("");
						}
					}

				}
				if (i != 0) {
					// 获取字段对应的名字 和字段 循环判断 是否对应 然后赋值 将 其组成key value
					// 获的排序好的字段 的list
					Cell[] cells = sheet1.getRow(i);
					Bean bean = new Bean();
					for (int j = 0; j < cells.length; j++) {
						if (!"".equals(newlist.get(j))) {
							if (!StringUtils.isEmpty(cells[j].getContents())) {
								// 需要保存到数据库的字段
								bean.set(newlist.get(j), cells[j].getContents());
							}
						}
					}
					result.add(bean);

				}
			}
		} catch (Exception e) {
			throw new TipException("Excel文件解析错误，请校验！");
		} finally {
			workbook.close();
		}

		OutBean outBean = saveFromExcel(result, servid);
		return outBean;
	}

	/**
	 * 从excel文件中读取审核数据，并保存
	 *
	 * @param paramBean
	 *            paramBean
	 * @return outBean
	 */
	public OutBean saveFromExcel(List<Bean> list, String servid) {
		OutBean outBean = new OutBean();
		Bean queryBean = new Bean();
		int count = 0;
		if (list.size() != 0) {
			for (Bean bean : list) {
				queryBean.copyFrom(bean);
				if (ServDao.count(servid, queryBean) <= 0) {
					ServDao.save(servid, bean);
				}
				count++;
			}
		}
		return outBean.setCount(count).setMsg("添加成功").setOk();
	}

	/**
	 * 点击查看 展示报名信息
	 */
	public OutBean getShowData(Bean paramBean) {
		UserBean userBean = Context.getUserBean();
		String user_code = userBean.getCode();
		paramBean.set("user_code", user_code);
		OutBean phone = getPhone(paramBean);
		String phontnum = phone.getStr("phone");
		OutBean out = new OutBean();
		String bmid = paramBean.getStr("bmids");
		Bean bmbean = ServDao.find("TS_BMLB_BM", bmid);
		if (bmbean.isEmpty()) {
			return out.setError("此数据不存在");
		}
		String xm_id = bmbean.getStr("XM_ID");
		// 获取项目id
		String where1 = "AND XM_ID=" + "'" + xm_id + "'";
		List<Bean> bmglList = ServDao.finds("TS_XMGL_BMGL", where1);
		if (bmglList.size() == 0) {
			out.set("bmglbean", "");
			return out.setError("报名信息不存在");
		}
		Bean bmglbean = bmglList.get(0);
		out.set("bmglbean", bmglbean);
		out.set("bmbean", bmbean);
		out.set("phone", phontnum);
		//
		Bean xmbean = ServDao.find("TS_XMGL", xm_id);
		if (xmbean == null) {
			return out.setError("项目消失");
		}
		out.set("xmname", xmbean.getStr("XM_NAME"));
		return out;
	}

	/**
	 * 获取 项目kslb
	 */
	public OutBean getMatchData(Bean paramBean) {
		OutBean out = new OutBean();
		String wherexl = paramBean.getStr("where");
		String xm_id = paramBean.getStr("xm_id");
		String STATION_TYPE_CODE = paramBean.getStr("STATION_TYPE_CODE");
		String STATION_NO_CODE = paramBean.getStr("STATION_NO_CODE");
		UserBean userBean = Context.getUserBean();
		String code = userBean.getCode();
		// 获取到所有的模块 但可能重复
		List<Bean> xlList = ServDao.finds("TS_XMGL_BM_KSLB", wherexl);
		List<Bean> finds = ServDao.finds("TS_BMLB_BM", "and BM_XL_CODE='"+STATION_NO_CODE+"' and BM_LB_CODE='"+STATION_TYPE_CODE+"' and xm_id='"+xm_id+"' and BM_STATE='1' and BM_CODE ='"+code+"'");
		for (Bean bean : finds) {
			for(int i=0;i<xlList.size();i++){
				if(bean.getStr("KSLBK_ID").equals(xlList.get(i).getStr("KSLBK_ID"))){
					xlList.remove(i);
					break;
				}
			}
		}
		Bean mkBean = new Bean();
		Bean mkcodeBean = new Bean();
		if (xlList.size() != 0) {
			for (Bean bean : xlList) {
				String type = bean.getStr("KSLB_TYPE");
				String mk = bean.getStr("KSLB_MK");
				String mkcode = bean.getStr("KSLB_MK_CODE");
				if (mkBean.containsKey(mk)) {
					List<String> list = mkBean.getList(mk);
					list.add(type);
					mkBean.set(mk, list);
					mkcodeBean.set(mk, mkcode);
				} else {
					List<String> list = new ArrayList<String>();
					list.add(type);
					mkBean.set(mk, list);
					mkcodeBean.set(mk, mkcode);
				}
			}
			Bean finalmkbean = new Bean();
			for (Object mk : mkcodeBean.keySet()) {
				String mkcode = mkcodeBean.getStr(mk);
				finalmkbean.set(mk, mkcode);
			}
			out.set("list", xlList);
			out.set("mkoption", finalmkbean);
		} else {
			out.set("list", "");
			out.set("mkoption", "");
		}
		return out;
	}

	// 获取统计数据 针对中级考试 已通过 或待审核
	public OutBean getBmNum(Bean paramBean) {
		OutBean out = new OutBean();
		String user_code = paramBean.getStr("user_code");
		String xl_code = paramBean.getStr("xlcode");
		String lb_code = paramBean.getStr("lbcode");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String strdate = sdf.format(date);
		// 跨序列高级考试  本年度
		String highwhere = "AND BM_CODE=" + "'" + user_code+
				"' AND YEAR(BM_ENDDATE) = '"+strdate+"'"
				+ " AND BM_TYPE=3  AND BM_STATE='1'";
		List<Bean> highlist = ServDao.finds("TS_BMLB_BM", highwhere);
		// 查询出 通过了但请假没考的 的数据 这些数据不算
		for (Bean bean : highlist) {
			String bmid = bean.getStr("BM_ID");
			Bean find = ServDao.find("TS_QJLB_QJ", bmid);
			if (find != null) {
				if (find.getStr("QJ_STATUS").equals("2")) {
					highlist.remove(bean);
				}
			}
		}
		out.set("highnum", highlist.size());
		// 夸序列中级考试
		String where = "AND BM_CODE=" + "'" + user_code+
				"' AND YEAR(BM_ENDDATE) = '"+strdate+"'"
				+" AND BM_TYPE=2 AND BM_STATE='1'";
		List<Bean> list = ServDao.finds("TS_BMLB_BM", where);
		for (Bean bean : list) {
			String bmid = bean.getStr("BM_ID");
			Bean find = ServDao.find("TS_QJLB_QJ", bmid);
			if (find != null) {
				if (find.getStr("QJ_STATUS").equals('2')) {
					list.remove(bean);
				}
			}
		}
		out.set("allnum", list.size());
		// 本序列考试
		String where1 = "AND BM_CODE=" + "'" + user_code+
				"' AND YEAR(BM_ENDDATE) = '"+strdate+"'"
				+ " AND BM_LB_CODE='" + lb_code + "' AND BM_XL_CODE='"
				+ xl_code + "' AND BM_TYPE=2 AND BM_STATE='1'";
		List<Bean> list1 = ServDao.finds("TS_BMLB_BM", where1);
		for (Bean bean : list1) {
			String bmid = bean.getStr("BM_ID");
			Bean find = ServDao.find("TS_QJLB_QJ", bmid);
			if (find != null) {
				if (find.getStr("QJ_STATUS").equals('2')) {
					list1.remove(bean);
				}
			}
		}
		out.set("serianum", list1.size());
		// 夸序列 总数：
		String where2 = "AND BM_CODE=" + "'" + user_code+
				"' AND YEAR(BM_ENDDATE) = '"+strdate+"'"
				+ " AND BM_LB_CODE<>'" + lb_code + "' AND BM_XL_CODE<>'"
				+ xl_code + "' AND BM_TYPE=2  AND BM_STATE='1'";
		List<Bean> list3 = ServDao.finds("TS_BMLB_BM", where2);
		for (Bean bean : list3) {
			String bmid = bean.getStr("BM_ID");
			Bean find = ServDao.find("TS_QJLB_QJ", bmid);
			if (find != null) {
				if (find.getStr("QJ_STATUS").equals('2')) {
					list3.remove(bean);
				}
			}
		}
		out.set("othernum", list3.size());
		return out;
	}

	// 获取层级数
	public OutBean getcengji(Bean paramBean) {
		String DUTY_CODE = paramBean.getStr("DUTY_LV_CODE");
		String STATION_TYPE_CODE = paramBean.getStr("STATION_TYPE_CODE");
		String STATION_NO_CODE = paramBean.getStr("STATION_NO_CODE");
		String where = "AND POSTION_TYPE=" + "'" + STATION_TYPE_CODE + "'"
				+ " AND POSTION_NAME_CODE=" + "'" + DUTY_CODE + "'"
				+ " AND POSTION_SEQUENCE_ID='" + STATION_NO_CODE + "'";
		List<Bean> finds = ServDao.finds("TS_ORG_POSTION", where);
		String cengji = "";
		if (finds.size() != 0) {
			cengji = finds.get(0).getStr("POSTION_QUALIFICATION");
		}
		return new OutBean().set("num", cengji);
	}

	// 将选中的考试 id查询出来 返回到页面显示
	public OutBean getCheckedData(Bean paramBean) {
		List<Bean> list = new ArrayList<Bean>();
		String checkedid = paramBean.getStr("checked");
		String[] split = checkedid.split(",");
		for (int i = 0; i < split.length; i++) {
			if (!"".equals(split[i])) {
				Bean find = ServDao.find("TS_XMGL_BM_KSLBK", split[i]);
				list.add(find);
			}
		}
		return new OutBean().set("list", list);

	}

	// 获取已报名的此项目的考试
	public OutBean getBmData(Bean paramBean) {
		OutBean out = new OutBean();
		String xmid = paramBean.getStr("xmid");
		String user_code = paramBean.getStr("user_code");
		String highwhere = " AND XM_ID='" + xmid + "' AND BM_CODE=" + "'"
				+ user_code + "' AND BM_STATE='1'";
		List<Bean> finds2 = ServDao.finds("TS_XMGL_BMSH", "and xm_id='" + xmid
				+ "'");
		List<Bean> finds = ServDao.finds("TS_BMLB_BM", highwhere);

		if (finds2 != null && finds2.size() != 0) {
			String zd = finds2.get(0).getStr("SH_ZDSH");
			String sd = finds2.get(0).getStr("SH_RGSH");
			if (!"1".equals(zd)) {
				// 不进行自动验证
				if (!"1".equals(sd)) {
					// 不进行手动
					out.set("zd", "false");
				} else {
					// 进行手动
					out.set("zd", "true");
				}
			}
		}
		return out.set("list", finds);
	}

	// 获取所有的几点
	public OutBean getKSLBK_IDs(Bean paramBean) {
		OutBean out = new OutBean();
		String xmid = paramBean.getStr("xmid");
		String id = paramBean.getStr("ids");
		String where = "AND CODE_PATH like" + "'%" + id + "%'";
		List<Bean> finds = ServDao.finds("TS_XMGL_BM_KSLBK", where);
		String s = "";
		for (Bean bean : finds) {
			s += "'" + bean.getStr("KSLBK_ID") + "',";
		}
		String dohao = s.substring(0, s.length() - 1);
		String where1 = "AND KSLBK_ID in (" + dohao + ") AND XM_ID=" + "'"
				+ xmid + "'";
		List<Bean> finds2 = ServDao.finds("TS_XMGL_BM_KSLB", where1);
		String ss = "";
		for (Bean bean : finds2) {
			ss += bean.getStr("KSLBK_ID") + ",";
		}
		ss += id;
		return out.set("idss", ss);
	}

	/**
	 * 获取 类别
	 */
	public OutBean getkslbk(Bean paramBean) {
		List<Bean> finds = null;
		String where = "AND KSLBK_XL_CODE is null and KSLBK_CODE<>'023001'";// 管理类没有考试
		finds = ServDao.finds("TS_XMGL_BM_KSLBK", where);
		return new OutBean().set("LBS", finds);
	}

	/**
	 * 获取考试类别库id
	 * 
	 * @param paramBean
	 * @return
	 */
	public OutBean getOneKslbk(Bean paramBean) {
		SqlBean sql = new SqlBean();
		sql.and("KSLBK_CODE", paramBean.getStr("BM_LB_CODE"));
		sql.and("KSLBK_XL_CODE", paramBean.getStr("BM_XL_CODE"));
		sql.and("KSLBK_MKCODE", paramBean.getStr("BM_MK_CODE"));
		sql.and("KSLBK_TYPE", paramBean.getStr("BM_TYPE"));
		Bean find = ServDao.find("TS_XMGL_BM_KSLBK", sql);
		if (find != null) {
			return new OutBean().set("kslbk_id", find.getId());
		}
		return new OutBean();
	}

	/**
	 * 更新验证信息
	 */
	public OutBean updateYzxx(Bean paramBean) {
		String BMID = paramBean.getStr("bmid");
		String yzxx = paramBean.getStr("yzxx");
		SqlBean sql = new SqlBean();
		sql.and("DATA_ID", BMID);
		sql.and("SH_TYPE", 1);
		List<Bean> finds = ServDao.finds("TS_COMM_MIND", sql);
		if (finds != null && finds.size() != 0) {
			for (Bean bean : finds) {
				if (!"".equals(bean.getStr("SH_UCODE"))) {
					return new OutBean().setError("已手动审核验证失败");
				}
			}
			for (Bean bean : finds) {
				if ("".equals(bean.getStr("SH_UCODE"))) {
					bean.set("SH_MIND", yzxx);
					ServDao.save("TS_COMM_MIND", bean);
				}
			}
			return new OutBean();
		}
		return new OutBean();
	}

	/**
	 * 从新验证通过
	 */
	public OutBean takepass(Bean paramBean) {
		String BMID = paramBean.getStr("bmid");
		String yzxx = paramBean.getStr("yzxx");

		SqlBean sql1 = new SqlBean();
		sql1.and("DATA_ID", BMID);
		sql1.and("SH_TYPE", 1);
		List<Bean> finds = ServDao.finds("TS_COMM_MIND", sql1);
		if (finds != null && finds.size() != 0) {
			for (Bean bean : finds) {
				if (!"".equals(bean.getStr("SH_UCODE"))) {
					return new OutBean().setError("已手动审核验证失败");
				}
			}
			for (Bean bean : finds) {
				if ("".equals(bean.getStr("SH_UCODE"))) {
					bean.set("SH_MIND", yzxx);
					ServDao.save("TS_COMM_MIND", bean);
				}
			}

		}

		SqlBean sql = new SqlBean();
		sql.and("BM_ID", BMID);
		List<Bean> BMBeanList = ServDao.finds("TS_BMSH_NOPASS", sql);
		if (BMBeanList != null && BMBeanList.size() != 0) {
			Bean bean = BMBeanList.get(0);
			bean.remove("SH_ID");
			bean.remove("S_CMPY");
			bean.remove("S_ATIME");
			bean.remove("S_MTIME");
			bean.remove("S_FLAG");
			bean.remove("_PK_");
			bean.remove("ROW_NUM_");
			Bean newBean = new Bean();
			newBean.copyFrom(bean);
			ServDao.save("TS_BMSH_PASS", newBean);
			Bean find = ServDao.find("TS_BMLB_BM", BMID);
			if (find != null) {
				find.set("bm_sh_state", 1);
			}
			return new OutBean();
		}
		return new OutBean().setError("验证失败");
	}

	public OutBean takestay(Bean paramBean) {
		String BMID = paramBean.getStr("bmid");
		String year = paramBean.getStr("year");
		String yzxx = paramBean.getStr("yzxx");
		SqlBean sql1 = new SqlBean();
		sql1.and("DATA_ID", BMID);
		sql1.and("SH_TYPE", 1);
		List<Bean> finds = ServDao.finds("TS_COMM_MIND", sql1);
		if (finds != null && finds.size() != 0) {
			for (Bean bean : finds) {
				if (!"".equals(bean.getStr("SH_UCODE"))) {
					return new OutBean().setError("已手动审核验证失败");
				}
			}
			for (Bean bean : finds) {
				if ("".equals(bean.getStr("SH_UCODE"))) {
					bean.set("SH_MIND", yzxx);
					ServDao.save("TS_COMM_MIND", bean);
				}
			}

		}

		SqlBean sql = new SqlBean();
		sql.and("BM_ID", BMID);
		List<Bean> BMBeanList = ServDao.finds("TS_BMSH_NOPASS", sql);
		if (BMBeanList != null && BMBeanList.size() != 0) {
			Bean bean = BMBeanList.get(0);
			bean.remove("SH_ID");
			bean.remove("S_CMPY");
			bean.remove("S_ATIME");
			bean.remove("S_MTIME");
			bean.remove("S_FLAG");
			bean.remove("_PK_");
			bean.remove("ROW_NUM_");
			Bean newBean = new Bean();
			newBean.copyFrom(bean);
			bean.set("RZ_YEAR", year);
			ServDao.save("TS_BMSH_STAY", newBean);
			Bean find = ServDao.find("TS_BMLB_BM", BMID);
			if (find != null) {
				find.set("bm_sh_state", 0);
			}
			return new OutBean();
		}
		return new OutBean().setError("验证失败");
	}

	/**
	 * 获取自动验证信息 进行展示
	 */
	public OutBean getZdYzxx(Bean paramBean) {
		String bmid = paramBean.getStr("bmids");
		SqlBean sql1 = new SqlBean();
		sql1.and("DATA_ID", bmid);
		sql1.and("SH_TYPE", 1);
		sql1.and("SH_UCODE", "");
		List<Bean> finds = ServDao.finds("TS_COMM_MIND", sql1);
		if (finds != null && finds.size() != 0) {
			for (Bean bean : finds) {
				if ("".equals(bean.getStr("SH_UCODE"))) {
					return new OutBean().set("yzxx", bean.getStr("SH_MIND"));
				} else {
					return new OutBean().set("yzxx", "");
				}
			}
		}
		return new OutBean().set("yzxx", "");
	}

	/**
	 * 获取主次机构
	 */
	public OutBean getMSCodes(Bean paramBean) {
		OutBean out = new OutBean();
		String user_code = paramBean.getStr("user_code");
		// 住机构数据
		String where = "AND PERSON_ID='" + user_code + "' AND STRU_FLAG='0'";
		List<Bean> masterlist = ServDao.finds("SY_HRM_ZDSTAFFSTRU", where);
		if (masterlist != null && masterlist.size() != 0) {
			String masterid = masterlist.get(0).getStr("STRU_ID");
			out.set("master", masterid);
			out.set("mastername", masterlist.get(0).getStr("STRU_NAME"));
		} else {
			out.set("mastername", "");
			out.set("master", "");
		}

		// 次机构数据
		String where1 = "AND PERSON_ID='" + user_code + "' AND STRU_FLAG='1'";
		List<Bean> slavelist = ServDao.finds("SY_HRM_ZDSTAFFSTRU", where1);

		String slaveids = "";
		String slavenames = "";
		if (slavelist != null && slavelist.size() != 0) {
			for (Bean bean : slavelist) {
				String deptcode = bean.getStr("STRU_ID");
				DeptBean dept = OrgMgr.getDept(bean.getStr("STRU_ID"));
				String oDeptCode = dept.getODeptCode();
				if (deptcode.equals(oDeptCode)) {
					// 机构
				} else {
					// 部门
					slaveids += bean.getStr("STRU_ID") + ",";
					String fullname = dept.getODeptBean().getName() + "-"
							+ bean.getStr("STRU_NAME") + ",";
					slavenames += fullname;
				}
			}
			out.set("slaver", slaveids);
			out.set("slavenames", slavenames);
		} else {
			out.set("slavenames", slavenames);
			out.set("slaver", slaveids);
		}

		return out;
	}

	/**
	 * 导出所有数据时的id
	 */
	public OutBean getexportdata(Bean paramBean) {
		String where = paramBean.getStr("where");
		String dataid = "";
		String servid = paramBean.getStr("servId");
		List<Bean> finds = ServDao.finds(servid, where);
		if (finds != null && finds.size() != 0) {
			for (Bean bean : finds) {
				dataid += bean.getId() + ",";
			}
			return new OutBean().set("dataids", dataid);
		}
		return new OutBean().set("dataids", "");
	}

	/**
	 * 导出所有数据
	 */
	public OutBean getAllBelongData(Bean paramBean) {
		/**
		 * 获取辖内机构某一页的数据
		 *
		 * @param paramBean
		 * @return
		 */
		// 当前审核人
		String servid = paramBean.getStr("servId");
		UserBean user = Context.getUserBean();
		Bean userPvlgToHT = RoleUtil
				.getPvlgRole(user.getCode(), "TS_BMGL_XNBM");
		Bean userPvlgToHTBean = (Bean) userPvlgToHT.get("TS_BMGL_XNBM_PVLG");
		Bean str = (Bean) userPvlgToHTBean.get("XN_BM");
		String dept_code = str.getStr("ROLE_DCODE");
		if ("".equals(dept_code)) {
			dept_code = user.getStr("ODEPT_CODE");
		}
		dept_code = dept_code.substring(0, 10);
		String xmid = paramBean.getStr("xmid");
		String deptwhere = "";
		/*
		 * if("belong".equals(xianei)){ //根据项目id找到流程下的所有节点 String belongwhere =
		 * "AND XM_ID='"+xmid+"'"; List<Bean> finds =
		 * ServDao.finds("TS_XMGL_BMSH", belongwhere); String deptcodes = "";
		 * if(finds.size()!=0){ String wfsid = finds.get(0).getStr("WFS_ID");
		 * //根据流程id查找所有审核节点 String wfswhere =
		 * "AND WFS_ID='"+wfsid+"' AND SHR_USERCODE='"+user_code+"'"; List<Bean>
		 * finds2 = ServDao.finds("TS_WFS_BMSHLC", wfswhere); //遍历审核节点 获取
		 * 当前人的审核机构 for (Bean bean : finds2) { belongdeptcode =
		 * bean.getStr("DEPT_CODE"); String[] split = belongdeptcode.split(",");
		 * if(split.length>0){ for (String string : split) {
		 * if(!"".equals(string)){ deptcodes+=string+","; List<DeptBean>
		 * deptlist = OrgMgr.getChildDeptsAll(compycode, string); for (Bean
		 * deptbean : deptlist) { String id = deptbean.getId();
		 * deptcodes+=id+","; } } } }
		 * 
		 * } } if(!"".equals(deptcodes)){ deptcodes=deptcodes.substring(0,
		 * deptcodes.length()-1)+""; } deptwhere =
		 * "AND S_DEPT IN ("+deptcodes+")"; }else{
		 */
		// 管理员以下的所有机构部门

		if (dept_code.equals("0010100000")) {
			deptwhere += "AND XM_ID='" + xmid + "'";
		} else {
			/*
			 * List<DeptBean> finds = OrgMgr.getChildDeptsAll(compycode,
			 * dept_code); for (Bean bean : finds) {
			 * dept_code+=","+bean.getStr("DEPT_CODE"); } deptwhere =
			 * "AND S_DEPT IN ("+dept_code+")";
			 * 
			 * }
			 */
			DeptBean dept = OrgMgr.getDept(dept_code);
			String codepath = dept.getCodePath();
			String sql = "select * from "
					+ servid
					+ " a where exists(select dept_code from sy_org_dept b where code_path like concat('"
					+ codepath
					+ "','%') and a.s_dept=b.dept_code and s_flag='1') AND XM_ID='"
					+ xmid + "'";
			List<Bean> query = Transaction.getExecutor().query(sql);
			String ids = "";
			for (Bean bean : query) {
				ids += bean.getId() + ",";
			}
			return new OutBean().set("ids", ids);
		}

		// 根据审核 机构 匹配当前机构下的所有人

		List<Bean> list = ServDao.finds(servid, deptwhere);
		String ids = "";
		for (Bean bean : list) {
			ids += bean.getId() + ",";
		}
		return new OutBean().set("ids", ids);
	}

	/**
	 * 非资格不重复报名
	 */
	public OutBean pdfzg(Bean paramBean) {
		UserBean userBean = Context.getUserBean();
		String userCode = userBean.getCode();
		String str = paramBean.getStr("ids");
		String[] split = str.split(",");
		for (String string : split) {
			if (!"".equals(string)) {
				List<Bean> finds = ServDao.finds("TS_BMLB_BM",
						"and KSLBK_ID ='" + string + "' AND BM_CODE = '"
								+ userCode + "'");
				if (finds != null) {
					if (finds.size() == 0) {
					} else {
						return new OutBean().set("flag", "false");
					}
				}
			}
		}
		return new OutBean().set("flag", "true");
	}

	/**
	 * 是否有权限查看辖内报名
	 */
	public OutBean lookXn(Bean paramBean) {
		UserBean userBean = Context.getUserBean();
		Bean userPvlgToHT = RoleUtil.getPvlgRole(userBean.getCode(),
				"TS_BMGL_XNBM");
		Bean userPvlgToHTBean = (Bean) userPvlgToHT.get("TS_BMGL_XNBM_PVLG");
		if (userPvlgToHTBean == null) {
			return new OutBean().set("look", "false");
		}
		if ("0".equals(userPvlgToHTBean.getStr("XN_BM"))) {
			log.error(userPvlgToHTBean.getStr("XN_BM"));
			return new OutBean().set("look", "false");
		} else {
			Bean str = (Bean) userPvlgToHTBean.get("XN_BM");
			if (str == null) {
				return new OutBean().set("look", "false");
			}
			String dept_code = str.getStr("ROLE_DCODE");
			if ("".equals(dept_code)) {
				return new OutBean().set("look", "false");
			}
		}
		return new OutBean().set("look", "true");
	}

	/**
	 * 数据是否在代办中 代办 不可撤销
	 */
	public OutBean sureDelete(Bean paramBean) {
		String bmid = paramBean.getStr("bmid");
		List<Bean> finds = ServDao.finds("TS_COMM_TODO", "and DATA_ID='" + bmid
				+ "'");
		List<Bean> finds1 = ServDao.finds("TS_COMM_TODO_DONE", "and DATA_ID='"
				+ bmid + "'");
		if (finds != null && finds.size() != 0) {
			return new OutBean().set("flag", "false");
		}
		if (finds1 != null && finds1.size() != 0) {
			return new OutBean().set("flag", "false");
		}
		return new OutBean().set("flag", "true");
	}

	/**
	 * 是否有审核
	 */
	public OutBean getShState(Bean paramBean) {
		String XMID = paramBean.getStr("XM_ID");
		int count = XmglMgr.existSh(XMID);
		return new OutBean().set("count", count);
	}

	/**
	 * 融易联
	 */
	public OutBean getPhone(Bean paramBean) {
		String user_code = paramBean.getStr("user_code");
		OutBean out = new OutBean();
		List<Bean> finds = ServDao.finds("TS_OBJECT",
				"AND SERV_ID='ts_bmlb_bm' AND DATA_ID = '" + user_code + "'");
		if (finds != null && finds.size() != 0) {
			String phone = finds.get(0).getStr("STR1");
			out.set("phone", phone);

		} else {
			out.set("phone", "");
		}
		return out;
	}
	
	/**
	 * 判断除了本序列 外是否 还有其它考试
	 */
	
	public OutBean checkXl(Bean paramBean){
		String xm_id = paramBean.getStr("xm_id");
		String STATION_NO_CODE =paramBean.getStr("STATION_NO_CODE");
		String STATION_TYPE_CODE =paramBean.getStr("STATION_TYPE_CODE");
		List<Bean> list = ServDao.finds("TS_XMGL_BM_KSLB", "AND (KSLB_XL_CODE<>'"+STATION_NO_CODE+"') AND KSLB_CODE='"+STATION_TYPE_CODE+"' AND XM_ID='"+xm_id+"'");
		List<Bean> find = ServDao.finds("TS_BMLB_BM", "and xm_id='"+xm_id+"' AND BM_XL_CODE<>'"+STATION_NO_CODE+"' and bm_lb_code ='"+STATION_TYPE_CODE+"'");
		//删除 已报名的数据 之后
		for (Bean bean : find) {
			for(int i=0;i<list.size();i++){
				if(bean.getStr("KSLBK_ID").equals(list.get(i).getStr("KSLBK_ID"))){
					list.remove(i);
				}
			}
		}
		if(list ==null||list.size()==0){
			return new OutBean().set("flag", "true");
		}
		
		return new OutBean().set("flag", "false");
	}
	/**
	 * 获取几个部门的最大部门
	 */
	public OutBean getMaxDept(Bean paramBean){
		String codes = paramBean.getStr("codes");
		String G_ID = paramBean.getStr("G_ID");
		//查找此群组下的所有 部门   重新计算
		List<Bean> list = ServDao.finds("TS_BM_GROUP_USER", "AND G_ID='"+G_ID+"' AND G_TYPE='2'");
		String dept_code = "";
		for (Bean bean : list) {
			 dept_code += ",'"+bean.getStr("USER_DEPT_CODE")+"'";
		}
		codes+=dept_code;
		
		String sql = "SELECT dept_code,dept_name FROM sy_org_dept WHERE dept_level =(SELECT Min(dept_level) FROM sy_org_dept WHERE dept_code IN ("+codes+"))AND dept_code IN("+codes+")";
		List<Bean> list2 = Transaction.getExecutor().query(sql);
		String sql1 = "DELETE FROM TS_BM_GROUP_USER_DEPT WHERE G_ID='"+G_ID+"' AND G_TYPE='2'";
		Transaction.getExecutor().execute(sql1);
		for (Bean bean : list2) {
			Bean newBean = new Bean();
			newBean.set("USER_DEPT_CODE", bean.getStr("DEPT_CODE"));
			newBean.set("USER_DEPT_NAME", bean.getStr("DEPT_NAME"));
			newBean.set("G_ID", G_ID);
			newBean.set("G_TYPE", 2);
			ServDao.save("TS_BM_GROUP_DEPT", newBean);
		}
		return new OutBean().setOk("添加成功");
	}
	/**
	 * 此人已报名 数据 不能再显示出来
	 */
	public OutBean getYibmids(Bean paramBean){
		UserBean userBean = Context.getUserBean();
		String code = userBean.getCode();
		String xmid = paramBean.getStr("xmid");
		String sql1 = "select KSLBK_ID FROM TS_XMGL_BM_KSLB  WHERE XM_ID='"+xmid+"'";
		List<Bean> query = Transaction.getExecutor().query(sql1);
		/*String sql = "select kslbk_pid from ts_xmgl_bm_kslbk where kslbk_id in (select kslbk_pid from ts_xmgl_bm_kslbk where kslbk_id in (SELECT KSLBK_PID FROM TS_XMGL_BM_KSLBK WHERE KSLBK_ID IN (select KSLBK_ID FROM TS_XMGL_BM_KSLB  WHERE XM_ID='"+xmid+"')))union select kslbk_pid from ts_xmgl_bm_kslbk where kslbk_id in (SELECT KSLBK_PID FROM TS_XMGL_BM_KSLBK WHERE KSLBK_ID IN (select KSLBK_ID FROM TS_XMGL_BM_KSLB  WHERE XM_ID='"+xmid+"'))union SELECT KSLBK_PID FROM TS_XMGL_BM_KSLBK WHERE KSLBK_ID IN (select KSLBK_ID FROM TS_XMGL_BM_KSLB  WHERE XM_ID='"+xmid+"')union select KSLBK_ID FROM TS_XMGL_BM_KSLB  WHERE XM_ID='"+xmid+"'";
		List<Bean> query = Transaction.getExecutor().query(sql);*/
			List<Bean> list = ServDao.finds("TS_BMLB_BM", "AND BM_CODE = '"+code+"' and XM_ID='"+xmid+"' AND BM_STATE = 1");
			//删除已报名的  kslbk_id
			for (Bean bean : list) {
				String KSLBK_ID = bean.getStr("KSLBK_ID");
			for(int i = 0;i<query.size();i++){
				if(query.get(i).getId().equals(KSLBK_ID)){
					query.remove(i);
					break;
				}
				}
			}
			//最后的
			String ids = "";
			for(int i = 0;i<query.size();i++){
				if(i==0){
					ids="'"+query.get(i).getId()+"'";
				}else{
					ids+=",'"+query.get(i).getId()+"'";
				}
			}
			
			String sql = "select kslbk_pid from ts_xmgl_bm_kslbk where kslbk_id in (select kslbk_pid from ts_xmgl_bm_kslbk where kslbk_id in (SELECT KSLBK_PID FROM TS_XMGL_BM_KSLBK WHERE KSLBK_ID IN ("+ids+")))union select kslbk_pid from ts_xmgl_bm_kslbk where kslbk_id in (SELECT KSLBK_PID FROM TS_XMGL_BM_KSLBK WHERE KSLBK_ID IN ("+ids+"))union SELECT KSLBK_PID FROM TS_XMGL_BM_KSLBK WHERE KSLBK_ID IN ("+ids+")";
			List<Bean> query1 = Transaction.getExecutor().query(sql);
			String newids="";
			for(int i = 0;i<query1.size();i++){
				if(i==0){
					newids="'"+query1.get(i).getId()+"'";
				}else{
					newids+=",'"+query1.get(i).getId()+"'";
				}
			}
			newids+=","+ids;
			return new OutBean().set("ids", newids);
	}
}