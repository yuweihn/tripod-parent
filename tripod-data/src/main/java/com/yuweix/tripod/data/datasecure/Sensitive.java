package com.yuweix.tripod.data.datasecure;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * 基于正则表达式的脱敏注解
 * eg.
 * 密码用星号代替         [\s\S]*,                  ******
 * 11位手机号            ^(\\d{3})\\d{4}(\\d{4})$, $1****$2
 * 16或者18身份证号       ^(\\d{4})\\d{8,10}(\\d{4})$, $1****$2
 * 银行卡号              ^(\\d{4})\\d*(\\d{4}), $1****$2
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Sensitive {
    String regex();
    String replacement();
    String desc() default "";
}
