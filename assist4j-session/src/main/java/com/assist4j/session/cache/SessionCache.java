package com.assist4j.session.cache;


/**
 * 缓存引擎接口
 * @author yuwei
 */
public interface SessionCache {
	/**
	 * expiredTime 有效期(单位：秒)。
	 */
	<T>boolean put(String key, T value, long expiredTime);
	<T>T get(String key);
	void remove(String key);
}
