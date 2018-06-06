package com.assist4j.session;


import java.util.Date;
import com.assist4j.session.cache.SessionCache;


/**
 * @author yuwei
 */
public final class SessionUtil {
	private static SessionCache cache;
	private static String sessionIdKeyPre;


	private SessionUtil(SessionCache cache, String sessionIdKeyPre) {
		SessionUtil.cache = cache;
		SessionUtil.sessionIdKeyPre = sessionIdKeyPre;
	}


	/**
	 * 根据指定sessionId获得登录时间
	 * eg.  sessionId:  cache.weixin.session.9ee627c1a0d14d17a5c794ad2dd8421d
	 * @return
	 */
	public static Date getCreateTimeBySessionId(String sessionId) {
		if (sessionId == null) {
			return null;
		}
		CacheSessionAttribute attribute = CacheSessionAttribute.decode(cache.get(sessionId));
		return attribute == null ? null : attribute.getCreateTime();
	}

	/**
	 * 查询指定session中指定属性的值
	 */
	@SuppressWarnings("unchecked")
	public static <T>T getAttributeBySessionId(String sessionId, String key) {
		if (sessionId == null) {
			return null;
		}
		CacheSessionAttribute attribute = CacheSessionAttribute.decode(cache.get(sessionId));
		if (attribute == null) {
			return null;
		}
		return (T) attribute.getAttribute(key);
	}

	public static <T>String getSessionIdByUserId(T userId) {
		String sessionIdKey = sessionIdKeyPre + "." + userId.toString();
		return cache.get(sessionIdKey);
	}
}
