package com.assist4j.boot.exception;


import com.assist4j.core.exception.ExceptionHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "assist4j.boot.exception.enabled")
public class ExceptionAutoConfiguration {

    @ConditionalOnMissingBean(name = "exceptionHandler")
    @Bean(name = "exceptionHandler")
    public HandlerExceptionResolver handlerExceptionResolver(@Value("${assist4j.boot.exception.error-page:}") String errorPage
            , @Qualifier("exceptionClassAndMessage") ExceptionClassAndMessage exceptionClassAndMessage) {
        Map<Class<?>, String> errorMsgMap = new HashMap<Class<?>, String>();

        Map<String, String> classMessageMap = null;
        if (exceptionClassAndMessage != null && (classMessageMap = exceptionClassAndMessage.getClassMessageMap()) != null) {
            Set<Map.Entry<String, String>> entrySet = classMessageMap.entrySet();
            for (Map.Entry<String, String> entry: entrySet) {
                try {
                    errorMsgMap.put(Class.forName(entry.getKey()), entry.getValue());
                } catch (ClassNotFoundException e) {
                }
            }
        }


        ExceptionHandler exceptionHandler = new ExceptionHandler();
        exceptionHandler.setErrorPage(errorPage);
        exceptionHandler.setErrorMsgMap(errorMsgMap);
        return exceptionHandler;
    }

    @ConditionalOnMissingBean(name = "exceptionClassAndMessage")
    @Bean(name = "exceptionClassAndMessage")
    @ConfigurationProperties(prefix = "assist4j.boot.exception", ignoreUnknownFields = true)
    public ExceptionClassAndMessage exceptionClassAndMessage() {
        return new ExceptionClassAndMessage() {
            private Map<String, String> classMessageMap = new HashMap<String, String>();

            @Override
            public Map<String, String> getClassMessageMap() {
                return classMessageMap;
            }
        };
    }

    private interface ExceptionClassAndMessage {
        Map<String, String> getClassMessageMap();
    }
}
