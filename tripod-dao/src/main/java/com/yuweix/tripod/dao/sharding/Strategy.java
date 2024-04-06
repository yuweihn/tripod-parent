package com.yuweix.tripod.dao.sharding;



/**
 * 分库分表策略
 * @author yuwei
 */
public interface Strategy {

    /**
     * @param databaseName               逻辑库名
     * @param tableName                  逻辑表名
     * @param shardingVal                分库字段的值
     * @return   返回如：0000,0001等等
     */
    <T>String getShardingDatabaseIndex(String databaseName, String tableName, T shardingVal);

    /**
     * @param tableName                  逻辑表名
     * @param shardingVal                分表字段的值
     * @return   返回如：0000,0001等等
     */
    <T>String getShardingIndex(String tableName, T shardingVal);
}
