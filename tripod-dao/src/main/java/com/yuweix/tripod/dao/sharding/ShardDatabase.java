package com.yuweix.tripod.dao.sharding;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * @author yuwei
 */
@Target({PARAMETER})
@Retention(RUNTIME)
public @interface ShardDatabase {
    /**
     * 分库策略
     */
    Class<? extends DatabaseStrategy> strategy() default DatabaseModStrategy.class;
}
