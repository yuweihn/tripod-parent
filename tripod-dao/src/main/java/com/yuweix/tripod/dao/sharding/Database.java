package com.yuweix.tripod.dao.sharding;


import java.lang.annotation.*;


/**
 * 分库时使用，注解在方法参数上，参数作为分库字段，如果该参数为对象，该对象中必须包含一个属性，该属性有{@link Sharding}注解
 * @author yuwei
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Database {

}
