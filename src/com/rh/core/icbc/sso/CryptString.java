package com.rh.core.icbc.sso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Context;

/**
 *
 * 字符串加密公用类
 * 
 */
public class CryptString {
	/** log */
    private static Log log = LogFactory.getLog(CryptString.class);

	private Cipher encryptCipher = null;
	private Cipher decryptCipher = null;

	private String secretKey;

	/**
	 * 用 secretKey 去加密 unEncryptStr
	 * 
	 * @param secretKey
	 * @param unEncryptStr
	 *            未加密的串
	 */
	public CryptString(String secretKey) {
		if (secretKey.length() < 8) { //如果长度不够8位，补齐
			secretKey = StringUtils.leftPad(secretKey, 8, "0");
		}
		this.secretKey = secretKey;
	    try {
			init();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws UnsupportedEncodingException
	 */
	private void init() throws InvalidKeyException, NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException,
			UnsupportedEncodingException {

		DESKeySpec keySpec = new DESKeySpec(secretKey.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

		SecretKey key = keyFactory.generateSecret(keySpec);

		encryptCipher = Cipher.getInstance("DES");
		decryptCipher = Cipher.getInstance("DES");
		encryptCipher.init(Cipher.ENCRYPT_MODE, key);
		decryptCipher.init(Cipher.DECRYPT_MODE, key);
	}

	/**
	 * Encrypt a string using DES encryption, and return the encrypted string as
	 * a base64 encoded string.
	 * 
	 * @param unencryptedString
	 *            The string to encrypt.
	 * @return String The DES encrypted and base 64 encoded string.
	 * @throws Exception
	 *             If an error occurs.
	 */
	public String encryptBase64(String unencryptedString) throws Exception {
		// Encode the string into bytes using utf-8
		byte[] unencryptedByteArray = unencryptedString.getBytes("UTF-8");

		// Encrypt
		byte[] encryptedBytes = encryptCipher.doFinal(unencryptedByteArray);

		// Encode bytes to base64 to get a string
		byte[] encodedBytes = Base64.encodeBase64(encryptedBytes);

		return new String(encodedBytes);
	}

	/**
	 * Decrypt a base64 encoded, DES encrypted string and return the unencrypted
	 * string.
	 * 
	 * @return String The decrypted string.
	 * @throws Exception
	 *             If an error occurs.
	 */
	public String decryptBase64(String encryptedString) throws Exception {
		// Encode bytes to base64 to get a string
		byte[] decodedBytes = Base64.decodeBase64(encryptedString.getBytes("UTF-8"));

		// Decrypt
		byte[] unencryptedByteArray = decryptCipher.doFinal(decodedBytes);

		// Decode using utf-8
		return new String(unencryptedByteArray, "UTF8");
	}

	/**
	 * Main unit test method.
	 * 
	 * @param args
	 *            Command line arguments.
	 * 
	 */
	public static void main(String args[]) {
//		try {
//			String encryptedString = encryptUserInfo("888801183");
//			System.out.println("Encrypted String:" + encryptedString);
//
//			// Decrypt the string
//			System.out.println("UnEncrypted String:" + decryptUserInfo(encryptedString));
//
//		} catch (Exception e) {
//			System.err.println("Error:" + e.toString());
//		}
		try {
			String encryptedString = encryptUserInfo("000803837");
			System.out.println(encryptedString); // r7QM04a963adsnqyk8whtw==
			
			System.out.println(URLDecoder.decode("r7QM04a963adsnqyk8whtw%253D%253D", "utf-8")); // r7QM04a963adsnqyk8whtw%3D%3D
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(decryptUserInfo("r7QM04a963adsnqyk8whtw%3D%3D"));
	}
	
	/**
	 * 加密用户信息
	 * 
	 * @param userInfo 明码
	 * @return
	 */
	public static String encryptUserInfo(String userInfo) {
		String secretKey = getSecretKey();
		CryptString crypt = new CryptString(secretKey);
		try {
			return URLEncoder.encode(crypt.encryptBase64(userInfo), "utf-8");
//			return crypt.encryptBase64(userInfo);
		} catch (Exception e) {
			log.error("用户身份信息加密失败");
		}
		return null;
	}
	
	public static String decryptUserInfo(String encUserInfo) {
		String secretKey = getSecretKey();
		CryptString crypt = new CryptString(secretKey);
		try {
//			return crypt.decryptBase64(URLDecoder.decode(encUserInfo, "utf-8"));
			return crypt.decryptBase64(encUserInfo);
		} catch (Exception e) {
			log.error("用户身份信息解密失败");
		}
		return null;
	}
	
	/**
	 * 根据loginName生成用户邮箱的密钥
	 * @param loginName - 登录名
	 * @return - 密钥
	 */
	public static String getSecretKey() {
		String secretKey = DigestUtils.md5Hex("ROA") + Context.getSyConf("ROA_USERINFO_SECRETKEY", "");
//		String secretKey = DigestUtils.md5Hex("ROA");
//		System.out.println("---生成的密钥---" + secretKey);
		return secretKey;
	}
}