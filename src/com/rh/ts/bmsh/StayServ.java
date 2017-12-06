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
import com.rh.ts.util.RoleUtil;


public class StayServ extends CommonServ {

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
		String servId = "TS_BMSH_STAY";
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

		for (Bean bean : query) {
			if(!"".equals(bean.getId())){}
			//dept_code
			dept_code+="'"+bean.getId()+"',";
			String[] split = dept_code.split(",");
			dept_code="";
			for (String string : split) {
				dept_code+=string+",";
			}
		}
		List<Bean> list = new ArrayList<Bean>();
				if(dept_code.length()>5){
			dept_code = dept_code.substring(0,dept_code.length()-1);
			String sql1 = "select distinct code_path from sy_org_dept where dept_level in(select min(dept_level) from sy_org_dept where dept_code in ("+dept_code+")) and dept_code in("+dept_code+")";
					List<Bean> query1 = Transaction.getExecutor().query(sql1);
					String sql3 = "";
					sql3 += "select * from (select a.*,b.code_path from ts_bmsh_stay a left join sy_org_dept b on a.s_dept=b.dept_code and xm_id = '"+xmid+"') c ";
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
		/*String user_code = paramBean.getStr("user_code");
		List<Bean> list = new ArrayList<Bean>();

		for (Bean bean : list1) {
			String other = bean.getStr("SH_OTHER");
			if (other.contains(user_code)) {
				list.add(bean);
			}
		}*/
		int yeshu = ALLNUM / meiye;
		int yushu = ALLNUM % meiye;
		// 获取总页数
		if (yushu != 0) {
			yeshu += 1;
		}

		// 放到Array中
		List<Bean> list2 = new ArrayList<Bean>();
		
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
		
		_PAGE_.set("ALLNUM", ALLNUM);
		_PAGE_.set("NOWPAGE", NOWPAGE);
		_PAGE_.set("PAGES", yeshu);
		_PAGE_.set("SHOWNUM", SHOWNUM);
		outBean.set("list", list);
		outBean.set("_PAGE_", _PAGE_);
		outBean.set("first", chushi);
		return outBean;
	}

	/**
	 * 获取项目下所有待审核的数据
	 * 
	 * @param paramBean
	 * @return
	 */
	public Bean getAllData(Bean paramBean) {
		OutBean out = new OutBean();
		UserBean user = Context.getUserBean();
		String user_code = user.getStr("USER_CODE");
		String xmid = paramBean.getStr("xmid");
		//根据项目id找到流程下的所有节点
		String belongwhere = "AND XM_ID='"+xmid+"'";
		List<Bean> finds = ServDao.finds("TS_XMGL_BMSH", belongwhere);
		if(finds.size()!=0){
			String wfsid = 	finds.get(0).getStr("WFS_ID");
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
						out.set("level", codebean.getStr("NODE_STEPS"));
						out.set("node_id", codebean.getStr("NODE_NAME"));
						break;
					}
					
				}
				if(flagstr){
					break;
				}
			}
			
		}
		
		return out;
	}

	/**
	 * 修改 审核状态
	 * 
	 * @param paramBean
	 */
	@SuppressWarnings("static-access")
	public Bean update(Bean paramBean) {
		String shenuser = "";
		UserBean userBean = Context.getUserBean();
		if (userBean.isEmpty()) {
			return new OutBean().setError("ERROR:user_code 为空");
		} else {
			shenuser = userBean.getStr("USER_CODE");
		}
		
		String node_name = "";
		String nodeid = paramBean.getStr("nodeid");

		String levels = paramBean.getStr("level");
		int level = 0;
		if (!"".equals(levels)) {

			level = Integer.parseInt(levels);
		}
		String s = paramBean.getStr("checkedid");
		String xmid = paramBean.getStr("xmid");
		
		//判断逐级  越级
		String flag = "";
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
		// 获取当前的审核层级 如果是最高层级审核结束只留下最高级的审核人
		for (String id : ss) {
			if (!"".equals(id)) {
				
				Bean bean = ServDao.find("TS_BMSH_STAY", id);
				String bmid = bean.getStr("BM_ID");
				// 获取审核人信息
				int flowname = 1;
				ParamBean parambean = new ParamBean();
				parambean.set("examerUserCode", bean.getStr("BM_CODE"));
				parambean.set("level", level);
				parambean.set("deptCode", bean.getStr("S_DEPT"));
				parambean.set("odeptCode", bean.getStr("S_ODEPT"));
				parambean.set("shrUserCode", shenuser);
				parambean.set("flowName", flowname);
				parambean.set("xmId", xmid);
				OutBean outbean = ServMgr.act("TS_WFS_APPLY", "backFlow",
						parambean);

				String allman = "";
				String blist = outbean.getStr("result");

				if(!"".equals(blist)){
					allman= blist.substring(0,blist.length()-1);
				}
				//
				// 审核通过
				if (state.equals("1")) {
					// 查找下一层级的人当前人力资源编码
					if (level == 1) {
						// 流程最后一次审核
						ServDao.delete("TS_BMSH_STAY", id);
					} else {
						// 更新
						bean.set("SH_LEVEL", level);
						bean.set("SH_USER", allman);
						bean.set("SH_OTHER", allman);
						ServDao.save("TS_BMSH_STAY", bean);
					}

					// 审核通过里面数据进行修改 同步
					bean.remove("SH_ID");
					bean.remove("S_CMPY");
					bean.remove("S_ATIME");
					bean.remove("S_MTIME");
					bean.remove("S_FLAG");
					bean.remove("_PK_");
					bean.remove("ROW_NUM_");
					// 保存完之后将新的bean保存到 审核通过的(未通过) 如果审核层级是0 说明是第一次审核 新建数据 否则
					// 进行查询修改数据
					String where = "AND BM_ID=" + "'" + bean.getStr("BM_ID")
							+ "'";
					List<Bean> newlist = ServDao.finds("TS_BMSH_PASS", where);
					if (newlist.size() != 0) {
						Bean newBean = newlist.get(0);
						newBean.copyFrom(bean);
						if(flag.equals("1")){
							//    逐级  
							newBean.set("SH_USER", shenuser);
							newBean.set("SH_LEVEL", level);
							String newother = newlist.get(0).getStr("SH_OTHER")+","+shenuser;
							
							newBean.set("SH_OTHER", newother);
						}else{
							//越级  所有人可见
							ParamBean parambean1 = new ParamBean();
							parambean1.set("examerUserCode", bean.getStr("BM_CODE"));
							parambean1.set("level", 0);
							parambean.set("deptCode", bean.getStr("S_DEPT"));
							parambean.set("odeptCode", bean.getStr("S_ODEPT"));
							parambean1.set("shrUserCode", shenuser);
							parambean1.set("flowName", flowname);
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
							ServDao.delete("TS_BMSH_STAY", id);
						}
						
						ServDao.save("TS_BMSH_PASS", newBean);
					} else {
						Bean newBean = new Bean();
						newBean.copyFrom(bean);
						newBean.set("SH_USER", shenuser);
						newBean.set("SH_LEVEL", level);
						
						
						if(flag.equals("1")){
							//    逐级  
							newBean.set("SH_USER", shenuser);
							newBean.set("SH_LEVEL", level);
							
							newBean.set("SH_OTHER", shenuser);
						}else{
							//越级  所有人可见
							ParamBean parambean1 = new ParamBean();
							parambean1.set("examerUserCode", bean.getStr("BM_CODE"));
							parambean1.set("level", 0);
							parambean.set("deptCode", bean.getStr("S_DEPT"));
							parambean.set("odeptCode", bean.getStr("S_ODEPT"));
							parambean1.set("shrUserCode", shenuser);
							parambean1.set("flowName", flowname);
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
							ServDao.delete("TS_BMSH_STAY", id);
						}
						ServDao.save("TS_BMSH_PASS", newBean);
					}

					// 修改报名的状态
					Bean bm_bean = ServDao.find("TS_BMLB_BM", bmid);
					if (bm_bean != null) {
						bm_bean.set("BM_SH_STATE", "1");
						ServDao.save("TS_BMLB_BM", bm_bean);
					}
				} else {
					// 审核未通过的数据不再往上提交 将审核权限 只放在当前人手中、
					Bean newBean = new Bean();
					// 进行查询修改数据
					String where1 = "AND BM_ID=" + "'" + bean.getStr("BM_ID")
							+ "'";
					bean.remove("SH_ID");
					bean.remove("S_CMPY");
					bean.remove("S_ATIME");
					bean.remove("S_MTIME");
					bean.remove("S_FLAG");
					bean.remove("_PK_");
					bean.remove("ROW_NUM_");
					newBean.copyFrom(bean);
					if(flag.equals("1")){
						List<Bean> newlist = ServDao.finds("TS_BMSH_PASS", where1);
						String shothers ="";
						if(newlist!=null&&newlist.size()!=0){
						 shothers = newlist.get(0).getStr("SH_OTHER")+","+shenuser;
						}
						if(!"".equals(shothers)){
							
							newBean.set("SH_OTHER", shothers);
						}else{
							newBean.set("SH_OTHER", shenuser);
						}
						
						// 只有 当前人能让审核再次进行下去   逐级  
						newBean.set("SH_USER", shenuser);
						newBean.set("SH_LEVEL", level);
					}else{
					
						//越级  所有人可见
						ParamBean parambean1 = new ParamBean();
						parambean1.set("examerUserCode", bean.getStr("BM_CODE"));
						parambean1.set("level", 0);
						parambean.set("deptCode", bean.getStr("S_DEPT"));
						parambean.set("odeptCode", bean.getStr("S_ODEPT"));
						parambean1.set("shrUserCode", shenuser);
						parambean1.set("flowName", flowname);
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
					ServDao.delete("TS_BMSH_STAY", id);
					String where = "AND BM_ID=" + "'" + bean.getStr("BM_ID")
							+ "'";
					List<Bean> newlist = ServDao.finds("TS_BMSH_PASS", where);
					if (newlist.size() == 0) {
					} else {
						String id1 = newlist.get(0).getId();
						ServDao.delete("TS_BMSH_PASS", id1);
					}
					Bean bm_bean = ServDao.find("TS_BMLB_BM", bmid);
					if (bm_bean != null) {
						bm_bean.set("BM_SH_STATE", "2");
						ServDao.save("TS_BMLB_BM", bm_bean);
					}
					noPassBmIdList.add(bmid);
				}
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
				mindbean.set("SH_ULOGIN",localip);
				mindbean.set("SH_UNAME", userBean.getName());
				mindbean.set("SH_UCODE", shenuser);
				mindbean.set("SH_TYPE", 1);
				mindbean.set("SH_NODE", nodeid);
				ServDao.save("TS_COMM_MIND", mindbean);
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
		outBean.set("string", w);
		return outBean;
	}

	/**
	 * 获取用户信息
	 */
	public Bean getUserInfo(Bean paramBean) {
		Bean returnBean = new Bean();
		Bean outBean = new Bean();
		String bm_code = paramBean.getStr("bm_code");
		try {
			// 根据人力编码获取人力信息
			UserBean userBean = UserMgr.getUser(bm_code);

			String s = userBean.getODeptName() + ",";
			// 获取当前机构;
			DeptBean oneodeptcode1 = userBean.getODeptBean();
			String codes = "";
			if (oneodeptcode1 != null) {
				// 获取所有逗号分隔的字符串
				codes = getusercodes(oneodeptcode1, s);
				if("".equals(codes)){
					codes=s;
				}
			}
			String[] codesarr = codes.split(",");

			int j = 0;
			for (int i = codesarr.length - 1; i >= 0; i--) {
				// 最后一个 deptcodename
				String evname = codesarr[i];
				j++;
				outBean.set("LEVEL" + j, evname);
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
		List<Bean> list = new ArrayList<Bean>();
		list.add(outBean);
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
		returnBean.set("list", w.toString());
		return returnBean;
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
			DeptBean oneodeptcode1 = userBean.getODeptBean();
			String codes = "";
			if (oneodeptcode1 != null) {
				// 获取所有逗号分隔的字符串
				codes = getusercodes(oneodeptcode1, s);
			}
			String[] codesarr = codes.split(",");

			int j = 6;
			for (int i = codesarr.length - 1; i >= 0; i--) {
				// 最后一个 deptcodename
				String evname = codesarr[i];
				j--;
				outBean.set("LEVEL" + j, evname);
			}
			String shuser = "";
			UserBean userBean1 = Context.getUserBean();
			if (userBean1.isEmpty()) {
				return new OutBean().setError("ERROR:user_code 为空");
			} else {
				shuser = userBean.getStr("USER_NAME");
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
			String name = newBean.getName() + ",";
			codes+=getusercodes(newBean, name);
			return codes;
		} else {

			return "";
		}
	}

	/** 每次获取数据条数 */
	private static final int ONETIME_EXP_NUM = 5000;
	/** excel最大行数 */
	private static final int EXCEL_MAX_NUM = 65536;

	/**
	 * 提供导出Excel
	 * 
	 * @param paramBean
	 *            参数信息
	 * @return 执行结果
	 */
	@Override
	public OutBean exp(ParamBean paramBean) {
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
			Bean outBean = new Bean();
			String NOWPAGE = paramBean.getStr("nowpage");
			String SHOWNUM = paramBean.getStr("shownum");
			String where1 = paramBean.getStr("where");
			String sql = "select n.dept_code from (select node_id from(select wfs_id from ts_xmgl_bmsh where xm_id='"+xmid+"') c left join TS_WFS_NODE_APPLY d on c.wfs_id = d.wfs_id) m left join TS_WFS_BMSHLC n on m.node_id = n.node_id  "+
					"where n.shr_usercode= '"+usercode+"'";
			List<Bean> query = Transaction.getExecutor().query(sql);
			String dept_code="";

			for (Bean bean : query) {
				if(!"".equals(bean.getId())){}
				//dept_code
				dept_code+="'"+bean.getId()+"',";
			}
			if(dept_code.length()>5){
				dept_code = dept_code.substring(0,dept_code.length()-1);
				String sql1 = "select distinct code_path from sy_org_dept where dept_level in(select min(dept_level) from sy_org_dept where dept_code in ("+dept_code+")) and dept_code in("+dept_code+")";
				List<Bean> query1 = Transaction.getExecutor().query(sql1);
				for (Bean bean : query1) {
					//判断哪些考生部门 在此codepath 下
					String sql3 = "select * from (select a.*,b.code_path from ts_bmsh_stay a left join sy_org_dept b on a.s_dept=b.dept_code and xm_id='"+xmid+"') c where c.code_path like concat('"+bean.getId()+"','%')";
					List<Bean> query2 = Transaction.getExecutor().query(sql3);
					dataList.addAll(query2);
				}
			}else{
				return new OutBean().setError("空");
			}


		}

		List<Bean> finalList = new ArrayList<Bean>();

		// 判断user_code 是否为空 若为空则 导出所有

		searchWhere = " AND USER_CODE =" + "'" + user_code1 + "' order by cast(PX_XUHAO as SIGNED)";

		// 排序用的 parr存读取th
		parr.setQuerySearchWhere(searchWhere);
		LinkedHashMap<String, Bean> cols = new LinkedHashMap<String, Bean>();
		List<Bean> pxdatalist1 = ServDao.finds("TS_BMSH_PX", searchWhere);
		if (pxdatalist1.size() == 0) {
			String where1 = "AND USER_CODE is null ";
			pxdatalist1 = ServDao.finds("TS_BMSH_PX", where1);
		}
		// 查询出所有的 待审核记录
		OutBean outBean = query(paramBean);
		for (Bean bean : dataList) {
			String work_num = bean.getStr("BM_CODE");
			Bean userBean = getUserInfo1(work_num);
			Bean newBean = new Bean();
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
					Bean parambeansss = new Bean();
					parambeansss.set("codes", bean.getStr("SH_OTHER"));
					Bean outBeans = getusername(bean);
					name = outBeans.getStr("usernames");
				}
				if("SH_STATUS".equals(namecol)){
					//审核状态;
					name = "审核中...";
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
			count = outBean.getCount();
			// 总数大于excel可写最大值
			if (count > EXCEL_MAX_NUM) {
				return new OutBean().setError("导出数据总条数大于Excel最大行数："
						+ EXCEL_MAX_NUM);
			}
			// 导出第一次查询数据
			paramBean.setQueryPageNowPage(1); // 导出当前第几页
			afterExp(paramBean, outBean); // 执行导出查询后扩展方法
			// 查询出表头 查询出 对应数据 hashmaplist
			expExcel.createHeader(cols);
			
			expExcel.appendData1(finalList, paramBean);
			// 存在多页数据
			if (ONETIME_EXP_NUM < count) {
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
			}
			expExcel.addSumRow();
		} catch (Exception e) {
			log.error("导出Excel文件异常" + e.getMessage(), e);
		} finally {
			expExcel.close();
		}
		return new OutBean().setOk();
	}

	/**
	 * 根据usercode 获取username
	 */
	public Bean getusername(Bean paramBean) {
		UserBean user = Context.getUserBean();
		String usercod = user.getCode();
		Bean outBean = new Bean();
		String s = "";
		String codes = paramBean.getStr("codes");
		if (!"".equals(codes)) {
			String[] split = codes.split(",");
			for (int i = 0; i < split.length; i++) {

				if (!"".equals(split[i])) {
					if(usercod.equals(split[i])){
						continue;
					}
					try {
						UserBean userBean = UserMgr.getUser(split[i]);
						if (userBean.isEmpty()) {
							return new OutBean().setError("ERROR:user_code 为空");
						} else {
							if (s.equals("")) {
								s += userBean.getStr("USER_NAME");
							} else {
								s +=  ","+userBean.getStr("USER_NAME");
							}
						}
					}catch(Exception e){

					}
				}
			}
		}
		
		outBean.set("usernames", s);
		return outBean;
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
		//根据项目id找到流程下的所有节点    审核人审核的机构
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
		String servId = "TS_BMSH_STAY";
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
				String sql = "select * from TS_BMSH_STAY where 1=1"+where1;
				ALLNUM = Transaction.getExecutor().count(sql);
				 if(jieshu>ALLNUM){
					 showpage=ALLNUM-chushi;
				 }
				 String datasql = "select * from TS_BMSH_PASS where 1=1"+where1 +" limit "+chushi+","+showpage;
				  list = Transaction.getExecutor().query(datasql);

			}else{
			/*	List<DeptBean> finds = OrgMgr.getChildDepts(compycode, user.getODeptCode());
				for (Bean bean : finds) {
					dept_code+=","+bean.getStr("DEPT_CODE");
				}
				deptwhhere = "AND S_DEPT IN ("+dept_code+")";*/
				deptwhere = "AND S_DEPT IN ("+dept_code+")";
				DeptBean dept = OrgMgr.getDept(dept_code);
				String codepath = dept.getCodePath();
				String sql = "select count(*) from "+servId+" a where exists(select dept_code from sy_org_dept b where code_path like concat('"+codepath+"','%') and a.s_dept=b.dept_code and s_flag='1')"+where1;
				ALLNUM = Transaction.getExecutor().count(sql);
				 ALLNUM = Transaction.getExecutor().count(sql);
				 String datasql = "select * from "+servId+" a where exists(select dept_code from sy_org_dept b where code_path like concat('"+codepath+"','%') and a.s_dept=b.dept_code and s_flag='1')"+where1+" limit "+chushi+","+jieshu;
				  list = Transaction.getExecutor().query(datasql);
				
			}
		//根据审核  机构 匹配当前机构下的所有人
		Bean _PAGE_ = new Bean();
		Bean outBean = new Bean();
		

		// 计算页数
		int yeshu = ALLNUM / meiye;
		int yushu = ALLNUM % meiye;
		// 获取总页数
		if (yushu != 0) {
			yeshu += 1;
		}

		// 放到Array中
		List<Bean> list2 = new ArrayList<Bean>();
		
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
	 * 获取各模块 数量
	 */
	public OutBean tongjinum(Bean paramBean){
		OutBean out = new OutBean();
		String xianei = paramBean.getStr("xianei");
				//当前审核人
				UserBean user = Context.getUserBean();
				String user_code = user.getStr("USER_CODE");
				String belongdeptcode = "";
				String xmid = paramBean.getStr("xmid");
				Bean userPvlgToHT = RoleUtil.getPvlgRole(user.getCode(),"TS_BMGL_XNBM");
				Bean userPvlgToHTBean = (Bean) userPvlgToHT.get("TS_BMGL_XNBM_PVLG");
				Bean str = (Bean)userPvlgToHTBean.get("XN_BM");
				String dept_code = str.getStr("ROLE_DCODE");
				if("".equals(dept_code)){
					dept_code=user.getStr("ODEPT_CODE");
				}
				dept_code = dept_code.substring(0,10);
				String compycode = user.getCmpyCode();
				if("belong".equals(xianei)){
					
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
		//根据审核  机构 匹配当前机构下的所有人
		}else{
			//自己所在机构以下的所有数据
			//管理员以下的所有机构
			String deptwhere1="";
			if(dept_code.equals("0010100000")){
				//所有人员
				 deptwhere1 = "WHERE XM_ID='"+xmid+"'";
				 String sql = "select count(*) from TS_BMSH_STAY "+deptwhere1;
				 List<Bean> query = Transaction.getExecutor().query(sql);
				 int stay = Integer.parseInt(query.get(0).getId());
				 out.set("staynum", stay);
				 String sql1 = "select count(*) from TS_BMSH_PASS "+deptwhere1;
				 List<Bean> query1 = Transaction.getExecutor().query(sql1);
				 int PASS =Integer.parseInt(query1.get(0).getId());
				 out.set("passnum", PASS);
				 String sql2 = "select count(*) from TS_BMSH_NOPASS "+deptwhere1;
				 List<Bean> query2 = Transaction.getExecutor().query(sql2);
				 int nopass = Integer.parseInt(query2.get(0).getId());
				 out.set("nopassnum", nopass);
				 out.set("allnum", stay+nopass+PASS);
			}else{
				DeptBean dept = OrgMgr.getDept(dept_code);
				String codepath = dept.getCodePath();
				String sql = "select COUNT(*) from TS_BMSH_STAY a where exists(select dept_code from sy_org_dept b where code_path like concat('"+codepath+"','%') and a.s_dept=b.dept_code and s_flag='1') AND XM_ID='"+xmid+"'";
				 List<Bean> query = Transaction.getExecutor().query(sql);
				 int stay = Integer.parseInt(query.get(0).getId());
				out.set("staynum", stay);
				String sql1 = "select COUNT(*) from TS_BMSH_NOPASS a where exists(select dept_code from sy_org_dept b where code_path like concat('"+codepath+"','%') and a.s_dept=b.dept_code and s_flag='1') AND XM_ID='"+xmid+"'";
				 List<Bean> query1 = Transaction.getExecutor().query(sql1);
				int nopass = Integer.parseInt(query1.get(0).getId());
				out.set("nopassnum", nopass);
				String sql2 = "select COUNT(*) from TS_BMSH_PASS a where exists(select dept_code from sy_org_dept b where code_path like concat('"+codepath+"','%') and a.s_dept=b.dept_code and s_flag='1') AND XM_ID='"+xmid+"'";
				 List<Bean> query2 = Transaction.getExecutor().query(sql2);
				int pass = Integer.parseInt(query2.get(0).getId());
				out.set("passnum", pass);
				out.set("allnum", stay+nopass+pass);
			}
		
		}
		
		return out;
	}
	/**
	 * 审核人是否有需要审核的数据提醒
	 */
	public OutBean getStayList(Bean paramBean){
		int count = 0;
		String usercode = Context.getUserBean().getCode();
		//查询当前审核人的流程 绑定的审核机构
		Bean _PAGE_ = new Bean();
		Bean outBean = new Bean();
		String servId = "TS_BMSH_STAY";
		String sql = "select n.dept_code from (select node_id from(select wfs_id from ts_xmgl_bmsh where sh_rgsh=1) c left join TS_WFS_NODE_APPLY d on c.wfs_id = d.wfs_id) m left join TS_WFS_BMSHLC n on m.node_id = n.node_id  "+
				"where n.shr_usercode= '"+usercode+"'";
		List<Bean> query = Transaction.getExecutor().query(sql);
		String dept_code="";

		for (Bean bean : query) {
			if(!"".equals(bean.getId())){}
			//dept_code
			dept_code+="'"+bean.getId()+"',";
		}
		List<Bean> list = new ArrayList<Bean>();
		if(dept_code.length()>5){
			dept_code = dept_code.substring(0,dept_code.length()-1);
			String sql1 = "select distinct code_path from sy_org_dept where dept_level in(select min(dept_level) from sy_org_dept where dept_code in ("+dept_code+")) and dept_code in("+dept_code+")";
			List<Bean> query1 = Transaction.getExecutor().query(sql1);
			for (Bean bean : query1) {
				//判断哪些考生部门 在此codepath 下
				String sql3 = "select * from (select a.*,b.code_path from ts_bmsh_pass a left join sy_org_dept b on a.s_dept=b.dept_code) c where c.code_path like concat('"+bean.getId()+"','%')";
				  count += Transaction.getExecutor().count(sql3);
			}
			/*
		for (String string : split) {
			if(!"".equals(string)){
				//当前审核人 待审核的数据
				List<Bean> list1 = ServDao.finds("TS_BMSH_STAY", "AND XM_ID='"+string+"'");
				
				for (Bean bean : list1) {
					String other = bean.getStr("SH_OTHER");
					if (other.contains(user_code)) {
						list.add(bean);
					}
				}
				
				
			}
		}*/
		}
		OutBean out = new OutBean();
		if(list.size()==0){
			out.set("num", 0);
			return  out.set("flag", "false");
		}else{
			out.set("num", count);
			return out.set("flag", "true");
		}
		
	}
	/**
	 * 审核人需要审核的数据提醒（一个项目下）
	 */
	public OutBean getsingxmnum(Bean paramBean){
		String str = paramBean.getStr("xmid");
		UserBean userBean = Context.getUserBean();
		String user_code = userBean.getCode();
		int count=0;
			if(!"".equals(str)){//多个机构不能用字符串逗号拼接   以后改  不能用like
				//当前审核人 待审核的数据
				SqlBean sqlbean = new SqlBean();
				sqlbean.appendWhere("AND XM_ID=? AND SH_OTHER LIKE ?", str,"'%"+user_code+"%'");
				 count = ServDao.count("TS_BMSH_STAY", sqlbean);
			
			}
		OutBean out = new OutBean();
		if(count==0){
			out.set("num", "");
			return  out.set("flag", "false");
		}else{
			out.set("num", count);
			return out.set("flag", "true");
		
	}
}
}