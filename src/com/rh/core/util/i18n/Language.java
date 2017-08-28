package com.rh.core.util.i18n;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.BaseContext;
import com.rh.core.base.BaseContext.APP;
import com.rh.core.base.Context;

public class Language {
	
	private static Log log = LogFactory.getLog(Language.class);
	
	private static LinkedHashMap<String, String> resourceMap = null;

	public static void initMap() {
		if (resourceMap == null) {
			resourceMap = new LinkedHashMap<String, String>();
			initMap(resourceMap);
		}
	}

	private static synchronized void initMap(LinkedHashMap<String, String> map) {

		Properties prop = BaseContext.getProperties(BaseContext.app(APP.WEBINF) + "/language.properties");

		Enumeration<?> enum1 = prop.propertyNames();
		
		while (enum1.hasMoreElements()) {
			
			String strKey = (String) enum1.nextElement();
			
			String strValue = prop.getProperty(strKey);
			
			map.put(strKey, strValue);
		}
	}
	
	public static String trans(String msg) {

		try {

			initMap();

			if (!isEn() && !isChinese(msg)) {
				return msg;
			}

			for (Map.Entry<String, String> entry : resourceMap.entrySet()) {

				String key = entry.getKey();

				String val = entry.getValue();

				if (msg.equals(val)) {
					log.error("trans " + msg + " to: " + transPunctuation(key));
					msg = transPunctuation(key);
					break;

				} else if (msg.indexOf(val) > -1) {

					String temp = transPunctuation(msg.replace(val, key));

					if (isChinese(temp)) {
						msg = trans(temp);
						break;
					} else {
						log.error("trans " + msg + " to: " + temp);
						msg = temp;
						break;
					}
				}
			}
		} catch (Exception e) {
			log.error(e);
		}
		return msg;
	}
	
	private static boolean isChinese(String str) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (!m.find()) {
			return false;
		}
		return true;
	}
	
	public static boolean isEn() {
		boolean isEn = false;
		Cookie[] cookies = Context.getRequest().getCookies();

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("RhLanguage")
					&& cookie.getValue().equals("en")) {
				isEn = true;
				break;
			}
		}
		return isEn;
	}
	
	private static String transPunctuation(String msg) {
		
		return msg.replaceAll("_", " ").replaceAll("。", ".")
				.replaceAll("，", ",").replaceAll("？", "?").replaceAll("！", "!");

	}

}
