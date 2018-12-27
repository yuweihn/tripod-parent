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

	String getApplicationName();
	
	/**
	 * session同步完成之后的操作
	 */
	default void afterCompletion(String sessionId) {}
}
