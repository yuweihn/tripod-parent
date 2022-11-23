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
public @interface HbShard {
    /**
     * 分片参数名称，默认第一个参数
     * @return
     */
    String value() default "";
}
