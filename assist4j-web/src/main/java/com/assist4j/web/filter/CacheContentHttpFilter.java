package com.assist4j.web.filter;


import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author yuwei
 */
public class CacheContentHttpFilter extends HttpFilter<ContentCachingRequestWrapper, ContentCachingResponseWrapper> {
    @Override
    protected ContentCachingRequestWrapper wrap(HttpServletRequest request) {
        return new ContentCachingRequestWrapper(request);
    }

    @Override
    protected ContentCachingResponseWrapper wrap(HttpServletResponse response) {
        return new ContentCachingResponseWrapper(response);
    }

    @Override
    protected void printRequest(ContentCachingRequestWrapper request) {
        super.printRequest(request);
    }
}
