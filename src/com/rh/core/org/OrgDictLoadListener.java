package com.rh.core.org;

import com.rh.core.serv.dict.DictMgr;

/**
 * 
 * @author yangjy
 *
 */
public class OrgDictLoadListener {
    /**
     * 加载服务监听
     */
    public void start() {
    	DictMgr.firstLoadCache();
    }
    /**
     * 销毁
     */
    public void stop() {
    }
}
