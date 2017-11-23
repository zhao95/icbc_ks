package com.rh.ts.xmglsz;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.icbc.ctp.jdbc.transaction.TransactionManager;
import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.ts.util.TsConstant;

/**
 * 项目管理设置
 * 
 * @author
 *
 */
public class XmglszServ extends CommonServ {

	public OutBean findByXmid(Bean paramBean) {
		OutBean result = new OutBean();
		List<Bean> list = ServDao.finds("TS_XMGL_SZ", paramBean);

		result.set("resList", list);
		return result;

	}

	public OutBean findBmId(Bean paramBean) {
		String XM_SZ_ID = paramBean.getId();
		String where = " and XM_SZ_ID='" + XM_SZ_ID + "'";
		List<Bean> listBmgl = ServDao.finds("TS_XMGL_BMGL", where);
		if (listBmgl.isEmpty()) {
			// 返回一个可以新建卡片的
		} else {
			for (int i = 0; i < listBmgl.size(); i++) {
			}
		}
		// ServDao.save(servId, dataBean)
		return null;

	}

	public OutBean existSH(Bean paramBean) {

		OutBean out = new OutBean();

		String xmId = paramBean.getStr("XM_ID");

		SqlBean sql = new SqlBean();

		sql.and("XM_ID", xmId);

		sql.and("S_FLAG", 1);

		List<Bean> list = ServDao.finds(TsConstant.SERV_BMSH, sql);

		if (list != null && list.size() > 0) {
			Bean bean = list.get(0);
			int zd = bean.getInt("SH_ZDSH");
			int rg = bean.getInt("SH_RGSH");

			if (zd == 1 && rg == 1) { // 自动和人工
				out.setCount(3);
			} else if (zd == 1) { // 自动审核
				out.setCount(1);
			} else if (rg == 1) { // 人工审核
				out.setCount(2);
			}else{
				out.setCount(0); // 无审核
			}
		} else {
			out.setCount(0); // 无审核
		}

		return out;
	}

	public OutBean existModule(Bean paramBean) {

		OutBean out = new OutBean();

		String szName = paramBean.getStr("XM_SZ_NAME");

		String xmId = paramBean.getStr("XM_ID");

		SqlBean sql = new SqlBean();

		sql.and("XM_SZ_NAME", szName);

		sql.and("XM_ID", xmId);

		sql.and("S_FLAG", 1);

		int count = ServDao.count(TsConstant.SERV_XMGL_SZ, sql);

		if (count > 0) {
			out.setCount(count);
		}

		return out;
	}
	/**
	 * 获取待安排的项目下的考场
	 * @param paramBean
	 * @return
	 */
	public OutBean getStayApXm(Bean paramBean){
		//所有考场安排的 项目设置ID
		UserBean userBean = Context.getUserBean();
		String odeptcode = userBean.getODeptCode();
	List<Bean> dataList = new ArrayList<Bean>();
	List<Bean> ALLList = ServDao.finds("TS_XMGL_SZ", "AND XM_SZ_NAME='考场安排' AND XM_SZ_TYPE='进行中'");
	for (Bean bean : ALLList) {
		boolean flag= false;
		//判断项目 是否  启用了
			//判断时间
			Date date = new Date();
			SimpleDateFormat simp =new SimpleDateFormat("yyyy-MM-dd");
			String simpdate = simp.format(date);
			List<Bean> xmlist = ServDao.finds("TS_XMGL", "AND XM_ID='"+bean.getStr("XM_ID")+"' AND '"+simpdate+"' between xm_start and xm_end");
			if(xmlist.size()==0){
				continue;
			}
			String xm_name = xmlist.get(0).getStr("XM_NAME");
			String xm_start = xmlist.get(0).getStr("XM_START");
			String xm_end = xmlist.get(0).getStr("XM_END");
			bean.set("xm_name", xm_name);
			bean.set("xm_start", xm_start);
			bean.set("xm_end", xm_end);
		//所有可见报名人员  的 odept
		List<Bean> grouplist = ServDao.finds("TS_BM_GROUP", "AND XM_ID='"+bean.getStr("XM_ID")+"'");
		for (Bean bean2 : grouplist) {
			if(flag==true){
				flag = false;
				break;
			}
			//遍历所有群组
			String groupId =  bean2.getId();
			//用户
			String sql = "SELECT USER_DEPT_CODE FROM TS_BM_GROUP_USER_DEPT WHERE G_ID='"+groupId+"' AND G_TYPE='1' GROUP BY USER_DEPT_CODE";
			List<Bean> userList = Transaction.getExecutor().query(sql);
			for (Bean bean3 : userList) {
			UserBean user = UserMgr.getUser(bean3.getStr("USER_DEPT_CODE"));
			String odept_code = user.getODeptCode();
			if(odeptcode.equals(odept_code)){
				//可以安排此考场
			//判断此考场是否已经被提交 被安排
				String xmid = bean.getStr("XM_ID");
				Boolean tjState = getTjState(odeptcode,xmid);
				if(tjState==true){
					//没有提交
					dataList.add(bean);
					flag=true;
					break;
				}
			}
			}
			if(flag==true){
				flag = false;
				break;
			}
			//部门
			List<Bean> deptList = ServDao.finds("TS_BM_GROUP_USER", "AND G_ID='"+groupId+"' AND G_TYPE='2' GROUP BY USER_DEPT_CODE");
			for (Bean bean4 : deptList) {
				if(bean4.getStr("USER_DEPT_CODE").equals("0010100000")){
					//总行不用判断
					String xmid = bean.getStr("XM_ID");
					Boolean tjState = getTjState(odeptcode,xmid);
					if(tjState==true){
						//没有提交
						dataList.add(bean);
						flag=true;
						break;
					}
				}else{
					//部门编码
				String dept_code = bean4.getStr("USER_DEPT_CODE");
				DeptBean odept = OrgMgr.getOdept(dept_code);
				if(odept.equals(odeptcode)){
					String xmid = bean.getStr("XM_ID");
					Boolean tjState = getTjState(odeptcode,xmid);
					if(tjState==true){
						//没有提交
						dataList.add(bean);
						flag=true;
						break;
					}
				}
			}
			}
		}
	}
	return new OutBean().setData(dataList);
	}
	//判断是否提交了数据
public Boolean getTjState(String odeptcode,String xmid){
	 Bean sqlbean = new Bean();
	 sqlbean.set("XM_ID", xmid);
	 sqlbean.set("TJ_DEPT_CODE", odeptcode);
	 int count = ServDao.count("TS_XMGL_KCAP_TJJL", sqlbean);
	 return count==0?true:false;
}



}
