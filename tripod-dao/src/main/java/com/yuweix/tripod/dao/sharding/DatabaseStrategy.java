package com.yuweix.tripod.dao.sharding;



/**
 * 分库策略
 * @author yuwei
 */
public interface DatabaseStrategy {
    default DatabaseConfig getDatabaseConf(String databaseName) {
        return ShardingContext.getInstance().getDatabaseConf(databaseName);
    }

    /**
     * @param databaseName                  逻辑库名
     * @param shardingVal                   分库字段的值
     * @return   返回分库。如：0000,0001等等
     */
    <T>String getShardingIndex(String databaseName, T shardingVal);
}
