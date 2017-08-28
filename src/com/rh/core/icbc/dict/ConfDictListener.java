package com.rh.core.icbc.dict;

import com.rh.core.base.Bean;
import com.rh.core.serv.dict.DictListener;
import com.rh.core.util.EncryptUtils;

/**
 * 服务配置字典在装载至内存中时将加密字段解密
 * @author zhangjx
 *
 */
public class ConfDictListener implements DictListener {

	@Override
	public void each(Bean item) {
		// 如果SY_COMM_CONFIG的字典项中CONF_ENCRYPT是1，即需要加密VALUE值，将进行解密
		int confEncrypt = item.getInt("CONF_ENCRYPT");
		// 如果加密标志为1，即为加密
		if (confEncrypt == 1) {
			// 解密VALUE
			String encValue = item.getStr("NAME");
			String confValue = EncryptUtils.desDecrypt(encValue);
			item.set("NAME", confValue);
		}
	}
}
