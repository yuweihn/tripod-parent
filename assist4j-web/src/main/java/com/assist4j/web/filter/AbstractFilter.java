package com.assist4j.web.filter;


import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author yuwei
 */
@Slf4j
public abstract class AbstractFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		long startTimeMillis = System.currentTimeMillis();
		beforeFilter(request, response);
		HttpServletRequest requestWrapper = wrapper(request);
		filterChain.doFilter(requestWrapper, response);
		afterFilter(requestWrapper, response);
		long endTimeMillis = System.currentTimeMillis();
		log.info("This call takes {} milliseconds.", endTimeMillis - startTimeMillis);
	}

	protected abstract void beforeFilter(HttpServletRequest request, HttpServletResponse response);

	protected HttpServletRequest wrapper(HttpServletRequest request) {
		return request;
	}

	protected abstract void afterFilter(HttpServletRequest request, HttpServletResponse response);
}
