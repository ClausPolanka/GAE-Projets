package com.swagswap.dao.cache;

import java.util.Collections;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;

import org.apache.log4j.Logger;

public class SwagCacheManagerImpl implements SwagCacheManager {
	
	private static final Logger log = Logger.getLogger(ItemCacheManager.class);

	private Cache cache;
	
	

	public SwagCacheManagerImpl() {
		super();
		createCache();
	}

	/* (non-Javadoc)
	 * @see com.swagswap.dao.cache.SwagCacheManager#getCache()
	 */
	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	private void createCache() {
		log.info("Creating swagCache");
		cache = CacheManager.getInstance().getCache("swagCache");
		if (cache == null) {
			try {
				cache = CacheManager.getInstance().getCacheFactory()
						.createCache(Collections.emptyMap());
				CacheManager.getInstance()
						.registerCache("swagCache", cache);

			} catch (CacheException e) {
				throw new RuntimeException("Failure to create swagCache", e);
			}
		}
	}
		
	
}
