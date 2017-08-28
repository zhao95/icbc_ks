package com.rh.core.icbc.basedata.task;

import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.util.threadpool.RhThreadTask;

public class ImpCommonTask extends RhThreadTask {
	
	private static final long serialVersionUID = 6353919961488570053L;
	private String smtime = "";
	private Boolean incrementFlag = false;
//	private AbstractTable abstractTable = null;

	/**
	 * 构造函数,接收传入的参数
	 * 
	 * @param param
	 */
	public ImpCommonTask(ParamBean param) {
		this.smtime = param.getStr("S_MTIME");
		this.incrementFlag = param.get("INCREMENT", false);
//		this.abstractTable = (AbstractTable) param.get("CLAZZ");
	}
	
	@Override
	public boolean execute() {
		boolean successFlag = true;
		
		if (incrementFlag) { // 增量
//			successFlag = abstractTable.impIncData();
		} else { // 全量
//			successFlag = abstractTable.impFullData();
		}
		
		// 导入完成后设置导入结果状态
//		ServDao.deletes("SY_BASEDATA_IMP_STATE", new ParamBean().set("IMP_DATE", smtime).set("SERV_ID", abstractTable.getTargetServId()));
		ParamBean dataBean = new ParamBean();
//		dataBean.set("SERV_ID", abstractTable.getTargetServId());
		dataBean.set("IMP_DATE", smtime);
		if (successFlag) {
			dataBean.set("IMP_STATE", 10);
		} else {
			dataBean.set("IMP_STATE", 20);
		}
		ServDao.create("SY_BASEDATA_IMP_STATE", dataBean);
		
		return true;
	}

}
