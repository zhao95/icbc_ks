package com.rh.ts.xmgl;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
		NBean.set("XM_JD", bean.getStr("XM_JD"));
		NBean.set("EXCEL_TEMPLATE_ID", bean.getStr("EXCEL_TEMPLATE_ID"));
		NBean.set("XM_GJ", bean.getStr("XM_GJ"));
		NBean.set("XM_FQDW_CODE", bean.getStr("XM_FQDW_CODE"));
		NBean.set("XM_KHDKZ", bean.getStr("XM_KHDKZ"));
		NBean.set("XM_KCAP_PUBLISH_USER_CODE", bean.getStr("XM_KCAP_PUBLISH_USER_CODE"));
		NBean.set("XM_KCAP_PUBLISH_TIME", bean.getStr("XM_KCAP_PUBLISH_TIME"));
		Bean beanA= ServDao.save(servId, NBean);
		afterSaveToSz( beanA);
		
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
		// 根据XM_ID查询，从数据库查询
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
					} else if (gj[i].equals("审核")) {
						s.set("XM_NAME_NUM", 2);
					} else if (gj[i].equals("请假")) {
						s.set("XM_NAME_NUM", 3);
					} else if (gj[i].equals("异地借考")) {
						s.set("XM_NAME_NUM", 4);
					} else if (gj[i].equals("试卷")) {
						s.set("XM_NAME_NUM", 5);
					} else if (gj[i].equals("场次测算")) {
						s.set("XM_NAME_NUM", 6);
					} else if (gj[i].equals("考场安排")) {
						s.set("XM_NAME_NUM", 7);
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
		for (Bean bean : query) {
			qz += "," + bean.getStr("G_ID");
		}
		if (!Strings.isBlank(qz)) {
			// 去掉重复群组
			qz = Strings.removeSame(qz);
		}
		String[] qzArray1 = qz.split(",");
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
		String sql1="select a.*,b.bm_end from ts_xmgl a left join ts_xmgl_bmgl b on a.xm_id = b.xm_id where '"+datestr+"' between  b.BM_TZ_START AND b.BM_TZ_END order by b.bm_end desc";
		/*String sql1 = "select * from ts_xmgl where xm_id in(SELECT XM_ID FROM TS_XMGL_BMGL WHERE '"+datestr+"' BETWEEN BM_TZ_START AND BM_TZ_END order by BM_END ASC)";*/
		List<Bean> list = Transaction.getExecutor().query(sql1);
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
			param.set("xmid", bean.getId());
			Bean outBeanCode = ServMgr.act("TS_XMGL_RYGL_V", "getCodes", param);
			
			String codes = outBeanCode.getStr("rycodes");
			if ("".equals(codes)) {
			} else {
				// 本人所在的群组编码
				String[] codeArray = codes.split(",");
				for (int b = 0; b < qzArray1.length; b++) {
					if (Arrays.asList(codeArray).contains(qzArray1[b])) {
						if ("1".equals(bean.getStr("XM_STATE"))) {
							OutBean act2 = ServMgr.act("TS_XMGL_BMGL", "getBMState", param);
							bean.set("START_TIME_BM", act2.getStr("START_TIME"));
							bean.set("END_TIME_BM", act2.getStr("END_TIME"));
							bean.set("STATE_BM", act2.getStr("state"));
							lastlist.add(bean);
							break;
						}
					}
				}
			}
			/*// 可见的项目id
			if (boo == true) {
				kjxm.add(xmarray[a]);
			}*/
		}

	/*	// kjxm为可见项目idlist stringlist 为已报名的项目idlist
	
		for (int i = 0; i < list.size(); i++) {
			Bean bean = list.get(i);
			// 项目中已存在array的 title 数据 将展示在 已报名信息中
			String id = bean.getStr("XM_ID");
			if (kjxm.contains(id)) {
				// 已报名这个考试之后 或者他不能报名这个考试 中断循环 继续开始
				
			}
		}*/

	
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
		Boolean show = false;
		for (Bean bean : list) {
			String id = bean.getId();
			// 查询待审核 表 里的other字段判断 是否包含user_code
			String where = "AND XM_ID=" + "'" + id + "'";
			List<Bean> staylist = ServDao.finds("TS_BMSH_STAY", where);

			for (Bean bean2 : staylist) {

				String other = bean2.getStr("SH_OTHER");
				if (other.contains(user_code)) {
					show = true;
				}
			}
			if (show) {
				SHlist.add(bean);
			}
		}

		Bean out = new Bean();
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
		Bean _PAGE_ = new Bean();
		String zhuangtai = paramBean.getStr("zhuangtai");
		String user_code = paramBean.getStr("user_code");
		String NOWPAGE = paramBean.getStr("nowpage");
		String SHOWNUM = paramBean.getStr("shownum");
		String where1 = paramBean.getStr("where");
		String sql = "SELECT * FROM TS_XMGL WHERE XM_ID IN(select XM_ID from TS_XMGL_BMSH WHERE SH_RGSH = '1') "+where1;
		List<Bean> list = Transaction.getExecutor().query(sql);
		List<Bean> SHlist = new ArrayList<Bean>();
		for (Bean bean : list) {
			// 根据报名id找到审核数据的状态
			String id = bean.getId();
			ParamBean paramb = new ParamBean();
			paramb.set("xmid", id);
			OutBean out = ServMgr.act("TS_XMGL_BMGL", "getSHState", paramb);
			String state = "";
			List<Bean> list2 = out.getList("nojson");
			if (list2.size() != 0) {
				state = list2.get(0).getStr("STATE");
			}
			// 根据项目id找到流程下的所有节点
			String belongwhere = "AND XM_ID='" + id + "'";
			List<Bean> finds = ServDao.finds("TS_XMGL_BMSH", belongwhere);
			if (finds.size() != 0) {
				String wfsid = finds.get(0).getStr("WFS_ID");
				// 根据流程id查找所有审核节点
				String wfswhere = "AND WFS_ID='" + wfsid + "'";
				List<Bean> finds2 = ServDao.finds("TS_WFS_NODE_APPLY", wfswhere);
				// 遍历审核节点 获取 当前人的审核机构
				for (Bean nodebean : finds2) {
					// 根据流程id获取 流程绑定的人和审核机构
					String nodeid = nodebean.getStr("NODE_ID");
					String nodewhere = "AND NODE_ID='" + nodeid + "'";
					List<Bean> finds3 = ServDao.finds("TS_WFS_BMSHLC", nodewhere);
					for (Bean codebean : finds3) {
						if (user_code.equals(codebean.getStr("SHR_USERCODE"))) {
							// 此流程内包含此审核人
							if ("1".equals(zhuangtai) && "待报名".equals(state)) {
								SHlist.add(bean);
							} else if ("2".equals(zhuangtai) && "已结束".equals(state)) {
								SHlist.add(bean);
							} else if ("全部".equals(zhuangtai)&&!"未开始".equals(state)) {
								SHlist.add(bean);
							}

						}
					}
				}
			}

		}

		int ALLNUM = SHlist.size();
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
					list2.add(SHlist.get(i - 1));
				}

			} else {
				for (int j = chushi; j < ALLNUM + 1; j++) {
					list2.add(SHlist.get(j - 1));
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

		_PAGE_.set("ALLNUM", SHlist.size());
		_PAGE_.set("NOWPAGE", NOWPAGE);
		_PAGE_.set("PAGES", yeshu);
		_PAGE_.set("SHOWNUM", SHOWNUM);
		outBean.set("list", w.toString());
		outBean.set("alllist", SHlist);
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
		List<Bean> finds = ServDao.finds("TS_XMGL", where1);
		for (Bean bean : finds) {
			ParamBean param = new ParamBean();
			param.set("xmid", bean.getId());
			OutBean act = ServMgr.act("TS_XMGL_BMGL","getShowLook", param);
			int showlook = act.getInt("showlook");
			
			if("1".equals(zhuangtai)&&"审核中".equals(act.getStr("state"))){
				if(showlook==1){
					bean.set("SH_STATE_STR", act.getStr("state"));
					list.add(bean);
				}

			}else if("2".equals(zhuangtai)&&"审核结束".equals(act.getStr("state"))){
				if(showlook==1){
					bean.set("SH_STATE_STR", act.getStr("state"));
					list.add(bean);
				}
			}else if("全部".equals(zhuangtai)){
				if(showlook==1){
					bean.set("SH_STATE_STR", act.getStr("state"));
					list.add(bean);
				}
			}
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
}