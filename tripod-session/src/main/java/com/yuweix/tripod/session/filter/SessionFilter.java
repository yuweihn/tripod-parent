package com.yuweix.tripod.session.filter;


import com.yuweix.tripod.core.json.Fastjson;
import com.yuweix.tripod.core.json.Json;
import com.yuweix.tripod.session.CacheHttpServletRequest;
import com.yuweix.tripod.session.cache.SessionCache;
import com.yuweix.tripod.session.conf.PathPattern;
import com.yuweix.tripod.session.conf.SessionConf;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author yuwei
 */
public abstract class SessionFilter implements Filter {
	/**
	 * 需要排除的URI
	 */
	private static final String EXCLUSIVE = "exclusive";


	public SessionFilter() {
		this(null);
	}
	/**
	 * @param cache
	 */
	public SessionFilter(SessionCache cache) {
		setCache(cache);
		setJson(new Fastjson());
	}

	public void setCache(SessionCache cache) {
		SessionConf.getInstance().setCache(cache);
	}
	
	public void setJson(Json json) {
		SessionConf.getInstance().setJson(json);
	}
	/**
	 * 设置session失效时间(分钟)
	 */
	public void setMaxInactiveInterval(int maxInactiveInterval) {
		SessionConf.getInstance().setMaxInactiveInterval(maxInactiveInterval);
	}
	public void setApplicationName(String applicationName) {
		SessionConf.getInstance().setApplicationName(applicationName);
	}


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		PathPattern exclusivePattern = SessionConf.getInstance().getExclusivePattern();
		if (exclusivePattern != null && exclusivePattern.matches(httpRequest)) {
			chain.doFilter(request, response);
			return;
		}

		before(httpRequest, httpResponse);
		CacheHttpServletRequest cacheRequest = new CacheHttpServletRequest(httpRequest, getSessionId(httpRequest, httpResponse));

		chain.doFilter(cacheRequest, httpResponse);
		cacheRequest.sync();
		after(cacheRequest, httpResponse);
	}

	protected abstract String getSessionId(HttpServletRequest request, HttpServletResponse response);

	protected void before(HttpServletRequest request, HttpServletResponse response) {

	}

	protected void after(HttpServletRequest request, HttpServletResponse response) {

	}

	@Override
	public void init(FilterConfig config) {
		SessionConf sessionConf = SessionConf.getInstance();

		String exclusive = config.getInitParameter(EXCLUSIVE);
		if (exclusive != null && !"".equals(exclusive.trim())) {
			sessionConf.setExclusivePattern(exclusive.split(","));
		}

		sessionConf.check();
	}

	@Override
	public void destroy() {

	}
}
