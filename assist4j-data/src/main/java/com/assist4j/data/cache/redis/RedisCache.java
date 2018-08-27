package com.assist4j.data.cache.redis;


import com.assist4j.data.cache.Cache;
import com.assist4j.data.cache.MessageCache;
import com.assist4j.data.cache.DistLock;


/**
 * @author yuwei
 */
public interface RedisCache extends Cache, MessageCache, DistLock {

}
