package com.yuweix.tripod.web;


import com.yuweix.tripod.core.io.StreamUtil;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author yuwei
 */
public class XssHttpServletRequest extends HttpServletRequestWrapper {
	public XssHttpServletRequest(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String getParameter(String name) {
		String value = super.getParameter(name);
		if (value == null || "".equals(value)) {
			return value;
		}
		value = new HtmlFilter().filter(value);
		return value;
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] values = super.getParameterValues(name);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				String value = values[i];
				if (value != null && !"".equals(value)) {
					value = new HtmlFilter().filter(value);
				}
				values[i] = value;
			}
		}
		return values;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> parameters = super.getParameterMap();
		Map<String, String[]> map = new LinkedHashMap<>();
		if (parameters != null) {
			for (String key : parameters.keySet()) {
				String[] values = parameters.get(key);
				for (int i = 0; i < values.length; i++) {
					String value = values[i];
					if (value != null && !"".equals(value)) {
						value = new HtmlFilter().filter(value);
					}
					values[i] = value;
				}
				map.put(key, values);
			}
		}
		return map;
	}

	@Override
	public String getHeader(String name) {
		String value = super.getHeader(name);
		if (value != null && !"".equals(value)) {
			value = new HtmlFilter().filter(value);
		}
		return value;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		String header = super.getHeader(HttpHeaders.CONTENT_TYPE);
		if (!MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(header) && !MediaType.APPLICATION_FORM_URLENCODED_VALUE.equalsIgnoreCase(header)) {
			return super.getInputStream();
		}

		byte[] bytes = StreamUtil.read(super.getInputStream());
		if (bytes == null || bytes.length <= 0) {
			return super.getInputStream();
		}

		String content = new String(bytes);
		if ("".equals(content)) {
			return super.getInputStream();
		}

		// xss过滤
		content = new HtmlFilter().filter(content).trim();
		final ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
		return new ServletInputStream() {
			@Override
			public boolean isFinished() {
				return true;
			}
			@Override
			public boolean isReady() {
				return true;
			}
			@Override
			public void setReadListener(ReadListener readListener) {

			}
			@Override
			public int read() throws IOException {
				return bis.read();
			}
		};
	}
}
