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
import com.rh.core.serv.util.ExportExcel;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.ImpUtils;
import com.rh.ts.util.RoleUtil;

/**
 * 没有通过的审核
 * 
 * @author shiyun
 *
 */
public class NoPassServ extends CommonServ {
	/** 每次获取数据条数 */
	private static final int ONETIME_EXP_NUM = 5000;
	/** excel最大行数 */
	private static final int EXCEL_MAX_NUM = 65536;

	/**
	 * 获取那一页的记录 返回
	 * 
	 * @param paramBean
	 * @return
	 */
	public Bean getUncheckList(Bean paramBean) {
		String usercode = Context.getUserBean().getCode();
		//查询当前审核人的流程 绑定的审核机构
		String xmid= paramBean.getStr("xmid");
		Bean _PAGE_ = new Bean();
		Bean outBean = new Bean();
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
			if(!"".equals(bean.getId())){}
			//dept_code
			dept_code=bean.getId();
			String[] split = dept_code.split(",");
			for (String string : split) {
				deptcodes+="'"+string+"',";
			}
		}
		
		List<Bean> list = new ArrayList<Bean>();
		if(deptcodes.length()>5){
			deptcodes = deptcodes.substring(0,deptcodes.length()-1);
	String sql1 = "select distinct code_path from sy_org_dept where dept_level in(select min(dept_level) from sy_org_dept where dept_code in ("+deptcodes+")) and dept_code in("+deptcodes+")";
			List<Bean> query1 = Transaction.getExecutor().query(sql1);
			String sql3 = "";
			sql3 += "select * from (select a.*,b.code_path from ts_bmsh_nopass a left join sy_org_dept b on a.s_dept=b.dept_code where 1=1 "+where1+") c ";
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
	 * 获取项目下所有审核不通过的数据
	 * 
	 * @param paramBean
	 * @return
	 */
	public Bean getAllData(Bean paramBean) {
		String xmid = paramBean.getStr("xmid");
		Bean outBean = new Bean();
		String servId = "TS_BMSH_NOPASS";
		String where1 = paramBean.getStr("where");
		List<Bean> list1 = ServDao.finds(servId, where1);
		String user_code = paramBean.getStr("user_code");
		List<Bean> list = new ArrayList<Bean>();
		for (Bean bean : list1) {
			String other = bean.getStr("SH_OTHER");
			if (other.contains(user_code)) {
				list.add(bean);
			}
		}

		if (list.size() == 0) {
			return new OutBean();
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
		parambean.set("examerUserCode", list.get(0).getStr("BM_CODE"));
		parambean.set("deptCode", list.get(0).getStr("S_DEPT"));
		parambean.set("odeptCode", list.get(0).getStr("S_ODEPT"));
		parambean.set("level", 0);
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
	public OutBean update(Bean paramBean) {
		String shenuser = "";
		UserBean userBean = Context.getUserBean();
		if (userBean.isEmpty()) {
			return new OutBean().setError("ERROR:user_code 为空");
		} else {
			shenuser = userBean.getStr("USER_CODE");
		}
		//节点名称
		String node_name="";
		String nodeid = paramBean.getStr("nodeid");
		String levels = paramBean.getStr("level");
		int level = 0;
		if (!"".equals(levels)) {

			level = Integer.parseInt(levels);
		}
		String s = paramBean.getStr("checkedid");
		String xmid = paramBean.getStr("xmid");
		//审核流程类型
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
		String liyou = paramBean.getStr("liyou");
		// 继续进行审核

		for (String id : ss) {
			if (!"".equals(id)) {
				// 获取下级参数
				Bean bean = ServDao.find("TS_BMSH_NOPASS", id);
				String bmid = bean.getStr("BM_ID");
				bean.remove("SH_ID");
				bean.remove("S_CMPY");
				bean.remove("S_ATIME");
				bean.remove("S_MTIME");
				bean.remove("S_FLAG");
				bean.remove("_PK_");
				bean.remove("ROW_NUM_");
				Bean newBean = new Bean();
				newBean.copyFrom(bean);
				newBean.set("SH_LEVEL", level);
				ServDao.save("TS_BMSH_PASS", newBean);
				
				// 修改报名状态
				Bean bm_bean = ServDao.find("TS_BMLB_BM", bmid);
				if (bm_bean != null) {
					bm_bean.set("BM_SH_STATE", "1");
					ServDao.save("TS_BMLB_BM",bm_bean);
				}
				ServDao.delete("TS_BMSH_NOPASS", id);
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
				mindbean.set("SH_STATUS", 1);
				mindbean.set("SH_ULOGIN",localip);
				mindbean.set("SH_UNAME", userBean.getName());
				mindbean.set("SH_UCODE", shenuser);
				mindbean.set("SH_TYPE", 1);
				mindbean.set("SH_NODE", nodeid);
				ServDao.save("TS_COMM_MIND", mindbean);
			}
		}
		return new OutBean().setOk();
	}

	/**
	 * 导出事获取 项目下的数据逗号分隔 返回 字符串 导出所有
	 */
	public Bean reSids(Bean paramBean) {
		Bean outBean = new Bean();
		String servId = paramBean.getStr("servId");
		String xmid = paramBean.getStr("xmid");
		String where = "AND XM_ID=" + "'" + xmid + "'";
		List<Bean> list = ServDao.finds(servId, where);
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
	 * 提交异议
	 */
	public Bean yiyi(Bean paramBean) {
		String liyou = paramBean.getStr("liyou");
		String bmid = paramBean.getStr("bmid");
		// 将异议状态改为1 已提交
		// 将提交理由保存到 此表 ts_bmlb_bm
		String where1 = "AND BM_ID=" + "'" + bmid + "'";
		List<Bean> bmlist = ServDao.finds("TS_BMLB_BM", where1);
		Bean bmbean = bmlist.get(0);
		bmbean.set("BM_YIYI_STATE", 1);
		bmbean.set("BM_SS_REASON", liyou);
		bmbean.set("BM_SH_STATE", 0);
		ServDao.save("TS_BMLB_BM", bmbean);
		String shenuser = "";
		UserBean userBean = Context.getUserBean();
		if (userBean.isEmpty()) {
			return new OutBean().setError("ERROR:user_code 为空");
		} else {
			shenuser = userBean.getStr("USER_CODE");
		}
		String where = "AND BM_ID=" + "'" + bmid + "'";
		List<Bean> list = ServDao.finds("TS_BMSH_NOPASS", where);
		ServDao.delete("TS_BMSH_NOPASS", where);
		// 继续走审核流程
		if (list.size() != 0) {
			Bean bean = list.get(0);
			String xmid = bean.getStr("XM_ID");
			String bm_code = bean.getStr("BM_CODE");
			ParamBean parambean = new ParamBean();
			parambean.set("examerUserCode", bm_code);
			parambean.set("level", 0);
			parambean.set("shrUserCode", shenuser);
			parambean.set("flowName", 1);
			parambean.set("xmId", xmid);
			parambean.set("deptCode", bean.getStr("S_DEPT"));
			parambean.set("odeptCode", bean.getStr("S_ODEPT"));
			OutBean outbean = ServMgr
					.act("TS_WFS_APPLY", "backFlow", parambean);
			String allman = "";
			String blist = outbean.getStr("result");

			if(!"".equals(blist)){
				allman= blist.substring(0,blist.length()-1);
			}
			bean.remove("SH_ID");
			bean.remove("S_CMPY");
			bean.remove("S_ATIME");
			bean.remove("S_MTIME");
			bean.remove("S_FLAG");
			bean.remove("_PK_");
			bean.remove("ROW_NUM_");
			Bean newBean = new Bean();
			newBean.copyFrom(bean);
			
				Bean newBean1 = new Bean();
				newBean1.copyFrom(bean);
				newBean1.set("SH_USER", allman);
				newBean1.set("SH_OTHER", allman);
				newBean1.set("SH_LEVEL", outbean.getStr("SH_LEVEL"));
				newBean1.set("BM_YIYI", bmid);
				newBean1.set("BM_STATUS", 0);
				ServDao.save("TS_BMSH_STAY", newBean1);
			
			// 添加一个字段用来标识异议 显示图标按钮
		}
		return new OutBean().setOk();
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
		
		String xmid = paramBean.getStr("xmid");
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
						sql3 += "select * from (select a.*,b.code_path from ts_bmsh_nopass a left join sy_org_dept b on a.s_dept=b.dept_code where xm_id = '"+xmid+"') c ";
						for (int i=0;i<query1.size();i++) {
							//判断哪些考生部门 在此codepath 下
							if(i==0){
								sql3+="where c.code_path like concat('"+query1.get(i).getId()+"','%')";
							}else{
								sql3+=" or c.code_path like concat('"+query1.get(i).getId()+"','%')";
							}
						}
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
					name = "审核未通过";
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
			if (count > EXCEL_MAX_NUM) {
				return new OutBean().setError("导出数据总条数大于Excel最大行数："
						+ EXCEL_MAX_NUM);
			}
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
					expExcel.appendData(out.getDataList(), paramBean);
				}
			}*/
			expExcel.addSumRow();
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
		String deptwhere = "";
		/*if("belong".equals(xianei)){
		String belongdeptcode = "";
		String xmid = paramBean.getStr("xmid");
		//根据项目id找到流程下的所有节点
		String belongwhere = "AND XM_ID='"+xmid+"'";
		List<Bean> finds = ServDao.finds("TS_XMGL_BMSH", belongwhere);
		String deptcodes = "";
		if(finds.size()!=0){
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
								String id = deptbean.getId();
								deptcodes+=id+",";
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
			//管理员以下的所有机构部门
		String servId = "TS_BMSH_NOPASS";
		String NOWPAGE = paramBean.getStr("nowpage");
		String SHOWNUM = paramBean.getStr("shownum");
		String where1 = paramBean.getStr("where")+deptwhere;
		
		int ALLNUM = 0;
		int meiye = Integer.parseInt(SHOWNUM);

		int nowpage = Integer.parseInt(NOWPAGE);
		int showpage = Integer.parseInt(SHOWNUM);
		// 计算第一项 开始
		int chushi = (nowpage - 1) * showpage ;
		// 计算结束项
		int jieshu = (nowpage - 1) * showpage + showpage;
		List<Bean> list;
			if(dept_code.equals("0010100000")){
				//所有人员
				String sql = "select * from TS_BMSH_NOPASS where 1=1 "+where1;
				 ALLNUM = Transaction.getExecutor().count(sql);
				 if(jieshu>ALLNUM){
					 showpage=ALLNUM-chushi;
				 }
				 String datasql = "select * from TS_BMSH_nopass where 1=1 "+where1 +" limit "+chushi+","+showpage;
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
		// 放到Array中
		
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
			log.error(userPvlgToHTBean);
			Bean str = (Bean)userPvlgToHTBean.get("XN_BM");
			log.error(str);
			String dept_code = str.getStr("ROLE_DCODE");
			if("".equals(dept_code)){
				dept_code=user.getStr("ODEPT_CODE");
			}
			log.error(dept_code);
			dept_code = dept_code.substring(0,10);
		
			
			List<Bean> dataList;
				if(dept_code.equals("0010100000")){
					//所有人员
					 String datasql = "select * from TS_BMSH_NOPASS where xm_id='"+xmid+"'";
					 dataList = Transaction.getExecutor().query(datasql);
					 
				}else{
					/*List<DeptBean> finds = OrgMgr.getChildDepts(compycode, user.getODeptCode());
					for (Bean bean : finds) {
						dept_code+=","+bean.getStr("DEPT_CODE");
					}
					deptwhere = "AND S_DEPT IN ("+dept_code+")";*/
					DeptBean dept = OrgMgr.getDept(dept_code);
					String codepath = dept.getCodePath();
					String sql = "select *from TS_BMSH_NOPASS a where exists(select dept_code from sy_org_dept b where code_path like concat('"+codepath+"','%') and a.s_dept=b.dept_code and s_flag='1') and xm_id='"+xmid+"'";
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
							name = "审核未通过";
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
							expExcel.appendData(out.getDataList(), paramBean);
						}
					}*/
					expExcel.addSumRow();
				} catch (Exception e) {
					log.error("导出Excel文件异常" + e.getMessage(), e);
				} finally {
					expExcel.close();
				}
				return new OutBean().setOk();	
	}
	/**
	 * 处理数据
	 *
	 * @param fileId
	 *            文件id
	 */
	public OutBean savedata(Bean paramBean){
		 OutBean outBean = new OutBean();
	        //获取前端传递参数
	        //*获取文件内容
	        List<Bean> rowBeanList = paramBean.getList("datalist");
	        List<String> codeList = new ArrayList<String>();//避免重复添加数据

	        List<Bean> beans = new ArrayList<Bean>();
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

	            if (codeList.contains(colCode)) {
	                //已包含 continue ：避免重复添加数据
	                rowBean.set(ImpUtils.ERROR_NAME, "EXCEL重复数据：" + colCode);
	                continue;
	            }

	          //将审核通过的数据   导入到不通过表中   

	            List<Bean> shlist = ServDao.finds("TS_BMSH_NOPASS", " and BM_ID = '"+colCode+"'");
	            if (shlist != null&&shlist.size()!=0) {
	                rowBean.set(ImpUtils.ERROR_NAME, "重复数据，报名数据已通过，不需导入");
	                continue;
	            }
	            //将审核通过的数据导入到不通过表中       
		           List<Bean> passlist = ServDao.finds("TS_BMSH_NOPASS", " and BM_ID = '"+colCode+"'");
		           List<Bean> staylist = ServDao.finds("TS_BMSH_STAY", " and BM_ID = '"+colCode+"'");
		           if(passlist!=null&&passlist.size()!=0){
		        	   Bean passbean = passlist.get(0);
		        	   //删除通过数据   或待审核数据
		        	   Bean whereBean = new Bean();
		        	   whereBean.set("_WHERE_"," and BM_ID = '"+colCode+"'");
		        	   ServDao.delete("TS_BMSH_PASS", whereBean);
		        	   ServDao.delete("TS_BMSH_STAY", whereBean);
		        	   //复制审核通过bean  到审核不通过中
		        	   passbean.remove("SH_ID");
		        	   passbean.remove("S_CMPY");
		        	   passbean.remove("S_ATIME");
		        	   passbean.remove("S_MTIME");
		        	   passbean.remove("S_FLAG");
		        	   passbean.remove("_PK_");
		        	   passbean.remove("ROW_NUM_");
						Bean newBean = new Bean();
						newBean.copyFrom(passbean);
						beans.add(newBean);
						codeList.add(colCode);
						//更新报名状态
						bmbean.set("BM_SH_STATE", "2");
						ServDao.save("TS_BMLB_BM", bmbean);
		           }else if(staylist!=null&&staylist.size()!=0){
		        	   //审核不通过中没有报名数据   
		        	   //去待审核中查找 
		        	   Bean staybean = staylist.get(0);
		        	   //删除不通过数据   或待审核数据
		        	   Bean whereBean = new Bean();
		        	   whereBean.set("_WHERE_"," and BM_ID = '"+colCode+"'");
		        	   ServDao.delete("TS_BMSH_PASS", whereBean);
		        	   ServDao.delete("TS_BMSH_STAY", whereBean);
		        	   //复制审核不同通过bean  到审核通过中
		        	   staybean.remove("SH_ID");
		        	   staybean.remove("S_CMPY");
		        	   staybean.remove("S_ATIME");
		        	   staybean.remove("S_MTIME");
		        	   staybean.remove("S_FLAG");
		        	   staybean.remove("_PK_");
		        	   staybean.remove("ROW_NUM_");
						Bean newBean = new Bean();
						newBean.copyFrom(staybean);
						beans.add(newBean);
						codeList.add(colCode);
						//更新报名状态
						bmbean.set("BM_SH_STATE", "2");
						ServDao.save("TS_BMLB_BM", bmbean);
		           }else{
		        	   //没有 报名 数据
		        	   rowBean.set(ImpUtils.ERROR_NAME, "审核模块中没有此报名数据");
		           }
	        }
	        ServDao.creates("TS_BMSH_NOPASS", beans);
	        
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
		String fileId = paramBean.getStr("FILE_ID");
   	 //方法入口
   	  paramBean.set("SERVMETHOD", "savedata");
   	 OutBean out =ImpUtils.getDataFromXls(fileId,paramBean);
     String failnum = out.getStr("failernum");
     String successnum = out.getStr("oknum");
     //返回导入结果
     return new OutBean().set("FILE_ID",out.getStr("fileid")).set("_MSG_", "导入成功："+successnum+"条， 导入失败："+failnum+"条");
  }

	
	
}