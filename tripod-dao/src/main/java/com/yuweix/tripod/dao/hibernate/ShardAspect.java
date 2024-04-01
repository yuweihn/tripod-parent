package com.yuweix.tripod.dao.hibernate;


import com.yuweix.tripod.dao.sharding.Sharding;
import org.aspectj.lang.ProceedingJoinPoint;
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
    @Pointcut("execution(public * com.yuweix.tripod.dao.sharding.Shardable+.*(..))")
    public void pointcut() {

    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object target = point.getTarget();
        if (!(target instanceof AbstractDao)) {
            return point.proceed();
        }

        Object shardingVal = parseShardingVal(point);
        AbstractDao<?, ?> dao = (AbstractDao<?, ?>) target;
        try {
            dao.beforeSharding(shardingVal);
            return point.proceed();
        } finally {
            dao.afterSharding();
        }
    }

    private Object parseShardingVal(ProceedingJoinPoint point) {
        Method method = ((MethodSignature) point.getSignature()).getMethod();

        Parameter[] params = method.getParameters();
        if (params == null || params.length <= 0) {
            throw new RuntimeException("Sharding parameter is required.");
        }
        int idx = -1;
        for (int i = 0, len = params.length; i < len; i++) {
            if (params[i].isAnnotationPresent(Sharding.class)) {
                idx = i;
                break;
            }
        }
        if (idx < 0 || idx > params.length - 1) {
            throw new RuntimeException("Sharding parameter is required.");
        }

        final Object[] args = point.getArgs();
        if (args == null || args.length <= 0) {
            throw new RuntimeException("Sharding parameter is required.");
        }
        return args[idx];
    }
}
