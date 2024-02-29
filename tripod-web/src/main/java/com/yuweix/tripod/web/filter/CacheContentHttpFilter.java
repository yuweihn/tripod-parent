package com.yuweix.tripod.web.filter;


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
	private int contentLimit = 100;


	public void setContentLimit(int contentLimit) {
		this.contentLimit = contentLimit;
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
		String bodyInfo = getBodyInfo(request);
		
		if (bodyInfo == null || "".equals(bodyInfo)) {
			return logInfoMap;
		}
		
		if (logInfoMap == null) {
			logInfoMap = new LinkedHashMap<>();
		}
		logInfoMap.put("requestBody", bodyInfo);
		return logInfoMap;
	}

	private String getBodyInfo(ContentCachingRequestWrapper request) {
		byte[] bytes = request.getContentAsByteArray();
		if (bytes.length <= 0) {
			return null;
		}

		String content = new String(bytes);
		if ("".equals(content)) {
			return null;
		}
		try {
			content = URLDecoder.decode(content, "utf-8");
		} catch (Exception ignored) {
		}

		if (contentLimit > 0 && content != null && contentLimit < content.length()) {
			content = content.substring(0, contentLimit) + "......";
		}
		return content;
	}

	@Override
	protected String getResponseBody(ContentCachingResponseWrapper response) {
		return new String(response.getContentAsByteArray(), Charset.defaultCharset());
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
