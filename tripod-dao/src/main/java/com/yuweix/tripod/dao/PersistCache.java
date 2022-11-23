package com.yuweix.tripod.dao;



/**
 * @author yuwei
 */
public interface PersistCache {
	/**
	 * @param key
	 * @param value
	 * @param timeout 过期时间(s)。
	 * @return
	 */
	<T>boolean put(String key, T value, long timeout);
	<T>T get(String key);
	void remove(String key);
}
