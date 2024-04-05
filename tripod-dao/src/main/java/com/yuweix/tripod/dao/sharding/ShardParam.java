package com.yuweix.tripod.dao.sharding;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * 注解在方法参数上，如果该参数包含一个{@link Sharding}注解的属性，则以该属性值进行分库分表，否则以该参数的值进行分库分表。
 * @author yuwei
 */
@Target({PARAMETER})
@Retention(RUNTIME)
public @interface ShardParam {

}
