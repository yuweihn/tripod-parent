package com.yuweix.tripod.dao.datasource;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


/**
 * @author yuwei
 */
@Aspect
public class DynamicDataSourceAspect {
    /**
     * 切点表达式
     */
    @Pointcut("@within(com.yuweix.tripod.dao.datasource.DataSource) || @annotation(com.yuweix.tripod.dao.datasource.DataSource)")
    public void pointcut() {

    }

    @Before("pointcut()")
    public void before(JoinPoint point) {
        DataSource annotation = getAnnotation(point, DataSource.class);
        if (annotation == null) {
            return;
        }
        String dataSourceName = annotation.value();
        DataSourceContextHolder.setDataSource(dataSourceName);
    }

    private <T extends Annotation>T getAnnotation(JoinPoint point, Class<T> clz) {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        if (method == null) {
            return null;
        }
        T t = null;
        if (method.isAnnotationPresent(clz)) {
            t = method.getAnnotation(clz);
        }
        if (t == null && method.getDeclaringClass().isAnnotationPresent(clz)) {
            t = method.getDeclaringClass().getAnnotation(clz);
        }
        return t;
    }

    @After("pointcut()")
    public void after(JoinPoint point) {
        DataSourceContextHolder.removeDataSource();
    }
}
