package com.assist4j.boot.exception;


import com.assist4j.core.exception.ExceptionHandler;
import com.assist4j.core.exception.ExceptionViewResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "assist4j.boot.exception.enabled")
public class ExceptionAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ExceptionHandler.class)
    public ExceptionHandler exceptionHandler(ClassMessagePair classMessagePair, ExceptionViewResolver viewResolver) {
        Map<Class<?>, String> errorMsgMap = new HashMap<Class<?>, String>();

        Map<String, String> classMessageMap = classMessagePair.getClassMessageMap();
        if (classMessageMap != null) {
            Set<Map.Entry<String, String>> entrySet = classMessageMap.entrySet();
            for (Map.Entry<String, String> entry: entrySet) {
                try {
                    errorMsgMap.put(Class.forName(entry.getKey()), entry.getValue());
                } catch (ClassNotFoundException e) {
                }
            }
        }

        ExceptionHandler exceptionHandler = new ExceptionHandler();
        exceptionHandler.setViewResolver(viewResolver);
        exceptionHandler.setErrorMsgMap(errorMsgMap);
        return exceptionHandler;
    }

    @Bean
    @ConditionalOnMissingBean(ClassMessagePair.class)
    @ConfigurationProperties(prefix = "assist4j.boot.exception", ignoreUnknownFields = true)
    public ClassMessagePair classMessagePair() {
        return new ClassMessagePair() {
            private Map<String, String> classMessageMap = new HashMap<String, String>();

            @Override
            public Map<String, String> getClassMessageMap() {
                return classMessageMap;
            }
        };
    }
}
