package com.rh.core.icbc.login;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.TipException;
import com.rh.core.comm.CacheMgr;
import com.rh.core.util.EncryptUtils;
import com.rh.core.util.Lang;

/**
 * 登录工具类
 * @author zhangjx
 *
 */
public class LoginMgr {
	
	private static Log log = LogFactory.getLog(LoginMgr.class);
	private static String CACHENAME = "LOGIN_PASSWD_RANDOMKEY"; // 用户登录加密密码随机数CACHE
	private static String JOINSTR = "@~@$#"; // 拼接常量
	
	
	/**
	 * 生成随机Key,跟设备绑定放入缓存
	 * @param deviceId - 设备编码
	 * @return 随机Key
	 */
	public static String getRandomKey(String deviceId) {
		// 生成15位的随机数
		final String randomKey = RandomStringUtils.randomAlphanumeric(15);
		// 放入二级缓存
		CacheMgr.getInstance().set(deviceId, randomKey, CACHENAME);
		// 返回随机数
		return randomKey;
	}
	
	/**
	 * 解密密码
	 * @param encPasswd - 密文密码
	 * @param desKey - DES加密Key
	 * @return - 明文密码
	 */
	public static String descryptPasswd(String encPasswd, String desKey) {
		String passwd = "";
		try {
			byte[] base64passwd = EncryptUtils.desDecrypt(encPasswd.getBytes("utf-8"), desKey.getBytes("utf-8"));
			passwd = Lang.byteTohex(base64passwd);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("解密密码失败！");
		}
		return passwd;
	}
	
	/**
	 * 创建DES加密Key
	 * @param deviceId - 设备ID
	 * @param loginName - 用户名
	 * @return - 随机数 + 设备ID + 用户名 生成的DES加密Key 
	 */
	public static String createDesKey(String deviceId, String loginName) {
		// 获取加密随机数
		final String randomKey = (String)CacheMgr.getInstance().get(deviceId, CACHENAME);
		
		// 如果没有随机数，证明随机数已过期 
		if(randomKey == null || randomKey.isEmpty()) {
			throw new TipException("1005,解密密码错误。");
		}
		
		// 拼接 随机数 + 设备ID + 用户名
		StringBuilder str = new StringBuilder();
		str.append(randomKey).append(JOINSTR).append(deviceId).append(JOINSTR).append(loginName);
		
		// 调用MD5加密
//		byte[] md5 = DigestUtils.md5(str.toString());
		String md5 = DigestUtils.md5Hex(str.toString());
		
		try {
			// 使用BASE64整理数据，生成DES加密Key
			return Base64.encodeBase64String(md5.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			log.error("使用BASE64整理数据，生成DES加密Key出现错误！");
			return "";
		}
	}
}
