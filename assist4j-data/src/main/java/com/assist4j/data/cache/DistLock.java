package com.assist4j.data.cache;




/**
 * 分布式锁
 * @author yuwei
 */
public interface DistLock {
	boolean lock(String key, String owner, long expiredTime);
	/**
	 * @param key
	 * @param owner
	 * @param expiredTime 单位：秒。
	 * @param reentrant   是否可重入
	 * @return
	 */
	boolean lock(String key, String owner, long expiredTime, boolean reentrant);
	boolean unlock(String key, String owner);
}
