package com.yuweix.tripod.web.filter;


import com.yuweix.tripod.core.json.JsonUtil;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author yuwei
 */
public class CacheContentHttpFilter extends AbstractFilter<ContentCachingRequestWrapper, ContentCachingResponseWrapper> {
	private Integer maxRequestSize = null;
	private Integer maxResponseSize = null;


	public void setMaxRequestSize(Integer maxRequestSize) {
		this.maxRequestSize = maxRequestSize;
	}
	public void setMaxResponseSize(Integer maxResponseSize) {
		this.maxResponseSize = maxResponseSize;
	}


	@Override
	protected ContentCachingRequestWrapper wrap(HttpServletRequest request) {
		return new ContentCachingRequestWrapper(request);
	}

	@Override
	protected ContentCachingResponseWrapper wrap(HttpServletResponse response) {
		return new ContentCachingResponseWrapper(response);
	}

	@Override
	protected Map<String, Object> logRequest(ContentCachingRequestWrapper request) {
		Map<String, Object> logInfoMap = super.logRequest(request);
		Object bodyInfo = getRequestBody(request);

		if (bodyInfo == null || "".equals(bodyInfo)) {
			return logInfoMap;
		}

		if (logInfoMap == null) {
			logInfoMap = new LinkedHashMap<>();
		}
		logInfoMap.put("requestBody", bodyInfo);
		return logInfoMap;
	}

	private Object getRequestBody(ContentCachingRequestWrapper request) {
		byte[] bytes = request.getContentAsByteArray();
		if (bytes.length <= 0) {
			return null;
		}

		String content = new String(bytes, Charset.forName(getEncoding()));
		if ("".equals(content)) {
			return null;
		}
		try {
			content = URLDecoder.decode(content, getEncoding());
		} catch (Exception ignored) {
		}
		return limit(content, maxRequestSize);
	}
	private Object limit(String str, Integer maxSize) {
		if (maxSize == null || maxSize < 0) {
			try {
				return JsonUtil.parse(str);
			} catch (Exception e) {
				return str;
			}
		}
		if (maxSize == 0) {
			return null;
		}
		if (str != null && str.length() > maxSize) {
			str = str.substring(0, maxSize) + "......";
		}
		try {
			return JsonUtil.parse(str);
		} catch (Exception e) {
			return str;
		}
	}

	@Override
	protected Object getResponseBody(ContentCachingResponseWrapper response) {
		String str = new String(response.getContentAsByteArray(), Charset.forName(getEncoding()));
		return limit(str, maxResponseSize);
	}

	@Override
	protected void afterFilter(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
		try {
			response.copyBodyToResponse();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
