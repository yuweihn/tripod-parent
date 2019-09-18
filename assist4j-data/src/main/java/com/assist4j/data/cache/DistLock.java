package com.assist4j.data.cache;




/**
 * 分布式锁
 * @author yuwei
 */
public interface DistLock {
	/**
	 * @param expiredTime 单位：秒。
	 */
	boolean lock(String key, String owner, long expiredTime);
	boolean lock(String key, String owner, long expiredTime, boolean reentrant);
	boolean unlock(String key, String owner);
}
