package com.yuweix.tripod.session.filter;


import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.yuweix.tripod.session.CookiesUtil;
import com.yuweix.tripod.session.SessionConstant;
import com.yuweix.tripod.session.cache.SessionCache;
import com.yuweix.tripod.session.conf.SessionConf;


/**
 * @author yuwei
 */
public class CookieSessionFilter extends SessionFilter {
	private String cookieName;


	public CookieSessionFilter(SessionCache cache) {
		super(cache);
	}
	public CookieSessionFilter() {

	}

	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}


	@Override
	protected String getSessionId(HttpServletRequest request, HttpServletResponse response) {
		String ckName = cookieName;
		if (ckName == null || "".equals(ckName)) {
			ckName = SessionConf.getInstance().getApplicationName() + SessionConstant.COOKIE_SESSION_ID_SUFFIX;
		}

		String sid = CookiesUtil.findValueByKey(request, ckName);
		if (sid == null || "".equals(sid)) {
			sid = UUID.randomUUID().toString().replace("-", "");
		}
		CookiesUtil.addCookie(request, response, ckName, sid, null, null
				, SessionConstant.COOKIE_MAX_AGE_DEFAULT);
		return sid;
	}
}
