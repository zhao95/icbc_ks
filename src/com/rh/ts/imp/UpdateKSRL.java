package com.rh.ts.imp;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.rh.core.base.Bean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.util.scheduler.RhJob;
import com.rh.core.util.scheduler.RhJobContext;

/**
 * 定时更新考试日历的数据（数据来源于项目管理  TS_XMGL）
 * @author wanglida
 *
 */
public class UpdateKSRL extends RhJob {
	/** log.日志 */
	private static Log log = LogFactory.getLog(UpdateKSRL.class);
	
	// 每次保存条数，批量操作数据数（暂未用到）
//	private int SAVE_COUNT = 5000;
	//获取到项目管理的服务
	String fromServData = "TS_XMGL";
	//获取到要更新的服务
	String updateServData ="TS_KS_CAL";
	
	@Override
	protected void executeJob(RhJobContext context) {
		//调用更新数据的方法，并把所需要的服务id传递过去。
		log.debug("-------------NImpBakJob is begin!-------------------");
		updateServData(fromServData,updateServData);
		//获取到需要定期更新的表
		//获取到项目管理中的数据
		//获取到到项目的s_mtime
		//判断项目s_mtime是否修改？
		//若修改，则获取修改后的时间，并把所有的数据更新到考试日历的表中
		//若未修改，则不更新数据
		log.debug("-------------NImpBakJob is end!-------------------");
	}

	@Override
	public void interrupt() {
		//中断的方法（暂未启用）
	}
	
	//更新数据的方法
	public void updateServData(String fromServ,String updateServ){
		ParamBean sqlBean1 = new ParamBean();
		sqlBean1.setWhere("and 1 = 1");
		//查询项目管理中的所有的数据，将结果存入一个对象的集合
		List<Bean> resultListF = ServDao.finds(fromServ, sqlBean1);
		for (int i = 0; i < resultListF.size(); i++) {
			String xm_id = resultListF.get(i).getStr("XM_ID");
			ParamBean valbean = new ParamBean();
			valbean.set("CAL_ID", xm_id);
			//查询是否有数据
			Bean find = ServDao.find(updateServ, valbean);
			//如果查询到的有数据，再比较考试开始时间是否一致，若不一致，则将日历中的所有数据进行更新
			if(find!=null){
				if((find.getStr("S_MTIME")!=resultListF.get(i).getStr("S_MTIME"))){
					find.set("CAL_ID",resultListF.get(i).getStr("XM_ID"));
					find.set("CAL_NAME",resultListF.get(i).getStr("XM_NAME"));
					find.set("CAL_TYPE",resultListF.get(i).getStr("XM_TYPE"));
					find.set("START_DATE",resultListF.get(i).getStr("XM_START"));
					find.set("END_DATE",resultListF.get(i).getStr("XM_END"));
					
					find.set("S_USER",resultListF.get(i).getStr("S_USER"));
					find.set("S_FLAG",resultListF.get(i).getStr("S_FLAG"));
					find.set("S_DEPT",resultListF.get(i).getStr("S_DEPT"));
					find.set("S_TDEPT",resultListF.get(i).getStr("S_TDEPT"));
					find.set("S_ODEPT",resultListF.get(i).getStr("S_ODEPT"));
					find.set("S_CMPY",resultListF.get(i).getStr("S_CMPY"));
					find.set("S_ATIME",resultListF.get(i).getStr("S_ATIME"));
					find.set("S_MTIME",resultListF.get(i).getStr("S_MTIME"));
				}
				//若时间一致， 不需要对其进行更新
				else{
					log.debug("-------------考试信息未修改，则保持原数据！-------------------");
				}
			}
			//如果查不到数据，则新增数据
			else if (find==null){
				ParamBean bean = new ParamBean();
				bean.set("CAL_ID",resultListF.get(i).getStr("XM_ID"));
				bean.set("CAL_NAME",resultListF.get(i).getStr("XM_NAME"));
				bean.set("CAL_TYPE",resultListF.get(i).getStr("XM_TYPE"));
				bean.set("START_DATE",resultListF.get(i).getStr("XM_START"));
				bean.set("END_DATE",resultListF.get(i).getStr("XM_END"));
				
				bean.set("S_USER",resultListF.get(i).getStr("S_USER"));
				bean.set("S_FLAG",resultListF.get(i).getStr("S_FLAG"));
				bean.set("S_DEPT",resultListF.get(i).getStr("S_DEPT"));
				bean.set("S_TDEPT",resultListF.get(i).getStr("S_TDEPT"));
				bean.set("S_ODEPT",resultListF.get(i).getStr("S_ODEPT"));
				bean.set("S_CMPY",resultListF.get(i).getStr("S_CMPY"));
				bean.set("S_ATIME",resultListF.get(i).getStr("S_ATIME"));
				bean.set("S_MTIME",resultListF.get(i).getStr("S_MTIME"));
				//调用数据库相关服务新增数据
				ServDao.save(updateServ,bean);
			}
		}
	}
	
}
