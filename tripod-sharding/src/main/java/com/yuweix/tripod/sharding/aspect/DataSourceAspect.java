package com.yuweix.tripod.sharding.aspect;


import com.yuweix.tripod.sharding.Shardable;
import com.yuweix.tripod.sharding.annotation.ShardingDataSource;
import com.yuweix.tripod.sharding.utils.ShardAopUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


/**
 * @author yuwei
 */
public abstract class DataSourceAspect {
    private static final Logger log = LoggerFactory.getLogger(DataSourceAspect.class);

    /**
     * 需要分库的切入点。
     * {@link Shardable}的子类，且当前切入点、当前切入点所在类或目标类，三者任一处有{@link ShardingDataSource}注解。
     */
    @Pointcut("execution(public * com.yuweix.tripod.sharding.Shardable+.*(..))")
    public void shard() {

    }

    @Around("shard()")
    public Object onShard(ProceedingJoinPoint point) throws Throwable {
        Object target = point.getTarget();
        if (!(target instanceof Shardable)) {
            return point.proceed();
        }
        Shardable shardable = (Shardable) target;
        ShardingDataSource annotation = getMergedAnnotation(point, ShardingDataSource.class);
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

    protected abstract void setDataSource(String dsName);
    protected abstract void removeDataSource();

    /**
     * 在当前方法、当前方法所在类或目标类，三者任一处获取到指定的组合注解即可
     */
    protected <T extends Annotation>T getMergedAnnotation(JoinPoint point, Class<T> clz) {
        Class<?> targetClz = point.getTarget().getClass();
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        if (method == null) {
            return null;
        }
        T ann = AnnotatedElementUtils.getMergedAnnotation(method, clz);
        if (ann != null) {
            return ann;
        }
        ann = AnnotatedElementUtils.getMergedAnnotation(method.getDeclaringClass(), clz);
        if (ann != null) {
            return ann;
        }
        ann = AnnotatedElementUtils.getMergedAnnotation(targetClz, clz);
        return ann;
    }
    /**
     * 在当前方法、当前方法所在类或目标类，三者任一处获取到指定注解即可
     */
    protected <T extends Annotation>T getAnnotation(JoinPoint point, Class<T> clz) {
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
