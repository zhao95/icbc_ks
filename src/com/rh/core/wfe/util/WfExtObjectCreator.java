package com.rh.core.wfe.util;

import org.apache.commons.lang.StringUtils;

import com.rh.core.util.Lang;


/**
 * 解析工作流定义上配置的扩展类及其对扩张类的配置。
 * 
 * @author yangjy
 */
public class WfExtObjectCreator {
    
    private String clsConf = "";
    
    private String config = "";
    
    /**
     * @param extClsConf 扩展类设置字符串。格式为：类名,配置值
     */
    public WfExtObjectCreator(String extClsConf) {
        this.clsConf = extClsConf;
    }
    
    /**
     * @param <T> 类名
     * @param clz 需要创建的实例对象类型
     * @return 指定的实例对象
     */
    public <T> T create(Class<T> clz) {
        if (StringUtils.isNotBlank(clsConf)) {
            String clsName = clsConf;
            int pos = clsName.indexOf(",");
            int keyLen = 1;
            if (pos > 0) {
                config = clsName.substring(pos + keyLen);
                clsName = clsName.substring(0, pos);
            }
            
            return Lang.createObject(clz, clsName);
        }
        return null;
    }
    
    /**
     * @return 对于扩展类的配置字符串
     */
    public String getConfig() {
        return this.config;
    }
}
