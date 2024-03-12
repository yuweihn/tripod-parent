package com.yuweix.tripod.web.filter;


import com.yuweix.tripod.core.ActionUtil;
import com.yuweix.tripod.core.Constant;

import com.yuweix.tripod.core.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * @author yuwei
 */
public abstract class AbstractFilter<R extends HttpServletRequest, T extends HttpServletResponse> extends OncePerRequestFilter {
	private static final Logger log = LoggerFactory.getLogger(AbstractFilter.class);

	private static final String ACCESS_CONTROL_REQUEST_HEADERS = "access-control-request-headers";
	private static final String DEFAULT_METHOD_PARAM = "_method";
	private static final String DEFAULT_ENCODING = Constant.ENCODING_UTF_8;
	private static final String DEFAULT_STATIC_PATH = "/static/";

	private String methodParam = DEFAULT_METHOD_PARAM;
	private String encoding = DEFAULT_ENCODING;
	private String staticPath = DEFAULT_STATIC_PATH;
	/**
	 * 跨域白名单
	 * 如果originWhiteList为空，所有origin都可访问，否则只允许规定的origin访问
	 */
	private List<String> originWhiteList = new ArrayList<>();
	private boolean allowLogRequest = true;

	private boolean logAllHeaders = false;
	private final List<String> logHeaders = new ArrayList<>();


	public void setMethodParam(String methodParam) {
		this.methodParam = methodParam;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setStaticPath(String staticPath) {
		this.staticPath = staticPath;
	}

	public void setOriginWhiteList(List<String> originWhiteList) {
		this.originWhiteList = originWhiteList;
	}

	public void setAllowLogRequest(boolean allowLogRequest) {
		this.allowLogRequest = allowLogRequest;
	}

	public void setLogHeaders(String headers) {
		if ("*".equals(headers)) {
			this.logAllHeaders = true;
			return;
		}
		this.logAllHeaders = false;
		if (headers == null) {
			return;
		}
		String[] arr = headers.trim().split(",");
		if (arr.length <= 0) {
			return;
		}
		this.logHeaders.clear();
		for (String h: arr) {
			if (h == null || h.trim().isEmpty()) {
				continue;
			}
			this.logHeaders.add(h.trim());
		}
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		PathPattern exclusivePattern = InitParameter.getInstance().getExclusivePattern();
		if (exclusivePattern != null && exclusivePattern.matches(request)) {
			filterChain.doFilter(request, response);
			return;
		}

		long startTimeMillis = System.currentTimeMillis();
		R req = wrap(adjustMethod(request));
		T resp = wrap(response);
		beforeFilter(req, resp);

		setCharacterEncoding(req, resp);
		setContextPath(req);
		setAccessControl(req, resp);

		filterChain.doFilter(req, resp);

		Map<String, Object> logInfoMap = new LinkedHashMap<>();
		if (allowLogRequest) {
			Map<String, Object> map = logRequest(req);
			if (map != null && !map.isEmpty()) {
				logInfoMap.putAll(map);
			}
		}
		Map<String, Object> requestHeader = getRequestHeader(req);
		if (requestHeader != null && !requestHeader.isEmpty()) {
			logInfoMap.put("headers", requestHeader);
		}
		Object responseBody = getResponseBody(resp);
		if (responseBody != null) {
			logInfoMap.put("responseBody", responseBody);
		}
		afterFilter(req, resp);
		long endTimeMillis = System.currentTimeMillis();
		logInfoMap.put("status", resp.getStatus());
		logInfoMap.put("elapsedTime", (endTimeMillis - startTimeMillis) + "ms");
		log.info("{}", JsonUtil.toJSONString(logInfoMap));
	}


	@SuppressWarnings("unchecked")
	protected R wrap(HttpServletRequest request) {
		return (R) request;
	}

	@SuppressWarnings("unchecked")
	protected T wrap(HttpServletResponse response) {
		return (T) response;
	}

	/**
	 * 浏览器不支持put、delete等method，需要将/service?_method=delete转换为标准的http delete方法
	 **/
	private HttpServletRequest adjustMethod(HttpServletRequest request) {
		if (!"post".equalsIgnoreCase(request.getMethod())) {
			return request;
		}

		String paramValue = request.getParameter(methodParam);
		if (paramValue == null || "".equals(paramValue.trim())) {
			return request;
		}

		final String method = paramValue.trim().toUpperCase(Locale.ENGLISH);
		return new HttpServletRequestWrapper(request) {
			@Override
			public String getMethod() {
				return method;
			}
		};
	}

	/**
	 * 打印请求参数
	 */
	protected Map<String, Object> logRequest(R request) {
		String url = URLDecoder.decode(request.getRequestURL().toString(), StandardCharsets.UTF_8);
        String contentType = request.getContentType();
		Map<String, String[]> params = request.getParameterMap();

		LinkedHashMap<String, Object> baseLogMap = new LinkedHashMap<>();
		baseLogMap.put("ip", ActionUtil.getRequestIP());
		baseLogMap.put("method", request.getMethod().toLowerCase());
		baseLogMap.put("url", url);
		if (contentType != null) {
			baseLogMap.put("contentType", contentType);
		}
		if (params != null && !params.isEmpty()) {
			baseLogMap.put("params", params);
		}

		Map<String, Object> preLogMap = preLog(request);
		Map<String, Object> postLogMap = postLog(request);
		Map<String, Object> allLogMap = new LinkedHashMap<>();
		if (preLogMap != null) {
			allLogMap.putAll(preLogMap);
		}
		allLogMap.putAll(baseLogMap);
		if (postLogMap != null) {
			allLogMap.putAll(postLogMap);
		}

		return allLogMap;
	}

	protected Map<String, Object> preLog(HttpServletRequest request) {
		return null;
	}

	protected Map<String, Object> postLog(HttpServletRequest request) {
		return null;
	}

	/**
	 * 设置字符集
	 **/
	protected void setCharacterEncoding(R request, T response) {
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
	protected void setContextPath(R request) {
		ActionUtil.addContextPath(request);
		ActionUtil.addStaticPath(request, staticPath);
	}

	/**
	 * 跨域请求设置
	 */
	protected void setAccessControl(R request, T response) {
		if (!response.containsHeader("Access-Control-Allow-Origin")) {
			String requestOrigin = request.getHeader("origin");
			if (requestOrigin == null || "".equals(requestOrigin.trim()) || "null".equals(requestOrigin.trim())
					|| (originWhiteList != null && originWhiteList.size() > 0 && !originWhiteList.contains(requestOrigin.trim()))) {
				response.setHeader("Access-Control-Allow-Origin", "*");
			} else {
				response.setHeader("Access-Control-Allow-Origin", requestOrigin);
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.setHeader("Access-Control-Max-Age", "3600");
		}

		if (!response.containsHeader("Access-Control-Allow-Methods")) {
			List<HttpMethod> allowedMethods = Arrays.asList(HttpMethod.values());
			response.setHeader("Access-Control-Allow-Methods", StringUtils.collectionToCommaDelimitedString(allowedMethods));
		}
		if (!response.containsHeader("Access-Control-Allow-Headers")) {
			response.setHeader("Access-Control-Allow-Headers", getAllowedHeaders(request));
		}
	}
	private String getAllowedHeaders(HttpServletRequest request) {
		Enumeration<String> headerNames = request.getHeaderNames();
		if (headerNames == null || !headerNames.hasMoreElements()) {
			return "*";
		}

		StringBuilder builder = new StringBuilder("");
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			if (ACCESS_CONTROL_REQUEST_HEADERS.equalsIgnoreCase(headerName)) {
				String headerVal = request.getHeader(headerName);
				if (headerVal != null && !"".equals(headerVal.trim())) {
					builder.append(headerVal).append(",");
				}
			} else {
				builder.append(headerName).append(",");
			}
		}
		return builder.deleteCharAt(builder.length() - 1).toString();
	}


	protected void beforeFilter(R request, T response) {

	}

	protected Map<String, Object> getRequestHeader(R request) {
		if (this.logAllHeaders) {
			Enumeration<String> headerNames = request.getHeaderNames();
			if (headerNames == null || !headerNames.hasMoreElements()) {
				return null;
			}
			Map<String, Object> map = new HashMap<>();
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement();
				String headerVal = request.getHeader(headerName);
				map.put(headerName, headerVal);
			}
			return map;
		}

		if (this.logHeaders.isEmpty()) {
			return null;
		}
		Map<String, Object> map = new HashMap<>();
		for (String h: this.logHeaders) {
			map.put(h, request.getHeader(h));
		}
		return map;
	}

	protected Object getResponseBody(T response) {
		return null;
	}

	protected void afterFilter(R request, T response) {

	}

	@Override
	public void initFilterBean() throws ServletException {
		super.initFilterBean();
		FilterConfig config = this.getFilterConfig();
		InitParameter initParameter = InitParameter.getInstance();

		assert config != null;
		String exclusive = config.getInitParameter("exclusive");
		if (exclusive != null && !"".equals(exclusive.trim())) {
			initParameter.setExclusivePattern(exclusive.split(","));
		}
	}
}
