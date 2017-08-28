/*********************************************
 * Copyright (c) 2009 ICBC.
 * All rights reserved.
 * Created on 2009-5-5 ����04:17:06
 * 
 * Contributors:
 *     ctp - initial implementation
 *********************************************/
package com.rh.core.icbc.sso;

import java.security.PrivateKey;
import java.security.PublicKey;

import com.icbc.ssic.base.PKIException;
import com.icbc.ssic.base.SSICService;
import com.rh.core.base.Context;

public class SsicManager {

	// 单例对象
	private static SsicManager instance;

	// 配置参数
	private String ssicKeyPath = Context.getSyConf("PE_HANDSHAKE_KEY_PATH", "/Users/if/Documents/code/eclipse/icbc/pcserver_icbc/pro/WEB-INF/config");

	private String ssicServerkeyName = Context.getSyConf("PE_HANDSHAKE_SERVER_KEY_NAME", "SSIC");

	private String ssicClientKeyName = Context.getSyConf("PE_HANDSHAKE_CLIENT_KEY_NAME", "OIS");

	private String privKeyPasswd = Context.getSyConf("PE_HANDSHAKE_KEY_PASSWD", "1a2b3c");

	private String tsInterval = Context.getSyConf("PE_HANDSHAKE_TS_INTERVAL", "600000000000");

	private String ssicIp = Context.getSyConf("PE_HANDSHAKE_SSIC_IP", "122.16.173.105:11688");

	private String clientIp = Context.getSyConf("PE_HANDSHAKE_CLIENT_IP", "122.18.157.137:9081");

	private String ssicVersion = Context.getSyConf("PE_HANDSHAKE_SSIC_VERSION", "1.0");

	private PublicKey publicKey;

	private PrivateKey privateKey;

	protected SsicManager() {

	}

	/**
	 * 单例
	 * @return
	 */
	public static SsicManager getInstance() {
		if (instance == null) {
			instance = new SsicManager();
		}
		return instance;
	}

	/**
	 * 使用spring框架的初始化操作
	 * @throws PKIException
	 */
	public void initialize() throws PKIException {
		try {
//			String ssicFlag = CTEConstance.getEnvProperty("enableSSIC");
			//if ("true".equals(ssicFlag)) {
				SSICService ssic = new SSICService(getSsicServerkeyName());
				SSICService client = new SSICService(getSsicClientKeyName());
				client.initialize(getPrivKeyPasswd(), getSsicKeyPath());
				ssic.initialize(getSsicKeyPath());
				publicKey = ssic.getPublicKey();
				privateKey = client.getPrivateKey();
			//}
		} catch (PKIException e) {
			throw e;
		}
	}

	/**
	 * 终止
	 */
	public void terminate() {
		instance = null;
	}

	public void setSsicKeyPath(String ssicKeyPath) {
		this.ssicKeyPath = ssicKeyPath;
	}

	public String getSsicKeyPath() {
		return ssicKeyPath;
	}

	public void setSsicServerkeyName(String ssicServerkeyName) {
		this.ssicServerkeyName = ssicServerkeyName;
	}

	public String getSsicServerkeyName() {
		return ssicServerkeyName;
	}

	public void setSsicClientKeyName(String ssicClientKeyName) {
		this.ssicClientKeyName = ssicClientKeyName;
	}

	public String getSsicClientKeyName() {
		return ssicClientKeyName;
	}

	public void setPrivKeyPasswd(String privKeyPasswd) {
		this.privKeyPasswd = privKeyPasswd;
	}

	public String getPrivKeyPasswd() {
		return privKeyPasswd;
	}

	public void setTsInterval(String tsInterval) {
		this.tsInterval = tsInterval;
	}

	public String getTsInterval() {
		return tsInterval;
	}

	public void setSsicIp(String ssicIp) {
		this.ssicIp = ssicIp;
	}

	public String getSsicIp() {
		return ssicIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setSsicVersion(String ssicVersion) {
		this.ssicVersion = ssicVersion;
	}

	public String getSsicVersion() {
		return ssicVersion;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}
}
