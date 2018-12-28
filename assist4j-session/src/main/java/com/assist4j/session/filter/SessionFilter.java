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
import com.assist4j.session.conf.PathPattern;
import com.assist4j.session.conf.SessionConf;
import com.assist4j.session.conf.ValueSplit;


/**
 * @author yuwei
 */
public class SessionFilter implements Filter {
	/**
	 * 需要排除的URI
	 */
	private static final String EXCLUSIVE = "exclusive";
	/**
	 * 是否分拆value值
	 */
	private static final String SPLIT = "split";
	/**
	 * 分拆value值时每个子串的最大长度
	 */
	private static final String MAX_LENGTH = "maxLength";






	/**
	 * @param cache                           缓存引擎
	 */
	public SessionFilter(SessionCache cache) {
		setCache(cache);
	}
	public SessionFilter() {

	}

	public void setCache(SessionCache cache) {
		SessionConf.getInstance().setCache(cache);
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
		CacheHttpServletRequest cacheRequest = new CacheHttpServletRequest(httpRequest, httpResponse
				, getSessionId(httpRequest, httpResponse));

		chain.doFilter(cacheRequest, httpResponse);
		cacheRequest.sync();
		after(cacheRequest, httpResponse);
	}

	protected String getSessionId(HttpServletRequest request, HttpServletResponse response) {
		return null;
	}

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

		ValueSplit vs = new ValueSplit();
		String split = config.getInitParameter(SPLIT);
		if (split != null) {
			vs.setFlag("1".equals(split) || "true".equalsIgnoreCase(split));
		}
		String maxLength = config.getInitParameter(MAX_LENGTH);
		if (maxLength != null) {
			vs.setMaxLength(Integer.parseInt(maxLength));
		}
		sessionConf.setValueSplit(vs);
	}

	@Override
	public void destroy() {

	}
}
