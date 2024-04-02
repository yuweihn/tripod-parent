package com.yuweix.tripod.dao.datasource;


import com.yuweix.tripod.dao.sharding.DatabaseStrategy;
import com.yuweix.tripod.dao.sharding.ShardDatabase;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


/**
 * @author yuwei
 */
@Aspect
public class DynamicDataSourceAspect {
    /**
     * 切点表达式
     */
    @Pointcut("execution(public * com.yuweix.tripod.dao.sharding.Shardable+.*(..))")
    public void pointcut() {

    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        DataSource annotation = getAnnotation(point, DataSource.class);
        if (annotation == null) {
            return point.proceed();
        }
        String logicDatabaseName = annotation.value();

        int shardingValIndex = getShardDatabaseAnnIndex(point);
        if (shardingValIndex < 0) {
            return point.proceed();
        }

        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Parameter[] params = method.getParameters();
        ShardDatabase shardDatabase = params[shardingValIndex].getAnnotation(ShardDatabase.class);

        try {
            String physicalDatabase = determinePhysicalDatabase(logicDatabaseName, point.getArgs()[shardingValIndex], shardDatabase.strategy());
            DataSourceContextHolder.setDataSource(physicalDatabase);
            return point.proceed();
        } finally {
            DataSourceContextHolder.removeDataSource();
        }
    }

    private String determinePhysicalDatabase(String logicDatabaseName, Object shardingVal, Class<? extends DatabaseStrategy> strategyClz) {
        return logicDatabaseName;
    }

    /**
     * 在当前方法的参数中，获取到分库注解
     * @return 返回注解在参数列表中的index
     */
    private int getShardDatabaseAnnIndex(ProceedingJoinPoint point) {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Parameter[] params = method.getParameters();
        if (params == null || params.length <= 0) {
            return -1;
        }
        for (int i = 0, len = params.length; i < len; i++) {
            if (params[i].isAnnotationPresent(ShardDatabase.class)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 在当前方法、当前方法所在类或目标类，三者任一处获取到指定注解即可
     */
    private <T extends Annotation>T getAnnotation(JoinPoint point, Class<T> clz) {
        Class<?> targetClz = point.getTarget().getClass();
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        if (method == null) {
            return null;
        }
        if (method.isAnnotationPresent(clz)) {
            return method.getAnnotation(clz);
        }
        if (method.getDeclaringClass().isAnnotationPresent(clz)) {
            return method.getDeclaringClass().getAnnotation(clz);
        }
        if (targetClz.isAnnotationPresent(clz)) {
            return targetClz.getAnnotation(clz);
        }
        return null;
    }
}
