package com.yuweix.assist4j.datasecure.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * eg.
 * MOBILE_PHONE("mobile_phone", "11位手机号", "^(\\d{3})\\d{4}(\\d{4})$", "$1****$2"),//$1第一个括号的内容,$2第二个括号里面的内容
 * ID_CARD("id_card", "16或者18身份证号", "^(\\d{4})\\d{8,10}(\\d{4})$", "$1****$2"),
 * BANK_CARD_NO("bankCardNo", "银行卡号", "^(\\d{4})\\d*(\\d{4})$", "$1****$2")
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Sensitive {
    String regex() default "";
    String replacement() default "";
    String desc() default "";
}
