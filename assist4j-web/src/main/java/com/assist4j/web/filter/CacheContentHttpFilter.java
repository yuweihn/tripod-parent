package com.assist4j.web.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


/**
 * @author yuwei
 */
public class CacheContentHttpFilter extends AbstractFilter<ContentCachingRequestWrapper, ContentCachingResponseWrapper> {
    private static final Logger log = LoggerFactory.getLogger(CacheContentHttpFilter.class);

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
    protected void logRequest(ContentCachingRequestWrapper request) {
        super.logRequest(request);
        printBody(request);
    }

    private void printBody(ContentCachingRequestWrapper request) {
        byte[] bytes = request.getContentAsByteArray();
        if (bytes == null || bytes.length <= 0) {
            return;
        }

        String content = new String(bytes);
        if (content == null || "".equals(content)) {
            return;
        }
        try {
            content = URLDecoder.decode(content, "utf-8");
        } catch (UnsupportedEncodingException e) {
        }

        if (contentLimit > 0 && contentLimit < content.length()) {
            content = content.substring(0, contentLimit) + "......";
        }
        log.info("RequestBody: {}", content);
    }

    @Override
    protected void afterFilter(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) throws IOException {
        response.copyBodyToResponse();
    }
}
