package com.yuweix.tripod.dao.sharding;


import com.yuweix.tripod.dao.datasource.DataSource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


/**
 * @author yuwei
 */
public abstract class DataSourceAspect {
    private static final Logger log = LoggerFactory.getLogger(DataSourceAspect.class);

    /**
     * 需要分库的切入点。
     * {@link Shardable}的子类，且当前切入点、当前切入点所在类或目标类，三者任一处有{@link DataSource}注解。
     */
    @Pointcut("execution(public * com.yuweix.tripod.dao.sharding.Shardable+.*(..))")
    public void shard() {

    }

    /**
     * 不需要分库，只是单纯的切换数据库的切入点。
     * 非{@link Shardable}的子类，且当前切入点或当前切入点所在类有{@link DataSource}注解。
     */
    @Pointcut("(!execution(* com.yuweix.tripod.dao.sharding.Shardable+.*(..))) && (@within(com.yuweix.tripod.dao.datasource.DataSource) || @annotation(com.yuweix.tripod.dao.datasource.DataSource))")
    public void change() {

    }

    @Around("shard()")
    public Object onShard(ProceedingJoinPoint point) throws Throwable {
        Object target = point.getTarget();
        if (!(target instanceof Shardable)) {
            return point.proceed();
        }
        Shardable shardable = (Shardable) target;
        DataSource annotation = getAnnotationFromPoint(point, DataSource.class);
        if (annotation == null) {
            return point.proceed();
        }
        String logicDatabaseName = annotation.value();

        String physicalDatabase = ShardAopUtil.determinePhysicalDatabase(point, logicDatabaseName);
        try {
            log.info("Physical Database Name: {}", physicalDatabase);
            setDataSource(physicalDatabase);
            shardable.onStart();
            Object proceed = point.proceed();
            shardable.onSuccess();
            return proceed;
        } catch (Exception e) {
            shardable.onFailure();
            throw e;
        } finally {
            try {
                shardable.onComplete();
            } catch (Exception ignored) {}
            removeDataSource();
        }
    }

    @Around("change()")
    public Object onChange(ProceedingJoinPoint point) throws Throwable {
        Object target = point.getTarget();
        if (target instanceof Shardable) {
            return point.proceed();
        }
        DataSource annotation = getAnnotationFromPoint(point, DataSource.class);
        if (annotation == null) {
            return point.proceed();
        }
        String databaseName = annotation.value();
        try {
            log.info("Database Name: {}", databaseName);
            setDataSource(databaseName);
            return point.proceed();
        } finally {
            removeDataSource();
        }
    }

    protected abstract void setDataSource(String dbName);
    protected abstract void removeDataSource();

    /**
     * 在当前方法、当前方法所在类或目标类，三者任一处获取到指定注解即可
     */
    private <T extends Annotation>T getAnnotationFromPoint(JoinPoint point, Class<T> clz) {
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
