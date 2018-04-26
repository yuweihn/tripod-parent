package com.assist4j.web.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author yuwei
 */
public abstract class AbstractFilter extends OncePerRequestFilter {
	private static final Logger log = LoggerFactory.getLogger(AbstractFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		long startTimeMillis = System.currentTimeMillis();
		HttpServletRequest requestWrapper = wrapper(request);
		beforeFilter(request, response);
		filterChain.doFilter(requestWrapper, response);
		afterFilter(requestWrapper, response);
		long endTimeMillis = System.currentTimeMillis();
		log.info("Status: {}. Time Cost: {} ms.", response.getStatus(), endTimeMillis - startTimeMillis);
	}

	protected abstract void beforeFilter(HttpServletRequest request, HttpServletResponse response);

	protected HttpServletRequest wrapper(HttpServletRequest request) {
		return request;
	}

	protected abstract void afterFilter(HttpServletRequest request, HttpServletResponse response);
}
