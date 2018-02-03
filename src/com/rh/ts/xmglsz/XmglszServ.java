package com.rh.ts.xmglsz;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

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
		OutBean outBean = new OutBean();
		outBean.setData(new ArrayList<Bean>());
		//所有考场安排的 项目设置ID
		UserBean userBean = Context.getUserBean();
		String user_code = userBean.getCode();
		String odeptcode ="";
		Bean userPvlgToHT = RoleUtil.getPvlgRole(user_code);
		Bean userPvlgToHTBean = (Bean) userPvlgToHT.get("TS_XMGL_KCAP_YAPZW_PVLG");

		if (userPvlgToHTBean == null) {
			return outBean.setError("无权限");
		}
		if("0".equals(userPvlgToHTBean.getStr("publish"))&&"0".equals(userPvlgToHTBean.getStr("auto"))){
			return outBean;
		}
		int  a=0;
		if ("0".equals(userPvlgToHTBean.getStr("publish"))) {

			Bean str = (Bean) userPvlgToHTBean.get("auto");
			if (str == null) {
				return outBean.setError("无权限");
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
				return outBean.setError("无权限");
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
                +" TS_KCZGL zgl"
                +" left join ts_kczgl_group gro on gro.KCZ_ID = zgl.KCZ_ID and zgl.SERV_ID ='TS_XMGL_CCCS_KCZGL'"
                +" left join TS_KCGL k on k.GROUP_ID=gro.GROUP_ID and gro.SERV_ID ='TS_KCZGL_GROUP'"
					//TS_KCZGL
//  +" TS_XMGL_KCAP_DAPCC c"
//  +" LEFT JOIN ts_kcgl k"
//   +" ON k.kc_id = c.kc_id"
+" WHERE zgl.XM_ID = '"+xmid+"'"
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
	return outBean.setData(dataList);
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

/*
//private static final int ONETIME_EXP_NUM = 20000;
//public OutBean exp(ParamBean paramBean) {
//	paramBean.set("serv", "TS_XMGL_ALLSTUDENTS");
//	String servId = paramBean.getServId();
//	ServDefBean serv = ServUtils.getServDef(servId);
//	long count = 0;
//	long times = 0;
//	paramBean.setQueryPageShowNum(ONETIME_EXP_NUM); // 设置每页最大导出数据量
//	beforeExp(paramBean); // 执行监听方法
//	if (paramBean.getId().length() > 0) { // 支持指定记录的导出（支持多选）
//	    String searchWhere = " and " + serv.getPKey() + " in ('" + paramBean.getId().replaceAll(",", "','") + "')";
//	    paramBean.setQuerySearchWhere(searchWhere);
//	}
//	ExportExcel expExcel = new ExportExcel(serv);
//	try {
//	    OutBean outBean = queryExp(paramBean);
//	    count = outBean.getCount();
//	    // 导出第一次查询数据
//	    paramBean.setQueryPageNowPage(1); // 导出当前第几页
//	    afterExp(paramBean, outBean); // 执行导出查询后扩展方法
//	    LinkedHashMap<String, Bean> cols = outBean.getCols();
//	    cols.remove("BUTTONS");
//	    expExcel.createHeader(cols);
//	    expExcel.appendData(outBean.getDataList(), paramBean);
//
//	    // 存在多页数据
//	    if (ONETIME_EXP_NUM < count) {
//		times = count / ONETIME_EXP_NUM;
//		// 如果获取的是整页数据
//		if (ONETIME_EXP_NUM * times == count && count != 0) {
//		    times = times - 1;
//		}
//		for (int i = 1; i <= times; i++) {
//		    paramBean.setQueryPageNowPage(i + 1); // 导出当前第几页
//		    OutBean out = query(paramBean);
//		    afterExp(paramBean, out); // 执行导出查询后扩展方法
//		    expExcel.appendData(out.getDataList(), paramBean);
//		}
//	    }
//	    expExcel.addSumRow();
//	} catch (Exception e) {
//	    log.error("导出Excel文件异常" + e.getMessage(), e);
//	} finally {
//	    expExcel.close();
//	}
//	return new OutBean().setOk();
//   }
*//** 每次获取数据条数 */
private static final int ONETIME_EXP_NUM = 20000;
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
		
				//所有人员
				 //String datasql = "select * from TS_BMSH_PASS  where xm_id='"+xmid+"'";
		
		LinkedHashMap<String, Bean> cols = new LinkedHashMap<String, Bean>();
		
		String where1 = "AND USER_CODE is null ";
		List<Bean> 	pxdatalist1 = ServDao.finds("TS_BMSH_PX", where1);
		
//	    String pxcol = namecol;
//	    Bean colBean = new Bean();
//		colBean.set("SAFE_HTML", "");
//		colBean.set("ITEM_LIST_FLAG", "1");
//		colBean.set("ITEM_CODE", namecol);
//		colBean.set("EN_JSON", "");
//		colBean.set("ITEM_NAME", aa);
//		cols.put(pxcol, colBean);
		for(int  k=0;k<pxdatalist1.size();k++){
			
			if(k==0){
				//String aa = pxbean.getStr("PX_NAME");
				//String namecol = pxbean.getStr("PX_COLUMN");
				if("人力资源编码".equals("人力资源编码")){
				//String pxcol = namecol;
				Bean colBean = new Bean();
				colBean.set("SAFE_HTML", "");
				colBean.set("ITEM_LIST_FLAG", "1");
				colBean.set("ITEM_CODE", "BM_CODE");
				colBean.set("EN_JSON", "");
				colBean.set("ITEM_NAME", "人力资源编码");
				cols.put("BM_CODE", colBean);
				}
			}
			if(k==1){
				if("统一认证号".equals("统一认证号")){
					//String pxcol = namecol;
					Bean colBean = new Bean();
					colBean.set("SAFE_HTML", "");
					colBean.set("ITEM_LIST_FLAG", "1");
					colBean.set("ITEM_CODE", "TONGYI");
					colBean.set("EN_JSON", "");
					colBean.set("ITEM_NAME", "统一认证号");
					cols.put("TONGYI", colBean);
					}
			}
			if(k==2){
				if("姓名".equals("姓名")){
					//String pxcol = namecol;
					Bean colBean = new Bean();
					colBean.set("SAFE_HTML", "");
					colBean.set("ITEM_LIST_FLAG", "1");
					colBean.set("ITEM_CODE", "BM_NAME");
					colBean.set("EN_JSON", "");
					colBean.set("ITEM_NAME", "姓名");
					cols.put("BM_NAME", colBean);
					}
				
			}
			if(k==3){
				 if("性别".equals("性别")){
						//String pxcol = namecol;
						Bean colBean = new Bean();
						colBean.set("SAFE_HTML", "");
						colBean.set("ITEM_LIST_FLAG", "1");
						colBean.set("ITEM_CODE", "USER_SEX");
						colBean.set("EN_JSON", "");
						colBean.set("ITEM_NAME", "性别");
						cols.put("USER_SEX", colBean);
					}
			}
			if(k==4){
				if("一级机构".equals("一级机构")){
				    //String pxcol = namecol;
				    Bean colBean = new Bean();
					colBean.set("SAFE_HTML", "");
					colBean.set("ITEM_LIST_FLAG", "1");
					colBean.set("ITEM_CODE", "LEVEL1");
					colBean.set("EN_JSON", "");
					colBean.set("ITEM_NAME", "一级机构");
					cols.put("LEVEL1", colBean);
				}
			}
			if(k==5){
				if("二级机构".equals("二级机构")){
				    //String pxcol = namecol;
				    Bean colBean = new Bean();
					colBean.set("SAFE_HTML", "");
					colBean.set("ITEM_LIST_FLAG", "1");
					colBean.set("ITEM_CODE", "LEVEL2");
					colBean.set("EN_JSON", "");
					colBean.set("ITEM_NAME", "二级机构");
					cols.put("LEVEL2", colBean);
				}
			}
			if(k==6){
				if("三级机构".equals("三级机构")){
				    //String pxcol = namecol;
				    Bean colBean = new Bean();
					colBean.set("SAFE_HTML", "");
					colBean.set("ITEM_LIST_FLAG", "1");
					colBean.set("ITEM_CODE", "LEVEL3");
					colBean.set("EN_JSON", "");
					colBean.set("ITEM_NAME", "三级机构");
					cols.put("LEVEL3", colBean);
				}
			}
			if(k==7){
				if("四级机构".equals("四级机构")){
				    //String pxcol = namecol;
				    Bean colBean = new Bean();
					colBean.set("SAFE_HTML", "");
					colBean.set("ITEM_LIST_FLAG", "1");
					colBean.set("ITEM_CODE", "LEVEL4");
					colBean.set("EN_JSON", "");
					colBean.set("ITEM_NAME", "四级机构");
					cols.put("LEVEL4", colBean);
				}
			}
			if(k==8){
				if("五级机构".equals("五级机构")){
				    //String pxcol = namecol;
				    Bean colBean = new Bean();
					colBean.set("SAFE_HTML", "");
					colBean.set("ITEM_LIST_FLAG", "1");
					colBean.set("ITEM_CODE", "LEVEL5");
					colBean.set("EN_JSON", "");
					colBean.set("ITEM_NAME", "五级机构");
					cols.put("LEVEL5", colBean);
				}
			}
			if(k==9){
				if("考试类别".equals("考试类别")){
				    //String pxcol = namecol;
				    Bean colBean = new Bean();
					colBean.set("SAFE_HTML", "");
					colBean.set("ITEM_LIST_FLAG", "1");
					colBean.set("ITEM_CODE", "BM_LB");
					colBean.set("EN_JSON", "");
					colBean.set("ITEM_NAME", "考试类别");
					cols.put("BM_LB", colBean);
				}
			}
			if(k==10){
				if("考试序列".equals("考试序列")){
				    //String pxcol = namecol;
				    Bean colBean = new Bean();
					colBean.set("SAFE_HTML", "");
					colBean.set("ITEM_LIST_FLAG", "1");
					colBean.set("ITEM_CODE", "BM_XL");
					colBean.set("EN_JSON", "");
					colBean.set("ITEM_NAME", "考试序列");
					cols.put("BM_XL", colBean);
				}
			}
			if(k==11){
				if("考试模块".equals("考试模块")){
				    //String pxcol = namecol;
				    Bean colBean = new Bean();
					colBean.set("SAFE_HTML", "");
					colBean.set("ITEM_LIST_FLAG", "1");
					colBean.set("ITEM_CODE", "BM_MK");
					colBean.set("EN_JSON", "");
					colBean.set("ITEM_NAME", "考试模块");
					cols.put("BM_MK", colBean);
				}
			}
			if(k==12){
				if("考试层级".equals("考试层级")){
				    //String pxcol = namecol;
				    Bean colBean = new Bean();
					colBean.set("SAFE_HTML", "");
					colBean.set("ITEM_LIST_FLAG", "1");
					colBean.set("ITEM_CODE", "BM_TYPE");
					colBean.set("EN_JSON", "");
					colBean.set("ITEM_NAME", "考试层级");
					cols.put("BM_TYPE", colBean);
				}
			}
			
		}
		
//		for (Bean pxbean : pxdatalist1) {//表头
//			String aa = pxbean.getStr("PX_NAME");
//			String namecol = pxbean.getStr("PX_COLUMN");
//			if("人力资源编码".equals("人力资源编码")){
//			String pxcol = namecol;
//			Bean colBean = new Bean();
//			colBean.set("SAFE_HTML", "");
//			colBean.set("ITEM_LIST_FLAG", "1");
//			colBean.set("ITEM_CODE", "BM_CODE");
//			colBean.set("EN_JSON", "");
//			colBean.set("ITEM_NAME", "人力资源编码");
//			cols.put("BM_CODE", colBean);
//			}
//             if("统一认证号".equals("统一认证号")){
//				String pxcol = namecol;
//				Bean colBean = new Bean();
//				colBean.set("SAFE_HTML", "");
//				colBean.set("ITEM_LIST_FLAG", "1");
//				colBean.set("ITEM_CODE", "TONGYI");
//				colBean.set("EN_JSON", "");
//				colBean.set("ITEM_NAME", "统一认证号");
//				cols.put("TONGYI", colBean);
//				}
//                if("姓名".equals("姓名")){
//						String pxcol = namecol;
//						Bean colBean = new Bean();
//						colBean.set("SAFE_HTML", "");
//						colBean.set("ITEM_LIST_FLAG", "1");
//						colBean.set("ITEM_CODE", "BM_NAME");
//						colBean.set("EN_JSON", "");
//						colBean.set("ITEM_NAME", "姓名");
//						cols.put("BM_NAME", colBean);
//						}
//                if("性别".equals("性别")){
//							String pxcol = namecol;
//							Bean colBean = new Bean();
//							colBean.set("SAFE_HTML", "");
//							colBean.set("ITEM_LIST_FLAG", "1");
//							colBean.set("ITEM_CODE", "USER_SEX");
//							colBean.set("EN_JSON", "");
//							colBean.set("ITEM_NAME", aa);
//							cols.put("USER_SEX", colBean);
//						}
//                if("一级机构".equals(aa)){
//						    String pxcol = namecol;
//						    Bean colBean = new Bean();
//							colBean.set("SAFE_HTML", "");
//							colBean.set("ITEM_LIST_FLAG", "1");
//							colBean.set("ITEM_CODE", namecol);
//							colBean.set("EN_JSON", "");
//							colBean.set("ITEM_NAME", aa);
//							cols.put(pxcol, colBean);
//						}
//                if("二级机构".equals(aa)){
//						    String pxcol = namecol;
//						    Bean colBean = new Bean();
//							colBean.set("SAFE_HTML", "");
//							colBean.set("ITEM_LIST_FLAG", "1");
//							colBean.set("ITEM_CODE", namecol);
//							colBean.set("EN_JSON", "");
//							colBean.set("ITEM_NAME", aa);
//							cols.put(pxcol, colBean);
//						}
//                if("三级机构".equals(aa)){
//						    String pxcol = namecol;
//						    Bean colBean = new Bean();
//							colBean.set("SAFE_HTML", "");
//							colBean.set("ITEM_LIST_FLAG", "1");
//							colBean.set("ITEM_CODE", namecol);
//							colBean.set("EN_JSON", "");
//							colBean.set("ITEM_NAME", aa);
//							cols.put(pxcol, colBean);
//						}
//                if("四级机构".equals(aa)){
//						    String pxcol = namecol;
//						    Bean colBean = new Bean();
//							colBean.set("SAFE_HTML", "");
//							colBean.set("ITEM_LIST_FLAG", "1");
//							colBean.set("ITEM_CODE", namecol);
//							colBean.set("EN_JSON", "");
//							colBean.set("ITEM_NAME", aa);
//							cols.put(pxcol, colBean);
//						}
//                if("五级机构".equals(aa)){
//						    String pxcol = namecol;
//						    Bean colBean = new Bean();
//							colBean.set("SAFE_HTML", "");
//							colBean.set("ITEM_LIST_FLAG", "1");
//							colBean.set("ITEM_CODE", namecol);
//							colBean.set("EN_JSON", "");
//							colBean.set("ITEM_NAME", aa);
//							cols.put(pxcol, colBean);
//						}
//                if("考试类别".equals(aa)){
//						    String pxcol = namecol;
//						    Bean colBean = new Bean();
//							colBean.set("SAFE_HTML", "");
//							colBean.set("ITEM_LIST_FLAG", "1");
//							colBean.set("ITEM_CODE", namecol);
//							colBean.set("EN_JSON", "");
//							colBean.set("ITEM_NAME", aa);
//							cols.put(pxcol, colBean);
//						}
//                if("考试序列".equals(aa)){
//						    String pxcol = namecol;
//						    Bean colBean = new Bean();
//							colBean.set("SAFE_HTML", "");
//							colBean.set("ITEM_LIST_FLAG", "1");
//							colBean.set("ITEM_CODE", namecol);
//							colBean.set("EN_JSON", "");
//							colBean.set("ITEM_NAME", aa);
//							cols.put(pxcol, colBean);
//						}
//                if("考试模块".equals(aa)){
//						    String pxcol = namecol;
//						    Bean colBean = new Bean();
//							colBean.set("SAFE_HTML", "");
//							colBean.set("ITEM_LIST_FLAG", "1");
//							colBean.set("ITEM_CODE", namecol);
//							colBean.set("EN_JSON", "");
//							colBean.set("ITEM_NAME", aa);
//							cols.put(pxcol, colBean);
//						}
//                if("考试层级".equals(aa)){
//						    String pxcol = namecol;
//						    Bean colBean = new Bean();
//							colBean.set("SAFE_HTML", "");
//							colBean.set("ITEM_LIST_FLAG", "1");
//							colBean.set("ITEM_CODE", namecol);
//							colBean.set("EN_JSON", "");
//							colBean.set("ITEM_NAME", aa);
//							cols.put(pxcol, colBean);
//						}
//			
//			
//		}
		
	
		//if(!dataList.isEmpty()){
	    Bean StartTimeBean = new Bean();
	    StartTimeBean.set("SAFE_HTML", "");
	    StartTimeBean.set("ITEM_LIST_FLAG", "1");
	    StartTimeBean.set("ITEM_CODE", "StartTime");
	    StartTimeBean.set("EN_JSON", "");
	    StartTimeBean.set("ITEM_NAME", "考试开始时间");
		cols.put("StartTime", StartTimeBean);
		Bean EndTimeBean = new Bean();
		EndTimeBean.set("SAFE_HTML", "");
		EndTimeBean.set("ITEM_LIST_FLAG", "1");
		EndTimeBean.set("ITEM_CODE", "EndTime");
		EndTimeBean.set("EN_JSON", "");
		EndTimeBean.set("ITEM_NAME", "考试结束时间");
		cols.put("EndTime", EndTimeBean);
		Bean KSZWNumBean = new Bean();
		KSZWNumBean.set("SAFE_HTML", "");
		KSZWNumBean.set("ITEM_LIST_FLAG", "1");
		KSZWNumBean.set("ITEM_CODE", "KSZWNum");
		KSZWNumBean.set("EN_JSON", "");
		KSZWNumBean.set("ITEM_NAME", "考试座位号");
		cols.put("EndKSZWNum", KSZWNumBean);
		Bean KCNameBean = new Bean();
		KCNameBean.set("SAFE_HTML", "");
		KCNameBean.set("ITEM_LIST_FLAG", "1");
		KCNameBean.set("ITEM_CODE", "KCName");
		KCNameBean.set("EN_JSON", "");
		KCNameBean.set("KCName", "考场名称");
		cols.put("EndTime", EndTimeBean);
		Bean KCAdenBean = new Bean();
		KCAdenBean.set("SAFE_HTML", "");
		KCAdenBean.set("ITEM_LIST_FLAG", "1");
		KCAdenBean.set("ITEM_CODE", "KCAden");
		KCAdenBean.set("EN_JSON", "");
		KCAdenBean.set("ITEM_NAME", "考场地址");
		cols.put("KCAden", KCAdenBean);
		Bean KCJKipBean = new Bean();
		KCJKipBean.set("SAFE_HTML", "");
		KCJKipBean.set("ITEM_LIST_FLAG", "1");
		KCJKipBean.set("ITEM_CODE", "KCJKip");
		KCJKipBean.set("EN_JSON", "");
		KCJKipBean.set("ITEM_NAME", "考场监控机ip");
		cols.put("KCJKip", KCJKipBean);
		Bean KCTMipBean = new Bean();
		KCTMipBean.set("SAFE_HTML", "");
		KCTMipBean.set("ITEM_LIST_FLAG", "1");
		KCTMipBean.set("ITEM_CODE", "KCTMip");
		KCTMipBean.set("EN_JSON", "");
		KCTMipBean.set("ITEM_NAME", "考场用机ip");
		cols.put("KCTMip", KCTMipBean);
		Bean qjYesNoBean = new Bean();
		qjYesNoBean.set("SAFE_HTML", "");
		qjYesNoBean.set("ITEM_LIST_FLAG", "1");
		qjYesNoBean.set("ITEM_CODE", "qjYesNo");
		qjYesNoBean.set("EN_JSON", "");
		qjYesNoBean.set("ITEM_NAME", "是否请假");
		cols.put("qjYesNo", qjYesNoBean);
		Bean qjStatusBean = new Bean();
		qjStatusBean.set("SAFE_HTML", "");
		qjStatusBean.set("ITEM_LIST_FLAG", "1");
		qjStatusBean.set("ITEM_CODE", "qjResult");
		qjStatusBean.set("EN_JSON", "");
		qjStatusBean.set("ITEM_NAME", "请假状态");
		cols.put("qjResult", qjStatusBean);
		Bean jkYesNoBean = new Bean();
		jkYesNoBean.set("SAFE_HTML", "");
		jkYesNoBean.set("ITEM_LIST_FLAG", "1");
		jkYesNoBean.set("ITEM_CODE", "jkYesNo");
		jkYesNoBean.set("EN_JSON", ""); 
		jkYesNoBean.set("ITEM_NAME", "是否借考");
		cols.put("jkYesNo", jkYesNoBean);
		Bean jkStatusBean = new Bean();
		jkStatusBean.set("SAFE_HTML", "");
		jkStatusBean.set("ITEM_LIST_FLAG", "1");
		jkStatusBean.set("ITEM_CODE", "jkResult");
		jkStatusBean.set("EN_JSON", "");
		jkStatusBean.set("ITEM_NAME", "借考状态");
		cols.put("jkResult", jkStatusBean);
		Bean jkCityBean = new Bean();
		jkCityBean.set("SAFE_HTML", "");
		jkCityBean.set("ITEM_LIST_FLAG", "1");
		jkCityBean.set("ITEM_CODE", "jkCity");
		jkCityBean.set("EN_JSON", "");
		jkCityBean.set("ITEM_NAME", "借考城市");
		cols.put("jkCity", jkCityBean);
		Bean shStyleBean = new Bean();
		shStyleBean.set("SAFE_HTML", "");
		shStyleBean.set("ITEM_LIST_FLAG", "1");
		shStyleBean.set("ITEM_CODE", "shStyle");
		shStyleBean.set("EN_JSON", "");
		shStyleBean.set("ITEM_NAME", "审核方式");
		cols.put("shStyle", shStyleBean);
		Bean node1Bean = new Bean();
		node1Bean.set("SAFE_HTML", "");
		node1Bean.set("ITEM_LIST_FLAG", "1");
		node1Bean.set("ITEM_CODE", "node1");
		node1Bean.set("EN_JSON", "");
		node1Bean.set("ITEM_NAME", "终级审核");
		cols.put("node1", node1Bean);
		Bean mide1Bean = new Bean();
		mide1Bean.set("SAFE_HTML", "");
		mide1Bean.set("ITEM_LIST_FLAG", "1");
		mide1Bean.set("ITEM_CODE", "mide1");
		mide1Bean.set("EN_JSON", "");
		mide1Bean.set("ITEM_NAME", "终级审核意见");
		cols.put("mide1", mide1Bean);
		
		Bean node2Bean = new Bean();
		node2Bean.set("SAFE_HTML", "");
		node2Bean.set("ITEM_LIST_FLAG", "1");
		node2Bean.set("ITEM_CODE", "node2");
		node2Bean.set("EN_JSON", "");
		node2Bean.set("ITEM_NAME", "一级审核");
		cols.put("node2", node2Bean);
		Bean mide2Bean = new Bean();
		mide2Bean.set("SAFE_HTML", "");
		mide2Bean.set("ITEM_LIST_FLAG", "1");
		mide2Bean.set("ITEM_CODE", "mide2");
		mide2Bean.set("EN_JSON", "");
		mide2Bean.set("ITEM_NAME", "一级审核意见");
		cols.put("mide2", mide2Bean);
		
		Bean node3Bean = new Bean();
		node3Bean.set("SAFE_HTML", "");
		node3Bean.set("ITEM_LIST_FLAG", "1");
		node3Bean.set("ITEM_CODE", "node3");
		node3Bean.set("EN_JSON", "");
		node3Bean.set("ITEM_NAME", "二级审核");
		cols.put("node3", node3Bean);
		Bean mide3Bean = new Bean();
		mide3Bean.set("SAFE_HTML", "");
		mide3Bean.set("ITEM_LIST_FLAG", "1");
		mide3Bean.set("ITEM_CODE", "mide3");
		mide3Bean.set("EN_JSON", "");
		mide3Bean.set("ITEM_NAME", "二级审核意见");
		cols.put("mide3", mide3Bean);
		
		Bean node4Bean = new Bean();
		node4Bean.set("SAFE_HTML", "");
		node4Bean.set("ITEM_LIST_FLAG", "1");
		node4Bean.set("ITEM_CODE", "node4");
		node4Bean.set("EN_JSON", "");
		node4Bean.set("ITEM_NAME", "三级审核");
		cols.put("node4", node4Bean);
		Bean mide4Bean = new Bean();
		mide4Bean.set("SAFE_HTML", "");
		mide4Bean.set("ITEM_LIST_FLAG", "1");
		mide4Bean.set("ITEM_CODE", "mide4");
		mide4Bean.set("EN_JSON", "");
		mide4Bean.set("ITEM_NAME", "三级审核意见");
		cols.put("mide4", mide4Bean);
		
		Bean node5Bean = new Bean();
		node5Bean.set("SAFE_HTML", "");
		node5Bean.set("ITEM_LIST_FLAG", "1");
		node5Bean.set("ITEM_CODE", "node5");
		node5Bean.set("EN_JSON", "");
		node5Bean.set("ITEM_NAME", "最低审核");
		cols.put("node5", node5Bean);
		Bean mide5Bean = new Bean();
		mide5Bean.set("SAFE_HTML", "");
		mide5Bean.set("ITEM_LIST_FLAG", "1");
		mide5Bean.set("ITEM_CODE", "mide5");
		mide5Bean.set("EN_JSON", "");
		mide5Bean.set("ITEM_NAME", "最低审核意见");
		cols.put("mide5", mide5Bean);
	
		//}
	
		String  table ="";
		List<Bean> finalList = new ArrayList<Bean>();
		for(int i=0;i<3;i++){
			if(i==0){
				table="TS_BMSH_PASS";
			}else if(i==1){
				table="TS_BMSH_NOPASS";
			}else  if(i==2){
				table="TS_BMSH_STAY";
			}
			
		
		String datasql = "SELECT  a.* ,b.SJ_ID,b.KC_ID,b.ZW_ID  FROM "+table+" a  LEFT JOIN  TS_XMGL_KCAP_YAPZW b  ON a.SH_ID=b.SH_ID WHERE  a.XM_ID='"+xmid+"'";  
				// 
				 List<Bean>	 dataList = Transaction.getExecutor().query(datasql);
				 
				 
			

			// 判断user_code 是否为空 若为空则 导出所有


			// 排序用的 parr存读取th
			
			// 查询出所有的 待审核记录
			for (Bean bean : dataList) {
				String work_num = bean.getStr("BM_CODE");//人力资源编码
				String sjid=bean.getStr("SJ_ID");
				String kcid=bean.getStr("KC_ID");
				String zwid=bean.getStr("ZW_ID");
				String  shid =bean.getStr("SH_ID");
				
				String bmid=bean.getStr("BM_ID");
				//Bean kcInfoBean=getKcMessages( work_num,xmid,lbCode,xlCode,mkCode,djCode);
				
				Bean kcInfoBean=getKcMessages(sjid,kcid ,zwid,work_num,xmid,shid,bmid);
				String 	StartTime=kcInfoBean.getStr("STARTTIME");
				String 	EndTime=kcInfoBean.getStr("ENDTIME");
				String 	kcName=kcInfoBean.getStr("kcName");
				String 	kcAddress=kcInfoBean.getStr("kcAddress");
				String 	jkips=kcInfoBean.getStr("jkips");
				String 	zwhNum=kcInfoBean.getStr("zwhNum");
				String 	zwIp=kcInfoBean.getStr("zwIp");
				String qjYesNo=kcInfoBean.getStr("qjYesNo");
				String 	qjResult=kcInfoBean.getStr("qjResult");
				String jkYesNo=kcInfoBean.getStr("jkYesNo");
				String 	jkResult=kcInfoBean.getStr("jkResult");
				String 	jkcity=kcInfoBean.getStr("jkcity");
				String 	shStyle=kcInfoBean.getStr("shStyle");
				String 	node1=kcInfoBean.getStr("node1");
				String 	mide1=kcInfoBean.getStr("mide1");
				String 	node2=kcInfoBean.getStr("node2");
				String 	mide2=kcInfoBean.getStr("mide2");
				String 	node3=kcInfoBean.getStr("node3");
				String 	mide3=kcInfoBean.getStr("mide3");
				String 	node4=kcInfoBean.getStr("node4");
				String 	mide4=kcInfoBean.getStr("mide4");
				String 	node5=kcInfoBean.getStr("node5");
				String 	mide5=kcInfoBean.getStr("mide5");
				
				Bean userBean = getUserInfo1(work_num);
				Bean newBean = new Bean();
				newBean.set("StartTime", StartTime);
				newBean.set("EndTime", EndTime);
				newBean.set("KCName", kcName);
				newBean.set("KCAden", kcAddress);
				newBean.set("KCJKip", jkips);
				newBean.set("KSZWNum", zwhNum);
				newBean.set("KCTMip", zwIp);
				newBean.set("qjYesNo", qjYesNo);
				newBean.set("qjResult", qjResult);
				newBean.set("jkYesNo", jkYesNo);
				newBean.set("jkResult", jkResult);
				newBean.set("jkCity", jkcity);
				newBean.set("shStyle", shStyle);
				newBean.set("node1", node1);
				newBean.set("mide1", mide1);
				newBean.set("node2", node2);
				newBean.set("mide2", mide2);
				newBean.set("node3", node3);
				newBean.set("mide3", mide3);
				newBean.set("node4", node4);
				newBean.set("mide4", mide4);
				newBean.set("node5", node5);
				newBean.set("mide5", mide5);
				// for循环排序bean
				for (Bean pxbean : pxdatalist1) {
					String namecol = pxbean.getStr("PX_COLUMN");
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
					/*if ("SH_OTHER".equals(namecol)) {
						// 其它办理人
						ParamBean parambeansss = new ParamBean();
						parambeansss.set("codes", bean.getStr("SH_OTHER"));
						Bean outBeans = ServMgr.act("TS_BMSH_STAY", "getusername",
								parambeansss);
						name = outBeans.getStr("usernames");
					}*/
					if("SH_STATUS".equals(namecol)){
						//审核状态;
//						name = "审核通过";
					
					if(i==0){
						name = "审核通过";
					}else if(i==1){
						name = "审核不通过";
					}else  if(i==2){
						name = "待审核";
					}
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
		if (user_sex == 1) {
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


//考场相关信息( work_num,xmid,lbCode,xlCode,mkCode,djCode);
public Bean getKcMessages(String sjid,String kcid ,String zwid,String  user_code,String xm_id,String sh_id,String bm_id) {
	Bean  KcInfoBean=new Bean();
	//String yapWhere="AND SH_ID='"+shid+"'";
	//String yapWhere="AND XM_ID='"+xmid+"' AND U_CODE='"+work_num+"' AND BM_LB='"+lbCode+"' AND BM_XL='"+xlCode+"' AND BM_MK ='"+mkCode+"' AND BM_LV='"+djCode+"'";
	//List<Bean> 	Beanlist=ServDao.finds("TS_XMGL_KCAP_YAPZW", yapWhere);
	String startTime = null;
	String endTime= null;
	String kcName= null;
	String  kcAddress=null;
	String  jkips=null;
	String  zwhNum=null;
	String zwIp=null;
	String  qjYesNo=null;
	String  qjResult=null;
	String  jkYesNo=null;
	String  jkResult=null;
	String jkcity=null;
	String  shStyle=null; 
	String  node1=null;
	String  mide1=null;
	String  node2=null;
	String  mide2=null;
	String  node3=null;
	String  mide3=null;
	String  node4=null;
	String  mide4=null;
	String  node5=null;
	String  mide5=null;
	
	
	//if(Beanlist !=null  && !Beanlist.isEmpty()){
			//String shId=Beanlist.get(0).getStr("SJ_ID");
			//String ccId=Beanlist.get(0).getStr("KC_ID");
			//String ZWId=Beanlist.get(0).getStr("ZW_ID");
			String ccsjWhere="AND SJ_ID='"+sjid+"'";
			List<Bean> 	CCSJBeanlist=ServDao.finds("TS_XMGL_KCAP_DAPCC_CCSJ", ccsjWhere);//开始起止时间
			if(CCSJBeanlist !=null  && !CCSJBeanlist.isEmpty()){
				startTime=CCSJBeanlist.get(0).getStr("SJ_START");
				 endTime=CCSJBeanlist.get(0).getStr("SJ_END");
			}
			
//			List<Bean> 	dapkcBeanlist=ServDao.finds("TS_KCGL", kcWhere);//考场名称，考场地址
//			if(dapkcBeanlist !=null  && !dapkcBeanlist.isEmpty()){
//				kcName=dapkcBeanlist.get(0).getStr("KC_NAME");
//				kcAddress=dapkcBeanlist.get(0).getStr("KC_ADDRESS");
//			}
			
			String   kcgldatasql="select  t.KC_NAME,t.KC_ADDRESS   from  TS_KCGL t  WHERE  KC_ID='"+kcid+"'";
			
			 List<Bean>	 dapkcBeanlist = Transaction.getExecutor().query(kcgldatasql);	
			 if(dapkcBeanlist !=null  && !dapkcBeanlist.isEmpty()){
					kcName=dapkcBeanlist.get(0).getStr("KC_NAME");
					kcAddress=dapkcBeanlist.get(0).getStr("KC_ADDRESS");
				}
			
			 String kcWhere="AND  KC_ID='"+kcid+"'";
			List<Bean> jkipBeanlist=ServDao.finds("TS_KCGL_JKIP", kcWhere);//监控机
			if(jkipBeanlist !=null && !jkipBeanlist.isEmpty()){
				for(int jk=0;jk<jkipBeanlist.size();jk++){
					 jkips=jkipBeanlist.get(jk).getStr("JKIP_IP");
					 jkips+=",";
				}
			}
			String   zwdatasql="select  t.ZW_ZWH_SJ,t.zw_ip   from  TS_KCGL_ZWDYB t  WHERE  ZW_ID='"+zwid+"'";
			//String zwidWhere="AND  ZW_ID='"+zwid+"'";
			//List<Bean> zwidBeanlist=ServDao.finds("TS_KCGL_ZWDYB", zwidWhere);//座位号。机ip
			List<Bean>	 zwidBeanlist = Transaction.getExecutor().query(zwdatasql);	
	        if(zwidBeanlist !=null && !zwidBeanlist.isEmpty()){
	        	zwhNum=zwidBeanlist.get(0).getStr("ZW_ZWH_SJ");
	        	zwIp=zwidBeanlist.get(0).getStr("zw_ip");
	        }
	//}
	        //请假
	        String  qjdatasql="select  r.QJ_STATUS , r.QJ_KSNAME  from  TS_QJLB_QJ r where  xm_Id='"+xm_id+"' AND  USER_CODE='"+user_code+"'";
	      List<Bean> qjbean= Transaction.getExecutor().query(qjdatasql);
	        if(qjbean !=null    && !qjbean.isEmpty()){
	        	for(int j=0;j<qjbean.size();j++){
	        		String qjKsName=qjbean.get(j).getStr("QJ_KSNAME");
	        		if(qjKsName.indexOf(sh_id)>-1){
	        	        qjYesNo="是";
	        	        String status=qjbean.get(j).getStr("QJ_STATUS");
	        	            if("1".equals(status)){
	        		            qjResult="审批中";
		                    }else if("2".equals(status)){
		        	            qjResult="已通过";
		                    }else if("3".equals(status)){
		        	            qjResult="被退回";
		                    }
	               }else{
	        	     qjYesNo="否";
	               }
	        	}
        	}else{
       	     qjYesNo="否";
            }		
	        //借考
	        String  jkdatasql="select  r.JK_STATUS,r.JK_JKCITY ,r.JK_KSNAME   from  TS_JKLB_JK r where  xm_Id='"+xm_id+"' AND  USER_CODE='"+user_code+"'";
	      List<Bean> jkbean= Transaction.getExecutor().query(jkdatasql);
	        if(jkbean !=null    && !jkbean.isEmpty()){
	        	for(int k=0;k<jkbean.size();k++){
	        		String jkKsName=jkbean.get(k).getStr("JK_KSNAME");
	        		if(jkKsName.indexOf(sh_id)>-1){
	        	      jkYesNo="是";
	        	      jkcity=jkbean.get(k).getStr("JK_JKCITY");
	        	      String status=jkbean.get(k).getStr("JK_STATUS");
	        	      if("1".equals(status)){
	        		     jkResult="审批中";
		              }else if("2".equals(status)){
		        	     jkResult="已通过";
		              }else if("3".equals(status)){
		              	jkResult="被退回";
		              }
	              }else{
	        	     jkYesNo="否";
	              }
	        	}
         }else{
       	     qjYesNo="否";
         }  
	        
	     String mindDataSql="SELECT  *   FROM  ts_comm_mind    WHERE  data_ID  ='"+bm_id+"'";    
	     List<Bean> mindData= Transaction.getExecutor().query(mindDataSql);
	     if(mindData !=null    && !mindData.isEmpty()){
	    	 String shfs=mindData.get(0).getStr("SH_UCODE");
	    	 if(!shfs.isEmpty() && !"".equals(shfs)){
	    		 shStyle="手动审核";
	    	 }else{
	    		 shStyle="自动审核"; 
	    	 }
	    	 for(int k=0;k<jkbean.size();k++){
	    		String nodeName= mindData.get(k).getStr("SH_NODE");
	    	   int  shMide= mindData.get(k).getInt("SH_STATUS");
	    		if("最终审核级别".equals(nodeName)){
	    			node1=nodeName;
	    			if(shMide==1){
	    				mide1="同意";
	    			}else if(shMide==2){
	    				mide1="不同意";
	    			}
	    			
	    		}else if("一级审核级别".equals(nodeName)){
	    			node2=nodeName;
	    			if(shMide==1){
	    				mide2="同意";
	    			}else if(shMide==2){
	    				mide2="不同意";
	    			}
	    			
	    		}else if("二级审核级别".equals(nodeName)){
	    			node3=nodeName;
	    			if(shMide==1){
	    				mide3="同意";
	    			}else if(shMide==2){
	    				mide3="不同意";
	    			}
	    			
	    	 }else  if("三级审核级别".equals(nodeName)){
	    		    node4=nodeName;
	    			if(shMide==1){
	    				mide4="同意";
	    			}else if(shMide==2){
	    				mide4="不同意";
	    			}
	    			
	    	 }else if ("最低审核级别".equals(nodeName)){
	    		    node5=nodeName;
	    			if(shMide==1){
	    				mide5="同意";
	    			}else if(shMide==2){
	    				mide5="不同意";
	    			}
	    			
	    	 }
	     }
	     }    
	KcInfoBean.set("STARTTIME", startTime);
	KcInfoBean.set("ENDTIME", endTime);
	KcInfoBean.set("kcName", kcName);
	KcInfoBean.set("kcAddress", kcAddress);
	KcInfoBean.set("jkips", jkips);
	KcInfoBean.set("zwhNum", zwhNum);
	KcInfoBean.set("zwIp", zwIp);
	KcInfoBean.set("qjYesNo", qjYesNo);
	KcInfoBean.set("qjResult", qjResult);
	KcInfoBean.set("jkYesNo", jkYesNo);
	KcInfoBean.set("jkResult", jkResult);
	KcInfoBean.set("jkcity", jkcity);
	KcInfoBean.set("shStyle", shStyle);//审核方式
	KcInfoBean.set("node1", node1);//1节点名称
	KcInfoBean.set("mide1", mide1);//1节点名称
	KcInfoBean.set("node2", node2);//2节点名称
	KcInfoBean.set("mide2", mide2);//2节点名称
	KcInfoBean.set("node3", node3);//3节点名称
	KcInfoBean.set("mide3", mide3);//4节点名称
	KcInfoBean.set("node4", node4);//1节点名称
	KcInfoBean.set("mide4", mide4);//1节点名称
	KcInfoBean.set("node5", node5);//1节点名称
	KcInfoBean.set("mide5", mide5);//1节点名称
	
	return KcInfoBean;
	
}


/**
 * 导出辖内报名
 */
//	public OutBean expwithdata(ParamBean paramBean){
//		String xmid = paramBean.getStr("xmid");
//		ParamBean parr = new ParamBean();
//		UserBean userBean1 = Context.getUserBean();
//		String user_code1 = "";
//		if (userBean1.isEmpty()) {
//			return new OutBean().setError("ERROR:user_code 为空");
//		} else {
//			user_code1 = userBean1.getStr("USER_CODE");
//		}
//		parr.copyFrom(paramBean);
//		parr.setServId("TS_BMSH_PX");
//		String servId = paramBean.getServId();
//		ServDefBean serv = ServUtils.getServDef(servId);
//		long count = 0;
//		long times = 0;
//		paramBean.setQueryPageShowNum(ONETIME_EXP_NUM); // 设置每页最大导出数据量
//		String searchWhere = "";
//		beforeExp(paramBean); // 执行监听方法
//		/*	String xianei = paramBean.getStr("xianei");*/
//			//当前审核人
//			UserBean user = Context.getUserBean();
//			
//			Bean userPvlgToHT = RoleUtil.getPvlgRole(user.getCode(),"TS_BMGL_XNBM");
//			Bean userPvlgToHTBean = (Bean) userPvlgToHT.get("TS_BMGL_XNBM_PVLG");
//			Bean str = (Bean)userPvlgToHTBean.get("XN_BM");
//			String dept_code = str.getStr("ROLE_DCODE");
//			if("".equals(dept_code)){
//				dept_code=user.getStr("ODEPT_CODE");
//			}
//			dept_code = dept_code.substring(0,10);
//		
//			
//			List<Bean> dataList=null;
//				if(dept_code.equals("0010100000")){
//					//所有人员
//					 String datasql = "select * from TS_BMSH_PASS  where xm_id='"+xmid+"'";
//					
//					 dataList = Transaction.getExecutor().query(datasql);
//					 
//				}
////					 else{
////					/*List<DeptBean> finds = OrgMgr.getChildDepts(compycode, user.getODeptCode());
////					for (Bean bean : finds) {
////						dept_code+=","+bean.getStr("DEPT_CODE");
////					}
////					deptwhere = "AND S_DEPT IN ("+dept_code+")";*/
////					DeptBean dept = OrgMgr.getDept(dept_code);
////					String codepath = dept.getCodePath();
////					String sql = "select * from TS_BMSH_PASS a where exists(select dept_code from sy_org_dept b where code_path like concat('"+codepath+"','%') and a.s_dept=b.dept_code and s_flag='1') and xm_id='"+xmid+"'";
////					int count2 = Transaction.getExecutor().count(sql);
////					if (count2 > EXCEL_MAX_NUM) {
////						return new OutBean().setError("导出数据总条数大于Excel最大行数："
////								+ EXCEL_MAX_NUM);
////					}
////					dataList = Transaction.getExecutor().query(sql);
////					  
////				}
//				
//				List<Bean> finalList = new ArrayList<Bean>();
//
//				// 判断user_code 是否为空 若为空则 导出所有
//
//				searchWhere = " AND USER_CODE=" + "'" + user_code1 + "' order by cast(PX_XUHAO as SIGNED)";
//
//				// 排序用的 parr存读取th
//				parr.setQuerySearchWhere(searchWhere);
//				LinkedHashMap<String, Bean> cols = new LinkedHashMap<String, Bean>();
//				List<Bean> pxdatalist1 = ServDao.finds("TS_BMSH_PX", searchWhere);
//				if (pxdatalist1.size() == 0) {
//					String where1 = "AND USER_CODE is null ";
//					pxdatalist1 = ServDao.finds("TS_BMSH_PX", where1);
//				}
//				
//				Bean BmIdBean = new Bean();
//				BmIdBean.set("SAFE_HTML", "");
//				BmIdBean.set("ITEM_LIST_FLAG", "1");
//				BmIdBean.set("ITEM_CODE", "BMID");
//				BmIdBean.set("EN_JSON", "");
//				BmIdBean.set("ITEM_NAME", "报名编码");
//				cols.put("BMID", BmIdBean);
//				// 查询出所有的 待审核记录
//				for (Bean bean : dataList) {
//					String work_num = bean.getStr("BM_CODE");
//					Bean userBean = getUserInfo1(work_num);
//					Bean newBean = new Bean();
//					newBean.set("BMID", bean.getStr("BM_ID"));
//					// for循环排序bean
//					for (Bean pxbean : pxdatalist1) {
//						String aa = pxbean.getStr("PX_NAME");
//						String namecol = pxbean.getStr("PX_COLUMN");
//						String pxcol = namecol;
//						Bean colBean = new Bean();
//
//						colBean.set("SAFE_HTML", "");
//						colBean.set("ITEM_LIST_FLAG", "1");
//						colBean.set("ITEM_CODE", namecol);
//						colBean.set("EN_JSON", "");
//						colBean.set("ITEM_NAME", aa);
//						cols.put(pxcol, colBean);
//
//						// 字段
//						// 如果 有值 赋值
//						String name = bean.getStr(namecol);
//						if (!"".equals(bean.getStr(namecol))) {
//							newBean.set(namecol, bean.getStr(namecol));
//						}
//						if (!"".equals(userBean.getStr(namecol))) {
//							newBean.set(namecol, userBean.getStr(namecol));
//							name = userBean.getStr(namecol);
//						}
//						if ("".equals(bean.getStr(namecol))
//								&& "".equals(userBean.getStr(namecol))) {
//							newBean.set(namecol, "");
//						}
//						if ("SH_OTHER".equals(namecol)) {
//							// 其它办理人
//							ParamBean parambeansss = new ParamBean();
//							parambeansss.set("codes", bean.getStr("SH_OTHER"));
//							Bean outBeans = ServMgr.act("TS_BMSH_STAY", "getusername",
//									parambeansss);
//							name = outBeans.getStr("usernames");
//						}
//						if("SH_STATUS".equals(namecol)){
//							//审核状态;
//							name = "审核通过";
//						}
//						if ("JOB_LB".equals(namecol)) {
//							name = bean.getStr("BM_LB");
//						}
//						if ("JOB_XL".equals(namecol)) {
//							name = bean.getStr("BM_XL");
//						}
//						if ("TONGYI".equals(namecol)) {
//							name = bean.getStr("BM_CODE");
//						}
//						String BM_TYPE = "";
//						if ("BM_TYPE".equals(namecol)) {
//							if ("1".equals(bean.getStr("BM_TYPE"))) {
//								BM_TYPE = "初级";
//							} else if ("2".equals(bean.getStr("BM_TYPE"))) {
//								BM_TYPE = "中级";
//							} else {
//								BM_TYPE = "高级";
//							}
//							name = BM_TYPE;
//
//						}
//						newBean.set(namecol, name);
//						newBean.set("_ROWNUM_", "");
//						newBean.set("ROWNUM_", "");
//					}
//					finalList.add(newBean);
//
//				}
//				ExportExcel expExcel = new ExportExcel(serv);
//				try {
//					// 查询出 要导出的数据
//					// 总数大于excel可写最大值
//					
//					// 导出第一次查询数据
//					paramBean.setQueryPageNowPage(1); // 导出当前第几页
//					// 查询出表头 查询出 对应数据 hashmaplist
//					expExcel.createHeader(cols);
//					expExcel.appendData1(finalList, paramBean);
//					// 存在多页数据
//					
//					expExcel.addSumRow();
//				} catch (Exception e) {
//					log.error("导出Excel文件异常" + e.getMessage(), e);
//				} finally {
//					expExcel.close();
//				}
//				return new OutBean().setOk();	
//				
//				
//				
//	}
	
}
