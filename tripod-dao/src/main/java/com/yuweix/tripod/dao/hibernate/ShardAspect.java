package com.yuweix.tripod.dao.hibernate;


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
    @Pointcut("@annotation(com.yuweix.tripod.dao.hibernate.HbShard)")
    public void pointcut() {

    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object target = joinPoint.getTarget();
        if (!(target instanceof AbstractDao)) {
            return joinPoint.proceed();
        }

        Object shardingVal = parseShardingVal(joinPoint);
        AbstractDao<?, ?> dao = (AbstractDao<?, ?>) target;
        try {
            dao.beforeSharding(shardingVal);
            return joinPoint.proceed();
        } finally {
            dao.afterSharding();
        }
    }

    private Object parseShardingVal(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        HbShard annoShard = method.getAnnotation(HbShard.class);

        int idx = -1;
        String shardParamName = annoShard.value();
        if (shardParamName == null || "".equals(shardParamName)) {
            idx = 0;
        } else {
            Parameter[] params = method.getParameters();
            if (params == null || params.length <= 0) {
                throw new RuntimeException("Sharding parameter is required.");
            }
            for (int i = 0; i < params.length; i++) {
                if (shardParamName.equals(params[i].getName())) {
                    idx = i;
                    break;
                }
            }
            if (idx < 0 || idx > params.length - 1) {
                throw new RuntimeException("Sharding parameter is required.");
            }
        }

        final Object[] args = joinPoint.getArgs();
        if (args == null || args.length <= 0) {
            throw new RuntimeException("Sharding parameter is required.");
        }
        return args[idx];
    }
}
