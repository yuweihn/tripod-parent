package com.yuweix.tripod.dao.datasource;


import com.yuweix.tripod.dao.sharding.DatabaseHelper;
import com.yuweix.tripod.dao.sharding.Shardable;
import com.yuweix.tripod.dao.sharding.Strategy;
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
    @Pointcut("execution(public * com.yuweix.tripod.dao.sharding.Shardable+.*(..))")
    public void pointcut() {

    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object target = point.getTarget();
        if (!(target instanceof Shardable)) {
            return point.proceed();
        }
        Strategy strategy = ((Shardable) target).getShardingStrategy();
        if (strategy == null) {
            return null;
        }

        DataSource annotation = getAnnotation(point, DataSource.class);
        if (annotation == null) {
            return point.proceed();
        }
        String logicDatabaseName = annotation.value();

        Object argObj = DatabaseHelper.getDatabaseArg(point);
        if (argObj == null) {
            return point.proceed();
        }

        try {
            String physicalDatabase = determinePhysicalDatabase(logicDatabaseName
                    , DatabaseHelper.parse(argObj), strategy);
            DataSourceContextHolder.setDataSource(physicalDatabase);
            return point.proceed();
        } finally {
            DataSourceContextHolder.removeDataSource();
        }
    }

    private String determinePhysicalDatabase(String logicDatabaseName, Object shardingVal, Strategy strategy) {
        if (shardingVal == null || strategy == null) {
            return logicDatabaseName;
        }
        return logicDatabaseName + "_" + strategy.getShardingDatabaseIndex(logicDatabaseName, shardingVal);
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
