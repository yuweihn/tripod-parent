package com.yuweix.tripod.dao.sharding;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;


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
        if (!(target instanceof Shardable)) {
            return point.proceed();
        }

        Object shardingVal = ShardAopUtil.getAnnotationArgVal(point, Shard.class, Sharding.class);
        Shardable shardable = (Shardable) target;
        try {
            shardable.beforeSharding(shardingVal);
            return point.proceed();
        } finally {
            shardable.afterSharding();
        }
    }
}
