package com.yuweix.tripod.sharding.annotation;


import com.yuweix.tripod.sharding.strategy.DefaultStrategy;
import com.yuweix.tripod.sharding.strategy.Strategy;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * @author yuwei
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface Sharding {
    Class<? extends Strategy> strategy() default DefaultStrategy.class;
}
