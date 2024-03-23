package com.yuweix.tripod.dao.datasource;


import java.lang.annotation.*;


/**
 * @author yuwei
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DataSource {
    String value();
}
