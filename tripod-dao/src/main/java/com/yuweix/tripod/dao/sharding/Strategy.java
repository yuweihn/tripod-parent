package com.yuweix.tripod.dao.sharding;



/**
 * 分库/表策略
 * @author yuwei
 */
public interface Strategy {
    /**
     * @param logicName                  逻辑库名
     * @param shardingVal                分库字段的值
     * @return   返回如：0000,0001等等
     */
    <T>String getShardingDatabaseIndex(String logicName, T shardingVal);

    /**
     * @param logicName                  逻辑表名
     * @param shardingVal                分表字段的值
     * @return   返回如：0000,0001等等
     */
    <T>String getShardingIndex(String logicName, T shardingVal);
}
