package com.yuweix.tripod.web;


import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author yuwei
 */
public class XssHttpServletRequest extends HttpServletRequestWrapper {
	private XssEncoder xssEncoder;

	public XssHttpServletRequest(HttpServletRequest request) {
		this(request, new DefaultXssEncoder());
	}
	public XssHttpServletRequest(HttpServletRequest request, XssEncoder xssEncoder) {
		super(request);
		this.xssEncoder = xssEncoder;
	}

	@Override
	public String getParameter(String name) {
		String value = super.getParameter(name);
		if (value == null || "".equals(value)) {
			return value;
		}
		value = xssEncoder.filter(value);
		return value;
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] values = super.getParameterValues(name);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				String value = values[i];
				if (value != null && !"".equals(value)) {
					value = xssEncoder.filter(value);
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
						value = xssEncoder.filter(value);
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
			value = xssEncoder.filter(value);
		}
		return value;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		String header = super.getHeader(HttpHeaders.CONTENT_TYPE);
		if (!MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(header) && !MediaType.APPLICATION_FORM_URLENCODED_VALUE.equalsIgnoreCase(header)) {
			return super.getInputStream();
		}

		byte[] bytes = read(super.getInputStream());
		if (bytes.length <= 0) {
			return super.getInputStream();
		}

		String content = new String(bytes);
		if ("".equals(content)) {
			return super.getInputStream();
		}

		// xss过滤
		content = xssEncoder.filter(content).trim();
		final ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
		return new ServletInputStream() {
			@Override
			public boolean isFinished() {
				return bis.available() == 0;
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

	private static byte[] read(InputStream in) throws IOException {
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len;
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			return out.toByteArray();
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
