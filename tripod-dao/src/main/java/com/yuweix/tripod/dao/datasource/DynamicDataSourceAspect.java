package com.yuweix.tripod.dao.datasource;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        DataSource annotation = getAnnotation(point, DataSource.class);
        if (annotation == null) {
            return point.proceed();
        }
        try {
            String dataSourceName = annotation.value();
            DataSourceContextHolder.setDataSource(dataSourceName);
            return point.proceed();
        } finally {
            DataSourceContextHolder.removeDataSource();
        }
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
}
