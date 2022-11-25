package com.yuweix.tripod.session.filter;


import com.yuweix.tripod.session.SessionConstant;
import com.yuweix.tripod.session.cache.SessionCache;
import com.yuweix.tripod.session.conf.SessionConf;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;


/**
 * 从header中取出某个值作为sessionId
 * @author yuwei
 */
public class HeaderSessionFilter extends SessionFilter {
	private String key;


	public HeaderSessionFilter(SessionCache cache) {
		super(cache);
	}
	public HeaderSessionFilter() {
		super();
	}

	public void setKey(String key) {
		this.key = key;
	}


	@Override
	protected String getSessionId(HttpServletRequest request, HttpServletResponse response) {
		String headerKey = key;
		if (headerKey == null || "".equals(headerKey)) {
			headerKey = SessionConf.getInstance().getApplicationName() + SessionConstant.COOKIE_SESSION_ID_SUFFIX;
		}

		String sid = request.getHeader(headerKey);
		if (sid == null || "".equals(sid.trim())) {
			sid = UUID.randomUUID().toString().replace("-", "");
		}
		response.setHeader(headerKey, sid);
		response.addHeader("Access-Control-Expose-Headers", headerKey);
		return sid;
	}
}
