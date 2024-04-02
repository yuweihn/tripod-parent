package com.yuweix.tripod.dao.sharding;



/**
 * 分表策略
 * @author yuwei
 */
public interface Strategy {
    default TableConfig getTableConf(String tableName) {
        return ShardingContext.getInstance().getTableConf(tableName);
    }

    /**
     * @param tableName                  逻辑表名
     * @param shardingVal                分表字段的值
     * @return   返回分片。如：0000,0001等等
     */
    <T>String getShardingIndex(String tableName, T shardingVal);
}
