package com.yuweix.tripod.dao.datasource;


import com.yuweix.tripod.dao.sharding.DataSourceAspect;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author yuwei
 */
@Aspect
public class DynamicDataSourceAspect extends DataSourceAspect {
    private static final Logger log = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    @Override
    protected void setDataSource(String dbName) {
        DataSourceContextHolder.setDataSource(dbName);
    }

    @Override
    protected void removeDataSource() {
        DataSourceContextHolder.removeDataSource();
    }
}
