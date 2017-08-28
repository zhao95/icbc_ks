package com.rh.core.util.exception;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 异常工具类
 * @author ruaho_hdy
 *
 */
public class ExceptionUtil {

	private static Log log = LogFactory.getLog(ExceptionUtil.class);
	
	/**
	 * 将异常信息格式化成字符串
	 * @param e 异常
	 * @return 字符转
	 */
	public static String toMsgString(Exception e) {
		ByteArrayOutputStream os = null;
		PrintStream ps = null;
        try {
            os = new ByteArrayOutputStream();
            ps = new PrintStream(os);
            e.printStackTrace(ps);
            ps.flush();
            return os.toString();
        } catch (Exception ex) {
            log.error(e.getMessage(), ex);
        } finally {
            IOUtils.closeQuietly(ps);
            IOUtils.closeQuietly(os);
        }
		return "";
	}
}
