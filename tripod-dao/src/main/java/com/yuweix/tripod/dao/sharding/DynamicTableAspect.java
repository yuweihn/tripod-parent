package com.yuweix.tripod.dao.sharding;


import com.yuweix.tripod.dao.PersistUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;


/**
 * @author yuwei
 */
@Aspect
public class DynamicTableAspect {
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
        if (shardingVal == null) {
            return point.proceed();
        }
        Shardable shardable = (Shardable) target;
        try {
            this.beforeSharding(shardable, shardingVal);
            return point.proceed();
        } finally {
            this.afterSharding();
        }
    }

    private void beforeSharding(Shardable shardable, Object shardingVal) {
        if (shardingVal == null) {
            return;
        }
        Class<?> clz = shardable.getPersistClz();
        String srcTableName = PersistUtil.getTableName(clz);
        String targetTableName = PersistUtil.getPhysicalTableName(clz, shardingVal);
        DynamicTableTL.set(srcTableName, targetTableName);
    }

    private void afterSharding() {
        DynamicTableTL.remove();
    }
}
