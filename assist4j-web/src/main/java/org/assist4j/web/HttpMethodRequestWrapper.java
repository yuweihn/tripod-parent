package org.assist4j.web;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


/**
 * @author yuwei
 */
public class HttpMethodRequestWrapper extends HttpServletRequestWrapper {
	private String method;
	
	/**
	 * Constructs a request object wrapping the given request.
	 *
	 * @param request
	 * @throws IllegalArgumentException if the request is null
	 */
	public HttpMethodRequestWrapper(HttpServletRequest request, String method) {
		super(request);
		this.method = method;
	}

	@Override
	public String getMethod() {
		return this.method;
	}
}
