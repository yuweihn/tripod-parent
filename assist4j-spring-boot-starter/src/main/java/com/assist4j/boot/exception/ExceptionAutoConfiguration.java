package com.assist4j.boot.exception;


import com.assist4j.core.exception.ExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.HashMap;
import java.util.Map;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "assist4j.boot.exception.enabled")
public class ExceptionAutoConfiguration {

    @ConditionalOnMissingBean(name = "exceptionHandler")
    @Bean(name = "exceptionHandler")
    public HandlerExceptionResolver handlerExceptionResolver(@Value("${error.page:}") String errorPage) {
        Map<Class<?>, String> errorMsgMap = new HashMap<Class<?>, String>();
        errorMsgMap.put(MaxUploadSizeExceededException.class, "上传文件太大");

        ExceptionHandler exceptionHandler = new ExceptionHandler();
        exceptionHandler.setErrorPage(errorPage);
        exceptionHandler.setErrorMsgMap(errorMsgMap);
        return exceptionHandler;
    }
}
