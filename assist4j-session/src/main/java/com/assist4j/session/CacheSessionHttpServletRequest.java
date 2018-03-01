package com.assist4j.session;


import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.assist4j.session.cache.SessionCache;


/**
 * HttpServletRequest的包装器，修改了获取Session方法的行为。
 * 现在将返回一个实际型别是(@see com.assist4j.session.CacheHttpSession)的HttpSession实例。
 *
 * 判断Session是否为失效超过最大不活动时间的时机是每次获取Session时。
 * 比如getSession()和getSession(true)都将造成更新最后访问时间。
 * 但是getSession(false)被调用时，如果本身没有绑定Session那么不会有这个动作。
 *
 * @author yuwei
 */
public class CacheSessionHttpServletRequest extends HttpServletRequestWrapper {
	private SessionCache cache;
	/**
	 * session有效期(分钟)
	 */
	private int maxInactiveInterval;
	private CacheHttpSession cacheSession;
	/**
	 * Cookie中保存sessionID的属性名称
	 */
	private String sessionCookieName;
	private String sessionKeyPrefix;

	private HttpServletRequest request;
	private HttpServletResponse response;
	
	
	/**
	 * 构造一个HttpServletRequest的包装器。
	 */
	public CacheSessionHttpServletRequest(HttpServletRequest request, HttpServletResponse response, String sessionKeyPrefix, SessionCache cache) {
		super(request);
		this.request = request;
		this.response = response;
		this.cache = cache;
		this.sessionKeyPrefix = sessionKeyPrefix;
	}


	/**
	 * 获取会话实例，如果不存在则创建。
	 * @return 会话实例。
	 */
	@Override
	public HttpSession getSession() {
		return getSession(true);
	}

	/**
	 * 获取会话实例，调用者指定如果不存在时是否创建一个新的。
	 * @param create true不存在则创建，false不存在返回null.
	 * @return 会话实例。
	 */
	@Override
	public HttpSession getSession(boolean create) {
		return doGetSession(create);
	}

	/**
	 * 设置在Cookie中保存sessionID的属性名称.
	 * @param sessionCookieName 属性名称。
	 */
	public void setSessionCookieName(String sessionCookieName) {
		this.sessionCookieName = sessionCookieName;
	}

	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}

	/**
	 * 如果Session不为空，同步至缓存。
	 */
	public void syncSessionToCache() {
		if (cacheSession != null) {
			cacheSession.syncToCache();
		}
	}

	/**
	 * 实际构造会话实例的方法。根据参数来决定如果当前没有绑定时是否进行创建。
	 * @param create true创建，false不创建。
	 * @return 会话实例。
	 */
	private HttpSession doGetSession(boolean create) {
		if (cacheSession == null) {
			String sessionId = CookiesUtil.findValueByKey(request, sessionCookieName);
			if (sessionId != null) {
				cacheSession = buildCacheHttpSession(sessionId, false);
			} else {
				cacheSession = buildCacheHttpSession(create);
			}
		}

		if (cacheSession != null) {
			/**
			 * 判断现有session是否已失效，是则新建
			 */
			if (cacheSession.isInvalid()) {
				cacheSession.removeSessionFromCache();
				cacheSession = buildCacheHttpSession(create);
			}

			if (cacheSession != null) {
				cacheSession.access();
			}
		}

		return cacheSession;
	}

	/**
	 * 根据指定的id构造一个新的会话实例。
	 * @param sessionId 会话id.
	 * @param cookie 是否更新cookie值。true更新，false不更新。
	 * @return 会话实例。
	 */
	private CacheHttpSession buildCacheHttpSession(String sessionId, boolean cookie) {
		CacheHttpSession session = new CacheHttpSession(sessionId, maxInactiveInterval, sessionKeyPrefix, cache);

		if (cookie) {
			CookiesUtil.addCookie(request, response, sessionCookieName, sessionId, CookiesUtil.COOKIE_MAXAGE_DEFAULT);
		}

		return session;
	}

	/**
	 * 以UUID的方式构造一个会话实例。如果create为false则返回null.
	 * @param create false方法调用返回null.
	 * @return 会话实例。
	 */
	private CacheHttpSession buildCacheHttpSession(boolean create) {
		String sessionId = UUID.randomUUID().toString().replace("-", "");
		return create ? buildCacheHttpSession(sessionId, true) : null;
	}
}
