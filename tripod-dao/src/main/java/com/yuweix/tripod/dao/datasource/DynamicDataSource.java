package com.yuweix.tripod.dao.datasource;


import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;


/**
 * @author yuwei
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDataSource();
    }
}
