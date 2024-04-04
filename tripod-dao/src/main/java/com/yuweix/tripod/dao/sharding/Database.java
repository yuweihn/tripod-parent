package com.yuweix.tripod.dao.sharding;


import java.lang.annotation.*;


/**
 * 分库时使用，注解在方法参数上
 * @author yuwei
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Database {

}
