package com.assist4j.web.filter;


import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.assist4j.web.HttpMethodRequestWrapper;
import com.assist4j.core.Constant;
import com.assist4j.core.ActionUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


/**
 * @author yuwei
 */
public class HttpFilter extends AbstractFilter {
	private static final Logger log = LoggerFactory.getLogger(HttpFilter.class);
	public static final String DEFAULT_METHOD_PARAM = "_method";
	public static final String DEFAULT_ENCODING = Constant.ENCODING_UTF_8;
	public static final String DEFAULT_STATIC_PATH = "/static/";

	private String methodParam = DEFAULT_METHOD_PARAM;
	private String encoding = DEFAULT_ENCODING;
	private String staticPath = DEFAULT_STATIC_PATH;
	private String protocol = null;


	public void setMethodParam(String methodParam) {
		Assert.hasText(methodParam, "'methodParam' is required.");
		this.methodParam = methodParam;
	}

	public void setEncoding(String encoding) {
		Assert.hasText(encoding, "'encoding' is required.");
		this.encoding = encoding;
	}

	public void setStaticPath(String staticPath) {
		Assert.hasText(staticPath, "'staticPath' is required.");
		this.staticPath = staticPath;
	}

	public void setProtocol(String protocol) {
		Assert.hasText(protocol, "'protocol' is required.");
		this.protocol = protocol;
	}



	@Override
	protected void beforeFilter(HttpServletRequest request, HttpServletResponse response) {
		printParams(request);
		setCharacterEncoding(request, response);
		setContextPath(request);
		setAccessControl(request, response);
	}

	/**
	 * 浏览器不支持put,delete等method,由该filter将/service?_method=delete转换为标准的http delete方法
	 **/
	@Override
	protected HttpServletRequest wrapper(HttpServletRequest request) {
		String paramValue = request.getParameter(methodParam);
		if ("POST".equalsIgnoreCase(request.getMethod()) && StringUtils.hasLength(paramValue)) {
			String method = paramValue.toUpperCase(Locale.ENGLISH);
			return new HttpMethodRequestWrapper(request, method);
		} else {
			return request;
		}
	}

	@Override
	protected void afterFilter(HttpServletRequest request, HttpServletResponse response) {
	}

	/**
	 * 打印请求参数
	 */
	private void printParams(HttpServletRequest request) {
		String ip = ActionUtil.getRequestIP();
		String url = request.getRequestURL().toString();
		String method = request.getMethod().toLowerCase();
		Map<String, String[]> params = request.getParameterMap();

		if(CollectionUtils.isEmpty(params)) {
			log.info("ip: {}, method: {}, url: {}", ip, method, url);
		} else {
			log.info("ip: {}, method: {}, url: {}, params: {}", ip, method, url, params);
		}
	}

	/**
	 * 设置字符集
	 **/
	private void setCharacterEncoding(HttpServletRequest request, HttpServletResponse response) {
		try {
			request.setCharacterEncoding(encoding);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		response.setCharacterEncoding(encoding);
	}

	/**
	 * 将站点域名和static资源地址存入context
	 **/
	private void setContextPath(HttpServletRequest request) {
		ActionUtil.addContextPath(request, protocol);
		ActionUtil.addStaticPath(request, staticPath);
	}

	/**
	 * 跨域请求设置
	 */
	private void setAccessControl(HttpServletRequest request, HttpServletResponse response) {
		String referrer = request.getHeader("Referer");
		if(referrer != null) {
			log.info("Referrer: {}", referrer);
		}

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with, Content-Type");
	}
}
