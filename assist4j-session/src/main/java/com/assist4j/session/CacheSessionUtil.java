package com.assist4j.session;


import java.util.Date;
import com.assist4j.session.cache.SessionCache;


/**
 * @author yuwei
 */
public class CacheSessionUtil {
	private String sessionIdKeyPre;
	private SessionCache cache;

	private static volatile CacheSessionUtil instance;



	private CacheSessionUtil() {

	}
	private CacheSessionUtil(SessionCache cache, String sessionIdKeyPre) {
		instance = new CacheSessionUtil();
		instance.cache = cache;
		instance.sessionIdKeyPre = sessionIdKeyPre;
	}

	public static CacheSessionUtil getInstance() {
		if (instance == null || instance.cache == null) {
			throw new RuntimeException("对象未初始化");
		}
		return instance;
	}

	/**
	 * 根据指定sessionId获得登录时间
	 * eg.  sessionId:  cache.weixin.session.9ee627c1a0d14d17a5c794ad2dd8421d
	 * @return
	 */
	public Date getCreateTimeBySessionId(String sessionId) {
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
	public <T>T getAttributeBySessionId(String sessionId, String key) {
		if (sessionId == null) {
			return null;
		}
		CacheSessionAttribute attribute = CacheSessionAttribute.decode(cache.get(sessionId));
		if (attribute == null) {
			return null;
		}
		return (T) attribute.getAttribute(key);
	}

	public <T>String getSessionIdByUserId(T userId) {
		String sessionIdKey = sessionIdKeyPre + "." + userId;
		return cache.get(sessionIdKey);
	}
}
