package com.rh.core.util.msg.listener;

import com.rh.core.util.msg.CommonMsg;
import com.rh.core.util.msg.MsgCenter;

public class WarningMsg extends CommonMsg {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3152998702428916137L;

    /**
     * 构造方法
     */
    private WarningMsg(String serv, String act) {
        super(serv, act);
    }
    public static WarningMsg newInstance(String provider, 
            String providerName, String content, String data, 
            String user, String time) {
        return new WarningMsg(provider, providerName, content, data, user, time);
    }
    public WarningMsg(String provider, 
            String providerName, String content, String data, 
            String user, String time) {
        this(MsgCenter.WARNINGLOG_MSG_TYPE, providerName);
        setProvider(provider);
        setProviderName(providerName);
        setContent(content);
        setData(data);
        setUserCode(user);
        setTime(time);
    }
    
    /**
     * 设置来源编码
     */
    public void setProvider(String provider) {
        set("PROVIDER", provider);
    }
    /**
     * 取得来源编码
     */
    public String getProvider() {
        return getStr("PROVIDER");
    }
    
    /**
     * 设置来源名称
     */
    public void setProviderName(String providerName) {
        set("PROVIDER_NAME", providerName);
    }
    /**
     * 取得来源名称
     */
    public String getProviderName() {
        return getStr("PROVIDER_NAME");
    }
    
    /**
     * 设置事件内容
     */
    public void setContent(String content) {
        set("CONTENT", content);
    }
    /**
     * 取得事件内容
     */
    public String getContent() {
        return getStr("CONTENT");
    }
    
    /**
     * 设置扩展数据
     */
    public void setData(String data) {
        set("DATA", data);
    }
    /**
     * 取得扩展数据
     */
    public String getData() {
        return getStr("DATA");
    }
    
    /**
     * 设置用户编码
     */
    public void setUserCode(String user) {
        set("S_USER", user);
    }
    /**
     * 取得用户编码
     */
    public String getUserCode() {
        return getStr("S_USER");
    }
    
    /**
     * 设置时间
     */
    public void setTime(String time) {
        set("A_TIME", time);
    }
    /**
     * 取得时间
     */
    public String getTime() {
        return getStr("A_TIME");
    }
    
}
