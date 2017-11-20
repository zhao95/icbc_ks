package com.rh.core.icbc.basedata.serv;

import org.apache.commons.lang.time.StopWatch;

import com.rh.core.icbc.basedata.task.KSTipBMTask;
import com.rh.core.icbc.basedata.task.KSTipQJ_JKTask;
import com.rh.core.icbc.basedata.task.KSTipZKZTask;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.util.threadpool.RhThreadPool;

/**
 * 定时发提醒的服务类
 * @author leader
 */
public class KSTipMessageServ extends CommonServ {
	
	/**
	 * 启动提醒的整体服务类
	 * @return
	 */
	public OutBean startJob() {
		RhThreadPool tipThreadPoll = new RhThreadPool(2, 5, 10);
		//开始计时
		StopWatch sw = new StopWatch();
		sw.start();
		//报名相关提醒线程任务
		KSTipBMTask bmTask = new KSTipBMTask();
		tipThreadPoll.execute(bmTask);
		
		KSTipQJ_JKTask name = new KSTipQJ_JKTask();
		tipThreadPoll.execute(name);
		
		KSTipZKZTask name1 = new KSTipZKZTask();
		tipThreadPoll.execute(name1);
		
		// 请假/借考相关提醒线程任务
		log.error("-------------开始通知参考人员可以开始请假。----------------");
		//请假开始提醒
		log.error("-------------完成通知参考人员可以开始请假。----------------");
		
		log.error("-------------开始通知参考人员请假结果。----------------");
		//请假结果提醒
		log.error("-------------完成通知参考人员请假结果。----------------");
		
		log.error("-------------开始通知参考人员可以开始借考。----------------");
		//借考开始提醒
		log.error("-------------完成通知参考人员开始借考。----------------");
		
		log.error("-------------开始通知参考人员借考结果。----------------");
		//借考结果提醒
		
		//准考证打印提醒
		//考场座位安排公示提醒
		log.error("-------------完成通知参考人员借考结果。----------------");
		
		while(true) {
			// 线程结束 或者 超过2小时
			if (tipThreadPoll.isFinished() || sw.getTime() >= 2 * 60 * 60 * 1000) {
				log.error("---------------线程池超时或使用完毕关闭---------------");
				//关闭线程池
				tipThreadPoll.shutdown();
				sw.stop();
				break;
			}
		}
		return  new OutBean().setOk("完成提醒任务!");
	}
}
