package com.yuweix.assist4j.datasecure.annotations;


import com.yuweix.assist4j.datasecure.enums.SensitiveType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface SensitiveInfo {
    SensitiveType value();
    /**
     * 附加值, 自定义正则表达式等
     */
    String[] attach() default {};
}
