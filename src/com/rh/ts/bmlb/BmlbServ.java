package com.rh.ts.bmlb;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.text.StyledEditorKit.ForegroundAction;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.base.BeanUtils;
import com.rh.core.base.TipException;
import com.rh.core.comm.FileMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.util.Constant;
import com.rh.ts.xmgl.XmglMgr;

public class BmlbServ extends CommonServ {
	/**
	 * 非资格考试的新增
	 * 
	 * @param paramBean
	 * @return
	 */
	public void addData(Bean paramBean) {
		// 获取服务ID
		String servId = paramBean.getStr(Constant.PARAM_SERV_ID);
		// 获取前台传过来的值
		String user_code = paramBean.getStr("USER_CODE");
		String user_name = paramBean.getStr("USER_NAME");
		String user_sex = paramBean.getStr("USER_SEX");
		String odept_name = paramBean.getStr("ODEPT_NAME");
		String user_office_phone = paramBean.getStr("USER_OFFICE_PHONE");
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
			beans.set("BM_CODE", user_code);
			beans.set("BM_NAME", user_name);
			beans.set("BM_SEX", user_sex);
			beans.set("ODEPT_NAME", odept_name);
			beans.set("BM_OFFICE_PHONE", user_office_phone);
			beans.set("BM_PHONE", user_mobile);
			beans.set("BM_ATIME", user_cmpy_date);
			beans.set("BM_STARTDATE", fzgks_date1);
			beans.set("BM_ENDDATE", fzgks_date2);
			beans.set("BM_TITLE", fzgks_name);
			beans.set("XM_ID", xm_id);
			beans.set("BM_SH_STATE", 0);
			// 新增到数据库
			Bean bmbean = ServDao.create(servId, beans);
			// 获取到报名id
			String bm_id = bmbean.getStr("BM_ID");
			// 根据报名id获取到非资格考试表单
			// 添加公共表
			Bean objBean = new Bean();
			objBean.set("DATA_ID", xm_id);
			objBean.set("STR1", user_code);
			objBean.set("INT1", 0);
			ServDao.save("TS_OBJECT", objBean);

			ParamBean param = new ParamBean();
			param.set("examerUserCode", user_code);
			param.set("level", 0);
			param.set("xmId", xm_id);
			param.set("flowName", 1);
			param.set("shrUserCode", user_code);
			OutBean out = ServMgr.act("TS_WFS_APPLY", "backFlow", param);
			List<Bean> blist = out.getList("result");
			/*List<Bean> blist = (List<Bean>) out.get("result");*/
			String allman = "";
			String node_name = "";
			if (blist != null && blist.size() > 0) {
				node_name = blist.get(0).getStr("NODE_NAME");
				for (int l = 0; l < blist.size(); l++) {
					if (l == 0) {
						allman = blist.get(l).getStr("S_USER");
					} else {
						allman += blist.get(l).getStr("S_USER") + ",";
					}

				}
			}
			// 添加到审核表中
			Bean shBean = new Bean();
			shBean.set("XM_ID", xm_id);
			shBean.set("BM_ID", bm_id);
			shBean.set("BM_NAME", user_name);
			shBean.set("BM_CODE", user_code);
			shBean.set("SH_NODE", node_name);// 目前审核节点
			shBean.set("SH_USER", allman);// 当前办理人
			shBean.set("SH_OTHER", allman);// 其他办理人
			ServDao.save("TS_BMSH_STAY", shBean);

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
		String odept_name = paramBean.getStr("ODEPT_NAME");
		String user_office_phone = paramBean.getStr("USER_OFFICE_PHONE");
		String ryl_mobile = paramBean.getStr("USER_MOBILE");
		String user_cmpy_date = paramBean.getStr("USER_CMPY_DATE");
		String xm_id = paramBean.getStr("XM_ID");
		String bm_start = paramBean.getStr("BM_START");
		String bm_end = paramBean.getStr("BM_END");
		String xm_name = paramBean.getStr("XM_NAME");
		String liststr = paramBean.getStr("BM_LIST");
		String yzgzstr = paramBean.getStr("YZGZ_LIST");
		String odept_code = paramBean.getStr("ODEPT_CODE");
		OutBean outBean = new OutBean();
		JSONArray json;
		JSONObject yzgzstrjson;
		try {
			json = new JSONArray(liststr);
			yzgzstrjson = new JSONObject(yzgzstr);
			if (json.length() > 0) {
				for (int i = 0; i < json.length(); i++) {
					JSONObject job = json.getJSONObject(i); // 遍历 jsonarray
															// 数组，把每一个对象转成 json
															// 对象
					String kslb_code = (String) job.get("BM_LB");
					String kslb_xl_code = (String) job.get("BM_XL");
					String kslb_mk_code = (String) job.get("BM_MK");
					String kslb_type = (String) job.get("BM_TYPE");
					String kslb_id = (String) job.get("ID");
					String wherelbk = "";
					if (!kslb_mk_code.equals("")) {
						wherelbk = "AND KSLBK_CODE=" + "'" + kslb_code + "'" + " AND KSLBK_XL_CODE=" + "'"
								+ kslb_xl_code + "'" + " AND KSLBK_MKCODE=" + "'" + kslb_mk_code + "'"
								+ " AND KSLBK_TYPE=" + "'" + kslb_type + "'";
					}
					if (kslb_mk_code.equals("")) {
						wherelbk = "AND KSLBK_CODE=" + "'" + kslb_code + "'" + " AND KSLBK_XL_CODE=" + "'"
								+ kslb_xl_code + "'" + " AND KSLBK_MK='无模块'" + " AND KSLBK_TYPE=" + "'" + kslb_type
								+ "'";
					}
					List<Bean> lbkList = ServDao.finds("TS_XMGL_BM_KSLBK", wherelbk);
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
						kslb_type_name = lbkList.get(0).getStr("KSLBK_TYPE_NAME");
					}
					// 获取到考试名称
					String back_All = kslb_name + "-" + kslb_xl + "-" + kslb_mk + "-" + kslb_type;
					JSONArray yzgzArg = (JSONArray) yzgzstrjson.get(kslb_id);
					int flag = 0;
					String ad_rule = "";
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
					String ad_result = "";
					if (flag != 0) {
						// 验证不通过
						ad_result = "2";
					}
					if (flag == 0) {
						// 验证通过
						ad_result = "1";
					}
					// 0无审核,1自动审核, 2人工审核, 3自动+人工审核
					int count = XmglMgr.existSh(xm_id);
					Bean beans = new Bean();
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
					beans.set("ODEPT_CODE",odept_code);
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
					String struc = "AND DATA_ID=" + "'" + user_code + "'";
					List<Bean> ucList = ServDao.finds("TS_OBJECT", struc);
					Bean objBean = new Bean();
					if (ucList != null && ucList.size() > 0) {
						String id = ucList.get(0).getStr("ID");
						objBean.setId(id);
						objBean.set("STR1", ryl_mobile);
						ServDao.save("TS_OBJECT", objBean);
					} else {
						objBean.set("SERV_ID", "TS_BMLB_BM");
						objBean.set("DATA_ID", user_code);
						objBean.set("STR1", ryl_mobile);
						ServDao.save("TS_OBJECT", objBean);
					}

					// 获取流程相关信息
					ParamBean param = new ParamBean();
					param.set("examerUserCode", user_code);
					param.set("level", 0);
					param.set("xmId", xm_id);
					param.set("flowName", 1);
					param.set("shrUserCode", user_code);
					OutBean out = ServMgr.act("TS_WFS_APPLY", "backFlow", param);
					List<Bean> blist = out.getList("result");
					String allman = "";
					String node_name = "";
					if (blist.size() > 0) {
						node_name = blist.get(0).getStr("NODE_NAME");
						for (int l = 0; l < blist.size(); l++) {
							if (l == 0) {
								allman = blist.get(l).getStr("SHR_USERCODE");
							} else {
								allman += blist.get(l).getStr("SHR_USERCODE") + ",";
							}

						}
					}

					// 添加到审核表中
					Bean shBean = new Bean();
					shBean.set("XM_ID", xm_id);
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
					 if (count == 0) {
					 ServDao.save("TS_BMSH_PASS", shBean);
					 }
					 //自动审核 无手动
					 if (count == 1) {
					 if (ad_result.equals("1")) {
					 ServDao.save("TS_BMSH_PASS", shBean);
					 }else if(ad_result.equals("2")){
						 ServDao.save("TS_BMSH_NOPASS", shBean);
						 }
					 }
					//只有手动审核
					 if (count == 2) {
					 ServDao.save("TS_BMSH_STAY", shBean);
					 }
					 //自动加手动
					 if (count == 3) {
					 if (ad_result.equals("1")) {
					 ServDao.save("TS_BMSH_PASS", shBean);
					 }
					 if (ad_result.equals("2")) {
					 ServDao.save("TS_BMSH_NOPASS", shBean);
					 }
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		outBean.set("strresult", "提交成功");
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
		String wheremk = "";
		if (!MK.equals("")) {
			wheremk = "AND KSLB_NAME=" + "'" + LB + "'" + " AND KSLB_XL=" + "'" + XL + "'" + " AND KSLB_MK_CODE=" + "'"
					+ MK + "'" + " AND XM_ID=" + "'" + XM_ID + "'";
		}
		if (MK.equals("")) {
			wheremk = "AND KSLB_NAME=" + "'" + LB + "'" + " AND KSLB_XL=" + "'" + XL + "'" + " AND KSLB_MKE='无模块'"
					+ " AND XM_ID=" + "'" + XM_ID + "'";
		}
		List<Bean> list = ServDao.finds("TS_XMGL_BM_KSLB", wheremk);
		String KSLB_TYPE = "";
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				if (i == 0) {
					KSLB_TYPE = list.get(i).getStr("KSLB_TYPE");
				} else {
					KSLB_TYPE += "," + list.get(i).getStr("KSLB_TYPE");
				}
			}
		}
		Bean outBean = new Bean();
		outBean.set("list", KSLB_TYPE);
		return outBean;
	}

	public OutBean getFzgValue(Bean paramBean) {
		String STATION_TYPE = paramBean.getStr("STATION_TYPE");
		String STATION_NO = paramBean.getStr("STATION_NO");
		String xm_id = paramBean.getStr("xm_id");
		String where2 = "";
		if (!STATION_TYPE.equals("") || !STATION_NO.equals("")) {
			where2 = " AND KSLB_XL<>" + "'" + STATION_NO + "'" + " AND XM_ID=" + "'" + xm_id + "'";
		}
		if (STATION_TYPE.equals("") || STATION_NO.equals("")) {
			where2 = " AND XM_ID=" + "'" + xm_id + "'";
		}
		List<Bean> zgList = ServDao.finds("TS_XMGL_BM_KSLB", where2);
		if (zgList != null && zgList.size() > 0) {
			zgList = ServDao.finds("TS_XMGL_BM_KSLB", where2);
		}
		OutBean outBean = new OutBean();
		outBean.setData(zgList);
		return outBean;

	}

	/**
	 * 根据name条件查询非资格
	 * 
	 * @param paramBean
	 * @return
	 */
	public Bean getSelectName(Bean paramBean) {
		String servId = paramBean.getStr("servId");
		String id = paramBean.getStr("id");
		String user_code = paramBean.getStr("user_code");
		String name = paramBean.getStr("name");
		String where = "";
		if (name == "" || "全部查询".equals(name)) {
			where = "AND XM_ID=" + "'" + id + "' " + " AND BM_CODE=" + "'" + user_code + "'";
		} else {

			where = "AND XM_ID=" + "'" + id + "' " + " AND BM_CODE=" + "'" + user_code + "' " + "AND BM_NAME like "
					+ "'%" + name + "%'";
		}
		List<Bean> list = ServDao.finds(servId, where);
		Bean outBean = new Bean();
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
	 * 获取项目下 已经报考的考试
	 * 
	 * @param paramBean
	 * @return
	 */
	public Bean getList(Bean paramBean) {
		String servId = "TS_BMLB_BM";
		String id = paramBean.getStr("id");
		String user_code = paramBean.getStr("user_code");
		String where = "AND XM_ID=" + "'" + id + "' " + " AND BM_CODE=" + "'" + user_code + "' order by BM_STATE";
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
		String where = "AND BM_CODE=" + "'" + user_code + "' " + where1 + " order by BM_STATE";
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
	 * 
	 * @param paramBean
	 * @return
	 */
	public Bean getSelectedData(Bean paramBean) {
		Bean _PAGE_ = new Bean();
		Bean outBean = new Bean();
		String servId = "TS_BMLB_BM";
		String NOWPAGE = paramBean.getStr("nowpage");
		String SHOWNUM = paramBean.getStr("shownum");
		String user_code = paramBean.getStr("user_code");
		String where1 = paramBean.getStr("where");
		String where = "AND BM_CODE=" + "'" + user_code + "' " + where1 + " order by BM_STATE";
		List<Bean> list = ServDao.finds(servId, where);
		int ALLNUM = list.size();
		// 计算页数
		int meiye = Integer.parseInt(SHOWNUM);
		int yeshu = ALLNUM / meiye;
		int yushu = ALLNUM % meiye;
		// 获取总页数
		if (yeshu == 0 && yushu != 0) {
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

		outBean.set("list", w.toString());
		_PAGE_.set("ALLNUM", list.size());
		_PAGE_.set("NOWPAGE", NOWPAGE);
		_PAGE_.set("PAGES", yeshu);
		outBean.set("list", w.toString());
		outBean.set("_PAGE_", _PAGE_);
		outBean.set("first", chushi);
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
		String where = "AND XM_ID=" + "'" + id + "' " + "AND BM_CODE=" + "'" + user_code + "' order by BM_STATE";
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
	 */
	public void deletesingle(Bean paramBean) {
		String servId = "TS_BMLB_BM";
		String id = paramBean.getStr("id");
		String where = "AND BM_ID=" + "'" + id + "'";
		List<Bean> list = ServDao.finds(servId, where);
		for (int i = 0; i < list.size(); i++) {
			Bean dataBean = list.get(i);
			dataBean.set("BM_STATE", 2);
			ServDao.update(servId, dataBean);
		}
		//删除  审核中 审核通过   审核未通过数据
		String wherebm = "AND BM_ID='"+id+"'";
		List<Bean> finds = ServDao.finds("TS_BMSH_STAY", wherebm);
		for (Bean bean : finds) {
			ServDao.delete("TS_BMSH_STAY", bean);
		}
		List<Bean> finds2 = ServDao.finds("TS_BMSH_PASS", wherebm);
		for (Bean bean : finds2) {
			ServDao.delete("TS_BMSH_PASS", bean);
		}
		List<Bean> finds3 = ServDao.finds("TS_BMSH_NOPASS", wherebm);
		for (Bean bean : finds3) {
			ServDao.delete("TS_BMSH_NOPASS", bean);
		}
	}

	public Bean lookstate(Bean paramBean) {
		String xmid = paramBean.getStr("xmid");
		String user_code = paramBean.getStr("user_code");
		String where = "AND XM_ID=" + "'" + xmid + "' " + "AND BM_CODE=" + "'" + user_code + "' order by BM_STATE";
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
		String where = "AND XM_ID=" + "'" + xmid + "'" + " AND BM_CODE=" + "'" + user_code + "' order by BM_STATE";
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
		List<Bean> list1 = new ArrayList<Bean>();
		List<Bean> list2 = new ArrayList<Bean>();
		List<Bean> list3 = new ArrayList<Bean>();
		for (Bean bean : list) {
			// 序列为空 为第一层级
			String s = bean.getStr("KSLBK_XL");
			String ss = bean.getStr("KSLBK_MK");
			String sss = bean.getId();
			if (s == "") {
				s1 += "{text: '" + bean.getStr("KSLBK_IDVALUE") + "', value: '" + bean.getStr("KSLBK_IDVALUE")
						+ "', extendAttr: { id: " + bean.getId() + " } }, ";
			} else if ("".equals(bean.getStr("KSLBK_MK"))) {
				s2 += "{ " + '"' + "text" + '"' + ": " + '"' + bean.getStr("KSLBK_XL") + '"' + "," + '"' + "value" + '"'
						+ ": " + '"' + bean.getStr("KSLBK_XL") + '"' + "," + '"' + "extendAttr" + '"' + ": {" + '"'
						+ "id" + '"' + ": " + '"' + bean.getId() + '"' + "," + '"' + "parentId" + '"' + ":" + '"'
						+ bean.getStr("KSLBK_PID") + '"' + "} }, ";
			} else if (!"".equals(bean.getStr("KSLBK_XL")) && "".equals(bean.getStr("KSLBK_TYPE"))
					&& !"".equals(bean.getStr("KSLBK_MK"))) {
				s3 += "{ " + '"' + "text" + '"' + ": " + '"' + bean.getStr("KSLBK_MK") + '"' + "," + '"' + "value" + '"'
						+ ": " + '"' + bean.getStr("KSLBK_MK") + '"' + "," + '"' + "extendAttr" + '"' + ": {" + '"'
						+ "id" + '"' + ": " + '"' + bean.getId() + '"' + "," + '"' + "parentId" + '"' + ":" + '"'
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
		// 转换成json格式字符串
		ObjectMapper mapper = new ObjectMapper();
		StringWriter w = new StringWriter();
		try {
			mapper.writeValue(w, filelist);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		outBean.set("list", w.toString());
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
		String id = paramBean.getStr("bmid");
		String where = "AND BM_ID=" + "'" + id + "'";
		List<Bean> list = ServDao.finds("TS_BMLB_BM", where);
		if (list.size() == 0) {
			return new OutBean().setOk("信息为空");
		}
		Bean outBean = new Bean();
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
	 * 通过excl文件获取试卷相关信息
	 *
	 * @param fileId
	 *            文件id
	 */
	public OutBean getDataFromXls(Bean paramBean) throws IOException, BiffException {
		List<Bean> result = new ArrayList<>();
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
			Sheet sheet1 = workbook.getSheet(serlist.get(0).getStr("SERV_NAME"));
			int rows = sheet1.getRows();
			List<String> newlist = new ArrayList<String>();
			for (int i = 0; i < rows; i++) {
				if (i == 0) {
					// 获取第一行单元格
					Cell[] cells = sheet1.getRow(0);
					for (Cell cell : cells) {
						for (Bean columnbean : listcolumn) {
							String s = cell.getContents();
							if (cell.getContents().equals(columnbean.getStr("ITEM_NAME"))) {
								newlist.add(columnbean.getStr("ITEM_CODE"));
							}
						}
					}

				}
				if (i != 0) {
					// 获取字段对应的名字 和字段 循环判断 是否对应 然后赋值 将 其组成key value
					// 获的排序好的字段 的list
					Cell[] cells = sheet1.getRow(i);
					Bean bean = new Bean();
					for (int j = 0; j < cells.length; j++) {

						if (!StringUtils.isEmpty(cells[j].getContents())) {
							bean.set(newlist.get(j), cells[j].getContents());
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
		OutBean out = new OutBean();
		String bmid = paramBean.getStr("bmids");
		Bean bmbean = ServDao.find("TS_BMLB_BM", bmid);
		if (bmbean.isEmpty()) {
			return out.setError("此数据不存在");
		}
		String xm_id = bmbean.getStr("XM_ID");
		Bean xmbean = ServDao.find("TS_XMGL", xm_id);
		if (xmbean.isEmpty()) {
			return out.setError("项目消失");
		}
		// 获取项目id
		String where1 = "AND XM_ID=" + "'" + xm_id + "'";
		List<Bean> bmglList = ServDao.finds("TS_XMGL_BMGL", where1);
		if (bmglList.size() == 0) {
			return out.setError("报名信息不存在");
		}
		Bean bmglbean = bmglList.get(0);
		out.set("bmglbean", bmglbean);
		out.set("bmbean", bmbean);
		out.set("xmname", xmbean.getStr("XM_NAME"));
		return out;
	}
	
	/**
	 * 获取 项目kslb
	 */
	public OutBean getMatchData(Bean paramBean){
		OutBean  out = new OutBean();
		String wherexl=paramBean.getStr("where");
		//获取到所有的模块 但可能重复
		List<Bean> xlList = ServDao.finds("TS_XMGL_BM_KSLB", wherexl);
			Bean mkBean = new Bean();
			Bean mkcodeBean = new Bean();
			if(xlList.size()!=0){
				for (Bean bean : xlList) {
						String type = bean.getStr("KSLB_TYPE");
						String mk = bean.getStr("KSLB_MK");
						String mkcode = bean.getStr("KSLB_MK_CODE");
						if (mkBean.containsKey(mk)) {
							List list = mkBean.getList(mk);
							list.add(type);
							mkBean.set(mk,list);
							mkcodeBean.set(mk,mkcode);
						} else {
							List list = new ArrayList();
							list.add(type);
							mkBean.set(mk,list);
							mkcodeBean.set(mk,mkcode);
						}
				}
				Bean finalmkbean = new Bean();
				for(Object mk: mkcodeBean.keySet()){
					String mkcode =mkcodeBean.getStr(mk);
					finalmkbean.set(mk,mkcode);
				}
				out.set("list", xlList);
				out.set("mkoption", finalmkbean);
	}else{
		out.set("list", "");
		out.set("mkoption", "");
	}
			return out;
}
	//获取统计数据  针对中级考试 已通过 或待审核
	public OutBean getBmNum(Bean paramBean){
		OutBean out = new OutBean();
		String user_code = paramBean.getStr("user_code");
		String xl_code = paramBean.getStr("xlcode");
		String lb_code = paramBean.getStr("lbcode");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String strdate = sdf.format(date);
		//跨序列高级考试
		String highwhere = "AND BM_CODE="+"'"+user_code+"' AND BM_ENDDATE like"+"'%"+strdate+"%' AND BM_LB_CODE<>'"+lb_code+"' AND BM_XL_CODE<>'"+xl_code+"' AND BM_TYPE=3 AND(BM_SH_STATE=0 or BM_SH_STATE=1) AND BM_STATE='1'";
		List<Bean> highlist = ServDao.finds("TS_BMLB_BM",highwhere);
		out.set("highnum", highlist.size());
		//夸序列中级考试
		String where = "AND BM_CODE="+"'"+user_code+"' AND  BM_ENDDATE like"+"'%"+strdate+"%' AND BM_LB_CODE<>'"+lb_code+"' AND BM_XL_CODE<>'"+xl_code+"' AND BM_TYPE=2 AND(BM_SH_STATE=0 or BM_SH_STATE=1) AND BM_STATE='1'";
		List<Bean> list = ServDao.finds("TS_BMLB_BM",where);
		out.set("allnum", list.size());
		//本序列考试
		String where1 = "AND BM_CODE="+"'"+user_code+"' AND BM_ENDDATE like"+"'%"+strdate+"%' AND BM_LB_CODE='"+lb_code+"' AND BM_XL_CODE='"+xl_code+"' AND BM_TYPE=2 AND(BM_SH_STATE=0 or BM_SH_STATE=1) AND BM_STATE='1'";
		List<Bean>list1 = ServDao.finds("TS_BMLB_BM", where1);
		out.set("serianum", list1.size());
		//夸序列  总数：
		String where2 = "AND BM_CODE="+"'"+user_code+"' AND BM_ENDDATE like"+"'%"+strdate+"%' AND BM_LB_CODE<>'"+lb_code+"' AND BM_XL_CODE<>'"+xl_code+"' AND BM_TYPE=2 AND(BM_SH_STATE=0 or BM_SH_STATE=1) AND BM_STATE='1'";
		List<Bean> list3 = ServDao.finds("TS_BMLB_BM", where2);
		out.set("othernum",list3.size());
		return out;
	}
	//获取层级数
	public OutBean getcengji(Bean paramBean){
		String DUTY_CODE=paramBean.getStr("DUTY_CODE");
		String STATION_TYPE_CODE=paramBean.getStr("STATION_TYPE_CODE");
		String STATION_NO_CODE=paramBean.getStr("STATION_NO_CODE");
		String where = "AND POSTION_TYPE="+"'"+STATION_TYPE_CODE+"'"+" AND POSITION_NAME_CODE="+"'"+DUTY_CODE+"'"+"AND POSTION_SEQUENCE_ID='"+STATION_NO_CODE+"'";
		List<Bean> finds = ServDao.finds("TS_ORG_STATION",where);
		String cengji = "";
		if(finds.size()!=0){
			 cengji = finds.get(0).getStr("POSTION_QUALIFICATION");
		}
		return new OutBean().set("num", cengji);
	}
	
	//将选中的考试 id查询出来 返回到页面显示
	public OutBean getCheckedData(Bean paramBean){
		List<Bean> list = new ArrayList<Bean>();
		String checkedid = paramBean.getStr("checked");
		String[] split = checkedid.split(",");
		for(int i=0;i<split.length;i++){
			if(!"".equals(split[i])){
				Bean find = ServDao.find("TS_XMGL_BM_KSLBK",split[i]);
				list.add(find);
			}
		}
		return new OutBean().set("list", list);
		
	}
	//获取已报名的此项目的考试  且 已通过
	public OutBean getBmData(Bean paramBean){
		String xmid = paramBean.getStr("xmid");
		String user_code = paramBean.getStr("user_code");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String strdate = sdf.format(date);
		String highwhere = " AND XM_ID='"+xmid+"' AND BM_CODE="+"'"+user_code+"' AND "+"'"+strdate+"' BETWEEN BM_STARTDATE AND BM_ENDDATE AND(BM_SH_STATE=0 or BM_SH_STATE=2) AND BM_STATE='1'";
		List<Bean> finds = ServDao.finds("TS_BMLB_BM",highwhere);
		return new OutBean().set("list", finds);
	}
	//获取所有的几点
	public OutBean getKSLBK_IDs(Bean paramBean){
		OutBean out = new OutBean();
		String xmid = paramBean.getStr("xmid");
		String id = paramBean.getStr("ids");
		String where = "AND CODE_PATH like"+"'%"+id+"%'";
		List<Bean> finds = ServDao.finds("TS_XMGL_BM_KSLBK", where);
		String s = "";
		for (Bean bean : finds) {
			s+="'"+bean.getStr("KSLBK_ID")+"',";
		}
		String dohao = s.substring(0,s.length()-1);
		String where1 = "AND KSLBK_ID in ("+dohao+") AND XM_ID="+"'"+xmid+"'";
		List<Bean> finds2 = ServDao.finds("TS_XMGL_BM_KSLB", where1);
		String ss = "";
		for (Bean bean : finds2) {
			ss+=bean.getStr("KSLBK_ID")+",";
		}
		ss+=id;
		return out.set("idss", ss);
	}
}