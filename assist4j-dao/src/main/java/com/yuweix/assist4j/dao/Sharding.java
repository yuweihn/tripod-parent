package com.yuweix.assist4j.dao;


import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * @author yuwei
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Sharding {}
