package com.rh.core.util.var;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.comm.ConfMgr;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.util.Constant;
import com.rh.core.util.device.DeviceUtils;


/**
 * 应用配置实现变量的接口类，变量格式为"@C_ + 应用配置键值@"
 * 
 * @author Jerry Li
 * @version $Id$
 */
public class ConfVar implements Var {
    /** 单例 */
    private static ConfVar inst = null;
    /**
     * 私有构建体，单例模式
     */
    private ConfVar() {
    }
    
    /**
     * 单例方法
     * @return 获取应用配置变量类
     */
    public static ConfVar getInst() {
        if (inst == null) {
            inst = new ConfVar();
        }
        return inst;
    }
    
    /**
     * 获取变量值
     * @param key 键值
     * @return 变量值
     */
    public String get(String key) {
        return Context.getSyConf(key.substring(3, key.length() - 1), key);
    }
    
    
    /**
     * 获取应用配置Map集合，私有配置和公用配置合并的集合,缺省只生成前端用到的应用配置map
     * @return 指定key的value
     */
    public Map<String, String> getMap() {
        return getMap(true);
    }
    
    /**
     * 获取系统配置Map集合，私有配置和公用配置合并的集合，并追加其他内置变量作为配置（如：是否移动服务、终端类型）
     * @param onlyClient true：只生成给前端的系统配置， false：生成所有的系统配置
     * @return 指定key的value
     */
    public Map<String, String> getMap(boolean onlyClient) {
        Map<String, String> pubConfMap = new HashMap<String, String>();
        Map<String, String> priConfMap = new HashMap<String, String>();
        boolean mobileFlag = Context.appBoolean("MOBILE"); // 如果访问的是移动版系统将key为MB_开头的覆盖无MB的
        Map<String, Bean> itemMap = DictMgr.getItemMap(DictMgr.getDict(ConfMgr.SY_COMM_CONFIG_PUBLIC)); //公有配置
        Set<String> pubKeys = itemMap.keySet();
        for (String key : pubKeys) {
            Bean item = itemMap.get(key);
            if (item != null && (item.getInt("FLAG") != Constant.NO_INT)
                    && !(onlyClient && item.getInt("CONF_FLAG") != Constant.YES_INT)) {
                if (mobileFlag) {
                    if (key.startsWith("MB_")) {
                        key = key.substring(3);
                    } else {
                        if (pubConfMap.containsKey(key)) {
                            continue;
                        }
                    }
                    pubConfMap.put("@C_" + key + "@", item.getStr("NAME"));
                } else {
                    pubConfMap.put("@C_" + key + "@", item.getStr("NAME"));
                }
            }
        }
        itemMap = DictMgr.getItemMap(DictMgr.getDict(ConfMgr.SY_COMM_CONFIG)); //私有配置
        Set<String> priKeys = itemMap.keySet();
        for (String key : priKeys) {
            Bean item = itemMap.get(key);
            if (item != null && (item.getInt("FLAG") != Constant.NO_INT)
                    && !(onlyClient && item.getInt("CONF_FLAG") != Constant.YES_INT)) {
                if (mobileFlag) {
                    if (key.startsWith("MB_")) {
                        key = key.substring(3);
                    } else {
                        if (priConfMap.containsKey(key)) {
                            continue;
                        }
                    }
                    priConfMap.put("@C_" + key + "@", item.getStr("NAME"));
                } else {
                    priConfMap.put("@C_" + key + "@", item.getStr("NAME"));
                }
            }
        }
        //使用私有配置覆盖公有配置
        pubConfMap.putAll(priConfMap);
        
        //是否是移动服务系统
        String isMobileServer = "false";
        if (Context.appBoolean("MOBILE")) {
            isMobileServer = "true";
        }
        pubConfMap.put("@C_MOBILE_SERVER@", isMobileServer);
        
        //终端访问设备类型
        String dev = "";
        if (Context.getRequest() != null) {
            dev = DeviceUtils.getCurrentDevice(Context.getRequest()).getDeviceType().toString();
        }
        pubConfMap.put("@C_DEVICE_TYPE@", dev);
        
        return pubConfMap;
    }
}
