package com.yuweix.tripod.dao.datasource;


import com.yuweix.tripod.dao.sharding.DataSourceAspect;
import com.yuweix.tripod.dao.sharding.Shardable;
import com.yuweix.tripod.dao.sharding.ShardingDataSource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author yuwei
 */
@Aspect
public class DynamicDataSourceAspect extends DataSourceAspect {
    private static final Logger log = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    /**
     * 不需要分库，只是单纯的切换数据库的切入点。
     * 非{@link Shardable}的子类，且当前切入点或当前切入点所在类有{@link ShardingDataSource}注解。
     */
    @Pointcut("(!execution(* com.yuweix.tripod.dao.sharding.Shardable+.*(..))) && (@within(com.yuweix.tripod.dao.datasource.DataSource) || @annotation(com.yuweix.tripod.dao.datasource.DataSource))")
    public void change() {

    }

    @Around("change()")
    public Object onChange(ProceedingJoinPoint point) throws Throwable {
        Object target = point.getTarget();
        if (target instanceof Shardable) {
            return point.proceed();
        }
        DataSource annotation = getAnnotation(point, DataSource.class);
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

    @Override
    protected void setDataSource(String dsName) {
        DataSourceContextHolder.setDataSource(dsName);
    }

    @Override
    protected void removeDataSource() {
        DataSourceContextHolder.removeDataSource();
    }
}
