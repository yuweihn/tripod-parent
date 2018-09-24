package com.assist4j.boot;


import com.assist4j.core.SpringContext;
import com.assist4j.core.exception.ExceptionHandler;
import com.assist4j.web.multipart.NotEmptyMultipartResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;


/**
 * @author yuwei
 */
@Configuration
@AutoConfigureOrder(Integer.MIN_VALUE)
@ConditionalOnProperty(name = "assist4j.boot.default.enabled", matchIfMissing = true)
public class DefaultAutoConfiguration {

    @ConditionalOnMissingBean(name = "springContext")
    @Bean(name = "springContext")
    public SpringContext springContext() {
        try {
            Class<?> clz = Class.forName(SpringContext.class.getName());
            Constructor<?> constructor = clz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return (SpringContext) constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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

    @ConditionalOnMissingBean(name = "multipartResolver")
    @Bean(name = "multipartResolver")
    public MultipartResolver multipartResolver(@Value("${upload.max.allowed.size:-1}") long maxUploadSize) {
        NotEmptyMultipartResolver multipartResolver = new NotEmptyMultipartResolver();
        multipartResolver.setMaxUploadSize(maxUploadSize);
        multipartResolver.setDefaultEncoding("utf-8");
        multipartResolver.setResolveLazily(true);
        return multipartResolver;
    }
}
