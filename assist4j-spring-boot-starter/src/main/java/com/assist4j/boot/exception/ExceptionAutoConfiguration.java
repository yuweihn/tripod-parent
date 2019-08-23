package com.assist4j.boot.exception;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.assist4j.core.Response;
import com.assist4j.core.exception.ExceptionHandler;
import com.assist4j.core.exception.ExceptionViewResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "assist4j.boot.exception.enabled")
public class ExceptionAutoConfiguration {
    @Configuration
    @ConditionalOnProperty(name = "assist4j.boot.exception.defaultError.enabled", matchIfMissing = true)
    protected static class ErrorControllerConfiguration {
        @Value("${assist4j.boot.exception.errorCode:}")
        private String errorCode;

        @Controller
        public class ErrorController {
            @RequestMapping(value = {"/error", "/error/**"})
            @ResponseBody
            public Response<Void> toErrorPage(HttpServletResponse response) {
                int status = response.getStatus();
                HttpStatus httpStatus = HttpStatus.valueOf(status);

                return new Response<Void>(errorCode == null || "".equals(errorCode) ? "" + status : errorCode
                        , httpStatus == null ? "Unknown" : httpStatus.getReasonPhrase() + "[" + status + "]");
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean(ExceptionViewResolver.class)
    public ExceptionViewResolver viewResolver(@Value("${assist4j.boot.exception.errorCode:500}")final String errorCode) {
        return new ExceptionViewResolver() {
            @Override
            public ModelAndView createView(String content) {
                FastJsonJsonView view = new FastJsonJsonView();
                String text = JSONObject.toJSONString(new Response<Void>(errorCode, content));
                Map<String, Object> attributes = JSONObject.parseObject(text, Map.class);
                view.setAttributesMap(attributes);
                return new ModelAndView(view);
            }
        };
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

    @Bean
    @ConditionalOnMissingBean(ExceptionHandler.class)
    public ExceptionHandler exceptionHandler(ClassMessagePair classMessagePair, ExceptionViewResolver viewResolver
            , @Value("${assist4j.boot.exception.showExceptionName:true}")boolean showExceptionName) {
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
        exceptionHandler.setShowExceptionName(showExceptionName);
        return exceptionHandler;
    }
}
