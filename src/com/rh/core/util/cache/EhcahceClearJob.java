package com.rh.core.util.cache;

import org.apache.log4j.Logger;

import com.rh.core.base.Context;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.util.scheduler.RhJobContext;
import com.rh.core.util.scheduler.RhLocalJob;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

public class EhcahceClearJob extends RhLocalJob {

	private Logger log = Logger.getLogger(getClass());

	@Override
	protected void executeJob(RhJobContext context) {
		evictExpiredElements();
		log.info("-----清理缓存成功----");
	}

	@Override
	public void interrupt() {

	}
	
	/**
     * 清除过期的缓存元素
     * @param param
     * @return
     */
    public void evictExpiredElements(){
    	CacheManager cacheMgr = EhCacheProvider.cacheMgr;
    	if(cacheMgr == null){
    		log.error("clear ehcache failed : cacheMgr is null !");
    	}
    	String[] cacheNames = cacheMgr.getCacheNames();
    	if(cacheNames == null){
    		log.error("clear ehcache failed : cacheNames is null !");
    	}
    	for(String cacheName : cacheNames){
    		Cache cache = cacheMgr.getCache(cacheName);
    		cache.evictExpiredElements();
    	}
    }

}
