package com.assist4j.session;


import java.util.Date;
import com.assist4j.session.cache.SessionCache;
import com.assist4j.session.conf.SessionConf;


/**
 * @author yuwei
 */
public abstract class SessionUtil {

	/**
	 * 根据指定sessionId获得登录时间
	 * eg.  sessionId:  cache.assist4j.session.9ee627c1a0d14d17a5c794ad2dd8421d
	 * @return
	 */
	public static Date getCreateTimeBySessionId(String sessionId) {
		if (sessionId == null) {
			return null;
		}
		SessionCache cache = SessionConf.getInstance().getCache();
		SessionAttribute attribute = SessionAttribute.decode(cache.get(sessionId));
		return attribute == null ? null : attribute.getCreateTime();
	}

	/**
	 * 查询指定session中指定属性的值
	 */
	@SuppressWarnings("unchecked")
	public static<T> T getAttributeBySessionId(String sessionId, String key) {
		if (sessionId == null) {
			return null;
		}
		SessionCache cache = SessionConf.getInstance().getCache();
		SessionAttribute attribute = SessionAttribute.decode(cache.get(sessionId));
		if (attribute == null) {
			return null;
		}
		return (T) attribute.getAttribute(key);
	}

	public static<T> String getSessionIdByRepeatKey(T repeatKey) {
		SessionCache cache = SessionConf.getInstance().getCache();
		String sessionIdKey = SessionConstant.SESSION_ID_PRE + cache.getApplicationName() + "." + SessionConstant.SESSION_ID_KEY_CURRENT + "." + repeatKey.toString();
		return cache.get(sessionIdKey);
	}
}
