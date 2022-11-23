package com.yuweix.tripod.dao.hibernate;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * @author yuwei
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface Shard {
    /**
     * 第几个参数为分片字段的值
     * @return
     */
    int value() default 0;
}
