package com.rh.core.icbc.imp;

import com.rh.core.icbc.imp.target.ImpUtils;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.threadpool.RhThreadPool;
import com.rh.core.util.threadpool.RhThreadTask;

/**
 * 从上游系统导入数据
 * 
 * @author caoyiqing
 *
 */
public class ImpDataServ extends CommonServ {

	/**
	 * 任务调度，定时从上游系统导入数据
	 * 
	 * @param param
	 *            参数信息
	 */

	public OutBean dataImporter(ParamBean param) {

		RhThreadTask task = new ImpDataTask(param);
		RhThreadPool.getDefaultPool().execute(task);
		return new OutBean().setMsg("正在进行从上游系统导入数据!").setOk();
	}

	/**
	 * 任务调度，定时从增量表导入数据
	 * 
	 * @param param
	 *            参数信息
	 */

	public OutBean impDatafromTable(ParamBean param) {

		RhThreadTask task = new ImpTableDataTask(param);
		RhThreadPool threadPool = RhThreadPool.getDefaultPool();
		threadPool.execute(task);
		return new OutBean().setOk("正在进行从增量表导入数据!");
	}

	/**
	 * 同步SY_ORG_ADDRESS_V表 部门机构变动后，需手动同步该表
	 * 
	 * @param param
	 * @return
	 */
	public OutBean synchronizeAddress(ParamBean param) {
		ImpUtils.createTableFromView();
		return new OutBean().setOk();
	}

}
