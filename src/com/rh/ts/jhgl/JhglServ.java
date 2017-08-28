package com.rh.ts.jhgl;
import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
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
			
			//获取服务ID
			String servId=paramBean.getStr(Constant.PARAM_SERV_ID);
			//获取 主键id  list
			String dataId = paramBean.getStr("pkCodes");
			
			String[] dataIds = dataId.split(",");
			//循环遍历 dataIds
			for (String string : dataIds) {
				
				//根据服务id 主键id获取 当前对象
				Bean bean = ServDao.find(servId, string);
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
			for (String string : dataIds) {
				
				//根据服务id 主键id获取 当前对象
				Bean bean = ServDao.find(servId, string);
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


}

