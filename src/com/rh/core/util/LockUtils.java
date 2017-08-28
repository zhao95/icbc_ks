package com.rh.core.util;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.rh.core.comm.ConfMgr;
import com.rh.core.comm.FileStorage;

public class LockUtils {

	private static final Logger log = Logger.getLogger(LockUtils.class);
	private static final ConcurrentHashMap<String, ScheduledFuture<?>> currentHashMap = new ConcurrentHashMap<String, ScheduledFuture<?>>();
	private static final ConcurrentHashMap<String, String> lockStartTimeMap = new ConcurrentHashMap<String, String>();
	
	/**
     * touch锁的定时时间，单位毫秒
     */
    private static final int TOUCH_INTERVAL = 30000;
    
    /**
     * 定时任务线程池
     */
    private final static int MAX_POOL_SIZE = 10;
    private final static ScheduledExecutorService service = Executors.newScheduledThreadPool(MAX_POOL_SIZE);
	
    /**
     * 锁文件开始定时touch
     * @param path 锁文件路径
     */
	public static void startTouch(final String path) {
		// 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        ScheduledFuture<?> future = service.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
//					log.info("start touch file:" + path);
	    				FileStorage.touch(path);
	    			} catch (IOException e) {
	    				log.error("touch file failure!" + e.getMessage());
	    			}
			}
		}, 0, TOUCH_INTERVAL, TimeUnit.MILLISECONDS);
        currentHashMap.put(path, future);
        lockStartTimeMap.put(path, System.currentTimeMillis() + "");
	}
	
	/**
	 * 结束锁文件touch
	 * @param path 锁文件路径
	 */
	public static void endTouch(String path) {
		ScheduledFuture<?> future = currentHashMap.get(path);
		if (future != null) {
//			log.info("end touch file:" + path);
			future.cancel(true);
			removePath(path);
		}
	}
	
	private static void removePath(String path) {
		currentHashMap.remove(path);
		lockStartTimeMap.remove(path);
	}
	
	/**
	 * 检测锁是否有效，如果当前时间减去上一次锁文件的touch时间大于锁文件touch时间间隔则该锁文件失效
	 * @param path 锁文件路径
	 * @return 锁是否有效
	 */
	public static boolean isLocking(String path) {
		try {
			if (!FileStorage.exists(path)) { // 如果锁文件不存在则锁肯定是无效的
				return false;
			}
			long currentMillis = System.currentTimeMillis();
			long lockLastModified = FileStorage.lastModified(path);
			if (currentMillis - lockLastModified > TOUCH_INTERVAL) { // 锁失效，重启系统时锁文件没有删除
				FileStorage.deleteFile(path);
				return false;
			}
			
			// 判断锁有没有超时，如果超时则移除该锁
			if (lockStartTimeMap.contains(path)) {
				int startTime = Integer.parseInt(lockStartTimeMap.get(path));
				int timeout = ConfMgr.getConf("CONCURRENT_LOCK_TIMEOUT", 2); // 同步锁超时时间，默认两个小时
				if (currentMillis - startTime > timeout * 3600 * 1000) { // 锁超时
					log.debug("锁超时，强制释放");
					endTouch(path);
					FileStorage.deleteFile(path);
					return false;
				}
			}
		} catch (IOException e) {
			log.error("检测锁是否有效：" + e.getMessage());
		}
		return true;
	}
	
}
