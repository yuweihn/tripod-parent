package com.assist4j.data.cache.redis;


import java.util.List;

import com.assist4j.data.cache.Cache;
import com.assist4j.data.cache.MessageCache;
import com.assist4j.data.cache.Lock;


/**
 * @author yuwei
 */
public interface RedisCache extends Cache, MessageCache, Lock {
	String execute(String script, List<String> keyList, List<String> argList);
}
