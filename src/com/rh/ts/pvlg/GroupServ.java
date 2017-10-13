package com.rh.ts.pvlg;

import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.util.Constant;
import com.rh.ts.pvlg.mgr.GroupMgr;
import com.rh.ts.util.TsConstant;

public class GroupServ extends CommonServ {

	/**
	 * 删除之后执行
	 */
	protected void afterDelete(ParamBean paramBean, OutBean outBean) {

		if (outBean.getStr("_MSG_").startsWith("OK")) {

			List<Bean> list = paramBean.getDeleteDatas();

			if (list != null && list.size() > 0) {

				for (Bean group : list) {
					// 删除群组缓存
					GroupMgr.removeGroupCache(group.getId(), TsConstant.SERV_GROUP);

					ParamBean whereBean = new ParamBean();
					whereBean.set("G_ID", group.getId());

					// 级联删除绑定角色 (如果设置级联删除，不需要执行删除。安全起见代码删除)
					ServMgr.act(TsConstant.SERV_GROUP_ROLE, ServMgr.ACT_DELETE, whereBean);

					// 级联删除绑定用户 (如果设置级联删除，不需要执行删除。安全起见代码删除)
					ServMgr.act(TsConstant.SERV_GROUP_USER, ServMgr.ACT_DELETE, whereBean);
				}
			}
		}
	}

	/**
	 * 保存后执行
	 */
	protected void afterSave(ParamBean paramBean, OutBean outBean) {

		if (outBean.getStr("_MSG_").startsWith("OK")) {

			GroupMgr.setGroupCache(paramBean.getId());
		}
	}

	/**
	 * 批量保存后执行
	 */
	protected void afterBatchSave(ParamBean paramBean, OutBean outBean) {

		if (outBean.getStr("_MSG_").startsWith("OK")) {

			List<Bean> list = paramBean.getBatchSaveDatas();

			if (list != null && list.size() > 0) {
				
				for (Bean bean : list) {
					GroupMgr.setGroupCache(bean.getId());
				}
			}
		}
	}

	//查询前添加查询条件
		protected void beforeQuery(ParamBean paramBean) {
			int treeWhereSize = paramBean.getList("_treeWhere").size();
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
			
//	        int treeWhereSize = paramBean.getList("_treeWhere").size();
//			if(treeWhereSize == 0){
//				StringBuilder strWhere = new StringBuilder(); 
//		        strWhere.append(" and 1=2");
//		        paramBean.setQueryExtWhere(strWhere.toString());
//			}
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
				 StringBuilder param_where=new StringBuilder();
				 param_where.append("AND CTLG_PCODE IN ( ");
				 param_where.append("SELECT CTLG_CODE_H FROM TS_COMM_CATALOG ");
				 param_where.append("WHERE  CTLG_MODULE='GROUP'  ");
				 param_where.append("and ( ");
				 StringBuilder subSQL= new StringBuilder();
				 for(int i=0;i<roles.length;i++){
					 subSQL.append("CTLG_PATH_H LIKE '%GROUP-"+roles[i]+"^%'  or");
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
			} 
		}	
	
}
