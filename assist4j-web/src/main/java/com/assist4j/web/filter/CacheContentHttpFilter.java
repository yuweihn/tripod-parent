package com.assist4j.web.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author yuwei
 */
public class CacheContentHttpFilter extends AbstractFilter<ContentCachingRequestWrapper, ContentCachingResponseWrapper> {
    private static final Logger log = LoggerFactory.getLogger(CacheContentHttpFilter.class);

    private boolean logBody = false;


    public void setLogBody(boolean logBody) {
        this.logBody = logBody;
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
    protected void printRequest(ContentCachingRequestWrapper request) {
        super.printRequest(request);

        if (logBody) {
            byte[] bytes = request.getContentAsByteArray();
            if (bytes != null && bytes.length > 0) {
                String content = new String(bytes);
                log.info("body: {}", content);
            }
        }
    }
}
