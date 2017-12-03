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
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;
import com.rh.ts.pvlg.PvlgUtils;
import com.rh.ts.util.RoleUtil;
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
		String user_code = userBean.getCode();
		String odeptcode ="";
		Bean userPvlgToHT = RoleUtil.getPvlgRole(user_code);
		Bean userPvlgToHTBean = (Bean) userPvlgToHT.get("TS_XMGL_KCAP_YAPZW_PVLG");
		if (userPvlgToHTBean == null) {
			return new OutBean().setError("无权限");
		}
		if("0".equals(userPvlgToHTBean.getStr("publish"))&&"0".equals(userPvlgToHTBean.getStr("auto"))){
			return new OutBean();
		}
		int  a=0;
		if ("0".equals(userPvlgToHTBean.getStr("publish"))) {
			
			Bean str = (Bean) userPvlgToHTBean.get("auto");
			if (str == null) {
				return new OutBean().setError("无权限");
			}
			odeptcode = str.getStr("ROLE_DCODE");
			if ("".equals(odeptcode)) {
				 odeptcode = userBean.getODeptCode();
			}
			//提交人
			a=1;
		} else {
			Bean str = (Bean) userPvlgToHTBean.get("publish");
			if (str == null) {
				return new OutBean().setError("无权限");
			}
			odeptcode = str.getStr("ROLE_DCODE");
			if ("".equals(odeptcode)) {
				 odeptcode = userBean.getODeptCode();
			}
			//发布人  
			a=2;
		}
		odeptcode=odeptcode.substring(0,10);
		Date date = new Date();
		SimpleDateFormat simp =new SimpleDateFormat("yyyy-MM-dd");
		String simpdate = simp.format(date);
		//查询此人最大权限
	List<Bean> dataList = new ArrayList<Bean>();
	String sql1 = "select a.* from ts_xmgl a left join TS_XMGL_SZ b on a.xm_id=b.xm_id where b.xm_sz_name = '考场安排 ' and xm_sz_type='进行中' and '"+simpdate+"' between a.xm_start and a.xm_end and a.XM_STATE =1"; 
	List<Bean> ALLList = Transaction.getExecutor().query(sql1);
	for (Bean bean : ALLList) {
		//判断项目 是否  启用了
			String xmid = bean.getStr("XM_ID");
			String sql = "SELECT k.KC_ID" 
					+" FROM"
  +" TS_XMGL_KCAP_DAPCC c" 
  +" LEFT JOIN ts_kcgl k" 
   +" ON k.kc_id = c.kc_id" 
+" WHERE c.XM_ID = '"+xmid+"'" 
  +" AND k.KC_ODEPTCODE IN" 
  +" (SELECT" 
   +" DEPT_CODE" 
  +" FROM"
    +" SY_ORG_DEPT" 
  +" WHERE SY_ORG_DEPT.CODE_PATH LIKE CONCAT('%"+odeptcode+"', '%')" 
    +" AND SY_ORG_DEPT.DEPT_TYPE = 2" 
    +" AND SY_ORG_DEPT.CMPY_CODE = 'icbc'" 
    +" AND SY_ORG_DEPT.S_FLAG = 1)";
			int count = Transaction.getExecutor().count(sql);
			if(count>0){
				if(a==1){
					Boolean tjState = getTjState(odeptcode,xmid);
					if(tjState==true){
						
						String xm_name = bean.getStr("XM_NAME");
						String xm_start = bean.getStr("XM_START");
						String xm_end = bean.getStr("XM_END");
						bean.set("xm_name", xm_name);
						bean.set("xm_start", xm_start);
						bean.set("xm_end", xm_end);
						dataList.add(bean);
					}
				}else if(a==2){
					//判断发布
					Bean find = ServDao.find("TS_XMGL", xmid);
					if(find!=null){
						String str = find.getStr("XM_KCAP_PUBLISH_TIME");
						if("".equals(str)){
							//未发布
							String xm_name = bean.getStr("XM_NAME");
							String xm_start = bean.getStr("XM_START");
							String xm_end = bean.getStr("XM_END");
							bean.set("xm_name", xm_name);
							bean.set("xm_start", xm_start);
							bean.set("xm_end", xm_end);
							dataList.add(bean);
						}
					}
				}
				//判断是否已发布
			}
			
		
	}
	return new OutBean().setData(dataList);
	}
	//判断是否提交了数据
public Boolean getTjState(String odeptcode,String xmid){
	//如果  上级 已提交 下级 不能在进行 安排
	 Bean sqlbean = new Bean();
	 sqlbean.set("XM_ID", xmid);
	 sqlbean.set("TJ_DEPT_CODE", odeptcode);
	 int count = ServDao.count("TS_XMGL_KCAP_TJJL", sqlbean);
	 return count==0?true:false;
}

//修改时间UPDATE TS_XMGL_SZ SET XM_SZ_EXPLAIN= 参数 WHERE XM_ID = 参数   AND   XM_SZ_ID=参数//String xmszExplain,String xmid,String xmszid){
public  OutBean  getTimes(ParamBean param){
	String  xmszExplain=param.getStr("xmszExplain");
	String  xmid=param.getStr("xmid");
	String  xmszid=param.getStr("xmszid");
	String impSql = "UPDATE TS_XMGL_SZ SET XM_SZ_EXPLAIN= ' "+xmszExplain+ "' WHERE XM_ID = '"+xmid+"'   AND   XM_SZ_ID='"+xmszid+"'";
    Transaction.getExecutor().execute(impSql);
    return new OutBean().setData(impSql) ;
}
public  OutBean  deleteTimes(ParamBean param){
	String  xmid=param.getStr("xmid");
	String  xmszid=param.getStr("xmszid");
	String impSql = "UPDATE TS_XMGL_SZ SET XM_SZ_EXPLAIN="+"' '"+" WHERE XM_ID = '"+xmid+"'   AND   XM_SZ_ID='"+xmszid+"'";
    Transaction.getExecutor().execute(impSql);
    return new OutBean().setData(impSql) ;
}
}
