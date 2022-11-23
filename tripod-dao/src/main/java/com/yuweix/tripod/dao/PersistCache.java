package com.yuweix.tripod.dao;



/**
 * @author yuwei
 */
public interface PersistCache {
	<T>boolean put(String key, T t);
	<T>T get(String key);
	void remove(String key);
}
