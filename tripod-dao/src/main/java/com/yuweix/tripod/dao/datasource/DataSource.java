package com.yuweix.tripod.dao.datasource;


import java.lang.annotation.*;


/**
 * @author yuwei
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DataSource {
    String value() default "";
}
