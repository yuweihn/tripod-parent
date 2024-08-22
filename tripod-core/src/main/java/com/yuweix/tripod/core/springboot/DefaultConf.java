package com.yuweix.tripod.core.springboot;


import com.yuweix.tripod.core.SpringContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Constructor;


/**
 * @author yuwei
 */
public class DefaultConf {
    @ConditionalOnMissingBean(SpringContext.class)
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
}
