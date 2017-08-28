package com.rh.core.icbc.imp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.threadpool.RhThreadPool;
import com.rh.core.util.threadpool.RhThreadTask;

/**
 * 数据导入服务扩展类
 * @author if
 *
 */
public class NImpDataServ extends CommonServ {

	/** log. */
	private static Log log = LogFactory.getLog(NImpDataServ.class);
	
	/**
	 * 从接口表中同步组织机构数据
	 * @param param
	 * @return
	 */
	public OutBean impDatafromTable(ParamBean param) {
		log.info("-------------- impDatafromTable ---------------");
		
		// 创建同步数据异步任务
		RhThreadTask task = new NImpTableDataTask(param);
		
		// 获取系统默认线程池
		RhThreadPool threadPool = RhThreadPool.getDefaultPool();
		
		// 执行异步线程操作
		log.info("------------------ execute impData task ----------------------");
		threadPool.execute(task);
		
		return new OutBean().setOk("正在进行从增量表导入数据!");
	}
}
