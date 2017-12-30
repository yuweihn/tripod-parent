package org.assist4j.session.cache;


/**
 * 缓存引擎接口
 * @author yuwei
 */
public interface SessionCache {
	/**
	 * expiredTime 有效期(单位：秒)。
	 */
	boolean put(String key, String value, long expiredTime);
	String get(String key);
	void remove(String key);	
}
