package com.assist4j.session.cache;


/**
 * @author yuwei
 */
public interface SessionCache {
	/**
	 * expiredTime 有效期(单位：秒)。
	 */
	boolean put(String key, String value, long expiredTime);
	String get(String key);
	void remove(String key);

	/**
	 * session有效期(分钟)
	 */
	int getMaxInactiveInterval();
	/**
	 * 缓存中session对象的key的前缀
	 */
	String getCacheSessionKey();
	/**
	 * Cookie中保存sessionId的属性名称
	 */
	String getCookieSessionName();
	
	/**
	 * session保存完成之后的操作
	 */
	void afterCompletion(String sessionId);
}
