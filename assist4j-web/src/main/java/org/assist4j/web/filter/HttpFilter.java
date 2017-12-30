package org.assist4j.web.filter;


import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.assist4j.core.CommonUtil;
import org.assist4j.web.HttpMethodRequestWrapper;
import org.assist4j.core.Constant;
import org.assist4j.core.ActionUtil;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;


/**
 * @author yuwei
 */
@Slf4j
public class HttpFilter extends AbstractFilter {
	public static final String DEFAULT_METHOD_PARAM = "_method";
	public static final String DEFAULT_ENCODING = Constant.ENCODING_UTF_8;
	public static final String DEFAULT_STATIC_PATH = "/static/";

	private String methodParam = DEFAULT_METHOD_PARAM;
	private String encoding = DEFAULT_ENCODING;
	private String staticPath = DEFAULT_STATIC_PATH;


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



	@Override
	protected void beforeFilter(HttpServletRequest request, HttpServletResponse response) {
		printParams(request);
		setCharacterEncoding(request, response);
		setContextPath(request);
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
		log.info("Status: {}", response.getStatus());
		allowOrigin(request, response);
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
		ActionUtil.addContextPath(request);
		ActionUtil.addStaticPath(request, staticPath);
	}

	/**
	 * 跨域请求设置
	 */
	private void allowOrigin(HttpServletRequest request, HttpServletResponse response) {
		String referrer = request.getHeader("Referer");
		if(referrer == null) {
			referrer = "*";
		} else {
			log.info("Referrer: {}", referrer);
			String domainUrl = CommonUtil.getDomainUrl(referrer);
			referrer = domainUrl == null ? "*" : domainUrl;
		}

		response.setHeader("Access-Control-Allow-Origin", referrer);
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with, Content-Type");
	}
}
