package com.rh.ts.bmsh;

import java.io.IOException;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.util.ExportExcel;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.ImpUtils;
import com.rh.core.util.Lang;
import com.rh.ts.util.RoleUtil;
import com.rh.ts.util.TsConstant;


public class PassServ extends CommonServ {
	
	/** 每次获取数据条数 */
	private static final int ONETIME_EXP_NUM = 5000;
	/** excel最大行数 */
	private static final int EXCEL_MAX_NUM = 65536;

	/**
	 *  获取那一页的记录 返回
	 * 
	 * @param paramBean
	 * @returnge
	 */
	public Bean getUncheckList(Bean paramBean) {
		//逐级 可查看当前层级以上的人  越级可以查看所有人
		String usercode = Context.getUserBean().getCode();
		//查询当前审核人的流程 绑定的审核机构
		String xmid= paramBean.getStr("xmid");
		Bean _PAGE_ = new Bean();
		Bean outBean = new Bean();
		String servId = "TS_BMSH_PASS";
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
		String sql = "select n.dept_code from (select node_id from(select wfs_id from ts_xmgl_bmsh where xm_id='"+xmid+"') c left join TS_WFS_NODE_APPLY d on c.wfs_id = d.wfs_id) m left join TS_WFS_BMSHLC n on m.node_id = n.node_id  "+
				"where n.shr_usercode= '"+usercode+"'";
		List<Bean> query = Transaction.getExecutor().query(sql);
		String dept_code="";
		String deptcodes = "";
		for (Bean bean : query) {
			if(!"".equals(bean.getId())){
				//dept_code
				dept_code=bean.getId();
				String[] split = dept_code.split(",");
				for (String string : split) {
					deptcodes+="'"+string+"',";
				}
			}
		}
		
		List<Bean> list = new ArrayList<Bean>();
		if(deptcodes.length()>5){
			deptcodes = deptcodes.substring(0,deptcodes.length()-1);
	String sql1 = "select distinct code_path from sy_org_dept where dept_level in(select min(dept_level) from sy_org_dept where dept_code in ("+deptcodes+")) and dept_code in("+deptcodes+")";
			List<Bean> query1 = Transaction.getExecutor().query(sql1);
			String sql3 = "";
			sql3 += "select * from (select a.*,b.code_path from ts_bmsh_pass a left join sy_org_dept b on a.s_dept=b.dept_code where 1=1 "+where1+") c ";
			for (int i=0;i<query1.size();i++) {
				//判断哪些考生部门 在此codepath 下
				if(i==0){
					sql3+="where c.code_path like concat('"+query1.get(i).getId()+"','%')";
				}else{
					sql3+=" or c.code_path like concat('"+query1.get(i).getId()+"','%')";
				}
			}
			ALLNUM = Transaction.getExecutor().count(sql3);
			 if(jieshu>ALLNUM){
				 showpage=ALLNUM-chushi;
			 }
			sql3+=" limit "+chushi+","+showpage;
			 list = Transaction.getExecutor().query(sql3);
		}else{
	return new OutBean().setError("空");
		}
		
		int yeshu = ALLNUM / meiye;
		int yushu = ALLNUM % meiye;
		// 获取总页数
		if (yushu != 0) {
			yeshu += 1;
		}
		outBean.set("list",list);
		
		_PAGE_.set("ALLNUM", ALLNUM);
		_PAGE_.set("NOWPAGE", NOWPAGE);
		_PAGE_.set("PAGES", yeshu);
		_PAGE_.set("SHOWNUM", SHOWNUM);
		outBean.set("_PAGE_", _PAGE_);
		 outBean.set("first", chushi+1);
		return outBean;
	}

	/**
	 * 获取项目下所有审核通过的数据
	 * 
	 * @param paramBean
	 * @return
	 */
	public Bean getAllData(Bean paramBean) {
		String xmid = paramBean.getStr("xmid");
		Bean outBean = new Bean();
		String servId = "TS_BMSH_PASS";
		String where1 = paramBean.getStr("where");
		List<Bean> list1 = ServDao.finds(servId, where1);
		String user_code = paramBean.getStr("user_code");
		List<Bean> list = new ArrayList<Bean>();
		if (list1.size() == 0) {
			return new OutBean();
		}
		for (Bean bean : list1) {
			String other = bean.getStr("SH_OTHER");
			if (other.contains(user_code)) {
				list.add(bean);
			}
		}

		String shenuser = "";
		UserBean userBean = Context.getUserBean();
		if (userBean.isEmpty()) {
			return new OutBean().setError("ERROR:user_code 为空");
		} else {
			shenuser = userBean.getStr("USER_CODE");
		}
		String nodeid = "";
		String levels = "";
		ParamBean parambean = new ParamBean();
		parambean.set("examerUserCode", list1.get(0).getStr("BM_CODE"));
		parambean.set("level", 0);
		parambean.set("deptCode", list1.get(0).getStr("S_DEPT"));
		parambean.set("odeptCode", list1.get(0).getStr("S_ODEPT"));
		parambean.set("shrUserCode", shenuser);
		parambean.set("flowName", 1);
		parambean.set("xmId", xmid);
		OutBean outbean = ServMgr.act("TS_WFS_APPLY", "backFlow", parambean);
		List<Bean> flowlist = outbean.getList("resultlist");
		for (Bean bean : flowlist) {
			if (shenuser.equals(bean.getStr("SHR_USERCODE"))) {
				levels = bean.getStr("NODE_STEPS");
				nodeid = bean.getStr("NODE_NAME");
			}
		}
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
		outBean.set("level", levels);
		outBean.set("node_id", nodeid);
		return outBean;
	}

	/**
	 * 修改 审核状态
	 * 
	 * @param paramBean
	 */
	@SuppressWarnings("static-access")
	public Bean update(Bean paramBean) {
		
		String node_name = "";
		int level = 0;
		String nodeid = paramBean.getStr("nodeid");
		String levels = paramBean.getStr("level");
		if (!"".equals(levels)) {
			level = Integer.parseInt(levels);
		}
		String s = paramBean.getStr("checkedid");
		String shenuser = "";
		UserBean userBean = Context.getUserBean();
		if (userBean.isEmpty()) {
			return new OutBean().setError("ERROR:user_code 为空");
		} else {
			shenuser = userBean.getStr("USER_CODE");
		}
		
		String xmid = paramBean.getStr("xmid");
		//判断逐级  越级
				String flag = "";
				String wherewfs = "AND XM_ID = '"+xmid+"'";
				List<Bean> finds = ServDao.finds("TS_XMGL_BMSH", wherewfs);
				for (Bean bean2 : finds) {
				String wfsid = 	bean2.getStr("WFS_ID");
				Bean find = ServDao.find("TS_WFS_APPLY", wfsid);
				flag = find.getStr("WFS_TYPE");
				
				String wfswhere = "AND WFS_ID='" + wfsid + "' ORDER BY NODE_STEPS ASC";
				List<Bean> finds2 = ServDao.finds("TS_WFS_NODE_APPLY", wfswhere);
				for (Bean nodebean : finds2) {
					boolean flagstr = false;
					// 根据流程id获取 流程绑定的人和审核机构
					String nodeids = nodebean.getStr("NODE_ID");
					String nodewhere = "AND NODE_ID='" + nodeids + "'";
					List<Bean> finds3 = ServDao.finds("TS_WFS_BMSHLC", nodewhere);
					for (Bean codebean : finds3) {
						if (shenuser.equals(codebean.getStr("SHR_USERCODE"))) {
							node_name= nodebean.getStr("NODE_NAME");
							flagstr = true;
							break;
						}
					}
					if(flagstr){
						break;
					}
				}
				}
		
		// 被选中的id
		String[] ss = s.split(",");
		String state = paramBean.getStr("radiovalue");
		String liyou = paramBean.getStr("liyou");
		List<String> noPassBmIdList = new ArrayList<String>();//审核通过后，后又审核不通过的bmId
		for (String id : ss) {
			if (!"".equals(id)) {
				// 获取当前对象
				Bean bean = ServDao.find("TS_BMSH_PASS", id);
				// 将数据删除 若存在的话在stay中 因为不会再往下推送 所以审核 级数不会变
				String bmid = bean.getStr("BM_ID");
				// 获取 stay里对象
					ServDao.delete("TS_BMSH_STAY", bmid);

				bean.remove("SH_ID");
				bean.remove("S_CMPY");
				bean.remove("S_ATIME");
				bean.remove("S_MTIME");
				bean.remove("S_FLAG");
				bean.remove("_PK_");
				bean.remove("ROW_NUM_");
				Bean newBean = new Bean();
				newBean.copyFrom(bean);
				// 不再推送 当前审核人就是下级审核人
				newBean.set("SH_USER", shenuser);
				newBean.set("SH_LEVEL", level);
				if(flag.equals("1")){
						String newother = newBean.getStr("SH_OTHER")+","+shenuser;
						String[] split = newother.split(",");
						List<String> newlist = new ArrayList<String>();
						for (String string : split) {
							if(!"".equals(string)){
								
								if(!newlist.contains(string)){
									newlist.add(string);
								}
							}
						}
						String newothers = "";
						for (int z=0;z<newlist.size();z++) {
							if(z==(newlist.size()-1)){
								newothers+=newlist.get(z);
							}else{
								newothers+=newlist.get(z)+",";
							}
							
						}
						newBean.set("SH_OTHER", newothers);
				}else{
					//越级  所有人可见
					ParamBean parambean1 = new ParamBean();
					parambean1.set("examerUserCode", bean.getStr("BM_CODE"));
					parambean1.set("level", 0);
					parambean1.set("deptCode", bean.getStr("S_DEPT"));
					parambean1.set("odeptCode", bean.getStr("S_ODEPT"));
					parambean1.set("shrUserCode", shenuser);
					parambean1.set("flowName", 1);
					parambean1.set("xmId", xmid);
					OutBean outbean1 = ServMgr.act("TS_WFS_APPLY", "backFlow",
							parambean1);
					String allman1 = "";
					String blist1 = outbean1.getStr("result");

					if(!"".equals(blist1)){
						allman1= blist1.substring(0,blist1.length()-1);
					}
					newBean.set("SH_USER", shenuser);
					newBean.set("SH_LEVEL", level);
					newBean.set("SH_OTHER", allman1);
				}
				ServDao.save("TS_BMSH_NOPASS", newBean);
				// 修改报名状态
				Bean bm_bean = ServDao.find("TS_BMLB_BM", bmid);
				if (bm_bean != null) {
					bm_bean.set("BM_SH_STATE", "2");
					ServDao.save("TS_BMLB_BM", bm_bean);
				}
				ServDao.delete("TS_BMSH_PASS", id);
				// 审核明细表中插入此次审核数据
				String localip="";
				 InetAddress ia=null; 
			        try { 
			            ia=ia.getLocalHost(); 
			             localip=ia.getHostAddress(); 

			        } catch (Exception e) { 

			            // TODO Auto-generated catch block 

			            e.printStackTrace(); 

			        } 
				Bean mindbean = new Bean();
				mindbean.set("SH_LEVEL", node_name);
				mindbean.set("SH_MIND", liyou);
				mindbean.set("DATA_ID", bean.getStr("BM_ID"));
				mindbean.set("SH_STATUS", state);
				mindbean.set("SH_ULOGIN", localip);
				mindbean.set("SH_UNAME", userBean.getName());
				mindbean.set("SH_UCODE", shenuser);
				mindbean.set("SH_TYPE", 1);
				mindbean.set("SH_NODE", nodeid);
				ServDao.save("TS_COMM_MIND", mindbean);
				noPassBmIdList.add(bmid);
			}

		}
		try{
			//中止流转中的借考流程
			ParamBean jkParamBean = new ParamBean();
			jkParamBean.set("bmIdList",noPassBmIdList);
			ServMgr.act("TS_JKLB_JK", "cancelFlow", jkParamBean);
		}catch (Exception e){
			log.error("中止流转中的借考流程失败");
			log.error(e);
		}
		return new OutBean().setOk();
	}

	/**
	 * 导出事获取 项目下的数据逗号分隔 返回 字符串
	 */
	public Bean reSids(Bean paramBean) {
		Bean outBean = new Bean();
		String servId = paramBean.getStr("servId");
		String xmid = paramBean.getStr("xmid");
		String where = "AND XM_ID=" + "'" + xmid + "'";
		List<Bean> list1 = ServDao.finds(servId, where);
		String user_code = paramBean.getStr("user_code");
		List<Bean> list = new ArrayList<Bean>();
		for (Bean bean : list1) {
			String other = bean.getStr("SH_OTHER");
			if (other.contains(user_code)) {
				list.add(bean);
			}
		}
		String ids = "";
		for (int i = 0; i < list.size(); i++) {
			String id = list.get(i).getId();
			if (i == list.size() - 1) {

				ids += id;
			} else {
				ids += id + ",";
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		StringWriter w = new StringWriter();
		try {
			mapper.writeValue(w, ids);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		outBean.set("string", w.toString());
		return outBean;

	}

	/**
	 * 提供导出Excel
	 * 
	 * @param paramBean
	 *            参数信息
	 * @return 执行结果
	 */
	@Override
	public OutBean exp(ParamBean paramBean) {
		if("true".equals(paramBean.getStr("within"))){
			expwithdata(paramBean);
			return new OutBean().setOk();
		}
		String where = paramBean.getStr("where");
		String servid = paramBean.getServId();
		ParamBean parr = new ParamBean();
		UserBean userBean1 = Context.getUserBean();
		String user_code1 = "";
		if (userBean1.isEmpty()) {
			return new OutBean().setError("ERROR:user_code 为空");
		} else {
			user_code1 = userBean1.getStr("USER_CODE");
		}
		parr.copyFrom(paramBean);
		parr.setServId("TS_BMSH_PX");
		String servId = paramBean.getServId();
		ServDefBean serv = ServUtils.getServDef(servId);
		long count = 0;
		long times = 0;
		paramBean.setQueryPageShowNum(ONETIME_EXP_NUM); // 设置每页最大导出数据量
		String searchWhere = "";
		beforeExp(paramBean); // 执行监听方法
		
		List<Bean> dataList= new ArrayList<Bean>();
		if (paramBean.getId().length() > 0) { // 支持指定记录的导出（支持多选）
			searchWhere = " and " + serv.getPKey() + " in ('"
					+ paramBean.getId().replaceAll(",", "','") + "')";
			paramBean.setQuerySearchWhere(searchWhere);
			dataList = ServDao.finds(servid, searchWhere);
		}else{ // 导出所有记录
			String usercode = Context.getUserBean().getCode();
			//查询当前审核人的流程 绑定的审核机构
			String xmid= paramBean.getStr("xmid");
			Bean _PAGE_ = new Bean();
			String NOWPAGE = paramBean.getStr("nowpage");
			String SHOWNUM = paramBean.getStr("shownum");
			String sql = "select n.dept_code from (select node_id from(select wfs_id from ts_xmgl_bmsh where xm_id='"+xmid+"') c left join TS_WFS_NODE_APPLY d on c.wfs_id = d.wfs_id) m left join TS_WFS_BMSHLC n on m.node_id = n.node_id  "+
					"where n.shr_usercode= '"+usercode+"'";
			List<Bean> query = Transaction.getExecutor().query(sql);
			String dept_code="";
			String deptcodes = "";
			for (Bean bean : query) {
				if(!"".equals(bean.getId())){}
				//dept_code
				dept_code=bean.getId();
				String[] split = dept_code.split(",");
				for (String string : split) {
					deptcodes+="'"+string+"',";
				}
			}
					if(deptcodes.length()>5){
						deptcodes = deptcodes.substring(0,deptcodes.length()-1);
				String sql1 = "select distinct code_path from sy_org_dept where dept_level in(select min(dept_level) from sy_org_dept where dept_code in ("+deptcodes+")) and dept_code in("+deptcodes+")";
						List<Bean> query1 = Transaction.getExecutor().query(sql1);
						String sql3 = "";
						sql3 += "select * from (select a.*,b.code_path from ts_bmsh_pass a left join sy_org_dept b on a.s_dept=b.dept_code where xm_id = '"+xmid+"') c ";
						for (int i=0;i<query1.size();i++) {
							//判断哪些考生部门 在此codepath 下
							if(i==0){
								sql3+="where c.code_path like concat('"+query1.get(i).getId()+"','%')";
							}else{
								sql3+=" or c.code_path like concat('"+query1.get(i).getId()+"','%')";
							}
						}
						sql3+=" and SH_LEVEL !=0 ";
						int count2 = Transaction.getExecutor().count(sql3);
						if (count2 > EXCEL_MAX_NUM) {
							return new OutBean().setError("导出数据总条数大于Excel最大行数："
									+ EXCEL_MAX_NUM);
						}
						dataList = Transaction.getExecutor().query(sql3);
					}else{
				return new OutBean().setError("空");
					}

		}
		// 所有
		 

		List<Bean> finalList = new ArrayList<Bean>();

		// 判断user_code 是否为空 若为空则 导出所有

		searchWhere = " AND USER_CODE=" + "'" + user_code1 + "' order by cast(PX_XUHAO as SIGNED)";

		// 排序用的 parr存读取th
		parr.setQuerySearchWhere(searchWhere);
		LinkedHashMap<String, Bean> cols = new LinkedHashMap<String, Bean>();
		List<Bean> pxdatalist1 = ServDao.finds("TS_BMSH_PX", searchWhere);
		if (pxdatalist1.size() == 0) {
			String where1 = "AND USER_CODE is null limit 0,5";
			pxdatalist1 = ServDao.finds("TS_BMSH_PX", where1);
		}
		
		Bean BmIdBean = new Bean();
		BmIdBean.set("SAFE_HTML", "");
		BmIdBean.set("ITEM_LIST_FLAG", "1");
		BmIdBean.set("ITEM_CODE", "BMID");
		BmIdBean.set("EN_JSON", "");
		BmIdBean.set("ITEM_NAME", "报名编码");
		cols.put("BMID", BmIdBean);
		// 查询出所有的 待审核记录
		for (Bean bean : dataList) {
			String work_num = bean.getStr("BM_CODE");
			Bean userBean = getUserInfo1(work_num);
			Bean newBean = new Bean();
			newBean.set("BMID", bean.getStr("BM_ID"));
			// for循环排序bean
			for (Bean pxbean : pxdatalist1) {
				String aa = pxbean.getStr("PX_NAME");
				String namecol = pxbean.getStr("PX_COLUMN");
				String pxcol = namecol;
				Bean colBean = new Bean();

				colBean.set("SAFE_HTML", "");
				colBean.set("ITEM_LIST_FLAG", "1");
				colBean.set("ITEM_CODE", namecol);
				colBean.set("EN_JSON", "");
				colBean.set("ITEM_NAME", aa);
				cols.put(pxcol, colBean);

				// 字段
				// 如果 有值 赋值
				String name = bean.getStr(namecol);
				if (!"".equals(bean.getStr(namecol))) {
					newBean.set(namecol, bean.getStr(namecol));
				}
				if (!"".equals(userBean.getStr(namecol))) {
					newBean.set(namecol, userBean.getStr(namecol));
					name = userBean.getStr(namecol);
				}
				if ("SH_OTHER".equals(namecol)) {
					// 其它办理人
					ParamBean parambeansss = new ParamBean();
					parambeansss.set("codes", bean.getStr("SH_OTHER"));
					Bean outBeans = ServMgr.act("TS_BMSH_STAY", "getusername",
							parambeansss);
					name = outBeans.getStr("usernames");
				}
				if("SH_STATUS".equals(namecol)){
					//审核状态;
					name = "审核通过";
				}
				if ("JOB_LB".equals(namecol)) {
					name = bean.getStr("BM_LB");
				}
				if ("JOB_XL".equals(namecol)) {
					name = bean.getStr("BM_XL");
				}
				if ("TONGYI".equals(namecol)) {
					name = bean.getStr("BM_CODE");
				}
				String BM_TYPE = "";
				if ("BM_TYPE".equals(namecol)) {
					if ("1".equals(bean.getStr("BM_TYPE"))) {
						BM_TYPE = "初级";
					} else if ("2".equals(bean.getStr("BM_TYPE"))) {
						BM_TYPE = "中级";
					} else {
						BM_TYPE = "高级";
					}
					name = BM_TYPE;

				}
				newBean.set(namecol, name);
				newBean.set("_ROWNUM_", "");
				newBean.set("ROWNUM_", "");
			}
			finalList.add(newBean);

		}
		ExportExcel expExcel = new ExportExcel(serv);
		try {
			// 查询出 要导出的数据
			// 总数大于excel可写最大值
			/*if (count > EXCEL_MAX_NUM) {
				return new OutBean().setError("导出数据总条数大于Excel最大行数："
						+ EXCEL_MAX_NUM);
			}*/
			// 导出第一次查询数据
			paramBean.setQueryPageNowPage(1); // 导出当前第几页
			// 查询出表头 查询出 对应数据 hashmaplist
			expExcel.createHeader(cols);
			expExcel.appendData1(finalList, paramBean);
			// 存在多页数据
			/*if (ONETIME_EXP_NUM < count) {
				times = count / ONETIME_EXP_NUM;
				// 如果获取的是整页数据
				if (ONETIME_EXP_NUM * times == count && count != 0) {
					times = times - 1;
				}
				for (int i = 1; i <= times; i++) {
					paramBean.setQueryPageNowPage(i + 1); // 导出当前第几页
					OutBean out = query(paramBean);
					afterExp(paramBean, out); // 执行导出查询后扩展方法
					expExcel.appendData1(out.getDataList(), paramBean);
				}
			}
			*/expExcel.addSumRow();
		} catch (Exception e) {
			log.error("导出Excel文件异常" + e.getMessage(), e);
		} finally {
			expExcel.close();
		}
		return new OutBean().setOk();
	}

	/**
	 * 获取用户信息
	 */
	public Bean getUserInfo1(String s) {
		Bean outBean = new Bean();
		try {
			// 根据人力编码获取人力信息
			UserBean userBean = UserMgr.getUser(s);
			// 获取当前机构;
			// 获取当前机构;
			DeptBean dept = OrgMgr.getDept(userBean.getDeptCode());
			String codePath = dept.getCodePath();
						 String[] codesarr = codePath.split("\\^");

						int j = 0;
						for (int i =0; i<codesarr.length; i++) {
							if(!"".equals(codesarr[i])){
								String evname = OrgMgr.getDept(codesarr[i]).getName();
								j++;
								outBean.set("LEVEL" + j, evname);
							}
						}

			String shuser = "";
			UserBean userBean1 = Context.getUserBean();
			if (userBean1.isEmpty()) {
				return new OutBean().setError("ERROR:user_code 为空");
			} else {
				shuser = userBean1.getStr("USER_NAME");
			}

			// 其它办理人

			// 当前办理人
			outBean.set("SH_USER", shuser);
			// 性别
			int user_sex = userBean.getSex();
			if (user_sex == 0) {
				outBean.set("USER_SEX", "男");
			} else {
				outBean.set("USER_SEX", "女");
			}
			// 入行时间
			String date = userBean.getStr("USER_CMPY_DATE");
			outBean.set("USER_CMPY_DATE", date);
			// 办公电话
			String office_phone = userBean.getOfficePhone();
			outBean.set("USER_OFFICE_PHONE", office_phone);
			// 手机号码
			String user_phone = userBean.getMobile();
			outBean.set("USER_MOBILE", user_phone);
			// 职务层级
			String cengji = userBean.getPost();
			outBean.set("USER_POST_LEVEL", cengji);
		} catch (Exception exception) {

		}

		return outBean;
	}

	/**
	 * 获取所有部门信息
	 * 
	 * @param oneodeptcode1
	 * @return
	 */
	public String getusercodes(DeptBean oneodeptcode1, String codes) {
		DeptBean newBean = oneodeptcode1.getParentDeptBean();
		if (newBean != null) {
			String s = newBean.getName() + ",";
			return codes += getusercodes(newBean, s);
		} else {

			return "";
		}
	}
	/**
	 * 获取辖内机构某一页的数据
	 * 
	 * @param paramBean
	 * @return
	 */
	public Bean getBelongToList(Bean paramBean) {
	/*	String xianei = paramBean.getStr("xianei");*/
		//当前审核人
		UserBean user = Context.getUserBean();
		String deptwhere = "";
		
		Bean userPvlgToHT = RoleUtil.getPvlgRole(user.getCode(),"TS_BMGL_XNBM");
		Bean userPvlgToHTBean = (Bean) userPvlgToHT.get("TS_BMGL_XNBM_PVLG");
		Bean str = (Bean)userPvlgToHTBean.get("XN_BM");
		String dept_code = str.getStr("ROLE_DCODE");
		if("".equals(dept_code)){
			dept_code=user.getStr("ODEPT_CODE");
		}
		dept_code = dept_code.substring(0,10);
		/*if("belong".equals(xianei)){*/
		//根据项目id找到流程下的所有节点
		/*List<Bean> finds = ServDao.finds("TS_XMGL_BMSH", belongwhere);
		String deptcodes = "";*/
		/*if(finds.size()!=0){
			String wfsid = finds.get(0).getStr("WFS_ID");
			//根据流程id查找所有审核节点
			String wfswhere = "AND WFS_ID='"+wfsid+"' AND SHR_USERCODE='"+user_code+"'";
			List<Bean> finds2 = ServDao.finds("TS_WFS_BMSHLC", wfswhere);
			//遍历审核节点  获取 当前人的审核机构
			for (Bean bean : finds2) {
				belongdeptcode = bean.getStr("DEPT_CODE");
				String[] split = belongdeptcode.split(",");
				if(split.length>0){
					for (String string : split) {
						if(!"".equals(string)){
							deptcodes+=string+",";
							List<DeptBean> deptlist = OrgMgr.getChildDepts(compycode, string);
							for (Bean deptbean : deptlist) {
								deptcodes+=deptbean.getId()+",";
							}
						}
					}
				}
				
		}
		}
		if(!"".equals(deptcodes)){
			deptcodes=deptcodes.substring(0, deptcodes.length()-1)+"";
		}
		 deptwhere = "AND S_DEPT IN ("+deptcodes+")";
		}else{*/
			//管理员以下的所有机构
		String servId = "TS_BMSH_PASS";
		String NOWPAGE = paramBean.getStr("nowpage");
		String SHOWNUM = paramBean.getStr("shownum");
		String where1 = paramBean.getStr("where")+deptwhere;
		
		int ALLNUM = 0;
		int meiye = Integer.parseInt(SHOWNUM);
		

		int nowpage = Integer.parseInt(NOWPAGE);
		int showpage = Integer.parseInt(SHOWNUM);
		// 计算第一项 开始
		int chushi = (nowpage - 1) * showpage;
		// 计算结束项
		int jieshu = (nowpage - 1) * showpage + showpage;
		List<Bean> list;
			if(dept_code.equals("0010100000")){
				//所有人员
				String sql = "select * from TS_BMSH_PASS where 1=1 "+where1;
				 ALLNUM = Transaction.getExecutor().count(sql);
				 if(jieshu>ALLNUM){
					 showpage=ALLNUM-chushi;
				 }
				 String datasql = "select * from TS_BMSH_PASS where 1=1 "+where1 +" limit "+chushi+","+showpage;
				  list = Transaction.getExecutor().query(datasql);
				 
			}else{
				/*List<DeptBean> finds = OrgMgr.getChildDepts(compycode, user.getODeptCode());
				for (Bean bean : finds) {
					dept_code+=","+bean.getStr("DEPT_CODE");
				}
				deptwhere = "AND S_DEPT IN ("+dept_code+")";*/
				DeptBean dept = OrgMgr.getDept(dept_code);
				String codepath = dept.getCodePath();
				String sql = "select count(*) from "+servId+" a where exists(select dept_code from sy_org_dept b where code_path like concat('"+codepath+"','%') and a.s_dept=b.dept_code and s_flag='1') "+where1;
				ALLNUM = Transaction.getExecutor().count(sql);
				 ALLNUM = Transaction.getExecutor().count(sql);
				 String datasql = "select * from "+servId+" a where exists(select dept_code from sy_org_dept b where code_path like concat('"+codepath+"','%') and a.s_dept=b.dept_code and s_flag='1') "+where1+" limit "+chushi+","+jieshu;
				  list = Transaction.getExecutor().query(datasql);
			}
		//根据审核  机构 匹配当前机构下的所有人
		Bean _PAGE_ = new Bean();
		Bean outBean = new Bean();
		int yeshu = ALLNUM / meiye;
		int yushu = ALLNUM % meiye;
		if (yushu != 0) {
			yeshu += 1;
		}

		
		// 计算页数
		
		// 获取总页数
		
		// 放到Array中
		List<Bean> list2 = new ArrayList<Bean>();
		/*if (ALLNUM == 0) {
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
		}*/
	
		outBean.set("list", list);
		_PAGE_.set("ALLNUM", ALLNUM);
		_PAGE_.set("NOWPAGE", NOWPAGE);
		_PAGE_.set("PAGES", yeshu);
		_PAGE_.set("SHOWNUM", SHOWNUM);
		outBean.set("_PAGE_", _PAGE_);
		 int first=chushi+1;
		 outBean.set("first", first);
		return outBean;
		
	}
	//获取一二级机构
	public OutBean getDept(Bean paramBean){
		OutBean out = new OutBean();
		String user_code = paramBean.getStr("user_code");
		UserBean userBean = UserMgr.getUser(user_code);
		// 获取当前机构;
		DeptBean oneodeptcode1 = userBean.getDeptBean();
		String codePath = oneodeptcode1.getCodePath();
		String[] codepatharr = codePath.split("\\^");
		
		for (int i =0;i< codepatharr.length; i++) {
			// 最后一个 deptcodename
			if(i==0){
				String evname = OrgMgr.getDept(codepatharr[i]).getName();
				out.set("LEVEL0", evname);
			}else if(i==1){
				String evname = OrgMgr.getDept(codepatharr[i]).getName();
				out.set("LEVEL1", evname);
			}
			if(i==codepatharr.length-1){
				out.set("dept_code",OrgMgr.getDept(codepatharr[i]).getCode());
			}
		}
		return out;
	}
	

/**
 * 导出辖内报名
 */
	public OutBean expwithdata(ParamBean paramBean){
		String xmid = paramBean.getStr("xmid");
		ParamBean parr = new ParamBean();
		UserBean userBean1 = Context.getUserBean();
		String user_code1 = "";
		if (userBean1.isEmpty()) {
			return new OutBean().setError("ERROR:user_code 为空");
		} else {
			user_code1 = userBean1.getStr("USER_CODE");
		}
		parr.copyFrom(paramBean);
		parr.setServId("TS_BMSH_PX");
		String servId = paramBean.getServId();
		ServDefBean serv = ServUtils.getServDef(servId);
		long count = 0;
		long times = 0;
		paramBean.setQueryPageShowNum(ONETIME_EXP_NUM); // 设置每页最大导出数据量
		String searchWhere = "";
		beforeExp(paramBean); // 执行监听方法
		/*	String xianei = paramBean.getStr("xianei");*/
			//当前审核人
			UserBean user = Context.getUserBean();
			
			Bean userPvlgToHT = RoleUtil.getPvlgRole(user.getCode(),"TS_BMGL_XNBM");
			Bean userPvlgToHTBean = (Bean) userPvlgToHT.get("TS_BMGL_XNBM_PVLG");
			Bean str = (Bean)userPvlgToHTBean.get("XN_BM");
			String dept_code = str.getStr("ROLE_DCODE");
			if("".equals(dept_code)){
				dept_code=user.getStr("ODEPT_CODE");
			}
			dept_code = dept_code.substring(0,10);
		
			
			List<Bean> dataList;
				if(dept_code.equals("0010100000")){
					//所有人员
					 String datasql = "select * from TS_BMSH_PASS  where xm_id='"+xmid+"'";
					 dataList = Transaction.getExecutor().query(datasql);
					 
				}else{
					/*List<DeptBean> finds = OrgMgr.getChildDepts(compycode, user.getODeptCode());
					for (Bean bean : finds) {
						dept_code+=","+bean.getStr("DEPT_CODE");
					}
					deptwhere = "AND S_DEPT IN ("+dept_code+")";*/
					DeptBean dept = OrgMgr.getDept(dept_code);
					String codepath = dept.getCodePath();
					String sql = "select * from TS_BMSH_PASS a where exists(select dept_code from sy_org_dept b where code_path like concat('"+codepath+"','%') and a.s_dept=b.dept_code and s_flag='1') and xm_id='"+xmid+"'";
					int count2 = Transaction.getExecutor().count(sql);
					if (count2 > EXCEL_MAX_NUM) {
						return new OutBean().setError("导出数据总条数大于Excel最大行数："
								+ EXCEL_MAX_NUM);
					}
					dataList = Transaction.getExecutor().query(sql);
					  
				}
				
				List<Bean> finalList = new ArrayList<Bean>();

				// 判断user_code 是否为空 若为空则 导出所有

				searchWhere = " AND USER_CODE=" + "'" + user_code1 + "' order by cast(PX_XUHAO as SIGNED)";

				// 排序用的 parr存读取th
				parr.setQuerySearchWhere(searchWhere);
				LinkedHashMap<String, Bean> cols = new LinkedHashMap<String, Bean>();
				List<Bean> pxdatalist1 = ServDao.finds("TS_BMSH_PX", searchWhere);
				if (pxdatalist1.size() == 0) {
					String where1 = "AND USER_CODE is null ";
					pxdatalist1 = ServDao.finds("TS_BMSH_PX", where1);
				}
				
				Bean BmIdBean = new Bean();
				BmIdBean.set("SAFE_HTML", "");
				BmIdBean.set("ITEM_LIST_FLAG", "1");
				BmIdBean.set("ITEM_CODE", "BMID");
				BmIdBean.set("EN_JSON", "");
				BmIdBean.set("ITEM_NAME", "报名编码");
				cols.put("BMID", BmIdBean);
				// 查询出所有的 待审核记录
				for (Bean bean : dataList) {
					String work_num = bean.getStr("BM_CODE");
					Bean userBean = getUserInfo1(work_num);
					Bean newBean = new Bean();
					newBean.set("BMID", bean.getStr("BM_ID"));
					// for循环排序bean
					for (Bean pxbean : pxdatalist1) {
						String aa = pxbean.getStr("PX_NAME");
						String namecol = pxbean.getStr("PX_COLUMN");
						String pxcol = namecol;
						Bean colBean = new Bean();

						colBean.set("SAFE_HTML", "");
						colBean.set("ITEM_LIST_FLAG", "1");
						colBean.set("ITEM_CODE", namecol);
						colBean.set("EN_JSON", "");
						colBean.set("ITEM_NAME", aa);
						cols.put(pxcol, colBean);

						// 字段
						// 如果 有值 赋值
						String name = bean.getStr(namecol);
						if (!"".equals(bean.getStr(namecol))) {
							newBean.set(namecol, bean.getStr(namecol));
						}
						if (!"".equals(userBean.getStr(namecol))) {
							newBean.set(namecol, userBean.getStr(namecol));
							name = userBean.getStr(namecol);
						}
						if ("".equals(bean.getStr(namecol))
								&& "".equals(userBean.getStr(namecol))) {
							newBean.set(namecol, "");
						}
						if ("SH_OTHER".equals(namecol)) {
							// 其它办理人
							ParamBean parambeansss = new ParamBean();
							parambeansss.set("codes", bean.getStr("SH_OTHER"));
							Bean outBeans = ServMgr.act("TS_BMSH_STAY", "getusername",
									parambeansss);
							name = outBeans.getStr("usernames");
						}
						if("SH_STATUS".equals(namecol)){
							//审核状态;
							name = "审核通过";
						}
						if ("JOB_LB".equals(namecol)) {
							name = bean.getStr("BM_LB");
						}
						if ("JOB_XL".equals(namecol)) {
							name = bean.getStr("BM_XL");
						}
						if ("TONGYI".equals(namecol)) {
							name = bean.getStr("BM_CODE");
						}
						String BM_TYPE = "";
						if ("BM_TYPE".equals(namecol)) {
							if ("1".equals(bean.getStr("BM_TYPE"))) {
								BM_TYPE = "初级";
							} else if ("2".equals(bean.getStr("BM_TYPE"))) {
								BM_TYPE = "中级";
							} else {
								BM_TYPE = "高级";
							}
							name = BM_TYPE;

						}
						newBean.set(namecol, name);
						newBean.set("_ROWNUM_", "");
						newBean.set("ROWNUM_", "");
					}
					finalList.add(newBean);

				}
				ExportExcel expExcel = new ExportExcel(serv);
				try {
					// 查询出 要导出的数据
					// 总数大于excel可写最大值
					
					// 导出第一次查询数据
					paramBean.setQueryPageNowPage(1); // 导出当前第几页
					// 查询出表头 查询出 对应数据 hashmaplist
					expExcel.createHeader(cols);
					expExcel.appendData1(finalList, paramBean);
					// 存在多页数据
					
					expExcel.addSumRow();
				} catch (Exception e) {
					log.error("导出Excel文件异常" + e.getMessage(), e);
				} finally {
					expExcel.close();
				}
				return new OutBean().setOk();	
				
				
				
	}
	
	/**
	 * 通过excl文件获取试卷相关信息
	 *
	 * @param fileId
	 *            文件id
	 */
	@SuppressWarnings("deprecation")
	public OutBean savedata(Bean paramBean){
		 OutBean outBean = new OutBean();
		 String xmid = paramBean.getStr("xmid");
		 String user_code = Context.getUserBean().getCode();
		  String nodeid = paramBean.getStr("nodeid");
			 String node_name = "";
	        //获取前端传递参数
	        List<Bean> rowBeanList = paramBean.getList("datalist");
	        List<String> codeList = new ArrayList<String>();//避免重复添加数据
	        int level = paramBean.getInt("level");
	        String flag = "";
	      //判断逐级  越级
    		String wherewfs = "AND XM_ID = '"+xmid+"'";
    		List<Bean> finds = ServDao.finds("TS_XMGL_BMSH", wherewfs);
    		for (Bean bean2 : finds) {
    		String wfsid = 	bean2.getStr("WFS_ID");
    		Bean find = ServDao.find("TS_WFS_APPLY", wfsid);
    		flag = find.getStr("WFS_TYPE");
    		String wfswhere = "AND WFS_ID='" + wfsid + "'  ORDER BY NODE_STEPS ASC";
  		List<Bean> finds2 = ServDao.finds("TS_WFS_NODE_APPLY", wfswhere);
  		for (Bean nodebean : finds2) {
  			boolean flagstr = false;
  			// 根据流程id获取 流程绑定的人和审核机构
  			String nodeids = nodebean.getStr("NODE_ID");
  			String nodewhere = "AND NODE_ID='" + nodeids + "'";
  			List<Bean> finds3 = ServDao.finds("TS_WFS_BMSHLC", nodewhere);
  			for (Bean codebean : finds3) {
  				if (user_code.equals(codebean.getStr("SHR_USERCODE"))) {
  					node_name= nodebean.getStr("NODE_NAME");
  					flagstr = true;
  					break;
  				}
  				
  			}
  			if(flagstr){
  				break;
  			}
  		}
        }
    		
	        List<Bean> beans = new ArrayList<Bean>();
	        List<Bean> bmbeans = new ArrayList<Bean>();
	        List<Bean> mindbeans = new ArrayList<Bean>();
	        for (Bean rowBean : rowBeanList) {
	        	
	            String colCode = rowBean.getStr(ImpUtils.COL_NAME + "1");
	            if("".equals(colCode)){
	            	 rowBean.set(ImpUtils.ERROR_NAME, "编码错误，没有此报名数据");
		                continue;
	            }
	            
	            Bean bmbean = ServDao.find("TS_BMLB_BM", colCode);
	            if (bmbean == null) {
	                rowBean.set(ImpUtils.ERROR_NAME, "编码错误，没有此报名数据");
	                continue;
	            }
	            String colCode2 = rowBean.getStr(ImpUtils.COL_NAME + "2");
	            if(colCode2.equals("")){
	            	 rowBean.set(ImpUtils.ERROR_NAME, "审核理由不能为空");
		                continue;
	            }
        	 if(level!=1){
 	        	if("1".equals(flag)){
 	        		   rowBean.set(ImpUtils.ERROR_NAME, "逐级审核下级不能导入最终结果");
 		                continue;
 	      		}
 	        	 }
	            //报名人所在机构
	            String  bmpath = OrgMgr.getDept(bmbean.getStr("S_DEPT")).getCodePath();
	            if (codeList.contains(colCode)) {
	                //已包含 continue ：避免重复添加数据
	                rowBean.set(ImpUtils.ERROR_NAME, "EXCEL重复数据：" + colCode);
	                continue;
	            }
	            //判断是否是此人的辖内数据
	            String sql = "select * from (select b.* from ts_xmgl_bmsh a left join ts_wfs_bmshlc b on  a.wfs_id = b.wfs_id and a.xm_id = '"+xmid+"')c where c.shr_usercode='"+user_code+"'";
	            List<Bean> shrlist = Transaction.getExecutor().query(sql);
	            String odeptcodes = "";
	            if(shrlist!=null&&shrlist.size()!=0){
	            	for (Bean bean : shrlist) {
	            		if(!"".equals(bean.getStr("DEPT_CODE"))){
	            			odeptcodes+=bean.getStr("DEPT_CODE")+",";
	            		}
					}
	            }else{
	            	 rowBean.set(ImpUtils.ERROR_NAME, "审核人不存在");
		                continue;
	            }
	            int pathflag = 0;
	           if(!"".equals(odeptcodes)){
	        	   String[] split = odeptcodes.split(",");
	        	   if(split.length!=0){
	        		  for (String string : split) {
	        			  String codepath = OrgMgr.getDept(string).getCodePath();
	        			  if(!"".equals(codepath)){
	        				  if(bmpath.indexOf(codepath)!=-1){
	        					  //包含此人的权限
	        					  pathflag++;
	        				  }
	        			  }
					}
	        	   }else{
	        		   rowBean.set(ImpUtils.ERROR_NAME, "没有审核机构，超出审核权限");
		                continue;
	        	   }
	           }
	           if(pathflag==0){
	        	   rowBean.set(ImpUtils.ERROR_NAME, "超出审核权限，此人不再审核范围内");
	                continue;
	           }
	           
	            //将审核不通过的数据   导入到通过表中   
	            List<Bean> passshlist = ServDao.finds("TS_BMSH_PASS", " and BM_ID = '"+colCode+"'");
	            List<Bean> shlist = ServDao.finds("TS_BMSH_STAY", " and BM_ID = '"+colCode+"'");
	            List<Bean> nopassshlist = ServDao.finds("TS_BMSH_NOPASS", " and BM_ID = '"+colCode+"'");
	            if (passshlist != null&&passshlist.size()!=0) {
	                rowBean.set(ImpUtils.ERROR_NAME, "重复数据，报名数据已通过，不需导入");
	                continue;
	            }
	            if (shlist != null&&shlist.size()!=0) {
	            	//判断审核层级
	            	if(level>shlist.get(0).getInt("SH_LEVEL")){
	            		rowBean.set(ImpUtils.ERROR_NAME, "上级已审核 没有权限审核此数据");
	            		continue;
	            	}else{

		            	Bean bean = shlist.get(0);
		            	
		            	//判断审核层级
		            	if(level>shlist.get(0).getInt("SH_LEVEL")){
		            		rowBean.set(ImpUtils.ERROR_NAME, "上级已审核 没有权限审核此数据");
		            		continue;
		            	}else{
		            		ParamBean parambean = new ParamBean();
		    				parambean.set("examerUserCode", bean.getStr("BM_CODE"));
		    				parambean.set("level", level);
		    				parambean.set("deptCode", bean.getStr("S_DEPT"));
		    				parambean.set("odeptCode", bean.getStr("S_ODEPT"));
		    				parambean.set("shrUserCode", user_code);
		    				parambean.set("flowName", "1");
		    				parambean.set("xmId", xmid);
		    				OutBean outbean = ServMgr.act("TS_WFS_APPLY", "backFlow",
		    						parambean);
								//越级  所有人可见   逐级  一级审核人 可以直接导入最终结果 
								ServDao.delete("TS_BMSH_STAY", bean.getId());
								String allman1 = "";
								String blist1 = outbean.getStr("result");
								if(!"".equals(blist1)){
									allman1= blist1.substring(0,blist1.length()-1);
								}
								Bean newBean = new Bean();
								bean.remove("SH_ID");
								bean.remove("S_CMPY");
								bean.remove("S_ATIME");
								bean.remove("S_MTIME");
								bean.remove("S_FLAG");
								bean.remove("_PK_");
								bean.remove("ROW_NUM_");
								newBean.copyFrom(bean);
								newBean.set("SH_USER", user_code);
								newBean.set("SH_LEVEL", level);
								newBean.set("SH_OTHER", allman1);
										bmbean.set("BM_SH_STATE", "1");
									Bean mindbean = new Bean();
									mindbean.set("SH_LEVEL", node_name);
									mindbean.set("SH_MIND", colCode2);
									mindbean.set("DATA_ID", bean.getStr("BM_ID"));
									mindbean.set("SH_STATUS", "1");
									mindbean.set("SH_UNAME", Context.getUserBean().getName());
									mindbean.set("SH_UCODE", user_code);
									mindbean.set("SH_TYPE", 1);
									mindbean.set("SH_NODE", nodeid);
									mindbeans.add(mindbean);
									bmbeans.add(bmbean);
									codeList.add(bean.getStr("BM_ID"));
									beans.add(newBean);
									ServDao.save("TS_BMLB_BM", bmbean);
		            	}	
	            	}
	            }else if (nopassshlist != null&&nopassshlist.size()!=0) {
	            	//判断审核层级
	            	Bean bean = nopassshlist.get(0);
	            	if(level>nopassshlist.get(0).getInt("SH_LEVEL")){
	            		rowBean.set(ImpUtils.ERROR_NAME, "上级已审核 没有权限审核此数据");
	            		continue;
	            	}else{
	            		//将审核通过的数据导入到不通过的表中
	            		ServDao.delete("TS_BMSH_NOPASS", bean.getId());
	            		Bean newBean = new Bean();
						newBean.copyFrom(bean);
						newBean.set("SH_USER", user_code);
						newBean.set("SH_LEVEL", level);
							//异议过的数据不能再进行异议
							bmbean.set("BM_SH_STATE", "1");
							Bean mindbean = new Bean();
							mindbean.set("SH_LEVEL", node_name);
							mindbean.set("SH_MIND", colCode2);
							mindbean.set("DATA_ID", bean.getStr("BM_ID"));
							mindbean.set("SH_STATUS", "1");
							mindbean.set("SH_UNAME", Context.getUserBean().getName());
							mindbean.set("SH_UCODE", user_code);
							mindbean.set("SH_TYPE", 1);
							mindbean.set("SH_NODE", nodeid);
							mindbeans.add(mindbean);
							bmbeans.add(bmbean);
							codeList.add(bean.getStr("BM_ID"));
							beans.add(newBean);
							 ServDao.save("TS_BMLB_BM", bmbean);
	            	}
	            }else{
	            	//没有 报名 数据
	            	rowBean.set(ImpUtils.ERROR_NAME, "审核模块中没有此报名数据");
	            }
	        }
	        ServDao.creates("TS_BMSH_PASS", beans);
	        ServDao.creates("TS_COMM_MIND", mindbeans);
	           
	        
	        return outBean.set("alllist", rowBeanList).set("successlist", codeList);
	}
	public OutBean saveExternalData(Bean paramBean){
		String xmid = paramBean.getStr("xmid");
		 OutBean outBean = new OutBean();
		Bean xmbean = ServDao.find("TS_XMGL", xmid);
		 String user_code = Context.getUserBean().getCode();
		  String nodeid = paramBean.getStr("nodeid");
		  String node_name = "";
	        //获取前端传递参数
	        List<Bean> rowBeanList = paramBean.getList("datalist");
	        List<String> codeList = new ArrayList<String>();//避免重复添加数据
	        String flag = "";
	      //判断逐级  越级
  		String wherewfs = "AND XM_ID = '"+xmid+"'";
  		List<Bean> finds = ServDao.finds("TS_XMGL_BMSH", wherewfs);
  		for (Bean bean2 : finds) {
  		String wfsid = 	bean2.getStr("WFS_ID");
  		Bean find = ServDao.find("TS_WFS_APPLY", wfsid);
  		flag = find.getStr("WFS_TYPE");
  		String wfswhere = "AND WFS_ID='" + wfsid + "'  ORDER BY NODE_STEPS ASC";
		List<Bean> finds2 = ServDao.finds("TS_WFS_NODE_APPLY", wfswhere);
		for (Bean nodebean : finds2) {
			boolean flagstr = false;
			// 根据流程id获取 流程绑定的人和审核机构
			String nodeids = nodebean.getStr("NODE_ID");
			String nodewhere = "AND NODE_ID='" + nodeids + "'";
			List<Bean> finds3 = ServDao.finds("TS_WFS_BMSHLC", nodewhere);
			for (Bean codebean : finds3) {
				if (user_code.equals(codebean.getStr("SHR_USERCODE"))) {
					node_name= nodebean.getStr("NODE_NAME");
					flagstr = true;
					break;
				}
				
			}
			if(flagstr){
				break;
			}
		}
      }
	        List<Bean> beans = new ArrayList<Bean>();
	        List<Bean> BMbeans = new ArrayList<Bean>();
	        List<Bean> mindbeans = new ArrayList<Bean>();
	        for (Bean rowBean : rowBeanList) {
	        	UserBean userBean=null;
	            String colCode = rowBean.getStr(ImpUtils.COL_NAME + "1");
	            if("".equals(colCode)){
	            	 rowBean.set(ImpUtils.ERROR_NAME, "编码错误，用户不存在");
		                continue;
	            }else{
	            	  userBean = ImpUtils.getUserBeanByString(colCode);
	                 if (userBean == null) {
	                     rowBean.set(ImpUtils.ERROR_NAME, "找不到用户");
	                     continue;
	                 }
	            }
	            String colCode2 = rowBean.getStr(ImpUtils.COL_NAME + "2");
	            if("".equals(colCode2)){
	            	  rowBean.set(ImpUtils.ERROR_NAME, "报名类别为空");
		                continue;
	            }
	            String colCode3 = rowBean.getStr(ImpUtils.COL_NAME + "3");
	            if("".equals(colCode3)){
	            	  rowBean.set(ImpUtils.ERROR_NAME, "报名序列为空");
		                continue;
	            }
	            String colCode4 = rowBean.getStr(ImpUtils.COL_NAME + "4");
	            if("".equals(colCode4)){
	            	  rowBean.set(ImpUtils.ERROR_NAME, "报名模块为空");
		                continue;
	            }
	            String colCode5 = rowBean.getStr(ImpUtils.COL_NAME + "5");
	            if("".equals(colCode5)){
	            	  rowBean.set(ImpUtils.ERROR_NAME, "报名等级为空");
		                continue;
	            }
	            String colCode6 = rowBean.getStr(ImpUtils.COL_NAME + "6");
	            if("".equals(colCode6)){
	            	  rowBean.set(ImpUtils.ERROR_NAME, "导入理由为空");
		                continue;
	            }
	            String[] arr = new String[2];
    			arr[0]="0";
    			arr[1]="1";
	            SqlBean bean = new SqlBean();
	            bean.and("XM_ID", xmid);
	            bean.and("BM_CODE",userBean.getCode());
	            bean.and("BM_LB", colCode2);
	            bean.and("BM_XL", colCode3);
	            bean.and("BM_MK", colCode4);
	            bean.and("BM_STATE", "1");
	            bean.and("BM_SH_STATE", arr);
	            bean.and("BM_TYPE_NAME", colCode5);
	            Bean kslbean = new Bean();
	            kslbean.set("XM_ID", xmid);
	            kslbean.set("KSLB_NAME",colCode2);
	            kslbean.set("KSLB_XL", colCode3);
	            kslbean.set("KSLB_MK", colCode4);
	            kslbean.set("KSLB_TYPE_NAME", colCode5);
	            List<Bean> kslbbean = ServDao.finds("TS_XMGL_BM_KSLB", kslbean);
	            if (kslbbean.size()> 0) {
	            	//考试类别中有此考试
	            	if (ServDao.count("TS_BMLB_BM", bean) <= 0) {
	            		//没有报名此考试  增加到报名审核通过表中
	            		
	            		int allnum = 0;
	            		//高级考试只能报一个 
	            		SqlBean newbean1 = new SqlBean();
            			newbean1.and("XM_ID", xmid);
            			newbean1.and("BM_CODE",userBean.getCode());
            			newbean1.and("BM_STATE", "1");
            			if("高级".equals(colCode5)){
            				newbean1.set("BM_TYPE", "3");
            				allnum = 1;
            			}else if("中级".equals(colCode5)){
            				newbean1.set("BM_TYPE", "2");
            				List<Bean> CONFLIST = ServDao.finds("SY_COMM_CONFIG", " AND CONF_KEY='TS_BM_MIDDLE_MAXNUM'");
    	            		if(CONFLIST!=null&&CONFLIST.size()!=0){
    	            			allnum = CONFLIST.get(0).getInt("CONF_VALUE");
    	            		}
            			}else{
            				Bean xmnumbean = ServDao.find("TS_XMGL", xmid);
            				allnum=xmnumbean.getInt("XM_LBNUM");
            				if(allnum==0){
            					allnum = 10000;
            				}
            			}
            			newbean1.andIn("BM_SH_STATE", arr);
	            		if (ServDao.count("TS_BMLB_BM", newbean1) <allnum) {
	            		//超过最大报名数 
	            		Bean shBean = new Bean();
	            		String bmid =  Lang.getUUID();
	            		Bean bmbean = new Bean();
	            		List<Bean> bminfo = ServDao.finds("TS_XMGL_BMGL", " AND XM_ID = '"+xmid+"'");
	            		if(bminfo!=null&&bminfo.size()!=0){
	            			bmbean.set("BM_STARTDATE", bminfo.get(0).getStr("BM_START"));
							bmbean.set("BM_ENDDATE", bminfo.get(0).getStr("BM_END"));	
	            		}
	            		bmbean.set("BM_ID", bmid);
	            		bmbean.set("BM_YIYI_STATE", "2");
	            		bmbean.set("BM_CODE", userBean.getCode());
	            		bmbean.set("BM_NAME", userBean.getName());
	            		bmbean.set("BM_SEX", userBean.getSex());
	            		bmbean.set("ODEPT_NAME", userBean.getODeptName());
						bmbean.set("BM_OFFICE_PHONE", userBean.getOfficePhone());
						bmbean.set("BM_PHONE", userBean.getMobile());
						bmbean.set("BM_LB", colCode2);
						bmbean.set("BM_XL", colCode3);
						bmbean.set("BM_MK", colCode4);
						bmbean.set("BM_TYPE", kslbbean.get(0).getStr("KSLB_TYPE"));
						bmbean.set("BM_LB_CODE",  kslbbean.get(0).getStr("KSLB_CODE"));
						bmbean.set("BM_XL_CODE",  kslbbean.get(0).getStr("KSLB_XL_CODE"));
						bmbean.set("BM_MK_CODE",  kslbbean.get(0).getStr("KSLB_MK_CODE"));
						bmbean.set("BM_TYPE_NAME", colCode5);
						bmbean.set("XM_ID", xmid);
						bmbean.set("BM_TITLE", xmbean.getStr("XM_NAME"));
						bmbean.set("KSLBK_ID", kslbbean.get(0).getStr("KSLBK_ID"));
						bmbean.set("S_ODEPT", userBean.getODeptCode());
						bmbean.set("S_DEPT", userBean.getDeptCode());
						bmbean.set("S_TDEPT", userBean.getTDeptCode());
						bmbean.set("BM_SH_STATE", "1");
						BMbeans.add(bmbean);
						shBean.set("BM_TITLE", xmbean.getStr("XM_NAME"));
						shBean.set("XM_ID", xmid);
						shBean.set("ODEPT_CODE", userBean.getODeptCode());
						shBean.set("BM_ID", bmid);
						shBean.set("BM_NAME", userBean.getName());
						shBean.set("BM_CODE", userBean.getCode());
						shBean.set("KSLBK_ID", kslbbean.get(0).getStr("KSLBK_ID"));
						shBean.set("BM_LB", colCode2);
						shBean.set("BM_XL", colCode3);
						shBean.set("BM_MK", colCode4);
						shBean.set("BM_TYPE",  kslbbean.get(0).getStr("KSLB_TYPE"));
						shBean.set("BM_LB_CODE",  kslbbean.get(0).getStr("KSLB_CODE"));
						shBean.set("BM_XL_CODE",  kslbbean.get(0).getStr("KSLB_XL_CODE"));
						shBean.set("BM_MK_CODE",  kslbbean.get(0).getStr("KSLB_MK_CODE"));
						shBean.set("BM_TYPE_NAME", colCode5);
						shBean.set("SH_NODE", node_name);// 目前审核节点
						shBean.set("SH_USER", user_code);// 当前办理人
						shBean.set("SH_LEVEL", "1");
						shBean.set("S_ODEPT",userBean.getODeptCode());
						shBean.set("S_TDEPT", userBean.getTDeptCode());
						shBean.set("S_DEPT", userBean.getDeptCode());
						shBean.set("BM_KS_TIME",  kslbbean.get(0).getStr("KSLB_TIME"));
						shBean.set("BM_STATUS", 0);
						Bean mindbean = new Bean();
						mindbean.set("SH_LEVEL", node_name);
						mindbean.set("SH_MIND", colCode6);
						mindbean.set("DATA_ID", bmid);
						mindbean.set("SH_STATUS", "1");
						mindbean.set("SH_UNAME", Context.getUserBean().getName());
						mindbean.set("SH_UCODE", user_code);
						mindbean.set("SH_TYPE", 1);
						mindbean.set("SH_NODE", nodeid);
						mindbeans.add(mindbean);
						codeList.add(bmid);
						beans.add(shBean);
	            		
	            		}else{
	            			if("高级".equals(colCode5)){
	            				rowBean.set(ImpUtils.ERROR_NAME, "高级报名数限制");
	            			}else if("中级".equals(colCode5)){
	            				rowBean.set(ImpUtils.ERROR_NAME, "中级报名数限制");
	            			}else{
	            				rowBean.set(ImpUtils.ERROR_NAME, "最大报名数限制");
	            			}
		            		continue;
	            		}
	            	}else{
	            		rowBean.set(ImpUtils.ERROR_NAME, "已报名此考试，重复数据，请用另外的导入功能导入");
	            		continue;
	            	}
	            } else {
	                rowBean.set(ImpUtils.ERROR_NAME, "此项目没有此考试");
	                continue;
	            }
	            
	        }
	        ServDao.creates("TS_BMSH_PASS", beans);
	        ServDao.creates("TS_COMM_MIND", mindbeans);
	        ServDao.creates("TS_BMLB_BM", BMbeans);
	        return outBean.set("alllist", rowBeanList).set("successlist", codeList);
		  
	}
	/**
	 * 从excel文件中读取审核数据，并保存
	 *
	 * @param paramBean
	 *            paramBean
	 * @return outBean
	 */
	public OutBean saveFromExcel(ParamBean paramBean) {
		String impexter = paramBean.getStr("impexter");
		String fileId = paramBean.getStr("FILE_ID");
		if(("4").equals(impexter)){
			//方法入口
			paramBean.set("SERVMETHOD", "saveExternalData");
			OutBean out =ImpUtils.getDataFromXls(fileId,paramBean);
			String failnum = out.getStr("failernum");
			String successnum = out.getStr("oknum");
			//返回导入结果
			return new OutBean().set("FILE_ID",out.getStr("fileid")).set("_MSG_", "导入成功："+successnum+"条， 导入失败："+failnum+"条");
		}else{
			//方法入口
			paramBean.set("SERVMETHOD", "savedata");
			OutBean out =ImpUtils.getDataFromXls(fileId,paramBean);
			String failnum = out.getStr("failernum");
			String successnum = out.getStr("oknum");
			//返回导入结果
			return new OutBean().set("FILE_ID",out.getStr("fileid")).set("_MSG_", "导入成功："+successnum+"条， 导入失败："+failnum+"条");
		}
  }
	
}
