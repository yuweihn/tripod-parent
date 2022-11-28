package com.yuweix.tripod.sequence.base;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author yuwei
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SeqField {
    boolean isDao() default false;
    boolean isName() default false;
    boolean isMinValue() default false;
}
