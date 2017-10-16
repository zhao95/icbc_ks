package com.rh.ts.pvlg;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;
import com.rh.core.util.Strings;
import com.rh.ts.pvlg.mgr.RoleMgr;
import com.rh.ts.util.RoleUtil;
import com.rh.ts.util.TsConstant;

public class RoleServ extends CommonServ {

	/**
	 * 获取用户所有功能权限,无权限值为0,有权限值为关联部门
	 * 
	 * @param paramBean
	 *            必填参数USER_CODE
	 * @return
	 */
	public OutBean getPvlgRole(ParamBean paramBean) {

		OutBean outBean = new OutBean();

		String userCode = paramBean.getStr("USER_CODE");

		if (Strings.isBlank(userCode)) {
			UserBean userBean = Context.getUserBean();
			userCode = userBean.getCode();
		}

		// 用户所有功能权限
		Bean allOpt = RoleUtil.getPvlgRole(userCode);

		outBean.setData(allOpt);

		return outBean.setOk();
	}

	/**
	 * 删除之后执行
	 */
	protected void afterDelete(ParamBean paramBean, OutBean outBean) {

		if (outBean.getStr("_MSG_").startsWith("OK")) {
			// 删除角色缓存
			RoleMgr.removeRoleCache(paramBean.getId(), TsConstant.SERV_ROLE);
			// 删除功能权限缓存
			RoleMgr.removeRoleCache(paramBean.getId(), TsConstant.SERV_ROLE_MOD);

			Bean whereBean = new Bean();
			whereBean.set("ROLE_ID", paramBean.getId());
			// 删除功能权限
			ServDao.delete(TsConstant.SERV_ROLE_MOD, whereBean);
		}
	}

	/**
	 * 保存后执行
	 */
	protected void afterSave(ParamBean paramBean, OutBean outBean) {

		if (outBean.getStr("_MSG_").startsWith("OK")) {
			// 重置角色缓存
			if (!Strings.isBlank(paramBean.getId())) {
				RoleMgr.setRoleCache(paramBean.getId());
			}
		}
	}

	/**
	 * 查询之前的拦截方法，由子类重载
	 * 
	 * @param paramBean
	 *            参数信息
	 */
	protected void beforeQuery(ParamBean paramBean) {
		ParamBean param = new ParamBean();
		String  ctlgModuleName="ROLE";
		String  serviceName="TS_PVLG_ROLE";
		param.set("paramBean", paramBean);
		param.set("ctlgModuleName", ctlgModuleName);
		param.set("serviceName",serviceName);
		ServMgr.act("TS_UTIL", "userPvlg", param);	
		
		
		/**
		Bean item = paramBean.getBean("PVLG_ITEM");
		//机构tree path
		String path = item.getStr("CODE_PATH");
		//目录tree path
		String ctlgPath = item.getStr("CTLG_PATH");
		//tree的编码 机构编码
		String dcode = paramBean.getStr("PVLG_FIELD");
		//用户权限 所有权限的机构编码
		Bean userPvlg = paramBean.getBean("USER_PVLG");**/
		/*int treeWhereSize = paramBean.getList("_treeWhere").size();
		if(treeWhereSize == 0){
		 // tree的编码 机构编码
		Bean extParams = paramBean.getBean("extParams");
		// String dcode = extParams.getStr("PVLG_FIELD");
		// 用户权限 所有权限的机构编码
		Bean userPvlg = extParams.getBean("USER_PVLG");
		JSONObject jsonObject = new JSONObject(userPvlg);
		String result=null;
		Iterator iterator = jsonObject.keys();
	  	String key;
		while (iterator.hasNext()) {
			key = (String) iterator.next();
			try {
				JSONObject object = (JSONObject) jsonObject.get(key);
				String object2 = (String)object.get("ROLE_DCODE");
				String[] object3=object2.split(",");
				if(result!=null){
					for(int  i=0;i<object3.length;i++){
						if(result.indexOf(object3[i])<0){
							result+=","+object3[i];
						}
					}
				}else{
					result =object2;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
		System.out.println("result:"+result);//result 已经是权限值或者是 null
		
//        int treeWhereSize = paramBean.getList("_treeWhere").size();
//		if(treeWhereSize == 0){
//			StringBuilder strWhere = new StringBuilder(); 
//	        strWhere.append(" and 1=2");
//	        paramBean.setQueryExtWhere(strWhere.toString());
//		}
		 if(result !=null){			
			 //result 排序
			 String[] roles = result.split(",");
		/*	 Arrays.sort(roles);
			 String lastResult="";
			 for(int i=0;i<roles.length;i++){
				 lastResult+="^PROJECT-"+roles[i];
			 }
			 lastResult = lastResult.substring(1)+"^";
			 System.out.println("lastResult:"+lastResult);*/
			/* StringBuilder param_where=new StringBuilder();
			 param_where.append("AND CTLG_PCODE IN ( ");
			 param_where.append("SELECT CTLG_CODE_H FROM TS_COMM_CATALOG ");
			 param_where.append("WHERE  CTLG_MODULE='ROLE'  ");
			 param_where.append("and ( ");
			 StringBuilder subSQL= new StringBuilder();
			 for(int i=0;i<roles.length;i++){
				 subSQL.append("CTLG_PATH_H LIKE '%ROLE-"+roles[i]+"^%'  or");
			 }
			 subSQL.delete(subSQL.length()-2, subSQL.length());
			 param_where.append(subSQL);
			 param_where.append(") ) ");
			 paramBean.set(Constant.PARAM_WHERE, param_where.toString());
			 System.out.println("param_where:"+param_where.toString());
		 }else{
		 //无权限
		 paramBean.set(Constant.PARAM_WHERE, " and 1=2");
		 }
		}*/ 
		
	}

}
