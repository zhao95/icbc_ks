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
import com.rh.core.serv.ServMgr;
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
			ParamBean param = new ParamBean();
			String  ctlgModuleName="PLAN";
			String  serviceName="TS_JHGL";
			param.set("paramBean", paramBean);
			param.set("ctlgModuleName", ctlgModuleName);
			param.set("serviceName",serviceName);
			ServMgr.act("TS_UTIL", "userPvlg", param);	
		}
}

