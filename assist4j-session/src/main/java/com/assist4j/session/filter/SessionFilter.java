package com.assist4j.session.filter;


import java.io.IOException;
import java.lang.reflect.Constructor;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.assist4j.session.CacheSessionHttpServletRequest;
import com.assist4j.session.SessionUtil;
import com.assist4j.session.cache.SessionCache;
import com.assist4j.session.SessionConstant;


/**
 * @author yuwei
 */
public class SessionFilter implements Filter {
	/**
	 * session有效期(分钟)
	 */
	private int maxInactiveInterval;
	private SessionCache cache;
	/**
	 * 缓存中session对象的key的前缀
	 */
	private String cacheSessionKey;
	/**
	 * Cookie中保存sessionId的属性名称
	 */
	private String cookieSessionName;
	/**
	 * 是否收集session
	 */
	private boolean ifCollect = false;


	public SessionFilter(SessionCache cache, String cacheSessionKey, String cookieSessionName) {
		this(cache, cacheSessionKey, SessionConstant.DEFAULT_MAX_INACTIVE_INTERVAL, cookieSessionName);
	}
	public SessionFilter(SessionCache cache, String cacheSessionKey, int maxInactiveInterval, String cookieSessionName) {
		this(cache, cacheSessionKey, SessionConstant.DEFAULT_MAX_INACTIVE_INTERVAL, cookieSessionName, false);
	}
	/**
	 * @param cache                           缓存引擎
	 * @param cacheSessionKey                 缓存中session对象的key的前缀
	 * @param maxInactiveInterval             session有效期(分钟)
	 * @param cookieSessionName               Cookie中保存sessionId的属性名称
	 * @param ifCollect                       是否收集session(这个字段为true时有效率问题，还需优化。慎用！！！)
	 */
	public SessionFilter(SessionCache cache, String cacheSessionKey, int maxInactiveInterval
			, String cookieSessionName, boolean ifCollect) {
		this.cache = cache;
		this.cacheSessionKey = cacheSessionKey;
		this.maxInactiveInterval = maxInactiveInterval;
		this.cookieSessionName = cookieSessionName;
		this.ifCollect = ifCollect;
		initSessionUtil(cache);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		CacheSessionHttpServletRequest cacheRequest = new CacheSessionHttpServletRequest(httpRequest, httpResponse, cacheSessionKey, cache);
		cacheRequest.setSessionCookieName(cookieSessionName);
		cacheRequest.setMaxInactiveInterval(maxInactiveInterval);
		cacheRequest.setIfCollect(ifCollect);

		chain.doFilter(cacheRequest, httpResponse);
		cacheRequest.syncSessionToCache();
	}

	@Override
	public void init(FilterConfig config) {
		
	}

	@Override
	public void destroy() {
		
	}

	/**
	 * 初始化{@link SessionUtil}
	 * @param cache
	 */
	private void initSessionUtil(SessionCache cache) {
		try {
			Class<?> clz = Class.forName(SessionUtil.class.getName());
			Constructor<?> constructor = clz.getDeclaredConstructor(SessionCache.class, String.class);
			constructor.setAccessible(true);
			constructor.newInstance(cache, cacheSessionKey);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
