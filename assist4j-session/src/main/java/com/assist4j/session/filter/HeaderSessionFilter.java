package com.assist4j.session.filter;


import com.assist4j.session.cache.SessionCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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
        if (key == null || "".equals(key)) {
            throw new IllegalArgumentException("Parameter[key] is not initialized.");
        }

        String sessionId = request.getHeader(key);
        if (sessionId == null || "".equals(sessionId.trim())) {
            throw new IllegalArgumentException("Required header part '" + key + "' is not present.");
        }
        return sessionId;
    }
}
