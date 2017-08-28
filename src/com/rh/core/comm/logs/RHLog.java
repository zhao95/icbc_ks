package com.rh.core.comm.logs;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.commons.io.IOUtils;

import com.rh.core.base.Context;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.listener.RHLogReceiver;
import com.rh.core.util.DateUtils;
import com.rh.core.util.msg.CommonMsg;
import com.rh.core.util.msg.MsgCenter;

/**
 * RUAHO公共日志记录类
 */
public class RHLog {
	private static final String LOG_LEVEL_INFO = "info";
	private static final String LOG_LEVEL_DEBUG = "debug";
	private static final String LOG_LEVEL_ERROR = "error";
	
	/**
	 * info级别日志
	 */
	public static void info(String name, String content) {
		save(LOG_LEVEL_INFO, name, content);
	}
	public static void info(Class<?> clazz, String content) {
		save(LOG_LEVEL_INFO, clazz.getName(), content);
	}
	public static void info(String name, Throwable t) {
		save(LOG_LEVEL_INFO, name, t);
	}
	public static void info(Class<?> clazz, Throwable t) {
		save(LOG_LEVEL_INFO, clazz.getName(), t);
	}
	
	/**
	 * debug级别日志
	 */
	public static void debug(String name, String content) {
		save(LOG_LEVEL_DEBUG, name, content);
	}
	public static void debug(Class<?> clazz, String content) {
		save(LOG_LEVEL_DEBUG, clazz.getName(), content);
	}
	public static void debug(String name, Throwable t) {
		save(LOG_LEVEL_DEBUG, name, t);
	}
	public static void debug(Class<?> clazz, Throwable t) {
		save(LOG_LEVEL_DEBUG, clazz.getName(), t);
	}
	
	/**
	 * error级别日志
	 */
	public static void error(String name, String content) {
		save(LOG_LEVEL_ERROR, name, content);
	}
	public static void error(Class<?> clazz, String content) {
		save(LOG_LEVEL_ERROR, clazz.getName(), content);
	}
	public static void error(String name, Throwable t) {
		save(LOG_LEVEL_ERROR, name, t);
	}
	public static void error(Class<?> clazz, Throwable t) {
		save(LOG_LEVEL_ERROR, clazz.getName(), t);
	}
	
	/**
	 * 记录日志方法
	 */
	private static void save(String level, String name, String content) {
		String userCode = "";
		try {
			userCode = Context.getUserBean().getCode();
		} catch (Exception e) {}
		CommonMsg msg = new CommonMsg(RHLogReceiver.SY_COMM_LOG, ServMgr.ACT_SAVE);
		msg.set("LOG_LEVEL", level);
		msg.set("LOG_NAME", name);
		msg.set("LOG_CONTENT", content);
		msg.set("S_USER", userCode); //创建人
        msg.set("S_ATIME", DateUtils.getDatetimeTS()); //创建时间
        
        MsgCenter.getInstance().addMsg(msg);
	}
	
	/**
	 * 记录日志方法
	 */
	private static void save(String level, String name, Throwable t) {
		save(level, name, getContent(t));
	}
	
	/**
	 * 从Throwable取得错误信息
	 */
	public static String getContent(Throwable t) {
	    if (t == null) {
	    	return null;
	    }
	    
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			t.printStackTrace(new PrintStream(baos));
		} finally {
			IOUtils.closeQuietly(baos);
		}
		return baos.toString();
	}
}
