package com.assist4j.core.reactive;


import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;


/**
 * @author wei
 */
public abstract class ActionUtil {
	/**
	 * 获得客户端IP
	 * @return
	 */
	public static String getRequestIP(ServerHttpRequest request) {
		HttpHeaders headers = request.getHeaders();

		String ip = headers.getFirst("X-Real-IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = headers.getFirst("x-forwarded-for");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = headers.getFirst("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = headers.getFirst("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddress().toString();
		}

		if (ip == null) {
			return null;
		}
		return ip.split(",")[0];
	}
}
