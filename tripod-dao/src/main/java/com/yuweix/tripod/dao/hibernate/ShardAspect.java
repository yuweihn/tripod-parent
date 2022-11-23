package com.yuweix.tripod.dao.hibernate;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


/**
 * @author yuwei
 */
@Aspect
public class ShardAspect {
    @Pointcut("@annotation(com.yuweix.tripod.dao.hibernate.Shard)")
    public void pointcut() {

    }

    @Around("pointcut()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        Shard annoShard = method.getAnnotation(Shard.class);

        Object target = joinPoint.getTarget();
        if (!(target instanceof AbstractDao)) {
            joinPoint.proceed();
            return;
        }

        int idx = annoShard.value();
        final Object[] args = joinPoint.getArgs();
        if (args == null || args.length <= 0) {
            throw new RuntimeException("Sharding parameter is required.");
        }
        Object shardingVal = args[idx];

        AbstractDao<?, ?> dao = (AbstractDao<?, ?>) target;
        try {
            dao.beforeSharding(shardingVal);
            joinPoint.proceed();
        } finally {
            dao.afterSharding();
        }
    }
}
