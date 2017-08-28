package com.rh.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 匹配指定IP地址与预定义的Ip地址字符串。预定义IP地址，可以有多个，多个IP地址之间使用逗号分隔。IP地址中的数据可以使用*号通配符。例如
 * 10.0.1.2，10.0.1.3，10.0.2.*
 * 
 * @author yangjy
 * 
 */
public class IpAddressMatcher {

    private String[] pattern;

    /**
     * 
     * @param strPattern IP地址配置定义，多个IP地址之间使用逗号分隔。IP地址中的数据可以使用*号通配符。
     */
    public IpAddressMatcher(String strPattern) {
        pattern = strPattern.split("\\,");

    }

    /**
     * 验证客户端IP是否与配置IP相符.
     * 
     * @param ip 客户端IP
     * @return 是否匹配，匹配成功返回true，否则返回false。
     */
    public boolean match(String ip) {
        boolean b = false;
        if (pattern != null && pattern.length > 0) {
            for (int i = 0; i < pattern.length; i++) {
                b = match(ip, pattern[i]);
                if (b) {
                    return b;
                }
            }
        }
        return b;
    }

    /**
     * 
     * @param ip 带通配符或不带通配符的地址。可以为:172.16.0.1 或 172.16.0.*
     * @return 转换成正则表达式
     */
    private String convert2Regex(String ip) {
        String result = "^" + ip.replaceAll("\\.", "\\\\.");
        result = result.replaceAll("\\*", "[0-9]+");
        return result;
    }
    
    /**
     * 
     * @param ip 客户端IP
     * @param pattern 带通配符或不带通配符的地址。可以为:172.16.0.1 或 172.16.0.*
     * @return 是否匹配，匹配成功返回true，否则返回false。
     */
    private boolean match(String ip, String pattern) {
        String tempPattern = convert2Regex(pattern);
        Pattern p = Pattern.compile(tempPattern);
        Matcher m = p.matcher(ip);
        boolean b = m.matches();

        return b;
    }
}
