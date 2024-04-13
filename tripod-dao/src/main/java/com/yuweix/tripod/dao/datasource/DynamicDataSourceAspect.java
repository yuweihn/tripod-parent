package com.yuweix.tripod.dao.datasource;


import com.yuweix.tripod.dao.sharding.DataSourceAspect;
import org.aspectj.lang.annotation.Aspect;


/**
 * @author yuwei
 */
@Aspect
public class DynamicDataSourceAspect extends DataSourceAspect {
    @Override
    protected void setDataSource(String dsName) {
        DataSourceContextHolder.setDataSource(dsName);
    }

    @Override
    protected void removeDataSource() {
        DataSourceContextHolder.removeDataSource();
    }
}
