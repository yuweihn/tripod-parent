package com.yuweix.tripod.web.filter;


import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/**
 * @author yuwei
 */
public class PathPattern {
	private final List<String> patterns = new ArrayList<>();
	private PathMatcher matcher = new AntPathMatcher();


	public PathPattern(String... urlPatterns) {
		for (String urlPattern : urlPatterns) {
			if (urlPattern == null || "".equals(urlPattern.trim())) {
				continue;
			}
			this.patterns.add(urlPattern.trim());
		}
	}

	public boolean matches(HttpServletRequest request) {
		return matches(request.getRequestURI());
	}

	public boolean matches(String requestPath) {
		for (String pattern: this.patterns) {
			if (matcher.match(pattern, requestPath)) {
				return true;
			}
		}
		return false;
	}
}
