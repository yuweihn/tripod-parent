package com.assist4j.session.cache;


import com.assist4j.session.SessionConstant;

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
	default int getMaxInactiveInterval() {
		return SessionConstant.DEFAULT_MAX_INACTIVE_INTERVAL;
	}

	String getApplicationName();
	
	/**
	 * session同步完成之后的操作
	 */
	default void afterCompletion(String sessionId) {}
}
