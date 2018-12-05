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
public class CacheHttpServletRequest extends HttpServletRequestWrapper {
	private SessionCache cache;
	private CacheHttpSession cacheSession;

	private HttpServletRequest request;
	private HttpServletResponse response;

	/**
	 * 如果未指定sessionId，则由系统自动生成，否则使用指定的sessionId
	 */
	private String sessionId;


	/**
	 * 构造一个HttpServletRequest的包装器。
	 */
	public CacheHttpServletRequest(HttpServletRequest request, HttpServletResponse response, SessionCache cache) {
		this(request, response, cache, null);
	}
	public CacheHttpServletRequest(HttpServletRequest request, HttpServletResponse response, SessionCache cache, String sessionId) {
		super(request);
		this.request = request;
		this.response = response;
		this.cache = cache;
		if (sessionId != null && !"".equals(sessionId.trim())) {
			this.sessionId = sessionId.trim();
		}
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
	 * 如果Session不为空，同步至缓存。
	 */
	public void sync() {
		if (cacheSession == null) {
			return;
		}
		String fullSessionId = cacheSession.sync();
		if (fullSessionId == null || "".equals(fullSessionId)) {
			return;
		}
		cache.afterCompletion(fullSessionId);
	}

	/**
	 * 实际构造会话实例的方法。根据参数来决定如果当前没有绑定时是否进行创建。
	 * @param create true创建，false不创建。
	 * @return 会话实例。
	 */
	private HttpSession doGetSession(boolean create) {
		if (cacheSession == null) {
			if (sessionId != null) {
				cacheSession = new CacheHttpSession(sessionId, cache);
			} else {
				String sid = CookiesUtil.findValueByKey(request, cache.getCookieSessionName());
				if (sid != null) {
					cacheSession = new CacheHttpSession(sid, cache);
				} else if (create) {
					cacheSession = new CacheHttpSession(generateSessionId(), cache);
				}
			}
		}

		if (cacheSession != null) {
			/**
			 * 判断现有session是否已失效，是则新建
			 */
			if (cacheSession.isInvalid()) {
				cacheSession.removeSessionFromCache();
				if (sessionId != null) {
					cacheSession = new CacheHttpSession(sessionId, cache);
				} else if (create) {
					cacheSession = new CacheHttpSession(generateSessionId(), cache);
				}
			}

			if (cacheSession != null) {
				cacheSession.access();
			}
		}

		return cacheSession;
	}

	private String generateSessionId() {
		String sid = UUID.randomUUID().toString().replace("-", "");
		if (sid == null || "".equals(sid)) {
			throw new RuntimeException("生成SessionId失败！！！");
		}
		addCookie(sid);
		return sid;
	}

	/**
	 * 更新cookie值
	 */
	private void addCookie(String sessionId) {
		CookiesUtil.addCookie(request, response, cache.getCookieSessionName(), sessionId, SessionConstant.COOKIE_MAX_AGE_DEFAULT);
	}
}
