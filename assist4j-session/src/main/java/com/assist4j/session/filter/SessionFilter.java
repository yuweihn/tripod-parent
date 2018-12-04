package com.assist4j.session.filter;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.assist4j.session.CacheHttpServletRequest;
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
	}
	public SessionFilter() {

	}

	public void setCache(SessionCache cache) {
		this.cache = cache;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		CacheHttpServletRequest cacheRequest = new CacheHttpServletRequest(httpRequest, httpResponse, cache
				, getSessionId(httpRequest, httpResponse));

		chain.doFilter(cacheRequest, httpResponse);
		cacheRequest.sync();
	}

	protected String getSessionId(ServletRequest request, ServletResponse response) {
		return null;
	}

	@Override
	public void init(FilterConfig config) {

	}

	@Override
	public void destroy() {

	}
}
