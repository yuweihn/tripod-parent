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


/**
 * @author yuwei
 */
public class SessionFilter implements Filter {
	private SessionCache cache;


	/**
	 * @param cache                           缓存引擎
	 */
	public SessionFilter(SessionCache cache) {
		this.cache = cache;
		initSessionUtil(cache);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		CacheSessionHttpServletRequest cacheRequest = new CacheSessionHttpServletRequest(httpRequest, httpResponse, cache);

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
			Constructor<?> constructor = clz.getDeclaredConstructor(SessionCache.class);
			constructor.setAccessible(true);
			constructor.newInstance(cache);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
