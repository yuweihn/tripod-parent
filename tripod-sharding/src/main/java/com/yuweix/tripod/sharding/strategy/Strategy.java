package com.yuweix.tripod.sharding.strategy;



/**
 * 分库分表策略
 * @author yuwei
 */
public interface Strategy {
    /**
     * @param dbName                     逻辑库名
     * @param tableName                  逻辑表名
     * @param shardingVal                分库字段的值
     * @return   返回如：gateway_0000,gateway_0001
     */
    <T>String getPhysicalDatabaseName(String dbName, String tableName, T shardingVal);

    /**
     * @param tableName                  逻辑表名
     * @param shardingVal                分表字段的值
     * @return   返回如：user_0000,user_0001
     */
    <T>String getPhysicalTableName(String tableName, T shardingVal);
}
