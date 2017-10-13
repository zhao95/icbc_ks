package com.rh.ts.jhgl;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.util.Constant;

public class JhglServ extends CommonServ {
	
	/**
	 * 批量改变 状态(发布)
	 * @author Wangwei
	 * @param paramBean
	 * 
	 */
	public void UpdateStatusStart(Bean paramBean){
			try {
			//获取服务ID
			String servId=paramBean.getStr(Constant.PARAM_SERV_ID);
			//获取 主键id  list
			String dataId = paramBean.getStr("pkCodes");
			
			String[] dataIds = dataId.split(",");
			//循环遍历 dataIds,取出批量选择的大计划
			for (String JHPId : dataIds) {
				//获取到大计划下 的 详细计划，并修改各个详细计划的发布状态
				String whereSql ="and JH_PTITLE ='"+JHPId+"' and JH_FLAG = '2'";
				List<Bean> jhxxList = ServDao.finds("TS_JHGL", whereSql);
				for (int i = 0; i < jhxxList.size(); i++) {
					String jhxxStatus = jhxxList.get(i).getStr("JH_STATUS");
					//将STRING状态 转换为 int类型 因为是字典 判断state  1未发布 2 已发布  
					int jhxxStatusInt = Integer.valueOf(jhxxStatus);
					if(jhxxStatusInt==1){
						jhxxStatusInt+=1;
					}
					//将状态值设置进bean，并保存到数据库
					jhxxList.get(i).set("JH_STATUS", jhxxStatusInt);
					ServDao.save("TS_JHGL", jhxxList.get(i));
					//创建bean存储日历表中数据，将计划管理的计划详细逐条添加到日历表中
					Bean CalBean = new Bean();
//					CalBean.setId(jhxxList.get(i).getStr("JH_ID"));
					CalBean.set("CAL_ID", jhxxList.get(i).getStr("JH_ID"));
					CalBean.set("CAL_NAME",jhxxList.get(i).getStr("JH_CTITLE"));
					CalBean.set("START_DATE",jhxxList.get(i).getStr("JH_CREATEDATE"));
					CalBean.set("END_DATE",jhxxList.get(i).getStr("JH_ENDDATE"));
					CalBean.set("CAL_TYPE",jhxxList.get(i).getStr("JH_TYPE"));
					CalBean.set("BM_START_DATE",jhxxList.get(i).getStr("JH_BM_STARTDATE"));
					CalBean.set("BM_END_DATE",jhxxList.get(i).getStr("JH_BM_ENDDATE"));
					CalBean.set("KS_LEVEL",jhxxList.get(i).getStr("JH_LEVEL"));
					
					CalBean.set("S_USER",jhxxList.get(i).getStr("S_USER"));
					CalBean.set("S_TDEPT",jhxxList.get(i).getStr("S_TDEPT"));
					CalBean.set("S_ODEPT",jhxxList.get(i).getStr("S_ODEPT"));
					CalBean.set("S_MTIME",jhxxList.get(i).getStr("S_MTIME"));
					CalBean.set("S_ATIME",jhxxList.get(i).getStr("S_ATIME"));
					CalBean.set("S_FLAG",jhxxList.get(i).getStr("S_FLAG"));
					CalBean.set("S_CMPY",jhxxList.get(i).getStr("S_CMPY"));
					CalBean.set("S_DEPT",jhxxList.get(i).getStr("S_DEPT"));
					//判断数据库中是否有该数据
					//保存到日历数据库表中
					ServDao.save("TS_KS_CAL", CalBean);
				}
				
				//根据服务id 主键id获取 当前对象
				Bean bean = ServDao.find(servId, JHPId);
				//找到对应属性字段值
				String state = bean.getStr("JH_STATUS");
				//将STRING状态 转换为 int类型 因为是字典 判断state  1未发布 2 已发布  
				int state1 = Integer.valueOf(state);
				if(state1==1){
					state1+=1;
				}
				bean.set("JH_STATUS", state1);
				//保存到数据库
				ServDao.save(servId, bean);
			}
		} catch (Exception e) {
				throw new TipException("服务器异常，发布失败！");
		}
		}
	/**
	 * 批量改变 状态(取消发布)
	 * @author Wangwei
	 * @param paramBean
	 * 
	 */
	public void UpdateStatusStop(Bean paramBean){
			
			//获取服务ID
			String servId=paramBean.getStr(Constant.PARAM_SERV_ID);
			//获取 主键id  list
			String dataId = paramBean.getStr("pkCodes");
			
			String[] dataIds = dataId.split(",");
			//循环遍历 dataIds
			for (String JHPId : dataIds) {
				//获取到大计划下 的 详细计划，并修改各个详细计划的发布状态
				String whereSql ="and JH_PTITLE ='"+JHPId+"' and JH_FLAG = '2'";
				List<Bean> jhxxList = ServDao.finds("TS_JHGL", whereSql);
				for (int i = 0; i < jhxxList.size(); i++) {
					String jhxxStatus = jhxxList.get(i).getStr("JH_STATUS");
					//将STRING状态 转换为 int类型 因为是字典 判断state  1未发布 2 已发布  
					int jhxxStatusInt = Integer.valueOf(jhxxStatus);
					if(jhxxStatusInt==2){
						jhxxStatusInt-=1;
					}
					//将状态值设置进bean，并保存到数据库
					jhxxList.get(i).set("JH_STATUS", jhxxStatusInt);
					ServDao.save("TS_JHGL", jhxxList.get(i));
					//从考试日历表中删除
					ServDao.delete("TS_KS_CAL",jhxxList.get(i).getStr("JH_ID"));
				}
				
				//根据服务id 主键id获取 当前对象
				Bean bean = ServDao.find(servId, JHPId);
				//找到对应属性字段值
				String state = bean.getStr("JH_STATUS");
				//将STRING状态 转换为 int类型 因为是字典 判断state  1未发布 2 已发布  
				int state1 = Integer.valueOf(state);
				if(state1==2){
					state1-=1;
				}
				bean.set("JH_STATUS", state1);
				//保存到数据库
				ServDao.save(servId, bean);
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
				 param_where.append("WHERE  CTLG_MODULE='PLAN'  ");
				 param_where.append("and ( ");
				 StringBuilder subSQL= new StringBuilder();
				 for(int i=0;i<roles.length;i++){
					 subSQL.append("			 CTLG_PATH_H LIKE '%PLAN-"+roles[i]+"^%'  or");
				 }
				 subSQL.delete(subSQL.length()-2, subSQL.length());
				 param_where.append(subSQL);
				 param_where.append("		) ) ");
				 paramBean.set(Constant.PARAM_WHERE, param_where.toString());
				 System.out.println("param_where:"+param_where.toString());
			 }else{
			 //无权限
			 paramBean.set(Constant.PARAM_WHERE, " and 1=2");
			 }
			} 
		}

}

