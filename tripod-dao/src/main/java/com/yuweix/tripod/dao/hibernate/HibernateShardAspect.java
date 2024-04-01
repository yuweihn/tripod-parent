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
public class HibernateShardAspect {
    @Pointcut("@annotation(com.yuweix.tripod.dao.hibernate.HibernateShard)")
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
        HibernateShard hShard = method.getAnnotation(HibernateShard.class);

        int idx = -1;
        String shardParamName = hShard.value();
        if (shardParamName == null || "".equals(shardParamName)) {
            idx = 0;
        } else {
            Parameter[] params = method.getParameters();
            if (params == null || params.length <= 0) {
                throw new RuntimeException("Sharding parameter is required.");
            }
            for (int i = 0, len = params.length; i < len; i++) {
                if (shardParamName.equals(params[i].getName())) {
                    idx = i;
                    break;
                }
            }
            if (idx < 0 || idx > params.length - 1) {
                throw new RuntimeException("Sharding parameter is required.");
            }
        }

        final Object[] args = point.getArgs();
        if (args == null || args.length <= 0) {
            throw new RuntimeException("Sharding parameter is required.");
        }
        return args[idx];
    }
}
