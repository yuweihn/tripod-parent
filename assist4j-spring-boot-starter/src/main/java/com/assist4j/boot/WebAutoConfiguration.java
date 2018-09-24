package com.assist4j.boot;


import com.assist4j.core.exception.ExceptionHandler;
import com.assist4j.web.TextUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "assist4j.boot.web.enabled")
public class WebAutoConfiguration {

    @ConditionalOnMissingBean(name = "messageSource")
    @Bean
    public TextUtil textUtil(@Qualifier("messageSource") MessageSource messageSource) {
        try {
            Class<?> clz = Class.forName(TextUtil.class.getName());
            Constructor<?> constructor = clz.getDeclaredConstructor();
            constructor.setAccessible(true);
            TextUtil textUtil = (TextUtil) constructor.newInstance();
            textUtil.setMessageSource(messageSource);
            return textUtil;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ConditionalOnMissingBean(name = "localeResolver")
    @Bean(name = "localeResolver")
    public LocaleResolver localeResolver() {
        return new SessionLocaleResolver();
    }

    @ConditionalOnMissingBean(name = "messageSource")
    @Bean(name = "messageSource")
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("bundles.message");
        return messageSource;
    }

    @ConditionalOnProperty(name = "assist4j.boot.web.exceptionHandler.enabled", matchIfMissing = true)
    @ConditionalOnMissingBean(name = "exceptionHandler")
    @Bean(name = "exceptionHandler")
    public HandlerExceptionResolver handlerExceptionResolver() {
        Map<Class<?>, String> errorMsgMap = new HashMap<Class<?>, String>();
        errorMsgMap.put(MaxUploadSizeExceededException.class, "上传文件太大");

        ExceptionHandler exceptionHandler = new ExceptionHandler();
        exceptionHandler.setErrorMsgMap(errorMsgMap);
        return exceptionHandler;
    }
}
