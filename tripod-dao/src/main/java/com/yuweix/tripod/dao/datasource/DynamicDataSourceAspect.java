package com.yuweix.tripod.dao.datasource;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;


/**
 * @author yuwei
 */
@Aspect
public class DynamicDataSourceAspect {
    /**
     * 切点表达式
     */
    @Pointcut("@annotation(com.yuweix.tripod.dao.datasource.MyDataSource)")
    public void pointcut() {

    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        MyDataSource annotation = method.getAnnotation(MyDataSource.class);
        String dataSourceName = annotation.value();
        DataSourceContextHolder.setDataSource(dataSourceName);
    }

    @After("pointcut()")
    public void after(JoinPoint joinPoint) {
        DataSourceContextHolder.removeDataSource();
    }
}
