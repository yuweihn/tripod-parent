package com.yuweix.tripod.core.office.excel;


import java.lang.annotation.*;


/**
 * @author yuwei
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelKey {
	String title() default "";
	int order() default Integer.MAX_VALUE;
}
