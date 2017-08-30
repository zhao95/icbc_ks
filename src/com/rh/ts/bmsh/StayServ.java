package com.rh.ts.bmsh;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.rh.core.base.Bean;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;

public class StayServ extends CommonServ {

	/**
	 * 获取那一页的记录 返回
	 * 
	 * @param paramBean
	 * @return
	 */
	public Bean getUncheckList(Bean paramBean) {
		Bean outBean = new Bean();
		String servId = "TS_BMSH_STAY";
		String NOWPAGE = paramBean.getStr("nowpage");
		String SHOWNUM = paramBean.getStr("shownum");
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
		return outBean;
	}

	/**
	 * 获取项目下所有待审核的数据
	 * 
	 * @param paramBean
	 * @return
	 */
	public Bean getAllData(Bean paramBean) {
		Bean outBean = new Bean();
		String servId = "TS_BMSH_STAY";
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
	 * 修改 审核状态
	 * 
	 * @param paramBean
	 */
	public void update(Bean paramBean){
		String s = paramBean.getStr("checkedid");
		String xmid = "35QzQkC4xdvXoTjvYEYu";
		String bm_code = "888802713";
		String shenuser = "888800172";
		String slevel = "2";
		//被选中的id
		String[] ss = s.split(",");
		String state = paramBean.getStr("radiovalue");
		String liyou = paramBean.getStr("liyou");
		//获取当前的审核层级  如果是最高层级审核结束只留下最高级的审核人 
		for (String id : ss) {
			if(!"".equals(id)){
				
				Bean bean = ServDao.find("TS_BMSH_STAY", id);
				//获取审核人信息 
				UserBean userbean = UserMgr.getUserByWorkNum(shenuser);
				int cengjiint = Integer.parseInt(slevel);
				//返回下一级和所有的数据，  逐级的话将下一个审核节点的人编码  放进去，将当前审核人删除
				int level = Integer.parseInt(slevel);
				int flowname = 1;
				ParamBean parambean = new ParamBean();
				parambean.set("examerWorekNum", bm_code);
				parambean.set("level",level);
				parambean.set("shrWorekNum", shenuser);
				parambean.set("flowName", flowname);
				parambean.set("xmId", xmid);
				OutBean outbean = ServMgr.act("TS_WFS_APPLY", "backFlow", parambean);
				List<Bean> list = outbean.getList("result");
				
				String allman ="";
				String nextman = "";
				for (int l=0;l<list.size();l++) {
					if(l==0){
						nextman = list.get(l).getStr("BMSHLC_SHR");
					}
					if(l==list.size()-1){
						
						allman+= list.get(l).getStr("BMSHLC_SHR");
					}else{
						allman+= list.get(l).getStr("BMSHLC_SHR")+",";
					}
					
				}
				int nowlevel = list.size();
				//
				//审核通过
			if(state.equals("1")){
				//查找下一层级的人当前人力资源编码
				if(cengjiint==1){
					//流程最后一次审核 
					ServDao.delete("TS_BMSH_STAY", id);
				}else{
					//更新
					bean.set("SH_LEVEL", nowlevel);
					bean.set("SH_USER", nextman);
					bean.set("SH_OTHER", allman);
					ServDao.save("TS_BMSH_STAY", bean);
				}
					
					//审核通过里面数据进行修改 同步
					bean.remove("SH_ID");
					bean.remove("S_CMPY");
					bean.remove("S_ODEPT");
					bean.remove("S_TDEPT");
					bean.remove("S_DEPT");
					bean.remove("S_ATIME");
					bean.remove("S_MTIME");
					bean.remove("S_USER");
					bean.remove("S_FLAG");
					bean.remove("_PK_");
					bean.remove("ROW_NUM_");
				//保存完之后将新的bean保存到 审核通过的(未通过)  如果审核层级是0 说明是第一次审核  新建数据 否则 进行查询修改数据
				String where = "AND BM_ID="+"'"+bean.getStr("BM_ID")+"'";
				List<Bean> newlist = ServDao.finds("TS_BMSH_PASS", where);
				if(newlist.size()!=0){
					Bean newBean = newlist.get(0);
					newBean.copyFrom(bean);
					newBean.set("SH_LEVEL", nowlevel+1);
					newBean.set("SH_USER", shenuser);
					if(level==1){
						//最后一级审核
						newBean.set("SH_OTHER",shenuser);
					}else{
						
						newBean.set("SH_OTHER",allman);
					}
					ServDao.save("TS_BMSH_PASS", newBean);
				}else{
					Bean newBean = new Bean();
					newBean.copyFrom(bean);
					newBean.set("SH_LEVEL", nowlevel+1);
					newBean.set("SH_USER", shenuser);
					if(level==1){
						//最后一级审核
						newBean.set("SH_OTHER",shenuser);
					}else{
						
						newBean.set("SH_OTHER",allman);
					}
					ServDao.save("TS_BMSH_PASS", newBean);
				}
			}else{
				//审核未通过的数据不再往上提交 将审核权限  只放在当前人手中、
				Bean newBean = new Bean();
				bean.remove("SH_ID");
				bean.remove("S_CMPY");
				bean.remove("S_ODEPT");
				bean.remove("S_TDEPT");
				bean.remove("S_DEPT");
				bean.remove("S_ATIME");
				bean.remove("S_MTIME");
				bean.remove("S_USER");
				bean.remove("S_FLAG");
				bean.remove("_PK_");
				bean.remove("ROW_NUM_");
				newBean.copyFrom(bean);
				//只有 当前人能让审核再次进行下去
				newBean.set("SH_LEVEL",nowlevel+1);
				newBean.set("SH_USER",shenuser);
				newBean.set("SH_OTHER",shenuser);
				ServDao.save("TS_BMSH_NOPASS", newBean);
				ServDao.delete("TS_BMSH_STAY", id);
				String where = "AND BM_ID="+"'"+bean.getStr("BM_ID")+"'";
				List<Bean> newlist = ServDao.finds("TS_BMSH_PASS", where);
				if(newlist.size()==0){
				}else{
				String id1 = newlist.get(0).getId();
				ServDao.delete("TS_BMSH_PASS",id1);
				}
			}
			//审核明细表中插入此次审核数据
			Bean mindbean = new Bean();
			mindbean.set("SH_LEVEL",slevel);
			mindbean.set("SH_MIND", liyou);
			mindbean.set("DATA_ID",bean.getStr("BM_ID"));
			mindbean.set("SH_STATUS", state);
			mindbean.set("SH_ULOGIN",userbean.getLoginName());
			mindbean.set("SH_UNAME",userbean.getName());
			mindbean.set("SH_UCODE",shenuser);
			mindbean.set("SH_TYPE", 1);
			ServDao.save("TS_COMM_MIND",mindbean);
			}
		}
		
		
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
	public Bean getUserInfo(Bean paramBean){
		Bean returnBean = new Bean();
		Bean outBean = new Bean();
		String bm_code = paramBean.getStr("bm_code");
		try{
		//根据人力编码获取人力信息
		UserBean userBean = UserMgr.getUserByWorkNum("888802713");
	    
    	String s = userBean.getODeptName()+",";
    	//获取当前机构;
    	DeptBean oneodeptcode1= userBean.getODeptBean();
    	String codes = "";
    	if(oneodeptcode1!=null){
    		//获取所有逗号分隔的字符串
    		codes = getusercodes(oneodeptcode1,s);
    	}
    		String[] codesarr = codes.split(",");
    	
    		int j=6;
    	for(int i=codesarr.length-1;i>=0;i--){
    		//最后一个 deptcodename
    		String evname = codesarr[i];
    		j--;
    		outBean.set("LEVEL"+j,evname);
    	}
    
    	//性别
    	int user_sex = userBean.getSex();
    	if(user_sex==0){
    		outBean.set("USER_SEX","男");
    	}else{
    		outBean.set("USER_SEX","女");
    	}
    	//入行时间
    	String date = userBean.getStr("USER_CMPY_DATE");
    	outBean.set("USER_CMPY_DATE",date);
    	//办公电话
    	String office_phone = userBean.getOfficePhone();
    	outBean.set("USER_OFFICE_PHONE", office_phone);
    	//手机号码
    	String user_phone = userBean.getMobile();
    	outBean.set("USER_MOBILE",user_phone);
    	//职务层级
    	String cengji = userBean.getPost();
    	outBean.set("USER_POST_LEVEL",cengji);
		}catch(Exception exception){
			
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
	 * 获取所有部门信息
	 * @param oneodeptcode1
	 * @return
	 */
	  public String getusercodes(DeptBean oneodeptcode1,String codes){
  		DeptBean newBean= oneodeptcode1.getParentDeptBean();
  		if(newBean!=null){
  			String s = newBean.getName()+",";
  			return  codes +=getusercodes(newBean,s);
  		}else{
  			
  			return "";
  		}
  }
}
