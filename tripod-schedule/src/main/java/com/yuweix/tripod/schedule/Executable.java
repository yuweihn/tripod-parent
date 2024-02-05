package com.yuweix.tripod.schedule;


import org.springframework.core.annotation.AliasFor;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * @author yuwei
 */
@Target({METHOD})
@Retention(RUNTIME)
@Scheduled
public @interface Executable {
    @AliasFor(annotation = Scheduled.class, value = "cron")
    String cron() default "";
}
